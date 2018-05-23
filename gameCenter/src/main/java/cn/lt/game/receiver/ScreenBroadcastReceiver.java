package cn.lt.game.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.lt.game.service.ScreenMonitorService;

/**
 * Created by chon on 2017/6/20.
 * What? How? Why?
 */

public class ScreenBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        // start autoUpgrade,autoCover
        Intent service = new Intent(context, ScreenMonitorService.class);
        service.setAction(action);
        context.startService(service);
    }
}
