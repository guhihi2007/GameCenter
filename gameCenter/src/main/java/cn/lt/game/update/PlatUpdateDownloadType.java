package cn.lt.game.update;

import cn.lt.game.statistics.ReportEvent;


/**
 * @author chengyong
 * @time 2016/11/25 11:53
 * @des auto/manual 自动下载、手动下载
 */

public enum PlatUpdateDownloadType {

    /**
     * 自动下载
     */
    auto(ReportEvent.DOWNLOAD_TYPE_AUTO),
    /**
     * 手动下载
     */
    manual(ReportEvent.DOWNLOAD_TYPE_MANUAL);

    public String plat_type;

     PlatUpdateDownloadType(String name) {
        this.plat_type = name;
    }
}
