package cn.lt.game.ui.app.selectedgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.lt.game.R;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.model.GameBaseDetail;

public class DownloadAdAdapter extends BaseAdapter {

	ArrayList<GameBaseDetail> gameBeans = new ArrayList<GameBaseDetail>();
	private LayoutInflater mInflater;
	private Context context;
	private ViewHolder holder = null;
	private boolean[] isChecks =new boolean[9];
	
	public DownloadAdAdapter(Context context,ArrayList<GameBaseDetail> beans)
	{
		this.context = context;
		this.gameBeans = beans;
		mInflater = LayoutInflater.from(context); 
		for (int i = 0; i < isChecks.length; i++) {
			isChecks[i] = true;
		}
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return gameBeans.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return gameBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return gameBeans.get(position).getId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_downloadad_gridview, null);
			holder.root = (RelativeLayout) convertView.findViewById(R.id.item_downloadad_bodyRl);
			holder.logo = (ImageView) convertView.findViewById(R.id.item_downloadad_logoIv);
			holder.name = (TextView) convertView.findViewById(R.id.item_downloadad_nameTv);
			holder.check = (ImageView) convertView.findViewById(R.id.item_downloadad_checkIv);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		GameBaseDetail bean = gameBeans.get(position);
        ImageloaderUtil.loadLTLogo(context,bean.getLogoUrl(),holder.logo);
		holder.name.setText(bean.getName());
		if (isChecks[position]) {
			holder.check.setVisibility(View.VISIBLE);
		}
		else {
			holder.check.setVisibility(View.GONE);
		}
		
		
		
		holder.root.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                isChecks[position] = !isChecks[position];
				notifyDataSetChanged();
				
			}
		});
		
		return convertView;
	}

	
	public final class ViewHolder {  
		public RelativeLayout root;
        public ImageView logo;  
        public ImageView check;  
        public TextView name;  

    }  
}
