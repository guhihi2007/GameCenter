package cn.lt.game.net;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.UUID;

import cn.lt.game.application.MyApplication;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.lib.util.StorageSpaceDetection;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.net.NetUtils;

public class Params {
    public String uuid;
    String imei;
    String version;
    int version_code;
    String os_version;
    String device;
    String metrics;
    String channel;
    String brand;//手机品牌
    long memorySize;// 内存剩余空间
    boolean hasSim;// 有无sim卡
    String clientInstallTime;// 客户端首次安装时间
    String clientLastUpdateTime;// 客户端最后一次升级时间
    String region;
    String city;
    String country;
    boolean isWiFi;

    public Params(Context context) {
        MyApplication application = (MyApplication) (context.getApplicationContext());
        SharedPreferencesUtil share = new SharedPreferencesUtil(context);
        uuid = share.get(SharedPreferencesUtil.netUUID);
        if (uuid.equals("")) {
            uuid = UUID.randomUUID().toString();
            share.add(SharedPreferencesUtil.netUUID, uuid);
        }
        imei = MyApplication.imei;
        LogUtils.i("Erosion", "imei=====" + imei);
        version = AppUtils.getVersionName(application);
        version_code = AppUtils.getVersionCode(application);
        os_version = Utils.getAndroidSDKVersion();
        device = Utils.getDeviceName();
        if (context instanceof Activity) {
            metrics = Utils.getScreenWidth(context) + "*" + Utils.getScreenHeight(context);
        } else {
            metrics = MyApplication.width + "*" + MyApplication.height;
        }
        Log.i("Params", "metrics:" + metrics);
        channel = Constant.CHANNEL;
        brand = Utils.getBrand();
//		factory = Build.BRAND;

        memorySize = StorageSpaceDetection.getMemorySize_M();
        hasSim = AppUtils.hasSIMCard(context);
        clientInstallTime = TimeUtils.getStringToDateHaveHour(AppUtils.getClientInstallTime());
        clientLastUpdateTime = TimeUtils.getStringToDateHaveHour(AppUtils.getClientLastUpdateTime());
        region = MyApplication.application.region;
        city = MyApplication.application.city;
        country = MyApplication.application.country;
        isWiFi = NetUtils.isWifi(application);
    }
}