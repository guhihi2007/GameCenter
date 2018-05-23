package cn.lt.game.ui.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.application.wakeup.WakeUpUserTimer;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.bean.ConfigureBean;
import cn.lt.game.bean.NewLaunchBean;
import cn.lt.game.bean.SpreadBean;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.install.InstalledApp;
import cn.lt.game.lib.PreferencesUtils;
import cn.lt.game.lib.netdata.AnalyzeJson;
import cn.lt.game.lib.util.ActivityManager;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.CodeChangeUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.PopWidowManageUtil;
import cn.lt.game.lib.util.StorageSpaceDetection;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.file.TTGC_DirMgr;
import cn.lt.game.lib.util.image.BitmapUtil;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.util.threadpool.LTAsyncTask;
import cn.lt.game.lib.web.WebCallBackBase;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.lib.widget.DefaultSettingDialog;
import cn.lt.game.lib.widget.PermissionDialog;
import cn.lt.game.model.ToServiceApp;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.NetIniCallBack;
import cn.lt.game.net.Uri2;
import cn.lt.game.service.ScreenMonitorService;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsDataProductorImpl;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.statistics.pageunits.PageMultiUnitsReportManager;
import de.greenrobot.event.EventBus;

import static cn.lt.game.global.Constant.SPREAD_STATUS;


public class LoadingActivity extends BaseActivity implements SplashADListener {

    private SharedPreferences sps;
    private Editor edit;
    private SharedPreferences sharedPreferences;
    private Editor editor;
    private boolean showRedPoint = false;
    private SharedPreferences spp;
    private int xgGgameId = 0;
    private PackageInfo packageInfo;
    private String versionName;
    private ImageView rootIv, mLogo;
    private int netInitTime = 0;// 网络base接口失败次数
    private boolean netInitFinish = false;//
    private SharedPreferences loadingImgSp;// 保存loading图片链接的sharedPreferences
    private boolean imgIsClick = false;

    private ViewGroup container;
    private TextView skipView;
    private SplashAD splashAD;
    private static final String SKIP_TEXT = "点击跳过 %d";
    public boolean canJump = false;
    private static final int REQUEST_CODE_PERMISSION_SD = 100;
    private String imei;

    /** 上报启动页页面浏览*/
    private static final int REPORT_EXPOSURE = 1;

    /** 上报启动页图片点击事件*/
    private static final int REPORT_IMG_CLICK = 2;
//    private boolean phonePermission;
//    private boolean storagePermission;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 保存首次启动游戏中心的时间
        saveFirstStartTime();
        super.onCreate(savedInstanceState);
        MyApplication.isBackGroud = false;

        setContentView(R.layout.loading_splash);
        getRegion();
        loadingImgSp = this.getSharedPreferences("loadingImg", Context.MODE_PRIVATE);
        editor = loadingImgSp.edit();

        int srceenHeight = MyApplication.height;
        mLogo = (ImageView) findViewById(R.id.app_logo);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLogo.getLayoutParams();
        params.height = (int) (srceenHeight * 0.184375);

        container = (ViewGroup) this.findViewById(R.id.splash_container);
        skipView = (TextView) findViewById(R.id.skip_view);
        rootIv = (ImageView) findViewById(R.id.loading_activity_Iv);

        /*phonePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED;
        LogUtils.i("Erosion","phonePermission==+++++++++++" + phonePermission);
        if (!phonePermission) {
            getIMEI();
            LogUtils.i("Erosion","有电话权限===" + MyApplication.imei);
        } else {
            MyApplication.imei = Settings.Secure.getString(LoadingActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
            LogUtils.i("Erosion","没有电话权限===" + MyApplication.imei);
        }*/

