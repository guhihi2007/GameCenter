package cn.lt.game.service;


import android.app.IntentService;
import android.content.Intent;

import cn.lt.game.global.Constant;
import cn.lt.game.install.ApkInstallManger;
import cn.lt.game.model.GameBaseDetail;

public class InstallIntentservice extends IntentService {
    public static final String ACTION = "cn.lt.game.service.InstallIntentservice";

    public InstallIntentservice() {
        super("InstallIntentservice");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GameBaseDetail game = (GameBaseDetail) intent.getSerializableExtra("gameBaseDetail");
        System.out.println("IntentService = " + game.getDownPath());
        if (game != null) {
//			ApkInstallManger.self().installPkgOnNotify(this, game);
            ApkInstallManger.self().installPkg(game, Constant.MODE_SINGLE, null, false);
        }
    }

}
