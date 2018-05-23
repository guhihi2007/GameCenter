package cn.lt.game.lib.util.file;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.List;

import cn.lt.game.global.LogTAG;
import cn.lt.game.application.MyApplication;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.Utils;

/**
 * Created by JohnsonLin on 2017/3/14.
 * 游戏中心下载目录管理类
 * 游戏中心项目文件夹主路径、手机存储路径、sd卡路径等
 */

public class TTGC_DirMgr {

    /**
     * APK目录名
     */
    public static final String APK_RELATIVE_PATH = File.separator + "TiantianGame";

    /**
     * 游戏中心项目根目录文件夹
     */
    public static String GAME_CENTER_ROOT_DIRECTORY;

    /** 手机储存根路径*/
    public static String PHONE_STORAGE_ROOT_PATH;

    /** SD卡根路径（如果有的话）*/
    public static String SDCARD_ROOT_PATH;

    /** 当前保存的存储位置标记（手机存储/sd卡）*/
    public static int SAVE_SIGN;

    /** 代表手机存储*/
    public static final int saveToPhoneStorage = 0;

    /** 代表sd卡*/
    public static final int saveToSD = 1;

    private static String DIR_SPF_NAME = "TTGC_DirMgr";

    /** 初始化下载路径，给下载路径指定默认值*/
    public static void init() {
        resetParms();
        getRootStorage();
    }

    private static void resetParms() {
        GAME_CENTER_ROOT_DIRECTORY = "";
        PHONE_STORAGE_ROOT_PATH = "";
        SDCARD_ROOT_PATH = "";
    }

    /**
     * 判断使用的是手机存储还是SD卡
     **/
    private static void getRootStorage() {
        String phoneCardPath = "";
        String normalSDCardPath = "";
        List<String> volumePaths = Utils.getVolumePaths(MyApplication.application);
        for (int i = 0; i < volumePaths.size(); i++) {
            if (volumePaths.get(i).contains("emulated")) {
                PHONE_STORAGE_ROOT_PATH = volumePaths.get(i);
                phoneCardPath = PHONE_STORAGE_ROOT_PATH + APK_RELATIVE_PATH;
                Log.i("GameCenterPath", "phoneCardPath create=" + phoneCardPath);
            } else {
                SDCARD_ROOT_PATH = volumePaths.get(i);
                normalSDCardPath = SDCARD_ROOT_PATH + APK_RELATIVE_PATH;
                Log.i("GameCenterPath", "normalSDCardPath create=" + normalSDCardPath);
            }
        }

        //以下针对某些奇葩手机(ps:感觉这段代码很奇怪是不是？跟上面没区别是不是？我也不知道什么原因这个就能解决奇葩手机？先不管了，韬哥当时就是遇到奇葩手机问题，先放在这里)
        if (TextUtils.isEmpty(phoneCardPath)) {
            if (volumePaths.size() > 0) {
                PHONE_STORAGE_ROOT_PATH = volumePaths.get(0);
                phoneCardPath = volumePaths.get(0) + APK_RELATIVE_PATH;
                Log.i("GameCenterPath", "special phoneCardPath create=" + phoneCardPath);
            } else {
                phoneCardPath = "";
                Log.i("GameCenterPath", "special phoneCardPath is null");
            }
            if (volumePaths.size() > 1) {
                SDCARD_ROOT_PATH = volumePaths.get(1);
                normalSDCardPath = volumePaths.get(1) + APK_RELATIVE_PATH;
            } else {
                normalSDCardPath = "";
            }
        }

        if(TextUtils.isEmpty(PHONE_STORAGE_ROOT_PATH)) {
            PHONE_STORAGE_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
            phoneCardPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APK_RELATIVE_PATH;
        }

        GAME_CENTER_ROOT_DIRECTORY = MyApplication.application.getSharedPreferences(DIR_SPF_NAME, Context.MODE_PRIVATE).getString("savePosition", "");

        if(TextUtils.isEmpty(GAME_CENTER_ROOT_DIRECTORY)) {
            saveRootDirectory(phoneCardPath);
            setSaveSign(saveToPhoneStorage);
            LogUtils.i(LogTAG.DirErrorTAG, "SavePosition是空，默认使用手机储存空间");

        } else {

            if(GAME_CENTER_ROOT_DIRECTORY.contains(PHONE_STORAGE_ROOT_PATH)) {
                // 保存当前选择的是手机存储
                setSaveSign(saveToPhoneStorage);
            } else if(!TextUtils.isEmpty(SDCARD_ROOT_PATH)) {
                // 保存当前选择的是sd卡
                setSaveSign(saveToSD);
            } else if(TextUtils.isEmpty(SDCARD_ROOT_PATH)) {
                saveRootDirectory(phoneCardPath);
                setSaveSign(saveToPhoneStorage);
                LogUtils.i(LogTAG.DirErrorTAG, "用户选择的是sd卡，但是现在sd卡不存在，切换到手机存储");
            }

        }


    }