        setLoadingImgAndClick();
        /* 初始化网络连接，获取base接口数据 */
        netInstanceInit();
        requestData();
        fetchInstalledGames();
        spp = getSharedPreferences("packverson", 0);
        try {
            packageInfo = getPackageManager().getPackageInfo("cn.lt.game", 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        check();
        /* 创建相应目录 */
//        createDirectory();
        sharedPreferences = this.getSharedPreferences("version", 0);
        int nowVersion = AppUtils.getVersionCode(this);
        editor = sharedPreferences.edit();
        editor.putInt("oldVersion", nowVersion);
        editor.apply();
        sps = getSharedPreferences("configs", 0);
        edit = sps.edit();
//        getIntentData();

          /*
         * 默认一键安装没显示过，退出了客户端但实际application变量未重置，这里只是重置为false
         * 需求是每次重新启动之后，满足了一键安装条件必须弹，在应用期间只弹一次
        */
        MyApplication.castFrom(this).setHadShowOneKeyDialog(false);
    }

    /*public void getIMEI() {
        imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        if (TextUtils.isEmpty(imei)) {
            imei = Settings.Secure.getString(LoadingActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        MyApplication.imei = imei;
    }*/

    private void check() {
        /*storagePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
        LogUtils.i("Erosion","存储权限===" + storagePermission);*/

        final boolean isPhonePermission = AndPermission.hasPermission(LoadingActivity.this,"android.permission.READ_PHONE_STATE");
        final boolean isStoragePermission = AndPermission.hasPermission(LoadingActivity.this,"android.permission.WRITE_EXTERNAL_STORAGE");
        LogUtils.i("Erosion","存储权限===" + isPhonePermission  + "，电话权限＝＝＝" + isStoragePermission);

        if (isStoragePermission && isPhonePermission) {
            getIntentData();
            createDirectory();
            LogUtils.i("Erosion", "木有这个权限");
        } else {
            AndPermission.with(this).requestCode(REQUEST_CODE_PERMISSION_SD)
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
                    .rationale(new RationaleListener() {
                        @Override
                        public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
//                            AndPermission.rationaleDialog(LoadingActivity.this, rationale).show();
                            LogUtils.i("Erosion","showRequestPermissionRationale");
                            String msg = "";
                            if (!isStoragePermission) {
                                msg = "存储权限";
                            }

                            if (!isPhonePermission) {
                                msg = "电话权限";
                            }

                            if (!isStoragePermission && !isPhonePermission) {
                                msg = "存储权限和电话权限";
                            }
                            PermissionDialog dialog = new PermissionDialog(LoadingActivity.this,rationale,msg);
                            dialog.show();
                        }
                    })
                    .callback(listener)
                    .start();

            LogUtils.i("Erosion", "有这个权限");
        }
    }

