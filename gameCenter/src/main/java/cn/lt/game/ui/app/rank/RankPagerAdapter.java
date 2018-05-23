package cn.lt.game.ui.app.rank;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.lt.game.ui.app.rank.RankFragment.SelectorTabListener;


public class RankPagerAdapter extends FragmentStatePagerAdapter {
	
	private ArrayList<RankFragment> rankList ;
	private Context mContext;

	public RankPagerAdapter(FragmentManager fm, Context context , SelectorTabListener selectorListener) {
		super(fm);
		mContext = context;
		RankFragmentPageBuilder.instance().setSelectorTabListener(selectorListener);
		rankList = RankFragmentPageBuilder.instance().createFragments();
	}

	@Override
	public Fragment getItem(int position) {
		return rankList.get(position);
	}
	
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(RankTabInfoProvide.instance().getTitles()[position]);
    }

	@Override
	public int getCount() {
		return RankTabInfoProvide.instance().getTitles().length;
	}

	public void setUserVisibleHint(int position, boolean userVisibleHint) {
		if (position < 0 || position >= rankList.size()) {
			return;
		}

		if (rankList.get(position).isAdded()) {
			rankList.get(position).setUserVisibleHint(userVisibleHint);
		}
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
//		super.destroyItem(container, position, object);
	}
}
