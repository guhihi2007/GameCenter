package cn.lt.game.ui.app.community.topic.my;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host.HostType;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.CommunityBaseFragment;
import cn.lt.game.ui.app.community.model.Comment;
import cn.lt.game.ui.app.community.model.Comments;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

/***
 * 我发表的评论&&TA的评论
 */
@SuppressLint("ValidFragment")
public class CommentMineFragment extends CommunityBaseFragment implements RetryCallBack, SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener {

    private View mRoot;

    private ListView mListViewInPull;

    private RefreshAndLoadMoreListView mPullListView;

    private CommentTopicAdapter mAdapter;

    private int mCurrentNeedLoadPage = 1;

    private List<Comment> mList;

    private static final String TAG = "CommentMineFragment";

    private int mTopicTotalPage;

    private int titleResId;

    private int userId;

    private String uri;

    private UserBaseInfo mUserInfo;

    /**
     * 网络相关布局视图
     */
    private NetWorkStateView mNetWorkStateView;

    public static CommentMineFragment newInstance(int titleResId) {
        CommentMineFragment commentMineFragment = new CommentMineFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("titleResId", titleResId);
        commentMineFragment.setArguments(bundle);
        return commentMineFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        this.titleResId = bundle.getInt("titleResId");
        userId = activity.getIntent().getIntExtra("userId", -1);
        switch (titleResId) {
            case R.string.published_comment:
                uri = Uri.COM_USERS_COMMENTS;
                break;
            case R.string.ta_comment:
                uri = Uri.getOthersPageCommentUri(userId);
                break;
        }

    }

    @Override
    public void setPageAlias() {
        switch (titleResId) {
            case R.string.published_comment:
                if (UserInfoManager.instance().getUserInfo().getId() == this.userId) {// 是自己的主页
                    setmPageAlias(Constant.PAGE_COMMENT_MINE);
                } else {// 是ta的主页
                    setmPageAlias(Constant.PAGE_HOME_PAGE_HER);
                }
                break;
            case R.string.ta_comment:
                setmPageAlias(Constant.PAGE_COMMENT_HER);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.comment_topic_list, null);
        initView();
        super.onCreateView(inflater, container, savedInstanceState);
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checkNetWork();
    }

    private void initView() {
        mPullListView = (RefreshAndLoadMoreListView) mRoot.findViewById(R.id.comment_topic_list_listView1);
        mNetWorkStateView = (NetWorkStateView) mRoot.findViewById(R.id.rank_netwrolStateView);
        mNetWorkStateView.setRetryCallBack(this);
        mNetWorkStateView.setNoDataCatSyle(NetWorkStateView.CatStyle.SINISTER_SMILE);
        mNetWorkStateView.setNoDataLayoutText(titleResId == R.string.ta_comment ? "ta还没有发表过评论" : "你还没有发表过评论", null);
        iniListView();
        mPullListView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.margin_size_8dp));
    }

    private void iniListView() {
        mListViewInPull = mPullListView.getmListView();
        mPullListView.setmOnRefrshListener(this);
        mPullListView.setOnLoadMoreListener(this);
        mAdapter = new CommentTopicAdapter(this.getActivity(), null);
        mPullListView.setAdapter(mAdapter, false);
        LinearLayout headView = (LinearLayout) LayoutInflater.from(this.getActivity()).inflate(R.layout.footer_transparent, null);
        headView.removeViewAt(0);
        mListViewInPull.addFooterView(headView);
    }

    /**
     * 检查网络，如果有网路则请求网络数据，无网络显示无网络界面；
     */
    private void checkNetWork() {
        if (NetUtils.isConnected(this.getActivity())) {
            // reset;
            mCurrentNeedLoadPage = 1;
//            mPullListView.setMode(Mode.PULL_FROM_END);
            mNetWorkStateView.showLoadingBar();
            mPullListView.setVisibility(View.VISIBLE);
            requestData(mCurrentNeedLoadPage++);
        } else {
            inflateNetWorkErrView();
        }
    }

    /**
     * 初始化网络错误提示界面；
     */
    private void inflateNetWorkErrView() {
        mNetWorkStateView.showNetworkFailLayout();
        mPullListView.setVisibility(View.GONE);
        mCurrentNeedLoadPage = 1;
    }

    private void requestData(final int page) {

        Map<String, String> params = new HashMap<>();
        params.put("page", page + "");
        Net.instance().executeGet(HostType.FORUM_HOST, uri, params, new WebCallBackToObj<Comments>() {

            @Override
            protected void handle(Comments info) {
                mPullListView.onLoadMoreComplete();
                if (info != null && info.getTotal_page() > 0) {
                    mNetWorkStateView.hideNetworkView();
                    mTopicTotalPage = info.getTotal_page();
                    mList = titleResId == R.string.published_comment ? info.getDetail() : info.getData();
//                    addUserInfo(mList);
                    mAdapter.setmList(mList);
                    mPullListView.setCanLoadMore(page < info.getTotal_page());
                    mPullListView.onLoadMoreComplete();
//                    if (mTopicTotalPage <= page) {
//                        mPullListView.setMode(Mode.DISABLED);
//                    }
                } else if (page == 1) {
                    mNetWorkStateView.showNetworkNoDataLayout();
//                    mPullListView.setMode(Mode.DISABLED);
                }
            }


            @Override
            public void onFailure(int statusCode, Throwable error) {
                Log.d(TAG, "请求数据失败");
                mPullListView.onLoadMoreComplete();
                mPullListView.setVisibility(View.GONE);
                mCurrentNeedLoadPage--;
                if (page <= 1) {
                    mNetWorkStateView.showNetworkFailLayout();
                }
            }


        });

    }


    @Override
    public void retry() {
        checkNetWork();
    }
    @Override
    public void onRefresh() {
        requestData(mCurrentNeedLoadPage);
    }

    @Override
    public void onLoadMore() {
        requestData(mCurrentNeedLoadPage++);
    }
}
