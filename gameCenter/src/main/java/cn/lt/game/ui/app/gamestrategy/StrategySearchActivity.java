package cn.lt.game.ui.app.gamestrategy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huanju.data.content.raw.info.HjInfoItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.db.operation.FavoriteDbOperator;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.ImageViewText;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.view.SearchView;
import cn.lt.game.lib.view.SearchView.isTopActivityCallBack;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.view.TitleMoreButton;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsDataProductorImpl;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.parser.NetDataAddShell;
import cn.lt.game.ui.app.gamestrategy.BatchInformation.BachListCallBack;

/**
 * 攻略搜索
 *
 * @author Administrator
 */
public class StrategySearchActivity extends BaseActivity implements OnClickListener, BachListCallBack, isTopActivityCallBack, OnScrollListener, RetryCallBack, OnItemClickListener {
    private String searchKey;
    private TitleBarView titleBar;
    private SearchView searchView;
    private NetWorkStateView netWorkStateView;
    private List<ItemData<? extends BaseUIModule>> mGames;
    private LinearLayout root, relevantRoot;
    private ListView listView;
    private StrategySearchAdapter adapter;
    private ArrayList<HjInfoItem> list = new ArrayList<HjInfoItem>();
    private int headHeight = 0;
    private int headBottom = 0;
    private RelativeLayout noDataLayout;
    private GameBaseDetail game = new GameBaseDetail();
    private int mlastPagePositionOfNetData = 0;
    private View mNoneSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_data);
        Intent intent = getIntent();
        searchKey = intent.getStringExtra("searchData");
        init();
        checkNetWork(searchKey);
    }


    /**
     * 获取网络请求数据；
     */
    private void requestData() {
        Map<String, String> parames = new HashMap<String, String>();
        parames.put("q", searchKey);
        parames.put("page", 0 + "");
        netWorkStateView.showLoadingBar();
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.SEARCH_URI, parames, new WebCallBackToObject<UIModuleList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                netWorkStateView.showNetworkFailLayout();
                LogUtils.i("jjj", "攻虐搜索请求失败==>" );
            }

            @Override
            protected void handle(UIModuleList info) {
                LogUtils.i("jjj", "攻虐搜索请求成功==>" );
                netWorkStateView.hideLoadingBar();
                UIModuleList list = info;
                List<ItemData<? extends BaseUIModule>> temp = NetDataAddShell.wrapModuleList(list, mlastPagePositionOfNetData);
                setMlastPagePositionOfNetData(temp);
                if (mGames == null) {
                    mGames = new ArrayList<>();
                }
                mGames.clear();
                mGames.addAll(temp);
                initViewState();
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

    protected void initViewState() {

        if (mGames.size() == 0) {
            netWorkStateView.hideNetworkView();
            noDataLayout.setVisibility(View.VISIBLE);
            root.removeAllViews();
            this.list.clear();
            adapter.notifyDataSetChanged();
        } else {
            noDataLayout.setVisibility(View.GONE);
            addImageTextView(root, mGames.size());
            ArrayList<String> packageList = new ArrayList<String>();
            // 初始化搜索列表关键字
            for (int i = 0; i < mGames.size(); i++) {
                try {
                    packageList.add(((GameDomainBaseDetail) ((UIModule) mGames.get(i).getmData()).getData()).getPkgName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            getSearchData(packageList);
        }
    }

    // 获取玩卡搜索结果
    private void getSearchData(ArrayList<String> packageList) {
        BatchInformation batchInformation = new BatchInformation(this, packageList);
        batchInformation.setBachListCallBack(this);
    }

    private void checkNetWork(String searchKey) {
        if (NetUtils.isConnected(this)) {
            requestData();
        } else {
            netWorkStateView.showNetworkFailLayout();
        }
    }

    private void init() {
        // TODO Auto-generated method stub
        titleBar = (TitleBarView) findViewById(R.id.seachData_titleBar);
        titleBar.setMoreButtonType(TitleMoreButton.MoreButtonType.BackHome);
        titleBar.setTitle("攻略搜索");
        searchView = (SearchView) findViewById(R.id.strategycenter_searchbar);
        if (!TextUtils.isEmpty(searchKey)) {
            searchView.getSearchEt().setText(searchKey);
        }
        searchView.setIsTopActivityCallBack(this);

        noDataLayout = (RelativeLayout) findViewById(R.id.search_data_nodata);

        netWorkStateView = (NetWorkStateView) findViewById(R.id.seachData_netwrokStateView);
        netWorkStateView.setNoDataCatSyle(NetWorkStateView.CatStyle.NO_DATA);
        netWorkStateView.setNoDataLayoutText("没有找到相关内容", "");
        netWorkStateView.setRetryCallBack(this);
        root = (LinearLayout) findViewById(R.id.seachData_searchbar_aboutgame_rootView);
        relevantRoot = (LinearLayout) findViewById(R.id.seachData_searchbar_aboutgame_layout);
        listView = (ListView) findViewById(R.id.seachData_listView);
        adapter = new StrategySearchAdapter(getApplicationContext(), list);
        listView.setOnScrollListener(this);
        listView.setOnItemClickListener(this);

        relevantRoot.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                headHeight = relevantRoot.getHeight();
                headBottom = relevantRoot.getBottom();
                if (headHeight != 0 && headHeight > 0) {
                    addListViewHead(relevantRoot.getWidth(), relevantRoot.getBottom());
                    listView.setAdapter(adapter);
                    relevantRoot.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    private void addListViewHead(int w, int h) {
        TextView headerView = new TextView(this);
        headerView.setMinimumWidth(w);
        headerView.setMinimumHeight(h);
        headerView.setClickable(false);
        headerView.setBackgroundColor(this.getResources().getColor(R.color.background_grey));
        listView.addHeaderView(headerView);
    }

    public int getScrollY() {
        if (headHeight == 0) {
            return 0;
        }
        View c = listView.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int top = c.getTop();
        if (c instanceof TextView) {
            return -top + firstVisiblePosition * headHeight;
        } else {
            return (-top + (firstVisiblePosition - 1) * c.getHeight()) + headHeight;
        }
    }

    private void addImageTextView(LinearLayout root, int size) {
        root.removeAllViews();
        int viewHeight = (int) this.getResources().getDimension(R.dimen.game_detail_imageText_height);
        int viewWidth = (int) this.getResources().getDimension(R.dimen.game_detail_imageText_width);
        float textSize = this.getResources().getDimension(R.dimen.font_size_15sp);
        int marginRight = (int) this.getResources().getDimension(R.dimen.game_detail_gift_itemRoot_paddingLeft);
        int marginBottom = (int) this.getResources().getDimension(R.dimen.outerIntervalBottom);
        for (int i = 0; i < size; i++) {
            try {
                final GameDomainBaseDetail sameGame = (GameDomainBaseDetail) ((UIModule) mGames.get(i).getmData()).getData();
                final int position = i;
                ImageViewText imgText = new ImageViewText(this, viewWidth, viewHeight, marginRight, marginBottom, textSize);
                imgText.setText(sameGame.getName());
                ImageloaderUtil.loadLTLogo(this, sameGame.getIconUrl(), imgText.getImageView());
                imgText.imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String id = sameGame.getUniqueIdentifier();
                            if (!TextUtils.isEmpty(sameGame.getUniqueIdentifier()) && !"null".equals(sameGame.getUniqueIdentifier())) {
                                ActivityActionUtils.JumpToGameDetail(v.getContext(), Integer.valueOf(id));
                            }
                            StatisticsEventData sData = StatisticsDataProductorImpl.produceStatisticsData(mGames.get(position).getmPresentData(), id, getPageAlias(), ReportEvent.ACTION_CLICK, Constant.RETRY_TYPE_MANUAL, null, null);
                            DCStat.clickEvent(sData);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                });
                root.addView(imgText);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 获取视图的高度
        root.requestLayout();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.not_serach_btn_back:
                finish();
                break;
            case R.id.strategycenter_searchbar_search_img:

                break;
            default:
                break;
        }

    }

    @Override
    public void BachListSuccess(ArrayList<HjInfoItem> list) {
        // TODO Auto-generated method stub
        netWorkStateView.hideNetworkView();
        if (list.size() == 0) {
            // ShowNoDataLayout();
            relevantRoot.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
            root.removeAllViews();
            this.list.clear();
            adapter.notifyDataSetChanged();
        } else {
            relevantRoot.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.GONE);
            this.list.clear();
            this.list.addAll(list);
            adapter.notifyDataSetChanged();

        }

    }


    @Override
    public void BachListFailed() {
        // TODO Auto-generated method stub

        netWorkStateView.showNetworkFailLayout();
        // 这里需要处理～～
    }

    @Override
    public void OnRefreshCurrentClass() {
        // TODO Auto-generated method stub
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        searchKey = searchView.getSearchEt().getText().toString().trim();
        if (!TextUtils.isEmpty(searchKey)) {
            checkNetWork(searchKey);
        } else {
            ToastUtils.showToast(this, "请输入关键字");
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
        int scrollY = getScrollY();
        relevantRoot.setTranslationY(Math.max(-scrollY, -headBottom));
    }

    @Override
    public void retry() {
        // TODO Auto-generated method stub
        checkNetWork(searchKey);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (position != 0) {
            HjInfoItem item = list.get(position - 1);
            game.setPkgName(item.getPackage_name());
            ActivityActionUtils.jumpToGameOtherDetail(this, FavoriteDbOperator.GameStrategyTag, item.getId(), item.getTitle(), game);
        }
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_STRATEGY_SEARCH);
    }

}
