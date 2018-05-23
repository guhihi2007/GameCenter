package cn.lt.game.statistics.manger;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import cn.lt.game.application.MyApplication;
import cn.lt.game.bean.GameInfoBean;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.download.DownloadState;
import cn.lt.game.global.Constant;
import cn.lt.game.install.InstallState;
import cn.lt.game.install.autoinstaller.AutoInstallerContext;
import cn.lt.game.lib.util.CheckIsApkFile;
import cn.lt.game.lib.util.FromPageManager;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.threadpool.RequestTagManager;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.entity.StatisDownloadTempInfoData;


/***
 * Created by Administrator on 2015/12/28.
 */
public class DCStat {
    /**
     * 页面浏览
     *
     * @param data
     */
    public static void pageJumpEvent(StatisticsEventData data) {
        try {
            String lastPage = "";
            if (data != null && !TextUtils.isEmpty(data.getPage())) {
                if (!data.getPage().equals(Constant.PAGE_INDEX_NECESSARY)) {
                    MyApplication.application.mCurrentPage = data.getPage();
                    FromPageManager.setLastPage(data.getPage());
                    FromPageManager.setLastPageId(data.getPage(), data.getSrc_id());
                } //当前页面与上级页面 保存到内存(精选必玩不存)
                //如果frompage 或者 currPage 来自于自动匹配、搜索结果；则需上报word
                if (FromPageManager.setWordByPage(data.getPage())) {
                    data.setWord(MyApplication.application.mCurrentWord);
                }
                lastPage = FromPageManager.getLastPage();
                data.setFrom_page(lastPage);
                //专题详情、游戏详情、分类页面、分类列表、Float、礼包详情--活动相关页
                setFromId(data, null, lastPage);
                StatManger.self().saveStatisticDataToDb(data.getString(), false);
            }
        } catch (Exception e) {
            LogUtils.e("pageJumpEvent failed: " + e.toString());
        }
    }

    /**
     * @param data
     * @param tempInfoData
     * @param lastPage     //专题详情、游戏详情、分类页面、分类列表、Float、礼包详情--活动相关页
     */
    private static void setFromId(StatisticsEventData data, StatisDownloadTempInfoData tempInfoData, String lastPage) {
        if (lastPage.equals(Constant.PAGE_GAME_DETAIL) || lastPage.equals(Constant.PAGE_SUBJECT_DETAIL) || lastPage.equals(Constant.PAGE_CATEGORY_HOT) || lastPage.equals(Constant.PAGE_CATEGORY_LIST) || Constant.PAGE_FLOAT.equals(lastPage) || Constant.PAGE_GIFT_DETAIL.equals(lastPage) || Constant.PAGE_CHOUJIANG.equals(lastPage) || Constant.PAGE_JIFENG.equals(lastPage) || Constant.PAGE_ZHONGJIANG.equals(lastPage) || Constant.PAGE_JIFEN_RECORD.equals(lastPage) || Constant.PAGE_HISTORY.equals(lastPage)) {
            LogUtils.e("juice", "上报浏览时，设置fromId" + FromPageManager.getLastPageId());
            if (data != null) {
                data.setFrom_id(FromPageManager.getLastPageId());
            }
            if (tempInfoData != null) {
                tempInfoData.setFrom_id(FromPageManager.getLastPageId());
            }
        }
    }

