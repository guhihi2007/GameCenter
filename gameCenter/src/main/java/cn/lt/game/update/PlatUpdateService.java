package cn.lt.game.update;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

import cn.lt.game.application.MyApplication;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.install.autoinstaller.AutoInstallerContext;
import cn.lt.game.install.system.OnInstalledPackaged;
import cn.lt.game.install.system.SystemInstaller;
import cn.lt.game.lib.ShellUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.PackageUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.file.FileUtil;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.util.threadpool.LTAsyncTask;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.sidebar.UpdateInfo;
import cn.lt.game.ui.notification.LTNotificationManager;
import de.greenrobot.event.EventBus;

/**
 * 升级服务
 *
 * @author dxx
 */
public class PlatUpdateService extends Service {

    public static final String TAG = LogTAG.HTAG;

    private PlatUpdateManager mUpdateManager;

    private PlatDownloadManager mDownloadManager;

    private String mDownloadPath;

    //是否正在下载
    private boolean isLoading;

    private Context mContext;

    //由用户主动触发安装行为
//    private boolean isInstallAfterDownloadCompleted;

    private PlatUpdateInfo mUpdateInfo;

    private Handler mHandler;

    //是否通知栏点击或者主动点击弹框升级进来
    private boolean isFromNotification;

    private boolean isPush;

    //是否通知栏点击重新下载
    private boolean isRetry;

    //是否已经告知给通知栏下载完成了
    private boolean isNotified;
    //通知栏和用户主动点击下载回调
    private RequestCallBack<File> mCallback = new RequestCallBack<File>() {

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            isLoading = true;
            if (total == 0) {
                return;
            }
            final Progress p = new Progress();
            p.current = current;
            p.total = total;
            if (isFromNotification) {
                LTNotificationManager.getinstance().UpGradeNotification(total, current, (int) (current * 100 / total), mUpdateInfo.getmUpgradeVersion());
                if (current == total) {
                    isNotified = true;
                    LTNotificationManager.getinstance().UpGradeNotification(100, 100, 100, mUpdateInfo.getmUpgradeVersion());
                }
                EventBus.getDefault().post(p);
            }

        }

