package cn.lt.game.net;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import cn.lt.game.application.GlobalParams;
import cn.lt.game.application.MyApplication;
import cn.lt.game.lib.netdata.ErrorFlag;
import cn.lt.game.lib.util.AdMd5;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.util.threadpool.LTAsyncTask;
import cn.lt.game.lib.web.WebCallBackBase;
import cn.lt.game.lib.web.WebCallBackToEvent;
import cn.lt.game.lib.web.WebClient;
import cn.lt.game.lib.web.WebClientOfReport;
import cn.lt.game.lib.web.WebClientOfShortTimeout;
import cn.lt.game.net.Host.HostType;

public class Net {

    private volatile static Net mInstance = null;
    private String mParamsValue = null;
    private String mUUID = "abcdefg12345";

    private String baseInfoError = "网络无法连接";//"基础参数获取失败";

    private Net() {
    }

    public static Net instance() {
        if (mInstance == null) {
            synchronized (Net.class) {
                if (mInstance == null) {
                    mInstance = new Net();
                }
            }
        }
        return mInstance;
    }

    public boolean init(Context context, NetIniCallBack callback) {

        // 生成“PARAMS"字段的值
        generateParamsValue(context);

        // 向服务器请求通信用基本网络信息
        return requestNetBaseInfo(callback, false);
    }

    public void executeGet(final HostType host, final String uri, final Map<String, String> params, final WebCallBackBase callBack) {
        if (callBack != null) {
            callBack.setParam(params);
        }
        if (TextUtils.isEmpty(Host.getHost(host))) {
            requestNetBaseInfo(new NetIniCallBack() {

                @Override
                public void callback(int code) {
                    if (code == 0) {
                        executeGet(host, uri, params, callBack);
                    } else {
                        callBack.onFailure(ErrorFlag.netError, new Exception(baseInfoError));
                    }
                }
            }, false);
        } else {
            WebClient client = WebClient.singleton();
            client.doGet_messagePack(Host.getHost(host) + uri, params,
                    getCustomHeader(uri, GlobalParams.salt.getSalt(host)), callBack,false);
        }
    }

    public void executeGet(String url, Map<String, String> params, WebCallBackBase callBack) {
        if (callBack != null) {
            callBack.setParam(params);
        }
        WebClient client = WebClient.singleton();
        client.doGet_messagePack(url, null, getCustomHeader("", null), callBack,false);
    }

    public void executeGet(HostType hostType, String uri, WebCallBackBase callBack) {
        executeGet(hostType, uri, null, callBack);
    }

    /**
     * 此方法目前仅启动页使用（请求超时设定只为3秒），其他地方慎用
     */
    public void executeGetOfShortTimeout(final HostType host, final String uri, final Map<String, String> params, final WebCallBackBase callBack) {
        if (callBack != null) {
            callBack.setParam(params);
        }
        if (TextUtils.isEmpty(Host.getHost(host))) {
            requestNetBaseInfo(new NetIniCallBack() {

                @Override
                public void callback(int code) {
                    if (code == 0) {
                        executeGet(host, uri, params, callBack);
                    } else {
                        callBack.onFailure(ErrorFlag.netError, new Exception(baseInfoError));
                    }
                }
            }, false);
        } else {
            WebClientOfShortTimeout client = WebClientOfShortTimeout.singleton();
            client.doGet_messagePack(Host.getHost(host) + uri, params, getCustomHeader(uri, GlobalParams.salt.getSalt(host)), callBack,false);
        }
    }

