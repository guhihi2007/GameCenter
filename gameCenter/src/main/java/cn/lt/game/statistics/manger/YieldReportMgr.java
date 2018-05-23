package cn.lt.game.statistics.manger;

import android.content.Context;
import android.system.Os;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.application.MyApplication;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.database.dao.RealReportDataContainerDao;
import cn.lt.game.threadPool.ThreadPoolProxyFactory;

import static cn.lt.game.statistics.manger.YieldReportMgr.YieldReportMgrHolder.sInstance;


/**
 * @author chengyong
 * @time 2017/7/24 14:35
 * @des ${数据上报轮询机制}
 */

public class YieldReportMgr {

    private boolean isSaving;
    private boolean isReporting;
    private String currentData = "";

    public static class YieldReportMgrHolder {
        public static YieldReportMgr sInstance ;
        static {
            sInstance = new YieldReportMgr();
        }
    }

    public static YieldReportMgr self() {
        return YieldReportMgrHolder.sInstance;
    }
    private YieldReportMgr(){

    }

    /**
     * 生产统计数据(多个线程生产)
     *
     * @param data
     */
    public void saveReportData(final Context context, final String data) {
        ThreadPoolProxyFactory.getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {//核心线程数int最大值，可满足统计存储数据的数量
                LogUtils.e(LogTAG.RC, "saveReportData:=before synchronized=>" + data);
                synchronized (sInstance) {
                    try {
                        isSaving = true;
                        LogUtils.i(LogTAG.RC, "存数据:=" + data);
                        AppUtils.saveLog(" insert data:" + data);
                        RealReportDataContainerDao.getInstance(context).insertSingleData(data);
                        LogUtils.i(LogTAG.RC, "唤醒去 上报数据->");
                        isSaving = false;
                        sInstance.notifyAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                        AppUtils.saveLog(" insert data failed:" + data);
                        LogUtils.i("GOOD", "保存上报数据异常,不上报数据:=" + data);
                    } finally {
                        isSaving = false;
                        sInstance.notifyAll();
                    }
                }
            }
        });
    }

    /**
     * 数据上报初始化入口（looper）--一个线程消费
     *
     * @param context
     */
    public void postReportData(final Context context) {
        ThreadPoolProxyFactory.getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                List<String> containerList;
                while (true) {
                    LogUtils.d(LogTAG.RC, "report(looper)：i am running!");
                    synchronized (sInstance) {
                        try {
                            Thread.sleep(1000);
                            if (isSaving) {
                                LogUtils.d(LogTAG.RC, "report：在saving...等300ms自动唤醒");
                                sInstance.wait(300);
                            }
                            if (!NetUtils.isConnected(context)) {
                                continue;
                            }
                            isReporting = true;
                            containerList = RealReportDataContainerDao.getInstance(context).requireAll();
                            if (containerList.size() >= 1) {
                                if (currentData.equals(containerList.get(0))) {
//                                    LogUtils.d("GOOD", "遍历太快，数据一样，重新扫描...");
                                    continue;
                                }
                                currentData = containerList.get(0);
                                yieldReportSingleData(containerList.get(0), context);
                            } else {
                                isReporting = false;
                                sInstance.notifyAll();
                                LogUtils.i("GOOD", "数据库中无数据上报，唤醒去存，轮询休眠中......");
                                sInstance.wait(10000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private synchronized void yieldReportSingleData(final String data, final Context context) {
        Map<String, String> params = new HashMap<>();
        params.put("data", data);
        params.put("source", "game_center2");
        /**
         * 请求网络
         */
        Net.instance().executeReportData(Host.HostType.DCENTER_HOST, "", params, new WebCallBackToString() {
            @Override
            public void onSuccess(String result) {
                LogUtils.i("GOOD", "上报成功->" + data);
                try {
                    AppUtils.saveLog(" report data success:" + data);
                    RealReportDataContainerDao.getInstance(context).deleteSingleData(data);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.i(LogTAG.DOWNLOAD_REPORT, "上报成功，删除final库失败" + e.getMessage());
                    YieldReportMgr.self().reportOtherData("", "", "上报成功，删除final库失败" + e.getMessage());
                    try {
                        RealReportDataContainerDao.getInstance(context).deleteSingleData(data);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        LogUtils.i(LogTAG.DOWNLOAD_REPORT, "再次删除--final库失败" + e1.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                AppUtils.saveLog(" report data failed:" + data);
                LogUtils.e("GOOD", "上报失败->" + statusCode + error.getMessage() + "==" + (data));
                currentData = "";
            }
        });
        LogUtils.d(LogTAG.RC, "上报回调回来，唤醒去存数据->");
        isReporting = false;
        sInstance.notifyAll();
    }

    /**
     * 分析数据
     *
     * @param packageName
     * @param gameId
     * @param remark
     */
    public void reportOtherData(final String packageName, String gameId, String remark) {
        try {
            StatisticsEventData statiData = new StatisticsEventData();
            statiData.setActionType(ReportEvent.ACTION_ANALYSIS);
            statiData.setPackage_name(packageName);
            statiData.setSrc_id(gameId);
            statiData.setRemark(remark);
            final String finalData = statiData.getString();
            if (TextUtils.isEmpty(finalData)) return;

            Map<String, String> params = new HashMap<>();
            params.put("data", finalData);
            params.put("source", "game_center2");
            /**
             * 请求网络
             */
            Net.instance().executePost(Host.HostType.DCENTER_HOST, "", params, new WebCallBackToString() {
                @Override
                public void onSuccess(String result) {
                    LogUtils.d("BAD", "上报成功-catch_message->" + finalData);
                }

                @Override
                public void onFailure(int statusCode, Throwable error) {
                    LogUtils.d("BAD", "上报失败-catch_message->" + statusCode + error.getMessage() + "==" + (finalData));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
