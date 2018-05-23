package cn.lt.game.download;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.ta.util.http.AsyncHttpClient;
import com.ta.util.http.FileHttpResponseHandler;
import com.ta.util.http.MySSLSocketFactory;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.install.ApkInstallManger;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.util.AdMd5;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.file.FileUtil;
import cn.lt.game.lib.util.file.TTGC_DirMgr;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.statistics.manger.DownSpeedDistributor;
import de.greenrobot.event.EventBus;

import static cn.lt.game.download.DownloadState.downloadPause;

/**
 * Created by wcn on 2016/2/26.
 */
public class DownloadRunnable implements Runnable {
    private static final String TAG = "DownloadRunnable";
    private static final int MAX_RETRY_TIMES = 5;
    private Context context;
    private MyApplication application;
    private FileDownloader mLoader;
    private AsyncHttpClient syncHttpClient = null;
    private DownloadFileHttpResponseHandler fileHttpResponseHandler = null;
    private Boolean finish = false;//下载完成标志：false-未完成；true-已完成
    private Boolean isPaused = true;//暂停状态标志：true-暂停；false-未暂停，下载中
    private int retry = 0;//重试次数，不为0，表示处于正在重试的状态
    private DownloadUpdateEvent updateEvent = new DownloadUpdateEvent();


    public DownloadRunnable(FileDownloader downloader, Context context) {
        this.mLoader = downloader;
        this.context = context;
        this.application = MyApplication.castFrom(context);
    }

    public void start() {
        LogUtils.d("jjj", "任务开始了");
        isPaused = false;//置为下载状态
        retry = 0;
        // 如果文件已经下载完，直接安装
        try {
            if (TextUtils.isEmpty(mLoader.getDownFileInfo().getDownUrl())) {
                downloadFail("下载地址为空");
                DCStat.downloadFialedEvent(mLoader.getDownFileInfo(), "下载地址为空");
                isPaused = true;
                return;
            }
            if (mLoader.getDownFileInfo().getDownPath() == null) {
                mLoader.getSaveFile();
            }
            File file = new File(mLoader.getDownFileInfo().getDownPath());
            if (file.exists() && AdMd5.md5sum(mLoader.getDownFileInfo().getDownPath()).equalsIgnoreCase(mLoader.getDownFileInfo().getMd5())) {
                downloadSuccess();
                return;
            }
            LogUtils.e("juice", "下载的url==>" + mLoader.getDownFileInfo().getDownUrl() + "==存的地址==>" + mLoader.getDownFileInfo().getDownPath());
        } catch (Throwable e) {
            // TODO: handle exception
            LogUtils.e("jjj", "还没下，就抛异常了" + e.getMessage());
            downloadFail(e.getMessage());
            e.printStackTrace();
            isPaused = true;
            return;
        }
        download();
    }


    @Override
    public void run() {
        start();
    }

