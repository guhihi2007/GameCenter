package cn.lt.game.ui.app.specialtopic;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.essence.DomainType;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.domain.essence.FunctionEssenceImpl;
import cn.lt.game.domain.essence.ImageType;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.view.TitleMoreButton;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsDataProductorImpl;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.pageunits.PageMultiUnitsReportManager;
import cn.lt.game.ui.app.adapter.LTBaseAdapter;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListenerImpl;
import cn.lt.game.ui.app.adapter.parser.NetDataAddShell;
import de.greenrobot.event.EventBus;

/**
 * 专题列表界面
 */
public class SpecialTopicActivity extends BaseActivity implements NetWorkStateView.RetryCallBack, SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener, RefreshAndLoadMoreListView.IOnScrollStateChanged {

    private RefreshAndLoadMoreListView mPullToRefreshListView;

    private LTBaseAdapter adapter;
    private ImageView iv_headImageView;
    private TextView tv_xbym;
    private TextView tv_beforeTopic;// 往期间隔专题文字
    private View headBannerview;
    private NetWorkStateView netWorkStateView;

    private int screenWidth;// 实际屏幕宽
    private BaseOnclickListenerImpl mClickListener;
    private ItemData<? extends BaseUIModule> bannerData;// banner图的数据
    private List<ItemData<? extends BaseUIModule>> requestDatalist;
    private List<ItemData<? extends BaseUIModule>> befroeDatalist = new ArrayList<>();
    private int page = 1;

