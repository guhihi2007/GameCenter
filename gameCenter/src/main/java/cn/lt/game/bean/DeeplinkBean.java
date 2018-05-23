package cn.lt.game.bean;

import cn.lt.game.lib.netdata.BaseBean;

/**
 * deeplink搜索信息
 * Created by Administrator on 2015/11/10.
 */
public class DeeplinkBean extends BaseBean {
    private String name;
    private long package_size;
    private String icon_url;
    private String download_count;
    private String reviews;
    private String color;
    private String ad;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPackage_size() {
        return package_size;
    }

    public void setPackage_size(long package_size) {
        this.package_size = package_size;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getDownload_count() {
        return download_count;
    }

    public void setDownload_count(String download_count) {
        this.download_count = download_count;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }
}
