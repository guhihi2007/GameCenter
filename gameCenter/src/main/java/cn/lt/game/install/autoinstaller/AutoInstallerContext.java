package cn.lt.game.install.autoinstaller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.PreferencesUtils;
import cn.lt.game.lib.util.PopWidowManageUtil;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.lib.util.log.Logger;
import cn.lt.game.lib.widget.MessageDialog;

/**
 * Created by wenchao on 2015/6/24.
 * 自动安装对外api
 */
public class AutoInstallerContext {

    private static final long WEEK = 7 * 24 * 60 * 60 * 1000;//单位毫秒

    public static final int STATUS_DISABLE = 0;
    public static final int STATUS_OPEN = 1;
    public static final int STATUS_CLOSE = 2;

    private static Context mContext;

    private boolean needPropUser = true;


    /**
     * 是否开启自动安装功能
     *
     * @return
     */
    public static int getAccessibilityStatus() {
        int i = 0;
        try {
            i = Settings.Secure.getInt(mContext.getContentResolver(), "accessibility_enabled");
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
//            return STATUS_DISABLE;
        }
        if (i == 0) {
            //乐视手机返回0
            //华为手机返回1
//            return STATUS_CLOSE;
        }

        String string = Settings.Secure.getString(mContext.getContentResolver(), "enabled_accessibility_services");
        if (string == null) {
            return STATUS_CLOSE;
        }

        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
        simpleStringSplitter.setString(string);
        while (simpleStringSplitter.hasNext()) {
            if (simpleStringSplitter.next().equalsIgnoreCase(mContext.getPackageName() + "/" + AccessibilityService.class.getName())) {
                Logger.i("AutoInstall is start.");
                return STATUS_OPEN;
            }
        }
        return STATUS_CLOSE;
    }

    private static final String TAG = "AutoInstallerContext";

    /**
     * 用户自动装功能开启逻辑
     */
    public void promptUserOpen(Activity context) {
        //山寨机渠道不使用辅助功能自动安装功能
        if (Constant.CHANNEL.contains("szj")) {
            return;
        }
        if (context == null) return;
//        checkTime();
        //自动装没有开启就提示用户开启
        int status = getAccessibilityStatus();
        switch (status) {
            case STATUS_DISABLE:
                Logger.w("the phone do not support Accessibility Service!");
                break;
            case STATUS_CLOSE:
/*                if (needPropUser) {
                    needPropUser = false;
                    setLastWarmTime();
                    showConfirmDialog(context);
                }*/
                if (PopWidowManageUtil.needAutoInstallDialog(context)) {
                    showConfirmDialog(context);
                }
                Logger.w("Accessibility Service is close,but once prop one");
                break;
            case STATUS_OPEN:
                Logger.w("Accessibility Service is aleardy enabled.");
                break;
        }

    }

    /**
     * 弹出确认框
     */
    private static void showConfirmDialog(final Activity context) {
        PreferencesUtils.putLong(context, PopWidowManageUtil.LAST_AUTO_INSTALL_TIME, System.currentTimeMillis());
        PreferencesUtils.putBoolean(context, Constant.AUTO_INSTALL_SHOWED, true);
        final MessageDialog dialog = new MessageDialog(context, context.getResources().getString(R.string.gentle_reminder), context.getResources().getString(R.string.app_auto_install_title), context.getResources().getString(R.string.cancel_ignor_bt), context.getResources().getString(R.string.gallery_send));
        dialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {
            @Override
            public void OnClick(View view) {
                try {
                    //跳转到设置页面
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    context.startActivity(intent);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //弹出引导
                            showSettingGuid(context);
                        }
                    }, 100);
                } catch (Exception e) {
                    //No Activity found to handle Intent
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }

    /**
     * 显示引导界面
     *
     * @param context
     */
    private static void showSettingGuid(Activity context) {

        final WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.format = PixelFormat.TRANSLUCENT;
        params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.START | Gravity.TOP;
        params.x = 0;
        params.y = 0;
        final View view = context.getLayoutInflater().inflate(R.layout.window_autoinstall_guid, null);
        wm.addView(view, params);

        view.findViewById(R.id.autoinstall_guid_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击我知道了消失
                wm.removeView(view);
            }
        });
    }

    public void markNeedPropUser() {
        needPropUser = true;
    }

    private void checkTime() {
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(mContext);
        long lastTime = sharedPreferencesUtil.getLong(SharedPreferencesUtil.AUTO_INSTALL_TIME);
        long currentTime = System.currentTimeMillis();
        if (lastTime == 0 || currentTime - lastTime >= WEEK) {
            markNeedPropUser();
        } else {
            needPropUser = false;
        }
    }

    private void setLastWarmTime() {
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(mContext);
        sharedPreferencesUtil.add(SharedPreferencesUtil.AUTO_INSTALL_TIME, System.currentTimeMillis());

    }


    private AutoInstallerContext() {
    }

    private static AutoInstallerContext instance;

    public static AutoInstallerContext getInstance() {
        if (instance == null) {
            synchronized (AutoInstallerContext.class) {
                if (instance == null) {
                    instance = new AutoInstallerContext();
                }
            }
        }
        return instance;

    }

    public void init(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

}
