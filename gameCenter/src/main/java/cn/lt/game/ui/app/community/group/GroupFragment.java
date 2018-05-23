package cn.lt.game.ui.app.community.group;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.threadpool.LTAsyncTask;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.CommunityActivity;
import cn.lt.game.ui.app.community.CommunityActivity.CloseType;
import cn.lt.game.ui.app.community.CommunityBaseFragment;
import cn.lt.game.ui.app.community.group.GroupMemberActivity.EventBean;
import cn.lt.game.ui.app.community.model.CommentEvent;
import cn.lt.game.ui.app.community.model.Group;
import cn.lt.game.ui.app.community.model.Groups;
import cn.lt.game.ui.app.community.model.TopicEvent;
import cn.lt.game.ui.app.community.widget.GroupView;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;
import de.greenrobot.event.EventBus;

/*
 * 推荐小组
 */
public class GroupFragment extends CommunityBaseFragment implements RetryCallBack, UserInfoUpdateListening, SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener, RefreshAndLoadMoreListView.IOnScrollStateChanged {
    private RefreshAndLoadMoreListView lv_group;
    private NetWorkStateView networkView;
    private View view;
    private int currentPage = 1;
    private GroupAdapter adapter;
    private int totalPage;
    private GroupView.GroupViewType viewType;
    private int titleResId;
    private static final String TAG = "GroupFragment";
    private int userId;
    private String uri;

