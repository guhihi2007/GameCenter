package cn.lt.game.ui.app.gamedetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

import com.flyco.tablayout.SlidingTabLayout;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragmentActivity;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.detail.GameDomainDetail;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.view.TitleMoreButton;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.community.model.Group;
import cn.lt.game.ui.app.community.model.ShareBean;
import cn.lt.game.ui.app.community.widget.ShareDialog;
import de.greenrobot.event.EventBus;

/***
 * 游戏详情
 *
 * @author atian
 */
public class GameDetailHomeActivity extends BaseFragmentActivity implements NetWorkStateView.RetryCallBack {
    private SlidingTabLayout indicator;
    public GameDetailPagerAdapter adapter;
    private TitleBarView titleBarView;
    private NetWorkStateView netWorkStateView;
    private ViewPager pager;
    public int mID;
    //    private int commentCount;//游戏评论数
    private int groupId;//社区小组ＩＤ
    public String pushId;//推送id


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (bundle != null) {
            try {
                mID = Integer.parseInt(bundle.getString("mID", ""));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                finish();
            }
        }
        setContentView(R.layout.activity_gamedetailhome);
        initView();
        try {
            mID = Integer.parseInt(getIntent().getStringExtra("id"));
            pushId = (getIntent().getStringExtra("pushId"));
            Log.i("zzz", "游戏 ID==" + mID + "push ID==" + pushId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
//        requestData();//请求游戏社区信息
        requestCoummunityData();//请求游戏详情信息
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pager.getCurrentItem() == 0) {
            adapter.getItem(0).setUserVisibleHint(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getIntent().removeExtra("id");
        getIntent().removeExtra("isPush");
        EventBus.getDefault().unregister(this);

        // 回收游戏截图的Bitmap,防止内存溢出
        if (adapter.getItem(0) != null && adapter.getItem(0) instanceof GameInfoFragment) {
            ((GameInfoFragment) adapter.getItem(0)).recycleBitmaps();
        }
    }

    private void initView() {
        netWorkStateView = (NetWorkStateView) findViewById(R.id.game_detail_netWrokStateView);
        netWorkStateView.showLoadingBar();
        netWorkStateView.setRetryCallBack(this);
        if (adapter == null) {
            adapter = new GameDetailPagerAdapter(getSupportFragmentManager(), this);
        }
        pager = (ViewPager) findViewById(R.id.gamedetail_pager);
        pager.setAdapter(adapter);
        indicator = (SlidingTabLayout) findViewById(R.id.gamedetail_indicator);
        indicator.setViewPager(pager);
        titleBarView = (TitleBarView) findViewById(R.id.detail_action_bar);
        titleBarView.setMoreButtonType(TitleMoreButton.MoreButtonType.GameDetail);
        titleBarView.hideShareBtn();
    }

    @Override
    public void setNodeName() {
        setmNodeName("");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Log.i("zzz", "销毁前ID==" + getIntent().getStringExtra("id"));
        bundle.putString("mID", getIntent().getStringExtra("id"));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            try {
                mID = Integer.parseInt(savedInstanceState.getString("mID"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                finish();
            }
        }
    }

    private void requestCoummunityData() {
        Net.instance().executeGet(Host.HostType.FORUM_HOST, Uri.getForumInfoUri(String.valueOf(mID)), null, new WebCallBackToObj<Group>() {
            @Override
            protected void handle(Group info) {
                if (null != info) {
                    groupId = info.group_id;
                    LogUtils.i("zzz", "社区 ID:" + groupId);
                    requestGameDetailData();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                requestGameDetailData();
            }

        });
    }

    /**
     * 请求游戏详情信息
     */
    private void requestGameDetailData() {
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.getGameDetailUriByIdOrPkgName(mID + ""), null, new WebCallBackToObject<UIModuleList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                if (statusCode == 404) {
                    netWorkStateView.showNetworkNoDataLayout();
                    netWorkStateView.setNoDataLayoutText("该游戏已下架", "");
                    titleBarView.hideShareBtn();
                    titleBarView.hideSearchBtn();
                    if (getIntent().getBooleanExtra("isPush", false)) {
                        DCStat.pushEvent(pushId, "" + mID, "Game", "clicked", getIntent().getBooleanExtra("isFromWakeUp", false) ? "WAKE_UP" : Constant.PAGE_GE_TUI, "", ""); //下架时不会请求Fagment的数据
                    }
                } else {//请求失败；
                    netWorkStateView.showNetworkFailLayout();
                    titleBarView.hideShareBtn();

                    titleBarView.hideSearchBtn();
                }
            }

            @Override
            protected void handle(UIModuleList info) {

                netWorkStateView.hideNetworkView();
                UIModuleList uiData = info;
                UIModule uiModule = (UIModule) uiData.get(0);
                GameDomainDetail domainDetail = (GameDomainDetail) uiModule.getData();
                int comment_count = domainDetail.getExtraDetail().getCommentCnt();
                LogUtils.i("zzz", "评论数：" + comment_count);
                GameBaseDetail game = new GameBaseDetail();// 存放游戏信息
                updateCommentCount(groupId, comment_count);
                game.setGameDetail(domainDetail);
                adapter.setGame(game);
                adapter.setGameDomainDetail(domainDetail);
                initTitleBar(game);
                ((GameInfoFragment) adapter.getItem(0)).getData();
                ((GameCommentInfoFragment) adapter.getItem(1)).requestGameDetailData();
                titleBarView.displayShareBtn();
                titleBarView.displaySearchBtn();
            }
        });
    }

    /***
     * 初始化导航栏
     *
     * @param game
     */
    private void initTitleBar(GameBaseDetail game) {
        titleBarView.setTitle(game.getName());
        ShareBean sb = new ShareBean();
//        sb.setText("我在游戏中心发现了一款非常好玩的游戏-" + game.getName() + "。一起来玩吧！赶快来下载哦");
        LogUtils.i(LogTAG.shareTAG, "Review = " + game.getReview());
        LogUtils.i(LogTAG.shareTAG, "Description = " + game.getDescription());
        String description = game.getDescription().replace("<p>&nbsp;</p>", "").replace("<p>", "").replace("</p>", "").replace("<br/>", "").replace("<br />", "");
        LogUtils.i(LogTAG.shareTAG, "Description(格式化后) = " + description);
        sb.setText(TextUtils.isEmpty(game.getReview()) ? description : game.getReview());
        sb.setTitleurl(game.getDownUrl());
        sb.shareType = ShareBean.GAME;
        sb.setTitle(game.getName());
        sb.setGameIconUrl(game.getLogoUrl());
        titleBarView.setShareBean(sb, ShareDialog.ShareDialogType.gameDetail);
    }

    /***
     * 刷新评论数
     */
    public void updateCommentCount(int groupId, int commentCount) {
        LogUtils.i("zzz", "刷新选项卡" + groupId);
        String commentText = commentCount >= 9999 ? ("评论(" + commentCount + "+)") : ("评论(" + commentCount + ")");
        if (groupId == 0) {
            String[] mm = new String[]{"游戏详情", commentText};
            adapter.setmTabTile(mm);
            LogUtils.i("zzz", "不加载社区");
            adapter.setFragment(false);
        } else {
            String[] mmm = new String[]{"游戏详情", commentText, "社区"};
            adapter.setmTabTile(mmm);
            LogUtils.i("zzz", "加载社区");
            adapter.setFragment(true);
        }
        indicator.notifyDataSetChanged();
    }
    @Override
    public void retry() {
        requestCoummunityData();
    }

    /***
     * 提供给Fragment调用游戏详情信息
     *
     * @return
     */
    public GameBaseDetail getAdapterGameDetail() {
        return adapter.getGame();
    }

    public GameDomainDetail getGameDomainDetail() {
        return adapter.getGameDomainDetail();
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupId() {
        return this.groupId;
    }

}
