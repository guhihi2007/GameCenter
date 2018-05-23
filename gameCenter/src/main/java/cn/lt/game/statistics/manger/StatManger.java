package cn.lt.game.statistics.manger;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import cn.lt.game.BuildConfig;
import cn.lt.game.application.MyApplication;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.lib.util.threadpool.RequestTagManager;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.database.dao.StatisticTempBuilderDao;
import cn.lt.game.statistics.entity.StatisDownloadTempInfoData;

import static cn.lt.game.statistics.ReportEvent.ACTION_INSTALLFAILED;
import static cn.lt.game.statistics.ReportEvent.ACTION_UPDATEINSTALLFAILED;


public class StatManger {
    private Context mContext;
    private String currentData;
    private String currentExposureData;
    private SharedPreferencesUtil spUtil;

    private StatManger() {
    }

    public static StatManger self() {
        return DataCollectMangerHolder.sInstance;
    }

    public void init(Context context) {
        this.mContext = context;
    }

    /**
     * 下载请求调用，保存数据并提交
     *
     * @param data
     */
    public void saveDownloadTempDataAndReportToServer(final StatisDownloadTempInfoData data) {
        if (data == null) {
            return;
        }
        StatisDownloadTempInfoData cloneData = data.clone();
        try {
            StatisticsEventData sData = new StatisticsEventData();
            sData.setActionType(cloneData.getmActionType());
            sData.setPage(cloneData.getmPage());
            sData.setFrom_page(cloneData.getFrom_page());
            sData.setFrom_id(cloneData.getFrom_id());
            sData.setPackage_name(cloneData.getmPkgName());
            sData.setDownload_mode(cloneData.getDownload_mode());
            sData.setSrc_id(cloneData.getmGameID());
            sData.setDownloadType(cloneData.getmDownloadType());
            sData.setPos(cloneData.getPos() == 0 ? 1 : cloneData.getPos());
            sData.setSubPos(cloneData.getSubPos() == 0 ? 1 : cloneData.getSubPos());
            sData.setRemark(cloneData.getmRemark());
            sData.setWord(cloneData.getWord());
            sData.setPage_id(cloneData.getPage_id());
            if (!RequestTagManager.hasTag(mContext, sData.getSrc_id())) {
                saveStatisticDataToDb(sData.getString(), false);
                RequestTagManager.saveRequestTag(mContext, sData.getSrc_id());
            }
        } catch (Throwable e) {
            LogUtils.d(LogTAG.DOWNLOAD_REPORT, "下载统计表保存数据异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                StatisticTempBuilderDao.newInstance(mContext).saveOrUpdateSingleData(cloneData);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.i(LogTAG.DOWNLOAD_REPORT, "下载请求时，插入数据 抛异常");
                YieldReportMgr.self().reportOtherData(data.getmPkgName(), data.getmGameID(), "下载请求时，插入数据抛异常：" + e.getMessage());//很多
            }
        }

    }


    /**
     * 下载完成和下载失败 需要查询统计表数据
     *
     * @param data
     * @param apkPackageName
     */
    public void compareDownloadDataAndReport(StatisDownloadTempInfoData data, boolean downoadFailed, String apkPackageName) {
        StatisticsEventData event = new StatisticsEventData();
        event.setSrc_id(data.getmGameID());
        event.setDownSpeed(data.getDownSpeed());
        event.setPackage_name(apkPackageName);
        event.setRemark(data.getmRemark()); //注意：取库需要拿下发包名，上报需拿apk实际包名
        StatisDownloadTempInfoData game = null;
        try {
            game = StatisticTempBuilderDao.newInstance(mContext).quarySingleBuildData(data);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(LogTAG.DOWNLOAD_REPORT, "下载完成或失败时，查询数据 抛异常");
            YieldReportMgr.self().reportOtherData(data.getmPkgName(), data.getmGameID(), "下载完成或失败时，查询数据 抛异常" + e.getMessage());//很多
        }
        if (game != null) {
            String downloadType = game.getmDownloadType();
            String isUpdate = Constant.DOWNLOAD_TYPE_AUTO_UPDATE.equals(downloadType) ? Constant.STATE_UPDATE : game.getIsupdate();
            if (downoadFailed) {
                event.setActionType(Constant.STATE_UPDATE.equals(isUpdate) ? ReportEvent.ACTION_UPDATEDOWNLOADFAILED : ReportEvent.ACTION_DOWNLOADFAILED);
            } else {
                event.setActionType(Constant.STATE_UPDATE.equals(isUpdate) ? ReportEvent.ACTION_UPDATEDOWNLOADED : ReportEvent.ACTION_DOWNLOADED);
            }
            event.setPos(game.getPos());
            event.setSubPos(game.getSubPos());
            event.setDownloadType(downloadType);
            event.setDownload_mode(game.getDownload_mode());
            event.setPage(TextUtils.isEmpty(game.getmPage()) ? MyApplication.application.mCurrentPage : game.getmPage());
            event.setPage_id(game.getPage_id());
            event.setFrom_id(game.getFrom_id());
            event.setFrom_page(game.getFrom_page());
            event.setWord(game.getWord());
        } else {
            event.setActionType(ReportEvent.ACTION_DOWNLOADED);
            LogUtils.i(LogTAG.DOWNLOAD_REPORT, "下载完成查询统计表数据为空");
            YieldReportMgr.self().reportOtherData(data.getmPkgName(), data.getmGameID(), "下载完成或失败，查询统计表数据为空");//最多
        }
        saveStatisticDataToDb(event.getString(), false);
    }


