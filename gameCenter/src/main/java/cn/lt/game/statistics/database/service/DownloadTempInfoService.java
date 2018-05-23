package cn.lt.game.statistics.database.service;

import android.content.Context;

import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.statistics.database.dao.supers.AbstractDao;
import cn.lt.game.statistics.database.dao.supers.AbstractDao.UpdateTableType;
import cn.lt.game.statistics.database.service.supers.AbstractService;
import cn.lt.game.statistics.entity.StatisDownloadTempInfoData;
import cn.lt.game.statistics.manger.YieldReportMgr;

/**
 * 4.0下载临时数据处理类；
 *
 * @author dxx
 */
public class DownloadTempInfoService extends AbstractService<StatisDownloadTempInfoData> {

    public DownloadTempInfoService(Context context) {
        super(context);
    }

    @Override
    public void deleteSingleDataFromDB(StatisDownloadTempInfoData data, AbstractDao<StatisDownloadTempInfoData> dao) {
        try {
            dao.deleteSingleData(data);
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtils.i(LogTAG.DOWNLOAD_REPORT, "DownloadTemInfoService层删除数据异常：" + e.getMessage());
//            YieldReportMgr.self().reportOtherData(data.getmPkgName(), data.getmGameID(), "删除数据异常：" + e.getMessage());
        }
        dao.close();
    }

    @Override
    public void insertSingleDataToDB(StatisDownloadTempInfoData data, AbstractDao<StatisDownloadTempInfoData> dao) {
        try {
            // 插入数据之前先判断是否有该游戏的下载数据；
//            if(null==dao)return;
            StatisDownloadTempInfoData dataDB = dao.requireSingleData(data);
            if (dataDB != null) {
                LogUtils.i(LogTAG.DOWNLOAD_REPORT, "DownloadTemInfoService层更新数据");
                dao.updateSingleData(data, UpdateTableType.add);
            } else {
                LogUtils.i(LogTAG.DOWNLOAD_REPORT, "DownloadTemInfoService层插入数据");
                dao.insertSingleData(data);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtils.i(LogTAG.DOWNLOAD_REPORT, "DownloadTemInfoService层插入或更新数据异常：" + e.getMessage());
//            YieldReportMgr.self().reportOtherData(data.getmPkgName(), data.getmGameID(), "更新/插入数据异常：" + e.getMessage());
        }finally {
            dao.close();
        }
    }

    @Override
    public StatisDownloadTempInfoData getSingleDataFromDB(StatisDownloadTempInfoData data, AbstractDao<StatisDownloadTempInfoData> dao) {
        StatisDownloadTempInfoData tempData = null;
        try {
//            if(null==dao)return null;
            tempData = dao.requireSingleData(data);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(LogTAG.DOWNLOAD_REPORT, "DownloadTemInfoService层查询数据异常：" + e.getMessage());
//            YieldReportMgr.self().reportOtherData(data.getmPkgName(), data.getmGameID(), "查询下载数据异常：" + e.getMessage());
        }finally {
            dao.close();
        }
        return tempData;
    }
}
