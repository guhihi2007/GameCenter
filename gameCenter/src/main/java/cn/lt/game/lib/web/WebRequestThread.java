package cn.lt.game.lib.web;

import android.os.Handler;
import android.text.TextUtils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

import cn.lt.game.lib.netdata.ErrorFlag;
import cn.lt.game.lib.util.LogUtils;


public class WebRequestThread implements Runnable {

    private final HttpUriRequest request;
    private final AbstractHttpClient client;
    private final Handler handler;
    private final WebCallBackBase callback;

    public WebRequestThread(HttpUriRequest request, AbstractHttpClient client, WebCallBackBase callback) {
        super();
        this.request = request;
        this.client = client;
        this.callback = callback;
        this.handler = null;
    }

    public WebRequestThread(HttpUriRequest request, AbstractHttpClient client, Handler handler, WebCallBackBase callback) {
        super();
        this.request = request;
        this.client = client;
        this.handler = handler;
        this.callback = callback;
    }

    @Override
    public void run() {
        LogUtils.e("junjun", "run===================:" );
        requestRun();
    }

    public void requestRun() {
        HttpResponse resp;
        Header[] headerArr = null;
        String strResp = "";
        try {
            LogUtils.e("junjun", "请求url:" + request.getURI() + "    ThreadName:" + Thread.currentThread().getName());
            resp = client.execute(request);
            if (isHttpRespOk(resp)) {
                strResp = EntityUtils.toString(resp.getEntity());
                headerArr = resp.getAllHeaders();
                callback.setHeaderArr(headerArr);
                if (null != handler) {
                    handler.sendMessage(handler.obtainMessage(WebClient.HTTP_SUCCESS, new Object[]{callback, strResp, request.getURI()}));
                } else {
                    callback.executeFilter(strResp, request.getURI());
                }
            } else {
//				strResp = EntityUtils.toString(resp.getEntity());
//				LogText.writeLog(strResp);
                strResp = EntityUtils.toString(resp.getEntity());
                try {
                    if (!TextUtils.isEmpty(strResp)) {
                        JSONObject json = new JSONObject(strResp);
                        strResp = json.optString("message", "异常-:" + resp.getStatusLine().getStatusCode());
                    } else {
                        strResp = "异常--:" + resp.getStatusLine().getStatusCode();
                    }
                } catch (JSONException e) {
                    strResp = "异常---:" + strResp + "\t" + resp.getStatusLine().getStatusCode();
                }
                int code = resp.getStatusLine().getStatusCode();
                if (isUnAuthorize(resp)) {
                    code = ErrorFlag.userLogout;
                }
                //此处是为了和老接口返回的一样
                if (null != handler) {
                    handler.sendMessage(handler.obtainMessage(WebClient.HTTP_FAILURE, new Object[]{callback, code, new Exception(strResp)}));
                } else {
                    callback.onFailure(code, new Exception(strResp));
                    LogUtils.e("junjun", "Exception失败->"+new Exception(strResp));
                }
            }
        } catch (IOException e) {
            if (null != handler) {
                handler.sendMessage(handler.obtainMessage(WebClient.HTTP_FAILURE, new Object[]{callback, ErrorFlag.netError, new Exception("网络连接失败")}));
            } else {
                LogUtils.e("junjun", "网络连接失败->");
                callback.onFailure(ErrorFlag.netError, new Exception("网络连接失败"));

            }
        } finally {
            request.abort();
        }
    }

    private boolean isHttpRespOk(HttpResponse resp) {
        return resp.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK || resp.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_CREATED;
    }

    private boolean isUnAuthorize(HttpResponse resp) {
        return resp.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED;
    }

    public WebCallBackBase getCallback() {
        return callback;
    }

}
