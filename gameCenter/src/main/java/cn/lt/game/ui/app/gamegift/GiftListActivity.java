package cn.lt.game.ui.app.gamegift;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.db.operation.FavoriteDbOperator;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.DownLoadBarForGift;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.app.adapter.LTBaseAdapter;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListenerImpl;
import cn.lt.game.ui.app.adapter.parser.NetDataAddShell;
import cn.lt.game.ui.app.gamegift.view.GiftInfoView;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;
import de.greenrobot.event.EventBus;

public class GiftListActivity extends BaseActivity implements RetryCallBack, UserInfoUpdateListening, RefreshAndLoadMoreListView.OnLoadMoreListener {

    public static final String GAME_ID = "game_id";
    private static final String TAG = "GiftListActivity";
    private ListView mListViewInPull;
    private RefreshAndLoadMoreListView mPullListView;
    private GiftInfoView mGiftMsgView;
    private DownLoadBarForGift mDownloadBar;
    private LTBaseAdapter mAdapter;
    private BaseOnclickListener mClickListener;
    private String mGameId = "";
    private int mTotalPage;
    private int mCurrPage = 1;
    /**
     * 网络相关布局视图
     */
    private NetWorkStateView mNetWorkStateView;

    private RelativeLayout mDownloadRoot;

    private GiftDomainDetail mTitleGiftInfo;
    private int mlastPagePositionOfNetData = 0;

