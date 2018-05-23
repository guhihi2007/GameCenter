package cn.lt.game.ui.app.category;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
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
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.statistics.pageunits.PageMultiUnitsReportManager;
import cn.lt.game.ui.app.adapter.LTBaseAdapter;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.data.PresentData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListenerImpl;
import cn.lt.game.ui.app.adapter.parser.NetDataAddShell;
import cn.lt.game.ui.app.requisite.widget.AutoGridView;

/**
 * 分类详情
 * Created by Administrator on 2015/1/28. 分类列表的界面 界面基本上专题的主界面一样所以直接使用专题详情的布局 123
 */
public class CategoryItemResultActivity extends BaseActivity implements RefreshAndLoadMoreListView.OnLoadMoreListener, RefreshAndLoadMoreListView.IOnScrollStateChanged {
    private static final int LABEL_ALL = 6;

    private RefreshAndLoadMoreListView mPullToRefreshListView;
    private View headView;

    private AutoGridView mGridView;
    /**
     * 网络相关布局视图
     */
    private NetWorkStateView netWorkStateView;

    private int mCurrentPage = 1;

    /**
     * 大类id
     */
    private String mCategoryId;
    private String mCategoryTitle;
    private String mTagId;
    private ArrayList<String> mIdList;
    private ArrayList<String> mTitleList;

