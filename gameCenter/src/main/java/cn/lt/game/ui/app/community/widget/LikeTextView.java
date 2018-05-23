package cn.lt.game.ui.app.community.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.net.Host.HostType;
import cn.lt.game.net.Net;
import cn.lt.game.net.NetIniCallBack;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.CheckUserRightsTool;
import cn.lt.game.ui.app.community.model.ILike;
import cn.lt.game.ui.app.community.model.TextType;
import de.greenrobot.event.EventBus;

/**
 * 
 * 点赞按键
 *
 */
public class LikeTextView extends TextView {

	protected ILike data;
	private OnClickListener mClickListener;
	protected boolean isSendingRequest = false;
	private TextType textType = TextType.CHAR;
	
	/** 来自哪个页面*/
	private String forPage;
	private  Context mContext;
	public LikeTextView(Context context) {
		super(context);
		mContext = context;
		EventBus.getDefault().register(LikeTextView.this);
	}

	public LikeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public LikeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		EventBus.getDefault().register(LikeTextView.this);
	}

	public void setData(ILike data) {
		this.data = data;
		initView();
	}

	public void setTextType(TextType type) {
		this.textType = type;
	}

	public OnClickListener getOnClickListener() {
		return mClickListener;
	}
	
	


	// 初始化按键
	private void initView() {
		if (data.isLiked() == false) {
			setNotPressedView();
		} else {
			setPressedView();
		}

		mClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Context context = v.getContext();
				/***
				 * 1.先判断用户是否登陆 2.再判断用户是否加入此小组
				 */
				CheckUserRightsTool.instance().checkUserRights(context,true,
						data.getGroupId(), new NetIniCallBack() {
							@Override
							public void callback(int code) {
								if (code == 0) {
									if (!data.isLiked()) {
										execLikeAction(context);
									} else {
										execCancelLikeAction(context);
									}
								}
							}
						});
				


			}
		};
		setOnClickListener(mClickListener);
	}

	// 执行点赞操作
	protected void execLikeAction(final Context context) {
		if (isSendingRequest) {
			return;
		}
		isSendingRequest = true;
		// 构造点赞请求参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", data.getLikeType().toString());
		params.put("id", Integer.toString(data.getTopicId()));

		// 发送点赞请求给后台
		Net.instance().executePost(HostType.FORUM_HOST,
				Uri.getTopicLikeUri(data.getTopicId()), params,
				new WebCallBackToString() {

					@Override
					public void onFailure(int statusCode, Throwable error) {
						ToastUtils.showToast(context, "点赞失败——" + error.getMessage());
						isSendingRequest = false;
					}

					@Override
					public void onSuccess(String result) {
						// 设置为已点击状态
						data.setLiked(true);

						// 点赞数目+1
						increaseLikeNum();

						// 设置图标为已点击状态
						setPressedView();

						ToastUtils.showToast(context, "赞的漂亮");
						// 防止频繁点赞
						isSendingRequest = false;
					}
				});
	}

	// 执行取消点赞操作
	protected void execCancelLikeAction(final Context context) {
		if (isSendingRequest) {
			return;
		}
		isSendingRequest = true;
		// 发送取消点赞消息
		Net.instance().executeDelete(HostType.FORUM_HOST,
				Uri.getTopicCancelLikeUri(data.getTopicId()),
				new WebCallBackToString() {

					@Override
					public void onFailure(int statusCode, Throwable error) {
						ToastUtils.showToast(context,
								"取消点赞失败——" + error.getMessage());
						isSendingRequest = false;
					}

					@Override
					public void onSuccess(String result) {
						// 设置为未点击状态
						data.setLiked(false);
						// 点赞数目-1
						decreaseLikeNum();

						// 设置图标为未点击状态
						setNotPressedView();

						ToastUtils.showToast(context, "取消点赞");
						// 防止频繁点击
						isSendingRequest = false;
					}
				});
	}

	// 设置左图为已点击状态
	protected void setPressedView() {
		if (textType == TextType.NUM) {
			setText(getNumString(data.getLikeNum()));
		} else {
			setText("已赞");
		}
		setLeftDrawable(R.mipmap.btn_like_p);
	}

	// 设置左图标为未点击状态
	protected void setNotPressedView() {
		if (textType == TextType.NUM) {
			setText(getNumString(data.getLikeNum()));
		} else {
			setText("赞");
		}
		setLeftDrawable(R.mipmap.btn_like_n);
	}

	// 设置左图
	private void setLeftDrawable(int resId) {
		Drawable drawable = getResources().getDrawable(resId);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		LikeTextView.this.setCompoundDrawables(drawable, null, null, null);
	}

	protected void increaseLikeNum() {
		data.setLikeNum(data.getLikeNum() + 1);
	}

	protected void decreaseLikeNum() {
		data.setLikeNum(data.getLikeNum() - 1);
	}

	// 转换：赞数-》字符串
	private String getNumString(int num) {
		return num == 0 ? getResources().getString(R.string.like) : String
				.valueOf(num);
	}

	// 转换：字符串-》赞数
	private int getNumFromString(String num) {
		return num.equalsIgnoreCase(getResources().getString(R.string.like)) == true ? 0
				: Integer.parseInt(num);
	}

}