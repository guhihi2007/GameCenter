package cn.lt.game.event;

/**
 * Created by ATian on 2017/6/1.
 * 用于通知底部菜单显示小红点
 */

public class RedPointEvent {
    public boolean needShow;

    public RedPointEvent(boolean needShow) {
        this.needShow = needShow;
    }
}
