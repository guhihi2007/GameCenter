package cn.lt.game.ui.app.tabbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

public class TabFragmentChangeManager {
    private FragmentManager mFragmentManager;
    private int mContainerViewId;
    /**
     * Fragment切换数组
     */
    private ArrayList<Fragment> mFragments;
    /**
     * 当前选中的Tab
     */
    private int mCurrentTab;

    public TabFragmentChangeManager(FragmentManager fm, int containerViewId, ArrayList<Fragment> fragments) {
        this.mFragmentManager = fm;
        this.mContainerViewId = containerViewId;
        this.mFragments = fragments;
        initFragments();
    }

    /**
     * 初始化fragments
     */
    private void initFragments() {
        for (Fragment fragment : mFragments) {
            mFragmentManager.beginTransaction().add(mContainerViewId, fragment).hide(fragment).commit();
            fragment.setUserVisibleHint(false);
        }

        mFragmentManager.beginTransaction().show(mFragments.get(0)).commit();
        mCurrentTab = 0;
    }

    /**
     * 界面切换控制
     */
    /*public void setFragments(int index) {
        for (int i = 0; i < mFragments.size(); i++) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            Fragment fragment = mFragments.get(i);
            if (i == index) {
                ft.show(fragment);
                fragment.setUserVisibleHint(true);
            } else if(i == mCurrentTab){
                ft.hide(fragment);
                fragment.setUserVisibleHint(false);
            }
            else {
                ft.hide(fragment);
            }
            ft.commit();
        }
        mCurrentTab = index;
    }*/
    public void setFragments(int index) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Fragment fragment = mFragments.get(index);
        Fragment lastFragment = mFragments.get(mCurrentTab);
        ft.hide(lastFragment).show(fragment).commit();
        mCurrentTab = index;
        lastFragment.setUserVisibleHint(false);
        fragment.setUserVisibleHint(true);

    }

    public int getCurrentTab() {
        return mCurrentTab;
    }

    public Fragment getCurrentFragment() {
        return mFragments.get(mCurrentTab);
    }
}