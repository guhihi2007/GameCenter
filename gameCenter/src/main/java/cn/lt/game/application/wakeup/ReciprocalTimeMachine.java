package cn.lt.game.application.wakeup;

import android.content.Context;
import android.text.TextUtils;

import cn.lt.game.global.LogTAG;
import cn.lt.game.bean.RequestCountBean;
import cn.lt.game.lib.util.GsonUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.model.SharePreferencesKey;

/**
 * Created by LinJunSheng on 2016/11/3.
 */

public class ReciprocalTimeMachine {
    private static final long SIX_HOURS = 21600000;
    private Context context;

    public ReciprocalTimeMachine(Context context) {
        this.context = context;
    }

    public long getFirstTime() {
        long reciprocalTime = 0;

        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(context, SharePreferencesKey.WAKEUP_SILENCE_USER, Context.MODE_PRIVATE);

        String requestCountData = spUtil.get(SharePreferencesKey.REQUEST_COUNT_DATA);
        LogUtils.i(LogTAG.wakeUpUser, "getFirstTime -- > requestCountBean_json = " + requestCountData);
        if (!TextUtils.isEmpty(requestCountData)) {
            try {
                RequestCountBean requestCountBean = GsonUtil.GsonToBean(requestCountData, RequestCountBean.class);

                if (requestCountBean.isSameDate(System.currentTimeMillis())) {

                    long lastRequstTiem = requestCountBean.getLastRequestTime();
                    if (System.currentTimeMillis() - lastRequstTiem < SIX_HOURS) {
                        reciprocalTime = SIX_HOURS - (System.currentTimeMillis() - lastRequstTiem);
                        LogUtils.i(LogTAG.wakeUpUser, "(计算首次倒数时间)同一天已经请求过数据，离下一次请求时间还差 = " + TimeUtils.getSurplusTimeString(reciprocalTime));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.i(LogTAG.wakeUpUser, "getFirstTime -- > GSON解析RequestCountBean出错");
            }


        } else {
            LogUtils.i(LogTAG.wakeUpUser, "(计算首次倒数时间)查询requestCountData为空，即时从来没有请求过");
        }

        if (reciprocalTime == 0) {
            //延迟10秒后再执行，可以保证如果用户启动了游戏中心，会保存首次启动时间，这样之后再执行检测沉默用户就能准确判断沉默用户类型了
            reciprocalTime = 10000;
            LogUtils.i(LogTAG.wakeUpUser, "(计算首次倒数时间)ReciprocalTime == 0，10秒钟后进行沉默用户检测");
        }

        return reciprocalTime;
    }
}
