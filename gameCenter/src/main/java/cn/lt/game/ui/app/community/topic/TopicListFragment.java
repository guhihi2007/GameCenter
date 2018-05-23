package cn.lt.game.ui.app.community.topic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host.HostType;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.CommunityActivity;
import cn.lt.game.ui.app.community.CommunityActivity.CloseType;
import cn.lt.game.ui.app.community.CommunityBaseFragment;
import cn.lt.game.ui.app.community.model.ClearEvent;
import cn.lt.game.ui.app.community.model.CommentEvent;
import cn.lt.game.ui.app.community.model.ILike;
import cn.lt.game.ui.app.community.model.ReplyEvent;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.model.TopicDetails;
import cn.lt.game.ui.app.community.model.TopicEvent;
import cn.lt.game.ui.app.community.widget.CircleRefreshView;
import cn.lt.game.ui.app.community.widget.FailedBar;
import cn.lt.game.ui.app.community.widget.ShareDialog;
import de.greenrobot.event.EventBus;

/**
 * 热门话题
 */
@SuppressLint("ValidFragment")
public class TopicListFragment extends CommunityBaseFragment implements RetryCallBack, SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener {

    private View mView;
    private RefreshAndLoadMoreListView listView;
    private int currentPage = 1;
    private int totalPage = 1;
    private TopicListAdapter adapter;
    private String uri;
    private NetWorkStateView networkView;
    private int titleResId;
    private FailedBar fl_publish_failed;
    private CircleRefreshView mCircleView;


    public static TopicListFragment newInstance(int titleResId) {
        TopicListFragment newFragment = new TopicListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("titleResId", titleResId);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public void setPageAlias() {
        switch (titleResId) {
            case R.string.published_topic:
                setmPageAlias(Constant.PAGE_TOPIC_MINE);// 统计页面（我发表的话题）
                break;
            case R.string.collected_topic:
                setmPageAlias(Constant.PAGE_COLLECT_MINE);// 统计页面（我的收藏）
                break;
            case R.string.new_topic:
                setmPageAlias(Constant.PAGE_TOPIC_HOT);// 统计页面（热门话题）
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle args = getArguments();
        this.titleResId = args.getInt("titleResId");
        switch (titleResId) {
            case R.string.published_topic:
                uri = Uri.COM_USERS_TOPIC;
                break;
            case R.string.collected_topic:
                uri = Uri.COM_USERS_COLLECT_TOPICS;
                break;
            case R.string.new_topic:
                uri = Uri.COM_TOPIC_NEW;
                break;
            default:
                break;
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_newtopic, container, false);
        initView();
        initFailedBar();
        requestData(currentPage);
        return mView;
    }

    /***
     * 有内容发表失败，弹出草稿箱通知栏
     *
     * @param event
     */
    public void onEventMainThread(TopicEvent event) {
        if (!event.isResult()) {
            Log.i("zzz", "话题发送失败！");
            fl_publish_failed.setToFont();
        } else {
//			requestData();// 发表成功，刷新页面
//			listView.setRefreshing(true);
            dealPullRefresh();
            Log.i("zzz", "话题发送成功，刷新话题列表页面！");
            fl_publish_failed.setVisible(View.GONE);
        }
    }

    /**
     * 刷新点赞
     *
     * @param data
     */
    public void onEventMainThread(ILike data) {
        ArrayList<TopicDetail> list = adapter.getList();
        for (TopicDetail detail : list) {
            if (data.getTopicId() == detail.getTopicId() && data.getGroupId() == detail.getGroupId()) {
                detail.setLikeNum(data.getLikeNum());
                detail.setLiked(data.isLiked());
            }
        }
        adapter.notifyDataSetChanged();

    }

    /***
     * 取消收藏重新请求最新数据
     * @param event
     */
    public void onEventMainThread(ShareDialog.CollectEventBean event) {
        if ("refreshCollectData".equals(event.tag)) {
            if (!mActivity.isFinishing()) {
                Log.i("zzz", "取消收藏，刷新数据！");
//				listView.setRefreshing(true);
                dealPullRefresh();
            }
        }
    }

    public void onEventMainThread(String s) {
        if ("取消收藏".equals(s)) {
            networkView.showNetworkNoDataLayout();
            // 收藏为空时，设置此页面不能再下拉刷新
            //// TODO: 2017/7/28  honaf
//			listView.setMode(Mode.DISABLED);
            listView.setRefreshEnabled(false);
            listView.setCanLoadMore(false);
        }
        // 用户点击过提示栏以后让其他所有地方的提示栏消失
        if ("hideFailedBar".equals(s)) {
            fl_publish_failed.setVisible(View.GONE);
        }
    }

    /***
     * 发表评论
     *
     * @param event
     */
    public void onEventMainThread(CommentEvent event) {
        if (!event.getResult()) {
            Log.i("zzz", "评论发送失败！");
            fl_publish_failed.setToFont();
        } else {
            fl_publish_failed.setVisible(View.GONE);
        }
    }

    /***
     * 发表回复
     *
     * @param event
     */
    public void onEventMainThread(ReplyEvent event) {
        if (!event.isResult()) {
            Log.i("zzz", "回复发送失败！");
            fl_publish_failed.setToFont();
        } else {
            fl_publish_failed.setVisible(View.GONE);
        }
    }

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

    private void initView() {
        // 添加用户信息动态监听
        initListView();
        initNetworkView();
    }

    private void initFailedBar() {
        fl_publish_failed = (FailedBar) mView.findViewById(R.id.fl_publish_failed);
    }

    private void initNetworkView() {
        networkView = (NetWorkStateView) mView.findViewById(R.id.group_netWrokStateView);
        networkView.showLoadingBar();
        networkView.setRetryCallBack(this);
    }

    private void initListView() {
        listView = (RefreshAndLoadMoreListView) mView.findViewById(R.id.topic_listView);
        listView.setOnLoadMoreListener(this);
        listView.setmOnRefrshListener(this);
        adapter = new TopicListAdapter(mActivity, titleResId);
        listView.setAdapter(adapter, false);
        mCircleView = (CircleRefreshView) mView.findViewById(R.id.fa_button);
        mCircleView.setOnclick(new CircleRefreshView.OnClickToRefresh() {
            @Override
            public void onClick() {
                listView.getmListView().smoothScrollToPosition(0);
                retry();
            }
        });
        listView.setVisibility(View.GONE);
        listView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.margin_size_8dp));
    }


