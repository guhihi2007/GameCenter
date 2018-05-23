package cn.lt.game.ui.app.gamegift;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.view.banner.BannerView;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.app.adapter.LTBaseAdapter;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListenerImpl;
import cn.lt.game.ui.app.adapter.parser.NetDataAddShell;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

public class GiftCenterFragment extends BaseFragment implements OnClickListener, RetryCallBack, UserInfoUpdateListening, SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener {

    /**
     * listview 的item显示礼包的个数；
     */
    public static final int NUMBER_PER_ROW = 3;
    private final String TAG = "GiftCenterFragment";
    private View mRoot;
    private ListView mListViewInPull;
    private RefreshAndLoadMoreListView mPullListView;
    /**
     * 网络请求分页查询的总页数
     */
    private int mTotalPage;

    private int mCurrPage = 1;

    /**
     * Banner图
     */
    private BannerView mBannerView;

    /**
     * 搜索框
     */
    private View mHeaderSearchView;

    /**
     * 此view为FrameLayout布局的最上层根布局用来放需要展示在最上层的view
     */
    private LinearLayout mRootFrameLayout;

    /**
     * Banner URI
     */
    private ItemData<? extends BaseUIModule> mBanners;

    /**
     * 网络相关布局视图
     */
    private NetWorkStateView mNetWorkStateView;

    private LTBaseAdapter mAdapter;

    private MyApplication mApplication;

    private boolean hasCache;

    private boolean isDrawChlid;

    private BaseOnclickListener mClickListener;
    private int mlastPagePositionOfNetData = 0;

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_GIFT_CENTER);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserInfoManager.instance().addListening(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.gamegiftcenter_layout, container, false);
            mRootFrameLayout = (LinearLayout) mRoot.findViewById(R.id.llt_gift_center);
            mClickListener = new BaseOnclickListenerImpl(getActivity(), getPageAlias());

            mApplication = (MyApplication) getActivity().getApplication();
            initView();
            mNetWorkStateView.showLoadingBar();
            fillLayout();
        }
        return mRoot;
    }

    /**
     * 初始化网络错误提示界面；
     */
    private void inflateNetWorkErrView() {
        mNetWorkStateView.showNetworkFailLayout();
//        mPullListView.setMode(Mode.DISABLED);
        mPullListView.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBannerView != null) {
            mApplication.isRun = true;
            mBannerView.startBannerTimer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
        if (mBannerView != null) {
            mApplication.isRun = true;
            mBannerView.stopBannerTimer();
        }
        UserInfoManager.instance().removeListening(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBannerView != null) {
            mApplication.isRun = true;
            mBannerView.stopBannerTimer();
        }
    }

    /**
     * 填充数据；
     */
    private void fillLayout() {
        resetLayout();
        requestData(false);
    }

    /**
     * 初始化视图；
     */
    private void initView() {
        mPullListView = (RefreshAndLoadMoreListView) mRoot.findViewById(R.id.giftcenter_listView);
        mBannerView = new BannerView(this.getActivity(), getResources().getDimensionPixelOffset(R.dimen.banner_height2), null, false);
        mNetWorkStateView = (NetWorkStateView) mRoot.findViewById(R.id.rank_netwrolStateView);
        mNetWorkStateView.hideNetworkFailLayout();
        mNetWorkStateView.setRetryCallBack(this);
        mNetWorkStateView.setNoDataLayoutText("暂无最新礼包！", null);
        mHeaderSearchView = LayoutInflater.from(mActivity).inflate(R.layout.gift_serach_bar, null);
        initListView();
    }

    private void initListView() {
        mListViewInPull = mPullListView.getmListView();
        mAdapter = new LTBaseAdapter(mActivity, mClickListener);
//        mPullListView.setOverideTouthEvent(true);
        mPullListView.setmOnRefrshListener(this);
        mPullListView.setOnLoadMoreListener(this);
        addHeadToListView();
        mPullListView.setAdapter(mAdapter, false);
    }

