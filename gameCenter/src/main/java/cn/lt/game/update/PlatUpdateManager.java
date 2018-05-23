package cn.lt.game.update;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.util.List;

import cn.lt.game.lib.util.AdMd5;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.ui.app.sidebar.UpdateInfo;

/**
 * 管理平台升级的相关信息；
 *
 * @author dxx
 */
public class PlatUpdateManager {

    public static final String UPDATE_VERSION_KEY_TARGET_VERSION = "targetVersion";
    public static final String UPDATE_VERSION_KEY_DIALOG_SHOW_lASTTIME = "lastShowTime";
    public static final String UPDATE_VERSION_KEY_INSTALL_STATUS = "canInstall";
    public static final String UPDATE_VERSION_KEY_IS_DOWNLOADING = "isDownloading";
    public static final String UPDATE_VERSION_KEY_UPDATE_MODE = "updateType";  //mode
    public static final String UPDATE_VERSION_KEY_PLAT_UPDATE_TYPE = "platUpdateType";
    public static final String UPDATE_VERSION_KEY_PLAT_UPDATE_INSTALL_TYPE = "platUpdateInstallType";
    public static final String UPDATE_VERSION_KEY_PLAT_UPDATE_DOWNLOAD_ACTION = "platUpdatedownloadAction";
    public static final String UPDATE_RED_POINT_SHOW = "showRedPoint";
    public static final String PLAT_FIRST_INSTALL = "platFirstInstall";
    public static final String PLAT_NEW_VERSION_NAME = "plat_new_version_name";
    public static final String PLAT_LAST_VERSION_CODE = "plat_last_version_code";
    public static String PRE_FILE_NAME = "versionUpdate";
    public static final String HAVE_DOWNLOADED = "have_downloaded";
    public PlatUpdateInfo mUpdateInfo;

    public PlatUpdateManager(Context context) {
        init(context);
    }

