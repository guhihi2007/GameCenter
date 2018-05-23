package cn.lt.game.lib.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import cn.lt.game.application.MyApplication;
import cn.lt.game.lib.util.net.NetUtils;

/**
 * Created by chon on 2017/3/22.
 * What? How? Why?
 */

public class ConditionsUtil {
    public static final String TAG = "ScreenMonitorService";

    /**
     * is the pre conditions met?
     *
     * @return met return true
     */
    public static boolean isMet(Context context) {
        // 判断是否系统权限
        if (!PackageUtils.isSystemApplication(context)) {
            LogUtils.e(TAG, "不具备静默安装的权限");
            return false;
        }
        MyApplication.application.setSystemInstall(true);
//        LogUtils.i(TAG,"具备静默安装的权限");

        // 判断网络环境，再进一步操作
        if (!NetUtils.isWifi(context)) {
            LogUtils.e(TAG, "非wifi环境");
            return false;
        }
//        LogUtils.i("wifi环境");

        // 是否电量充足
        if (!isHighLevel(context)) {
            LogUtils.e(TAG, "电量不足");
            return false;
        }

//        LogUtils.i(TAG, "电量充足");

        return true;
    }

    /**
     * 判断电量是否充足
     */
    private static boolean isHighLevel(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        if (batteryStatus == null) {
            return false;
        }

        // 是否在充电
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
//        LogUtils.i(TAG, "手机充电中或者满电~ = " + isCharging);

        //当前剩余电量
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//        LogUtils.i(TAG, "当前剩余电量 = " + level);

        //电量最大值
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//        LogUtils.i(TAG, "电量最大值 = " + scale);

        //电量百分比
        float batteryPct = level / (float) scale;
//        LogUtils.i(TAG, "电量百分比 = " + batteryPct);

        // 是否正在充电或者电量大于30%
        return isCharging || batteryPct >= 0.3;
    }
}