    private PermissionListener listener = new PermissionListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            LogUtils.i("Erosion", "获取权限成功===" + requestCode);
            // Successfully.
            if (requestCode == 100) {
                // TODO ...
                final boolean isStoragePermission = AndPermission.hasPermission(LoadingActivity.this,"android.permission.WRITE_EXTERNAL_STORAGE");

                if (!isStoragePermission) {
                    // 第一种：用AndPermission默认的提示语。
//                    AndPermission.defaultSettingDialog(LoadingActivity.this, 400).show();
                    DefaultSettingDialog dialog = new DefaultSettingDialog(LoadingActivity.this,400);
                    dialog.show();
                } else {
                    MyApplication.imei = AppUtils.getIMEI(LoadingActivity.this);
                    createDirectory();
                    getIntentData();
                }


            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            LogUtils.i("Erosion", "获取权限===" + deniedPermissions.toString());

            // Failure.
            if (requestCode == 100) {
                // TODO ...

                if (AndPermission.hasAlwaysDeniedPermission(LoadingActivity.this, deniedPermissions)) {
                    // 第一种：用AndPermission默认的提示语。
//                    AndPermission.defaultSettingDialog(LoadingActivity.this, 400).show();
                    DefaultSettingDialog dialog = new DefaultSettingDialog(LoadingActivity.this,400);
                    dialog.show();
                    return;
                }

                final boolean isPhonePermission = AndPermission.hasPermission(LoadingActivity.this,"android.permission.READ_PHONE_STATE");
                final boolean isStoragePermission = AndPermission.hasPermission(LoadingActivity.this,"android.permission.WRITE_EXTERNAL_STORAGE");

                if (!isPhonePermission && isStoragePermission) {
                    createDirectory();
                    getIntentData();
                } else {
                    AndPermission.with(LoadingActivity.this).requestCode(REQUEST_CODE_PERMISSION_SD)
                            .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
                            .rationale(new RationaleListener() {
                                @Override
                                public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
//                                AndPermission.rationaleDialog(LoadingActivity.this, rationale).show();
                                    String msg = "";

                                    if (!isStoragePermission) {
                                        msg = "存储权限";
                                    }

                                    if (!isPhonePermission) {
                                        msg = "电话权限";
                                    }

                                    if (!isStoragePermission && !isPhonePermission) {
                                        msg = "存储权限和电话权限";
                                    }

                                    PermissionDialog dialog = new PermissionDialog(LoadingActivity.this,rationale,msg);
                                    dialog.show();
                                }
                            })
                            .callback(listener)
                            .start();
                }

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean isStoragePermission = AndPermission.hasPermission(LoadingActivity.this,"android.permission.WRITE_EXTERNAL_STORAGE");
        switch (requestCode) {
            case 400:
                if (!isStoragePermission) {
                    // 第一种：用AndPermission默认的提示语。
//                    AndPermission.defaultSettingDialog(LoadingActivity.this, 400).show();
                    DefaultSettingDialog dialog = new DefaultSettingDialog(LoadingActivity.this,400);
                    dialog.show();
                } else {
                    createDirectory();
                    getIntentData();
                }
                break;
        }
    }

