package cn.lt.game.ui.app.community.face;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;

public class FaceView extends LinearLayout {

	Context context;
	ViewPager vp_content; //
	LinearLayout ll_navigate;

	static String[] faceUnicode = { "\ud83d\ude01", "\ud83d\ude02",
			"\ud83d\ude03", "\ud83d\ude04", "\ud83d\ude05", "\ud83d\ude06",
			"\ud83d\ude09", "\ud83d\ude0a", "\ud83d\ude0b", "\ud83d\ude0c",
			"\ud83d\ude0d", "\ud83d\ude0f", "\ud83d\ude12", "\ud83d\ude13",
			"\ud83d\ude14", "\ud83d\ude16", "\ud83d\ude18", "\ud83d\ude1a",
			"\ud83d\ude1c", "\ud83d\ude1d", "\ud83d\ude1e", "\ud83d\ude20",
			"\ud83d\ude21", "\ud83d\ude22", "\ud83d\ude23", "\ud83d\ude24",
			"\ud83d\ude25", "\ud83d\ude28", "\ud83d\ude29", "\ud83d\ude2a",
			"\ud83d\ude2b", "\ud83d\ude2d", "\ud83d\ude30", "\ud83d\ude31",
			"\ud83d\ude32", "\ud83d\ude33", "\ud83d\ude35", "\ud83d\ude37",
			"\ud83d\ude38", "\ud83d\ude39", "\ud83d\ude3a", "\ud83d\ude3b",
			"\ud83d\ude3c", "\ud83d\ude3d", "\ud83d\ude3e", "\ud83d\ude3f",
			"\ud83d\ude40", "\ud83d\ude45", "\ud83d\ude46", "\ud83d\ude47",
			"\ud83d\ude48", "\ud83d\ude49", "\ud83d\ude4a", "\ud83d\ude4b",
			"\ud83d\ude4c", "\ud83d\ude4d", "\ud83d\ude4e", "\ud83d\ude4f" };
	
	// 一页表情数（固定）
	static int one_faces_sum = 21;
	// 页数（表情总数除一页表情数）
	int index_sum = faceUnicode.length % one_faces_sum > 0 ? faceUnicode.length
			/ one_faces_sum + 1 : faceUnicode.length / one_faces_sum;

	int[] one_faces; // 单页表情

	ArrayList<GridView> gridViews; //
	Work work;

	public FaceView(final Context context, AttributeSet attrs, Work work) {
		super(context, attrs);
		this.context = context;
		this.work = work;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.topic_layout_faceview, this);
		vp_content = (ViewPager) findViewById(R.id.vp_content);
		ll_navigate = (LinearLayout) findViewById(R.id.ll_navigate);
		initViewPager();
	}

	private void initViewPager() {

		LayoutInflater inflater = LayoutInflater.from(context);

		gridViews = new ArrayList<GridView>();
		GridView gView;

		// 生成表情
		for (int i = 0; i < index_sum; i++) {
			gView = (GridView) inflater.inflate(
					R.layout.topic_layout_faceview_gridview, null);

			List<Map<String, String>> listItems = new ArrayList<Map<String, String>>();
			final List<String> listItems_str = new ArrayList<String>();
			// final List<Integer> listItems_id = new ArrayList<Integer>();

			// 生成一页表情
			for (int j = i * one_faces_sum; j < (i + 1) * one_faces_sum; j++) {
				if (j >= faceUnicode.length) { // 超出表情总数时，终止
					break;
				}
				// System.out.println("添加第" + j + "个表情");
				Map<String, String> listItem = new HashMap<String, String>();
				listItem.put("image", faceUnicode[j]);
				listItems.add(listItem);

				listItems_str.add(faceUnicode[j]);
				// listItems_id.add(all_faces[j]);
			}
			SimpleAdapter simpleAdapter = new SimpleAdapter(context, listItems,
					R.layout.topoc_item_faceview_gridview,
					new String[] { "image" }, new int[] { R.id.iv_face });
			gView.setAdapter(simpleAdapter);
			gView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					work.onClick(listItems_str.get(arg2));
				}
			});

			gridViews.add(gView);

			// 每产生一页表情，增加一个导航点
			ImageView view = new ImageView(context);
			view.setBackgroundResource(R.drawable.insoft_point_selector);
			view.setEnabled(false);
			ll_navigate.addView(view);
			if (i == 0) { // 第一个为选中
				view.setEnabled(true);
			}
		}

		// 填充ViewPager的数据适配器
		PagerAdapter pagerAdapter = new PagerAdapter() {
			@Override
			public int getCount() {
				return gridViews.size();
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView((View) object);
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				View view = gridViews.get(position);
				container.addView(view);
				return view;
			}
		};
		vp_content.setAdapter(pagerAdapter);
		vp_content
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int arg0) {
						// TODO Auto-generated method stub
						for (int i = 0; i < ll_navigate.getChildCount(); i++) {
							ImageView view = (ImageView) ll_navigate
									.getChildAt(i);
							if (i == arg0) {
								view.setEnabled(true);
								continue;
							}
							view.setEnabled(false);
						}
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPageScrollStateChanged(int arg0) {
						// TODO Auto-generated method stub

					}
				});
	}

	public interface Work {
		void onClick(String item_str);
	}
}
