package cn.lt.game.ui.app.community.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.net.NetIniCallBack;
import cn.lt.game.ui.app.community.CheckUserRightsTool;
import cn.lt.game.ui.app.community.SendCommentActivity;
import cn.lt.game.ui.app.community.model.CommentEvent;
import cn.lt.game.ui.app.community.model.IComment;
import cn.lt.game.ui.app.community.model.TextType;
import de.greenrobot.event.EventBus;

/**
 * 
 * 评论按键 注意：不需要在调用此TextView的地方设置点击事件
 * 
 */
public class CommentTextView extends TextView {

	/**热门话题列表*/
	public static final int FROM_HOTTOPICLIST = 1;
	/**话题详情*/
	public static final int FROM_TOPICDETAIL = 2;

	private OnClickListener mClickListener;
	private boolean isAutoJump = false;
	private IComment data;
	private TextType textType = TextType.CHAR;
	
	/** 来自哪个页面*/
	private String forPage;

	public CommentTextView(Context context) {
		super(context);
		EventBus.getDefault().register(CommentTextView.this);
	}

	public CommentTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		EventBus.getDefault().register(CommentTextView.this);
	}

	public CommentTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		EventBus.getDefault().register(CommentTextView.this);
	}

	/**
	 * 设置评论按键所需的数据
	 * 
	 * @param groupId
	 *            小组ID
	 * @param topicId
	 *            话题ID
	 * @param commentNum
	 *            话题评论总数
	 */
	public void setData(IComment data,int from) {
		this.data = data;
		setClickListener(this.data,from);
		if (textType == TextType.NUM) {
			setCommentNum(data.getCommentNum());
		} else {
			setText("评论");
		}
	}

	public void setTextType(TextType type) {
		this.textType = type;
	}

	/**
	 * 设置话题评论数
	 * 
	 * @param num
	 *            评论数
	 */
	public void setCommentNum(int num) {
		setText(getNumString(num));
	}

	private void increaseCommentNum() {
		data.setCommentNum(data.getCommentNum() + 1);
		setCommentNum(data.getCommentNum());
	}

	/**
	 * 设置是否评论完后自动跳到话题详情页
	 * 
	 * @param isAuto
	 *            是否自动跳转
	 */
	public void setAutoJumpToTopicDetail(boolean isAuto) {
		this.isAutoJump = isAuto;
	}

	/**
	 * 获取点击事件
	 * 
	 * @return 点击事件
	 */
	public OnClickListener getOnClickListener() {
		return mClickListener;
	}
	

	public void onEventMainThread(CommentEvent info) {
		if (info.getResult() == true && info.getGroupId() == data.getGroupId()
				&& info.getTopicId() == data.getTopicId()
				&& textType == TextType.NUM) {
			increaseCommentNum();
		}
	}

	/**
	 * 设置点击监听事件
	 * 
	 * @param groupId
	 *            小组ID
	 * @param topicId
	 *            话题ID
	 */
	private void setClickListener(final IComment data,final int from) {
		mClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Context context = v.getContext();
				/***
				 * 1.先判断用户是否登陆 2.再判断用户是否加入此小组
				 */
				CheckUserRightsTool.instance().checkUserRights(context,false,
						data.getGroupId(), new NetIniCallBack() {
							@Override
							public void callback(int code) {
								if (code == 0) {
									String content;
									if(from == FROM_TOPICDETAIL) {
										content = new String(Base64.decode(data.getTopicContent(), Base64.DEFAULT));
									}else{//FROM_HOTTOPICLIST
										content = data.getTopicContent();
									}
									Intent intent = SendCommentActivity
											.getIntent(context,
													data.getGroupId(),
													data.getTopicTitle(),
													content,
													data.getGroupTitle(),
													isAutoJump,
													data.getTopicId(), "1");
									context.startActivity(intent);
								}
							}
						});
			}
		};
		setOnClickListener(mClickListener);
	}

	private String getNumString(int num) {
		return num == 0 ? getResources().getString(R.string.comment) : String
				.valueOf(num);
	}

}
