package cn.lt.game.application.wakeup;

import android.content.Context;

import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.model.SharePreferencesKey;

import static cn.lt.game.application.wakeup.SilenceUserTypeGeter.NETWORKING_BUT_NOT_START;
import static cn.lt.game.application.wakeup.SilenceUserTypeGeter.START_BUT_NOT_DOWNLOAD;
import static cn.lt.game.application.wakeup.WakeUpSilenceUserManager.noData;

/**
 * Created by LinJunSheng on 2016/10/31.
 * 获取间隔时间（拉活沉默用户模块用）
 */

public class TimeIntervalTypeGeter {
    private Context context;

    /**
     * 4周>= time >=2周
     */
    public final static int BETWEEN_2WEEKS_AND_4WEEKS = 1;

    /**
     * 6周>= time >4周
     */
    public final static int BETWEEN_4WEEKS_AND_6WEEKS = 2;

    /**
     * time >6周
     */
    public final static int OVER_6WEEKS = 3;



    public TimeIntervalTypeGeter(Context context) {
        this.context = context;
    }

    public int getTimeIntervalType(int silenceUserType) {

        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(context, SharePreferencesKey.WAKEUP_SILENCE_USER, Context.MODE_PRIVATE);

        switch (silenceUserType) {
            case NETWORKING_BUT_NOT_START:
                long firstNetworkingTime = spUtil.getLong(SharePreferencesKey.FIRST_NETWORKING_TIME);
                if (firstNetworkingTime == 0) {
                    spUtil.add(SharePreferencesKey.FIRST_NETWORKING_TIME, System.currentTimeMillis());
                    LogUtils.i(LogTAG.wakeUpUser, "(判断时间)判断时间段类型 ， firstNetworkingTime = 0");
                    return noData;
                }
                return judgeType(firstNetworkingTime);
            case START_BUT_NOT_DOWNLOAD:
                long firstStartTime = WakeUpUserTimer.getFirstStartTime(context);
                if (firstStartTime == 0) {
                    LogUtils.i(LogTAG.wakeUpUser, "(判断时间)判断时间段类型 ， firstStartTime = 0");
                    return noData;
                }

                return judgeType(firstStartTime);
        }

        return noData;
    }

    private int judgeType(long time) {
        if (isExceedDay(time, 14) && isAtMostDay(time, 28)) {
            LogUtils.i(LogTAG.wakeUpUser, "(判断时间)时间段类型 = （4周>= time >=2周）");
            return BETWEEN_2WEEKS_AND_4WEEKS;
        }

        if (isExceedDay(time, 28) && isAtMostDay(time, 42)) {
            LogUtils.i(LogTAG.wakeUpUser, "(判断时间)时间段类型 = （6周>= time >4周）");
            return BETWEEN_4WEEKS_AND_6WEEKS;
        }

        if (isExceedDay(time, 42)) {
            LogUtils.i(LogTAG.wakeUpUser, "(判断时间)时间段类型 =  （time >6周）");
            return OVER_6WEEKS;
        }

        LogUtils.i(LogTAG.wakeUpUser, "(判断时间)沉默时长只有 " + TimeUtils.getSurplusTimeString(System.currentTimeMillis() - time));

        return noData;
    }


    /**
     * 判断是否大于多少天
     *
     * @param recordTime 时间
     * @param dayCount   天数
     */
    private boolean isExceedDay(long recordTime, long dayCount) {
        boolean flag = false;

        long curTime = System.currentTimeMillis();

        if ((curTime - recordTime) >= (1000 * 3600 * 24 * dayCount)) {
            flag = true;
        }

        return flag;
    }

    /**
     * 判断是否小于多少天
     *
     * @param recordTime 时间
     * @param dayCount   天数
     */
    private boolean isAtMostDay(long recordTime, long dayCount) {
        boolean flag = false;

        long curTime = System.currentTimeMillis();

        if ((curTime - recordTime) <= (1000 * 3600 * 24 * dayCount)) {
            flag = true;
        }

        return flag;
    }


}
