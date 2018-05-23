package cn.lt.game.ui.app.gamestrategy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huanju.data.content.raw.info.HjInfoListItem;

import java.util.ArrayList;

import cn.lt.game.R;
import cn.lt.game.lib.util.TimeUtils;

public class StrategyAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<HjInfoListItem> list;

	public StrategyAdapter(Context context, ArrayList<HjInfoListItem> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		inflater = LayoutInflater.from(this.context);
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		HjInfoListItem item = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.strategycenter_listitem,null);
			viewHolder.logo = (ImageView) convertView.findViewById(R.id.strategy_listitem_image);
			viewHolder.title = (TextView) convertView.findViewById(R.id.strategy_listitem_name);
			viewHolder.date = (TextView) convertView.findViewById(R.id.strategy_listitem_date);
			viewHolder.divider = convertView.findViewById(R.id.divider);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.title.setText(item.title);
		viewHolder.date.setText("发布于 " + TimeUtils.getLongtoString(item.ctime));
		if(item.images.size()>0){
			Glide.with(context).load(item.images.get(0)).into(viewHolder.logo);
		}else{
			viewHolder.logo.setImageResource(R.mipmap.img_default_80x80_round);
		}

		if (position == getCount()-1){
			viewHolder.divider.setVisibility(View.VISIBLE);
		} else {
			viewHolder.divider.setVisibility(View.GONE);
		}
	

		return convertView;
	}

	public final class ViewHolder {
		public ImageView logo;
		public TextView title;
		public TextView date;
		public View divider;
	}
}
