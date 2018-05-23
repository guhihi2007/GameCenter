package cn.lt.game.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.lt.game.application.MyApplication;
import cn.lt.game.bean.GameInfoBean;
import cn.lt.game.bean.LimitBean;
import cn.lt.game.domain.UIModuleGroup;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.event.RedPointEvent;
import cn.lt.game.event.UpgradeEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.install.ApkInstallManger;
import cn.lt.game.install.InstallState;
import cn.lt.game.install.InstalledApp;
import cn.lt.game.lib.PreferencesUtils;
import cn.lt.game.lib.util.ActivityManager;
import cn.lt.game.lib.util.ConditionsUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.StorageInfo;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.web.WebCallBackToBean;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.model.AppInfo;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.model.State;
import cn.lt.game.model.ToServiceApp;
import cn.lt.game.net.Host;
import cn.lt.game.net.HttpResult;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.management.ManagementActivity;
import cn.lt.game.ui.notification.LTNotificationManager;
import de.greenrobot.event.EventBus;

import static cn.lt.game.download.FileDownloaders.MAX_DOWN_COUNT;

/**
 * Created by chon on 2017/3/9.
 * What? How? Why?
 * 开亮屏自动升级、覆盖安装逻辑处理
 */

public class ScreenMonitorService extends Service {
    public static final String TAG = "ScreenMonitorService";

    public static final String GAME_SYNC = "game_sync";
    private static final String GAME_COVER_SYNC = "game_cover_sync";
    private String currentType = GAME_SYNC;

    private final byte RETRY_TIMES = 3;

    private final long TIME_FREQUENCY = 24 * 60 * 60 * 1000;
    private int startId;

    private AsyncTask<Void, Void, String> mAsyncTask;
    private Handler mHandler;
    // CDN limit apps,other platform apps,cover apps
    private CopyOnWriteArrayList<GameBaseDetail> CDNUpGradeApps, otherUpGradeApps, allCoverApps;
    // real download task Ids
    private CopyOnWriteArrayList<Integer> realTaskIds;

    // request fail times count
    private byte canBeDownloadedFailTimes, heartBeatsFailTimes;

