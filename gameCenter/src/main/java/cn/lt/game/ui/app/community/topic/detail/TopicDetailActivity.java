package cn.lt.game.ui.app.community.topic.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.event.NetworkChangeEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.view.TitleMoreButton;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host.HostType;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.EventTools;
import cn.lt.game.ui.app.community.model.ClearEvent;
import cn.lt.game.ui.app.community.model.CommentEvent;
import cn.lt.game.ui.app.community.model.ILike;
import cn.lt.game.ui.app.community.model.ShareBean;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.model.TopicEvent;
import cn.lt.game.ui.app.community.topic.detail.CommentView.replyOnclickPositionListener;
import cn.lt.game.ui.app.community.topic.detail.GroupListActionbar.ActionbarOnClickListener;
import cn.lt.game.ui.app.community.topic.detail.GroupListActionbar.ICallBack;
import cn.lt.game.ui.app.community.topic.detail.GroupListActionbar.MeasureHeaderHeight;
import cn.lt.game.ui.app.community.widget.CommentTextView;
import cn.lt.game.ui.app.community.widget.FailedBar;
import cn.lt.game.ui.app.community.widget.LikeTextViewOfTopicDerail;
import cn.lt.game.ui.app.community.widget.ShareDialog;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;
import de.greenrobot.event.EventBus;

/***
 * 话题详情页面
 *
 */
