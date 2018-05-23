package cn.lt.game.ui.common.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.huanju.data.HjDataClient;
import com.huanju.data.content.raw.HjRequestFrom;
import com.huanju.data.content.raw.info.HjAlbumInfo;
import com.huanju.data.content.raw.info.HjInfoListItem;
import com.huanju.data.content.raw.listener.IHjRequestNoResultItemListListener;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.db.operation.FavoriteDbOperator;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.DownLoadBarForOther;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.view.StrategyListInfoView;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.view.loadfresh.AutoListView;
import cn.lt.game.lib.view.loadfresh.AutoListView.OnLoadListener;
import cn.lt.game.model.GameBaseDetail;

//攻略 新闻 资讯 列表
public class GameOtherInfoActivity extends BaseActivity implements OnItemClickListener, OnLoadListener, RetryCallBack {
    /**
     * 消息:数据请求失败
     */
    public static final int MESSAGE_REQUEST_FAILED = 2;
    /**
     * 消息:数据请求成功
     */
    public static final int MESSAGE_DATA_READY = 3;

    public static final int MESSAGE_EMPTY = 4;
    private ArrayList<HjInfoListItem> list = new ArrayList<HjInfoListItem>();
    private AutoListView listView;
    private GameOtherInfoAdapter adapter;
    private TitleBarView titleView;
    private GameBaseDetail game = new GameBaseDetail();
    private IHjRequestNoResultItemListListener<HjInfoListItem> listener;
    private int currentPage = 1;
    private StrategyListInfoView gameInfoView;
    private DownLoadBarForOther downLoadBar;
    private NetWorkStateView netWorkStateView;
    private int whereFrom;
    private HjDataClient client;

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DATA_READY:
                    // 请求成功,当前页码自加
                    currentPage++;
                    listView.onLoadComplete();
                    if ((Integer) msg.obj < 10) {
                        System.out.println("不够10条");
                        listView.setLoadEnable(false);
                    }
                    listView.setResultSize((Integer) msg.obj);
                    adapter.notifyDataSetChanged();
                    netWorkStateView.hideNetworkView();
                    break;
                case MESSAGE_REQUEST_FAILED:
                    netWorkStateView.showNetworkFailLayout();
                    listView.onLoadComplete();
                    listView.setResultSize(-1);
                    break;
                case MESSAGE_EMPTY:
                    listView.setLoadEnable(false);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_otherinfo);
        client = HjDataClient.getInstance(this);

        initIntentData();
        initView();
        initViewState();
        checkNetwork();

    }

    private void initViewState() {

        if (titleView != null) {
            titleView.setTitle(getResources().getStringArray(R.array.favoriteItem)[whereFrom] + "列表");
        }

        if (gameInfoView != null) {
            gameInfoView.setGame(game);
        }

        if (downLoadBar != null) {
            downLoadBar.initDownLoadBar(game, getPageAlias());
        }

    }

    private void initIntentData() {
        // TODO Auto-generated method stub
        Intent intent = getIntent();
        if (intent != null) {
            whereFrom = intent.getIntExtra("whereFrom", FavoriteDbOperator.GameStrategyTag);
            game = (GameBaseDetail) intent.getSerializableExtra("gameDetail");
            intent.removeExtra("whereFrom");
            intent.removeExtra("game");
        }

    }

    private void initView() {
        listView = (AutoListView) findViewById(R.id.game_other_info_listView);
        titleView = (TitleBarView) findViewById(R.id.game_other_info_titleBar);
        titleView.setBackHomeVisibility(View.INVISIBLE);
        titleView.setScrollVerticalViewVisible(View.GONE);
        gameInfoView = (StrategyListInfoView) findViewById(R.id.game_other_info_view);

        netWorkStateView = (NetWorkStateView) findViewById(R.id.game_other_info_netwrokStateView);
        downLoadBar = (DownLoadBarForOther) findViewById(R.id.game_other_info_downLoadBar);
        adapter = new GameOtherInfoAdapter(this, list);
        View headView = LayoutInflater.from(this).inflate(R.layout.header_transparent, null);
        listView.addHeaderView(headView);
        listView.setAdapter(adapter);
        listView.setOnLoadListener(this);
        listView.setOnItemClickListener(this);
        gameInfoView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityActionUtils.JumpToGameDetail(GameOtherInfoActivity.this, game.getId());
            }
        });

        netWorkStateView.setRetryCallBack(this);
    }

    /* 检查网络状态 */
    private void checkNetwork() {
        // TODO Auto-generated method stub
        /* 获取网络数据 */
        if (NetUtils.isConnected(this)) {
            netWorkStateView.showLoadingBar();
            getNetworkData();
            // 联网获取数据
        } else {
            netWorkStateView.showNetworkFailLayout();
        }
    }

    private void getNetworkData() {
        switch (whereFrom) {
            // 获取攻略数据
            case FavoriteDbOperator.GameStrategyTag:
                getStrategyListData();
                break;
            // 获取资讯数据
            case FavoriteDbOperator.GameinfomationTag:
                getInformationListData();
                break;
            // 获取新闻数据
            case FavoriteDbOperator.GameNewsTag:
                getNewsListData();
                break;
            default:
                break;
        }

    }

    private void getStrategyListData() {
        initGameOtherInfoListener();
        client.requestStrategyList(listener, game.getPkgName(), game.getName(), 10, currentPage, HjRequestFrom.hj_gamedetial);

    }

    private void getNewsListData() {
        initGameOtherInfoListener();
        client.requestNewsList(listener, game.getPkgName(), game.getName(), 10, currentPage, HjRequestFrom.hj_gamedetial);

    }

    private void getInformationListData() {
        initGameOtherInfoListener();
        client.requestReviewsList(listener, game.getPkgName(), game.getName(), 10, currentPage, HjRequestFrom.hj_gamedetial);
    }

    private void initGameOtherInfoListener() {
        listener = new IHjRequestNoResultItemListListener<HjInfoListItem>() {

            @Override
            public void onSuccess(long l, boolean b, List<String> arg3, HjAlbumInfo hjAlbumInfo, List<HjInfoListItem> arg4) {
                list.addAll(arg4);
                Message msg1 = mHander.obtainMessage();
                msg1.what = MESSAGE_DATA_READY;
                msg1.obj = arg4.size();
                mHander.sendMessage(msg1);
            }

            @Override
            public void onEmpty() {
                Message msg1 = mHander.obtainMessage();
                msg1.what = MESSAGE_EMPTY;
                msg1.obj = -1;
                mHander.sendMessage(msg1);
            }

            @Override
            public void onResultEmpty(long l, List<String> arg1, List<HjInfoListItem> arg2) {
                list.addAll(arg2);
                Message msg1 = mHander.obtainMessage();
                msg1.what = MESSAGE_DATA_READY;
                msg1.obj = arg2.size();
                mHander.sendMessage(msg1);
            }

            @Override
            public void onFailed(int i, int i1, String s) {
                Message msg1 = mHander.obtainMessage();
                msg1.what = MESSAGE_REQUEST_FAILED;
                msg1.obj = -1;
                mHander.sendMessage(msg1);
            }
        };
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            return;
        }
        HjInfoListItem item = list.get(position - 1);
        ActivityActionUtils.jumpToGameOtherDetail(this, whereFrom, item.id, item.title, game);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        HjDataClient.getInstance(this).release();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onLoad() {
        // TODO Auto-generated method stub
        if (NetUtils.isConnected(this)) {
            getNetworkData();
        } else {
            Message msg1 = mHander.obtainMessage();
            msg1.what = MESSAGE_REQUEST_FAILED;
            msg1.obj = -1;
            mHander.sendMessage(msg1);
        }
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
                setmPageAlias(Constant.PAGE_STRATEGY_LIST);
                break;
            case FavoriteDbOperator.GameinfomationTag: // 资讯
                break;
            case FavoriteDbOperator.GameNewsTag:// 新闻
                break;

            default:
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initIntentData();
        initViewState();
        list.clear();
        currentPage = 1;
        checkNetwork();
    }

}
