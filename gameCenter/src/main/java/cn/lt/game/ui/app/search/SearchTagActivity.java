package cn.lt.game.ui.app.search;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.pageunits.PageMultiUnitsReportManager;
import cn.lt.game.ui.app.adapter.LTBaseAdapter;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListenerImpl;
import cn.lt.game.ui.app.adapter.parser.NetDataAddShell;

/***
 * 搜索标签页/游戏首发页
 *
 * @author tiantian
 * @des /首页入口/游戏详情热门标签会跳转到这个页面
 */
public class SearchTagActivity extends BaseActivity implements RetryCallBack, SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener, RefreshAndLoadMoreListView.IOnScrollStateChanged {
    public static final String INTENT_TAG_ID = "id";
    public static final String INTENT_TAG_TITLE = "title";
    public static final int NEWPUBLISH = 0;
    public static final int TAGRESULT = 1;
    public NetWorkStateView netWorkStateView;
    private RefreshAndLoadMoreListView mPullToRefreshListView;
    private TitleBarView titlebar;
    private String mId = "";
    private String mTitle;
    private int mCurrPage = 1;
    private int mTotalPage;
    private BaseOnclickListener mClickListener;
    private LTBaseAdapter adapter;
    private int requestType;
    private int mlastPagePositionOfNetData = 0;
    private List<ItemData<? extends BaseUIModule>> saveTemp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getIntentData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tag);
        mClickListener = new BaseOnclickListenerImpl(this, getPageAlias());
        initView();
        resetLayout();
        requestData(false);
    }

    private void getIntentData() {
        mId = getIntent().getStringExtra(INTENT_TAG_ID);
        mTitle = getIntent().getStringExtra(INTENT_TAG_TITLE);
        if (TextUtils.isEmpty(mId) && TextUtils.isEmpty(mTitle)) {
            requestType = NEWPUBLISH;
        } else {
            requestType = TAGRESULT;
        }
    }

    private void initView() {
        netWorkStateView = (NetWorkStateView) findViewById(R.id.search_netWrokStateView);
        netWorkStateView.setRetryCallBack(this);
        netWorkStateView.showLoadingBar();
        titlebar = (TitleBarView) findViewById(R.id.search_action_bar);
        setTitleBar();
        mPullToRefreshListView = (RefreshAndLoadMoreListView) findViewById(R.id.search_tag_result_listview);
        mPullToRefreshListView.setmOnRefrshListener(this);
        mPullToRefreshListView.setOnLoadMoreListener(this);
        adapter = new LTBaseAdapter(this, mClickListener);
        mPullToRefreshListView.setAdapter(adapter, false);
        mPullToRefreshListView.setMyOnScrollListener(this);
//        netWorkStateView.setJumpIndexCallBack(this);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    private void setTitleBar() {
        switch (requestType) {
            case NEWPUBLISH:
                titlebar.setBackHomeVisibility(View.INVISIBLE); //GONE对布局有影响
                titlebar.setTitle("首发");
                break;
            case TAGRESULT:
                titlebar.setBackHomeVisibility(View.INVISIBLE);
                titlebar.setTitle(mTitle);
                break;
        }
    }

    protected boolean isFirstPage() {
        return mCurrPage == 1;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 更新数据，触发注册按键的下载进度监听
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            PageMultiUnitsReportManager.getInstance().buildPageUnits(saveTemp, mPullToRefreshListView, Constant.PAGE_GAME_TAG, getIntent().getStringExtra(INTENT_TAG_TITLE), getIntent().getStringExtra(INTENT_TAG_ID), "", "");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void setPageAlias() {
        if (NEWPUBLISH == requestType) { //首发
            setmPageAlias(Constant.PAGE_FIRST_PUBLISH);
        } else {
            setmPageAlias(Constant.PAGE_GAME_TAG, mId);
        }
    }

    @Override
    public void retry() {
        resetLayout();
        requestData(false);
    }

    private void resetLayout() {
        mCurrPage = 1;
        mlastPagePositionOfNetData = 0;
    }


    private void setPage(int loadingPage) {
        if (loadingPage == 1 && adapter != null) {
            adapter.resetList();
        }
        if (mCurrPage < mTotalPage) {
            mCurrPage = loadingPage + 1;
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

    /**
     * 请求网络
     *
     * @param isFromRefresh
     */
    private void requestData(final boolean isFromRefresh) {
        final Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(mCurrPage));
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, NEWPUBLISH == requestType ? Uri2.FIRST_PUBLISH_URI : Uri2.getHotTagDetailUri(mId), params, new WebCallBackToObject<UIModuleList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                mPullToRefreshListView.onLoadingFailed();
                inflateFaileLayout();
            }

            @Override
            protected void handle(UIModuleList info) {
                if (info.size() > 0) {
                    mTotalPage = getLastPage();
                    mPullToRefreshListView.setVisibility(View.VISIBLE);
                    mPullToRefreshListView.setCanLoadMore(mCurrPage < mTotalPage);
                    mPullToRefreshListView.onLoadMoreComplete();
                    netWorkStateView.hideNetworkFailLayout();
                    setPage(Integer.valueOf(params.get("page")));
                    List<ItemData<? extends BaseUIModule>> temp = NetDataAddShell.wrapModuleList(info, mlastPagePositionOfNetData);
                    setMlastPagePositionOfNetData(temp);
                    if (isFromRefresh) saveTemp.clear();
                    saveTemp.addAll(temp);
                    adapter.addList(temp);
                    MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PageMultiUnitsReportManager.getInstance().buildPageUnits(saveTemp, mPullToRefreshListView, Constant.PAGE_GAME_TAG, getIntent().getStringExtra(INTENT_TAG_TITLE), mId, "", "");
                        }
                    }, 500);
                    showLayout(temp);
                } else {
                    mPullToRefreshListView.setVisibility(View.GONE);
                    netWorkStateView.setNoDataLayoutText("该资源不存在", "");
                    netWorkStateView.showNetworkNoDataLayout();
                }
            }
        });
    }

    private void showLayout(List<ItemData<? extends BaseUIModule>> temp) {
        if (temp != null && temp.size() > 0) {
            inflateDataLayout();
        } else {
            inflateFaileLayout();
        }

    }


    private void inflateDataLayout() {
        mPullToRefreshListView.setVisibility(View.VISIBLE);
        netWorkStateView.hideNetworkView();
    }

    private void inflateFaileLayout() {
        netWorkStateView.showNetworkFailLayout();
        mPullToRefreshListView.setVisibility(View.GONE);
    }

    // 刷新Adapter
    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {
        resetLayout();
        requestData(true);
    }

    @Override
    public void onLoadMore() {
        LogUtils.i("SearchTagActivity","onLoadMore mCurrentPage"+mCurrPage);
        requestData(false);
    }

    @Override
    public void onScrollChangeListener(int scrollState) {
        reportWhenScroll(scrollState, Constant.PAGE_GAME_TAG, saveTemp, mPullToRefreshListView, getIntent().getStringExtra(INTENT_TAG_TITLE), getIntent().getStringExtra(INTENT_TAG_ID), "", "");
    }
}
