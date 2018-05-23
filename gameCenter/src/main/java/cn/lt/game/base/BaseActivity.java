package cn.lt.game.base;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;

import com.baidu.mobstat.StatService;

import java.util.List;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.PreferencesUtils;
import cn.lt.game.lib.util.ActivityManager;
import cn.lt.game.lib.util.FromPageManager;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.PopWidowManageUtil;
import cn.lt.game.lib.util.SystemBarTintManager;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsDataProductorImpl;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.statistics.pageunits.PageMultiUnitsReportManager;
import cn.lt.game.ui.app.SplashADActivity;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.update.PlatUpdateManager;


public abstract class BaseActivity extends Activity {

    protected String mEventID;
    private SystemBarTintManager tintManager;
    /**
     * 页面的别名，用于数据统计
     */
    private String mPageAlias = "";

    public String getPageAlias() {
        return mPageAlias;
    }

    public void setmPageAlias(String pageAlias) {
        this.mPageAlias = pageAlias;
    }

    public void setmPageAlias(String pageAlias, String id) {
        this.mPageAlias = pageAlias;
        this.mEventID = id;
    }
    public void setTabUrl(String tabUrl) {
        this.tabUrl = tabUrl;
    }

    private String tabUrl;

    /**
     * 设置页面的别名，用于统计。。
     * 复写该该方法时直接调用{@link #setmPageAlias(String s)}<p/>
     * 获取页面名称时直接调用{@link #getPageAlias()}
     */
    public abstract void setPageAlias();

    /**
     * 滚动停止时逻辑
     *
     * @param scrollState
     * @param page
     * @param saveTemp
     * @param listView
     * @param title
     * @param pageId
     * @param mCurrentTagId
     * @param mCurrentTagLableTitle
     */
    public void reportWhenScroll(int scrollState, String page, List<ItemData<? extends BaseUIModule>> saveTemp, RefreshAndLoadMoreListView listView, String title, String pageId, String mCurrentTagId, String mCurrentTagLableTitle) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                LogUtils.i("pppp", "BaseActivity=>SCROLL_STATE_IDLE");
                PageMultiUnitsReportManager.getInstance().buildPageUnits(saveTemp, listView, page, mCurrentTagLableTitle, pageId, "", title);
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setPageAlias();
        setTransparentTitleBar();
        ActivityManager.self().add(this);
    }

    @Override
    protected void onResume() {
        setPageAlias();
        StatisticsEventData event = StatisticsDataProductorImpl.produceStatisticsData(null, 0, 0, mEventID, getPageAlias(), ReportEvent.ACTION_PAGEJUMP, null, tabUrl, null, "");
        FromPageManager.pageJumpReport(event);
        StatService.onResume(this);
        super.onResume();
        if (!MyApplication.castFrom(this).isActive()) {
            MyApplication.castFrom(this).setIsActive(true);
            DCStat.checkEvent(ReportEvent.ACTION_CHECKIN);
        }

        if (MyApplication.isBackGroud) {
            MyApplication.isBackGroud = false;
            if (!(mPageAlias.equals(Constant.PAGE_PERSONAL_FEEDBACK) || mPageAlias.equals(Constant.PAGE_PERSONAL_SETTING) || mPageAlias.equals(Constant.PAGE_PERSONAL_UNLOGIN) || mPageAlias.equals(Constant.PAGE_PERSONAL_HAS_LOGIN) || mPageAlias.equals(Constant.PAGE_PERSONAL_ABOUT) || mPageAlias.equals(Constant.PAGE_PERSONAL_CHANGE_EMAIL) || mPageAlias.equals(Constant.PAGE_PERSONAL_BINDING_PHONE) || mPageAlias.equals(Constant.PAGE_PERSONAL_EDIT))) {
                cn.lt.game.lib.util.LogUtils.i("AD_DEMO", mPageAlias + "++++++++++++++++++");
                PreferencesUtils.putLong(this, PopWidowManageUtil.FRONT_DESK_TIME, System.currentTimeMillis());

                if (PopWidowManageUtil.needSplashAD(this)) {
                    Intent intent = new Intent(this, SplashADActivity.class);
                    intent.putExtra("fromGameCenterActivity", true);
                    startActivity(intent);
                }
            } else {
                MyApplication.isBackGroud = false;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!PlatUpdateManager.isForeground(this)) {
            MyApplication.castFrom(this).setIsActive(false);
            DCStat.checkEvent(ReportEvent.ACTION_CHECKOUT);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    /**
     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
     * @flags属性: 在顶部常驻:Notification.FLAG_ONGOING_EVENT 点击去除：
     * Notification.FLAG_AUTO_CANCEL
     */
    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.self().remove(this);
    }

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
            tintManager = getTintManager();
            // 开启状态栏上色开关
            tintManager.setStatusBarTintEnabled(true);

            // 状态栏设置颜色
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.theme_green));

            // 设置状态栏背景图片
//        	tintManager.setStatusBarTintDrawable(MyDrawable);

            // 开启导航栏栏上色
//			tintManager.setNavigationBarTintEnabled(true);

            // 设置导航栏背景图片
//			tintManager.setNavigationBarTintResource(R.mipmap.icon_11_level);

            //设置Activity是否在状态栏下方（否则嵌入到标题栏）
            ActivityManager.getRootView(this).setFitsSystemWindows(true);
        }
    }


}
