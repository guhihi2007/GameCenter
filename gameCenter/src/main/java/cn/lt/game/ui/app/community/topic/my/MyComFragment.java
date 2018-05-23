package cn.lt.game.ui.app.community.topic.my;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.net.NetUtils;
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
import cn.lt.game.ui.app.community.model.OthersPage;
import cn.lt.game.ui.app.community.topic.my.MyComFragment.GroupSubject.Type;
import cn.lt.game.ui.app.community.widget.UserGradeProgressBar;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

/***
 * 我的社区
 *
 * @author tiantian
 */
@SuppressLint("ValidFragment")
public class MyComFragment extends CommunityBaseFragment implements OnClickListener, RetryCallBack, SwipeRefreshLayout.OnRefreshListener, UserInfoUpdateListening {

    /**
     * Fragment根容器
     */
    private View mRoot;

    /**
     * listview
     */
    private ListView mListViewInPull;

    private RefreshAndLoadMoreListView mPullListView;


    private ComMineAdapter mAdapter;

    /**
     * listview item的基础数据
     */
    private List<ComMineAdapter.ItemData> mList = new ArrayList();

    private static String TAG = "ComMineFragment";

    /**
     * 网络相关布局视图
     */
    private NetWorkStateView mNetWorkStateView;

    /**
     * 登录相关的布局
     */
    private ViewStub mViewStubLoggin;


    private UserGradeProgressBar mProgressBar;

    /**
     * 登录相关的布局
     */
    private View mUnlogginRootView;

    @SuppressWarnings("unused")
    private String mId;

    private final static String UN_LOGGIN = "在登录状态下才能查看我的社区呢";

    @Override
    public void onRefresh() {
        requestGroup();
    }

    private enum State {
        unLogin, networkErr, noGroup, success, loading
    }

    private void showView(State state) {
        switch (state) {
            case unLogin:
                inflateUnloginView();
                hideListViewAndNetStateView();
                break;
            case noGroup:
                break;
            case networkErr:
                inflateNetWorkErrView();
                break;
            case success:
                hideUnloginView();
                mNetWorkStateView.hideNetworkView();
                mPullListView.setVisibility(View.VISIBLE);
                break;
            case loading:
                hideUnloginView();
                mPullListView.setVisibility(View.GONE);
                mNetWorkStateView.showLoadingBar();
                break;
        }
    }

