package cn.lt.game.ui.app.tabbar;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import cn.lt.game.application.MyApplication;
import cn.lt.game.lib.PreferencesUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.PopWidowManageUtil;
import cn.lt.game.update.PlatUpdateManager;

/**
 * Created by ATian on 2017/11/2.
 */

public class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private Application mApplication;

    public MyActivityLifecycleCallbacks(Application application) {
        this.mApplication = application;

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (!PlatUpdateManager.isForeground(mApplication)) {
            LogUtils.i("Erosion", "isForeground");
            PreferencesUtils.putLong(mApplication, PopWidowManageUtil.BACKGROUND_TIME, System.currentTimeMillis());
            MyApplication.isBackGroud = true;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