    private String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gift_list);
        tag = UUID.randomUUID().toString();
        UserInfoManager.instance().addListening(this);
        EventBus.getDefault().register(this);
        mClickListener = new BaseOnclickListenerImpl(this, getPageAlias());
        getIntentData();
        initView();
        checkNetWork();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDownloadBar != null) {
            mDownloadBar = null;
        }
        EventBus.getDefault().unregister(this);
        UserInfoManager.instance().removeListening(this);
    }

    @Override
    protected void onResume() {
        if (mDownloadBar != null) {
            // 更新安装按键状态
            mDownloadBar.updateProgressState();
        }
        mEventID = mGameId;
        super.onResume();
    }

    public void onEventMainThread(GiftDomainDetail info) {
        if (info != null && mGameId.equals(info.getGame().getUniqueIdentifier())) {
            checkNetWork();
        }
    }

    private void getIntentData() {
        mGameId = getIntent().getStringExtra(GAME_ID);
    }

    private void initView() {
        mDownloadRoot = (RelativeLayout) findViewById(R.id.gift_list_download);
        mPullListView = (RefreshAndLoadMoreListView) findViewById(R.id.gift_list_listView);
        mGiftMsgView = (GiftInfoView) findViewById(R.id.gift_list_msgLayout);
        mDownloadBar = (DownLoadBarForGift) findViewById(R.id.gift_downlaodBar);
        mNetWorkStateView = (NetWorkStateView) findViewById(R.id.rank_netwrolStateView);
        mNetWorkStateView.setRetryCallBack(this);
        iniListView();
    }

    private void iniListView() {
        mListViewInPull = mPullListView.getmListView();
        mAdapter = new LTBaseAdapter(this, mClickListener);
        mPullListView.setAdapter(mAdapter, false);
        mPullListView.setRefreshEnabled(false);
        mPullListView.setOnLoadMoreListener(this);
    }

    /**
     * 检查网络，如果有网路则请求网络数据，无网络显示无网络界面；
     */
    private void checkNetWork() {
        if (NetUtils.isConnected(this)) {
            mNetWorkStateView.showLoadingBar();
            mGiftMsgView.setVisibility(View.GONE);
            mDownloadRoot.setVisibility(View.GONE);
            mPullListView.setVisibility(View.VISIBLE);
            mCurrPage = 1;
            mlastPagePositionOfNetData = 0;
            requestData();
        } else {
            inflateNetWorkErrView();
        }
    }

    /**
     * 初始化网络错误提示界面；
     */
    private void inflateNetWorkErrView() {
        mNetWorkStateView.showNetworkFailLayout();
        mGiftMsgView.setVisibility(View.GONE);
        mDownloadRoot.setVisibility(View.GONE);
        mPullListView.setVisibility(View.GONE);
    }


    private void initDownloadBar() {
        if (mDownloadBar != null) {
            mDownloadBar.initDownLoadBar(mTitleGiftInfo, FavoriteDbOperator.GAMEDETAIL_TABLE_NAME, getPageAlias());
        }
    }

    private void fillGameInfo() {
        mGiftMsgView.fillView(mTitleGiftInfo);
    }

    @Override
    public void retry() {
        checkNetWork();
    }


    @Override
    public void userLogin(UserBaseInfo userBaseInfo) {
        if (!isFinishing()) {
            if (mAdapter != null) {
                mAdapter.resetList();
            }
            checkNetWork();
        }
    }

    @Override
    public void updateUserInfo(UserBaseInfo userBaseInfo) {

    }

    @Override
    public void userLogout() {
        if (!this.isFinishing()) {
            checkNetWork();
        }
    }

    @Override
    public void setPageAlias() {
        // TODO Auto-generated method stub
        setmPageAlias(Constant.PAGE_GIFT_LIST, mGameId);
    }

    private void setMlastPagePositionOfNetData(List<ItemData<? extends BaseUIModule>> temp) {
        try {
            if (temp != null && temp.size() > 0) {
                ItemData item = temp.get(temp.size() - 1);
                this.mlastPagePositionOfNetData = item.getPos();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPage(int loadingPage, UIModuleList list) {
        List<ItemData<? extends BaseUIModule>> temp = null;
        if (loadingPage == 1) {
            int count = list.size();
            for (int i = 0; i < count; i++) {
                UIModule module = (UIModule) list.get(i);
                if (PresentType.game_gifts_summary == module.getUIType()) {
                    mTitleGiftInfo = (GiftDomainDetail) ((UIModule) list.remove(i)).getData();
                    break;
                }
            }
            fillGameInfo();
            initDownloadBar();
            mAdapter.resetList();
        }
        temp = NetDataAddShell.wrapModuleList(list, mlastPagePositionOfNetData);
        int count = temp.size();
        for (int i = 0; i < count; i++) {
            ItemData<? extends BaseUIModule> item = temp.get(i);
            UIModule module = (UIModule) item.getmData();
            GiftDomainDetail gift = (GiftDomainDetail) module.getData();
            gift.setGame(mTitleGiftInfo.getGame());
        }
        setMlastPagePositionOfNetData(temp);
        mAdapter.addList(temp);
        if (mCurrPage < mTotalPage) {
            mCurrPage = loadingPage + 1;
        } else if (mCurrPage >= mTotalPage) {
            mPullListView.setCanLoadMore(false);
        }
    }


    /**
     * 请求网络
     */
    private void requestData() {

        final Map<String, String> params = new HashMap<>();
        params.put("id", mGameId);
        params.put("page", String.valueOf(mCurrPage));
        mNetWorkStateView.showLoadingBar();

        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.getGiftListUri(mGameId), params, new WebCallBackToObject<UIModuleList>() {

            /**
             * 网络请求出错时调用
             *
             * @param statusCode 异常编号
             * @param error      异常信息
             */
            @Override
            public void onFailure(int statusCode, Throwable error) {
                mPullListView.setCanLoadMore(mCurrPage <= mTotalPage);
                mPullListView.onLoadMoreComplete();
                if (statusCode == 404) {
                    mGiftMsgView.setVisibility(View.GONE);
                    mDownloadRoot.setVisibility(View.GONE);
                    mPullListView.setVisibility(View.GONE);
                    mNetWorkStateView.showNetworkNoDataLayout();
                    mNetWorkStateView.setNoDataLayoutText("该游戏已下架", "");
                } else {//请求失败；
                    inflateNetWorkErrView();
                }
            }

            @Override
            protected void handle(UIModuleList list) {
                mTotalPage = getLastPage();
                setPage(Integer.valueOf(params.get("page")), list);
                mGiftMsgView.setVisibility(View.VISIBLE);
                mDownloadRoot.setVisibility(View.VISIBLE);
                mNetWorkStateView.hideLoadingBar();
                mPullListView.setCanLoadMore(mCurrPage <= mTotalPage);
                mPullListView.onLoadMoreComplete();
            }
        });
    }

    @Override
    public void onLoadMore() {
        requestData();
    }
}