    private void download() {
        try {
            // 如果已有下载，需停止之前的下载，再重新启动
            doStopDownload();
            // 避免频繁启动，此处加入每次启动前的延时
            doSleep();
            if (!isPaused) {
                // 启动下载
                startDownload();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 如果产生异常，停止下载
            stopDownload();
        }
    }

    /**
     * ****注意：****startDownload()不可单独多次调用，必须与stopDownload()配对调用
     */
    private void startDownload() {
        isPaused = false;
        doStartDownload();
    }

    private void doStartDownload() {
        // 启动新下载
        try {
            if (fileHttpResponseHandler != null) {
                return;
            }
            fileHttpResponseHandler = new DownloadFileHttpResponseHandler(mLoader);
            fileHttpResponseHandler.setInterrupt(false);
            syncHttpClient = new AsyncHttpClient();
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            syncHttpClient.setSSLSocketFactory(sf);
            if (!isPaused) {
                String downloadUrl = mLoader.getDownFileInfo().getDownUrl();
                /*转成URI是为了防止下载地址中包含非法字符串导致下载失败  ByATian*/
                URL url = new URL(downloadUrl);
                URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                downloadUrl = uri.toString();
                if (!URLUtil.isNetworkUrl(downloadUrl)) {
                    downloadFail("下载地址有问题或不存在");
                } else {
                    LogUtils.d("jjj", mLoader.getDownFileInfo().getName() + "---开始下载--下载地址----" + mLoader.getDownFileInfo().getDownUrl());
                    syncHttpClient.download(downloadUrl, fileHttpResponseHandler);
                }
            } else {
                fileHttpResponseHandler = null;
                syncHttpClient = null;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            LogUtils.e("jjj", "异常来自doStartDownload" + t.getMessage());  //只有这个抛异常可能会上报5次
            downloadFail(t.getMessage());
            //抛异常重新下载
//            fileHttpResponseHandler.dealDownloadFailureAndRetryDownload(e);     //TODO 有可能会导致失败数据多次重复上报
        }
    }

    /**
     * 停止下载.
     */
    public void stopDownload() {
        this.isPaused = true;// 设置停止标志
        this.retry = MAX_RETRY_TIMES;
        doStopDownload();// 执行停止操作
        updateEvent.game = mLoader.getDownFileInfo();
        EventBus.getDefault().post(updateEvent);
    }

    /**
     * 执行停止下载操作.
     */
    private void doStopDownload() {
        if (fileHttpResponseHandler != null) {
            synchronized (DownloadRunnable.class) {
                fileHttpResponseHandler.setInterrupt(true);
                fileHttpResponseHandler = null;
            }
        }
    }

    private void doSleep() {
        LogUtils.d(TAG, mLoader.getDownFileInfo().getName() + "---In DownloadThread3:before sleep----");
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, mLoader.getDownFileInfo().getName() + "---In DownloadThread3:after sleep----");
    }

    private void downloadSuccess() {
        //***dada 统计事件；
        LogUtils.d("kkk", "Runnable 下载完成平均速度=>" + DownSpeedDistributor.getInstance().getAVG(mLoader.getDownFileInfo().getId()));
        DCStat.downloadCompletedEvent(mLoader.getDownFileInfo(), NetUtils.getNetworkType(context), DownSpeedDistributor.getInstance().getAVG(mLoader.getDownFileInfo().getId()) + "", mLoader.getDownFileInfo().getDownPath());
        DownSpeedDistributor.getInstance().stop(mLoader.getDownFileInfo().getId());
        this.finish = true;
        this.isPaused = true;
        /** 更新下载长度 */
        mLoader.getDownFileInfo().setDownLength(mLoader.getDownFileInfo().getFileTotalLength());
        /** 更新下载状态 */
        mLoader.setState(DownloadState.downloadComplete);
        updateEvent.game = mLoader.getDownFileInfo();
        EventBus.getDefault().post(updateEvent);

        //***dada <----
        /** 启动下一个下载 */
        FileDownloaders.downloadNext();

        /** 启动安装 */
        ApkInstallManger.self().installPkg(mLoader.getDownFileInfo(), Constant.MODE_SINGLE, null, false);
    }

    public void downloadPause() {
        stopDownload();
        mLoader.savePrevStateAndPause();// 网络断开，导致下载暂停。此处需要保存上一状态，以备在网络再次连接时，能够自动启动下载
        mLoader.setState(downloadPause);
        updateEvent.game = mLoader.getDownFileInfo();
        EventBus.getDefault().post(updateEvent);
    }

    private void downloadFail(String errorMessage) {
        stopDownload();
        /** 启动下一个下载 */
        mLoader.setState(DownloadState.downloadFail);
        FileDownloaders.downloadNext();
        DCStat.downloadFialedEvent(mLoader.getDownFileInfo(), errorMessage); //确定是升级、普通下载
        updateEvent.game = mLoader.getDownFileInfo();
        EventBus.getDefault().post(updateEvent);
    }

    /**
     * 下载是否完成.
     *
     * @return true, if is finish
     */
    public boolean isFinish() {
        return finish;
    }

    public Boolean getFinish() {
        return finish;
    }

    public void setFinish(Boolean finish) {
        this.finish = finish;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void deleteFile() {
        if (mLoader.getDownFileInfo().getDownPath() == null) {
            return;
        }
        try {
            FileHttpResponseHandler fileHttpResponseHandlerTmp;
            if (fileHttpResponseHandler == null) {
                fileHttpResponseHandlerTmp = new DownloadFileHttpResponseHandler(mLoader);
            } else {
                fileHttpResponseHandlerTmp = fileHttpResponseHandler;
            }

            File file = fileHttpResponseHandlerTmp.getFile();
            if (file != null && file.exists()) {
                file.delete();
            }
            file = fileHttpResponseHandlerTmp.getTempFile();
            if (file != null && file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DownloadFileHttpResponseHandler extends FileHttpResponseHandler {

        private int printFlag = 1;
        private long lastSize = -1;
        private FileDownloader loader;

        DownloadFileHttpResponseHandler(FileDownloader loader) {
            super(loader.getDownFileInfo().getDownPath() == null ? loader.getSaveFile().getAbsolutePath() : loader.getDownFileInfo().getDownPath());
            this.loader = loader;
        }

        @Override
        public void onProgress(long totalSize, long currentSize, long speed) {
            super.onProgress(totalSize, currentSize, speed);
            DownSpeedDistributor.getInstance().start(loader.getDownFileInfo().getId());
            if (loader.getDownFileInfo().getState() != DownloadState.downInProgress || isPaused()) {
                LogUtils.d(TAG, "no---" + DownloadRunnable.this.hashCode() + "--------" + speed + "kbps");
                stopDownload();
                return;
            }
            int overTime = 0;
            if (speed != 0) {
                overTime = (int) ((totalSize - currentSize) / 1024 / speed);
            }

            long downloadPercent = currentSize * 100 / totalSize;
            if (printFlag == 1)
                LogUtils.d(TAG, "state=" + loader.getDownFileInfo().getState() + " per=" + downloadPercent + "--------" + speed + "kbps");

            if (currentSize != 0 && currentSize <= totalSize) {
                loader.getDownFileInfo().setDownLength(currentSize);
            }
            loader.getDownFileInfo().setFileTotalLength(totalSize);
            loader.getDownFileInfo().setDownSpeed(speed);
            if (overTime != 0) {
                loader.getDownFileInfo().setDownTimeLeft(overTime);
            }

            loader.update(loader.getDownFileInfo());

            //发送进度更新通知
            updateEvent.game = loader.getDownFileInfo();
            EventBus.getDefault().post(updateEvent);

            /* 开始正常下载后，重新给予5次重试机会 */
            if (lastSize > 0 && lastSize != currentSize) {
                retry = 0;
            }
            lastSize = currentSize;

        }

        @Override
        public void onFailure(Throwable error) {
            super.onFailure(error);
            LogUtils.e("jjj", "下载失败的回调" + error.getMessage());
            error.printStackTrace();
            if (!NetUtils.isConnected(context)) {
                downloadPause();
            } else {
                if (retry < MAX_RETRY_TIMES) {
                    retry++;
                    LogUtils.d(TAG, loader.getDownFileInfo().getName() + "---重试----" + retry);
                    doStopDownload();
                    doStartDownload();
                } else {
                    downloadFail(error.getMessage());  //无法解析host的下载失败异常必来自于这里(需验证)。（已经自动重试5次了） //经过验证 error.getMessage()==null有很多请情况下是网络很弱引起。
                }
            }
//            dealDownloadFailureAndRetryDownload(error);
        }


        @Override
        public void onSuccess(byte[] binaryData) {
            super.onSuccess(binaryData);
            downloadSuccess();
            LogUtils.d(TAG, "下载成功！");
        }

        @Override
        public void onFinish() {
            super.onFinish();
            LogUtils.d(TAG, "******************onFinish！");
        }


        /**
         * 处理下载失败结果并重新下载
         *
         * @param error
         */
        public void dealDownloadFailureAndRetryDownload(Throwable error) {
            /*
            Dir or File not exist ： 文件夹或文件被删除
            write failed: EIO (I/O error) ： 原来使用SD卡下载，下载过程中拔出SD卡
             */
            if (error.getMessage() != null && (error.getMessage().contains("Dir or File not exist") || error.getMessage().contains("write failed: EIO (I/O error)"))) {
                TTGC_DirMgr.init();
                TTGC_DirMgr.makeDirs();
                mLoader.getSaveFile();
                GameBaseDetail game = mLoader.getDownFileInfo().clone();
                DCStat.downloadFialedEvent(game, "Dir or File not exist");
                LogUtils.i("DirErrorTAG", "重试下载，重新创建目录和文件");
                // 上报重试请求
                String downloadType = "";
                if (mLoader.getDownFileInfo().getPrevState() == InstallState.upgrade) {
                    game.setState(InstallState.upgrade);
                } else {
                    game.setState(DownloadState.undownload);
                }
                downloadType = Constant.AUTO;
                DCStat.downloadRequestEvent(game, Constant.AUTO_PAGE, new StatisticsEventData(), false, Constant.MODE_RETRY_REQUEST, downloadType, false, 0);

            }

            if (loader.getDownFileInfo() == null) {
                return;
            }
            if (loader.getDownFileInfo().getState() != DownloadState.downInProgress || isPaused()) {
                return;
            }
            loader.getDownFileInfo().setDownSpeed(0);
            loader.getDownFileInfo().setDownTimeLeft(0);
            loader.getDownFileInfo().setDownTimeLeft(0);
            loader.update(loader.getDownFileInfo());
            if (error.getMessage() != null && "Content has been consumed".equals(error.getMessage())) {
                // zql
                // 如果是java.lang.IllegalStateException: Content has been
                // consumed的错误
                // 删除文件重新下载
                FileUtil.deleteFile(loader.getDownFileInfo().getDownPath());
            }

            if (error.getMessage() != null && error.getMessage().toLowerCase().contains("no space" + " left on device")) {
                //存储空间不足,添加错误信息
                loader.getDownFileInfo().setDownloadFailedReason(application.getString(R.string.storage_sapce_not_enough_download_fail));
            }

            if (NetUtils.isConnected(context)) {
                if (retry < MAX_RETRY_TIMES) {
                    retry++;
                    LogUtils.d(TAG, loader.getDownFileInfo().getName() + "---重试----" + retry);
                    doStopDownload();
                    doStartDownload();
                } else {
                    downloadFail(error.getMessage());  //无法解析host的下载失败异常必来自于这里(需验证)。（已经自动重试5次了） //经过验证 error.getMessage()==null有很多请情况下是网络很弱引起。
                }
            } else {
                LogUtils.d(TAG, "无网络, 暂停---------！");
                downloadPause();
            }
        }

    }
}
