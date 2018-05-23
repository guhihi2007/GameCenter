package cn.lt.game.update;

import cn.lt.game.statistics.ReportEvent;

/**
 * 升级类型DownloadMode  :   alert/force/push
 */
public enum PlatUpdateMode {
    /**
     * 系统弹框升级；
     */
    alert(ReportEvent.DOWNLOAD_MODE_ALERT),
    /**
     * 静默升级；
     */
    auto(ReportEvent.DOWNLOAD_MODE_AUTO),
    /**
     * 通知升级；
     */
    push(ReportEvent.DOWNLOAD_MODE_PUSH),
    /**
     * 强制升级；
     */
    force(ReportEvent.DOWNLOAD_MODE_FORCE);

    public String type;

     PlatUpdateMode(String name) {
        this.type = name;
    }
}
