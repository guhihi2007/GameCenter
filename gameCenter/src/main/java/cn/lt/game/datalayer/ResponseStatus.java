package cn.lt.game.datalayer;

/**
 * Created by Administrator on 2015/12/10.
 */
public class ResponseStatus {
    public int responseCode = 0;  //0表示正确
    public String errMessage = null;  //错误信息
    public boolean isCache = false;  //是否是缓存数据

    public ResponseStatus() { /**/ }
    public ResponseStatus(int errCode, String errMessage) {
        this.responseCode = errCode;
        this.errMessage = errMessage;
    }

    public ResponseStatus(int errCode, String errMessage,boolean isCache) {
        this.responseCode = errCode;
        this.errMessage = errMessage;
        this.isCache = isCache;
    }

    public ResponseStatus setIsCache(boolean isCache) {
        this.isCache = isCache;
        return this;
    }
}
