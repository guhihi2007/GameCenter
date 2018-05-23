package cn.lt.game.update;

import cn.lt.game.statistics.ReportEvent;

/**
 * @author chengyong
 * @time 2016/11/25 12:15
 * @des 平台下载动作 first/retry_request
 */

public enum PlatDownloadAction {

    /**
     * 版本第一次下载
     */
    first(ReportEvent.PLAT_DOWNLOAD_ACTION_FIRST),
    /**
     * 版本重新下载
     */
    retry_request(ReportEvent.PLAT_DOWNLOAD_ACTION_RETRY_REQUEST);

    public String plat_download_action;

    PlatDownloadAction(String name) {
        this.plat_download_action = name;
    }
}
