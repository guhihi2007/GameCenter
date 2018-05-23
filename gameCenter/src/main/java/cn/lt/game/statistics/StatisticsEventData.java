package cn.lt.game.statistics;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import cn.lt.game.application.MyApplication;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.FromPageManager;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.statistics.entity.AdReportEventBean;
import cn.lt.game.statistics.entity.AnalysisEventBean;
import cn.lt.game.statistics.entity.ClickEventBean;
import cn.lt.game.statistics.entity.DefaultEventBean;
import cn.lt.game.statistics.entity.DownloadReqEventBean;
import cn.lt.game.statistics.entity.DownloadedEventBean;
import cn.lt.game.statistics.entity.ErrorEventBean;
import cn.lt.game.statistics.entity.InstallReqEventBean;
import cn.lt.game.statistics.entity.JumpEventBean;
import cn.lt.game.statistics.entity.PageMultiUnitEventBean;
import cn.lt.game.statistics.entity.PlatUpdateEventBean;
import cn.lt.game.statistics.entity.PushEventBean;
import cn.lt.game.statistics.entity.SearchEventBean;
import cn.lt.game.ui.app.requisite.manger.SharedPreference;

/***
 * 4.0（含）以上统计事件对象；
 * Created by Administrator on 2015/11/25.
 */
public class StatisticsEventData {

    private String actionType;
    private String page;
    private String presentType;
    private int pos;
    private int subPos;
    private String id; //推送ID
    private String src_id; //资源ID、游戏ID、页面id
    private String remark;
    private String downloadType;
    private String srcType;
    private String downSpeed;
    private String type;//安装内存统计字段
    private boolean isAutoInstall;
    private String remain;//安装剩余内存
    private String event;//事件名称
    private String event_type;//事件类型
    private String ad_type;//广告类型
    private String source; //来源

    private String push_type;//推送类型
    private String download_mode;//下载模式，onekey,single
    private String package_name;
    private String install_mode;
    private String install_type;
    private long install_time;
    private String from_version;  //版本升级的
    private String download_action;
    private String push_Id;
    private int install_count;
    private String title; //启动页图片标题
    private String url;
    private String word;
    private String label;
    private String history;
    private String from_page;
    private String from_id;
    private String pageId;
    private String to_version;

    public long getInstall_time() {
        return install_time;
    }