    private String mSort = "";
    private LTBaseAdapter mAdapter;
    private SparseArray<List<ItemData<? extends BaseUIModule>>> saveDatas = new SparseArray<>();
    /**
     * 当前选中的label
     */
    private int mCurrentLabel = LABEL_ALL;
    private int mlastPagePositionOfNetData = 0;
    private String mCurrentTagId = "";
    private static String mCurrentTagLableTitle = "";
    private boolean isBigCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_categoryhottags);
        initIntentData();
        initView();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 更新数据，触发注册按键的下载进度监听
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        try {
            PageMultiUnitsReportManager.getInstance().buildPageUnits(saveDatas.get(Integer.parseInt(mCurrentTagId)), mPullToRefreshListView, Constant.PAGE_CATEGORY_LIST, mCurrentTagLableTitle, mCurrentTagId, "", mCategoryTitle);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    void initIntentData() {
        isBigCategory = getIntent().getBooleanExtra("is_big_category", true);
        mCategoryId = getIntent().getStringExtra("category_id");
        mCategoryTitle = getIntent().getStringExtra("category_title");
        mIdList = getIntent().getStringArrayListExtra("id_list");
        mTitleList = getIntent().getStringArrayListExtra("title_list");
        try {
            if (isBigCategory) {
                //显示全部
                mTagId = "";
                mCurrentTagId = mCategoryId;
                mCurrentTagLableTitle = "全部";
            } else {
                //根据标签显示
                mTagId = getIntent().getStringExtra("click_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        mIdList.add(0, "");
        mTitleList.add(0, "全部");
    }


    /**
     * 连接
     */
    protected void loadData() {
        if (NetUtils.isConnected(this)) {
            initAction();
        } else {
            netWorkStateView.showNetworkFailLayout();
            ToastUtils.showToast(getApplicationContext(), "网络连接失败");
            TextView tryAgain = (TextView) findViewById(R.id.network_fail_tryAgain);
            tryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    netWorkStateView.showLoadingBar();
                    new Handler() {
                        public void handleMessage(android.os.Message msg) {
                            loadData();
                        }
                    }.sendEmptyMessageDelayed(0, 1000);
                }
            });
        }
    }

    void getData(int currentLabel, final int page, final String tagId, final String sort) {
        if(page==1){
            mlastPagePositionOfNetData=0;
        }
        mCurrentTagId = tagId.equals("") ? mCategoryId : tagId;
        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        params.put("tag_id", tagId);
        params.put("sort", sort);
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.getCatsListUri(mCategoryId), params, new WebCallBackToObject<UIModuleList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
            }

            @Override
            protected void handle(UIModuleList info) {
                mPullToRefreshListView.setCanLoadMore(page < getLastPage());
                mPullToRefreshListView.onLoadMoreComplete();
                netWorkStateView.hideNetworkView();
                final List<ItemData<? extends BaseUIModule>> moduleList = NetDataAddShell.wrapModuleList(info, mlastPagePositionOfNetData);
                if(null!=moduleList){
                    mlastPagePositionOfNetData+=moduleList.size();
                }
                List<ItemData<? extends BaseUIModule>> list = new ArrayList<ItemData<? extends BaseUIModule>>();
                for (ItemData itemData : moduleList) {
                    list.add(itemData);
                }
                saveNetDataToTemp(list, mCurrentTagId);
                if (mCurrentPage == 1) {
                    mAdapter.setList(moduleList);
                } else {
                    mAdapter.addList(moduleList);
                }
                try {
                    MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PageMultiUnitsReportManager.getInstance().buildPageUnits(saveDatas.get(Integer.parseInt(mCurrentTagId)), mPullToRefreshListView, Constant.PAGE_CATEGORY_LIST, mCurrentTagLableTitle, mCurrentTagId, "", mCategoryTitle);
                        }
                    }, 500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveNetDataToTemp(List<ItemData<? extends BaseUIModule>> moduleList, String tagId) {
        try {
            int id = Integer.parseInt(tagId);
            List<ItemData<? extends BaseUIModule>> itemDatas = saveDatas.get(id);
            if (itemDatas == null) {
                itemDatas = moduleList;
                saveDatas.put(id, itemDatas);
            } else {
                itemDatas.addAll(moduleList);
                saveDatas.delete(id);
                saveDatas.put(id, itemDatas);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void initAction() {
        mSort = "all";
        mCurrentPage = 1;
        mCurrentLabel = LABEL_ALL;
        netWorkStateView.showLoadingBar();
        getData(mCurrentLabel, mCurrentPage, mTagId, mSort);
    }

    private void initListView() {
        mAdapter = new LTBaseAdapter(this, new BaseOnclickListenerImpl(this, getPageAlias()));
        mPullToRefreshListView.setAdapter(mAdapter, false);
        mPullToRefreshListView.setRefreshEnabled(false);
        mPullToRefreshListView.setOnLoadMoreListener(this);
        mPullToRefreshListView.setMyOnScrollListener(this);
    }


    protected void initView() {
        mPullToRefreshListView = (RefreshAndLoadMoreListView) findViewById(R.id.pullToRefreshListView);
        initListView();
        TitleBarView search_bar = (TitleBarView) findViewById(R.id.search_bar);
        search_bar.setTitle(mCategoryTitle);
        search_bar.setMoreButtonType(TitleMoreButton.MoreButtonType.Special);
        netWorkStateView = (NetWorkStateView) findViewById(R.id.game_detail_netWrokStateView);
        netWorkStateView.showLoadingBar();
        headView = View.inflate(this, R.layout.item_category_detail_head_v2, null); //

        int initialPosition = 0;
        if (!mTagId.isEmpty()) {
            for (int i = 0; i < mIdList.size(); i++) {
                if (mIdList.get(i).equals(mTagId)) {
                    initialPosition = i;
                    break;
                }
            }
        }
        mGridView = (AutoGridView) headView.findViewById(R.id.cats_gridView);
        final GridAdapter mGridAdapter = new GridAdapter(this, mTitleList, initialPosition);
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mGridAdapter.setSelectedAtPosition(position);
                mGridAdapter.notifyDataSetChanged();

                //tag点击事件
                mAdapter.resetList();
                mCurrentPage = 1;
                if (position != 0) {
                    mCurrentLabel = position;
                } else {
                    mCurrentLabel = LABEL_ALL;
                }
                mTagId = mIdList.get(position);
                mCurrentTagLableTitle = mTitleList.get(position);
                mSort = "all";
                getData(mCurrentLabel, mCurrentPage, mTagId, mSort);
                PresentData presentData = new PresentData();
                presentData.setPos(1);
                presentData.setSubPos(position + 1);
                presentData.setmType(PresentType.label);
                StatisticsEventData sData = StatisticsDataProductorImpl.produceStatisticsData(presentData, mTagId, getPageAlias(), ReportEvent.ACTION_CLICK, "", mTitleList.get(position), null);
                DCStat.clickEvent(sData);
            }
        });


        mPullToRefreshListView.getmListView().addHeaderView(headView, null, false);
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_CATEGORY_LIST, mCategoryId);

    }


    @Override
    public void onLoadMore() {
        getData(mCurrentLabel, ++mCurrentPage, mTagId, mSort);
        LogUtils.i("Erosion", "mCurrentPage" + mCurrentPage);
    }

    @Override
    public void onScrollChangeListener(int scrollState) {
        try {
            reportWhenScroll(scrollState, Constant.PAGE_CATEGORY_LIST, saveDatas.get(Integer.parseInt(mCurrentTagId)), mPullToRefreshListView, getIntent().getStringExtra("category_title"), mCategoryId, mCurrentTagId, TextUtils.isEmpty(mCurrentTagLableTitle) ? "全部" : mCurrentTagLableTitle);//tagid/lable
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    private static class GridAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> titles;
        private int selectedPosition;

        public GridAdapter(Context context, List<String> titles, int initialPosition) {
            this.mContext = context;
            this.titles = titles;
            this.selectedPosition = initialPosition;
        }

        @Override
        public int getCount() {
            return titles.size();
        }

        @Override
        public Object getItem(int position) {
            return titles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CateGoryHolder holder;
            if (convertView == null) {
                holder = new CategoryItemResultActivity.CateGoryHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_category_result, parent, false);

                holder.title = (TextView) convertView.findViewById(R.id.category_title);
                holder.divider = convertView.findViewById(R.id.category_divider);

                convertView.setTag(holder);
            } else {
                holder = (CategoryItemResultActivity.CateGoryHolder) convertView.getTag();
            }

            holder.divider.setVisibility(position % 5 == 0 ? View.INVISIBLE : View.VISIBLE);
            holder.title.setText(titles.get(position));
            holder.title.setSelected(selectedPosition == position);
            if (selectedPosition == position) {
                mCurrentTagLableTitle = titles.get(position);
            }
            return convertView;
        }

        public void setSelectedAtPosition(int position) {
            selectedPosition = position;
        }

    }

    private static class CateGoryHolder {
        View divider;
        TextView title;
    }
}
