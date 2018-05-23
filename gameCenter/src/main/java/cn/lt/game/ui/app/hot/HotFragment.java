package cn.lt.game.ui.app.hot;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.bean.HotJumpTypeBean;
import cn.lt.game.db.service.DownFileService;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.detail.GameDomainDetail;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.download.H5DownloadState;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.event.RedPointEvent;
import cn.lt.game.event.UninstallEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.install.ApkInstallManger;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.PreferencesUtils;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.H5Util;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.OpenAppUtil;
import cn.lt.game.lib.util.StatusBarUtils;
import cn.lt.game.lib.util.StorageSpaceDetection;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.deeplink.DeepLinkUtil;
import cn.lt.game.lib.util.file.TTGC_DirMgr;
import cn.lt.game.lib.util.log.Logger;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.util.redpoints.RedPointsCallback;
import cn.lt.game.lib.util.redpoints.RedPointsViewUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.model.PageDetail;
import cn.lt.game.model.PageMap;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.index.widget.SearchView;
import cn.lt.game.ui.app.jump.IJumper;
import cn.lt.game.ui.app.jump.JumpFactory;
import de.greenrobot.event.EventBus;


/**
 * Created by Administrator on 2017/5/11.
 */

public class HotFragment extends BaseFragment implements NetWorkStateView.RetryCallBack, RedPointsCallback {
    private View mHotView;
    private View mRedPoint;
    private WebView x5WebView;
    private NetWorkStateView netWorkStateView;
    private static String URL = "/client/hot/";
    private String url;
    private String hot_host;
    private boolean urlIsChange = false;//如果是从别的页面条转过来的，当前页面不重新加载
    private static final String JS_NAME = "ttigame";
    private String mShortGotoUrl;
    private String packageNameData;
    CopyOnWriteArrayList<GameBaseDetail> allUpGradeFileInfo = new CopyOnWriteArrayList<>();
    CopyOnWriteArrayList<GameBaseDetail> allInstalledDownFileInfo = new CopyOnWriteArrayList<>();
    private boolean isLoading = false;
    //超时时间，单位毫秒
    private static final int TIMEOUT_LIMIT = 8 * 1000;
    private String hotDetailUrl;
    private String tab_id;
    private boolean isLoadError = false;

