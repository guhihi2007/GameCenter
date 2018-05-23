package cn.lt.game.ui.app.community.group;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.view.TitleMoreButton.MoreButtonType;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.NetIniCallBack;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.CheckUserRightsTool;
import cn.lt.game.ui.app.community.SendTopicActivity;
import cn.lt.game.ui.app.community.model.Group;
import cn.lt.game.ui.app.community.model.GroupMembers;
import cn.lt.game.ui.app.community.model.User;
import cn.lt.game.ui.app.community.personalpage.PersonalActivity;
import cn.lt.game.ui.app.community.widget.QuitPopWindow;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;
import cn.lt.game.ui.common.quickadpter.BaseAdapterHelper;
import cn.lt.game.ui.common.quickadpter.QuickAdapter;
import de.greenrobot.event.EventBus;

/***
 * @author ltbl
 * @category 小组成员列表
 */
public class GroupMemberActivity extends BaseActivity implements OnClickListener, RetryCallBack, UserInfoUpdateListening {
    private RefreshAndLoadMoreListView listView;
    private NetWorkStateView netWorkStateView;
    private TitleBarView titleBar;
    private Button btn_joinGroup;// 加入小組
    private int currentPage = 1;// 当前页码
    private int totalPage;// 总页数
    private int groupId;
    private TextView tv_title;// listView头部标题
    private QuickAdapter<User> adapter;
    private Group groupInfo;
    private boolean isLogin = false;// 判断用户是否登陆

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);
        initView();
        requestData(currentPage);
        EventBus.getDefault().register(this);
        UserInfoManager.instance().addListening(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        UserInfoManager.instance().removeListening(this);
    }

    public void onEventMainThread(EventBean bean) {
        if ("refreshData".equals(bean.tag)) {
            Log.i("zzz", "刷新小组成员列表");
            if (!this.isFinishing()) {
                retry();
            }
        }
    }

    private void initView() {
        groupInfo = (Group) getIntent().getSerializableExtra("GroupMember");
        netWorkStateView = (NetWorkStateView) findViewById(R.id.member_netwrolStateView);
        netWorkStateView.setRetryCallBack(this);
        netWorkStateView.showLoadingBar();
        btn_joinGroup = (Button) findViewById(R.id.btn_joinGroup);
        btn_joinGroup.setOnClickListener(this);
        listView = (RefreshAndLoadMoreListView) findViewById(R.id.member_listView);
        setListViewLisener();// 初始化下拉刷新事件
        listView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.margin_size_8dp));
        titleBar = (TitleBarView) findViewById(R.id.group_member_bar);
        titleBar.setMoreButtonType(MoreButtonType.BackHome);
        titleBar.setTitle("小组成员");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        tv_title = new TextView(this);
        tv_title.setGravity(Gravity.CENTER_VERTICAL);
        tv_title.setTextColor(Color.parseColor("#333333"));
        tv_title.setTextSize(17);
        if (groupInfo != null) {
            groupId = groupInfo.group_id;
            setHeadViewTitle(groupInfo.member_count);
        }
        layout.addView(tv_title, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(R.dimen.user_center_44)));
        ListView lv = listView.getmListView();
        lv.addHeaderView(layout);
        titleBar.setBackHomeVisibility(0);
        setAdapter();
    }

    private void setListViewLisener() {
        listView.setmOnRefrshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                requestData(currentPage);
            }
        });
        listView.setOnLoadMoreListener(new RefreshAndLoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                requestData(++currentPage);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setButtonState();
    }

    /***
     * 设置底部按钮状态
     */
    private void setButtonState() {
        isLogin = UserInfoManager.instance().isLogin();
        CheckUserRightsTool.instance().hasUserJoinGroup(this, groupId, new NetIniCallBack() {
            @Override
            public void callback(int code) {
                if (code == 0 && isLogin) {
                    btn_joinGroup.setText("发表话题");
                } else {
                    btn_joinGroup.setText("加入小组");
                }
            }
        });
    }

    private void setAdapter() {
        adapter = new QuickAdapter<User>(this, R.layout.group_member_item, tempList) {
            @Override
            protected void convert(final BaseAdapterHelper helper, final User item) {
//                ImageLoader.getInstance().display(item.getUser_icon(), (CircleImageView) helper.getView(R.id.iv_member_head));
                ImageView imageView = helper.getView(R.id.iv_member_head);
//                ImageloaderUtil.loadImage(GroupMemberActivity.this,item.getUser_icon(), (RoundImageView) helper.getView(R.id.iv_member_head), false);
                Glide.with(GroupMemberActivity.this).load(item.getUser_icon()).into(imageView);
                helper.setText(R.id.tv_memberName, item.getUser_nickname());
                helper.setText(R.id.tv_joinTime, "加入时间：" + TimeUtils.curtimeDifference2(item.getJoined_at()));
                helper.getView(R.id.iv_member_head).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityActionUtils.activity_Jump_Value(v.getContext(), PersonalActivity.class, "userId", item.getUser_id());
                    }
                });
                helper.getView(R.id.tv_memberName).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityActionUtils.activity_Jump_Value(v.getContext(), PersonalActivity.class, "userId", item.getUser_id());
                    }
                });
                helper.setImageLevel(R.id.iv_user_level, item.user_level);
                if (item.user_type == 2) {// 1-普通用户,2-组长或管理员;
                    helper.setVisible(R.id.iv_isAdmin, true);
                } else if (item.user_type == 1) {
                    helper.setVisible(R.id.iv_isAdmin, false);
                }
                if (item.is_myself) {
                    helper.setVisible(R.id.iv_isMyself, true);
                    helper.setVisible(R.id.iv_quit, true);
                    helper.setOnClickListener(R.id.iv_quit, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPop((ImageView) helper.getView(R.id.iv_quit), groupId);
                        }
                    });
                } else {
                    helper.setVisible(R.id.iv_isMyself, false);
                    helper.setVisible(R.id.iv_quit, false);
                }
            }
        };
        listView.setAdapter(adapter, false);
    }

    private void showPop(ImageView iv, int groupId) {
        QuitPopWindow pop = new QuitPopWindow(this);
        pop.setGroupId(groupId);
        pop.showPopupWindow(iv);
    }

    public void requestData(final int page) {
        Map<String, String> parames = new HashMap<String, String>();
        parames.put("page", page + "");
        Net.instance().executeGet(Host.HostType.FORUM_HOST, Uri.getGroupMembersUri(groupId), parames, new WebCallBackToObj<GroupMembers>() {
            @Override
            protected void handle(GroupMembers info) {
                if (info != null) {
                    totalPage = info.total_page;
                    processData(info);
                    int nextPage = page + 1;
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
                        ToastUtils.showToast(GroupMemberActivity.this, "小组成员列表数据异常");
                        netWorkStateView.showNetworkFailLayout();
                        break;
                    case -3:
                        ToastUtils.showToast(GroupMemberActivity.this, "小组成员列表返回数据为空");
                        netWorkStateView.showNetworkNoDataLayout();
                        break;
                }
            }
        });
    }

    private List<User> tempList;

    private void processData(GroupMembers info) {
        if (info.member_count <= 0) {
            netWorkStateView.setGroupId(this, groupId, btn_joinGroup);
            netWorkStateView.setNotDataState(NetWorkStateView.JoinGroup);
            netWorkStateView.setNoDataCatSyle(NetWorkStateView.CatStyle.NO_DATA);
            netWorkStateView.setNoDataLayoutText("暂时还没有成员", "马上加入");
            netWorkStateView.showNetworkNoDataLayout();
        } else {
            netWorkStateView.hideNetworkNoDataLayout();
            netWorkStateView.hideNetworkView();
            setHeadViewTitle(info.member_count);
            tempList = new ArrayList<User>();
            tempList.addAll(info.detail);
//            if (isFirstPage()) {
//                setAdapter();
//            } else {
            adapter.addAll(tempList);
//            }
        }
    }

    protected boolean isFirstPage() {
        return currentPage == 1;
    }

    private void setHeadViewTitle(int memberCount) {
        tv_title.setText(groupInfo.group_title + "(" + memberCount + "人)");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_joinGroup:
                String btnValue = btn_joinGroup.getText().toString();
                if ("发表话题".equals(btnValue)) {
                    publishTopic();
                } else if ("加入小组".equals(btnValue)) {
                    CheckUserRightsTool.instance().checkIsUserLoginAndGoinGroup(this, groupId, new NetIniCallBack() {
                        @Override
                        public void callback(int code) {
                            if (code == 0) {
                                Log.i("zzz", "加入小组返回码==" + code);
                                ToastUtils.showToast(GroupMemberActivity.this, "成功加入小组");
                                btn_joinGroup.setText("发表话题");
                                EventBus.getDefault().post(new EventBean("refreshData"));
                            } else if (code == -2) {
                                // ToastUtils.showToast(GroupMemberActivity.this,
                                // "您已经加入过该小组");
                                btn_joinGroup.setText("发表话题");
                            } else {
                                ToastUtils.showToast(GroupMemberActivity.this, "加入小组失败，请重试");
                            }
                        }
                    });
                }
                break;
        }
    }

    private void publishTopic() {
        CheckUserRightsTool.instance().checkIsUserForbade(this, groupId, new NetIniCallBack() {
            @Override
            public void callback(int code) {
                if (code == 0) {
                    Intent intent = new Intent(GroupMemberActivity.this, SendTopicActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("category", groupInfo.categories);
                    intent.putExtra("type", "1");// 1：正常启动
                    intent.putExtra("group_id", groupInfo.group_id);// 1：正常启动
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    ToastUtils.showToast(GroupMemberActivity.this, "您已被禁言，请联系社区客服");
                }
            }
        });
    }

    @Override
    public void retry() {
//        listView.setRefreshing(true);
        requestData(currentPage);
        setButtonState();
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_GROUP_MEMBER_LIST);
    }

    @Override
    public void userLogin(UserBaseInfo userBaseInfo) {
        retry();
    }

    @Override
    public void updateUserInfo(UserBaseInfo userBaseInfo) {

    }

    @Override
    public void userLogout() {

    }

    public static class EventBean {

        public EventBean(String s) {
            this.tag = s;
        }

        public String tag = "";
    }
}
