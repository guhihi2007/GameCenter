package cn.lt.game.push;

import android.content.Context;

import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.service.NoticeService;
import cn.lt.game.ui.app.sidebar.AppInfoBackDoorActivity;
import cn.lt.game.ui.notification.PushPayloadParser;


/**
 * Created by JohnsonLin on 2017/4/20.
 * 推送管理类
 */

public class CQPushManager {

    /** 处理推送过来的透传数据*/
    public static void sendPushByPayload(Context context, String payloadString) {
        String pushId = PushPayloadParser.parse(payloadString);
        LogUtils.d(LogTAG.PushTAG, "Got Payload : " + pushId);
        AppInfoBackDoorActivity.payloadId = pushId;

        try {
            if (!pushId.equals(PushPayloadParser.NEVER_PUSH)) {
                context.startService(NoticeService.getIntent(context, Integer.valueOf(pushId)).putExtra("waitTime", 1000));
            } else {
                PushPayloadParser.parseOpenLog(payloadString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
