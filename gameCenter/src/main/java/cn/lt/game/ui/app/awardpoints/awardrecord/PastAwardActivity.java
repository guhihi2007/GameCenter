package cn.lt.game.ui.app.awardpoints.awardrecord;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.global.LogTAG;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;

/**
 * @author chengyong
 * @time 2017/6/13 11:23
 * @des ${过期奖品}
 */

public class PastAwardActivity extends BaseActivity implements NetWorkStateView.RetryCallBack, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener {
    private NetWorkStateView netWorkStateView;
    private RefreshAndLoadMoreListView mPullToRefreshListView;
    List<AwardRecordBean> mRecordBeanList = new ArrayList<>();
    private AwardRecordAdapter adapter;
    private TitleBarView titleBar;

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_HISTORY);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_award_past);
        initView();
        loadData();
    }

    private void loadData() {
        LogUtils.i(LogTAG.CHOU, "开始请求 过期奖品");
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.AWARD_EXPIRE, null, new WebCallBackToString() {
            @Override
            public void onSuccess(String result) {
                LogUtils.i(LogTAG.CHOU, "获取 过期奖品 数据成功" + result);
                netWorkStateView.hideLoadingBar();
                netWorkStateView.hideNetworkView();
                mPullToRefreshListView.onLoadMoreComplete();
                netWorkStateView.setVisibility(View.GONE);
                mRecordBeanList = AwardJsonAnalyzeUtil.parseResult(result);
                if (adapter == null) {
                    adapter = new AwardRecordAdapter(mRecordBeanList, PastAwardActivity.this);
                    mPullToRefreshListView.setAdapter(adapter, false);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.i(LogTAG.CHOU, "获取 过期奖品 数据失败" + statusCode + error);
                if (statusCode == 404) {
                    netWorkStateView.showNetworkNoDataLayout();
                    netWorkStateView.setNoDataLayoutText((PastAwardActivity.this.getResources().getString(R.string.award_message_nodata_record_past)), "");
                } else if (statusCode == 201) {
                    ToastUtils.showToast(PastAwardActivity.this, "未登录");
                } else {
                    netWorkStateView.showNetworkFailLayout();
                }
            }
        });
    }

    private void initView() {
        titleBar = (TitleBarView) findViewById(R.id.action_bar_award_past);
        mPullToRefreshListView = (RefreshAndLoadMoreListView) findViewById(R.id.lv_award_past);
        netWorkStateView = (NetWorkStateView) findViewById(R.id.net_award_past);
        mPullToRefreshListView.setmOnRefrshListener(this);
        titleBar.setBackHomeVisibility(View.INVISIBLE);
        titleBar.setTitle("已过期奖品");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void retry() {
        loadData();
    }


    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onLoadMore() {
        loadData();
    }
}