    private void dealPullRefresh() {
        currentPage = 1;
        mCircleView.startRotate();// 只要请求网络就开始旋转小球
        requestData(currentPage);
    }

    private void dealLoadRefresh() {
        requestData(++currentPage);
    }

    protected boolean isFirstPage() {
        return currentPage == 1;
    }

    protected void initPage() {
        currentPage = 1;
    }

    protected int nextPage() {
        return ++currentPage;
    }

    private void requestData(final int page) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", page + "");
        Net.instance().executeGet(HostType.FORUM_HOST, uri, params, new WebCallBackToObj<TopicDetails>() {
            @Override
            protected void handle(TopicDetails info) {
                networkView.hideNetworkView();
                mCircleView.stopRotate();// 只要请求网络就停止旋转小球
                if (info != null && info.getTotal_page() > 0) {
                    listView.setVisibility(View.VISIBLE);
                    if (titleResId == R.string.new_topic) {
                        mCircleView.setVisibility(View.VISIBLE);
                        mCircleView.bringToFront();
                    }
                    totalPage = info.getTotal_page();
                    if (isFirstPage()) {
                        adapter.setData(info.getDetails());
                    } else {
                        adapter.addData(info.getDetails());
                    }
                    listView.setCanLoadMore(currentPage < totalPage);
                    listView.onLoadMoreComplete();
//					listView.onRefreshComplete();
//					int nextPage = page + 1;
//					if (nextPage > totalPage) {
//						listView.setMode(Mode.PULL_FROM_START);
//					} else {
//						listView.setMode(Mode.BOTH);
//					}
                } else {
                    setNoDataView();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                ToastUtils.showToast(mActivity, error.getMessage());
                networkView.hideLoadingBar();
                fl_publish_failed.setVisible(View.GONE);
//				listView.onRefreshComplete();
//				listView.onLoadMoreComplete();
                listView.onLoadingFailed();
                mCircleView.stopRotate();// 网络错误也要停止旋转
                if (statusCode == 503) {
                    ((CommunityActivity) mActivity).abnormalDisplay(CloseType.shut);
                } else if (statusCode == 901) {
                    ((CommunityActivity) mActivity).abnormalDisplay(CloseType.disita);
                } else {
                    networkView.showNetworkFailLayout();
                    fl_publish_failed.setVisible(View.GONE);
                }
            }
        });
    }

    private void setNoDataView() {
        listView.setVisibility(View.GONE);
        networkView.showNetworkNoDataLayout();
        switch (titleResId) {
            case R.string.published_topic:
                networkView.setNoDataCatSyle(NetWorkStateView.CatStyle.SINISTER_SMILE);
                networkView.setNoDataLayoutText("你还没有发表过话题", null);
                break;
            case R.string.collected_topic:
                networkView.setNoDataCatSyle(NetWorkStateView.CatStyle.NO_DATA);
                networkView.setNoDataLayoutText("你还没有收藏过话题", null);
                break;
            case R.string.new_topic:
                networkView.setNoDataLayoutText("暂时还没有内容", null);
                break;
            default:
                break;
        }
    }

    @Override
    public void retry() {
//		listView.setRefreshing(true);
//		listView.getRefreshableView().setSelection(0);
        mCircleView.startRotate();
        requestData(currentPage);
        initFailedBar();
    }

//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.fa_button) {
////			listView.setRefreshing(true);
//            mCircleView.startRotate();
//            requestData();
//        }
//
//    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        dealPullRefresh();
    }

    /**
     * Called when the list reaches the last item (the last item is visible
     * to the user)
     */
    @Override
    public void onLoadMore() {
        dealLoadRefresh();
    }
}