        @Override
        public void onSuccess(ResponseInfo<File> responseInfo) {
            isLoading = false;
            Log.e(LogTAG.HTAG, "mCallback=>onSuccess=>客户端下载完成");
            PlatUpdateManager.savePlatOldVersion(mContext, Constant.versionName);
            if (PlatUpdateManager.isDowloaded(mContext)) {
                DCStat.platUpdateEvent(ReportEvent.ACTION_PLATUPDATEDOWNLOADED, PlatUpdateManager.getPlatUpdateMode(mContext), null, PlatUpdateManager.getPlatDownloadType(mContext), "", Constant.versionName, PlatUpdateManager.getTargetVersionCode(mContext), PlatUpdateManager.getPlatDownloadAction(mContext));
                if (isFromNotification && !isNotified) {
                    isNotified = true;
                    LTNotificationManager.getinstance().UpGradeNotification(100, 100, 100, mUpdateInfo.getmUpgradeVersion());
                }
                PlatUpdateManager.saveDownloaded(mContext, true);
                new LTAsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        installApk(isFromNotification);
                        return null;
                    }
                }.execute();
            } else {
                DCStat.platUpdateEvent(ReportEvent.ACTION_PLATUPDATEDOWNLOADFAILED, PlatUpdateManager.getPlatUpdateMode(mContext), "md5不一致", PlatUpdateManager.getPlatDownloadType(mContext), "", Constant.versionName, PlatUpdateManager.getTargetVersionCode(mContext), PlatUpdateManager.getPlatDownloadAction(mContext));
                FileUtil.delFile(new File(mDownloadPath));
                if (isFromNotification) {
                    errorNotification();
                    EventBus.getDefault().post("md5不一致");
                }
                stopMe();
            }

        }

        @Override
        public void onFailure(HttpException error, String msg) {
            LogUtils.i(LogTAG.HTAG, "msg=>" + msg + ";error=>" + error.getMessage());
            isLoading = false;

            DCStat.platUpdateEvent(ReportEvent.ACTION_PLATUPDATEDOWNLOADFAILED, PlatUpdateManager.getPlatUpdateMode(mContext), msg, PlatUpdateManager.getPlatDownloadType(mContext), "", Constant.versionName, PlatUpdateManager.getTargetVersionCode(mContext), PlatUpdateManager.getPlatDownloadAction(mContext));
            if (isFromNotification) {
                errorNotification();
                EventBus.getDefault().post(msg);
            }
            try {
                String errorMessage = error.getMessage();
                if (errorMessage != null) {
                    if (errorMessage.contains("maybe the file has downloaded completely")) {
                        FileUtil.delFile(new File(mDownloadPath));
                    } else if (errorMessage.contains("No such file or directory")) {
                        boolean result = PlatUpdatePathManger.createFile(mDownloadPath);
                        LogUtils.i(LogTAG.HTAG, "onfailure=createNewFile=>" + result);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            stopMe();
        }
    };

    @Override
    public void onCreate() {
        LogUtils.i(LogTAG.HTAG, "plat=>onCreate");
        super.onCreate();
        MyApplication.application.addServices(this);
        mContext = this;
        initDownManager();
        mHandler = new Handler();
    }
    private VersionCheckManger.VersionCheckCallback mVersionCheckCallBack;
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        LogUtils.i("honaf==>plat", "onStartCommand");
        mVersionCheckCallBack = new VersionCheckManger.VersionCheckCallback() {
            @Override
            public void callback(Result result) {
                switch (result) {
                    case have:
                        dealCommand(intent);
                        break;
                    default:
                        // 判断是否有在下载，无则删除文件；
                        Log.i(TAG, "onStartCommand=>case none......");
                        stopMe();
                        break;
                }
                mVersionCheckCallBack = null;
            }
        };
        VersionCheckManger.getInstance().checkVerison(mVersionCheckCallBack, VersionCheckManger.MODE_SELF);

        return super.onStartCommand(intent, flags, startId);
    }

    private void dealCommand(Intent intent) {
        PlatUpdateManager.savePlatOldVersion(mContext, Constant.versionName);
        if (intent != null) {

            final String action = intent.getStringExtra(PlatUpdateAction.ACTION);
            isRetry = intent.getBooleanExtra(Constant.RETRY_FLAG, false);
            String pushId = intent.getStringExtra("pushId");
            boolean isFromWakeUp = intent.getBooleanExtra("isFromWakeUp", false);

            if (PlatUpdateAction.ACTION_NOTIFICATION.equals(action) || PlatUpdateAction.ACTION_DIALOG_CONFIRM.equals(action)) {
                isFromNotification = true;
                if (PlatUpdateAction.ACTION_NOTIFICATION.equals(action)) {
                    PlatUpdateManager.savePlatUpdateMode(mContext, PlatUpdateMode.push);

                    String pushType;
                    if (isRetry) {
                        pushType = "CLIENT";
                    } else {
                        pushType = isFromWakeUp ? "WAKE_UP" : Constant.PAGE_GE_TUI;
                    }

                    DCStat.pushEvent(pushId, "", "platUpgrade", "clicked", pushType, "", "");
                }
                //} else if (PlatUpdateAction.ACTION_NORMAL.equals(action) || PlatUpdateAction.ACTION_DIALOG_CANCEL.equals(action)) {
            }
            isPush = intent.getBooleanExtra("isPush", false);
            if (isPush) {//通知触发的下载请求在此处统计，其他统计在dialog提示框点击等处理；
                intent.removeExtra("isPush");
            }
        }

        new LTAsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                checkUpdate(true, isRetry);
                return null;
            }
        }.execute();
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.i("honaf==>plat", "onBind");
        LogUtils.i(LogTAG.HTAG, "onBind====isFromNotification=>" + isFromNotification);
        final RemoteCallbackList<IPlatUpdateCallback> mCallBackList = new RemoteCallbackList<>();
        return new IPlatUpdateService.Stub() {

            @Override
            public void checkVersion() throws RemoteException {
                new LTAsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        checkUpdate(false, isRetry);
                        return null;
                    }
                }.execute();
            }

            @Override
            public void requestNetWork() throws RemoteException {
                VersionCheckManger.VersionCheckCallback mVersionCheckCallBack = new VersionCheckManger.VersionCheckCallback() {
                    @Override
                    public void callback(Result result) {
                        switch (result) {
                            case have:
                                mUpdateManager.init(PlatUpdateService.this);
                                final int N = mCallBackList.beginBroadcast();
                                for (int i = 0; i < N; i++) {
                                    try {
                                        mCallBackList.getBroadcastItem(i).callback(true);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                                mCallBackList.finishBroadcast();
                                break;
                            case none:
                                break;
                            case fail:
                                break;
                        }
                    }
                };
                VersionCheckManger.getInstance().checkVerison(mVersionCheckCallBack, VersionCheckManger.MODE_AUTO);
            }

            @Override
            public void registerCallback(IPlatUpdateCallback callback) throws RemoteException {
                if (callback != null) {
                    mCallBackList.register(callback);
                }
            }

            @Override
            public void removeCallback(IPlatUpdateCallback callback) throws RemoteException {
                if (callback != null) {
                    mCallBackList.unregister(callback);
                }
            }
        };
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.application.removeService(this);
    }

    private void stopMe() {
        Log.e(LogTAG.HTAG, "the service is stopped...");
        mVersionCheckCallBack = null;
        mDownloadManager.unRegisterAllCallback();
        MyApplication.castFrom(this).setmDownloadManger(null);
        stopSelf();
    }


    public void initDownManager() {
        mDownloadPath = PlatUpdatePathManger.getDownloadPath(this);
        mUpdateManager = new PlatUpdateManager(this);
        mDownloadManager = new PlatDownloadManager(this);
        MyApplication.castFrom(this).setmDownloadManger(mDownloadManager);
        mUpdateInfo = mUpdateManager.getUpdateInfo();
    }

    /**
     * 检查是否需要升级；包含两种情况：
     * <p/>
     * 1、后台无升级提示；无升级信息提示则检查文件是否存在并删除；
     * <p/>
     * 2、有升级信息 ：
     * <p/>
     * a,已经下载完成，需要判断是否为系统应用或者有无root权限，1）、如果有权限则静默安装{安装失败则检查是否要弹框}；2）、无权限
     * 检查是否需要弹框提示； 无权限则检查是否需要弹框提示；
     */

    private void checkUpdate(final boolean isFromClick, boolean isRetry) {
        mUpdateInfo = mUpdateManager.getUpdateInfo();
        LogUtils.i(LogTAG.HTAG, "plat=>checkUpdate");
        if (isFromClick) {
            PlatUpdateManager.savePlatDownloadType(mContext, PlatUpdateDownloadType.manual);
        } else {
            PlatUpdateManager.savePlatDownloadType(mContext, PlatUpdateDownloadType.auto);
        }
        if (TextUtils.isEmpty(PlatUpdateManager.getTargetVersionCode(mContext)) && !isLoading) {// 不需要升级；//
            // 判断是否有在下载，无则删除文件；
            Log.i(TAG, "checkUpdate=>do not need update......");
            stopMe();
        } else { // 可以升级

            // 已经下载完成
            if (PlatUpdateManager.isDowloaded(mContext)) {
                if (isFromNotification && !isNotified) {
                    isNotified = true;
                    LTNotificationManager.getinstance().UpGradeNotification(100, 100, 100, mUpdateInfo.getmUpgradeVersion());
                }
                Log.i(TAG, "checkUpdate=>already downloaded,install the game now");

                // 安装；
                installApk(isFromClick);

            } else { // 下载中或者待下载；
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (isFromClick) {
                            if (isLoading) { // 正在下载中
                                ToastUtils.showToast(mContext, "升级包正在下载，请稍候...");
                            } else {
                                ToastUtils.showToast(mContext, "正在下载...");
                            }
                        }
                    }
                });
                if (!isLoading) {

                    if (PlatUpdateManager.getDownloaded(mContext)) {
                        DCStat.platUpdateEvent(ReportEvent.ACTION_PLATUPDATEINSTALLFAILED, PlatUpdateManager.getPlatUpdateMode(mContext), "安装包被删除", PlatUpdateManager.getPlatDownloadType(mContext), "", Constant.versionName, PlatUpdateManager.getTargetVersionCode(mContext), PlatUpdateManager.getPlatDownloadAction(mContext));
                        PlatUpdateManager.saveDownloaded(mContext, false);
                        isRetry = true;
                    }
//                    }
                    startDownload(isRetry);
                }

            }
        }
    }


    /**
     * 启动下载；
     *
     * @param isRetry
     */
    public void startDownload(boolean isRetry) {
        LogUtils.i("honaf==>plat", "startDownload");
        if (!isFromNotification && !NetUtils.isWifiNet(mContext)) {
            LogUtils.i(LogTAG.HTAG, "服务启动时不是wifi不启动下载......");
            return;
        }
        if (NetUtils.netWorkConnection(mContext)) {
            isLoading = true;
//            String url = mUpdateManager.getUpdateInfo().getDownloadUrl();
            String url = UpdateInfo.getDownload_link();
            Log.i(TAG, "更新包地址==" + url + "开始下载");
            //普通平台下载请求
            if (isRetry) {
                PlatUpdateManager.savePlatDownloadAction(mContext, PlatDownloadAction.retry_request);
            } else {
                PlatUpdateManager.savePlatDownloadAction(mContext, PlatDownloadAction.first);
            }
            DCStat.platUpdateEvent(ReportEvent.ACTION_PLATUPDATEREQUEST, PlatUpdateManager.getPlatUpdateMode(mContext), null, PlatUpdateManager.getPlatDownloadType(mContext), "", Constant.versionName, PlatUpdateManager.getTargetVersionCode(mContext), PlatUpdateManager.getPlatDownloadAction(mContext));
            mDownloadManager.addNewDownload(url, "update.apk.abyz", mDownloadPath, true, false, mCallback);
        } else {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    if (isFromNotification) {
                        errorNotification();
                        ToastUtils.showToast(mContext, "网络异常，请检查网络");
                    }
                    stopMe();
                }
            });
        }

    }

    private void errorNotification() {
        LTNotificationManager.getinstance().UpGradeNotification(Constant.TITLE_ERROR, Constant.CONTENT_ERROR);
    }


    /***
     * 1.如果是系统应用且手机处于锁屏状态，开始静默安装
     * 3.如果开启自动装服务，且没有其他应用在前台(除游戏中心)，开始静默安装
     * 2.Root装
     * 4.如果无系统权限  执行install()方法安装
     */
    private void installApk(boolean isClick) {
        if (AutoInstallerContext.getInstance().getAccessibilityStatus() == 1) {
            PlatUpdateManager.savePlatInstallType(mContext, PlatInstallType.auto);
        } else {
            PlatUpdateManager.savePlatInstallType(mContext, PlatInstallType.normal);
        }

        LogUtils.i("honaf==>plat", "installApk");
        if (!PlatUpdateManager.isDowloaded(mContext)) {
            DCStat.platUpdateEvent(ReportEvent.ACTION_PLATUPDATEINSTALLED, PlatUpdateManager.getPlatUpdateMode(this), "MD5值不一致", PlatUpdateManager.getPlatDownloadType(this), "", Constant.versionName, PlatUpdateManager.getTargetVersionCode(mContext), PlatUpdateManager.getPlatDownloadAction(this));
            //下载完成又有问题的包没必要保存(否则影响到断点续传)
            FileUtil.delFile(new File(mDownloadPath));
            return;
        }
        modifyPermission();
        if (isFromNotification) {
            PlatUpdateManager.savePlatDownloadType(mContext, PlatUpdateDownloadType.manual);
        } else {
            PlatUpdateManager.savePlatDownloadType(mContext, PlatUpdateDownloadType.auto);
        }
        PlatUpdateManager.savePlatOldVersion(mContext, Constant.versionName);
        if (isClick) {
            Log.e(TAG, "点击马上触发开始正常安装");
            PackageUtils.installNormal(mContext, mDownloadPath, null, true, mUpdateInfo, Constant.MODE_SINGLE, null);
            isFromNotification = false;
        } else if (PlatUpdateManager.isForeground(mContext)) {
            Log.e(TAG, "在前台运行，不允许静默操作");
            if (isFromNotification) {
                Log.e(TAG, "开始正常安装");
                PackageUtils.installNormal(mContext, mDownloadPath, null, true, mUpdateInfo, Constant.MODE_SINGLE, null);
            }
        } else {
            Log.i(TAG, "在后台运行，允许静默升级");
            //如果有系统权限并且已锁屏
            if (PackageUtils.isSystemApplication(mContext)) {
                Log.e(TAG, "有系统权限，开始静默安装");
                try {
                    PlatUpdateManager.savePlatInstallType(mContext, PlatInstallType.system);
                    MyApplication.getMainThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            DCStat.platUpdateEvent(ReportEvent.ACTION_PLATUPDATEINSTALLREQUEST, PlatUpdateManager.getPlatUpdateMode(mContext), null, PlatUpdateManager.getPlatDownloadType(mContext), PlatUpdateManager.getPlatInstallType(mContext), Constant.versionName, mUpdateInfo.getmUpgradeVersion(), PlatUpdateManager.getPlatDownloadAction(mContext));  //系统安装请求
                        }
                    });

                    SystemInstaller.getInstance(mContext).install(mDownloadPath, new OnInstalledPackaged() {
                        @Override
                        public void packageInstalled(String packageName, int returnCode) {
                            Log.e(TAG, "系统权限安装返回码=" + returnCode);
                        }
                    });


                } catch (Exception e) {
                    DCStat.platUpdateEvent(ReportEvent.ACTION_PLATUPDATEINSTALLFAILED, PlatUpdateManager.getPlatUpdateMode(this), e.getMessage(), PlatUpdateManager.getPlatDownloadType(this), "system", Constant.versionName, mUpdateInfo.getmUpgradeVersion(), PlatUpdateManager.getPlatDownloadAction(this));
                    e.printStackTrace();
                }
                return;
            } else if (ShellUtils.checkRootPermission()) {
                int returnCode = 0;
                Log.e(TAG, "有Root权限，开始静默安装");
                try {
                    PlatUpdateManager.savePlatInstallType(mContext, PlatInstallType.root);
                    DCStat.platUpdateEvent(ReportEvent.ACTION_PLATUPDATEINSTALLREQUEST, PlatUpdateManager.getPlatUpdateMode(mContext), null, PlatUpdateManager.getPlatDownloadType(mContext), PlatUpdateManager.getPlatInstallType(mContext), Constant.versionName, mUpdateInfo.getmUpgradeVersion(), PlatUpdateManager.getPlatDownloadAction(mContext));  //root安装请求
                    returnCode = PackageUtils.installSilent(mContext, mDownloadPath);
                    if (PackageUtils.INSTALL_SUCCEEDED != returnCode) {
                        DCStat.platUpdateEvent(ReportEvent.ACTION_PLATUPDATEINSTALLFAILED, PlatUpdateManager.getPlatUpdateMode(this), "returnCode:" + returnCode + "/" + "root权限不足", PlatUpdateManager.getPlatDownloadType(this), "root", Constant.versionName, mUpdateInfo.getmUpgradeVersion(), PlatUpdateManager.getPlatDownloadAction(this));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DCStat.platUpdateEvent(ReportEvent.ACTION_PLATUPDATEINSTALLFAILED, PlatUpdateManager.getPlatUpdateMode(this), "returnCode:" + returnCode + "/" + e.getMessage(), PlatUpdateManager.getPlatDownloadType(this), "root", Constant.versionName, mUpdateInfo.getmUpgradeVersion(), PlatUpdateManager.getPlatDownloadAction(this));

                }
                Log.e(TAG, "Root安装返回码==" + returnCode);
            }
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopMe();
            }
        }, 3 * 1000);
    }

    /**
     * 修改文件的权限；
     */

    private void modifyPermission() {
        try {
            String cmd = "chmod 777 " + mDownloadPath;
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
