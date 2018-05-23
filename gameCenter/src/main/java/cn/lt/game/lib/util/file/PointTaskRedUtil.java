package cn.lt.game.lib.util.file;

import java.util.ArrayList;

import cn.lt.game.bean.GameInfoBean;
import cn.lt.game.global.Constant;

/**
 * Created by honaf on 2017/1/8.
 *
 */
public class PointTaskRedUtil {



    public static ArrayList<GameInfoBean> getLocalTaskList() {
        ArrayList<GameInfoBean> list = new ArrayList<>();
        Object obj = FileUtil.getDataFromCache(Constant.TASK_POINTS);
        if (obj != null) {
            list = (ArrayList<GameInfoBean>) obj;
        }
        return list;
    }

    public static void setLocalTaskList(ArrayList<GameInfoBean> list) {
        FileUtil.saveDataToCache(Constant.TASK_POINTS,list);
    }


}
