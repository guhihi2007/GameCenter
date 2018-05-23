package cn.lt.game.ui.app.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.MyBaseFragment;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleGroup;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.statistics.pageunits.PageMultiUnitsReportManager;
import cn.lt.game.ui.app.adapter.LTBaseAdapter;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListenerImpl;
import cn.lt.game.ui.app.adapter.parser.NetDataAddShell;
import cn.lt.game.ui.app.search.AdvertisementFragment.TitlesListFragmentCallBack;

/***
 * 搜索结果页面
 *
 * @author tiantian
 */
public class SearchResultFragment extends MyBaseFragment implements SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener, RefreshAndLoadMoreListView.IOnScrollStateChanged {
    private String keyWord;
    private RefreshAndLoadMoreListView mPullToRefreshListView;
    private BaseOnclickListener mClickListener;
    private int mCurrPage = 1;
    private int mTotalPage;
    private LTBaseAdapter adapter;
    private TitlesListFragmentCallBack mTitlesListFragmentCallBack;
    private int mlastPagePositionOfNetData = 0;
    private List<ItemData<? extends BaseUIModule>> saveTemp = new ArrayList<>();

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_SEARCH_RESULT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mClickListener = new BaseOnclickListenerImpl(getActivity(), getPageAlias());
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        // 更新数据，触发注册按键的下载进度监听
        if (adapter != null && getUserVisibleHint()) {
            adapter.notifyDataSetChanged();
            PageMultiUnitsReportManager.getInstance().buildPageUnits(saveTemp, mPullToRefreshListView, Constant.PAGE_SEARCH_RESULT, "", "", keyWord, "");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof TitlesListFragmentCallBack)) {
            throw new IllegalStateException("TitlesListFragment所在的Activity必须实现TitlesListFragmentCallBack接口");
        }
        mTitlesListFragmentCallBack = (TitlesListFragmentCallBack) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTitlesListFragmentCallBack = null;
    }

    protected boolean isFirstPage() {
        return mCurrPage == 1;
    }


    public void initAction() {
        Bundle bundle = this.getArguments();
        keyWord = bundle.getString("keyWord");
        SharedPreferencesUtil sputils = new SharedPreferencesUtil(context);
        if (TextUtils.isEmpty(sputils.get("keyWord_GameName"))) {
            sputils.add("keyWord_GameName", keyWord);
        }
        SearchTitleBarView search_bar = (SearchTitleBarView) view.findViewById(R.id.search_bar);
        search_bar.setTextViewText(keyWord);
        mPullToRefreshListView = (RefreshAndLoadMoreListView) view.findViewById(R.id.pullToRefreshListView);
        mPullToRefreshListView.setmOnRefrshListener(this);
        mPullToRefreshListView.setOnLoadMoreListener(this);
        adapter = new LTBaseAdapter(context, mClickListener);
        mPullToRefreshListView.setAdapter(adapter, false);
        mPullToRefreshListView.setmOnRefrshListener(this);
        mPullToRefreshListView.setMyOnScrollListener(this);
        resetLayout();
        requestData();
        DCStat.searchEvent(keyWord);

    }


    public int getContentViewLayoutId() {
        return R.layout.activity_serachresult;
    }

    @Override
    public void retry() {
        LogUtils.i("zzz", "搜索结果页面重试");
        initAction();
    }

    private void setPage(int loadingPage) {
        if (loadingPage == 1 && adapter != null) {
            adapter.resetList();
        }
        if (mCurrPage < mTotalPage) {
            mCurrPage = loadingPage + 1;
        } else if (mCurrPage >= mTotalPage) {
        }
    }

    /**
     * 获取网络请求数据；
     */
    private void requestData() {
        mPullToRefreshListView.setVisibility(View.VISIBLE);
        final Map<String, String> params = new HashMap<String, String>();
        params.put("q", keyWord);
        if (mCurrPage > 1) {
            params.put("page", String.valueOf(mCurrPage));
        }
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.SEARCH_URI, params, new WebCallBackToObject<UIModuleList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                mPullToRefreshListView.onLoadingFailed();
                mPullToRefreshListView.setVisibility(View.GONE);
                netWorkStateView.showNetworkFailLayout();
            }

            @Override
            protected void handle(UIModuleList list) {
                netWorkStateView.hideNetworkFailLayout();
                mTotalPage = getLastPage() == -1 ? mTotalPage : getLastPage();
                LogUtils.i("SearchResultFragment", "mCurrPage" + mCurrPage + ",mTotalPage" + mTotalPage);
                mPullToRefreshListView.setCanLoadMore(mCurrPage < mTotalPage);
                mPullToRefreshListView.onLoadMoreComplete();
                if (params.get("page") != null) {
                    setPage(Integer.valueOf(params.get("page")));
                } else {
                    setPage(1);
                }
                if(list == null || list.size() == 0) {
                    return;
                }

                List<ItemData<? extends BaseUIModule>> temp = null;

                // 搜索结果为空时，需要对数据拆解后重新封装下，不然数据上报资源位置会有问题
                if (list.get(0).getUIType() == PresentType.search_null) {
                    List<GameDomainBaseDetail> requestGameList = ((UIModuleGroup) list.get(0)).getData();

                    List<BaseUIModule> gameDatas = new ArrayList<>();

                    BaseUIModule empty = new UIModule<>(PresentType.search_null_head, null);
                    gameDatas.add(empty);

                    for (GameDomainBaseDetail data : requestGameList) {
                        BaseUIModule module1 = new UIModule<>(PresentType.game, data);
                        gameDatas.add(module1);
                    }
                    temp = NetDataAddShell.wrapModuleList(gameDatas, mlastPagePositionOfNetData);

                } else if (list.size() >= 2 && list.get(1).getUIType() == PresentType.search_null) {
                    List<GameDomainBaseDetail> requestGameList = ((UIModuleGroup) list.get(1)).getData();

                    List<BaseUIModule> gameDatas = new ArrayList<>();

                    gameDatas.add(list.get(0));

                    BaseUIModule empty = new UIModule<>(PresentType.search_null_head, null);
                    gameDatas.add(empty);

                    for (GameDomainBaseDetail data : requestGameList) {
                        BaseUIModule module1 = new UIModule<>(PresentType.game, data);
                        gameDatas.add(module1);
                    }
                    temp = NetDataAddShell.wrapModuleList(gameDatas, mlastPagePositionOfNetData);

                } else {
                    temp = NetDataAddShell.wrapModuleList(list, mlastPagePositionOfNetData);
                }


                setMlastPagePositionOfNetData(temp);
                if (temp != null && temp.size() > 0) {
                    if (list.get(0).getUIType() == PresentType.search_null) {
                        LogUtils.i("kkk", "mCurrPage=========" + mCurrPage);
                        if (mCurrPage > 1) {
                            LogUtils.i("kkk", "mCurrPage=========3333333333");
                            return;
                        }
                        LogUtils.i("kkk", "mCurrPage=========1111");
                        mTitlesListFragmentCallBack.saveNoDataList(temp);
                        mTitlesListFragmentCallBack.gotoNoDataFragment();
                    } else {
                        saveTemp.addAll(temp);
                        netWorkStateView.setVisibility(View.GONE);
                        adapter.addList(temp);
                        MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PageMultiUnitsReportManager.getInstance().buildPageUnits(saveTemp, mPullToRefreshListView, Constant.PAGE_SEARCH_RESULT, "", "", keyWord, "");
                            }
                        }, 500);  //才能取到可见的position

                    }
                }
            }
        });
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

    private void resetLayout() {
        mCurrPage = 1;
        netWorkStateView.showLoadingBar();
    }

    @Override
    public void onRefresh() {
        LogUtils.i("Erosion", "fefhewuiegheghe");
        mCurrPage = 1;
        requestData();
    }

    @Override
    public void onLoadMore() {
        requestData();
    }

    @Override
    public void onScrollChangeListener(int scrollState) {
        reportWhenScroll(scrollState, Constant.PAGE_SEARCH_RESULT, saveTemp, mPullToRefreshListView);
    }
}
