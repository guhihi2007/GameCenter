package cn.lt.game.lib.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cn.lt.game.BuildConfig;
import cn.lt.game.application.MyApplication;
import cn.lt.game.bean.PkgInfo;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.PreferencesUtils;
import cn.lt.game.lib.util.file.TTGC_DirMgr;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.threadPool.ThreadPoolProxyFactory;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by wenchao on 2016/1/22.
 */
public class AppUtils {

    private static List<PackageInfo> systemApps = new ArrayList<>();

    /***
     * 是否有系统权限
     *
     * @param context
     * @return
     */
    public static boolean isSystemApp(Context context) {
        return ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) || ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    /**
     * 检查安装状态
     *
     * @param packageName
     * @return
     */
    public static boolean isInstalled(String packageName) {
        return !TextUtils.isEmpty(packageName) && getPackageInfo(packageName) != null;
    }

    /**
     * APp 是否已安装，和可升级
     *
     * @param pkgName
     * @param versionCode
     * @return
     */
    public static boolean[] appStatus(String pkgName, int versionCode) {
        boolean[] array = new boolean[2];
        if (TextUtils.isEmpty(pkgName)) {
            array[0] = false;
            array[1] = false;
            return array;
        }

        PackageInfo packageInfo = getPackageInfo(pkgName);
        if (packageInfo == null) {
            array[0] = false;
            array[1] = false;
        } else {
            array[0] = true;
            int localVersionCode = packageInfo.versionCode;
            array[1] = versionCode > localVersionCode;
        }

        return array;
    }

    @Nullable
    public static PackageInfo getPackageInfo(String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = MyApplication.application.getPackageManager().getPackageInfo(packageName, 0);
        } catch (Exception e) {
            if (!(e instanceof PackageManager.NameNotFoundException)) {
                LogUtils.e("ScreenMonitorService", "packageName = " + packageName + "\tgetPackageInfo error: " + e.toString());
            }
        }

        if (packageInfo == null) {
            if (systemApps.size() <= 0) {
                getSystemApps();
            }

            for (PackageInfo appInfo : systemApps) {
                if (appInfo.packageName.equals(packageName)) {
                    packageInfo = appInfo;
                    break;
                }
            }
        }

