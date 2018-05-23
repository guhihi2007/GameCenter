package cn.lt.game.update;

import cn.lt.game.statistics.ReportEvent;

/**
 * @author chengyong
 * @time 2016/11/25 12:15
 * @des 平台安装类型
 */

public enum PlatInstallType {

    /**
     * 系统装
     */
    system(ReportEvent.PLAT_INSTALL_TYPE_SYSTEM),
    /**
     * root装
     */
    root(ReportEvent.PLAT_INSTALL_TYPE_ROOT),
    /**
     * 自动装
     */
    auto(ReportEvent.PLAT_INSTALL_TYPE_AUTO),
    /**
     * 普通装
     */
    normal(ReportEvent.PLAT_INSTALL_TYPE_NORMAL);

    public String plat_install_type;

    PlatInstallType(String name) {
        this.plat_install_type = name;
    }
}
