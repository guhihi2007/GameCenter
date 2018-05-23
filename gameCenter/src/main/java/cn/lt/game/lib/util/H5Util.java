package cn.lt.game.lib.util;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.lt.game.bean.NewUrlBean;
import cn.lt.game.bean.NewUrlDataBean;
import cn.lt.game.db.service.DownFileService;
import cn.lt.game.download.DownloadChecker;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.download.H5DownloadState;
import cn.lt.game.event.RedPointEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.model.State;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsDataProductorImpl;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.notification.LTNotificationManager;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/9/1.
 */

public class H5Util {
    public static final String HOT_DETAIL_URL = "hot_detail_url";
    public static final String HOT_HOST = "HotHost";


    /**
     * 转换原生游戏状态成H5状态（由于原生和H5不统一）
     *
     * @param gameStatus
     * @return
     */
    public static int changeToH5Status(int gameStatus) {
        int status;
        switch (gameStatus) {
            case DownloadState.invalid:
            case DownloadState.undownload:
                status = H5DownloadState.NOT_DOWNLOAD;
                break;
            case DownloadState.downloadComplete:
            case InstallState.install:
                status = H5DownloadState.DOWNLOAD_FINISH;
                break;
            case DownloadState.downInProgress:
                status = H5DownloadState.DOWNLOADING;
                break;
            case DownloadState.downloadPause:
                status = H5DownloadState.STOP;
                break;
            case DownloadState.downloadFail:
            case InstallState.installFail:
                status = H5DownloadState.RETRY;
                break;
            case DownloadState.waitDownload:
                status = H5DownloadState.WAITING;
                break;
            case InstallState.installComplete:
                status = H5DownloadState.INSTALL_FINISH;
                break;
            case InstallState.upgrade:
                status = H5DownloadState.UPGRADE;
                break;
            case InstallState.ignore_upgrade:
                status = H5DownloadState.UPGRADE;
                break;
            case InstallState.installing:
                status = H5DownloadState.INSTALLING;
                break;
            case InstallState.check:
                status = H5DownloadState.CHECK;
                break;
            default:
                status = H5DownloadState.NOT_DOWNLOAD;
                break;
        }
        return status;
    }

    /**
     * 暂停按钮响应
     *
     * @param gameBaseDetail
     */
    public static void dealPauseClick(GameBaseDetail gameBaseDetail) {
        FileDownloaders.stopDownload(gameBaseDetail.getId());
        State.updateState(gameBaseDetail, DownloadState.downloadPause);
        FileDownloaders.downloadNext();
    }

    /**
     * 把数据还原成未下载
     */
    public static void restoreDataAndFlush(GameBaseDetail game) {
        // apk安装包不存在时，删除任务后，需要重新下载
        FileDownloaders.removeButNotPause(game.getDownUrl(), true);
        LTNotificationManager.getinstance().deleteGameNotification(game);
//        EventBus.getDefault().post(new DownloadUpdateEvent(game, DownloadUpdateEvent.EV_DELETE));
        if (game.getPrevState() == InstallState.upgrade) {
            game.setState(InstallState.upgrade);
        } else {
            game.setState(DownloadState.undownload);
        }
        game.mRemark = Constant.TYPE_REDOWNLOAD;
        DCStat.installFailedEvent(game, "single", "normal", "apk is not exists");
    }