    /**
     * 设置启动页图片和处理点击事件
     */
    private void setLoadingImgAndClick() {
        String md5 = loadingImgSp.getString("img_md5", "");
        String path = TTGC_DirMgr.getCachePicDirectory() + File.separator + md5;
        File file = new File(path);

        if (rootIv != null) {
            if (!TextUtils.isEmpty(md5) && file.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    Bitmap bm = BitmapFactory.decodeStream(fis);
                    rootIv.setImageBitmap(bm);
                    fis.close();
                    fis = null;
                    setImgListener();
                } catch (Exception e) {
                    rootIv.setImageBitmap(BitmapUtil.decodeResource(this.getResources(), R.mipmap.loading_yxzx, Utils.getScreenWidth(this), Utils.getScreenHeight(this)));
                    e.printStackTrace();
                }
            } else if (getResources().getString(R.string.app_name).equals("游戏中心")) {
                rootIv.setImageBitmap(BitmapUtil.decodeResource(this.getResources(), R.mipmap.loading_yxzx, Utils.getScreenWidth(this), Utils.getScreenHeight(this)));
            } else {
                rootIv.setImageBitmap(BitmapUtil.decodeResource(this.getResources(), R.mipmap.loading_ttyxzx, Utils.getScreenWidth(this), Utils.getScreenHeight(this)));
            }
        }

    }

    private void fetchInstalledGames() {
        final List<ToServiceApp> installedPkgNames = InstalledApp.getUserPkgName(MyApplication.application);
        if (installedPkgNames.size() != 0) {
            // 根据找到本地的app去拼接json
            final Gson gson = new Gson();
            String data = gson.toJson(installedPkgNames);
            Map<String, String> params = new HashMap<>();
            params.put("data", data);
            Net.instance().executePost(Host.HostType.GCENTER_HOST, Uri2.GAME_MANAGER_URI, params, new WebCallBackToString() {
                @Override
                public void onFailure(int statusCode, Throwable error) {

                }

                @Override
                public void onSuccess(String result) {
                    // 记录上次调用接口得时间
                    MyApplication.application.getSharedPreferences("setting", Context.MODE_PRIVATE).edit().putLong(ScreenMonitorService.GAME_SYNC, System.currentTimeMillis()).apply();
                    UIModuleList list = AnalyzeJson.analyzeJson(result);
                    ScreenMonitorService.handleLocalGameSyncResult(list, true);
                }
            });

        }
    }

    private void requestData() {
        MyApplication.application.switchIsReady = false;

        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.POPUP_INTERVAL_URI, null, new WebCallBackToObject<UIModuleList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                PreferencesUtils.putBoolean(LoadingActivity.this,Constant.HOT_CENTENT_STATE,false);
            }

            @Override
            protected void handle(UIModuleList moduleList) {
                UIModuleList list = moduleList;
                if (list != null && list.size() > 0) {
                    UIModule module = (UIModule) list.get(0);
                    ConfigureBean info = (ConfigureBean) module.getData();
                    if (null != info) {
                        LogUtils.i("LoadingActivity", "弹窗管理请求成功");
                        try {
                            PopWidowManageUtil.savePopWindowInfo(LoadingActivity.this, info);
                            MyApplication.application.switchIsReady = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            PreferencesUtils.putBoolean(LoadingActivity.this,Constant.HOT_CENTENT_STATE,false);
                            LogUtils.i("LoadingActivity", "printStackTrace");
                        }
                    } else {
                        PreferencesUtils.putBoolean(LoadingActivity.this,Constant.HOT_CENTENT_STATE,false);
                        LogUtils.i("LoadingActivity", "弹窗管理请求失败");
                    }
                } else {
                    PreferencesUtils.putBoolean(LoadingActivity.this,Constant.HOT_CENTENT_STATE,false);
                }
            }
        });
    }

    /***
     * 请求开屏广告信息
     */
    private void requestAdsData(Context context) {

        if (PreferencesUtils.getBoolean(context, SPREAD_STATUS, false)) {
            MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i("SplashPosID", "SplashPosID=" + Constant.SplashPosID);
                    if (imgIsClick) {
                        LogUtils.i("Splash", "已经点击过不再加载广告");
                        goHomeActivity();
                    } else {
                        Log.i("Splash", "没点击过加载广告");
                        splashAD = new SplashAD(LoadingActivity.this, container, skipView, Constant.APPID, Constant.SplashPosID, LoadingActivity.this, 0);
                    }
                }
            }, 1000);
        } else {
            Log.i("Splash", "广点通已关闭");
            goHomeActivity();
        }
    }

    private void goHomeActivity() {
        MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JudgeStartActivity();
            }
        }, 2000);
    }

    private void setImgListener() {
        if (rootIv != null) {
            rootIv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.i(LogTAG.LoadingImgTAG, "图片被点击啦");

                    // 上报点击事件（控制多次点击只报一次）
                    if (!imgIsClick) {
                        reportLoadingImgData(REPORT_IMG_CLICK);
                    }

                    imgIsClick = true;
                }
            });
        }

    }

    /* 初始化网络连接，获取base接口数据 */
    private void netInstanceInit() {
        Net.instance().init(LoadingActivity.this, new NetIniCallBack() {
            @Override
            public void callback(int code) {
                if (!LoadingActivity.this.isFinishing()) {
                    if (code == 0) {
                        // 网络初始化成功
                        netInitFinish = true;
                    } else {
                        // 如果base接口失败，重新获取三次。
                        netInitTime++;
                        if (netInitTime < 3) {
                            netInstanceInit();
                        }

                    }
                }
            }
        });
    }

    private void JudgeStartActivity() {
        // 进入主界面
        boolean isHot = PreferencesUtils.getBoolean(this,Constant.HOT_CENTENT_STATE,false);
        LogUtils.i("Erosion","ishot===" + isHot);

        if (sps.getBoolean("is_first", false) && spp.getString("packverson", "1.0").equals(versionName)) {
            edit.putBoolean("is_first", true).commit();
            Intent localIntent = new Intent(LoadingActivity.this, HomeActivity.class);
            localIntent.putExtra("showRedPoint", showRedPoint);
            localIntent.putExtra("is_hot", isHot);
            localIntent.putExtra("imgIsClick", imgIsClick);
            // 这行代码的作用是保持HomeActivity唯一。防止推送启动Activity时启动多个HomeActivity
            localIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(localIntent);
            overridePendingTransition(R.anim.loading_image_push_left_in, R.anim.loading_image_push_left_out);
            LoadingActivity.this.finish();

        } else {
            // 进入引导界面
            sps.edit().putBoolean("is_first", true).commit();
            spp.edit().putString("packverson", versionName).commit();
            // 判断是不是小米系统
            String sysProp = NetUtils.getSystemProperty("ro.miui.ui.version.name");
            if (sysProp != null && sysProp.equals("")) {
            }
            Intent localIntent = new Intent(LoadingActivity.this, HomeActivity.class);
            localIntent.putExtra("showRedPoint", showRedPoint);
            // 这行代码的作用是保持HomeActivity唯一。防止推送启动Activity时启动多个HomeActivity
            localIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            localIntent.putExtra("imgIsClick", imgIsClick);
            localIntent.putExtra("is_hot", isHot);
            startActivity(localIntent);
            overridePendingTransition(R.anim.loading_image_push_left_in, R.anim.loading_image_push_left_out);
            LoadingActivity.this.finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /* 创建游戏中心所需的文件夹 */
    private void createDirectory() {

        new LTAsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // 初始化项目目录管理配置
                TTGC_DirMgr.init();
                TTGC_DirMgr.makeDirs();
                return null;
            }
        }.execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            next();
        }
        canJump = true;
        reportLoadingImgData(REPORT_EXPOSURE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    public void getIntentData() {

        Intent intent = getIntent();
        boolean jump_search = intent.getBooleanExtra("jump_search", false);

        boolean jump_h5 = intent.getBooleanExtra("jump_h5", false);

        boolean jump_gameDetail = intent.getBooleanExtra("jump_gameDetail", false);

        boolean jump_Management = intent.getBooleanExtra("jump_Management", false);

        boolean jump_special = intent.getBooleanExtra("jump_SpecialDetail", false);

        boolean jump_RoutineActivity = intent.getBooleanExtra("jump_RoutineActivity", false);

        boolean jump_content = intent.getBooleanExtra("jump_content", false);

        boolean isPicture = intent.getBooleanExtra("isPicture", false);
        boolean isPush = intent.getBooleanExtra("isPush", false);
        boolean isFromWakeUp = intent.getBooleanExtra("isFromWakeUp", false);  //传到HomeActivity
        String h5URL = intent.getStringExtra("h5Url");
        String pushId = intent.getStringExtra("pushId");

        boolean isHot = PreferencesUtils.getBoolean(this,Constant.HOT_CENTENT_STATE,false);

        if (jump_search || jump_gameDetail || jump_Management || jump_h5 || jump_special) {
            MyApplication.application.mIsFromNotification = true;
            MyApplication.application.mIsFromNotificationForIndexNet = true;
            MyApplication.application.mIsFromNotificationForGameInfo = true;
            LogUtils.i("juice", "LoadingActivity设置来自通知 " + System.currentTimeMillis());
        }
        if (jump_search) {
            LogUtils.i("LoadingActivityIntent", "jump_search");
            Intent localIntent = new Intent(LoadingActivity.this, HomeActivity.class);
            localIntent.putExtra("showRedPoint", showRedPoint);
            localIntent.putExtra("jump_search", true);
            localIntent.putExtra("pushId", pushId);
            // 这行代码的作用是保持HomeActivity唯一。防止推送启动Activity时启动多个HomeActivity
            localIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            localIntent.putExtra("is_hot", isHot);
            startActivity(localIntent);
            intent.removeExtra("jump_search");

            overridePendingTransition(R.anim.loading_image_push_left_in, R.anim.loading_image_push_left_out);
            LoadingActivity.this.finish();
        } else if (jump_gameDetail) {
            LogUtils.i("LoadingActivityIntent", "jump_gameDetail");
            Intent localIntent = new Intent(LoadingActivity.this, HomeActivity.class);
            localIntent.putExtra("showRedPoint", showRedPoint);
            localIntent.putExtra("jump_gameDetail", true);
            localIntent.putExtra("id", getIntent().getIntExtra("id", -1));
            localIntent.putExtra("isPicture", isPicture);
            localIntent.putExtra("isPush", isPush);
            localIntent.putExtra("pushId", pushId);
            localIntent.putExtra("isFromWakeUp", isFromWakeUp);
            localIntent.putExtra("is_hot", isHot);
            // 这行代码的作用是保持HomeActivity唯一。防止推送启动Activity时启动多个HomeActivity
            localIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(localIntent);
            intent.removeExtra("jump_gameDetail");
            intent.removeExtra("id");
            intent.removeExtra("isPicture");
            intent.removeExtra("isPush");
            intent.removeExtra("pushId");
            intent.removeExtra("isFromWakeUp");

            overridePendingTransition(R.anim.loading_image_push_left_in, R.anim.loading_image_push_left_out);
            LoadingActivity.this.finish();
        } else if (jump_Management) {
            LogUtils.i("LoadingActivityIntent", "jump_Management");
            boolean isNotif = getIntent().getBooleanExtra("isNotif", false);
            boolean isLook = getIntent().getBooleanExtra("isLook", false);
            boolean clickRetry = getIntent().getBooleanExtra("clickRetry", false);
            boolean isUpGradeAll = getIntent().getBooleanExtra("upgrade_all", false);

            // 如果是点击通知栏，下载重试的，需上报数据
            if (clickRetry) {
                if (StorageSpaceDetection.check(true, null, false)) {
                    FileDownloaders.autoStartDownload();
                }
            }
            int tab_id = getIntent().getIntExtra("tab_id", -1);
            Intent localIntent = new Intent(LoadingActivity.this, HomeActivity.class);
            localIntent.putExtra("showRedPoint", showRedPoint);
            localIntent.putExtra("jump_Management", true);
            localIntent.putExtra("tab_id", tab_id);
            localIntent.putExtra("isNotif", isNotif);
            localIntent.putExtra("isLook", isLook);
            localIntent.putExtra("isPicture", isPicture);
            localIntent.putExtra("upgrade_all", isUpGradeAll);
            localIntent.putExtra("isFromWakeUp", isFromWakeUp);
            localIntent.putExtra("upgradeGameIds", getIntent().getStringExtra("upgradeGameIds"));
            localIntent.putExtra("is_hot", isHot);
            // 这行代码的作用是保持HomeActivity唯一。防止推送启动Activity时启动多个HomeActivity
            localIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(localIntent);
            intent.removeExtra("jump_Management");
            intent.removeExtra("isNotif");
            intent.removeExtra("tab_id");
            intent.removeExtra("isPicture");
            intent.removeExtra("isLook");
            intent.removeExtra("clickRetry");
            intent.removeExtra("isFromWakeUp");
            intent.removeExtra("upgradeGameIds");
            intent.removeExtra("upgrade_all");

            overridePendingTransition(R.anim.loading_image_push_left_in, R.anim.loading_image_push_left_out);
            LoadingActivity.this.finish();

        } else if (jump_h5) {
            LogUtils.i("LoadingActivityIntent", "H5");
            Intent localIntent = new Intent(LoadingActivity.this, HomeActivity.class);
            localIntent.putExtra("isFromWakeUp", isFromWakeUp);
            localIntent.putExtra("jump_h5", true);
            localIntent.putExtra("isPush", isPush);
            localIntent.putExtra("pushId", pushId);
            localIntent.putExtra("h5Url", h5URL);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            localIntent.putExtra("is_hot", isHot);
            startActivity(localIntent);
            overridePendingTransition(R.anim.loading_image_push_left_in, R.anim.loading_image_push_left_out);
            LoadingActivity.this.finish();
        } else if (jump_special) {
            LogUtils.i("LoadingActivityIntent", "jump_special");
            Bundle bundle = intent.getExtras();
            Intent localIntent = new Intent(LoadingActivity.this, HomeActivity.class);
            localIntent.putExtra("isFromWakeUp", isFromWakeUp);
            localIntent.putExtra("isPush", isPush);
            localIntent.putExtra("isPicture", isPicture);
            localIntent.putExtra("pushId", pushId);
            localIntent.putExtras(bundle);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            localIntent.putExtra("is_hot", isHot);
            startActivity(localIntent);
            intent.removeExtra("jump_SpecialDetail");
            intent.removeExtra("isPush");
            intent.removeExtra("isPicture");
            intent.removeExtra("isFromWakeUp");

            overridePendingTransition(R.anim.loading_image_push_left_in, R.anim.loading_image_push_left_out);
            LoadingActivity.this.finish();

        } else if (jump_RoutineActivity) {
            LogUtils.i("LoadingActivityIntent", "routineActivity");
            Intent localIntent = new Intent(LoadingActivity.this, HomeActivity.class);
            localIntent.putExtra("isFromWakeUp", isFromWakeUp);
            localIntent.putExtra("jump_RoutineActivity", true);
            localIntent.putExtra("isPush", isPush);
            localIntent.putExtra("pushId", pushId);
            localIntent.putExtra("is_hot", isHot);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(localIntent);
            overridePendingTransition(R.anim.loading_image_push_left_in, R.anim.loading_image_push_left_out);
            LoadingActivity.this.finish();
        } else if (jump_content) {
            Intent localIntent = new Intent(LoadingActivity.this, HomeActivity.class);
            localIntent.putExtra("isFromWakeUp", isFromWakeUp);
            localIntent.putExtra("jump_content", true);
            localIntent.putExtra("isPush", isPush);
            localIntent.putExtra("pushId", pushId);
            localIntent.putExtra("contentBundle", intent.getBundleExtra("contentBundle"));
            localIntent.putExtra("is_hot", isHot);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(localIntent);
            overridePendingTransition(R.anim.loading_image_push_left_in, R.anim.loading_image_push_left_out);
            LogUtils.i(LogTAG.PushTAG, "jump_content");
            LoadingActivity.this.finish();
        } else {
            LogUtils.i("LoadingActivityIntent", "正常进入主界面");
            if (xgGgameId > 0) {
                LogUtils.i("LoadingActivityIntent", "xgGgameId > 0--》JudgeStartActivity");
                ((MyApplication) (LoadingActivity.this.getApplication())).setGameId(xgGgameId);
                JudgeStartActivity();// 判断进入主界面还是引导页面
            } else {
                LogUtils.i("LoadingActivityIntent", "没有需要传递的信息，直接进入主页面");
                ((MyApplication) (LoadingActivity.this.getApplication())).setGameId(0);
                requestAdsData(this);
            }
        }

    }


    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_LOADING);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // 系统4.4以上才有效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // enable status bar tint
            getTintManager().setStatusBarTintEnabled(false);

            ActivityManager.getRootView(this).setFitsSystemWindows(false);
        }
    }

    /**
     * 保存首次启动游戏中心的时间
     */
    private void saveFirstStartTime() {
        LogUtils.i(LogTAG.wakeUpUser, "(saveFirstStartTime)");
        long firstStartTime = WakeUpUserTimer.getFirstStartTime(this);
        if (firstStartTime == 0) {
            //honaf记录第一次打开app
            MyApplication.firstOpen = true;
            long time = System.currentTimeMillis();
            WakeUpUserTimer.saveFirstStartTime(this, time);
            LogUtils.i(LogTAG.wakeUpUser, "(saveFirstStartTime) = " + time);
        }
    }


    @Override
    public void onADDismissed() {
        LogUtils.i("AD_DEMO", "SplashADDismissed");
        next();
    }

    @Override
    public void onNoAD(int i) {
        LogUtils.i("AD_DEMO", "LoadSplashADFail, eCode=" + i);
//        showBackgroundImage();
        JudgeStartActivity();
        DCStat.adsSpreadEvent("noAD", "GDT", "splash");

    }

    @Override
    public void onADPresent() {
        LogUtils.i("AD_DEMO", "SplashADPresent");
        DCStat.adsSpreadEvent("adPresent", "GDT", "splash");
        skipView.setVisibility(View.VISIBLE);
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(1000);
        if (container != null) {
            container.startAnimation(animation);
        }
    }

    @Override
    public void onADClicked() {
        DCStat.adsSpreadEvent("adClicked", "GDT", "splash");
        LogUtils.i("AD_DEMO", "SplashADClicked");
    }

    @Override
    public void onADTick(long l) {
        LogUtils.i("AD_DEMO", "SplashADTick " + l + "ms");
        skipView.setText(String.format(SKIP_TEXT, Math.round(l / 1000f)));
    }

    private void next() {
        if (canJump) {
            JudgeStartActivity();
        } else {
            canJump = true;
        }
    }

    /**
     * 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /** 上报启动页图片相关数据（页面浏览、图片点击事件）*/
    private synchronized void reportLoadingImgData(int what) {
        NewLaunchBean bean = new Gson().fromJson(LoadingImgWorker.getInstance(LoadingActivity.this).getLoadingImgDataJsonStr(), NewLaunchBean.class);
        String title = "";
        String clickType = "first";
        int id = 0;
        String url = "";
        String gamePkgName = "";

        if (bean != null) {
            clickType = bean.getClick_type();

            title = bean.getData().getTitle();
            title = TextUtils.isEmpty(title) ? "default" : title;

            if (clickType.equals("game")) {
                id = Integer.parseInt(bean.getData().getGame().getId());
                gamePkgName = bean.getData().getGame().getPackage_name();
            } else {
                id = bean.getData().getId();
            }

            if (!TextUtils.isEmpty(bean.getData().getUrl())) {
                url = bean.getData().getUrl();
            }
        }

        switch (what) {
            // 上报页面浏览
            case REPORT_EXPOSURE:
                try {
                    PageMultiUnitsReportManager.buildlLoadingDataEvent(title, "loading_page", clickType, id, url, Constant.PAGE_LOADING, gamePkgName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            // 上报启动页图片点击事件
            case REPORT_IMG_CLICK:
                StatisticsEventData sData = StatisticsDataProductorImpl.produceStatisticsData("loading_page", 0, 0, String.valueOf(id), getPageAlias(), ReportEvent.ACTION_CLICK, null, url, clickType, "", gamePkgName);
                DCStat.clickEvent(sData);
                break;
        }
    }

    /**
     * 获得用户地址
     */
    private void getRegion() {
//        http://submit.apnic.net/cgi-bin/jwhois.pl   //备用，需要流解析
//        http://ip.taobao.com/service/getIpInfo2.php?ip=myip
        Net.instance().executeGet(Uri2.TAOBAO_CITY, null, new WebCallBackBase() {
            @Override
            public void route(String result) {
                LogUtils.e("region", "route success"+result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String code = null;
                    code = jsonObject.getString("code");
                    if (code.equals("0")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String IP = data.getString("ip");
                        String city = data.getString("city");
                        String region = data.getString("region");
                        String country = data.getString("country");
                        LogUtils.d("region", "您的IP地址是=>" + IP+"您的城市是=>" + city+"=省份=>" + region+"国家=>" + country);
                        String unicode_city= CodeChangeUtil.stringToUnicode(city);
                        String unicode_region= CodeChangeUtil.stringToUnicode(region);
                        String unicode_country= CodeChangeUtil.stringToUnicode(country);
                        LogUtils.d("region", "转hui=unicode_region:" +unicode_region+"转hui=unicode_city:" +unicode_city );
                        MyApplication.application.country=unicode_country;
                        MyApplication.application.region=unicode_region;
                        MyApplication.application.city=unicode_city;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.e("region", "route onFailure");
            }
        });
    }
}
