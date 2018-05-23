package cn.lt.game.event;

/**
 * Created by LinJunSheng on 2017/3/13.
 * 点击安装时，apk不存在导致需要重新下载的event
 */

public class ApkNotExistEvent {
    public String curPage;

    public ApkNotExistEvent(String curPage) {
        this.curPage = curPage;
    }
}
