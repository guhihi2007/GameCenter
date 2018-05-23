package cn.lt.game.ui.common.listener;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.bean.NewUrlBean;
import cn.lt.game.bean.NewUrlDataBean;
import cn.lt.game.db.service.DownFileService;
import cn.lt.game.download.DownloadChecker;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.ApkNotExistEvent;
import cn.lt.game.event.DownloadBtnClickedEvent;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.event.RedPointEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.install.ApkInstallManger;
import cn.lt.game.install.InstallState;
import cn.lt.game.install.autoinstaller.AutoInstallerContext;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.OpenAppUtil;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.file.TTGC_DirMgr;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.model.State;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.gamegift.GiftDetailActivity;
import cn.lt.game.ui.installbutton.InstallButton;
import cn.lt.game.ui.notification.LTNotificationManager;
import de.greenrobot.event.EventBus;

/**
 * 普通下载按钮监听
 */
public class InstallButtonClickListener extends BaseOnclickListener {
    private InstallButton mBtn;
    private boolean showRedPoint = true;

    public InstallButtonClickListener(Context context, GameBaseDetail game, InstallButton btn, String pageName) {
        super(context, pageName);
        mContext = context;
        mGame = game;
        mBtn = btn;
    }

    public InstallButtonClickListener(Context context, GameBaseDetail game, InstallButton btn, boolean showRedPoint, String pageName) {
        super(context, pageName);
        mContext = context;
        mGame = game;
        mBtn = btn;
        this.showRedPoint = showRedPoint;
    }


