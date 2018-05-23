package cn.lt.game.ui.app.rank;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.SlidingTabLayout;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.StatusBarUtils;
import cn.lt.game.lib.util.redpoints.RedPointsCallback;
import cn.lt.game.lib.util.redpoints.RedPointsViewUtils;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.ui.app.rank.RankFragment.SelectorTabListener;
import de.greenrobot.event.EventBus;

public class RankMainFragment extends BaseFragment implements OnPageChangeListener, SelectorTabListener, RedPointsCallback{

    public static Boolean isScroll = false;
    ViewPager mViewPager;
    private RankPagerAdapter adapter;
    private SlidingTabLayout indicator;
    private int indexPage = 0;
    private int state = -1;
    private int tabCnt = 0;
    private View mRoot;
    private Context mContext;
    private View mRedPoint;
    @Override
    public void setPageAlias() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.activity_rank, container, false);
        mContext = getActivity();
        adapter = new RankPagerAdapter(getChildFragmentManager(), mContext, this);
        tabCnt = adapter.getCount();
        mViewPager = (ViewPager) mRoot.findViewById(R.id.pager);
        mRedPoint = mRoot.findViewById(R.id.tv_titleBar_redPoint);
        View statusBar = mRoot.findViewById(R.id.status_bar);
        StatusBarUtils.showSelfStatusBar(mContext,statusBar);
        RedPointsViewUtils.initTopManagerRedPoints(mRedPoint,mContext);

        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);
        indicator = (SlidingTabLayout) mRoot.findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(this);
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setCurrentPosition(int item) {
        if (item < 0 || item > 3) {
            item = 0;
        }
        indicator.setCurrentTab(item);
    }

    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.i(LogTAG.HTAG,"RankMainFragment"+isVisibleToUser);
        MyApplication.application.rankMainVisible = isVisibleToUser;
        LogUtils.e("ccc", "setUserVisibleHint中  最大排行可见吗？" + isVisibleToUser + "==time:" + System.currentTimeMillis());
        EventBus.getDefault().post("updateView");
        if (adapter != null) {
            adapter.setUserVisibleHint(mViewPager.getCurrentItem(), (isVisibleToUser && mRoot != null));
        }
//        if(isVisibleToUser) {
//            RedPointsViewUtils.initTopManagerRedPoints(mRedPoint,mContext);
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int page;
        if (state < 0) {
            page = indexPage;
        } else {
            page = state;
        }
        indicator.setCurrentTab(page);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onPageScrollStateChanged(int arg0) {
        isScroll = false;
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        isScroll = true;
    }

    @Override
    public void onPageSelected(int arg0) {
        indexPage = arg0;
    }

    @Override
    public void selectTab() {
        try {
            RefreshAndLoadMoreListView pullToRefreshListView = ((RankFragment) adapter.getItem(indexPage)).getListView();
            if (pullToRefreshListView != null) {
                pullToRefreshListView.onLoadMoreComplete();
                pullToRefreshListView.getmListView().setSelection(0);
            }
            indexPage++;
            if (indexPage == tabCnt) {
                indexPage = 0;
                indicator.arrowScroll(2);
            } else {
                indicator.arrowScroll(1);
            }
            indicator.setCurrentTab(indexPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshTopManageRedPoints() {
        RedPointsViewUtils.initTopManagerRedPoints(mRedPoint,mActivity);
    }
}