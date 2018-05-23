package cn.lt.game.statistics.entity;

import android.text.TextUtils;

import java.io.Serializable;

import cn.lt.game.global.Constant;

/***
 * Created by Administrator on 2015/12/19.
 */
public class StatisDownloadTempInfoData implements Serializable, Cloneable {
    private String mGameID;
    private String mPkgName;
    private String mActionType;
    private String mDownloadType;
    private int mPreState;
    private String mRemark;
    private String mPage;
    private String networkType;
    private String downSpeed;
    //下载安装请求需要添加模块中的位置
    private int pos = 1;
    private int subPos = 1;
    private String download_mode;

    private String install_type;
    private String install_mode;
    private String presenType;
    public boolean isFromRetry;
    public String mRealRemark;
    public String mRealActionType;
    private long install_time;//安装时间（获取当前毫秒）
    private int install_count;
    private String from_page;
    private String from_id;
    private String word;
    private boolean isAutoInstall;
    private String isupdate = Constant.STATE_NORMAL; //normal  update
    private String pageId;

    public String getIsupdate() {
        return isupdate;
    }

    public void setIsupdate(String isupdate) {
        this.isupdate = isupdate;
    }

    public long getInstall_time() {
        return install_time;
    }

    public void setInstall_time(long install_time) {
        this.install_time = install_time;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
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

    public int getInstall_count() {
        return install_count;
    }

    public void setInstall_count(int install_count) {
        this.install_count = install_count;
    }

    private boolean isFromOtherMarket;


    public boolean isFromOtherMarket() {
        return isFromOtherMarket;
    }

    public String getPresenType() {
        return presenType;
    }

    public void setPresenType(String presenType) {
        this.presenType = presenType;
    }

    public String getInstall_mode() {
        return install_mode;
    }

    public void setInstall_mode(String install_mode) {
        this.install_mode = install_mode;
    }

    public String getInstall_type() {
        return install_type;
    }

    public void setInstall_type(String install_type) {
        this.install_type = install_type;
    }

    public StatisDownloadTempInfoData() {

    }

    public String getDownload_mode() {
        return download_mode;
    }

    public void setDownload_mode(String download_mode) {
        this.download_mode = download_mode;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getSubPos() {
        return subPos;
    }

    public void setSubPos(int subPos) {
        this.subPos = subPos;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getDownSpeed() {
        return downSpeed;
    }

    public void setDownSpeed(String downSpeed) {
        this.downSpeed = downSpeed;
    }

    public String getmGameID() {
        return mGameID;
    }

    public StatisDownloadTempInfoData setmGameID(String mGameID) {
        this.mGameID = mGameID;
        return this;
    }

    public String getmPkgName() {
        return mPkgName;
    }

    public StatisDownloadTempInfoData setmPkgName(String mPkgName) {
        this.mPkgName = mPkgName;
        return this;
    }

    public String getmActionType() {
        return mActionType;
    }

    public StatisDownloadTempInfoData setmActionType(String mActionType) {
        this.mActionType = mActionType;
        return this;
    }

    public String getmDownloadType() {
        return mDownloadType;
    }

    public StatisDownloadTempInfoData setmDownloadType(String mDownloadType) {
        this.mDownloadType = mDownloadType;
        return this;
    }

    public int getmPreState() {
        return mPreState;
    }

    public StatisDownloadTempInfoData setmPreState(int mPreState) {
        this.mPreState = mPreState;
        return this;
    }

    public StatisDownloadTempInfoData setIsFromOtherMarket(boolean isFromOtherMarket) {
        this.isFromOtherMarket = isFromOtherMarket;
        return this;
    }

    public String getmRemark() {
        if (TextUtils.isEmpty(mRemark)) {
            return "";
        }
        return mRemark;
    }

    public StatisDownloadTempInfoData setmRemark(String mRemark) {
        this.mRemark = mRemark;
        return this;
    }

    public String getmPage() {
        return mPage;
    }

    public StatisDownloadTempInfoData setmPage(String mPage) {
        this.mPage = mPage;
        return this;
    }

    public String getPage_id() {
        return pageId;
    }

    public void setPage_id(String pageId) {
        this.pageId = pageId;
    }

    //    @Override
//    public String toString() {
//        String s = "gameId-->" + mGameID + ",pkgName-->" + mPkgName + ",actionType-->" +
//                mActionType + ",downloadType-->" + mDownloadType + ",presate-->" + mPreState + "," +
//                "remark-->" + mRemark + ",page-->" + mPage;
//        return s;
//    }


    @Override
    public String toString() {
        return "StatisDownloadTempInfoData{" + "mGameID='" + mGameID + '\'' + ", mPkgName='" + mPkgName + '\'' + ", mActionType='" + mActionType + '\'' + ", mDownloadType='" + mDownloadType + '\'' + ", mPreState=" + mPreState + ", mRemark='" + mRemark + '\'' + ", mPage='" + mPage + '\'' + ", networkType='" + networkType + '\'' + ", downSpeed='" + downSpeed + '\'' + ", pos=" + pos + ", subPos=" + subPos + ", download_mode='" + download_mode + '\'' + ", install_type='" + install_type + '\'' + ", install_mode='" + install_mode + '\'' + ", presenType='" + presenType + '\'' + ", isFromRetry=" + isFromRetry + ", mRealRemark='" + mRealRemark + '\'' + ", mRealActionType='" + mRealActionType + '\'' + ", install_count=" + install_count + ", from_page='" + from_page + '\'' + ", from_id='" + from_id + '\'' + ", word='" + word + '\'' + ", isFromOtherMarket=" + isFromOtherMarket + '}';
    }

    @Override
    public StatisDownloadTempInfoData clone() {
        StatisDownloadTempInfoData data = null;
        try {
            data = (StatisDownloadTempInfoData) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return data;
    }
}