    /**
     * 安装成功和安装失败 数据调用的公共类
     *
     * @param data
     * @param isInstallSuccess
     */
    public void compareInstallDataAndReport(StatisDownloadTempInfoData data, StatisticsEventData sData, boolean isInstallSuccess) {
        if (data != null) {
            String downloadType = data.getmDownloadType();
            String isUpdate = Constant.DOWNLOAD_TYPE_AUTO_UPDATE.equals(downloadType) ? Constant.STATE_UPDATE : data.getIsupdate();
            if (isInstallSuccess) {
                sData.setActionType(Constant.STATE_UPDATE.equals(isUpdate) ? ReportEvent.ACTION_UPDATEINSTALLSUCCESS : ReportEvent.ACTION_INSTALLSUCCESS);
            } else {
                sData.setActionType(Constant.STATE_UPDATE.equals(isUpdate) ? ACTION_UPDATEINSTALLFAILED : ACTION_INSTALLFAILED);
            }
            sData.setDownSpeed(data.getDownSpeed());
            sData.setPage(data.getmPage());
            sData.setPos(data.getPos());
            sData.setSubPos(data.getSubPos());
            sData.setFrom_id(data.getFrom_id());
            sData.setFrom_page(data.getFrom_page());
            sData.setWord(data.getWord());
            sData.setDownloadType(downloadType);
            sData.setDownload_mode(data.getDownload_mode());
            sData.setInstall_type(data.getInstall_type());
            sData.setInstall_mode(data.getInstall_mode());
            sData.setSrc_id(data.getmGameID());
            sData.setPackage_name(TextUtils.isEmpty(sData.getPackage_name()) ? data.getmPkgName() : sData.getPackage_name());
            sData.setRemark(sData.getRemark());
        } else {
            sData.setActionType(ReportEvent.ACTION_INSTALLSUCCESS);
            sData.setPackage_name(sData.getPackage_name());
            sData.setRemark("other_market");
        }
        if (sData != null) {
            saveStatisticDataToDb(sData.getString(), false);
        }

    }

