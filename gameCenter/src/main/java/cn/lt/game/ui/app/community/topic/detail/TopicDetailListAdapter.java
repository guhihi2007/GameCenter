package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.topic.detail.CommentView.replyOnclickPositionListener;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

public class TopicDetailListAdapter extends BaseAdapter {
	private static final int TYPECOUNT = 8;
	private Context context;
	private ArrayList<ICommentList> itemList;
	private replyOnclickPositionListener positionListener;
	private UserBaseInfo userBaseInfo;
	private TopicDetail info;

	public TopicDetailListAdapter(Context context,
			ArrayList<ICommentList> listItem,
			replyOnclickPositionListener positionListener,
			UserBaseInfo UserBaseInfo) {
		this.context = context;
		this.positionListener = positionListener;
		this.userBaseInfo = UserBaseInfo;
		itemList = listItem;

	}

	public TopicDetail getInfo() {
		return info;
	}

	public void setInfo(TopicDetail info) {
		this.info = info;
	}

	@Override
	public int getCount() {
		return itemList == null ? 0 : itemList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return TYPECOUNT;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return itemList.get(position).getType();
	}

	@Override
	public boolean isEnabled(int position) {
		return itemList.get(position).isClickable();
	}

	public void setUserBaseInfo(UserBaseInfo userBaseInfo) {
		this.userBaseInfo = userBaseInfo;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ICommentList item = itemList.get(position);

		if (position == itemList.size() - 1) {
			item.setLineVisibility(View.GONE);
		} else {
			item.setLineVisibility(View.VISIBLE);
		}

		convertView = item.createView(context, convertView,
				position, info, positionListener, userBaseInfo);

		return convertView;

	}

}
