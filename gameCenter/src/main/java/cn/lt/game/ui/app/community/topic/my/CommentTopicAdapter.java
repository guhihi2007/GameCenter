package cn.lt.game.ui.app.community.topic.my;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.ui.app.community.model.Comment;
import cn.lt.game.ui.app.community.widget.MyCommentItemView;

public class CommentTopicAdapter extends BaseAdapter {
	private Context mContext;

	private List<Comment> mList = new ArrayList<Comment>();

	public List<Comment> getmList() {
		return mList;
	}

	public void setmList(List<Comment> list) {
		if (list != null) {
			mList.addAll(list);
		}
		this.notifyDataSetChanged();
	}

	public CommentTopicAdapter(Context context, List<Comment> list
			) {
		this.mContext = context;
		setmList(list);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHodler item = null;
		if (convertView == null) {
			item = new ViewHodler();
			convertView = new MyCommentItemView(mContext);
			item.view = convertView;
			convertView.setTag(item);
		} else {
			item = (ViewHodler) convertView.getTag();
		}
		fillItem(item, position);
		return convertView;
	}

	private void fillItem(ViewHodler item, int position) {
		Comment bean = mList.get(position);
		((MyCommentItemView) item.view).setData(bean);
	}

	class ViewHodler {
		public View view;

	}

}
