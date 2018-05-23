package cn.lt.game.lib.util.file;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import cn.lt.game.bean.SyncPointsBean;
import cn.lt.game.global.Constant;

/**
 * Created by honaf on 2017/1/8.
 *
 */
public class SyncPointsUtil {


    public static int getLocalTotalPoints() {
        ArrayList<SyncPointsBean> list = new ArrayList<>();
        Object obj = FileUtil.getDataFromCache(Constant.SYNC_POINTS);
        if (obj != null) {
            list = (ArrayList<SyncPointsBean>) obj;
        }
        int totalPoints = 0;
        for (int i = 0; i < list.size(); i++) {
            totalPoints += list.get(i).getPoints();
        }
        return totalPoints;
    }

    public static ArrayList<SyncPointsBean> getSyncPointsList() {
        ArrayList<SyncPointsBean> list = new ArrayList<>();
        Object obj = FileUtil.getDataFromCache(Constant.SYNC_POINTS);
        if (obj != null) {
            list = (ArrayList<SyncPointsBean>) obj;
        }
        return list;
    }

    public static void setSyncPointsList(ArrayList<SyncPointsBean> list) {
        FileUtil.saveDataToCache(Constant.SYNC_POINTS,list);
    }


    public static String getLocalPointsListJson() {
        ArrayList<SyncPointsBean> list = new ArrayList<>();
        Object obj = FileUtil.getDataFromCache(Constant.SYNC_POINTS);
        if (obj != null) {
            list = (ArrayList<SyncPointsBean>) obj;
        }
        return syncPointsToJson(list);
    }

    private static String syncPointsToJson(ArrayList<SyncPointsBean> list) {
        if (null != list && list.size() != 0) {
            Gson gs = new Gson();
            return gs.toJson(list, new TypeToken<ArrayList<SyncPointsBean>>() {
            }.getType());
        }
        return "";
    }


}
