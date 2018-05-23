package cn.lt.game.ui.app.awardgame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.web.WebCallBackToStringForAward;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.app.awardgame.bean.AwardInfoBean;
import cn.lt.game.ui.app.awardgame.listener.AwardUpdateListener;
import cn.lt.game.ui.app.awardgame.view.AwardsPlateView;

/**
 * @author chengyong
 * @time 2017/6/1 14:32
 * @des ${抽奖fragment}
 */

public class AwardFragment extends BaseFragment implements View.OnClickListener,
        NetWorkStateView.RetryCallBack,AwardUpdateListener {
    private View mView = null;
    private NetWorkStateView netWrokStateView;
    private AwardsPlateView mAwardPlateView;
    private ScrollView elasticScrollView;
    private TextView mAwardProcessTv;
    private TextView mAwardRuleTv;
    private boolean isRefreshByTimeout;
    private boolean isFromCreate;
    private AwardInfoBean awardInfoBean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if(mView==null){
            mView = inflater.inflate(R.layout.fragment_award, container, false);
            initView();
        }
        return mView;
    }


    private void initView() {
        netWrokStateView = (NetWorkStateView) mView.findViewById(R.id.award_netWrokStateView);
        elasticScrollView = (ScrollView) mView.findViewById(R.id.award_elasticSV);
        mAwardPlateView = (AwardsPlateView) mView.findViewById(R.id.award_container);
        mAwardProcessTv = (TextView) mView.findViewById(R.id.award_process_tv);
        mAwardRuleTv = (TextView) mView.findViewById(R.id.award_rule_tv);
        netWrokStateView.setRetryCallBack(this);
        netWrokStateView.showLoadingBar();
        mAwardPlateView.setUpdateListener(this);
        mAwardPlateView.startView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAwardPlateView.startGame(v,((AwardActivity)getActivity()).points);
            }
        });
        loadAwardData(false,true);
    }

    /**
     * 请求抽奖活动信息
     * String.format(getResources().getString(R.string.user_center_sign_count), 5)
     * @param isRefreshByTimeout
     * @param isFromCreate
     */
    private void loadAwardData(boolean isRefreshByTimeout, final boolean isFromCreate) {
        this.isRefreshByTimeout=isRefreshByTimeout;
        this.isFromCreate=isFromCreate;
        netWrokStateView.showLoadingBar();
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.AWARD_INFO_FRAGMENT,
                null, new WebCallBackToStringForAward() {
                    @Override
                    public void onSuccess(String result) {
                        LogUtils.i(LogTAG.CHOU, "获取活动数据成功==" + result);
                        netWrokStateView.hideLoadingBar();
                        netWrokStateView.hideNetworkView();
                        parseData(result);
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error) {
                        LogUtils.i(LogTAG.CHOU, "获取活动数据失败" + statusCode+error);
                        netWrokStateView.showNetworkFailLayout();
                        if(getUserVisibleHint() && isFromCreate){
                            LogUtils.i(LogTAG.CHOU, "可见，获取抽奖活动数据失败+上报数据");
                            pageJumpReport("", Constant.PAGE_CHOUJIANG);
                        }
                    }
                });
    }

    private void parseData(String result) {
        try {
            awardInfoBean = new Gson().fromJson(result,AwardInfoBean.class);
            ((AwardActivity) getActivity()).titleBarView.setTitle(awardInfoBean.activity_name);
            mAwardProcessTv.setText(awardInfoBean.activity_flow);
            mAwardRuleTv.setText(awardInfoBean.activity_rule);
            MyApplication.application.activityId= awardInfoBean.activity_id;
            mAwardPlateView.setAwardData(awardInfoBean,isRefreshByTimeout);
            if(getUserVisibleHint() && isFromCreate){
                LogUtils.i(LogTAG.CHOU, "可见，获取抽奖活动数据成功+上报数据");
                pageJumpReport(awardInfoBean.activity_id,Constant.PAGE_CHOUJIANG);
            }
        } catch (JsonSyntaxException e) {
            LogUtils.i(LogTAG.CHOU, "可见，获取抽奖活动数据解析异常"+e.getMessage());
            e.printStackTrace();
        }
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.i(LogTAG.CHOU, "获取抽奖活动 setUserVisibleHint"+isVisibleToUser);
        if(awardInfoBean!=null && isVisibleToUser){
            pageJumpReport(awardInfoBean.activity_id, Constant.PAGE_CHOUJIANG);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_CHOUJIANG);
    }

    @Override
    public void retry() {
        LogUtils.i(LogTAG.CHOU, "点击重试-抽奖活动页" );
        loadAwardData(false, false);
    }

    @Override
    public void updateScore() {
        LogUtils.i(LogTAG.CHOU, "抽奖活动页 updateScore" );
        ((AwardActivity)getActivity()).getUserDataNet();
    }

    @Override
    public void updateScoreByManual(int offScore) {
        ((AwardActivity)getActivity()).setPointsByManual(offScore);
    }

    @Override
    public void updateTimes(boolean isRefreshByTimeout) {
        LogUtils.i(LogTAG.CHOU, "再次请求抽奖活动页 updateTimes" );
        loadAwardData(isRefreshByTimeout, false);
    }

    @Override
    public void jumpToScoreFragment() {
        LogUtils.i(LogTAG.CHOU, "awardFragment页面： jumpToScoreFragment" );
        ((AwardActivity)getActivity()).indicator.setCurrentTab(1);
    }
}
