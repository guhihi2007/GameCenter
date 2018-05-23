package cn.lt.game.bean;

/**
 * Created by ltbl on 2017/3/25.
 */

public class SpreadAdBean {
    private String advert_name;//广告名称
    private int status;//开关状态
    private long time;//弹出时间间隔

    public String getAdvert_name() {
        return advert_name;
    }

    public void setAdvert_name(String advert_name) {
        this.advert_name = advert_name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
