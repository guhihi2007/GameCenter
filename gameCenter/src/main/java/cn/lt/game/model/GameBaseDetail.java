package cn.lt.game.model;

import android.content.Context;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import cn.lt.game.bean.DeeplinkBean;
import cn.lt.game.db.service.DownFileService;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.domain.detail.GameDomainDetail;
import cn.lt.game.domain.detail.GameDomainExtraDetail;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.download.DownloadState;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.util.AdMd5;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.statistics.entity.StatisDownloadTempInfoData;

public class GameBaseDetail implements Comparable<GameBaseDetail>, Serializable, Cloneable {
    protected static final long serialVersionUID = 1L;

    protected GameDomainExtraDetail gameExtraDetail = new GameDomainExtraDetail();

    /* 基本信息 **/
    protected GameDomainBaseDetail gameDomainBase = new GameDomainBaseDetail();


    /* 游戏管理信息：下载、打开等 **/
    protected int mPrevState = 0; // 游戏的上一个状态，为了区分升级的下载过程和首次下载过程
    protected int mState = DownloadState.undownload; // 游戏状态（包括下载状态DownloadState和安装状态InstallState）
    protected String mDownPath = null; // apk本地保存路径
    protected long mDownLength = -1; // apk已下载长度，单位BYTE
    public long mFileTotalLength = 0; // apk总长度，单位BYTE
    protected long mDownSpeed = 0; // 下载速度
    protected int downTimeLeft = 0; // 管理页面 下载剩余时间 单位秒
    protected long openTime = -1; // 游戏打开时间
    protected String mSuffix = null; // 下载文件名后缀（暂未使用）
    protected Boolean isShowToggle = false; // 2.0管理页面用的 ，是否打开抽屉层
    protected String downloadFailedReason = "";
    public String mRemark;
    public long mSetUpTime;
    public long mInstallTime;
    public int mInstallListener = InstallState.install_listener;
    public boolean mIsRunning;
    public String pageId;
    public int pos;

    // 是否是可覆盖的游戏
    public boolean isCoveredApp = false;

    /**
     * 预约wifi下载
     */
    private boolean isOrderWifiDownload = false;

    //4.3添加
//    private boolean isAccepted;
//    private int downloadPoint;
//    private String downloadFrom;

    public DeeplinkBean getDeeplink_app() {
        return gameDomainBase.getDeeplink_app();
    }

    public void setDeeplink_app(DeeplinkBean deeplink_app) {
        gameDomainBase.setDeeplink_app(deeplink_app);
    }

    public String getDeeplink() {
        return gameDomainBase.getDeeplink();
    }

    public void setDeeplink(String deeplink_app) {
        gameDomainBase.setDeeplink(deeplink_app);
    }

    public String getDownloadFrom() {
        return gameDomainBase.getDownload_from();
    }

    public void setDownloadFrom(String downloadFrom) {
        gameDomainBase.setDownload_from(downloadFrom);
    }

    public int isAccepted() {
        return gameDomainBase.getIs_accepted();
    }

    public void setAccepted(int accepted) {
        gameDomainBase.setIs_accepted(accepted);
    }

    public int getDownloadPoint() {
        return gameDomainBase.getDownload_point();
    }

    public void setDownloadPoint(int downloadPoint) {
        gameDomainBase.setDownload_point(downloadPoint);
    }

    public boolean isOrderWifiDownload() {
        return isOrderWifiDownload;
    }

    public void setOrderWifiDownload(boolean orderWifiDownload) {
        isOrderWifiDownload = orderWifiDownload;
    }

    /* 统计数据 */
    protected StatisDownloadTempInfoData mStatisticsData = null;

    public StatisDownloadTempInfoData getDownloadTempInfo() {
        if (mStatisticsData == null) {
            mStatisticsData = new StatisDownloadTempInfoData();
        }
        return mStatisticsData.setmGameID(String.valueOf(getId())).setmPkgName(getPkgName()).setmPreState(mState);
    }

    public void setGameDetail(GameDomainDetail gameDomainDetail) {
        setGameBaseInfo(gameDomainDetail);
        gameExtraDetail = gameDomainDetail.getExtraDetail();
    }

    public GameBaseDetail setGameBaseInfo(GameDomainBaseDetail baseInfo) {
        gameDomainBase = baseInfo;
        return this;
    }

    public List<String> getFlags() {
        return gameDomainBase.getFlags();
    }

    public boolean isBusinessPackage() {
        return gameDomainBase.isBusinessPackage();
    }

