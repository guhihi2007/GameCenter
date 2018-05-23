package cn.lt.game.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import cn.lt.game.lib.util.Utils;

public class HandleService extends Service {


    public static final String TAG = "HandleService";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        boolean isAlive = Utils.isServiceRunning(this, CoreService.class);
        if (!isAlive) {
            getApplicationContext().startService(new Intent(getApplicationContext(), CoreService.class));
        }
        startForeground(0, null);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
