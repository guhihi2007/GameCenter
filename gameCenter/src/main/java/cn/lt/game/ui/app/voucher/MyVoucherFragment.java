package cn.lt.game.ui.app.voucher;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.bean.MyVoucherItemBean;
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
 * 我的代金券
 * Created by Erosion on 2018/1/15.
 */

public class MyVoucherFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener, NetWorkStateView.RetryCallBack {
    private View mView;
    private RefreshAndLoadMoreListView mPullToRefreshListView;
    private NetWorkStateView netWorkStateView;
    private int page = 1;
    private MyVoucherAdapter adapter;
    private List<MyVoucherItemBean> itemBeans = new ArrayList<>();
    private int mTotalPage;

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_MY_VOUCHER);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_my_voucher, container, false);
        initView();
        LogUtils.i("Erosion","onCreateView");
        return mView;
    }

    private void initView() {
        mPullToRefreshListView = (RefreshAndLoadMoreListView) mView.findViewById(R.id.lv_my_voucher);
        netWorkStateView = (NetWorkStateView) mView.findViewById(R.id.net_my_voucher);
        netWorkStateView.setRetryCallBack(this);
        mPullToRefreshListView.setmOnRefrshListener(this);
        mPullToRefreshListView.setOnLoadMoreListener(this);
        adapter = new MyVoucherAdapter(getActivity());
        mPullToRefreshListView.setAdapter(adapter, false);
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

    @Override
    public void onRefresh() {
        page = 1;
        requestData();
    }

    @Override
    public void retry() {
        page = 1;
        LogUtils.i("Erosion", "retry");
        requestData();
    }

    private void requestData() {
        final Map<String, String> params = new HashMap();
        params.put("page", String.valueOf(page));
        netWorkStateView.showLoadingBar();
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.MY_VOUCHER_LIST, params, new WebCallBackToString() {
            @Override
            public void onSuccess(String result) {
                LogUtils.i("Erosion", "info===" + result);
                netWorkStateView.hideLoadingBar();

                LogUtils.i("Erosion", "getLastPage==" + getLastPage());
                mTotalPage = getLastPage() == -1 ? mTotalPage : getLastPage();
                mPullToRefreshListView.setCanLoadMore(page < mTotalPage);
                mPullToRefreshListView.onLoadMoreComplete();
                try {
                    JSONObject object = new JSONObject(result);
                    if (page == 1) {
                        itemBeans.clear();
                    }
                    itemBeans.addAll(GsonUtil.parseArray(object.getString("vouchers"), MyVoucherItemBean[].class));
                    if (null != itemBeans && itemBeans.size() > 0) {
                        adapter.setList(itemBeans,page == getLastPage() ? true : false);
                        adapter.notifyDataSetChanged();
                        netWorkStateView.hideNetworkFailLayout();
                        netWorkStateView.hideNetworkNoDataLayout();
                        mPullToRefreshListView.setVisibility(View.VISIBLE);
                        netWorkStateView.setVisibility(View.GONE);
                        LogUtils.i("Erosion", "itemBeans:" + itemBeans.size());
                    } else {
                        netWorkStateView.setNoDataLayoutText("您还没有可用的代金券", null);
                        netWorkStateView.showNetworkNoDataLayout();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                if (page != 1) {
                    page--;
                }
                mPullToRefreshListView.onLoadingFailed();
                mPullToRefreshListView.setVisibility(View.GONE);
                netWorkStateView.hideLoadingBar();
                if (page == 1) {
                    if (error.getMessage().toString().equals("请求未授权")) {
                        startActivity(UserInfoManager.instance().getLoginIntent(getActivity(), true));
                    } else {
                        netWorkStateView.showNetworkFailLayout();
                    }
                }
                LogUtils.i("Erosion", "onFailure===" + statusCode + ",==" + error.getMessage().toString());
            }

        });
    }

    @Override
    public void onLoadMore() {
        page++;
        requestData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(ExchangeVoucherEvent event) {
        if (event.isSuccess && event.updateMine) {
            page = 1;
            requestData();
        }
    }
}