public class TopicDetailActivity extends BaseActivity implements  OnItemClickListener, RefreshAndLoadMoreListView.IScrollTopListener, ICallBack, ActionbarOnClickListener, replyOnclickPositionListener, MeasureHeaderHeight, RetryCallBack, UserInfoUpdateListening, ShareDialog.ItopdetailSortCallback,SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener {
    private static final int DEFAULTVIEW_SIZE = 4; // 不管有没数据 ListView 一定存在3
    public static final int REQUEST_CODE = 5;
    private TitleBarView titleBar;//  标题栏（含返回键）
    private RefreshAndLoadMoreListView listView;
    // private TopicDetailHeaderView headerView;
    private GroupListActionbar actionbar;//内容主view
    private FrameLayout headerViewGroup;
    private TopicDetailListAdapter adapter;
    private ArrayList<ICommentList> itemList = new ArrayList<ICommentList>();
    private int headerViewHeight = 0;// 头部的高度
    private int headerViewWidth = 0;// 头部的宽度
    private int topicId = -1;
    private int groupId = -1;
    private String groupTitle;
    private int actionHeight = 0;
    private CommentLoadingItem loadItem; // listView 里面的LoadView实例
    private CommentHintItem hintItem;
    private Boolean isSuspend = false; // 判断是否悬浮
    // private Boolean intentSuspend = false; // Intent过来的字段，根据此字段让导航条悬浮
    private int replyPosition = -1;
    private LikeTextViewOfTopicDerail likeView;
    private CommentTextView commentTextView;
    private LinearLayout comment;
    private LinearLayout like;
    private NetWorkStateView netWorkStateView;
    private ArrayList<ICommentList> emptyList;
    private UserBaseInfo UserBaseInfo;
    private int comment_count = 0;
    private FailedBar fl_publish_failed;
    private int comment_num = 0;
    private Boolean isForResult = false;
    private Boolean isOnCreate = true;
    public static final String TOPIC_ID = "topicId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topicdetail);
        EventBus.getDefault().register(this);
        if (!isForResult && isOnCreate) {
            getIntentData();
            initView();
            checkNetWork();
        }
    }

    private void getIntentData() {
        Intent intent = getIntent();
        topicId = intent.getIntExtra("topicId", -1);

    }

    private void initView() {
        actionHeight = (int) getResources().getDimension(R.dimen.group_list_actionbar_height);
        titleBar = (TitleBarView) findViewById(R.id.topicDetail_titleBar);
        titleBar.setTitle("小组话题");

        titleBar.setSortCallBack(this);
        titleBar.setBackHomeVisibility(View.VISIBLE);
        likeView = (LikeTextViewOfTopicDerail) findViewById(R.id.tv_like);


        fl_publish_failed = (FailedBar) findViewById(R.id.fl_publish_failed);

        like = (LinearLayout) findViewById(R.id.like);

        commentTextView = (CommentTextView) findViewById(R.id.tv_comment);

        comment = (LinearLayout) findViewById(R.id.comment);

        headerViewGroup = (FrameLayout) findViewById(R.id.topicDetail_headerViewGroup);

        actionbar = (GroupListActionbar) findViewById(R.id.topicDetail_listView_header_actionBar);
        actionbar.setLikeTextViewInstance(likeView);
        actionbar.setTopicId(topicId);
        actionbar.setDataCallBack(this);
        actionbar.setActionbarOnClickListener(this);
        actionbar.setMeasureHeaderHeight(this);

        listView = (RefreshAndLoadMoreListView) findViewById(R.id.topicDetail_listView);
//        listView.setMode(Mode.PULL_FROM_END);
//        listView.getmListView().setOnScrollListener(this);
//        listView.setOnRefreshListener(this);
        listView.setmOnRefrshListener(this);
        listView.setmScrollTopListener(this);
        listView.getmListView().setOnItemClickListener(this);
        adapter = new TopicDetailListAdapter(this, itemList, this, UserBaseInfo);
        netWorkStateView = (NetWorkStateView) findViewById(R.id.activity_topicdetail_netwrokStateView);
        netWorkStateView.setRetryCallBack(this);

//		UserInfoManager.instance().addListening(this);//这个会导致多一次没必要的网络请求

    }

    public void onEventMainThread(NetworkChangeEvent event) {
        //网络改变更新数据就好了，图片会自动根据网络分辨大小
        actionbar.invalidate();
    }

    private void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void checkNetWork() {

        netWorkStateView.showLoadingBar();
        if (NetUtils.isConnected(this)) {
            Net.instance().executeGet(HostType.FORUM_HOST, Uri.getTopicDetailUri(topicId), new WebCallBackToObj<TopicDetail>() {

                @Override
                protected void handle(TopicDetail info) {

                    EventTools.instance().send(EventTools.JUMP_TAG, false, info.getGroupId(), info.getTopicId());
                    initState(info);

                }

                @Override
                public void onFailure(int statusCode, Throwable error) {
                    listView.onLoadingFailed();
                    // TODO Auto-generated method stub
                    if (800 == statusCode) {
                        ToastUtils.showToast(TopicDetailActivity.this, "您查看的话题已删除");
                        finish();
                    }

                    error.printStackTrace();
                    netWorkStateView.showNetworkFailLayout();
                }
            });
        } else {
            netWorkStateView.showNetworkFailLayout();
        }
    }

    protected void initState(TopicDetail info) {
        titleBar.setMoreButtonType(TitleMoreButton.MoreButtonType.TopicDetail);
        titleBar.setTopicDetail(info);
        ShareBean sb = new ShareBean();
        sb.setText(info.topic_title);
        sb.setTitleurl(info.share_link);
        titleBar.setShareBean(sb, ShareDialog.ShareDialogType.TopicDetail);
        groupTitle = info.getGroupTitle();
        topicId = info.getTopicId();
        groupId = info.getGroupId();
        comment_count = info.comment_count;
        actionbar.addListViewHead(info, groupId, groupTitle);
        actionbar.setCommentNum(info.comment_count);
        actionbar.setLikeNum(info.getLikeNum());
        commentTextView.setData(info, CommentTextView.FROM_TOPICDETAIL);
        likeView.setData(info);
        like.setOnClickListener(likeView.getOnClickListener());
        comment.setOnClickListener(commentTextView.getOnClickListener());
        adapter.setInfo(info);
        listView.setAdapter(adapter, false);

    }

