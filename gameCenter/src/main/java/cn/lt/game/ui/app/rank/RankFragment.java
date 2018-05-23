package cn.lt.game.ui.app.rank;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView.OnLoadMoreListener;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.pageunits.PageMultiUnitsReportManager;
import cn.lt.game.ui.app.adapter.LTBaseAdapter;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListenerImpl;
import cn.lt.game.ui.app.adapter.parser.NetDataAddShell;

@SuppressLint("ValidFragment")
public class RankFragment extends BaseFragment implements RetryCallBack, SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener, RefreshAndLoadMoreListView.IOnScrollStateChanged {
    private Activity mActivity;
    private String mTabType = "";
    private View mRootView;
    private RefreshAndLoadMoreListView listView;
    private NetWorkStateView netWorkStateView;
    private LTBaseAdapter mAdapter;
//    private ILoadingLayout endLabels;
    /**
     * 用于记录从服务器获取数据的最后一个的位置，初次请求时为0；
     */
    private int mlastPagePositionOfNetData = 0;
    private int mTotalPage;
    private int mCurrPage = 1;
    private BaseOnclickListener mClickListener;
    private SelectorTabListener selectorTabListener;
    //4个上报数据集
    private List<ItemData<? extends BaseUIModule>> saveTemp_offline = new ArrayList<>();
    private List<ItemData<? extends BaseUIModule>> saveTemp_online = new ArrayList<>();
    private List<ItemData<? extends BaseUIModule>> saveTemp_hot = new ArrayList<>();
    private List<ItemData<? extends BaseUIModule>> saveTemp_new = new ArrayList<>();
    private boolean isVisibleToUser;
    private SelectorTabRunnable selectorRunnable = new SelectorTabRunnable();

