package cn.lt.game.lib.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.StatFs;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.download.DownloadChecker;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.file.FileSizeUtil;
import cn.lt.game.lib.util.file.TTGC_DirMgr;
import cn.lt.game.lib.widget.GlobalDialog;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.requisite.manger.SharedPreference;

/**
 * Created by wenchao on 2016/1/6.
 * 内存空间检测
 */
public class StorageSpaceDetection {
    private static final long GET_AVAILABLE_SIZE_ERROR = -100;

    public static void check(Context context, DownloadChecker.Executor runnable, DownloadChecker.Executor pauseRunnable) {
//        String savePath = MyApplication.application.getSavePosition();
        long surplus = getMemorySize_byte();
        long limit = getMinSpaceConfig();
        long surplusM = surplus / (1048 * 1024);
        LogUtils.d(LogTAG.HTAG, "检测到内存剩余" + surplusM + "M");
        //最低值
        if (surplus != GET_AVAILABLE_SIZE_ERROR && surplusM <= 0) {  //不执行下载
            showEmptyTips(context, "下载");
            // TODO: 2016/3/31 空间为0，结果上报
            Log.i("zzz", "内存空间为0，数据上报");
            DCStat.outofmemoryEvent("0M", "剩余内存为0", null);
        } else if (surplus != GET_AVAILABLE_SIZE_ERROR && surplusM <= limit) {
            showPathSettingDialog(context, pauseRunnable, limit);
            Log.i("zzz", "内存小于200M数据上报，可用内存:" + surplusM + "(内存不足200M)");
            DCStat.outofmemoryEvent(String.valueOf(surplusM), "内存不足200M", null);
            runnable.run();
        } else {
            runnable.run();
        }

        LogUtils.i(LogTAG.MemorySizeTAG, "获取手机内部剩余存储空间 = " + FileSizeUtil.getPhoneStorageMemorySize() / (1048 * 1024) + "M");
        LogUtils.i(LogTAG.MemorySizeTAG, "获取SDCARD剩余存储空间 = " + FileSizeUtil.getSDCardMemorySize() / (1048 * 1024) + "M");
        LogUtils.i(LogTAG.MemorySizeTAG, "获取当前可用运行内存 = " + FileSizeUtil.getAvailableMemory(context) / (1048 * 1024) + "M");
    }

    /**
     * 检查内存大小，然后数据上报，   1,给wifi自动下载提供 2,下载按钮点击下载提供
     * @param noReportData
     * @param context 如果不需要弹窗的话直接传null进来即可
     * @param showDialog 是否需要弹窗提示
     * @return
     */
    public static boolean check(boolean noReportData, Context context, boolean showDialog) {
        long surplus = getMemorySize_byte();
        long limit = getMinSpaceConfig();
        long surplusM = surplus / (1048 * 1024);
        LogUtils.d(LogTAG.HTAG, "检测到内存剩余" + surplusM + "M");
        //最低值
        if (surplus <= 0) {
            // TODO: 2016/3/31 空间为0，结果上报
            if (!noReportData) {
                LogUtils.i("zzz", "内存空间为0，数据上报");
                DCStat.outofmemoryEvent("0M", "剩余内存为0", null);
            }
            if (showDialog) {
                showEmptyTips(context, "下载");
            }
            return false;

        } else if (surplusM <= limit) {
            // TODO: 2016/3/31 空间小于限定值，结果上报
            if (!noReportData) {
                LogUtils.i("zzz", "内存小于200M数据上报，可用内存:" + surplusM + "(内存不足200M)");
                DCStat.outofmemoryEvent(String.valueOf(surplusM), "内存不足200M", null);
            }

            if (showDialog) {
                showPathSettingDialog(context, null, limit);
            }
            return true;
        }
        return true;
    }

    public static boolean noMemory(long packageSize) {
        String savePath = TTGC_DirMgr.GAME_CENTER_ROOT_DIRECTORY;
        long surplusB = getMemorySize_byte();
        return surplusB < packageSize;
    }


