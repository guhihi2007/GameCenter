package cn.lt.game.ui.app.voucher;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cn.lt.game.base.BaseFragment;

/**
 * Created by Erosion on 2018/1/15.
 */

public class CouponRecordAdapter extends FragmentPagerAdapter {

    private String[] mTabTile;

    private BaseFragment[] fragments;

    public CouponRecordAdapter(FragmentManager fm) {
        super(fm);
        mTabTile = new String[]{"我的代金券", "兑换代金券"};
        fragments = new BaseFragment[]{new MyVoucherFragment(), new ExchangeVoucherFragment()};
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return mTabTile.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTile[position];
    }
}
