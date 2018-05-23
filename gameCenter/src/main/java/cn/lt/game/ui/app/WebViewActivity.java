package cn.lt.game.ui.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.db.service.DownFileService;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.detail.GameDomainDetail;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.download.H5DownloadState;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.install.ApkInstallManger;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.H5Util;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.OpenAppUtil;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.lib.util.StorageSpaceDetection;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.file.TTGC_DirMgr;
import cn.lt.game.lib.util.log.Logger;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.view.X5WebView;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.community.model.ShareBean;
import cn.lt.game.ui.app.community.widget.ShareDialog;
import cn.lt.game.ui.app.personalcenter.BindPhoneActivity;
import cn.lt.game.ui.app.personalcenter.PersonalCenterActivity;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;
import de.greenrobot.event.EventBus;

/**
 * web视图activity
 */
public class WebViewActivity extends BaseActivity {

    private TitleBarView actionbar;
    private X5WebView webview;
    private NetWorkStateView netWorkStateView;
    private String gotoUrl;
    private String mShortGotoUrl;
    private String voucherType = "";

    public String getmShortGotoUrl() {
        return mShortGotoUrl;
    }

    public void setmShortGotoUrl(String gotoUrl) {
        if (gotoUrl.contains("?")) {
            this.mShortGotoUrl = gotoUrl.substring(0, gotoUrl.indexOf("?"));
        } else {
            this.mShortGotoUrl = gotoUrl;
        }
    }

    //H5传送的包名json串
    private String packageNameData;
    private Context mContext;

    //请求详情后的eventbus是否需要去下载，默认需要（原因该页面和详情页发送了同样的eventbus请求）
    @SuppressWarnings("unused")
    private Boolean isDeal = true;
    private String tabId;
    private String voucherId;

