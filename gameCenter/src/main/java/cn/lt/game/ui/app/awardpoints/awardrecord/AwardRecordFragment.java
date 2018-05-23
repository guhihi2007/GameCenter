package cn.lt.game.ui.app.awardpoints.awardrecord;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.global.LogTAG;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.app.awardpoints.AwardPointsRecordActivity;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;

/***
 * 中奖记录
 */
public class AwardRecordFragment extends BaseFragment implements OnClickListener, SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener, NetWorkStateView.RetryCallBack {
    private NetWorkStateView netWorkStateView;
    private View mView;
    private RefreshAndLoadMoreListView mPullToRefreshListView;
    List<AwardRecordBean> mRecordBeanList = new ArrayList<>();
    private AwardRecordAdapter adapter;
    private boolean isFirst = true;

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_ZHONGJIANG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_award_record, container, false);
        initView();
        loadData();
        return mView;
    }

    private void initView() {
        mPullToRefreshListView = (RefreshAndLoadMoreListView) mView.findViewById(R.id.lv_award_record);
        netWorkStateView = (NetWorkStateView) mView.findViewById(R.id.net_award_record);
        mPullToRefreshListView.setmOnRefrshListener(this);
        mPullToRefreshListView.setOnLoadMoreListener(this);
        View view = creatFootView();
        (view.findViewById(R.id.past_container)).setOnClickListener(this);
        (mPullToRefreshListView.getmListView()).addFooterView(view);
    }

    private View creatFootView() {
        return LayoutInflater.from(getActivity()).inflate(R.layout.item_award_check_past, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void loadData() {
        LogUtils.i(LogTAG.CHOU, "开始请求==中奖纪录");
        if (isFirst) {
            netWorkStateView.showLoadingBar();
            isFirst = false;
        }
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.AWARD_HISTORY, null, new WebCallBackToString() {
            @Override
            public void onSuccess(String result) {
                LogUtils.i(LogTAG.CHOU, "获取中奖纪录数据成功==" + result);
                netWorkStateView.hideLoadingBar();
                netWorkStateView.hideNetworkView();
                mPullToRefreshListView.onLoadMoreComplete();
                netWorkStateView.setVisibility(View.GONE);
                mRecordBeanList = AwardJsonAnalyzeUtil.parseResult(result);
                mPullToRefreshListView.setCanLoadMore(false);
                if (adapter == null) {
                    adapter = new AwardRecordAdapter(mRecordBeanList, getActivity());
                    mPullToRefreshListView.setAdapter(adapter,true);
                } else {
                    adapter.setData(mRecordBeanList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.i(LogTAG.CHOU, "获取中奖纪录数据失败" + statusCode + error);
                netWorkStateView.hideLoadingBar();
                netWorkStateView.hideNetworkView();
                mPullToRefreshListView.onLoadingFailed();
                if (statusCode == 404) {
                    netWorkStateView.showNetworkNoDataLayout();
                    netWorkStateView.setNoDataLayoutText((getActivity().getResources().getString(R.string.award_message_nodata_record)), "");
                } else if (statusCode == 201) {
                    ToastUtils.showToast(getActivity(), "登录过期，请重新登录");
                    UserInfoManager.instance().userLogout(false);
                    Intent intent = UserInfoManager.instance().getLoginIntent(getActivity(), true);
                    getActivity().startActivity(intent);
                } else {
                    netWorkStateView.showNetworkFailLayout();
                }
            }
        });
    }


    @Override
    public void retry() {
        loadData();
        ((AwardPointsRecordActivity) getActivity()).loadTitle();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.past_container:
                LogUtils.i(LogTAG.CHOU, "点击查看已过期");
                ActivityActionUtils.JumpToPastAwardActivity(getActivity());
                break;
            default:
                break;
        }
    }

//    @Override
//    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//        loadData();
//    }
//
//    @Override
//    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//        loadData();
//    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onLoadMore() {
        LogUtils.i(LogTAG.CHOU, "加载更多，以后TODO");
    }
}