    /**
     * 获取APK下载目录
     */
    public static String getApkDownloadDirectory() {
        if (TextUtils.isEmpty(TTGC_DirMgr.GAME_CENTER_ROOT_DIRECTORY )) {
            init();
        }
        // 目前为了解决apk被一些流氓手机管家删除掉，暂时使用根目录
        return GAME_CENTER_ROOT_DIRECTORY;
    }

    /**
     * 获取启动页图片缓存目录
     */
    public static String getCachePicDirectory() {
        return GAME_CENTER_ROOT_DIRECTORY + File.separator + "cachePic";
    }

    /** 保存当前选择的存储根位置（手机存储/sd卡）
     * 更换时必须要调一次这个方法*/
    public static void saveRootDirectory(String savePosition) {
        GAME_CENTER_ROOT_DIRECTORY = savePosition;
        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(DIR_SPF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("savePosition", savePosition).apply();
    }

    /** 判断手机是否插有SD卡*/
    public static boolean hasSdCard(){
        return Utils.getVolumePaths(MyApplication.application).size() > 1;
    }

    private static int getSaveSign() {
        return MyApplication.application.getSharedPreferences(DIR_SPF_NAME, Context.MODE_PRIVATE).getInt("saveSign", -1);
    }

    public static void setSaveSign(int saveSign) {
        SAVE_SIGN = saveSign;
        MyApplication.application.getSharedPreferences(DIR_SPF_NAME, Context.MODE_PRIVATE).edit().putInt("saveSign", saveSign).apply();
    }


    /** 创建游戏中心所需要用的各个文件夹目录*/
    public static void makeDirs() {
        try {

            LogUtils.i(LogTAG.DirErrorTAG, "createDirectory -- > productRootDirectory =  " + TTGC_DirMgr.GAME_CENTER_ROOT_DIRECTORY);
            File productRootDirectory = new File(TTGC_DirMgr.GAME_CENTER_ROOT_DIRECTORY);
            if (!productRootDirectory.exists()) {
                // 如果文件夹不存在
                productRootDirectory.mkdir();
                File appDirectory = new File(TTGC_DirMgr.getApkDownloadDirectory());
                appDirectory.mkdir();

                File cacheDirectory = new File(TTGC_DirMgr.getCachePicDirectory());
                cacheDirectory.mkdir();

            } else if (!productRootDirectory.isDirectory()) {
                // 如果不是文件夹
                productRootDirectory.delete();
                productRootDirectory.mkdir();
                File appDirectory = new File(TTGC_DirMgr.getApkDownloadDirectory());
                appDirectory.mkdir();
                File cacheDirectory = new File(TTGC_DirMgr.getCachePicDirectory());
                cacheDirectory.mkdir();
            } else {
                File appDirectory = new File(TTGC_DirMgr.getApkDownloadDirectory());
                File cacheDirectory = new File(TTGC_DirMgr.getCachePicDirectory());
                if (!appDirectory.exists() || !appDirectory.isDirectory()) {
                    appDirectory.delete();
                    appDirectory.mkdir();
                }
                if (!cacheDirectory.exists() || !cacheDirectory.isDirectory()) {
                    cacheDirectory.delete();
                    cacheDirectory.mkdir();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(LogTAG.DirErrorTAG, "makeDirs抛异常，--> " + e.toString());

        }
    }

}
