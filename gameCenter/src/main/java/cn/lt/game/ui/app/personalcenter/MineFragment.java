package cn.lt.game.ui.app.personalcenter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.application.GlobalParams;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.bean.BaseBean;
import cn.lt.game.bean.SignPointsNet;
import cn.lt.game.event.RedPointEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.netdata.ErrorFlag;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.StatusBarUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.util.redpoints.RedPointsManager;
import cn.lt.game.lib.web.WebCallBackToBean;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.app.ExitManager;
import cn.lt.game.ui.app.HomeActivity;
import cn.lt.game.ui.app.awardgame.AwardActivity;
import cn.lt.game.ui.app.awardpoints.AwardPointsRecordActivity;
import cn.lt.game.ui.app.voucher.CouponRecordActivity;
import cn.lt.game.ui.app.management.ManagementActivity;
import cn.lt.game.ui.app.personalcenter.appsetting.AppSettingActivity;
import cn.lt.game.ui.app.personalcenter.info.EditUserInfoActivity;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;
import cn.lt.game.ui.app.sidebar.AboutActivity;
import cn.lt.game.ui.app.sidebar.LoadingDialog;
import cn.lt.game.ui.app.sidebar.UpdateInfo;
import cn.lt.game.ui.app.sidebar.UpdateInfoDialog;
import cn.lt.game.ui.app.sidebar.feedback.FeedBackActivity;
import cn.lt.game.update.VersionCheckManger;
import de.greenrobot.event.EventBus;

/**
 * @author chengyong
 * @time 2017/10/27 10:50
 * @des ${tab我}
 */

public class MineFragment extends BaseFragment implements View.OnClickListener, UserInfoUpdateListening {

    private FragmentActivity mContext;
    private View mRoot;
    private static final int NEED_CALLBACK = 100;

    private TextView mLoginPrompt;


    private TextView tvUserName;
    private ImageView ivAvatar;

    private UpdateInfoDialog updateDialog;
    private TextView tvNewVer;
    private TextView tvNotNewVer;
    private TextView tvSign;
    private TextView continuousSign;
    private TextView feedbackCount;
    private TextView awardCount;
    private TextView newTask;
    private TextView myCouponCount;

    private VersionCheckManger.VersionCheckCallback callback;
    private LoadingDialog loadingDialog;

    //下載管理和遊戲升級的小紅點控制
    private TextView myDownloadRed, myUpdateRed;
    private int couponCounts;

