package cn.lt.game.ui.app;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.view.View;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.lt.game.R;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.global.Constant;
import cn.lt.game.install.ApkInstallManger;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.util.ActivityManager;
import cn.lt.game.lib.web.WebClient;
import cn.lt.game.lib.widget.ExitWarnDialog;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.model.GameBaseDetail;

/**
 * Created by chon on 2017/7/27.
 * What? How? Why?
 * 退出客户端弹窗逻辑处理
 */

public class ExitManager {

    public static void exit(Activity mActivity) {
        if (mActivity == null) {
            return;
        }

        if (FileDownloaders.getFirstDownloaderByState(DownloadState.downInProgress) != null) {
            // 有正在下载的任务
            new ExitWarnDialog(mActivity, FileDownloaders.getDownloadTaskCount(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 退出程序
                    ActivityManager.self().exitAppWithoutShutdown();
                    // 退出时终止所有网络请求
                    WebClient.singleton().cancelAllTask();
                }
            }).show();

        }  else {
            List<GameBaseDetail> details = FileDownloaders.getDownFileInfoByState(DownloadState.downloadComplete, InstallState.install);

            if (details.size() > 0) {
                // 过滤游戏安装失败
                final CopyOnWriteArrayList<GameBaseDetail> copyOnWriteArrayList = new CopyOnWriteArrayList<>(details);
                PackageManager pm = mActivity.getPackageManager();
                for (GameBaseDetail detail : copyOnWriteArrayList) {
                    // 过滤签名不一致
                    if (detail.getPrevState() == InstallState.signError) {
                        copyOnWriteArrayList.remove(detail);
                        continue;
                    }

                    // 包不存在
                    if (!new File(detail.getDownPath()).exists()) {
                        copyOnWriteArrayList.remove(detail);
                        continue;
                    }

                    // TODO 解析软件包时出现问题
                    try {
                        PackageInfo info = pm.getPackageArchiveInfo(detail.getDownPath(),0);
                        if (info == null) {
                            copyOnWriteArrayList.remove(detail);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (copyOnWriteArrayList.size() == 0) {
                    // 无正在下载的任务，也没有需要安装的任务
                    exitImmediate(mActivity);
                    return;
                }


                // 未安装游戏提示窗 (1个多个提示框不一)
                Resources resources = mActivity.getResources();
                String message;
                if (copyOnWriteArrayList.size() == 1) {
                    message = String.format(resources.getString(R.string.exit_with_uninstalled),copyOnWriteArrayList.get(0).getName());
                } else {
                    message = String.format(resources.getString(R.string.exit_with_multi_uninstalled),copyOnWriteArrayList.get(0).getName(),copyOnWriteArrayList.size());
                }

                final MessageDialog exitDialog = new MessageDialog(mActivity,
                        resources.getString(R.string.gentle_reminder),
                        message,
                        resources.getString(R.string.exit),
                        resources.getString(R.string.gallery_send));
                exitDialog.setLeftOnClickListener(new MessageDialog.LeftBtnClickListener() {
                    @Override
                    public void OnClick(View view) {
                        ActivityManager.self().exitAppWithoutShutdown();
                        // 退出时终止所有网络请求
                        WebClient.singleton().cancelAllTask();
                    }
                });
                exitDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {

                    @Override
                    public void OnClick(View view) {
                        for (GameBaseDetail detail : copyOnWriteArrayList) {
                            ApkInstallManger.self().installPkg(detail, Constant.MODE_ONEKEY_EXIT, null, false);
                        }
                    }
                });
                exitDialog.show();

            } else {
                // 无正在下载的任务，也没有需要安装的任务
                exitImmediate(mActivity);
            }
        }
    }

    private static void exitImmediate(Activity mActivity) {
        Resources resources = mActivity.getResources();
        final MessageDialog exitDialog = new MessageDialog(mActivity,
                resources.getString(R.string.gentle_reminder),
                resources.getString(R.string.exit_reminder),
                resources.getString(R.string.cancel_ignor_bt),
                resources.getString(R.string.gallery_send));
        exitDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {

            @Override
            public void OnClick(View view) {
                ActivityManager.self().exitAppWithoutShutdown();
                // 退出时终止所有网络请求
                WebClient.singleton().cancelAllTask();
            }
        });
        exitDialog.show();
    }

}
