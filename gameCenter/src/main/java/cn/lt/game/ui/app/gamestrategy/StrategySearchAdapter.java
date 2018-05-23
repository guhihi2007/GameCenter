package cn.lt.game.ui.app.gamestrategy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huanju.data.content.raw.info.HjInfoItem;

import java.util.ArrayList;

import cn.lt.game.R;
import cn.lt.game.lib.util.TimeUtils;

public class StrategySearchAdapter extends BaseAdapter {
	private ArrayList<HjInfoItem> list;
	private LayoutInflater inflater;
	private Context mContext;

	public StrategySearchAdapter(Context context, ArrayList<HjInfoItem> list) {
		// TODO Auto-generated constructor stub
		this.list = list;
		inflater = LayoutInflater.from(context);
		this.list = list;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
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
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		HjInfoItem item = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.strategycenter_listitem,
					null);
			viewHolder.logo = (ImageView) convertView
					.findViewById(R.id.strategy_listitem_image);
			viewHolder.title = (TextView) convertView
					.findViewById(R.id.strategy_listitem_name);
			viewHolder.date = (TextView) convertView
					.findViewById(R.id.strategy_listitem_date);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.title.setText(item.getTitle());
		viewHolder.date.setText("发布于 " + TimeUtils.getLongtoString(item.getCtime()));
		if(item.getImages().size()>0){
			Glide.with(mContext).load(item.getImages().get(0)).into(viewHolder.logo);
		}else{
			viewHolder.logo.setImageResource(R.mipmap.img_default_80x80_round);
		}
		return convertView;

	}
	
	public final class ViewHolder {
		public ImageView logo;
		public TextView title;
		public TextView date;

	}

}
