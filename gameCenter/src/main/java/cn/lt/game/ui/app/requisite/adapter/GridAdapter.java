package cn.lt.game.ui.app.requisite.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.ui.app.requisite.RequisiteDialog.RequisiteItem;
import cn.lt.game.ui.app.requisite.widget.RequisiteGameView;

public class GridAdapter extends BaseAdapter {

	private List<RequisiteItem> mList;

	private Context mContext;

	public GridAdapter(Context context, List<RequisiteItem> list) {
		this.mContext = context;
		setList(list);
	}

	public void setList(List<RequisiteItem> list) {
		if (list == null) {
			mList = new ArrayList<>();
		}
		mList = list;
		notifyDataSetChanged();
	}
	
	public List<RequisiteItem> getList() {
		return mList;
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
		GameHolder gameView;

		if (convertView == null) {
			gameView = new GameHolder();
			convertView = new RequisiteGameView(mContext);
			gameView.view = (RequisiteGameView) convertView;
			convertView.setTag(gameView);
		} else {
			gameView = (GameHolder) convertView.getTag();
		}
		fillItem(gameView, position);
		return convertView;
	}

	private void fillItem(GameHolder gameView, int position) {
		try {
			gameView.view.fillView(mList.get(position));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class GameHolder {
		public RequisiteGameView view;
	}

}
