package cn.lt.game.download;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.lt.game.application.MyApplication;
import cn.lt.game.bean.InstallTemInfo;
import cn.lt.game.db.service.DownFileService;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.event.RedPointEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.install.ApkInstallManger;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.util.ActivityManager;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.PopWidowManageUtil;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.model.State;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.entity.StatisDownloadTempInfoData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.statistics.manger.StatManger;
import cn.lt.game.ui.notification.LTNotificationManager;
import de.greenrobot.event.EventBus;

//非实例化工具类：用于缓存数据库
public class FileDownloaders {

    public static final int MAX_DOWN_COUNT = 3;
    private static HashMap<String, FileDownloader> mDownloaders = new HashMap<>();
    private static Context mContext = MyApplication.application;
    private static DownloadUpdateEvent updateEvent = new DownloadUpdateEvent();

    private FileDownloaders() {
        throw new AssertionError();
    }

    static {
        LogUtils.i("FileDownloaders", "init()");
        List<GameBaseDetail> downFiles = DownFileService.getInstance(mContext).getDownFiles();
        if (downFiles != null) {
            for (GameBaseDetail downFile : downFiles) {
                LogUtils.i("FileDownloaders", downFile.getPkgName()+"==拿到预状态：()"+downFile.getPrevState());

                int state = downFile.getState();
                boolean[] appStatus = AppUtils.appStatus(downFile.getPkgName(), downFile.getVersionCode());

                // 如果应用已经安装完成过
                if (appStatus[0] && !downFile.isCoveredApp) {

                    // 此种情况是在游戏中戏之外完成的安装，但是在游戏中心的数据库的状态还是未完成安装的状态
                    // 此时需要重置状态为安装完成
                    if (!InstallState.isInstalledState(state) && downFile.getPrevState() != InstallState.upgrade && downFile.getPrevState() != InstallState.upgrade_inProgress && !appStatus[1]) {

                        downFile.setPrevState(0);
                        downFile.setState(InstallState.installComplete);
                        // 此种情况是：意外退出的
                        // 游戏中心数据库的状态还是过程中状态，要重置为暂停状态

                    } else if (State.isStateCanPause(state) && downFile.getPrevState() == InstallState.upgrade) {
                        downFile.setState(DownloadState.downloadPause);
                    } else if (downFile.getState() == InstallState.installing) {
                        downFile.setState(InstallState.installComplete);
                    }

                } else {

                    // 下载任务不变，支持自动下载
                    if (downFile.getState() == DownloadState.downInProgress || downFile.getState() == DownloadState.waitDownload) {

                        downFile.setPrevState(downFile.getState());
                        downFile.setState(DownloadState.downloadPause);
                        File file;
                        if (downFile.getDownPath() == null) {
                            downFile.setDownLength(0);
                        } else if (!(file = new File(downFile.getDownPath())).exists()) {
                            downFile.setDownLength(0);
                        } else {
                            downFile.setDownLength(file.length());
                        }

                    } else if (downFile.getPrevState() != InstallState.upgrade && downFile.getPrevState() != InstallState.upgrade_inProgress && (downFile.getState() == InstallState.installComplete || downFile.getState() == InstallState.upgrade)) {

                        // 未安装应用安装完成状态，重置为 未下载，这是一种异常情况
                        downFile.setState(DownloadState.undownload);
                        downFile.setDownLength(0);

                    } else if (downFile.getState() == DownloadState.downloadComplete) {

                        // 下载完成 重置为安装
                        File file;
                        if (downFile.getDownPath() == null) {
                            downFile.setDownLength(0);
                            downFile.setState(DownloadState.downloadPause);
                        } else if (!(file = new File(downFile.getDownPath())).exists()) {
                            downFile.setDownLength(0);
                            downFile.setState(DownloadState.downloadPause);
                        } else if (file.length() == downFile.getFileTotalLength()) {
                            downFile.setState(InstallState.install);
                        } else {
                            downFile.setDownLength(file.length());
                            downFile.setState(DownloadState.downloadPause);
                        }

                    } else if (downFile.getState() == InstallState.installing) {
                        // 安装中 重置 为安装
                        downFile.setState(InstallState.install);
                    }
                }

//                LogUtils.i("FileDownloaders", downFile.getName() + "\t packagename = " + downFile.getPkgName() + "\t state = " + downFile.getState() + " --- preState = " + downFile.getPrevState());
                LogUtils.i("FileDownloaders", downFile.getPkgName()+"==一进来就更新预状态后的状态：()"+downFile.getPrevState());
                FileDownloader loader = new FileDownloader(mContext, downFile, 1);
                loader.update(downFile);
                mDownloaders.put(downFile.getDownUrl(), loader);

            }
        }
    }

