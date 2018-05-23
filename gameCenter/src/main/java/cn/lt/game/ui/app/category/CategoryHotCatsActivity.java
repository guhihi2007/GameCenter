package cn.lt.game.ui.app.category;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

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
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.view.TitleMoreButton;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.pageunits.PageMultiUnitsReportManager;
import cn.lt.game.ui.app.adapter.LTBaseAdapter;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListenerImpl;
import cn.lt.game.ui.app.adapter.parser.NetDataAddShell;

/**
 * 热门详情
 * 分类中的热门分类 Created by Apple on 15/3/31.
 */
public class CategoryHotCatsActivity extends BaseActivity implements RefreshAndLoadMoreListView.OnLoadMoreListener, RefreshAndLoadMoreListView.IOnScrollStateChanged {

    private RefreshAndLoadMoreListView mPullToRefreshListView;
    private NetWorkStateView netWrokStateView;
    private String categoryId;
    private String categoryTitle;
    private int mCurrentPage = 1;
    private LTBaseAdapter mAdapter;

    private int lastPagePositionOfNetData = 0;

    private List<ItemData<? extends BaseUIModule>> saveTemp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_categoryhottags);
        initView();
        loadData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 更新数据，触发注册按键的下载进度监听
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        PageMultiUnitsReportManager.getInstance().buildPageUnits(saveTemp, mPullToRefreshListView, Constant.PAGE_CATEGORY_HOT, getIntent().getStringExtra("title"), getIntent().getStringExtra("id"), "", "");
    }

    /**
     * 连接
     */
    protected void loadData() {
        if (NetUtils.isConnected(this)) {
            initAction();
        } else {
            final NetWorkStateView netWorkStateView = (NetWorkStateView) findViewById(R.id.game_detail_netWrokStateView);
            if (netWorkStateView != null) {
                netWorkStateView.showNetworkFailLayout();
                ToastUtils.showToast(getApplicationContext(), "网络连接失败");
                TextView tryAgain = (TextView) findViewById(R.id.network_fail_tryAgain);

                tryAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        netWorkStateView.showLoadingBar();
                        new Handler() {
                            public void handleMessage(android.os.Message msg) {
                                loadData();
                            }
                        }.sendEmptyMessageDelayed(0, 1000);
                    }
                });
            }
        }
    }

    protected void initView() {
        categoryId = getIntent().getStringExtra("id");
        categoryTitle = getIntent().getStringExtra("title");
        TitleBarView search_bar = (TitleBarView) findViewById(R.id.search_bar);
        mPullToRefreshListView = (RefreshAndLoadMoreListView) findViewById(R.id.pullToRefreshListView);
        search_bar.setTitle(categoryTitle);// 设置titlebar的文本
        search_bar.setMoreButtonType(TitleMoreButton.MoreButtonType.Special);
        mPullToRefreshListView.setRefreshEnabled(false);
        mPullToRefreshListView.setOnLoadMoreListener(this);
        mAdapter = new LTBaseAdapter(this, new BaseOnclickListenerImpl(this, getPageAlias()));
        mPullToRefreshListView.setAdapter(mAdapter, false);
        mPullToRefreshListView.setMyOnScrollListener(this);
    }

    public void initAction() {
        netWrokStateView = (NetWorkStateView) findViewById(R.id.game_detail_netWrokStateView);
        netWrokStateView.showLoadingBar();
        mCurrentPage = 1;
        getData(mCurrentPage);
    }


    private void getData(int page) {
        Map<String, String> map = new HashMap<>();
        map.put("id", categoryId);
        map.put("page", String.valueOf(page));
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.HOT_CATS_URI, map, new WebCallBackToObject<UIModuleList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                mPullToRefreshListView.onLoadingFailed();
            }

            @Override
            protected void handle(UIModuleList info) {

                netWrokStateView.hideNetworkView();
                List<ItemData<? extends BaseUIModule>> moduleList = NetDataAddShell.wrapModuleList(info, lastPagePositionOfNetData);
                setmLastPagePositionOfNetData(moduleList);

                if (moduleList.size() == 0) {
                    mPullToRefreshListView.setCanLoadMore(false);
                } else {
                    mPullToRefreshListView.setCanLoadMore(true);
                }
                saveTemp.addAll(moduleList);
                if (mCurrentPage == 1) {
                    mAdapter.setList(moduleList);
                } else {
                    mAdapter.addList(moduleList);
                }
                mPullToRefreshListView.onLoadMoreComplete();
                MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PageMultiUnitsReportManager.getInstance().buildPageUnits(saveTemp, mPullToRefreshListView, Constant.PAGE_CATEGORY_HOT, getIntent().getStringExtra("title"), categoryId, "", "");
                    }
                }, 500);
            }
        });
    }

    private void setmLastPagePositionOfNetData(List<ItemData<? extends BaseUIModule>> temp) {
        try {
            if (temp != null && temp.size() > 0) {
                this.lastPagePositionOfNetData += temp.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_CATEGORY_HOT, categoryId);

    }

    @Override
    public void onLoadMore() {
        getData(++mCurrentPage);
    }

    @Override
    public void onScrollChangeListener(int scrollState) {
        reportWhenScroll(scrollState, Constant.PAGE_CATEGORY_HOT, saveTemp, mPullToRefreshListView, getIntent().getStringExtra("title"), getIntent().getStringExtra("id"), "", "");
    }
}
