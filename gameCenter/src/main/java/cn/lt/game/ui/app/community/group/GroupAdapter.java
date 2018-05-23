package cn.lt.game.ui.app.community.group;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.ui.app.community.model.Group;
import cn.lt.game.ui.app.community.widget.GroupView;
import cn.lt.game.ui.app.community.widget.GroupView.GroupViewType;

/***
 * 
 * @author tiantian
 * @des 推荐小组适配器
 */
public class GroupAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Group> list = new ArrayList<Group>();
	private GroupViewType viewType;

	public GroupAdapter(Context context,GroupViewType type) {
		this.context = context;
		this.viewType = type;
	}

	public synchronized void setData(List<Group> list) {
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	
	public synchronized void clearList() {
        this.list.clear();
    }

	public synchronized void addData(List<Group> list) {
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new GroupView(context,viewType);
		}
		((GroupView) convertView).fillView(list.get(position));
		return convertView;
	}
}
