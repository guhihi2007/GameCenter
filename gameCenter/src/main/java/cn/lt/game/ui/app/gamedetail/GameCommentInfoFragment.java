package cn.lt.game.ui.app.gamedetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.db.service.DownFileService;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.detail.GameCommentDomainDetail;
import cn.lt.game.domain.detail.GameDomainDetail;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.install.ApkInstallManger;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.util.AppIsInstalledUtil;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.GameDetailInfoView;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.lib.widget.MessageDialog.LeftBtnClickListener;
import cn.lt.game.lib.widget.MessageDialog.RightBtnClickListener;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.common.listener.InstallButtonClickListener;
import cn.lt.game.ui.common.quickadpter.BaseAdapterHelper;
import cn.lt.game.ui.common.quickadpter.QuickAdapter;
import cn.lt.game.ui.installbutton.DetailInstallButton;
import de.greenrobot.event.EventBus;

/***
 * 游戏详情-评论页面
 *
 * @author ltbl
 */
public class GameCommentInfoFragment extends BaseFragment implements RetryCallBack, OnClickListener {
    private View mView;
    private GameDetailInfoView mHeadView;
    private ListView listView;
    private NetWorkStateView netWorkStateView;
    private DrawableCenterTextView downloadView;// 下载文本显示条
    private Button btn_comment;// 我要评论按钮
    private int currentPage = 1;// 当前页码
    private int totalPage;// 总页数
    private QuickAdapter<BaseUIModule> adapter;
    private DetailInstallButton installButton;// 下载按钮
    private ProgressBar downProgress;
    private boolean isInstalled = false;// 判断是否已安装
    private GameBaseDetail game = null;
    private TextView network_goDownLoading;
    private int gameId;
    //    private ListView lv;
    private GameDomainDetail gameDomainDetail;
    private int groupId;//社区小组ＩＤ

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_GAME_DETAIL_COMMENT_LIST);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.gamedetail_community_fragment, container, false);
        initView();
        return mView;
    }

    /***
     *获取游戏详情信息
     */
    public void requestGameDetailData() {
        game = ((GameDetailHomeActivity) (getActivity())).getAdapterGameDetail();
        gameDomainDetail = ((GameDetailHomeActivity) getActivity()).getGameDomainDetail();
        initHeadView(game);
        initBottomButton(game);
    }


    /***
     * 初始化控件
     */
    private void initView() {
        listView = (ListView) mView.findViewById(R.id.gamedetail_comment_listView);
        netWorkStateView = (NetWorkStateView) mView.findViewById(R.id.detail_comment_netwrolStateView);
        network_goDownLoading = (TextView) mView.findViewById(R.id.network_goDownLoading);
        network_goDownLoading.setOnClickListener(this);
        netWorkStateView.showLoadingBar();
        netWorkStateView.setRetryCallBack(this);
        downloadView = (DrawableCenterTextView) mView.findViewById(R.id.downloadView);
        btn_comment = (Button) mView.findViewById(R.id.btn_comment);
        downProgress = (ProgressBar) mView.findViewById(R.id.download_progress_bar);
        mHeadView = new GameDetailInfoView(mActivity);
        listView.addHeaderView(mHeadView);

        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.game_comment_list_title, null);
        listView.addHeaderView(headView);

        View footView = LayoutInflater.from(getActivity()).inflate(R.layout.comment_item_foot_view, null);
        listView.addFooterView(footView);

        installButton = new DetailInstallButton(game, downProgress, downloadView, "我要评论");
    }

    /***
     * 初始化评论按钮
     */
    private void initBottomButton(GameBaseDetail game) {
        GameBaseDetail downloadgame = DownFileService.getInstance(mActivity).getDownFileById(gameId);
        // 判断是否安装过
        isInstalled = AppIsInstalledUtil.isInstalled(mActivity, game.getPkgName());
        //初始化评论/下载按钮状态，如果游戏已经安装过则隐藏评论按钮，否则显示评论按钮
        if (downloadgame != null) {
            btn_comment.setVisibility(View.GONE);
            downloadView.setVisibility(View.VISIBLE);
            downProgress.setVisibility(View.VISIBLE);
            installButton.game = game;
            downloadView.setOnClickListener(new InstallButtonClickListener(mActivity, game, installButton, getPageAlias()));
            // 更新安装按键状态
            updateProgressState();

        } else {
            btn_comment.setVisibility(View.VISIBLE);
            downloadView.setVisibility(View.GONE);
            downProgress.setVisibility(View.GONE);
        }
        btn_comment.setOnClickListener(this);
    }

    /***
     * 更新下载进度
     */
    public void updateProgressState() {
        if (game == null) {
            return;
        }
        GameBaseDetail downFile = FileDownloaders.getDownFileInfoById(gameId);
        if (downFile != null) {
            game.setDownInfo(downFile);
        } else {
            game.setState(DownloadState.undownload);
            game.setDownLength(0);
        }
        int state = game.getState();

        if (game.isCoveredApp) {
            state = InstallState.installComplete;
        }

        if (state == InstallState.installComplete) {
            downloadView.setVisibility(View.GONE);
            btn_comment.setVisibility(View.VISIBLE);
        }
        installButton.setViewBy(state, game.getDownPercent());

    }

    @Override
    public void onResume() {
        try {
            gameId = Integer.parseInt(mActivity.getIntent().getStringExtra("id"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        /* 检查网络状态，状态正常则联网获取数据初始化界面 */
        checkNetwork();
        super.onResume();
    }

    /***
     * 获取网络数据
     */
    private void checkNetwork() {
        if (NetUtils.isConnected(mActivity)) {
            requestData();// 联网获取数据
        } else {
            ToastUtils.showToast(mActivity, "网络连接失败");
            netWorkStateView.showNetworkFailLayout();
        }
    }

    /**
     * 请求游戏社区信息
     */


    /**
     * 请求网络
     */
    private void requestData() {
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.getGameCommentsUri(gameId + ""), null, new WebCallBackToObject<UIModuleList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                netWorkStateView.showNetworkFailLayout();
            }

            @Override
            protected void handle(UIModuleList info) {
                UIModuleList uiData = info;
                uiModuleList = new ArrayList<>();
                uiModuleList.addAll(uiData);
                processData(uiModuleList);
            }
        });
    }

    /***
     * 处理网络请求
     */
    private List<BaseUIModule> uiModuleList;


    public void onEventMainThread(DownloadUpdateEvent updateEvent) {
        if (updateEvent == null || updateEvent.game == null || game == null) return;
        if (updateEvent.game.getId() == game.getId()) {
            updateProgressState();
        }
    }

    /***
     * 处理请求数据
     *
     * @param commentList
     */
    private void processData(List<BaseUIModule> commentList) {
        if (commentList == null || commentList.size() <= 0) {
            netWorkStateView.setNotDataState(NetWorkStateView.publishComment);
            netWorkStateView.setNoDataCatSyle(NetWorkStateView.CatStyle.SINISTER_SMILE);
            netWorkStateView.setNoDataLayoutText("暂时没有评论", "我要评论");
            netWorkStateView.showNetworkNoDataLayout();
        } else {
            netWorkStateView.hideNetworkNoDataLayout();
            netWorkStateView.hideNetworkView();
            if (isFirstPage()) {
                setAdapter();
            } else {
                adapter.addAll(uiModuleList);
            }
        }
    }

    /***
     * 初始化评论详情头部信息
     *
     * @param game
     */
    private void initHeadView(GameBaseDetail game) {
        mHeadView.setGame(game);
    }


    private void setAdapter() {
        adapter = new QuickAdapter<BaseUIModule>(mActivity, R.layout.game_detail_comment_item, uiModuleList) {
            @Override
            protected void convert(BaseAdapterHelper helper, BaseUIModule item) {
                UIModule uimodule = (UIModule) item;
                GameCommentDomainDetail commDetail = (GameCommentDomainDetail) uimodule.getData();
                helper.setText(R.id.tv_userName, commDetail.getNickname());
                helper.setText(R.id.tv_comment_content, commDetail.getContent());
            }
        };
        listView.setAdapter(adapter);
    }

    protected boolean isFirstPage() {
        return currentPage == 1;
    }

    @Override
    public void retry() {
        checkNetwork();
    }

    private MessageDialog promptDialog;

    private void prepareDownload() {
        if (isInstalled || game.getState() == InstallState.installComplete) {
            Intent intent = new Intent(mActivity, SendGameDetailCommentActivity.class);
            intent.putExtra("gameId", gameId);
            mActivity.startActivity(intent);
        } else {
            promptDialog = new MessageDialog(mActivity, "提示", "安装该游戏后才能进行评论", "取消", "安装");
            promptDialog.setRightOnClickListener(new RightBtnClickListener() {
                @Override
                public void OnClick(View view) {
                    btn_comment.setVisibility(View.GONE);
                    downloadView.setVisibility(View.VISIBLE);
                    downProgress.setVisibility(View.VISIBLE);
                    downloadView.setOnClickListener(new InstallButtonClickListener(mActivity, game, installButton, getPageAlias()));
                    if (game != null) {
                        if (game.getState() == InstallState.install) {
                            ApkInstallManger.self().installPkg(game, Constant.MODE_SINGLE, null, false);
                        } else {
                            Utils.gameDown(mActivity, game, getPageAlias(), true, Constant.MODE_SINGLE, Constant.DOWNLOAD_TYPE_NORMAL, null);
                        }
                    }
                    // 更新安装按键状态
                    updateProgressState();
                    promptDialog.dismiss();
                    if (uiModuleList.size() == 0) {
                        EventBus.getDefault().post("selectTab");
                    }
                }
            });
            promptDialog.setLeftOnClickListener(new LeftBtnClickListener() {
                @Override
                public void OnClick(View view) {
                    promptDialog.dismiss();
                }
            });
            promptDialog.show();

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_comment:
                preDownload();
                break;
            case R.id.network_goDownLoading:
                String btnStr = network_goDownLoading.getText().toString();
                if ("我要评论".equals(btnStr)) {
                    preDownload();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void preDownload() {
        if (NetUtils.isConnected(mActivity)) {
            prepareDownload();
        } else {
            ToastUtils.showToast(mActivity, "沒有可用的网络");
        }
    }
}
