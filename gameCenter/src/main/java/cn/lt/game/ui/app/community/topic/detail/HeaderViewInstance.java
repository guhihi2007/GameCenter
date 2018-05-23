package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import cn.lt.game.R;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.topic.group.GroupTopicActivity;

public class HeaderViewInstance {
	private static HeaderView instance = null;

	public static HeaderView getInstance(Context context) {
		if (instance == null) {
			instance = new HeaderView(context);
		}
		return instance;

	}

	public static void onDestroy() {
		instance = null;
	}

	public static class HeaderView extends LinearLayout {
		private GroupNameTitle groupNameTitle;//用户组view
		private TopicDetailUserInfoView userInfoView;//话题标题view
		private int groupId = -1;

		public HeaderView(Context context) {
			this(context, null);
			// TODO Auto-generated constructor stub
		}

		public HeaderView(Context context, AttributeSet attrs) {
			this(context, attrs, 0);
			// TODO Auto-generated constructor stub
		}

		public HeaderView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			// TODO Auto-generated constructor stub
			LayoutInflater.from(context).inflate(
					R.layout.topicdetail_headerview, this);
			initView();
		}

		private void initView() {
			groupNameTitle = (GroupNameTitle) findViewById(R.id.topicDetail_listView_title);
			userInfoView = (TopicDetailUserInfoView) findViewById(R.id.topicDetail_listView_UserInfoView);

			groupNameTitle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (groupId != -1) {

						ActivityActionUtils.activity_Jump_Value(v.getContext(),
								GroupTopicActivity.class, "group_id", groupId);

					}

				}
			});
		}

		public void setDetail(TopicDetail detail) {
			userInfoView.fillLayout(detail);
		}

		public void setGroupTitle(String title, int groupId) {
			groupNameTitle.setTitle("来自 " + title);
			this.groupId = groupId;
		}
		
		public void setGroupIcon(String url) {
			groupNameTitle.setIcon(url);
		}

	}

}
