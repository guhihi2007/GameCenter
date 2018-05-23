package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.view.View;

import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.model.User;
import cn.lt.game.ui.app.community.topic.detail.CommentView.replyOnclickPositionListener;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

public class LikeListItem implements ICommentList {
	public static final int LIKELISTVIEWTYPE = 2;
	private User user;
	private LikeListView likeListView;
	private int visibility = View.VISIBLE;

	public LikeListItem(User user) {
		this.user = user;
	}

	@Override
	public boolean isClickable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return LIKELISTVIEWTYPE;
	}

	@Override
	public Object getData() {
		// TODO Auto-generated method stub
		return user;
	}

	@Override
	public View createView(Context context, View convertView,
			 int position, TopicDetail info,
			replyOnclickPositionListener positionListener,
			UserBaseInfo UserBaseInfo) {

		if (convertView == null) {
			likeListView = new LikeListView(context);
			convertView = likeListView;
		}

		likeListView = (LikeListView) convertView;
		updata(likeListView);

		return convertView;
	}

	private void updata(LikeListView view) {
		view.upDate(user);
		view.hidetLine(visibility);
	}

	@Override
	public View getView() {

		return likeListView;
	}

	@Override
	public void setLineVisibility(int visibility) {
		this.visibility = visibility;
	}

}
