package cn.lt.game.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import cn.lt.game.global.LogTAG;
import cn.lt.game.application.wakeup.WakeUpUserTimer;
import cn.lt.game.event.DownloadInitEvent;
import cn.lt.game.lib.util.LogUtils;
import de.greenrobot.event.EventBus;

/**
 * Created by wenchao on 2015/7/3.
 * 下载服务
 */
public class DownloadService extends Service {
    private static final String TAG = "DownloadService";

    public static final int TYPE_ADD     = 1;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Cannot bind to Download Manager Service");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /**下载初始化事件，内存不足时会被回收，需要重新设置界面*/
        EventBus.getDefault().post(new DownloadInitEvent());

        LogUtils.i(TAG, "------下载服务初始化创建");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "下载服务销毁了");
        FileDownloaders.stopAllDownload(true, false);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int command = super.onStartCommand(intent, flags, startId);

        int            type = intent != null ? intent.getIntExtra("type", 0) : 0;
        LogUtils.i(TAG, "接收到下载指令" + type);
        switch (type) {
            case TYPE_ADD:
                String url = intent.getStringExtra("url");
                FileDownloader fileDownloader = FileDownloaders.getFileDownloader(url);
                if (fileDownloader != null) {
                    fileDownloader.download();
                    LogUtils.i(TAG, "开始下载" + fileDownloader.getDownUrl());

                    // 保存首次下载游戏的时间
                    saveFirstDownloadTime();
                }
                break;

        }

        return command;
    }

    /** 保存首次下载游戏的时间*/
    private void saveFirstDownloadTime() {
        LogUtils.i(LogTAG.wakeUpUser, "执行(保存首次下载时间)");
        if(WakeUpUserTimer.getFirstDownloadTime(this) == 0) {
            long time = System.currentTimeMillis();
            WakeUpUserTimer.saveFirstDownloadTime(this, time);
            LogUtils.i(LogTAG.wakeUpUser, "(保存首次下载时间) = " + time);
        } else {
            LogUtils.i(LogTAG.wakeUpUser, "非首次下载，不用保存首次下载时间");
        }
    }
}