    @Override
    public void setPageAlias() {
        if (TextUtils.isEmpty(hotDetailUrl)) {
            setmPageAlias(Constant.PAGE_HOT);
            String tabId;
            LogUtils.i("Erosion", "hot_host===" + URL);
            if (!URL.contains("=")) {
                setTabUrl(0 + "");
            } else {
                tabId = URL.substring(URL.indexOf("=") + 1, URL.length());
                setTabUrl(tabId);
            }
        } else {
            setmPageAlias(Constant.PAGE_HOT_DETAIL);
            String hotDetailId = H5Util.getTabId(hotDetailUrl);
            setTabUrl(hotDetailId);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle args = getArguments();
        if (args != null) {
            this.hotDetailUrl = args.getString(H5Util.HOT_DETAIL_URL);
            LogUtils.i("HotFragment", "onAttach url:" + hotDetailUrl);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if (null != Host.getHost(Host.HostType.HOT_HOST)) {
            hot_host = Host.getHost(Host.HostType.HOT_HOST);
            PreferencesUtils.putString(mActivity, H5Util.HOT_HOST, hot_host);
        } else {
            hot_host = PreferencesUtils.getString(mActivity, H5Util.HOT_HOST, "");
        }
        if (!urlIsChange) {
            URL = "/client/hot/";
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            MyApplication.getMainThreadHandler().removeCallbacks(runnable);
            LogUtils.i(LogTAG.HTAG, "runnable" + isLoading);
            if (isLoading) {
                loadBlankAndShowNetError();
                isLoading = false;
            }
        }
    };

    @Override
    public void onResume() {
        if (TextUtils.isEmpty(hot_host)) {
            hot_host = PreferencesUtils.getString(mActivity == null ? MyApplication.application : mActivity, H5Util.HOT_HOST);
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        URL = "/client/hot/";
        EventBus.getDefault().unregister(this);
        x5WebView.removeAllViews();
        if (x5WebView != null) {
            x5WebView.destroy();
            x5WebView = null;
        }
        MyApplication.getMainThreadHandler().removeCallbacks(runnable);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        LogUtils.i("Erosion", "setUserVisibleHint = " + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
//            RedPointsViewUtils.initTopManagerRedPoints(mRedPoint,mActivity);
            LogUtils.i("Erosion", "urlIsChange = " + urlIsChange);
             /*内容页面会走setUserVisibleHint，走onCreateView会出现加载内容页面空白*/
            if (urlIsChange && mHotView != null) {
                loadWebView();
            }
            //TODO 先保留 下个版本会用到
                /*Uri uri = Uri.parse(URL);
                String str = uri.getQuery();
                int position = Integer.parseInt(str.substring(str.indexOf("=") + 1,str.length()));
                LogUtils.i("Erosion","str====" + position);
                x5WebView.loadUrl("javascript:tabIdChange(" + 1 + ")");*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mHotView == null) {
            mHotView = inflater.inflate(R.layout.hot_fragment, container, false);
            initView();
            /*内容详情页面会走onCreateView，不走setUserVisibleHint*/
            if (!TextUtils.isEmpty(hotDetailUrl)) {
                loadWebView();
            }
            urlIsChange = true;
        }
        return mHotView;
    }

    private void initView() {
        x5WebView = (WebView) mHotView.findViewById(R.id.hot_webview);
        mRedPoint = mHotView.findViewById(R.id.tv_titleBar_redPoint);
        netWorkStateView = (NetWorkStateView) mHotView.findViewById(R.id.netWrokStateView);
        netWorkStateView.setRetryCallBack(this);
        View statusBar = mHotView.findViewById(R.id.status_bar);
        RedPointsViewUtils.initTopManagerRedPoints(mRedPoint, mActivity);
        SearchView searchView = (SearchView) mHotView.findViewById(R.id.search_bar);
        if (TextUtils.isEmpty(hotDetailUrl)) {
            StatusBarUtils.showSelfStatusBar(mActivity, statusBar);
            url = hot_host + "/client/hot/";
        } else {
            searchView.setVisibility(View.GONE);
            url = hot_host + url;
        }
        x5WebView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024);
        x5WebView.getSettings().setAppCachePath(getActivity().getCacheDir().getAbsolutePath());
        x5WebView.getSettings().setAllowFileAccess(true);
        x5WebView.getSettings().setAppCacheEnabled(true);
        x5WebView.getSettings().setJavaScriptEnabled(true);
        x5WebView.getSettings().setDomStorageEnabled(true);
        x5WebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        JavascriptInterface mJavascriptInterface = new JavascriptInterface();
        x5WebView.addJavascriptInterface(mJavascriptInterface, JS_NAME);
    }

    private void loadBlankAndShowNetError() {
//        x5WebView.loadUrl("about:blank");// 避免出现默认的错误界面
//        x5WebView.setVisibility(View.GONE);
        netWorkStateView.setVisibility(View.VISIBLE);
        netWorkStateView.showNetworkFailLayout();
    }

    public void loadWebView() {
        if (!NetUtils.isConnected(mActivity)) {
            netWorkStateView.showNetworkFailLayout();
            return;
        }
        LogUtils.i("HotFragment", "loadWebView() ");
        MyApplication.getMainThreadHandler().postDelayed(runnable, TIMEOUT_LIMIT);
        netWorkStateView.showLoadingBar();
        final long t1 = System.currentTimeMillis();
        x5WebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                LogUtils.i("GOOD", "onReceivedTitle:" + title);
                super.onReceivedTitle(view, title);
                // android 6.0 以下通过title获取
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (title.contains("404") || title.contains("500") || title.contains("Error")) {
                        loadBlankAndShowNetError();
                    }
                }
                if (H5Util.loadingRetryURL(title) || isLoadError) {
                    isLoadError = false;
                    loadBlankAndShowNetError();
                }
            }

        });
        x5WebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                LogUtils.i("GOOD", "加载热点H5耗时：" + (System.currentTimeMillis() - t1));
                LogUtils.i("GOOD", "加载热点=：" + view.getTitle());
                super.onPageFinished(view, url);
                netWorkStateView.hideLoadingBar();

                if (H5Util.loadingRetryURL(view.getTitle()) || isLoadError) {
                    isLoadError = false;
                    loadBlankAndShowNetError();
                } else {
                    x5WebView.setVisibility(View.VISIBLE);
                    netWorkStateView.setVisibility(View.GONE);
                    isLoading = false;
                }
                LogUtils.i(LogTAG.HTAG, "onPageFinished");
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                // 这个方法在6.0才出现
                int statusCode = errorResponse.getStatusCode();
                LogUtils.i("GOOD", "onReceivedHttpError:" + statusCode);
                if (500 == statusCode) {
                    loadBlankAndShowNetError();
                }
                isLoading = false;
                LogUtils.i(LogTAG.HTAG, "onReceivedHttpError");
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                LogUtils.i("GOOD", "onReceivedError:" + error.getErrorCode());
                super.onReceivedError(view, request, error);
                isLoading = false;
                if (error.getErrorCode() == ERROR_HOST_LOOKUP || error.getErrorCode() == ERROR_CONNECT || error.getErrorCode() == ERROR_TIMEOUT) {
                    isLoadError = true;
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtils.i(LogTAG.HTAG, "shouldOverrideUrlLoading");
                String httpHead = url.substring(0, 5);
                if (httpHead.equals("http:") || httpHead.equals("https")) {
//                    webView.loadUrl(filterAddClientInfoParamForV4(s));
                    return false;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    mActivity.startActivity(intent);
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                isLoading = true;
                netWorkStateView.showLoadingBar();
                LogUtils.i(LogTAG.HTAG, "onPageStarted");
            }
        });

        Map<String, String> header = new HashMap<>();
        header.put("color", "normal");
        if (TextUtils.isEmpty(hotDetailUrl)) {
            x5WebView.loadUrl(hot_host + URL, header);
        } else {
            if (hotDetailUrl.contains("http")) {
                URL = hotDetailUrl;
            } else {
                URL = hot_host + hotDetailUrl;
            }
            LogUtils.i("Erosion", "hotDetailUrl===++++++" + URL);
            x5WebView.loadUrl(URL, header);
        }
        urlIsChange = false;
    }

    public synchronized void setUrl(String url) {
        urlIsChange = !URL.equals(url);
        URL = url;
        LogUtils.i("Erosion", "hot_host =====" + URL);
    }


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

    /**
     * 请求游戏详情信息
     */
    private void requestGameDetailData(int gameId, final int pos, final int status) {
        if (FileDownloaders.couldDownload(mActivity)) {
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
                    LogUtils.i("Erosion", "statusCode===" + statusCode + ",error===" + error.getMessage().toString());
                    if (!TextUtils.isEmpty(error.getMessage().toString()) && "网络连接失败".equals(error.getMessage().toString())) {
                        ToastUtils.showToast(mActivity, "网络连接失败");
                    } else {
                        ToastUtils.showToast(mActivity, "游戏不存在或已下架");
                    }
                }

                @Override
                protected void handle(UIModuleList uiData) {
                    UIModule uiModule = (UIModule) uiData.get(0);
                    GameDomainDetail domainDetail = (GameDomainDetail) uiModule.getData();
                    LogUtils.i("Erosion", "domainDetail" + domainDetail.toString());
                    GameBaseDetail game = new GameBaseDetail();// 存放游戏信息
                    game.setGameDetail(domainDetail);

                    // 需要和数据库进行同步一下，保证可升级游戏数据一致
                    GameBaseDetail downFile = FileDownloaders.getDownFileInfoById(game.getId());
                    if (downFile != null) {
                        game.setDownInfo(downFile);
                    }

                    if (TextUtils.isEmpty(game.getDownUrl())) {
                        DCStat.downloadFialedEvent(game, "下载地址不存在");
                        ToastUtils.showToast(mActivity, "下载地址不存在");
                    } else {
                        if (status == H5DownloadState.UPGRADE) {
                            game.setPrevState(InstallState.upgrade);
                            game.setState(InstallState.upgrade);
                        }
                        checkDownload(game, pos);
                    }
                }
            });
        } else {
            ToastUtils.showToast(mActivity, "请检查网络！");
        }
    }

    MessageDialog tipDialog = null;

    public void checkDownload(final GameBaseDetail game, final int pos) {
        if (urlIsChange) {
            setmShortGotoUrl(URL);
        } else {
            setmShortGotoUrl(url);
        }

        if (NetUtils.isWifiNet(mActivity)) {
            startDownload(game, getPageAlias(), pos, Constant.MODE_SINGLE);
        } else if (NetUtils.isMobileNet(mActivity)) {

            if (tipDialog == null) {
                String str = mActivity.getResources().getString(R.string.download_mobile_network_tips);
                tipDialog = new MessageDialog(getActivity(), mActivity.getResources().getString(R.string.gentle_reminder), str, mActivity.getResources().getString(R.string.download_continue2), mActivity.getResources().getString(R.string.order_wifi_download));
            }

            tipDialog.setLeftOnClickListener(new MessageDialog.LeftBtnClickListener() {
                @Override
                public void OnClick(View view) {
                    startDownload(game, getPageAlias(), pos, Constant.MODE_SINGLE);
                }
            });

            tipDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {
                @Override
                public void OnClick(View view) {
                    StatisticsEventData sData = new StatisticsEventData(ReportEvent.ACTION_CLICK, "null", pos, null, 0, "" + game.getId(), null, "manual", "orderWifiDownload", game.getPkgName(), tab_id);
                    if (TextUtils.isEmpty(hotDetailUrl)) {
                        FileDownloaders.orderWifiDownload(mActivity, game, Constant.MODE_SINGLE, Constant.DOWNLOAD_TYPE_NORMAL, Constant.PAGE_HOT + "-" + tab_id, sData, false);
                    } else {
                        String tabId = H5Util.getTabId(hotDetailUrl);
                        FileDownloaders.orderWifiDownload(mActivity, game, Constant.MODE_SINGLE, Constant.DOWNLOAD_TYPE_NORMAL, Constant.PAGE_HOT + "-" + tabId, sData, false);
                    }
                    DCStat.clickEvent(sData);
                }
            });

            tipDialog.show();
        } else {
            ToastUtils.showToast(mActivity, "请检查网络");
        }
    }

    public void startDownload(GameBaseDetail game, String page_h5, int pos, String downloadMode) {
        if(InstallState.upgrade == game.getState()) {
            MyApplication.castFrom(mActivity).setNewGameUpdate(true);
            EventBus.getDefault().post(new RedPointEvent(true));
        }

        if(DownloadState.undownload == game.getState()) {
            MyApplication.castFrom(mActivity).setNewGameDownload(true);
            EventBus.getDefault().post(new RedPointEvent(true));
        }

        //开始下载
        LogUtils.i("Erosion", "startDownload==" + tab_id);
        game.pageId = tab_id;
        FileDownloaders.download(mActivity, game, downloadMode, Constant.DOWNLOAD_TYPE_NORMAL, page_h5, null, true, false, pos);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void retry() {
        LogUtils.i("GOOD", "loadWebView03 " + URL);
        loadWebView();
        MyApplication.getMainThreadHandler().postDelayed(runnable, TIMEOUT_LIMIT);
    }

    @Override
    public void refreshTopManageRedPoints() {
        RedPointsViewUtils.initTopManagerRedPoints(mRedPoint, mActivity);
    }

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
                    requestGameDetailData(Integer.parseInt(id), 0, 0);
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
        public void app_download(final String gameId, String type, final String packageName, final int status, final int pos, final String tb_url) {
            LogUtils.i("Erosion", "pos===" + pos);
            LogUtils.i("honaf", "gameId = " + gameId + ", packageName = " + packageName + ", type = " + type + ", status = " + status);
            if (TextUtils.isEmpty(hotDetailUrl) && !TextUtils.isEmpty(tb_url)) {
                tab_id = tb_url.substring(tb_url.indexOf("=") + 1, tb_url.length());
            } else {
                tab_id = H5Util.getTabId(hotDetailUrl);
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    final int id = Integer.parseInt(gameId);
                    final GameBaseDetail game = DownFileService.getInstance(mActivity).getDownFileById(id);
                    if (FileDownloaders.couldDownload(mActivity)) {
                        //下载完成、下载中、等待中无需检查内存
                        if (!(status == H5DownloadState.DOWNLOAD_FINISH || status == H5DownloadState.DOWNLOADING || status == H5DownloadState.WAITING || status == H5DownloadState.RETRY)) {
                            if (!StorageSpaceDetection.check(false, getActivity(), true)) {
                                LogUtils.e("honaf", "内存为0,不能下");
                                return;
                            }
                        }

                    } else {
                        //当没有网络的时候,安装本不需要做下载验证
                        if (status == H5DownloadState.DOWNLOAD_FINISH) {
                            ApkInstallManger.self().installPkg(game, Constant.MODE_SINGLE, null, false);
                        } else {
                            ToastUtils.showToast(mActivity, "请检查网络");
                        }
                        return;
                    }
                    switch (status) {
                        case H5DownloadState.UPGRADE:
                        case H5DownloadState.NOT_DOWNLOAD:// 未下载
                            requestGameDetailData(id, pos, status);
                            break;
                        case H5DownloadState.WAITING:// 等待中
                        case H5DownloadState.DOWNLOADING:// 下载中
                            H5Util.dealPauseClick(game);
                            DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, getPageAlias(), pos, null, 1, "" + game.getId(), null, Constant.RETRY_TYPE_MANUAL, "downStop", game.getPkgName(), tab_id));
                            LogUtils.e("honaf=>js" + "hotFragment", "status = " + H5DownloadState.STOP + ", sofar = " + game.getDownLength() + ", totla = " + game.getFileTotalLength());
                            x5WebView.loadUrl("javascript:percent(" + game.getDownLength() + "," + game.getFileTotalLength() + "," + H5DownloadState.STOP + ",\"" + game.getPkgName() + "\")");
                            break;
                        case H5DownloadState.STOP:// 暂停
                            checkDownload(game, pos);
                            break;

                        case H5DownloadState.RETRY:// 重试
                            LogUtils.e("honaf=>game", game.toString());
                            H5Util.retryDownLoad(getActivity(), gameId, game, pos, getPageAlias(), tab_id);
                            break;
                        case H5DownloadState.DOWNLOAD_FINISH:// 下载完成

                            if (AppUtils.apkIsNotExist(game.getDownPath())) {
                                ToastUtils.showToast(mActivity, game.getName() + " 的安装包不存在，准备为您重新下载");
                                game.setDownLength(0);
                                TTGC_DirMgr.init();
                                TTGC_DirMgr.makeDirs();

                                if (urlIsChange) {
                                    setmShortGotoUrl(URL);
                                } else {
                                    setmShortGotoUrl(url);
                                }

                                // 重置游戏状态为未下载
                                H5Util.restoreDataAndFlush(game);
                                startDownload(game, getPageAlias(), pos, Constant.MODE_RETRY_REQUEST);
                            } else {
                                game.pageId = tab_id;
                                ApkInstallManger.self().installPkg(game, Constant.MODE_SINGLE, null, false);
                            }
                            break;
                        case H5DownloadState.INSTALL_FINISH:// 安装完成
                            OpenAppUtil.openApp(game, mActivity, getPageAlias());
                            break;
                        default:
                            break;
                    }
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
            LogUtils.d("honaf=init=>", data);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
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

        @android.webkit.JavascriptInterface
        public void toast(final String msg) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showToast(mActivity, msg);
                }
            });
        }

        /**
         * @param id          应用id
         * @param packageName 应用包名
         * @param type        应用分类
         */
        @android.webkit.JavascriptInterface
        public void app_goAppDetail(final int id, final String packageName, final String type, final int pos, final String tabUrl) {
            if (Utils.isFastClick()) return;
            LogUtils.i("Erosion", "app_goAppDetail===" + id);
            LogUtils.i("Erosion", "app_goAppDetail===" + pos);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (id > 0) {
                        DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, getPageAlias(), pos, "game", 0, "" + id, "", null, "hot_tab", packageName, tabUrl));
                        ActivityActionUtils.hotPageJumpToGameDetail(mActivity, id);
                        LogUtils.i("Erosion", "id====" + id + this);
                    } else {
                        ToastUtils.showToast(mActivity, "游戏已下架");
                    }
                }
            });
        }

        /**
         * 内容跳转内容详情
         *
         * @param url
         */
        @android.webkit.JavascriptInterface
        public void app_goHotDetail(final String url, final int pos, final String tabUrl) {
            if (Utils.isFastClick()) return;
            LogUtils.i("Erosion", "app_goHotDetail===" + url);
            LogUtils.i("Erosion", "app_goHotDetail===" + tabUrl);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(url)) {
                        if (url.contains("http")) {
                            ActivityActionUtils.jumpToWebView(mActivity, "", url, tabUrl);
                            DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, getPageAlias(), pos, "hot_detail", 0, "", "", " ", "hot_tab", null, tabUrl));
                        } else {
                            ActivityActionUtils.jumpToHotDetail(mActivity, url);
                            DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, getPageAlias(), pos, "hot_detail", 0, "", "", " ", "hot_tab", null, TextUtils.isEmpty(hotDetailUrl) ? tabUrl : H5Util.getTabId(hotDetailUrl)));
                        }
                    }
                }
            });
        }

        /**
         * 应用内跳转
         *
         * @param data
         */
        @android.webkit.JavascriptInterface
        public void app_jump(final String data) {
            if (Utils.isFastClick()) return;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    LogUtils.i("Erosion", "data===" + data);
                    HotJumpTypeBean bean = new Gson().fromJson(data, HotJumpTypeBean.class);
                    PageDetail page = PageMap.instance().getPageDetail(bean.getJump_type());
                    if (page != null) {
                        if (bean.getData() != 0) {
                            page.value = String.valueOf(bean.getData());
                            page.id = String.valueOf(bean.getData());
                            LogUtils.i("Erosion", "data===" + bean.getData());
                        }
                        IJumper jumper = JumpFactory.produceJumper(PresentType.hot_h5, null);
                        jumper.jump(page, mActivity);
                        LogUtils.i("Erosion", "pos===" + bean.getPos());
                        String url = bean.getTab_url();
                        DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, getPageAlias(), bean.getPos(), bean.getJump_type(), 0, bean.getData() + "", url, " ", "hot_tab", null, bean.getTab_url() + ""));
                    }
                }
            });
        }

        /**
         * 页面浏览数据上报
         */
        @android.webkit.JavascriptInterface
        public void pageReported(final String tab_url) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    URL = "/client/hot?tab_id=" + tab_url;
                    LogUtils.i("Erosion", "pageReported===" + tab_url);
                    H5Util.pageReported(getPageAlias(), tab_url);
                }
            });
        }

        /**
         * 下拉刷新数据上报
         *
         * @param tabId
         */
        @android.webkit.JavascriptInterface
        public void refreshReported(final String tabId) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    LogUtils.i("Erosion", "刷新上报：" + tabId);
                    H5Util.pageReported(getPageAlias() + "_refresh", tabId);
                }
            });
        }

        /**
         * deepLink跳转第三方应用
         *
         * @param deepLinkUrl
         */
        @android.webkit.JavascriptInterface
        public void deepLinkJump(final String deepLinkUrl, final String tabId, final int pos) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    LogUtils.i("Erosion", "deepLink:" + deepLinkUrl);
                    DeepLinkUtil.openApp(getContext(), deepLinkUrl, getPageAlias());
                    DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, getPageAlias(), pos, "deeplink", 0, "", "", "", "hot_tab", null, tabId));
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
                        LogUtils.i("Erosion", "detail.getState()===" + detail.getState() + ",detail.getId()" + detail.getPkgName());
                        status = H5Util.changeToH5Status(detail.getState());
                        break;
                    }
                }
                if (!flag) {
                    for (GameBaseDetail detail : allInstalledDownFileInfo) {
                        // 所有安装的应用
                        if (detail.getId() == game.getId()) {
                            flag = true;
                            LogUtils.i("Erosion", "所有安装的应用detail.getState()===" + detail.getState() + ",detail.getId()" + detail.getPkgName());
                            if (detail.getState() == InstallState.ignore_upgrade) {
                                status = H5DownloadState.UPGRADE;
                            } else {
                                status = H5DownloadState.INSTALL_FINISH;
                            }
                            break;
                        }
                    }
                }
                if (!flag) {
                    status = H5Util.changeToH5Status(game.getState());
                }
            }

            LogUtils.i("honaf", "init--->(" + packageName + ") sofar = " + sofar + ", total = " + total + ", status = " + status);
            urls.add("javascript:percent(" + sofar + "," + total + "," + status + ",\"" + packageName + "\",\"normal\")");
        }

        for (final String url : urls) {
            if (x5WebView != null) {
                x5WebView.loadUrl(url);
            }
        }
    }


    /**
     * 下载安装状态回调
     *
     * @param downloadEvent
     */
    public synchronized void onEventMainThread(final DownloadUpdateEvent downloadEvent) {
        LogUtils.d("honaf", "下载原始status = " + downloadEvent.game.getState() + "=====" + downloadEvent.ev);
        final GameBaseDetail game = downloadEvent.game;
        if (game == null) {
            return;
        }
        //解决Bug 1982 【客户端-热点】热点列表页绑定的游戏偶尔会出现点击下载按钮没有改变按钮状态，实际上是已经在下载了 ATian
        //这里要判断包名是否包含才去更新进度是为了节省资源？
//        if (!TextUtils.isEmpty(packageNameData) && packageNameData.contains(game.getPkgName())) {
        GameBaseDetail downFile = FileDownloaders.getDownFileInfoById(game.getId());
        if (downFile != null) {
            game.setDownInfo(downFile);
        } else {
            game.setState(DownloadState.undownload);
            game.setDownLength(0);
        }
        final int status = H5Util.changeToH5Status(game.getState());
        LogUtils.d("honaf", "changeToH5Status = " + status);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (game.getDownLength() == game.getFileTotalLength() && status == H5DownloadState.DOWNLOADING) {
                    x5WebView.loadUrl("javascript:percent(" + game.getDownLength() + "," + game.getFileTotalLength() + "," + H5DownloadState.DOWNLOAD_FINISH + ",\"" + game.getPkgName() + "\",\"normal\")");
                } else {
                    x5WebView.loadUrl("javascript:percent(" + game.getDownLength() + "," + game.getFileTotalLength() + "," + status + ",\"" + game.getPkgName() + "\",\"normal\")");
                }

            }
        });

//        }
    }

    public void onEventMainThread(UninstallEvent unInstallEvent) {
        initStatusH5();
    }

}
