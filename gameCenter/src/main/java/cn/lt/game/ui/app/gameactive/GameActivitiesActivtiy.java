package cn.lt.game.ui.app.gameactive;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.datalayer.EventContext;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.domain.essence.FunctionEssenceImpl;
import cn.lt.game.domain.essence.IdentifierType;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.view.TitleMoreButton.MoreButtonType;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.app.adapter.LTBaseAdapter;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.parser.NetDataAddShell;
import de.greenrobot.event.EventBus;

/**
 * 游戏活动页面
 *
 * @author tiantian
 * @des
 */
public class GameActivitiesActivtiy extends BaseActivity implements  SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener , RetryCallBack {
    public NetWorkStateView netWorkStateView;
    private RefreshAndLoadMoreListView mPullToRefreshListView;
    private TitleBarView titlebar;
    private int currentPage = 1;
    private EventContext eventContext;
    private List<ItemData<? extends BaseUIModule>> requestDatalist;
    private LTBaseAdapter adapter;
    private ActivitiesClickListener activitiesClickListener;
    private int mlastPagePositionOfNetData = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tag);
        initView();
        checkNetWork();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        netWorkStateView = (NetWorkStateView) findViewById(R.id.search_netWrokStateView);
        netWorkStateView.setRetryCallBack(this);
        titlebar = (TitleBarView) findViewById(R.id.search_action_bar);
        titlebar.setMoreButtonType(MoreButtonType.Special);
        titlebar.setTitle("活动列表");
        mPullToRefreshListView = (RefreshAndLoadMoreListView) findViewById(R.id.search_tag_result_listview);
        mPullToRefreshListView.setmOnRefrshListener(this);
        mPullToRefreshListView.setOnLoadMoreListener(this);
//        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        requestDatalist = new ArrayList<>();
        activitiesClickListener = new ActivitiesClickListener(this, getPageAlias());
        adapter = new LTBaseAdapter(this, activitiesClickListener);
        mPullToRefreshListView.setAdapter(adapter, false);
    }


    /**
     * 检查是否有网络，否则显示网络错误页面
     */
    private void checkNetWork() {
        if (NetUtils.isConnected(this)) {
            netWorkStateView.showLoadingBar();
            requestData(currentPage);
        } else {
            mPullToRefreshListView.setVisibility(View.GONE);
            netWorkStateView.showNetworkFailLayout();
        }
    }

    /**
     * 请求网络数据
     */
    private void requestData(final int page) {
        final Map<String, String> params = new HashMap<>();
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.ACTIVITIES_URI, params, new WebCallBackToObject<UIModuleList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                mPullToRefreshListView.setVisibility(View.GONE);
                mPullToRefreshListView.onLoadingFailed();
                mPullToRefreshListView.setCanLoadMore(true);
                mPullToRefreshListView.onLoadMoreComplete();
                netWorkStateView.hideLoadingBar();
                netWorkStateView.showNetworkFailLayout();
            }

            @Override
            protected void handle(UIModuleList info) {
                try {
                    requestDatalist = NetDataAddShell.wrapModuleList(info, mlastPagePositionOfNetData);
                    setMlastPagePositionOfNetData(requestDatalist);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                processData2();
                netWorkStateView.hideLoadingBar();
                netWorkStateView.hideNetworkView();
                mPullToRefreshListView.setCanLoadMore(false);
                mPullToRefreshListView.onLoadMoreComplete();
                mPullToRefreshListView.setVisibility(View.VISIBLE);
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


    private void processData2() {
        if (requestDatalist.size() == 0) {
            mPullToRefreshListView.setVisibility(View.GONE);
            netWorkStateView.setNotDataState(NetWorkStateView.gotoIndexActity);
            netWorkStateView.setNoDataLayoutText("还没有活动，敬请期待", "");
            netWorkStateView.showNetworkNoDataLayout();
        } else {
            mPullToRefreshListView.setVisibility(View.VISIBLE);
            netWorkStateView.hideNetworkNoDataLayout();
            netWorkStateView.hideNetworkView();
            adapter.resetList();
            adapter.addList(requestDatalist);
        }
    }

    protected boolean isFirstPage() {
        return currentPage == 1;
    }


    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_ACTIVITY_LIST);
    }

    @Override
    public void retry() {
        checkNetWork();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRefresh() {
        requestData(currentPage);
    }

    @Override
    public void onLoadMore() {
        requestData(++currentPage);
    }

    private class ActivitiesClickListener extends BaseOnclickListener {

        public ActivitiesClickListener(Context context, String pageName) {
            super(context, pageName);
            this.mContext = context;
            this.mPageName = pageName;
        }

        @Override
        public boolean realOnClick(View v, String mPageName) {
            FunctionEssence data = (FunctionEssenceImpl) v.getTag(R.id.view_data);

            /**跳转到WebView页面*/
            ActivityActionUtils.jumpToWebView(GameActivitiesActivtiy.this, data.getTitle() == null ? "" : data.getTitle(), data.getUniqueIdentifierBy(IdentifierType.URL));
            Log.i("活动Log", "jumpToWebView~~~跳呀跳, id = " + data.getUniqueIdentifierBy(IdentifierType.ID));
            return false;
        }


    }
}