    @Override
    public void setPageAlias() {
        if (UserInfoManager.instance().isLogin()) {
            setmPageAlias(Constant.PAGE_PERSONAL_HAS_LOGIN);
        } else {
            setmPageAlias(Constant.PAGE_PERSONAL_UNLOGIN);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.activity_user_center, container, false);
        initView();
        EventBus.getDefault().register(this);
        return mRoot;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        View statusBar = mRoot.findViewById(R.id.status_bar);
        StatusBarUtils.showSelfStatusBar(mContext, statusBar);

        tvUserName = (TextView) mRoot.findViewById(R.id.user_center_user_name);
        ivAvatar = (ImageView) mRoot.findViewById(R.id.user_center_avatar);
        myDownloadRed = (TextView) mRoot.findViewById(R.id.user_center_new_download);
        myUpdateRed = (TextView) mRoot.findViewById(R.id.user_center_new_update);

        RelativeLayout btnSetting = (RelativeLayout) mRoot.findViewById(R.id.user_center_setting);
        RelativeLayout btnCheckUpdate = (RelativeLayout) mRoot.findViewById(R.id.user_center_check_update);
        RelativeLayout btnFeedback = (RelativeLayout) mRoot.findViewById(R.id.user_center_feedback);
        RelativeLayout btnAbout = (RelativeLayout) mRoot.findViewById(R.id.user_center_about);
        RelativeLayout btnExitApp = (RelativeLayout) mRoot.findViewById(R.id.user_center_exitapp);
        RelativeLayout rlMyAward = (RelativeLayout) mRoot.findViewById(R.id.user_center_myaward);
        RelativeLayout rlMyTask = (RelativeLayout) mRoot.findViewById(R.id.user_center_mytask);
        RelativeLayout rlMyCoupon = (RelativeLayout) mRoot.findViewById(R.id.user_center_mycoupon);

        RelativeLayout rlManager = (RelativeLayout) mRoot.findViewById(R.id.user_center_downloadManager);
        RelativeLayout rlUpgrade = (RelativeLayout) mRoot.findViewById(R.id.user_center_upgrade);
        mLoginPrompt = (TextView) mRoot.findViewById(R.id.user_center_login_prompt);

//        LinearLayout btnGameModule = (LinearLayout) mRoot.findViewById(R.id.user_center_game_module);
//        LinearLayout btnCommunityModule = (LinearLayout) mRoot.findViewById(R.id.user_center_community_module);
//        LinearLayout btnGiftModule = (LinearLayout) mRoot.findViewById(R.id.user_center_gift_module);

        tvNewVer = (TextView) mRoot.findViewById(R.id.user_center_new_ver);
        tvNotNewVer = (TextView) mRoot.findViewById(R.id.user_center_not_new_ver);
        tvSign = (TextView) mRoot.findViewById(R.id.user_center_sign);
        continuousSign = (TextView) mRoot.findViewById(R.id.user_center_sign_count);
        feedbackCount = (TextView) mRoot.findViewById(R.id.tv_user_center_new_feedback);
        awardCount = (TextView) mRoot.findViewById(R.id.tv_award_count);
        newTask = (TextView) mRoot.findViewById(R.id.user_center_new_task);
        myCouponCount = (TextView) mRoot.findViewById(R.id.user_center_coupon_count);
        tvSign.setOnClickListener(this);

        String version = UpdateInfo.getVersion();
        if (TextUtils.isEmpty(version)) {
            tvNewVer.setVisibility(View.GONE);
            tvNotNewVer.setVisibility(View.VISIBLE);
        } else {
            tvNewVer.setVisibility(View.VISIBLE);
            tvNotNewVer.setVisibility(View.GONE);
        }

//        btnGameModule.setOnClickListener(this);
//        btnCommunityModule.setOnClickListener(this);
//        btnGiftModule.setOnClickListener(this);

        btnSetting.setOnClickListener(this);
        btnCheckUpdate.setOnClickListener(this);
        btnFeedback.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
        btnExitApp.setOnClickListener(this);
        rlMyAward.setOnClickListener(this);
        rlMyTask.setOnClickListener(this);
        rlManager.setOnClickListener(this);
        rlUpgrade.setOnClickListener(this);
        rlMyCoupon.setOnClickListener(this);
        mRoot.findViewById(R.id.user_center_user_info).setOnClickListener(this);

        UserInfoManager.instance().addRealListening(this);
        showFeedbackRed(RedPointsManager.getInstance().redPointsBean.getFeedbackNum());

        if (UserInfoManager.instance().isLogin()) {
            tvUserName.setText(UserInfoManager.instance().getUserInfo().getNickname());
            ImageloaderUtil.loadUserHead(mContext, UserInfoManager.instance().getUserInfo().getAvatar(), ivAvatar);
            setLoginUIVisible();
            setLastSignUi();
            getUserDataNet();
        } else {
            updateUIForLogout();
        }
        refreshDownloadAndUpdateRedPoint();
    }

