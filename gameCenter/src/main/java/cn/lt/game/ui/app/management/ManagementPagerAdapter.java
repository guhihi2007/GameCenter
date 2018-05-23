package cn.lt.game.ui.app.management;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import cn.lt.game.R;

public class ManagementPagerAdapter extends FragmentPagerAdapter {
	
	private Context mContext;
	private int mTabTile[] = {R.string.install_management, R.string.update ,R.string.installed,};
	private Fragment fragments[] = {new DownloadManagerFragment(), new UpgradeFragment(),new InstalledFragment()};
	

	public ManagementPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		mContext = context;
	}

	@Override
	public Fragment getItem(int position) {
		return fragments[position];
	}
	
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(mTabTile[position]);
    }

	@Override
	public int getCount() {
		return mTabTile.length;
	}

	public void setUserVisibleHint(int position, boolean userVisibleHint) {
		if (position < 0 || position >= fragments.length) {
			return;
		}

		if (fragments[position].isAdded()) {
			fragments[position].setUserVisibleHint(userVisibleHint);
		}
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
	}
}