    @Override
    public boolean realOnClick(View v, String mPageName) {
        final int state = mGame.getState();
        // 下载流程；
        if (hasOnclick) {
            if (state == InstallState.install) {
                hasOnclick = false;
                downCtrl(v, mPageName);
                MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hasOnclick = true;
                    }
                }, 2000);
            } else {
                downCtrl(v, mPageName);
                hasOnclick = true;
            }
        }
        return true;
    }

    private void downCtrl(View v, final String mPageName) {
        final StatisticsEventData eventData = (StatisticsEventData) v.getTag(R.id.statistics_data);
        if (eventData != null)
            LogUtils.d("hhh", "InstallButtonListener的点击位置一p1:" + eventData.getPos() + "==p2:" + eventData.getSubPos());
        if (mGame == null) {
            return;
        }
        int state = mGame.getState();
        LogUtils.i("nnn", "state====" + state);
        int percent = mGame.getDownPercent();

        if (mGame.isCoveredApp) {
            state = InstallState.installComplete;
        }

        switch (state) {

            case DownloadState.undownload:
            case DownloadState.downloadPause:
            case InstallState.upgrade:
            case InstallState.ignore_upgrade:
            case InstallState.installFail: {
                startDownload(state, mPageName, eventData, Constant.MODE_SINGLE, percent, true, false);
                break;
            }
//            重试情况
//           （1）手动重试
//            --重试向服务器请求新的下载地址，判断当前下载地址与新的下载地址是否一致，如一致，继续下载；
//              如不一致，根据新的下载地址执行重新下载。
//            --客户端点击重试，但服务端无返回数据，则客户端不做任何处理，仅弹出toast提示：请求失败
            case DownloadState.downloadFail:
                DownloadChecker.getInstance().check(mContext, new DownloadChecker.Executor() {
                    @Override
                    public void run() {
                        if (FileDownloaders.couldDownload(mContext)) {
                            Map<String, String> params = new HashMap<>();
                            try {
                                params.put("game_id", mGame.getId() + "");
                                Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.RETRY_DOWNLOAD, params, new WebCallBackToString() {

                                    @Override
                                    public void onSuccess(String result) {
                                        LogUtils.d(LogTAG.HTAG, "重试请求的服务器新地址=>" + result);
                                        Gson gson = new Gson();
                                        NewUrlDataBean info = gson.fromJson(result, NewUrlDataBean.class);
                                        if (info != null) {
                                            NewUrlBean newUrlBean = info.getNewUrlBean();
//                                            newUrlBean.setDownload_url("http://appcenter.dev.ttigame.cn/uploads/apks/9/9/99014b7ec869b4be9650c5485820b3f1.apksss");
                                            if (!TextUtils.isEmpty(newUrlBean.getDownload_url())) {
                                                mGame.getGameDomainBase().setMd5(newUrlBean.getGame_md5());
                                                mBtn.setViewBy(DownloadState.downInProgress, mGame.getDownPercent());
                                                String oldUrl = mGame.getDownUrl();
                                                LogUtils.d(LogTAG.HTAG, "重试请求后地址对比结果==>" + oldUrl.equals(newUrlBean.getDownload_url()));
                                                LogUtils.d(LogTAG.HTAG, "重试请求后本地地址=>" + mGame.getDownPath());
                                                if (oldUrl.equals(newUrlBean.getDownload_url())) {
                                                    //如果地址一样，继续下载
                                                    dealDownloadClick(mGame.getDownPercent(), false, Constant.MODE_SINGLE, Constant.RETRY_TYPE_MANUAL, mPageName, eventData, true, false);
                                                    eventData.setSrcType("downRetry");
                                                    DCStat.clickEvent(eventData);//放在这里单独报是因为不想导致重复上报失败数据
                                                } else {
                                                    //地址不一样建立新的下载
                                                    mGame.setDownUrl(newUrlBean.getDownload_url());
                                                    mGame.setDownLength(0);
                                                    File tempFile = new File(mGame.getDownPath() + ".download");
                                                    if (tempFile.exists()) {
                                                        boolean tempDelResult = tempFile.delete();
                                                        LogUtils.d(LogTAG.HTAG, "重试请求后删除文件结果=>temp=" + tempDelResult);
                                                    }
                                                    DownFileService.getInstance(mContext).updateById(mGame);
                                                    dealDownloadClickRetryDiffUrl(oldUrl, Constant.MODE_RETRY_REQUEST, Constant.RETRY_TYPE_MANUAL);
                                                }
                                            } else {
                                                ToastUtils.showToast(mContext, "下载地址不存在");
                                                eventData.setRemark("下载地址不存在");
                                                eventData.setSrcType("downRetry");
                                                DCStat.clickEvent(eventData);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Throwable error) {
                                        ToastUtils.showToast(mContext, "请求失败");
                                        LogUtils.e(LogTAG.HTAG, "自动重试请求地址失败" + statusCode + "==" + error.getMessage());
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void reportOrderWifiClick() {

                    }
                }, new DownloadChecker.Executor() {
                    @Override
                    public void run() {
                        dealPauseClick(mGame.getDownPercent());
                    }

                    @Override
                    public void reportOrderWifiClick() {

                    }
                }, new DownloadChecker.Executor() {// 预约wifi下载
                    @Override
                    public void run() {
                        dealDownloadClick(0, true, Constant.MODE_SINGLE, Constant.DOWNLOAD_TYPE_NORMAL, mPageName, eventData, true, false);
                    }

                    @Override
                    public void reportOrderWifiClick() {
                        DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, "null", eventData.getPos(), null, eventData.getSubPos(), "" + mGame.getId(), null, "manual", "orderWifiDownload", mGame.getPkgName(),""));

                    }
                });
                break;
            case DownloadState.waitDownload:
            case DownloadState.downInProgress: {
                // 暂停
                DCStat.downloadRequestEvent(mGame, mPageName, eventData, true, Constant.MODE_SINGLE, Constant.DOWNLOAD_TYPE_NORMAL, false, 0);
                dealPauseClick(percent);
                break;
            }
            case DownloadState.downloadComplete:
                ApkInstallManger.self().installPkg(mGame, Constant.MODE_SINGLE, eventData, false);
                break;
            case InstallState.install: {
                if (AppUtils.apkIsNotExist(mGame.getDownPath())) {
                    ToastUtils.showToast(mContext, mGame.getName() + " 的安装包不存在，准备为您重新下载");
                    mGame.setDownLength(0);
                    percent = 0;
                    TTGC_DirMgr.init();
                    TTGC_DirMgr.makeDirs();
                    startDownload(DownloadState.undownload, mPageName, eventData, Constant.MODE_RETRY_REQUEST, percent, false, true);
                    break;
                }

                ApkInstallManger.self().installPkg(mGame, Constant.MODE_SINGLE, eventData, false);
                mGame = FileDownloaders.getDownFileInfoById(mGame.getId());
                mBtn.setViewBy(mGame.getState(), percent);
                break;
            }
            case InstallState.installComplete: {
                if (mContext instanceof GiftDetailActivity) {
                    ((GiftDetailActivity) mContext).copy();
                }
                if (!OpenAppUtil.openApp(mGame, mContext,getmPageName())) {
                    mBtn.setViewBy(state, percent);
                }
                break;
            }
        }
    }

    private void startDownload(final int state, final String mPageName, final StatisticsEventData eventData, final String downloadMode, final int percent, final boolean isFromBaseOnclick, final boolean byApkNotExist) {
        DownloadChecker.getInstance().check(mContext, new DownloadChecker.Executor() {
            @Override
            public void run() {
                if (byApkNotExist) {
                    restoreDataAndFlush(mPageName);
                }

                if (FileDownloaders.couldDownload(mContext)) {
                    if (state == InstallState.upgrade || state == InstallState.ignore_upgrade) {
                        State.updatePrevState(mGame, InstallState.upgrade);
                    }
                    mBtn.setViewBy(DownloadState.downInProgress, percent);
                }


                if (state == DownloadState.undownload) {
                    MyApplication.castFrom(mContext).setNewGameDownload(true);
                    // 显示小红点
                    EventBus.getDefault().post(new RedPointEvent(true));
                    AutoInstallerContext.getInstance().promptUserOpen((Activity) mContext);
                }

                if (state == InstallState.upgrade) {
                    MyApplication.castFrom(mContext).setNewGameUpdate(true);
                    // 显示小红点
                    EventBus.getDefault().post(new RedPointEvent(true));
                }

                dealDownloadClick(percent, false, downloadMode, Constant.DOWNLOAD_TYPE_NORMAL, mPageName, eventData, isFromBaseOnclick, byApkNotExist);

            }

            @Override
            public void reportOrderWifiClick() {

            }
        }, new DownloadChecker.Executor() {
            @Override
            public void run() {
                if (state != DownloadState.undownload) {
                    dealPauseClick(mGame.getDownPercent());
                }
            }

            @Override
            public void reportOrderWifiClick() {

            }
        }, new DownloadChecker.Executor() {// 预约wifi下载
            @Override
            public void run() {
                if (byApkNotExist) {
                    restoreDataAndFlush(mPageName);
                }

                if (state == InstallState.upgrade) {
                    State.updatePrevState(mGame, InstallState.upgrade);
                }
                mBtn.setViewBy(DownloadState.downloadPause, percent);

                if (state == DownloadState.undownload) {
                    MyApplication.castFrom(mContext).setNewGameDownload(true);
                    // 显示小红点
                    showRedPoint = true;
                    AutoInstallerContext.getInstance().promptUserOpen((Activity) mContext);
                }

                dealDownloadClick(percent, true, Constant.MODE_SINGLE, Constant.DOWNLOAD_TYPE_NORMAL, mPageName, eventData, false, false);
            }

            @Override
            public void reportOrderWifiClick() {
                DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, "null", (eventData == null) ? 0 : eventData.getPos(), null, (eventData == null) ? 0 : eventData.getSubPos(), "" + mGame.getId(), null, "manual", "orderWifiDownload", mGame.getPkgName(),""));

            }
        });
    }

    /**
     * 把数据还原成未下载时并通知刷新所在的页面
     */
    private void restoreDataAndFlush(String mPageName) {
        // apk安装包不存在时，删除任务后，需要重新下载
        FileDownloaders.remove(mGame.getDownUrl(), true);
        LTNotificationManager.getinstance().deleteGameNotification(mGame);
        DCStat.installFailedEvent(mGame, "single", "normal", "apk is not exists");
        EventBus.getDefault().post(new DownloadUpdateEvent(mGame, DownloadUpdateEvent.EV_DELETE));
        if (mGame.getPrevState() == InstallState.upgrade) {
            mGame.setState(InstallState.upgrade);
        } else {
            mGame.setState(DownloadState.undownload);
        }
        mGame.mRemark = Constant.TYPE_REDOWNLOAD;

        // 如果当前是在下载管理页面，需要强制更新页面
        if (mPageName.equals(Constant.PAGE_MANGER_DOWNLOAD)) {
            MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new ApkNotExistEvent(Constant.PAGE_MANGER_DOWNLOAD));
                }
            }, 200);// 这里延时是因为需要在下载代码执行完毕后才刷新页面
        }

    }

    private void dealDownloadClick(int percent, boolean isOrderWifiDownload, String download_mode, String download_type, String mPageName, StatisticsEventData eventData, boolean isFromBaseOnclick, boolean byApkNotExist) {
        if (FileDownloaders.couldDownload(mContext)) {
            LogUtils.i(LogTAG.DirErrorTAG, mGame.getName() + "的downPath = " + mGame.getDownPath());

            if (isOrderWifiDownload) {
                FileDownloaders.orderWifiDownload(mContext, mGame, download_mode, download_type, mPageName, eventData, false);

            } else {
                FileDownloaders.download(mContext, mGame, download_mode, byApkNotExist ? Constant.AUTO : download_type, mPageName, eventData, isFromBaseOnclick, false, 0);
            }
            mBtn.setViewBy(mGame.getState(), percent);

            if (showRedPoint) {
                EventBus.getDefault().post(new RedPointEvent(true));
            }

            // 发送按钮被点击的事件（有需要的业务地方可进行接收）
            EventBus.getDefault().post(new DownloadBtnClickedEvent());
        }
    }

    private void dealDownloadClickRetryDiffUrl(String oldUrl, String download_mode, String download_type) {
        if (FileDownloaders.couldDownload(mContext)) {
            FileDownloaders.downloadRetryDiffUrl(mContext, oldUrl, mGame, download_mode, download_type);
            mBtn.setViewBy(mGame.getState(), 0);
            if (showRedPoint) {
                EventBus.getDefault().post(new RedPointEvent(true));
            }
        }
    }

    private void dealPauseClick(int percent) {
        mBtn.setViewBy(DownloadState.downloadPause, percent);
        State.updateState(mGame, DownloadState.downloadPause);
        LogUtils.i("DownloadService", "InstallButtonClickListener暂停后的状态==" + mGame.getState());
        FileDownloaders.stopDownload(mGame.getId());
        FileDownloaders.downloadNext();

        // 发送按钮被点击的事件（有需要的业务地方可进行接收）
        EventBus.getDefault().post(new DownloadBtnClickedEvent());
    }

}
