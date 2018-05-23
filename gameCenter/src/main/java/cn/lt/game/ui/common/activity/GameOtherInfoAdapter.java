package cn.lt.game.ui.common.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huanju.data.content.raw.info.HjInfoListItem;

import java.util.ArrayList;

import cn.lt.game.R;
import cn.lt.game.lib.util.TimeUtils;

public class GameOtherInfoAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<HjInfoListItem> list;
	public GameOtherInfoAdapter(Context context ,ArrayList<HjInfoListItem> list) {
		inflater = LayoutInflater.from(context);
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
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	public String getItemTime( int position){
		
		return TimeUtils.getLongtoString(list.get(position).ctime);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		HjInfoListItem item = list.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.game_otherinfo_list_item, null);
			holder.value = (TextView) convertView.findViewById(R.id.game_otherinfo_listitem_value);
			holder.time = (TextView) convertView.findViewById(R.id.game_otherinfo_listitem_time);
			holder.divider = convertView.findViewById(R.id.divider);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(position == getCount()-1) {
			holder.divider.setVisibility(View.VISIBLE);
		} else {
			holder.divider.setVisibility(View.GONE);
		}
		holder.value.setText(item.title.trim());
		holder.time.setText(TimeUtils.getLongtoString(item.ctime));
		return convertView;
	}

	
	public final class ViewHolder {
		public TextView value;
		public TextView time;
		public View divider;
	}

}
