package cn.lt.game.ui.app.awardgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.global.LogTAG;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.bean.BaseBean;
import cn.lt.game.bean.GameInfoBean;
import cn.lt.game.bean.GamePointsNet;
import cn.lt.game.bean.SignPointsNet;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.netdata.ErrorFlag;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.redpoints.RedPointsManager;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.file.PointTaskRedUtil;
import cn.lt.game.lib.view.DownLoadBar;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.web.WebCallBackToBean;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.app.adapter.LTBaseAdapter;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListenerImpl;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;


/**
 * 获取积分页面
 */

public class ScoreFragment extends BaseFragment implements View.OnClickListener, NetWorkStateView.RetryCallBack, DownLoadBar.PullToBottomCallBack, UserInfoUpdateListening, SwipeRefreshLayout.OnRefreshListener {

    private RefreshAndLoadMoreListView mPullToRefreshListView;

    private Context mContext;
    private LTBaseAdapter mAdapter;
    private BaseOnclickListener mClickListener;

    private TextView tvSign;
    private TextView continuousSign;
    private TextView tvDownloadAd;
    private TextView tvSignAd;
    private TextView tvEmptyTip;

    private boolean isFirst = true;
    private NetWorkStateView netWorkStateView;
    private GamePointsNet info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View mView = inflater.inflate(R.layout.fragment_score, container, false);
        initView(mView);
        initHeadView();
        mClickListener = new BaseOnclickListenerImpl(mContext, getPageAlias());
        getPointGameDataNet();
        return mView;
    }

    private void initHeadView() {
        ListView mRealListView = mPullToRefreshListView.getmListView();
        View mHeadView = LayoutInflater.from(mContext).inflate(R.layout.fragment_score_top, null);
        continuousSign = (TextView) mHeadView.findViewById(R.id.user_center_sign_count);
        tvDownloadAd = (TextView) mHeadView.findViewById(R.id.tv_download_ad);
        tvSignAd = (TextView) mHeadView.findViewById(R.id.tv_sign_ad);
        tvEmptyTip = (TextView) mHeadView.findViewById(R.id.tv_empty_tip);
        tvSign = (TextView) mHeadView.findViewById(R.id.user_center_sign);
        tvSign.setOnClickListener(this);
        mRealListView.addHeaderView(mHeadView);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = this.getActivity();
    }

    private void initView(View view) {
        mPullToRefreshListView = (RefreshAndLoadMoreListView) view.findViewById(R.id.lv_points);
        mPullToRefreshListView.setmOnRefrshListener(this);
        mPullToRefreshListView.setCanLoadMore(false);
        netWorkStateView = (NetWorkStateView) view.findViewById(R.id.net_points_record);
        netWorkStateView.setRetryCallBack(this);
        UserInfoManager.instance().addRealListening(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UserInfoManager.instance().removeListening(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_center_sign:
                if (!UserInfoManager.instance().isLogin()) {
                    Intent intent = UserInfoManager.instance().getLoginIntent(mContext, true);
                    startActivity(intent);
                } else {
                    Map<String, String> params = new HashMap<>();
                    params.put("point_style", String.valueOf(Constant.POINTS_SIGN));
                    Net.instance().executePost(Host.HostType.GCENTER_HOST, Uri2.SIGN_POINTS, params, new WebCallBackToBean<BaseBean<SignPointsNet>>() {

                        @Override
                        public void handle(BaseBean<SignPointsNet> baseBean) {
                            LogUtils.d(LogTAG.HTAG, baseBean.toString());
                            SignPointsNet info = baseBean.getData();
                            if (info == null) {
                                return;
                            }
                            ToastUtils.showToast(mContext, "签到成功，+" + info.getPoints() + "积分");
//                            continuousSign.setText(String.format(getResources().getString(R.string.user_center_sign_count), info.getContinuous()));
                            continuousSign.setText(Html.fromHtml("<font color='#999999'>已连续签到 </font>" + "<font color='#ff8800'>" + String.valueOf(info.getContinuous()) + "</font>" + "<font color='#999999'> 天</font>"));
                            //刷新总积分
                            ((AwardActivity) mContext).setPoints(info.getTotal_point());
                            tvSign.setText(getResources().getString(R.string.user_center_signed));
                            tvSign.setTextColor(getResources().getColor(R.color.signed));
                            tvSign.setBackgroundResource(R.drawable.btn_green_selector_point_signed);
                            tvSign.setEnabled(false);
                        }

                        @Override
                        public void onFailure(int statusCode, Throwable error) {
                            LogUtils.d(LogTAG.HTAG, statusCode + "" + error.getMessage());
                            ToastUtils.showToast(mContext, error.getMessage());
                            if (statusCode == 0) {
                                tvSign.setText(getResources().getString(R.string.user_center_signed));
                                tvSign.setTextColor(getResources().getColor(R.color.signed));
                                tvSign.setBackgroundResource(R.drawable.btn_green_selector_point_signed);
                                tvSign.setEnabled(false);
                            } else if (statusCode == ErrorFlag.userLogout) {
//                                updateUIForLogout();
                                startActivity(UserInfoManager.instance().getLoginIntent(mContext, true));
                            }
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_JIFENG);
    }

    @Override
    public void pullToBottom() {

    }

    @Override
    public void retry() {
        getPointGameDataNet();
    }

    private void getPointGameDataNet() {
        if (isFirst) {
            mPullToRefreshListView.setVisibility(View.GONE);
            netWorkStateView.showLoadingBar();
            isFirst = false;
        }
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.ACTIVITY_POINTS, null, new WebCallBackToBean<BaseBean<GamePointsNet>>() {

            @Override
            protected void handle(BaseBean<GamePointsNet> baseBean) {
                info = baseBean.getData();
                if (info == null) {
                    return;
                }
                LogUtils.d(LogTAG.HTAG, info.toString());

                tvDownloadAd.setText(info.getDownload_ad());
                tvSignAd.setText(info.getSign_ad());
                saveActivityValue(info);
                if (!UserInfoManager.instance().isLogin()) {
                    updateUIForLogout();
                } else {
                    setLoginSignValueTip(info.is_signed(), info.getContinuous());
                }
                filterDownloadFromOther(info);
                mPullToRefreshListView.onLoadMoreComplete();
                mPullToRefreshListView.setVisibility(View.VISIBLE);
                netWorkStateView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                mPullToRefreshListView.onLoadMoreComplete();
                LogUtils.d(LogTAG.HTAG, String.valueOf(statusCode));
                if (statusCode == ErrorFlag.userLogout) {
                    updateUIForLogout();
                } else {
                    netWorkStateView.setVisibility(View.VISIBLE);
                    netWorkStateView.showNetworkFailLayout();
                    mPullToRefreshListView.setVisibility(View.GONE);
                }
                if (getUserVisibleHint() && isFirst) {
                    LogUtils.i(LogTAG.CHOU, "可见，获取积分活动数据失败+上报数据");
                    pageJumpReport("", Constant.PAGE_JIFENG);
                }

            }
        });
    }

    private void updateUIForLogout() {
        continuousSign.setVisibility(View.INVISIBLE);
        tvSign.setText(getResources().getString(R.string.user_center_sign));
        tvSign.setEnabled(true);
        tvSign.setTextColor(getResources().getColor(R.color.theme_green));
        tvSign.setBackgroundResource(R.drawable.btn_green_selector);
        ((AwardActivity) mContext).updateUIForLogout();
    }

    private void setLoginSignValueTip(boolean isSigned, int continuous) {
        tvSign.setText(isSigned ? getResources().getString(R.string.user_center_signed) : getResources().getString(R.string.user_center_sign));
        tvSign.setTextColor(isSigned ? getResources().getColor(R.color.signed) : getResources().getColor(R.color.theme_green));
        tvSign.setBackgroundResource(isSigned ? R.drawable.btn_green_selector_point_signed : R.drawable.btn_green_selector);
        tvSign.setEnabled(!isSigned);
        continuousSign.setVisibility(View.VISIBLE);
//        continuousSign.setText(String.format(getResources().getString(R.string.user_center_sign_count)
//                , continuous));
        continuousSign.setText(Html.fromHtml("<font color='#999999'>已连续签到 </font>" + "<font color='#ff8800'>" + String.valueOf(continuous) + "</font>" + "<font color='#999999'> 天</font>"));
    }

    /**
     * 过滤掉非活动页下载的游戏
     *
     * @param info
     */
    private void filterDownloadFromOther(GamePointsNet info) {
        List<ItemData<? extends BaseUIModule>> mDataList = new ArrayList<>();
        ArrayList<GameInfoBean> gameInfoBeanArrayList = info.getGames();
        if (gameInfoBeanArrayList == null) {
            return;
        }
        List<GameBaseDetail> downloadFileInfoList = FileDownloaders.getAllDownloadFileInfo();
        for (int i = 0; i < gameInfoBeanArrayList.size(); i++) {
            GameBaseDetail downloadFileInfo;
            boolean needAdd = true;

            if (UserInfoManager.instance().isLogin() && gameInfoBeanArrayList.get(i).getIs_accepted() == 1) {
                needAdd = false;
            } else {
                for (int j = 0; j < downloadFileInfoList.size(); j++) {
                    downloadFileInfo = downloadFileInfoList.get(j);
                    if (gameInfoBeanArrayList.get(i).getId().equals(String.valueOf(downloadFileInfo.getId()))) {
                        //下载来源非活动过滤,已领取过滤
                        if (!Constant.FROM_ACTIVITY.equals(downloadFileInfo.getDownloadFrom()) || downloadFileInfo.isAccepted() == 1) {
                            needAdd = false;
                        }
                        break;
                    }
                }
            }

            if (needAdd) {
                UIModule<GameDomainBaseDetail> module = new UIModule<>(PresentType.game_activity);
                module.setData(new GameDomainBaseDetail(gameInfoBeanArrayList.get(i)));

                ItemData<? extends BaseUIModule> item = new ItemData<>(module);
                item.setmType(module.getUIType());
                item.setPos(i);

                mDataList.add(item);
            }
        }
        //过滤完之后需要排序
        Collections.sort(mDataList, new ScoreComparator());
        //since 1.8+
        //mDataList.sort(new ScoreComparator());
        if (mAdapter == null) {
            mAdapter = new LTBaseAdapter(mContext, mClickListener);
            mPullToRefreshListView.setAdapter(mAdapter, false);
        }
        mAdapter.setList(mDataList);
        if (mDataList.size() == 0) {
            tvEmptyTip.setVisibility(View.VISIBLE);
        } else {
            tvEmptyTip.setVisibility(View.GONE);
        }

        PointTaskRedUtil.setLocalTaskList(gameInfoBeanArrayList);
        MyApplication.application.myTaskFlag = false;
        RedPointsManager.getInstance().redPointsBean.setMyTask(false);
    }

    /**
     * 保存活动信息
     *
     * @param info
     */
    public void saveActivityValue(GamePointsNet info) {
        MyApplication.application.activityId = String.valueOf(info.getActivity_id());
        MyApplication.application.activityName = info.getActivity_name();
        if (getUserVisibleHint() && isFirst) {
            LogUtils.i(LogTAG.CHOU, "可见，获取积分活动数据成功+上报数据");
            pageJumpReport(String.valueOf(info.getActivity_id()), Constant.PAGE_JIFENG);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.i(LogTAG.CHOU, "获取积分活动 setUserVisibleHint" + isVisibleToUser);
        if (info != null && isVisibleToUser) {
            pageJumpReport(String.valueOf(info.getActivity_id()), Constant.PAGE_JIFENG);
        }
    }

    /**
     * 登陆成功返回
     *
     * @param userBaseInfo
     */
    @Override
    public void userLogin(UserBaseInfo userBaseInfo) {

    }

    @Override
    public void updateUserInfo(UserBaseInfo userBaseInfo) {
        continuousSign.setVisibility(View.VISIBLE);
        getUserDataNet();
        getPointGameDataNet();
    }

    /**
     * 注销
     */
    @Override
    public void userLogout() {
        updateUIForLogout();
    }


    public void getUserDataNet() {
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.USER_CENTER_DATA, null, new WebCallBackToBean<SignPointsNet>() {

            @Override
            protected void handle(SignPointsNet info) {
                LogUtils.d(LogTAG.HTAG, info.toString());
                setLoginSignValueTip(info.is_signed(), info.getContinuous());
                ((AwardActivity) mContext).setPoints(info.getPoints());
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                if (statusCode == ErrorFlag.userLogout) {
                    updateUIForLogout();
                }
                LogUtils.d(LogTAG.HTAG, statusCode + "");
            }
        });
    }

    @Override
    public void onRefresh() {
        getPointGameDataNet();
    }
}
