package cn.lt.game.bean;

import cn.lt.game.lib.netdata.BaseBean;
/**
 * Created by JohnsonLin on 2017/10/31.
 * 推送数据bean，所有类型共性字段都在此
 */

public class PushBaseBean extends BaseBean {
    private String id;

    private String icon;

    private String image;

    //1为默认，2为小图，3为大图(必须有默认值1)
    private String notice_style = "1";

    private String main_title;

    private String sub_title;

    //title
    private String title;

    //简介
    private String summary;

    //更新时间
    private String updated_at;

    /** 跳转的url(公用)*/
    private String url;

    /** 当前通知的ID*/
    private String notice_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNotice_style() {
        return notice_style;
    }

    public void setNotice_style(String notice_style) {
        this.notice_style = notice_style;
    }

    public String getMain_title() {
        return main_title;
    }

    public void setMain_title(String main_title) {
        this.main_title = main_title;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNotice_id() {
        return notice_id;
    }

    public void setNotice_id(String notice_id) {
        this.notice_id = notice_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