    private static void showPathSettingDialog(final Context context, final DownloadChecker.Executor pauseRunnable, final long limit) {
        final Activity topAct = ActivityManager.self().topActivity();
        if (context != null && null != topAct) {
            topAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final MessageDialog messageDialog = new MessageDialog(topAct, "内存不足", String.format(context.getResources().getString(R.string.storage_space_not_enough_change_path), limit), context.getResources().getString(R.string.cancel), context.getResources().getString(R.string.clean));
                    messageDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {
                        @Override
                        public void OnClick(View view) {
                            if (pauseRunnable != null) {
                                pauseRunnable.run();
                            }
                            context.startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                        }
                    });
                    messageDialog.show();
                }
            });
        }
    }

    public static void showEmptyTips(final Context context, final String type) {
        final Activity topAct = ActivityManager.self().topActivity();
        if (null != context && null != topAct) {
            topAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final MessageDialog messageDialog = new MessageDialog(topAct, "内存不足", "手机空间不足，无法完成" + type + "，请卸载不常用应用释放空间！", context.getResources().getString(R.string.cancel), "清理");
                    messageDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {
                        @Override
                        public void OnClick(View view) {
                            FileDownloaders.stopAllDownload();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                            if (topAct != null) {
                                topAct.startActivity(intent);
                            } else {
                                if (!(context instanceof Activity)) {
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                }
                                context.startActivity(intent);
                            }
                        }
                    });
                    messageDialog.setLeftOnClickListener(new MessageDialog.LeftBtnClickListener() {
                        @Override
                        public void OnClick(View view) {
                            if ("下载".equals(type)) {
                                FileDownloaders.stopAllDownload();
                            } else {
                                messageDialog.dismiss();
                            }
                        }
                    });
                    messageDialog.setCancelOnClickListener(new MessageDialog.CancelCliclListener() {
                        @Override
                        public void onClicl(View view) {
                            if ("下载".equals(type)) {
                                FileDownloaders.stopAllDownload();
                            } else {
                                messageDialog.dismiss();
                            }
                        }
                    });
                    messageDialog.show();
                }
            });

        }
    }


    /**
     * 单位是M
     *
     * @return
     */
    private static long getMinSpaceConfig() {
        String minSpace = SharedPreference.getMinSpaceLimit(MyApplication.application);
        if(TextUtils.isEmpty(minSpace)) {
            return 200;
        }
        try {
            return Long.parseLong(minSpace);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 200;
    }

    /**
     * 剩余空间百分比
     *
     * @param path
     * @return
     */
    private static float getSurplusPercentage(String path) {
        try {
            StatFs fileStats = new StatFs(path);
            fileStats.restat(path);
            long available = (long) fileStats.getAvailableBlocks() * fileStats.getBlockSize();
            long total = (long) fileStats.getBlockCount() * fileStats.getBlockSize();
            return available * 1f / total;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 计算剩余空间
     *
     * @param path
     * @return
     */
    public static long getAvailableSize(String path) {
        try {
            return FileSizeUtil.getPhoneStorageMemorySize();
        } catch (Exception e) {
            e.printStackTrace();
            return GET_AVAILABLE_SIZE_ERROR;
        }
    }

    /**
     * 计算总空间
     *
     * @param path
     * @return
     */
    private static long getTotalSize(String path) {
        StatFs fileStats = new StatFs(path);
        fileStats.restat(path);
        return (long) fileStats.getBlockCount() * fileStats.getBlockSize();
    }

    public static void showInstallFailure(final Context context) {
//        if(!isRunningForeground(context)){
//            return;
//        }
        // TODO: 2016/4/8 静默安装内存不足数据上报
        long surplusM = getMemorySize_byte() / (1048 * 1024);
        DCStat.outofmemoryEvent(String.valueOf(surplusM), "内存不足200M", null);
        new GlobalDialog("内存不足", "手机空间不足，无法完成安装，请卸载不常用应用释放空间！", "取消", "清理", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    /**
     * 程序是否在前台运行
     *
     * @param context
     * @return
     */
    private static boolean isRunningForeground(Context context) {
        android.app.ActivityManager am = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName componentName = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = componentName.getPackageName();
        return currentPackageName != null && currentPackageName.equals(context.getPackageName());
    }

    /**
     * 获取剩余存储空间（单位 ：M）
     */
    public static long getMemorySize_M() {
        // 这里是为了防止有sd卡的被移除了导致下载不了，所以要重新设定默认下载路径
        TTGC_DirMgr.init();

        // 当前下载保存路径
        int sign = TTGC_DirMgr.SAVE_SIGN;
        switch (sign) {
            case TTGC_DirMgr.saveToPhoneStorage:// 获取手机内部剩余存储空间
                LogUtils.i(LogTAG.DirErrorTAG, "手机内存空间 = " + FileSizeUtil.getPhoneStorageMemorySize() / (1048 * 1024) + "M");
                return FileSizeUtil.getPhoneStorageMemorySize() / (1048 * 1024);
            case TTGC_DirMgr.saveToSD:// 获取SDCARD剩余存储空间
                LogUtils.i(LogTAG.DirErrorTAG, "手机SD卡空间 = " + FileSizeUtil.getSDCardMemorySize() / (1048 * 1024) + "M");
                return FileSizeUtil.getSDCardMemorySize() / (1048 * 1024);
            default:
                return FileSizeUtil.getPhoneStorageMemorySize() / (1048 * 1024);
        }
    }

    /**
     * 获取剩余存储空间（单位 ：byte）
     */
    public static long getMemorySize_byte() {
        // 这里是为了防止有sd卡的被移除了导致下载不了，所以要重新设定默认下载路径
        TTGC_DirMgr.init();

        // 当前下载保存路径
        int sign = TTGC_DirMgr.SAVE_SIGN;
        switch (sign) {
            case TTGC_DirMgr.saveToPhoneStorage:// 获取手机内部剩余存储空间
                return FileSizeUtil.getPhoneStorageMemorySize();
            case TTGC_DirMgr.saveToSD:// 获取SDCARD剩余存储空间
                return FileSizeUtil.getSDCardMemorySize();
            default:
                return FileSizeUtil.getPhoneStorageMemorySize();
        }
    }
}
