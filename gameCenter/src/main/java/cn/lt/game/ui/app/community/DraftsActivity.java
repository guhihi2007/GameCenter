package cn.lt.game.ui.app.community;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.db.service.DraftService;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.view.TitleMoreButton;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.ui.app.community.model.ClearEvent;
import cn.lt.game.ui.app.community.model.DraftBean;
import cn.lt.game.ui.app.community.model.DraftsDeletEvent;
import cn.lt.game.ui.app.community.model.DraftsDeleteByEditeEvent;
import cn.lt.game.ui.app.community.model.DraftsEvent;
import de.greenrobot.event.EventBus;



/***
 * 草稿箱主页面
 *
 * @author ltbl
 */
public class DraftsActivity extends BaseActivity implements RefreshAndLoadMoreListView.OnLoadMoreListener  {
    private TitleBarView drafts_title_bar;
    private RefreshAndLoadMoreListView drafts_listView;
    private DraftsAdapter drafts_listView_adapter;
    private int currentPage = 0;// 数据库默认第一页从0开始
    private NetWorkStateView netWorkView;
    private boolean isLoading = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drafts);
        EventBus.getDefault().register(this);
        initView();
        drafts_listView.setRefreshEnabled(false);
        drafts_listView.setOnLoadMoreListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    /***
     * 当草稿箱为空的时候展示空页面
     *
     * @param event
     */
    public void onEventMainThread(ClearEvent event) {
        if (event.isResult()) {
            showNoDataLayout();
        }
    }

    // EventBus的接收回调
    public void onEventMainThread(DraftsEvent event) {
        currentPage = 0;
        drafts_listView_adapter.setData(DraftService.getSingleton(this).findByPage(currentPage));
        if (drafts_listView_adapter.getCount() == 0) {
            showNoDataLayout();
        } else {
            showDataLayout();
        }
    }

    public void onEventMainThread(DraftsDeletEvent event) {
        drafts_listView_adapter.remove(event.getDb());
        if (drafts_listView_adapter.getCount() == 0) {
            showNoDataLayout();
        }
    }

    public void onEventMainThread(DraftsDeleteByEditeEvent event) {
        String tag = event.getTime();
        drafts_listView_adapter.removeByTag(tag);
        if (drafts_listView_adapter.getCount() == 0) {
            showNoDataLayout();
        }
    }

    /***
     * 如果草稿箱为空，展示空页面
     *
     * @param
     */
    public void initData() {
        currentPage = 0;
        List<DraftBean> dataList = DraftService.getSingleton(this).findByPage(currentPage);
        if (null != dataList && dataList.size() > 0) {
            netWorkView.hideLoadingBar();
            drafts_listView.setVisibility(View.VISIBLE);
            drafts_listView_adapter = new DraftsAdapter(this);
            drafts_listView_adapter.setData(dataList);
            drafts_listView.setAdapter(drafts_listView_adapter, false);
        } else {
            showNoDataLayout();
        }

        if(dataList.size() > 10){
            drafts_listView.setCanLoadMore(true);
        }
    }

    /***
     * 展示空页面
     */
    private void showNoDataLayout() {
        drafts_listView.setVisibility(View.GONE);
        netWorkView.showNetworkNoDataLayout();
    }

    private void showDataLayout() { // 如果有数据就加载有数据的页面，隐藏掉提示页面
        drafts_listView.setVisibility(View.VISIBLE);
        netWorkView.hideNetworkView();
    }

    private void initView() {
        drafts_title_bar = (TitleBarView) findViewById(R.id.drafts_title_bar);
        drafts_listView = (RefreshAndLoadMoreListView) findViewById(R.id.drafts_listView);
        drafts_listView.setVisibility(View.GONE);
        netWorkView = (NetWorkStateView) findViewById(R.id.draft_netWrokStateView);
        netWorkView.setNoDataCatSyle(NetWorkStateView.CatStyle.SMILE);
        netWorkView.setNoDataLayoutText("取消发送或发送失败的内容可以被存为草稿", null);
        netWorkView.showLoadingBar();
        drafts_title_bar.setTitle("草稿箱");

        drafts_title_bar.setMoreButtonType(TitleMoreButton.MoreButtonType.BackHome);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

//    @Override
//    // 上拉刷新
//    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//        if (isLoading) {
//            return;
//        }
//        isLoading = true;
//        currentPage = currentPage + 1;
//        ArrayList<DraftBean> al = (ArrayList<DraftBean>) DraftService.getSingleton(this)
//                .findByPage(currentPage);
//        if (al != null && al.size() > 0) {
//            drafts_listView_adapter.addData(DraftService.getSingleton(this).findByPage
//                    (currentPage));
//        } else {
//            currentPage = currentPage - 1;
//            ToastUtils.showToast(DraftsActivity.this, "无更多数据");
//        }
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                drafts_listView.onRefreshComplete();
//            }
//        }, 500);
//        isLoading = false;
//    }

//    @Override
//    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//        // TODO Auto-generated method stub
//
//    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_DRAFTS_MINE );

    }



    @Override
    public void onLoadMore() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        currentPage = currentPage + 1;
        ArrayList<DraftBean> al = (ArrayList<DraftBean>) DraftService.getSingleton(this)
                .findByPage(currentPage);
        if (al != null && al.size() > 0) {
            drafts_listView_adapter.addData(DraftService.getSingleton(this).findByPage
                    (currentPage));
        } else {
            currentPage = currentPage - 1;
//            ToastUtils.showToast(DraftsActivity.this, "无更多数据");

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                drafts_listView.setCanLoadMore(false);
                drafts_listView.setLabLoadMoreText("无更多数据");
                drafts_listView.onLoadMoreComplete();
            }
        }, 500);
        isLoading = false;
    }
}
