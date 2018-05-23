package cn.lt.game.ui.app.community;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.NetIniCallBack;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.ForbadeActivity.IntentType;
import cn.lt.game.ui.app.community.model.User;
import cn.lt.game.ui.app.personalcenter.UserInfoLoginCallback;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

/***
 * 检查用户是否登陆、是否加入小组、加入小组、退出小组、是否被禁言
 * 
 * @author tiantian
 * 
 */

public class CheckUserRightsTool {

	public static final int CONFRIM = 4; // 确认
	public static final int CANCEL = 5; // 取消
	public static final int CLOSE = 6; // 关闭
	public static CheckUserRightsTool rightsTool;

	public static CheckUserRightsTool instance() {
		if (rightsTool == null) {
			synchronized (CheckUserRightsTool.class) {
				if (rightsTool == null) {
					rightsTool = new CheckUserRightsTool();
				}
			}
		}
		return rightsTool;
	}

	/**
	 * 判断用户是否登录
	 * 
	 * @return
	 */
	public boolean isLogin() {
		return UserInfoManager.instance().isLogin();
	}

	public void gotoLogin(final Context context) {
		haveLoginAndNextBehavior(context, null);
	}

	public boolean haveLoginAndNextBehavior(Context context, UserInfoLoginCallback loginCallBack) {
		return UserInfoManager.instance().isLoginHaveCall(context, true, loginCallBack);
	}
	/***
	 * 退出小组
	 * 
	 * @param context
	 * @param groupId
	 * @param callback
	 */
	public void quitGroup(final Context context, int groupId, final NetIniCallBack callback) {
		Net.instance().executeDelete(Host.HostType.FORUM_HOST, Uri.quitGroupUri(groupId), new WebCallBackToString() {
			@Override
			public void onFailure(int statusCode, Throwable error) {
				if (callback != null) {
					callback.callback(-2);
					LogUtils.i("zzz", "请求失败");
				}
			}

			@Override
			public void onSuccess(String result) {
				try {
					JSONObject obj = new JSONObject(result);
					if (obj.getInt("status") == 1) {
						if (callback != null) {
							callback.callback(0);
							LogUtils.i("zzz", "退出小组请求成功");
						}
					} else {
						if (callback != null) {
							callback.callback(-1);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

	/***
	 * 检查用户是否登陆、是否被禁言、加入小组【added by zhou 2015.7.24】
	 * 
	 * @param context
	 *            去登录 ，点赞,加入小组时无需判断是否被禁言
	 */
	public void checkUserRights(final Context context, final boolean isDianZan, final int groupId, final NetIniCallBack callback) {
		boolean hasLogin = haveLoginAndNextBehavior(context, new UserInfoLoginCallback() {
			@Override
			public void userLogin(UserBaseInfo userBaseInfo) {
				isForbade(context, isDianZan, groupId, callback);
			}
		});
		if (hasLogin) {
			isForbade(context, isDianZan, groupId, callback);   
		}
	}

	/***
	 * 
	 * 检查是否被禁言
	 * 
	 * @param context
	 * @param isDianZan
	 * @param groupId
	 * @param callback
	 */
	private void isForbade(Context context, boolean isDianZan, int groupId, NetIniCallBack callback) {
		if (isDianZan) {
			if (callback != null) {
				callback.callback(0);
			}
		} else {
			// 检查用户是否被禁言
			checkIsUserForbade(context, groupId, callback);
		}
	}

	/***
	 * 检查是否登录，然后直接加入小组
	 * 
	 * @param context
	 * @param groupId
	 * @param callback
	 */
	public void checkIsUserLoginAndGoinGroup(final Context context, final int groupId, final NetIniCallBack callback) {
		boolean hasLogin = haveLoginAndNextBehavior(context, new UserInfoLoginCallback() {
			@Override
			public void userLogin(UserBaseInfo userBaseInfo) {
				requestJoinGroup(context, groupId, callback);
			}
		});
		if (hasLogin) {
			requestJoinGroup(context, groupId, callback);
		}
	}

	/***
	 * 检查用户是否有被禁言
	 */
	public void checkIsUserForbade(final Context context, int groupId, final NetIniCallBack callback) {
		Net.instance().executeGet(Host.HostType.FORUM_HOST, Uri.getIsUserForbade(groupId), null, new WebCallBackToString() {
			@Override
			public void onSuccess(String result) {
				try {
					JSONObject obj = new JSONObject(result);
					JSONObject data = obj.getJSONObject("data");
					boolean hasFobid = data.getBoolean("success");
					if (hasFobid) {
						callback.callback(0);
					} else {
						callback.callback(-1);
						ActivityActionUtils.activity_Jump_Value(context, ForbadeActivity.class, "type", IntentType.forbid.type);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Throwable error) {
				ToastUtils.showToast(context, "请求网络失败！");
			}
		});
	}

	/***
	 * 只用来检测用户是否加入过该小组
	 * 
	 * @param context
	 * @param groupId
	 * @param callback
	 */
	public void hasUserJoinGroup(final Context context, final int groupId, final NetIniCallBack callback) {
		Net.instance().executeGet(Host.HostType.FORUM_HOST, Uri.getIsJoinGroupUri(groupId), null, new WebCallBackToObj<User>() {
			@Override
			public void onFailure(int statusCode, Throwable error) {
				if (callback != null) {
					callback.callback(-1);
				}
			}

			@Override
			protected void handle(User info) {
				boolean hasJoinGroup = info.is_join;
				LogUtils.i("zzz", "检测用户是否加入过该小组=" + hasJoinGroup);
				if (!hasJoinGroup) {
					if (callback != null) {
						callback.callback(-1);
					}
				} else {
					if (callback != null) {
						callback.callback(0);
					}
				}
			}
		});
	}

	/***
	 * 加入小组请求
	 * 
	 * @param context
	 * @param groupId
	 * @param callback
	 */
	public void requestJoinGroup(final Context context, int groupId, final NetIniCallBack callback) {
		Net.instance().executePost(Host.HostType.FORUM_HOST, Uri.getJoinGroupUri(groupId), null, new WebCallBackToString() {
			@Override
			public void onFailure(int statusCode, Throwable error) {
				if (callback != null) {
					callback.callback(-2);
				}
			}

			@Override
			public void onSuccess(String result) {
				try {
					JSONObject obj = new JSONObject(result);
					int code = obj.getInt("status");
					if (1 == code) {
						callback.callback(0);
						LogUtils.i("zzz", "加入小组请求成功");
					} else if (800 == code) {
						callback.callback(-2);
					} else {
						LogUtils.i("zzz", "加入小组请求失败");
						callback.callback(-1);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}


	/**
	 * 加关注
	 * @param context
	 * @param userId
	 * @param callback
	 */
	public void addAttention(final Context context, final int userId, final NetIniCallBack callback){
		Net.instance().executePost(Host.HostType.FORUM_HOST, Uri.addAttentionUri(userId), null, new WebCallBackToString() {
			@Override
			public void onSuccess(String result) {
				try {
					JSONObject obj = new JSONObject(result);
					int status = obj.getInt("status");
					if (1==status) {
						LogUtils.i("zzz","关注成功");
						ToastUtils.showToast(context, "关注成功！");
						callback.callback(0);
					} else {
						ToastUtils.showToast(context, "关注失败，请稍后重试！");
						callback.callback(-1);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Throwable error) {
				ToastUtils.showToast(context, "请求网络失败！");
			}
		});
	}

	/**
	 * 取消关注
	 * @param context
	 * @param userId
	 * @param callback
	 */
	public void cancelAttention(final Context context, final int userId, final NetIniCallBack callback){
		Net.instance().executeDelete(Host.HostType.FORUM_HOST, Uri.addAttentionUri(userId), new WebCallBackToString() {
			@Override
			public void onSuccess(String result) {
				try {
					JSONObject obj = new JSONObject(result);
					int status = obj.getInt("status");
					if (1 == status) {
						LogUtils.i("zzz", "取消关注成功");
						ToastUtils.showToast(context, "已取消关注！");
						callback.callback(0);
					} else {
						ToastUtils.showToast(context, "取消关注失败，请稍后重试！");
						callback.callback(-1);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Throwable error) {
				ToastUtils.showToast(context, "请求网络失败！");
			}
		});
	}
	public void collectReq(final Context context, final int topicId, final NetIniCallBack callback){
		// 发送收藏请求
		Net.instance().executePost(Host.HostType.FORUM_HOST, Uri.getCollectTopicUri(topicId), new WebCallBackToString() {
			@Override
			public void onSuccess(String result) {
				callback.callback(0);
				ToastUtils.showToast(context, "收藏成功");

			}

			@Override
			public void onFailure(int statusCode, Throwable error) {
				callback.callback(-1);
				ToastUtils.showToast(context, "收藏失败—— " + error.getMessage());
			}

		});
	}

	/***
	 * 是否绑定手机号码
	 * 用于发送私信检查
	 * @param context
	 * @param callback
	 */
	public void hasBindPhone(final Context context, final NetIniCallBack callback){
		boolean hasLogin = haveLoginAndNextBehavior(context, new UserInfoLoginCallback() {
			@Override
			public void userLogin(UserBaseInfo userBaseInfo) {
				if (TextUtils.isEmpty(userBaseInfo.getMobile())){
					callback.callback(-1);
				}else{
					LogUtils.i("zzz","登录后用户绑定的手机号码=="+userBaseInfo.getMobile());
					callback.callback(0);
				}
			}
		});
		if (hasLogin) {
			String mobile =  UserInfoManager.instance().getUserInfo().getMobile();
			if (TextUtils.isEmpty(mobile)){
				callback.callback(-1);
			}else{
				LogUtils.i("zzz","已经登录过的，用户绑定的手机号码=="+mobile);
				callback.callback(0);
			}

		}
	}
}
