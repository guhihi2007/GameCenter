package cn.lt.game.datalayer;

/**
 * Created by Administrator on 2015/12/10.
 */
public class RequestMode {
    /**
     * //是否获取缓存数据
     */
    public boolean isRequestCache = false;
    /**
     * //是否请求网络数据
     */
    public boolean isRequestNet = true;
    /**
     * //是否向上层通知网络响应数据
     */
    public boolean isNotifyNetResponse = true;
    /**
     * //是否存储网络响应数据到缓存
     */
    public boolean isSaveNetResponseIntoCache = false;
    /**
     * //是否存储网络响应数据到缓存
     */
    public boolean isScheduleListening = false;
}
