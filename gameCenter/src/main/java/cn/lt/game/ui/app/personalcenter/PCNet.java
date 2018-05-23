package cn.lt.game.ui.app.personalcenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import java.util.HashMap;
import java.util.Map;

import cn.lt.game.application.MyApplication;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.web.WebCallBackBase;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.net.Host;
import cn.lt.game.net.Host.HostType;
import cn.lt.game.net.Net;
import cn.lt.game.net.NetBaseInfo;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 *
 */
public class PCNet {
	
	private final static int getRequest = 0;
	private final static int putRequest = 1;
	private final static int postRequest = 2;
	
	public final static String myTrades_all = "all";
	public final static String myTrades_success = "success";
	public final static String myTrades_unsuccess = "unsuccess";
	
	@SuppressWarnings("rawtypes")
	public static void ucRequestBase(final int requestType,final String uri,final Map para,final WebCallBackBase callBack) {
		if(TextUtils.isEmpty(Host.getHost(HostType.UCENETER_HOST))||TextUtils.isEmpty(Net.instance().getetUcenterSalt())){
			Net.instance().executeGet(HostType.UCENETER_BASE_HOST, Uri.USER_BASE_URI, null, new WebCallBackToObj<NetBaseInfo>() {
				
				@Override 
				protected void handle(NetBaseInfo info) {
//					mSalt.setServerSalt(info.getSalt());
					Host.setHost(HostType.UCENETER_HOST, info.getServer_host());
					Net.instance().setUcenterSalt(info.getSalt());
					if(!info.isTokenExists()&&UserInfoManager.instance().isLogin()){
						UserInfoManager.instance().userLogout(false);
						ToastUtils.showToast(MyApplication.application, "登录超时,请重新登录");
					}else{
						ucRequestBase(requestType, uri, para, callBack);
					}
				}

                @Override
				public void onFailure(int statusCode, Throwable error) {
					callBack.onFailure(statusCode, error);
				}
				
			});
		}else{
			sendRequest(requestType, uri, para, callBack);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void sendRequest(int requestType, String uri, Map para, WebCallBackBase callBack) {
		switch (requestType) {
		case getRequest:
			Net.instance().executeGet(HostType.UCENETER_HOST, uri, para, callBack);
			break;
		case putRequest:
			Net.instance().executePut(HostType.UCENETER_HOST, uri, para, callBack);
			break;
		case postRequest:
			Net.instance().executePost(HostType.UCENETER_HOST, uri, para, callBack);
			break;

		default:
			break;
		}
	}
	
	/**
	 * 登录
	 * @param userName
	 * @param passWord
	 * @param callBack
	 */
	public static void login(String userName, String passWord, final WebCallBackBase callBack) {
		final Map<String, String> para = new HashMap<String, String>();
		para.put("username", userName);
		para.put("password", passWord);
		ucRequestBase(putRequest,Uri.USER_SIGIN_URI, para,callBack);
	}
	
	/**
	 * 获取个人资料
	 * @param callBack
	 */
	public static void fetchUserInfo(WebCallBackBase callBack) {
		ucRequestBase(getRequest, Uri.USER_INFO_URI, null, callBack);
	}

	/**
	 * 发送验证码
	 * @param moblie
	 * @param exist 检查手机号码是否注册过。true：开启检查；false：不用检查
	 * @param check 0:false，1:true
	 * @param callback
	 */
	public static void sendCode(String moblie,boolean exist,int check,WebCallBackBase callback){
		Map<String, String> map = new HashMap<String, String>();
		map.put("mobile", moblie);
		map.put("check", check+"");
		if(exist){
			map.put("exist", "1");
		}else{
			map.put("exist", "0");
		}
		ucRequestBase(getRequest, Uri.USER_SMS_SEND_URI,map,callback);
	}
	
	/**
	 * 用户注册
	 * @param userName
	 * @param passWord
	 * @param code
	 * @param callback
	 */
	public static void register(String userName, String passWord, String code, WebCallBackBase callback) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (userName.indexOf("@") == -1) {
			map.put("mobile", userName);
		} else {
			map.put("email", userName);
		}
		map.put("password", passWord);
		if(code!=null){
			map.put("code", code);
		}
		ucRequestBase(postRequest, Uri.USER_CREATE_URI, map, callback);
	}
	
	/**
	 * 更新用户信息
	 * @param user
	 * @param callback
	 */
	public static void updateUserInfo(UserBaseInfo user, WebCallBackBase callback) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("avatar", user.getAvatar());
		map.put("nickname", user.getNickname());
		map.put("sex", user.getSex());
		map.put("birthday", user.getBirthday()/1000+"");
		map.put("address", user.getAddress());
		map.put("summary", user.getSummary());

		ucRequestBase(putRequest, Uri.USER_UPDATE_URI, map, callback);
	}
	
