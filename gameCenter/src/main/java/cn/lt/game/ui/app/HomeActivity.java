package cn.lt.game.ui.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.igexin.sdk.PushManager;
import com.tencent.smtt.sdk.QbSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.application.GlobalParams;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseFragmentActivity;
import cn.lt.game.bean.BaseBean;
import cn.lt.game.bean.BottomMenuBean;
import cn.lt.game.bean.FloatAdsBean;
import cn.lt.game.bean.GameInfoBean;
import cn.lt.game.bean.GamePointsNet;
import cn.lt.game.bean.InstallInfoBean;
import cn.lt.game.bean.NewLaunchBean;
import cn.lt.game.bean.TabEntity;
import cn.lt.game.bean.TabEnum;
import cn.lt.game.download.ConnectionChangeReceiver;
import cn.lt.game.download.DownloadService;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.JumpToContentTabEvent;
import cn.lt.game.event.RedPointEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.install.ApkInstallManger;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.PreferencesUtils;
import cn.lt.game.lib.ScreenUtils;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.ActivityManager;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.CheckSilentUtils;
import cn.lt.game.lib.util.GsonUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.PopWidowManageUtil;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.lib.util.StorageSpaceDetection;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.file.FeedbackRedUtil;
import cn.lt.game.lib.util.file.FileUtil;
import cn.lt.game.lib.util.file.PointTaskRedUtil;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.util.redpoints.RedPointsManager;
import cn.lt.game.lib.web.WebCallBackToBean;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.model.EntryPages;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.model.PageDetail;
import cn.lt.game.model.PageMap;
import cn.lt.game.model.SharePreferencesKey;
import cn.lt.game.model.State;
import cn.lt.game.net.Host;
import cn.lt.game.net.HttpResult;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.push.getui.GeTuiIntentService;
import cn.lt.game.push.getui.GeTuiService;
import cn.lt.game.statistics.database.dao.UserDao;
import cn.lt.game.statistics.database.provider.NotifyUserInfoToAppCenterMgr;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.category.CategoryFragment;
import cn.lt.game.ui.app.gamedetail.GameDetailHomeActivity;
import cn.lt.game.ui.app.gamestrategy.HjSdk;
import cn.lt.game.ui.app.hot.HotFragment;
import cn.lt.game.ui.app.index.IndexFragment;
import cn.lt.game.ui.app.jump.IJumper;
import cn.lt.game.ui.app.jump.JumpFactory;
import cn.lt.game.ui.app.personalcenter.MineFragment;
import cn.lt.game.ui.app.personalcenter.PCNet;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;
import cn.lt.game.ui.app.rank.RankMainFragment;
import cn.lt.game.ui.app.requisite.RequisiteDialog;
import cn.lt.game.ui.app.requisite.manger.SharedPreference;
import cn.lt.game.ui.app.search.SearchActivity;
import cn.lt.game.ui.app.sidebar.UpdateInfo;
import cn.lt.game.ui.app.sidebar.UpdateInfoDialog;
import cn.lt.game.ui.app.specialtopic.SpecialTopicDetailsActivity;
import cn.lt.game.ui.app.tabbar.CoverDialog;
import cn.lt.game.ui.app.tabbar.TabLayout;
import cn.lt.game.ui.notification.LTNotification;
import cn.lt.game.ui.notification.LTNotificationManager;
import cn.lt.game.update.PlatUpdateManager;
import cn.lt.game.update.PlatUpdatePathManger;
import cn.lt.game.update.VersionCheckManger;
import de.greenrobot.event.EventBus;

import static cn.lt.game.bean.TabEnum.HOT;
import static cn.lt.game.bean.TabEnum.INDEX;

/***
 * 主页
 */
public class HomeActivity extends BaseFragmentActivity {

//    public int INDEX = 0, RANK = 1, HOT, CAT, MANGER;
//    private TextView redPoint;
    /**
     * 版本更新的检查结果回调；
     */

    private VersionCheckManger.VersionCheckCallback mVersionCheckCallBack;
    private MonitionCallback mMonitionCallback;
    private Intent mIntent;
    private ArrayList<Fragment> mFragments;
    private String selectColor, unSelectColor;

    // 缓存的精选必玩数据和浮层广告数据
    private String mNecessary;
    private FloatAdsBean mFloatAdsBean;

    private BroadcastReceiver mConnectionChangeReceiver = new ConnectionChangeReceiver(this);
    HotFragment hotFragment;
    MineFragment mineFragment;
    IndexFragment indexFragment;
    RankMainFragment rankMainFragment;
    CategoryFragment categoryFragment;
    boolean isHot;

    private String[] mTitles = null;

    private ArrayList<TabEntity> mTabEntities = new ArrayList<>();
    private TabLayout tabLayout;
    //管理页真实索引
    private int minePosition = 3;
    int loadDrawableCount;

    /**
     * 个人中心是否显示红点
     */
    public void showMineFootRedPoints(boolean show) {
        if (tabLayout == null) {
            return;
        }
        if (show) {
            tabLayout.showDot(minePosition);
        } else {
            tabLayout.hideMsg(minePosition);
        }
    }

    /**
     * 管理页面是否显示红点
     */
    public void onEventMainThread(RedPointEvent event) {

        LogUtils.e("redpoint==>"+event.needShow);
        if (!this.isFinishing()) {
            showMineFootRedPoints(RedPointsManager.getInstance().isShowMineRed(this));

            indexFragment.refreshTopManageRedPoints();
            rankMainFragment.refreshTopManageRedPoints();
            if (hotFragment != null) {
                hotFragment.refreshTopManageRedPoints();
            }
            categoryFragment.refreshTopManageRedPoints();
        }
    }

