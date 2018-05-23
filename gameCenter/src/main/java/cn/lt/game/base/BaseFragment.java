package cn.lt.game.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.baidu.mobstat.StatService;

import java.lang.reflect.Field;
import java.util.List;

import cn.lt.game.application.MyApplication;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.PreferencesUtils;
import cn.lt.game.lib.util.FromPageManager;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.PopWidowManageUtil;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsDataProductorImpl;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.pageunits.PageMultiUnitsReportManager;
import cn.lt.game.ui.app.SplashADActivity;
import cn.lt.game.ui.app.adapter.data.ItemData;

@SuppressLint("ValidFragment")
public abstract class BaseFragment<T> extends Fragment {
    public static final String TAG = "BaseFragment";
    public Boolean isConnected = false;
    public int indexPage;// 由上一页面传入的游戏id
    protected Activity mActivity;
    protected String mEventID;
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

    public String getTabUrl() {
        return tabUrl;
    }

    public void setTabUrl(String tabUrl) {
        this.tabUrl = tabUrl;
    }

    private String tabUrl;

    /**
     * 设置页面的别名，用于统计。。
     * 复写该该方法时直接调用{@link #setmPageAlias(String s)}赋值；
     * 获取页面名称时直接调用{@link #getPageAlias()}
     */
    public abstract void setPageAlias();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        setPageAlias();
        if (isVisibleToUser && isResumed()) {
            LogUtils.i(TAG, "setUserVisibleHint 可见的页面" + getPageAlias());
            if(Constant.PAGE_CHOUJIANG.equals(getPageAlias()) || Constant.PAGE_JIFENG.equals(getPageAlias())){
                LogUtils.i(TAG, "是抽奖、积分页面，交给子类去报" + getPageAlias());
            }else{
                statEvent();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = getActivity();
    }

    /**
     * 统计lll
     */
    private void statEvent() {
        StatisticsEventData event = null;
        Fragment parent = getParentFragment();
        List<Fragment> children = getChildFragmentManager().getFragments();

        if (parent == null) {
            if (children == null) {
                event = StatisticsDataProductorImpl.produceStatisticsData(null, 0, 0, mEventID, getPageAlias(), ReportEvent.ACTION_PAGEJUMP, null, tabUrl, null, "");
            } else {
                // 没有父Fragment,有子Fragment
//                if (getUserVisibleHint()) {
//                    for (Fragment fragment : children) {
//                        if (fragment != null && fragment.getUserVisibleHint()) {
//                            event = StatisticsDataProductorImpl.produceStatisticsData(null, 0, 0, mEventID, ((BaseFragment) fragment).getPageAlias(), ReportEvent.ACTION_PAGEJUMP, null, null, null);
//                            break;
//                        }
//                    }
//                }
            }
        } else {
            if (children == null) {
                // 作为子Fragment嵌套在别的Fragemnt里面，且没有子Fragment
                if (parent.getUserVisibleHint() && getUserVisibleHint()) {
                    event = StatisticsDataProductorImpl.produceStatisticsData(null, 0, 0, mEventID, getPageAlias(), ReportEvent.ACTION_PAGEJUMP, null, tabUrl, null, "");
                }
            } else {
                // 有父Fragment又有子Fragment；
                if (parent.getUserVisibleHint()) {
                    for (Fragment fragment : children) {
                        if (fragment != null && fragment.getUserVisibleHint()) {
                            event = StatisticsDataProductorImpl.produceStatisticsData(null, 0, 0, mEventID, ((BaseFragment) fragment).getPageAlias(), ReportEvent.ACTION_PAGEJUMP, null, tabUrl, null, "");
                            break;
                        }
                    }
                }
            }
        }
//        if (event != null && TextUtils.isEmpty(event.getPage())) {

        // 延迟50ms 解决手机邮箱注册切换时的多上报登陆界面
//        savePageToMemory(event);
//        View view = getView();
//        if (view != null) {
//            final StatisticsEventData finalEvent = event;
//            view.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (isResumed()) {
//                        DCStat.pageJumpEvent(finalEvent);
//                    }
//                }
//            },50);
//            if (isResumed()) {
//                savePageToMemory(event);
//            }
//        } else {
//            DCStat.pageJumpEvent(event);
//            savePageToMemory(event);
//        }

        FromPageManager.pageJumpReport(event);
    }

    /**
     *给子页面单独报
     * @param id
     * @param pageChoujiang
     */
    public void pageJumpReport(String id, String pageChoujiang) {
        StatisticsEventData statisticsEventData = StatisticsDataProductorImpl.produceStatisticsData(
                null, 0, 0, id, pageChoujiang,
                ReportEvent.ACTION_PAGEJUMP, null, null, null, "");
        FromPageManager.pageJumpReport(statisticsEventData);
    }

    /**
     * 滚动停止时逻辑
     *
     * @param scrollState
     * @param page
     * @param saveTemp
     * @param listView
     */
    public void reportWhenScroll(int scrollState, String page, List<ItemData<? extends BaseUIModule>> saveTemp, T listView) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                PageMultiUnitsReportManager.getInstance().buildPageUnits(saveTemp, listView, page, "", "", "", "");
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        indexPage = mActivity.getIntent().getIntExtra("indexPage", 0);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setPageAlias();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stubnetWorkStateViewFactory =
        // NetWorkStateViewFactory
        //		netWorkStateViewFactory = new NetWorkStateViewFactory(
        //				(ViewGroup) getView());
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        setPageAlias();
        // TODO Auto-generated method stub
        LogUtils.e("onResume: " + getClass().getCanonicalName() + "\t isVisible: " + getUserVisibleHint());
        if (getUserVisibleHint()) {
            LogUtils.i(TAG, "onResume 可见的页面" + getPageAlias());
            if(Constant.PAGE_CHOUJIANG.equals(getPageAlias()) || Constant.PAGE_JIFENG.equals(getPageAlias())){
                LogUtils.i(TAG, "是抽奖、积分页面，交给子类去报" + getPageAlias());
            }else{
                statEvent();
            }
        }

        if (MyApplication.isBackGroud) {
            MyApplication.isBackGroud = false;
            if (!(mPageAlias.equals(Constant.PAGE_PERSONAL_REGISTER_EMAIL) || mPageAlias.equals(Constant.PAGE_PERSONAL_REGISTER_PHONE) || mPageAlias.equals(Constant.PAGE_PERSONAL_REGISTER_SET_ALIAS)
                    || mPageAlias.equals(Constant.PAGE_PERSONAL_FIND_PASSWORD_EMAIL) || mPageAlias.equals(Constant.PAGE_PERSONAL_FIND_PASSWORD_PHONE) || mPageAlias.equals(Constant.PAGE_PERSONAL_FIND_PASSWORD)
                    || mPageAlias.equals(Constant.PAGE_PERSONAL_CHANGE_PASSWORD) || mPageAlias.equals(Constant.PAGE_PERSONAL_LOGIN))) {
                cn.lt.game.lib.util.LogUtils.i("AD_DEMO", mPageAlias + "++++++++++++++++++");
                PreferencesUtils.putLong(getActivity(), PopWidowManageUtil.FRONT_DESK_TIME, System.currentTimeMillis());
                if (PopWidowManageUtil.needSplashAD(getActivity())) {
                    Intent intent = new Intent(getActivity(), SplashADActivity.class);
                    intent.putExtra("fromGameCenterActivity", true);
                    startActivity(intent);
                }
            } else {
                MyApplication.isBackGroud = false;
            }
        }

        StatService.onResume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        StatService.onPause(this);
        super.onPause();
    }

    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(mActivity, 1, new Intent(), flags);
        return pendingIntent;
    }

}
