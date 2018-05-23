package cn.lt.game.bean;

import android.text.TextUtils;

import cn.lt.game.lib.netdata.BaseBean;
import cn.lt.game.lib.util.PopWidowManageUtil;
import cn.lt.game.model.EntryPages;
import cn.lt.game.model.PageMap;

/**
 * Created by Administrator on 2015/11/12.
 */
public class DataShowBean extends BaseBean {
    // 角标示文字
    private String mark;
    // 标示背景颜色
    private String color;
    // 图片地址
    private String image_url;
    // ID 适用于 游戏ID，专题ID，分类ID,可为空
    private String id;
    // h5地址（可为空）
    private String url;
    // 页面别名
    private String page_name;
    // 标题
    private String title;
    // 活动中使用的大图片
    private String big_image_url;
    // 摘要
    private String summary;

    private String stocked_at;

    private String unstocked_at;

    private String page_name_410;

    private String image;

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPage_name_410() {
        return page_name_410;
    }

    public void setPage_name_410(String page_name_410) {
        this.page_name_410 = page_name_410;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPage_name() {
        return page_name;
    }

    public void setPage_name(String page_name) {
        this.page_name = page_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBig_image_url() {
        return big_image_url;
    }

    public void setBig_image_url(String big_image_url) {
        this.big_image_url = big_image_url;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStocked_at() {
        return stocked_at;
    }

    public void setStocked_at(String stocked_at) {
        this.stocked_at = stocked_at;
    }

    public String getUnstocked_at() {
        return unstocked_at;
    }

    public void setUnstocked_at(String unstocked_at) {
        this.unstocked_at = unstocked_at;
    }

    public void setRealClickType() {
        if (TextUtils.isEmpty(high_click_type)) {
            return;
        }

        if (!PageMap.instance().isIdentifiable(high_click_type)) {
            high_click_type = "";// 不可识别的类型，去掉搞本本跳转
            return;
        }

        // 热点相关跳转需要判断开关是否有效，否则使用低版本跳转
        if (high_click_type.equals(EntryPages.hot_tab) || high_click_type.equals(EntryPages.hot_detail)) {
            if (!PopWidowManageUtil.hotContentIsReady()) {
                high_click_type = "";// 去掉搞本本跳转
            }
        }
    }

}