    /**
     * @param data  安装请求时调用,同时保存安装数据
     * @param sData
     */
    public void reportInstallRequestAndSaveData(final StatisDownloadTempInfoData data, final StatisticsEventData sData) {
        StatisDownloadTempInfoData downloadTempInfoData = null;
        try {
            try {
                downloadTempInfoData = StatisticTempBuilderDao.newInstance(mContext).quarySingleBuildData(data);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.i(LogTAG.DOWNLOAD_REPORT, "安装请求时，查询数据 抛异常");
                YieldReportMgr.self().reportOtherData(data.getmPkgName(), data.getmGameID(), "安装请求时，查询数据 抛异常：" + e.getMessage());//很多
            }
            /*上报数据部分*/
            if (downloadTempInfoData != null) {
                sData.setSrc_id(data.getmGameID());
                String downloadType = downloadTempInfoData.getmDownloadType();
                sData.setDownloadType(downloadType);
                sData.setDownload_mode(downloadTempInfoData.getDownload_mode());
                sData.setFrom_page(downloadTempInfoData.getFrom_page());
                sData.setPage(MyApplication.application.mCurrentPage);  //拿当前页面
                sData.setPos(downloadTempInfoData.getPos());
                sData.setSubPos(downloadTempInfoData.getSubPos());
                String isUpdate = Constant.DOWNLOAD_TYPE_AUTO_UPDATE.equals(downloadType) ? Constant.STATE_UPDATE : downloadTempInfoData.getIsupdate();
                sData.setActionType(Constant.STATE_UPDATE.equals(isUpdate) ? ReportEvent.ACTION_UPDATE_INSTALL_REQUEST : ReportEvent.ACTION_INSTALL_REQUEST);
            } else {
                LogUtils.i(LogTAG.DOWNLOAD_REPORT, "安装请求上报：下载信息数据为空不上报安装请求数据");
                YieldReportMgr.self().reportOtherData(data.getmPkgName(), data.getmGameID(), "安装请求时，查询数据为空");//最多
            }
            saveStatisticDataToDb(sData.getString(), false);
        } catch (Exception e) {
            LogUtils.i(LogTAG.DOWNLOAD_REPORT, "安装请求上报异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            /*存库部分*/
            if (downloadTempInfoData != null) {
                int installCount = downloadTempInfoData.getInstall_count();
                long currentTime = System.currentTimeMillis();
                String isUpdate = Constant.DOWNLOAD_TYPE_AUTO_UPDATE.equals(downloadTempInfoData.getmDownloadType()) ? Constant.STATE_UPDATE : downloadTempInfoData.getIsupdate();
                downloadTempInfoData.setmGameID(data.getmGameID());
                downloadTempInfoData.setmPreState(data.getmPreState());

                downloadTempInfoData.setmPkgName(sData.getPackage_name());
                downloadTempInfoData.setmActionType(sData.getActionType());
                downloadTempInfoData.setmRemark(sData.getRemark());
                downloadTempInfoData.setInstall_type(sData.getInstall_type());
                downloadTempInfoData.setInstall_mode(sData.getInstall_mode());

                downloadTempInfoData.setmDownloadType(downloadTempInfoData.getmDownloadType());
                downloadTempInfoData.setmPage(downloadTempInfoData.getmPage());    //安装完成或失败拿下载请求的页面
                downloadTempInfoData.setPos(downloadTempInfoData.getPos());
                downloadTempInfoData.setSubPos(downloadTempInfoData.getSubPos());
                downloadTempInfoData.setDownload_mode(downloadTempInfoData.getDownload_mode());
                downloadTempInfoData.setInstall_count(++installCount);
                downloadTempInfoData.setFrom_page(downloadTempInfoData.getFrom_page());
                downloadTempInfoData.setFrom_id(downloadTempInfoData.getFrom_id());
                downloadTempInfoData.setWord(downloadTempInfoData.getWord());
                downloadTempInfoData.setInstall_time(currentTime);
                downloadTempInfoData.setIsupdate(isUpdate);
                StatManger.self().update(downloadTempInfoData);  //更新数据必须全部是最新的
            }
        }
    }

    /**
     * 保存统计数据入口
     *
     * @param data
     * @param isExposureData
     */
    public void saveStatisticDataToDb(final String data, boolean isExposureData) {
        if (isExposureData) {
            if (!Constant.EXPOSURE_TOGGLE) {
                return;
            }
        }
        if (isExposureData) {
            if (data.equals(currentExposureData)) return;
            currentExposureData = data;
        } else {
            if (data.equals(currentData)) {
                AppUtils.saveLog("数据过滤出现重复数据，不上报数据:" + "currentData:" + currentData + ",data:" + data);
                LogUtils.i("GOOD", "数据过滤出现重复数据，不上报数据:" + "currentData:" + currentData + ",data:" + data);
                return;
            } else {
                currentData = data;
            }
        }
        YieldReportMgr.self().saveReportData(mContext, data);
    }

    /**
     * 查库，供外界调用---提供包名
     *
     * @param packageName
     */
    public StatisDownloadTempInfoData queryDBbyPackageName(final String packageName) {
        try {
            StatisDownloadTempInfoData game = StatisticTempBuilderDao.newInstance(mContext).quarySingleBuildData(new StatisDownloadTempInfoData().setmPkgName(packageName));
            if (game != null) {
                return game;
            } else {
                YieldReportMgr.self().reportOtherData(packageName, "", "queryDBbyPackageName时，查询数据 为空：注：这个可能是正常情况");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(LogTAG.DOWNLOAD_REPORT, "queryDBbyPackageName时，查询数据 抛异常");
            YieldReportMgr.self().reportOtherData(packageName, "", "queryDBbyPackageName时，查询数据 抛异常：" + e.getMessage());//很多
        }
        return null;
    }

    /**
     * 移除库，供外界调用---提供包名
     *
     * @param gameID
     */
    public void removeStaticByGameID(final String gameID) {
        try {
//            StatisDownloadTempInfoData tempData = new StatisDownloadTempInfoData().setmGameID(gameID);
//            StatisticTempBuilderDao.newInstance(mContext).deleteSingleBuildData(tempData);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(LogTAG.DOWNLOAD_REPORT, "removeStaticByGameID 时，查询数据 抛异常");
        }
    }

    /**
     * 保存安装请求的数据
     *
     * @param data
     */
    public void update(StatisDownloadTempInfoData data) {
        if (data == null) return;
        try {
            StatisticTempBuilderDao.newInstance(mContext).updateSingleBuildData(data);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(LogTAG.DOWNLOAD_REPORT, "update 时，插入或更新数据 抛异常");
            YieldReportMgr.self().reportOtherData(data.getmPkgName(), data.getmGameID(), "update 时，插入或更新数据 抛异常：" + e.getMessage());
        }
    }

    /***
     * 释放
     */
    public void releaseDatabase() {
        StatisticTempBuilderDao.newInstance(mContext).releaseDataBase();
    }

    public static class DataCollectMangerHolder {
        public static StatManger sInstance = new StatManger();
    }
}
