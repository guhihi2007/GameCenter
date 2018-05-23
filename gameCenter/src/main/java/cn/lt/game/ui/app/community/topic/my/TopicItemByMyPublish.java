package cn.lt.game.ui.app.community.topic.my;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.ui.app.community.model.TopicDetail;

/**
 * 
 * 我发表的话题项，包括：话题标题，所在小组，发表时间
 * 
 */
public class TopicItemByMyPublish extends RelativeLayout {

	private TopicTitleView title;
	private TextView tv_topicTime;

	public TopicItemByMyPublish(Context context) {
		super(context);
		initView(context);
		init_findView();
	}

	public TopicItemByMyPublish(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		init_findView();
	}

	public TopicItemByMyPublish(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
		init_findView();
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.topic_item_by_my_publish, this);
		setBackgroundResource(R.color.white);
		setPadding(DensityUtil.dip2px(context, 12), 0,
				DensityUtil.dip2px(context, 12), 0);
	}

	private void init_findView() {
		title = (TopicTitleView) findViewById(R.id.title);
		tv_topicTime = (TextView) findViewById(R.id.tv_topicTime);
	}

	public void setData(final TopicDetail topic) {
		title.setTopicTitle(topic);
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ("verifying".equals(topic.status)) {
					Log.i("zzz", "点击到了待审核，不予跳转");
					ToastUtils.showToast(v.getContext(), "您查看的话题正在审核中！");
				}else{
					ActivityActionUtils.jumpToTopicDetail(v.getContext(), topic.topic_id);
				}

			}
		});

		setTopicPublishTime(topic.published_at);

	}


	private void setTopicPublishTime(String time) {
		tv_topicTime.setText(TimeUtils.curtimeDifference(time));
	}



}
