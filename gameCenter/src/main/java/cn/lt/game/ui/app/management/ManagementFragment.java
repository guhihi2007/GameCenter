package cn.lt.game.ui.app.management;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.event.RedPointEvent;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.StatusBarUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.redpoints.RedPointsViewUtils;
import de.greenrobot.event.EventBus;


public class ManagementFragment extends BaseFragment {
    private TextView downloadManagementRedPoint, upgradeRedPoint;
    private SlidingTabLayout indicator;
    private ViewPager pager;
    private MyApplication myApplication;
    private ManagementPagerAdapter adapter;
    private View mRoot;
    private Context mContext;
    private View mRedPoint;
    private int mCurrentPage;

    public static final int POSITION_DOWNLOAD_MANAGEMENT = 0;
    public static final int POSITION_UPGRADE = 1;
    public static final int POSITION_INSTALLED = 2;

    @Override
    public void setPageAlias() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApplication = (MyApplication) getActivity().getApplication();
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.activity_management, container, false);
        View statusBar = mRoot.findViewById(R.id.status_bar);
        StatusBarUtils.showSelfStatusBar(mContext,statusBar);
        return mRoot;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    public void setCurrentPosition(int item) {
        if (item < 0 || item > 2) {
            item = 0;
        }
        if (indicator == null) {
            mCurrentPage = item;
        } else {
            indicator.setCurrentTab(item);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.i(LogTAG.HTAG,"ManagementFragment"+isVisibleToUser);
        EventBus.getDefault().post("updateView");

        if (isVisibleToUser && mRoot != null) {
            setRedPoint();
            RedPointsViewUtils.initTopManagerRedPoints(mRedPoint,mContext);

        }
    }

    private void setRedPoint() {
        if (adapter != null) {
            adapter.setUserVisibleHint(pager.getCurrentItem(), true);
        }
        downloadManagementRedPoint.setVisibility(myApplication.getNewGameDownload() ? View.VISIBLE : View.GONE);
        upgradeRedPoint.setVisibility(myApplication.getNewGameUpdate() ? View.VISIBLE : View.GONE);

        int position = pager.getCurrentItem();
        if (position == POSITION_DOWNLOAD_MANAGEMENT) {
            downloadManagementRedPoint.setVisibility(View.GONE);
            myApplication.setNewGameDownload(false);
        } else if (position == POSITION_UPGRADE) {
            upgradeRedPoint.setVisibility(View.GONE);
            myApplication.setNewGameUpdate(false);
        }

        setHomeRedPoint();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init_point();
        adapter = new ManagementPagerAdapter(getChildFragmentManager(), mContext);
        pager = (ViewPager) mRoot.findViewById(R.id.pager);
        pager.setAdapter(adapter);
        mRedPoint = mRoot.findViewById(R.id.tv_titleBar_redPoint);
        RedPointsViewUtils.initTopManagerRedPoints(mRedPoint,mContext);
        indicator = (SlidingTabLayout) mRoot.findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        pager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == POSITION_DOWNLOAD_MANAGEMENT) {
                    downloadManagementRedPoint.setVisibility(View.GONE);
                    myApplication.setNewGameDownload(false);
                } else if (position == POSITION_UPGRADE) {
                    upgradeRedPoint.setVisibility(View.GONE);
                    myApplication.setNewGameUpdate(false);
                }
                setHomeRedPoint();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        indicator.setCurrentTab(mCurrentPage);

        setRedPoint();
    }

    private void setHomeRedPoint() {
        if (myApplication.getNewGameDownload() || myApplication.getNewGameUpdate()) {
            EventBus.getDefault().post(new RedPointEvent(true));
        } else {
            EventBus.getDefault().post(new RedPointEvent(false));
        }
    }

    /* 设置小红点位置 */
    private void init_point() {
        downloadManagementRedPoint = (TextView) mRoot.findViewById(R.id.management_redPoint1);
        upgradeRedPoint = (TextView) mRoot.findViewById(R.id.management_redPoint2);

        int screenWidth = Utils.getScreenWidth(mContext);
        int tmp = (int) (screenWidth / 3.0);
        int size = DensityUtil.dip2px(mContext, 6);
        int margin = DensityUtil.dip2px(mContext, 8);
        int margin9 = DensityUtil.dip2px(mContext, 9);

        RelativeLayout.LayoutParams pPointLayoutParams = new RelativeLayout.LayoutParams(size, size);
        pPointLayoutParams.setMargins(tmp - size * 4, margin, 0, 0);
        downloadManagementRedPoint.setLayoutParams(pPointLayoutParams);

        RelativeLayout.LayoutParams iPointLayoutParams = new RelativeLayout.LayoutParams(size, size);
        iPointLayoutParams.setMargins(screenWidth / 2 + margin9 * 2, margin, 0, 0);
        upgradeRedPoint.setLayoutParams(iPointLayoutParams);


    }


}
