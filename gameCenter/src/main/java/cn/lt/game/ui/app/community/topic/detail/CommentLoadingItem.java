package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.view.View;

import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.topic.detail.CommentView.replyOnclickPositionListener;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

public class CommentLoadingItem implements ICommentList {
	public static final int COMMENTLOADING = 3;
	private CommentLoadingView commentLoadingView;

    public CommentLoadingItem(Context context) {

		commentLoadingView = new CommentLoadingView(context);
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
		return COMMENTLOADING;
	}

	@Override
	public Object getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View createView(Context context, View convertView,
			 int position, TopicDetail info,
			replyOnclickPositionListener positionListener,UserBaseInfo UserBaseInfo) {

		if (convertView == null) {
			commentLoadingView = new CommentLoadingView(context);
		}
		
		convertView = commentLoadingView;

		return convertView;
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return commentLoadingView;
	}

	public void show() {
		if (commentLoadingView != null) {
			commentLoadingView.show();
		}

	}

	public void hide() {
		if (commentLoadingView != null) {
			commentLoadingView.hide();
		}
	}


	@Override
	public void setLineVisibility(int visibility) {
		// TODO Auto-generated method stub
		
	}
}
