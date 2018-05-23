package cn.lt.game.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.igexin.sdk.PushManager;
import com.ta.util.download.DownLoadConfigUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.lt.game.application.wakeup.WakeUpSilenceUserManager;
import cn.lt.game.download.DownloadService;
import cn.lt.game.global.Constant;
import cn.lt.game.net.Net;
import cn.lt.game.net.NetIniCallBack;
import cn.lt.game.push.getui.GeTuiIntentService;
import cn.lt.game.push.getui.GeTuiService;
import cn.lt.game.threadPool.ThreadPoolProxyFactory;
import cn.lt.game.update.IPlatUpdateCallback;
import cn.lt.game.update.IPlatUpdateService;
import cn.lt.game.update.PlatUpdateService;

public class CoreService extends Service {

    private ServiceConnection mPlatUpdateServiceConntection;
    private IPlatUpdateService mPlatServiceStub;
    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        initNetwork();
        bindPlatUpdateService();
        initPush();//注册个推推送
        DownLoadConfigUtil.loadConfig(getApplicationContext());
        this.startService(new Intent(this, DownloadService.class));
        ScheduledExecutorService executor = ThreadPoolProxyFactory.getScheduledThreadPool();
        executor.scheduleWithFixedDelay(platUpdateTask, 10000, Constant.CHECKVERSIONPERIOD, TimeUnit.MILLISECONDS);
        // 检测沉默用户
        if (timer == null) {
            timer = new Timer();
        }
        WakeUpSilenceUserManager.getInstance(CoreService.this, timer).startCheck();


    }

    /***
     * 一个用来执行绑定服务的线程
     */
    TimerTask platUpdateTask = new TimerTask() {
        @Override
        public void run() {
            startUpdateService();
        }
    };

    private void bindPlatUpdateService() {
        mPlatUpdateServiceConntection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mPlatServiceStub = IPlatUpdateService.Stub.asInterface(service);
                try {
                    mPlatServiceStub.registerCallback(new IPlatUpdateCallback.Stub() {
                        @Override
                        public void callback(boolean hasUpdate) throws RemoteException {
                            if (hasUpdate) {
                                mPlatServiceStub.checkVersion();
                            }
                        }
                    });
                    mPlatServiceStub.requestNetWork();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mPlatServiceStub = null;
            }
        };
    }

    private void initNetwork() {
        Net.instance().init(getApplicationContext(), new NetIniCallBack() {
            @Override
            public void callback(int code) {
            }
        });
    }


    @Override
    public IBinder onBind(Intent arg0) {
        System.out.println("核心服务bind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("核心服务unbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        System.out.println("核心服务rebind");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("核心服务startCommand");
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        System.out.println("核心服务destroy");
        Intent sevice = new Intent(this, CoreService.class);
        startService(sevice);
        Intent intent = new Intent("cn.lt.game.service.CoreService");
        // 3.1以后的版本直接设置Intent.FLAG_INCLUDE_STOPPED_PACKAGES的value：32
        if (android.os.Build.VERSION.SDK_INT >= 12) {
            intent.setFlags(32);
        }
        sendBroadcast(intent);
//        unregisterReceiver(mScreenReceiver);
        super.onDestroy();
        WakeUpSilenceUserManager.getInstance(this, null).setInstanceNull();
    }

    private void initPush() {
        System.out.println("核心服务init push");
        initGeTuiPush();
    }

    /**
     * 初始化 个推推送（启动应用只会调用一次）
     */
    private void initGeTuiPush() {
        PushManager.getInstance().initialize(this.getApplicationContext(), GeTuiService.class);

        // com.getui.demo.DemoIntentService 为第三方自定义的推送服务事件接收类
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), GeTuiIntentService.class);
    }


    private void startUpdateService() {
        Intent serverIntent = new Intent(this, PlatUpdateService.class);
        serverIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (mPlatServiceStub == null) {
            this.bindService(serverIntent, mPlatUpdateServiceConntection, Context.BIND_AUTO_CREATE);
        } else {
            try {
                mPlatServiceStub.requestNetWork();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