    public void executePost(final HostType host, final String uri, final Map<String, ?> params, final WebCallBackBase callBack) {
//        System.out.println("发送请求1"+Host.getHost(host));
        callBack.setParam(params);
        if (TextUtils.isEmpty(Host.getHost(host))) {
            requestNetBaseInfo(new NetIniCallBack() {

                @Override
                public void callback(int code) {
                    if (code == 0) {
                        executePost(host, uri, params, callBack);
                    } else {
                        callBack.onFailure(ErrorFlag.netError, new Exception(baseInfoError));
                    }
                }
            }, false);
        } else {
            WebClient client = WebClient.singleton();
            client.doPost_messagePack(Host.getHost(host) + uri, params, getCustomHeader(uri, GlobalParams.salt.getSalt(host)), callBack);
        }
    }
    public void executeReportData(final HostType host, final String uri, final Map<String, ?> params, final WebCallBackBase callBack) {
//        System.out.println("发送请求1"+Host.getHost(host));
        callBack.setParam(params);
        if (TextUtils.isEmpty(Host.getHost(host))) {
            requestNetBaseInfo(new NetIniCallBack() {

                @Override
                public void callback(int code) {
                    if (code == 0) {
                        executeReportData(host, uri, params, callBack);
                    } else {
                        callBack.onFailure(ErrorFlag.netError, new Exception(baseInfoError));
                    }
                }
            },true);
        } else {
            WebClient client = WebClient.singleton();
            client.doPost_ReportData(Host.getHost(host) + uri, params, getCustomHeader(uri, GlobalParams.salt.getSalt(host)), callBack);
        }
    }
    public void executePost(HostType hostType, String uri, WebCallBackBase callBack) {
        executePost(hostType, uri, null, callBack);
    }

    public void executeDelete(final HostType host, final String uri, final WebCallBackBase callBack) {
        if (TextUtils.isEmpty(Host.getHost(host))) {
            requestNetBaseInfo(new NetIniCallBack() {

                @Override
                public void callback(int code) {
                    if (code == 0) {
                        executeDelete(host, uri, callBack);
                    } else {
                        callBack.onFailure(ErrorFlag.netError, new Exception(baseInfoError));
                    }
                }
            }, false);
        } else {
            WebClient client = WebClient.singleton();
            client.doDelect_messagePack(Host.getHost(host) + uri, getCustomHeader(uri, GlobalParams.salt.getSalt(host)), callBack);
        }
    }

    public void executePut(final HostType host, final String uri, final Map<String, String> params, final WebCallBackBase callBack) {
        callBack.setParam(params);
        if (TextUtils.isEmpty(Host.getHost(host))) {
            requestNetBaseInfo(new NetIniCallBack() {

                @Override
                public void callback(int code) {
                    if (code == 0) {
                        executePut(host, uri, params, callBack);
                    } else {
                        callBack.onFailure(ErrorFlag.netError, new Exception(baseInfoError));
                    }
                }
            }, false);
        } else {
            WebClient client = WebClient.singleton();
            client.doPut_messagePack(Host.getHost(host) + uri, params, getCustomHeader(uri, GlobalParams.salt.getSalt(host)), callBack);
        }
    }


    public void executePut(HostType hostType, String uri, WebCallBackBase callBack) {
        executePut(hostType, uri, null, callBack);
    }

    public void executeSendBigFile(final HostType host, final String uri, final File file, final WebCallBackBase callBack) {
        if (TextUtils.isEmpty(Host.getHost(host))) {
            requestNetBaseInfo(new NetIniCallBack() {

                @Override
                public void callback(int code) {
                    if (code == 0) {
                        executeSendBigFile(host, uri, file, callBack);
                    } else {
                        callBack.onFailure(ErrorFlag.netError, new Exception(baseInfoError));
                    }
                }
            }, false);
        } else {
            WebClient client = WebClient.singleton();
            client.sendBigFile(Host.getHost(host) + uri, file, getCustomHeader(uri, GlobalParams.salt.getSalt(host)), callBack);
        }
    }

