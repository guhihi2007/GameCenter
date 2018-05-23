package cn.lt.game.event;

/**
 * Created by wenchao on 2015/8/28.
 * 网络状态改变事件
 */
public class NetworkChangeEvent {

    public NetworkChangeEvent(int type){
        this.type = type;
    }

    /**
     * 网络类型
     * @link ConnectivityManager.TYPE_WIFI
     * @link ConnectivityManager.TYPE_MOBILE
     */
    public int type;

}
