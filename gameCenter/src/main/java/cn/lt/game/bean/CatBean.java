package cn.lt.game.bean;

import android.text.TextUtils;

import cn.lt.game.lib.netdata.BaseBean;
import cn.lt.game.lib.netdata.BaseBeanList;
import cn.lt.game.lib.util.PopWidowManageUtil;

/**
 * Created by Administrator on 2015/11/13.
 */
public class CatBean extends BaseBean{
    private String id;
    private String title;
    private String color;
    private String image;
    private String image_url;
    private BaseBeanList<TagAndWordBean> tags = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public BaseBeanList<TagAndWordBean> getTags() {
        return tags;
    }

    public void setTags(BaseBeanList<TagAndWordBean> tags) {
        this.tags = tags;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setRealClickType() {
        if (TextUtils.isEmpty(high_click_type)) {
            return;
        }

        // 热点相关跳转需要判断开关是否有效，否则使用低版本跳转
        if (high_click_type.equals("hot_tab") || high_click_type.equals("hot_detail")) {
            if (!PopWidowManageUtil.hotContentIsReady()) {
                high_click_type = "";// 去掉搞本本跳转
            }
        }
    }

}
