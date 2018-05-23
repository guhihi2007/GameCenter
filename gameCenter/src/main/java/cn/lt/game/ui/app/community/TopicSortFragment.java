package cn.lt.game.ui.app.community;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.ui.app.community.model.Category;
//话题分类Fragment
@SuppressLint("ValidFragment")
public class TopicSortFragment extends BaseFragment {
	private String id;
	private View view;
	private ListView lv;
	private MyAdapter myadapter;
	private LayoutInflater lf;
	private String sortId;
	private ArrayList<Category> categories;

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		lf = LayoutInflater.from(activity);
	}

	public String getSortId() {
		return sortId;
	}

	public void setData(String sortid) {
		this.sortId = sortid;
	}

	public static TopicSortFragment newInstance(String id,
			ArrayList<Category> categories) {
		TopicSortFragment myFragment = new TopicSortFragment();
		Bundle args = new Bundle();
		args.putString("id", id);
		args.putSerializable("categories", categories);
		myFragment.setArguments(args);
		return myFragment;
	}

	@Override
	public void setPageAlias() {

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.id = getArguments().getString("id");
		this.categories = (ArrayList<Category>) getArguments().getSerializable(
				"categories");
		if (null != view) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (null != parent) {
				parent.removeView(view);
			}
		} else {
			view = inflater.inflate(R.layout.topicsort_fragment, container,
					false);
			initView();
			initListener();
		}
		return view;
	}

	private void initListener() {
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				for (int i = 0; i < categories.size(); i++) {
					categories.get(i).bolean = false;
				}
				categories.get(position).bolean = true;
				sortId = "" + categories.get(position).id;
				myadapter.notifyDataSetChanged();

			}
		});
	}

	private void initView() {
		lv = (ListView) view.findViewById(R.id.lv);
		myadapter = new MyAdapter();
		if (sortId == null) {
			if (categories == null) {
				return;
			}
			for (int i = 0; i < categories.size(); i++) {
				categories.get(i).bolean = false;
			}

			if (categories.size() == 0) {
				return;
			}
			sortId = "" + categories.get(0).id;
			categories.get(0).bolean = true;
		} else {
			if (categories == null) {
				return;
			}
			for (int i = 0; i < categories.size(); i++) {
                categories.get(i).bolean = Integer.toString(categories.get(i).id).equals(sortId);
			}
		}
		lv.setAdapter(myadapter);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (categories == null) {
				return 0;
			}
			return categories.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Category cs = categories.get(position);
			ViewHolder viewholder;
			if (convertView == null) {
				viewholder = new ViewHolder();
				convertView = lf.inflate(R.layout.topic_sort_item, null);
				viewholder.imageView = (ImageView) convertView
						.findViewById(R.id.imageview);
				viewholder.textView = (TextView) convertView
						.findViewById(R.id.textview);
				convertView.setTag(viewholder);
			} else {
				viewholder = (ViewHolder) convertView.getTag();
			}
			if (cs.bolean) {
				viewholder.imageView
						.setBackgroundResource(R.mipmap.topic_sort_yes);
			} else {
				viewholder.imageView
						.setBackgroundResource(R.mipmap.uncheck);
			}
			viewholder.textView.setText(cs.title);
			return convertView;
		}
	}

	private class ViewHolder {
		ImageView imageView;
		TextView textView;
	}

}
