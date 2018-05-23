package cn.lt.game.application.wakeup;

import android.content.Context;

import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.model.SharePreferencesKey;

/**
 * Created by LinJunSheng on 2016/10/31.
 * 获取沉默用户类型
 */

public class SilenceUserTypeGeter {

    private Context context;

    public SilenceUserTypeGeter(Context context) {
        this.context = context;
    }

    /**
     * 联网不启动
     */
    public final static int NETWORKING_BUT_NOT_START = 1;

    /**
     * 启动不下载
     */
    public final static int START_BUT_NOT_DOWNLOAD = 2;

    /**
     * 正常使用的用户
     */
    public final static int NORMAL_USER = 3;

    public int getUserType() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(context, SharePreferencesKey.WAKEUP_SILENCE_USER, Context.MODE_PRIVATE);

//        long firstStartTime = spUtil.getLong(SharePreferencesKey.FIRST_START_TIME);
        long firstStartTime = WakeUpUserTimer.getFirstStartTime(context);
        LogUtils.i(LogTAG.wakeUpUser, "(沉默类型)firstStartTime = " + firstStartTime);

        if (firstStartTime == 0) {
            LogUtils.i(LogTAG.wakeUpUser, "(沉默类型)firstStartTime是0，这是联网不启动的用户");

            long firstNetworkingTime = spUtil.getLong(SharePreferencesKey.FIRST_NETWORKING_TIME);
            if (firstNetworkingTime == 0) {
                LogUtils.i(LogTAG.wakeUpUser, "(沉默类型)firstNetworkingTime是0，记录当前时间为第一次联网时间");
                spUtil.add(SharePreferencesKey.FIRST_NETWORKING_TIME, System.currentTimeMillis());
            }
            spUtil = null;
            return NETWORKING_BUT_NOT_START;

        } else {

            long firstDownloadTime = WakeUpUserTimer.getFirstDownloadTime(context);
            if (firstDownloadTime == 0) {
                LogUtils.i(LogTAG.wakeUpUser, "(沉默类型)firstDownloadTime是0，这是启动不下载的用户");
                spUtil = null;
                return START_BUT_NOT_DOWNLOAD;
            }
        }

        LogUtils.i(LogTAG.wakeUpUser, "(沉默类型)这是正常使用游戏中心的用户");
        return NORMAL_USER;
    }
}
