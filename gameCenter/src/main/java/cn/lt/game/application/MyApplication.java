package cn.lt.game.application;

import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.baidu.mobstat.StatService;
import com.facebook.stetho.Stetho;
import com.ta.util.download.DownLoadConfigUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.lt.game.BuildConfig;
import cn.lt.game.bean.SignPointsNet;
import cn.lt.game.global.Constant;
import cn.lt.game.install.ApkInstallManger;
import cn.lt.game.install.autoinstaller.AutoInstallerContext;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.ClipBoardManagerUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.MetaDataUtil;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.net.Net;
import cn.lt.game.receiver.ScreenBroadcastReceiver;
import cn.lt.game.service.CoreService;
import cn.lt.game.statistics.manger.StatManger;
import cn.lt.game.statistics.manger.YieldReportMgr;
import cn.lt.game.ui.app.sidebar.UpdateInfo;
import cn.lt.game.ui.app.tabbar.MyActivityLifecycleCallbacks;
import cn.lt.game.update.PlatDownloadManager;
import cn.lt.game.update.VersionCheckManger;

public class MyApplication extends Application {


    public static int width, height, dpi;
    public static double screenSize;
    public static MyApplication application;
    public boolean isRun;
    public static boolean isBackGroud = false;    //是否是用户Home键打开游戏中心
    /**
     * 是否来自通知栏，点击通知栏时过滤
     */
    public boolean mIsFromNotification;
    public boolean mIsFromNotificationForIndexNet;
    public boolean mIsFromNotificationForGameInfo;
    /**
     * 当前页面
     */
    public String mCurrentPage = "";
    /**
     * 当前搜索词
     */
    public String mCurrentWord = "";

    SharedPreferences sharedPreferences;

    private List<Service> services = new ArrayList<>();
    /**
     * 平台更新管理
     */
    private PlatDownloadManager mDownloadManger;
    /* 新版本信息 */
    private boolean hasNewVersion = false;// 是否存在新版本
    private String addTime = "";// 添加时间
    private String downUrl = "";// 下载地址
    private int id = 0;// id
    private String updateContent = "";// 更新内容
    private String version = "";// 版本名
    private int versionCode = 0;// 版本号
    private boolean isForce = false; //是否强更
    /* 更新是否已经下载完成 */
    private boolean isUpdatting = false;
    private boolean stopUpdatting = false;
    private boolean isDownFinish = false;
    private boolean newGameDownload = false;
    private boolean newGameUpdate = false;
    private boolean isActive = false;
    private int saveSign = -1;// 0代表ROM，1代表SD卡，2代表扩展SD卡
    public static String imei = "";
    public static String imsi = "";
    /* 渠道号 */
    private volatile String channel;
    private int gameId = 0;
    //记录是否是第一次打开app,默认不是
    public static boolean firstOpen;
    //记录活动id
    public String activityId;
    //记录活动名字
    public String activityName;
    public SignPointsNet lastPoint;
    //记录我的任务小红点
    public boolean myTaskFlag;
    /**
     * 上一页面的容器
     */
    private List<String> mLastPageList = new ArrayList<>();
    /**
     * 上一页面Id的容器
     */
    private List<String> mLastPageIdList = new ArrayList<>();
    public boolean rankMainVisible;

    /**
     * 请求开关数据是否已完成
     */
    public boolean switchIsReady = false;
    public String region;
    public String city;
    public String country;

    private boolean hadShowOneKeyDialog = false;

    public boolean isHadShowOneKeyDialog() {
        return hadShowOneKeyDialog;
    }

    public void setHadShowOneKeyDialog(boolean hadShowOneKeyDialog) {
        this.hadShowOneKeyDialog = hadShowOneKeyDialog;
    }

    public List<String> getmLastPageIdList() {
        return mLastPageIdList;
    }

    public List<String> getmLastPageList() {
        return mLastPageList;
    }

