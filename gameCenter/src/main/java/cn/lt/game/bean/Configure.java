package cn.lt.game.bean;

/**
 * Created by ltbl on 2016/9/1.
 */
public class Configure {
    private int status; //按钮状态
    private Long frequency;     //弹框频率
    private Long ad_screen_off_delay_time;

    public Long getAd_screen_off_delay_time() {
        return ad_screen_off_delay_time;
    }

    public void setAd_screen_off_delay_time(Long ad_screen_off_delay_time) {
        this.ad_screen_off_delay_time = ad_screen_off_delay_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getFrequency() {
        return frequency;
    }

    public void setFrequency(Long frequency) {
        this.frequency = frequency;
    }
}
