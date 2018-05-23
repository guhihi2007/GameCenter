package cn.lt.game.bean;

import cn.lt.game.lib.netdata.BaseBean;

/**
 * Created by Administrator on 2015/11/14.
 */
public class GameCommentBean extends BaseBean {
    protected String nickname;
    protected String avatar;
    protected String content;
    protected String device;
    protected String star;
    protected String created_at;


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