    private static final String JS_NAME = "ttigame";
    private Intent intent;
    private boolean isLoaded = false;
    CopyOnWriteArrayList<GameBaseDetail> allUpGradeFileInfo = new CopyOnWriteArrayList<>();
    CopyOnWriteArrayList<GameBaseDetail> allInstalledDownFileInfo = new CopyOnWriteArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        mContext = this;
        intent = getIntent();
        EventBus.getDefault().register(this);
        initialize();
    }

    @Override
    public void setPageAlias() {
        gotoUrl = getIntent().getStringExtra("gotoUrl");
        if (gotoUrl.contains("?")) {
            String params = gotoUrl.substring(gotoUrl.indexOf("?"),gotoUrl.length());
            if (params.contains("voucher")) {
                voucherId = gotoUrl.substring(gotoUrl.indexOf("=") + 1,  gotoUrl.indexOf("&"));
                setTabUrl(voucherId);
                setmPageAlias(cn.lt.game.global.Constant.PAGE_H5 + "-DJQ", "");
            } else {
                setmPageAlias(cn.lt.game.global.Constant.PAGE_H5, tabId);
                setTabUrl(gotoUrl);
            }
        }
    }

    private void initialize() {
        Uri uri = intent.getData();
        if (uri != null) {
            gotoUrl = uri.getQueryParameter("gotoUrl");
        } else {
            gotoUrl = intent.getStringExtra("gotoUrl");
            tabId = intent.getStringExtra("tabId");
        }
        if (gotoUrl.contains("voucher")) {
            voucherType = "voucher";
        }
        LogUtils.i("Erosion","gotoUrl===" + gotoUrl);
        LogUtils.d(LogTAG.HTAG, "gotoUrl=>" + gotoUrl);
        //初始化
        netWorkStateView = (NetWorkStateView) findViewById(R.id.netWrokStateView);
        netWorkStateView.setRetryCallBack(new NetWorkStateView.RetryCallBack() {
            @Override
            public void retry() {
                loadWebview();
            }
        });
        //设置标题
        actionbar = (TitleBarView) findViewById(R.id.actionbar);
        actionbar.setBackHomeVisibility(View.INVISIBLE);
        actionbar.setTitle("加载中...");

        webview = (X5WebView) findViewById(R.id.full_web_webview);

        JavascriptInterface mJavascriptInterface = new JavascriptInterface();
        webview.addJavascriptInterface(mJavascriptInterface, JS_NAME);

        webview.getSettings().setAppCacheMaxSize(5 * 1024 * 1024);
        webview.getSettings().setAppCachePath(getCacheDir().getAbsolutePath());
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        loadWebview();
    }

    public void loadWebview() {
        if (!NetUtils.isConnected(mContext)) {
            netWorkStateView.showNetworkFailLayout();
            actionbar.setTitle("加载失败");
            return;
        }
        netWorkStateView.showLoadingBar();

        webview.setWebChromeClient(new com.tencent.smtt.sdk.WebChromeClient() {
            @Override
            public void onReceivedTitle(com.tencent.smtt.sdk.WebView webView, String s) {
                if (!TextUtils.isEmpty(gotoUrl)) {
                    actionbar.setTitle(s);
                } else {
                    actionbar.setTitle("错误页面");
                }
                super.onReceivedTitle(webView, s);

            }
        });

        final long t1 = System.currentTimeMillis();
        webview.setWebViewClient(new com.tencent.smtt.sdk.WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView webView, String s) {
                String httpHead = s.substring(0, 5);
                if (httpHead.equals("http:") || httpHead.equals("https")) {
                    webView.loadUrl(filterAddClientInfoParamForV4(s));
                    return false;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setClassName("com.android.browser",
                            "com.android.browser.BrowserActivity");
                    intent.setData(Uri.parse(s));
                    mContext.startActivity(intent);
                    return true;
                }
            }

            @Override
            public void onPageStarted(com.tencent.smtt.sdk.WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                netWorkStateView.showLoadingBar();
            }

            @Override
            public void onLoadResource(com.tencent.smtt.sdk.WebView webView, String s) {
                super.onLoadResource(webView, s);
            }

            @Override
            public void onPageFinished(com.tencent.smtt.sdk.WebView webView, String s) {
                super.onPageFinished(webView, s);
                LogUtils.i("GOOD", "营销H5页面加载耗时：" + (System.currentTimeMillis() - t1));
                netWorkStateView.hideLoadingBar();
                netWorkStateView.setVisibility(View.GONE);
                isLoaded = true;
            }
        });

        //跳转到URL
        if (!TextUtils.isEmpty(gotoUrl)) {
            gotoUrl = filterAddClientInfoParamForV4(gotoUrl);
            Map<String, String> header = new HashMap<>();
            header.put("color", "normal");
            webview.loadUrl(gotoUrl,header);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        destroyWebView();
    }

    public void destroyWebView() {
        webview.removeAllViews();
        if (webview != null) {
            webview.destroy();
            webview = null;
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 请求游戏详情信息
     */
    private void requestGameDetailData(int gameId, final int status) {
        if (FileDownloaders.couldDownload(mContext)) {
            Map<String, String> param = new HashMap<>();
            param.put("id", gameId + "");
            Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.getGameDetailUriByIdOrPkgName(gameId + ""), param, new WebCallBackToObject<UIModuleList>() {
                /**
                 * 网络请求出错时调用
                 *
                 * @param statusCode 异常编号
                 * @param error      异常信息
                 */
                @Override
                public void onFailure(int statusCode, Throwable error) {
                    ToastUtils.showToast(mContext, "游戏不存在或已下架");
                }

                @Override
                protected void handle(UIModuleList uiData) {
                    UIModule uiModule = (UIModule) uiData.get(0);
                    GameDomainDetail domainDetail = (GameDomainDetail) uiModule.getData();
                    GameBaseDetail game = new GameBaseDetail();// 存放游戏信息
                    game.setGameDetail(domainDetail);
                    if (TextUtils.isEmpty(game.getDownUrl())) {
                        ToastUtils.showToast(mContext, "下载地址不存在");
                        DCStat.downloadFialedEvent(game, "下载地址不存在");
                    } else {
                        if (status == H5DownloadState.UPGRADE) {
                            game.setPrevState(InstallState.upgrade);
                            game.setState(InstallState.upgrade);
                        }
                        checkDownload(game);
                    }
                }
            });
        } else {
            ToastUtils.showToast(mContext, "请检查网络！");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    MessageDialog tipDialog = null;

    public void checkDownload(final GameBaseDetail game) {
        setmShortGotoUrl(gotoUrl);
        if (NetUtils.isWifiNet(mContext)) {
            startDownload(game, getPageAlias() + "_" + getmShortGotoUrl(), Constant.MODE_SINGLE);
        } else if (NetUtils.isMobileNet(mContext)) {
            if (tipDialog == null) {
                tipDialog = new MessageDialog(this, "温馨提示", "当前处于2G/3G/4G环境,下载将消耗流量,是否继续下载?", getResources().getString(R.string.cancel_ignor_bt), "下载");
            }
            tipDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {

                @Override
                public void OnClick(View view) {
                    startDownload(game, getPageAlias() + "_" + getmShortGotoUrl(), Constant.MODE_SINGLE);
                }
            });
            tipDialog.show();
        } else {
            ToastUtils.showToast(mContext, "请检查网络");
        }
    }

    public void startDownload(GameBaseDetail game, String page_h5, String downloadMode) {
        //开始下载
        FileDownloaders.download(mContext, game, downloadMode, Constant.DOWNLOAD_TYPE_NORMAL, page_h5, null, true, false, 0);
    }

    @SuppressWarnings("unused")
    private void showShareDialog(String logo, String id, String type, String link, String content, String title) {
        ShareDialog dialog = new ShareDialog(mContext, ShareDialog.ShareDialogType.Default);
        ShareBean shareBean = new ShareBean();
        shareBean.setTitle(title);
        shareBean.setText(content);
        shareBean.setTitleurl(link);
        dialog.setShareBean(shareBean);
        dialog.show();
    }

    /**
     * 显示登陆框，登陆成功后跳转url，此url在原url基础之上添加了参数
     *
     * @param url 登陆成功后跳转url
     */
    private void showLoginDialog(final String url) {
        UserInfoManager.instance().addListening(new UserInfoUpdateListening() {
            @Override
            public void userLogin(UserBaseInfo userBaseInfo) {
                loadAddUserParams(url);
            }

            @Override
            public void updateUserInfo(UserBaseInfo userBaseInfo) {
            }

            @Override
            public void userLogout() {
            }
        });
        //开始登陆
        UserInfoManager.instance().starLogin(mContext, true);
    }

    /**
     * url中添加参数
     *
     * @param url 需要添加后缀的地址
     */
    private void loadAddUserParams(String url) {
        LogUtils.i("Erosion","url=====" + url);
        if (url.contains("clientinfo={")) {
            //如果已经有此参数，则不用添加了,也不需要重新加载
            return;
        }

        URI uri = URI.create(url);
        Params phoneParams = new Params(mContext);
        String value = new Gson().toJson(phoneParams);

        List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
        if (params != null && params.size() > 0) {
            if (voucherType.equals("voucher")) {
                url = url.substring(0,url.indexOf("clientinfo"));
                LogUtils.i("Erosion","url============" + url);
                url = url + "clientinfo=" + value;
                LogUtils.i("Erosion","url===" + url);
            } else {
                url = url + "&clientinfo=" + value;
            }
            LogUtils.i("Erosion","clientinfo11111");
        } else {
            url = url + "?clientinfo=" + value;
            LogUtils.i("Erosion","clientinfo22222");
        }
        webview.loadUrl(url);

    }

    /**
     * 执行js脚本
     *
     * @param js
     */
    private void execJs(String js) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webview.evaluateJavascript(js, null);
        } else {
            webview.loadUrl(js);
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public class JavascriptInterface {
        /**
         * 旧版按钮操作
         *
         * @param ids
         */
        @android.webkit.JavascriptInterface
        public void app_download(String ids) {
            Logger.i("app_download_ids:" + ids);
            String[] gameIdArray = ids.split(",");
            for (String id : gameIdArray) {
                try {
                    requestGameDetailData(Integer.parseInt(id), 0);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Logger.i("GameId解析失败" + id);
                }
            }
        }

        /**
         * 新版按钮操作
         * 点击触发按钮，会有不同状态
         */
        @android.webkit.JavascriptInterface
        public void app_download(final String gameId, String type, final String packageName, final int status) {
            LogUtils.i("honaf", "gameId = " + gameId + ", packageName = " + packageName + ", type = " + type + ", status = " + status);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    final int id = Integer.parseInt(gameId);
                    final GameBaseDetail game = DownFileService.getInstance(mContext).getDownFileById(id);


                    if (FileDownloaders.couldDownload(mContext)) {
                        //下载完成、下载中、等待中无需检查内存
                        if (!(status == H5DownloadState.DOWNLOAD_FINISH || status == H5DownloadState.DOWNLOADING || status == H5DownloadState.WAITING)) {
                            if (!StorageSpaceDetection.check(false, mContext, true)) {
                                LogUtils.e("honaf", "内存为0,不能下");
                                return;
                            }
                        }

                    } else {
                        //当没有网络的时候,安装本不需要做下载验证
                        if (status == H5DownloadState.DOWNLOAD_FINISH) {
                            ApkInstallManger.self().installPkg(game, Constant.MODE_SINGLE, null, false);
                        } else {
                            ToastUtils.showToast(mContext, "请检查网络");
                        }
                        return;
                    }

                    switch (status) {
                        case H5DownloadState.UPGRADE:
                        case H5DownloadState.NOT_DOWNLOAD:// 未下载
                            requestGameDetailData(id, status);
                            break;
                        case H5DownloadState.WAITING:// 等待中
                        case H5DownloadState.DOWNLOADING:// 下载中

                            H5Util.dealPauseClick(game);
                            DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, getPageAlias(), 1, null, 1, "" + game.getId(), null, Constant.RETRY_TYPE_MANUAL, "downStop", game.getPkgName(), ""));
                            LogUtils.e("honaf=>js", "status = " + H5DownloadState.STOP + ", sofar = " + game.getDownLength() + ", totla = " + game.getFileTotalLength());
                            webview.loadUrl("javascript:percent(" + game.getDownLength() + "," + game.getFileTotalLength() + "," + H5DownloadState.STOP + ",\"" + game.getPkgName() + "\")");

                            break;
                        case H5DownloadState.STOP:// 暂停
                            checkDownload(game);
                            break;
                        case H5DownloadState.RETRY:// 重试
                            LogUtils.e("honaf=>game", game.toString());
                            H5Util.retryDownLoad(WebViewActivity.this, gameId, game, 0, getPageAlias(), "");
                            break;
                        case H5DownloadState.DOWNLOAD_FINISH:// 下载完成
                            if (AppUtils.apkIsNotExist(game.getDownPath())) {
                                ToastUtils.showToast(mContext, game.getName() + " 的安装包不存在，准备为您重新下载");
                                game.setDownLength(0);
                                TTGC_DirMgr.init();
                                TTGC_DirMgr.makeDirs();

                                // 重置游戏状态为未下载
                                H5Util.restoreDataAndFlush(game);
                                startDownload(game, getPageAlias() + "_" + getmShortGotoUrl(), Constant.MODE_RETRY_REQUEST);
                            } else {
                                ApkInstallManger.self().installPkg(game, Constant.MODE_SINGLE, null, false);
                            }
                            break;
                        case H5DownloadState.INSTALL_FINISH:// 安装完成
                            OpenAppUtil.openApp(game, mContext, getPageAlias());
                            break;
                        default:
                            break;
                    }
                }
            });


        }

        /**
         * js接口（旧版）
         *
         * @param logo    分享logo
         * @param title   分享标题
         * @param content 分享内容
         * @param link    分享跳转链接
         */
        @android.webkit.JavascriptInterface
        public void app_share(final String logo, final String title, final String content, final String link) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showShareDialog(logo, "", "", link, content, title);
                }
            });
        }

        /**
         * js接口（新版）
         *
         * @param logoUrl 分享logo
         * @param id      分享标题
         * @param type    分享内容
         * @param link    分享跳转链接
         */
        @android.webkit.JavascriptInterface
        public void app_share(final String logoUrl, final String id, final String type, final String link, final String content, final String title) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showShareDialog(logoUrl, id, type, link, content, title);
                }
            });
        }

        /**
         * 旧版登录
         */
        @android.webkit.JavascriptInterface
        public void app_signin() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    goLogin();
                }
            });

        }

        /**
         * 登录
         */
        @android.webkit.JavascriptInterface
        public void user_login() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    goLogin();
                    LogUtils.i("Erosion","user_login");
                }
            });

        }



        /**
         * 注册
         */
        @android.webkit.JavascriptInterface
        public void user_register() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showRegisterDialog();
                }
            });

        }

        @android.webkit.JavascriptInterface
        public void toast(final String message) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showToast(WebViewActivity.this,message);
                }
            });
        }

        /**
         * 离开当前页
         */
        @android.webkit.JavascriptInterface
        public void finishActivity() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    WebViewActivity.this.finish();
                }
            });
        }

        /**
         * 初始化
         *
         * @param data 包名数组
         */
        @android.webkit.JavascriptInterface
        public void app_init_app_status(final String data) {
//            LogUtils.d(LogTAG.HTAG, Thread.currentThread() + "");
            LogUtils.d("honaf=init=>", data);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    checkLogin();
                    packageNameData = data;
                    initStatusH5();
                }
            });

        }

        @android.webkit.JavascriptInterface
        public void log() {
        }

        @android.webkit.JavascriptInterface
        public void log(String msg) {
            LogUtils.d("honaf==>", msg);
        }

        /**
         * @param id          应用id
         * @param packageName 应用包名
         * @param type        应用分类
         */
        @android.webkit.JavascriptInterface
        public void app_goAppDetail(final int id, final String packageName, final String type) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (id > 0) {
                        isDeal = false;
                        ActivityActionUtils.JumpToGameDetail(mContext, id);
                    } else {
                        ToastUtils.showToast(mContext, "游戏已下架");
                    }
                }
            });
        }
    }

    private void initStatusH5() {
        if (TextUtils.isEmpty(packageNameData)) {
            return;
        }
        String[] packageNameArr = packageNameData.split(",");
        long sofar;
        long total;
        int status = H5DownloadState.NOT_DOWNLOAD;
        final List<String> urls = new ArrayList<>();
        LogUtils.d(LogTAG.HTAG, "initStatusH5===1");
        List<GameBaseDetail> gameBaseDetailList = FileDownloaders.getGamesByPkgNames(packageNameArr);
        LogUtils.d(LogTAG.HTAG, "initStatusH5===2");
        allUpGradeFileInfo.clear();
        allUpGradeFileInfo.addAll(FileDownloaders.getAllUpGradeFileInfo());
        LogUtils.d(LogTAG.HTAG, "initStatusH5===3");
        allInstalledDownFileInfo.clear();
        allInstalledDownFileInfo.addAll(FileDownloaders.getAllInstalledDownFileInfo());
        LogUtils.d(LogTAG.HTAG, "initStatusH5===4");
        for (int i = 0; i < packageNameArr.length; i++) {

            String packageName = packageNameArr[i];
//            GameBaseDetail game = DownFileService.getInstance(mContext).getDownFileByPkg(packageName);
            // 通过本地数据库查询本地游戏信息
            GameBaseDetail game = gameBaseDetailList.get(i);
            LogUtils.e("honaf", packageNameArr.length + " -- " + (game == null));
            if (game == null) {
                sofar = 0;
                total = 0;
                status = H5DownloadState.NOT_DOWNLOAD;
            } else {
                sofar = game.getDownLength();
                total = game.getFileTotalLength();
                boolean flag = false;
                for (GameBaseDetail detail : allUpGradeFileInfo) {
                    // 所有升级的应用
                    if (detail.getId() == game.getId()) {
                        flag = true;
                        status = H5Util.changeToH5Status(detail.getState());
                        break;
                    }
                }
                if (!flag) {
                    for (GameBaseDetail detail : allInstalledDownFileInfo) {
                        // 所有安装的应用
                        if (detail.getId() == game.getId()) {
                            flag = true;
                            status = H5DownloadState.INSTALL_FINISH;
                            break;
                        }
                    }
                }
                if (!flag) {
                    status = H5Util.changeToH5Status(game.getState());
                }
            }

            LogUtils.i("honaf", "init--->(" + packageName + ") sofar = " + sofar + ", total = " + total + ", status = " + status);
            urls.add("javascript:percent(" + sofar + "," + total + "," + status + ",\"" + packageName + "\")");
        }

        for (final String url : urls) {
            if (webview != null) {
                webview.loadUrl(url);
            }
        }

    }

    /**
     * 下载安装状态回调
     *
     * @param downloadEvent
     */
    public synchronized void onEventMainThread(final DownloadUpdateEvent downloadEvent) {
        LogUtils.d("honaf", "下载原始status = " + downloadEvent.game.getState());
        final GameBaseDetail game = downloadEvent.game;
        if (TextUtils.isEmpty(packageNameData) || packageNameData.contains(game.getPkgName())) {
            if (game == null) {
                return;
            }

            GameBaseDetail downFile = FileDownloaders.getDownFileInfoById(game.getId());
            if (downFile != null) {
                game.setDownInfo(downFile);
            } else {
                game.setState(DownloadState.undownload);
                game.setDownLength(0);
            }
            final int status = H5Util.changeToH5Status(game.getState());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (game.getDownLength() == game.getFileTotalLength() && status == H5DownloadState.DOWNLOADING) {
                        webview.loadUrl("javascript:percent(" + game.getDownLength() + "," + game.getFileTotalLength() + "," + H5DownloadState.DOWNLOAD_FINISH + ",\"" + game.getPkgName() + "\")");
                    } else {
                        webview.loadUrl("javascript:percent(" + game.getDownLength() + "," + game.getFileTotalLength() + "," + status + ",\"" + game.getPkgName() + "\")");
                    }

                }
            });

        }
    }


    private void showRegisterDialog() {
        Intent intent = new Intent(mContext, PersonalCenterActivity.class);
        intent.putExtra("type", "register");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d(LogTAG.HTAG, "onResume");
        isDeal = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.d(LogTAG.HTAG, "onRestart");
        if (isLoaded) {
            LogUtils.d(LogTAG.HTAG, "onRestart重新执行了initStatusH5");
            checkLogin();
            initStatusH5();
        }
    }

    /**
     * 检查是否已登录
     */
    private void checkLogin() {
        UserBaseInfo userBaseInfo = UserInfoManager.instance().getUserInfo();
        String token = null;
        if (userBaseInfo != null) {
            token = UserInfoManager.instance().getUserInfo().getToken();
            LogUtils.i("Erosion","token===" + token);
        }
        webview.loadUrl("javascript:checkLogin(\"" + (token == null ? "" : token) + "\")");
    }

    private void goLogin() {
        String url = webview.getUrl();
        //调用登陆框
        if (UserInfoManager.instance().isLogin()) {
//                已经登陆
            loadAddUserParams(url);
        } else {
            //未登陆
            showLoginDialog(url);

        }
    }


    public class Params {
        public String uuid;
        String imei;
        String version;
        int version_code;
        String os_version;
        String device;
        String metrics;
        String channel;
        String access_token;

        public Params(Context context) {
            MyApplication application = (MyApplication) (context.getApplicationContext());
            SharedPreferencesUtil share = new SharedPreferencesUtil(context);
            uuid = share.get(SharedPreferencesUtil.netUUID);
            if (uuid.equals("")) {
                uuid = UUID.randomUUID().toString();
                share.add(SharedPreferencesUtil.netUUID, uuid);
            }
            imei = MyApplication.imei;
            version = AppUtils.getVersionName(mContext);
            version_code = AppUtils.getVersionCode(mContext);
            os_version = Utils.getAndroidSDKVersion();
            device = Utils.getDeviceName();
            if (context instanceof Activity) {
                metrics = Utils.getScreenWidth(context) + "*" + Utils.getScreenHeight(context);
            } else {
                metrics = MyApplication.width + "*" + MyApplication.height;
            }
            System.out.println("metrics:" + metrics);
            channel = Constant.CHANNEL;

            String token = Net.instance().getToken();
            if (token != null) {
                access_token = token;
            } else {
                access_token = "";
            }

        }
    }


    private static final String CLIENT_TYPR = "1"; //当clientInfo == 1时，给clientInfo重新赋值为param,为2时，不重新赋值

    /**
     * 4.0版本
     *
     * @param url
     * @return
     */
    public String filterAddClientInfoParamForV4(String url) {
        try {
            URI uri = URI.create(url);
            String host = uri.getAuthority();
            String scheme = uri.getScheme();
            List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
            String replaceParam = null;
            for (NameValuePair param : params) {
                if (param.getName().equals("clientinfo") && param.getValue().equals(CLIENT_TYPR)) {
                    replaceParam = param.getName() + "=" + param.getValue();
                    Params phoneParams = new Params(mContext);
                    String value = new Gson().toJson(phoneParams);
                    String newParam = "clientinfo=" + value;
                    url = url.replace(replaceParam, newParam);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

}
