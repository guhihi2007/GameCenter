package cn.lt.game.ui.app.community.topic.my;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.ui.app.community.model.TopicDetail;

public class TopicTitleView extends TextView {
	/** 推荐标签 */
	private Drawable push_logo;

	/** 置顶标签 */
	private Drawable top_logo;

	/** 精华标签 */
	private Drawable essence_logo;

	/** 采纳标签 */
	private Drawable accept_logo;

	/** 审核标签 */
	private Drawable check_logo;

	private boolean hasLabel;

	private SpannableStringBuilder style;

	public TopicTitleView(Context context) {
		super(context);
		initView(context);
	}

	public TopicTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public TopicTitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		if(push_logo == null) {
			push_logo = getResources().getDrawable(R.mipmap.topic_recommend_logo);
			push_logo.setBounds(0, 0, push_logo.getIntrinsicWidth(), push_logo.getIntrinsicHeight());
		}

		if(top_logo == null) {
			top_logo = getResources().getDrawable(R.mipmap.topic_top_logo);
			top_logo.setBounds(0, 0, top_logo.getIntrinsicWidth(), top_logo.getIntrinsicHeight());
		}

		if(essence_logo == null) {
			essence_logo = getResources().getDrawable(R.mipmap.topic_essence_logo);
			essence_logo.setBounds(0, 0, essence_logo.getIntrinsicWidth(), essence_logo.getIntrinsicHeight());
		}

		if(accept_logo == null) {
			accept_logo = getResources().getDrawable(R.mipmap.topic_essence_logo);
			accept_logo.setBounds(0, 0, accept_logo.getIntrinsicWidth(), accept_logo.getIntrinsicHeight());
		}


		if(check_logo == null) {
			check_logo = getResources().getDrawable(R.mipmap.topic_check_logo);
			check_logo.setBounds(0, 0, check_logo.getIntrinsicWidth(), check_logo.getIntrinsicHeight());
		}


		if(style == null) {
			style = new SpannableStringBuilder();
		}

	}


	/**
	 * 设置话题标题
	 * @param topic 话题实体类
	 */
	public void setTopicTitle(TopicDetail topic) {
		// 先清空textView的内容
		style.clear();
		hasLabel = false;

		setPush(topic.isIs_push());
		setEssence(topic.isIs_essence());
		setIsCheck(topic.status);
		setTop(topic.isIs_top());

		style.append((hasLabel ? " " : "") + topic.getTopicTitle());
		this.setText(style);
	}

	private void setPush(boolean isPush) {
		if (isPush) {
			setTitleLabel(push_logo, "推荐");
		}
	}

	private void setTop(boolean isTop) {
		if (isTop) {
			setTitleLabel(top_logo, "置顶");
		}
	}

	private void setEssence(boolean isEssence) {
		if (isEssence) {
			setTitleLabel(essence_logo, "精华");
		}
	}

	private void setAccept(boolean isAccept) {
		// 采纳（目前没有）
	}

	private void setIsCheck(String status) {
		if (null != status && "verifying".equals(status)) {
			setTitleLabel(check_logo, "审核");
		}
	}

	private void setTitleLabel(Drawable drawable, String label) {
		hasLabel = true;

		// 把标签图片插入到文本控件的最前面
		style.insert(0,label);
		style.setSpan(new ImageSpan(drawable,ImageSpan.ALIGN_BASELINE), 0,
				label.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}


}