//    @Override
//    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//
//    }
//
//    @Override
//    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//
//        if (actionbar != null) {
//            actionbar.getData();
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (itemList.get(position - 1).getType() == AdvItem.ADV_TYPE) {
            itemList.get(position - 1).onClick();
        }

    }

    // 数据回传接口
    @Override
    public void dataCallBack(ArrayList<ICommentList> list, Boolean isScrollTop, ArrayList<ICommentList> emptyList, Boolean isDisabled) {
        this.emptyList = emptyList;
        hintItem = (CommentHintItem) emptyList.get(2);
        loadItem = (CommentLoadingItem) emptyList.get(3);

        loadItem.hide();

        itemList.clear();
        itemList.addAll(list);

        if (list.size() <= DEFAULTVIEW_SIZE) { // 空数据判断 《=2 是因为一定存在2个View
            // 一个是LoadingView
            hintItem.setVisibility(View.VISIBLE);
        } else {
            hintItem.setVisibility(View.GONE);
        }

        if (isScrollTop) {
            listView.getmListView().setSelection(2);

        }

//        listView.onRefreshComplete();
        listView.onLoadMoreComplete();
        notifyDataSetChanged();

        initListViewState(list, isDisabled);

    }

    @Override
    public void dataCallBackOnFail() {
        if (actionbar != null) {
            actionbar.HeaderDestroy();
        }
        netWorkStateView.showNetworkFailLayout();
    }

    private void initListViewState(ArrayList<ICommentList> list, boolean isDisabled) {

        if (isDisabled) {
//            listView.setMode(Mode.DISABLED);
            // TODO: 2017/7/28  honaf
            listView.setRefreshEnabled(false);
            listView.setCanLoadMore(false);
        } else {
//            listView.setMode(Mode.PULL_FROM_END);
            //// TODO: 2017/7/28  honaf
            listView.setCanLoadMore(false);
        }


    }

    public int getScrollY() {
        int scrollY = 0;
        if (itemList.size() > 0) {
            View view = itemList.get(0).getView();
            scrollY = -view.getBottom() + headerViewHeight;
            int position = listView.getmListView().getFirstVisiblePosition();

            if (position > 1) {

                scrollY = headerViewHeight;
            }
        }
        return scrollY;

    }

    public float CalculateAlpha(int scrollY) {
        float alpha = 0f;
        if (scrollY != 0) {
            alpha = (float) -scrollY / headerViewHeight * 255;

            if (alpha < 1) {
                alpha = 0;
            }
        }

        return alpha;

    }

    @Override
    public void commentOnClick(CommentLoadingItem commentLoadingItem, CommentHintItem commentHintItem) {
        initItem(commentLoadingItem, commentHintItem, "还没有人评论");

    }

    @Override
    public void sortOnClick(CommentLoadingItem commentLoadingItem, CommentHintItem commentHintItem) {
        initItem(commentLoadingItem, commentHintItem, "还没有人评论");
    }

    @Override
    public void likeOnClick(CommentLoadingItem commentLoadingItem, CommentHintItem commentHintItem) {
        initItem(commentLoadingItem, commentHintItem, "还没有人点赞");

    }

    private void initItem(CommentLoadingItem commentLoadingItem, CommentHintItem commentHintItem, String hint) {

        initItemList();
        initHintItem(commentHintItem, hint);
        initLoadingItem(commentLoadingItem);

    }

    private void initItemList() {
        itemList.clear();
        itemList.addAll(emptyList);
        notifyDataSetChanged();
    }

    // 隐藏空数据Item项
    private void initHintItem(CommentHintItem commentHintItem, String hint) {
        if (commentHintItem != null) {
            hintItem = commentHintItem;
        }

        hintItem.setHint(hint);
        hintItem.setVisibility(View.GONE);
    }

    // 显示菊花Item项
    private void initLoadingItem(CommentLoadingItem commentLoadingItem) {
        if (commentLoadingItem != null) {
            loadItem = commentLoadingItem;
        }

        loadItem.show();
    }

    // 暂时没用 获取自己回复的位置
    @Override
    public void positionListener(int position) {

        replyPosition = position;
    }

    @Override
    protected void onResume() {
//        if (!isForResult && isOnCreate) {
//            getIntentData();
//            initView();
//            checkNetWork();
//        }

        UserBaseInfo = UserInfoManager.instance().getUserInfo();
        adapter.setUserBaseInfo(UserBaseInfo);
        notifyDataSetChanged();

        isForResult = false;
        super.onResume();
    }

    // 点赞Event事件
    public void onEventMainThread(ILike data) {
        actionbar.setLikeNum(data.getLikeNum());
        actionbar.getLikeData();

//		likeView.setData(data);
    }

    // 评论Event事件
    public void onEventMainThread(CommentEvent info) {
        if (info.getResult() && actionbar != null) {
//			actionbar.refreshData();
            if (!this.isFinishing()) {
                checkNetWork();
            }
        }

        if (!info.getResult()) {
            fl_publish_failed.setToFont();
        } else {
            fl_publish_failed.setVisible(View.GONE);
        }
    }

    /***
     * 有内容发表失败，弹出草稿箱通知栏
     *
     * @param event
     */
    public void onEventMainThread(TopicEvent event) {
        if (!event.isResult()) {
            fl_publish_failed.setToFont();
        } else {
            fl_publish_failed.setVisible(View.GONE);
        }
    }