    public void setInstall_time(long install_time) {
        this.install_time = install_time;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public void setAd_type(String ad_type) {
        this.ad_type = ad_type;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getFrom_page() {
        return from_page;
    }

    public void setFrom_page(String from_page) {
        this.from_page = from_page;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getInstall_count() {
        return install_count;
    }

    public void setInstall_count(int install_count) {
        this.install_count = install_count;
    }

    public String getPush_Id() {
        return push_Id;
    }

    public void setPush_Id(String push_Id) {
        this.push_Id = push_Id;
    }

    public String getSrc_id() {
        return src_id;
    }

    public void setSrc_id(String src_id) {
        this.src_id = src_id;
    }

    public String getFrom_version() {
        return from_version;
    }

    public void setFrom_version(String from_version) {
        this.from_version = from_version;
    }

    public String getTo_version() {
        return to_version;
    }

    public void setTo_version(String to_version) {
        this.to_version = to_version;
    }

    public String getDownload_action() {
        return download_action;
    }

    public void setDownload_action(String download_action) {
        this.download_action = download_action;
    }

    public String getInstall_type() {
        return install_type;
    }

    public void setInstall_type(String install_type) {
        this.install_type = install_type;
    }

    public String getInstall_mode() {
        return install_mode;
    }

    public void setInstall_mode(String install_mode) {
        this.install_mode = install_mode;
    }

    public String getDownload_mode() {
        return download_mode;
    }

    public void setDownload_mode(String download_mode) {
        this.download_mode = download_mode;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getPush_type() {
        return push_type;
    }

    public void setPush_type(String push_type) {
        this.push_type = push_type;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public String getRemain() {
        return remain;
    }

    public void setRemain(String remain) {
        this.remain = remain;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public StatisticsEventData() {
    }

    public StatisticsEventData(String actionType, String page, int pos, String presentType, int subPos, String id, String remark, String downloadType, String srcType, String packagName,String pageId) {
        this.actionType = actionType;
        this.page = page;
        this.pos = pos;
        this.presentType = presentType;
        this.subPos = subPos;
        this.src_id = id;
        this.remark = remark;
        this.downloadType = downloadType;
        this.srcType = srcType;
        this.package_name = packagName;
        this.pageId = pageId;
    }

    public boolean isAutoInstall() {
        return isAutoInstall;
    }

    public void setAutoInstall(boolean autoInstall) {
        isAutoInstall = autoInstall;
    }

    public String getDownSpeed() {
        return downSpeed;
    }

    public void setDownSpeed(String downSpeed) {
        this.downSpeed = downSpeed;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getPresentType() {
        return presentType;
    }

    public void setPresentType(String presentType) {
        this.presentType = presentType;
    }

    public int getSubPos() {
        return subPos;
    }

    public void setSubPos(int subPos) {
        this.subPos = subPos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(String downloadType) {
        this.downloadType = downloadType;
    }

    public String getSrcType() {
        return srcType;
    }

    public void setSrcType(String srcType) {
        this.srcType = srcType;
    }

    public String getPage_id() {
        return pageId;
    }

    public void setPage_id(String pageId) {
        this.pageId = pageId;
    }

    public String getString() {
        if (TextUtils.isEmpty(actionType)) {
            LogUtils.i("zzz", "actionType为空");
            return null;
        }
        String s = getStatString();
        return s;
    }

    /**
     * 获得统计将要上报的数据    presentType  srcType  id  pageId  url  word  label remark
     *
     * @return
     */
    private String getStatString() {
        String s = "";
        Gson gson = getGson();
        String netType = NetUtils.getNetworkType(MyApplication.application.getApplicationContext());
        try {
            if (FromPageManager.setWordByPage(page)) {
                if (!(ReportEvent.ACTION_UPDATEDOWNLOADFAILED.equals(actionType) || ReportEvent.ACTION_DOWNLOADFAILED.equals(actionType) ||
                        ReportEvent.ACTION_UPDATEDOWNLOADED.equals(actionType) || ReportEvent.ACTION_DOWNLOADED.equals(actionType) ||
                        ReportEvent.ACTION_UPDATEINSTALLSUCCESS.equals(actionType) || ReportEvent.ACTION_INSTALLSUCCESS.equals(actionType) ||
                        ReportEvent.ACTION_UPDATEINSTALLFAILED.equals(actionType) || ReportEvent.ACTION_INSTALLFAILED.equals(actionType))) {
                    word = MyApplication.application.mCurrentWord;  //非下载、安装完成、失败的取内存
                }
            }
            if (pos == 0) pos = 1;
            if (subPos == 0) subPos = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ReportEvent.ACTION_CLICK.equals(actionType)) {
            ClickEventBean bean = new ClickEventBean();
            bean.setActionType(actionType);
            bean.setPage(page);
            bean.setId(src_id);
            bean.setPage_id(pageId);
            if (page.equals(Constant.PAGE_GE_TUI)) {
                bean.setPresentType("game");
            } else {
                bean.setPresentType(presentType);
            }
            bean.setDownloadType(downloadType);
            if (pos == -1 || pos == 0) {
                bean.setPos(1);
            } else {
                bean.setPos(pos);
            }
            if (subPos == -1 || subPos == 0) {
                bean.setSubPos(1);
            } else {
                bean.setSubPos(subPos);
            }
            if (TextUtils.isEmpty(word)) {
                bean.setWord("");
            } else {
                bean.setWord(word);
            }
            bean.setPackage_name(package_name);
            bean.setSrcType(srcType);
            bean.setNetworkType(netType);
            bean.setRemark(remark);
            s = gson.toJson(bean);
        } else if (ReportEvent.ACTION_PAGEJUMP.equals(actionType)) {
            JumpEventBean bean = new JumpEventBean();
            bean.setPage(page);
            bean.setId(src_id);
            bean.setFrom_page(from_page);
            bean.setFrom_id(from_id);
            bean.setWord(word);
            bean.setNetworkType(netType);
            bean.setRemark(remark);
            bean.setActionType(actionType);
            s = gson.toJson(bean);
        } else if (ReportEvent.ACTION_SEARCH.equals(actionType)) {
            SearchEventBean bean = new SearchEventBean();
            bean.setActionType(actionType);
            bean.setPage(page);
            bean.setWord(remark);
            bean.setNetworkType(netType);
            s = gson.toJson(bean);
        } else if (ReportEvent.ACTION_DOWNLOADREQUEST.equals(actionType) || ReportEvent.ACTION_UPDATEDOWNLOADREQUEST.equals(actionType) || ReportEvent.ACTION_UPDATEDOWNLOADFAILED.equals(actionType) || ReportEvent.ACTION_DOWNLOADFAILED.equals(actionType)) { //下载请求及失败组
            DownloadReqEventBean bean = new DownloadReqEventBean();
            bean.setActionType(actionType);
            bean.setPackage_name(package_name);
            bean.setPage(page);
            bean.setId(src_id);
            if (ReportEvent.ACTION_DOWNLOADREQUEST.equals(actionType) || ReportEvent.ACTION_UPDATEDOWNLOADREQUEST.equals(actionType)) { //下载请求
                bean.setFrom_id(from_id);
                bean.setWord(word);
            }
            if (pos == -1 || pos == 0) {
                bean.setPos(1);
            } else {
                bean.setPos(pos);
            }
            if (subPos == -1 || subPos == 0) {
                bean.setSubPos(1);
            } else {
                bean.setSubPos(subPos);
            }
            bean.setDownloadType(downloadType);
            bean.setDownloadMode(download_mode);
            bean.setFrom_page(from_page);
            bean.setPage_id(pageId);
            bean.setRemark(remark);
            bean.setNetworkType(netType);
            s = gson.toJson(bean);
        } else if (ReportEvent.ACTION_DOWNLOADED.equals(actionType) || ReportEvent.ACTION_UPDATEDOWNLOADED.equals(actionType)) { //下载成功
            DownloadedEventBean bean = new DownloadedEventBean();
            bean.setActionType(actionType);
            bean.setPackage_name(package_name);
            if (pos == -1 || pos == 0) {
                bean.setPos(1);
            } else {
                bean.setPos(pos);
            }
            if (subPos == -1 || subPos == 0) {
                bean.setSubPos(1);
            } else {
                bean.setSubPos(subPos);
            }
            bean.setPage(page);
            bean.setId(src_id);
            bean.setFrom_page(from_page);
            bean.setFrom_id(from_id);
            bean.setWord(word);
            bean.setDownloadType(downloadType);
            bean.setDownloadMode(download_mode);
            bean.setDownSpeed(downSpeed);
            bean.setRemark(remark);
            bean.setPage_id(pageId);
            bean.setNetworkType(netType);
            s = gson.toJson(bean);
        } else if (ReportEvent.ACTION_INSTALL_REQUEST.equals(actionType) || ReportEvent.ACTION_UPDATE_INSTALL_REQUEST.equals(actionType) || ReportEvent.ACTION_UPDATEINSTALLSUCCESS.equals(actionType) || ReportEvent.ACTION_INSTALLSUCCESS.equals(actionType) || ReportEvent.ACTION_UPDATEINSTALLFAILED.equals(actionType) || ReportEvent.ACTION_INSTALLFAILED.equals(actionType)) {  //安装组
            InstallReqEventBean bean = new InstallReqEventBean();
            bean.setActionType(actionType);
            bean.setPage(page);
            bean.setId(src_id);
            if (pos == -1 || pos == 0) {
                bean.setPos(1);
            } else {
                bean.setPos(pos);
            }
            if (subPos == -1 || subPos == 0) {
                bean.setSubPos(1);
            } else {
                bean.setSubPos(subPos);
            }
            if (ReportEvent.ACTION_UPDATEINSTALLSUCCESS.equals(actionType) || ReportEvent.ACTION_INSTALLSUCCESS.equals(actionType) || ReportEvent.ACTION_INSTALLFAILED.equals(actionType)) {   //安装成功
                bean.setFrom_page(from_page);
                bean.setFrom_id(from_id);
                bean.setWord(word);
            }
            bean.setDownloadType(downloadType);
            bean.setDownloadMode(download_mode);
            bean.setInstallMode(install_mode);
            bean.setInstallType(install_type);
            bean.setPackageName(package_name);
            bean.setAutoInstall(isAutoInstall);
            bean.setPage_id(pageId);
            bean.setRemark(remark);
            bean.setNetworkType(netType);
            s = gson.toJson(bean);
        } else if (ReportEvent.ACTION_MEMORY_ERROR.equals(actionType)) {
            ErrorEventBean bean = new ErrorEventBean();
            bean.setActionType(actionType);
            bean.setType(type);
            bean.setRemain(remain);
            bean.setRemark(remark);
            bean.setDownload_mode(download_mode);
            bean.setDownloadType(downloadType);
            bean.setPackage_name(package_name);
            bean.setId(src_id);
            bean.setNetworkType(netType);
            s = gson.toJson(bean);
        } else if (ReportEvent.ACTION_PUSH.equals(actionType)) {    //组建推送事件
            PushEventBean bean = new PushEventBean();
            bean.setActionType(actionType);
            bean.setEvent(event);
            bean.setPush_id(push_Id);
            bean.setId(id);
            bean.setPush_type(push_type);
            bean.setSrcType(srcType);
            bean.setPresentType(presentType);
            bean.setRemark(remark);
            bean.setNetworkType(netType);
            s = gson.toJson(bean);
        } else if (ReportEvent.ACTION_PLATUPDATEREQUEST.equals(actionType) || ReportEvent.ACTION_PLATUPDATEDOWNLOADED.equals(actionType) || ReportEvent.ACTION_PLATUPDATEINSTALLED.equals(actionType) || ReportEvent.ACTION_PLATUPDATEDOWNLOADFAILED.equals(actionType) || ReportEvent.ACTION_PLATUPDATEINSTALLREQUEST.equals(actionType) || ReportEvent.ACTION_PLATUPDATEINSTALLFAILED.equals(actionType)) {    //组建平台升级事件
            PlatUpdateEventBean bean = new PlatUpdateEventBean();
            bean.setActionType(actionType);
            bean.setDownloadType(downloadType);
            bean.setDownload_mode(download_mode);
            bean.setFrom_version(from_version);
            bean.setTo_version(to_version);
            bean.setDownload_action(download_action);
            if (ReportEvent.ACTION_PLATUPDATEINSTALLED.equals(actionType) || ReportEvent.ACTION_PLATUPDATEINSTALLREQUEST.equals(actionType)) {
                bean.setInstall_type(install_type);
            }
            bean.setRemark(remark);
            bean.setNetworkType(netType);
            s = gson.toJson(bean);
        } else if (ReportEvent.ACTION_PAGE_MULTI_UNITS.equals(actionType)) {    //组建页面详细资源位曝光
            PageMultiUnitEventBean bean = new PageMultiUnitEventBean();
            bean.setActionType(actionType);
            bean.setPage(page);
            bean.setPageId(pageId);
            bean.setPresentType(presentType);
            bean.setSrcType(srcType);
            bean.setId(src_id);
            bean.setPackage_name(package_name);
            if (pos == -1 || pos == 0) {
                bean.setPos(1);
            } else {
                bean.setPos(pos);
            }
            if (subPos == -1 || subPos == 0) {
                bean.setSubPos(1);
            } else {
                bean.setSubPos(subPos);
            }
            bean.setLabel(label);
            bean.setWord(word);
            bean.setTitle(title);
            bean.setRemark(remark);
            bean.setNetworkType(netType);
//            bean.setTime(currentTime);//不能加时间，否则无法过滤重复数据
            bean.setUrl(url);
            s = gson.toJson(bean);
        } else if (ReportEvent.ACTION_ADREPORT.equals(actionType)) {
            AdReportEventBean bean = new AdReportEventBean();
            bean.setActionType(actionType);
            bean.setEvent(event);
            bean.setAdSource(source);
            bean.setAdType(ad_type);
            bean.setRemark(remark);
            bean.setNetworkType(netType);
            if (!TextUtils.isEmpty(id)) {
                // 浮层广告广告ID
                bean.setId(id);
            }
            s = gson.toJson(bean);
        } else if (ReportEvent.ACTION_ANALYSIS.equals(actionType)) {
            AnalysisEventBean bean = new AnalysisEventBean();
            bean.setActionType(actionType);
            bean.setNetworkType(netType);
            bean.setPackage_name(package_name);
            bean.setId(src_id);
            bean.setTime(System.currentTimeMillis());
            bean.setRemark(remark);
            s = gson.toJson(bean);
        } else {
            DefaultEventBean bean = new DefaultEventBean();
            bean.setActionType(actionType);
            bean.setNetworkType(netType);
            bean.setIntervalTime(System.currentTimeMillis() - SharedPreference.getLastCheckTime(MyApplication.application));
            SharedPreference.saveThisCheckTime(MyApplication.application, System.currentTimeMillis());
            bean.setRemark(TextUtils.isEmpty(remark) ? "" : remark);
            s = gson.toJson(bean);
        }
        return s;
    }

    @NonNull
    private Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(String.class, new TypeAdapter<String>() {

            @Override
            public void write(JsonWriter out, String value) throws IOException {
                if (value == null) {
                    out.value(""); // 序列化时将 null 转为 ""
                } else {
                    out.value(value);
                }
            }

            @Override
            public String read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                // return in.nextString();
                String str = in.nextString();
                if (str.equals("")) { // 反序列化时将 "" 转为 null
                    return null;
                } else {
                    return str;
                }
            }

        });

        return gsonBuilder.disableHtmlEscaping().create();
    }

}