    public static GroupFragment newInstance(int titleResId) {
        GroupFragment newFragment = new GroupFragment();
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
            case R.string.browsegroups:
                viewType = GroupView.GroupViewType.RecommendGroup;
                uri = Uri.COM_GROUP;
                break;
            case R.string.joined_groups:
                viewType = GroupView.GroupViewType.MyHub;
                uri = Uri.COM_MY_GROUP;
                break;
            case R.string.ta_group:
                viewType = GroupView.GroupViewType.MyHub;
                uri = Uri.getOthersPageGroupUrl(userId);
                break;
        }
    }

    @Override
    public void setPageAlias() {
        switch (titleResId) {
            case R.string.browsegroups:
                setmPageAlias(Constant.PAGE_GROUP_RECOMMEND);
                break;
            case R.string.joined_groups:
                setmPageAlias(Constant.PAGE_GROUP_MINE);
                break;
            case R.string.ta_group:
                setmPageAlias(Constant.PAGE_GROUP_HER);
                break;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this.getActivity();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        UserInfoManager.instance().removeListening(this);
    }

    /***
     * 加入和退出小组刷新小组列表页面
     * @param bean
     */
    public void onEventMainThread(EventBean bean) {
        if ("refreshData".equals(bean.tag)) {
            Log.i("zzz", "加入或退出小组后刷新推荐小组页面");
            flushDataFromUserLogin();
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
        } else {
            if (!mActivity.isFinishing()) {
                Log.i("zzz", "发表评论成功，刷新话题列表页面");
                flushDataFromUserLogin();
            }
        }
    }

    public void onEventMainThread(TopicEvent event) {
        if (!event.isResult()) {
            Log.i("zzz", "话题发送失败！");
        } else {
            // 发表成功，刷新话题列表页面
            if (!mActivity.isFinishing()) {
                Log.i("zzz", "发表话题成功，刷新话题列表页面");
                flushDataFromUserLogin();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestData(currentPage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.group_fragment, container, false);
        initView();
        return view;
    }

    private List<Group> temList;
    private int curListViewposition;
    private View topView;
    private int DistanceOfTop;
    private LTAsyncTask flushPageThread;

    private void requestData(final int page) {
        Map<String, String> parames = new HashMap<String, String>();
        parames.put("page", page + "");
        Net.instance().executeGet(Host.HostType.FORUM_HOST, uri, parames, new WebCallBackToObj<Groups>() {
            @Override
            protected void handle(Groups info) {
                networkView.hideLoadingBar();
                totalPage = info.getTotal_page();
                if (info != null && totalPage > 0) {
                    lv_group.setVisibility(View.VISIBLE);
                    temList = new ArrayList<>();
                    temList.addAll(titleResId == R.string.browsegroups ? info.getDetail() : info.getData());
                    if (isFirstPage()) {
                        adapter.setData(temList);
                    } else {
                        adapter.addData(temList);
                    }
                    lv_group.setCanLoadMore(page <= totalPage);
                    lv_group.onLoadMoreComplete();
                } else {
                    lv_group.setVisibility(View.GONE);
                    networkView.showNetworkNoDataLayout();
                    networkView.setNoDataLayoutText("还没有任何小组", null);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                ToastUtils.showToast(mActivity, error.getMessage());
                if (statusCode == 503) {
                    ((CommunityActivity) mActivity).abnormalDisplay(CloseType.shut);
                } else if (statusCode == 901) {
                    ((CommunityActivity) mActivity).abnormalDisplay(CloseType.disita);
                } else {
                    lv_group.setVisibility(View.GONE);
                    networkView.showNetworkFailLayout();
                }
            }
        });
    }

    protected boolean isFirstPage() {
        return currentPage == 1;
    }

    private void initView() {
        lv_group = (RefreshAndLoadMoreListView) view.findViewById(R.id.lv_group);
        lv_group.setVisibility(View.GONE);
        networkView = (NetWorkStateView) view.findViewById(R.id.group_netWrokStateView);
        networkView.showLoadingBar();
        networkView.setRetryCallBack(this);
        adapter = new GroupAdapter(mActivity, viewType);
        lv_group.setAdapter(adapter, false);
        UserInfoManager.instance().addListening(this);
        lv_group.setmOnRefrshListener(this);
        lv_group.setOnLoadMoreListener(this);
        lv_group.setMyOnScrollListener(this);
        lv_group.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.margin_size_8dp));

    }

    /**
     * 用户途中登录后重新刷新页面数据
     */
    private void flushDataFromUserLogin() {
        networkView.showLoadingBar();
        lv_group.setVisibility(View.GONE);
        adapter.clearList();

        this.flushPageThread = new LTAsyncTask() {
            @Override
            protected Object doInBackground(Object[] aa) {
                int getDataPage = 1;

                // 记录是否最后一次获取数据
                boolean isLastGetData = false;
                final Map<String, String> params = new HashMap<String, String>();

                while (getDataPage <= currentPage) {
                    params.put("page", Integer.toString(getDataPage));

                    // 设置已经是最后一页了
                    if (getDataPage == currentPage) {
                        isLastGetData = true;
                    }

                    flushPageDataOnUserLogin(params, isLastGetData);

                    getDataPage++;

                    synchronized (this) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        };


        this.flushPageThread.execute();

    }

    private void flushPageDataOnUserLogin(Map<String, String> params, final boolean isLastGetData) {
        Net.instance().executeGet(Host.HostType.FORUM_HOST, Uri.COM_GROUP, params, new WebCallBackToObj<Groups>() {
            @Override
            protected void handle(Groups info) {

                if (info != null && info.getTotal_page() > 0) {
                    adapter.addData(info.getDetail());

                    if (isLastGetData) {
                        // 跳转到刷新页面前 listView的停留位置
                        lv_group.getmListView().setSelectionFromTop(curListViewposition, DistanceOfTop);

                        networkView.hideLoadingBar();
                        lv_group.setVisibility(View.VISIBLE);
                    }

                    synchronized (flushPageThread) {
                        flushPageThread.notify();
                    }
                } else {
                    lv_group.setVisibility(View.GONE);
                    networkView.showNetworkNoDataLayout();
                    networkView.setNoDataLayoutText("还没有任何小组", null);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
//                ToastUtils.showToast(mActivity, error.getMessage());
                if (statusCode == 503) {
                    ((CommunityActivity) mActivity).abnormalDisplay(CloseType.shut);
                } else if (statusCode == 901) {
                    ((CommunityActivity) mActivity).abnormalDisplay(CloseType.disita);
                } else {
                    lv_group.setVisibility(View.GONE);
                    networkView.showNetworkFailLayout();
                }
            }
        });

    }

    @Override
    public void retry() {
        requestData(currentPage);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void userLogin(UserBaseInfo userBaseInfo) {
        flushDataFromUserLogin();
    }

    @Override
    public void updateUserInfo(UserBaseInfo userBaseInfo) {

    }

    @Override
    public void userLogout() {

    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        requestData(currentPage);
    }

    @Override
    public void onLoadMore() {
        requestData(++currentPage);
        if (currentPage == totalPage + 1) {
            ToastUtils.showToast(mActivity, getResources().getString(R.string.no_more));
        }
    }

    @Override
    public void onScrollChangeListener(int scrollState) {
        // 不滚动时保存当前滚动到的位置
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            curListViewposition = lv_group.getmListView().getFirstVisiblePosition();
            // 获取第一个item与屏幕顶端的距离： top
            topView = lv_group.getmListView().getChildAt(0);
            DistanceOfTop = (topView == null) ? 0 : topView.getTop();
        }
    }
}
