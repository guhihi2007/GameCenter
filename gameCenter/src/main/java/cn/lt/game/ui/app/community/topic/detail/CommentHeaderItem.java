package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.view.View;

import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.topic.detail.CommentView.replyOnclickPositionListener;
import cn.lt.game.ui.app.community.topic.detail.HeaderViewInstance.HeaderView;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

public class CommentHeaderItem implements ICommentList {
	public static final int COMMENTHEADER_TYPE = 6;
	private HeaderView headerView;//用户信息，评论内容

	public CommentHeaderItem(HeaderView headerView, int groupId,
			String groupTitle) {
		this.headerView = headerView;
	}

	@Override
	public boolean isClickable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick() {

	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return COMMENTHEADER_TYPE;
	}

	@Override
	public Object getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View createView(Context context, View convertView,
			 int position, TopicDetail info,
			replyOnclickPositionListener positionListener,
			UserBaseInfo UserBaseInfo) {

		convertView = headerView;
		headerView.setGroupTitle(info.group_title, info.group_id);
		headerView.setGroupIcon(info.author_icon);

		return convertView;
	}

	@Override
	public View getView() {
		return headerView;

	}

	@Override
	public void setLineVisibility(int visibility) {

	}

}
