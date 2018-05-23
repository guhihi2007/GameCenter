package cn.lt.game.domain.detail;

import java.io.Serializable;
import java.util.List;

import cn.lt.game.bean.DeeplinkBean;
import cn.lt.game.bean.GameInfoBean;
import cn.lt.game.domain.essence.DomainEssence;
import cn.lt.game.domain.essence.DomainType;

/**
 * Created by Administrator on 2015/11/17.
 */
public class GameDomainBaseDetail extends DomainEssence implements Serializable {
    private final static DomainType DOMAIN_TYPE = DomainType.GAME;

    private String name = null;  //游戏名称
    private String catName = null;//游戏所属分类名称
    private String pkgName = null;//包名
    private String md5 = null;//安装包MD5值
    private long pkgSize = -1;//包大小，单位Byte
    private String verName = null;//版本名称
    private int verCode = -1;//版本代码
    private String iconUrl = null;//icon图片链接
    private String changeLog = null;//更新信息
    private String reviews = null;//小编点评
    private String downUrl = null;//下载链接
    private String downCnt = null;//下载数
    private String cornerUrl = null;//角标链接
    private int groupId = 0;//社区小组id，为0时不存在社区
    private boolean hasGift = false;//1表示存在礼包，0表示不存在
    private String mark = null;//游戏标识（礼包/社区/攻略...）
    private float score = -1; // 评分
    private boolean isBusinessPackage;//是否商务包(用于自动升级是否走CDN)
    private boolean canUpgrade;//是否在白名单(用于自动升级识别)
    private List<String> flags;//运营标志
    private String symbol_url; //运营角标

    private boolean hasStrategy = false;
    public int autoMatchPos ;

    //积分活动新加字段
    private int download_point;
    private int is_accepted; //1已领取 0未领取
    private String download_from;


    //搜素deeplink新增字段
    private DeeplinkBean deeplink_app;
    private String deeplink;

    public DeeplinkBean getDeeplink_app() {
        return deeplink_app;
    }

    public void setDeeplink_app(DeeplinkBean deeplink_app) {
        this.deeplink_app = deeplink_app;
    }

    public String getDeeplink() {
        return deeplink;
    }

    public void setDeeplink(String deeplink) {
        this.deeplink = deeplink;
    }

    public GameDomainBaseDetail() { /**/ }

    public GameDomainBaseDetail(GameInfoBean game) {
        setUniqueIdentifier(game.getId());
        this.name = game.getName();
        this.catName = game.getCat_name();
        this.pkgName = game.getPackage_name();
        this.md5 = game.getPackage_md5();
        this.pkgSize = game.getPackage_size();
        this.verName = game.getVersion_name();
        this.verCode = game.getVersion_code();
        this.iconUrl = game.getIcon_url();
        this.changeLog = game.getChangelog();
        this.reviews = game.getReviews();
        this.downUrl = game.getDownload_url();
        this.downCnt = game.getDownload_count();
        this.cornerUrl = game.getCorner_url();
        this.groupId = game.getGroup_id();
        this.hasGift = game.isHas_gifts();
        this.mark = game.getMark();
        this.score = game.getComments();
        this.isBusinessPackage = game.isBusiness_package();
        this.canUpgrade = game.can_upgrade();
        this.download_from = game.getDownload_from();
        this.download_point = game.getDownload_point();
        this.is_accepted = game.getIs_accepted();
        this.flags = game.getFlags();
        this.symbol_url = game.getSymbol_url();
        this.deeplink = game.getDeeplink();
        this.deeplink_app = game.getDeeplink_app();
    }


    public int getDownload_point() {
        return download_point;
    }

    public void setDownload_point(int download_point) {
        this.download_point = download_point;
    }

    public int getIs_accepted() {
        return is_accepted;
    }

    public void setIs_accepted(int is_accepted) {
        this.is_accepted = is_accepted;
    }

    public String getDownload_from() {
        return download_from;
    }

    public void setDownload_from(String download_from) {
        this.download_from = download_from;
    }

    @Override
    public DomainType getDomainType() {
        return DOMAIN_TYPE;
    }

    public GameDomainBaseDetail setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public GameDomainBaseDetail setCatName(String catName) {
        this.catName = catName;
        return this;
    }

    public String getCatName() {
        return this.catName;
    }

    public GameDomainBaseDetail setPkgName(String pkgName) {
        this.pkgName = pkgName;
        return this;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public GameDomainBaseDetail setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public String getMd5() {
        return md5;
    }

    public GameDomainBaseDetail setPkgSize(long pkgSize) {
        this.pkgSize = pkgSize;
        return this;
    }

    public long getPkgSize() {
        return this.pkgSize;
    }

    public GameDomainBaseDetail setVerName(String verName) {
        this.verName = verName;
        return this;
    }

    public String getVerName() {
        return this.verName;
    }

    public GameDomainBaseDetail setVerCode(int verCode) {
        this.verCode = verCode;
        return this;
    }

    public int getVerCode() {
        return this.verCode;
    }

    public GameDomainBaseDetail setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public GameDomainBaseDetail setChangeLog(String changeLog) {
        this.changeLog = changeLog;
        return this;
    }

    public String getChangeLog() {
        return this.changeLog;
    }

    public GameDomainBaseDetail setDownUrl(String downUrl) {
        this.downUrl = downUrl;
        return this;
    }
    public String getDownUrl() {
        return this.downUrl;
    }
    public GameDomainBaseDetail setDownCnt(String downCnt) {
        this.downCnt = downCnt;
        return this;
    }
    public String getDownCnt() {
        return this.downCnt;
    }
    public GameDomainBaseDetail setCornerUrl(String cornerUrl) {
        this.cornerUrl = cornerUrl;
        return this;
    }
    public String getCornerUrl() {
        return cornerUrl;
    }
    public GameDomainBaseDetail setGroupId(int groupId) {
        this.groupId = groupId;
        return this;
    }
    public int getGroupId() {
        return this.groupId;
    }
    public GameDomainBaseDetail setMark(String mark) {
        this.mark = mark;
        return this;
    }
    public String getMark() {
        return this.mark;
    }
    public String getReviews() {
        return reviews;
    }
    public GameDomainBaseDetail setReviews(String reviews) {
        this.reviews = reviews;
        return this;
    }
    public GameDomainBaseDetail setHasGift(boolean hasGift) {
        this.hasGift = hasGift;
        return this;
    }
    public boolean hasGift() {
        return hasGift;
    }

    public boolean hasStrategy() {
        return hasStrategy;
    }

    public GameDomainBaseDetail setHasStrategy(boolean hasStrategy) {
        this.hasStrategy = hasStrategy;
        return this;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public boolean isBusinessPackage() {
        return isBusinessPackage;
    }

    public void setBusinessPackage(boolean businessPackage) {
        this.isBusinessPackage = businessPackage;
    }

    public boolean canUpgrade() {
        return canUpgrade;
    }

    public void setCanUpgrade(boolean canUpgrade) {
        this.canUpgrade = canUpgrade;
    }

    public List<String> getFlags() {
        return flags;
    }

    public String getSymbol_url() {
        return symbol_url;
    }
}

