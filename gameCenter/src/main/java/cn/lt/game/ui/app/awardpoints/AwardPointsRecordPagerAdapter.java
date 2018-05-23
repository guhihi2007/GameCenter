package cn.lt.game.ui.app.awardpoints;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cn.lt.game.base.BaseFragment;
import cn.lt.game.ui.app.awardpoints.awardrecord.AwardRecordFragment;

/**
 * 中奖/积分记录tab适配器
 */
public class AwardPointsRecordPagerAdapter extends FragmentPagerAdapter {
    private String[] mTabTile;

    private BaseFragment[] fragments;


    public AwardPointsRecordPagerAdapter(FragmentManager fm) {
        super(fm);
        mTabTile = new String[]{"中奖记录", "积分记录"};
        fragments = new BaseFragment[]{new AwardRecordFragment(), new PointsRecordFragment()};
    }


    @Override
    public Fragment getItem(int arg0) {
        return fragments[arg0];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTile[position];
    }

    @Override
    public int getCount() {
        return mTabTile.length;
    }

}
