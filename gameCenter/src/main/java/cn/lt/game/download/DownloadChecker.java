package cn.lt.game.download;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.util.ActivityManager;
import cn.lt.game.lib.util.StorageSpaceDetection;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.model.GameBaseDetail;

/**
 * Created by wenchao on 2015/7/10.
 */
public class DownloadChecker {
    /**
     * 是否记住了3G直接下载
     */
    private boolean needToast = true;



    /**
     * 处理各种网络状态下的弹框提示
     * @param context  顶部activity contex对象
     * @param downloadExecutor 继续下载执行器
     * @param pauseExecutor 暂停下载执行器
     * @param ordeWifiDownloadExecutor 预约wifi下载执行器
     */
    public void check(final Context context, final Executor downloadExecutor,
                      final Executor pauseExecutor, final Executor ordeWifiDownloadExecutor) {

        catchNetworkStatusIfNeed(context, new Executor() {
            @Override
            public void run() {
                StorageSpaceDetection.check(context,downloadExecutor,pauseExecutor);
            }

            @Override
            public void reportOrderWifiClick() {

            }
        }, ordeWifiDownloadExecutor);
    }

    /**
     * 处理各种网络状态下的弹框提示
     * @param context  顶部activity contex对象
     * @param executor 继续下载执行器
     */
    public void catchNetworkStatusIfNeed(Context context, Executor executor, Executor ordeWifiDownloadExecutor) {
        do {
            if (noNetworkPromp(context)) break;
            if (mobileNetworkPromp(context, executor, ordeWifiDownloadExecutor)) break;
            wifiNetwork(executor);
        } while (false);
    }

    /**
     * 无网络提示
     *
     * @param context
     * @return true无网络， false有网络
     */
    private boolean noNetworkPromp(final Context context) {
        //无网络情况,弹出对话框提醒
        if (!NetUtils.isConnected(context)) {
            final MessageDialog messageDialog = new MessageDialog(context, context.getResources().getString(R.string.gentle_reminder), context.getResources().getString(R.string.download_no_network), context.getResources().getString(R.string.cancel_ignor_bt), context.getResources().getString(R.string.go_setting));
            messageDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {
                @Override
                public void OnClick(View view) {
                    //跳转到系统设置
                    context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            });
            messageDialog.show();
            return true;
        }
        return false;
    }

    /**
     * 手机网络状态时，点击下载弹出提醒框
     * @param context         顶层Activity的context
     * @param downloadExcutor 下载执行器
     * @param ordeWifiDownloadExecutor 预约wifi下载执行器
     */
    private boolean mobileNetworkPromp(final Context context, final Executor downloadExcutor, final Executor ordeWifiDownloadExecutor) {
        if (NetUtils.isMobileNet(context)) {
            if (isNeedToast()) {
//                ToastUtils.showToast(context, R.string.download_mobile_network_tips);
                String str = context.getResources().getString(R.string.download_mobile_network_tips);

                Activity topActivity = ActivityManager.self().topActivity();
                MessageDialog messageDialog = new MessageDialog(topActivity != null ? topActivity: context, context.getResources().getString(R.string.gentle_reminder), str, context.getResources().getString(R.string.download_continue2), context.getResources().getString(R.string.order_wifi_download));

                messageDialog.setLeftOnClickListener(new MessageDialog.LeftBtnClickListener() {
                    @Override
                    public void OnClick(View view) {
                        downloadExcutor.run();
                    }
                });

                messageDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {
                    @Override
                    public void OnClick(View view) {
                        if (!noNetworkPromp(context)) {
                            ordeWifiDownloadExecutor.run();
                            ordeWifiDownloadExecutor.reportOrderWifiClick();
                        }
                    }
                });

                messageDialog.show();
                return true;
            }
        }
        return false;
    }


    private ArrayList<GameBaseDetail> downLoadList = new ArrayList<GameBaseDetail>();
    /**
     * 切换到手机网络提示框
     *
     * @param context
     * @param executor
     * @return
     */
    public void changeToMoileNetworkPromp(Context context, final Executor executor, final OrderWifiDownloadExecutor ordeWifiDownloadExecutor) {
        if (NetUtils.isMobileNet(context)) {
            //允许3g下载
            downLoadList.clear();
            List<GameBaseDetail> allData = FileDownloaders.getAllDownloadFileInfo();
            for (Iterator<GameBaseDetail> it = allData.iterator(); it.hasNext(); ) {
                GameBaseDetail game = it.next();
                int state = game.getState();
                if (game.getPrevState() == InstallState.upgrade || game.getPrevState() ==
                        InstallState.upgrade_inProgress) {
                    continue;
                }
                if (state == DownloadState.downInProgress
                        || state == DownloadState.downloadFail
                        || state == DownloadState.downloadPause
                        || state == DownloadState.waitDownload) {
                    downLoadList.add(game);
                }
            }

            String str = "";
            Log.i("ttt", "size" + downLoadList.size());
            if (downLoadList.size() == 0) {
                str = context.getResources().getString(R.string.download_mobile_network_autopause);
            } else {
                str = "当前处于2G/3G/4G环境，下载将消耗流量，您有" + downLoadList.size() + "个下载任务，是否继续下载？";
            }

            MessageDialog messageDialog = new MessageDialog(context, context.getResources().getString(R.string.gentle_reminder), str, context.getResources().getString(R.string.download_continue2), context.getResources().getString(R.string.order_wifi_download));

            messageDialog.setLeftOnClickListener(new MessageDialog.LeftBtnClickListener() {
                @Override
                public void OnClick(View view) {
                    executor.run();
                }
            });

            messageDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {
                @Override
                public void OnClick(View view) {
                    ordeWifiDownloadExecutor.run(downLoadList);
                }
            });

            messageDialog.show();
//            markNoNeedToast();
        }
    }


    private void wifiNetwork(Executor executor) {
        executor.run();
    }



    /**
     * 3G是否直接下载
     *
     * @return
     */
    public boolean isNeedToast() {
        return needToast;
    }

    /**
     * 下载执行器
     */
    public interface Executor {
        void run();
        void reportOrderWifiClick();
    }

    /**
     * 预约wifi下载执行器
     */
    public interface OrderWifiDownloadExecutor {
        void run(List<GameBaseDetail> downLoadList);
    }


    private static DownloadChecker ourInstance = new DownloadChecker();


    public static DownloadChecker getInstance() {
        return ourInstance;
    }

    private DownloadChecker() {
    }



}
