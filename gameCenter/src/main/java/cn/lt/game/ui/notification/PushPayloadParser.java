package cn.lt.game.ui.notification;


import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import cn.lt.game.global.LogTAG;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.ui.notification.model.PushPlayloadBean;


/**
 * Created by LinJunSheng on 2016/11/7.
 * 解析推送透传消息工具
 */
public class PushPayloadParser {
    /** 不需要推送*/
    public static final String NEVER_PUSH = "neverPush";

    /** 全版本推送*/
    private static final String ALL_VERSIONS = "all";

    /** 手机Log开关*/
    private static final String openLog = "openLog";
    /** 手机Log开*/
    public static final String ON = "on";
    /** 手机Log关*/
    public static final String OFF = "off";

    /**
     * 对透传消息进行解析
     * @param payloadString 接收到的推送透传消息
     * @return 推送id
     */
    public static String parse(String payloadString) {
        // 推送资源
        PushPlayloadBean playloadBean = null;
        try {
            playloadBean = getPlayloadBean(payloadString);
        } catch (Exception e) {
            e.printStackTrace();
            return NEVER_PUSH;
        }

        return judgeNeedPush(playloadBean);
    }

    /** json数据转化为实体*/
    private static PushPlayloadBean getPlayloadBean(String payloadString) {
        LogUtils.i(LogTAG.PushTAG, "payloadString = " + payloadString);
        Gson gson = new Gson();
        return gson.fromJson(payloadString, PushPlayloadBean.class);
    }

    /**
     * 通过解释判断是否需要推送
     * @param playloadBean 推送数据实体类
     * @return 是否需要推送的结果
     */
    private static String judgeNeedPush(PushPlayloadBean playloadBean) {
        if (playloadBean != null) {
            String op = playloadBean.getOp();

            if (null != op) {
                if(op.equals(ALL_VERSIONS)) {
                    LogUtils.i(LogTAG.PushTAG, "推送给全版本~");

                    // 推送全版本
                    return playloadBean.getId();
                } else {

                    int pushVersionCode = 0;
                    try{
                        pushVersionCode = Integer.parseInt(playloadBean.getVersion_code());
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.i(LogTAG.PushTAG, "pushVersionCode转换出异常~ " + "code = " + playloadBean.getVersion_code());
                        return NEVER_PUSH;
                    }

                    // 根据参数判断获取推送id
                    return getPushId(op, pushVersionCode, playloadBean.getId());
                }
            }
        }
        return NEVER_PUSH;

    }

    /**
     * 根据接收到的规则返回推送id
     * @param op 运算符号（规则）
     * @param pushVersionCode 版本代号
     * @param pushId 推送id
     * @return 是否需要推送的结果
     */
    private static String getPushId(String op, int pushVersionCode, String pushId) {
        int localVersion = Integer.parseInt(Constant.versionCode);

        if(op.equals(">")) {
            if(localVersion > pushVersionCode) {
                LogUtils.i(LogTAG.PushTAG, "localVersion(" + localVersion + ")  >  " + "pushVersionCode(" +  pushVersionCode + ") 哦,需要推送！");
                return pushId;
            }
        }

        if(op.equals(">=")) {
            if(localVersion >= pushVersionCode) {
                LogUtils.i(LogTAG.PushTAG, "localVersion(" + localVersion + ")  >=  " + "pushVersionCode(" +  pushVersionCode + ") 哦,需要推送！");
                return pushId;
            }
        }

        if(op.equals("<")) {
            if(localVersion < pushVersionCode) {
                LogUtils.i(LogTAG.PushTAG, "localVersion(" + localVersion + ")  <  " + "pushVersionCode(" +  pushVersionCode + ") 哦,需要推送！");
                return pushId;
            }
        }

        if(op.equals("<=")) {
            if(localVersion <= pushVersionCode) {
                LogUtils.i(LogTAG.PushTAG, "localVersion(" + localVersion + ")  <=  " + "pushVersionCode(" +  pushVersionCode + ") 哦,需要推送！");
                return pushId;
            }
        }

        if(op.equals("=")) {
            if(localVersion == pushVersionCode) {
                LogUtils.i(LogTAG.PushTAG, "localVersion(" + localVersion + ")  =  " + "pushVersionCode(" +  pushVersionCode + ") 哦,需要推送！");
                return pushId;
            }
        }

//        DCStat.pushEvent(pushId,"","neverPush","GE_TUI");
        LogUtils.i(LogTAG.PushTAG, "localVersion(" + localVersion + ")  " + op + "  pushVersionCode(" +  pushVersionCode + ")？ 不对， 不需要推送！");
        return NEVER_PUSH;
    }


    /** 手机Log开关*/
    public static String parseOpenLog(String payloadString) {
        if(payloadString.contains(openLog)) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(payloadString);
                return jsonObject.getString(openLog);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

}
