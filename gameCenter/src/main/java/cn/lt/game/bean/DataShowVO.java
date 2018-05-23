package cn.lt.game.bean;

import android.text.TextUtils;

import cn.lt.game.lib.netdata.BaseBean;
import cn.lt.game.lib.util.PopWidowManageUtil;

/**
 * Created by Administrator on 2015/11/12.
 */
public class DataShowVO extends BaseBean {
    private String click_type;
    private DataShowBean data;

    public String getClick_type() {
        return click_type;
    }

    public void setClick_type(String click_type) {
        this.click_type = click_type;
    }

    public String getMark() {
        return data.getMark();
    }

    public void setMark(String mark) {
        data.setMark(mark);
    }

    public String getColor() {
        return data.getColor();
    }

    public void setColor(String color) {
        data.setColor(color);
    }

    public String getImage_url() {
        return data.getImage_url();
    }

    public void setImage_url(String image_url) {
        data.setImage_url(image_url);
    }

    public String getId() {
        return data.getId();
    }

    public void setId(String id) {
        data.setId(id);
    }

    public String getUrl() {
        return data.getUrl();
    }

    public void setUrl(String url) {
        data.setUrl(url);
    }

    public String getPage_name() {
        return data.getPage_name();
    }

    public void setPage_name(String page_name) {
        data.setPage_name(page_name);
    }

    public String getTitle() {
        return data.getTitle();
    }

    public void setTitle(String title) {
        data.setTitle(title);
    }

    public String getSummary() {
        return data.getSummary();
    }

    public void setSummary(String summary) {
        data.setSummary(summary);
    }

    public String getBig_image_url() {
        return data.getBig_image_url();
    }

    public void setBig_image_url(String big_image_url) {
        data.setBig_image_url(big_image_url);
    }

    public String getHigh_resource(){
        return data.getHigh_resource();
    }

    public String getRealClickType() {
        if (TextUtils.isEmpty(high_click_type)) {
            return click_type;
        }

        // 热点相关跳转需要判断开关是否有效，否则使用低版本跳转
        if (high_click_type.equals("hot_tab") || high_click_type.equals("hot_detail")) {
            if (!PopWidowManageUtil.hotContentIsReady()) {
                high_click_type = "";
                return click_type;
            }
        }

        return high_click_type;
    }

}
