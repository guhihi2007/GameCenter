package cn.lt.game.lib.util;

import android.content.Context;

import cn.lt.game.application.MyApplication;
import cn.lt.game.install.ApkInstallManger;
import cn.lt.game.lib.ShellUtils;
import cn.lt.game.lib.util.threadpool.LTAsyncTask;
import static cn.lt.game.application.MyApplication.firstOpen;

/**
 * @author chengyong
 * @time 2017/5/2 15:37
 * @des ${TODO}
 */

public class CheckSilentUtils {
    /**
     * 检测静默装权限
     * @param context
     */
    public static void checkSilentAndSaveResult(final Context context) {
        try {
            checkSystemInstallAndSave(context);
            checkRootInstallAndSave();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("silent", "检测系统权限crash"+e.getMessage().toString());
        }
    }
    /**
     * 检查是否有System权限
     *
     * @param context
     */
    private static void checkSystemInstallAndSave(final Context context) {
        try {
            new LTAsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    if (PackageUtils.isSystemApplication(context)) {
                        return 2;
                    } else {
                        return 3;
                    }
                }

                @Override
                protected void onPostExecute(Integer i) {
                    super.onPostExecute(i);
                    if (i == 2) {
                        MyApplication.application.setSystemInstall(true);
                    } else {
                        MyApplication.application.setSystemInstall(false);
                    }
                    LogUtils.i("silent", "检测系统权限结果？"+(i==2?"是":"否"));
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查是否有root权限
     *
     */
    private static void checkRootInstallAndSave() {
        try {
            new LTAsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    if (ShellUtils.checkRootPermission()) {
                        return 0;
                    } else {
                        return 1;
                    }
                }

                @Override
                protected void onPostExecute(Integer i) {
                    super.onPostExecute(i);
                    if (i == 0) {
                        MyApplication.application.setRootInstall(true);
                        if(firstOpen) {
                            firstOpen = false;
                            MyApplication.application.setRootInstallIsChecked(true);
                        }
                    } else {
                        MyApplication.application.setRootInstall(false);
                        if (!MyApplication.application.getSystemInstall()) {
                            ApkInstallManger.self().removeAllInstallingApp();
                            LogUtils.i("silent", "root权限消失移除安装请求过监控");
                        }
                    }
                    LogUtils.i("silent", "检测root权限结果？"+(i==0?"是":"否"));
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
