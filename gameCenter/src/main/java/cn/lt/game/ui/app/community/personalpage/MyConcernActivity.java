package cn.lt.game.ui.app.community.personalpage;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.view.TitleMoreButton;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.model.GroupMembers;
import cn.lt.game.ui.app.community.model.User;
import cn.lt.game.ui.common.quickadpter.BaseAdapterHelper;
import cn.lt.game.ui.common.quickadpter.QuickAdapter;

/**
 * 我的关注/我的粉丝
 *
 * @author tiantian 2015/11/05
 */
public class MyConcernActivity extends BaseActivity implements View.OnClickListener, NetWorkStateView.RetryCallBack, SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener {
    private RefreshAndLoadMoreListView listView;
    private NetWorkStateView netWorkStateView;
    private TitleBarView titleBar;
    private QuickAdapter<User> adapter;
    private int currentPage = 1;// 当前页码
    private int totalPage;// 总页数
    private int userType;
    public static int MYATTENTION = 1;
    public static int MYFANS = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_concern);
        initView();
        userType = getIntent().getIntExtra("userType", -1);
        Log.i("zzz", "userType" + userType);
    }

    private void initView() {
        netWorkStateView = (NetWorkStateView) findViewById(R.id.member_netwrolStateView);
        netWorkStateView.setRetryCallBack(this);
        netWorkStateView.showLoadingBar();
        listView = (RefreshAndLoadMoreListView) findViewById(R.id.member_listView);
        listView.setOnLoadMoreListener(this);
        listView.setmOnRefrshListener(this);
        titleBar = (TitleBarView) findViewById(R.id.group_member_bar);
        titleBar.setMoreButtonType(TitleMoreButton.MoreButtonType.BackHome);
        listView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.margin_size_8dp));
    }

    @Override
    protected void onResume() {
        super.onResume();
        titleBar.setTitle(userType == MYATTENTION ? "我的关注" : "我的粉丝");
        requestData(currentPage);
    }

    @Override
    public void setPageAlias() {
        switch (userType) {
            case 1:
                setmPageAlias(Constant.PAGE_ATTENTION_MINE);// 添加统计（我的关注页面）
                break;
            case 2:
                setmPageAlias(Constant.PAGE_FANS_MINE);// 添加统计（我的粉丝页面）
                break;
        }
    }

    private void requestData(final int page) {
        Map<String, String> parames = new HashMap<String, String>();
        parames.put("page", page + "");
        Net.instance().executeGet(Host.HostType.FORUM_HOST, setUri(), parames, new WebCallBackToObj<GroupMembers>() {
            @Override
            protected void handle(GroupMembers info) {
                if (info != null) {
                    totalPage = info.total_page;
                    processData(info);
                    int nextPage = page + 1;
                    listView.onLoadMoreComplete();
                    if (nextPage > totalPage) {
                        listView.setCanLoadMore(false);
                    } else {
                        listView.setCanLoadMore(true);
                    }
                    listView.onLoadMoreComplete();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                netWorkStateView.showNetworkFailLayout();
                listView.onLoadingFailed();
                switch (statusCode) {
                    case -2:
                        ToastUtils.showToast(MyConcernActivity.this, "数据异常");
                        netWorkStateView.showNetworkFailLayout();
                        break;
                    case -3:
                        ToastUtils.showToast(MyConcernActivity.this, "返回数据为空");
                        netWorkStateView.showNetworkNoDataLayout();
                        break;
                }
            }
        });
    }

    /**
     * 根据类型返回不同的URI地址
     */
    private String setUri() {
        switch (userType) {
            case 1:
                return Uri.getMyAttentionUri();
            case 2:
                return Uri.getMyFansUri();
        }
        return "";
    }

    private List<User> tempList;

    private void processData(GroupMembers info) {
        if (info.data.size() <= 0) {
            netWorkStateView.setNotDataState(NetWorkStateView.JoinGroup);
            netWorkStateView.setNoDataCatSyle(NetWorkStateView.CatStyle.NO_DATA);
            netWorkStateView.setNoDataLayoutText(userType == MYATTENTION ? "你还没有关注的人" : "你还没有粉丝", "");
            netWorkStateView.showNetworkNoDataLayout();
        } else {
            netWorkStateView.hideNetworkNoDataLayout();
            netWorkStateView.hideNetworkView();
            tempList = new ArrayList<User>();
            tempList.addAll(info.data);
            if (isFirstPage()) {
                setAdapter();
            } else {
                adapter.addAll(tempList);
            }
        }
    }

    private void setAdapter() {
        adapter = new QuickAdapter<User>(this, R.layout.my_concern_item, tempList) {
            @Override
            protected void convert(BaseAdapterHelper helper, final User item) {
                ImageView iv_circle = helper.getView(R.id.iv_concern);
                Glide.with(MyConcernActivity.this).load(item.getUser_icon()).into(iv_circle);
                helper.setText(R.id.tv_user_name, item.getUser_nickname());
                helper.setText(R.id.tv_concern_time, userType == MYATTENTION ? TimeUtils.curtimeDifference(item.getFollowed_at()) + "关注" : TimeUtils.curtimeDifference(item.getFollowed_at()));
                ImageView userLevel = helper.getView(R.id.iv_user_level);
                userLevel.setImageLevel(item.user_level);
                helper.setOnClickListener(R.id.rl_root, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityActionUtils.activity_Jump_Value(MyConcernActivity.this, PersonalActivity.class, "userId", item.getUser_id());
                    }
                });
            }
        };
        listView.setAdapter(adapter, false);
    }

    protected boolean isFirstPage() {
        return currentPage == 1;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void retry() {
//        listView.setRefreshing(true);
        requestData(currentPage);
    }

//    @Override
//    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//        Log.i("zzz","下拉刷新");
//        currentPage = 1;
//        requestData(currentPage);
//    }
//
//    @Override
//    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//        Log.i("zzz","翻页");
//        requestData(++currentPage);
//    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        Log.i("zzz", "下拉刷新");
        currentPage = 1;
        requestData(currentPage);
    }

    /**
     * Called when the list reaches the last item (the last item is visible
     * to the user)
     */
    @Override
    public void onLoadMore() {
        Log.i("zzz", "翻页");
        requestData(++currentPage);
    }
}