    public void setBusinessPackage(boolean businessPackage) {
        gameDomainBase.setBusinessPackage(businessPackage);
    }

    public boolean canUpgrade() {
        return gameDomainBase.canUpgrade();
    }

    public void setCanUpgrade(boolean canUpgrade) {
        gameDomainBase.setCanUpgrade(canUpgrade);
    }

    public GameDomainBaseDetail getGameDomainBase() {
        return gameDomainBase;
    }

    public float getScore() {
        return gameDomainBase.getScore();
    }

    public GameBaseDetail setScore(float score) {
        gameDomainBase.setScore(score);
        return this;
    }


    public int getForumId() {
        return gameDomainBase.getGroupId();
    }

    public GameBaseDetail setForumId(int forumId) {
        gameDomainBase.setGroupId(forumId);
        return this;
    }

    public int getDownTimeLeft() {
        return downTimeLeft;
    }

    public GameBaseDetail setDownTimeLeft(int downTimeLeft) {
        this.downTimeLeft = downTimeLeft;
        return this;
    }

    public Boolean getIsShowToggle() {
        return isShowToggle;
    }

    public GameBaseDetail setIsShowToggle(Boolean isShowToggle) {
        this.isShowToggle = isShowToggle;
        return this;
    }

    public long getOpenTime() {
        return openTime;
    }

    public GameBaseDetail setOpenTime(long openTime) {
        this.openTime = openTime;
        return this;
    }

    public String getMark() {
        return gameDomainBase.getMark();
    }

    public void setMark(String mark) {
        gameDomainBase.setMark(mark);
    }


    public int getId() {
        return Integer.parseInt(TextUtils.isEmpty(gameDomainBase.getUniqueIdentifier()) ? "0" : gameDomainBase.getUniqueIdentifier());
    }

    public GameBaseDetail setId(int id) {
        gameDomainBase.setUniqueIdentifier(String.valueOf(id));
        return this;
    }

    public String getName() {
        return gameDomainBase.getName();
    }

    public GameBaseDetail setName(String name) {
        gameDomainBase.setName(name);
        return this;
    }


    public String getShortName() {
        return getNameWithLength(6);
    }

    public String getLongName() {
        return getNameWithLength(12);
    }

    private String getNameWithLength(int length) {
        return getName().length() >= length ? getName().substring(0, length - 1) + "..." : getName();
    }

    public int getVersionCode() {
        return gameDomainBase.getVerCode();
    }

    public GameBaseDetail setVersionCode(int versionCode) {
        gameDomainBase.setVerCode(versionCode);
        return this;
    }

    public String getVersion() {
        return gameDomainBase.getVerName();
    }

    public GameBaseDetail setVersion(String version) {
        gameDomainBase.setVerName(version);
        return this;
    }

    public String getLogoUrl() {
        return gameDomainBase.getIconUrl();
    }

    public GameBaseDetail setLogoUrl(String logoUrl) {
        gameDomainBase.setIconUrl(logoUrl);
        return this;
    }

    public String getDownUrl() {
        return gameDomainBase.getDownUrl();
    }

    public GameBaseDetail setDownUrl(String downUrl) {
        gameDomainBase.setDownUrl(downUrl);
        return this;
    }

    public String getDownloadCnt() {
        return gameDomainBase.getDownCnt();
    }

    public void setDownloadCnt(String downloadCnt) {
        gameDomainBase.setDownCnt(downloadCnt);
    }

    public GameBaseDetail setDownloadCnt(int downloadCnt) {
        gameDomainBase.setDownCnt(String.valueOf(downloadCnt));
        return this;
    }

    public long getPkgSize() {
        return gameDomainBase.getPkgSize() != 0 ? gameDomainBase.getPkgSize() : mFileTotalLength;
    }

    public GameBaseDetail setPkgSize(long pkgSize) {
        if (pkgSize >= 1024) {
            gameDomainBase.setPkgSize(pkgSize);
        }
        return this;
    }

    /* 如果mPkgSize的单位是byte，用此方法 */
    public String getPkgSizeInM() {
        double size = (gameDomainBase.getPkgSize() != 0 ? gameDomainBase.getPkgSize() : mFileTotalLength);
        return String.format(Locale.CHINESE, "%.2fMB", size / 1024 / 1024);
    }

    public long getDownLength() {
        return mDownLength;
    }

    public GameBaseDetail setDownLength(long downLength) {
        mDownLength = downLength;
        return this;
    }

