package cn.lt.game.ui.app.awardgame.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.global.LogTAG;
import cn.lt.game.application.MyApplication;
import cn.lt.game.lib.netdata.ErrorFlag;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.web.WebCallBackToBean;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.threadPool.ThreadPoolProxyFactory;
import cn.lt.game.ui.app.awardgame.bean.AwardDetailBean;
import cn.lt.game.ui.app.awardgame.bean.AwardInfoBean;
import cn.lt.game.ui.app.awardgame.dialog.NormalDialog;
import cn.lt.game.ui.app.awardgame.dialog.PhysicalAwardDialog;
import cn.lt.game.ui.app.awardgame.dialog.PhysicalEditDialog;
import cn.lt.game.ui.app.awardgame.listener.AwardUpdateListener;
import cn.lt.game.ui.app.awardgame.listener.ItenFocusListener;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

/**
 * @author chengyong
 * @time 2017/6/1 14:32
 * @des ${抽奖盘view}
 */

public class AwardsPlateView extends LinearLayout implements View.OnClickListener, UserInfoUpdateListening {
    private final Context context;
    public SingleAwardsView startView;
    private SingleAwardsView itemView1, itemView2, itemView3, itemView4, itemView6, itemView7, itemView8, itemView9;

    private ItenFocusListener[] itemViewArr = new ItenFocusListener[8];
    private int currentIndex = 0;
    private int copyCurrentIndex = 0;
    private int currentTotal = 0; //循环次数
    private int stayIndex = 0;
    private boolean isGameRunning = false;
    private boolean isTryToStop = false;

    private static final int DEFAULT_SPEED = 150;
    private static final int MIN_SPEED = 50;
    private int currentSpeed = DEFAULT_SPEED;
    private TextView mAwardNumberExpaire;
    private TextView mAwardJoinNumber;
    private volatile int prize_style;
    private String awardId;
    public List<Integer> failurePositions = new ArrayList<>();
    private AwardUpdateListener awardUpdateListener;
    private int wasteScore;
    private String exchange_code;
    private SharedPreferencesUtil mSp;
    private String prize_alias;
    private String prize_words;
    private int mFreeLotteryNumber;
    private AwardInfoBean awardInfoBean = new AwardInfoBean();
    private AwardDetailBean mAwardDetailBean = new AwardDetailBean();
    private String message;
    private boolean interrupt;
    private boolean isTimeOut;
    public static final String AWARDCOUPON="coupon";
    public AwardsPlateView(@NonNull Context context) {
        this(context, null);
    }

