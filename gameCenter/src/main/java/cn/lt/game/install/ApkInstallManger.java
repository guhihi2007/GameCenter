package cn.lt.game.install;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.bean.InstallTemInfo;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.install.system.OnInstalledPackaged;
import cn.lt.game.install.system.SystemInstaller;
import cn.lt.game.lib.util.AdMd5;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.CheckIsApkFile;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.PackageUtils;
import cn.lt.game.lib.util.StorageSpaceDetection;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.file.FileUtil;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.model.AppInfo;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.model.State;
import cn.lt.game.service.InstalledEventLooper;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.entity.StatisDownloadTempInfoData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.statistics.manger.StatManger;
import cn.lt.game.threadPool.ThreadPoolProxyFactory;
import cn.lt.game.ui.notification.LTNotificationManager;
import de.greenrobot.event.EventBus;

import static cn.lt.game.lib.util.Utils.isScreenOn;

/***
 * Created by Administrator on 2016/1/7.
 */
public class ApkInstallManger {
    private Context mContext;
    private boolean mIsSystemApp, canRootInstall;
    private String installType, installMode;
    private static final String TAG = "ApkInstallManger";

    private ApkInstallManger() {
    }

    public static ApkInstallManger self() {
        return ApkInstallMangerHolder.instance;
    }

    public void init(Context context) {
        this.mContext = context;

    }

    /**
     * 供外界调用安装
     *
     * @param game
     * @param install_mode
     * @param eventData
     * @ 安装优先级:1.判断是否已经在安装中 2.判断包名是否存在
     * 3.判断APK包名和GameBaseDetail包名是否一致
     * 4.判断内存是否可用
     * 5.检测MD5和签名信息
     */
    public void installPkg(final GameBaseDetail game, final String install_mode, final StatisticsEventData eventData, final boolean fromAutoUpgrade) {
        if (null == game || isAppInstalling(game.getPkgName())) {
            LogUtils.i(TAG, "包名存值，不能再次安装");
            return;
        }

        // 同时符合两个条件才能root装（1.设备有root权限； 2.用户已打开root装）
        canRootInstall = MyApplication.application.getRootInstall() && MyApplication.castFrom(mContext).getRootInstallIsChecked();
        mIsSystemApp = MyApplication.application.getSystemInstall();
        LogUtils.i(TAG, "，rootInstall权限结果==>" + canRootInstall + "------isSystemApp权限结果==>" + mIsSystemApp);
        confirmInstallType(install_mode);
        // 必须先检测是否符合执行安装条件,这个先在子线程完成，然后在主线程启动子线程，不算线程嵌套吧？
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                cn.lt.game.model.State.updateState(game, InstallState.check);
                EventBus.getDefault().post(new DownloadUpdateEvent(game));
                return canInstall(game, install_mode, eventData);
            }

