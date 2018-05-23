package cn.lt.game.ui.app.sidebar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.PreferencesUtils;
import cn.lt.game.lib.util.ActivityManager;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.StorageSpaceDetection;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.notification.LTNotificationManager;
import cn.lt.game.update.PlatUpdateAction;
import cn.lt.game.update.PlatUpdateManager;
import cn.lt.game.update.PlatUpdateMode;
import cn.lt.game.update.PlatUpdateService;

/**
 * 平台更新提示框
 *
 * @author Administrator
 */
public class UpdateInfoDialog extends Dialog implements View.OnClickListener {

    private TextView mVersionTv;

    private TextView mUpdateDateTv;

    private TextView mInfoTv;

    private String mVersionStr = "";

    private String mUpdateDateStr = "";

    private String mInfoStr = "";


    private Button mConfirmBtn;

    private ImageView mCloseIv;

    private RelativeLayout mRootLayout;

    private MyApplication mApplication;

    private Context mContext;

    private int mScreenWidth;

    private int mScreenHeight;

    private boolean isForce;// 强制更新

    private View mVoidFlux;
    private LinearLayout llForce;
    private TextView tvForceTip;
    private Button messageDialog_leftBtn, messageDialog_rightBtn;

    public UpdateInfoDialog(Context context) {
        super(context, R.style.updateInfoDialogStyle);
        this.mContext = context;
        init();
    }

    private void init() {
        mApplication = (MyApplication) mContext.getApplicationContext();
        WindowManager windowManger = (WindowManager) mApplication.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        windowManger.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels; // 当前分辨率 宽度
        mScreenHeight = dm.heightPixels;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isForce) {
                ActivityManager.self().exitAppWithoutShutdown();
            }
            UpdateInfoDialog.this.cancel();
        }
        return super.onKeyDown(keyCode, event);
    }

    /* 设置是否强制更新 */
    public void setForce(boolean isforce) {
        this.isForce = isforce;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_updateinfo);
        init_findViewById();
        initView();
        updateView();
    }

    // 绑定控件
    private void init_findViewById() {
        mVersionTv = (TextView) findViewById(R.id.updateDialog_version);
        mUpdateDateTv = (TextView) findViewById(R.id.updateDialog_updateDate);
        mInfoTv = (TextView) findViewById(R.id.updateDialog_info);
        mConfirmBtn = (Button) findViewById(R.id.updateDialog_confirm);
        mRootLayout = (RelativeLayout) findViewById(R.id.updateDialog_root);
        mCloseIv = (ImageView) findViewById(R.id.updateDialog_closeIv);
        mVoidFlux = findViewById(R.id.iv_void_flux);
        llForce = (LinearLayout) findViewById(R.id.ll_force);
        tvForceTip = (TextView) findViewById(R.id.tv_force_tip);
        messageDialog_leftBtn = (Button) findViewById(R.id.messageDialog_leftBtn);
        messageDialog_rightBtn = (Button) findViewById(R.id.messageDialog_rightBtn);
    }

    //  初始化界面
    private void initView() {
        if (PlatUpdateManager.isDowloaded(mContext)) {
            mVoidFlux.setVisibility(View.VISIBLE);
            mConfirmBtn.setText("马上安装");
            messageDialog_rightBtn.setText("安装");
        } else {
            mVoidFlux.setVisibility(View.INVISIBLE);
            mConfirmBtn.setText("马上升级");
            messageDialog_rightBtn.setText("升级");
        }
        if (isForce) {
            llForce.setVisibility(View.VISIBLE);
            tvForceTip.setVisibility(View.VISIBLE);
            mConfirmBtn.setVisibility(View.GONE);
        } else {
            llForce.setVisibility(View.GONE);
            tvForceTip.setVisibility(View.GONE);
            mConfirmBtn.setVisibility(View.VISIBLE);
        }
        mRootLayout.getLayoutParams().width = (int) (mScreenWidth * 0.9);

        mInfoTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        mConfirmBtn.setOnClickListener(this);
        mCloseIv.setOnClickListener(this);
        messageDialog_leftBtn.setOnClickListener(this);
        messageDialog_rightBtn.setOnClickListener(this);
        this.setCanceledOnTouchOutside(false);
    }

    // 初始化数据
    private void initData() {
        mVersionStr = UpdateInfo.getVersion();
        mUpdateDateStr = UpdateInfo.getCreated_at();
        mInfoStr = UpdateInfo.getFeature();
    }

    // 更新界面
    private void updateView() {
        // TODO Auto-generated method stub
        if (mVersionStr != null) {
            mVersionTv.setText(mVersionTv.getText().toString().concat(mVersionStr));
        }
        if (mUpdateDateStr != null) {
            mUpdateDateTv.setText(mUpdateDateTv.getText().toString().concat(mUpdateDateStr));
        }
        if (mInfoStr != null) {
            mInfoTv.setText(mInfoStr);
        }
        if (mInfoTv.getMaxLines() > 4) {
            mInfoTv.setMaxLines(4);
        }
    }

    /**
     * 显示dialog
     *
     * @param isSystemDialog 是否需要作为系统dialog弹出；
     */
    public void showDialog(boolean isSystemDialog) {
        if (isSystemDialog) {
            LogUtils.i("ddd", "start up a system dialog");
            this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        } else {
            LogUtils.i("ddd", "start up a non system dialog");
        }
        initData();
        PlatUpdateManager.saveDialogShowThisTime(mContext, System.currentTimeMillis());
        PreferencesUtils.putBoolean(mContext, Constant.CLIENT_UPDATE_SHOWED, true);
        if (!isShowing()) {
            show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 点击更新按钮
            case R.id.updateDialog_confirm:
            case R.id.messageDialog_rightBtn:
                if (StorageSpaceDetection.noMemory(UpdateInfo.getPackage_size())) {
                    DCStat.outofmemoryEvent("0M","包大小大于当前剩余内存", null);
                    ToastUtils.showToast(mContext, "内存不够，请清理空间！");
                    return;
                }
                if (isForce) {
                    PlatUpdateManager.savePlatUpdateMode(mContext, PlatUpdateMode.force);
                } else {
                    PlatUpdateManager.savePlatUpdateMode(mContext, PlatUpdateMode.alert);
                    UpdateInfoDialog.this.dismiss();
                }
                // 事件统计；
                startUpdateService(PlatUpdateAction.ACTION_DIALOG_CONFIRM);

                break;
            case R.id.updateDialog_closeIv:
            case R.id.messageDialog_leftBtn:
                cancelDialog(isForce);
                LTNotificationManager.getinstance().sendGameCenterUpGradeN(UpdateInfo.getVersion(), false);
                break;
        }
    }

    /**
     * 关闭dialog
     *
     * @param isExit 是否需要退出应用；0
     */
    private void cancelDialog(boolean isExit) {
        if (isExit) {
            UpdateInfoDialog.this.cancel();
            ActivityManager.self().exitAppWithoutShutdown();
        } else {
            UpdateInfoDialog.this.dismiss();
        }
    }

    private void startUpdateService(String action) {
        Intent serverIntent = new Intent(mContext, PlatUpdateService.class);
        serverIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        serverIntent.putExtra(Constant.RETRY_FLAG,false);
        if (!TextUtils.isEmpty(action)) {
            serverIntent.putExtra(PlatUpdateAction.ACTION, action);
        }
        mContext.startService(serverIntent);
    }

}
