package cn.lt.game.ui.app.gamestrategy;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cn.lt.game.R;

public class StrategyPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private int mTabTile[] = {R.string.newstrategy, R.string.strategycenter};
    private BaseStrategyFragment[] fragments;

    public StrategyPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        // TODO Auto-generated constructor stub
        mContext = context;
        fragments = new BaseStrategyFragment[]{GameNewStrategyFrament.newInstance(context.getResources().getString(R.string.newstrategyID)), GameStrategyCenterFrament.newInstance(context.getResources().getString(R.string.hotstrategyID))};
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
