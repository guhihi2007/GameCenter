package cn.lt.game.statistics;

import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.PresentData;

/***
 * Created by Administrator on 2015/12/12.
 */
public class StatisticsDataProductorImpl {

    /**
     * 获取统计相关数据，需要在子类实现。。
     */
    public static StatisticsEventData produceStatisticsData(String presentType, int pos, int subPos,
                                                            String id, String
                                                                    pageName, String action,
                                                            String downloadType, String mark,
                                                            String srcType, String keyword,String...PackageName) {
        StatisticsEventData mStatisticsData = null;
        try {
            mStatisticsData = new StatisticsEventData();
            mStatisticsData.setPresentType(presentType);
            mStatisticsData.setSubPos(subPos);
            mStatisticsData.setPos(pos);
            mStatisticsData.setSrc_id(id);
            mStatisticsData.setPage(pageName);
            mStatisticsData.setActionType(action);
            mStatisticsData.setDownloadType(downloadType);
            mStatisticsData.setRemark(mark);
            mStatisticsData.setSrcType(srcType);
            mStatisticsData.setWord(keyword);
            mStatisticsData.setPackage_name(PackageName.length>0?PackageName[0]:"");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mStatisticsData;
    }


    /**
     * 获取统计相关数据，需要在子类实现。。
     */
    public static StatisticsEventData produceStatisticsData(PresentData data, String id, String
            pageName, String action, String downloadType, String mark, String srcType) {
        StatisticsEventData mStatisticsData = null;
        try {
            mStatisticsData = new StatisticsEventData();
            mStatisticsData.setPresentType(data.getmType().presentType);
            if (data.getmType().presentType.equals(PresentType.hot_words.toString())){
                mStatisticsData.setPos(1);
            }else{
                mStatisticsData.setPos(data.getPos());
            }
            mStatisticsData.setSubPos(data.getSubPos());
            mStatisticsData.setSrc_id(id);
            mStatisticsData.setPage(pageName);
            mStatisticsData.setActionType(action);
            mStatisticsData.setDownloadType(downloadType);
            mStatisticsData.setRemark(mark);
            mStatisticsData.setSrcType(srcType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mStatisticsData;
    }
}
