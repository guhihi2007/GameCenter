package cn.lt.game.download;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.db.service.DownFileService;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.file.TTGC_DirMgr;
import cn.lt.game.lib.util.threadpool.RequestTagManager;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.model.State;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.threadPool.ThreadPoolProxyFactory;
import cn.lt.game.ui.notification.LTNotificationManager;
import de.greenrobot.event.EventBus;

/**
 * 描述：多线程支持断点续传下载器.
 */
public class FileDownloader {
    private static final String TAG = "FileDownloader";
    private DownloadRunnable mRunnable;
    private DownFileService mDownFileService;
    private File saveFile;
    private GameBaseDetail mDownFile = new GameBaseDetail();
    private Context mContext;
    public boolean loopered = false;

    /**
     * 构建文件下载器.
     *
     * @param context   the context
     * @param downFile  the down file
     * @param threadNum 下载线程数
     */
    public FileDownloader(Context context, GameBaseDetail downFile, int threadNum) {
        mContext = context;
        mDownFile = downFile.clone();
        mDownFileService = new DownFileService(context);
        mRunnable = new DownloadRunnable(this, context);
    }

    /**
     * 更新指定线程最后下载的位置.
     *
     * @param downFile the down file
     */
    public synchronized void update(GameBaseDetail downFile) {
        mDownFile = downFile;
        this.mDownFileService.updateById(downFile);
    }

    public synchronized void save(GameBaseDetail downFile) {
        mDownFile = downFile;
        this.mDownFileService.save(downFile);
    }

    public synchronized void delete() {
        this.mDownFileService.delete(mDownFile.getId());
    }

    public synchronized void updateOpenTimeByPackName(String packName, long openTime) {
        mDownFile.setOpenTime(openTime);
        this.mDownFileService.updateOpenTimeByPackName(packName, openTime);
    }

    public long getDownLength() {
        return mDownFile.getDownLength();
    }

    public void setDownLength(long downLength) {
        mDownFile.setDownLength(downLength);
        update(mDownFile);
    }

    public int getState() {
        return mDownFile.getState();
    }

    /**
     * 下载等状态有变化时发送应用内通知
     *
     * @param state
     */
    public void setState(int state) {
        mDownFile.setState(state);
        update(mDownFile);
        if (!mDownFile.isOrderWifiDownload()) {
            LTNotificationManager.getinstance().sendNotification(mDownFile);
        }
    }

    public int getPrevState() {
        return mDownFile.getPrevState();
    }

    public void setPrevState(int state) {
        mDownFile.setPrevState(state);
        update(mDownFile);
    }

    public String getDownUrl() {
        return mDownFile.getDownUrl();
    }

    public GameBaseDetail getDownFileInfo() {
        return mDownFile;
    }

    /**
     * 开始下载文件.
     *
     * @return 已下载文件大小
     * @throws Exception the exception
     */
    public synchronized void download() {
        // ！！注意：此方法有两个try-catch，不要把代码都放到一个try-catch里面，否则mDownFile.getDownPath()是空的时候抛异常就会导致无法下载了
        boolean isFileExists = false;
        try {
            File file = new File(mDownFile.getDownPath());
            isFileExists = file.exists();
        } catch (Exception e) {
            // TODO: handle exception
        }

        try {
            LogUtils.i("DownloadService", "下载条件：" + mRunnable.isFinish() + "/" + mRunnable.isPaused() + couldBeStarted() + "/" + !isFileExists);
            //下载条件：false/true true/false
            if (couldBeStarted() || !isFileExists) {
                if (!isFileExists && getSaveFile() == null) {
                    State.updateState(mDownFile, DownloadState.downloadFail);
                    LogUtils.i(LogTAG.DirErrorTAG, "下载前条件判断失败，返回了！");
                    return;
                }
                if (mDownFile.getState() == DownloadState.undownload) {
                    setDownLength(0);
                }
                State.updateState(mDownFile, DownloadState.downInProgress);
                EventBus.getDefault().post(new DownloadUpdateEvent(mDownFile));
                doDownload();
            }
        } catch (Exception e) {
            LogUtils.i("DownloadService", "下载异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopDownload() {
        mRunnable.stopDownload();
        getDownFileInfo().setState(DownloadState.downloadPause);
        FileDownloaders.update(getDownFileInfo());
        EventBus.getDefault().post(new DownloadUpdateEvent(getDownFileInfo()));
        LogUtils.d(TAG, "++++++++++++++++stop");
    }

    private void doDownload() {
        LogUtils.i(TAG, "downPath=" + mDownFile.getDownPath());
        ThreadPoolProxyFactory.getCachedThreadPool().execute(mRunnable);
    }

    private boolean isDownloadComplete() {
        return mDownFile.getDownLength() == mDownFile.getFileTotalLength();
    }

    private boolean couldBeStarted() {
        return mRunnable.isPaused() && !mRunnable.isFinish();
    }

    public void setmRunnableFinish(boolean isFinish) {
        if (mRunnable != null) {
            mRunnable.setFinish(isFinish);
        }
    }

    /**
     * 自动暂停时
     */
    public void savePrevStateAndPause() {
        if (State.isStateCanPause(getState())) {
            if (getPrevState() == InstallState.upgrade) {
                setPrevState(InstallState.upgrade_inProgress);
            } else {
                setPrevState(getState());
            }
            setState(DownloadState.downloadPause);
            LogUtils.d("sss", "最低有几个");
            DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, Constant.AUTO_PAGE, -1, null, -1, getDownFileInfo().getId() + "", null, Constant.AUTO, "downStop", getDownFileInfo().getPkgName(), ""));
        }
    }

