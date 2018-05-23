package cn.lt.game.statistics;

import cn.lt.game.ui.app.adapter.data.PresentData;

/***
 * Created by Administrator on 2015/12/12.
 */
public interface StatisticsDataProductor {


    /**
     * 获取统计相关数据，需要在子类实现。。
     */
    StatisticsEventData produceStatisticsData(PresentData data, String id, String
            pageName, String action, String downloadType, String mark, String srcType, String packageName);

    /**
     * 获取统计相关数据，需要在子类实现。。
     */
    StatisticsEventData produceStatisticsData(String PresentType, int pos, int subPos,
                                              String id, String
                                                      pageName, String action, String downloadType, String mark, String srcType);
}
