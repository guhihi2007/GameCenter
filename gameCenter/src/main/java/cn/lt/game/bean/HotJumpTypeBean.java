package cn.lt.game.bean;

/**
 * 热点跳转类型
 * Created by Erosion on 2017/8/3.
 */

public class HotJumpTypeBean {
    private String jump_type;
    private int data; // tab_id
    private int position;
    private String tabId;


    public String getJump_type() {
        return jump_type;
    }

    public void setJump_type(String jump_type) {
        this.jump_type = jump_type;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public int getPos() {
        return position;
    }

    public void setPos(int pos) {
        this.position = pos;
    }

    public String getTab_url() {
        return tabId;
    }

    public void setTab_url(String tab_url) {
        this.tabId = tab_url;
    }
}
