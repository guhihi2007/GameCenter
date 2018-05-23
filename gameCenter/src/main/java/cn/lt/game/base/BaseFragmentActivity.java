package cn.lt.game.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

import com.baidu.mobstat.StatService;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.lib.util.ActivityManager;
import cn.lt.game.lib.util.SystemBarTintManager;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.HomeActivity;
import cn.lt.game.update.PlatUpdateManager;

public abstract class BaseFragmentActivity extends FragmentActivity {

    /**
     * 节点名称；每次当前activiy到栈顶时会上报代表的节点名称到路径记录器用于路径统计分析；
     */
    public String mNodeName;
    private SystemBarTintManager tintManager;

    public String getmNodeName() {
        return mNodeName;
    }

    public void setmNodeName(String mNodeName) {
        this.mNodeName = mNodeName;
    }

    int i;

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        StatService.onResume(this);
        super.onResume();
        if (!MyApplication.castFrom(this).isActive()) {
            MyApplication.castFrom(this).setIsActive(true);
            DCStat.checkEvent(ReportEvent.ACTION_CHECKIN);
        }
//        MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                String pageName = MyApplication.application.mCurrentPage;
//
//                if (MyApplication.isBackGroud) {
//                    if (!(pageName.equals(Constant.PAGE_PERSONAL_REGISTER_EMAIL) || pageName.equals(Constant.PAGE_PERSONAL_REGISTER_PHONE) || pageName.equals(Constant.PAGE_PERSONAL_REGISTER_SET_ALIAS)
//                            || pageName.equals(Constant.PAGE_PERSONAL_FIND_PASSWORD_EMAIL) || pageName.equals(Constant.PAGE_PERSONAL_SETTING) || pageName.equals(Constant.PAGE_PERSONAL_ABOUT)
//                            || pageName.equals(Constant.PAGE_PERSONAL_LOGIN))) {
//                        LogUtils.i("AD_DEMO", pageName+"++++++++++++++++++");
//                        Intent intent = new Intent(BaseFragmentActivity.this, LoadingActivity.class);
//                        intent.putExtra("fromGameCenterActivity", true);
//                        startActivity(intent);
//                    }else {
//                        MyApplication.isBackGroud = false;
//                    }
//                }
//            }
//        },5);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!PlatUpdateManager.isForeground(BaseFragmentActivity.this)) {
            MyApplication.castFrom(BaseFragmentActivity.this).setIsActive(false);
            DCStat.checkEvent(ReportEvent.ACTION_CHECKOUT);
        }

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        StatService.onPause(this);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setTransparentTitleBar();
        setNodeName();
//        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        ActivityManager.self().add(this);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        ActivityManager.self().remove(this);
    }

    public abstract void setNodeName();

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setApplyBackgroundTinting();
    }

    /**
     * 设置APP透明导航栏和透明状态栏
     */
    private void setTransparentTitleBar() {
        // 系统4.4以上才有效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    protected SystemBarTintManager getTintManager() {
        if (tintManager == null) tintManager = new SystemBarTintManager(this);
        return tintManager;
    }

    /**
     * 设置沉浸式
     */
    public void setApplyBackgroundTinting() {
        // 系统4.4以上才有效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (this instanceof HomeActivity) {
                ActivityManager.getRootView(this).setFitsSystemWindows(false);
            } else {
                tintManager = getTintManager();
                // 开启状态栏上色开关
                tintManager.setStatusBarTintEnabled(true);

                // 设置状态栏背景图片
//        	tintManager.setStatusBarTintDrawable(MyDrawable);

                // 开启导航栏栏上色
//			tintManager.setNavigationBarTintEnabled(true);

                // 设置导航栏背景图片
//			tintManager.setNavigationBarTintResource(R.mipmap.icon_11_level);

                //设置Activity是否在状态栏下方（否则嵌入到标题栏）

                // 状态栏设置颜色
                tintManager.setStatusBarTintColor(getResources().getColor(R.color.theme_green));
                ActivityManager.getRootView(this).setFitsSystemWindows(true);
            }
        }

    }

//    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
//        String SYSTEM_REASON = "reason";
//        String SYSTEM_HOME_KEY = "homekey";
//        String SYSTEM_HOME_KEY_LONG = "recentapps";
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
//                String reason = intent.getStringExtra(SYSTEM_REASON);
//                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
//                    //表示按了home键,程序到了后台
//                    MyApplication.isBackGroud = true;
//                    Toast.makeText(getApplicationContext(), "home", 1).show();
//                } else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
//                    //表示长按home键,显示最近使用的程序列表
//                }
//            }
//        }
//    };

}