//	/***
//	 * 发表回复
//	 *
//	 * @param event
//	 */
//	public void onEventMainThread(ReplyEvent event) {
//		if (event.isResult() && actionbar != null) {
//			actionbar.refreshData();
//		}
//		if (!event.isResult()) {
//			fl_publish_failed.setToFont();
//		} else {
//			fl_publish_failed.setVisible(View.GONE);
//		}
//	}

    /***
     * 如果草稿箱为空，同步清除草稿箱通知栏
     *
     * @param event
     */
    public void onEventMainThread(ClearEvent event) {
        if (event.isResult()) {
            fl_publish_failed.setVisible(View.GONE);
        } else {
            fl_publish_failed.setToFont();
        }
    }

    @Override
    public void headerHeight(int w, int h) {
        headerViewHeight = h;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, h + actionHeight);
        headerViewGroup.setLayoutParams(params);
        headerViewGroup.requestLayout();
        netWorkStateView.hideNetworkView();
        notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if (actionbar != null) {
            actionbar.HeaderDestroy();
        }
        titleBar.release();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void retry() {
        checkNetWork();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (actionbar != null) {
            actionbar.HeaderDestroy();
            actionbar.initActionBar();
        }

        isOnCreate = false;
        comment_num = 0;
        getIntentData();
        actionbar.setTopicId(topicId);
        checkNetWork();

    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_TOPIC_DETAIL);
    }

    @Override
    public void userLogin(UserBaseInfo userBaseInfo) {
        if (!this.isFinishing()) {
            checkNetWork();
        }

    }

    @Override
    public void updateUserInfo(UserBaseInfo userBaseInfo) {
        // TODO Auto-generated method stub

    }

    @Override
    public void userLogout() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        if (requestCode == REQUEST_CODE) {

            isForResult = true;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
    排序回调接口
    * */
    @Override
    public void sortCallBack(Orderby orderby) {
        actionbar.setSort(orderby);
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {

    }

    /**
     * Called when the list reaches the last item (the last item is visible
     * to the user)
     */
    @Override
    public void onLoadMore() {
        if (actionbar != null) {
            actionbar.getData();
        }
    }


    @Override
    public void onScrollTop(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int scrollY = getScrollY();
        int max = Math.max(-scrollY, -headerViewHeight);
        isSuspend = max == -headerViewHeight;

        actionbar.setGradientViewAlpha(CalculateAlpha(max));
        headerViewGroup.setTranslationY(max);
    }
}
