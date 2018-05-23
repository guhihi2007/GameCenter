package cn.lt.game.ui.app.management;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.base.Item;
import cn.lt.game.download.DownloadChecker;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloader;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.ApkNotExistEvent;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.event.InstallEvent;
import cn.lt.game.event.RefreshUpgradePageEvent;
import cn.lt.game.event.UninstallEvent;
import cn.lt.game.event.UpgradeEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.install.ApkInstallManger;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.ScreenUtils;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.JumpIndexCallBack;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.model.State;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.notification.LTNotification;
import cn.lt.game.ui.notification.LTNotificationManager;
import de.greenrobot.event.EventBus;

/**
 * 升级界面
 */
public class UpgradeFragment extends BaseFragment implements OnClickListener, JumpIndexCallBack, OnScrollListener {
    private static final int COUNT = 3;
    private Activity mActivity;
    private List<GameBaseDetail> mUpgradeGames;
    private UpgradeAdapter mAdapter;
    private View mView = null;
    private ListView listView;
    private NetWorkStateView netWorkStateView;
    private RelativeLayout downAll_Layout;
    private TextView downAll_btn;
    private TextView downAll_title;

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_MANGER_UPGRADE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
        mAdapter = new UpgradeAdapter(mActivity, this);

        EventBus.getDefault().register(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView = inflater.inflate(R.layout.fragment_managemet_layout, container, false);
        findView();
        initEmptyLayout();
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
        getIntent();
        initEmptyLayout();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(UpgradeEvent upgradeEvent) {
        updateView();
    }

    public void onEventMainThread(InstallEvent installEvent) {
        updateView();
    }

    public void onEventMainThread(UninstallEvent unInstallEvent) {
        updateView();
    }

    public void onEventMainThread(DownloadUpdateEvent updateEvent) {
        if (updateEvent == null) return;
        if (updateEvent.ev == DownloadUpdateEvent.EV_IGNORE_UPGRADE || updateEvent.ev == DownloadUpdateEvent.EV_CANCLE_IGNORE_UPGRADE) {
            updateView();
        } else {
            for (int i = 0; i < mAdapter.getList().size(); i++) {
                Item item = mAdapter.getList().get(i);
                if (item.data instanceof GameBaseDetail) {
                    if (((GameBaseDetail) item.data).getId() == updateEvent.game.getId()) {
                        updateView(i);
                    }
                }
            }
        }

    }

    private void updateView() {
        getData();
        initEmptyLayout();
        initDownAllShow();
    }


    private void updateView(int position) {
        int visiblePosition = listView.getFirstVisiblePosition();
        if (position - visiblePosition >= 0) {
            View view = listView.getChildAt(position - visiblePosition);
            mAdapter.updateView(view, position);
        }
    }

    private void getIntent() {
        int tab = mActivity.getIntent().getIntExtra("tab_id", -1);
        boolean isNotif = mActivity.getIntent().getBooleanExtra("isNotif", false);
        boolean isUpgradeAll = mActivity.getIntent().getBooleanExtra("upgrade_all", false);
        LogUtils.i("Erosion", "isUpgradeAll===" + isUpgradeAll);
        if (isNotif && tab == LTNotification.TAB_UPGRADE) {
            if (isUpgradeAll) {
                MyApplication.castFrom(mActivity).setNewGameUpdate(false);
                onKeyUpgrade(null);
            }
            mActivity.getIntent().removeExtra("tab_id");
            mActivity.getIntent().removeExtra("isNotif");
        }

    }

    private void findView() {
        listView = (ListView) mView.findViewById(R.id.management_listView);
        netWorkStateView = (NetWorkStateView) mView.findViewById(R.id.management_networkStateView);
        netWorkStateView.setNoDataLayoutText("好棒棒哦！游戏都是最新的版本", "随便逛逛");
        netWorkStateView.setIsfinish(false);
        netWorkStateView.setJumpIndexCallBack(this);
        downAll_Layout = (RelativeLayout) mView.findViewById(R.id.management_downAll);
        downAll_btn = (TextView) mView.findViewById(R.id.down_all_game_down);
        downAll_title = (TextView) mView.findViewById(R.id.down_all_game_title);
        downAll_btn.setOnClickListener(this);
        downAll_btn.setText("一键升级");

        listView.setAdapter(mAdapter);

        mAdapter.setOnItemMoreClickListener(new InstalledAdapter.OnItemMoreClickListener() {
            @Override
            public void onclick(int position, View view, final GameBaseDetail gameBaseDetail) {
                // 是否已经启动过下载的升级任务
                boolean notDownloading = gameBaseDetail.getState() == InstallState.upgrade || gameBaseDetail.getState() == InstallState.ignore_upgrade;
                if (notDownloading) {
                    // 弹忽略升级按钮
                    showIgnorePopup(view, gameBaseDetail);
                } else {
                    // 弹删除按钮
                    showDeleteTaskPopup(view, gameBaseDetail);
                }

            }
        });
    }

    private void showDeleteTaskPopup(View view, final GameBaseDetail game) {
        ItemPopupWindow popupWindow = new ItemPopupWindow(mActivity, new int[]{R.string.delete});
        popupWindow.showAsDropDown(view, -(int) ScreenUtils.dpToPx(mActivity, 35), (int) ScreenUtils.dpToPx(mActivity, 8));

        popupWindow.setOnPopupItemClickListener(new ItemPopupWindow.OnPopupItemClickListener() {
            @Override
            public void onClick(int position) {
                FileDownloader loader = FileDownloaders.getFileDownloader(game.getDownUrl());
                if (loader != null) {
                    loader.stopDownload();
                    loader.deleteSaveFile();
                }
                // 还原本类的game的状态为upgrade
                game.setState(InstallState.upgrade);
                game.setDownLength(0);
                game.setOrderWifiDownload(false);
                FileDownloaders.update(game);
                EventBus.getDefault().post(new DownloadUpdateEvent(game));
                LTNotificationManager.getinstance().deleteGameNotification(game);
                updateView();
                DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, Constant.PAGE_MANGER_UPGRADE, 0, "game", 0, game.getId() + "", null, Constant.RETRY_TYPE_MANUAL, "upgradeDelete", game.getPkgName(),""));
            }
        });
    }

    private void showIgnorePopup(View view, final GameBaseDetail gameBaseDetail) {
        final boolean ignore = mUpgradeGames != null && mUpgradeGames.contains(gameBaseDetail);
        ItemPopupWindow popupWindow = new ItemPopupWindow(mActivity, new int[]{ignore ? R.string.ignore_upgrade : R.string.cancel_ignore});
        popupWindow.showAsDropDown(view, -(int) ScreenUtils.dpToPx(mActivity, 35), (int) ScreenUtils.dpToPx(mActivity, 8));

        popupWindow.setOnPopupItemClickListener(new ItemPopupWindow.OnPopupItemClickListener() {
            @Override
            public void onClick(int position) {
                if (ignore) {
                    // 忽略升级的逻辑
                    DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, Constant.PAGE_MANGER_UPGRADE, 0, null, 0, gameBaseDetail.getId() + "", null, Constant.RETRY_TYPE_MANUAL, "updateIgnore", gameBaseDetail.getPkgName(),""));
                    if (gameBaseDetail.getState() == DownloadState.downInProgress || gameBaseDetail.getState() == DownloadState.waitDownload) {
                        State.updateState(gameBaseDetail, DownloadState.downloadPause);
                    }
                    // 把当前状态保存到之前状态～～
                    State.updatePrevState(gameBaseDetail, gameBaseDetail.getState());
                    State.updateState(gameBaseDetail, InstallState.ignore_upgrade);
                    FileDownloaders.downloadNext();

                    EventBus.getDefault().post(new DownloadUpdateEvent(gameBaseDetail, DownloadUpdateEvent.EV_IGNORE_UPGRADE));
                } else {
                    State.updateState(gameBaseDetail, InstallState.upgrade);
                    State.updatePrevState(gameBaseDetail, InstallState.ignore_upgrade);
                    EventBus.getDefault().post(new DownloadUpdateEvent(gameBaseDetail, DownloadUpdateEvent.EV_CANCLE_IGNORE_UPGRADE));
                    LogUtils.d("ddd", "取消忽略升级了 ");
                    DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, getPageAlias(), 0, null, 0, gameBaseDetail.getId() + "", null, Constant.RETRY_TYPE_MANUAL, "cancleUpdateIgnore", gameBaseDetail.getPkgName(),""));
                }
            }
        });
    }

    private void getData() {
        getUpgradeData();
    }

    private void getUpgradeData() {
        if (mUpgradeGames == null) {
            mUpgradeGames = new ArrayList<>();
        } else {
            mUpgradeGames.clear();
        }

        mUpgradeGames.addAll(FileDownloaders.getAllUpGradeFileInfo());

        List<Item> list = new ArrayList<>();
        if (mUpgradeGames.size() > 0) {
            list.add(new Item(UpgradeAdapter.TYPE_LABEL_UPGRADE, "待升级(共" + mUpgradeGames.size() + "个)"));
            for (GameBaseDetail game : mUpgradeGames) {
                list.add(new Item(UpgradeAdapter.TYPE_GAME, game));
            }
        }

        List<GameBaseDetail> mIgnoreGames = FileDownloaders.getDownFileInfoByState(InstallState.ignore_upgrade);

        if (mIgnoreGames.size() > 0) {
            list.add(new Item(UpgradeAdapter.TYPE_LABEL_IGNORE, "已忽略升级(共" + mIgnoreGames.size() + "个)"));
        }
        mAdapter.setList(list, mIgnoreGames);

        initDownAllShow();

    }


    private void initDownAllShow() {
        int waitUpgradeCount = 0;
        float totalLength = 0;
        for (GameBaseDetail gameBaseDetail : mUpgradeGames) {
            int state = gameBaseDetail.getState();
            if (state != DownloadState.downInProgress && state != DownloadState.waitDownload) {
                waitUpgradeCount++;
                totalLength += gameBaseDetail.getFileTotalLength();
            }
        }
        if (waitUpgradeCount >= COUNT) {
            downAll_Layout.setVisibility(View.VISIBLE);

            String tmp = "您还有" + waitUpgradeCount + "个应用待升级" + " (共" + String.format(Locale.CHINESE,"%.2fMB", totalLength / 1024 / 1024) + ")";
            SpannableString spanText = new SpannableString(tmp);
            spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#aaaaaa")), tmp.lastIndexOf("("), spanText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            spanText.setSpan(new RelativeSizeSpan(0.8f), tmp.lastIndexOf("("), spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            downAll_title.setText(spanText);
        } else {
            downAll_Layout.setVisibility(View.GONE);
        }
    }

    private void upgrade(final GameBaseDetail game, Context context, boolean isOrderWifiDownload, String download_mode) {
        if (game.getState() == DownloadState.downloadComplete || game.getState() == InstallState.install) {
            ApkInstallManger.self().installPkg(game, Constant.MODE_SINGLE, null, false);
        } else {

            if (isOrderWifiDownload) {
                Utils.gameDownByOrderWifi(mActivity, game, getPageAlias(), false, download_mode, Constant.DOWNLOAD_TYPE_NORMAL, null);
                return;
            }

            if (FileDownloaders.couldDownload(mActivity)) {
                Utils.gameDown(mActivity, game, getPageAlias(), false, download_mode, Constant.DOWNLOAD_TYPE_NORMAL, null);
            } else {
                ToastUtils.showToast(mActivity, R.string.network_fail);
            }
        }
    }

    private void initEmptyLayout() {
        if (mAdapter.getList().size() == 0) {
            listView.setVisibility(View.GONE);
            netWorkStateView.showNetworkNoDataLayout();
            netWorkStateView.setNoDataCatSyle(NetWorkStateView.CatStyle.SMILE);
        } else {
            listView.setVisibility(View.VISIBLE);
            netWorkStateView.hideNetworkView();
        }
    }

    @Override
    public void onClick(final View v) {
        onKeyUpgrade(v);
    }

    private void onKeyUpgrade(View v) {
        // 一键升级
        DownloadChecker.getInstance().check(mActivity, new DownloadChecker.Executor() {
            @Override
            public void run() {
                downAll_Layout.setVisibility(View.GONE);
                for (GameBaseDetail game : mUpgradeGames) {
                    if (game.getState() != DownloadState.downInProgress) {
                        upgrade(game, mActivity.getApplicationContext(), false, Constant.MODE_ONEKEY);
                    }
                }
            }

            @Override
            public void reportOrderWifiClick() {

            }
        }, new DownloadChecker.Executor() {
            @Override
            public void run() {
                downAll_Layout.setVisibility(View.VISIBLE);
                for (GameBaseDetail game : mUpgradeGames) {
                    pause(game);
                }
            }

            @Override
            public void reportOrderWifiClick() {

            }
        }, new DownloadChecker.Executor() {
            @Override
            public void run() {
                downAll_Layout.setVisibility(View.GONE);
                for (GameBaseDetail game : mUpgradeGames) {
                    if (game.getState() != DownloadState.downInProgress) {
                        upgrade(game, mActivity.getApplicationContext(), true, Constant.MODE_ONEKEY);
                    }
                }
            }

            @Override
            public void reportOrderWifiClick() {
                for (GameBaseDetail game : mUpgradeGames) {
                    DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, "null", 0, null, 0, "" + game.getId(), null, "manual", "orderWifiDownload", game.getPkgName(),""));
                }

            }
        });

    }


    private void pause(GameBaseDetail game) {
        FileDownloaders.stopDownload(game.getId());
        State.updateState(game, DownloadState.downloadPause);
        FileDownloaders.downloadNext();
    }


    @Override
    public void jump() {
//        ((HomeActivity)getActivity()).subCheck(TabEnum.INDEX);
        ActivityActionUtils.jumpToHomeActivityIndex(mActivity);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                break;
            default:
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
    }

    /**
     * 安装时，apk不存，被执行了重新下载需要页面配合更新
     */
    public void onEventMainThread(ApkNotExistEvent event) {
        if (event.curPage.equals(Constant.PAGE_MANGER_UPGRADE)) {
            getIntent();
            getData();
            initEmptyLayout();
            LogUtils.i(LogTAG.apkIsNotExistTAG, "刷新升级管理页面吧~~~~~~！");
        }
    }

    /**
     * 安装时，apk不存，被执行了重新下载需要页面配合更新
     */
    public void onEventMainThread(RefreshUpgradePageEvent event) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.i(LogTAG.HTAG,"UpgradeFragment"+isVisibleToUser);
    }
}
