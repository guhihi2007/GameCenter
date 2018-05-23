package cn.lt.game.ui.notification;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.deeplink.DeepLinkUtil;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.adapter.PresentType;

/**
 * Created by JohnsonLin on 2017/11/1.
 */

public class PushIntentService extends IntentService {

    public static final String ACTION = "cn.lt.game.ui.notification.PushIntentService";

    public PushIntentService() {
        super("PushIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            String jumpBy = intent.getStringExtra(NoticeConstants.jumpBy);

            LogUtils.i(LogTAG.PushTAG, "jumpBy = " + jumpBy);
            if (!TextUtils.isEmpty(jumpBy)) {

                // 处理推送deepLink
                if (NoticeConstants.jumpByPushDeepLink.equals(jumpBy)) {
                    handlePushDeepLink(intent);
                }
            }
        }

    }

    private void handlePushDeepLink(Intent intent) {
        String url = intent.getStringExtra("url");

        String pushId = intent.getStringExtra("pushId");
        boolean isFromWakeUp = intent.getBooleanExtra("isFromWakeUp", false);

        if (!TextUtils.isEmpty(url)) {
            LogUtils.i(LogTAG.PushTAG, "PushDeepLink URL = " + url);
            DeepLinkUtil.openApp(this, url, "push");
        }
        DCStat.pushEvent(pushId, "", PresentType.push_deeplink.toString(), "clicked", isFromWakeUp ? "WAKE_UP" : Constant.PAGE_GE_TUI, "", "");

    }


}
