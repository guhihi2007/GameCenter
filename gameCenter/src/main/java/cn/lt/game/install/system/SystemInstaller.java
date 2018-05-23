package cn.lt.game.install.system;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by wenchao on 2015/9/17.
 * 系统app安装方式,
 * 需要install_packages权限
 * 需要android:sharedUserId="android.uid.packageinstaller"
 */
public class SystemInstaller {

    private SystemInstallerManager installerManager;

    public void install(String apkFilePath, OnInstalledPackaged onInstalledPackaged) throws InvocationTargetException, IllegalAccessException {
        //设置监听
        installerManager.setOnInstalledPackaged(onInstalledPackaged);
        //开始安装
        installerManager.installPackage(apkFilePath);

    }


    //singleton=======================================
    private static SystemInstaller ourInstance;

    public static SystemInstaller getInstance(Context context) throws NoSuchMethodException {
        if (ourInstance == null) {
            synchronized (SystemInstaller.class) {
                if (ourInstance == null) {
                    ourInstance = new SystemInstaller(context);
                }
            }
        }
        return ourInstance;
    }

    private SystemInstaller(Context context) throws NoSuchMethodException {
        installerManager = new SystemInstallerManager(context);
    }
}
