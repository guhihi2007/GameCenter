package cn.lt.game.ui.app.community;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cn.lt.game.R;
import cn.lt.game.ui.app.community.group.GroupFragment;
import cn.lt.game.ui.app.community.topic.TopicListFragment;
import cn.lt.game.ui.app.community.topic.my.MyComFragment;

public class CommunityAdapter extends FragmentPagerAdapter {

	private Context mContext;
	private int mTabTile[] = { R.string.hot_topic, R.string.browsegroups, R.string.my_community };
	private CommunityBaseFragment[] fragments;

	public CommunityAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.mContext = context;
		fragments = new CommunityBaseFragment[] { 
				TopicListFragment.newInstance(R.string.new_topic),
				 GroupFragment.newInstance(R.string.browsegroups),
				 MyComFragment.newInstance(context.getResources().getString(R.string.my_community)) };
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
