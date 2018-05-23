package cn.lt.game.ui.app.index;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.bean.TabEnum;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.ActivityManager;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.redpoints.RedPointsCallback;
import cn.lt.game.lib.util.redpoints.RedPointsViewUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.view.banner.BannerView;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.pageunits.IndexScroll;
import cn.lt.game.statistics.pageunits.PageMultiUnitsReportManager;
import cn.lt.game.threadPool.ThreadPoolProxyFactory;
import cn.lt.game.ui.app.HomeActivity;
import cn.lt.game.ui.app.adapter.LTBaseAdapter;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListenerImpl;
import cn.lt.game.ui.app.adapter.parser.NetDataAddShell;
import cn.lt.game.ui.app.adapter.weight.IndexItemEntryView;
import cn.lt.game.ui.app.index.manger.IndexTouchManger;
import cn.lt.game.ui.app.index.widget.MyLinearLayout;
import cn.lt.game.ui.app.index.widget.SearchView;
import de.greenrobot.event.EventBus;

public class IndexFragment extends BaseFragment implements RetryCallBack, OnClickListener,RedPointsCallback, SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener, RefreshAndLoadMoreListView.IOnScrollStateChanged {

    private LTBaseAdapter mAdapter;

    private View ll_downloadMgr, mRedPoint;

    private int mTotalPage = -1;
    private int mCurrPage = 1;

    /**
     * 没有缓存数据，直接使用网络获取的数据
     */
    private boolean hasCache;

    // 存放特别推荐是否已经下载完成

    private RefreshAndLoadMoreListView mListView;

    /**
     * 网络相关布局视图
     */
    private NetWorkStateView mNetWorkStateView;

    private BannerView mBannerView;

//    private ListView mRealListView;

    private IndexTouchManger mAnimationManger;

    private MyLinearLayout mLiftViewRoot;

    private RelativeLayout mSearchBarView;

    private RelativeLayout mTitleView;

    private ItemData<? extends BaseUIModule> mBanners = null;

    private BaseOnclickListenerImpl mClickListener;

    private View mRoot;

    private int mLastPagePositionOfNetData = 0;

    private List<ItemData<? extends BaseUIModule>> preLoadDataList = new ArrayList<>();
    private List<ItemData<? extends BaseUIModule>> saveTemp = new ArrayList<>();
    private SearchView mSv_searchView;
    private int mTop;

