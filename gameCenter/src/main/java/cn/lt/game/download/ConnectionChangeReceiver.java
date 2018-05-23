package cn.lt.game.download;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.util.List;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.event.NetworkChangeEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ActivityManager;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.StorageSpaceDetection;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.WebViewActivity;
import cn.lt.game.ui.notification.LTNotificationManager;
import de.greenrobot.event.EventBus;

/**
 * Modified by wenchao on 2015/7/13.
 * 网络状态广播接收器,用于处理下载处理
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "netWorkChangeTAG";
    private static final int TYPE_NONE = -1;


    /**注：这里变量必须设置为static*/
    /**
     * 上次广播的网络状态
     */
    private static int preType = TYPE_NONE;
    /**
     * 是否第一收到广播
     */
    private static boolean isFirst = true;
    private static int currType;
    private static boolean NetworkChangedIsHandle;
    private Context context;

    public ConnectionChangeReceiver() {
    }

    public ConnectionChangeReceiver(Context context) {
        this.context = context;
    }

    // -1->无网络，0->移动网络，1->WIFI
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            /*由于好多手机切换网络时会连续下发多种状态，此方法会被调用多次，但是只有最后一种才是目标状态，
            * 所以这里做了控制，只处理一次，而且要等待5秒后才进行处理，以达到最终想要的效果*/
            if (NetworkChangedIsHandle) {
                return;
            }
            MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    currType = NetUtils.getNetType(context);
                    LogUtils.i(TAG, "currType = " + currType + "++++++" + preType);
                    if (isFirst) {
                        isFirst = false;
                        handleNetworkChanged(context, currType);
                        preType = currType;
                    } else if (preType != currType) {
                        LogUtils.i(TAG, "走走走走走哦坐在");
                        preType = currType;
                        handleNetworkChanged(context, currType);
                    } else if (isWifiChanged(context)) {
                        //如果是切换wifi切wifi
                        LogUtils.i(TAG, "wifi切wifi 了=");
                        FileDownloaders.stopAllDownload();
                        //清除通知
                        LTNotificationManager.getinstance().cancelAutoPauseNoti();

                        if (StorageSpaceDetection.check(true, null, false)) {
                            FileDownloaders.autoStartDownload();
                        }
                        //自动开始需要的下载
                        //发送事件监听
                        EventBus.getDefault().post(new NetworkChangeEvent(ConnectivityManager.TYPE_WIFI));


                    }

                    //记录最后一次ssid
                    if (NetUtils.isWifi(context)) {
                        if (mWifiManager == null) {
                            mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        }
                        String currSSID = mWifiManager.getConnectionInfo().getSSID();
                        preSSID = currSSID;
                    } else {
                        preSSID = null;
                    }
                    NetworkChangedIsHandle = false;

                }
            }, 5000);

            NetworkChangedIsHandle = true;

        }
    }

    /**
     * 处理网络改变
     *
     * @param context
     * @param currType
     */
    private synchronized void handleNetworkChanged(Context context, int currType) {
        switch (currType) {
            case ConnectivityManager.TYPE_WIFI:
                LogUtils.i(TAG, "wifi连接");
                //清除通知
                LTNotificationManager.getinstance().cancelAutoPauseNoti();

                if (StorageSpaceDetection.check(true, null, false)) {
                    FileDownloaders.autoStartDownload();
                }
                //发送事件监听
                EventBus.getDefault().post(new NetworkChangeEvent(ConnectivityManager.TYPE_WIFI));
                break;
            case ConnectivityManager.TYPE_MOBILE:
                LogUtils.i(TAG, "2g/3g网络连接");
                handleMobile(context);
                //发送事件监听
                EventBus.getDefault().post(new NetworkChangeEvent(ConnectivityManager.TYPE_MOBILE));
                break;
            case TYPE_NONE:
                LogUtils.i(TAG, "无网络");
                ToastUtils.showToast(context, R.string.download_no_network);
                FileDownloaders.stopAllDownload(true, false);
                break;
        }
    }

    private void handleMobile(final Context context) {

        /**有下载的项*/
        if (FileDownloaders.getCanAutoStartCount() > 0) {
            if (isRunningForeground(context)) {
                //前台运行弹出对话框处理
                Activity activity = ActivityManager.self().topActivity();
                if (activity instanceof WebViewActivity) {
                    return;
                }
                DownloadChecker.getInstance().changeToMoileNetworkPromp(ActivityManager.self().topActivity(), new DownloadChecker.Executor() {
                    @Override
                    public void run() {
                        FileDownloaders.autoStartDownload();
                    }

                    @Override
                    public void reportOrderWifiClick() {

                    }
                }, new DownloadChecker.OrderWifiDownloadExecutor() {  //前台运行：切换移动网时点击预约
                    @Override
                    public void run(List<GameBaseDetail> downLoadList) {
                        if (downLoadList == null) {
                            return;
                        }
                        for (GameBaseDetail game : downLoadList) {
                            DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, "null", 0, null, 0, "" + game.getId(), null, "manual", "orderWifiDownload", game.getPkgName(),""));
                            FileDownloaders.orderWifiDownload(context, game, downLoadList.size() > 1 ? Constant.MODE_ONEKEY : Constant.MODE_SINGLE, Constant.DOWNLOAD_TYPE_NORMAL, "", null, true);
                        }
                    }
                });
            } else {
                //后台通知栏处理
                LTNotificationManager.getinstance().autoPauseDownloadInMobile(FileDownloaders.getCanAutoStartCount());
            }
        }

        //停止正在下载的 任务， 并标记了preState，作为区分 点击下载和自动下载。
        FileDownloaders.stopAllDownload(true, false);

    }

    /**
     * 程序是否在前台运行
     *
     * @param context
     * @return
     */
    private boolean isRunningForeground(Context context) {
        android.app.ActivityManager am = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName componentName = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = componentName.getPackageName();
        return currentPackageName != null && currentPackageName.equals(context.getPackageName());
    }


    private static WifiManager mWifiManager;
    private static String preSSID;//最后一次保存的ssid

    /**
     * 判断wifi是否是从一个wifi改变为另外一个wifi
     *
     * @param context
     * @return
     */
    private boolean isWifiChanged(Context context) {
        boolean isChanged = false;
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }
        String currSSID = mWifiManager.getConnectionInfo().getSSID();
        if (TextUtils.isEmpty(preSSID)) {
            isChanged = false;
        } else if (!currSSID.equals(preSSID)) {
            isChanged = true;
        }
        return isChanged;
    }

}
