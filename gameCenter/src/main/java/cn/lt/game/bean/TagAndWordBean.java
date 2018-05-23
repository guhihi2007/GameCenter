package cn.lt.game.bean;

import cn.lt.game.lib.netdata.BaseBean;

/**
 * 标签 和 搜索的热门词汇
 * Created by Administrator on 2015/11/13.
 */
public class TagAndWordBean extends BaseBean {
    private String id;
    private String title;
    private String icon_url;
    private String word;
    private String gift_total;

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

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getGift_total() {
        return gift_total;
    }

    public void setGift_total(String gift_total) {
        this.gift_total = gift_total;
    }
}
