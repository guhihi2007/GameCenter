package cn.lt.game.ui.app.gamegift.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.ui.app.gamegift.AllGiftFragment;
import cn.lt.game.ui.app.gamegift.GiftCenterFragment;
import cn.lt.game.ui.app.gamegift.GiftMineFragment;

public class GiftHomeAdapter extends FragmentPagerAdapter{
	private Context mContext;
	private int mTabTile[] = { R.string.packagecenter, R.string.allGift, R.string.mypackage};
	private BaseFragment[] fragments;

	public GiftHomeAdapter(FragmentManager fm,Context context) {
		super(fm);
		// TODO Auto-generated constructor stub
		mContext = context;
		fragments = new BaseFragment[] {
				new GiftCenterFragment(),
				new AllGiftFragment(),
				new GiftMineFragment() };
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
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
