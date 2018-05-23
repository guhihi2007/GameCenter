package cn.lt.game.application.wakeup;

import android.content.Context;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.lt.game.bean.PushBaseBean;
import cn.lt.game.global.LogTAG;
import cn.lt.game.bean.PushAppInfo;
import cn.lt.game.bean.PushH5Bean;
import cn.lt.game.bean.RequestCountBean;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.lib.util.GsonUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.util.threadpool.LTAsyncTask;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.model.SharePreferencesKey;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.notification.LTNotificationManager;

/**
 * Created by LinJunSheng on 2016/10/31.
 * 沉默用户拉活管理类
 */
public class WakeUpSilenceUserManager {

    private static WakeUpSilenceUserManager instance;

    public void setInstanceNull() {
        LogUtils.i(LogTAG.wakeUpUser, "setInstanceNull==============>>>>>");
        timerTask.cancel();
        instance = null;
        System.gc();
    }

    /**
     * 空数据类型（数据可能被清空了）
     */
    public final static int noData = -1;
    private static final long SIX_HOURS = 21600000;

    private final TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            check();
        }
    };

    private final ReciprocalTimeMachine reciprocalTimeMachine;

    private Context context;
    private final Timer timer;
    private final SilenceUserTypeGeter userTypeGeter;
    private final TimeIntervalTypeGeter timeIntervalTypeGeter;

    private int userType = 0;
    private int timeIntervalType = 0;


    private WakeUpSilenceUserManager(Context context, Timer timer) {
        this.context = context;
        LogUtils.i(LogTAG.wakeUpUser, "WakeUpSilenceUserManager创建实例");
        this.timer = timer;
        this.userTypeGeter = new SilenceUserTypeGeter(context);
        this.timeIntervalTypeGeter = new TimeIntervalTypeGeter(context);
        this.reciprocalTimeMachine = new ReciprocalTimeMachine(context);

    }

    public static WakeUpSilenceUserManager getInstance(Context context, Timer timer) {
        if (instance == null) {
            synchronized (WakeUpSilenceUserManager.class) {
                if (instance == null) {
                    instance = new WakeUpSilenceUserManager(context, timer);
                }
            }
        }

        return instance;
    }

    public void startCheck() {
        // 先计算首次等待时间，之后6小时执行一次（21600000）
        try {
            timer.schedule(timerTask, reciprocalTimeMachine.getFirstTime(), SIX_HOURS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void check() {

        // 判断沉默用户的类型 1.联网不启动 2.启动不联网
        getSilenceUserType();

        // 判断对应沉默用户的时间间隔类型
        judgeTimeIntervalType();

        // 请求拉活用户通知数据
        requestNoticeData();
    }

    /**
     * 判断沉默用户的类型
     * 1.联网不启动
     * 2.启动不联网
     */
    private void getSilenceUserType() {
        userType = userTypeGeter.getUserType();
    }

    /**
     * 判断对应沉默用户的时间间隔类型
     */
    private void judgeTimeIntervalType() {
        timeIntervalType = timeIntervalTypeGeter.getTimeIntervalType(userType);
    }

    /**
     * 请求拉活用户通知数据
     */
    private void requestNoticeData() {
        if (timeIntervalType == noData) {
            LogUtils.i(LogTAG.wakeUpUser, "没有满足沉默用户的时间条件，so不发出请求");
            return;
        }

        // 判断时间段（8:00-24:00）
        if (!isMeetTheTime()) {
            return;
        }

        if (NetUtils.isConnected(context)) {
            LogUtils.i(LogTAG.wakeUpUser, "现在是联网状态，进行请求次数判断...");

            boolean canRequest = judgeCanRequest();

            if (canRequest) {
                goRequset();
            }
        } else {
            LogUtils.i(LogTAG.wakeUpUser, "非联网状态，终止请求并且不记录请求次数");
        }


    }

    private boolean isMeetTheTime() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.US);

        try {
            //定义区间值
            Date dateAfter = df.parse("24:00");
            Date dateBefor = df.parse("8:00");


            //将你输入的String 数据转化为Date
            Date time = df.parse(TimeUtils.getLongtoTime(System.currentTimeMillis()));

            //判断time是否在XX之后，并且 在XX之前
            if (time.before(dateAfter) && time.after(dateBefor)) {
                LogUtils.i(LogTAG.wakeUpUser, "当前时间是" + TimeUtils.getLongtoTime(System.currentTimeMillis()) + "，在（8:00-24:00）区间");
                return true;
            } else {
                LogUtils.i(LogTAG.wakeUpUser, "当前时间是" + TimeUtils.getLongtoTime(System.currentTimeMillis()) + "，不在（8:00-24:00）区间，不进行请求了");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 判断能否发送请求
     */
    private boolean judgeCanRequest() {
        boolean canRequest = true;

        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(context, SharePreferencesKey.WAKEUP_SILENCE_USER, Context.MODE_PRIVATE);
        String requestCountData = spUtil.get(SharePreferencesKey.REQUEST_COUNT_DATA);
        LogUtils.i(LogTAG.wakeUpUser, "judgeCanRequest -- > requestCountBean_json = " + requestCountData);
        if (!TextUtils.isEmpty(requestCountData)) {
            RequestCountBean requestCountBean;
            try {
                requestCountBean = GsonUtil.GsonToBean(requestCountData, RequestCountBean.class);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.i(LogTAG.wakeUpUser, "judgeCanRequest -- > GSON解析RequestCountBean出错");
                return false;
            }

            // 同一天，需要判断请求次数，否则就是当天没请求过
            if (requestCountBean.isSameDate(System.currentTimeMillis())) {
                LogUtils.i(LogTAG.wakeUpUser, "日期是 = " + requestCountBean.getDate() + "与手机时间为同一天，马上查看请求次数....");

                if (requestCountBean.isExceed3()) {
                    LogUtils.i(LogTAG.wakeUpUser, "请求次数 = " + requestCountBean.getRequestCount() + ", 超过3次，今天不再发情请求！");
                    canRequest = false;
                } else {
                    LogUtils.i(LogTAG.wakeUpUser, "请求次数 = " + requestCountBean.getRequestCount() + ", 没有超过3次，准备发起请求！");
                }
            }

            // 可以发起请求的话，保存请求次数相关的数据
            if (canRequest) {
                saveRequestCountData(requestCountBean);
            }

        } else {
            saveRequestCountData(new RequestCountBean());
            LogUtils.i(LogTAG.wakeUpUser, "没有保存过的相关请求次数数据，直接发送请求~ = ");
        }

        return canRequest;

    }

    /**
     * 保存请求次数相关的数据
     */
    private void saveRequestCountData(RequestCountBean requestCountBean) {
        // 同一天，只需更新请求次数
        if (requestCountBean.isSameDate(System.currentTimeMillis())) {
            requestCountBean.add1();
            requestCountBean.setLastRequestTime(System.currentTimeMillis());
            LogUtils.i(LogTAG.wakeUpUser, "(保存请求次数相关的数据) 同一天，只需更新请求次数, count = " + requestCountBean.getRequestCount());
        } else {
            // 不是同一天，更新请求日期，和记录次数为1
            requestCountBean.setDate(TimeUtils.getLongtoString(System.currentTimeMillis()));
            requestCountBean.setRequestCount(1);
            requestCountBean.setLastRequestTime(System.currentTimeMillis());
            LogUtils.i(LogTAG.wakeUpUser, "(保存请求次数相关的数据) 不是同一天，更新请求日期为 = " + requestCountBean.getDate() + "，和记录次数为1");
        }

        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(context, SharePreferencesKey.WAKEUP_SILENCE_USER, Context.MODE_PRIVATE);
        spUtil.add(SharePreferencesKey.REQUEST_COUNT_DATA, GsonUtil.GsonString(requestCountBean));
    }


    /**
     * 发送请求
     */
    private void goRequset() {
        LTAsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(getWaitTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LogUtils.e(LogTAG.wakeUpUser, "散裂分布式请求 --> 线程睡眠出异常，本次随机请求前的等待时间无效。。");
                }

                pushMessage();

                // 发送请求
                reportData();

            }
        });

    }

    public void pushMessage() {
        Map<String, String> params = new HashMap<>();
        params.put("user_type", userType + "");
        params.put("week_between", timeIntervalType + "");
        LogUtils.i(LogTAG.wakeUpUser, "发起推送请求了。type==>" + userType + "/" + timeIntervalType);

        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.SILENCE_USER_NOTICE_URI, params, new WebCallBackToObject<UIModuleList>() {

            @Override
            protected void handle(UIModuleList info) {
                if (info.size() > 0) {
                    UIModule module = (UIModule) info.get(0);
                    judgeNoticeId(module);

                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.i(LogTAG.wakeUpUser, "请求不成功，responseCode = " + statusCode);
            }

        });

    }

    private void judgeNoticeId(UIModule module) {
        String noticeId = getNoticeId(module);

        if (!TextUtils.isEmpty(noticeId)) {
            boolean isSameId = NoticeIdComparator.isSameId(context, Integer.valueOf(noticeId));

            if (!isSameId) {
                LTNotificationManager.getinstance().sendPushMessage(module);
                LogUtils.i(LogTAG.wakeUpUser, "通知成功发送出去了！");
            }
        }
    }

    private String getNoticeId(UIModule module) {

        if (module.getUIType() == PresentType.push_app
                || module.getUIType() == PresentType.push_game) {

            if(module.getUIType() == PresentType.push_app) {
                LogUtils.i(LogTAG.wakeUpUser, "请求成功（通知类型 -> 平台升级），请求到的通知 ID = " + ((PushBaseBean) module.getData()).getId());
            }

            if(module.getUIType() == PresentType.push_game) {
                LogUtils.i(LogTAG.wakeUpUser, "请求成功（通知类型 -> 游戏），请求到的通知 ID = " + ((PushBaseBean) module.getData()).getId());
            }


            PushAppInfo data = ((PushAppInfo) module.getData());
            data.isFromWakeUp = true;
            return data.getId();
        } else if (module.getUIType() == PresentType.push_topic) {
            LogUtils.i(LogTAG.wakeUpUser, "请求成功（通知类型 -> 专题），请求到的通知 ID = " + ((PushBaseBean) module.getData()).getNotice_id());
            PushBaseBean data = ((PushBaseBean) module.getData());
            data.isFromWakeUp = true;
            return data.getNotice_id();
        } else if (module.getUIType() == PresentType.push_h5) {
            LogUtils.i(LogTAG.wakeUpUser, "请求成功（通知类型 -> h5），请求到的通知 ID = " + ((PushBaseBean) module.getData()).getId());
            PushH5Bean data = ((PushH5Bean) module.getData());
            data.isFromWakeUp = true;
            return data.getId() + "";
        }
        return null;
    }


    private void reportData() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(context, SharePreferencesKey.WAKEUP_SILENCE_USER, Context.MODE_PRIVATE);
        long firstNetworkingTime = spUtil.getLong(SharePreferencesKey.FIRST_NETWORKING_TIME);
        long firstStartTime = WakeUpUserTimer.getFirstStartTime(context);
        long firstDownloadTime = WakeUpUserTimer.getFirstDownloadTime(context);
        long curPhoneTime = System.currentTimeMillis();
        String remark = " userType=" + userType
                + " | timeIntervalType=" + timeIntervalType
                + " | curPhoneTime=" + TimeUtils.getStringToDateHaveHour(curPhoneTime)
                + " | firstNetworkingTime=" + (firstNetworkingTime == 0 ? 0 : TimeUtils.getStringToDateHaveHour(firstNetworkingTime))
                + " | firstStartTime=" + (firstStartTime == 0 ? 0 : TimeUtils.getStringToDateHaveHour(firstStartTime))
                + " | firstDownloadTime=" + (firstDownloadTime == 0 ? 0 : TimeUtils.getStringToDateHaveHour(firstDownloadTime));
        DCStat.pushEventByWakeupUser("",  "", "", "request", "WAKE_UP", remark, "");
    }

    /**
     * 计算随机请求等待时间（实现散裂分布式请求）
     */
    private long getWaitTime() {
        int waitTime = (int) (1 + Math.random() * 20);
        LogUtils.i(LogTAG.wakeUpUser, "散裂分布式请求 --> 随机计算请求前的等待时间 = " + waitTime + "秒");
        return waitTime * 1000;
    }


}
