package cn.lt.game.bean;

import java.util.List;

import cn.lt.game.lib.netdata.BaseBean;

/**
 * 游戏信息
 * Created by Administrator on 2015/11/10.
 */
public class GameInfoBean extends BaseBean {
    private String id;
    private String name;
    private String package_name;
    private String package_md5;
    private long package_size;
    private String version_name;
    private int version_code;
    private String icon_url;
    private String changelog;
    private String download_url;
    private String download_count;
    private String mark;
    private String reviews;//*
    private boolean business_package;
    private boolean can_upgrade;      // 是否在白名单
    // 运营标识数组
    private List<String> flags;
    // 角标图片(时下热门)
    private String symbol_url;

    //推送里面有这个字段
    private String forum_id;



    //积分活动新加字段
    private int download_point;
    private int is_accepted;//1领取0未领取
    private String download_from;

    //搜素deeplink新增字段
    private DeeplinkBean deeplink_app;
    private String deeplink;

    public String getDeeplink() {
        return deeplink;
    }

    public void setDeeplink(String deeplink) {
        this.deeplink = deeplink;
    }

    public DeeplinkBean getDeeplink_app() {
        return deeplink_app;
    }

    public void setDeeplink_app(DeeplinkBean deeplink_app) {
        this.deeplink_app = deeplink_app;
    }

    public String getDownload_from() {
        return download_from;
    }

    public void setDownload_from(String download_from) {
        this.download_from = download_from;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCat_name() {
        return "";
    }

    public void setCat_name(String cat_name) {
//        this.cat_name = cat_name;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getPackage_md5() {
        return package_md5;
    }

    public void setPackage_md5(String package_md5) {
        this.package_md5 = package_md5;
    }

    public long getPackage_size() {
        return package_size;
    }

    public void setPackage_size(long package_size) {
        this.package_size = package_size;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getDownload_count() {
        return download_count;
    }

    public void setDownload_count(String download_count) {
        this.download_count = download_count;
    }

    public String getCorner_url() {
        return "";
    }

    public int getGroup_id() {
        return 0;
    }

    public void setGroup_id(int group_id) {
//        this.group_id = group_id;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public boolean isHas_gifts() {
        return false;
    }

    public void setHas_gifts(boolean has_gifts) {
//        this.has_gifts = has_gifts;
    }

    public int getGift_total() {
        return 0;
    }

    public void setGift_total(int gift_total) {
//        this.gift_total = gift_total;
    }

    public float getComments() {
        return 0;
    }

    public void setComments(int comments) {
//        this.comments = comments;
    }

    public String getForum_id() {
        return forum_id;
    }

    public void setForum_id(String forum_id) {
        this.forum_id = forum_id;
    }

    public boolean isBusiness_package() {
        return business_package;
    }

    public void setBusiness_package(boolean business_package) {
        this.business_package = business_package;
    }

    public boolean can_upgrade() {
        return can_upgrade;
    }

    public void setCan_upgrade(boolean can_upgrade) {
        this.can_upgrade = can_upgrade;
    }

    public List<String> getFlags() {
//        ArrayList<String> list = new ArrayList<>();
//        double random = Math.random();
//
//        if (random > 0.8) {
//            list.add("gift");
//        } else if (random > 0.6) {
//            list.add("bbs");
//        } else if (random > 0.4) {
//            list.add("strategy");
//        } else {
//            list.add("official");
//        }
//        return random > 0.2 ? list : null;
        return flags;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }

    public String getSymbol_url() {
        return symbol_url;
    }

    public void setSymbol_url(String symbol_url) {
        this.symbol_url = symbol_url;
    }
}
