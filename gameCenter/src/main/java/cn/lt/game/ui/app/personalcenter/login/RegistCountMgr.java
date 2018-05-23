package cn.lt.game.ui.app.personalcenter.login;

import cn.lt.game.bean.RegistCountBean;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.persist.PersistConst;
import cn.lt.game.lib.persist.PersistUtil;
import cn.lt.game.lib.util.LogUtils;

/**
 * Created by JohnsonLin on 2017/8/3.
 * 注册次数管理类
 */

public class RegistCountMgr {

    private static RegistCountMgr instance = new RegistCountMgr();

    private RegistCountMgr() {
    }

    public static RegistCountMgr getInstance() {
        return instance;
    }

    public boolean canRegist() {
        Object o = PersistUtil.readData(PersistConst.REGIST_COUNT);
        if (null != o && o instanceof RegistCountBean) {
            RegistCountBean registCountBean = (RegistCountBean) o;
            LogUtils.i(LogTAG.registCountTAG, "RegistCountBean = " + registCountBean.toString());
            return registCountBean.canRegist();
        }

        LogUtils.i(LogTAG.registCountTAG, "没找到相关的序列化数据");
        return true;
    }

    public void saveRegistCount() {
        Object o = PersistUtil.readData(PersistConst.REGIST_COUNT);
        RegistCountBean registCountBean = null;

        if (null != o && o instanceof RegistCountBean) {
            registCountBean = (RegistCountBean) o;
            if (registCountBean.isSameDate()) {
                LogUtils.i(LogTAG.registCountTAG, "同一天， 直接增加次数");
                registCountBean.addCount();
            } else {
                LogUtils.i(LogTAG.registCountTAG, "不是同一天， 保存新数据");
                registCountBean.saveNewData();
            }

        } else {
            registCountBean = new RegistCountBean();
            registCountBean.saveNewData();
        }

        LogUtils.i(LogTAG.registCountTAG, "新的RegistCountBean = " + registCountBean.toString());
        PersistUtil.persistData(registCountBean, PersistConst.REGIST_COUNT);
    }

}
