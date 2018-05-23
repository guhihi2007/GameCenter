package cn.lt.game.ui.app.specialtopic;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.domain.essence.FunctionEssenceImpl;
import cn.lt.game.domain.essence.ImageType;
import cn.lt.game.download.DownloadChecker;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.DownloadBtnClickedEvent;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.event.NetworkChangeEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.install.ApkInstallManger;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.util.log.Logger;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.view.TitleMoreButton;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.model.State;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.statistics.pageunits.PageMultiUnitsReportManager;
import cn.lt.game.ui.app.adapter.LTBaseAdapter;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListenerImpl;
import cn.lt.game.ui.app.adapter.parser.NetDataAddShell;
import de.greenrobot.event.EventBus;

/**
 * 这是专题详情的界面
 */
public class SpecialTopicDetailsActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, NetWorkStateView.RetryCallBack, RefreshAndLoadMoreListView.IOnScrollStateChanged {

    private TitleBarView search_bar;
    private RefreshAndLoadMoreListView mPullToRefreshListView;
    private String topicId;
    private NetWorkStateView netWorkStateView;
    private TextView tv_alldown;
    private RelativeLayout ly_alldown;
    private TextView tv_tilte;
    private TextView tv_summary;

    private List<ItemData<? extends BaseUIModule>> requestDatalist;
    private ItemData<? extends BaseUIModule> topicInfoData;
    private ImageView iv_headImageView;
    private LTBaseAdapter adapter;
    private BaseOnclickListener mClickListener;
    private List<GameBaseDetail> gameBaseDetailList;
    private int mlastPagePositionOfNetData = 0;
    private List<ItemData<? extends BaseUIModule>> saveTemp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_special_topic_details_v2);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
        loadData();
    }

    protected void initView() {
        topicId = getIntent().getStringExtra("topicId");
        mClickListener = new BaseOnclickListenerImpl(this, getPageAlias());
        search_bar = (TitleBarView) findViewById(R.id.search_bar);
        search_bar.setMoreButtonType(TitleMoreButton.MoreButtonType.Special);

        ly_alldown = (RelativeLayout) findViewById(R.id.rl_alldowns);
        ly_alldown.setVisibility(View.GONE);
        tv_alldown = (TextView) findViewById(R.id.tv_alldown);
        View head_detailsView = View.inflate(this, R.layout.item_special_topic_head_details_v2, null);
        tv_tilte = (TextView) head_detailsView.findViewById(R.id.tv_title);
        tv_summary = (TextView) head_detailsView.findViewById(R.id.tv_xbym);
        iv_headImageView = (ImageView) head_detailsView.findViewById(R.id.iv_headImageView);

        mPullToRefreshListView = (RefreshAndLoadMoreListView) findViewById(R.id.pullToRefreshListView);
        mPullToRefreshListView.getmListView().addHeaderView(head_detailsView, null, false);
        mPullToRefreshListView.setmOnRefrshListener(this);
        netWorkStateView = (NetWorkStateView) findViewById(R.id.game_detail_netWrokStateView);
        netWorkStateView.showLoadingBar();
        netWorkStateView.setRetryCallBack(this);

        tv_alldown.setOnClickListener(this);

        adapter = new LTBaseAdapter(this, mClickListener);
        mPullToRefreshListView.setAdapter(adapter, true);
        mPullToRefreshListView.setMyOnScrollListener(this);
    }


    /**
     * 连接
     */
    protected void loadData() {
        if (NetUtils.isConnected(this)) {
            getNetWorkData();
        } else {
            netWorkStateView.showNetworkFailLayout();
        }
    }

    @Override
    public void retry() {
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.getSpecialTopicDetailUri(topicId), new WebCallBackToObject<UIModuleList>() {

            @Override
            protected void handle(UIModuleList info) {

                if (info.size() == 0) {
                    netWorkStateView.hideLoadingBar();
                    netWorkStateView.showNetworkNoDataLayout();
                    netWorkStateView.setNoDataLayoutText("该资源不存在", "");
                } else {
                    mPullToRefreshListView.onLoadMoreComplete();
                    requestDatalist = NetDataAddShell.wrapModuleList(info, mlastPagePositionOfNetData);
                    saveTemp.addAll(requestDatalist);
                    setMlastPagePositionOfNetData(requestDatalist);
                    topicInfoData = requestDatalist.remove(0);
                    setTopicInfo(topicInfoData);
                    setGameLisData();
                    netWorkStateView.hideLoadingBar();
                    netWorkStateView.hideNetworkView();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                mPullToRefreshListView.onLoadingFailed();
                mPullToRefreshListView.setVisibility(View.GONE);
                netWorkStateView.hideLoadingBar();
                netWorkStateView.showNetworkFailLayout();
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

    public void onEventMainThread(DownloadUpdateEvent updateEvent) {
        if (updateEvent == null || updateEvent.game == null) {
            return;
        }
        LogUtils.i("sda3r","onEventMainThread:" + updateEvent.game.isOrderWifiDownload());


        if (DownloadState.downloadComplete == updateEvent.game.getState() || isWifi) {
            showAllDownLayout();
        }
    }

    private void setTopicInfo(ItemData<? extends BaseUIModule> topicInfoData) {
        UIModule module = (UIModule) topicInfoData.getmData();
        FunctionEssence data = (FunctionEssenceImpl) module.getData();

        search_bar.setTitle(data.getTitle());
        tv_tilte.setText(data.getTitle());
        tv_summary.setText("小编语录：" + data.getSummary());
        Logger.i("图片" + data.getImageUrl().get(ImageType.COMMON));
        ImageloaderUtil.loadRecImage(this, data.getImageUrl().get(ImageType.COMMON), iv_headImageView);
    }

    private void setGameLisData() {
        parseToGameList();
        showAllDownLayout();
        adapter.setList(requestDatalist);
        adapter.notifyDataSetChanged();
        MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PageMultiUnitsReportManager.getInstance().buildPageUnits(saveTemp, mPullToRefreshListView, Constant.PAGE_SUBJECT_DETAIL, "", getIntent().getStringExtra("topicId"), "", "");
            }
        }, 500);  //才能取到可见的position
    }

    private void getNetWorkData() {
        retry();
    }

    @Override
    protected void onResume() {
        // 更新数据，触发注册按键的下载进度监听
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        mEventID = topicId;
        showAllDownLayout();
        try {
            PageMultiUnitsReportManager.getInstance().buildPageUnits(saveTemp, mPullToRefreshListView, Constant.PAGE_SUBJECT_DETAIL, "", getIntent().getStringExtra("topicId"), "", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    private void showAllDownLayout() {
        if (gameBaseDetailList == null) {
            return;
        }

        int downCnt = 0;
        int installCnt = 0;
        int orderWifiDownloadCnt = 0;
        for (int i = 0; i < gameBaseDetailList.size(); i++) {
            GameBaseDetail game = gameBaseDetailList.get(i);
            GameBaseDetail downFile = FileDownloaders.getDownFileInfoById(game.getId());
            if (downFile != null) {
                game.setDownInfo(downFile);
            } else {
                game.setState(DownloadState.undownload);
                game.setDownLength(0);
            }

            if (game.isOrderWifiDownload()) {
                orderWifiDownloadCnt ++;
            }

            switch (game.getState()) {
                case DownloadState.undownload:
                case DownloadState.downloadPause:
                case DownloadState.downloadFail:
                case InstallState.upgrade: {
                    ++downCnt;
                    break;
                }
                case InstallState.install: {
                    ++installCnt;
                    break;
                }
                default:
                    break;
            }
        }
        LogUtils.i("sda3r", "downCnt = " + downCnt);
        if (orderWifiDownloadCnt == gameBaseDetailList.size()) {
            ly_alldown.setVisibility(View.GONE);
        } else if (downCnt >= 1) {
            ly_alldown.setVisibility(View.VISIBLE);
            tv_alldown.setText("一键下载");
        } else if (installCnt == gameBaseDetailList.size()) {

            ly_alldown.setVisibility(View.VISIBLE);
            tv_alldown.setText("一键安装");
        } else {
            ly_alldown.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {

        int installCnt = 0;
        List<GameBaseDetail> willDownloadList = new ArrayList<>();
        for (int i = 0; i < gameBaseDetailList.size(); i++) {
            GameBaseDetail game = gameBaseDetailList.get(i);
            switch (game.getState()) {
                case DownloadState.undownload:
                case DownloadState.downloadPause:
                case DownloadState.downloadFail:
                case InstallState.upgrade:
                case InstallState.installFail:
                    willDownloadList.add(game);
                    break;
                case InstallState.install:// 未安装计数
                    installCnt++;
                    break;
                default:
                    break;
            }

        }

        if (willDownloadList.size() > 0) {
            downloadAllApp(willDownloadList);
        }

        // 如果所有游戏都是未安装状态，全部启动安装
        if (installCnt == gameBaseDetailList.size()) {
            for (int i = 0; i < gameBaseDetailList.size(); i++) {
                GameBaseDetail game = gameBaseDetailList.get(i);
                ApkInstallManger.self().installPkg(game, Constant.MODE_ONEKEY, null, false);
            }
            ly_alldown.setVisibility(View.GONE);
        }


    }

    /**
     * 一键下载所有APP
     */
    private void downloadAllApp(final List<GameBaseDetail> willDownloadList) {
        // 一键下载
        DownloadChecker.getInstance().check(this, new DownloadChecker.Executor() {
            @Override
            public void run() {
                // 正常下载
                for (GameBaseDetail game : willDownloadList) {
                    Utils.gameDown(SpecialTopicDetailsActivity.this, game, getPageAlias(), true, Constant.MODE_ONEKEY, Constant.DOWNLOAD_TYPE_NORMAL, null);
                }
                ly_alldown.setVisibility(View.GONE);

            }

            @Override
            public void reportOrderWifiClick() {

            }
        }, new DownloadChecker.Executor() {
            @Override
            public void run() {
                // 暂停下载
                for (GameBaseDetail game : willDownloadList) {
                    FileDownloaders.stopDownload(game.getId());
                    State.updateState(game, DownloadState.downloadPause);
                    FileDownloaders.downloadNext();
                }

            }

            @Override
            public void reportOrderWifiClick() {

            }
        }, new DownloadChecker.Executor() {
            @Override
            public void run() {
                // 预约wifi下载
                for (GameBaseDetail game : willDownloadList) {
                    Utils.gameDownByOrderWifi(SpecialTopicDetailsActivity.this, game, getPageAlias(), true, Constant.MODE_ONEKEY, Constant.DOWNLOAD_TYPE_NORMAL, null);
                }
                ly_alldown.setVisibility(View.GONE);
            }

            @Override
            public void reportOrderWifiClick() {
                for (GameBaseDetail game : willDownloadList) {
                    DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, "null", 0, null, 0, "" + game.getId(), null, "manual", "orderWifiDownload", game.getPkgName(),""));
                }
            }
        });
    }

    private void parseToGameList() {
        gameBaseDetailList = new ArrayList<>();
        if (requestDatalist != null && requestDatalist.size() > 0) {
            for (int i = 0; i < requestDatalist.size(); i++) {
                UIModule module = (UIModule) requestDatalist.get(i).getmData();
                if (module.getUIType() == PresentType.game) {
                    GameDomainBaseDetail gameBase = (GameDomainBaseDetail) module.getData();
                    GameBaseDetail game = new GameBaseDetail().setGameBaseInfo(gameBase);
                    gameBaseDetailList.add(game);
                }
            }
        }
    }


    @Override
    public void setPageAlias() {

        setmPageAlias(Constant.PAGE_SUBJECT_DETAIL);
    }

//    @Override
//    public void onLoadMore() {
//        mPullToRefreshListView.onLoadMoreComplete();
//        ToastUtils.showToast(this, "专题详情加载完毕!");
//        //
//        // TODO: 2017/7/28 要暴露方法
////        mPullToRefreshListView.getmListView().setFooterHeight(tv_alldown.getMeasuredHeight() + 20);
//    }

    @Override
    public void onScrollChangeListener(int scrollState) {
        reportWhenScroll(scrollState, Constant.PAGE_SUBJECT_DETAIL, saveTemp, mPullToRefreshListView, "", getIntent().getStringExtra("topicId"), "", "");
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /** 监听下载按钮被点击*/
    public void onEventMainThread(DownloadBtnClickedEvent event) {

            MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showAllDownLayout();
                }
            }, 100);
    }

    private boolean isWifi = false;

    /**
     * 切换到WiFi网络
     * @param event
     */
    public void onEventMainThread(NetworkChangeEvent event) {
        LogUtils.i("sda3r", "event.type = " + event.type);
        if (event.type == ConnectivityManager.TYPE_WIFI) {
            isWifi = true;
        } else {
            isWifi = false;
        }
    }
}
