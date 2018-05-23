package cn.lt.game.ui.app.gamedetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.NetIniCallBack;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.CheckUserRightsTool;
import cn.lt.game.ui.app.community.SendTopicActivity;
import cn.lt.game.ui.app.community.group.GroupMemberActivity.EventBean;
import cn.lt.game.ui.app.community.model.Group;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.model.TopicDetails;
import cn.lt.game.ui.app.community.model.TopicEvent;
import cn.lt.game.ui.app.community.topic.group.GroupTopicActivity;
import cn.lt.game.ui.app.community.topic.group.GroupTopicListAdapter;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import de.greenrobot.event.EventBus;

/***
 * 游戏详情-社区页面
 *
 * @author ltbl
 *
 */
public class GameCommunityInfoFragment extends BaseFragment implements RetryCallBack, OnClickListener, SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener {
    private View mView, headView;
    private TextView tv_group_name, tv_group_des, tv_topic_count;
    private RelativeLayout rl_topic_head;
    private RefreshAndLoadMoreListView listView;
    private ImageView iv_group_logo;
    private Button btn_joinGroup;
    private NetWorkStateView networkView;
    private GroupTopicListAdapter adapter;
    private int currentPage = 1;// 当前页码
    private int totalPage, forum_id;// 总页数
    public Group groupInfo;
    private String orderby = "last-publish"; // 排序类型,默认排序为最新发布

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.gamedetail_comment_fragment, container, false);
        forum_id = ((GameDetailHomeActivity) getActivity()).getGroupId();
        Log.i("zzz", "要请求的社区ＩＤ＝＝" + forum_id);
        initView();
        /* 检查网络状态，状态正常则联网获取数据初始化界面 */
        checkNetwork();
        return mView;
    }

    /***
     *
     * 话题发表成功刷新页面
     *
     * @param event
     */
    public void onEventMainThread(TopicEvent event) {
        if (!event.isResult()) {
            Log.i("zzz", "话题发送失败！");
        } else {
            orderby = "last-publish";
            if (!this.mActivity.isFinishing()) {
                // 发表成功，刷新话题列表页面
                Log.i("zzz", "发表成功，刷新话题列表页面");
                checkNetwork();
            }
        }
    }

    private void initView() {
        listView = (RefreshAndLoadMoreListView) mView.findViewById(R.id.gamedetail_comment_listView);
        networkView = (NetWorkStateView) mView.findViewById(R.id.detail_comment_netwrolStateView);
        networkView.showLoadingBar();
        listView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.margin_size_8dp));
        networkView.setRetryCallBack(this);
        btn_joinGroup = (Button) mView.findViewById(R.id.btn_comment);
        btn_joinGroup.setText("加入小组");
        headView = LayoutInflater.from(mActivity).inflate(R.layout.topic_head_layout, null);
        rl_topic_head = (RelativeLayout) headView.findViewById(R.id.rl_topic_head);
        iv_group_logo = (ImageView) headView.findViewById(R.id.iv_topic_logo);
        tv_group_name = (TextView) headView.findViewById(R.id.tv_topic_name);
        tv_group_des = (TextView) headView.findViewById(R.id.tv_topic_des);
        tv_topic_count = (TextView) headView.findViewById(R.id.tv_topic_count);
        ListView ls = listView.getmListView();
        ls.addHeaderView(headView);
        adapter = new GroupTopicListAdapter(mActivity);
        listView.setAdapter(adapter, false);
        listView.setmOnRefrshListener(this);
        listView.setOnLoadMoreListener(this);
        btn_joinGroup.setOnClickListener(this);
        rl_topic_head.setOnClickListener(this);
    }

    private void checkNetwork() {
        /* 获取网络数据 */
        if (NetUtils.isConnected(mActivity)) {
            listView.getmListView().setSelection(0);
            requestHeadViewData();
            requestData(currentPage);// 联网获取数据
        } else {
            networkView.showNetworkFailLayout();
        }
    }

    /***
     * 请求小组话题列表头部数据
     */
    private void requestHeadViewData() {
        Net.instance().executeGet(Host.HostType.FORUM_HOST, Uri.getGroupInfoUri(forum_id), null, new WebCallBackToObj<Group>() {
            @Override
            protected void handle(Group info) {
                headView.setVisibility(View.VISIBLE);
                if (null != info) {
                    groupInfo = info;
                    processHeadViewData(groupInfo);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                Log.i("zzz", "头部数据请求失败");
                listView.setVisibility(View.GONE);
            }

        });

    }

    /***
     * 请求网络数据
     */
    private List<TopicDetail> tempList;

    public void requestData(final int page) {
        Map<String, String> parames = new HashMap<String, String>();
        parames.put("order_by", orderby);
        parames.put("page", page + "");
        Net.instance().executeGet(Host.HostType.FORUM_HOST, Uri.getGroupTopicsUri(forum_id), parames, new WebCallBackToObj<TopicDetails>() {
            @Override
            protected void handle(TopicDetails info) {
                networkView.hideNetworkView();
                listView.setVisibility(View.VISIBLE);
                if (null != info) {
                    totalPage = info.getTotal_page();
                    if (totalPage == 0) {
                        if (null != tempList) {
                            tempList.clear();
                            adapter.setData(tempList);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        tempList = new ArrayList<TopicDetail>();
                        tempList.addAll(info.getDetails());
                        if (isFirstPage()) {
                            adapter.setData(tempList);
                        } else {
                            adapter.addData(tempList);
                        }
                    }
                }
                int nextPage = page + 1;
                if (nextPage > totalPage) {
                    listView.setCanLoadMore(false);
                } else {
                    listView.setCanLoadMore(true);
                }
                listView.onLoadMoreComplete();
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                Log.i("zzz", "话题列表数据请求失败==" + error.getMessage());
                if (901 == statusCode) {
                    showForbadView();
                } else {
                    networkView.showNetworkFailLayout();
                    listView.setVisibility(View.GONE);
//					listView.onRefreshComplete();
                }
                listView.onLoadingFailed();
                listView.setCanLoadMore(true);
                listView.onLoadMoreComplete();
            }
        });
    }

    /**
     * 显示禁言/禁访页面
     */
    private void showForbadView() {
        listView.setVisibility(View.GONE);
        networkView.showNetworkNoDataLayout();
        networkView.setNotDataState(NetWorkStateView.gotoFeedback);
        networkView.setNoDataLayoutText("您已被封禁，该操作无法进行", "联系管理员");
        networkView.bringToFront();
    }

    private void processHeadViewData(Group groupInfo) {
//		ImageLoader.getInstance().displayLogo(groupInfo.group_icon, iv_group_logo);
        ImageloaderUtil.loadLTLogo(getActivity(), groupInfo.group_icon, iv_group_logo);
        tv_group_name.setText(groupInfo.group_title);
        tv_group_des.setText(groupInfo.group_summary);
        tv_topic_count.setText(groupInfo.popularity + "");
    }

    protected boolean isFirstPage() {
        return currentPage == 1;
    }

    @Override
    public void retry() {
        checkNetwork();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_comment:
                String state = btn_joinGroup.getText().toString();
                if ("加入小组".equals(state)) {
                    CheckUserRightsTool.instance().checkIsUserLoginAndGoinGroup(mActivity, forum_id, new NetIniCallBack() {
                        @Override
                        public void callback(int code) {
                            if (code == 0) {
                                ToastUtils.showToast(mActivity, "成功加入小组");
                                btn_joinGroup.setText("发表话题");
                                EventBus.getDefault().post(new EventBean("refreshData"));
                            } else if (code == -2) {
                                btn_joinGroup.setText("发表话题");
                            }
                        }
                    });
                } else {
                    CheckUserRightsTool.instance().checkUserRights(mActivity, false, forum_id, new NetIniCallBack() {
                        @Override
                        public void callback(int code) {
                            if (code == 0) {
                                Intent intent = new Intent(mActivity, SendTopicActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("category", groupInfo.categories);
                                intent.putExtra("type", "1");// 1：正常启动
                                intent.putExtra("group_id", groupInfo.group_id);// 1：正常启动
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }
                    });
                }
                break;
            // 跳转到小组话题列表页面
            case R.id.rl_topic_head:
                // 跳转到小组话题列表页面
                ActivityActionUtils.activity_Jump_Value(mActivity, GroupTopicActivity.class, "group_id", forum_id);
                break;
        }
    }

    private boolean isLogin = false;// 判断用户是否登陆

    @Override
    public void onResume() {
        super.onResume();
        isLogin = UserInfoManager.instance().isLogin();
        if (isLogin) {
            btn_joinGroup.setText("发表话题");
        } else {
            btn_joinGroup.setText("加入小组");
        }
    }


    private void requestAllData(int currentPage) {
        requestData(currentPage);
        requestHeadViewData();
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        requestAllData(currentPage);
    }

    @Override
    public void onLoadMore() {
        requestData(++currentPage);
    }
}