    private boolean isFirstLoadData = true;
    private int mlastPagePositionOfNetData = 0;
    private int totalPageSize = 0;
    private List<ItemData<? extends BaseUIModule>> saveTemp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_special_topic_v2);
        mClickListener = new BaseOnclickListenerImpl(this, getPageAlias());
        initView();
        loadData();
    }

    protected void initView() {
        TitleBarView search_bar = (TitleBarView) findViewById(R.id.detail_action_bar);
        search_bar.setMoreButtonType(TitleMoreButton.MoreButtonType.Special);
        search_bar.setTitle("专题列表");

        headBannerview = View.inflate(this, R.layout.item_special_topic_head_v2, null);
        iv_headImageView = (ImageView) headBannerview.findViewById(R.id.iv_specialTopicImage);
        tv_xbym = (TextView) headBannerview.findViewById(R.id.tv_specialTopicTitle);
        tv_beforeTopic = (TextView) headBannerview.findViewById(R.id.tv_beforeTopic);

        mPullToRefreshListView = (RefreshAndLoadMoreListView) findViewById(R.id.pullToRefreshListView);
        mPullToRefreshListView.getmListView().addHeaderView(headBannerview);
        mPullToRefreshListView.setmOnRefrshListener(this);
        mPullToRefreshListView.setOnLoadMoreListener(this);
        mPullToRefreshListView.setRefreshEnabled(false);

        netWorkStateView = (NetWorkStateView) findViewById(R.id.game_detail_netWrokStateView);
        netWorkStateView.setRetryCallBack(this);

        adapter = new LTBaseAdapter(SpecialTopicActivity.this, mClickListener);
        mPullToRefreshListView.setAdapter(adapter, false);
        mPullToRefreshListView.setMyOnScrollListener(this);
    }

    /**
     * 连接
     */
    protected void loadData() {
        if (NetUtils.isConnected(this)) {
            netWorkStateView.showLoadingBar();
            getNetWorkData();
        } else {
            netWorkStateView.showNetworkFailLayout();
        }
    }

    private void getNetWorkData() {
        retry();
    }

    @Override
    public void retry() {
        Map<String, String> params = new HashMap<>();
        params.put("page", page + "");
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.SPECIAL_TOPICS_URI, params, new WebCallBackToObject<UIModuleList>() {

            @Override
            protected void handle(UIModuleList info) {
                try {
                    requestDatalist = NetDataAddShell.wrapModuleList(info, mlastPagePositionOfNetData);
                    setMlastPagePositionOfNetData(requestDatalist);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                saveTemp.addAll(requestDatalist);
                // 如果是刚进页面第一次请求，
                if (isFirstLoadData) {

                    // 没有数据的话，显示友好界面
                    if (null == requestDatalist || requestDatalist.size() == 0) {
                        mPullToRefreshListView.setVisibility(View.GONE);
                        netWorkStateView.hideLoadingBar();
                        netWorkStateView.setNoDataLayoutText("亲，目前还没有专题哦~", "");
                        netWorkStateView.showNetworkNoDataLayout();
                        return;
                    }

                    // 设置第一个item的banner数据
                    bannerData = requestDatalist.remove(0);
                    processBannerData();
                }
                processBeforeData();

                netWorkStateView.hideLoadingBar();
                netWorkStateView.hideNetworkView();
                totalPageSize = getLastPage();
                mPullToRefreshListView.setCanLoadMore(page < totalPageSize);
                mPullToRefreshListView.onLoadMoreComplete();
                mPullToRefreshListView.setVisibility(View.VISIBLE);

                if (totalPageSize == 1) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        }
                    }, 500);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                mPullToRefreshListView.setVisibility(View.GONE);
                mPullToRefreshListView.onLoadMoreComplete();
                netWorkStateView.hideLoadingBar();
                netWorkStateView.showNetworkFailLayout();
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PageMultiUnitsReportManager.getInstance().buildPageUnits(saveTemp, mPullToRefreshListView, Constant.PAGE_SUBJECT_LIST, "", "", "", "");
    }

    private void setMlastPagePositionOfNetData(List<ItemData<? extends BaseUIModule>> temp) {
        try {
            if (temp != null && temp.size() > 0) {
                ItemData item = temp.get(temp.size() - 1);
                this.mlastPagePositionOfNetData = item.getPos();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processBeforeData() {
        if (requestDatalist != null && requestDatalist.size() > 0) {
            befroeDatalist.addAll(requestDatalist);
            if (isFirstLoadData) {
                adapter.setList(befroeDatalist);
                isFirstLoadData = false;
            }
            adapter.notifyDataSetChanged();
            MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    PageMultiUnitsReportManager.getInstance().buildPageUnits(saveTemp, mPullToRefreshListView, Constant.PAGE_SUBJECT_LIST, "", "", "", "");
                }
            }, 500);  //才能取到可见的position
            tv_beforeTopic.setVisibility(View.VISIBLE);
        } else {
            tv_beforeTopic.setVisibility(View.GONE);
        }

    }


    private void processBannerData() {

        screenWidth = Utils.getScreenWidth(this);

        UIModule module = (UIModule) bannerData.getmData();
        FunctionEssence data = (FunctionEssenceImpl) module.getData();


		/* Modify by ztl 2015.04.17 */
        int imageViewHeight = (((screenWidth - DensityUtil.dip2px(this, 10)) * 140) / 420);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, imageViewHeight);
        iv_headImageView.setLayoutParams(lp);
        ImageloaderUtil.loadBigImage(this, data.getImageUrl().get(ImageType.COMMON), iv_headImageView);
        StatisticsEventData sData = StatisticsDataProductorImpl.produceStatisticsData(bannerData.getmPresentData(), data.getUniqueIdentifier(), mClickListener.getmPageName(), ReportEvent.ACTION_CLICK, null, null, null);
        setViewTagForClick(iv_headImageView, data, data.getDomainEssence().getDomainType(), PresentType.topic, sData);
        tv_xbym.setText(data.getTitle());
    }

    /**
     * 给view绑定点击事件；
     *
     * @param data  通过tag携带的具体数据；
     * @param dType 通过tag携带的资源类型（如跳转ht、游戏、礼包...）；
     * @param pType 通过tag携带的数据展现类型；
     * @param sData 通过tag携带的具体统计数据；
     */
    protected void setViewTagForClick(View view, Object data, DomainType dType, PresentType pType, StatisticsEventData sData) {
        //添加点击时需要的各类数据； 1、实体对象；2、统计对象；3、PresentType;4、资源类型；
        view.setTag(R.id.view_data, data);
        view.setTag(R.id.src_type, dType);
        view.setTag(R.id.statistics_data, sData);
        view.setTag(R.id.present_type, pType);
        view.setOnClickListener(mClickListener);
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_SUBJECT_LIST);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        if (page < totalPageSize) {
            page++;
            getNetWorkData();
        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPullToRefreshListView.onLoadMoreComplete();
                }
            }, 500);
        }
    }

    @Override
    public void onScrollChangeListener(int scrollState) {
        reportWhenScroll(scrollState, Constant.PAGE_SUBJECT_LIST, saveTemp, mPullToRefreshListView, "", "", "", "");
    }
}