//    private void addListener() {
//        mPullListView.setOnRefreshListener(this);
//    }

    /**
     * 添加listView 头部View
     */
    private void addHeadToListView() {
        mBannerView.setBackgroundColor(mActivity.getResources().getColor(R.color.background_grey));
//        mPullListView.setMode(Mode.PULL_FROM_START);
        mListViewInPull.addHeaderView(mBannerView);
        mListViewInPull.addHeaderView(mHeaderSearchView);
    }

    /**
     * 判断第一个可见的view是否为第一个位置；
     *
     * @return true--表示item1已经完全在当前窗口隐藏掉,false--表示当前可见的视图为item1；
     */
    public boolean isFirstItemVisible() {
        View c = mListViewInPull.getChildAt(0);
        if (c == null) {
            return false;
        }
        int firstVisiblePosition = mListViewInPull.getFirstVisiblePosition();
        return firstVisiblePosition >= 2;
    }

    /**
     * 此方法用于滑动时控制搜索框浮动在最顶部不隐藏;
     */
    @SuppressWarnings("unused")
    private void setSearhBarReLocal() {
        if (isFirstItemVisible()) {
            // 此两条方法都必须使用，否则会导致mListView仍然拥有mHeadSearchView的引用；
            mListViewInPull.removeHeaderView(mHeaderSearchView);
            mListViewInPull.removeViewInLayout(mHeaderSearchView);
            if (mHeaderSearchView.getParent() != null) {
            } else {
                mRootFrameLayout.setVisibility(View.VISIBLE);
                mRootFrameLayout.addView(mHeaderSearchView, 0);
            }

            isDrawChlid = true;
        } else {
            if (isDrawChlid == true) {
                mRootFrameLayout.removeView(mHeaderSearchView);
                mRootFrameLayout.setVisibility(View.GONE);
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mHeaderSearchView.setLayoutParams(lp);
                mListViewInPull.addHeaderView(mHeaderSearchView);
                isDrawChlid = false;
            }

        }
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void retry() {
        resetLayout();
        requestData(true);
    }

    private void resetLayout() {
        mCurrPage = 1;
        //        mNetWorkStateView.showLoadingBar();
//        mPullListView.setMode(Mode.BOTH);
//        mPullListView.setMode(Mode.PULL_FROM_START);
    }


    private void setPage(int loadingPage, UIModuleList list, boolean fromNet) {

        if (loadingPage == 1) {
            if (!fromNet && list != null && list.size() > 0) {
                hasCache = true;
            } else if (fromNet && list != null && list.size() == 0 && hasCache) {
                return;
            }
            mAdapter.resetList();
            parserJson(list, fromNet, true);
        } else {
            parserJson(list, true, false);
        }
        if (fromNet) {
            if (mCurrPage < mTotalPage) {
                mCurrPage = loadingPage + 1;
                mPullListView.setCanLoadMore(true);
            } else if (mCurrPage >= mTotalPage) {
                mPullListView.setCanLoadMore(false);
            }
        }
    }

    /**
     * 获取网络请求数据；
     */
    private void requestData(final boolean refresh) {
        final Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(mCurrPage));
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.GIFTS_URI, params, new WebCallBackToObject<UIModuleList>() {

            /**
             * 网络请求出错时调用
             *
             * @param statusCode 异常编号
             * @param error      异常信息
             */
            @Override
            public void onFailure(int statusCode, Throwable error) {
                Log.d(TAG, "数据请求失败！" + statusCode);
                mPullListView.setVisibility(View.VISIBLE);
                mPullListView.setCanLoadMore(mCurrPage <= mTotalPage);
                mPullListView.onLoadMoreComplete();
                if (!hasCache && mCurrPage == 1) {
                    inflateNetWorkErrView();
                }
            }

            @Override
            protected void handle(UIModuleList info) {
                mPullListView.setVisibility(View.VISIBLE);
                mTotalPage = getLastPage();
                setPage(Integer.valueOf(params.get("page")), info, refresh);
                if (mCurrPage < mTotalPage) {
                    mPullListView.setCanLoadMore(true);
                } else {
                    mPullListView.setCanLoadMore(false);
                }
                mPullListView.onLoadMoreComplete();

            }
        });

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

    /**
     * 解析json数据；
     *
     * @param moduleList 网络层返回的数据；
     * @param fromNet    标记是否为从网络获取的数据；
     * @return true -- 有数据；false-- 表示没数据；
     */
    private boolean parserJson(UIModuleList moduleList, boolean fromNet, boolean isFirstPage) {
        try {
            addTextItem(moduleList, isFirstPage);
            List<ItemData<? extends BaseUIModule>> temp = NetDataAddShell.wrapModuleList(moduleList, mlastPagePositionOfNetData);
            if (fromNet) {
                setMlastPagePositionOfNetData(temp);
            }
            if (isFirstPage) {
                for (int i = 0; i < temp.size(); i++) {
                    if (PresentType.carousel == temp.get(i).getmPresentType()) {
                        mBanners = temp.remove(i);
                    }
                }

            }
            mAdapter.addList(temp);
            mBannerView.fillLayout(mBanners, 0, 1);
            mNetWorkStateView.hideNetworkView();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "解析json错误！");
            if (!hasCache && mCurrPage == 1 && fromNet) {
                inflateNetWorkErrView();
            }
        }
        return false;
    }

    private void addTextItem(UIModuleList moduleList, boolean isFirstPage) {

        if (isFirstPage) {
            int size = moduleList.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    if (PresentType.new_gifts == moduleList.get(i).getUIType() && (i < size || mTotalPage > 1)) {
                        UIModule<String> item = new UIModule<>(PresentType.new_gifts_title, "最新礼包");
                        moduleList.add(i, item);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void userLogin(UserBaseInfo userBaseInfo) {
        if (!getActivity().isFinishing()) {
            mNetWorkStateView.showLoadingBar();
            fillLayout();
        }
    }

    @Override
    public void updateUserInfo(UserBaseInfo userBaseInfo) {

    }

    @Override
    public void userLogout() {

    }

    @Override
    public void onRefresh() {
        resetLayout();
        requestData(true);
        if (mBannerView != null) {
            mApplication.isRun = true;
            mBannerView.startBannerTimer();
        }
    }

    @Override
    public void onLoadMore() {
        requestData(false);
    }
}