            @Override
            protected void onPostExecute(Boolean canInstall) {
                if (canInstall) {
                    LogUtils.i(TAG, "可以执行安装");
                    executeInstall(game, install_mode, eventData, fromAutoUpgrade);
                } else {
                    LogUtils.i(TAG, "不可以执行安装");
                }

            }
        }.execute();


    }

    private boolean canInstall(final GameBaseDetail game, String install_mode, StatisticsEventData eventData) {
        // 检测apk是否存在
        long t1 = System.currentTimeMillis();
        File saveFile = new File(game.getDownPath());
        if (!saveFile.exists()) {
            if (!game.isCoveredApp) {
                cn.lt.game.model.State.updateState(game, InstallState.installFail);
                EventBus.getDefault().post(new DownloadUpdateEvent(game));
            } else {
                LogUtils.i("ScreenMonitorService", "覆盖文件不存在，删除已有下载数据");
                FileDownloaders.remove(game.getDownUrl(), false);
            }
            removeInstallingApp(game.getPkgName());//安装不成功从已安装列表移除掉
            DCStat.installRequest(game, install_mode, installType);
            DCStat.installFailedEvent(game, installMode, installType, "apk is not exists");
            new Exception("apk is not exists : " + game.getName() + ", " + game.getDownPath()).printStackTrace();
            return false;
        }
        LogUtils.i(TAG, "检测文件是否存在耗时：" + (System.currentTimeMillis() - t1));

        // 检测下载完成的apk包名是否一致，否则不执行安装
        long t2 = System.currentTimeMillis();
        String locakPkgName = CheckIsApkFile.getPackageNameByDownPath(game.getDownPath(), mContext);
        if (TextUtils.isEmpty(locakPkgName) && !"system".equals(installType) && !"root".equals(installType)) {
            DCStat.installFailedEvent(game, installMode, installType, "解析包出错(手动装)");
        } else if (!TextUtils.isEmpty(locakPkgName) && !game.getPkgName().equals(locakPkgName)) {
            DCStat.installRequest(game, installMode, installType);
            DCStat.installFailedEvent(game, installMode, installType, "下载被劫持");
            if (!game.isCoveredApp) {
                resetStatusToInstallfail(game);
            }
            removeInstallingApp(game.getPkgName());
            return false;
        }
        LogUtils.i(TAG, "检测解析包出错耗时：" + (System.currentTimeMillis() - t2));
        // 检测内存空间是否足够
        // 安装时计算剩余内存空间应该使用手机内存，这是Android系统默认的，切记不要使用SD卡路径来计算，会不准确的
        long t3 = System.currentTimeMillis();
        long phoneStorageSize = StorageSpaceDetection.getMemorySize_byte();
        Log.i(TAG, "可用内存空间大小===" + phoneStorageSize + "\t游戏: " + game.getName() + "体积大小==" + game.getPkgSize());
        if (phoneStorageSize == 0) {
            // 低内存状态下置为安装状态
            State.updateState(game, InstallState.install);
            EventBus.getDefault().post(new DownloadUpdateEvent(game));
            DCStat.outofmemoryEvent("0M", "剩余内存为0", null);
            removeInstallingApp(game.getPkgName());//从安装列表中移除任务
            StorageSpaceDetection.showEmptyTips(mContext, "安装");
            return false;
        }

        if (phoneStorageSize <= game.getPkgSize()) {// 检测手机剩余内存是否大于apk体积
            // 低内存状态下置为安装状态
            State.updateState(game, InstallState.install);
            EventBus.getDefault().post(new DownloadUpdateEvent(game));
            DCStat.outofmemoryEvent("可用空间：" + phoneStorageSize, "安装体积大于可用空间", null);
            removeInstallingApp(game.getPkgName());//从安装列表中移除任务
            StorageSpaceDetection.showEmptyTips(mContext, "安装");
            return false;
        }
        LogUtils.i(TAG, "检测可用内存耗时：" + (System.currentTimeMillis() - t3));

        return true;
    }

    private void executeInstall(final GameBaseDetail game, final String install_mode, final StatisticsEventData eventData, final boolean fromAutoUpgrade) {
        ThreadPoolProxyFactory.getCachedThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                doInstall(mContext, game, install_mode, eventData, fromAutoUpgrade);
                checkGameFile(game); //耗时操作放在这里，防止影响大文件安装时堵塞UI
            }
        });
    }

    private void confirmInstallType(String installMode) {
        this.installMode = installMode;
        if (mIsSystemApp) {
            installType = "system";
        } else if (canRootInstall) {
            installType = "root";
        } else {
            installType = PackageUtils.canAutoInstall() ? "auto" : "normal";
        }
    }

    private void signatureCheck(final GameBaseDetail game, final boolean fromAutoUpgrade, final boolean isException) {
        new AsyncTask<Void, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                //系统权限安装
                return ApkSignatureCompare.isSignatureSame(MyApplication.application, game.getDownPath(), game.getPkgName());
            }

            @Override
            protected void onPostExecute(Boolean resullt) {
                if (!resullt) {
                    LogUtils.i(TAG, "签名不一致");
                    State.updatePrevState(game, InstallState.signError);
                    /*如果是来自应用自动升级则不弹出签名不一致*/
                    if (fromAutoUpgrade) {
                        LogUtils.i(TAG, "普通装自动升级安装不需要提示卸载签名不一致的包");
                    } else {
                        if (!isException) {
                            showDialog(game);
                            LogUtils.i(TAG, "普通装需要提示卸载签名不一致的包");
                        } else {
                            LogUtils.i(TAG, "系统异常已经上报过");
                        }
                    }
                    DCStat.installFailedEvent(game, installMode, "normal", "签名不一致(手动装)");  //只针对签名包错误的
                    removeInstallingApp(game.getPkgName());
                } else {
                    LogUtils.i(TAG, "签名一致");
                }
            }
        }.execute();
    }

    private void showDialog(final GameBaseDetail game) {
        if (game == null) return;
        boolean isScreenOn = Utils.isScreenOn();
        if (isScreenOn) {
            try {
                final MessageDialog messageDialog = new MessageDialog(cn.lt.game.lib.util.ActivityManager.self().topActivity(), "提示", TextUtils.isEmpty(game.getName()) ? "" : game.getName() + "存在签名冲突,会导致安装失败,卸载后我们将为你重新安装。", "取消", "卸载");
                messageDialog.setLeftOnClickListener(new MessageDialog.LeftBtnClickListener() {
                    @Override
                    public void OnClick(View view) {
                        messageDialog.dismiss();
                    }
                });
                messageDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {
                    @Override
                    public void OnClick(View view) {
                        ApkUninstaller.uninstall(cn.lt.game.lib.util.ActivityManager.self().topActivity(), game.getPkgName());
                        messageDialog.dismiss();
                    }
                });
                messageDialog.setCancelOnClickListener(new MessageDialog.CancelCliclListener() {
                    @Override
                    public void onClicl(View view) {
                        messageDialog.dismiss();
                    }
                });
                if (!TextUtils.isEmpty(game.getName())) {
                    messageDialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void doInstall(final Context context, final GameBaseDetail game, final String install_mode, final StatisticsEventData eventData, final boolean fromAutoUpgrade) {
        try {

            if (mIsSystemApp || canRootInstall) {
                LogUtils.i(TAG, "，加入监听的包名==>" + game.getPkgName());
                addInstallingApp(game.getPkgName());
            }

            // 修改文件权限
            String cmd = "chmod 777 " + game.getDownPath();
            Runtime.getRuntime().exec(cmd);
            Thread.sleep(500);
            //消息提示
            final File saveFile = new File(game.getDownPath());
            saveInstallTemData(game);
            Log.d(TAG, "开始安装了，当前是否开启自动装" + canRootInstall + "\t系统权限：" + mIsSystemApp);
            if (mIsSystemApp) {
                try {
                    //系统权限安装
                    cn.lt.game.model.State.updateState(game, InstallState.installing);
                    EventBus.getDefault().post(new DownloadUpdateEvent(game));
                    systemInstall(context, saveFile, game, install_mode, eventData, fromAutoUpgrade);
                } catch (Exception e) {
                    LogUtils.e(TAG, "系统安装抛异常了:" + e.toString());
                    removeInstallingApp(game.getPkgName());
//                    cn.lt.game.model.State.updateState(game, InstallState.check);
//                    EventBus.getDefault().post(new DownloadUpdateEvent(game));
                    if (e instanceof InvocationTargetException) {
                        LogUtils.e(TAG, "系统装异常:" + ((InvocationTargetException) e).getTargetException());
                        DCStat.installFailedEvent(game, installMode, installType, "系统装异常：" + ((InvocationTargetException) e).getTargetException());
                    }
                    if (!game.isCoveredApp) {
                        SystemClock.sleep(1000);
                        if (canInstall(game, install_mode, eventData)) {
                            if (canRootInstall) {
                                rootInstall(context, saveFile, game, install_mode, eventData, fromAutoUpgrade);
                            } else {
                                cn.lt.game.model.State.updateState(game, InstallState.install);
                                // 覆盖安装的不需要跳转至手动安装
                                normalInstall(context, saveFile.getPath(), game, install_mode, eventData, fromAutoUpgrade, true);
                                MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        EventBus.getDefault().post(new DownloadUpdateEvent(game));
                                    }
                                },500);
                            }
                        }
                    }

                }
            } else if (canRootInstall) {
                //root权限安装
                try {
                    cn.lt.game.model.State.updateState(game, InstallState.installing);
                    EventBus.getDefault().post(new DownloadUpdateEvent(game));
                    rootInstall(context, saveFile, game, install_mode, eventData, fromAutoUpgrade);
                } catch (Exception e) {
                    removeInstallingApp(game.getPkgName());
                    DCStat.installFailedEvent(game, installMode, installType, "Root装异常：" + e.getMessage());

                    MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!game.isCoveredApp) {
                                // 覆盖安装的游戏安装失败不需要跳转到手动安装
                                normalInstall(context, saveFile.getPath(), game, install_mode, eventData, fromAutoUpgrade, true);
                            }
                        }
                    }, 500);  //防止插入数据，快于查询速度。需要同步

                    cn.lt.game.model.State.updateState(game, InstallState.install);
                    MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new DownloadUpdateEvent(game));
                        }
                    },1000);

                    e.printStackTrace();
                }
            } else {
                // 如果没有设置自动装，采用正常安装方式
                Log.d(TAG, "开始安装了，当前采用正常安装方式");
                normalInstall(context, saveFile.getPath(), game, install_mode, eventData, fromAutoUpgrade, false);
                if (game.getState() != InstallState.install) {
                    cn.lt.game.model.State.updateState(game, InstallState.install);
                    MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new DownloadUpdateEvent(game));
                        }
                    },500);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            GameBaseDetail tmpGame = FileDownloaders.getDownFileInfoById(game.getId());
            AppInfo appInfo = new AppInfo(context, game.getPkgName());
            if (tmpGame.getState() != InstallState.installComplete || tmpGame.getVersionCode() != appInfo.getVersionCode()) {
                doFailure(context, e, game);
            }
        }


    }

    /***
     * 内存中保存临时安装数据，在数据上报查询不到表数据时替补
     * @param game
     */
    private void saveInstallTemData(GameBaseDetail game) {
        if (mInstallMap != null) {
            boolean upgrade = false;
            if (!game.isCoveredApp) {
                upgrade = AppUtils.isInstalled(game.getPkgName());
            }
            String gameId = String.valueOf(game.getId());
            mInstallMap.put(game.getPkgName(), new InstallTemInfo(gameId, upgrade, System.currentTimeMillis()));
        }
    }

    public void doFailure(final Context context, Exception e, GameBaseDetail game) {
        // ***dada 统计安装失败；
        if (game.getDownPath() != null) {
            FileUtil.deleteFile(game.getDownPath());
        }
        game.setDownLength(0);
        FileDownloaders.setDownLength(game.getDownUrl(), 0);
        cn.lt.game.model.State.updateState(game, InstallState.installFail);
        EventBus.getDefault().post(new DownloadUpdateEvent(game));
        if (mIsSystemApp || canRootInstall) {
            LogUtils.i("juice", "，最大catch移除 监听的包名==>" + game.getPkgName());
            removeInstallingApp(game.getPkgName());
        }
        MyApplication.getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showToast(context, R.string.install_fail);
            }
        });
    }

