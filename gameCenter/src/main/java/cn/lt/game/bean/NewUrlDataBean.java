package cn.lt.game.bean;

import java.io.Serializable;

/**
 * 新的下载地址和md5
 */
public class NewUrlDataBean implements Serializable{
    private String type;
    private NewUrlBean data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public NewUrlBean getNewUrlBean() {
        return data;
    }

    public void setNewUrlBean(NewUrlBean newUrlBean) {
        this.data = newUrlBean;
    }
}
