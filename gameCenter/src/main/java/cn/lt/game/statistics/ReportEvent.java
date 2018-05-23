package cn.lt.game.statistics;

public class ReportEvent {


    public static final String ACTION_PAGE_MULTI_UNITS = "pageUnits";

    /**
     * 统计时间的ActionType:
     * 非下载点击事件；
     */
    public static final String ACTION_CLICK = "click";
    /**
     * 统计时间的ActionType:
     * 页面跳转；
     */
    public static final String ACTION_PAGEJUMP = "pageJump";
    /**
     * 统计时间的ActionType:
     * 主动升级下载请求；
     */
    public static final String ACTION_UPDATEDOWNLOADREQUEST = "updateDownloadRequest";
    /**
     * 统计时间的ActionType:
     * 主动下载请求；
     */
    public static final String ACTION_DOWNLOADREQUEST = "downloadRequest";
    /**
     * 统计时间的ActionType:
     * 主动升级下载失败；
     */
    public static final String ACTION_UPDATEDOWNLOADFAILED = "updateDownloadFailed";
    /**
     * 统计时间的ActionType:
     * 主动下载失败；
     */
    public static final String ACTION_DOWNLOADFAILED = "downloadFailed";
    /**
     * 统计时间的ActionType:
     * 主动升级下载成功；
     */
    public static final String ACTION_UPDATEDOWNLOADED = "updateDownloaded";
    /**
     * 统计时间的ActionType:
     * 主动下载成功；
     */
    public static final String ACTION_DOWNLOADED = "downloaded";
    /**
     * 统计时间的ActionType:
     * 主动升级下载安装成功；
     */
    public static final String ACTION_UPDATEINSTALLSUCCESS = "updateInstallSuccess";
    /**
     * 统计时间的ActionType:
     * 主动下载安装成功；
     */
    public static final String ACTION_INSTALLSUCCESS = "installSuccess";
    /**
     * 统计时间的ActionType:
     * 主动升级安装失败；
     */
    public static final String ACTION_UPDATEINSTALLFAILED = "updateInstallFailed";
    /**
     * 统计时间的ActionType:
     * 主动下载安装失败；
     */
    public static final String ACTION_INSTALLFAILED = "installFailed";


    /**
     * 统计时间的ActionType:
     * 所有静默安装失败；
     */
    public static final String ACTION_INSTALL_REQUEST = "installRequest";
    /**
     *
     * 所有升级安装请求；
     */
    public static final String ACTION_UPDATE_INSTALL_REQUEST = "updateInstallRequest";

    /**
     * 统计时间的ActionType:
     * 平台升级请求；
     */
    public static final String ACTION_PLATUPDATEREQUEST = "platUpdateRequest";
    /**
     * 统计时间的ActionType:
     * 平台下载成功；
     */
    public static final String ACTION_PLATUPDATEDOWNLOADED = "platUpdateDownloaded";
    /**
     * 统计时间的ActionType:
     * 平台下载失败；
     */
    public static final String ACTION_PLATUPDATEDOWNLOADFAILED = "platUpdateDownloadFailed";
    /**
     * 统计时间的ActionType:
     * 平台安装请求；
     */
    public static final String ACTION_PLATUPDATEINSTALLREQUEST = "platUpdateInstallRequest";
    /**
     * 统计时间的ActionType:
     * 平台安装成功；
     */
    public static final String ACTION_PLATUPDATEINSTALLED = "platUpdateInstalled";
    /**
     * 统计时间的ActionType:
     * 平台安装成功；
     */
    public static final String ACTION_PLATUPDATEINSTALLFAILED = "platUpdateInstallFailed";
    /**
     * 统计时间的ActionType:
     * 搜索点击；
     */
    public static final String ACTION_SEARCH = "search";

    /*******************************************************
     * 平台升级下载的下载mode-----------------------------
     * ；
     */
    public static final String DOWNLOAD_MODE_ALERT = "alert";
    public static final String DOWNLOAD_MODE_PUSH = "push";
    public static final String DOWNLOAD_MODE_AUTO = "auto";
    public static final String DOWNLOAD_MODE_FORCE = "force";
    /**
     * 平台升级下载的下载type-----------------------------
     */
    public static final String DOWNLOAD_TYPE_AUTO = "auto";
    public static final String DOWNLOAD_TYPE_MANUAL = "manual";
    /**
     * 平台升级下载的install_type-----------------------------
     */
    public static final String PLAT_INSTALL_TYPE_SYSTEM = "system";  //install_type:system/auto/root/normal.     //系统装/自动装/ROOT装/普通装
    public static final String PLAT_INSTALL_TYPE_AUTO = "auto";
    public static final String PLAT_INSTALL_TYPE_ROOT = "root";
    public static final String PLAT_INSTALL_TYPE_NORMAL = "normal";
    /**
     * 平台升级下载的download_action-----------------------------
     */
    public static final String PLAT_DOWNLOAD_ACTION_FIRST = "first";
    public static final String PLAT_DOWNLOAD_ACTION_RETRY_REQUEST = "retry_request";
    /**
     * 统计时间的Download Type:
     * ；
     */
    public static final String DOWNLOAD_TYPE_NORMAL = "normal";

    /**
     * 统计时间的ActionType:
     */
    public static final String ACTION_CHECKIN = "checkIn";

    /**
     * 统计时间的ActionType:
     */
    public static final String ACTION_CHECKOUT = "checkOut";
    /***
     * 安装内存不足Type
     */
    public static final String ACTION_MEMORY_ERROR = "error";
    /**
     * 推送
     */
    public static final String ACTION_PUSH = "push";
    /***
     * 开屏广告
     */
    public static final String ACTION_ADREPORT = "adReport";
    /***
     * 分析上报问题的事件
     */
    public static final String ACTION_ANALYSIS = "analysis";
}