    /**
     * 上报点击事件（包括暂停、继续等有关下载的点击）
     *
     * @param data
     */
    public static void clickEvent(StatisticsEventData data) {
        try {
            if ((data != null) && !TextUtils.isEmpty(data.getActionType())) {
                if (!Constant.AUTO.equals(data.getDownloadType()) && !Constant.DOWNLOAD_TYPE_AUTO_UPDATE.equals(data.getDownloadType()) && !Constant.DOWNLOAD_TYPE_AUTO_COVER.equals(data.getDownloadType())) {
                    LogUtils.i("Erosion", "clickEvent");
                    data.setDownloadType(Constant.RETRY_TYPE_MANUAL);  //默认为manaul
                }
                //取库
//                StatisDownloadTempInfoData DBData = StatManger.self().queryDBbyPackageName(data.getPackage_name());  //query data
//                if (DBData != null) {
//                    data.setPresentType(DBData.getPresenType());
//                    cn.lt.game.lib.util.LogUtils.d("kkk", "not null==" + DBData.getPresenType());
//                }
                data.setPresentType(data.getPresentType());
                if (Constant.PAGE_GAME_DETAIL_RECOMMEND.equals(data.getPage())) {
                    data.setPage(Constant.PAGE_GAME_DETAIL_RECOMMEND);  //  详情页面推荐
                } else {
                    data.setPage(MyApplication.application.mCurrentPage);  //页面取实时的
                }

                data.setPage_id(data.getPage_id());
                if (FromPageManager.setWordByPage(data.getPage())) {
                    data.setWord(MyApplication.application.mCurrentWord);
                }

                StatManger.self().saveStatisticDataToDb(data.getString(), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void searchEvent(String key) {
        try {
            if (!TextUtils.isEmpty(key)) {
                StatisticsEventData data = new StatisticsEventData();
                data.setActionType(ReportEvent.ACTION_SEARCH);
                data.setRemark(key);
                StatManger.self().saveStatisticDataToDb(data.getString(), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上报下载请求，暂停，继续，重试
     *
     * @param game
     * @param pageName
     * @param eventData     只是为了传位置
     * @param isOrderClick
     * @param download_mode
     * @param download_type
     * @param isFromUpGrade
     * @param pos
     */
    public static synchronized void downloadRequestEvent(GameBaseDetail game, String pageName, StatisticsEventData eventData, boolean isOrderClick, String download_mode, String download_type, boolean isFromUpGrade, int pos) {
        StatisDownloadTempInfoData downloadTempInfoData = null;
        try {
            LogUtils.i("DownloadReport", "downloadRequestEvent CurrentThreadName:" + Thread.currentThread().getName());
            if (game != null) {
                String page = pageName;
                String lastPage;
                String temPageName = MyApplication.application.mCurrentPage;
                downloadTempInfoData = game.getDownloadTempInfo();
                downloadTempInfoData.setDownload_mode(download_mode);
                downloadTempInfoData.setmRemark(game.mRemark);
                downloadTempInfoData.setmPage(TextUtils.isEmpty(page) ? temPageName : page);
                if (eventData != null && !Constant.PAGE_FLOAT.equals(pageName)) {
                    downloadTempInfoData.setPos(eventData.getPos());
                    downloadTempInfoData.setSubPos(eventData.getSubPos());
                    downloadTempInfoData.setPresenType(eventData.getPresentType());
                } else {
                    downloadTempInfoData.setPos(pageName.contains(Constant.PAGE_HOT) ? pos : 1);//是否来自于热点页面
                    downloadTempInfoData.setSubPos(1);
                }
                lastPage = FromPageManager.getLastPage();
                if (Constant.DOWNLOAD_TYPE_AUTO_COVER.equals(download_type)) {
                    lastPage = "";
                }
                downloadTempInfoData.setPage_id(game.pageId);
                downloadTempInfoData.setFrom_page(lastPage);
                if (Constant.PAGE_FLOAT.equals(pageName)) {
                    downloadTempInfoData.setFrom_page(pageName);
                    if (eventData != null) {
                        downloadTempInfoData.setFrom_id(eventData.getFrom_id());
                        downloadTempInfoData.setmPage(eventData.getPage());
                    }
                }
                setFromId(null, downloadTempInfoData, lastPage);
                if (FromPageManager.setWordByPage(temPageName)) {
                    downloadTempInfoData.setWord(MyApplication.application.mCurrentWord);
                }
                switch (game.getState()) {
                    case DownloadState.undownload:
                        LogUtils.d("DCStat", "DownloadState.undownload");
                        // 用户主动触发"下载请求"事件；
                        LogUtils.d("DCStat", "isFromUpGrade==========" + isFromUpGrade);
                        if (isFromUpGrade) {
                            downloadTempInfoData.setIsupdate(Constant.STATE_UPDATE);
                            downloadTempInfoData.setmActionType(ReportEvent.ACTION_UPDATEDOWNLOADREQUEST).setmDownloadType(download_type);
                        } else {
                            downloadTempInfoData.setIsupdate(Constant.STATE_NORMAL);
                            downloadTempInfoData.setmActionType(ReportEvent.ACTION_DOWNLOADREQUEST).setmDownloadType(download_type);
                        }
                        break;
                    case InstallState.upgrade:
                        LogUtils.d("DCStat", "InstallState.upgrade" + pageName);
                        //升级下载请求存本地，
                        downloadTempInfoData.setIsupdate(Constant.STATE_UPDATE);
                        downloadTempInfoData.setmActionType(ReportEvent.ACTION_UPDATEDOWNLOADREQUEST).setmDownloadType(download_type);   // by ATian
                        break;
                    case DownloadState.downloadFail:
                        //原生控件点击重试 在点击listener已经上报
//                        if (pageName.contains(Constant.PAGE_HOT)) {//是否来自于热点页面
//                            clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, pageName, downloadTempInfoData.getPos(), downloadTempInfoData.getPresenType(), downloadTempInfoData.getSubPos(), downloadTempInfoData.getmGameID(), downloadTempInfoData.getmRemark(), download_type, "downRetry", downloadTempInfoData.getmPkgName(),game.pageId));
//                        }
                        downloadTempInfoData = null;
                        break;
                    case InstallState.installFail:
                        LogUtils.d("DCStat", "InstallState.installFail, DownloadState.downloadFail");
                        clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, pageName, downloadTempInfoData.getPos(), downloadTempInfoData.getPresenType(), downloadTempInfoData.getSubPos(), downloadTempInfoData.getmGameID(), downloadTempInfoData.getmRemark(), download_type, "downRetry", downloadTempInfoData.getmPkgName(), game.pageId));
                        downloadTempInfoData = null;
                        break;
                    case DownloadState.waitDownload:
                        LogUtils.d("DCStat", "DownloadState.waitDownload");
                        clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, pageName, downloadTempInfoData.getPos(), downloadTempInfoData.getPresenType(), downloadTempInfoData.getSubPos(), downloadTempInfoData.getmGameID(), downloadTempInfoData.getmRemark(), download_type, "downStop", downloadTempInfoData.getmPkgName(), game.pageId));
                        downloadTempInfoData = null;
                        break;
                    case DownloadState.downloadPause:
                        LogUtils.d("DCStat", "DownloadState.downloadPause, DownloadState.waitDownload");
                        if (!isOrderClick) {
                            clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, pageName, downloadTempInfoData.getPos(), downloadTempInfoData.getPresenType(), downloadTempInfoData.getSubPos(), downloadTempInfoData.getmGameID(), downloadTempInfoData.getmRemark(), download_type, "downContinue", downloadTempInfoData.getmPkgName(), game.pageId));
                        }
                        downloadTempInfoData = null;
                        break;
                    case DownloadState.downInProgress:
                        LogUtils.d("DCStat", "DownloadState.downInProgress");
                        clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, pageName, downloadTempInfoData.getPos(), downloadTempInfoData.getPresenType(), downloadTempInfoData.getSubPos(), downloadTempInfoData.getmGameID(), downloadTempInfoData.getmRemark(), download_type, "downStop", downloadTempInfoData.getmPkgName(), game.pageId));
                        downloadTempInfoData = null;
                        break;
                    case DownloadState.downloadComplete:
                    case InstallState.install:
                    case InstallState.installing:
                        LogUtils.d("DCStat", "DownloadState.downloadComplete, InstallState.install, " + "InstallState.installing");
                        downloadTempInfoData = null;
                        break;
                    case InstallState.installComplete:
                        downloadTempInfoData = null;
                        break;
                    case InstallState.ignore_upgrade:
                        LogUtils.d("DCStat", "InstallState.installComplete, InstallState" + ".ignore_upgrade");
                        //升级下载请求存本地，
                        downloadTempInfoData.setIsupdate(Constant.STATE_UPDATE);
                        // 统计用户主动触发"升级请求"事件；
                        downloadTempInfoData.setmActionType(ReportEvent.ACTION_UPDATEDOWNLOADREQUEST).setmDownloadType(download_type);
                        break;
                    default:
                        LogUtils.d("DCStat", "default");
                        break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            YieldReportMgr.self().reportOtherData(game.getPkgName(), game.getId() + "", "下载请求组装数据异常：" + e.getMessage());
        } finally {
            long t1 = System.currentTimeMillis();
            if (downloadTempInfoData != null) {
                StatManger.self().saveDownloadTempDataAndReportToServer(downloadTempInfoData);
                LogUtils.i("GOOD", "保存耗时:" + (System.currentTimeMillis() - t1) + "毫秒");
            }
        }
    }

    /***
     * 下载完成
     *  @param game
     * @param networkType
     * @param downSpeed
     * @param downPath
     */
    public static synchronized void downloadCompletedEvent(GameBaseDetail game, String networkType, String downSpeed, String downPath) {
        try {
            if (game != null) {
                LogUtils.i("DownloadReport", "downloadCompletedEvent CurrentThreadName:" + Thread.currentThread().getName());
                StatisDownloadTempInfoData data = game.getDownloadTempInfo().clone();
                data.setmActionType(ReportEvent.ACTION_DOWNLOADED);//这是默认，实际还得取库
                data.setNetworkType(networkType);
                data.setmPreState(DownloadState.downloadComplete);
                data.setDownSpeed(downSpeed);
                data.setmGameID(String.valueOf(game.getId()));
                data.setPage_id(game.pageId);
                //通过apk拿包名,如果是被劫持的下载完成 拿不到统计数据
                String packageName = CheckIsApkFile.getPackageNameByDownPath(downPath, MyApplication.application);
                String apkPackageName = TextUtils.isEmpty(packageName) ? game.getPkgName() : packageName;
                data.setmPkgName(game.getPkgName());//用于查库
                StatManger.self().compareDownloadDataAndReport(data, false, apkPackageName); //下载完成
                RequestTagManager.deleteRequestTag(MyApplication.application, data.getmGameID());
            }
        } catch (Throwable e) {
            YieldReportMgr.self().reportOtherData(game.getPkgName(), game.getId() + "", "下载完成组装数据异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized void downloadFialedEvent(GameBaseDetail game, String message) {
        try {
            if (game != null) {
                LogUtils.i("DownloadReport", "downloadFialedEvent CurrentThreadName:" + Thread.currentThread().getName());
                StatisDownloadTempInfoData data = game.getDownloadTempInfo();
                data.setmActionType(ReportEvent.ACTION_DOWNLOADFAILED);
                data.setmGameID(data.getmGameID());
                data.setmRemark(message);
                data.setmPkgName(game.getPkgName());//用于查库
                data.setPage_id(data.getPage_id());
                data.setFrom_page(FromPageManager.getLastPage());
                LogUtils.i("Erosion", "from:" + data.getFrom_page() + ",page:" + FromPageManager.getLastPage());
                StatManger.self().compareDownloadDataAndReport(data, true, game.getPkgName()); //下载失败
            }
        } catch (Exception e) {
//            YieldReportMgr.self().reportOtherData(game.getPkgName(), game.getId() + "", "下载失败组装数据异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 普通安装请求 升级安装请求
     *
     * @param game
     * @param install_type
     */
    public static synchronized void installRequest(GameBaseDetail game, String install_mode, String install_type) {
        try {
            if (game != null) {
                LogUtils.i("DownloadReport", "installRequest CurrentThreadName:" + Thread.currentThread().getName());
                StatisDownloadTempInfoData data = game.getDownloadTempInfo();
                StatisticsEventData sData = new StatisticsEventData();
                sData.setActionType(ReportEvent.ACTION_INSTALL_REQUEST);  //默认
                sData.setInstall_mode(install_mode);
                sData.setInstall_type(install_type);
                setRealPackageNameForInstall(game, sData);
                sData.setPage_id(game.pageId);
                sData.setRemark(TextUtils.isEmpty(game.mRemark) ? "" : game.mRemark);
                sData.setAutoInstall(AutoInstallerContext.getInstance().getAccessibilityStatus() == AutoInstallerContext.STATUS_OPEN);
                data.setmPkgName(game.getPkgName());
                StatManger.self().reportInstallRequestAndSaveData(data, sData);//第一个参数用于查，第二个用于上报
                RequestTagManager.deleteRequestTag(MyApplication.application, data.getmGameID());
            }
        } catch (Exception e) {
//            YieldReportMgr.self().reportOtherData(game.getPkgName(), game.getId() + "", "安装请求组装数据异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /***
     * 根据包名查询安装次数
     * @param pkgName
     * @return
     */
    public static synchronized int getInstallCount(String pkgName) {
        StatisDownloadTempInfoData tempInfoData = null;
        try {
            tempInfoData = StatManger.self().queryDBbyPackageName(pkgName);
            if (tempInfoData != null) {
                return tempInfoData.getInstall_count();
            }
        } catch (Exception e) {
//            YieldReportMgr.self().reportOtherData(pkgName, "", "获取安装次数数据异常：" + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /***
     * 查询安装时间
     * @param pkgName
     * @return
     */
    public static synchronized long getInstallTime(String pkgName) {
        StatisDownloadTempInfoData tempInfoData = null;
        try {
            tempInfoData = StatManger.self().queryDBbyPackageName(pkgName);
            if (tempInfoData != null) {
                return tempInfoData.getInstall_time();
            }
        } catch (Exception e) {
//            YieldReportMgr.self().reportOtherData(pkgName, "", "查询安装时间数据异常：" + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /***
     * 根据包名上报第三方应用市场安装完成数据或者 广播完成的补报。
     * 安装成功事件
     * @param pkgName
     */
    public static synchronized void installSuccess(final String pkgName, StatisDownloadTempInfoData data) {
        try {
            if (data != null) {
                data.setmPkgName(pkgName).setmPreState(InstallState.installComplete);
            }
            LogUtils.i("DownloadReport", "installSuccess CurrentThreadName:" + Thread.currentThread().getName());
            StatisticsEventData sData = new StatisticsEventData();
            sData.setActionType((ReportEvent.ACTION_INSTALLSUCCESS));  //默认
            sData.setRemark(null != data ? data.getmRemark() : "");
            sData.setPackage_name(pkgName);
            sData.setPage_id(null != data ? data.getPage_id() : "");
            sData.setAutoInstall(AutoInstallerContext.getInstance().getAccessibilityStatus() == AutoInstallerContext.STATUS_OPEN);
            StatManger.self().compareInstallDataAndReport(data, sData, true); //安装成功
//            /*如果是从非游戏中心安装的则不删除统计数据，防止在覆盖白名单执行上过中数据丢失*/
            if (data != null) {
                LogUtils.e("ScreenMonitorService", "非第三方安装成功，删除记录");
                StatManger.self().removeStaticByGameID(data.getmGameID()); //移除库
                RequestTagManager.deleteRequestTag(MyApplication.application, data.getmGameID());
            } else {
                LogUtils.e("ScreenMonitorService", "第三方安装成功，不删除记录");
            }
        } catch (Throwable e) {
            YieldReportMgr.self().reportOtherData(pkgName, "", "安装完成组装数据异常：" + e.getMessage());//ConnectivityService: Neither user 10021 nor current process has android.permission.ACCESS_NETWORK_STATE.
            e.printStackTrace();
        }
    }

    /**
     * 通过apk存储路径拿实际包名
     *
     * @param game
     * @param sData
     */
    private static synchronized void setRealPackageNameForInstall(GameBaseDetail game, StatisticsEventData sData) {
        //通过apk拿包名
        String packageName = CheckIsApkFile.getPackageNameByDownPath(game.getDownPath(), MyApplication.application);
        sData.setPackage_name(TextUtils.isEmpty(packageName) ? game.getPkgName() : packageName);
    }

    /***
     * 公外部调用
     * 根据游戏ID复位统计数据
     * @param pkgName
     */
    public static synchronized void resetInstallData(String pkgName) {
        if (TextUtils.isEmpty(pkgName)) return;
        try {
            StatisDownloadTempInfoData sData = StatManger.self().queryDBbyPackageName(pkgName);
            if (null != sData) {
                LogUtils.i("ScreenMonitorService", "重置安装时间和安装次数");
                sData.setInstall_time(System.currentTimeMillis());
                sData.setInstall_count(0);
                StatManger.self().update(sData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 安装失败事件
     *
     * @param game
     * @param message
     */
    public static synchronized void installFailedEvent(GameBaseDetail game, String installMode, String installType, String message) {
        try {
            if (game != null) {
                LogUtils.i("DownloadReport", "installFailedEvent CurrentThreadName:" + Thread.currentThread().getName());
                StatisDownloadTempInfoData tempData = game.getDownloadTempInfo();
                StatisticsEventData sData = new StatisticsEventData();
                sData.setActionType((ReportEvent.ACTION_INSTALLFAILED));  //判断是属于升级安装还是普通安装请求
                setRealPackageNameForInstall(game, sData);
                sData.setId(String.valueOf(game.getId()));
                tempData.setInstall_type(installType);
                tempData.setInstall_mode(installMode);
                sData.setRemark(message);
                tempData.setIsupdate(game.getPrevState() == InstallState.upgrade ? Constant.STATE_UPDATE : Constant.STATE_NORMAL);
                sData.setAutoInstall(AutoInstallerContext.getInstance().getAccessibilityStatus() == AutoInstallerContext.STATUS_OPEN);
                StatManger.self().compareInstallDataAndReport(tempData, sData, false); //安装失败
            }
        } catch (Throwable e) {
//            YieldReportMgr.self().reportOtherData(game.getPkgName(), "", "安装失败组装数据异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param actionType
     * @param download_mode   alert/force/push.   //弹窗升级，强更,推送过来进行下载。
     * @param mark
     * @param downloadType    auto/manual.         // 自动、手动
     * @param install_type
     * @param from_version
     * @param to_version
     * @param download_action
     */
    public static synchronized void platUpdateEvent(String actionType, String download_mode, String mark, String downloadType, String install_type, String from_version, String to_version, String download_action) {

        StatisticsEventData data = new StatisticsEventData();
        try {
            LogUtils.e("platInstallRequest", "上报平台事件了" + actionType);
            data.setActionType(actionType);
            data.setDownloadType(downloadType);
            data.setDownload_mode(download_mode);
            data.setInstall_type(install_type);
            data.setRemark(mark);
            data.setDownload_action(download_action);
            data.setFrom_version(from_version);
            data.setTo_version(to_version);
        } catch (Exception e) {
            LogUtils.e("platInstallRequest", "platUpdateEvent error:" + e.getMessage());
            e.printStackTrace();
        }finally {
            if(ReportEvent.ACTION_PLATUPDATEINSTALLREQUEST.equals(actionType)){
                yieldReportSingleData(data.getString());
//                StatManger.self().saveStatisticDataToDb(data.getString(), false);
            }else{
                StatManger.self().saveStatisticDataToDb(data.getString(), false);
            }
        }
    }

    public static void checkEvent(String checkType) {
        try {
            if (!TextUtils.isEmpty(checkType)) {
                StatisticsEventData data = new StatisticsEventData();
                data.setActionType(checkType);
                StatManger.self().saveStatisticDataToDb(data.getString(), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 安装内存不足统计事件
     *
     * @param remark
     * @param tempInfoData
     */
    public static void outofmemoryEvent(String remain, String remark, StatisDownloadTempInfoData tempInfoData) {
        try {
            if (!TextUtils.isEmpty(remark)) {
                StatisticsEventData data = new StatisticsEventData();
                data.setActionType(ReportEvent.ACTION_MEMORY_ERROR);
                data.setRemark(remark);
                data.setType("memory");
                if (null != tempInfoData) {
                    data.setDownload_mode(tempInfoData.getDownload_mode());
                    data.setDownloadType(tempInfoData.getmDownloadType());
                    data.setPackage_name(tempInfoData.getmPkgName());
                    data.setSrc_id(tempInfoData.getmGameID());
                }
                data.setRemain(remain);
                StatManger.self().saveStatisticDataToDb(data.getString(), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * @param pushId
     * @param type      推送的客户端信息类型
     * @param event
     * @param push_type GETUI/app
     * @param srcType
     */
    public static synchronized void pushEvent(String pushId, String gameIds, String type, String event, String push_type, String remark, String srcType) {
        try {
            StatisticsEventData data = new StatisticsEventData();
            data.setActionType(ReportEvent.ACTION_PUSH);
            data.setPush_Id(pushId);
            data.setId(gameIds);
            data.setPresentType(type);
            data.setPush_type(push_type);//推送类型
            data.setEvent(event);
            data.setRemark(remark);
            data.setSrcType(srcType);
            StatManger.self().saveStatisticDataToDb(data.getString(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * @param pushId
     * @param type      推送的客户端信息类型
     * @param event
     * @param push_type GETUI/app
     * @param srcType
     */
    public static synchronized void pushEventByWakeupUser(String pushId, String gameIds, String type, String event, String push_type, String remark, String srcType) {
        try {
            StatisticsEventData data = new StatisticsEventData();
            data.setActionType(ReportEvent.ACTION_PUSH);
            data.setPush_Id(pushId);
            data.setId(gameIds);
            data.setPresentType(type);
            data.setPush_type(push_type);//推送类型
            data.setEvent(event);
            data.setRemark(remark);
            data.setSrcType(srcType);
            yieldReportSingleData(data.getString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void yieldReportSingleData(final String data) {
        Map<String, String> params = new HashMap<>();
        params.put("data", data);
        params.put("source", "game_center2");
        /**
         * 请求网络
         */
        Net.instance().executePost(Host.HostType.DCENTER_HOST, "", params, new WebCallBackToString() {
            @Override
            public void onSuccess(String result) {
                LogUtils.i("GOOD", "上报成功(direct)->" + data);
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.e("GOOD", "上报失败(direct)->" + statusCode + error.getMessage() + "==" + (data));
            }
        });
    }

    /***
     * 广告上报事件
     * @param event
     */
    public static synchronized void adsSpreadEvent(String event, String ad_source, String ad_type) {
        try {
            StatisticsEventData data = new StatisticsEventData();
            data.setActionType(ReportEvent.ACTION_ADREPORT);
            data.setSource(ad_source);
            data.setAd_type(ad_type);
            data.setEvent(event);
            StatManger.self().saveStatisticDataToDb(data.getString(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 浮层广告上报，需要加广告id
     *
     * @param event
     * @param ad_source
     * @param ad_type
     * @param id
     */
    public static synchronized void adsSpreadEvent(String event, String ad_source, String ad_type, String id) {
        try {
            StatisticsEventData data = new StatisticsEventData();
            data.setActionType(ReportEvent.ACTION_ADREPORT);
            data.setSource(ad_source);
            data.setAd_type(ad_type);
            data.setId(id);
            data.setEvent(event);
            StatManger.self().saveStatisticDataToDb(data.getString(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /****************************************以下为组装页面多资源数据*************************************************/
    /***
     * @des commomBuild
     * @param src_type
     * @param page
     * @param url
     * @param title
     */
    public static synchronized String buildPageMultiUnitsData(String src_type, int position, int subPosition, String presentType, String pkgName, String src_id, String page, String url, String title, String... tagId) {
        try {
            StatisticsEventData data = new StatisticsEventData();
            data.setActionType(ReportEvent.ACTION_PAGE_MULTI_UNITS);
            data.setPos(position);
            data.setSubPos(subPosition == -1 ? 1 : subPosition);
            data.setPresentType(presentType);
            data.setPackage_name(pkgName);
            data.setPage(page);
            data.setSrc_id(src_id);
            data.setSrcType(src_type);
            data.setUrl(url);
            data.setTitle(title);
            data.setLabel((tagId == null || tagId.length < 1) ? "" : tagId[0].toString());
            data.setPageId((tagId == null || tagId.length < 2) ? "" : tagId[1].toString());
            data.setWord((tagId == null || tagId.length < 3) ? "" : tagId[2].toString());
            LogUtils.i("nnn", page + "==>每个数据=>" + data.getString());
            return data.getString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param gameBaseDetail
     * @param page
     * @param i
     * @return
     * @des GameDetailBuild
     */
    public static synchronized String pageGameDetailRecommen(GameDomainBaseDetail gameBaseDetail, String page, int i, int pageid, String pagename) {
        try {
            StatisticsEventData data = new StatisticsEventData();
            data.setActionType(ReportEvent.ACTION_PAGE_MULTI_UNITS);
            data.setPos(1);
            data.setSubPos(i + 1);
            data.setPage(page);
            data.setPackage_name(gameBaseDetail.getPkgName());
            data.setSrc_id("" + gameBaseDetail.getUniqueIdentifier());
            data.setPageId("" + pageid);
            data.setTitle(pagename);
            data.setPresentType(Constant.SRCTYPE_DETAILRECOM);
            LogUtils.i("nnn", page + "recom每个数据=>" + data.getString());
            return data.getString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param functionEssence
     * @param page
     * @param j
     * @return
     * @des GameDetailBuild
     */
    public static synchronized String pageGameDetailLable(FunctionEssence functionEssence, String page, int j, int pageid, String pagename) {
        try {
            StatisticsEventData data = new StatisticsEventData();
            data.setActionType(ReportEvent.ACTION_PAGE_MULTI_UNITS);
            data.setPos(2);
            data.setSubPos(j + 1);
            data.setPage(page);
            data.setSrc_id(functionEssence.getUniqueIdentifier());
            data.setLabel(functionEssence.getTitle());
            data.setPageId("" + pageid);
            data.setTitle(pagename);
            data.setPresentType(Constant.SRCTYPE_DETAILLABLE);
            LogUtils.i("nnn", page + "lable每个数据=>" + data.getString());
            return data.getString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param gameDomainBaseDetail
     * @param pageName
     * @param keyword
     * @param i
     * @return
     * @des searchBuild
     */
    public static synchronized String autoSearchAccuratebuild(GameDomainBaseDetail gameDomainBaseDetail, String pageName, String keyword, int i) {
        try {
            StatisticsEventData data = new StatisticsEventData();
            data.setActionType(ReportEvent.ACTION_PAGE_MULTI_UNITS);
            data.setPage(pageName);
            data.setPos(1);
            data.setSubPos(1 + i);
            data.setPackage_name(gameDomainBaseDetail.getPkgName());
            data.setSrc_id(gameDomainBaseDetail.getUniqueIdentifier());
            data.setWord(keyword.toString());
            data.setPresentType(Constant.SRCTYPE_SEARCHACCU);
            LogUtils.i("nnn", "绑定 自动匹配中的一种小类型  每个数据=>" + data.getString());
            return data.getString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param history
     * @param pageName
     * @param keyword
     * @param i
     * @return
     * @des searchBuild
     */
    public static synchronized String autoSearchhistoryBuild(String history, String pageName, String keyword, int i) {
        try {
            StatisticsEventData data = new StatisticsEventData();
            data.setActionType(ReportEvent.ACTION_PAGE_MULTI_UNITS);
            data.setPage(pageName);
            data.setPos(1);
            data.setSubPos(1 + i);
            data.setWord(keyword.toString());
            data.setTitle(history);
            data.setPresentType(Constant.SRCTYPE_SEARCHHIS);
            LogUtils.i("nnn", "history 自动匹配中的一种小类型  每个数据=>" + data.getString());
            return data.getString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param pageName
     * @param j
     * @return
     * @des searchBuild
     */
    public static String searchAdvertisementBuild(FunctionEssence functionEssence, String pageName, int j) {
        try {
            StatisticsEventData data = new StatisticsEventData();
            data.setActionType(ReportEvent.ACTION_PAGE_MULTI_UNITS);
            data.setPage(pageName);
            data.setPos(1);
            data.setSubPos(j + 1);
            data.setTitle(functionEssence.getTitle());
            LogUtils.i("nnn", "搜索推荐 中的每个数据=>" + data.getString());
            return data.getString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return
     * @des searchBuild
     */
    public static synchronized String autoSearchFuzzyBuild(FunctionEssence fe, String pageName, String keyword, int i) {
        try {
            StatisticsEventData data = new StatisticsEventData();
            data.setActionType(ReportEvent.ACTION_PAGE_MULTI_UNITS);
            data.setPos(1);
            data.setSubPos(1 + i);
            data.setTitle(fe.getTitle());
            data.setPage(pageName);
            data.setWord(keyword.toString());
            data.setPresentType(Constant.SRCTYPE_SEARCHFUZ);
            LogUtils.i("nnn", "模糊 自动匹配中的一种小类型  每个数据=>" + data.getString());
            return data.getString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param gameBaseDetail
     * @param subPosition
     * @param page
     * @return
     * @des biWanBuild
     */
    public static synchronized String pageBiWanEvent(GameInfoBean gameBaseDetail, int subPosition, String page) {
        try {
            StatisticsEventData data = new StatisticsEventData();
            data.setActionType(ReportEvent.ACTION_PAGE_MULTI_UNITS);
            data.setPos(1);
            data.setSubPos(subPosition + 1);
            data.setPackage_name(gameBaseDetail.getPackage_name());
            data.setSrc_id("" + gameBaseDetail.getId());
            data.setPage(page);
            LogUtils.i("nnn", "精选必玩每个数据=>" + data.getString());
            return data.getString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
