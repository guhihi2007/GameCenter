package cn.lt.game.event;

import cn.lt.game.model.GameBaseDetail;

/**
 * Created by wcn on 2016/3/17.
 * 下载进度更新事件
 */
public class DownloadUpdateEvent {
    public static final int EV_UPDATE = 0;//进度更新事件
    public static final int EV_DELETE = 1;//下载任务删除事件
    public static final int EV_IGNORE_UPGRADE = 2;//忽略升级事件
    public static final int EV_CANCLE_IGNORE_UPGRADE = 3;//取消忽略升级事件

    public DownloadUpdateEvent() {}
    public DownloadUpdateEvent(GameBaseDetail game) {
        this.game = game;
        this.ev = EV_UPDATE;
    }
    public DownloadUpdateEvent(GameBaseDetail game, int ev) {
        this.game = game;
        this.ev = ev;
    }

    public GameBaseDetail game;
    public int ev;
}