    @SuppressWarnings("unchecked")
    private void initAnimation() {
        if (mAnimationManger == null) {
            mAnimationManger = new IndexTouchManger(getActivity(), mLiftViewRoot, mSearchBarView,
                    mListView, mTitleView).init(new IndexScroll() {
                @Override
                public void scrollStopSubscriberToIndex() {
//                    reportWhenScroll(AbsListView.OnScrollListener.SCROLL_STATE_IDLE, Constant.PAGE_INDEX, saveTemp, mListView);
                }

                @Override
                public void scrollFromBottomToTop(float apha) {
                    LogUtils.i("IndexTouchManger", "是否到底，顶:" + apha);
                    if(null!=mSv_searchView){
                        if(apha==1){ //顶
                            mSv_searchView.setPadding(0,mTop-DensityUtil.dip2px(getActivity(),2),0,0);
                        }else if(apha==0){
                            mSv_searchView.setPadding(0,mTop,0,0);
                        }
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.activity_index_v2, container, false);
            mClickListener = new BaseOnclickListenerImpl(mActivity, getPageAlias()) {
                @Override
                public boolean realOnClick(View v, String mPageName) {
                    try {
                        String s = (String) v.getTag(R.id.view_data);
                        if ("hot".equals(s)) {
                            ((HomeActivity) getActivity()).jumpTab(TabEnum.RANK, 2);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            };
            ((HomeActivity) getActivity()).registerMotionCallback(new HomeActivity.MonitionCallback() {
                @Override
                public boolean callback(MotionEvent ev) {
                    if (mAnimationManger == null) {
                        initAnimation();
                    }
                    return mAnimationManger.onEvent(ev);
                }
            });
            initView();
            initBanners();
            initListView();
            fillLayout();
            inflateLoadingLayout();
        }
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null && getUserVisibleHint()) {
            mAdapter.notifyDataSetChanged();
            MyApplication.application.isRun = true;
            mBannerView.startBannerTimer();
        }
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_INDEX);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initBanners() {
        mBannerView.fillLayout(mBanners, 0, -1);
    }

    private void initListView() {
        IndexItemEntryView mHeadView = new IndexItemEntryView(mActivity, mClickListener);
        mAdapter = new LTBaseAdapter(mActivity, mClickListener);
        mListView.setAdapter(mAdapter, false);
        mListView.setOnLoadMoreListener(this);
        mListView.setmOnRefrshListener(this);
        mListView.setMyOnScrollListener(this);
        mListView.getmListView().addHeaderView(mHeadView);
    }

    private void initView() {
        mLiftViewRoot = (MyLinearLayout) mRoot.findViewById(R.id.llt_content_container);
        mBannerView = (BannerView) mRoot.findViewById(R.id.hv_index);
        mTitleView = (RelativeLayout) mRoot.findViewById(R.id.tv_title_index);
        mSearchBarView = (RelativeLayout) mRoot.findViewById(R.id.rl_search_contanier);
        mListView = (RefreshAndLoadMoreListView) mRoot.findViewById(R.id.pullToRefreshListView);
        ll_downloadMgr = mRoot.findViewById(R.id.ll_downloadMgr);
        ll_downloadMgr.setOnClickListener(this);
        mRedPoint = mRoot.findViewById(R.id.tv_titleBar_redPoint);
        mNetWorkStateView = (NetWorkStateView) mRoot.findViewById(R.id.rank_netwrolStateView);
        mNetWorkStateView.setRetryCallBack(this);
        adjustBannerHeight();
        RedPointsViewUtils.initTopManagerRedPoints(mRedPoint,mActivity);
    }

    /**
     * 根据手机状态栏高度，把首页顶部控件往下偏移（只针对Android4.4以上）
     */
    private void adjustBannerHeight() {
        try {
            // 手机系统版本小于4.4的，不执行
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return;
            }

            // 获取手机状态栏高度
            mTop = ActivityManager.getStatusHeight(getActivity());
            MyLinearLayout marginView = (MyLinearLayout) mRoot.findViewById(R.id.llt_content_container);
            FrameLayout.LayoutParams marginLP = (FrameLayout.LayoutParams) marginView.getLayoutParams();
            marginLP.bottomMargin = marginLP.bottomMargin + mTop;
            RelativeLayout.LayoutParams mTitleViewLP = (RelativeLayout.LayoutParams) mTitleView.getLayoutParams();
            mTitleViewLP.height = mTitleViewLP.height + mTop;

            RelativeLayout.LayoutParams mMenuViewLP = (RelativeLayout.LayoutParams) ll_downloadMgr.getLayoutParams();
            mMenuViewLP.setMargins(0, mTop, 0, 0);

            LinearLayout ll_SearchViewLinearLayout = (LinearLayout) mRoot.findViewById(R.id.ll_SearchViewLinearLayout);
            RelativeLayout.LayoutParams ll_SearchViewLP = (RelativeLayout.LayoutParams) ll_SearchViewLinearLayout.getLayoutParams();
            ll_SearchViewLP.height = ll_SearchViewLP.height + mTop;

            mSv_searchView = (SearchView) mRoot.findViewById(R.id.sv_searchView);
            LinearLayout.LayoutParams sv_searchViewLP = (LinearLayout.LayoutParams) mSv_searchView.getLayoutParams();
            sv_searchViewLP.height = sv_searchViewLP.height + mTop;
            mSv_searchView.setPadding(0, mTop,0,0);

            // 显示顶部半透明渐变遮罩
            View v_zhezhao = mRoot.findViewById(R.id.v_zhezhao);
            v_zhezhao.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 填充数据；
     */
    private void fillLayout() {
        // 先使用缓存数据；
        LogUtils.i("ccc", "填充数据请求=");
        requestData(false, false);
    }

    private void notifyDataSetChanged() {
        initBanners();
//        mAdapter.setList(mDataList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
        if (mBannerView != null) mBannerView.stopBannerTimer();
    }

    @Override
    public void onDestroyView() {
        //        mAnimationManger = null;
        if (mRoot != null && mRoot.getParent() != null) {
            ((ViewGroup) mRoot.getParent()).removeView(mRoot);
        }
        super.onDestroyView();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.i(LogTAG.HTAG,"IndexFragment"+isVisibleToUser);
        try {
            if (isVisibleToUser) {
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                    MyApplication.application.isRun = true;
                    mBannerView.startBannerTimer();
                }
                LogUtils.i("jingxuan", "IndexFragment是不是来自通知" + MyApplication.application.mIsFromNotification + System.currentTimeMillis());
                if (!MyApplication.application.mIsFromNotification) { //过滤通知过来的:退出客户端，推送会走两次此方法
                    LogUtils.i("jingxuan", "IndexFragment可见了setUserVisibleHint 不是来自通知" + System.currentTimeMillis());
                    PageMultiUnitsReportManager.getInstance().buildPageUnits(saveTemp, mListView, Constant.PAGE_INDEX, "", "", "", "");
                }
                MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyApplication.application.mIsFromNotification = false;
                    }
                }, 1000);
//                RedPointsViewUtils.initTopManagerRedPoints(mRedPoint,mActivity);
            } else {
                if (MyApplication.application != null) {
                    MyApplication.application.isRun = false;
                }
                if (mBannerView != null) {
                    mBannerView.stopBannerTimer();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mBannerView != null) {
            mBannerView.stopBannerTimer();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_downloadMgr) {
            ActivityActionUtils.JumpToManager(getActivity(), 0);
        }
    }


    /**
     * 请求网络
     */
    private void requestData(final boolean isFromPreLoad, final boolean isRefresh) {
        final Map<String, String> params = new HashMap<>();
        params.put("pagesize", String.valueOf(10));
        params.put("page", String.valueOf(mCurrPage));
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.INDEX_URI, params, new WebCallBackToObject<UIModuleList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                if (mCurrPage == 1 && !isRefresh) {
                    inflateFaileLayout();
                }
                mListView.onLoadMoreComplete();
                mListView.onLoadingFailed();
                MyApplication.application.mIsFromNotificationForIndexNet = false;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void handle(UIModuleList info) {
                mTotalPage = getLastPage() == -1 ? mTotalPage : getLastPage();
                if (isFromPreLoad) {
                    saveData(info, true);    //上拉与缓存绑定
                } else {
                    if(isRefresh){
                        mLastPagePositionOfNetData=0;
                    }
                    mListView.setCanLoadMore(mCurrPage <= mTotalPage);
                    mListView.onLoadMoreComplete();
                    setPage(Integer.valueOf(params.get("page")));
                    parserJson(info, true);
                    notifyDataSetChanged();
                    LogUtils.i("jingxuan", "精选页可见吗==请求网络时上报>" + getUserVisibleHint()); //可见又请求须报、不可见有请求不能报
                    if (getUserVisibleHint() && !MyApplication.application.mIsFromNotificationForIndexNet) {
                        MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PageMultiUnitsReportManager.getInstance().buildPageUnits(saveTemp, mListView, Constant.PAGE_INDEX, "", "", "", "");
                            }
                        }, 500);  //才能取到可见的position
                    }
                    MyApplication.application.mIsFromNotificationForIndexNet = false;
                    inflateDataLayout();
                }
            }
        });

    }

    private void setPage(int loadingPage) {
        if (loadingPage == 1 && mAdapter != null) {
            mAdapter.resetList();
        }
        if (mCurrPage < mTotalPage) {
            mCurrPage = loadingPage + 1;
        }
        LogUtils.i("ccc", "当前加载的页面=" + loadingPage + "总数" + mTotalPage);
        if (loadingPage >= mTotalPage && mTotalPage != -1) {
            // 设置不允许拉
            mListView.setCanLoadMore(false);
        }
    }


    private void setmLastPagePositionOfNetData(List<ItemData<? extends BaseUIModule>> temp) {
        try {
            if (temp != null && temp.size() > 0) {
                ItemData item = temp.get(temp.size() - 1);
                this.mLastPagePositionOfNetData = item.getPos();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析json数据；设置数据
     *
     * @param moduleList 网络层返回的数据；
     * @param fromNet    标记是否为从网络获取的数据；
     * @return true -- 有数据；false-- 表示没数据；
     */
    private boolean parserJson(UIModuleList moduleList, boolean fromNet) {
        try {
            List<ItemData<? extends BaseUIModule>> temp = NetDataAddShell.wrapModuleList(moduleList, mLastPagePositionOfNetData);
            if (fromNet) {
                setmLastPagePositionOfNetData(temp);
            }
            saveTemp.addAll(temp);
            for (int i = 0; i < temp.size(); i++) {
                if (PresentType.carousel == temp.get(i).getmPresentType()) {
                    mBanners = temp.remove(i);
                }
            }
            mAdapter.addList(temp);
            requestData(true, false);
            if (!fromNet) {
                hasCache = true;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解析json数据；保存数据
     *
     * @param moduleList 网络层返回的数据；
     * @param fromNet    标记是否为从网络获取的数据；
     */
    private void saveData(UIModuleList moduleList, boolean fromNet) {
        try {
            List<ItemData<? extends BaseUIModule>> temp = NetDataAddShell.wrapModuleList(moduleList, mLastPagePositionOfNetData);
            if (fromNet) {
                setmLastPagePositionOfNetData(temp);
            }
            saveTemp.addAll(temp);
            for (int i = 0; i < temp.size(); i++) {
                if (PresentType.carousel == temp.get(i).getmPresentType()) {
                    temp.remove(i);
                }
            }
            preLoadDataList.clear();
            preLoadDataList.addAll(temp);
            LogUtils.d("iii", "保存数据了,大小为==>" + preLoadDataList.size());
            if (!fromNet) {
                hasCache = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示加载中
     */
    private void inflateLoadingLayout() {
        mNetWorkStateView.showLoadingBar();
        mListView.setVisibility(View.GONE);
        mSearchBarView.setVisibility(View.INVISIBLE);
    }

    private void inflateDataLayout() {
        mListView.setVisibility(View.VISIBLE);
        mSearchBarView.setVisibility(View.VISIBLE);
        mNetWorkStateView.hideNetworkView();
    }

    private void inflateFaileLayout() {
        mNetWorkStateView.showNetworkFailLayout();
        mListView.setVisibility(View.GONE);
        mSearchBarView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void retry() {
        requestData(false, false);
        EventBus.getDefault().post("重新请求Tab");
    }

    @Override
    public void onRefresh() {
        mCurrPage = 1;
        requestData(false,true);
    }

    @Override
    public void onLoadMore() {
        if (mCurrPage <= mTotalPage) {
            if (preLoadDataList.size() == 0) {
                LogUtils.d("iii", "已经没有缓存了,请求并设置数据，速度会慢");
                requestData(false, false); //请求并设置数据
            } else {
                MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //追加预加载数据
                        saveTemp.addAll(preLoadDataList);
//                        mListView.onRefreshComplete();
                        mListView.onLoadMoreComplete();
                        mAdapter.addList(preLoadDataList);
                        MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PageMultiUnitsReportManager.getInstance().buildPageUnits(saveTemp, mListView, Constant.PAGE_INDEX, "", "", "", "");
                            }
                        }, 500);  //才能取到可见的position
                        inflateDataLayout();
                        preLoadDataList.clear();
                        setPage(mCurrPage);
                        if (mCurrPage < mTotalPage) requestData(true, false);   //请求并缓存数据
                    }
                }, 100);
            }
        }
    }

    @Override
    public void onScrollChangeListener(final int scrollState) {
        ThreadPoolProxyFactory.getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                reportWhenScroll(scrollState, Constant.PAGE_INDEX, saveTemp, mListView);
            }
        });

    }

    @Override
    public void refreshTopManageRedPoints() {
        RedPointsViewUtils.initTopManagerRedPoints(mRedPoint,mActivity);
    }
}