    public static MyComFragment newInstance(String titleResId) {
        MyComFragment newFragment = new MyComFragment();
        return newFragment;
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_COMMUNTIY_MINE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.layout_com_mine, container, false);
        UserInfoManager.instance().addListening(this);
        initView();
        return mRoot;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (NetUtils.isConnected(this.getActivity())) {
            if (UserInfoManager.instance().isLogin()) {
                checkNetWork();
            } else {
                showView(State.unLogin);
            }
        } else {
            showView(State.networkErr);
        }


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnlogginRootView = null;
    }

    /**
     * 初始化并显示未登录的提示界面；未登录是调用此方法；
     */
    private void inflateUnloginView() {
        if (mUnlogginRootView == null) {
            mUnlogginRootView = mViewStubLoggin.inflate();
            mUnlogginRootView.findViewById(R.id.tv_loggin_mine).setOnClickListener(this);
            mUnlogginRootView.findViewById(R.id.tv_register_mine).setOnClickListener(this);
            ((TextView) mUnlogginRootView.findViewById(R.id.tv_un_loggin_text)).setText(UN_LOGGIN);
        }
        mUnlogginRootView.setVisibility(View.VISIBLE);
    }

    private void hideListViewAndNetStateView() {
        mNetWorkStateView.hideNetworkView();
        mPullListView.setVisibility(View.GONE);
    }

    /**
     * 检查网络，如果有网路则请求网络数据，无网络显示无网络界面；
     */
    private void checkNetWork() {
        mNetWorkStateView.showLoadingBar();
        requestGroup();
    }

    private void hideUnloginView() {
        if (mUnlogginRootView != null) {
            mUnlogginRootView.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化网络错误提示界面；
     */
    private void inflateNetWorkErrView() {
        mNetWorkStateView.showNetworkFailLayout();
        mPullListView.setVisibility(View.GONE);
        if (mUnlogginRootView != null) {
            mUnlogginRootView.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化界面布局；
     */
    private void initView() {
        mRoot.setVisibility(View.VISIBLE);
        mViewStubLoggin = (ViewStub) mRoot.findViewById(R.id.vs_unloggin);
        mNetWorkStateView = (NetWorkStateView) mRoot.findViewById(R.id.rank_netwrolStateView);
        mNetWorkStateView.setRetryCallBack(this);
        mPullListView = (RefreshAndLoadMoreListView) mRoot.findViewById(R.id.mycom_listView1);
        mListViewInPull = mPullListView.getmListView();
        initListView();
    }

    /**
     * 初始化listview相关；
     */
    private void initListView() {
        mAdapter = new ComMineAdapter(mActivity, null);
        mListViewInPull.setAdapter(mAdapter);
        mPullListView.setmOnRefrshListener(this);
        mPullListView.setCanLoadMore(false);
    }

    /**
     * 请求网络数据；
     */
    private void requestGroup() {

        Map<String, String> params = new HashMap<String, String>();

        // 网络请求数据；
        Net.instance().executeGet(HostType.FORUM_HOST, Uri.MY_COMMUNITY, params, new WebCallBackToObj<OthersPage>() {

            @Override
            protected void handle(OthersPage info) {
                mPullListView.onLoadMoreComplete();
                if (info != null) {
                    if (mList != null) {
                        mList.clear();
                    }
                    mList.add(getUserInfo(info));
                    mList.add(getComInfo(info));
                    mList.addAll(produceGroupSubject(info));
                    mAdapter.setmList(mList);
                    showView(State.success);
                } else {
                    showView(State.networkErr);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                ToastUtils.showToast(mActivity, error.getMessage());
                Log.d(TAG, error.toString());
                mPullListView.onLoadMoreComplete();
                if (statusCode == 503) {
                    ((CommunityActivity) getActivity()).abnormalDisplay(CloseType.shut);
                } else if (statusCode == 901) {
                    ((CommunityActivity) getActivity()).abnormalDisplay(CloseType.disita);
                } else if (statusCode == 201) {
                    return;
                } else {
                    showView(State.networkErr);
                }
            }
        });
    }

    private UserInfo getUserInfo(OthersPage info) {
        UserInfo userinfo = new UserInfo();
        userinfo.setUser_nickname(info.getUser_nickname());
        userinfo.setUser_level(info.getUser_level());
        userinfo.setUser_icon(info.getUser_icon());
        userinfo.setUser_gold(info.getUser_gold());
        userinfo.setBackground_img(info.getBackground_img());
        userinfo.setUser_summary(info.getUser_summary());
        userinfo.setUser_upgrade_percent(info.getUser_upgrade_percent());
        return userinfo;

    }

    private ComInfo getComInfo(OthersPage info) {
        ComInfo comInfo = new ComInfo();
        comInfo.setTopic_count(info.getTopic_count());
        comInfo.setComment_count(info.getComment_count());
        comInfo.setGroup_count(info.getGroup_count());
        return comInfo;
    }

    private List<ComMineAdapter.ItemData> produceGroupSubject(OthersPage info) {

        List<ComMineAdapter.ItemData> list = new ArrayList();
        for (int i = 0; i < 6; i++) {
            GroupSubject subject = new GroupSubject();
            switch (i) {
                case 0:
                    subject.setmName("我的私信");
                    subject.setHasNewMsg(info.getHas_new_letter());
                    subject.setmType(Type.MY_LETTER);
                    break;

                case 1:
                    subject.setmName("社区通知");
                    subject.setHasNewNotice(info.getHas_new_notice());
                    subject.setmType(Type.MY_NOTICE);
                    break;

                case 2:
                    subject.setmName("我的关注");
                    subject.setmType(Type.MY_CONCERN);
                    break;
                case 3:
                    subject.setmName("我的粉丝");
                    subject.setmType(Type.MY_FAN);
                    break;
                case 4:
                    subject.setmName("我的收藏");
                    subject.setmType(Type.MY_COLLECT);
                    break;
                case 5:
                    subject.setmName("我的草稿");
                    subject.setmType(Type.MY_DRADT);
                    break;
            }
            list.add(subject);
        }
        return list;
    }

    @Override
    public void retry() {
        checkNetWork();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_register_mine:
                UserInfoManager.instance().starRegister(mActivity);
                break;
            case R.id.tv_loggin_mine:
                UserInfoManager.instance().starLogin(mActivity, true);
                break;
        }
    }

//    @Override
//    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//        requestGroup();
//    }
//
//    @Override
//    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//
//    }

    @Override
    public void requestData() {

    }

    @Override
    public void release() {

    }

    @Override
    public void userLogin(UserBaseInfo userBaseInfo) {
        checkNetWork();
    }

    @Override
    public void updateUserInfo(UserBaseInfo userBaseInfo) {

    }

    @Override
    public void userLogout() {
        showView(State.unLogin);
    }

    public static class ComInfo extends ComMineAdapter.ItemData {
        private int topic_count;
        private int comment_count;
        private int group_count;

        public ComInfo() {
            setmObjType(ObjectType.ComInfo);
        }

        public int getTopic_count() {
            return topic_count;
        }

        public void setTopic_count(int topic_count) {
            this.topic_count = topic_count;
        }

        public int getComment_count() {
            return comment_count;
        }

        public void setComment_count(int comment_count) {
            this.comment_count = comment_count;
        }

        public int getGroup_count() {
            return group_count;
        }

        public void setGroup_count(int group_count) {
            this.group_count = group_count;
        }
    }

    public static class UserInfo extends ComMineAdapter.ItemData {
        public String user_nickname;//作者昵称
        public String user_icon;//作者icon图地址
        private String background_img;//TA的主页封面图片地址
        public int user_level;//用户等级
        private String user_summary;//用户签名
        private int user_gold;//用户金币
        private int user_upgrade_percent;//⽤户距离升级的经验百分⽐

        public UserInfo() {
            setmObjType(ObjectType.UserInfo);
        }

        public int getUser_level() {
            return user_level;
        }

        public void setUser_level(int user_level) {
            this.user_level = user_level;
        }

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }

        public String getUser_icon() {
            return user_icon;
        }

        public void setUser_icon(String user_icon) {
            this.user_icon = user_icon;
        }

        public String getBackground_img() {
            return background_img;
        }

        public void setBackground_img(String background_img) {
            this.background_img = background_img;
        }

        public String getUser_summary() {
            return user_summary;
        }

        public void setUser_summary(String user_summary) {
            this.user_summary = user_summary;
        }

        public int getUser_gold() {
            return user_gold;
        }

        public void setUser_gold(int user_gold) {
            this.user_gold = user_gold;
        }

        public int getUser_upgrade_percent() {
            return user_upgrade_percent;
        }

        public void setUser_upgrade_percent(int user_upgrade_percent) {
            this.user_upgrade_percent = user_upgrade_percent;
        }
    }

    public static class GroupSubject extends ComMineAdapter.ItemData {

        private String mName;

        public enum Type {
            MY_LETTER, MY_NOTICE, MY_CONCERN, MY_FAN, MY_COLLECT, MY_DRADT, PUBLISH_TOPIC, COMMENT_TOPIC, COLLECT_TOPIC, DRAFT_TOPIC
        }

        public GroupSubject() {
            setmObjType(ObjectType.ItemInfo);
        }

        private int hasNewMsg;

        private int hasNewNotice;
        private Type mType;

        public Type getmType() {
            return mType;
        }

        public void setmType(Type mType) {
            this.mType = mType;
        }

        public String getmName() {
            return mName;
        }

        public void setmName(String mName) {
            this.mName = mName;
        }

        public int getHasNewMsg() {
            return hasNewMsg;
        }

        public void setHasNewMsg(int hasNewMsg) {
            this.hasNewMsg = hasNewMsg;
        }

        public int getHasNewNotice() {
            return hasNewNotice;
        }

        public void setHasNewNotice(int hasNewNotice) {
            this.hasNewNotice = hasNewNotice;
        }
    }

}
