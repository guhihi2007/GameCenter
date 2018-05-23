package cn.lt.game.ui.app.community.topic.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.lt.game.R;
import cn.lt.game.ui.app.community.model.Category;

public class SpinerPopAdapter extends BaseAdapter {
	private Context context;
	private List<Category> list;

	// 下拉列表里面的回调处理
	public interface IOnItemSelectListener {
		void onItemClick(int pos);
	}

    // 标题栏里面的下拉菜单回调事件处理
	public interface IOnMenuSelectListener {
		void onMenuClick(int pos);
	}

	public SpinerPopAdapter(Context context, List<Category> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list == null ? 0 : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.spiner_item_layout, null);
			viewHolder = new ViewHolder();
			viewHolder.mTextView = (TextView) convertView.findViewById(R.id.tv_title);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Category bean = list.get(position);
		viewHolder.mTextView.setText(bean.title);
		return convertView;
	}

	public static class ViewHolder {
		public TextView mTextView;
	}
}
