package cn.lt.game.ui.app.gamegift;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.view.SearchView;
import cn.lt.game.lib.view.SearchView.isTopActivityCallBack;
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

public class GiftSearchActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener,
        RetryCallBack, UserInfoUpdateListening {

    public final static int GAME_NUMBER_PER_LINE = 3;
    private String TAG = "GiftSearchActivity";
    /**
     * 搜索条
     */
    private SearchView mSearchView;

    /**
     * 展示数据
     */

    private ListView mListViewInPull;

    private RefreshAndLoadMoreListView mPullListView;

    /**
     * 查询页面Adapter
     */
    private LTBaseAdapter mAdapter;
    private BaseOnclickListener mClickListener;

    /**
     * 点击搜索按钮时搜索框内的搜索关键字
     */
    private String mKeyWord = "";
    private int mTotalPage;
    private int mCurrPage = 1;
    private TextView mNoSearchData;

    /**
     * 网络相关布局视图
     */
    private NetWorkStateView mNetWorkStateView;
    private int mlastPagePositionOfNetData = 0;

    /**
     * 最热礼包数据
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserInfoManager.instance().removeListening(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giftsearch);
        mClickListener = new BaseOnclickListenerImpl(this, getPageAlias());
        initView();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getIntentData();
        UserInfoManager.instance().addListening(this);
    }

    private void getIntentData() {
        mKeyWord = getIntent().getStringExtra(SearchView.KEYWORD);
        if (!TextUtils.isEmpty(mKeyWord)) {
            checkNetWork();
        }
    }

    /**
     * 检查网络，如果有网路则请求网络数据，无网络显示无网络界面；
     */
    private void checkNetWork() {
        resetLayout();
        if (NetUtils.isConnected(this)) {
            requestData();
        } else {
            inflateNetWorkErrView();
        }

    }

    /**
     * 初始化网络错误提示界面；
     */
    private void inflateNetWorkErrView() {
        mNetWorkStateView.showNetworkFailLayout();
        mCurrPage = 1;
    }

    private void initView() {
        mSearchView = (SearchView) findViewById(R.id.sv_gift_search);
        mPullListView = (RefreshAndLoadMoreListView) findViewById(R.id.glv_gift_search1);
        mNoSearchData = (TextView) findViewById(R.id.tv_no_search_data);
        mNetWorkStateView = (NetWorkStateView) findViewById(R.id.rank_netwrolStateView);
        mNetWorkStateView.setRetryCallBack(this);
        initSearchView();
        initListView();
    }

    private void initListView() {
        mPullListView.setRefreshEnabled(false);
        mListViewInPull = mPullListView.getmListView();
        mAdapter = new LTBaseAdapter(this, mClickListener);
        mPullListView.setAdapter(mAdapter, false);
        mPullListView.setmOnRefrshListener(this);
        mPullListView.setOnLoadMoreListener(this);
    }

    private void initSearchView() {
        String key = getIntent().getStringExtra("keyWord");
        mSearchView.setEtTextCharacters(key);
        mSearchView.getSearchEt().setSelection(key.length());
        mSearchView.setIsTopActivityCallBack(new isTopActivityCallBack() {

            @Override
            public void OnRefreshCurrentClass() {
                // 隐藏软键盘
                if (GiftSearchActivity.this.getCurrentFocus() != null) {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(GiftSearchActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                getInputKeyWord();
                checkNetWork();
            }

        });
    }

    /**
     * 搜索礼包；
     */
    private void getInputKeyWord() {
        mKeyWord = mSearchView.getSearchEt().getText().toString().trim();
    }

    @Override
    public void retry() {
        checkNetWork();
    }

    private void resetLayout() {
        mCurrPage = 1;
        mNetWorkStateView.showLoadingBar();
    }

    @Override
    public void userLogin(UserBaseInfo userBaseInfo) {
        if (!isFinishing()) {
            checkNetWork();
        }
    }

    @Override
    public void updateUserInfo(UserBaseInfo userBaseInfo) {

    }

    @Override
    public void userLogout() {

    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_GIFT_SEARCH);
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
     * 获取网络请求数据；
     */
    private void requestData() {
        final Map<String, String> params = new HashMap<>();
        params.put("q", mKeyWord);
        if (mCurrPage > 1) {
            params.put("page", String.valueOf(mCurrPage));
        }
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.GIFTS_URI, params, new WebCallBackToObject<UIModuleList>() {
            @Override
            protected void handle(UIModuleList info) {
                mPullListView.setCanLoadMore(mCurrPage<=mTotalPage);
                mPullListView.onLoadMoreComplete();
                mPullListView.setVisibility(View.VISIBLE);
                mNetWorkStateView.hideNetworkView();
                mTotalPage = getLastPage();
                List<ItemData<? extends BaseUIModule>> temp = NetDataAddShell
                        .wrapModuleList( info,mlastPagePositionOfNetData);
                setMlastPagePositionOfNetData(temp);
                if (params.get("page") != null) {//默认情况下为第一页时是不会带改参数的；大于第一页；
                    setPage(Integer.valueOf(params.get("page")));
                } else {//第一页；
                    setPage(1);
                    addTextItem(temp, true);
                }
                mAdapter.addList(temp);
            }

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
                mPullListView.setVisibility(View.VISIBLE);
                mNetWorkStateView.hideNetworkView();
                inflateNetWorkErrView();
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


    private void checkResult(boolean flag) {
        if (flag) {
            mNoSearchData.setText("抱歉，暂无相关游戏礼包！");
            mNoSearchData.setVisibility(View.VISIBLE);
        } else {
            mNoSearchData.setVisibility(View.GONE);
        }
    }

    private void addTextItem(List<ItemData<? extends BaseUIModule>> moduleList, boolean
            isFirstPage) {

        if (isFirstPage) {
            int size = moduleList.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    if (i == 0 && PresentType.hot_gifts == moduleList.get(i).getmPresentType()) {
                        checkResult(true);
                    } else {
                        if (i == 0) {
                            checkResult(false);
                        }
                        if (PresentType.gifts_search_lists == moduleList.get(i).getmPresentType() &&
                                (i < size || mTotalPage > 1)) {
                            UIModule<String> item = new UIModule<>(PresentType.new_gifts_title, "按礼包排序");
                            ItemData data = new ItemData(item);
                            data.setmType(PresentType.new_gifts_title);
                            moduleList.add(i, data);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        requestData();
    }
}
