package cn.lt.game.bean;


import cn.lt.game.lib.netdata.BaseBean;

/**
 * Created by ATian on 2016/7/8.
 *
 * @des 弹窗配置信息
 */
public class ConfigureBean extends BaseBean {
    private Configure auto_install;  //自动装
    private Configure client_update;  //客户端升级
    private Configure selection_play;  //精选必备
    private Configure auto_upgrade;       //应用自动升级
    private Configure floating_ads;       //浮层广告
    private Configure hot_content;  //热点开关
    private Configure guangdiantong_of_tencent;//开屏配置

    public Configure getAuto_install() {
        return auto_install;
    }

    public void setAuto_install(Configure auto_install) {
        this.auto_install = auto_install;
    }

    public Configure getClient_update() {
        return client_update;
    }

    public void setClient_update(Configure client_update) {
        this.client_update = client_update;
    }

    public Configure getSelection_play() {
        return selection_play;
    }

    public void setSelection_play(Configure selection_play) {
        this.selection_play = selection_play;
    }

    public Configure getAuto_upgrade() {
        return auto_upgrade;
    }

    public void setAuto_upgrade(Configure auto_upgrade) {
        this.auto_upgrade = auto_upgrade;
    }

    public Configure getFloating_ads() {
        return floating_ads;
    }

    public void setFloating_ads(Configure floating_ads) {
        this.floating_ads = floating_ads;
    }

    public Configure getHot_content() {
        return hot_content;
    }

    public void setHot_content(Configure hot_content) {
        this.hot_content = hot_content;
    }

    public Configure getGuangdiantong_of_tencent() {
        return guangdiantong_of_tencent;
    }

    public void setGuangdiantong_of_tencent(Configure guangdiantong_of_tencent) {
        this.guangdiantong_of_tencent = guangdiantong_of_tencent;
    }
}
