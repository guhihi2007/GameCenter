package cn.lt.game.ui.app.community.ta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.NetIniCallBack;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.CheckUserRightsTool;
import cn.lt.game.ui.app.community.CommunityActivity;
import cn.lt.game.ui.app.community.CommunityBaseFragment;
import cn.lt.game.ui.app.community.SendCommentActivity;
import cn.lt.game.ui.app.community.model.Category;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.model.TopicDetails;
import cn.lt.game.ui.app.community.topic.group.SpinerPopAdapter;
import cn.lt.game.ui.app.community.widget.SpinerPopWindow;
import cn.lt.game.ui.common.quickadpter.BaseAdapterHelper;
import cn.lt.game.ui.common.quickadpter.QuickAdapter;

/**
 * TA的话题
 * Created by tiantian on 2015/11/18.
 */
public class TaTopicFragment extends CommunityBaseFragment implements NetWorkStateView.RetryCallBack, SpinerPopAdapter.IOnMenuSelectListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener {
    private RefreshAndLoadMoreListView mListView;
    private NetWorkStateView networkView;
    private View view;
    private int currentPage = 1;
    private QuickAdapter<TopicDetail> adapter;
    private int totalPage;
    private int titleResId;
    private int userId;
    private String uri;
    private static final String TAG = "TaTopicFragment";

    public static TaTopicFragment newInstance(int titleResId) {
        TaTopicFragment newFragment = new TaTopicFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("titleResId", titleResId);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        this.titleResId = bundle.getInt("titleResId");
        userId = activity.getIntent().getIntExtra("userId", -1);
        switch (titleResId) {
            case R.string.ta_topic:
                uri = Uri.getOthersPageTopicUri(userId);
                break;
            case R.string.ta_comment:
                uri = Uri.getOthersPageCommentUri(userId);
                break;
        }

    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_TOPIC_HER);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.group_fragment, container, false);
        requestData(currentPage);
        return view;
    }

    private List<TopicDetail> temList;

    private void requestData(final int page) {
        Map<String, String> parames = new HashMap<String, String>();
        parames.put("page", page + "");
        Net.instance().executeGet(Host.HostType.FORUM_HOST, uri, parames, new WebCallBackToObj<TopicDetails>() {
            @Override
            protected void handle(TopicDetails info) {
                networkView.hideLoadingBar();
                totalPage = info.getTotal_page();
                if (info != null && totalPage > 0) {
                    mListView.setVisibility(View.VISIBLE);
                    temList = new ArrayList<>();
                    temList.addAll(info.getData());
                    if (isFirstPage()) {
                        setAdapter();
//                        mListView.setMode(PullToRefreshBase.Mode.BOTH);
                    } else {
                        adapter.addAll(temList);
                    }
//                    mListView.onRefreshComplete();
                    mListView.onLoadMoreComplete();
                    mListView.setCanLoadMore(currentPage < totalPage);
//                    int nextPage = page + 1;
//                    if (nextPage > totalPage) {
//                        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//                    } else {
//                        mListView.setMode(PullToRefreshBase.Mode.BOTH);
//                    }
                } else {
                    mListView.setVisibility(View.GONE);
                    networkView.showNetworkNoDataLayout();
                    networkView.setNoDataLayoutText("ta还没有发表过话题", null);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                mListView.onLoadingFailed();
                ToastUtils.showToast(mActivity, error.getMessage());
                if (statusCode == 503) {
                    ((CommunityActivity) mActivity).abnormalDisplay(CommunityActivity.CloseType.shut);
                } else if (statusCode == 901) {
                    ((CommunityActivity) mActivity).abnormalDisplay(CommunityActivity.CloseType.disita);
                } else {
                    mListView.setVisibility(View.GONE);
                    networkView.showNetworkFailLayout();
                }
            }
        });
    }

    protected boolean isFirstPage() {
        return currentPage == 1;
    }

    private void initView() {
        mListView = (RefreshAndLoadMoreListView) view.findViewById(R.id.lv_group);
        mListView.setVisibility(View.GONE);
//        mListView.setOnRefreshListener(this);
        mListView.setOnLoadMoreListener(this);
        mListView.setmOnRefrshListener(this);
        networkView = (NetWorkStateView) view.findViewById(R.id.group_netWrokStateView);
        networkView.showLoadingBar();
        networkView.setRetryCallBack(this);
        mListView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.margin_size_8dp));

    }

    private void setAdapter() {
        adapter = new QuickAdapter<TopicDetail>(mActivity, R.layout.ta_topic_item, temList) {
            @Override
            protected void convert(BaseAdapterHelper helper, final TopicDetail item) {
                helper.setText(R.id.tv_group_name, item.group_title);
                helper.setText(R.id.tv_time, TimeUtils.curtimeDifference(item.published_at));
                helper.setText(R.id.tv_topic_summary, item.topic_title);
                helper.setOnClickListener(R.id.iv_more, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPop(item);
                    }
                });
                helper.setOnClickListener(R.id.ll_ta_topic_root, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityActionUtils.jumpToTopicDetail(v.getContext(), item.topic_id);
                    }
                });
            }
        };
        mListView.setAdapter(adapter, false);
    }

    private TopicDetail topicDetail;

    private void showPop(TopicDetail topicDetail) {
        this.topicDetail = topicDetail;
        List<Category> list = new ArrayList<Category>();
        list.add(new Category(0, "评论"));
        list.add(new Category(1, "收藏"));
        SpinerPopWindow pop = new SpinerPopWindow(mActivity, list);
        pop.showAtLocation(view, Gravity.CENTER, 0, 0);
        pop.setmMenuSelectListener(this);

    }

    @Override
    public void retry() {
//        mListView.setRefreshing(true);
        requestData(currentPage);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {

    }

//    @Override
//    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
//        currentPage = 1;
//        requestData(currentPage);
//    }
//
//    @Override
//    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
//        requestData(++currentPage);
//    }

    @Override
    public void onMenuClick(int pos) {
        if (pos == 0) {
            CheckUserRightsTool.instance().checkUserRights(mActivity, false, topicDetail.getGroupId(), new NetIniCallBack() {
                @Override
                public void callback(int code) {
                    if (code == 0) {
                        String content;
                        content = new String(Base64.decode(topicDetail.getTopicContent(), Base64.DEFAULT));
                        Intent intent = SendCommentActivity.getIntent(mActivity, topicDetail.getGroupId(), topicDetail.getTopicTitle(), content, topicDetail.getGroupTitle(), true, topicDetail.getTopicId(), "1");
                        mActivity.startActivity(intent);
                    }
                }
            });
        } else {
            CheckUserRightsTool.instance().collectReq(mActivity, topicDetail.getTopicId(), new NetIniCallBack() {
                @Override
                public void callback(int code) {
                    if (code == 0) {

                    }
                }
            });
        }
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        currentPage = 1;
        requestData(currentPage);
    }

    /**
     * Called when the list reaches the last item (the last item is visible
     * to the user)
     */
    @Override
    public void onLoadMore() {
        requestData(++currentPage);
    }
}
