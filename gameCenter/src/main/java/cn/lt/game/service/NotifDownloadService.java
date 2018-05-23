package cn.lt.game.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

import cn.lt.game.application.MyApplication;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.file.FileUtil;
import cn.lt.game.lib.util.file.TTGC_DirMgr;

public class NotifDownloadService extends Service {
    public static final String ACTION = "cn.lt.game.NotifDownloadService";
    private MyApplication application;
    private HttpHandler httpHandler;
    private int versionCode = 0;
    private String updatePath;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        try {
            /* 记录sd卡中包的sharedPreferences */

            sharedPreferences = this.getSharedPreferences("version", 0);
            editor = sharedPreferences.edit();
            application = (MyApplication) this.getApplication();
            versionCode = application.getVersionCode();
            String downloadUrl = intent.getStringExtra("url");
            intent.removeExtra("url");
            updatePath = TTGC_DirMgr.getApkDownloadDirectory() + File.separator + "update.apk";
            runUpdate(this, downloadUrl);

        } catch (Exception ex) {
            stopSelf();
        }

    }

    /* 下载更新包 */
    private void runUpdate(Context context, String downloadUrl) {

		/* 如果update.apk已经存在并且version为最新 */
        // File file = new File(Constant.UPDATE_PACKAGE_PATH);
        File file = new File(updatePath);

        if (file.exists() && sharedPreferences.getInt(Constant.VERSIONCODE, 0) == versionCode) {
            installApk(file);
            return;
        }

        if (!application.getIsUpdatting()) {
            application.setIsUpdatting(true);
            application.setIsDownFinish(false);
            editor.putInt(Constant.VERSIONCODE, 0);
            editor.commit();
            // 执行更新
            HttpUtils http = new HttpUtils();
            // 测试apk下载链接
            httpHandler = http.download(application.getDownUrl(), updatePath, true, false, new RequestCallBack<File>() {

                /* 下载中 */
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    if (application.getStopUpdatting()) {
                        httpHandler.cancel();
                        application.setStopUpdatting(false);
                    } else {
                        int intTotalKb = (int) (total / 1024);
                        int intCurrentKb = (int) (current / 1024);
                        int percent = 0;
                        if (intTotalKb == 0) {
                            percent = 0;
                        } else {
                            percent = (int) (intCurrentKb * 100.0 / intTotalKb);
                        }


                        // notif.contentView.setTextViewText(
                        // R.id.update_notifi_percent, percent
                        // + "%");
                        // notif.contentView.setProgressBar(
                        // R.id.notificationProgress, intTotalKb,
                        // intCurrentKb, false);
                        // manager.notify(Constant.UPDATE_NOTIFICATION_ID,
                        // notif);
                    }
                }

                /* 下载失败 */
                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    // 因为更新包的记录错误导致的下载失败。删除更新包
                    if (arg1.equals("maybe the file has downloaded completely")) {
                        FileUtil.deleteFile(updatePath);
                    }
                    application.setStopUpdatting(false);
                    application.setIsUpdatting(false);
                    // manager.cancel(Constant.UPDATE_NOTIFICATION_ID);
                }

                /* 下载成功 */
                @Override
                public void onSuccess(ResponseInfo<File> arg0) {
                    // TODO Auto-generated method stub
                    application.setStopUpdatting(false);
                    application.setIsUpdatting(false);
                    application.setIsDownFinish(true);
                    // 下载成功，记录更新包的版本号
                    editor.putInt(Constant.VERSIONCODE, versionCode);
                    editor.commit();
                    // 安装
                    installApk(arg0.result);
                }
            });
        }
    }

    // 安装apk
    protected void installApk(File file) {
        try {
            String cmd = "chmod 777 " + file.getPath();
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Intent intent = new Intent();
        // 执行动作
        intent.setAction(Intent.ACTION_VIEW);
        // 执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);

		/* 清除通知 */
        try {
        } catch (Exception e) {
        }
        FileDownloaders.stopAllDownload();
        System.exit(0);
    }

}