    public static RankFragment newInstance(String type) {
        RankFragment newFragment = new RankFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        newFragment.setArguments(bundle);
        return newFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mTabType = args.getString("type");
        mActivity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mTabType = savedInstanceState.getString("Type");
        }
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_rank, container, false);
        mClickListener = new BaseOnclickListenerImpl(mActivity, getPageAlias());
        initView();
        LogUtils.d("yyy", "onCreateView  getUserVisibleHint？==" + getUserVisibleHint());
        return mRootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCurrPage = 1;
        netWorkStateView.showLoadingBar();
        loadData(false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void setPageAlias() {
        if ("offline".equals(mTabType)) {
            setmPageAlias(Constant.PAGE_RANK_OFFLINE);
        } else if ("online".equals(mTabType)) {
            setmPageAlias(Constant.PAGE_RANK_ONLINE);
        } else if ("hot".equals(mTabType)) {
            setmPageAlias(Constant.PAGE_RANK_HOT);
        } else if ("new".equals(mTabType)) {
            setmPageAlias(Constant.PAGE_RANK_NEWS);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.i(LogTAG.HTAG,"RankFragment"+isVisibleToUser+getPageAlias());
        this.isVisibleToUser = isVisibleToUser;
        LogUtils.e("ccc", "排行小页面可见吗？：" + isVisibleToUser + "==父排行可见吗？：" + MyApplication.application.rankMainVisible + "==time:" + System.currentTimeMillis());
        if (isVisibleToUser && MyApplication.application.rankMainVisible) {
            try {
                LogUtils.e("ccc", "getUserVisibleHint？==排行即将上报的页面是：" + getPageAlias());
                if (!MyApplication.application.mIsFromNotification) { //过滤通知过来的
                    PageMultiUnitsReportManager.getInstance().buildPageUnits(getTempData(), listView, getPageAlias(), "", "", "", "");
                }
                MyApplication.application.mIsFromNotification = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        resetLayout();
        clearStatData();
        super.onDestroyView();
    }

    // 初始化组件
    private void initView() {
        listView = (RefreshAndLoadMoreListView) mRootView.findViewById(R.id.rank_listView);
        netWorkStateView = (NetWorkStateView) mRootView.findViewById(R.id.rank_netwrolStateView);
        netWorkStateView.setRetryCallBack(this);
        View view = new View(mActivity);
        view.setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.margin_size_8dp));
        listView.getmListView().addHeaderView(view);
        listView.setmOnRefrshListener(this);
        listView.setOnLoadMoreListener(this);
        listView.setMyOnScrollListener(this);
        mAdapter = new LTBaseAdapter(mActivity, mClickListener);
        listView.setAdapter(mAdapter, false);
    }


    public void setSelectorTabListener(SelectorTabListener selectorTabListener) {
        this.selectorTabListener = selectorTabListener;
    }

    // 无网络重试回调
    @Override
    public void retry() {
        resetLayout();
        loadData(false);
    }

    public RefreshAndLoadMoreListView getListView() {
        return listView;
    }

    public void setListView(RefreshAndLoadMoreListView listView) {
        this.listView = listView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("Type", mTabType);
        super.onSaveInstanceState(outState);
    }

    private void setPage(int loadingPage) {
        if (loadingPage == 1 && mAdapter != null) {
            mAdapter.resetList();
        }
        mCurrPage = loadingPage + 1;
        if (loadingPage >= mTotalPage) {
            listView.setCanLoadMore(false);
        } else {
            listView.setCanLoadMore(true);
        }
    }

    private void setMlastPagePositionOfNetData(List<ItemData<? extends BaseUIModule>> temp) {
        try {
            if (temp != null && temp.size() > 0) {
                ItemData item = temp.get(temp.size() - 1);
                this.mlastPagePositionOfNetData = item.getPos();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean fromRefresh;

    // 获取数据
    public void loadData(final boolean isFromRefresh) {
        this.fromRefresh = isFromRefresh;
        final Map<String, String> params = new HashMap<>();
        params.put("type", mTabType);
        params.put("page", String.valueOf(mCurrPage));
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.GAMES_RANK_URI, params, new WebCallBackToObject<UIModuleList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                listView.onLoadingFailed();
                if (mCurrPage == 1) {
                    if (!fromRefresh) netWorkStateView.showNetworkFailLayout();
                }
            }

            @Override
            protected void handle(UIModuleList info) {
                mTotalPage = getLastPage();
                listView.onLoadMoreComplete();
                setPage(Integer.valueOf(params.get("page")));
                List<ItemData<? extends BaseUIModule>> temp = NetDataAddShell.wrapModuleList(info, mlastPagePositionOfNetData);
                addReportData(temp, isFromRefresh);
                setMlastPagePositionOfNetData(temp);
                mAdapter.addList(temp);
                listView.setVisibility(View.VISIBLE);
                netWorkStateView.setVisibility(View.GONE);
                LogUtils.i("pppp", getPageAlias() + "可见吗==>" + isVisibleToUser);
                if (isVisibleToUser && MyApplication.application.rankMainVisible) {
                    MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PageMultiUnitsReportManager.getInstance().buildPageUnits(getTempData(), listView, getPageAlias(), "", "", "", "");
                        }
                    }, 500);  //才能取到可见的position
                }
            }
        });
    }

    private void addReportData(List<ItemData<? extends BaseUIModule>> temp, boolean isFromRefresh) {
        if ("offline".equals(mTabType)) {
            if (isFromRefresh) saveTemp_offline.clear();
            saveTemp_offline.addAll(temp);
        } else if ("online".equals(mTabType)) {
            if (isFromRefresh) saveTemp_online.clear();
            saveTemp_online.addAll(temp);
        } else if ("hot".equals(mTabType)) {
            if (isFromRefresh) saveTemp_hot.clear();
            saveTemp_hot.addAll(temp);
        } else if ("new".equals(mTabType)) {
            if (isFromRefresh) saveTemp_new.clear();
            saveTemp_new.addAll(temp);
        }
    }

    private void clearStatData() {
        if ("offline".equals(mTabType)) {
            saveTemp_offline.clear();
        } else if ("online".equals(mTabType)) {
            saveTemp_online.clear();
        } else if ("hot".equals(mTabType)) {
            saveTemp_hot.clear();
        } else if ("new".equals(mTabType)) {
            saveTemp_new.clear();
        }
    }

    /**
     * 根据type拿数据集
     */
    private List<ItemData<? extends BaseUIModule>> getTempData() {
        if ("offline".equals(mTabType)) {
            return saveTemp_offline;
        } else if ("online".equals(mTabType)) {
            return saveTemp_online;
        } else if ("hot".equals(mTabType)) {
            return saveTemp_hot;
        } else {
            return saveTemp_new;
        }
    }

    /**
     * 左右切换和下拉刷新时需要还原成初始状态
     */
    private void resetLayout() {
        mCurrPage = 1;
        mlastPagePositionOfNetData = 0;
    }

    @Override
    public void onRefresh() {
        resetLayout();
        loadData(true);
    }

    @Override
    public void onLoadMore() {
        if (mCurrPage <= mTotalPage) {
            loadData(false);
        } else {
//            MyApplication.getMainThreadHandler().postDelayed(selectorRunnable, 500);
        }
    }

    @Override
    public void onScrollChangeListener(int scrollState) {
        reportWhenScroll(scrollState, getPageAlias(), getTempData(), listView);
    }

    public interface SelectorTabListener {
        void selectTab();
    }

    public class SelectorTabRunnable implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (selectorTabListener != null) {
                selectorTabListener.selectTab();// 切换Tab
            }
        }
    }
}
