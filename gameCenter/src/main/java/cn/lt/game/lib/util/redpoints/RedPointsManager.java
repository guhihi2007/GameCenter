package cn.lt.game.lib.util.redpoints;

import android.content.Context;

import cn.lt.game.application.MyApplication;
import cn.lt.game.bean.RedPointsBean;
import cn.lt.game.lib.util.LogUtils;

/**
 * Created by honaf on 2017/10/30.
 */

public class RedPointsManager {
    public RedPointsBean redPointsBean = new RedPointsBean();
    private static RedPointsManager instance = null;

    public static RedPointsManager getInstance() {
        if (instance == null) {
            instance = new RedPointsManager();
        }
        return instance;
    }

    public boolean isShowMineRed(Context context) {
        LogUtils.e("redpoint==>" + redPointsBean.isMyTask() + redPointsBean.isPlatUpdate() + showFeedback()
                + MyApplication.castFrom(context).getNewGameDownload()
                + MyApplication.castFrom(context).getNewGameUpdate());
        return redPointsBean.isMyTask() || redPointsBean.isPlatUpdate() || showFeedback()
                || MyApplication.castFrom(context).getNewGameDownload()
                || MyApplication.castFrom(context).getNewGameUpdate();
    }

    public boolean showFeedback() {
        return redPointsBean.getFeedbackNum() != 0;
    }
}
