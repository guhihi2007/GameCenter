package cn.lt.game.bean;

/***
 * 启动页图片跳转
 * Created by JohnsonLin on 2017/6/13.
 * v4.3.0开始用这个新的
 */
public class NewLaunchBean {
    private String click_type;
    private LaunchBean data;

    public String getClick_type() {
        return click_type;
    }

    public void setClick_type(String click_type) {
        this.click_type = click_type;
    }

    public LaunchBean getData() {
        return data;
    }

    public void setData(LaunchBean data) {
        this.data = data;
    }
}