    private void refreshDownloadAndUpdateRedPoint() {
        if (myDownloadRed != null) {
            myDownloadRed.setVisibility(MyApplication.castFrom(mContext).getNewGameDownload() ? View.VISIBLE : View.GONE);
            myUpdateRed.setVisibility(MyApplication.castFrom(mContext).getNewGameUpdate() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UserInfoManager.instance().isLogin()) {
            setmPageAlias(Constant.PAGE_PERSONAL_HAS_LOGIN);
            getCouponCounts();
        } else {
            setmPageAlias(Constant.PAGE_PERSONAL_UNLOGIN);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_page_back:
                mContext.finish();
                break;
            case R.id.user_center_setting:
                startActivity(new Intent(getContext(), AppSettingActivity.class));
                break;
            case R.id.user_center_check_update:
                checkVersion();
                break;
            case R.id.user_center_feedback:
                feedbackCount.setVisibility(View.INVISIBLE);
                startActivity(new Intent(getContext(),
                        FeedBackActivity.class));
                RedPointsManager.getInstance().redPointsBean.setFeedbackNum(0);
                refreshTabMineRedForHomeActivity();
                break;
            case R.id.user_center_about:
                startActivity(new Intent(getContext(), AboutActivity.class));
                break;
            case R.id.user_center_user_info:
                if (!UserInfoManager.instance().isLogin()) {
                    Intent intent = UserInfoManager.instance().getLoginIntent(mContext, true);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(getContext(), EditUserInfoActivity.class));
                }
                break;
            case R.id.user_center_exitapp:
                ExitManager.exit(mContext);
                break;
            case R.id.user_center_mycoupon:
                if (!UserInfoManager.instance().isLogin()) {
                    startActivity(UserInfoManager.instance().getLoginIntent(mContext, true));
                } else {
                    startActivityForResult(new Intent(mContext, CouponRecordActivity.class), NEED_CALLBACK);
                }
                break;
            case R.id.user_center_myaward:
                if (!UserInfoManager.instance().isLogin()) {
                    startActivity(UserInfoManager.instance().getLoginIntent(mContext, true));
                } else {
                    startActivityForResult(new Intent(mContext, AwardPointsRecordActivity.class), NEED_CALLBACK);
                }
                break;
            case R.id.user_center_mytask:
                Intent taskIntent = new Intent(mContext, AwardActivity.class);
//                taskIntent.putExtra(AwardActivity.JUMP_TYPE, AwardActivity.TO_SCORE);
                startActivityForResult(taskIntent, NEED_CALLBACK);
                newTask.setVisibility(View.INVISIBLE);
                MyApplication.application.myTaskFlag = false;
                RedPointsManager.getInstance().redPointsBean.setMyTask(false);
                refreshTabMineRedForHomeActivity();
                break;
            case R.id.user_center_sign:
                if (!UserInfoManager.instance().isLogin()) {
                    Intent intent = UserInfoManager.instance().getLoginIntent(mContext, true);
                    startActivity(intent);
                } else {
                    signNet();
                }
                break;
            case R.id.user_center_downloadManager:
                MyApplication.castFrom(mContext).setNewGameDownload(false);
                refreshTabMineRedForHomeActivity();
                ActivityActionUtils.JumpToManager(mContext, ManagementActivity.POSITION_DOWNLOAD_MANAGEMENT);
                refreshDownloadAndUpdateRedPoint();
                break;
            case R.id.user_center_upgrade:
                MyApplication.castFrom(mContext).setNewGameUpdate(false);
                refreshTabMineRedForHomeActivity();
                ActivityActionUtils.JumpToManager(mContext, ManagementActivity.POSITION_UPGRADE);
                refreshDownloadAndUpdateRedPoint();
                break;
            default:
                break;
        }

    }

    /**
     * 签到
     */
    private void signNet() {
        Map<String, String> params = new HashMap<>();
        params.put("point_style", String.valueOf(Constant.POINTS_SIGN));
        Net.instance().executePost(Host.HostType.GCENTER_HOST, Uri2.SIGN_POINTS, params, new WebCallBackToBean<BaseBean<SignPointsNet>>() {

            @Override
            public void handle(BaseBean<SignPointsNet> baseBean) {
                LogUtils.d(LogTAG.HTAG, baseBean.toString());
                SignPointsNet info = baseBean.getData();
                if (info == null) {
                    return;
                }
                ToastUtils.showToast(mContext, "签到成功，+" + info.getPoints() + "积分");
                continuousSign.setText(Html.fromHtml("<font color='#ffffff'>已连续签到 </font>" + "<font color='#f3d113'>" + String.valueOf(info.getContinuous()) + "</font>" + "<font color='#ffffff'> 天</font>"));
                tvSign.setText(getResources().getString(R.string.user_center_signed));
                tvSign.setTextColor(getResources().getColor(R.color.signed));
                tvSign.setBackgroundResource(R.drawable.btn_green_signed_selector);
                tvSign.setEnabled(false);
                mLoginPrompt.setText(Html.fromHtml("<font color='#bcda91'>我的积分：</font>" + "<font color='#f3d113'>" + String.valueOf(info.getTotal_point()) + "</font>"));
                setLastSignInfo(info);
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.d(LogTAG.HTAG, statusCode + "" + error.getMessage());
                if (statusCode == 0) {
                    tvSign.setEnabled(false);
                    tvSign.setBackgroundResource(R.drawable.btn_green_signed_selector);
                    tvSign.setTextColor(getResources().getColor(R.color.signed));
                    tvSign.setText(getResources().getString(R.string.user_center_signed));
                } else if (statusCode == ErrorFlag.userLogout) {
                    startActivity(UserInfoManager.instance().getLoginIntent(mContext, true));
                }

                ToastUtils.showToast(mContext, error.getMessage());

            }
        });
    }

