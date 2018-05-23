package cn.lt.game.ui.app.gamegift;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.db.operation.FavoriteDbOperator;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.AppIsInstalledUtil;
import cn.lt.game.lib.util.ClipBoardManagerUtil;
import cn.lt.game.lib.util.OpenAppUtil;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.DownLoadBarForGift;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.app.gamegift.GiftManger.GetGiftResponseListener;
import cn.lt.game.ui.app.gamegift.view.GiftInfoDetailView;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

@SuppressLint({"SimpleDateFormat", "ResourceAsColor"})
public class GiftDetailActivity extends BaseActivity implements RetryCallBack, OnClickListener, UserInfoUpdateListening {

    public static final String GIFT_ID = "gift_id";

    private NetWorkStateView mNetWorkStateView;

    private String TAG = "GiftDetailActivity";

    private String mGiftID = "";

    private GiftInfoDetailView mGiftMsgView;

    private boolean isRecived;

    private RelativeLayout mBottomBarRoot;

    private GiftDomainDetail mGiftInfo;

    private TextView mKeyCodeTV;

    private Button mKeyCodeCopyBT;

    private TextView mGiftContentTV;

    private TextView mGiftGetProcessTV;

    private TextView mGiftGetColsedDataTV;

    private View mKeyCodeRoot;

    private View mGiftDetails;

    private Button mGetGiftBT;

    private int mResetGiftCount;

    @SuppressWarnings("unused")
    private boolean isGetingGift;

    private DownLoadBarForGift mDownloadBar;

    private RelativeLayout mGetGiftButtonRoot;
    private GiftState mGiftState;