    // own resource to execute download
    private boolean ownResource;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action;
        if (intent != null && !TextUtils.isEmpty(action = intent.getAction())) {
            LogUtils.e(TAG, "action = " + action);
            switch (action) {
                case Intent.ACTION_SCREEN_OFF:
                    this.startId = startId;
                    mHandler = new Handler();
                    // may stop self immediately,execute this at the end.
                    executeAutoUpgrade();
                    break;
                case Intent.ACTION_SCREEN_ON:
                case Intent.ACTION_USER_PRESENT:
                default:
                    stopSelf(startId);
            }
        } else {
            stopSelf(startId);
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void executeAutoUpgrade() {
        // pre conditions
        if (!ConditionsUtil.isMet(this)) {
            stopSelf(startId);
            return;
        }

        if (StorageInfo.getSDCardMemory()[1] / (1024 * 1024) <= 500) {
            LogUtils.e(TAG, "手机内存小于500M，开始走结束流程");
            stopSelf(startId);
            return;
        }

        // honaf
        if (FileDownloaders.getDownloadTaskCount() >= MAX_DOWN_COUNT) {
            LogUtils.e(TAG, "当前下载任务数>=3,什么都不做,静静等待下载任务腾出位置");
            if (mHandler != null) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 5秒间隔比较大,回收之后此处不应该继续执行
                        if (mHandler != null) {
                            executeAutoUpgrade();
                        }
                    }
                }, 5 * 1000);
            }
            return;
        }

        // switch
        boolean isAutoUpgradeOpen = PreferencesUtils.getBoolean(MyApplication.application, Constant.Setting.AUTOUPGRADE, true);
        if (!isAutoUpgradeOpen) {
            LogUtils.e(TAG, "自动升级开关没打开，开始执行自动覆盖流程");
            executeAutoCover();
            return;
        }

        currentType = GAME_SYNC;
        //  客户端自动向服务端请求是否有(待升级游戏 | 待覆盖游戏)，请求频率为每天一次。
        long timeUpgrade = MyApplication.application.getSharedPreferences("setting", Context.MODE_PRIVATE).getLong(GAME_SYNC, 0);
        if (System.currentTimeMillis() - timeUpgrade > TIME_FREQUENCY) {
            LogUtils.i(TAG, "客户端向服务端请求是否有待升级游戏(一天一次)");
            fetchData();
        } else {
            LogUtils.i(TAG, "查询数据库是否有待自动升级游戏");
            List<GameBaseDetail> allUpGradeFileInfoList = FileDownloaders.getAllUpGradeFileInfo();
            if (allUpGradeFileInfoList.size() > 0) {
                CDNUpGradeApps = new CopyOnWriteArrayList<>();
                otherUpGradeApps = new CopyOnWriteArrayList<>();
                for (GameBaseDetail detail : allUpGradeFileInfoList) {
                    if (!detail.canUpgrade()) {
                        // not in the white list
                        LogUtils.e(TAG, "《" + detail.getName() + "》不需要自动升级");
                        continue;
                    }

                    if (detail.isBusinessPackage()) {
                        CDNUpGradeApps.add(detail);
                    } else {
                        otherUpGradeApps.add(detail);
                    }
                }

                // ----------------------------------- log start -----------------------------------
                StringBuilder builder = new StringBuilder("客户端自动升级的商务包(" + CDNUpGradeApps.size() + "):");
                for (GameBaseDetail gradeApp : CDNUpGradeApps) {
                    builder.append(gradeApp.getName()).append(",");
                }
                String pkgs = builder.toString();
                LogUtils.i(TAG, pkgs.substring(0, pkgs.length() - 1));

                builder = new StringBuilder("客户端自动升级的三方包(" + otherUpGradeApps.size() + "):");
                for (GameBaseDetail gradeApp : otherUpGradeApps) {
                    builder.append(gradeApp.getName()).append(",");
                }
                pkgs = builder.toString();
                LogUtils.i(TAG, pkgs.substring(0, pkgs.length() - 1));
                // ----------------------------------- log end -----------------------------------
            }

            if (allUpGradeFileInfoList.size() == 0) {
                // have no app to upgrade,execute cover
                LogUtils.e(TAG, "客户端没有需要自动升级的游戏，开始执行自动覆盖流程");
                executeAutoCover();
            } else {
                if (CDNUpGradeApps.size() == 0) {
                    LogUtils.e(TAG, "没有CDN 升级限流的app，开始执行自动覆盖流程");
                    // have no enough storage to upgrade,execute cover
                    executeAutoCover();
                    return;
                }

                int totalLength = 0;
                for (GameBaseDetail detail : CDNUpGradeApps) {
                    totalLength += detail.getFileTotalLength();
                }

                // CDN 升级限流的app数量为0，或者 判断是否大于500M 设备可用内存>下载包大小总和；
                if (StorageInfo.getSDCardMemory()[1] < totalLength) {
                    LogUtils.e(TAG, "手机内存小于安装包总和，so应用自动升级不启动，开始执行自动覆盖流程");
                    // have no enough storage to upgrade,execute cover
                    executeAutoCover();
                } else {
                    // ask server if can download
                    LogUtils.i(TAG, "客户端有需要自动升级的商务包(" + CDNUpGradeApps.size() + "),询问服务器是否有资源执行下载");
                    askIfCanBeDownloaded();
                }
            }

        }
    }

    private void executeAutoCover() {
        currentType = GAME_COVER_SYNC;
        //  客户端自动向服务端请求是否有(待覆盖游戏)，请求频率为每天一次。
        long timeCover = MyApplication.application.getSharedPreferences("setting", Context.MODE_PRIVATE).getLong(GAME_COVER_SYNC, 0);
        if (System.currentTimeMillis() - timeCover > TIME_FREQUENCY) {
            LogUtils.i(TAG, "客户端向服务端请求是否有待覆盖游戏(一天一次)");
            fetchData();
        } else {
            LogUtils.i(TAG, "查询数据库是否有待自动覆盖游戏");

            // get allCoverApps from database
            List<GameBaseDetail> allCoveredFileInfoList = FileDownloaders.getCoveredDownFileInfo();
            if (allCoveredFileInfoList.size() > 0) {
                allCoverApps = new CopyOnWriteArrayList<>();
                allCoverApps.addAll(allCoveredFileInfoList);
            }

            if (allCoveredFileInfoList.size() == 0) {
                // have no app to cover
                if (otherUpGradeApps != null && otherUpGradeApps.size() > 0) {
                    currentType = GAME_SYNC;
                    LogUtils.e(TAG, "客户端没有需要自动覆盖的游戏有三方升级包(" + otherUpGradeApps.size() + ")，执行三方包升级");
                    notifyServerReleased();
                    executeDownload(otherUpGradeApps);
                } else {
                    LogUtils.e(TAG, "客户端没有需要自动覆盖的游戏且无三方升级包，开始走结束流程");
                    stopSelf(startId);
                }
            } else {

                int totalLength = 0;
                for (GameBaseDetail detail : allCoverApps) {
                    totalLength += detail.getFileTotalLength();
                }

                // 判断是否大于500M 设备可用内存>下载包大小总和；
                if (StorageInfo.getSDCardMemory()[1] < totalLength) {
                    LogUtils.e(TAG, "手机内存小于安装包总和，so应用自动覆盖不启动");
                    // have no enough storage to cover,stop self
                    stopSelf(startId);
                } else {
                    if (ownResource) {
                        LogUtils.i(TAG, "客户端有需要自动覆盖的游戏(" + allCoveredFileInfoList.size() + "),且当前客户端有资源");
                        executeDownload(allCoverApps);
                    } else {
                        LogUtils.i(TAG, "客户端有需要自动覆盖的游戏(" + allCoveredFileInfoList.size() + "),询问服务器是否有资源执行下载");
                        // ask server if can download
                        askIfCanBeDownloaded();
                    }
                }
            }
        }
    }


    /**
     * To sync the newest server data
     */
    @SuppressLint("StaticFieldLeak")
    private void fetchData() {
        mAsyncTask = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                final Gson gson = new Gson();
                // spend a lot time，do it in sub thread.
                LogUtils.i(TAG, "获取客户端已安装游戏包名版本MD5开始时间：" + System.currentTimeMillis());
                List<ToServiceApp> installedPkgNames = InstalledApp.getUserPkgName(MyApplication.application, GAME_COVER_SYNC.equals(currentType));
                return gson.toJson(installedPkgNames);
            }

            @Override
            protected void onPostExecute(String data) {
                LogUtils.i(TAG, "客户端已安装游戏包名版本MD5获取完成时间：" + System.currentTimeMillis());
//                LogUtils.i(TAG, "data：" + data);
                Map<String, String> params = new HashMap<>();

                if (GAME_SYNC.equals(currentType)) {
                    LogUtils.i(TAG, "获取服务器最新的自动升级游戏");

                    params.put("data", data);
                    // fetch newest upgrade apps,and stored in database
                    Net.instance().executePost(Host.HostType.GCENTER_HOST, Uri2.GAME_MANAGER_URI, params, new WebCallBackToObject<UIModuleList>() {
                        @Override
                        public void onFailure(int statusCode, Throwable error) {
                            LogUtils.e(TAG, "获取服务器最新的自动升级游戏失败，退出流程");
                            stopSelf(startId);
                        }

                        @Override
                        protected void handle(UIModuleList info) {
                            // 记录上次调用接口时间
                            MyApplication.application.getSharedPreferences("setting", Context.MODE_PRIVATE).edit().putLong(GAME_SYNC, System.currentTimeMillis()).apply();
                            handleLocalGameSyncResult(info, false);
                            executeAutoUpgrade();
                        }
                    });


                } else if (GAME_COVER_SYNC.equals(currentType)) {
                    LogUtils.i(TAG, "获取服务器最新的可覆盖游戏");

                    params.put("client_install_packages", data);
                    // fetch newest cover apps,and stored in database
                    Net.instance().executePost(Host.HostType.GCENTER_HOST, Uri2.COVER_SYNC, params, new WebCallBackToBean<HttpResult<List<GameInfoBean>>>() {
                        @Override
                        public void onFailure(int statusCode, Throwable error) {
                            LogUtils.e(TAG, "获取服务器最新的可覆盖游戏失败，退出流程:" + error.toString());
                            stopSelf(startId);
                        }

                        @Override
                        protected void handle(HttpResult<List<GameInfoBean>> info) {
                            // 记录上次调用接口时间
                            MyApplication.application.getSharedPreferences("setting", Context.MODE_PRIVATE).edit().putLong(GAME_COVER_SYNC, System.currentTimeMillis()).apply();
                            List<GameInfoBean> infoBeans = info.data;
                            if (infoBeans != null && infoBeans.size() > 0) {
                                List<GameBaseDetail> serverCoveredApps = new ArrayList<>();

                                for (GameInfoBean baseInfo : infoBeans) {
                                    GameDomainBaseDetail gameDomainDetail = new GameDomainBaseDetail(baseInfo);
                                    GameBaseDetail gameBaseDetail = new GameBaseDetail().setGameBaseInfo(gameDomainDetail);

                                    // mark the app can be covered.
                                    gameBaseDetail.isCoveredApp = true;
                                    serverCoveredApps.add(gameBaseDetail);
                                }

                                // stored in database,also delete these not in white list
                                List<GameBaseDetail> cacheCoveredApps = FileDownloaders.getCoveredDownFileInfo();
                                for (GameBaseDetail cacheCoveredApp : cacheCoveredApps) {

                                    boolean removed = true;
                                    for (GameBaseDetail serverCoveredApp : serverCoveredApps) {
                                        if (cacheCoveredApp.getId() == serverCoveredApp.getId()) {
                                            if (cacheCoveredApp.getState() == InstallState.installComplete) {
                                                serverCoveredApp.isCoveredApp = true;
                                            } else {
                                                serverCoveredApp.setDownInfo(cacheCoveredApp);
                                            }

                                            LogUtils.e(TAG, "本地数据库缓存的游戏还在白名单上：" + cacheCoveredApp.getPkgName());
                                            // 数据库缓存的版本比
                                            removed = cacheCoveredApp.getVersionCode() > serverCoveredApp.getVersionCode();
                                            break;
                                        }

                                    }

                                    if (removed) {
                                        LogUtils.e(TAG, "删除不在白名单上的本地数据库缓存：" + cacheCoveredApp.getPkgName());
                                        // report to server
                                        DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, null, -1, "game", -1, String.valueOf(cacheCoveredApp.getId()), null, "MD5_auto", "downDelete", cacheCoveredApp.getPkgName(), ""));

                                        // delete these not in white list
                                        FileDownloaders.remove(cacheCoveredApp.getDownUrl(), true);
                                        LogUtils.e(TAG, "删除数据 1：" + cacheCoveredApp.getPkgName());
                                    }
                                }


                                // stored in database
                                FileDownloaders.update(serverCoveredApps);
                                // 检查是否有在游戏中心外面卸载的游戏，如果有，删除记录
                                FileDownloaders.checkInstalled(1);

                                executeAutoCover();
                            } else {
                                List<GameBaseDetail> cacheCoveredApps = FileDownloaders.getCoveredDownFileInfo();
                                LogUtils.e(TAG, "不在白名单上却还在本地数据库中游戏数：" + cacheCoveredApps.size());
                                for (GameBaseDetail cacheCoveredApp : cacheCoveredApps) {
                                    LogUtils.e(TAG, "删除不在白名单上的本地数据库缓存：" + cacheCoveredApp.getPkgName());
                                    // report to server
                                    DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, null, -1, "game", -1, String.valueOf(cacheCoveredApp.getId()), null, "MD5_auto", "downDelete", cacheCoveredApp.getPkgName(), ""));

                                    FileDownloaders.remove(cacheCoveredApp.getDownUrl(), true);
                                    LogUtils.i(TAG, "删除数据2：" + cacheCoveredApp.getPkgName());
                                }
                                // 检查是否有在游戏中心外面卸载的游戏，如果有，删除记录
                                FileDownloaders.checkInstalled(2);

                                if (otherUpGradeApps == null || otherUpGradeApps.size() == 0) {
                                    LogUtils.e(TAG, "客户端没有需要自动覆盖的游戏，开始走结束流程");
                                    stopSelf(startId);
                                } else {
                                    LogUtils.e(TAG, "客户端没有需要自动覆盖的游戏，有升级的三方包(" + otherUpGradeApps.size() + ")，执行三方包升级，释放下载通道");
                                    notifyServerReleased();
                                    executeDownload(otherUpGradeApps);
                                }
                            }
                        }
                    });

                }

            }
        };

        mAsyncTask.execute();
    }

    /**
     * Ask the server if its can be downloaded
     * if can,do download,and notify serve downloading every M min,do ask after N min otherwise
     * also need to receive task finished broadcast,notify serve while all tasks finished.
     */
    private void askIfCanBeDownloaded() {
        ownResource = true;
        Net.instance().executePost(Host.HostType.CDN_LIMIT_HOST, Uri2.LIMIT_GET_TICKET, new WebCallBackToBean<HttpResult<LimitBean>>() {
            @Override
            protected void handle(HttpResult<LimitBean> info) {
                canBeDownloadedFailTimes = 0;

                if (info.status == 0) {
                    LogUtils.e(TAG, "服务器当前有空闲资源");
                    ownResource = true;

                    // pause other upGrade Apps
                    if (otherUpGradeApps != null && otherUpGradeApps.size() > 0) {
                        for (GameBaseDetail game : otherUpGradeApps) {
                            int state = game.getState();
                            if (state == DownloadState.downInProgress || state == DownloadState.waitDownload) {
                                // 必须要在改变状态前上报，否则会上报成继续
                                DCStat.downloadRequestEvent(game, Constant.AUTO_PAGE, new StatisticsEventData(), false, Constant.MODE_ONEKEY, Constant.DOWNLOAD_TYPE_AUTO_UPDATE, false, 0);
                                LogUtils.i(TAG, "暂停正在下载的任务~~ " + game.getPkgName());
                                // 只有当当前任务正在下载的时候才去暂停
                                FileDownloaders.stopDownload(game.getId());
                                State.updatePrevState(game, InstallState.upgrade);
                                State.updateState(game, DownloadState.downloadPause);
                            }
                        }
                    }

                    // execute download,notify serve downloading every heartbeat min
                    executeDownload(GAME_SYNC.equals(currentType) ? CDNUpGradeApps : allCoverApps);
                } else if (info.status == 1) {
                    ownResource = false;

                    if (otherUpGradeApps != null && otherUpGradeApps.size() > 0) {
                        executeDownload(otherUpGradeApps);
                    }

                    // do ask after delay min
                    LogUtils.e(TAG, "服务器当前没有空闲资源，间隔" + info.data.delay + "s询问服务器是否有空闲资源");
                    if (mHandler != null) {
                        // Network asynchronous request,mHandler may be null when handle invoke.
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                askIfCanBeDownloaded();
                            }
                        }, info.data.delay * 1000);
                    }
                } else {
                    // may hold the resource.
                    LogUtils.e(TAG, "状态不对：" + info.message);
                    ownResource = true;
                    stopSelf(startId);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                // count fail times
                canBeDownloadedFailTimes++;
                LogUtils.e(TAG, "请求服务器是否有空闲资源接口失败" + canBeDownloadedFailTimes + "次: " + statusCode + " --- " + error.toString());
                if (canBeDownloadedFailTimes >= RETRY_TIMES) {
                    LogUtils.e(TAG, "请求服务器是否有空闲资源接口失败" + RETRY_TIMES + "次，开始走结束流程");
                    stopSelf(startId);
                } else {
                    askIfCanBeDownloaded();
                }
            }
        });

    }


    /**
     * execute download,record real download tasks id
     */
    private void executeDownload(List<GameBaseDetail> apps) {
        if (apps == null) {
            stopSelf(startId);
            return;
        }

        // 是否是三方的下载
        boolean isOtherApps = apps == otherUpGradeApps;

        // there apps won't be null and its size must greater than 0.
        for (GameBaseDetail game : apps) {
            boolean isUpgradeApp = isOtherApps || GAME_SYNC.equals(currentType);

            int state = game.getState();
            LogUtils.i(TAG, "state = " + state + ",\tpreState = " + game.getPrevState());
            if (state != DownloadState.downInProgress && state != DownloadState.downloadFail) {
                if (state == InstallState.installing) {
                    // installing.
                    LogUtils.i(TAG, game.getPkgName() + " 安装中...");
                } else if (state == InstallState.installComplete) {
                    // already install completed.
                    LogUtils.i(TAG, game.getPkgName() + " 已经覆盖成功");
                } else if (state == DownloadState.downloadComplete || state == InstallState.install) {
                    LogUtils.i(TAG, game.getPkgName() + " 执行安装...");
                    doInstall(game);
                } else {
                    LogUtils.i(TAG, game.getDownUrl() + " -> 开始下载了：" + game.getId());
                    if (isUpgradeApp) {
                        State.updatePrevState(game, InstallState.upgrade);
                    }

                    if (realTaskIds == null) {
                        realTaskIds = new CopyOnWriteArrayList<>();
                    }
                    // record real download tasks,notify sever while all finished.
                    Utils.gameDown(MyApplication.application, game, Constant.AUTO_PAGE, false, Constant.MODE_SINGLE, isUpgradeApp ? Constant.DOWNLOAD_TYPE_AUTO_UPDATE : Constant.DOWNLOAD_TYPE_AUTO_COVER, null);  //自动升级

                    if (!isOtherApps) {
                        realTaskIds.add(game.getId());
                    }
                }
            }
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (!isOtherApps) {
            if (realTaskIds != null && realTaskIds.size() > 0) {
                // notify server downloading every interval min
                executeHeartBeats(0);
            } else {
                if (GAME_SYNC.equals(currentType)) {
                    LogUtils.e(TAG, "并没有真正需要执行下载的限流升级下载任务，执行自动覆盖流程");
                    if (CDNUpGradeApps != null) {
                        CDNUpGradeApps.clear();
                        CDNUpGradeApps = null;
                    }
                    executeAutoCover();
                } else if (GAME_COVER_SYNC.equals(currentType)) {
                    LogUtils.e(TAG, "并没有真正需要执行下载的覆盖下载任务，开始走结束流程");
                    stopSelf(startId);
                }
            }
        }
    }

    /***
     * 根据安装时间和安装次数限制能够进行静默安装流程
     */
    private void doInstall(GameBaseDetail game) {
        long currentTime = System.currentTimeMillis();
        long lastCurrentTime = DCStat.getInstallTime(game.getPkgName());
        long gapTime = currentTime - lastCurrentTime;
        LogUtils.i(TAG, "当前时间：" + currentTime + "  ,上次记录时间：" + lastCurrentTime + "  ," + "时间差：" + gapTime);
        if (gapTime < TimeUtils.DAY) {
            LogUtils.i(TAG, "小于一天时间，只能安装三次");
            int installCount = DCStat.getInstallCount(game.getPkgName());
//            LogUtils.i(TAG, "已经安装的次数==" + installCount);
            if (installCount <= 2) {//一天内如果安装次数大于3次以后不再执行
                LogUtils.i(TAG, "一天内已经安装的次数小于等于3次,执行安装");
                ApkInstallManger.self().installPkg(game, Constant.MODE_SINGLE, null, true);
            } else {
//                FileDownloaders.remove(game.getDownUrl(), true);
                LogUtils.i(TAG, "一天内已经安装的次数大于3次");
            }
        } else {
            LogUtils.i(TAG, "大于一天时间，执行安装并复位");
            DCStat.resetInstallData(game.getPkgName());
            ApkInstallManger.self().installPkg(game, Constant.MODE_SINGLE, null, true);
        }
    }

    /**
     * notify serve client downloading every interval min
     *
     * @param interval time interval
     */
    private void executeHeartBeats(final int interval) {
        Net.instance().executeGet(Host.HostType.CDN_LIMIT_HOST, Uri2.LIMIT_CHECK_TICKET, new WebCallBackToBean<HttpResult<LimitBean>>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                heartBeatsFailTimes++;
                // After a few minutes of running in the background, the network may be disconnected.
                LogUtils.e(TAG, "请求服务器心跳接口失败" + heartBeatsFailTimes + "次:" + statusCode + " --- " + error.toString());
                if (heartBeatsFailTimes >= RETRY_TIMES) {
                    LogUtils.e(TAG, "请求服务器心跳接口失败" + RETRY_TIMES + "次，开始走结束流程");
                    stopSelf(startId);
                } else {
                    executeHeartBeats(0);
                }
            }

            @Override
            protected void handle(final HttpResult<LimitBean> info) {
                heartBeatsFailTimes = 0;

                if (info.status == 0) {
                    // execute download,tell serve downloading every heartbeat min
                    LogUtils.e(TAG, "服务器当前有空闲资源，执行下载，并间隔" + info.data.heartbeat + "s上报心跳 - " + interval);
                    if (mHandler != null) {
                        // Network asynchronous request,mHandler may be null when handle invoke.
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                executeHeartBeats(info.data.heartbeat * 1000);
                            }
                        }, interval);
                    }
                } else if (info.status == 1) {
                    LogUtils.e(TAG, "发送心跳过程中服务器说没资源了，暂停任务：" + info.message);
                    // pause tasks
                    executePause();
                    askIfCanBeDownloaded();
                }
            }
        });

    }

    /**
     * execute pause,notify serve all tasks finished
     * will be called in onDestroy
     */
    private void executePause() {
        CopyOnWriteArrayList<GameBaseDetail> apps = null;
        boolean isUpgradeApp = false;
        if (CDNUpGradeApps != null) {
            apps = CDNUpGradeApps;
            isUpgradeApp = true;
        } else if (allCoverApps != null) {
            apps = allCoverApps;
        }

        if (apps != null && apps.size() > 0) {
            for (GameBaseDetail game : apps) {
                int state = game.getState();
                if (state == DownloadState.downInProgress || state == DownloadState.waitDownload) {
                    // 必须要在改变状态前上报，否则会上报成继续
                    DCStat.downloadRequestEvent(game, Constant.AUTO_PAGE, new StatisticsEventData(), false, Constant.MODE_ONEKEY, isUpgradeApp ? Constant.DOWNLOAD_TYPE_AUTO_UPDATE : Constant.DOWNLOAD_TYPE_AUTO_COVER, false, 0);
//                    LogUtils.i(TAG, "暂停正在下载的任务~~ " + game.getPkgName());
                    // 只有当当前任务正在下载的时候才去暂停
                    FileDownloaders.stopDownload(game.getId());

                    if (isUpgradeApp) {
                        State.updatePrevState(game, InstallState.upgrade);
                    }
                    State.updateState(game, DownloadState.downloadPause);
                }
            }
        }

    }

    private void executePauseOthers() {
        if (otherUpGradeApps != null && otherUpGradeApps.size() > 0) {
            for (GameBaseDetail game : otherUpGradeApps) {
                int state = game.getState();
                if (state == DownloadState.downInProgress || state == DownloadState.waitDownload) {
                    // 必须要在改变状态前上报，否则会上报成继续
                    DCStat.downloadRequestEvent(game, Constant.AUTO_PAGE, new StatisticsEventData(), false, Constant.MODE_ONEKEY, Constant.DOWNLOAD_TYPE_AUTO_UPDATE, false, 0);
                    LogUtils.i(TAG, "暂停正在下载的任务~~ " + game.getPkgName());
                    // 只有当当前任务正在下载的时候才去暂停
                    FileDownloaders.stopDownload(game.getId());
                    State.updatePrevState(game, InstallState.upgrade);
                    State.updateState(game, DownloadState.downloadPause);
                }
            }
        }
    }

    private long printTime = 0;

    public synchronized void onEventMainThread(DownloadUpdateEvent downloadUpdateEvent) {
        GameBaseDetail detail = downloadUpdateEvent.game;
        int id = detail.getId();

        if (otherUpGradeApps != null && otherUpGradeApps.size() > 0) {
            // 三方包接收状态（无下载通道情况下，可能先下完三方包）
            for (GameBaseDetail app : otherUpGradeApps) {
                if (app.getId() == id) {
                    // download failed or successful,remove it
                    int state = detail.getState();
                    if (state == DownloadState.downloadFail || state == DownloadState.downloadComplete
                            || state == InstallState.install || state == InstallState.installing) {
                        otherUpGradeApps.remove(app);
                        if (state == DownloadState.downloadFail) {
                            LogUtils.e(TAG, "任务失败，id = " + id + ",taskSize = " + realTaskIds.size());
                        } else {
                            LogUtils.e(TAG, "任务完成，id = " + id + ",taskSize = " + realTaskIds.size());
                        }

                        if (otherUpGradeApps.size() == 0) {

                            if ((CDNUpGradeApps == null || CDNUpGradeApps.size() == 0) && (allCoverApps == null || allCoverApps.size() == 0)) {
                                // 商务包和覆盖的任务都已完成
                                LogUtils.e(TAG, "所有任务都已完成，结束");
                                stopSelf(startId);
                            } else {
                                // 没有下载通道，先下载的三方包
                                LogUtils.e(TAG, "三方包已升级完成，继续等待下载通道");
                            }
                        }
                    }
                }
            }
        }

//        Log.w(TAG, "STATE:" + detail.getState());

        if (realTaskIds != null) {
            for (Integer realTaskId : realTaskIds) {
                if (realTaskId == id) {
                    if (System.currentTimeMillis() - printTime > 1000) {
                        printTime = System.currentTimeMillis();
                        LogUtils.i(TAG, "process:" + realTaskId + " - " + detail.getDownLength() + "/" + detail.getFileTotalLength());
                    }

                    // download failed or successful,remove it
                    int state = detail.getState();
                    if (state == DownloadState.downloadFail || state == DownloadState.downloadComplete
                            || state == InstallState.install || state == InstallState.installing) {
                        if (CDNUpGradeApps != null) {
                            for (GameBaseDetail app : CDNUpGradeApps) {
                                if (app.getId() == realTaskId) {
                                    CDNUpGradeApps.remove(app);
                                    break;
                                }
                            }
                        }

                        if (allCoverApps != null) {
                            for (GameBaseDetail app : allCoverApps) {
                                if (app.getId() == realTaskId) {
                                    allCoverApps.remove(app);
                                    break;
                                }
                            }
                        }

                        realTaskIds.remove(realTaskId);
                        if (state == DownloadState.downloadFail) {
                            LogUtils.e(TAG, "任务失败，id = " + realTaskId + ",taskSize = " + realTaskIds.size());
                        } else {
                            LogUtils.e(TAG, "任务完成，id = " + realTaskId + ",taskSize = " + realTaskIds.size());
                        }

                        if (realTaskIds.size() == 0) {
                            // notify server all task finished,and stopSelf
                            if (GAME_SYNC.equals(currentType)) {
                                LogUtils.e(TAG, "限流自动升级任务完结，开始覆盖流程");
                                CDNUpGradeApps.clear();
                                CDNUpGradeApps = null;

                                // There is no need to ask the server if can be downloaded.
                                executeAutoCover();
                            } else if (GAME_COVER_SYNC.equals(currentType)) {
                                if (otherUpGradeApps != null && otherUpGradeApps.size() > 0) {
                                    LogUtils.e(TAG, "自动覆盖任务完结，有三方升级包，释放下载通道资源，继续下载三方包");
                                    notifyServerReleased();
                                    executeDownload(otherUpGradeApps);
                                } else {
                                    LogUtils.e(TAG, "自动覆盖任务完结，且无三方升级包，开始走结束流程");
                                    stopSelf(startId);
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    /**
     * notify server finished all tasks,release resources
     */
    private void notifyServerReleased() {
        if (allCoverApps != null) {
            allCoverApps.clear();
        }
        if (CDNUpGradeApps != null) {
            CDNUpGradeApps.clear();
        }
        ownResource = false;

        Net.instance().executeGet(Host.HostType.CDN_LIMIT_HOST, Uri2.LIMIT_RETURN_TICKET, new WebCallBackToBean<HttpResult<LimitBean>>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.e(TAG, "通知服务器释放资源失败:" + error.toString());
            }

            @Override
            protected void handle(HttpResult<LimitBean> info) {
                LogUtils.i(TAG, "通知服务器释放资源成功");
            }
        });
    }

    @Override
    public void onDestroy() {
        LogUtils.e(TAG, ": onDestroy --- 是否拥有资源：" + ownResource);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if (mAsyncTask != null && !mAsyncTask.isCancelled()) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
        }

        if (realTaskIds != null && realTaskIds.size() > 0) {
            executePause();
        }
        executePauseOthers();

        if (ownResource) {
            notifyServerReleased();
        }

        super.onDestroy();
    }

    @SuppressWarnings("unchecked")
    public static void handleLocalGameSyncResult(UIModuleList uiModuleList, boolean needShowNotification) {
        if (uiModuleList == null) {
            return;
        }

        if (uiModuleList.size() > 0) {
            UIModuleGroup uiModuleGroup = (UIModuleGroup) uiModuleList.get(0);
            List<GameDomainBaseDetail> gameDomainBaseDetailList = uiModuleGroup.getData();
            List<GameBaseDetail> gameBaseDetailList = new ArrayList<>();
            for (GameDomainBaseDetail detail : gameDomainBaseDetailList) {
                gameBaseDetailList.add(new GameBaseDetail().setGameBaseInfo(detail));
            }
            handleGameManageData(gameBaseDetailList, needShowNotification);
        } else {
            //======移除服务器不存在，已经安装 /数据库存在的游戏=======
            List<GameBaseDetail> dbList = FileDownloaders.getAllInstalledDownFileInfo();
            for (GameBaseDetail gameBaseDetail : dbList) {
                FileDownloaders.remove(gameBaseDetail.getDownUrl(), true);
                LogUtils.i(TAG, "删除数据3：" + gameBaseDetail.getPkgName());
            }
            //================================================
            // 检查是否有在游戏中心外面卸载的游戏，如果有，删除记录
            FileDownloaders.checkInstalled(3);
            if (needShowNotification) {
                LTNotificationManager.getinstance().sendAllUpdataNotification();
            }
        }
        // TODO: 2017/8/22  管理页面小红点
        // 同步了升级列表更新到了数据库 还要通知升级界面
        EventBus.getDefault().post(new UpgradeEvent());
    }

    /*这个方法，下版本建议重构*/
    private static void handleGameManageData(List<GameBaseDetail> responseGames, boolean needShowNotification) {
        // 遍历所有响应游戏
        for (GameBaseDetail responseGame : responseGames) {
            // 单个响应游戏
            // 通过本地数据库查询本地游戏信息
            GameBaseDetail localdownFile = FileDownloaders.getDownFileInfoById(responseGame.getId());
//            if(null!=localdownFile){
//                responseGame.setPrevState(localdownFile.getPrevState());//必须加
//            }
            // 删除返回的九游应用信息
            if (responseGame.getPkgName().equalsIgnoreCase("cn.ninegame.gamemanager")) {
                continue;
            }
            // 获取本地应用版本
            AppInfo info = new AppInfo(MyApplication.application, responseGame.getPkgName());
            int localVersionCode = info.getVersionCode();
            if (localdownFile == null) {
                // 处理通过非本游戏中心下载的游戏，使其能够在本游戏中心升级或打开
                if (localVersionCode < responseGame.getVersionCode()) {
                    // 升级
                    responseGame.setState(InstallState.upgrade);
                    responseGame.setPrevState(InstallState.upgrade);
                    responseGame.setVersionCode(localVersionCode);
                } else {
                    // 安装完成
                    responseGame.setState(InstallState.installComplete);
                    responseGame.setFileTotalLength(responseGame.getPkgSize());
                }

            } else {
                // 通过本游戏中心下载的游戏
                // 处理异常情况，这里的游戏是已经安装的游戏
                responseGame.setPrevState(localdownFile.getPrevState());//必须加
                if (localdownFile.getState() == DownloadState.undownload && !localdownFile.isCoveredApp) {
                    localdownFile.setState(InstallState.installComplete);
                }

                if (localdownFile.getVersionCode() > responseGame.getVersionCode()) {
                    // 数据库缓存的版本比服务器高（后台将高版本的下架，上了低版本）
                    FileDownloaders.remove(localdownFile.getDownUrl(), true);
                    LogUtils.e(TAG, "删除统计4" + localdownFile.getPkgName());
                }

                if (localVersionCode >= responseGame.getVersionCode()) {
                    if (localdownFile.isCoveredApp || Constant.FROM_ACTIVITY.equals(localdownFile.getDownloadFrom())) {
                        // 覆盖的游戏状态不变
                        responseGame.setDownInfo(localdownFile);
                    } else {
                        responseGame.setState(InstallState.installComplete);// 安装完成
                        responseGame.setFileTotalLength(responseGame.getPkgSize());
                    }

                } else {
                    if (localdownFile.isCoveredApp) {
                        // 覆盖的游戏中途升级了
                        LogUtils.e(TAG, "删除覆盖中途变升级的包：" + localdownFile.getPkgName());
                        // report to server
                        DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, null, -1, "game", -1, String.valueOf(localdownFile.getId()), null, "MD5_auto", "downDelete", localdownFile.getPkgName(), ""));

                        // delete these now state is upgrade
                        FileDownloaders.remove(localdownFile.getDownUrl(), true);
                        LogUtils.e(TAG, "删除统计5" + localdownFile.getPkgName());
                        responseGame.setState(InstallState.upgrade);
                    } else {

                        if (localdownFile.getState() == InstallState.installComplete || localdownFile.getState() == InstallState.upgrade) {
                            responseGame.setState(InstallState.upgrade);// 升级
                        } else if (localdownFile.getState() == InstallState.ignore_upgrade) {
                            responseGame.setState(InstallState.ignore_upgrade);// 忽略升级
                        } else {
                            responseGame.setDownInfoLess(localdownFile);
                        }
                        // 保存引用打开时间
                        responseGame.setOpenTime(localdownFile.getOpenTime());
                        // 保存预约wifi下载
                        responseGame.setOrderWifiDownload(localdownFile.isOrderWifiDownload());
                    }
                }
                //整个方法他么的就觉得有问题，直接拿远程覆盖本地，巨坑，本地好多存的数据都没了
                responseGame.setDeeplink(localdownFile.getDeeplink());


            }

        }

        //======移除服务器不存在，已经安装 /数据库存在的游戏=======
        List<GameBaseDetail> dbList = FileDownloaders.getAllInstalledDownFileInfo();
        for (GameBaseDetail gameBaseDetail : dbList) {
            boolean remove = true;
            for (GameBaseDetail responseGame : responseGames) {
                if (responseGame.getId() == gameBaseDetail.getId()) {
                    remove = false;
                    break;
                }
            }

            if (remove) {
                if (gameBaseDetail.getState() != InstallState.installComplete) {
                    FileDownloaders.remove(gameBaseDetail.getDownUrl(), true);
                    LogUtils.i(TAG, "删除数据6：" + gameBaseDetail.getPkgName());
                }
            }
        }
        //================================================

        // 把最新游戏信息存入数据库
        FileDownloaders.update(responseGames);//--数据库中所有字段都必须更新
        // 检查是否有在游戏中心外面卸载的游戏，如果有，删除记录
        FileDownloaders.checkInstalled(4);

        boolean showManagerRedPoint = FileDownloaders.getAllUpGradeFileInfo().size() > 0;

        Activity topActivity = ActivityManager.self().topActivity();
        if (showManagerRedPoint) {
            if (topActivity == null || !(topActivity instanceof ManagementActivity)) {
                MyApplication.application.setNewGameUpdate(true);
                EventBus.getDefault().post(new RedPointEvent(true));
            }
        }

        if (needShowNotification) {
            LTNotificationManager.getinstance().sendAllUpdataNotification();
        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