    private Map<String, String> getCustomHeaderBase() {
        Map<String, String> map = new HashMap<String, String>();
        if (mParamsValue == null) {
            generateParamsValue(MyApplication.application);
        }
        try {
            JSONObject json = new JSONObject(mParamsValue);
            if (getToken() != null) {
                json.put("access_token", getToken());
            } else {
                json.put("access_token", "");
            }
            if(!TextUtils.isEmpty(MyApplication.application.region)){
                json.put("region", MyApplication.application.region);
            }else{
                json.put("region", "");
            }
            if(!TextUtils.isEmpty(MyApplication.application.city)){
                json.put("city", MyApplication.application.city);
            }else{
                json.put("city", "");
            }
            if(!TextUtils.isEmpty(MyApplication.application.country)){
                json.put("country", MyApplication.application.country);
            }else{
                json.put("country", "");
            }
                json.put("isWiFi", NetUtils.isWifi(MyApplication.application));
            LogUtils.d("region", "存入头country=>" + MyApplication.application.country+"-region=>" + MyApplication.application.region+"-city=>"
                    + MyApplication.application.city+"-isWiFi=>" + NetUtils.isWifi(MyApplication.application));
            mParamsValue = json.toString();
        } catch (JSONException e) {
        }
//        map.put("PARAMS", mParamsValue);
        map.put("X-Client-Info", mParamsValue);
        return map;
    }

    private Map<String, String> getCustomHeader(String uri, String salt) {
        Map<String, String> map = getCustomHeaderBase();
        String sign = generateSignValue(uri, salt);
        if (sign != null) {
            map.put("SIGN", sign);
        }
        if (salt != null) {
            map.put("SALT", salt);
        }
        if (getToken() != null) {
            map.put("TOKEN", getToken());
        }
        //map.put("If-Modify-Since", new Date().toString());
        return map;
    }

    public boolean requestNetBaseInfo(final NetIniCallBack callback, boolean isReport) {
        WebClient.singleton().cancelAllTask();//Base请求前先取消已经存在的任务，防止堵塞启动页
        WebClient.singleton().doGet_messagePack(Host.getHost(), null, getCustomHeaderBase(), new WebCallBackToEvent() {

            @Override
            public void onSuccess(String result) throws JSONException {
                JSONObject json = new JSONObject(result);
                Host.setHost(HostType.GCENTER_HOST, json.optString("gcenter_host", null));
                Host.setHost(HostType.UCENETER_BASE_HOST, json.optString("ucenter_host", null));
                Host.setHost(HostType.FORUM_HOST, json.optString("forum_host", null));
                Host.setHost(HostType.DCENTER_HOST, json.optString("dcenter_host", null));
                Host.setHost(HostType.CDN_LIMIT_HOST, json.optString("cdnlimit_host", null));
                Host.setHost(HostType.HOT_HOST,json.optString("hot_host",null));
                if (callback != null) {
                    callback.callback(0);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                if (callback != null) {
                    callback.callback(ErrorFlag.netError);
                }

            }
        },isReport);
        return true;
    }

    private String generateParamsValue(final Context context) {
        Params params = new Params(context);
        mUUID = params.uuid;
        Gson gson = new Gson();
        mParamsValue = gson.toJson(params);
        return mParamsValue;
    }

    private String generateSignValue(String uri, String salt) {
        if (TextUtils.isEmpty(salt) || salt.equalsIgnoreCase("null")) {
            return null;
        }
        return AdMd5.MD5(salt + uri + mUUID + salt);
    }


    public String getToken() {
        return GlobalParams.token;
    }

    public void setUcenterSalt(String salt) {
        GlobalParams.salt.setUserSalt(salt);
    }

    public String getetUcenterSalt() {
        return GlobalParams.salt.getSalt(Host.HostType.UCENETER_HOST);
    }

    public void sendUDPForWifi(Context context) {
        if (NetUtils.isWifi(context)) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

            final String ipAddress = Formatter.formatIpAddress(dhcpInfo.serverAddress);

            new LTAsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    byte[] message = "ltbl-game-center".getBytes();
                    try {
                        DatagramSocket ds = new DatagramSocket();
                        InetAddress address = InetAddress.getByName(ipAddress);
                        DatagramPacket dp = new DatagramPacket(message, message.length, address, 80);
                        ds.send(dp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();

        }
    }

}
