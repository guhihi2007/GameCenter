package cn.lt.game.install;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.util.ArrayMap;

import cn.lt.game.application.MyApplication;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.event.InstallEvent;
import cn.lt.game.event.UninstallEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.update.PlatUpdateManager;
import de.greenrobot.event.EventBus;

import static cn.lt.game.db.service.DownFileService.mContext;


public class InstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i("InstallReceiver", "********安装广播收到了*****");
        String packageName = intent.getDataString();
        packageName = packageName.substring("package:".length());
        ApkInstallManger.self().removeInstallingApp(packageName);
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            LogUtils.i("InstallReceiver", "********PACKAGE_ADDED*****");
            final String finalPackageName = packageName;
            final int state = FileDownloaders.onInstall(packageName);
            MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //如果轮询已经完成过安装完成数据上报，则广播不再上报
                    ArrayMap<String, GameBaseDetail> myInstallMap = FileDownloaders.getInstallingList();
                    GameBaseDetail gameDetail = myInstallMap.remove(finalPackageName);
                    if (gameDetail == null) {
                        LogUtils.i("ScreenMonitorService", "轮询没有上报过数据，广播这里需上报");
                        FileDownloaders.reportInstallComplete(finalPackageName, "广播", state);
                    } else {
                        LogUtils.i("ScreenMonitorService", "轮询已经上报过数据，广播这里不需上报");
                    }
                }
            }, 10000);
            postEvent(packageName);
        } else if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
            LogUtils.i("InstallReceiver", "********REPLACED*****" + packageName);
            if (context.getPackageName().equals(packageName)) { //平台升级安装完成；
                PlatUpdateManager.saveDialogShowThisTime(context, 0);
                PlatUpdateManager.setShowRedPoint(context, false);
                // ***dada 安装成功，统计事件；
                Context mContext = MyApplication.application;
                DCStat.platUpdateEvent(ReportEvent.ACTION_PLATUPDATEINSTALLED, PlatUpdateManager.getPlatUpdateMode(mContext), null, PlatUpdateManager.getPlatDownloadType(mContext), PlatUpdateManager.getPlatInstallType(mContext), PlatUpdateManager.getPlatOldVersion(mContext), Constant.versionName, PlatUpdateManager.getPlatDownloadAction(mContext));
                PlatUpdateManager.savePlatUpdateMode(context, null);
            }

            postEvent(packageName);
            PlatUpdateManager.clearPlatOldVersion(mContext);

        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
            LogUtils.i("InstallReceiver", "卸载了 :" + packageName);
            FileDownloaders.onUninstall(packageName);
            //发送event，界面去接收更新
            GameBaseDetail game = new GameBaseDetail();
            game.setPkgName(packageName);
            DownloadUpdateEvent updateEvent = new DownloadUpdateEvent(game);
            EventBus.getDefault().post(updateEvent);
            UninstallEvent event = new UninstallEvent();
            event.packageName = packageName;
            EventBus.getDefault().post(event);
        }
    }


    private void postEvent(String packageName) {
        InstallEvent event = new InstallEvent();
        event.packageName = packageName;
        EventBus.getDefault().post(event);
    }
}