//    public void installPkgOnNotify(Context context, GameBaseDetail game) {
//        // MD5校验
//        try {
//            checkGameFile(game);
//            String cmd = "chmod 777 " + game.getDownPath();
//            Runtime.getRuntime().exec(cmd);
//            File saveFile = new File(game.getDownPath());
//            if (!saveFile.exists()) {
//                throw new Exception("apk is not exists : " + game.getName());
//            }
//            if (PackageUtils.isSystemApplication(context) || MyApplication.castFrom(context).getRootInstallIsChecked()) {
//                int sysInstall = installSilent(context, saveFile.getPath());
//                if (sysInstall != PackageUtils.INSTALL_SUCCEEDED) {
//                    PackageUtils.installNormal(context, saveFile.getPath(), game, false, null, Constant.MODE_SINGLE, null);
//                }
//            } else {
//                PackageUtils.installNormal(context, saveFile.getPath(), game, false, null, Constant.MODE_SINGLE, null);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 校验游戏是否能安装（apk体积越大越耗时）
     *
     * @param game
     * @return
     */
    private void checkGameFile(GameBaseDetail game) {
        long t1 = System.currentTimeMillis();
        if (game.getDownPath() == null) {
            new Exception("down path is null : " + game.getName()).printStackTrace();
        }
        // MD5校验
        String md5 = AdMd5.md5sum(game.getDownPath());
        String mRemark = "";
        if (TextUtils.isEmpty(md5)) {
            game.getDownloadTempInfo().setmRemark("MD5未计算完成或计算异常");
            mRemark = "MD5未计算完成或计算异常";
        } else if (!md5.equalsIgnoreCase(game.getMd5())) {
            game.getDownloadTempInfo().setmRemark("MD5值不一致");
            mRemark = "MD5值不一致";
//            DCStat.installFailedEvent(game, installMode, installType, "MD5值不一致");
        } else {
            LogUtils.i(TAG, "MD5值比对完成，一致");
        }
        StatisDownloadTempInfoData statisDownloadTempInfoData = StatManger.self().queryDBbyPackageName(game.getPkgName());
        DCStat.outofmemoryEvent("",mRemark,statisDownloadTempInfoData);
        LogUtils.i(TAG, "检测MD5耗时：" + (System.currentTimeMillis() - t1));
    }

    /**
     * root权限安装
     *
     * @param context
     * @param apkFile
     * @param game
     * @param install_mode
     * @param eventData
     */
    private void rootInstall(final Context context, File apkFile, GameBaseDetail game, String install_mode, StatisticsEventData eventData, boolean fromAutoUpgrade) {
        //监控安装完成情况
        InstalledEventLooper.getInstance().startInstall(game); //轮询监控
        DCStat.installRequest(game, install_mode, installType); //TODO 添加  自动
        int returnCode = PackageUtils.installSilent(context, apkFile.getPath());
        installResultHandle(context, returnCode, apkFile, game, install_mode, eventData, true, fromAutoUpgrade);
    }

    /**
     * 普通安装方式
     *
     * @param context
     * @param apkPath
     * @param game
     * @param install_mode
     * @param eventData
     */
    private void normalInstall(Context context, String apkPath, GameBaseDetail game, String install_mode, StatisticsEventData eventData, boolean fromAutoUpgrade, boolean isException) {
        try {
            installType = PackageUtils.canAutoInstall() ? "auto" : "normal";//installType重置为手动
            PackageUtils.installNormal(context, apkPath, game, false, null, install_mode, eventData);
            int state = game.getPrevState();
            if (state == InstallState.upgrade || state == InstallState.ignore_upgrade || state == InstallState.signError) {
                signatureCheck(game, fromAutoUpgrade, isException);
            }
            //如果是静默装失败后启动的手动装也要上报解析包出错
            if (isException) {
                String locakPkgName = CheckIsApkFile.getPackageNameByDownPath(game.getDownPath(), mContext);
                if (TextUtils.isEmpty(locakPkgName)) {
                    DCStat.installFailedEvent(game, installMode, installType, "解析包出错(手动装)");
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
            Log.i(TAG, "安装包异常日志：" + e.getMessage());
            DCStat.installFailedEvent(game, installMode, installType, "安装异常：" + e.getMessage());
        }
    }

    /**
     * 系统app权限安装
     *
     * @param context
     * @param install_mode
     * @param eventData
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void systemInstall(final Context context, final File apkFile, final GameBaseDetail game, final String install_mode, final StatisticsEventData eventData, final boolean fromAutoUpgrade) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // 覆盖安装的，安装的时候需要先判断文件是否已经删除了
        if (game.isCoveredApp && !AppUtils.isInstalled(game.getPkgName())) {
            LogUtils.e("ScreenMonitorService", "覆盖安装的时候原安装的应用已卸载");
            return;
        }
        if (!game.isCoveredApp) {
            //监控安装完成情况
            InstalledEventLooper.getInstance().startInstall(game); //轮询监控:覆盖安装不监控
        }
        DCStat.installRequest(game, install_mode, installType);
        SystemInstaller.getInstance(context).install(apkFile.getPath(), new OnInstalledPackaged() {
            @Override
            public void packageInstalled(String packageName, int returnCode) {
                installResultHandle(context, returnCode, apkFile, game, install_mode, eventData, false, fromAutoUpgrade);
            }
        });
    }

    private void installResultHandle(final Context context, int returnCode, File apkFile, final GameBaseDetail game, String install_mode, StatisticsEventData eventData, boolean installByRoot, boolean fromAutoUpgrade) {
        String failMessage = "";
        boolean needToNormalInstall = true;
        switch (returnCode) {
            case PackageUtils.INSTALL_FAILED_ALREADY_EXISTS:
                failMessage = "安装失败，包已安装";
                break;
            case PackageUtils.INSTALL_FAILED_INVALID_APK:
                failMessage = "安装失败，无效的包";
                // delete apk file，also delete db recorder
                // 只有在覆盖白名单流程里才需要删除  ATian
                if (game.isCoveredApp) {
                    if (apkFile.exists()) {
                        FileDownloaders.remove(game.getDownUrl(), true);
                        LogUtils.e("ScreenMonitorService A", "安装失败，无效的包，删除记录");
                    }
                }
                break;
            case PackageUtils.INSTALL_FAILED_INVALID_URI:
                failMessage = "安装失败，无效的包路径";
                break;
            case PackageUtils.INSTALL_FAILED_INSUFFICIENT_STORAGE:
                if (!apkFile.exists()) {
                    failMessage = "安装包不存在";
                } else {
                    failMessage = "安装失败，存储空间不足";
                    MyApplication.getMainThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            StorageSpaceDetection.showInstallFailure(context);
                        }
                    });
                }
                break;
            case PackageUtils.INSTALL_FAILED_DUPLICATE_PACKAGE:
                failMessage = "安装失败，重复的包";
                break;
            case PackageUtils.INSTALL_FAILED_NO_SHARED_USER:
                failMessage = "安装失败，No shared user";
                break;
            case PackageUtils.INSTALL_FAILED_UPDATE_INCOMPATIBLE:
                failMessage = "签名不一致(静默装).";
                if (!game.isCoveredApp) {
                    State.updatePrevState(game, InstallState.signError);
                }
                /*如果是来自应用自动升级则不弹出签名不一致*/
                needToNormalInstall = signErrorDialog(game, installByRoot, needToNormalInstall);
                break;
            case PackageUtils.INSTALL_FAILED_SHARED_USER_INCOMPATIBLE:
                failMessage = "安装失败，shared user不兼容";
                break;
            case PackageUtils.INSTALL_FAILED_MISSING_SHARED_LIBRARY:
                failMessage = "安装失败，missing shared library!";
                break;
            case PackageUtils.INSTALL_FAILED_REPLACE_COULDNT_DELETE:
                failMessage = "安装失败，replace couldnt delete!";
                break;
            case PackageUtils.INSTALL_FAILED_DEXOPT:
                failMessage = "安装失败，dexopt!";
                break;
            case PackageUtils.INSTALL_FAILED_OLDER_SDK:
                failMessage = "安装失败，older sdk!";
                break;
            case PackageUtils.INSTALL_FAILED_CONFLICTING_PROVIDER:
                failMessage = "安装失败，conflicting provider";
                break;
            case PackageUtils.INSTALL_FAILED_NEWER_SDK:
                failMessage = "安装失败，newer sdk!";
                break;
            case PackageUtils.INSTALL_FAILED_TEST_ONLY:
                failMessage = "安装失败，test only!";
                break;
            case PackageUtils.INSTALL_FAILED_CPU_ABI_INCOMPATIBLE:
                failMessage = "安装失败，cpu abi incompatible!";
                break;
            case PackageUtils.INSTALL_FAILED_MISSING_FEATURE:
                failMessage = "安装失败，missing feature!";
                break;
            case PackageUtils.INSTALL_FAILED_CONTAINER_ERROR:
                failMessage = "安装失败，container error!";
                break;
            case PackageUtils.INSTALL_FAILED_INVALID_INSTALL_LOCATION:
                failMessage = "安装失败，无效的安装路径!";
                break;
            case PackageUtils.INSTALL_FAILED_MEDIA_UNAVAILABLE:
                failMessage = "安装失败，media unavailable!";
                break;
            case PackageUtils.INSTALL_FAILED_VERIFICATION_TIMEOUT:
                failMessage = "安装失败，verification timeout!";
                break;
            case PackageUtils.INSTALL_FAILED_VERIFICATION_FAILURE:
                failMessage = "安装失败，verification failure!";
                break;
            case PackageUtils.INSTALL_FAILED_PACKAGE_CHANGED:
                failMessage = "安装失败，package changed!";
                break;
            case PackageUtils.INSTALL_FAILED_UID_CHANGED:
                failMessage = "安装失败，uid changed!";
                break;
            case PackageUtils.INSTALL_PARSE_FAILED_NOT_APK:
                failMessage = "安装失败，解析失败not apk!";
                // TODO delete apk file，also delete db recorder
                // 只有在覆盖白名单流程里才需要删除  ATian
                if (game.isCoveredApp) {
                    if (apkFile.exists()) {
                        FileDownloaders.remove(game.getDownUrl(), true);
                        LogUtils.e("ScreenMonitorService", "B 安装失败，解析失败not apk! 删除记录");
                    }
                }
                break;
            case PackageUtils.INSTALL_PARSE_FAILED_BAD_MANIFEST:
                failMessage = "安装失败，bad manifest";
                break;
            case PackageUtils.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION:
                failMessage = "安装失败，unexpected exception!";
                break;
            case PackageUtils.INSTALL_PARSE_FAILED_NO_CERTIFICATES:
                failMessage = "安装失败，no certificates!";
                break;
            case PackageUtils.INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES:
                failMessage = "签名不一致(静默装)";
                State.updatePrevState(game, InstallState.signError);
                /*如果是来自应用自动升级则不弹出签名不一致*/
                needToNormalInstall = signErrorDialog(game, installByRoot, needToNormalInstall);
                break;
            case PackageUtils.INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING:
                failMessage = "安装失败，certificate encoding!";
                break;
            case PackageUtils.INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME:
                failMessage = "安装失败，bad package name!";
                break;
            case PackageUtils.INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID:
                failMessage = "安装失败，bad shared user id!";
                break;
            case PackageUtils.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED:
                failMessage = "安装失败，manifest malformed!";
                break;
            case PackageUtils.INSTALL_PARSE_FAILED_MANIFEST_EMPTY:
                failMessage = "安装失败，manifest empty!";
                break;
            case PackageUtils.INSTALL_FAILED_INTERNAL_ERROR:
                failMessage = "安装失败，internal error!";
                break;
            case PackageUtils.INSTALL_FAILED_OTHER:
                failMessage = "安装失败，未知原因!";
                break;
            case PackageUtils.ROOT_INSTALL_FAIL_BECAUSE_BE_INTERCEPTED:
                failMessage = "root装时可能被拦截了！";
                MyApplication.application.setRootInstall(false);
                break;
        }

        if (PackageUtils.INSTALL_SUCCEEDED == returnCode) {
            removeInstallingApp(game.getPkgName());
            //此处不做处理，在接收到广播后统一处理
        } else {
            //上报系统安装失败数据
            LogUtils.d("juice", "静默装失败信息==" + failMessage);
            cn.lt.game.model.State.updateState(game, InstallState.install);
            EventBus.getDefault().post(new DownloadUpdateEvent(game));
            boolean isException = (returnCode == PackageUtils.INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES) || (returnCode == PackageUtils.INSTALL_FAILED_UPDATE_INCOMPATIBLE || (returnCode == PackageUtils.INSTALL_FAILED_INVALID_APK));
            removeInstallingApp(game.getPkgName());
            DCStat.installFailedEvent(game, installMode, installType, failMessage);
            if (!installByRoot) {
                if (needToNormalInstall && !game.isCoveredApp) {
                    normalInstall(context, apkFile.getPath(), game, install_mode, eventData, fromAutoUpgrade, isException);
                }
            } else {
                if (!game.isCoveredApp) {
                    normalInstall(context, apkFile.getPath(), game, install_mode, eventData, fromAutoUpgrade, isException);
                }
            }
        }
    }

    private boolean signErrorDialog(final GameBaseDetail game, boolean installByRoot, boolean needToNormalInstall) {
        if (!installByRoot) {
            if (!isScreenOn()) {  //黑屏
                needToNormalInstall = false;
            } else {
                MyApplication.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        showDialog(game);
                    }
                });

            }
        }
        return needToNormalInstall;
    }

    /**
     * 重置状态为安装失败
     */
    private void resetStatusToInstallfail(GameBaseDetail game) {
        // 更新数据状态
        game.setState(InstallState.installFail);
        game.setDownLength(0);
        FileDownloaders.update(game);

        // 取消相关通知
        LTNotificationManager.getinstance().cancelNotification(game.getId());

        // 更新按钮状态
        EventBus.getDefault().post(new DownloadUpdateEvent(game));

        // 删除apk(不能删除对应的下载数据，产品要求下载任务页面保留着)
        FileUtil.deleteFile(game.getDownPath());
    }

    private static class ApkInstallMangerHolder {
        private static ApkInstallManger instance = new ApkInstallManger();
    }

    private ConcurrentHashMap<String, InstallTemInfo> mInstallMap = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, InstallTemInfo> getmInstallMap() {
        return mInstallMap;
    }

    public void setmInstallMap(ConcurrentHashMap<String, InstallTemInfo> mInstallMap) {
        this.mInstallMap = mInstallMap;
    }

    private List<String> installingApps = new ArrayList<String>();

    public void addInstallingApp(String pkg) {
        synchronized (installingApps) {
            if (!installingApps.contains(pkg)) {
                installingApps.add(pkg);
            }
        }
    }

    public boolean isAppInstalling(String pkg) {
        synchronized (installingApps) {
            return installingApps.contains(pkg);
        }
    }

    public void removeInstallingApp(String pkg) {
        synchronized (installingApps) {
            if (installingApps.contains(pkg)) {
                installingApps.remove(pkg);
            }
        }
    }

    public void removeAllInstallingApp() {
        installingApps.clear();
    }
}