    public long getDownLengthFromDb(Context context) {
        GameBaseDetail data = DownFileService.getInstance(context).getDownFile(getDownUrl());
        return mDownLength = (data == null ? 0 : data.getDownLength());
    }

    public GameBaseDetail setDownLengthToDb(Context context, long downLength) {
        mDownLength = downLength;
        DownFileService.getInstance(context).updateById(this);
        return this;
    }


    public String getDownSpeedWithKbOrMb() {
        String str;
        if (mDownSpeed < 1024) {
            str = mDownSpeed + " KB/s";
        } else {
            str = mDownSpeed / 1024 + " MB/s";
        }
        return str;
    }

    public GameBaseDetail setDownSpeed(long mDownSpeed) {
        this.mDownSpeed = mDownSpeed;
        return this;
    }

    public long getDownSpeed() {
        return this.mDownSpeed;
    }

    public int getDownPercent() {
        int percent = 0;
        if (getDownLength() != 0 && getFileTotalLength() != 0) {
            percent = (int) (getDownLength() * 10000 / getFileTotalLength());
        }
        return percent > 10000 ? 10000 : percent;
    }


    public int getState() {
        return mState;
    }

    public GameBaseDetail setState(int state) {
        // 当状态设置为未下载时，需再次验证是否为已安装应用，进而修正状态
        if (state == DownloadState.undownload) {
            if (AppUtils.isInstalled(getPkgName()) && !isCoveredApp && getPrevState() != InstallState.upgrade) {//过滤掉白名单覆盖的，否则会出现 已经覆盖成功，然后卸载覆盖的应用，熄屏后出现重新覆盖的Bug modify by ATian
                state = InstallState.installComplete;
            }
        }
        mState = state;
        return this;
    }

    public int getStateFromDb(Context context) {
        GameBaseDetail data = DownFileService.getInstance(context).getDownFile(getDownUrl());
        return mState = (data == null ? 0 : data.getState());
    }

    public int getPrevState() {
        return mPrevState;
    }

    public GameBaseDetail setPrevState(int state) {
        mPrevState = state;
        return this;
    }

    public String getSuffix() {
        return mSuffix;
    }

    public GameBaseDetail setSuffix(String suffix) {
        this.mSuffix = suffix;
        return this;
    }

    public String getDownPath() {
        return mDownPath;
    }

    public GameBaseDetail setDownPath(String downPath) {
        this.mDownPath = downPath;
        return this;
    }

    public GameBaseDetail setDownInfo(GameBaseDetail downFile) {
        setDownInfoLess(downFile);
        gameDomainBase.setCanUpgrade(downFile.getGameDomainBase().canUpgrade());
        gameDomainBase.setBusinessPackage(downFile.getGameDomainBase().isBusinessPackage());
        return this;
    }


    public GameBaseDetail setDownInfoLess(GameBaseDetail downFile) {
        mDownPath = downFile.getDownPath();
        mDownLength = downFile.getDownLength() == -1 ? 0 : downFile.getDownLength();
        mSuffix = downFile.getSuffix();
        setFileTotalLength(downFile.getFileTotalLength());
        mState = downFile.getState();
        mPrevState = downFile.getPrevState();
        gameDomainBase.setDownUrl(downFile.getDownUrl());
        setPkgSize(downFile.getFileTotalLength());
        // mVersion = downFile.getVersion();
        setVersionCode(downFile.getVersionCode());
        setMd5(downFile.getMd5());
        openTime = downFile.getOpenTime();
        isCoveredApp = downFile.isCoveredApp;
        //// TODO: 2017/6/13  注意有可能值被覆盖没
        gameDomainBase.setDownload_from(downFile.getGameDomainBase().getDownload_from());
        gameDomainBase.setDownload_point(downFile.getGameDomainBase().getDownload_point());
        gameDomainBase.setIs_accepted(downFile.getGameDomainBase().getIs_accepted());

        isOrderWifiDownload = downFile.isOrderWifiDownload;
        return this;
    }

    public long getFileTotalLength() {
        return (mFileTotalLength != 0 ? mFileTotalLength : gameDomainBase.getPkgSize());
    }

    public GameBaseDetail setFileTotalLength(long fileTotalLength) {
        if (fileTotalLength >= 1024) {
            mFileTotalLength = fileTotalLength;
        }
        return this;
    }

    public String getPkgName() {
        return gameDomainBase.getPkgName();
    }

