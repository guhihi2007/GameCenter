package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.view.View;

import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.topic.detail.CommentView.replyOnclickPositionListener;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

public class CommentHintItem implements ICommentList {
	public static final int COMMENHINT_TYPE = 4;
	private CommentHintView hintView;
	private String hintString = "还没有人评论";
	private int visibility = View.VISIBLE;

	public CommentHintItem(Context context) {
		hintView = new CommentHintView(context);

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
		return COMMENHINT_TYPE;
	}

	@Override
	public Object getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View createView(Context context, View convertView,
			 int position,TopicDetail info,
			replyOnclickPositionListener positionListener,UserBaseInfo UserBaseInfo) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			hintView = new CommentHintView(context);
		}

		hintView.hintSetVisibility(visibility);
		hintView.setHint(hintString);
		convertView = hintView;
		return convertView;
	}

	@Override
	public View getView() {
		return hintView;
	}

	public void setHint(String string) {
		hintString = string;
	}

	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}


	@Override
	public void setLineVisibility(int visibility) {
		// TODO Auto-generated method stub
		
	}

}
