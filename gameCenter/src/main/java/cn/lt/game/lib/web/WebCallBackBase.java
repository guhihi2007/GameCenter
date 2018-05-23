package cn.lt.game.lib.web;

import android.text.TextUtils;
import android.util.Log;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import cn.lt.game.lib.netdata.ErrorFlag;

/**
 * 回调方法的基类,不要直接使用
 */
public abstract class WebCallBackBase implements FileUploadProgressListener {
    private boolean IsListener = true;
    /**
     * 响应头
     */
    private Header[] headerArr;
    /**
     * 过滤器,请求返回的数据会先经过过滤器
     */
    private WebHttpFilter filter;

    private Map<String, ?> param;

    protected WebCallBackBase() {
        filter = new SimpleWebHttpFilter();
    }

    /**
     * 路由方法，进过过滤器的数据会根据这个方法决定后边的传递，由子类根据具体需要来实现
     *
     * @param result
     */
    public abstract void route(String result);

    /**
     * 网络请求出错时调用
     *
     * @param statusCode 异常编号
     * @param error      异常信息
     */
    public abstract void onFailure(int statusCode, Throwable error);

    /**
     * 获取所有的响应头的信息
     *
     * @return 所有的响应头
     */
    public Header[] getHeaderArr() {
        return headerArr;
    }

    /**
     * 根据Key来获取相对应的响应头的值
     *
     * @param key 响应头的Key
     * @return 响应头的值，没有这个值的时候返回null
     */
    public String getHeaderByKey(String key) {
        for (int i = 0; i < headerArr.length; i++) {
            if (headerArr[i].getName().equals(key)) {
                return headerArr[i].getValue();
            }
        }
        return null;
    }

    public int getLastPage(String str) {
        try {
            JSONObject json = new JSONObject(str);
            return json.optInt("last_page", 1);
        } catch (JSONException e) {
            Log.e("DataCenter", "错误信息:" + e.getMessage());
            return 0;
        }
    }

    public int getLastPage() {
        try {
            String xLinks = getHeaderByKey("X-Links");
            if (!TextUtils.isEmpty(xLinks)) {
                JSONObject json = new JSONObject(xLinks);
                return json.optInt("last_page", 1);
            } else {
                return 1;
            }
        } catch (JSONException e) {
            Log.e("DataCenter", "错误信息:" + e.getMessage());
            return 0;
        }
    }

    /**
     * 根据响应头作为map返回
     *
     * @return 响应头
     */
    public Map<String, String> getHeaderToMap() {
        Map<String, String> map = new HashMap<>();
        if (headerArr != null) {
            for (int i = 0; i < headerArr.length; i++) {
                map.put(headerArr[i].getName(), headerArr[i].getValue());
            }
        }
        return map;
    }

    /**
     * 设置响应头，吃方法仅提供给请求线程内部调用
     *
     * @param headerArr 响应头
     */
    public void setHeaderArr(Header[] headerArr) {
        this.headerArr = headerArr;
    }

    /**
     * 设置过滤器，有自定义的过滤器时可以使用
     *
     * @param filter 过滤器
     */
    public void setFilter(WebHttpFilter filter) {
        this.filter = filter;
    }

    /**
     * 执行过滤，对网络返回的数据进行过滤处理，处理数据过程由过滤器的实现而定
     *
     * @param result 请求返回的数据
     * @param uri    此次请求的uri
     */
    public void executeFilter(String result, URI uri) {
        if (TextUtils.isEmpty(result)) {
            Log.e("net", "无返回数据");
            onFailure(ErrorFlag.netError, new Exception("无返回数据"));
            return;
        }
        if (filter != null) {
            result = filter.filterHandler(result, uri);
        }
        route(result);
    }

    public Map<String, ?> getParam() {
        return param;
    }

    public void setParam(Map<String, ?> param) {
        this.param = param;
    }

    @Override
    public void transferred(long uploadSize, long totalSize) {

    }

    @Override
    public boolean getIsListener() {
        return IsListener;
    }

    public void setIsListener(boolean isListener) {
        IsListener = isListener;
    }

}
