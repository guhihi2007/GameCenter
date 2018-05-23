package cn.lt.game.ui.app.voucher;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.bean.ExchangeVoucherBean;
import cn.lt.game.bean.ExchangeVoucherItemBean;
import cn.lt.game.event.ExchangeVoucherEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.GsonUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import de.greenrobot.event.EventBus;

/**
 * 兑换代金券
 * Created by Erosion on 2018/1/15.
 */

public class ExchangeVoucherFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, NetWorkStateView.RetryCallBack {
    private View mView;
    private NetWorkStateView netWorkStateView;
    private RefreshAndLoadMoreListView mPullToRefreshListView;
    private ExchangeVoucherAdapter adapter;
    private TextView point;

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_EXCHANGE_VOUCHER);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_exchange_voucher, container, false);
        initView();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UserInfoManager.instance().isLogin()) {
            requestData();
        } else {
            getActivity().finish();
        }
    }

    private void initView() {
        netWorkStateView = (NetWorkStateView) mView.findViewById(R.id.net_exchange_voucher);
        mPullToRefreshListView = (RefreshAndLoadMoreListView) mView.findViewById(R.id.lv_exchange_voucher);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.exchange_voucher_head_view, null);
        point = (TextView) view.findViewById(R.id.user_point);
        mPullToRefreshListView.getmListView().addHeaderView(view);
        mPullToRefreshListView.setmOnRefrshListener(this);
        mPullToRefreshListView.setCanLoadMore(false);
        netWorkStateView.setRetryCallBack(this);
        adapter = new ExchangeVoucherAdapter(getActivity());
        mPullToRefreshListView.setAdapter(adapter, false);
    }

    private void requestData() {
        netWorkStateView.showLoadingBar();
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.EXCHANGE_VOUCHER_LIST, new WebCallBackToString() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                mPullToRefreshListView.onLoadingFailed();
                mPullToRefreshListView.setVisibility(View.GONE);
                netWorkStateView.hideLoadingBar();
                if (error.getMessage().toString() .equals("请求未授权")) {
                    startActivity(UserInfoManager.instance().getLoginIntent(getActivity(), true));
                } else {
                    netWorkStateView.showNetworkFailLayout();
                }
                LogUtils.i("Erosion", "onFailure:" + statusCode + ",error:" + error.getMessage().toString());
            }

            @Override
            public void onSuccess(String result) {
                LogUtils.i("Erosion", "result:" + result);
                netWorkStateView.hideLoadingBar();
                netWorkStateView.hideNetworkFailLayout();
                mPullToRefreshListView.onLoadMoreComplete();
                ExchangeVoucherBean bean = GsonUtil.GsonToBean(result, ExchangeVoucherBean.class);
                List<ExchangeVoucherItemBean> beanList = bean.getData();
                if (beanList != null && beanList.size() > 0) {
                    mPullToRefreshListView.setVisibility(View.VISIBLE);
                    adapter.setList(beanList);
                    adapter.notifyDataSetChanged();
                    netWorkStateView.hideNetworkNoDataLayout();
                    netWorkStateView.hideNetworkFailLayout();
                } else {
                    LogUtils.i("Erosion","无数据");
                    netWorkStateView.setNoDataLayoutText("啊哦，代金券都被抢光啦",null);
                    netWorkStateView.showNetworkNoDataLayout();
                    mPullToRefreshListView.setVisibility(View.GONE);
                }

                point.setText(String.valueOf(bean.getPoint()));
            }
        });
    }

    @Override
    public void onRefresh() {
        requestData();
    }

    @Override
    public void retry() {
        requestData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 重新拉取数据
     * @param event
     */
    public void onEventMainThread(ExchangeVoucherEvent event) {
        if (event.isSuccess) {
            requestData();
        }
    }
}
