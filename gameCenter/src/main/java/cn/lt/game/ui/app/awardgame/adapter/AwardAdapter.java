package cn.lt.game.ui.app.awardgame.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author chengyong
 * @time 2017/6/1 14:18
 * @des ${TODO}
 */

public class AwardAdapter extends FragmentPagerAdapter {

    private  Context mContext;
    private  String[] mTabTile;
    private List<Fragment> mFragments;

    public AwardAdapter(FragmentManager supportFragmentManager, Context context,
                        List<Fragment> mFragments, String[] mTabTile) {
        super(supportFragmentManager);
        mContext = context;
        this.mTabTile=mTabTile;
        this.mFragments=mFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTile[position];
    }
}
