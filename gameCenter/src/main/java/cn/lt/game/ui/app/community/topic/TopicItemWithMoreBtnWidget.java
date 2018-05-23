package cn.lt.game.ui.app.community.topic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import cn.lt.game.R;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.widget.MoreButton;

/**
 *
 * 带有更多按键的话题项，包括：用户信息、话题内容、阅读数、评论数、点赞数
 *
 * 可以在布局中直接使用
 *
 */
public class TopicItemWithMoreBtnWidget extends RelativeLayout {

	private MoreButton moreBtn;
	protected TopicItemWidget topicItem;

	public TopicItemWithMoreBtnWidget(Context context) {
		super(context);
		initView(context);
		init_findView();
	}

	public TopicItemWithMoreBtnWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		init_findView();
	}

	public TopicItemWithMoreBtnWidget(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
		init_findView();
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(
				R.layout.layout_topic_item_with_more_btn, this);
		setPadding(DensityUtil.dip2px(context, 12), 0,
				DensityUtil.dip2px(context, 12), 0);
		setBackgroundResource(R.drawable.border_left_top_right_bottom);
	}

	private void init_findView() {
		moreBtn = (MoreButton) findViewById(R.id.more);
		topicItem = (TopicItemWidget) findViewById(R.id.topic);
	}

	public void setData(TopicDetail topic) {
		topicItem.setData(topic);
		moreBtn.setData(topic, MoreButton.TopicType.Default_Topic);
	}
}
