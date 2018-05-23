package cn.lt.game.event;

/**
 * Created by JohnsonLin on 2017/7/26.\
 * 跳转到内容TAB事件
 */

public class JumpToContentTabEvent {
    public String url;

    public JumpToContentTabEvent(String url) {
        this.url = url;
    }
}
