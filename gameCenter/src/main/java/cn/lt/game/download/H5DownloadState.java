package cn.lt.game.download;

/**
 * H5定义的下载安装状态
 * Created by Erosion on 2017/8/16.
 */

public class H5DownloadState {
    public static final int NOT_DOWNLOAD = 0;
    public static final int WAITING = 1;
    public static final int DOWNLOADING = 2;
    public static final int STOP = 3;
    public static final int DOWNLOAD_FINISH = 4;
    public static final int INSTALL_FINISH = 5;
    public static final int RETRY = 6;
    public static final int UPGRADE = 7;
    public static final int INSTALLING = 8;
    public static final int CHECK = 9;
}
