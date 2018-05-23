package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.ui.app.community.model.Reply;

public class ReplyView extends LinearLayout {
	private static final String REPLY = " 回复 ";
	private TextView valueTv;
	private TextView timeTv;
	private int replyerId, acceptorId;
	private OnReplyerClickListener onReplyerClickListener;
	private String replyTo;
	private String replyAt;

	public ReplyView(Context context) {
		this(context, null);
	}

	public ReplyView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ReplyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.replyview_layout, this);
//		setBackground(getResources().getDrawable(R.drawable.reply_background));
		setOrientation(VERTICAL);
//

		initView();
	}

	private void initView() {
		valueTv = (TextView) findViewById(R.id.reply_value);
		timeTv = (TextView) findViewById(R.id.reply_time);
	}

	/** 是在话题详情页面显示，回复内容只显示5行*/
	public void setisDisplayOnTopicDetail() {
		valueTv.setMaxLines(5);
		valueTv.setEllipsize(TextUtils.TruncateAt.END);
	}

	public void setValue(Context context, String replyTo, String replyAt,
			String value, int replyerId, int acceptorId) {
		initReplyData(replyTo, replyAt, replyerId, acceptorId);

		SpannableStringBuilder style = new SpannableStringBuilder();
		if(!TextUtils.isEmpty(replyAt)){
			style.append(replyTo).append(REPLY).append(replyAt + " : ");

			//改变 回复者 的用户名颜色（蓝色）
			style.setSpan(new ForegroundColorSpan(Color.parseColor("#336699")), 0,
					replyTo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			style.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")),
					replyTo.length(), replyTo.length() + REPLY.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			//改变 被回复人 的用户名颜色（蓝色）
			style.setSpan(new ForegroundColorSpan(Color.parseColor("#336699")),
					replyTo.length() + REPLY.length(), style.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			setReplyerClickable(style, replyTo.length());
			setAcceptorClickable(style, replyTo.length() + REPLY.length(), style.length());
			valueTv.setMovementMethod(LinkMovementMethod.getInstance());


		}else{
			style.append(replyTo + " : ");
			style.setSpan(new ForegroundColorSpan(Color.parseColor("#336699")), 0,
					replyTo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			setReplyerClickable(style, replyTo.length());
		}
		
		style.append(value);
		valueTv.setText(style);

	}

	/** 初始化回复view相关的数据*/
	private void initReplyData(String replyTo, String replyAt, int replyerId, int acceptorId) {
		this.replyTo = replyTo;
		this.replyAt = replyAt;
		this.replyerId = replyerId;
		this.acceptorId = acceptorId;
	}

	/** 设置 回复人 点击事件*/
	private void setReplyerClickable(SpannableStringBuilder style, int spanEnd) {
		style.setSpan(new Clickable(new OnClickListener() {
					@Override
					public void onClick(View v) {
							  if (onReplyerClickListener != null) {
								  Log.i("replyLog", "ReplyView~~~Replyer~~~点我啦！");
								  onReplyerClickListener.OnReplyerNameClick(replyTo, replyerId);
							  } else {
								  Log.i("replyLog", "ReplyView~~~Replyer~~~点中我了，但是没回调！");
							  }
					}
				}),
				0, spanEnd,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	/** 设置被 回复人 点击事件*/
	private void setAcceptorClickable(SpannableStringBuilder style,int spanStart, int spanEnd) {
		style.setSpan(new Clickable(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(onReplyerClickListener != null) {
							Log.i("replyLog", "ReplyView~~~Acceptor~~~被点啦！");
							onReplyerClickListener.OnAcceptorNameClick(replyAt, acceptorId);
					    } else {
							Log.i("replyLog", "ReplyView~~~Acceptor~~~点中我了，但是没回调！");
						}
					}
				}),
				spanStart, spanEnd,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	public void setTime(Reply reply) {
		if(null != reply.published_at && !TextUtils.isEmpty(reply.published_at)) {
			timeTv.setText(TimeUtils.curtimeDifference(reply.published_at));
		}

		if (null != reply.created_at && !TextUtils.isEmpty(reply.created_at)) {
			timeTv.setText(TimeUtils.curtimeDifference(reply.created_at));
		}

	}

	/** 设置回复内容最多显示的行数*/
	public void setReplyMaxLines(int line) {
		valueTv.setMaxLines(line);
		valueTv.setEllipsize(TextUtils.TruncateAt.END);
	}

	public void setOnReplyerClickListener(OnReplyerClickListener onReplyerClickListener) {
		this.onReplyerClickListener = onReplyerClickListener;
	}

	public interface OnReplyerClickListener {
		/** 当 回复者 名称被点击时*/
		void OnReplyerNameClick(String replyerName, int replyerId);

		/** 当 被回复者 名称被点击时*/
		void OnAcceptorNameClick(String acceptorName, int acceptorId);
	}

	class Clickable extends ClickableSpan implements View.OnClickListener {

		private final View.OnClickListener mListener;

		public Clickable(View.OnClickListener listener) {
			mListener = listener;
		}


		@Override
		public void updateDrawState(TextPaint ds) {// 重写该方法是为了去掉链接的颜色和下划线

		}

		@Override
		public void onClick(View widget) {
			mListener.onClick(widget);
		}
	}


}
