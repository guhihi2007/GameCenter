package cn.lt.game.lib.util;

import android.content.Context;
import android.util.Log;
import android.view.View;

import cn.lt.game.application.MyApplication;
import cn.lt.game.bean.ConfigureBean;
import cn.lt.game.bean.SpreadBean;
import cn.lt.game.global.Constant;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.PreferencesUtils;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.model.GameBaseDetail;

import static cn.lt.game.global.Constant.AUTO_UPGRADE_POP_STATE;
import static cn.lt.game.global.Constant.CLIENT_UPDATE_POP_STATE;
import static cn.lt.game.global.Constant.SPREAD_STATUS;
import static cn.lt.game.update.PlatUpdateManager.getDialogShowLastTime;

/**
 * Created by ATian on 2016/11/18.
 *
 * @des 弹框管理
 */

public class PopWidowManageUtil {

    public static final String LAST_AUTO_INSTALL_TIME = "last_auto_install_time";//最后一次自动装弹框时间
    public static final String LAST_SPREAD_SHOW_TIME = "last_spread_show_time";//最后一次推广图弹框时间
    public static final String LAST_APP_AUTO_UPGRADE_TIME = "last_app_auto_upgrade_time";//最后一次应用自动升级弹框时间
    public static final String LAST_FLOAT_SHOW_TIME = "last_float_show_time";//最后一次浮层广告弹框时间
    public static final String BACKGROUND_TIME = "background_time";//按下home键的时间
    public static final String FRONT_DESK_TIME = "front_desk_time";//进入APP的时间

    /***
     * 自动装弹出框
     *
     * @param context
     * @return
     */
    public static boolean needAutoInstallDialog(Context context) {
        boolean state = PreferencesUtils.getBoolean(context, Constant.AUTO_INSTALL_POP_STATE, false);
        long lastTime = PreferencesUtils.getLong(context, LAST_AUTO_INSTALL_TIME, 0);
        long periodTime = PreferencesUtils.getLong(context, Constant.AUTO_INSTALL_PERIOD, Constant.DEFAULT_PERIOD);
        boolean hasShowed = PreferencesUtils.getBoolean(context, Constant.AUTO_INSTALL_SHOWED, false);
        return needShow(state, lastTime, periodTime, hasShowed);
    }

    /***
     * 平台升级框弹出时间
     *
     * @param context
     * @return
     */
    public static boolean needShowClientUpdateDialog(Context context) {
        boolean state = PreferencesUtils.getBoolean(context, CLIENT_UPDATE_POP_STATE, false);
        long lastTime = getDialogShowLastTime(context);
        long periodTime = PreferencesUtils.getLong(context, Constant.CLIENT_UPDATE_PERIOD, Constant.DEFAULT_PERIOD);
        boolean hasShowed = PreferencesUtils.getBoolean(context, Constant.CLIENT_UPDATE_SHOWED, false);
        return needShow(state, lastTime, periodTime, hasShowed);
    }