    public AwardsPlateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AwardsPlateView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_award_plate, this);
        initView();
        this.context = context;
        mSp = new SharedPreferencesUtil(context);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtils.i(LogTAG.CHOU, "onDetachedFromWindow");
        isGameRunning = false;
        UserInfoManager.instance().removeListening(this);
    }

    private void initView() {
        startView = (SingleAwardsView) findViewById(R.id.start);
        startView.getmAwardDes().setVisibility(GONE);
        startView.getmAwardIconView().setVisibility(GONE);
        startView.setBackGound(R.drawable.award_start_selector);
        mAwardNumberExpaire = (TextView) findViewById(R.id.award_number_expaire);
        mAwardJoinNumber = (TextView) findViewById(R.id.award_join_number_tv);
        itemView1 = (SingleAwardsView) findViewById(R.id.item1);
        itemView2 = (SingleAwardsView) findViewById(R.id.item2);
        itemView3 = (SingleAwardsView) findViewById(R.id.item3);
        itemView4 = (SingleAwardsView) findViewById(R.id.item4);
        itemView6 = (SingleAwardsView) findViewById(R.id.item6);
        itemView7 = (SingleAwardsView) findViewById(R.id.item7);
        itemView8 = (SingleAwardsView) findViewById(R.id.item8);
        itemView9 = (SingleAwardsView) findViewById(R.id.item9);
        itemViewArr[0] = itemView1;
        itemViewArr[1] = itemView2;
        itemViewArr[2] = itemView3;
        itemViewArr[3] = itemView6;
        itemViewArr[4] = itemView9;
        itemViewArr[5] = itemView8;
        itemViewArr[6] = itemView7;
        itemViewArr[7] = itemView4;
        UserInfoManager.instance().addRealListening(this);
    }

    /**
     * 请求网络成功绑定数据
     *
     * @param awardInfoBean
     * @param isRefreshByTimeout
     * @return
     */
    public void setAwardData(AwardInfoBean awardInfoBean, boolean isRefreshByTimeout) {
        this.awardInfoBean = awardInfoBean;
        try {
            LogUtils.i(LogTAG.CHOU, "刷新活动数据，是来自于活动过期么？" + isRefreshByTimeout);
            if (isRefreshByTimeout) {
                for (ItenFocusListener itenFocusListener : itemViewArr) {
                    itenFocusListener.setFocus(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            wasteScore = awardInfoBean.waste_point_number;
            mFreeLotteryNumber = awardInfoBean.free_lottery_number;
            List<AwardInfoBean.PrizesBean> prizes = awardInfoBean.prizes;
            if (prizes.size() == 8) {
                for (int i = 0; i < 8; i++) {
                    itemViewArr[i].setAwardMessage(prizes.get(i).prize_pic, prizes.get(i).prize_alias);
                    setFailurePositionTag(prizes, i);
                }
            }
            mAwardNumberExpaire.setText(UserInfoManager.instance().isLogin() ? ((awardInfoBean.free_lottery_number == 0) ? getStringSource(R.string.award_message_tips_lost_score, wasteScore) : getStringSource(R.string.award_message_tips_free_times_login, awardInfoBean.free_lottery_number)) : getStringSource(R.string.award_message_tips_free_times_unlogin, awardInfoBean.free_lottery_number));
            mAwardJoinNumber.setText(getStringSource(R.string.award_message_join_num, awardInfoBean.participants_number));
            if (awardInfoBean.unaccepted_number > 0) {
                if (System.currentTimeMillis() - getFirstTime() > 24 * 60 * 60 * 1000) {
                    showNormalDialog(getStringSource(R.string.award_title_tips), getStringSource(R.string.award_message_tips_not_get, awardInfoBean.unaccepted_number), getStringSource(R.string.award_button_get));
                    saveFirstTime();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新免费抽奖次数、抽奖消耗积分
     *
     * @param free_lottery_number
     * @called by 登录后、抽完奖、刚进活动页面
     */
    public void updateFreeNum(int free_lottery_number) {
        mFreeLotteryNumber = free_lottery_number;
        mAwardNumberExpaire.setText((free_lottery_number == 0) ? getStringSource(R.string.award_message_tips_lost_score, wasteScore) : getStringSource(R.string.award_message_tips_free_times_login, free_lottery_number));
    }

    private void saveFirstTime() {
        // 第一次提示有待领取奖品时间
        mSp.add(SharedPreferencesUtil.AWARD_SHOWTIME, System.currentTimeMillis());
    }

    private float getFirstTime() {
        // 第一次提示时间
        return mSp.getLong(SharedPreferencesUtil.AWARD_SHOWTIME);
    }

    /**
     * 设置不中奖的标记位
     *
     * @param prizes
     * @param i
     */
    private void setFailurePositionTag(List<AwardInfoBean.PrizesBean> prizes, int i) {
        if (Integer.parseInt(prizes.get(i).prize_style) == 0) {
            failurePositions.add(prizes.get(i).position);
        }
    }

    /**
     * 动态改变间歇时间，控制速度
     *
     * @return
     */
    private long getInterruptTime() {
        currentTotal++;
        if (isTryToStop) {  //点击停止后的速度控制
            currentSpeed += 10;  //速度由快(50ms)变慢（150ms）-->最后稳定（150ms循环一次）
            if (currentSpeed > DEFAULT_SPEED) {
                currentSpeed = DEFAULT_SPEED;
            }
            Log.e("juice", "speed请求网络完成后的循环：当前循环次数currentTotal==" + currentTotal + "==当前的间歇时间是==" + currentSpeed);
        } else {
            if (currentTotal / itemViewArr.length > 0) {
                currentSpeed -= 10;
            }
            if (currentSpeed < MIN_SPEED) {
                currentSpeed = MIN_SPEED; //速度由慢（150ms）变快(50ms)-->最后稳定（50ms循环一次）
            }
//            Log.e("juice", "speed正常在循环：当前循环次数currentTotal==" + currentTotal+"==当前的间歇时间是==" + currentSpeed);
        }
        return currentSpeed;
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 开始抽奖
     *
     * @param v
     * @param points
     * @called by AwardFragment
     */
    public void startGame(final View v, final int points) {
        LogUtils.i(LogTAG.CHOU, "==点击抽奖了==");
        if (checkNet()) {
            return;
        }
        if (!UserInfoManager.instance().isLogin()) {
            ToastUtils.showToast(context, "请先登录");
            Intent intent = UserInfoManager.instance().getLoginIntent(context, true);
            context.startActivity(intent);
            return;
        }
        LogUtils.i(LogTAG.CHOU, "当前积分==" + points + "当前第几次==" + (mAwardDetailBean.today_lottery_number + 1) + "总次==" + awardInfoBean.max_lottery_number);
//        if (awardInfoBean.today_lottery_number == awardInfoBean.max_lottery_number || mAwardDetailBean.today_lottery_number + 1 >= awardInfoBean.max_lottery_number) {
//            showNormalDialog(getStringSource(R.string.award_title_tips), getStringSource(R.string.award_message_tips_times), getStringSource(R.string.award_button_earn_score_now));
//            return;
//        }
        LogUtils.i(LogTAG.CHOU, "下次抽奖将要消耗积分=" + wasteScore + "当前积分=" + points);
        if (mFreeLotteryNumber == 0 && (points < wasteScore || points == 0)) {
            showNormalDialog(getStringSource(R.string.award_title_tips), getStringSource(R.string.award_message_tips_lack_score), getStringSource(R.string.award_button_earn_score));
            return;
        }
        if (interrupt) {
            ToastUtils.showToast(context, "正在抽奖，稍后再试");
            return;
        }
        LogUtils.i(LogTAG.CHOU, "设置为在抽奖中=");
        interrupt = true;
        isGameRunning = true;
        isTryToStop = false;
        currentSpeed = DEFAULT_SPEED;
        if (awardUpdateListener != null) {
            awardUpdateListener.updateScoreByManual(wasteScore);
        }
        executeAward();
        ThreadPoolProxyFactory.getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                synchronized (AwardsPlateView.class) {
                    while (isGameRunning) {
                        try {
                            Thread.sleep(getInterruptTime()); //改变时间来控制循环的速度
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!isGameRunning) {
                            LogUtils.i(LogTAG.CHOU, "锁住，应该返回");
                            break;
                        }
                        post(new Runnable() {
                            @Override
                            public void run() {
                                int preIndex = currentIndex;
                                currentIndex++;
                                if (currentIndex >= itemViewArr.length) {
                                    currentIndex = 0; //TODO 是否控制转的圈数
                                }
                                itemViewArr[preIndex].setFocus(false);
                                itemViewArr[currentIndex].setFocus(true);
                                LogUtils.i(LogTAG.CHOU, "isTryToStop" + isTryToStop + "==currentSpeed" + currentSpeed + "==stayIndex" + stayIndex);
                                if (isTryToStop && currentSpeed == DEFAULT_SPEED && stayIndex == currentIndex) {
                                    isGameRunning = false; //当前选中的与设定一致,确定结果，结束循环
                                    LogUtils.i(LogTAG.CHOU, "转盘停止，currentIndex=" + currentIndex + "---stayIndex=" + stayIndex + "时间" + System.currentTimeMillis());
                                    if (isTimeOut) {
                                        LogUtils.i(LogTAG.CHOU, "转盘停止，活动过期，弹刷新框");
                                        showNormalDialog(getStringSource(R.string.award_title_tips), getStringSource(R.string.award_message_refresh), getStringSource(R.string.award_button_refresh));
                                    } else {
                                        awardUpdateListener.updateScore();//成功后更新 point
                                        MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                showDialog(prize_style, exchange_code, message);
                                            }
                                        }, 500);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 点击抽奖、网络请求返回中奖结果
     *
     * @param isSuccess
     * @param info
     * @param isTimeOut
     */
    public void finish(boolean isSuccess, AwardDetailBean info, boolean isTimeOut) {
        isTryToStop = true;
        this.isTimeOut = isTimeOut;

        if (isSuccess) {
            stayIndex = Integer.parseInt(info.position) - 1;
            this.prize_style = Integer.parseInt(info.prize_style);
            this.awardId = "" + info.id;
            this.exchange_code = info.exchange_code;
            this.prize_alias = info.prize_alias;
            this.prize_words = info.prize_words;
            this.message = info.message;
        } else {
            int randomPosition = getRandomFailurePosition();
            stayIndex = randomPosition - 1;//决定不中奖的位置
            prize_style = 0;//决定弹不中奖的框
            LogUtils.e(LogTAG.CHOU, "抽奖失败==" + stayIndex);
            if (awardUpdateListener != null) {
                awardUpdateListener.updateScoreByManual(0);
            }
        }
    }

    /**
     * 请求网络，返回抽奖结果
     */
    private void executeAward() {
        final Map<String, String> params = new HashMap<>();
        params.put("activity_id", awardInfoBean.activity_id);
        Net.instance().executePost(Host.HostType.GCENTER_HOST, Uri2.AWARD_START, params, new WebCallBackToBean<AwardDetailBean>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.e(LogTAG.CHOU, "抽奖失败==" + statusCode + error);
                if (statusCode == ErrorFlag.userLogout) {
                    UserInfoManager.instance().userLogout(false);
                    showToast("登录过期，请重新登录");
                } else if (statusCode == ErrorFlag.awardTimeOut) {
                    finish(false, null, true);
                    return;
                } else if (statusCode == ErrorFlag.netError || statusCode == ErrorFlag.netTimeout) {
                    showToast("网络异常");
                }
                MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish(false, null, false);
                    }
                }, 1000); //转久点
            }

            @Override
            protected void handle(final AwardDetailBean info) {
                LogUtils.e(LogTAG.CHOU, "抽奖成功==" + info);
                wasteScore = info.waste_point_number;
                mAwardDetailBean = info;
                updateFreeNum(info.free_lottery_number);
                MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish(true, info, false);
                    }
                }, 1000); //转久点
            }
        });
    }

    /**
     * 领奖
     *
     * @param awardId id 中奖纪录 ID
     * @param dialog
     * @param view
     */
    public void getAwardFromNet(String awardId, final PhysicalEditDialog dialog, final View view) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", dialog.getEditName());
        params.put("phone", dialog.getEditPhone());
        params.put("address", dialog.getEditAddress());
        Net.instance().executePost(Host.HostType.GCENTER_HOST, Uri2.AWARD_GET + awardId, params, new WebCallBackToString() {
            @Override
            public void onSuccess(String result) {
                LogUtils.i(LogTAG.CHOU, "领奖成功：" + result);
                view.setClickable(true);
                ((Button) view).setText("提交");
                ToastUtils.showToast(context, "领奖成功");
                saveData(dialog.getEditAddress(), dialog.getEditName(), dialog.getEditPhone());
                dialog.hide();
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.i(LogTAG.CHOU, "领奖失败：" + statusCode + "====" + error);
                view.setClickable(true);
                ((Button) view).setText("提交");
                if (statusCode == ErrorFlag.netError || statusCode == ErrorFlag.netTimeout) {
                    ToastUtils.showToast(context, "网络异常");
                } else {
                    ToastUtils.showToast(context, error.getMessage());
                }
            }
        });
    }

    /**
     * 存本地
     *
     * @param editAddress
     * @param editName
     * @param editPhone
     */
    private void saveData(String editAddress, String editName, String editPhone) {
        mSp.add(SharedPreferencesUtil.AWARD_EDIT_ADDRESS, editAddress);
        mSp.add(SharedPreferencesUtil.AWARD_EDIT_NAME, editName);
        mSp.add(SharedPreferencesUtil.AWARD_EDIT_PHONE, editPhone);
        LogUtils.i(LogTAG.CHOU, "领奖信息保存成功：" + editAddress + editName + editPhone);
    }

    /**
     * 抽奖成功后弹框
     *
     * @param prize_style
     * @param exchange_code
     * @param message
     */
    private void showDialog(int prize_style, String exchange_code, String message) {
        switch (prize_style) {
            case 0: //未中奖 0
                showNormalDialog(getStringSource(R.string.award_title_tips), getStringSource(R.string.award_message_tips), getStringSource(R.string.award_button_continue));
                break;
            case 1:   //实物 1
                showPhysicalDialog(getStringSource(R.string.award_title_tips), prize_words, getStringSource(R.string.award_button_click_get), false, exchange_code, prize_style, message);
                break;
            case 2:   //  券 2
                showPhysicalDialog(getStringSource(R.string.award_title_success), prize_words, getStringSource(R.string.award_button_continue), true, exchange_code, prize_style, message);
                break;
            case 3:   //流量 3
                showPhysicalDialog(getStringSource(R.string.award_title_tips), prize_words, getStringSource(R.string.award_button_click_get), false, exchange_code, prize_style, message);
                break;
            case 4:   //积分 4
                showPhysicalDialog(getStringSource(R.string.award_title_success), prize_words, getStringSource(R.string.award_button_continue), false, exchange_code, prize_style, message);
                break;
            case 5:   //礼包 5
                showPhysicalDialog(getStringSource(R.string.award_title_success), prize_words, getStringSource(R.string.award_button_continue), true, exchange_code, prize_style, message);
                break;
            case 6:   //代金券 6
                showPhysicalDialog(getStringSource(R.string.award_title_success), prize_words, getStringSource(R.string.award_button_continue), true, AWARDCOUPON, prize_style, message);
                break;
            default:
        }
    }

    /**
     * 随机 获取网络奖盘数据中不中奖的position
     *
     * @return
     */
    private int getRandomFailurePosition() {
        Collections.sort(failurePositions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return Math.random() > 0.5 ? 1 : -1;
            }
        });
        return failurePositions.get(0);
    }

    /**
     * 点击前检查网络是否连接
     *
     * @return
     */
    private boolean checkNet() {
        if (!NetUtils.isConnected(context)) {
            final MessageDialog messageDialog = new MessageDialog(context, context.getResources().getString(R.string.gentle_reminder), context.getResources().getString(R.string.download_no_network), context.getResources().getString(R.string.cancel_ignor_bt), context.getResources().getString(R.string.go_setting));
            messageDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {
                @Override
                public void OnClick(View view) {
                    //跳转到系统设置
                    context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            });
            messageDialog.show();
            return true;
        }
        return false;
    }

    /**
     * 根据资源id获取String
     *
     * @param srcString
     * @return
     */
    private String getStringSource(int srcString) {
        return context.getResources().getString(srcString);
    }

    /**
     * 根据资源id获取String 带占位符
     *
     * @param srcString
     * @return
     */
    private String getStringSource(int srcString, int num) {
        return context.getResources().getString(srcString, num);
    }

    /**
     * 没抽中，或积分不足，或有奖品领取
     *
     * @param title
     * @param message
     * @param button
     */
    public void showNormalDialog(String title, String message, final String button) {
        NormalDialog dialog = new NormalDialog(context, title, message, button);
        dialog.setRightOnClickListener(new NormalDialog.RightBtnClickListener() {
            @Override
            public void OnClick(View view) {
                if (button.equals(getStringSource(R.string.award_button_earn_score)) || button.equals(getStringSource(R.string.award_button_earn_score_now))) {
                    LogUtils.i(LogTAG.CHOU, "跳转到积分页面");
                    awardUpdateListener.jumpToScoreFragment();
                } else if (button.equals(getStringSource(R.string.award_button_get))) {
                    LogUtils.i(LogTAG.CHOU, "跳转到中奖纪录页面");
                    ActivityActionUtils.JumpToAwardRecordActivity(context);
                } else if (button.equals(getStringSource(R.string.award_button_refresh))) {
                    if (awardUpdateListener != null) {
                        awardUpdateListener.updateTimes(true);//刷新活动
                        LogUtils.e(LogTAG.CHOU, "活动过期，刷新页面");
                    }
                }
            }
        });
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                interrupt = false;
                LogUtils.i(LogTAG.CHOU, "设置抽奖结束：");
            }
        });

    }

    /**
     * 无需领奖，直接后台发放到账号
     *
     * @param title
     * @param message
     * @param button
     * @param prize_style
     */
    public void showPhysicalDialog(String title, String message, String button, boolean isTicket, String mChangeCode, final int prize_style, String realTitle) {
        PhysicalAwardDialog dialog = new PhysicalAwardDialog(context, title, message, button, isTicket, mChangeCode, realTitle);
        dialog.setRightOnClickListener(new PhysicalAwardDialog.RightBtnClickListener() {
            @Override
            public void OnClick(View view) {
                if (prize_style == 1 || prize_style == 3) {
                    showPhysicalEditDialog(awardId, prize_style == 1);
                    LogUtils.i(LogTAG.CHOU, "流量、实物领取");
                }
            }
        });
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                interrupt = false;
                LogUtils.i(LogTAG.CHOU, "设置抽奖结束：");
            }
        });
    }

    /**
     * 实物，领奖弹框
     */
    public void showPhysicalEditDialog(final String id, boolean isPhysical) {
        final PhysicalEditDialog dialog = new PhysicalEditDialog(context, isPhysical);
        dialog.setRightOnClickListener(new PhysicalEditDialog.RightBtnClickListener() {
            @Override
            public void OnClick(View view) {
                view.setClickable(false);
                ((Button) view).setText("提交中");
                getAwardFromNet(id, dialog, view);
                LogUtils.i(LogTAG.CHOU, "实物领奖框：点击提交了");
            }
        });
        dialog.show();
    }


    public void setUpdateListener(AwardUpdateListener awardUpdateListener) {
        this.awardUpdateListener = awardUpdateListener;
    }

    @Override
    public void userLogin(UserBaseInfo userBaseInfo) {
        awardUpdateListener.updateTimes(false);
    }

    @Override
    public void updateUserInfo(UserBaseInfo userBaseInfo) {

    }

    @Override
    public void userLogout() {

    }

    private void showToast(String error) {
        ToastUtils.showToast(context, error);
    }
}
