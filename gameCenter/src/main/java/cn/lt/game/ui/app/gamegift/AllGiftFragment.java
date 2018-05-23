package cn.lt.game.ui.app.gamegift;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.SearchView;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.app.adapter.LTBaseAdapter;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListenerImpl;
import cn.lt.game.ui.app.adapter.parser.NetDataAddShell;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

/**
 * Created by JohnsonLin on 2017/7/27.
 */

public class AllGiftFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        NetWorkStateView.RetryCallBack, RefreshAndLoadMoreListView.OnLoadMoreListener, UserInfoUpdateListening {

    private BaseOnclickListenerImpl mClickListener;
    private View mRoot;
    private RefreshAndLoadMoreListView mPullListView;
    private NetWorkStateView mNetWorkStateView;
    private LinearLayout mHeaderSearchView;
    private ListView mListViewInPull;
    private LTBaseAdapter mAdapter;
    private int mCurrPage;
    private int mTotalPage;

    private int lastPagePositionOfNetData = 0;

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_ALL_GIFT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.all_gift_layout, container, false);
            mClickListener = new BaseOnclickListenerImpl(getActivity(), getPageAlias());
            initView();
            fillLayout();
        }

        return mRoot;
    }


    /**
     * 初始化视图；
     */
    private void initView() {
        mPullListView = (RefreshAndLoadMoreListView) mRoot.findViewById(R.id.allGift_listView);
        mNetWorkStateView = (NetWorkStateView) mRoot.findViewById(R.id.netwrolStateView);
        mNetWorkStateView.hideNetworkFailLayout();
        mNetWorkStateView.setRetryCallBack(this);
        mNetWorkStateView.setNoDataLayoutText("暂无最新礼包！", null);
        initListView();

        mNetWorkStateView.showLoadingBar();
    }


    private void initListView() {
        mListViewInPull = mPullListView.getmListView();
        mAdapter = new LTBaseAdapter(mActivity, mClickListener);
        mPullListView.setmOnRefrshListener(this);
        mPullListView.setOnLoadMoreListener(this);
        addHeadToListView();
        mPullListView.setAdapter(mAdapter, false);
    }

    /**
     * 添加listView 头部View
     */
    private void addHeadToListView() {
        initHeadView();
        mListViewInPull.addHeaderView(mHeaderSearchView);
    }

    private void initHeadView() {
        mHeaderSearchView = (LinearLayout) LayoutInflater.from(mActivity).inflate(R.layout.gift_serach_bar, null);
        SearchView searchView = (SearchView) mHeaderSearchView.findViewById(R.id.gamegiftCenter_searchBar);
        ((LinearLayout.LayoutParams) searchView.getLayoutParams()).bottomMargin = getResources().getDimensionPixelSize(R.dimen.margin_size_8dp);

        View whiteView = new View(getContext());
        whiteView.setBackground(getResources().getDrawable(R.color.white));
        mHeaderSearchView.addView(whiteView);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) whiteView.getLayoutParams();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = getResources().getDimensionPixelSize(R.dimen.margin_size_14dp);
    }

    /**
     * 填充数据；
     */
    private void fillLayout() {
        resetLayout();
        requestData();
    }

    private void resetLayout() {
        mCurrPage = 1;
        mPullListView.setCanLoadMore(true);
    }

    /**
     * 获取网络请求数据；
     */
    private void requestData() {
        final Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(mCurrPage));
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.ALL_GIFTS, params, new WebCallBackToObject<UIModuleList>() {

            @Override
            protected void handle(UIModuleList list) {

                if (null != list && list.size() > 0) {
                    mPullListView.setVisibility(View.VISIBLE);
                    mTotalPage = getLastPage();

                    setData(list);

                } else if (mCurrPage == 1){
                    showNoDataNetWorkView();
                }

                mPullListView.onLoadMoreComplete();
            }


            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.d("AllGiftTAG", "数据请求失败！" + statusCode);
                mPullListView.setVisibility(View.VISIBLE);
                mPullListView.onLoadingFailed();
                if (mCurrPage == 1) {
                    inflateNetWorkErrView();
                }
            }
        });

    }

    private void setData(UIModuleList moduleList) {
        try {
            List<ItemData<? extends BaseUIModule>> temp = NetDataAddShell.wrapModuleList(moduleList, lastPagePositionOfNetData);
            if (mCurrPage == 1) {
                mAdapter.resetList();
                setLastPagePositionOfNetData(temp);
            }
            mAdapter.addList(temp);
            mNetWorkStateView.hideNetworkView();
            mPullListView.setCanLoadMore(mCurrPage < mTotalPage);
        } catch (Exception e) {
            e.printStackTrace();
            if (mCurrPage == 1) {
                inflateNetWorkErrView();
            }
        }
    }



    private void setLastPagePositionOfNetData(List<ItemData<? extends BaseUIModule>> temp) {
        try {
            if (temp != null && temp.size() > 0) {
                ItemData item = temp.get(temp.size() - 1);
                this.lastPagePositionOfNetData = item.getPos();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    public void retry() {
        resetLayout();
        requestData();
    }


    @Override
    public void onRefresh() {
        resetLayout();
        requestData();
    }

    @Override
    public void onLoadMore() {
        mCurrPage ++;
        requestData();
    }

    /**
     * 初始化网络错误提示界面；
     */
    private void inflateNetWorkErrView() {
        mNetWorkStateView.showNetworkFailLayout();
        mPullListView.setVisibility(View.GONE);
    }

    private void showNoDataNetWorkView() {
        mNetWorkStateView.showNetworkNoDataLayout();
        mPullListView.setVisibility(View.GONE);
    }
}