    public static String getTargetVersionCode(Context context) {
        return context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).getString(UPDATE_VERSION_KEY_TARGET_VERSION, "");
    }

    public static void saveTargetVersionCode(Context context, String versionCode) {
        Editor editor = context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(UPDATE_VERSION_KEY_TARGET_VERSION, versionCode);
        editor.apply();
    }

    public static long getDialogShowLastTime(Context context) {
        return context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).getLong(UPDATE_VERSION_KEY_DIALOG_SHOW_lASTTIME, 0);
    }

    public static void saveDialogShowThisTime(Context context, long time) {
        Editor editor = context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(UPDATE_VERSION_KEY_DIALOG_SHOW_lASTTIME, time);
        editor.apply();
    }

    public static boolean getAutoInstallStatus(Context context) {
        return context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).getBoolean(UPDATE_VERSION_KEY_INSTALL_STATUS, true);
    }

    public static void saveAutoInstallStatus(Context context, boolean canInstalled) {
        Editor editor = context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(UPDATE_VERSION_KEY_INSTALL_STATUS, canInstalled);
        editor.apply();
    }

    public static boolean getIsDownloading(Context context) {
        return context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).getBoolean(UPDATE_VERSION_KEY_IS_DOWNLOADING, false);
    }

    public static void saveIsDownloading(Context context, boolean canInstalled) {
        Editor editor = context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(UPDATE_VERSION_KEY_IS_DOWNLOADING, canInstalled);
        editor.apply();
    }

    public static boolean showRedPoint(Context context) {
        return context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).getBoolean(UPDATE_RED_POINT_SHOW, false);
    }

    public static void setShowRedPoint(Context context, boolean canInstalled) {
        Editor editor = context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(UPDATE_RED_POINT_SHOW, canInstalled);
        editor.apply();
    }

    /********************************
     * 需要保持本地的
     **********************************/
    public static String getPlatUpdateMode(Context context) {
        return context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).getString(UPDATE_VERSION_KEY_UPDATE_MODE, PlatUpdateMode.auto.name());
    }

    public static void savePlatUpdateMode(Context context, PlatUpdateMode type) {
        Editor editor = context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(UPDATE_VERSION_KEY_UPDATE_MODE, type == null ? "" : type.type);
        editor.apply();
    }

    public static void savePlatDownloadType(Context context, PlatUpdateDownloadType platUpdateType) {
        Editor editor = context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(UPDATE_VERSION_KEY_PLAT_UPDATE_TYPE, platUpdateType == null ? "" : platUpdateType.plat_type);
        editor.apply();
    }

    public static String getPlatDownloadType(Context context) {
        return context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).getString(UPDATE_VERSION_KEY_PLAT_UPDATE_TYPE, "");
    }

    /**
     * 保存客户端升级前的旧版本号到本地文件
     */
    public static void savePlatOldVersion(Context context, String version) {
        Editor editor = context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(PLAT_NEW_VERSION_NAME, version);
        editor.apply();
    }

    /**
     * 从本地获取客户端升级前的旧版本号
     */
    public static String getPlatOldVersion(Context context) {
        if (context != null) {
            return context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_MULTI_PROCESS).getString(PLAT_NEW_VERSION_NAME, "");
        }
        return "";
    }

    /**
     * 从本地获取客户端升级前的旧版本号
     */
    public static void clearPlatOldVersion(Context context) {
        if (null != context) {
            Editor editor = context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(PLAT_NEW_VERSION_NAME, "");
            editor.apply();
        }
    }

    //安装存
    public static void savePlatInstallType(Context context, PlatInstallType platUpdateType) {
        if (null != context) {
            Editor editor = context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(UPDATE_VERSION_KEY_PLAT_UPDATE_INSTALL_TYPE, platUpdateType == null ? "" : platUpdateType.plat_install_type);
            editor.apply();
        }
    }

    public static String getPlatInstallType(Context context) {
        return context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).getString(UPDATE_VERSION_KEY_PLAT_UPDATE_INSTALL_TYPE, "");
    }

    //download_action
    public static void savePlatDownloadAction(Context context, PlatDownloadAction platUpdateType) {
        Editor editor = context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(UPDATE_VERSION_KEY_PLAT_UPDATE_DOWNLOAD_ACTION, platUpdateType == null ? "" : platUpdateType.plat_download_action);
        editor.apply();
    }

    public static String getPlatDownloadAction(Context context) {
        if (null != context) {
            return context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).getString(UPDATE_VERSION_KEY_PLAT_UPDATE_DOWNLOAD_ACTION, ReportEvent.PLAT_DOWNLOAD_ACTION_FIRST);
        }
        return "";
    }

    /*******/
    public static boolean getFirstInstall(Context context) {
        return context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).getBoolean(PLAT_FIRST_INSTALL, false);
    }

    public static void saveFirstInstall(Context context, boolean canInstalled) {
        Editor editor = context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(PLAT_FIRST_INSTALL, canInstalled);
        editor.apply();
    }

    public static boolean getDownloaded(Context context) {
        if (null != context) {
            return context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).getBoolean(HAVE_DOWNLOADED, false);
        }
        return false;
    }

    public static void saveDownloaded(Context context, boolean downloaded) {
        if (null != context) {
            Editor editor = context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).edit();
            editor.putBoolean(HAVE_DOWNLOADED, downloaded);
            editor.apply();
        }
    }

    /********************************存本地结束**************************************/

    /**
     * 判断是否已经下载完成；
     *
     * @return
     */
    public static boolean isDowloaded(Context context) {
        String path = PlatUpdatePathManger.getDownloadPath(context);
        if (!TextUtils.isEmpty(path)) {
            if (apkExist(context) && UpdateInfo.getPackage_md5().equalsIgnoreCase(AdMd5.md5sum(path))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否已经下载完成；
     *
     * @return
     */
    public static boolean isMD5Match(Context context) {
        String path = PlatUpdatePathManger.getDownloadPath(context);
        return UpdateInfo.getPackage_md5().equalsIgnoreCase(AdMd5.md5sum(path));
    }

    /***
     * 安装包是否存在
     *
     * @param context
     * @return
     */
    public static boolean apkExist(Context context) {
        String path = PlatUpdatePathManger.getDownloadPath(context);
        File file = new File(path);
        return file.exists();
    }
    /**
     * 判断当前应用程序处于前台还是后台
     */
    public static boolean isForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningAppProcessInfo> pis = manager.getRunningAppProcesses();
            ActivityManager.RunningAppProcessInfo topAppProcess = pis.get(0);
            if (topAppProcess != null && topAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : topAppProcess.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        return true;
                    }
                }
            }
        } else {
            List localList = manager.getRunningTasks(1);
            ActivityManager.RunningTaskInfo taskInfo = (ActivityManager.RunningTaskInfo) localList.get(0);
            if (taskInfo != null) {
                ComponentName componentName = taskInfo.topActivity;
                String packageName = componentName.getPackageName();
                if (!TextUtils.isEmpty(packageName) && packageName.equals(context.getPackageName())) {
                    return true;
                }
            }
        }
        return false;

    }

    public void init(Context context) {
        if (mUpdateInfo == null) {
            mUpdateInfo = new PlatUpdateInfo();
        }
        mUpdateInfo.setDownloadUrl(UpdateInfo.getDownload_link());
        mUpdateInfo.setForce(UpdateInfo.isIs_force());
        mUpdateInfo.setmReleaseData(UpdateInfo.getCreated_at());
        mUpdateInfo.setmUpgradeVersionCode(UpdateInfo.getVersion_code());
        mUpdateInfo.setmUpgradeVersion(UpdateInfo.getVersion());
    }

    public PlatUpdateInfo getUpdateInfo() {
        return mUpdateInfo;
    }


    /**
     * 保存上次需要升级的新版本号(断点续传用到)
     */
    public static void saveLastDownloadVersion(Context context, long versionCode) {
        Editor editor = context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(PLAT_LAST_VERSION_CODE, versionCode);
        editor.apply();
    }

    /**
     * 获取上次需要升级的新版本号(断点续传用到)
     */
    public static long getLastDownloadVersion(Context context) {
        if (context != null) {
            return context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE).getLong(PLAT_LAST_VERSION_CODE, 0);
        }
        return 0;
    }
}