    public GameBaseDetail setPkgName(String pkgName) {
        gameDomainBase.setPkgName(pkgName);
        return this;
    }

    public String getMd5() {
        return gameDomainBase.getMd5();
    }

    public GameBaseDetail setMd5(String md5) {
        gameDomainBase.setMd5(md5);
        return this;
    }

    public boolean isMd5Valid() {
        return getMd5() != null && getMd5() != "" && getMd5() != "0";
    }

    public boolean checkMd5() {
        return AdMd5.checkMd5(getMd5(), getDownPath());
    }

    public String getUpdateContent() {
        return gameDomainBase.getChangeLog();
    }

    public GameBaseDetail setUpdateContent(String updateContent) {
        gameDomainBase.setChangeLog(updateContent);
        return this;
    }

    public String getCategory() {
        if (gameDomainBase.getCatName() == null) {
            return "null";
        } else {
            return gameDomainBase.getCatName();
        }
    }

    /* 用于首页下载按钮点击时提交百度统计的类别 */
    public GameBaseDetail setCategory(String category) {
        gameDomainBase.setCatName(category);
        return this;
    }

    /* 小编点评 */
    public String getReview() {
        if (gameDomainBase.getReviews() == null) {
            return "null";
        } else {
            return gameDomainBase.getReviews();
        }
    }

    /* 2.0版本新增 */
    /* 小编点评 */
    public GameBaseDetail setReview(String review) {
        gameDomainBase.setReviews(review);
        return this;
    }

    public boolean hasGift() {
        return gameDomainBase.hasGift();
    }

    public GameBaseDetail setHasGift(boolean hasGift) {
        gameDomainBase.setHasGift(hasGift);
        return this;
    }

    public boolean hasStrategy() {
        return gameDomainBase.hasStrategy();
    }

    public GameBaseDetail setHasStrategy(boolean hasStrategy) {
        gameDomainBase.setHasStrategy(hasStrategy);
        return this;
    }


    public String getDescription() {
        return gameExtraDetail.getDescription();
    }


    public GameBaseDetail setDescription(String description) {
        gameExtraDetail.setDescription(description);
        return this;
    }

    public List<String> getScreenshotUrls() {
        return gameExtraDetail.getScreenShots();
    }

    public GameBaseDetail setScreenshotUrls(List<String> screenshotUrls) {
        gameExtraDetail.setScreenShots(screenshotUrls);
        return this;
    }

    public List<GiftDomainDetail> getGiftList() {
        return gameExtraDetail.getGifts();
    }

    public List<GameDomainBaseDetail> getRecommendList() {
        return gameExtraDetail.getRecommendedGames();
    }

    public GameBaseDetail setRecommendList(List<GameDomainBaseDetail> recommendList) {
        gameExtraDetail.setRecommendedGames(recommendList);
        return this;
    }

    public GameBaseDetail setGiftList(List<GiftDomainDetail> giftList) {
        gameExtraDetail.setGifts(giftList);
        return this;
    }

    public String getUpdated_at() {
        return gameExtraDetail.getUpdated_at();
    }

    public GameBaseDetail setUpdated_at(String updated_at) {
        gameExtraDetail.setUpdated_at(updated_at);
        return this;
    }

    public int getCommentCnt() {
        return gameExtraDetail.getCommentCnt();
    }

    public GameBaseDetail setCommentCnt(int commentCnt) {
        gameExtraDetail.setCommentCnt(commentCnt);
        return this;
    }

    public String getDownloadFailedReason() {
        return downloadFailedReason;
    }

    public void setDownloadFailedReason(String downloadFailedReason) {
        this.downloadFailedReason = downloadFailedReason;
    }

    @Override
    public int compareTo(GameBaseDetail another) {
        if (this.getState() < another.getState()) {
            return (this.getState() - another.getState());
        }
        if (this.getState() > another.getState()) {
            return (this.getState() - another.getState());
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return ((GameBaseDetail) obj).getId() == getId() && getPkgName().equals(((GameBaseDetail) obj).getPkgName());
    }

    @Override
    public GameBaseDetail clone() {
        GameBaseDetail game = null;
        try {
            game = (GameBaseDetail) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return game;
    }
//    @Override
//    public String toString() {
//        return "GameBaseDetail [mId=" + getId() + ", mName=" + getName() + ", mVersion=" + getVersion() + ", " + "mLogoUrl=" + getLogoUrl() + ", mDownUrl=" + getDownUrl() + "]";
//    }
}