    public void deleteSaveFile() {
        mRunnable.deleteFile();
        RequestTagManager.deleteRequestTag(mContext, String.valueOf(mDownFile.getId()));
    }

    /**
     * Gets the save file.
     *
     * @return the save file
     */
    public File getSaveFile() {
        LogUtils.i(LogTAG.DirErrorTAG, "FileDownloader.getSaveFile()-- saveFile = " + saveFile);
        // 构建保存文件
        if (saveFile == null || !saveFile.exists()) {
            String downPath = getDownPath();
            saveFile = new File(downPath);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            if (!saveFile.exists()) {
                try {
                    saveFile.createNewFile();
                } catch (IOException e) {
                    LogUtils.e("FileDownloader -> file create failed: " + e.toString());
                    LogUtils.i(LogTAG.DirErrorTAG, "saveFile.createNewFile()抛异常，--> " + e.toString());

                    if (!TextUtils.isEmpty(e.toString()) && e.toString().contains("Device or resource busy")) {

                        String newPath = TTGC_DirMgr.getApkDownloadDirectory().replace("TiantianGame", "") + "Download"
                                + "/" + mDownFile.getPkgName() + mDownFile.getMd5() + ".apk.abyz";

                        mDownFile.setDownPath(newPath);
                        LogUtils.i(LogTAG.DirErrorTAG, "抛异常啦，新的地址是 = " + newPath);

                        File file = new File(mDownFile.getDownPath());
                        if (!file.canWrite() || !file.canRead() || !file.exists()) {
                            mDownFile.setDownPath(newPath);
                            mDownFile.setDownLength(0);
                        }

                        LogUtils.i(LogTAG.DirErrorTAG, "mDownFile.getDownPath() = " + mDownFile.getDownPath());


                        saveFile = new File(newPath);
                        try {
                            saveFile.createNewFile();
                            return saveFile;
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            LogUtils.e("FileDownloader -> 又又又file create failed: " + e1.toString());
                            LogUtils.i(LogTAG.DirErrorTAG, "saveFile.createNewFile()又又又抛异常，--> " + e1.toString());
                        }
                    }
                    return null;
                }
            }
        } else if (mDownFile.getDownPath() == null) {
            mDownFile.setDownPath(saveFile.getAbsolutePath());
        }

        return saveFile;
    }

    private String getDownPath() {
        final String PATH = TTGC_DirMgr.GAME_CENTER_ROOT_DIRECTORY + "/" + mDownFile.getPkgName() + mDownFile.getMd5() + ".apk.abyz";
        if (mDownFile.getDownPath() != null) {
            File file = new File(mDownFile.getDownPath());
            if (!file.canWrite() || !file.canRead() || !file.exists()) {
                mDownFile.setDownPath(PATH);
                mDownFile.setDownLength(0);
            }
        } else if (mDownFile.getDownloadFailedReason().equalsIgnoreCase(MyApplication.application.getString(R.string.storage_sapce_not_enough_download_fail))) {
            mDownFile.setDownPath(PATH);
            mDownFile.setDownLength(0);
        } else {
            mDownFile.setDownPath(PATH);
            mDownFile.setDownLength(0);
        }
        LogUtils.i(LogTAG.DirErrorTAG, "mDownFile.getDownPath() = " + mDownFile.getDownPath());

        return mDownFile.getDownPath();
    }

}
