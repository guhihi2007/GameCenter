package cn.lt.game.ui.common.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huanju.data.HjDataClient;
import com.huanju.data.content.raw.HjRequestFrom;
import com.huanju.data.content.raw.info.HjInfoDetail;
import com.huanju.data.content.raw.info.HjInfoListItem;
import com.huanju.data.content.raw.listener.IHjRequestItemDetailListener;

import org.json.JSONException;

import java.util.HashMap;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.db.operation.FavoriteDbOperator;
import cn.lt.game.global.Constant;
import cn.lt.game.jsonparser.DetailRespParser;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.DownLoadBarForOther;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.view.StrategyListInfoView;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.net.Host.HostType;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri;

//攻略 资讯 新闻 详情
public class GameOtherDetailActivity extends BaseActivity implements OnClickListener, RetryCallBack {
    private static final int MESSAGE_SUCCESS = 1;
    private static final int MESSAGE_FAILED = 2;
    private WebView webView;
    private TextView OtherInfoTxOne, OtherInfoTxTwo, OtherInfoTxThree, OtherInfotitle;
    private int whereFrom;
    private TitleBarView titleView;
    private StrategyListInfoView gameInfoView;
    private String mStrategyContent = "";
    private IHjRequestItemDetailListener<HjInfoDetail> detailListener;
    private HashMap<String, HjInfoListItem> hjMap = new HashMap<>();
    private String id;
    private String title;
    private LinearLayout gameOtherInfo_Layout, scrollLayout;
    private NetWorkStateView netWorkStateView;
    private DownLoadBarForOther downLoadBar;
    private GameBaseDetail game;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (!GameOtherDetailActivity.this.isFinishing()) {
                switch (what) {
                    case MESSAGE_SUCCESS:
                        webView.loadDataWithBaseURL(null, mStrategyContent, "text/html", "utf-8", null);

                        break;
                    case MESSAGE_FAILED:
                        netWorkStateView.showNetworkFailLayout();
                        break;
                    default:
                        break;
                }
            }
        }
    };
    private View mNoneSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getIntentData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameotherdetail);
        initView();
        checkNetwork();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        game = (GameBaseDetail) intent.getSerializableExtra("game_base_detail");
        whereFrom = intent.getIntExtra("where_from", FavoriteDbOperator.GameStrategyTag);
        id = intent.getStringExtra("id");
        title = getResources().getStringArray(R.array.favoriteItem)[whereFrom];
        switch (whereFrom) {
            case FavoriteDbOperator.GameStrategyTag:// 攻略
                break;
            case FavoriteDbOperator.GameinfomationTag: // 资讯
                break;
            case FavoriteDbOperator.GameNewsTag:// 新闻
                break;

            default:
                break;
        }
    }

    private void initView() {
        webView = (WebView) findViewById(R.id.gameOtherdetail_webView);
        netWorkStateView = (NetWorkStateView) findViewById(R.id.gameOtherdetail_networkstateView);
        downLoadBar = (DownLoadBarForOther) findViewById(R.id.gameOtherdetail_downloadbar);
        OtherInfotitle = (TextView) findViewById(R.id.gameOtherdetail_startegy_title);
        OtherInfoTxOne = (TextView) findViewById(R.id.gameOtherdetail_startegy_msgOne);
        OtherInfoTxTwo = (TextView) findViewById(R.id.gameOtherdetail_startegy_msgTwo);
        OtherInfoTxThree = (TextView) findViewById(R.id.gameOtherdetail_startegy_msgThree);
        titleView = (TitleBarView) findViewById(R.id.gameOtherdetail_titleBar);
        mNoneSearchView = findViewById(R.id.not_serach_btn_moreButton);
        mNoneSearchView.setVisibility(View.GONE);
        titleView.setTitle(title + "详情");
        titleView.setBackHomeVisibility(View.INVISIBLE);
        titleView.setScrollVerticalViewVisible(View.GONE);
        OtherInfotitle.setText("推荐" + title);
        gameInfoView = (StrategyListInfoView) findViewById(R.id.gameOtherdetail_info_view);

        gameInfoView.setOnClickListener(this);
        gameOtherInfo_Layout = (LinearLayout) findViewById(R.id.gameOtherdetail_startegyLayout);

        scrollLayout = (LinearLayout) findViewById(R.id.gameOtherdetail_scrollLayout);

        OtherInfoTxOne.setOnClickListener(this);
        OtherInfoTxTwo.setOnClickListener(this);
        OtherInfoTxThree.setOnClickListener(this);
        gameInfoView.setOnClickListener(this);
        netWorkStateView.setRetryCallBack(this);
        initWebViewSetting();
    }

    private void initWebViewSetting() {
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true); // 打开javascript
        setting.setPluginState(PluginState.ON); // 打开flash插件
        setting.setDefaultTextEncodingName("UTF-8");// 部分手机需要制定后方不会出现乱码
        setting.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); // 内嵌图片自适应

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                initOtherView();
                netWorkStateView.hideNetworkView();
            }
        });
    }

    private void initGameOtherDetailListener() {
        detailListener = new IHjRequestItemDetailListener<HjInfoDetail>() {

            @Override
            public void onSuccess(HjInfoDetail strategyDetail) {

                mStrategyContent = strategyDetail.content;
                int maxSize = Math.min(3, strategyDetail.recItems.size());
                for (int i = 0; i < maxSize; i++) {
                    HjInfoListItem detail = strategyDetail.recItems.get(i);
                    hjMap.put(detail.id, detail);
                }
                mHandler.sendEmptyMessage(MESSAGE_SUCCESS);
            }

            @Override
            public void onFailed(int httpStatusCode, int errorCode, String errorMessage) {
                mHandler.sendEmptyMessage(MESSAGE_FAILED);
            }
        };
    }

    private void getGameInfoNetData() {

        Net.instance().executeGet(HostType.SERVER_HOST, Uri.getOtherGameDetailUri(game.getPkgName()), null, new WebCallBackToString() {

            @Override
            public void onFailure(int statusCode, Throwable error) {
                gameInfoView.setVisibility(View.GONE);
                downLoadBar.setVisibility(View.GONE);
                getWebViewData();
            }

            @Override
            public void onSuccess(String result) {

                try {
                    DetailRespParser.parseJson(result, game);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                initViewState();
                getWebViewData();
            }
        });
    }

    private void initViewState() {
        downLoadBar.initDownLoadBar(game, getPageAlias());
        gameInfoView.setGame(game);
    }

    private void getGameOtherDetailData() {
        HjDataClient.getInstance(this).requestInfoDetail(detailListener, id, HjRequestFrom.hj_default);
    }

    protected void initOtherView() {
        int index = hjMap.size();
        if (index > 0) {
            gameOtherInfo_Layout.setVisibility(View.VISIBLE);
        }

        int count = 0;
        for (String id : hjMap.keySet()) {
            HjInfoListItem item = hjMap.get(id);
            count++;
            String temp = item.title.trim();
            String title = "NULL".equalsIgnoreCase(temp) ? "" : temp;
            if (count == 1) {
                OtherInfoTxOne.setText(title);
                OtherInfoTxOne.setTag(id);
                OtherInfoTxOne.setVisibility(View.VISIBLE);
            } else if (count == 2) {
                OtherInfoTxTwo.setText(title);
                OtherInfoTxTwo.setTag(id);
                OtherInfoTxTwo.setVisibility(View.VISIBLE);
            } else if (count == 3) {
                OtherInfoTxThree.setText(title);
                OtherInfoTxThree.setTag(id);
                OtherInfoTxThree.setVisibility(View.VISIBLE);
            }
        }

    }

    /* 检查网络状态 */
    private void checkNetwork() {
        // TODO Auto-generated method stub
        /* 获取网络数据 */
        if (NetUtils.isConnected(this)) {
            netWorkStateView.showLoadingBar();
            getGameInfoNetData();
            // 联网获取数据
        } else {
            netWorkStateView.showNetworkFailLayout();
        }
    }

    private void getWebViewData() {
        // TODO Auto-generated method stub
        initGameOtherDetailListener();
        getGameOtherDetailData();
    }

    @Override
    protected void onResume() {
        if (webView != null) {
            webView.resumeTimers();
            webView.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.pauseTimers();
            webView.onPause();
            webView.clearCache(true);
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            scrollLayout.removeAllViews();
            webView.removeAllViews();
            webView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        String id;
        switch (v.getId()) {
            case R.id.gameOtherdetail_startegy_msgOne:
                id = (String) v.getTag();
                jumpSelf(id);
                break;
            case R.id.gameOtherdetail_startegy_msgTwo:
                id = (String) v.getTag();
                jumpSelf(id);
                break;
            case R.id.gameOtherdetail_startegy_msgThree:
                id = (String) v.getTag();
                jumpSelf(id);
                break;
            case R.id.gameOtherdetail_info_view:
                ActivityActionUtils.JumpToGameDetail(this, game.getId());
                break;

            default:
                break;
        }
    }

    private void jumpSelf(String id) {
        // 更新game信息 并跳转
        HjInfoListItem item = hjMap.get(id);
        game.setPkgName(item.package_name);
        ActivityActionUtils.jumpToGameOtherDetail(this, whereFrom, item.id, item.title, game);

    }

    @Override
    public void retry() {
        // TODO Auto-generated method stub
        checkNetwork();
    }

    @Override
    public void setPageAlias() {
        switch (whereFrom) {
            case FavoriteDbOperator.GameStrategyTag:// 攻略
                setmPageAlias(Constant.PAGE_STRATEGY_DETAIL, id);
                break;
            case FavoriteDbOperator.GameinfomationTag: // 资讯
                break;
            case FavoriteDbOperator.GameNewsTag:// 新闻
                break;

            default:
                break;
        }
    }

}
