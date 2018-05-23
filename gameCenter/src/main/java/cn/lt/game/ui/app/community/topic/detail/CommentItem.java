package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.ui.app.community.model.Comment;
import cn.lt.game.ui.app.community.model.Reply;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.topic.detail.CommentView.replyOnclickPositionListener;
import cn.lt.game.ui.app.community.topic.detail.ReplyALLLoadingView.IGetDataComplete;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

public class CommentItem implements ICommentList, IGetDataComplete {
	public static final int COMMENTVIEWTYPE = 1;
	private Comment comment;
	// private UserBaseInfo userBaseInfo ;
	private CommentView commentView;
	private int position;
	private replyOnclickPositionListener positionListener;
	private int visibility = View.VISIBLE;
	private int userId = -1;
	private TopicDetail info;

	public CommentItem(Comment comments) {
		this.comment = comments;
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
		return COMMENTVIEWTYPE;
	}

	@Override
	public Object getData() {
		// TODO Auto-generated method stub
		return comment;
	}

	@Override
	public View createView(Context context, View convertView,
			 int position, TopicDetail info,
			replyOnclickPositionListener positionListener,
			UserBaseInfo UserBaseInfo) {

		// this.userBaseInfo = UserBaseInfo;
		if (UserBaseInfo != null) {
			userId = UserBaseInfo.getId();
		}
		this.position = position;
		this.info = info;
		this.positionListener = positionListener;
		if (convertView == null) {
			convertView = new CommentView(context);
		}
		commentView = (CommentView) convertView;
		updateData( position);

		return convertView;
	}

	private void updateData( int position) {

		commentView.setiGetDataComplete(this);
		commentView.upDate(comment, position,userId,info);
		commentView.hideline(visibility);
		commentView.setOnclickPositionListener(positionListener);
	}

	@Override
	public void onSuccess(List<Reply> list) {
		comment.replies.clear();
		comment.replies.addAll(list);
		comment.reply_count = list.size();
		commentView.upDate(comment, position,  userId,info);
		

	}

	@Override
	public void remove() {

		if (comment.replies.size() >= CommentView.DEFAULTNUM) {
			ArrayList<Reply> list = null;
			list = new ArrayList<Reply>();
			for (int i = 0; i < CommentView.DEFAULTNUM; i++) {
				list.add(comment.replies.get(i));
			}
			comment.replies = list;
		}
		commentView.upDate(comment, position,  userId,info);

	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return commentView;
	}

	@Override
	public void setLineVisibility(int visibility) {

		this.visibility = visibility;
	}

}
