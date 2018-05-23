package cn.lt.game.ui.app.personalcenter.appsetting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import cn.lt.game.R;
import cn.lt.game.global.LogTAG;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.global.Constant;
import cn.lt.game.install.ApkInstallManger;
import cn.lt.game.lib.PreferencesUtils;
import cn.lt.game.lib.ShellUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.PackageUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.threadpool.LTAsyncTask;

public class AppSettingActivity extends BaseActivity implements OnClickListener, CompoundButton.OnCheckedChangeListener {
    private RelativeLayout rlDownloadPath;
    private ImageView btnBack;
    private ToggleButton tbtnAutoInstall;


    private ImageButton expandCollapse;
    private TextView mExpandOrCollapseDesc;

    private ToggleButton tbtnDelPack;

    private MyApplication application;

    private boolean autoUpgradeDescIsShow = false;// 记录零流量升级说明是否已经显示了


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (msg.arg1 == 1) {//获取root权限成功
                        LogUtils.i("silent", "获取root权限成功！");
                        ToastUtils.showToast(getApplicationContext(), "获取自动安装权限成功！");
                        MyApplication.application.setRootInstallIsChecked(true);
                        MyApplication.application.setRootInstall(true);
                        tbtnAutoInstall.setChecked(true);
                    } else if (msg.arg1 == 2) {//获取Root权限失败
                        LogUtils.i("silent", "获取root权限失败！");
                        MyApplication.application.setRootInstallIsChecked(false);
                        MyApplication.application.setRootInstall(false);
                        tbtnAutoInstall.setChecked(false);
                        removeAppInstallRequst();
                        ToastUtils.showToast(getApplicationContext(), "获取自动安装权限失败");
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private void removeAppInstallRequst() {
        if (!MyApplication.application.getSystemInstall()) {
            ApkInstallManger.self().removeAllInstallingApp();
            LogUtils.i("silent", "root权限消失 or rootToggle turnOff 移除安装请求过监控");
        }
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setting);
        ((TextView) findViewById(R.id.tv_page_title)).setText(R.string.user_center_title_setting);
        application = (MyApplication) getApplication();

        rlDownloadPath = (RelativeLayout) findViewById(R.id.setting_download_path);
        btnBack = (ImageView) findViewById(R.id.btn_page_back);
        tbtnAutoInstall = (ToggleButton) findViewById(R.id.setting_toggle_auto_install);

        // TODO 是否显示零流量升级
        boolean isSystemApp = PackageUtils.isSystemApplication(getApplicationContext());
        if (isSystemApp) {
            expandCollapse = (ImageButton) findViewById(R.id.expand_collapse);
            findViewById(R.id.ll_expand_or_collapse).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandCollapse.performClick();
                }
            });
            // 显示零流量升级
            RelativeLayout mUpgradeSection = (RelativeLayout) findViewById(R.id.setting_auto_upgrade);
            mUpgradeSection.setVisibility(View.VISIBLE);
            ToggleButton tbtnAutoUpgrade = (ToggleButton) findViewById(R.id.setting_toggle_auto_upgrade);
            boolean isOpen = PreferencesUtils.getBoolean(this, Constant.Setting.AUTOUPGRADE, true);
            tbtnAutoUpgrade.setChecked(isOpen);
            tbtnAutoUpgrade.setOnCheckedChangeListener(this);

            ExpandableTextView mExpandableTextView = (ExpandableTextView) findViewById(R.id.expand_text_view);
            mExpandOrCollapseDesc = (TextView) findViewById(R.id.expand_or_collapse_desc);
            mExpandableTextView.setText(getString(R.string.user_center_auto_upgrade_desc));
            mExpandableTextView.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
                @Override
                public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                    mExpandOrCollapseDesc.setText(isExpanded ? R.string.collapse_text : R.string.expand_text);
                    autoUpgradeDescIsShow = isExpanded;
                }
            });
            if (!isOpen) {
                autoUpgradeDescIsShow = true;
                expandCollapse.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        expandCollapse.performClick();
                    }
                },500);
            }

            mUpgradeSection.setVisibility(View.VISIBLE);
        }

        tbtnDelPack = (ToggleButton) findViewById(R.id.setting_toggle_auto_delete_pack);

        tbtnDelPack.setChecked(application.getDeleteApk());
        LogUtils.d(LogTAG.HTAG, "setChecked==>" + application.getRootInstall());
        setRootInstallBtn();

        btnBack.setOnClickListener(this);
        rlDownloadPath.setOnClickListener(this);

        tbtnAutoInstall.setOnCheckedChangeListener(this);
        tbtnDelPack.setOnCheckedChangeListener(this);

//        sharedPreferences = this.getSharedPreferences("setting", Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
    }

    private void setRootInstallBtn() {
        boolean deviceIsRoot = application.getRootInstall();
        boolean rootInstallIsChecked = application.getRootInstallIsChecked();
        tbtnAutoInstall.setChecked(deviceIsRoot && rootInstallIsChecked);
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(cn.lt.game.global.Constant.PAGE_PERSONAL_SETTING);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_download_path:
                new SettingPathDialog(AppSettingActivity.this).show();
                break;
            case R.id.btn_page_back:
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.setting_toggle_auto_upgrade:
                PreferencesUtils.putBoolean(this, Constant.Setting.AUTOUPGRADE, isChecked);

                // 设置零流量升级说明显示或隐藏
                if(isChecked) {
                    if(autoUpgradeDescIsShow) {
                        expandCollapse.performClick();
                    }
                } else {
                    if(!autoUpgradeDescIsShow) {
                        expandCollapse.performClick();
                    }
                }

                break;
            case R.id.setting_toggle_auto_install:
                if (isChecked) {
                    new LTAsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            Message msg = handler.obtainMessage(0);
                            if (ShellUtils.checkRootPermission()) {
                                msg.arg1 = 1;
                            } else {
                                msg.arg1 = 2;
                            }
                            handler.sendMessage(msg);
                            return null;
                        }
                    }.execute();
                } else {
                    tbtnAutoInstall.setChecked(false);
                    application.setRootInstallIsChecked(false);
                    removeAppInstallRequst();
                }
                break;
            case R.id.setting_toggle_auto_delete_pack:
                application.setDeleteApk(isChecked);
                break;
        }
    }


}
