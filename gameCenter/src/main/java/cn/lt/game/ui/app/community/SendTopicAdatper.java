package cn.lt.game.ui.app.community;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.ui.app.community.model.Category;

public class SendTopicAdatper extends FragmentPagerAdapter {

	private Context mContext;
	private int mTabTile[] = { R.string.topic_title, R.string.topic_content,
			R.string.topic_sort };
	private BaseFragment[] fragments;

	public SendTopicAdatper(FragmentManager fm, Context context,
			ArrayList<Category> al) {
		super(fm);
		this.mContext = context;
		fragments = new BaseFragment[] {
				TopicTitleFragment.newInstance(context.getResources()
						.getString(R.string.topic_title)),
				TopicContentFragment.newInstance(context.getResources()
						.getString(R.string.topic_content)),
				TopicSortFragment.newInstance(
						context.getResources().getString(R.string.topic_sort),
						al) };
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments[arg0];
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mContext.getResources().getString(mTabTile[position]);
	}

	@Override
	public int getCount() {
		return mTabTile.length;
	}
}