    public void registerMotionCallback(MonitionCallback callback) {
        mMonitionCallback = callback;
    }

    public void unregisterMotionCallback() {
        mMonitionCallback = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LogUtils.i("Erosion", "静茹时间====" + System.currentTimeMillis());
//        showCover();//450专用，以后版本要去掉
        initEnviron();
        initView();         //   这两个顺序不能颠倒，已知问题：1.会导致通知栏点进来HomeActivity崩溃
        getIntentData();    //   这两个顺序不能颠倒，已知问题：1.会导致通知栏点进来HomeActivity崩溃

        initRedPointForMangerPage();

        setCurrentView();
        getPointGameDataNet();
        initUserAccount();
        dealFeedbackRed();
    }

    private void showCover() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this, SharePreferencesKey.CQKJ_COVER, Context.MODE_PRIVATE);
        if(!spUtil.getBoolean(SharePreferencesKey.CQKJ_COVER,false)){
            new CoverDialog(this).show();
            spUtil.add(SharePreferencesKey.CQKJ_COVER,true);
        }
    }

    private void initUserAccount() {
        try {
            List<UserBaseInfo> userBaseInfos = UserDao.newInstance(MyApplication.application).queryUserData();
            LogUtils.d(LogTAG.USER, "游戏中心 HomeActivity：查自己的userBaseInfos:" + userBaseInfos);
            if (userBaseInfos.size() == 0) {//第一优先：看自己有没有
                LogUtils.d(LogTAG.USER, "游戏中心 HomeActivity：自己没有数据");
                List<UserBaseInfo> otherInfos = NotifyUserInfoToAppCenterMgr.quaryFromAppCenter(this);
                if (otherInfos != null && otherInfos.size() == 1) {
                    LogUtils.d(LogTAG.USER, "游戏中心 HomeActivity：查到应用市场有 Token:" + otherInfos.get(otherInfos.size() - 1).getToken());
                    GlobalParams.token = otherInfos.get(otherInfos.size() - 1).getToken();
                    getUserInfoByToken();
                } else {
                    LogUtils.d(LogTAG.USER, "游戏中心 HomeActivity：查询应用市场 无数据或查询失败");
//                    GlobalParams.token="";
//                    UserInfoManager.instance().userLogout(true);
                }
            } else {
                GlobalParams.token = userBaseInfos.get(0).getToken();
                getUserInfoByToken();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(LogTAG.USER, "游戏中心 HomeActivity：游戏中心查数据  抛异常:" + e.getMessage());
        }
    }

    public void getUserInfoByToken() {
        PCNet.fetchUserInfo(new WebCallBackToObj<UserBaseInfo>() {

            @Override
            protected void handle(UserBaseInfo info) {
                LogUtils.d(LogTAG.USER, "游戏中心 HomeActivity：token登陆成功");
                info.setToken(GlobalParams.token);//token请求没有返回token
                UserInfoManager.instance().setUserBaseInfo(info, true);
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.d(LogTAG.USER, "游戏中心 HomeActivity：token登陆失败");
            }
        });
    }


    private void initView() {
        MyApplication.screenSize = ScreenUtils.getScreenInch(this);
        mFragments = new ArrayList<>();
        indexFragment = new IndexFragment();
        rankMainFragment = new RankMainFragment();
        mFragments.add(indexFragment);
        mFragments.add(rankMainFragment);

        int[] mIconUnselectIds;
        int[] mIconSelectIds;
        isHot = getIntent().getBooleanExtra("is_hot", false);
        if (isHot) {
            minePosition = 4;
            hotFragment = new HotFragment();
            mFragments.add(hotFragment);
            mIconUnselectIds = new int[]{R.mipmap.btn_main_home_n, R.mipmap.btn_main_ranking_n,
                    R.mipmap.btn_main_hot_n, R.mipmap.btn_main_category_n, R.mipmap.btn_main_mine_n};
            mIconSelectIds = new int[]{R.mipmap.btn_main_home_p, R.mipmap.btn_main_rankranking_p,
                    R.mipmap.btn_main_hot_p, R.mipmap.btn_main_category_p, R.mipmap.btn_main_mine_p};

            mTitles = new String[]{"精选", "排行", "热点", "分类", "我"};
        } else {
            minePosition = 3;
            mIconUnselectIds = new int[]{R.mipmap.btn_main_home_n, R.mipmap.btn_main_ranking_n,
                    R.mipmap.btn_main_category_n, R.mipmap.btn_main_mine_n};
            mIconSelectIds = new int[]{R.mipmap.btn_main_home_p, R.mipmap.btn_main_rankranking_p,
                    R.mipmap.btn_main_category_p, R.mipmap.btn_main_mine_p};
            mTitles = new String[]{"精选", "排行", "分类", "我"};

        }
        mineFragment = new MineFragment();
        categoryFragment = new CategoryFragment();
        mFragments.add(categoryFragment);
        mFragments.add(mineFragment);

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        tabLayout = (TabLayout) this.findViewById(R.id.tl_2);
        tabLayout.setTabData(mTabEntities, this, R.id.fl_change, mFragments);

        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                LogUtils.i(LogTAG.HTAG, "onTabSelect" + position);
                if (position == 0) {
                    Activity activity = ActivityManager.self().topActivity();
                    if (activity instanceof HomeActivity) {
                        if (mNecessary != null) {
                            showRequisiteView(mNecessary);
                        } else if (mFloatAdsBean != null) {
                            showFloatAdsView(mFloatAdsBean);
                        }
                    }
                }
//                showMineFootRedPoints(RedPointsManager.getInstance().isShowMineRed(HomeActivity.this));
            }

            @Override
            public void onTabReselect(int position) {
                LogUtils.i(LogTAG.HTAG, "onTabReselect" + position);
            }
        });

    }

    /**
     * 初始化 个推推送（启动应用只会调用一次）
     */
    private void initGeTuiPush() {
        PushManager.getInstance().initialize(getApplicationContext(), GeTuiService.class);

        // com.getui.demo.DemoIntentService 为第三方自定义的推送服务事件接收类
        PushManager.getInstance().registerPushIntentService(getApplicationContext(), GeTuiIntentService.class);
    }

    private void initRedPointForMangerPage() {

        List<GameBaseDetail> allData = FileDownloaders.getAllDownloadFileInfo();
        for (GameBaseDetail game : allData) {
            int state = game.getState();
            if (game.getPrevState() == InstallState.upgrade) {
                continue;
            }
            if (State.isInstallState(state)) {
                showMineFootRedPoints(true);
                ((MyApplication) getApplication()).setNewGameDownload(true);
                break;
            }
        }
    }

    private void initEnviron() {
        fetchBottomMenu();

        //初始化搜索条上的小红点；
        VersionCheckManger.getInstance().setNeedShowRedPointAtSearchBar(PlatUpdateManager.showRedPoint(this));
        // 检查客户端版本
        requestVersionUpdate();

        ApkInstallManger.self().removeAllInstallingApp();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        ActivityManager.self().add(this);

        if (!Utils.isServiceRunning(this, DownloadService.class)) {
            startService(new Intent(this, DownloadService.class));
        }

        if (NetUtils.isMobileNet(HomeActivity.this)) {
            ToastUtils.showToast(HomeActivity.this, "当前使用2G/3G/4G网络");
        }

        // 自动开始任务
        if (NetUtils.isWifi(this) && StorageSpaceDetection.check(true, null, false)) {
            FileDownloaders.autoStartDownload();
        }

        // 唤醒任务
        wakeUpOtherApps();

        // 延迟任务
        MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CheckSilentUtils.checkSilentAndSaveResult(HomeActivity.this);
            }
        }, 2000);

        //初始化欢聚sdk
        HjSdk.getInstance().initOnCreateMainActivity(this);
        initGeTuiPush();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mConnectionChangeReceiver, mFilter);
        LogUtils.d("8887", "网络广播注册了~~");
        try {
            QbSdk.initX5Environment(HomeActivity.this, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (tabLayout != null && tabLayout.getCurrentTab() == 0 && mMonitionCallback != null) {
            if (mMonitionCallback.callback(ev)) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void toOperateJump() {
        String jsonData = LoadingImgWorker.getInstance(this).getLoadingImgDataJsonStr();
        if (TextUtils.isEmpty(jsonData)) {
            return;
        }
        NewLaunchBean bean = new Gson().fromJson(jsonData, NewLaunchBean.class);

        LogUtils.i(LogTAG.LoadingImgTAG, "clickType = " + bean.getClick_type());

        // 如果热点开关无效，跳转到热点相关页面不做处理
        if ((bean.getClick_type().equals(EntryPages.hot_tab) || bean.getClick_type().equals(EntryPages.hot_detail)) && !PopWidowManageUtil.hotContentIsReady()) {
            return;
        }


        PageDetail page = PageMap.instance().getPageDetail(bean.getClick_type());
        if (page != null) {
            if (bean.getData().getGame() != null && !TextUtils.isEmpty(bean.getData().getGame().getId())) {
                page.value = bean.getData().getGame().getId();
                page.id = bean.getData().getGame().getId();
                LogUtils.i(LogTAG.LoadingImgTAG, "跳转到游戏， id = " + page.value);
            } else if (bean.getData().getId() != 0) {
                page.value = String.valueOf(bean.getData().getId());
                page.id = String.valueOf(bean.getData().getId());
                page.value2 = bean.getData().getTitle();
                LogUtils.i(LogTAG.LoadingImgTAG, "跳转到其他类型， id = " + page.value + ", title = " + page.value2);

            } else if (!TextUtils.isEmpty(bean.getData().getUrl())) {
                page.value = bean.getData().getUrl();
                LogUtils.i(LogTAG.LoadingImgTAG, "跳转到H5或内容相关， url = " + page.value);
            }

            IJumper jumper = JumpFactory.produceJumper(PresentType.loading_page, null);
            jumper.jump(page, this);
        }

    }


    private void requestVersionUpdate() {
        mVersionCheckCallBack = new VersionCheckManger.VersionCheckCallback() {
            @Override
            public void callback(Result result) {
                switch (result) {
                    case have:
                    case none:
                        checkVersion();
                        break;
                    case fail:
                        showRequisite();
                        break;
                }
                //检查更新的boolean值在util中已赋值，这里只需获取即可
                showMineFootRedPoints(RedPointsManager.getInstance().isShowMineRed(HomeActivity.this));
                refreshMineSubRedForMineFragment();
                mVersionCheckCallBack = null;
            }
        };
        VersionCheckManger.getInstance().checkVerison(mVersionCheckCallBack, VersionCheckManger.MODE_SELF);
    }

    /* 检查是否有版本更新 */
    private void checkVersion() {
        UpdateInfoDialog updateDialog = new UpdateInfoDialog(this);
        String version = UpdateInfo.getVersion();
        boolean isForce = UpdateInfo.isIs_force();
        updateDialog.setForce(isForce);
        if (TextUtils.isEmpty(version)) {
            // 没有新版本
            FileUtil.delFile(new File(PlatUpdatePathManger.getDownloadPath(this)));
            PlatUpdateManager.saveDialogShowThisTime(this, 0);

            showRequisite();
            VersionCheckManger.getInstance().setNeedShowRedPointAtSearchBar(false);
            PlatUpdateManager.setShowRedPoint(this, false);
        } else {
            VersionCheckManger.getInstance().setNeedShowRedPointAtSearchBar(true);
            PlatUpdateManager.setShowRedPoint(this, true);
            ((MyApplication) getApplication()).setUpdateInfoToApplication();

            // 判断是否需要立即弹框；
            if (isForce || PopWidowManageUtil.needShowClientUpdateDialog(this)) {
                LogUtils.i("LoadingActivity", "需要弹升级提示框");
                updateDialog.showDialog(false);

                if (!isForce) {
                    updateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            showRequisite();
                        }
                    });
                }
            } else {
                showRequisite();
                LogUtils.i("LoadingActivity", "不需要弹升级提示框");
            }
        }
//        showIndexFragmentTopRedPoints();
    }

    private void fetchBottomMenu() {
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.BUTTOM_MENU, null, new WebCallBackToString() {
            @Override
            public void onSuccess(String result) {
                LogUtils.e("Erosion:fetchBottomMenu========= " + result);
                try {
                    JSONObject o = new JSONObject(result);
                    handleLaunchBottomMenus(o.getString("bottom_menus"));
                } catch (JSONException e) {
                    onFailure(0, e);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.i("Erosion", "statusCode==" + statusCode + "===fetchBottomMenu===onFailure==== " + error.getMessage());
            }
        });
    }


    /**
     * 显示精选必玩；
     * PS: 该接口还返回了其他数据，所以每次启动客户端必须调用，不是很合理
     */
    private void showRequisite() {
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.START_URI, new WebCallBackToString() {
            @Override
            public void onSuccess(String result) {
                try {
                    LogUtils.i("Erosion", "请求下来时间====" + System.currentTimeMillis());
                    JSONObject o = new JSONObject(result);
                    // 手机没有sim卡，精选必玩/浮层广告弹窗不显示
                    if (AppUtils.hasSIMCard(HomeActivity.this)) {
                        JSONArray jsonArray = o.optJSONArray("necessary");
                        // 精选必玩和浮层广告是否可显示判断
                        boolean needShowHotApps = PopWidowManageUtil.needShowSpreadDialog(HomeActivity.this);
                        boolean needShowFloatAds = PopWidowManageUtil.needShowFloatAds(HomeActivity.this);
                        if (jsonArray != null && jsonArray.length() > 0) {
                            // 装机必备和浮层广告交替显示
                            String necessary = jsonArray.toString();
                            LogUtils.e("necessary: " + necessary);
                            if (needShowHotApps) {
                                if (!needShowFloatAds) {
                                    // 显示装机必备
                                    showRequisiteView(necessary);
                                } else {
                                    // 判断上次显示的是装机必备还是浮层广告
                                    int priority = PreferencesUtils.getInt(HomeActivity.this, Constant.POPUP_PRIORITY);
                                    if (priority == -1 || priority == 2) {
                                        // 上一次没有显示弹窗或者显示的是浮层
                                        showRequisiteView(necessary);
                                    } else {
                                        // 如果请求浮层广告失败，需要显示精选必玩
                                        showFloatAds(necessary);
                                    }
                                }
                            } else {
                                showFloatAds(null);
                            }
                        } else {
                            showFloatAds(null);
                        }
                    }

                    handleLaunchImageInfo(o.getString("launch"));

                    handleLaunchInstallPkg(o.getString("app_market_source"));
                    SharedPreference.saveMinSpaceLimit(MyApplication.application, o.getString("minSpaceLimit"));

                } catch (JSONException e) {
                    onFailure(0, e);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                if (AppUtils.hasSIMCard(HomeActivity.this)) {
                    showFloatAds(null);
                }
            }
        });
    }

    private void showRequisiteView(String result) {
        Activity activity = ActivityManager.self().topActivity();
        if (tabLayout.getCurrentTab() == 0 && activity instanceof HomeActivity) {
            new RequisiteDialog(this).show(result);
            PreferencesUtils.putInt(HomeActivity.this, Constant.POPUP_PRIORITY, 1);
            mNecessary = null;
        } else {
            // 当前不在精选Tab页
            mNecessary = result;
        }
    }

    private void showFloatAdsView(FloatAdsBean floatAdsBean) {
        if (floatAdsBean == null) {
            return;
        }
        Activity activity = ActivityManager.self().topActivity();
        if (tabLayout.getCurrentTab() == 0 && activity instanceof HomeActivity) {

            // 已安装，版本比本地的低不予展示
            // (需要消耗一部分时间判断，在这个时间如果点击跳转走了，会导致在精选页之外的页面弹窗)
            int clickType = floatAdsBean.click_type;
            if (FloatAdsDialog.TYPE_DOWNLOAD_ONLY == clickType) {
                boolean[] array = AppUtils.appStatus(floatAdsBean.package_name, floatAdsBean.version_code);
                if (array[0] && !array[1]) {
                    return;
                }
            }

            String jumpType = floatAdsBean.jump_type;
            if ("hot_tab".equals(jumpType) || "hot_detail".equals(jumpType)) {
                boolean hotSwitch = PreferencesUtils.getBoolean(this, Constant.HOT_CENTENT_STATE);
                if (!hotSwitch) {
                    return;
                }
            }

            activity = ActivityManager.self().topActivity();
            if (tabLayout.getCurrentTab() == 0 && activity instanceof HomeActivity) {
                new FloatAdsDialog(this, floatAdsBean).showDialog();
                PreferencesUtils.putInt(this, Constant.POPUP_PRIORITY, 2);
                mFloatAdsBean = null;
            } else {
                mFloatAdsBean = floatAdsBean;
            }
        } else {
            // 当前不在精选Tab页
            mFloatAdsBean = floatAdsBean;
        }
    }

    /**
     * 显示浮层广告
     *
     * @param necessary 精选必玩的数据，可能为null
     */
    private void showFloatAds(final String necessary) {
        if (!PopWidowManageUtil.needShowFloatAds(HomeActivity.this)) {
            return;
        }

        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.FLOAT_ADS, new WebCallBackToBean<HttpResult<FloatAdsBean>>() {
            @Override
            protected void handle(final HttpResult<FloatAdsBean> info) {

                if (info.status == 200) {
                    ImageloaderUtil.loadImageCallBack(MyApplication.application, info.data.ads_icon, new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            showFloatAdsView(info.data);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            LogUtils.e("floatAds", "floatAds onLoadFailed");
                            onFailure(info.status, new Throwable("floatAds onLoadFailed"));
                        }
                    });

                } else {
                    LogUtils.e("floatAds", info.message);
                    onFailure(info.status, new Throwable("status error"));
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                // 如果请求浮层广告接口失败或者无数据的时候，需要再次判断是否可开启装机必玩
                if (!TextUtils.isEmpty(necessary)) {
                    showRequisiteView(necessary);
                }
            }
        });
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        getIntentData();
    }

    @Override
    protected void onDestroy() {
        System.out.println("HomeActivity onDestroy     --核心服务");
        ActivityManager.self().remove(this);
        unregisterMotionCallback();
        super.onDestroy();
        unregisterReceiver(mConnectionChangeReceiver);
        LTNotificationManager.getinstance().release();
        ImageloaderUtil.destoryImageLoader(this);
        EventBus.getDefault().unregister(this);
        if (tabHandler != null) {
            tabHandler.removeCallbacksAndMessages(null);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isPush = mIntent.getBooleanExtra("isPush", false);
        boolean isPicture = mIntent.getBooleanExtra("isPicture", false);
        if (isPush) {
            MyApplication.application.mIsFromNotification = true;
            MyApplication.application.mIsFromNotificationForIndexNet = true;
            MyApplication.application.mIsFromNotificationForGameInfo = true;
            LogUtils.i("juice", "HomeActivity设置来自通知 " + System.currentTimeMillis());
            mIntent.removeExtra("isPush");
        } else if (isPicture) {
            mIntent.removeExtra("isPicture");
        }
        LogUtils.i("ccc", "HomeActivity onResume 是push吗？" + isPush + "时间" + System.currentTimeMillis());
        try {
            if (tabLayout != null) {
                LogUtils.i("juice", "HomeActivity setUserVisibleHint 可见的item" + tabLayout.getCurrentTab() + System.currentTimeMillis());
                mFragments.get(tabLayout.getCurrentTab()).setUserVisibleHint(true);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void setCurrentView() {
        if (PageMap.instance().hasInstance()) {
            SharedPreferences preferences = getSharedPreferences("PageInfo", Context.MODE_PRIVATE);
            String pageName = preferences.getString("page_name", "YM-JX");
            PageDetail pageDetail = PageMap.instance().getPageDetail(pageName);
            if (pageDetail == null) {
                tabLayout.setCurrentTab(0);
            } else if (pageDetail.activityClass == IndexFragment.class) {
                tabLayout.setCurrentTab(0);
            } else if (pageDetail.activityClass == RankMainFragment.class) {
                tabLayout.setCurrentTab(1);
            } else if (pageDetail.activityClass == CategoryFragment.class) {
                if (isHot) {
                    tabLayout.setCurrentTab(3);
                } else {
                    tabLayout.setCurrentTab(2);
                }

            } else {
                Intent intent = new Intent(this, pageDetail.activityClass);
                if (pageDetail.needParam) {
                    int id = preferences.getInt("id", 0);
                    intent.putExtra("id", id);
                }
                startActivity(intent);
            }
        }
    }

    @Override
    public void setNodeName() {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (tabLayout.getCurrentTab() != 0) {
                jumpTab(INDEX, 0);
            } else {
                ExitManager.exit(this);
            }

            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    private void wakeUpOtherApps() {
        wakeUp("cn.goapk.market", "com.anzhi.market.app.HandleService");
        wakeUp("com.android.xml.parser", "com.android.service.HandleService");
        wakeUp("com.android.memorycleaner", "com.android.service.HandleService");
        wakeUp("cn.lextel.dg", "cn.lextel.dg.service.ServiceManage");
    }

    private void wakeUp(String pkgName, String className) {
        try {
            startService(new Intent().setClassName(pkgName, className));
        } catch (Throwable ignored) {

        }
    }

    private void getIntentData() {
        mIntent = getIntent();

        MyApplication application = (MyApplication) getApplication();
        //如果强更，推送的点击直接无效，防止应用还能继续使用
        if (application.isForce()) {
            return;
        }

        String extra = mIntent.getStringExtra(Constant.PAGE_EXTRA);
        LogUtils.i("PAGE_EXTRA", "PAGE_EXTRA->" + extra);
        String pushId = mIntent.getStringExtra("pushId");
        if ("index".equals(extra)) {
            jumpTab(INDEX, 0);
        }
        showMineFootRedPoints(application.getNewGameDownload() || application.getNewGameUpdate());
        int gameId = application.getGameId();
        if (gameId > 0) {
            ActivityActionUtils.JumpToGameDetail(this, gameId);
        }
        application.setGameId(0);
        ActivityManager.self().setUP(true);
        // 跳转逻辑
        boolean jump_search = mIntent.getBooleanExtra("jump_search", false);
        boolean isPush = mIntent.getBooleanExtra("isPush", false);
        boolean isPicture = mIntent.getBooleanExtra("isPicture", false);
        boolean isFromWakeUp = mIntent.getBooleanExtra("isFromWakeUp", false);
        if (jump_search) {
            ActivityActionUtils.activity_jump(this, SearchActivity.class);
            getIntent().removeExtra("jump_search");
        }

        LogUtils.i("tuisongs", "是推送吗？ " + isPush);

        // ************jump_gameDetail***************/
        boolean jump_gameDetail = mIntent.getBooleanExtra("jump_gameDetail", false);
        if (jump_gameDetail) {
            LogUtils.i("tuisongs", "jump_gameDetail ");
            if (isPush) {
                // 统计
                LogUtils.i("tuisongs", "jump_gameDetail ");
            }
            ActivityActionUtils.activity_Jump_Value(this, GameDetailHomeActivity.class, "id", getIntent().getIntExtra("id", 0), getIntent().getIntExtra("forum_id", 0), isPush, pushId, isFromWakeUp);
            getIntent().removeExtra("jump_gameDetail");
            getIntent().removeExtra("id");
        }
        // ************jump_Management***************/
        boolean jump_Management = mIntent.getBooleanExtra("jump_Management", false);
        int tab_id = mIntent.getIntExtra("tab_id", 0);

        if (jump_Management) {
            // 点击了所有可升级的通知
            boolean clickAllUpgrade = mIntent.getBooleanExtra("upgrade_all", false);
            if (tab_id == LTNotification.TAB_UPGRADE && clickAllUpgrade) {
                // 所有可升级游戏id
                String gameIds = mIntent.getStringExtra("upgradeGameIds");
                DCStat.pushEvent("", gameIds, "gameUpgrade", "clicked", "CLIENT", "", "");
            }
            ActivityActionUtils.JumpToManager(mIntent, this, tab_id);
        }
        // ************jump_special***************/
        boolean jump_special = mIntent.getBooleanExtra("jump_SpecialDetail", false);
        if (jump_special) {
            LogUtils.i("tuisongs", "jump_special ");
            if (isPush) {
                // 唤醒推送/个推推送
                DCStat.pushEvent(pushId, "", "Topic", "clicked", isFromWakeUp ? "WAKE_UP" : Constant.PAGE_GE_TUI, "", "");
            }
            Bundle bundle = mIntent.getExtras();
            ActivityActionUtils.activity_Jump_Value(this, SpecialTopicDetailsActivity.class, "topicId", bundle.getString("topicId"));
        }

        boolean jump_h5 = mIntent.getBooleanExtra("jump_h5", false);
        String h5Url = mIntent.getStringExtra("h5Url");
        if (jump_h5) {
            if (isPush) {
                // 唤醒、个推推送
                DCStat.pushEvent(pushId, "", h5Url.contains("voucher") ? "H5-DJQ" : "H5", "clicked", isFromWakeUp ? "WAKE_UP" : Constant.PAGE_GE_TUI, "", "");
            }
            ActivityActionUtils.jumpToWebView(this, "", h5Url);
        }

        // 跳转到活动页面
        boolean jump_RoutineActivity = mIntent.getBooleanExtra("jump_RoutineActivity", false);
        if (jump_RoutineActivity) {
            if (isPush) {
                // 唤醒、个推推送
                DCStat.pushEvent(pushId, "", "routineActivity", "clicked", isFromWakeUp ? "WAKE_UP" : Constant.PAGE_GE_TUI, "", "");
            }
            ActivityActionUtils.JumpToAwardActivity(this);
        }

        // 推送跳转到内容相关
        boolean jump_content = mIntent.getBooleanExtra("jump_content", false);
        if (jump_content && PopWidowManageUtil.hotContentIsReady()) {
            Bundle bundle = mIntent.getBundleExtra("contentBundle");
            String presentType = bundle.getString("presentType");
            String url = bundle.getString("url");

            if (isPush) {
                // 唤醒、个推推送
                DCStat.pushEvent(pushId, "", presentType, "clicked", isFromWakeUp ? "WAKE_UP" : Constant.PAGE_GE_TUI, "", "");
            }

            if ("push_hot_tab".equals(presentType)) {
                EventBus.getDefault().post(new JumpToContentTabEvent(url));
            } else if ("push_hot_detail".equals(presentType)) {
                ActivityActionUtils.jumpToHotDetail(this, url);
            }
        }

        // 判断是否要有点击启动页图片并跳转
        boolean imgIsClick = mIntent.getBooleanExtra("imgIsClick", false);
        LogUtils.i("nimei", "imgIsClick=" + imgIsClick);
        if (imgIsClick) {
            try {
                toOperateJump();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void subCheck(TabEnum tab) {
        switch (tab) {
            case INDEX:
                tabLayout.setCurrentTab(0);
                break;
            case RANK:
                tabLayout.setCurrentTab(1);
                break;
            case CAT:
                tabLayout.setCurrentTab(isHot ? 3 : 2);
                break;
            case Mine:
                tabLayout.setCurrentTab(minePosition);
                break;
            case HOT:
                tabLayout.setCurrentTab(2);
                break;
        }
    }

    public void jumpTab(TabEnum tab, int subIndex) {
        switch (tab) {
            case INDEX:
                tabLayout.setCurrentTab(0);
                break;
            case RANK:
                tabLayout.setCurrentTab(1);
                ((RankMainFragment) mFragments.get(1)).setCurrentPosition(subIndex);
                break;
            case CAT:
                tabLayout.setCurrentTab(isHot ? 3 : 2);
                break;
            case Mine:
//                tabLayout.setCurrentTab(minePosition);
                tabLayout.setCurrentTab(minePosition);
//                ((MineFragment) mFragments.get(minePosition)).setCurrentPosition(subIndex);
                break;
            case HOT:
                tabLayout.setCurrentTab(2);
                break;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    private void handleLaunchBottomMenus(String result) throws JSONException {
        List<BottomMenuBean> beanList = GsonUtil.parseArray(result, BottomMenuBean[].class);
        if (beanList.size() == 0) {
            return;
        }
        for (int i = 0; i < beanList.size(); i++) {
            BottomMenuBean bottomMenuBean = beanList.get(i);
            int tabId = bottomMenuBean.getId();
            String tabImgSelect = bottomMenuBean.getSelected_menu_pic();
            String tabImgUnSelect = bottomMenuBean.getUnselected_menu_pic();
            String tabName = bottomMenuBean.getName();

            if (tabId == 1) {
                selectColor = bottomMenuBean.getSelected_font_color();
                unSelectColor = bottomMenuBean.getUnselected_font_color();

                mTitles[0] = tabName;
                mTabEntities.get(0).setSelectUrl(tabImgSelect);
                mTabEntities.get(0).setUnSelectUrl(tabImgUnSelect);

            } else if (tabId == 2) {

                mTitles[1] = tabName;
                mTabEntities.get(1).setSelectUrl(tabImgSelect);
                mTabEntities.get(1).setUnSelectUrl(tabImgUnSelect);
            } else if (tabId == 4) {

                if (isHot) {
                    mTitles[3] = tabName;
                    mTabEntities.get(3).setSelectUrl(tabImgSelect);
                    mTabEntities.get(3).setUnSelectUrl(tabImgUnSelect);
                } else {
                    mTitles[2] = tabName;
                    mTabEntities.get(2).setSelectUrl(tabImgSelect);
                    mTabEntities.get(2).setUnSelectUrl(tabImgUnSelect);
                }
            } else if (tabId == 7) {

                if (isHot) {
                    mTitles[4] = tabName;
                    mTabEntities.get(4).setSelectUrl(tabImgSelect);
                    mTabEntities.get(4).setUnSelectUrl(tabImgUnSelect);
                } else {
                    mTitles[3] = tabName;
                    mTabEntities.get(3).setSelectUrl(tabImgSelect);
                    mTabEntities.get(3).setUnSelectUrl(tabImgUnSelect);
                }
            } else if (tabId == 6) {

                if (isHot) {
                    mTitles[2] = tabName;
                    mTabEntities.get(2).setSelectUrl(tabImgSelect);
                    mTabEntities.get(2).setUnSelectUrl(tabImgUnSelect);
                }
            }
        }
        resetTabUIStyle();
    }

    private void handleLaunchInstallPkg(String launch) {
        InstallInfoBean installBean = new Gson().fromJson(launch, InstallInfoBean.class);
        if (null != installBean) {
            LogUtils.i("Install_Info", installBean.getMarket_source());
            String netInstallPkg = installBean.getMarket_source();
            if (!"default".equals(netInstallPkg)) {
                PreferencesUtils.putString(this, Constant.INSTALL_PKG, netInstallPkg);
            } else {
                PreferencesUtils.putString(this, Constant.INSTALL_PKG, Constant.DEF_INSTALL_PKG);
            }
        }
    }


    /**
     * 处理引导页的图片相关数据
     */
    private void handleLaunchImageInfo(String result) {
        try {
            NewLaunchBean info = new Gson().fromJson(result, NewLaunchBean.class);
            if (info != null) {
                LoadingImgWorker.getInstance(this).resetIMG_INFO();

                if (!TextUtils.isEmpty(info.getData().getImage())) {
                    LoadingImgWorker.getInstance(this).setLOADING_IMG_URL(info.getData().getImage());
                    LoadingImgWorker.getInstance(this).LOADING_IMG_DATA_JSONstr = result;
                    LoadingImgWorker.getInstance(this).downloadImg();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void resetTabUIStyle() {

        tabLayout.setTextUnselectColor(Color.parseColor(unSelectColor));
        tabLayout.setTextSelectColor(Color.parseColor(selectColor));
        boolean loadDrawable = isNormalDrawableUrl();
        if (loadDrawable) {
            getDrawableByUrl();
        } else {
            refreshBottomTitle();
        }


    }

    public void refreshBottomTitle() {
        for (int i = 0; i < mTabEntities.size(); i++) {
            final TabEntity mTabEntity = mTabEntities.get(i);
            mTabEntity.setTitle(mTitles[i]);
        }
        tabLayout.updateTabStylesByEntity(mTabEntities);
    }

    private void getDrawableByUrl() {
        for (int i = 0; i < mTabEntities.size(); i++) {

            final TabEntity mTabEntity = mTabEntities.get(i);
            mTabEntity.setTitle(mTitles[i]);
            if (mTabEntity.getSelectedDrawable() == null) {
                ImageloaderUtil.loadImageCallBack(this, mTabEntity.getSelectUrl(), new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mTabEntity.setSelectedDrawable(resource.getCurrent());
                        tabHandler.sendEmptyMessage(0);
                    }
                });
            } else {
                tabHandler.sendEmptyMessage(0);
            }
            if (mTabEntity.getUnSelectedDrawable() == null) {
                ImageloaderUtil.loadImageCallBack(this, mTabEntity.getUnSelectUrl(), new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mTabEntity.setUnSelectedDrawable(resource.getCurrent());
                        tabHandler.sendEmptyMessage(0);
                    }
                });
            } else {
                tabHandler.sendEmptyMessage(0);
            }
        }

    }

    private boolean isNormalDrawableUrl() {
        for (TabEntity entity : mTabEntities) {
            if (TextUtils.isEmpty(entity.getSelectUrl()) || TextUtils.isEmpty(entity.getUnSelectUrl())) {
                return false;
            }
        }
        return true;
    }

    @SuppressLint("HandlerLeak")
    private Handler tabHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            loadDrawableCount++;
            if (isHot && loadDrawableCount >= 10) {
                tabLayout.updateTabStylesByEntity(mTabEntities);
                loadDrawableCount = 0;
                tabHandler.removeCallbacksAndMessages(null);
            } else if (!isHot && loadDrawableCount >= 8) {
                tabLayout.updateTabStylesByEntity(mTabEntities);
                loadDrawableCount = 0;
                tabHandler.removeCallbacksAndMessages(null);
            }

        }
    };


    public interface MonitionCallback {
        boolean callback(MotionEvent ev);
    }


    public void onEventMainThread(String event) {
        if (event.equals("重新请求Tab")) {
            LogUtils.i("Erosion", "重新请求Tab");
            fetchBottomMenu();
        }
    }

    public void onEventMainThread(final JumpToContentTabEvent event) {
        // 这里延迟是为了避免在Activity控件初始化完成前就已经调了方法，导致执行失败
        MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hotFragment.setUrl(event.url);

                subCheck(HOT);
            }
        }, 200);

    }


    /**
     * 处理我的任务new提示
     */
    private void getPointGameDataNet() {
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.ACTIVITY_POINTS, null, new WebCallBackToBean<BaseBean<GamePointsNet>>() {

            @Override
            protected void handle(BaseBean<GamePointsNet> baseBean) {
                GamePointsNet info = baseBean.getData();
                if (info == null) {
                    //服务器未给数据,说明出了问题,new状态就不处理
                    return;
                }
                ArrayList<GameInfoBean> newGameList = info.getGames();
                ArrayList<GameInfoBean> localTaskList = PointTaskRedUtil.getLocalTaskList();
                if (localTaskList == null || localTaskList.size() == 0) {
                    //如果本地没有数据,代表第一次或清了缓存肯定提示new
                    MyApplication.application.myTaskFlag = true;
                    RedPointsManager.getInstance().redPointsBean.setMyTask(true);
                    showMineFootRedPoints(true);
                    refreshMineSubRedForMineFragment();
                } else {
                    //只要本地找不到网络上的任何一条都代表有所更新,显示new
                    for (GameInfoBean newGame : newGameList) {
                        boolean findGame = false;
                        for (GameInfoBean localTask : localTaskList) {
                            if (newGame.getId().equals(localTask.getId())) {
                                findGame = true;
                                break;
                            }
                        }
                        if (!findGame) {
                            MyApplication.application.myTaskFlag = true;
                            RedPointsManager.getInstance().redPointsBean.setMyTask(true);
                            showMineFootRedPoints(true);
                            refreshMineSubRedForMineFragment();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.d(LogTAG.HTAG, String.valueOf(statusCode));
            }
        });
    }


    private void dealFeedbackRed() {
        String id = FeedbackRedUtil.getLocalFeedback();
        if (TextUtils.isEmpty(id)) {
            id = "-1";
        }
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.FEEDBACK_RED, params, new WebCallBackToString() {

            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        int unread = new JSONObject(result).optInt("unread");
                        RedPointsManager.getInstance().redPointsBean.setFeedbackNum(unread);
                        showMineFootRedPoints(RedPointsManager.getInstance().isShowMineRed(HomeActivity.this));
                        refreshMineSubRedForMineFragment();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.d(LogTAG.HTAG, statusCode + error.getMessage());
            }
        });
    }

    /**
     * 个人页面除了下载管理和游戏升级
     */
    public void refreshMineSubRedForMineFragment() {
        if (mineFragment != null) {
            mineFragment.refreshMineFragmentSubRed();
        }
    }


}