    @Override
    public void userLogin(UserBaseInfo userBaseInfo) {
    }


    private void setLoginUIVisible() {
        continuousSign.setVisibility(View.VISIBLE);
    }


    @Override
    public void updateUserInfo(UserBaseInfo userBaseInfo) {
        setLoginUIVisible();
        tvUserName.setText(userBaseInfo.getNickname());
        ImageloaderUtil.loadUserHead(mContext, userBaseInfo.getAvatar(), ivAvatar);
        getUserDataNet();
        showFeedbackRed(RedPointsManager.getInstance().redPointsBean.getFeedbackNum());
    }

    private void getCouponCounts() {

        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.COUNPON_COUNTS, null, new WebCallBackToString() {


            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.d("gpp", "onFailure==" + statusCode + " ,error==" + error.getMessage());
            }

            @Override
            public void onSuccess(String result) {
                LogUtils.d("gpp", "CouponCounts==" + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    couponCounts = jsonObject.getInt("count");
                    String str = "" + couponCounts + " 张可用代金券";
                    if (couponCounts>99){
                        str = "99+ 张可用代金券";
                    }
//                    int index = str.indexOf("张");
                    Spannable msg = new SpannableString(str);
//                    msg.setSpan(new ForegroundColorSpan(Color.parseColor("#FF8800")), 0, index, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    msg.setSpan(new ForegroundColorSpan(Color.parseColor("#FF8800")), 0, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    myCouponCount.setText(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getUserDataNet() {
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.USER_CENTER_DATA, null, new WebCallBackToBean<SignPointsNet>() {

            @Override
            protected void handle(SignPointsNet info) {
                LogUtils.d(LogTAG.HTAG, info.toString());
                bindUserDataNet(info);
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                if (statusCode == ErrorFlag.userLogout) {
                    updateUIForLogout();
                }
                LogUtils.d(LogTAG.HTAG, statusCode + "");
            }
        });
    }

    private void bindUserDataNet(SignPointsNet info) {
        setLoginUIVisible();
        continuousSign.setText(Html.fromHtml("<font color='#ffffff'>已连续签到 </font>" + "<font color='#f3d113'>" + String.valueOf(info.getContinuous()) + "</font>" + "<font color='#ffffff'> 天</font>"));
        mLoginPrompt.setText(Html.fromHtml("<font color='#bcda91'>我的积分：</font>" + "<font color='#f3d113'>" + String.valueOf(info.getPoints()) + "</font>"));
        if (info.getUnaccepted() != 0) {
            awardCount.setVisibility(View.VISIBLE);
            awardCount.setText(String.format(getResources().getString(R.string.user_center_unaward_count),
                    info.getUnaccepted() <= 99 ? info.getUnaccepted() : "99+"));
        } else {
            awardCount.setVisibility(View.INVISIBLE);
        }
        tvSign.setText(info.is_signed() ? getResources().getString(R.string.user_center_signed)
                : getResources().getString(R.string.user_center_sign));
        tvSign.setBackgroundResource(info.is_signed() ? R.drawable.btn_green_signed_selector : R.drawable.btn_green_sign_selector);
        tvSign.setTextColor(info.is_signed() ? getResources().getColor(R.color.signed) : getResources().getColor(R.color.white));
        tvSign.setEnabled(!info.is_signed());
        //签到和这里数据不统一,需要重新设置统一缓存
        info.setTotal_point(info.getPoints());
        setLastSignInfo(info);
        getCouponCounts();
    }

    private void setLastSignInfo(SignPointsNet lastSign) {
        MyApplication.application.lastPoint = lastSign;
    }

    private void setLastSignUi() {
        SignPointsNet info = MyApplication.application.lastPoint;
        if (info == null) {
            info = new SignPointsNet();
        }
        tvSign.setText(info.is_signed() ? getResources().getString(R.string.user_center_signed)
                : getResources().getString(R.string.user_center_sign));
        tvSign.setBackgroundResource(info.is_signed() ? R.drawable.btn_green_signed_selector : R.drawable.btn_green_sign_selector);
        tvSign.setTextColor(info.is_signed() ? getResources().getColor(R.color.signed) : getResources().getColor(R.color.white));
        tvSign.setEnabled(!info.is_signed());
        continuousSign.setText(Html.fromHtml("<font color='#ffffff'>已连续签到 </font>" + "<font color='#f3d113'>" + info.getContinuous() + "</font>" + "<font color='#ffffff'> 天</font>"));
        mLoginPrompt.setText(Html.fromHtml("<font color='#bcda91'>我的积分：</font>" + "<font color='#f3d113'>" + info.getTotal_point() + "</font>"));
    }

    @Override
    public void userLogout() {
        updateUIForLogout();
        showFeedbackRed(RedPointsManager.getInstance().redPointsBean.getFeedbackNum());
    }

    private void updateUIForLogout() {
        ivAvatar.setImageDrawable(getResources().getDrawable(R.mipmap.user_center_avatar));

        tvUserName.setText(R.string.user_center_user_name);
//        tvUserName.requestLayout();

        continuousSign.setVisibility(View.INVISIBLE);

        mLoginPrompt.setText(getResources().getString(R.string.user_center_login_prompt));
//        mLoginPrompt.requestLayout();

        tvSign.setEnabled(true);
        tvSign.setText(getResources().getString(R.string.user_center_sign));
        tvSign.setBackgroundResource(R.drawable.btn_green_sign_selector);
        tvSign.setTextColor(getResources().getColor(R.color.white));
//        tvSign.setVisibility(View.VISIBLE);
        awardCount.setText("");
        myCouponCount.setText("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateDialog = null;
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
        if (callback != null) {
            callback = null;
        }
        UserInfoManager.instance().removeListening(this);
    }

    private void checkVersion() {
        if (!NetUtils.isConnected(mContext)) {
            ToastUtils.showToast(mContext, "请检查网络");
            return;
        }
        if (callback == null) {
            callback = new VersionCheckManger.VersionCheckCallback() {
                @Override
                public void callback(Result result) {
                    loadingDialog.dismiss();
                    switch (result) {
                        case have:
                            tvNewVer.setVisibility(View.VISIBLE);
                            tvNotNewVer.setVisibility(View.GONE);
                            updateDialog = new UpdateInfoDialog(mContext);
                            updateDialog.showDialog(false);
                            break;
                        case none:
                            tvNewVer.setVisibility(View.GONE);
                            tvNotNewVer.setVisibility(View.VISIBLE);
                            ToastUtils.showToast(mContext, "已经是最新版本了！");
                            break;
                        case fail:
                            ToastUtils.showToast(mContext, "请求失败，请稍后重试！");
                            break;
                    }
                    refreshTabMineRedForHomeActivity();
                    callback = null;
                }
            };
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(mContext, "检测中...");
            }
            loadingDialog.show();
            VersionCheckManger.getInstance().checkVerison(callback, VersionCheckManger.MODE_SELF);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEED_CALLBACK && UserInfoManager.instance().isLogin()) {
            getUserDataNet();
        }
    }


    public void showFeedbackRed(int unread) {
        if (unread > 0) {
            feedbackCount.setVisibility(View.VISIBLE);
            feedbackCount.setText(unread > 99 ? "99+" : String.valueOf(unread));
        } else {
            feedbackCount.setVisibility(View.INVISIBLE);
        }
    }

    public void showMyTask(boolean show) {
        newTask.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public void showPlatUpdate(boolean show) {
        if (show) {
            tvNewVer.setVisibility(View.VISIBLE);
            tvNotNewVer.setVisibility(View.GONE);
        } else {
            tvNewVer.setVisibility(View.GONE);
            tvNotNewVer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 除了下载和升级其他红点ui刷新
     */
    public void refreshMineFragmentSubRed() {
        if (feedbackCount == null) {
            return;
        }
        showFeedbackRed(RedPointsManager.getInstance().redPointsBean.getFeedbackNum());
        showMyTask(RedPointsManager.getInstance().redPointsBean.isMyTask());
        showPlatUpdate(RedPointsManager.getInstance().redPointsBean.isPlatUpdate());

    }

    /**
     * 刷新底部menu 我的小红点
     * 和我的小红点相关item点击都要再次刷新我的红点状态
     */
    public void refreshTabMineRedForHomeActivity() {
        if (mContext != null) {
            ((HomeActivity) mContext).showMineFootRedPoints(RedPointsManager.getInstance().isShowMineRed(mContext));
        }

    }

    /**
     * 管理,升级item红点接受通知
     */
    public void onEventMainThread(RedPointEvent event) {
        refreshDownloadAndUpdateRedPoint();
    }


}
