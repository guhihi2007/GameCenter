package cn.lt.game.download;

public class DownloadState {
    public static final int invalid = -1;
    // 0表示未开始下载
    public static final int undownload = 0;
    // 1表示已下载完成
    public static final int downloadComplete = 1;
    // 2表示已开始下载
    public static final int downInProgress = 2;
    // 3表示下载暂停
    public static final int downloadPause = 3;
    // 4表示下载失败
    public static final int downloadFail = 4;
    // 5表示等待下载
    public static final int waitDownload = 5;
    //表示待卸载
    public static final int needUninstall = 6;


    /**
     * 此方法供给webview用的，其他慎用！
     */
    public static boolean isRealIng(final int status) {
        return status == downInProgress || status == downloadFail;
    }
}