    private static FileDownloader createFileDownloader(Context context, GameBaseDetail game) {
        int id = game.getId();
        String downloadUrl = game.getDownUrl();
        if (TextUtils.isEmpty(downloadUrl)) {
            game.setDownUrl(downloadUrl + "?id=" + game.getId());
        }
        FileDownloader loader = getFileDownloaderById(id);
        // 如果没有记录---》创建记录
        if (loader == null) {
            loader = new FileDownloader(context, game, 1);
            loader.save(game);

            mDownloaders.put(game.getDownUrl(), loader);
            LogUtils.e("ScreenMonitorService", "第一次存入数据库~~~- " + game.getName() + "\t" + game.getPkgName() + "cover:" + game.isCoveredApp);
            // 如果有记录，并且下载链接没有改变---》仅更新MD5值（因为有时后台传过来的MD5是错误的）
        } else if (loader.getDownUrl().equalsIgnoreCase(game.getDownUrl())) {
            loader.getDownFileInfo().setMd5(game.getMd5());
            loader.getDownFileInfo().setForumId(game.getForumId());
            loader.getDownFileInfo().setHasGift(game.hasGift());
            loader.getDownFileInfo().setScore(game.getScore());
            loader.getDownFileInfo().setCategory(game.getCategory());
            loader.getDownFileInfo().setCommentCnt(game.getCommentCnt());
            loader.getDownFileInfo().setDescription(game.getDescription());
            loader.getDownFileInfo().setName(game.getName());
            loader.getDownFileInfo().setLogoUrl(game.getLogoUrl());
            loader.getDownFileInfo().setPkgSize(game.getPkgSize());
            loader.getDownFileInfo().setOrderWifiDownload(game.isOrderWifiDownload());
            loader.getDownFileInfo().isCoveredApp = game.isCoveredApp;
            loader.getDownFileInfo().setAccepted(game.isAccepted());
            loader.getDownFileInfo().setDownloadFrom(game.getDownloadFrom());
            loader.getDownFileInfo().setDownloadPoint(game.getDownloadPoint());
            loader.getDownFileInfo().setCanUpgrade(game.canUpgrade());
            loader.getDownFileInfo().setBusinessPackage(game.isBusinessPackage());
            LogUtils.e("ScreenMonitorService", "存入数据库~~~- " + loader.getDownFileInfo().getName() + "\t" + loader.getDownFileInfo().getPkgName() + "\tcover:" + loader.getDownFileInfo().isCoveredApp + "\tcanAutoUpgrade:" + loader.getDownFileInfo().canUpgrade());
            loader.update(game);
        } else {
            loader.getDownFileInfo().setOrderWifiDownload(game.isOrderWifiDownload());
            loader.getDownFileInfo().isCoveredApp = game.isCoveredApp;
            mDownloaders.remove(loader.getDownUrl());
            mDownloaders.put(game.getDownUrl(), loader);
            loader.update(game);
        }
        return loader;
    }


    public static FileDownloader getFileDownloader(String url) {
        return mDownloaders.get(url);
    }

    public static GameBaseDetail getDownFileInfo(String url) {
        FileDownloader loader = getFileDownloader(url);
        return loader == null ? null : loader.getDownFileInfo();
    }

    public static List<GameBaseDetail> getInprogressDownInfo(Context context) {
        return DownFileService.getInstance(context).getInProgressDownFile();
    }


    public static void setState(String url, int state) {
        FileDownloader loader = getFileDownloader(url);
        if (loader != null) {
            loader.setState(state);
        }
    }

    public static void setPrevState(String url, int state) {
        FileDownloader loader = getFileDownloader(url);
        if (loader != null) {
            loader.setPrevState(state);
        }
    }

    public static int getState(String url) {
        FileDownloader loader = getFileDownloader(url);
        return loader == null ? DownloadState.undownload : loader.getState();
    }

    public static void setDownLength(String url, long length) {
        FileDownloader loader = getFileDownloader(url);
        if (loader != null) {
            loader.setDownLength(length);
        }
    }

    public static long getDownLength(String url) {
        FileDownloader loader = getFileDownloader(url);
        return loader == null ? 0 : loader.getDownLength();
    }

    public static void setOpenTime(String packName, long openTime) {
        FileDownloader loader = getFileDownloaderByPkgName(packName);
        if (loader != null) {
            loader.updateOpenTimeByPackName(packName, openTime);
        }
    }

    /**
     * 普通下载  //下载功能里 移动网才报 ; 手动点击、WiFi是在baseOnclick里报 ，  自动下载的 这里WiFi也要报
     *  @param context
     * @param game
     * @param download_mode
     * @param download_type
     * @param mPageName
     * @param eventData
     * @param isFromBaseOnclick
     * @param isFromUpGrade
     * @param pos
     */
    public static  void download(final Context context, final GameBaseDetail game,
                                 String download_mode, String download_type, String mPageName,
                                 StatisticsEventData eventData, boolean isFromBaseOnclick, boolean isFromUpGrade, int pos) {
        LogUtils.d("Erosion", "状态!=下载完成的重置为false>" + game.getState());
        int button = game.getState();
        String downloadUrl = game.getDownUrl();
        LogUtils.d(LogTAG.HTAG, "downloadUrl=>" + downloadUrl);
        if (TextUtils.isEmpty(downloadUrl)) {
            game.setDownUrl(downloadUrl + "?id=" + game.getId());
        }
        LogUtils.d(LogTAG.HTAG, "downloadUrl after=>" + game.getDownUrl());
        // 判断点击升级的时候是否要弹窗提示开启自动升级
        if (context instanceof Activity) {
            PopWidowManageUtil.whetherShowAppAutoUpgradeDialog(context, game);
        } else {
            Activity topActivity = ActivityManager.self().topActivity();
            if (topActivity != null) {
                PopWidowManageUtil.whetherShowAppAutoUpgradeDialog(topActivity, game);
            }
        }
        game.setOrderWifiDownload(false);
        final FileDownloader loader = createFileDownloader(context, game);
        List<GameBaseDetail> downFiles = getDownFileInfoByState(DownloadState.downInProgress);
        if (DownloadState.downloadComplete != game.getState()) {
            LogUtils.d("DownloadService", "状态!=下载完成的重置为false>" + game.getState());
            loader.setmRunnableFinish(false);
        }
        //上报下载请求或继续
        DCStat.downloadRequestEvent(game, mPageName, eventData, false, download_mode, download_type, isFromUpGrade,pos); //上报下载请求或继续
        if (downFiles.size() < MAX_DOWN_COUNT) {
            State.updateState(game, DownloadState.downInProgress);
            State.updatePrevState(game, game.getPrevState());
            goServiceDownload(loader.getDownUrl());
        } else {
            loader.setState(DownloadState.waitDownload);
            game.setState(DownloadState.waitDownload);
            updateEvent.game = loader.getDownFileInfo();
            EventBus.getDefault().post(updateEvent);
        }
        if (button != DownloadState.downloadPause) {
            EventBus.getDefault().post("动态下载按钮");
        }
    }