    /**
     * h5、热点页面重试下载
     * @param context
     * @param gameId
     * @param mGame
     * @param pos
     */
    public static void retryDownLoad(final Context context, final String gameId, final GameBaseDetail mGame, final int pos, final String page, final String tab_id) {
        DownloadChecker.getInstance().check(context, new DownloadChecker.Executor() {
            @Override
            public void run() {
                if (FileDownloaders.couldDownload(context)) {
                    Map<String, String> params = new HashMap<>();
                    try {
                        params.put("game_id", gameId);
                        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.RETRY_DOWNLOAD, params, new WebCallBackToString() {

                            @Override
                            public void onSuccess(String result) {
                                LogUtils.d(LogTAG.HTAG, "重试请求的服务器新地址=>" + result);
                                Gson gson = new Gson();
                                NewUrlDataBean info = gson.fromJson(result, NewUrlDataBean.class);
                                if (info != null) {
                                    NewUrlBean newUrlBean = info.getNewUrlBean();
                                    if (!TextUtils.isEmpty(newUrlBean.getDownload_url())) {
                                        mGame.getGameDomainBase().setMd5(newUrlBean.getGame_md5());
                                        String oldUrl = mGame.getDownUrl();
                                        LogUtils.d(LogTAG.HTAG, "重试请求后地址对比结果==>" + oldUrl.equals(newUrlBean.getDownload_url()));
                                        LogUtils.d(LogTAG.HTAG, "重试请求后本地地址=>" + mGame.getDownPath());
                                        if (oldUrl.equals(newUrlBean.getDownload_url())) {
                                            //如果地址一样，继续下载
                                            dealDownloadClick(context, false, Constant.MODE_SINGLE, Constant.RETRY_TYPE_MANUAL, page, null, true, false, mGame);
                                            DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, page, 0, null, pos, "" + mGame.getId(), null, Constant.RETRY_TYPE_MANUAL, "downRetry", mGame.getPkgName(), tab_id));
                                        } else {
                                            //地址不一样建立新的下载
                                            mGame.setDownUrl(newUrlBean.getDownload_url());
                                            mGame.setDownLength(0);
                                            File tempFile = new File(mGame.getDownPath() + ".download");
                                            if (tempFile.exists()) {
                                                boolean tempDelResult = tempFile.delete();
                                                LogUtils.d(LogTAG.HTAG, "重试请求后删除文件结果=>temp=" + tempDelResult);
                                            }
                                            DownFileService.getInstance(context).updateById(mGame);
                                            FileDownloaders.downloadRetryDiffUrl(context, oldUrl, mGame, Constant.MODE_RETRY_REQUEST, Constant.RETRY_TYPE_MANUAL);
                                        }
                                    } else {
                                        ToastUtils.showToast(context, "下载地址不存在");
                                        DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, page, 0, null, pos, "" + mGame.getId(), "下载地址不存在", Constant.RETRY_TYPE_MANUAL, "downRetry", mGame.getPkgName(), tab_id));
                                    }
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Throwable error) {
                                ToastUtils.showToast(context, "请求失败");
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
                H5Util.dealPauseClick(mGame);
            }

            @Override
            public void reportOrderWifiClick() {

            }
        }, new DownloadChecker.Executor() {// 预约wifi下载
            @Override
            public void run() {
                dealDownloadClick(context, true, Constant.MODE_SINGLE, Constant.DOWNLOAD_TYPE_NORMAL, page, null, true, false, mGame);
            }

            @Override
            public void reportOrderWifiClick() {
                DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, "null", 0, null, pos, "" + mGame.getId(), null, "manual", "orderWifiDownload", mGame.getPkgName(), tab_id));

            }
        });

    }

    public static void dealDownloadClick(Context mContext, boolean isOrderWifiDownload, String download_mode, String download_type, String mPageName, StatisticsEventData eventData, boolean isFromBaseOnclick, boolean byApkNotExist, GameBaseDetail mGame) {
        if (FileDownloaders.couldDownload(mContext)) {
            LogUtils.i(LogTAG.DirErrorTAG, mGame.getName() + "的downPath = " + mGame.getDownPath());

            if (isOrderWifiDownload) {
                FileDownloaders.orderWifiDownload(mContext, mGame, download_mode, download_type, mPageName, eventData, false);
            } else {
                FileDownloaders.download(mContext, mGame, download_mode, byApkNotExist ? Constant.AUTO : download_type, mPageName, eventData, isFromBaseOnclick, false, 0);
            }

        }
    }

    /***
     * 根据URL截取tabId
     * @param url
     * @return
     */
    public static String getTabId(String url) {
        String tabId = "";
        if (TextUtils.isEmpty(url)) {
            return tabId;
        } else {
            tabId = url.substring(url.lastIndexOf("/") + 1, url.length());
            return tabId;
        }

    }

    /**
     * 错误页面重新加载
     * @param title
     * @return
     */
    public static boolean loadingRetryURL(String title) {
        if (title.equals("网页无法打开") || title.equals("找不到网页")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 内容页面浏览数据上报
     * @param pageName
     * @param tabId
     */
    public static void pageReported(String pageName,String tabId) {
        StatisticsEventData event = null;
        event = StatisticsDataProductorImpl.produceStatisticsData(null, 0, 0, "", pageName, ReportEvent.ACTION_PAGEJUMP, null, tabId, null, "");
        FromPageManager.pageJumpReport(event);
    }

}
