package cn.lt.game.ui.app.gamegift;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.app.adapter.LTBaseAdapter;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListenerImpl;
import cn.lt.game.ui.app.adapter.parser.NetDataAddShell;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

/**
 * 我的礼包Tab
 */
public class GiftMineFragment extends BaseFragment implements
        RetryCallBack, UserInfoUpdateListening ,SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener{

    private final static String UN_LOGGIN = "在登录状态下才能查看我的礼包呢";
    private static String TAG = "GiftMineFragment";
    /**
     * Fragment根容器
     */
    private View mRoot;
    private ListView mListViewInPull;
    private RefreshAndLoadMoreListView mPullListView;
    private LTBaseAdapter mAdapter;
    private BaseOnclickListener mClickListener;
    /**
     * 网络相关布局视图
     */
    private NetWorkStateView mNetWorkStateView;
    /**
     * 网络请求分页查询的总页数
     */
    private int mTotalPage;

    private int mCurrPage = 1;

    /**
     * 登录相关的布局
     */
    private ViewStub mViewStubLoggin;

    /**
     * 没有礼包时显示的布局
     */
    private ViewStub mViewStubNoGift;

    /**
     * 登录相关的布局
     */
    private View mUnlogginRootView;

    /**
     * 没有礼包时显示的布局
     */
    private View mNoGiftRootView;
    private int mlastPagePositionOfNetData = 0;

    private void showView(State state) {
        switch (state) {
            case unLogin:
                inflateUnloginView();
                break;
            case noData:
                inflateNoGiftView();
                break;
            case networkErr:
                inflateNetWorkErrView();
                break;
            case success:
                hideUnloginView();
                mNetWorkStateView.hideNetworkView();
                mPullListView.setVisibility(View.VISIBLE);
                break;
            case loading:
                hideUnloginView();
                mPullListView.setVisibility(View.GONE);
                mNetWorkStateView.showLoadingBar();
                break;
        }
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_GIFT_MINE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRoot = inflater.inflate(R.layout.layout_gift_mine, container, false);
        UserInfoManager.instance().addListening(this);
        mClickListener = new BaseOnclickListenerImpl(getActivity(), getPageAlias());
        initView();
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (UserInfoManager.instance().isLogin()) {
            checkNetWork();
        } else {
            showView(State.unLogin);
        }
    }

    public void onEventMainThread(GiftDomainDetail info) {
        if (info != null) {
            checkNetWork();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnlogginRootView = null;
        mNoGiftRootView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UserInfoManager.instance().removeListening(this);
    }

    /**
     * 初始化并显示未登录的提示界面；未登录是调用此方法；
     */
    private void inflateUnloginView() {
        if (mNoGiftRootView == null) {
            mNoGiftRootView = mViewStubNoGift.inflate();
        }

        mNoGiftRootView.findViewById(R.id.tv_no_gift_mine)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserInfoManager.instance().starLogin(mActivity, true);
                    }
                });

        ((TextView) mNoGiftRootView.findViewById(R.id.tv_explain)).setText("在登录状态下才能查看我的礼包呢");
        ((Button) mNoGiftRootView.findViewById(R.id.tv_no_gift_mine)).setText("马上去登录");
        ((ImageView) mNoGiftRootView.findViewById(R.id.iv_cat)).setImageResource(R.mipmap.sinister_smile_cat);
        hideListViewAndNetStateView();
        mNoGiftRootView.setVisibility(View.VISIBLE);

    }

    /**
     * 初始化并显示没有礼包数据的提示界面；
     */
    private void inflateNoGiftView() {
        if (mNoGiftRootView == null) {
            mNoGiftRootView = mViewStubNoGift.inflate();
        }

        mNoGiftRootView.findViewById(R.id.tv_no_gift_mine)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((GiftHomeActivity) mActivity).getmViewPager().setCurrentItem(0);
                    }
                });

        ((TextView) mNoGiftRootView.findViewById(R.id.tv_explain)).setText("你还没有领取过礼包");
        ((Button) mNoGiftRootView.findViewById(R.id.tv_no_gift_mine)).setText("马上领取");
        ((ImageView) mNoGiftRootView.findViewById(R.id.iv_cat)).setImageResource(R.mipmap.empty_data_img);
        hideListViewAndNetStateView();
        mNoGiftRootView.setVisibility(View.VISIBLE);
    }

    private void hideUnloginView() {
        if (mUnlogginRootView != null) {
            mUnlogginRootView.setVisibility(View.GONE);
        }
        mPullListView.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化网络错误提示界面；
     */
    private void inflateNetWorkErrView() {
        mNetWorkStateView.showNetworkFailLayout();
        mPullListView.setVisibility(View.GONE);
        mCurrPage = 1;
    }

    private void hideListViewAndNetStateView() {
        mNetWorkStateView.hideNetworkView();
        mPullListView.setVisibility(View.GONE);
    }

    /**
     * 检查网络，如果有网路则请求网络数据，无网络显示无网络界面；
     */
    private void checkNetWork() {
        resetLayout();
        if (NetUtils.isConnected(mActivity)) {
            requestData();
        } else {
            showView(State.networkErr);
        }

    }

    /**
     * 初始化界面布局；
     */
    private void initView() {
        mRoot.setVisibility(View.VISIBLE);
        mViewStubLoggin = (ViewStub) mRoot.findViewById(R.id.vs_unloggin);
        mViewStubNoGift = (ViewStub) mRoot.findViewById(R.id.vs_no_gift);
        mNetWorkStateView = (NetWorkStateView) mRoot
                .findViewById(R.id.rank_netwrolStateView);
        mNetWorkStateView.setRetryCallBack(this);
        mPullListView = (RefreshAndLoadMoreListView) mRoot
                .findViewById(R.id.mygift_listView1);
        mListViewInPull = mPullListView.getmListView();
        initListView();
        showView(State.loading);
    }

    /**
     * 初始化listview相关；
     */
    private void initListView() {
        mAdapter = new LTBaseAdapter(mActivity, mClickListener);
        mListViewInPull.setAdapter(mAdapter);
        mPullListView.setmOnRefrshListener(this);
        mPullListView.setOnLoadMoreListener(this);
    }

    @Override
    public void retry() {
        checkNetWork();
    }

//    @Override
//    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//        checkNetWork();
//    }
//
//    @Override
//    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//        requestData();
//    }

    @Override
    public void updateUserInfo(UserBaseInfo userBaseInfo) {

    }

    @Override
    public void userLogout() {
        if (!getActivity().isFinishing()) {
            showView(State.unLogin);
        }
    }

    @Override
    public void userLogin(UserBaseInfo userBaseInfo) {
        if (!getActivity().isFinishing()) {
            checkNetWork();
        }
    }

    private void setPage(int loadingPage) {
        if (loadingPage == 1 && mAdapter != null) {
            mAdapter.resetList();
        }

        if (mCurrPage < mTotalPage) {
            mCurrPage = loadingPage + 1;
        } else if (mCurrPage >= mTotalPage) {
            mPullListView.setCanLoadMore(false);
        }

    }

    /**
     * 请求网络
     */
    private void requestData() {
        final Map<String, String> parames = new HashMap<>();
        parames.put("page", String.valueOf(mCurrPage));

        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.GIFTS_MY, parames, new WebCallBackToObject<UIModuleList>() {

            /**
             * 网络请求出错时调用
             *
             * @param statusCode 异常编号
             * @param error      异常信息
             */
            @Override
            public void onFailure(int statusCode, Throwable error) {
                mPullListView.setCanLoadMore(mCurrPage<=mTotalPage);
                mPullListView.onLoadMoreComplete();
                showView(State.networkErr);
            }

            @Override
            protected void handle(UIModuleList info) {
                mPullListView.setCanLoadMore(mCurrPage<=mTotalPage);
                mPullListView.onLoadMoreComplete();
                mTotalPage = getLastPage();
                setPage(Integer.valueOf(parames.get("page")));
                List<ItemData<? extends BaseUIModule>> temp = NetDataAddShell.wrapModuleList( info, mlastPagePositionOfNetData);
                setMlastPagePositionOfNetData(temp);
                if (temp != null && temp.size() > 0) {
                    showView(State.success);
                    mAdapter.addList(temp);
                    showView(State.success);
                } else {
                    showView(State.noData);
                }
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


    private void resetLayout() {
        mCurrPage = 1;
        mlastPagePositionOfNetData = 0;
    }

    @Override
    public void onRefresh() {
        checkNetWork();
    }

    @Override
    public void onLoadMore() {
        requestData();
    }

    private enum State {
        unLogin, networkErr, noData, success, loading
    }

}