    /**
     * 重试之后地址不一样的下载情况   //下载功能里 移动网才报 ; 手动点击、WiFi是在baseOnclick里报 ，  自动下载的 这里WiFi也要报
     *
     * @param context       这个只有InstallButtonClick调用，只上报移动网
     * @param oldUrl
     * @param game
     * @param download_mode
     * @param download_type
     */
    public static void downloadRetryDiffUrl(final Context context, final String oldUrl, final GameBaseDetail game, String download_mode, String download_type) {
        game.setOrderWifiDownload(false);
        mDownloaders.remove(oldUrl);
        final FileDownloader loader = createFileDownloader(context, game);
        List<GameBaseDetail> downFiles = getDownFileInfoByState(DownloadState.downInProgress);
        //上报下载请求或继续
        StatisDownloadTempInfoData data = game.getDownloadTempInfo();
        DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, MyApplication.application.mCurrentPage, data.getPos(), data.getPresenType(), data.getSubPos(), data.getmGameID(), data.getmRemark(), download_type, "downRetry", data.getmPkgName(),game.pageId));
//        DCStat.downloadRequestEvent(game, "", NetUtils.getNetworkType(mContext), null, false, download_mode, download_type, false); //重试：baseOnclick不报
        if (downFiles.size() < MAX_DOWN_COUNT) {
            State.updateState(game, DownloadState.downInProgress);
            goServiceDownload(loader.getDownUrl());
        } else {
            loader.setState(DownloadState.waitDownload);
            game.setState(DownloadState.waitDownload);
            updateEvent.game = loader.getDownFileInfo();
            EventBus.getDefault().post(updateEvent);
        }
    }

    /**
     * //等待中时已经上报，此处无需再报
     */
    public static void downloadNext() {
        FileDownloader loader = getFirstDownloaderByState(DownloadState.waitDownload);
        List<GameBaseDetail> downFiles = getDownFileInfoByState(DownloadState.downInProgress);
        if (loader != null && downFiles.size() < MAX_DOWN_COUNT) {
            goServiceDownload(loader.getDownUrl());
        }
    }

    /**
     * 预约wifi下载====最终都会走到这里来
     */
    public static void orderWifiDownload(final Context context, final GameBaseDetail game, String download_mode, String download_type, String mPageName, StatisticsEventData eventData, boolean isFromNetChange) {
        if (!judgeIsOrderWifiDownload(game.getId()) && !isFromNetChange && game.getState() != DownloadState.downloadPause) { //手动点击也只有状态满足时才报请求 juice
            DCStat.downloadRequestEvent(game, mPageName, eventData, true, download_mode, download_type, false, 0);
        }
        LogUtils.i("order", "state====" + game.getState());
        game.setOrderWifiDownload(true);
        final FileDownloader loader = createFileDownloader(context, game);
        LogUtils.i("tiantian","预约下载设置预状态==="+game.getPrevState());
        loader.setPrevState(game.getPrevState());
        loader.setState(DownloadState.downloadPause);
        game.setState(DownloadState.downloadPause);
        updateEvent.game = loader.getDownFileInfo();
        EventBus.getDefault().post(updateEvent);
    }

    /**
     * 判断游戏是否处于预约wifi下载状态
     */
    public static boolean judgeIsOrderWifiDownload(int gameId) {
        GameBaseDetail gameBaseDetail = getDownFileInfoById(gameId);
        return gameBaseDetail != null && gameBaseDetail.isOrderWifiDownload();
    }

    /**
     * 正在下载的任务数量，包含下载中和等待下载
     *
     * @return
     */
    public static int getDownloadTaskCount() {
        return getDownFileInfoByState(DownloadState.downInProgress).size() + getDownFileInfoByState(DownloadState.waitDownload).size();

    }

    public static FileDownloader getFirstDownloaderByState(int state) {
        for (Entry<String, FileDownloader> e : mDownloaders.entrySet()) {
            FileDownloader loader = e.getValue();
            if (loader.getDownFileInfo().getState() == state) {
                return loader;
            }
        }
        return null;
    }

    public static boolean couldDownload(Context context) {
        return NetUtils.isConnected(context);
    }

    /**
     * 是否等待下载
     *
     * @return
     */
    public static boolean shouldWait() {
        List<GameBaseDetail> downFiles = getDownFileInfoByState(DownloadState.downInProgress);
        return downFiles.size() >= MAX_DOWN_COUNT;
    }

    public static void stopDownload(int id) {
        FileDownloader loader = getFileDownloaderById(id);
        if (loader != null) {
            loader.stopDownload();
        }
    }

    /**
     * 自动暂停所有
     *
     * @param savePrevState
     * @param isFromWifiToWifi
     */
    public static void stopAllDownload(boolean savePrevState, boolean isFromWifiToWifi) {
        LogUtils.d("sss", "调用暂停了");
        for (Entry<String, FileDownloader> e : mDownloaders.entrySet()) {
            FileDownloader loader = e.getValue();
            if (loader != null) {
                if (loader.getState() == DownloadState.downInProgress || loader.getState() == DownloadState.waitDownload) {
                    if (savePrevState) {
                        loader.savePrevStateAndPause();
                    } else {
                        if (State.isStateCanPause(loader.getState())) {
                            loader.setState(DownloadState.downloadPause);
                        }
                    }
                    loader.stopDownload();
                }

            }
        }
    }

    /**
     * 自动开始下载所有   //下载功能里 移动网才报 ; 手动点击、WiFi是在baseOnclick里报 ，  自动下载的 这里WiFi也要报
     * 切换wifi时会调用
     *
     * @param
     */
    public static void autoStartDownload() {
        Iterator<Entry<String, FileDownloader>> it = mDownloaders.entrySet().iterator();
        List<GameBaseDetail> downFiles = getDownFileInfoByState(DownloadState.downInProgress);
        int size = downFiles.size();
        boolean isOnekey = false;
        if (size > 1) {
            isOnekey = true;
        }
        while (it.hasNext()) {
            Map.Entry<String, FileDownloader> e = it.next();
            final FileDownloader loader = e.getValue();
            if (loader == null) {
                continue;
            }
            if (loader.getDownFileInfo().isCoveredApp) {
                // 自动覆盖的不自动继续开启下载
                continue;
            }

            int state = loader.getState();
            int prevState = loader.getPrevState();
            if (state == DownloadState.downloadPause) {
                LogUtils.e("sss", "自动开始下载状态：" + loader.getDownFileInfo().getState());
                DCStat.downloadRequestEvent(loader.getDownFileInfo(), Constant.AUTO_PAGE, null, false, isOnekey ? Constant.MODE_ONEKEY : Constant.MODE_SINGLE, Constant.AUTO, false, 0);
                // 标志为不是预约wifi下载
                loader.getDownFileInfo().setOrderWifiDownload(false);

                if (size < MAX_DOWN_COUNT) {
                    goServiceDownload(loader.getDownUrl());
                    size++;
                } else {
                    loader.setState(DownloadState.waitDownload);
                    updateEvent.game = loader.getDownFileInfo();
                    EventBus.getDefault().post(updateEvent);
                }

            } else if (state == DownloadState.downloadFail) {
                //自动重试相关代码暂时关闭,包括数据库创建,包括数据库创建,包括数据库创建,重要的事情说三遍
                //产品说暂时不要,千万不要删,不知道哪天又会要
                /*if (size < MAX_DOWN_COUNT) {
                    final GameBaseDetail mGame = loader.getDownFileInfo();
                    int todayRetryCount = RetryDownloadUtil.getInstance(mContext).getTodayRetryCount(mGame);
                    LogUtils.d(LogTAG.HTAG,"已经自动重试过" + todayRetryCount+"次");
                    if(todayRetryCount >= 4) {
                        LogUtils.d(LogTAG.HTAG,"不再自动重试");
                        return;
                    }
                    size++;
                    Map<String, String> params = new HashMap<>();

                    try {
                        params.put("game_id", mGame.getId()+"");
                        LogUtils.d(LogTAG.HTAG,"第"+(todayRetryCount+1)+"自动重试中");
                        RetryDownloadUtil.getInstance(mContext).updateTodayRetryCount(mGame);
//                        DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, null, 0,
//                                null, 0, mGame.getId() + "", null, Constant.AUTO, "downRetry"));
                        DCStat.downloadRequestEvent(loader.getDownFileInfo(), "", NetUtils.getNetworkType(mContext), null, false, Constant.MODE_SINGLE, Constant.AUTO);
                        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.RETRY_DOWNLOAD, params, new WebCallBackToString() {
                            @Override
                            public void onSuccess(String result) {
                                Gson gson = new Gson();
                                NewUrlDataBean info = gson.fromJson(result, NewUrlDataBean.class);
                                if (info != null) {
                                    NewUrlBean newUrlBean = info.getNewUrlBean();
                                    if (!TextUtils.isEmpty(newUrlBean.getDownload_url())) {
                                        mGame.getGameDomainBase().setMd5(newUrlBean.getGame_md5());
                                        String oldUrl = mGame.getDownUrl();
                                        String newUrl = newUrlBean.getDownload_url();
                                        LogUtils.d(LogTAG.HTAG,"自动重试旧地址"+oldUrl);
                                        LogUtils.d(LogTAG.HTAG,"自动重试新地址"+newUrl);
                                        if (oldUrl.equals(newUrl)) {
                                            //如果地址一样，继续下载
                                            DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, "", -1, null, -1, mGame.getId() + "", null, Constant.AUTO, "downContinue"));
                                            goServiceDownload(mGame.getDownUrl());
                                        } else {
                                            //地址不一样建立新的下载
                                            mGame.setDownUrl(newUrl);
//                                          DownFileService.getInstance(mContext).updateById(mGame);
                                            downloadRetryDiffUrl(mContext, oldUrl, mGame, Constant.AUTO, "retry_request");
                                        }
                                    } else {
                                        LogUtils.e(LogTAG.HTAG, "自动重试请求地址为空");
                                    }
                                }


                            }

                            @Override
                            public void onFailure(int statusCode, Throwable error) {
                                LogUtils.e(LogTAG.HTAG, "自动重试请求地址失败" + error.getMessage());
                            }
                        });

                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }

                } else {
                    loader.setState(DownloadState.waitDownload);
                }*/


            }
            if (prevState == DownloadState.downInProgress) {
                loader.setPrevState(DownloadState.undownload);
            } else if (prevState == InstallState.upgrade_inProgress) {
                loader.setPrevState(InstallState.upgrade);
            }
        }
    }

    public static void stopAllDownload() {
        stopAllDownload(true, false);
    }

    public static int getCanAutoStartCount() {
        int count = 0;
        Iterator<Entry<String, FileDownloader>> it = mDownloaders.entrySet().iterator();
        List<GameBaseDetail> downFiles = getDownFileInfoByState(DownloadState.downInProgress);
        int size = downFiles.size();

        while (it.hasNext()) {

            Map.Entry<String, FileDownloader> e = it.next();
            FileDownloader loader = e.getValue();
            if (loader == null) {
                continue;
            }

            int state = loader.getState();
            int prevState = loader.getPrevState();

            if ((state == DownloadState.downloadPause && prevState == DownloadState.downInProgress) || (state == DownloadState.downloadPause && prevState == DownloadState.waitDownload) || (state == DownloadState.downloadPause && prevState == InstallState.upgrade_inProgress) || state == DownloadState.downloadFail) {
                count++;
            }

        }
        return count;
    }

    public static List<GameBaseDetail> getAllDownloadFileInfo() {
        List<GameBaseDetail> infos = new ArrayList<>();
        for (Entry<String, FileDownloader> e : mDownloaders.entrySet()) {
            FileDownloader loader = e.getValue();
            if (loader != null) {
                infos.add(loader.getDownFileInfo());
            }
        }
        return infos;
    }

    public static List<GameBaseDetail> getAllUpGradeFileInfo() {
        List<GameBaseDetail> infos = new ArrayList<>();
        for (Entry<String, FileDownloader> e : mDownloaders.entrySet()) {

            FileDownloader loader = e.getValue();

            if (loader != null) {
                GameBaseDetail game = loader.getDownFileInfo();

                LogUtils.e("ScreenMonitorService", game.getName() + "\t packagename = " + game.getPkgName() + "\t state = " + game.getState() + " --- preState = " + game.getPrevState());

                if (!AppUtils.isInstalled(game.getPkgName())) {
                    continue;
                }

                int state = game.getState();
                int prevState = game.getPrevState();
                if ((prevState == InstallState.signError || state == InstallState.upgrade || prevState == InstallState.upgrade || prevState == InstallState.upgrade_inProgress) && state != InstallState.installComplete && state != InstallState.ignore_upgrade && !game.isCoveredApp) {
                    infos.add(game);
                }

            }
        }

        return infos;
    }

    public static GameBaseDetail getUpGradeFileInfoByPkg(String pkg) {

        for (Entry<String, FileDownloader> e : mDownloaders.entrySet()) {
            FileDownloader loader = e.getValue();
            if (loader != null) {
                GameBaseDetail game = loader.getDownFileInfo();
                int state = game.getState();
                int prevState = game.getPrevState();
                if ((pkg.equalsIgnoreCase(game.getPkgName())) && (state == InstallState.upgrade || prevState == InstallState.upgrade || prevState == InstallState.upgrade_inProgress) && state != InstallState.ignore_upgrade) {
                    return game;
                }
            }
        }
        return null;
    }


    public static void remove(String url, boolean needDelRepoteData) {
        FileDownloader loader = getFileDownloader(url);
        if (loader != null) {
            loader.stopDownload();
            loader.deleteSaveFile();
            loader.delete();
            if (needDelRepoteData) {
                LogUtils.d("ScreenMonitorService", "needDelRepoteData 删除统计库");
                StatManger.self().removeStaticByGameID(String.valueOf(loader.getDownFileInfo().getId())); //删除统计库
            }
            mDownloaders.remove(url);
        }
    }

    public static void remove(int id) {

        for (Entry<String, FileDownloader> e : mDownloaders.entrySet()) {

            FileDownloader loader = e.getValue();

            if (loader.getDownFileInfo().getId() == id) {
                loader.stopDownload();
                loader.deleteSaveFile();
                loader.delete();
                mDownloaders.remove(e.getKey());
                break;
            }
        }
    }

    /** 移除任务但是无需执行暂停操作（用于非下载中的任务，防止按钮状态改变异常）*/
    public static void removeButNotPause(String url, boolean needDelRepoteData) {
        FileDownloader loader = getFileDownloader(url);
        if (loader != null) {
            loader.deleteSaveFile();
            loader.delete();
            if (needDelRepoteData) {
                LogUtils.d("ScreenMonitorService", "needDelRepoteData 删除统计库");
                StatManger.self().removeStaticByGameID(String.valueOf(loader.getDownFileInfo().getId())); //删除统计库
            }
            mDownloaders.remove(url);
        }
    }

    /***
     * 用户安装守护线程的安装流程(轮询安装专用)
     *
     * @param game
     */
    public static void onInstall(final GameBaseDetail game) {
        try {
            FileDownloader loader = getFileDownloaderByPkgName(game.getPkgName());
            if (loader == null) {
                LogUtils.i("InstallReceiver", "轮询查询下载信息为空");
                return;
            }
            LogUtils.d("InstallReceiver", "轮询查询下载信息不为空，包名=" + loader.getDownFileInfo().getPkgName() + "，当前状态=" + loader.getState());
            ApkInstallManger.self().removeInstallingApp(game.getPkgName());
            if (!InstallState.isInstalledState(loader.getState())) {
                loader.setPrevState(InstallState.installing);
                loader.setState(InstallState.installComplete);
                reportInstallComplete(game.getPkgName(), "轮询", InstallState.installing_listener);//上报安装完成的数据
                LogUtils.d("InstallReceiver", "轮询已改变为打开状态并完成数据上报");
                if (installingList != null) {
                    installingList.put(game.getPkgName(), game);//改变状态并且完成数据上报则存入互斥集合
                }
                EventBus.getDefault().post(new DownloadUpdateEvent(game.setState(InstallState.installComplete)));
                loader.loopered = true;
                // TODO: 2017/8/7  此处小红点可能有问题
                // 显示小红点
//                EventBus.getDefault().post(new RedPointEvent(true));
//                MyApplication.castFrom(mContext).setNewGameInstalled(true);
                // 取消下载小红点
               /* if (getFirstDownloaderByState(DownloadState.downInProgress) == null) {
                    MyApplication.castFrom(mContext).setNewGameDownload(false);
                    EventBus.getDefault().post(new RedPointEvent(false));
                }*/

                boolean existDownloadList = existDownloadManagerList();
                if (!existDownloadList && !MyApplication.castFrom(mContext).getNewGameUpdate()) {
                    MyApplication.castFrom(mContext).setNewGameDownload(false);
                    EventBus.getDefault().post(new RedPointEvent(false));
                }

                // 取消安装通知
                LTNotificationManager.getinstance().cancelNotification(loader.getDownFileInfo().getId());

                // 更改通知状态
                LTNotificationManager.getinstance().deleteGameNotification(loader.getDownFileInfo());

                // 取消升级通知
                if (mContext.getPackageName().equalsIgnoreCase(game.getPkgName())) {
                    LTNotificationManager.getinstance().cancelUpGradeNotification();
                }
                /* 安装完成删除apk */
                deletePkgByPkgNameAfterInstallIfNeed(game.getPkgName());// zql

                // 覆盖安装的删除数据库记录
                if (game.isCoveredApp) {
                    FileDownloaders.remove(game.getDownUrl(), false);
                }
            }else{

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean existDownloadManagerList() {
        List<GameBaseDetail> allData = getAllDownloadFileInfo();
        for (GameBaseDetail game : allData) {
            int state = game.getState();
            int preState = game.getPrevState();
            if (state == InstallState.ignore_upgrade || preState == InstallState.upgrade || preState == InstallState.upgrade_inProgress || preState == InstallState.signError || game.isCoveredApp) {
                continue;
            }
            if (state == DownloadState.downInProgress || state == DownloadState.downloadFail || state == DownloadState.downloadPause || state == DownloadState.waitDownload || state == InstallState.installFail) {
                return true;
            } else if (state == InstallState.install || state == InstallState.installing) {
                return true;
            }
        }
        return false;
    }

    /***
     * 用于安装广播的流程（广播专用,补报）
     *
     * @param packageName
     */
    public static int onInstall(final String packageName) {
        int state;
        try {
            FileDownloader loader = getFileDownloaderByPkgName(packageName);
            if (loader == null) {
                //广播返回的包名是正确的包名，如果用该包名去查询数据库中的错误包名相应信息会为空，导致无法改变按钮状态
                LogUtils.i("InstallReceiver", "因为存在数据库的包名错误，无法查到到该包名对应的数据,设置状态失败，上报数据为第三方");
                return 0;
            } else {
                state = InstallState.installing_listener;
                LogUtils.d("InstallReceiver", "广播查询不为空,状态=" + loader.getState());
                if (!InstallState.isInstalledState(loader.getState())) {
                    loader.setPrevState(InstallState.installing);
                    loader.setState(InstallState.installComplete);
                    LogUtils.d("InstallReceiver", "广播改变为打开状态");
                    EventBus.getDefault().post(new DownloadUpdateEvent(loader.getDownFileInfo().setState(InstallState.installComplete)));
                    /*// 显示小红点
                    EventBus.getDefault().post(new RedPointEvent(true));
                    MyApplication.castFrom(mContext).setNewGameInstalled(true);
                    */
                    // 取消下载小红点
                /*    if (getFirstDownloaderByState(DownloadState.downInProgress) == null) {
                        MyApplication.castFrom(mContext).setNewGameDownload(false);
                        EventBus.getDefault().post(new RedPointEvent(false));
                    }*/

                    boolean existDownloadList = existDownloadManagerList();
                    if (!existDownloadList && !MyApplication.castFrom(mContext).getNewGameUpdate()) {
                        MyApplication.castFrom(mContext).setNewGameDownload(false);
                        EventBus.getDefault().post(new RedPointEvent(false));
                    }

                    // 取消安装通知
                    LTNotificationManager.getinstance().cancelNotification(loader.getDownFileInfo().getId());

                    // 更改通知状态
                    LTNotificationManager.getinstance().deleteGameNotification(loader.getDownFileInfo());

                    // 取消升级通知
                    if (mContext.getPackageName().equalsIgnoreCase(packageName)) {
                        LTNotificationManager.getinstance().cancelUpGradeNotification();
                    }
                /* 安装完成删除apk */
                    deletePkgByPkgNameAfterInstallIfNeed(packageName);// zql
                    // 覆盖安装的删除数据库记录
                    if (loader.getDownFileInfo().isCoveredApp) {
                        FileDownloaders.remove(loader.getDownFileInfo().getDownUrl(), false);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return state;
    }

    /***
     * 升级替换安装/卸载完成
     * @param packageName
     */
    public static void onUninstall(String packageName) {
        final FileDownloader loader = getFileDownloaderByPkgName(packageName);
        if (loader == null) {
            return;
        }
        int state = loader.getState();
        int prevState = loader.getPrevState();
        if (prevState == InstallState.signError) {
            LogUtils.i("InstallReceiver", "重新安装签名冲突的应用");
            ApkInstallManger.self().installPkg(loader.getDownFileInfo(), Constant.MODE_SINGLE, null, false);
        }
        /***
         * 如果状态是升级的任务不删除下载表信息
         */
        LogUtils.i("InstallReceiver", "卸载完成的状态" + prevState + "/" + state);
        /*如果是卸载完成，改变状态为为下载*/
        GameBaseDetail game = loader.getDownFileInfo();
        if (!game.isCoveredApp) {
            game.setState(DownloadState.undownload);
            game.setDownLength(0);
            loader.update(game);
            if (!loader.loopered) {
                LogUtils.i("InstallReceiver", "卸载完成，改变为为下载状态");
                EventBus.getDefault().post(new DownloadUpdateEvent(loader.getDownFileInfo()));

            }
        } else {
            LogUtils.i("InstallReceiver", "覆盖安装卸载完成，不改变下载状态");
        }
        LTNotificationManager.getinstance().deleteGameNotification(loader.getDownFileInfo());
    }

    public static void reportInstallComplete(final String packageName, String source, final int state) {
        LogUtils.i("InstallReceiver", source + "安装完成上报的数据");
        StatisDownloadTempInfoData downloadTempInfoData = StatManger.self().queryDBbyPackageName(packageName);
        InstallTemInfo temInfo = null;
        try {
            temInfo = ApkInstallManger.self().getmInstallMap().get(packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (downloadTempInfoData != null && temInfo != null) {
            LogUtils.e("ScreenMonitorService", "统计数据库有记录");
            if (state == InstallState.installing_listener) {
                DCStat.installSuccess(packageName, downloadTempInfoData);  //普通安装成功（包含升级安装成功）
            } else {
                DCStat.installSuccess(packageName, downloadTempInfoData.setmGameID(temInfo.getGameId()));   //普通安装成功（第三方）
            }
        } else {
            if (temInfo != null && !TextUtils.isEmpty(temInfo.getGameId())) {
                long currentTime = System.currentTimeMillis();
                long lastTime = temInfo.getTimeStamp();
                if (currentTime - lastTime < TimeUtils.MINUTE * 2) {
                    LogUtils.e("ScreenMonitorService", "统计数据库没有记录,内存中有GameID:" + temInfo.getGameId());
                    DCStat.installSuccess(packageName, new StatisDownloadTempInfoData().setmGameID(temInfo.getGameId()));  //普通安装成功（第三方）
                } else {
                    LogUtils.e("ScreenMonitorService", "统计数据库没有记录,内存中有GameID,安装时间大于两分钟" + temInfo.getGameId());
                    DCStat.installSuccess(packageName, null);  //普通安装成功（第三方）
                }
            } else {
                DCStat.installSuccess(packageName, null);  //普通安装成功（第三方）
            }
        }
    }

    public static List<GameBaseDetail> getAllInstalledDownFileInfo() {
        List<GameBaseDetail> infos = new ArrayList<>();
        for (Entry<String, FileDownloader> e : mDownloaders.entrySet()) {

            FileDownloader loader = e.getValue();
            if (loader != null) {
                GameBaseDetail game = loader.getDownFileInfo();

                if (AppUtils.isInstalled(game.getPkgName()) && !game.isCoveredApp) {
                    infos.add(loader.getDownFileInfo());
                }
            }

        }
        return infos;
    }

    public static List<GameBaseDetail> getCoveredDownFileInfo() {
        List<GameBaseDetail> infos = new ArrayList<>();

        for (Entry<String, FileDownloader> e : mDownloaders.entrySet()) {
            FileDownloader loader = e.getValue();
            GameBaseDetail downFileInfo = loader.getDownFileInfo();
            if (downFileInfo.isCoveredApp && AppUtils.isInstalled(downFileInfo.getPkgName())) {//防止在覆盖安装失败以后用户卸载该游戏，熄屏重新执行覆盖流程
                infos.add(downFileInfo);
            }
        }
        return infos;
    }

    public static List<GameBaseDetail> getDownFileInfoByState(int... states) {
        List<GameBaseDetail> infos = new ArrayList<>();
        if (states == null || states.length == 0) {
            return infos;
        }

        for (Entry<String, FileDownloader> e : mDownloaders.entrySet()) {
            FileDownloader loader = e.getValue();

            for (int state : states) {
                if (loader.getDownFileInfo().getState() == state) {
                    infos.add(loader.getDownFileInfo());
                }
            }
        }
        return infos;
    }

    private static FileDownloader getFileDownloaderById(int id) {

        for (Entry<String, FileDownloader> e : mDownloaders.entrySet()) {

            FileDownloader loader = e.getValue();
            if (loader != null && loader.getDownFileInfo() != null && loader.getDownFileInfo().getId() == id) {
                return loader;
            }
        }
        return null;
    }

    private static synchronized FileDownloader getFileDownloaderByPkgName(String pkgName) {
        for (Entry<String, FileDownloader> e : mDownloaders.entrySet()) {
            FileDownloader loader = e.getValue();
            if (pkgName.equalsIgnoreCase(loader.getDownFileInfo().getPkgName())) {
                return loader;
            }
        }
        return null;
    }

    /**
     * h5页面用到(根据包名集合获取游戏集合,由于h5有根据游戏为空作为条件,游戏为空也添加进集合)
     *
     * @param pkgNames
     * @return
     */
    public static List<GameBaseDetail> getGamesByPkgNames(String[] pkgNames) {
        List<GameBaseDetail> gameBaseDetailList = new ArrayList<>();
        for (String pkgName : pkgNames) {
            GameBaseDetail saveGame = null;
            for (Entry<String, FileDownloader> e : mDownloaders.entrySet()) {
                GameBaseDetail game = e.getValue().getDownFileInfo();
                if (pkgName.equalsIgnoreCase(game.getPkgName())) {
                    saveGame = game;
                    break;
                }
            }
            gameBaseDetailList.add(saveGame);
        }
        return gameBaseDetailList;

    }

    public static GameBaseDetail getDownFileInfoById(int id) {
        FileDownloader loader = getFileDownloaderById(id);
        return loader == null ? null : loader.getDownFileInfo();
    }

    public static void update(GameBaseDetail downFile) {
        createFileDownloader(mContext, downFile);
    }

    public static void update(List<GameBaseDetail> upgradeData) {
        for (GameBaseDetail game : upgradeData) {
            update(game);
        }
    }

    public static void deletePkgByPkgNameAfterInstallIfNeed(String pkgName) {

        FileDownloader loader = getFileDownloaderByPkgName(pkgName);
        if (loader == null) {
            return;
        }

        GameBaseDetail downFile = loader.getDownFileInfo();

        if (downFile.getState() == InstallState.installComplete) {
            if (AppUtils.isInstalled(downFile.getPkgName())) {
                if (MyApplication.application.getDeleteApk()) {
                    loader.deleteSaveFile();
                }
            }
        }
    }

    private static void goServiceDownload(String downUrl) {
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.putExtra("type", DownloadService.TYPE_ADD);
        intent.putExtra("url", downUrl);
        mContext.startService(intent);
    }

    /**
     * 校验数据库中的已安装状态的游戏是否有被删除掉的
     *
     * @param i
     */
    public static void checkInstalled(int i) {
//        LogUtils.d("ScreenMonitorService", "F checkInstalled===" + i);
        List<String> removeList = new ArrayList<>();

        for (Map.Entry<String, FileDownloader> entry : mDownloaders.entrySet()) {

            FileDownloader loader = entry.getValue();
            int state = loader.getDownFileInfo().getState();
            int prevState = loader.getDownFileInfo().getPrevState();

            if (prevState == InstallState.upgrade || prevState == InstallState.upgrade_inProgress || state == InstallState.installComplete || state == InstallState.upgrade || state == InstallState.ignore_upgrade) {

                if (!AppUtils.isInstalled(loader.getDownFileInfo().getPkgName())) {
                    removeList.add(loader.getDownUrl());
                }

            }
        }

        for (String downUrl : removeList) {
            FileDownloader loader = mDownloaders.get(downUrl);
            if (loader == null) {
                continue;
            }
            loader.stopDownload();
            loader.deleteSaveFile();
            loader.delete();
            DownFileService.getInstance(mContext).delete(loader.getDownFileInfo().getId());
            if (loader.getDownFileInfo().isCoveredApp) {
                StatManger.self().removeStaticByGameID(String.valueOf(loader.getDownFileInfo().getId())); //删除统计库
//                LogUtils.e("ScreenMonitorService", "F 删除统计表了");
            } else {
//                LogUtils.e("ScreenMonitorService", "F 删除各种表,但没删统计表");
            }
            mDownloaders.remove(downUrl);
        }
//        LogUtils.e("ScreenMonitorService", "check 外围的调用source==" + i);
    }

    /***
     * 安装完成数据上报要用的哈希表
     */
    private static ArrayMap<String, GameBaseDetail> installingList = new ArrayMap<>();

    public static ArrayMap<String, GameBaseDetail> getInstallingList() {
        return installingList;
    }
}
