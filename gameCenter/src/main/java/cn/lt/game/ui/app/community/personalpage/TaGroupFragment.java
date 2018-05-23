package cn.lt.game.ui.app.community.personalpage;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.CommunityActivity;
import cn.lt.game.ui.app.community.CommunityBaseFragment;
import cn.lt.game.ui.app.community.group.GroupMemberActivity;
import cn.lt.game.ui.app.community.model.CommentEvent;
import cn.lt.game.ui.app.community.model.Group;
import cn.lt.game.ui.app.community.model.Groups;
import cn.lt.game.ui.app.community.model.TopicEvent;
import cn.lt.game.ui.app.community.topic.group.GroupTopicActivity;
import cn.lt.game.ui.common.quickadpter.BaseAdapterHelper;
import cn.lt.game.ui.common.quickadpter.QuickAdapter;
import de.greenrobot.event.EventBus;

/**
 * Created by tiantian on 2015/11/25.
 * 用途：TA的主页-他的小组&我的社区-我加入的小组
 */
public class TaGroupFragment extends CommunityBaseFragment implements NetWorkStateView.RetryCallBack, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener {
    private RefreshAndLoadMoreListView mListView;
    private NetWorkStateView networkView;
    private View view;
    private int currentPage = 1;
    private QuickAdapter<Group> adapter;
    private int totalPage;
    private int titleResId;
    private int userId;

    private String uri;
    private static final String TAG = "TaTopicFragment";

    public static TaGroupFragment newInstance(int titleResId) {
        TaGroupFragment newFragment = new TaGroupFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("titleResId", titleResId);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    /***
     * 发表评论
     *
     * @param event
     */
    public void onEventMainThread(CommentEvent event) {
        if (!event.getResult()) {
            Log.i("zzz", "评论发送失败！");
        } else {
            if (!mActivity.isFinishing()) {
                Log.i("zzz", "发表评论成功，刷新小组列表页面");
                currentPage = 1;
                requestData(currentPage);

            }
        }
    }

    public void onEventMainThread(TopicEvent event) {
        if (!event.isResult()) {
            Log.i("zzz", "话题发送失败！");
        } else {
            // 发表成功，刷新话题列表页面
            if (!mActivity.isFinishing()) {
                Log.i("zzz", "发表话题成功，刷新小组列表页面");
                currentPage = 1;
                requestData(currentPage);
            }
        }
    }

    /***
     * 退出小组刷新小组列表页面
     * @param bean
     */
    public void onEventMainThread(GroupMemberActivity.EventBean bean) {
        if ("refreshData".equals(bean.tag)) {
            Log.i("zzz", "退出小组后刷新推荐小组页面");
            currentPage = 1;
            requestData(currentPage);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        this.titleResId = bundle.getInt("titleResId");
        userId = activity.getIntent().getIntExtra("userId", -1);
        switch (titleResId) {
            case R.string.ta_group:
                uri = Uri.getOthersPageGroupUrl(userId);
                Log.i("zzz", "他的小组");
                break;
            case R.string.joined_groups:
                uri = Uri.COM_MY_GROUP;
                Log.i("zzz", "我的小组");
                break;
        }

    }

    @Override
    public void setPageAlias() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        return view;
    }

    private List<Group> temList;

    private void requestData(final int page) {
        Map<String, String> parames = new HashMap<>();
        parames.put("page", page + "");
        Net.instance().executeGet(Host.HostType.FORUM_HOST, uri, parames, new WebCallBackToObj<Groups>() {
            @Override
            protected void handle(Groups info) {
                networkView.hideLoadingBar();
                totalPage = info.getTotal_page();
                if (info != null && totalPage > 0) {
                    mListView.setVisibility(View.VISIBLE);
                    temList = new ArrayList<Group>();
                    temList.addAll(info.getData());
                    if (isFirstPage()) {
                        setAdapter();
//                        mListView.setMode(PullToRefreshBase.Mode.BOTH);
                    } else {
                        adapter.addAll(temList);
                    }
                    mListView.setCanLoadMore(currentPage <= totalPage);
                    mListView.onLoadMoreComplete();
//                    mListView.onRefreshComplete();
//                    int nextPage = page + 1;
//                    if (nextPage > totalPage) {
//                        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//                    } else {
//                        mListView.setMode(PullToRefreshBase.Mode.BOTH);
//                    }
                } else {
                    mListView.setVisibility(View.GONE);
                    networkView.showNetworkNoDataLayout();
                    networkView.setNoDataLayoutText(titleResId == R.string.ta_group ? "ta还没有加入过小组" : "你还没有加入过小组", null);
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
        final int mScreenWidth = MyApplication.width; // 当前分辨率 宽度
        adapter = new QuickAdapter<Group>(mActivity, R.layout.group_item, temList) {
            @Override
            protected void convert(BaseAdapterHelper helper, final Group item) {
                helper.setRadiusImage(R.id.iv_group, item.group_icon);
                helper.setText(R.id.tv_group_name, item.group_title);
                helper.setText(R.id.tv_group_des, item.group_summary);
                helper.setText(R.id.tv_topic_count, item.popularity + "");
                helper.setText(R.id.tv_number_count, item.member_count + "");
                helper.setTextSize(R.id.btn_join, mScreenWidth <= 720 ? 12 : 15);
                if (titleResId == R.string.joined_groups) {
                    helper.setVisible(R.id.btn_join, false);
                } else {
                    helper.setVisible(R.id.btn_join, true);
                    helper.setText(R.id.btn_join, "进入小组");
                }
                helper.setOnClickListener(R.id.btn_join, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityActionUtils.activity_Jump_Value(mActivity, GroupTopicActivity.class, "group_id", item.group_id);
                    }
                });
                helper.setOnClickListener(R.id.ll_convertView, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityActionUtils.activity_Jump_Value(mActivity, GroupTopicActivity.class, "group_id", item.group_id);
                    }
                });
                helper.setOnClickListener(R.id.tv_number_count, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityActionUtils.activity_Jump_Values(mActivity, GroupMemberActivity.class, "GroupMember", item);
                    }
                });

            }
        };
        mListView.setAdapter(adapter, false);
    }

    @Override
    public void retry() {
//        mListView.setRefreshing(true);
        requestData(currentPage);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestData(currentPage);
    }

    @Override
    public void onClick(View v) {

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
