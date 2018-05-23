package cn.lt.game.ui.app.community.topic.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.util.threadpool.LTAsyncTask;
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
import cn.lt.game.ui.app.community.group.GroupMemberActivity;
import cn.lt.game.ui.app.community.model.Category;
import cn.lt.game.ui.app.community.model.CommentEvent;
import cn.lt.game.ui.app.community.model.Group;
import cn.lt.game.ui.app.community.model.ILike;
import cn.lt.game.ui.app.community.model.ReplyEvent;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.model.TopicDetails;
import cn.lt.game.ui.app.community.model.TopicEvent;
import cn.lt.game.ui.app.community.topic.group.SpinerPopAdapter.IOnItemSelectListener;
import cn.lt.game.ui.app.community.widget.CircleRefreshView;
import cn.lt.game.ui.app.community.widget.CircleRefreshView.OnClickToRefresh;
import cn.lt.game.ui.app.community.widget.FailedBar;
import cn.lt.game.ui.app.community.widget.SpinerPopWindow;
import cn.lt.game.ui.app.community.widget.SpinerPopWindow.Popcallback;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;
import de.greenrobot.event.EventBus;

/***
 * @author tiantian
 * @小组话题列表
 */
public class GroupTopicActivity extends BaseActivity implements OnClickListener, IOnItemSelectListener, RetryCallBack, Popcallback, UserInfoUpdateListening, RefreshAndLoadMoreListView.IOnScrollStateChanged {
    private View headView;
    private RefreshAndLoadMoreListView listView;
    private TextView tv_topic, tv_reply, tv_topic_name, tv_topic_des, tv_topic_count, tv_network_title, tv_networkbutton_text;
    private RelativeLayout network_notdata;
    private FailedBar fl_publish_failed;
    private ImageView iv_topic_logo;
    private List<Category> topic_category_list, reply_list;
    private TitleBarView titleBar;
    private ImageButton ib_topic, ib_reply;
    private Boolean isClick = false;
    private SpinerPopWindow mSpinerPopWindow;
    private NetWorkStateView networkView;
    private Button btn_publish_topic;
    private int state = 0;// 判断是在哪个下拉列表点击的
    private int currentPage = 1;// 当前页码
    private int totalPage, category_id, group_id;// 总页数/话题分类ID/小组Id
    private static String orderby = "last-comment"; // 排序类型,默认排序为最新评论
    private static String LAST_TOPIC = "last-publish";//最新话题
    private static String LAST_COMMENT = "last-comment";//最新评论
    private GroupTopicListAdapter adapter;
    private ListView ls;
    private CircleRefreshView mCircleView;
    private LinearLayout ll_publish_topic;
    private View v_divier;
    private static final String TAG = "GroupTopicActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_topic);
        EventBus.getDefault().register(this);
        group_id = getIntent().getExtras().getInt("group_id");
        initView();
        requestAllData(currentPage);
    }

    private Group groupInfo;
    private List<Category> categoryList;

    /***
     * 加入和退出小组刷新小组列表页面
     *
     * @param bean
     */
    public void onEventMainThread(GroupMemberActivity.EventBean bean) {
        if ("refreshData".equals(bean.tag)) {
            Log.i("zzz", "加入或退出小组后刷新推荐小组页面");
            requestAllData(1);
        }
    }

    /***
     * 有内容发表失败，弹出草稿箱通知栏,否则刷新话题列表
     *
     * @param event
     */
    public void onEventMainThread(TopicEvent event) {
        if (!event.isResult()) {
            Log.i("zzz", "话题发送失败！");
            fl_publish_failed.setToFont();
        } else {
            // 发表成功，刷新话题列表页面
            orderby = LAST_TOPIC;
            if (!this.isFinishing()) {
                Log.i("zzz", "发表话题成功，刷新话题列表页面");
                requestAllData(1);
            }
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
            orderby = LAST_COMMENT;
            if (!this.isFinishing()) {
                Log.i("zzz", "发表评论成功，刷新话题列表页面");
                requestAllData(1);
            }
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
     * 请求小组话题列表头部数据
     */
    private void requestHeadViewData() {
        Net.instance().executeGet(Host.HostType.FORUM_HOST, Uri.getGroupInfoUri(group_id), null, new WebCallBackToObj<Group>() {
            @Override
            protected void handle(Group info) {
                mCircleView.stopRotate();
                headView.setVisibility(View.VISIBLE);
                if (null != info) {
                    groupInfo = info;
                    processHeadViewData(groupInfo);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                Log.i("zzz", "头部数据请求失败--" + error.getMessage());
                // showNetWorkErrorView();
            }

        });

    }

    /***
     * 请求话题列表数据
     *
     * @param page
     */
    private boolean isAll = true;// 判断请求的话题分类是不是全部
    private int DistanceOfTop;
    private LTAsyncTask flushPageThread;
    private int curListViewposition;
    private View topView;

    public void requestData(final int page) {
        Map<String, String> parames = new HashMap<>();
        parames.put("order_by", orderby);
        if (!isAll) {
            parames.put("category_id", category_id + "");
        }
        parames.put("page", page + "");
        Net.instance().executeGet(Host.HostType.FORUM_HOST, Uri.getGroupTopicsUri(group_id), parames, new WebCallBackToObj<TopicDetails>() {
            @Override
            protected void handle(TopicDetails info) {
                networkView.hideNetworkView();
                listView.setVisibility(View.VISIBLE);
                mCircleView.stopRotate();
                if (null != info) {
                    totalPage = info.getTotal_page();
                    if (totalPage == 0) {
                        int paddingHeight = MyApplication.width;
                        network_notdata.setPadding(0, paddingHeight * 1 / 3, 0, 0);
                        network_notdata.setVisibility(View.VISIBLE);
                        ll_publish_topic.setVisibility(View.INVISIBLE);
                        v_divier.setVisibility(View.INVISIBLE);
                        mCircleView.setVisibility(View.INVISIBLE);
                        if (null != tempList) {
                            tempList.clear();
                            adapter.setData(tempList);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        network_notdata.setVisibility(View.GONE);
                        ll_publish_topic.setVisibility(View.VISIBLE);
                        v_divier.setVisibility(View.VISIBLE);
                        mCircleView.setVisibility(View.VISIBLE);
                        tempList = new ArrayList<>();
                        tempList.addAll(info.getDetails());
                        if (isFirstPage()) {
                            adapter.setData(tempList);
                        } else {
                            adapter.addData(tempList);
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
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                mCircleView.stopRotate();// 网络错误也要停止旋转
                if (901 == statusCode) {
                    showForbadView();
                } else if (800 == statusCode) {
                    ToastUtils.showToast(GroupTopicActivity.this, "您查看的小组已被删除！");
                    GroupTopicActivity.this.finish();
                    EventBus.getDefault().post(new GroupMemberActivity.EventBean("refreshData"));
                } else {
                    showNetWorkErrorView();
                }
            }
        });
    }

    /***
     * 处理小组话题列表头部数据
     *
     * @param groupInfo
     */
    private void processHeadViewData(Group groupInfo) {
        categoryList = new ArrayList<>();
        categoryList.addAll(groupInfo.categories);
        topic_category_list = new ArrayList<>();
        reply_list = new ArrayList<>();
        reply_list.add(new Category(1, "最新评论"));
        reply_list.add(new Category(2, "最新话题"));
        topic_category_list.add(new Category(0, "全部"));
        for (Category categoryBean : categoryList) {
            topic_category_list.add(categoryBean);
        }
        tv_topic.setText(topic_category_list.get(0).title);
        tv_reply.setText(orderby.equals(LAST_COMMENT) ? reply_list.get(0).title : reply_list.get(1).title);
        ImageloaderUtil.loadLTLogo(this, groupInfo.group_icon, iv_topic_logo);
        tv_topic_name.setText(groupInfo.group_title);
        tv_topic_des.setText(groupInfo.group_summary);
        tv_topic_count.setText(groupInfo.popularity + "");
        titleBar.setTitle(groupInfo.group_title);
        titleBar.setGroupInfo(groupInfo);
        titleBar.setMoreButtonType(groupInfo.is_join == true ? MoreButtonType.GroupTopic.setJoin(true) : MoreButtonType.GroupTopic.setJoin(false));

    }

    private void initView() {
        // 添加用户信息动态监听
        UserInfoManager.instance().addListening(this);
        networkView = (NetWorkStateView) findViewById(R.id.topic_netWrokStateView);
        networkView.showLoadingBar();
        networkView.setRetryCallBack(this);
        listView = (RefreshAndLoadMoreListView) findViewById(R.id.topic_list_view);
        listView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.margin_size_8dp));
        headView = LayoutInflater.from(this).inflate(R.layout.topic_head_view, null);
        headView.setVisibility(View.GONE);
        ls = listView.getmListView();
        ls.addHeaderView(headView);

        network_notdata = (RelativeLayout) findViewById(R.id.network_notdata);

        mCircleView = (CircleRefreshView) findViewById(R.id.fa_button);
        mCircleView.bringToFront();
        mCircleView.setOnclick(new OnClickToRefresh() {
            @Override
            public void onClick() {
                Log.i("zzz", "小球的回调。。。刷新数据");
                mCircleView.startRotate();
                currentPage = 1;
                isAll = true;
                listView.getmListView().smoothScrollToPosition(0);
                requestAllData(1);
            }
        });

        adapter = new GroupTopicListAdapter(this);
        listView.setAdapter(adapter, false);
        setRefreshLisener();
        listView.setMyOnScrollListener(this);
        titleBar = (TitleBarView) findViewById(R.id.topic_bar);
        titleBar.setBackHomeVisibility(0);
        titleBar.setMoreButtonType(MoreButtonType.GroupTopic.setJoin(true));
        titleBar.setActivity(this);

        fl_publish_failed = (FailedBar) findViewById(R.id.fl_publish_failed);

        ib_topic = (ImageButton) headView.findViewById(R.id.ib_topic);
        ib_reply = (ImageButton) headView.findViewById(R.id.ib_reply);

        iv_topic_logo = (ImageView) headView.findViewById(R.id.iv_topic_logo);

        tv_topic = (TextView) headView.findViewById(R.id.tv_topic_selector);
        tv_reply = (TextView) headView.findViewById(R.id.tv_reply_selector);

        tv_topic_name = (TextView) headView.findViewById(R.id.tv_topic_name);
        tv_topic_des = (TextView) headView.findViewById(R.id.tv_topic_des);

        tv_topic_count = (TextView) headView.findViewById(R.id.tv_topic_count);

        btn_publish_topic = (Button) findViewById(R.id.btn_publish_topic);

        tv_network_title = (TextView) findViewById(R.id.network_title);
        tv_networkbutton_text = (TextView) findViewById(R.id.network_goDownLoading);
        tv_network_title.setText("暂时还没有内容");
        tv_networkbutton_text.setText("我来发表");
        ll_publish_topic = (LinearLayout) findViewById(R.id.ll_publish_topic);
        v_divier = findViewById(R.id.v_divider);

        tv_topic.setOnClickListener(this);
        tv_reply.setOnClickListener(this);
        ib_topic.setOnClickListener(this);
        ib_reply.setOnClickListener(this);
        tv_networkbutton_text.setOnClickListener(this);
        btn_publish_topic.setOnClickListener(this);
    }

    /***
     * 下拉/下拉监听
     */
    private void setRefreshLisener() {
        listView.setMyOnScrollListener(new RefreshAndLoadMoreListView.IOnScrollStateChanged() {
            @Override
            public void onScrollChangeListener(int scrollState) {
                resetArrow();
                isClick = false;
            }
        });
        listView.getmListView().setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                network_notdata.setVisibility(View.GONE);// 滑动时隐藏前沙发提示
                return false;
            }
        });
        listView.setmOnRefrshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                mCircleView.startRotate();// 下拉刷新开始旋转小球
                isAll = true;
                requestAllData(currentPage);
            }
        });
        listView.setOnLoadMoreListener(new RefreshAndLoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                requestAllData(++currentPage);
            }
        });
    }

    private List<TopicDetail> tempList;

    /**
     * 显示网络错误页面
     */
    private void showNetWorkErrorView() {
        networkView.showNetworkFailLayout();
        listView.setVisibility(View.GONE);
        mCircleView.setVisibility(View.GONE);
        listView.onLoadMoreComplete();
    }

    /**
     * 显示禁言/禁访页面
     */
    private void showForbadView() {
        listView.setVisibility(View.GONE);
        mCircleView.setVisibility(View.GONE);
        networkView.showNetworkNoDataLayout();
        networkView.setNotDataState(NetWorkStateView.gotoFeedback);
        networkView.setNoDataLayoutText("啊哦......您暂时无法进行该操作呢", "联系管理员");
        networkView.bringToFront();
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderby = LAST_COMMENT;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_topic_selector:
            case R.id.ib_topic:
                state = 0;
                mSpinerPopWindow = new SpinerPopWindow(this, topic_category_list, this);
                mSpinerPopWindow.setItemListener(this);
                showSpiner(ib_topic);
                ib_reply.setBackgroundResource(R.mipmap.ic_arrow_unselect);
                break;
            case R.id.tv_reply_selector:
            case R.id.ib_reply:
                state = 1;
                mSpinerPopWindow = new SpinerPopWindow(this, reply_list, this);
                mSpinerPopWindow.setItemListener(this);
                showSpiner(ib_reply);
                ib_topic.setBackgroundResource(R.mipmap.ic_arrow_unselect);
                break;
            case R.id.btn_publish_topic:
            case R.id.network_goDownLoading:
                /***
                 * 1.先判断用户是否登陆 2.再判断用户是否加入此小组
                 */
                CheckUserRightsTool.instance().checkUserRights(this, false, group_id, new NetIniCallBack() {
                    @Override
                    public void callback(int code) {
                        if (code == 0) {
                            Intent intent = new Intent(GroupTopicActivity.this, SendTopicActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("category", groupInfo.categories);
                            intent.putExtra("type", "1");// 1：正常启动
                            intent.putExtra("group_id", groupInfo.group_id);// 1：正常启动
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                });
                break;
        }
    }

    /***
     * 显示popwindow并判断点击状态
     *
     * @param ib
     */
    private void showSpiner(ImageButton ib) {
        mSpinerPopWindow.showAtLocation(titleBar, Gravity.CENTER, 0, 0);
        if (!isClick) {// 点击了
            ib.setBackgroundResource(R.mipmap.ic_arrow_select);
            isClick = true;
        } else if (isClick) {
            ib.setBackgroundResource(R.mipmap.ic_arrow_unselect);
            mSpinerPopWindow.dismiss();
            isClick = false;
        }
    }

    /**
     * 设置选中的内容
     *
     * @param list
     * @param pos
     */
    private void setValue(List<Category> list, int pos) {
        if (pos >= 0 && pos <= list.size()) {
            String value = list.get(pos).title;
            if (state == 0) {
                tv_topic.setText(value);
            } else {
                tv_reply.setText(value);
            }
        }
    }

    @Override
    public void onItemClick(int pos) {
        isClick = false;
        if (state == 0) {
            ib_topic.setBackgroundResource(R.mipmap.ic_arrow_unselect);
            setValue(topic_category_list, pos);
            if (pos > 0) {
                category_id = topic_category_list.get(pos).id;
                isAll = false;
            } else {
                isAll = true;
            }
            requestData(currentPage);
        } else {
            ib_reply.setBackgroundResource(R.mipmap.ic_arrow_unselect);
            setValue(reply_list, pos);
            switch (pos) {
                case 0:
                    orderby = LAST_COMMENT;
                    break;
                case 1:
                    orderby = LAST_TOPIC;
                    break;
            }
            requestData(currentPage);
        }
    }

    protected boolean isFirstPage() {
        return currentPage == 1;
    }

    /**
     * 复位箭头
     */
    public void resetArrow() {
        ib_reply.setBackgroundResource(R.mipmap.ic_arrow_unselect);
        ib_topic.setBackgroundResource(R.mipmap.ic_arrow_unselect);
    }

    @Override
    public void retry() {
        Log.i("zzz", "重试请求网络");
        requestAllData(currentPage);
    }

    /**
     * 请求全部数据
     */
    private void requestAllData(int currentPage) {
//        listView.getmListView().setSelection(listView.getmListView().getTop());
        requestHeadViewData();
        requestData(currentPage);
        Log.i("zzz", "请求网络");
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_GROUP_TOPIC_LIST);
    }

    @Override
    public void popCallBack() {
        isClick = false;
        resetArrow();
    }

    /**
     * 用户途中登录后重新刷新页面数据
     */
    private void flushDataFromUserLogin() {
        networkView.showLoadingBar();
        adapter.clearList();

        this.flushPageThread = new LTAsyncTask() {
            @Override
            protected Object doInBackground(Object[] aa) {
                int getDataPage = 1;

                // 记录是否最后一次获取数据
                boolean isLastGetData = false;
                final Map<String, String> params = new HashMap<String, String>();

                params.put("order_by", orderby);
                if (!isAll) {
                    params.put("category_id", category_id + "");
                }

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
        Net.instance().executeGet(Host.HostType.FORUM_HOST, Uri.getGroupTopicsUri(group_id), params, new WebCallBackToObj<TopicDetails>() {
            @Override
            protected void handle(TopicDetails info) {
                if (null != info) {
                    totalPage = info.getTotal_page();
                    if (totalPage == 0) {
                        int paddingHeight = MyApplication.width;
                        network_notdata.setPadding(0, paddingHeight * 1 / 3, 0, 0);
                        network_notdata.setVisibility(View.VISIBLE);
                        ll_publish_topic.setVisibility(View.INVISIBLE);
                        v_divier.setVisibility(View.INVISIBLE);
                        mCircleView.setVisibility(View.INVISIBLE);
                    } else {
                        adapter.addData(info.getDetails());
                        // 是否最后一次获取数据
                        if (isLastGetData) {
                            // 跳转到刷新页面前 listView的停留位置
                            listView.getmListView().setSelectionFromTop(curListViewposition, DistanceOfTop);
                            networkView.hideLoadingBar();
                            networkView.hideNetworkView();
                            listView.setVisibility(View.VISIBLE);
                            network_notdata.setVisibility(View.GONE);
                        }

                        synchronized (flushPageThread) {
                            flushPageThread.notify();
                        }

                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                networkView.showNetworkFailLayout();
                listView.setVisibility(View.GONE);
                mCircleView.setVisibility(View.GONE);
                listView.onLoadingFailed();
                networkView.bringToFront();
            }
        });

    }

    @Override
    public void userLogin(UserBaseInfo userBaseInfo) {
        flushDataFromUserLogin();

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
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        UserInfoManager.instance().removeListening(this);
    }

    @Override
    public void onScrollChangeListener(int scrollState) {
        // 不滚动时保存当前滚动到的位置
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            curListViewposition = listView.getmListView().getFirstVisiblePosition();

            // 获取第一个item与屏幕顶端的距离： top
            topView = listView.getmListView().getChildAt(0);
            DistanceOfTop = (topView == null) ? 0 : topView.getTop();
        }
    }
}
