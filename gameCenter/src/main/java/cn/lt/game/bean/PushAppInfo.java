package cn.lt.game.bean;

/**
 * Created by Administrator on 2015/12/30.
 */
public class PushAppInfo extends PushBaseBean {
    private GameInfoBean game;
    private String app_channel;

    public GameInfoBean getGame() {
        return game;
    }

    public void setGame(GameInfoBean game) {
        this.game = game;
    }

    public String getApp_channel() {
        return app_channel;
    }

    public void setApp_channel(String app_channel) {
        this.app_channel = app_channel;
    }
}
