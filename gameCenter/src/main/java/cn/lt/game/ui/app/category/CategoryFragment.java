package cn.lt.game.ui.app.category;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.StatusBarUtils;
import cn.lt.game.lib.util.redpoints.RedPointsCallback;
import cn.lt.game.lib.util.redpoints.RedPointsViewUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.pageunits.PageMultiUnitsReportManager;
import cn.lt.game.ui.app.adapter.LTBaseAdapter;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.parser.NetDataAddShell;

/**
 * 主页分类模块
 */
@SuppressWarnings("rawtypes")
public class CategoryFragment extends BaseFragment implements RedPointsCallback, SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.IOnScrollStateChanged, cn.lt.game.lib.view.NetWorkStateView.RetryCallBack {

    private RefreshAndLoadMoreListView mPullToRefreshListView;
    private NetWorkStateView netWorkStateView;

    private View mRoot;
    private Context mContext;
    private View mRedPoint;
    private LTBaseAdapter mAdapter;
    private int mLastPagePositionOfNetData = 0;
    private List<ItemData<? extends BaseUIModule>> saveTemp = new ArrayList<>();
    private boolean isLoading = false;
    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_CATEGORY);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);//次方法必须调用；
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.activity_category, container, false);
            initView();
        }
        return mRoot;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.i(LogTAG.HTAG,"CategoryFragment"+isVisibleToUser);
        if (isVisibleToUser) {
            LogUtils.e("ccc", "分类页可见了");
            try {
                if (!MyApplication.application.mIsFromNotification) { //过滤通知过来的
                    PageMultiUnitsReportManager.getInstance().buildPageUnits(saveTemp, mPullToRefreshListView, Constant.PAGE_CATEGORY, "", "", "", "");
                }
                MyApplication.application.mIsFromNotification = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
//            RedPointsViewUtils.initTopManagerRedPoints(mRedPoint,mContext);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 连接
     */
    private void loadData() {
        mLastPagePositionOfNetData = 0;
        initAction();
    }
    /**
     * 初始化界面
     */
    @SuppressWarnings("unchecked")
    protected void initView() {
        netWorkStateView = (NetWorkStateView) mRoot.findViewById(R.id.game_detail_netWrokStateView);
        mPullToRefreshListView = (RefreshAndLoadMoreListView) mRoot.findViewById(R.id.pullToRefreshListView);
        mPullToRefreshListView.setmOnRefrshListener(this);
        mRedPoint = mRoot.findViewById(R.id.tv_titleBar_redPoint);
        View statusBar = mRoot.findViewById(R.id.status_bar);
        StatusBarUtils.showSelfStatusBar(mContext, statusBar);
        RedPointsViewUtils.initTopManagerRedPoints(mRedPoint,mContext);
        netWorkStateView.setRetryCallBack(this);
        mAdapter = new LTBaseAdapter(getActivity(), new BaseOnclickListener(getActivity(), getPageAlias()) {
            @Override
            public boolean realOnClick(View v, String mPageName) {
                PresentType type = (PresentType) v.getTag(R.id.present_type);
                //跳转热门分类详情
                if (PresentType.all_cats.equals(type)) {
                    //跳到分类详情
                    String categoryId = (String) v.getTag(R.id.cat_data_01);
                    ArrayList<String> idList = (ArrayList<String>) v.getTag(R.id.cat_data_02);
                    ArrayList<String> titleList = (ArrayList<String>) v.getTag(R.id.cat_data_03);
                    String clickCategoryId = (String) v.getTag(R.id.cat_data_04);
                    boolean isBigCategory = (boolean) v.getTag(R.id.cat_data_05);
                    String categoryTitle = (String) v.getTag(R.id.cat_data_06);
                    ActivityActionUtils.jumpToCategoryDetail(getActivity(), categoryId, categoryTitle, idList, titleList, clickCategoryId, isBigCategory);
                }
                return false;
            }
        });
        mPullToRefreshListView.setAdapter(mAdapter, false);
        mPullToRefreshListView.setMyOnScrollListener(this);
    }

    /**
     * 加载分类的数据
     * <p/>
     * /cats?
     */
    private void getData(final boolean isFromRefresh) {
        mLastPagePositionOfNetData=0;
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.CATS_URI, new WebCallBackToObject<UIModuleList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                isLoading = false;
                mPullToRefreshListView.onLoadMoreComplete();
                mPullToRefreshListView.setCanLoadMore(false);
                mPullToRefreshListView.setVisibility(View.GONE);
                netWorkStateView.showNetworkFailLayout();
            }

            @Override
            protected void handle(UIModuleList info) {
                isLoading = false;
                List<ItemData<? extends BaseUIModule>> moduleList = NetDataAddShell.wrapModuleList(info, mLastPagePositionOfNetData);
                mPullToRefreshListView.setCanLoadMore(false);
                if (isFromRefresh) saveTemp.clear();
                saveTemp.addAll(moduleList);
                mAdapter.setList(moduleList);
                mPullToRefreshListView.setVisibility(View.VISIBLE);
                netWorkStateView.hideNetworkView();
                mPullToRefreshListView.onLoadMoreComplete();
                if (getUserVisibleHint()) {
                    MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PageMultiUnitsReportManager.getInstance().buildPageUnits(saveTemp, mPullToRefreshListView, Constant.PAGE_CATEGORY, "", "", "", "");
                        }
                    }, 500);
                }
            }
        });

    }

    /**
     *
     */
    public void initAction() {
        mPullToRefreshListView.setVisibility(View.GONE);
        netWorkStateView.setVisibility(View.VISIBLE);
        netWorkStateView.showLoadingBar();
        isLoading = true;
        //修复分类白屏问题
        MyApplication.getMainThreadHandler().postDelayed(runnable, 16*1000);
        getData(false);
    }


    @Override
    public void onRefresh() {
        getData(true);
    }

    @Override
    public void onScrollChangeListener(int scrollState) {
        reportWhenScroll(scrollState, Constant.PAGE_CATEGORY, saveTemp, mPullToRefreshListView);
    }

    @Override
    public void retry() {
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MyApplication.getMainThreadHandler().removeCallbacks(runnable);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            MyApplication.getMainThreadHandler().removeCallbacks(runnable);
            if (isLoading) {
                loadBlankAndShowNetError();
            }
        }
    };

    private void loadBlankAndShowNetError() {
        isLoading = false;
        mPullToRefreshListView.setVisibility(View.GONE);
        netWorkStateView.setVisibility(View.VISIBLE);
        netWorkStateView.showNetworkFailLayout();
    }

    @Override
    public void refreshTopManageRedPoints() {
        RedPointsViewUtils.initTopManagerRedPoints(mRedPoint,mActivity);
    }
}
