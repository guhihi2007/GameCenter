package cn.lt.game.lib.util.file;

import cn.lt.game.global.Constant;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;

/**
 * Created by honaf on 2017/1/8.
 *
 */
public class FeedbackRedUtil {


    public static String getLocalFeedback() {
        Object obj = FileUtil.getDataFromCache(Constant.FEEDBACK_RED +
                (UserInfoManager.instance().isLogin() ? UserInfoManager.instance().getUserInfo().getId() : ""));
        if (obj != null) {
            return (String) obj;
        }
        return "";
    }

    public static void setLocalFeedback(String id) {
        FileUtil.saveDataToCache(Constant.FEEDBACK_RED +
                (UserInfoManager.instance().isLogin() ? UserInfoManager.instance().getUserInfo().getId() : ""), id);
    }


}
