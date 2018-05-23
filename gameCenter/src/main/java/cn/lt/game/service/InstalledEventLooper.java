package cn.lt.game.service;

import android.content.pm.PackageInfo;
import android.os.SystemClock;

import java.util.List;

import cn.lt.game.application.MyApplication;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.InstallEvent;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.threadPool.ThreadPoolProxyFactory;
import de.greenrobot.event.EventBus;

/**
 * @author chengyong
 * @time 2016/11/09 17:21
 * @des 用于检测安装是否完成
 */
public class InstalledEventLooper {
    private static final String TAG = InstalledEventLooper.class.getSimpleName();
    private static InstalledEventLooper instance;

    public static InstalledEventLooper getInstance() {
        if (instance == null) {
            synchronized (InstalledEventLooper.class) {
                if (instance == null) {
                    instance = new InstalledEventLooper();
                }
            }
        }
        return instance;
    }

    /**
     * 加入安装完成监控的轮询任务
     *
     * @param entity
     */
    public synchronized GameBaseDetail startInstall(GameBaseDetail entity) {
        InstalledLooperTask task = new InstalledLooperTask(entity);
        ThreadPoolProxyFactory.getDealInstalledEventThreadPoolProxy().submit(task, entity.getPkgName());
        return entity;
    }

    class InstalledLooperTask implements Runnable {
        private final GameBaseDetail entity;

        public InstalledLooperTask(GameBaseDetail entity) {
            this.entity = entity;
        }

        @Override
        public void run() {
            LogUtils.e("ccc", "加入任务=包名" + entity.getPkgName());
            entity.mSetUpTime = System.currentTimeMillis();
//            boolean needToRemoveTask = true;
            while (true) {
                if (System.currentTimeMillis() - entity.mSetUpTime > 1000 * 60 * 3) {   //移除某个bean的任务
                    ThreadPoolProxyFactory.getDealInstalledEventThreadPoolProxy().remove(this, entity.getPkgName());
                    LogUtils.d("InstallReceiver", "安装时间超过3分钟，移除目标");
                    break;
                }    //保险
                SystemClock.sleep(1000);
                if (AppUtils.isInstalled(entity.getPkgName())) {
                    LogUtils.i("InstallReceiver", "轮询:已安装包名:" + entity.getPkgName());
                    int state = entity.getState();
                    int preState = entity.getPrevState();
                    if (state == InstallState.ignore_upgrade || preState == InstallState.upgrade || preState == InstallState.upgrade_inProgress || preState == InstallState.signError) {
                        LogUtils.i("InstallReceiver", "轮询升级任务" + entity.getPkgName());
                        if (compareVersionCode(entity)) {
                            setState(entity);
                            LogUtils.i("InstallReceiver", "轮询升级任务:改变状态并上报数据:" + entity.getPkgName());
                            ThreadPoolProxyFactory.getDealInstalledEventThreadPoolProxy().remove(this, entity.getPkgName());
                            break;
                        }
                    } else {
                        LogUtils.i("InstallReceiver", "轮询普通下载任务" + entity.getPkgName());
                        setState(entity);
                        LogUtils.i("InstallReceiver", "轮询非升级任务:改变状态并上报数据:" + entity.getPkgName());
                        ThreadPoolProxyFactory.getDealInstalledEventThreadPoolProxy().remove(this, entity.getPkgName());
                        break;
                    }
                } else {
                    LogUtils.i("InstallReceiver", "轮询:未安装包名:" + entity.getPkgName());
                }

            }
        }
    }


    /**
     * 设置状态：打开
     *
     * @param appEntity
     */
    private void setState(GameBaseDetail appEntity) {
        FileDownloaders.onInstall(appEntity);
        postEvent(appEntity.getPkgName());
    }

    /**
     * 对比版本名字或 版本号（升级情况），判断是否发送安装完成的通知。
     * GameBaseDetail entity
     *
     * @return
     */
    private boolean compareVersionCode(GameBaseDetail entity) {
        try {
            List<PackageInfo> packageInfoList = AppUtils.getUserAppList(MyApplication.application.getApplicationContext());
            for (PackageInfo packageInfo : packageInfoList) {
                if (packageInfo.packageName.equals(entity.getPkgName())) {
                    //这里为了防止服务端传过来的versionCode与本地获取的VersionCode一致，去掉versionCode对比
                    if (packageInfo.versionName.equals(entity.getVersion())) {
                        LogUtils.i("InstallReceiver", "轮询:本地与服务端版本代号一致，状态正常");
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void postEvent(String packageName) {
        InstallEvent event = new InstallEvent();
        event.packageName = packageName;
        EventBus.getDefault().post(event);
    }

}
