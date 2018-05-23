package cn.lt.game.ui.app.management;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.baidu.mobstat.StatService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.base.Item;
import cn.lt.game.download.DownloadChecker;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.ApkNotExistEvent;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.event.InstallEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.install.ApkInstallManger;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.JumpIndexCallBack;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.lib.widget.MessageDialog.RightBtnClickListener;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.model.State;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.notification.LTNotification;
import de.greenrobot.event.EventBus;

public class DownloadManagerFragment extends BaseFragment implements OnClickListener, JumpIndexCallBack, OnScrollListener {
    private static final int DOWNALL = 3;
    private static final int INSTALL_ALL = 3;
    private Activity mActivity;
    private DownloadManagerAdapter mAdapter;
    private View mView = null;
    private NetWorkStateView netWorkStateView;
    private ListView listView;
    //下载任务列表
    private ArrayList<GameBaseDetail> downLoadList = new ArrayList<>();
    //安装任务列表
    private ArrayList<GameBaseDetail> installList = new ArrayList<>();
    private MessageDialog dialog;

    private boolean oneKeyInstallDialogShown;

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_MANGER_DOWNLOAD);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mAdapter = new DownloadManagerAdapter(mActivity);

        EventBus.getDefault().register(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_managemet_layout, container, false);
        findView();
        getData();
        initEmptyLayout();
        if (getUserVisibleHint()) {
            LogUtils.e("www", "onCreateView中  下载管理可见了");
            showDialogAndUpdateView();
        }
        return mView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.e("www", "setUserVisibleHint中  下载管理可见了");
        LogUtils.i(LogTAG.HTAG, "DownloadManagerFragment" + isVisibleToUser);
        showDialogAndUpdateView();
    }

    private void showDialogAndUpdateView() {
        if (getUserVisibleHint() && mView != null) {
            if (!oneKeyInstallDialogShown) {
                oneKeyInstallDialogShown = true;
                showOneKeyInstallDialog();
            }
            updateView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(DownloadUpdateEvent updateEvent) {
        if (updateEvent.ev == DownloadUpdateEvent.EV_DELETE) {
            updateView();
            return;
        }

        for (int i = 0; i < mAdapter.getList().size(); i++) {
            Item item = mAdapter.getList().get(i);
            if (item.data instanceof GameBaseDetail) {
                if (((GameBaseDetail) item.data).getId() == updateEvent.game.getId()) {
                    if (updateEvent.game.getState() == InstallState.install || updateEvent.game.getState() == InstallState.installing) {
                        updateView();
                    } else {
                        updateView(i);
                    }
                    break;
                }
            }
        }

    }


    public void onEventMainThread(InstallEvent installEvent) {
        updateView();
    }

    private void updateView() {
        getData();
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
        boolean isLook = mActivity.getIntent().getBooleanExtra("isLook", false);// 点击的是否“查看下载”

        if (isNotif && tab == LTNotification.TAB_INSTALL) {

            if (downLoadList != null && downLoadList.size() > 0 && FileDownloaders.couldDownload(mActivity) && !isLook) {
                for (GameBaseDetail mGame : downLoadList) {
                    int state = mGame.getState();
                    if (state == DownloadState.downloadFail) {
                        FileDownloaders.download(mActivity, mGame, Constant.MODE_ONEKEY, Constant.DOWNLOAD_TYPE_NORMAL, getPageAlias(), null, false, false, 0);
                    }

                }
            }

            mActivity.getIntent().removeExtra("tab_id");
            mActivity.getIntent().removeExtra("isNotif");
            mActivity.getIntent().removeExtra("isLook");
        }

    }

    private void findView() {
        netWorkStateView = (NetWorkStateView) mView.findViewById(R.id.management_networkStateView);
        netWorkStateView.setNoDataCatSyle(NetWorkStateView.CatStyle.SMILE);
        netWorkStateView.setNoDataLayoutText("啊哈！都下载完了，到外面逛逛呗", "随便逛逛");
        netWorkStateView.setIsfinish(false);
        netWorkStateView.setJumpIndexCallBack(this);

        listView = (ListView) mView.findViewById(R.id.management_listView);
        listView.setAdapter(mAdapter);

        listView.setOnScrollListener(this);
    }

    private void getData() {
        downLoadList.clear();
        installList.clear();
        List<GameBaseDetail> allData = FileDownloaders.getAllDownloadFileInfo();
        for (GameBaseDetail game : allData) {
            int state = game.getState();
            int preState = game.getPrevState();
            if (state == InstallState.ignore_upgrade || preState == InstallState.upgrade || preState == InstallState.upgrade_inProgress || preState == InstallState.signError || game.isCoveredApp) {
                continue;
            }
            if (state == DownloadState.downInProgress || state == DownloadState.downloadFail || state == DownloadState.downloadPause || state == DownloadState.waitDownload || state == InstallState.installFail) {
                downLoadList.add(game);
            } else if (state == InstallState.install || state == InstallState.installing) {
                installList.add(game);
            }
        }

        Collections.sort(downLoadList);


        List<Item> list = new ArrayList<>();
        if (downLoadList.size() > 0) {
            list.add(new Item(DownloadManagerAdapter.TYPE_LABEL, "下载任务"));
        }
        for (GameBaseDetail gameBaseDetail : downLoadList) {
            list.add(new Item(DownloadManagerAdapter.TYPE_GAME, gameBaseDetail));
        }

        if (installList.size() > 0) {
            list.add(new Item(DownloadManagerAdapter.TYPE_LABEL, "已下载"));
        }
        for (GameBaseDetail gameBaseDetail : installList) {
            list.add(new Item(DownloadManagerAdapter.TYPE_GAME, gameBaseDetail));
        }

        mAdapter.setList(list);

        initEmptyLayout();
    }


    private void upgrade(final GameBaseDetail game, boolean isOrderWifiDownload, String download_mode, String download_type) {
        if (game.getState() == DownloadState.downloadComplete || game.getState() == InstallState.install) {
            ApkInstallManger.self().installPkg(game, Constant.MODE_SINGLE, null, false);
        } else {

            // 预约wifi下载
            if (isOrderWifiDownload) {
                Utils.gameDownByOrderWifi(mActivity, game, getPageAlias(), false, download_mode, download_type, null);
                return;
            }

            if (FileDownloaders.couldDownload(mActivity)) {
                Utils.gameDown(mActivity, game, getPageAlias(), false, download_mode, download_type, null);
            } else {
                ToastUtils.showToast(mActivity, R.string.network_fail);
            }
        }
    }

    private void initEmptyLayout() {
        if (mAdapter.getList().size() == 0 && netWorkStateView != null) {
            netWorkStateView.showNetworkNoDataLayout();
            listView.setVisibility(View.GONE);
        } else {
            netWorkStateView.hideNetworkView();
            listView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onPause() {
        StatService.onPause(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        getIntent();
        getData();
        StatService.onResume(this);
        super.onResume();
    }

    @Override
    public void onClick(final View view) {

        // 一键下载
        DownloadChecker.getInstance().check(mActivity, new DownloadChecker.Executor() {
            @Override
            public void run() {
                // 正常下载
                for (GameBaseDetail game : downLoadList) {
                    upgrade(game, false, Constant.MODE_ONEKEY, Constant.DOWNLOAD_TYPE_NORMAL);
                }
                updateView();
            }

            @Override
            public void reportOrderWifiClick() {

            }
        }, new DownloadChecker.Executor() {
            @Override
            public void run() {
                // 暂停下载
                for (GameBaseDetail gameBaseDetail : downLoadList) {
                    pause(gameBaseDetail);
                }
                updateView();
            }

            @Override
            public void reportOrderWifiClick() {

            }
        }, new DownloadChecker.Executor() {
            @Override
            public void run() {
                // 预约wifi下载
                for (GameBaseDetail game : downLoadList) {
                    LogUtils.d("sss", "下载管理，一键下载");
                    upgrade(game, true, Constant.MODE_ONEKEY, Constant.DOWNLOAD_TYPE_NORMAL);
                }
                updateView();
            }

            @Override
            public void reportOrderWifiClick() {
                for (GameBaseDetail game : downLoadList) {
                    DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, "null", 0, null, 0, "" + game.getId(), null, "manual", "orderWifiDownload", game.getPkgName(), ""));
                }
            }
        });

    }

    private void pause(GameBaseDetail game) {
        FileDownloaders.stopDownload(game.getId());
        State.updateState(game, DownloadState.downloadPause);
        FileDownloaders.downloadNext();
    }

    public void showOneKeyInstallDialog() {
        if(MyApplication.castFrom(mActivity).isHadShowOneKeyDialog()) {
            return;
        }
        MyApplication.castFrom(mActivity).setHadShowOneKeyDialog(true);
        if (dialog != null && dialog.isShowing()) {
            return;
        }

        List<GameBaseDetail> allData = FileDownloaders.getAllDownloadFileInfo();

        for (GameBaseDetail game : allData) {

            if (AppUtils.isInstalled(game.getPkgName())) {
                // 过滤掉已安装的
                continue;
            }

            int state = game.getState();
            int preState = game.getPrevState();
            if (preState == InstallState.upgrade || preState == InstallState.ignore_upgrade || preState == InstallState.upgrade_inProgress) {
                continue;
            }

            if ((state == InstallState.install || state == InstallState.installing) && !installList.contains(game)) {
                installList.add(game);
            }
        }

        if (installList.size() >= INSTALL_ALL) {

            if (dialog == null) {
                String message = "列表中有" + installList.size() + "个游戏未安装，是否一键安装";
                String title = "提示安装";
                dialog = new MessageDialog(mActivity, title, message, "取消", "一键安装");
                dialog.setCanceledOnTouchOutside(false);
                dialog.setRightOnClickListener(new RightBtnClickListener() {

                    @Override
                    public void OnClick(View view) {
                        oneKeyInstall();
                    }
                });
            }

            dialog.show();
        }
    }

    private void oneKeyInstall() {
        for (int i = 0; i < installList.size(); i++) {
            ApkInstallManger.self().installPkg(installList.get(i), Constant.MODE_ONEKEY, null, false);
        }
    }

    @Override
    public void jump() {
//        ((HomeActivity) getActivity()).subCheck(TabEnum.INDEX);
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
    }

    /**
     * 安装时，apk不存，被执行了重新下载需要页面配合更新
     */
    public void onEventMainThread(ApkNotExistEvent event) {
        if (event.curPage.equals(Constant.PAGE_MANGER_DOWNLOAD)) {
            getIntent();
            getData();
            LogUtils.i(LogTAG.apkIsNotExistTAG, "刷新下载管理页面吧~~~~~~！");
        }
    }

}