    /** 礼包是过期的*/
    private boolean isOverdue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_gift_detail);
        UserInfoManager.instance().addListening(this);
        getIntentData();
        initView();
        checkNetWork();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDownloadBar != null) {
            // 更新安装按键状态
            mDownloadBar.updateProgressState();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDownloadBar != null) {
            mDownloadBar = null;
        }
        UserInfoManager.instance().removeListening(this);
    }

    private void getIntentData() {
        mGiftID = getIntent().getStringExtra(GIFT_ID);
    }

    private void initView() {
        initKeyValueView();
        initDetailView();
        mGetGiftButtonRoot = (RelativeLayout) findViewById(R.id.gift_detail_install_operation_bar);
        mDownloadBar = (DownLoadBarForGift) findViewById(R.id.gift_downlaodBar);
        mGetGiftBT = (Button) findViewById(R.id.bt_get_gift);
        mBottomBarRoot = (RelativeLayout) findViewById(R.id.gift_detail_bottom);
        mNetWorkStateView = (NetWorkStateView) findViewById(R.id.rank_netwrolStateView);
        mNetWorkStateView.setRetryCallBack(this);
        mGiftMsgView = (GiftInfoDetailView) findViewById(R.id.gift_detail_title);
    }

    private void initKeyValueView() {
        mKeyCodeRoot = findViewById(R.id.key_layout);
        mKeyCodeCopyBT = (Button) findViewById(R.id.bt_copy);
        mKeyCodeTV = (TextView) findViewById(R.id.tv_gift_key_value);
    }

    private void initDetailView() {
        mGiftDetails = findViewById(R.id.gift_detail_value);
        mGiftContentTV = (TextView) findViewById(R.id.gift_value_giftValue);
        mGiftGetProcessTV = (TextView) findViewById(R.id.gift_value_giftProcedures);
        mGiftGetColsedDataTV = (TextView) findViewById(R.id.gift_value_time);
    }

    private void inflateKeyValueView() {
        mKeyCodeRoot.setVisibility(View.VISIBLE);
        mKeyCodeTV.setText(mGiftInfo.getCode().toString().trim());
        if (isOverdue) {
            mKeyCodeCopyBT.setClickable(false);
            mKeyCodeCopyBT.setBackgroundResource(R.drawable.btn_install_selector);
            mKeyCodeCopyBT.setText("过期");
            mKeyCodeCopyBT.setTextColor(getResources().getColor(R.color.white));
        } else {
            mKeyCodeCopyBT.setClickable(true);
            mKeyCodeCopyBT.setBackgroundResource(R.drawable.btn_green_selector);
            mKeyCodeCopyBT.setText("复制");
            mKeyCodeCopyBT.setTextColor(getResources().getColor(R.color.theme_green));
            mKeyCodeCopyBT.setOnClickListener(this);
        }
    }

    private void hideKeyValueView() {
        mKeyCodeRoot.setVisibility(View.GONE);
    }

    private void inflateDetailView() {
        mGiftDetails.setVisibility(View.VISIBLE);
        mGiftContentTV.setText(mGiftInfo.getContent());
        mGiftGetProcessTV.setText(mGiftInfo.getUsage());
        mGiftGetColsedDataTV.setText(mGiftInfo.getEndTime());
    }

    private void fillBottomBar() {
        mGetGiftBT.setOnClickListener(this);
        if (isOverdue) {
            mGetGiftBT.setText("已过期");
            mGetGiftBT.setClickable(false);
        } else if (isRecived) {
            mGetGiftButtonRoot.setVisibility(View.GONE);
            mDownloadBar.setVisibility(View.VISIBLE);
            initDownloadBar();
            mGiftState = GiftState.Recived;
        } else if (mResetGiftCount <= 0) {
            mDownloadBar.setVisibility(View.GONE);
            mGetGiftBT.setText("领完了");
            mGetGiftBT.setBackgroundResource(R.drawable.get_gift_button_received);
            mGiftState = GiftState.NoGift;
        } else {
            mGetGiftBT.setText("领取礼包");
            mGiftState = GiftState.UnRecived;
        }
    }

    private void fillGameInfo() {
        mGiftMsgView.setVisibility(View.VISIBLE);
        mGiftMsgView.fillView(mGiftInfo);
    }

    private void fillGiftInfo() {
        if (isOverdue || isRecived) {
            inflateKeyValueView();
        } else {
            hideKeyValueView();
        }
        inflateDetailView();
    }

    /**
     * 初始化网络错误提示界面；
     */
    private void inflateNetWorkErrView() {
        mNetWorkStateView.setVisibility(View.VISIBLE);
        mNetWorkStateView.showNetworkFailLayout();
        mBottomBarRoot.setVisibility(View.GONE);
    }

    /**
     * 检查网络，如果有网路则请求网络数据，无网络显示无网络界面；
     */
    private void checkNetWork() {
        if (NetUtils.isConnected(this)) {
            mNetWorkStateView.showLoadingBar();
            mBottomBarRoot.setVisibility(View.VISIBLE);
            requestData();
        } else {
            inflateNetWorkErrView();
        }
    }

    @SuppressWarnings("unused")
    private void share() {
        /* 分享功能 */
        if (mGiftInfo != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, "选择要分享的应用");
            intent.putExtra(Intent.EXTRA_TEXT, "我在游戏中心发现了一款非常好玩的游戏-" + mGiftInfo.getTitle() + "。一起来玩吧！赶快来下载哦：");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(Intent.createChooser(intent, "选择要分享的应用"));
        }
    }

    @Override
    public void retry() {
        checkNetWork();
    }

    public void copy() {
        ClipBoardManagerUtil.self().save2ClipBoard(mKeyCodeTV.getText().toString().trim());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bt_copy) {
            String code = mKeyCodeTV.getText().toString().trim();
            if (!TextUtils.isEmpty(code)) {
                copy();
            }
        } else if (id == R.id.bt_get_gift) {
            switch (mGiftState) {
                case NoGift:
                    ToastUtils.showToast(this, "没有礼包了哦！");
                    break;
                case Recived:
                    if (AppIsInstalledUtil.isInstalled(this, mGiftInfo.getGame().getPkgName())) {
                        OpenAppUtil.openApp(mGiftInfo, this);
                    } else {
                        ToastUtils.showToast(this, "还没下载游戏，赶紧去下载吧！");
                    }
                    break;
                case Uninstall:
                    // 打开游戏；
                    // Utils.gameDown(this,
                    // GiftManger.giftGame2GameDetail(mGame, this));
                    break;
                case UnRecived:
                    // 领取礼包；
                    if (!isGetingGift) {
                        isGetingGift = true;
                        GiftManger manger = new GiftManger(this, mGiftInfo);
                        manger.setGetGiftResponeseListener(new GetGiftResponseListener() {

                            @Override
                            public void onSuccess() {
                                isGetingGift = false;
                                checkNetWork();
                            }

                            @Override
                            public void onFailure(GiftDomainDetail gift) {
                                //							mResetGiftCount = gift.getRemian();
                                mResetGiftCount = mGiftInfo.getRemain();
                                fillBottomBar();
                                isGetingGift = false;
                            }
                        });
                        manger.getGift(getPageAlias());
                    }
                    break;
            }
        }
    }

    private void initDownloadBar() {
        if (mDownloadBar != null) {
            mDownloadBar.initDownLoadBar(mGiftInfo, FavoriteDbOperator.GAMEDETAIL_TABLE_NAME, getPageAlias());
        }
    }

    @Override
    public void userLogin(UserBaseInfo userBaseInfo) {
        if (!isFinishing()) {
            checkNetWork();
        }
    }

    @Override
    public void updateUserInfo(UserBaseInfo userBaseInfo) {

    }

    @Override
    public void userLogout() {
        if (!isFinishing()) {
            checkNetWork();
        }
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_GIFT_DETAIL,mGiftID);

    }

    private void fillLayout() {
        isRecived = mGiftInfo.isReceived();
        mResetGiftCount = mGiftInfo.getRemain();

        // 判断礼包是否过期
        String endTime = mGiftInfo.getEndTime();
        try {
            long avalidateData = TimeUtils.string2Long(endTime.contains(":") ? endTime : endTime + " 23:59:59", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
            isOverdue = System.currentTimeMillis() > avalidateData;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        fillGameInfo();
        fillGiftInfo();
        fillBottomBar();
    }

    /**
     * 请求网络
     */
    private void requestData() {
        Map<String, String> params = new HashMap<>();
        params.put("id", mGiftID);
        mNetWorkStateView.showLoadingBar();

        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.getGiftDetailUri(mGiftID), params, new WebCallBackToObject<UIModuleList>() {

            /**
             * 网络请求出错时调用
             *
             * @param statusCode 异常编号
             * @param error      异常信息
             */
            @Override
            public void onFailure(int statusCode, Throwable error) {
                inflateNetWorkErrView();
            }

            @Override
            protected void handle(UIModuleList list) {
                mNetWorkStateView.hideNetworkView();
                UIModule module = (UIModule) list.get(0);
                mGiftInfo = (GiftDomainDetail) module.getData();
                fillLayout();
            }
        });
    }


    private enum GiftState {
        Recived, Uninstall, NoGift, UnRecived
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            //需要重新检查该packageName是否安装了
//            LogUtils.d("ccc", "详情Activity中取消了==请求码" + requestCode);
//            GameBaseDetail gameBaseDetail = MyApplication.application.normalInstallTaskLooper.get(requestCode);
            //移除轮询器中的监控任务
//            InstalledEventLooper.getInstance().setTaskRunning(gameBaseDetail.task);  //停止轮询任务
        }
    }
}
