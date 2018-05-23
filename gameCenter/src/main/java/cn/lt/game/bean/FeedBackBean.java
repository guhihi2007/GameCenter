package cn.lt.game.bean;

import cn.lt.game.lib.netdata.BaseBean;

/**
 * Created by Administrator on 2015/11/14.
 */
public class FeedBackBean extends BaseBean {
    protected String content;
    protected String identifyUser;
    protected String created_at;
    protected String image_url;
    protected String thumb_url;
    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdentifyUser() {
        return identifyUser;
    }

    public void setIdentifyUser(String identifyUser) {
        this.identifyUser = identifyUser;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }
}