    /***
     * 弹窗推广时间
     *
     * @param context
     * @return
     */
    public static boolean needShowSpreadDialog(Context context) {
        long lastTime, periodTime;
        boolean hasShowed, state;
        try {
            state = PreferencesUtils.getBoolean(context, Constant.SELECTION_PLAY_POP_STATE, false);
            lastTime = PreferencesUtils.getLong(context, LAST_SPREAD_SHOW_TIME, 0);
            periodTime = PreferencesUtils.getLong(context, Constant.SELECTION_PLAY_PERIOD, Constant.DEFAULT_PERIOD);
            hasShowed = PreferencesUtils.getBoolean(context, Constant.SELECTION_PLAY_SHOWED, false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return needShow(state, lastTime, periodTime, hasShowed);
    }

    /***
     * 应用自动升级管理频次判断
     *
     * @param context
     * @return
     */
    public static boolean needShowAppAutoUpgradeDialog(Context context) {
        boolean state = PreferencesUtils.getBoolean(context, AUTO_UPGRADE_POP_STATE, false);
        long lastTime = PreferencesUtils.getLong(context, LAST_APP_AUTO_UPGRADE_TIME, 0);
        long periodTime = PreferencesUtils.getLong(context, Constant.AUTO_UPGRADE_PERIOD, Constant.DEFAULT_PERIOD);
        boolean hasShowed = PreferencesUtils.getBoolean(context, Constant.AUTO_UPGRADE_SHOWED, false);
        Log.i("LoadingActivity", "应用自动升级时间===" + periodTime);
        return needShow(state, lastTime, periodTime, hasShowed);
    }

    /**
     * @param context
     */
    public static void whetherShowAppAutoUpgradeDialog(Context context, GameBaseDetail mGame) {
        // 先决条件 1-2-3
        boolean condition1 = mGame.getState() == InstallState.upgrade || mGame.getState() == InstallState.ignore_upgrade;//是否是可升级状态
//        boolean condition2 = MyApplication.castFrom(context).getAutoUpGrade();//是否开启零流量开关
        boolean condition2 = PreferencesUtils.getBoolean(context, Constant.Setting.AUTOUPGRADE, true);//是否开启零流量开关
        boolean condition3 = ConditionsUtil.isMet(context);//是否满足升级条件
        if (condition1 && !condition2 && condition3) {
            // 弹框提示用户开启自动升级,时间后台配置
            if (needShowAppAutoUpgradeDialog(context)) {
                promptAutoUpgrade(context);
            }
        }
    }

    private static void promptAutoUpgrade(final Context context) {
        PreferencesUtils.putLong(context, LAST_APP_AUTO_UPGRADE_TIME, System.currentTimeMillis());//设置最后一次弹框时间
        PreferencesUtils.putBoolean(context, Constant.AUTO_UPGRADE_SHOWED, true);
        final MessageDialog exitDialog = new MessageDialog(context, "提示", "开启零流量升级，WiFi环境下将自动升级游戏", "取消", "确定");
        exitDialog.show();
        exitDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {

            @Override
            public void OnClick(View view) {
//                MyApplication.castFrom(exitDialog.getContext()).setAutoUpGrade(true);
                PreferencesUtils.putBoolean(context, Constant.Setting.AUTOUPGRADE, true);
            }
        });
        exitDialog.setLeftOnClickListener(new MessageDialog.LeftBtnClickListener() {

            @Override
            public void OnClick(View view) {
                exitDialog.dismiss();
            }
        });
    }

    /***
     * 一天上报一次ＣＩＤ
     *
     * @param context
     * @return
     */
    public static boolean needPostLoacalData(Context context) {
        long nowTime = System.currentTimeMillis();
        long lastTime = PreferencesUtils.getLong(context, Constant.POST_CID_PERIOD, TimeUtils.DAY * 1);
        long gapTime = (nowTime - lastTime) / (TimeUtils.DAY * 1 * 1);
        return gapTime > 0;
    }

    /***
     * 一天内只允许执行三次安装
     *
     * @param context
     * @return
     */
    public static boolean canExcuteInstall(Context context) {
        long nowTime = System.currentTimeMillis();
        long lastTime = PreferencesUtils.getLong(context, Constant.INSTALL_LIMIT, TimeUtils.DAY * 1);
        long gapTime = (nowTime - lastTime) / (TimeUtils.DAY * 1 * 1);
        return gapTime > 0;
    }

    /**
     * 进入后台，再次进入app是否展示开屏广告
     *
     * @param context
     * @return
     */
    public static boolean needSplashAD(Context context) {
        long time = PreferencesUtils.getLong(context, BACKGROUND_TIME, System.currentTimeMillis());
        long lastTIme = PreferencesUtils.getLong(context, FRONT_DESK_TIME, 0);
        long gapTime = lastTIme - time;
        long serverTime = PreferencesUtils.getLong(context, Constant.SPREAD_PERIOD, 0);
        return gapTime >= serverTime && serverTime != 0;

    }

    /***
     * 根据弹窗条件判断是否需要弹窗
     *
     * @param lastTime
     * @param periodTime
     * @param hasShowed
     * @return
     */
    private static boolean needShow(boolean state, Long lastTime, Long periodTime, boolean hasShowed) {
        if (state) {
            long nowTime = System.currentTimeMillis();
             /*如果获取间隔时间失效的情况*/
            if (periodTime == 0) {
                return !hasShowed;
            } else {
                if (lastTime == 0 || nowTime - lastTime >= periodTime) {
                    return true;
                } else {
                    return !hasShowed;
                }
            }
        } else {
            /*如果后台设置弹窗关闭则不需要弹窗*/
            return false;
        }
    }

    /***
     * 浮层广告弹出
     *
     * @param context
     * @return
     */
    public static boolean needShowFloatAds(Context context) {
        boolean state = PreferencesUtils.getBoolean(context, Constant.FLOAT_ADS_STATE, false);
        long lastTime = PreferencesUtils.getLong(context, LAST_FLOAT_SHOW_TIME, 0);
        long periodTime = PreferencesUtils.getLong(context, Constant.FLOAT_ADS_PERIOD, Constant.DEFAULT_PERIOD);
        boolean hasShowed = PreferencesUtils.getBoolean(context, Constant.FLOAT_ADS_SHOWED, false);
        return needShow(state, lastTime, periodTime, hasShowed);
    }

    /***
     * 保存弹框规则
     * @param context
     * @param config
     */
    public static void savePopWindowInfo(Context context, ConfigureBean config) {
        Long clientUpdateTime = PreferencesUtils.getLong(context, Constant.CLIENT_UPDATE_PERIOD, Constant.DEFAULT_PERIOD);//客户端升级弹框时间
        Long autoInstallTime = PreferencesUtils.getLong(context, Constant.AUTO_INSTALL_PERIOD, Constant.DEFAULT_PERIOD);//自动装弹框时间
        Long autoUpgradeTime = PreferencesUtils.getLong(context, Constant.AUTO_UPGRADE_PERIOD, Constant.DEFAULT_PERIOD);//应用自动升级时间
        Long spreadTime = PreferencesUtils.getLong(context, Constant.SELECTION_PLAY_PERIOD, Constant.DEFAULT_PERIOD);//精选必备
        Long floatTime = PreferencesUtils.getLong(context, Constant.FLOAT_ADS_PERIOD, Constant.DEFAULT_PERIOD);//精选必备
        Long spread_period = PreferencesUtils.getLong(context, Constant.SPREAD_PERIOD, Constant.DEFAULT_PERIOD);//开屏

        if (config.getGuangdiantong_of_tencent().getStatus()==1){
            LogUtils.i("LoadingActivity", "开屏广告已开启");
            PreferencesUtils.putBoolean(context, SPREAD_STATUS, true);
            if (spread_period != config.getGuangdiantong_of_tencent().getFrequency() * 1000) {
                PreferencesUtils.putLong(context, Constant.SPREAD_PERIOD, config.getGuangdiantong_of_tencent().getFrequency()* 1000);
            }
        }else{
            LogUtils.i("LoadingActivity", "开屏广告已关闭");
            PreferencesUtils.putBoolean(context, SPREAD_STATUS, false);
        }
        if (config.getClient_update().getStatus() == 1) {
            LogUtils.i("LoadingActivity", "平台升级弹框已开启");
            PreferencesUtils.putBoolean(context, CLIENT_UPDATE_POP_STATE, true);
            if (clientUpdateTime != config.getClient_update().getFrequency() * 1000) {
                PreferencesUtils.putLong(context, Constant.CLIENT_UPDATE_PERIOD, config.getClient_update().getFrequency() * 1000);
                PreferencesUtils.putBoolean(context, Constant.CLIENT_UPDATE_SHOWED, false);//后台有更改弹出规则时，复位该值
            }
        } else {
            LogUtils.i("LoadingActivity", "平台升级弹框时间已关闭");
//            PreferencesUtils.putLong(context, Constant.CLIENT_UPDATE_PERIOD, DEFAULT_PERIOD);//如果后台关闭弹窗控制，要使用默认值
            PreferencesUtils.putBoolean(context, CLIENT_UPDATE_POP_STATE, false);

        }
        if (config.getAuto_install().getStatus() == 1) {
            LogUtils.i("LoadingActivity", "自动装弹框已开启");
            PreferencesUtils.putBoolean(context, Constant.AUTO_INSTALL_POP_STATE, true);
            if (autoInstallTime != config.getAuto_install().getFrequency() * 1000) {
                PreferencesUtils.putLong(context, Constant.AUTO_INSTALL_PERIOD, config.getAuto_install().getFrequency() * 1000);
                PreferencesUtils.putBoolean(context, Constant.AUTO_INSTALL_SHOWED, false);//后台有更改弹出规则时，复位该值
            }
        } else {
//            PreferencesUtils.putLong(context, Constant.AUTO_INSTALL_PERIOD, DEFAULT_PERIOD);
            LogUtils.i("LoadingActivity", "自动装弹框时间已关闭");
            PreferencesUtils.putBoolean(context, Constant.AUTO_INSTALL_POP_STATE, false);
        }
        if (config.getAuto_upgrade().getStatus() == 1) {
            LogUtils.i("LoadingActivity", "应用自动升级弹框已开启");
            PreferencesUtils.putBoolean(context, Constant.AUTO_UPGRADE_POP_STATE, true);
            if (autoUpgradeTime != config.getAuto_upgrade().getFrequency() * 1000) {
                PreferencesUtils.putLong(context, Constant.AUTO_UPGRADE_PERIOD, config.getAuto_upgrade().getFrequency() * 1000);
                PreferencesUtils.putBoolean(context, Constant.AUTO_UPGRADE_SHOWED, false);//后台有更改弹出规则时，复位该值
            }
        } else {
//            PreferencesUtils.putLong(context, Constant.AUTO_UPGRADE_PERIOD, DEFAULT_PERIOD);
            LogUtils.i("LoadingActivity", "应用自动升级弹框时间已关闭");
            PreferencesUtils.putBoolean(context, Constant.AUTO_UPGRADE_POP_STATE, false);
        }
        if (config.getSelection_play().getStatus() == 1) {
            LogUtils.i("LoadingActivity", "精选必玩弹框已开启");
            PreferencesUtils.putBoolean(context, Constant.SELECTION_PLAY_POP_STATE, true);
            if (null != config.getSelection_play() && spreadTime != config.getSelection_play().getFrequency() * 1000) {
                PreferencesUtils.putLong(context, Constant.SELECTION_PLAY_PERIOD, config.getSelection_play().getFrequency() * 1000);
                PreferencesUtils.putBoolean(context, Constant.SELECTION_PLAY_SHOWED, false);//后台有更改弹出规则时，复位该值
            }
        } else {
            LogUtils.i("LoadingActivity", "精选必玩弹框时间已关闭");
            PreferencesUtils.putBoolean(context, Constant.SELECTION_PLAY_POP_STATE, false);
        }
        if (config.getFloating_ads() != null && config.getFloating_ads().getStatus() == 1) {
            LogUtils.i("LoadingActivity", "浮层广告弹框已开启" + config.getFloating_ads().getFrequency());
            PreferencesUtils.putBoolean(context, Constant.FLOAT_ADS_STATE, true);
            if (floatTime != config.getFloating_ads().getFrequency() * 1000) {
                PreferencesUtils.putLong(context, Constant.FLOAT_ADS_PERIOD, config.getFloating_ads().getFrequency() * 1000);
                PreferencesUtils.putBoolean(context, Constant.FLOAT_ADS_SHOWED, false);//后台有更改弹出规则时，复位该值
            }
        } else {
            LogUtils.i("LoadingActivity", "浮层广告弹框时间已关闭");
            PreferencesUtils.putBoolean(context, Constant.FLOAT_ADS_STATE, false);
        }

        //热点开关状态的保存
        if (config.getHot_content().getStatus() == 1) {
            PreferencesUtils.putBoolean(context,Constant.HOT_CENTENT_STATE,true);
        } else {
            PreferencesUtils.putBoolean(context,Constant.HOT_CENTENT_STATE,false);
        }
    }

    /***
     * 保存开屏广告信息
     * @param context
     * @param bean
     */
    public static void saveSpeadAdInfo(Context context, SpreadBean bean) {
        if (bean.getGuangdiantong_of_tencent().getStatus() == 1) {
            PreferencesUtils.putLong(context, Constant.SPREAD_PERIOD, bean.getGuangdiantong_of_tencent().getTime());
        }
    }

    /** 判断热点相关页面是否可跳转*/
    public static boolean hotContentIsReady() {
        return MyApplication.application.switchIsReady && PreferencesUtils.getBoolean(MyApplication.application, Constant.HOT_CENTENT_STATE, false);
    }
}
