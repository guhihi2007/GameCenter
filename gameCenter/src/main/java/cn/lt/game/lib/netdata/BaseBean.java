package cn.lt.game.lib.netdata;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/11/10.
 */
public class BaseBean implements Serializable {
    private String type;
    public boolean isFromWakeUp;

    /** v4.3.0以上跳转类型*/
    protected String high_click_type;

    /** v4.3.0以上跳转数据*/
    protected String high_resource;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHigh_click_type() {
        return high_click_type;
    }

    public void setHigh_click_type(String high_click_type) {
        this.high_click_type = high_click_type;
    }

    public String getHigh_resource() {
        return high_resource;
    }

    public void setHigh_resource(String high_resource) {
        this.high_resource = high_resource;
    }
}