	/**
	 * 更新头像
	 * @param bitmap
	 * @param callback
	 */
	public static void updateAvatar(Bitmap bitmap, WebCallBackBase callback) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("avatar", Utils.Bitmap2InputStream(bitmap));
		ucRequestBase(postRequest, Uri.USER_AVATAR_URI, map, callback);
	}
	
	/**
	 * 手机找回密码
	 * @param moblie
	 * @param code
	 * @param callback
	 */
	public static void findPwdCheck(String moblie, String code, WebCallBackBase callback) {
		Map<String,String> map = new HashMap<String, String>();
		map.put("mobile", moblie);
		map.put("code", code);
		ucRequestBase(getRequest, Uri.USER_SMS_CHECK_URI, map, callback);
	}
	
	/**
	 * 邮箱找回密码
	 */
	public static void findPwdbyMail(String email, WebCallBackBase callback) {
		Map<String,String> map = new HashMap<String, String>();
		map.put("email", email);
		ucRequestBase(getRequest, Uri.USER_SMS_CHECK_URI, map, callback);
	}
	
	/**
	 * 修改密码
	 * @param oldPwd
	 * @param newPwd
	 * @param code
	 * @param callback
	 */
	public static void modifyPwd(String oldPwd, String newPwd, String code, WebCallBackBase callback) {
		Map<String,String> map = new HashMap<String, String>();
		if(!TextUtils.isEmpty(oldPwd)){
			map.put("old_password", oldPwd);
		}
		map.put("new_password", newPwd);
		if(!TextUtils.isEmpty(code)){
			map.put("code", code);
		}
		ucRequestBase(putRequest, Uri.USER_PWD_URI, map, callback);
	}


	/**
	 * 绑定手机
	 * @param phone
	 * @param code
	 * @param callback
	 */
	public static void bindPhone(String phone, String code, WebCallBackBase callback) {
		Map<String,String> map = new HashMap<String, String>();
		map.put("mobile", phone);
		map.put("code", code);
		ucRequestBase(putRequest, Uri.USER_BIND_URI, map, callback);
	}

	/**
	 * 绑定手机
	 * @param phone
	 * @param code
	 * @param callback
	 */
	public static void checkOldPhone(String phone, String code, WebCallBackBase callback) {
		Map<String,String> map = new HashMap<String, String>();
		map.put("mobile", phone);
		map.put("code", code);
		ucRequestBase(putRequest, Uri.USER_BIND_CHECK_PHONE, map, callback);
	}

	/**
	 * 绑定邮箱
	 * @param email
	 * @param callback
	 */
	public static void bindEmail(String email, WebCallBackBase callback) {
		Map<String,String> map = new HashMap<String, String>();
		map.put("email", email);
		ucRequestBase(putRequest, Uri.USER_BIND_URI, map, callback);
	}

	/**
	 * 我的时间
	 * @param type
	 * @param page
	 * @param callback
	 */
	public static void myTrades(String type, int page, WebCallBackBase callback) {
		Map<String,String> map = new HashMap<String, String>();
		map.put("type", type);
		map.put("page", page+"");
		ucRequestBase(getRequest, Uri.USER_TRADES_URI, map, callback);
	}
	
	public static void hideKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),0);
		}
	}

	public static void showKeyboard(final Activity activity) {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
				imm.showSoftInput(activity.getCurrentFocus(),InputMethodManager.SHOW_FORCED);
			}
		}, 50);
	}

}