    public static MyApplication castFrom(Context context) {
        return (MyApplication) (context.getApplicationContext());
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void addServices(Service service) {
        if (services == null) {
            services = new ArrayList<>();
        }
        this.services.add(service);
    }

    @Override
    public void onTrimMemory(int level) {
        LogUtils.i("onTrimMemory", "MyApplication低内存等级" + level);
        switch (level) {
            case TRIM_MEMORY_UI_HIDDEN:
                break;
        }
        super.onTrimMemory(level);
    }

    public void removeService(Service service) {
        this.services.remove(service);
    }


    public void setmDownloadManger(PlatDownloadManager mDownloadManger) {
        this.mDownloadManger = mDownloadManger;
    }

    private static Handler mMainThreadHandler;

    /**
     * 得到主线程的handler
     */
    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        String processName = getProcessName(android.os.Process.myPid());
        //主线程的Handler
        mMainThreadHandler = new Handler();
        getGDTID();
        sharedPreferences = this.getSharedPreferences("setting", Context.MODE_PRIVATE);
        Net.instance().sendUDPForWifi(this);
        // 捕获程序未处理的崩溃异常；通过异常捕获判断是否需要重启应用；
        CrashExceptionHandler.self().init(this);
        initWidthAndHeight();
        /** 初始化imageloader */
        initImageLoader();
        /** 初始化日志 */
//        initLogger();
        boolean phonePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED;
        if (!phonePermission) {
            imei = AppUtils.getIMEI(this);
            imsi = AppUtils.getIMSI(this);
            LogUtils.i("maskoo23", "有电话权限==imei=" + imei);
            LogUtils.i("maskoo23", "有电话权限==imsi=" + imsi);
        } else {
            imei = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

            // TODO 到时候要跟产品确认，这种情况下imei和imsi是一样的行不行
            imsi = imei;
            LogUtils.i("maskoo23", "没有电话权限===" + imei);
        }
        //初始化平台版本检查；
        initVersionCheck();
        boolean isAlive = Utils.isServiceRunning(this, CoreService.class);
        if (!isAlive) {
            startService(new Intent(this, CoreService.class));
        }
        /** 初始化配置 */
        Configuration.init(this);
        // ===========初始化自动装context============
        AutoInstallerContext.getInstance().init(this);
        ApkInstallManger.self().init(this);
        // 剪切板；
        initClipBoard();
        //百度统计是否打开日志
        StatService.setDebugOn(false);
        DownLoadConfigUtil.loadConfig(getApplicationContext());
        // 腾讯bugly的初始化
        initBugly();
        if (!TextUtils.isEmpty(processName) && processName.equals(this.getPackageName())) {
            saveSign = sharedPreferences.getInt("saveSign", -1);
            initStatManger();
            registerScreenBroadcastReceiver();

            // 初始化调试神器
            if (BuildConfig.DEBUG) {
                Stetho.initializeWithDefaults(this);
            }
            YieldReportMgr.self().postReportData(this.getApplicationContext());
//            Uri uri = Uri.parse("content://cn.lt.game.UserAccountProvider/account");
//            getContentResolver().registerContentObserver(uri, true, new UserObservable(new Handler()));
        }

        this.registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks(this));
    }

    private BroadcastReceiver mScreenBroadcastReceiver = new ScreenBroadcastReceiver();


    private void registerScreenBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenBroadcastReceiver, filter);
    }

    /**
     * 腾讯bugly的初始化
     */
    private void initBugly() {
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setUploadProcess(processName == null || processName.equals(getPackageName()));
        // 初始化Bugly
        CrashReport.initCrashReport(getApplicationContext(), "7e9bc1cc04", BuildConfig.DEBUG, strategy);
    }

    /***
     * 动态获取开屏广告ID
     */
    private void getGDTID() {
        String splash_id = MetaDataUtil.getMetaData("SplashPosID").replace("_", "").trim();
        Constant.SplashPosID = splash_id;
    }

    private void initVersionCheck() {
        VersionCheckManger.getInstance().init(this);
    }


    private void initImageLoader() {
        ImageloaderUtil.getInstance().init(this);
    }

    private void initStatManger() {
        StatManger.self().init(this);
    }

    private void initWidthAndHeight() {
        WindowManager mana = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mana.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        dpi = dm.densityDpi;
    }

    private void initClipBoard() {
        ClipBoardManagerUtil.self().init(this);
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }


    public boolean getDeleteApk() {
        return sharedPreferences.getBoolean(Constant.Setting.DELETE_APK, true);
    }

    public void setDeleteApk(boolean deleteapk) {
        sharedPreferences.edit().putBoolean(Constant.Setting.DELETE_APK, deleteapk).apply();
    }

    public boolean getRootInstallIsChecked() {
        return sharedPreferences.getBoolean(Constant.Setting.AUTOINSTALL, false);
    }

    public void setRootInstallIsChecked(boolean autoInstall) {
        sharedPreferences.edit().putBoolean(Constant.Setting.AUTOINSTALL, autoInstall).apply();
    }

    public boolean getSystemInstall() {
        return sharedPreferences.getBoolean(Constant.Setting.SYSTEMINSTALL, false);
    }

    public void setSystemInstall(boolean systemInstall) {
        sharedPreferences.edit().putBoolean(Constant.Setting.SYSTEMINSTALL, systemInstall).apply();
    }

    public boolean getRootInstall() {
        return sharedPreferences.getBoolean(Constant.Setting.ROOTINSTALL, false);
    }

    public void setRootInstall(boolean systemInstall) {
        sharedPreferences.edit().putBoolean(Constant.Setting.ROOTINSTALL, systemInstall).apply();
    }


    /* 新版本信息相关的get set */
    public void setHasNewVersion(boolean hasNewVersion) {
        this.hasNewVersion = hasNewVersion;
    }

    public boolean isForce() {
        return isForce;
    }

    public void setForce(boolean force) {
        isForce = force;
    }


    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public Boolean getIsUpdatting() {
        return isUpdatting;
    }

    /* 自我更新相关的get set */
    public void setIsUpdatting(boolean updatting) {
        this.isUpdatting = updatting;
    }

    public Boolean getStopUpdatting() {
        return stopUpdatting;
    }

    public void setStopUpdatting(boolean stop) {
        this.stopUpdatting = stop;
    }

    public Boolean getIsDownFinish() {
        return isDownFinish;
    }

    public void setIsDownFinish(boolean finish) {
        this.isDownFinish = finish;
    }

    private static final int REQUEST_CODE_PERMISSION_SD = 100;

  /*  public boolean getNewGameInstalled() {
        return newGameInstalled;
    }

    *//* 用于控件管理页面的小红点是否显示 *//*
    public void setNewGameInstalled(boolean has) {
        newGameInstalled = has;
    }*/

    public boolean getNewGameUpdate() {
        return newGameUpdate;
    }

    public void setNewGameUpdate(boolean has) {
        newGameUpdate = has;
    }

    public boolean getNewGameDownload() {
        return newGameDownload;
    }

    public void setNewGameDownload(boolean has) {
        newGameDownload = has;
    }

    public MyApplication setUpdateInfoToApplication() {
        String download_link = UpdateInfo.getDownload_link();
        String feature = UpdateInfo.getFeature();
        String created_at = UpdateInfo.getCreated_at();
        this.setHasNewVersion(true);
        this.setAddTime(created_at);
        this.setDownUrl(download_link);
        this.setUpdateContent(feature);
        this.setVersion(UpdateInfo.getVersion());
        this.setVersionCode(UpdateInfo.getVersion_code());
        this.setForce(UpdateInfo.isIs_force());
        return this;
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
