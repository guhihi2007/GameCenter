package cn.lt.game.ui.app.awardpoints;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.global.LogTAG;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.bean.PointsRecord;
import cn.lt.game.bean.PointsRecordNet;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.netdata.ErrorFlag;
import cn.lt.game.lib.util.GsonUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;


/***
 * 积分记录
 */
public class PointsRecordFragment extends BaseFragment implements OnClickListener, SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener, NetWorkStateView.RetryCallBack {
    private NetWorkStateView netWorkStateView;
    private PointsRecordAdapter adapter;
    private PointsRecordNet pointsRecordNet = new PointsRecordNet();
    private Context mContext;
    private RefreshAndLoadMoreListView mPullToRefreshListView;
    private boolean isFirst = true;
    private int mCurrPage = 1;

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_JIFEN_RECORD);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View mView = inflater.inflate(R.layout.fragment_points_record, container, false);
        initView(mView);
        requestPointsData();
        return mView;
    }

    private void initView(View view) {
        mPullToRefreshListView = (RefreshAndLoadMoreListView) view.findViewById(R.id.lv_points_record);
        netWorkStateView = (NetWorkStateView) view.findViewById(R.id.net_points_record);
        netWorkStateView.setRetryCallBack(this);
        mPullToRefreshListView.setmOnRefrshListener(this);
        mPullToRefreshListView.setOnLoadMoreListener(this);
    }


    @Override
    public void retry() {
        requestPointsData();
    }


    @Override
    public void onClick(View view) {

    }

    private void requestPointsData() {
        if (isFirst) {
            netWorkStateView.showLoadingBar();
            isFirst = false;
        }
        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(mCurrPage));
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.POINTS_RECORD, params, new WebCallBackToString() {
            @Override
            public void onSuccess(String result) {
                mPullToRefreshListView.onLoadMoreComplete();
                LogUtils.d(LogTAG.HTAG, result);
                if (mCurrPage == 1) {
                    pointsRecordNet.getPoint_histories().clear();
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    pointsRecordNet.setUse_able(jsonObject.optInt("use_able"));
                    if (jsonObject.optJSONArray("point_histories") != null) {
                        ArrayList<PointsRecord> recordArrayList = GsonUtil.jsonToArrayList(jsonObject.optJSONArray("point_histories").toString(), PointsRecord.class);
                        pointsRecordNet.getPoint_histories().addAll(recordArrayList);
                        mPullToRefreshListView.setCanLoadMore(true);
                    } else {
                        mPullToRefreshListView.setCanLoadMore(false);
                    }
                    notifyAdapter();
                    dealRefreshListView();
                    if (pointsRecordNet.getPoint_histories().size() == 0) {
                        netWorkStateView.showNetworkNoDataLayout();
                        netWorkStateView.setNoDataLayoutText(getResources().getString(R.string.point_record_nodata_tip), "");
                    } else {
                        netWorkStateView.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.d(LogTAG.HTAG, statusCode + "");
                netWorkStateView.hideLoadingBar();
                mPullToRefreshListView.onLoadingFailed();
                if (mCurrPage != 1) {
                    mCurrPage--;
                }
                if (statusCode == ErrorFlag.userLogout) {
                    startActivity(UserInfoManager.instance().getLoginIntent(mContext, true));
                    ((AwardPointsRecordActivity) mContext).finish();
                } else if (statusCode == ErrorFlag.netError) {
                    netWorkStateView.showNetworkFailLayout();
                }


            }
        });
    }

    private void notifyAdapter() {
        if (adapter == null) {
            adapter = new PointsRecordAdapter(mContext, pointsRecordNet);
            mPullToRefreshListView.setAdapter(adapter, false);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void dealRefreshListView() {
        if (pointsRecordNet.getPoint_histories().size() % 15 != 0) {
            mPullToRefreshListView.setCanLoadMore(false);
        } else {
//            mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        }

    }

    @Override
    public void onRefresh() {
        mCurrPage = 1;
        requestPointsData();
    }

    @Override
    public void onLoadMore() {
        mCurrPage++;
        requestPointsData();
    }
}