        return packageInfo;
    }


    /**
     * 检查安装状态
     *
     * @param packagename
     * @return
     */
    public static PackageInfo getPackageInfoByPkgName(String packagename) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = MyApplication.application.getPackageManager().getPackageInfo(packagename, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (packageInfo == null) {
            if (systemApps.size() <= 0) {
                getSystemApps();
            }

            for (PackageInfo appInfo : systemApps) {
                if (appInfo.packageName.equals(packagename)) {
                    packageInfo = appInfo;
                }
            }
        }

        return packageInfo;
    }


    /* 传入activity的context，获取屏幕宽 */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;

    }

    /* 传入activity的context，获取屏幕高 */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;

    }


    /***
     * 获取当前APP版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageInfo pi = null;
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /***
     * 获取当前APP版本号
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageInfo pi = null;
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0.0";
    }

    /**
     * 判断一个String是否是纯数字
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            if (ch[i] < '0' || ch[i] > '9') {
                return false;
            }
        }
        return true;
    }


    /**
     * 获取所有应用程序
     *
     * @param context
     * @return
     */
    public static List<PackageInfo> getAllApps(Context context) {
        List<PackageInfo> allApps = new ArrayList<>();

        // 获取系统应用列表
        if (systemApps.size() <= 0) {
            getSystemApps();
        }

        allApps.addAll(systemApps);

        // 获取用户自己装的应用列表
        allApps.addAll(getUserAppList(context));

        // 需要返回所有已安装列表
        return allApps;
    }

    /***
     * 获取已安装列表部分信息
     * @param context
     * @return
     */
    public static String getInstalledApps(Context context) {
//        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> apps = getUserAppList(context);
        List<PkgInfo> installedList = new ArrayList<>();
        if (apps != null) {
            for (android.content.pm.PackageInfo packageInfo : apps) {
                installedList.add(new PkgInfo((TextUtils.isEmpty(packageInfo.packageName) ? "" : packageInfo.packageName), String.valueOf(packageInfo.versionCode), TextUtils.isEmpty(packageInfo.versionName) ? "" : packageInfo.versionName));
            }
        }
        String str = new Gson().toJson(installedList);
        return str;
    }

    /**
     * 获取用户自己装的应用列表
     */
    public static List<PackageInfo> getUserAppList(Context context) {
        List<PackageInfo> appList = new ArrayList<>();

        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);

        // 获取用户自己装的应用列表
        for (PackageInfo packageInfo : packageInfoList) {

            // 值如果 <=0 则为自己装的程序
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                appList.add(packageInfo);
            }
        }

        return appList;
    }

    /**
     * 获取系统自带应用列表
     */
    private static void getSystemApps() {
        PackageManager packageManager = MyApplication.application.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);

        LogUtils.i("huoquxitonglieb", "还没获取过系统列表，马上获取。。");
        for (PackageInfo packageInfo : packageInfoList) {

            // 值如果 >0 则为系统的程序
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                systemApps.add(packageInfo);
            }
        }

    }


    /**
     * 程序是否在前台运行
     *
     * @param context
     * @return
     */
    public static boolean isRunningForeground(Context context) {
        android.app.ActivityManager am = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName componentName = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = componentName.getPackageName();
        return currentPackageName != null && currentPackageName.equals(context.getPackageName());
    }

    /***
     * 当用户已在网络注册时有效, CDMA 可能会无效（中国移动：46000 46002, 中国联通：46001,中国电信：46003）
     *
     * @param context
     * @return
     */
    public static String getNetworkOperator(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String operator = telephonyManager.getSimOperator();
        LogUtils.i("GETUI", "operator===" + operator);
        String operatorName = "未知";
        if (operator != null) {
            if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
                operatorName = "中国移动";
            } else if (operator.equals("46001") || operator.equals("46006")) {
                operatorName = "中国联通";
            } else if (operator.equals("46003") || operator.equals("46005") || operator.equals("46011")) {
                operatorName = "中国电信";
            }
        }
        return operatorName;
    }

    /***
     * 获取可用存储空间
     * 返回单位：MB
     *
     * @return
     */
    public static long getAvailablMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize / (1048 * 1024);
        } else {
            return getAvailableInternalMemorySize();
        }
    }

    /**
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize / (1048 * 1024);
    }

    /**
     * SDCARD是否存
     */
    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /***
     * 获取MeteData信息
     * @param ctx
     * @param key
     * @return
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }

    /***
     * 判断是否有SIM卡
     *
     * @return
     */
    public static boolean hasSIMCard(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        int state = mTelephonyManager.getSimState();
        return TelephonyManager.SIM_STATE_READY == state;
    }

    /***
     * 修改文件权限
     * @param filePath
     */
    public static void modifyPermission(String filePath) {
        try {
            String cmd = "chmod 777 " + filePath;
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断apk是否不存在
     */
    public static boolean apkIsNotExist(String apkPath) {
        modifyPermission(apkPath);
        File file = new File(apkPath);
        if (file.exists()) {
            LogUtils.i(LogTAG.apkIsNotExistTAG, "apk文件还在，执行安装");
            return false;
        }
        LogUtils.i(LogTAG.apkIsNotExistTAG, "apk文件不存在，执行重新下载");
        return true;
    }

    /**
     * 获取客户端安装时间
     */
    public static long getClientInstallTime() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = MyApplication.application.getPackageManager().getPackageInfo(MyApplication.application.getPackageName(), 0);
        } catch (Exception e) {
//            e.printStackTrace();
        }

        if (packageInfo != null) {
            return packageInfo.firstInstallTime;
        }

        return 0;
    }

    /**
     * 获取客户端最后一次升级时间
     */
    public static long getClientLastUpdateTime() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = MyApplication.application.getPackageManager().getPackageInfo(MyApplication.application.getPackageName(), 0);
        } catch (Exception e) {
//            e.printStackTrace();
        }

        if (packageInfo != null) {
            return packageInfo.lastUpdateTime;
        }

        return 0;
    }

    public static String getChannel(Context context) {
        String channel;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            channel = appInfo.metaData.getString("GameCenter_CHANNEL", "") + "";
            if (TextUtils.isEmpty(channel)) {
                int i = appInfo.metaData.getInt("GameCenter_CHANNEL", 0);
                channel = String.valueOf(i);
            }

        } catch (Exception e) {
            e.printStackTrace();
            channel = "";
        }

        return channel;
    }

    /**
     * 上报CID/运营商/本地应用列表
     *
     * @param context
     * @param type
     * @param cid
     */
    public static void postData(final Context context, String type, String cid) {
        final ArrayMap<String, String> para = new ArrayMap<>();
        String installedList = "";
        try {
            installedList = getInstalledApps(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        para.put("cid", cid);
        para.put("type", type);
        para.put("network_type", NetUtils.getNetworkType(context));
        para.put("network_operator", getNetworkOperator(context));
        para.put("already_install_apps", TextUtils.isEmpty(installedList) ? "" : installedList);
        Net.instance().executePost(Host.HostType.GCENTER_HOST, Uri2.CID_REPORT, para, new WebCallBackToString() {
            @Override
            public void onSuccess(String result) {
                PreferencesUtils.putLong(context, Constant.POST_CID_PERIOD, System.currentTimeMillis());
                LogUtils.i("GETUI", "推送传给服务端的数据-->" + "network_type:" + para.get("network_type") + ",network_operator:" + para.get("network_operator"));
                LogUtils.i("GETUI", "后台请求成功返回数据：" + result);
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                PreferencesUtils.putLong(context, Constant.POST_CID_PERIOD, System.currentTimeMillis());
                LogUtils.i("GETUI", "推送数据请求失败");
            }
        });
    }

    public static synchronized String getIMEI(Context context) {
        String mImei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Set<String> set = new HashSet<>();
        if (Build.VERSION.SDK_INT >= 21) {
            String imei_0 = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            String imei_1 = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId(TelephonyManager.NETWORK_TYPE_UNKNOWN);
            String imei_2 = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId(TelephonyManager.PHONE_TYPE_GSM);
            String imei_3 = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId(TelephonyManager.PHONE_TYPE_CDMA);
            set.add(imei_0);
            set.add(imei_1);
            set.add(imei_2);
            set.add(imei_3);
        } else {
            String imei_0 = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            set.add(imei_0);
        }
        for (String imei : set) {
            if (!TextUtils.isEmpty(imei) && imei.length() == 15 && !"000000000000000".equals(imei)) {
                mImei = imei;
            }
        }
        return mImei;
    }

    /***
     * 写日志（给测试自动化测试用）
     * @param data
     */
    public static void saveLog(final String data) {
        if (!BuildConfig.DEBUG) return;
        String filePath = TTGC_DirMgr.GAME_CENTER_ROOT_DIRECTORY + File.separator + "ReportLog.txt";
        final File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ThreadPoolProxyFactory.getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                FileWriter fw = null;
                try {
                    fw = new FileWriter(file.getAbsolutePath(), true);
                    fw.write(data + "\r\n");
                    fw.flush();
                    if (fw != null) fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fw != null) fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }



    /**
     * 获取手机IMSI号
     */
    public static String getIMSI(Context context){
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String imsi = mTelephonyMgr.getSubscriberId();

        return imsi ;
    }
}
