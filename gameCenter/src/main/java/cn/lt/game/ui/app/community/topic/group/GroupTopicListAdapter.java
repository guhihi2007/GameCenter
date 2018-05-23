package cn.lt.game.ui.app.community.topic.group;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.topic.TopicItemWithMoreBtnWidget;

/**
 * 
 * 小组话题列表适配器
 *
 */
public class GroupTopicListAdapter extends BaseAdapter {

	//普通视图
	private static final int TYPE_NORMAL = 0;
	//广告/推广位置
	private static final int TYPE_AD     = 1;

	private Context context;
	private ArrayList<TopicDetail> list = new ArrayList<TopicDetail>();
	private Handler handler = null;

	public GroupTopicListAdapter(Context context) {
		this.context = context;
	}

	public synchronized void setData(List<TopicDetail> list) {
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}

	public synchronized void addData(List<TopicDetail> list) {
		this.list.addAll(list);
		notifyDataSetChanged();
	}

	public  ArrayList<TopicDetail> getList(){
		return list;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}



	@Override
	public int getItemViewType(int position) {
		if(list.get(position).type.equals("advertisement")){
			//广告
			return TYPE_AD;
		}
		return TYPE_NORMAL;
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
	
	public synchronized void clearList() {
        this.list.clear();
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		Log.i("GOOD",position+"***"+convertView);
		switch (getItemViewType(position)){
			case TYPE_NORMAL:
				convertView = setViewNormal(convertView,position);
				break;
			case TYPE_AD:
				convertView = setViewAd(convertView,position);
				break;
			default:
				break;
		}


		return convertView;
	}

	private  View setViewNormal(View convertView,int position){
		if (convertView == null) {
			convertView = new TopicItemWithMoreBtnWidget(context);
		}

		((TopicItemWithMoreBtnWidget) convertView).setData(list.get(position));

		return convertView;
	}

	private View setViewAd(View convertView,int position){
		ViewHolderAd  holderAd ;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_hot_topic_ad, null, false);
			holderAd = new ViewHolderAd(convertView);
			convertView.setTag(holderAd);
		}else{
			holderAd  = (ViewHolderAd) convertView.getTag();
		}
		final TopicDetail detail = list.get(position);

		//头像
		holderAd.name.setText(detail.title);
		holderAd.content.setText(detail.content);
//		ImageLoader.getInstance().display(detail.image, holderAd.bigimage);
		ImageloaderUtil.loadImage(context,detail.image, holderAd.bigimage, false);
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityActionUtils.jumpToByUrl(context,detail.title, detail.link);
			}
		});

		return convertView;

	}


	private static class ViewHolderAd {
		public final ImageView headimage;
		public final TextView  name;
		public final TextView  time;
		public final ImageView bigimage;
		public final TextView content;

		public ViewHolderAd(View root) {
			headimage = (ImageView) root.findViewById(R.id.head_image);
			name = (TextView) root.findViewById(R.id.name);
			time = (TextView) root.findViewById(R.id.time);
			bigimage = (ImageView)root.findViewById(R.id.big_image);
			content = (TextView)root.findViewById(R.id.content);
		}
	}
}