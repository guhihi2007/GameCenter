package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.view.View;

import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.topic.detail.CommentView.replyOnclickPositionListener;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

public class CommentEmptyItem implements ICommentList {
	public static final int COMMENTEMPTY_TYPE = 5;
	private View view;

	public CommentEmptyItem(View view) {
		this.view = view;
	}

	@Override
	public boolean isClickable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return COMMENTEMPTY_TYPE;
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
		if (convertView == null) {
			convertView = view;
		}

		return convertView;
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return view;
	}

	@Override
	public void setLineVisibility(int visibility) {
		// TODO Auto-generated method stub

	}

}
