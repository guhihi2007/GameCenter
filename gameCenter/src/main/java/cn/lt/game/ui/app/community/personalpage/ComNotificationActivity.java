package cn.lt.game.ui.app.community.personalpage;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.html.HtmlUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.view.TitleMoreButton;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.model.Notice;
import cn.lt.game.ui.app.community.model.Notices;
import cn.lt.game.ui.common.quickadpter.BaseAdapterHelper;
import cn.lt.game.ui.common.quickadpter.QuickAdapter;

/**
 * 社区通知列表
 *
 * @author tiantian at 2015/11/10
 */
public class ComNotificationActivity extends BaseActivity implements NetWorkStateView.RetryCallBack, SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener {
    private RefreshAndLoadMoreListView listView;
    private NetWorkStateView netWorkStateView;
    private TitleBarView titleBar;
    private QuickAdapter<Notice> adapter;
    private int currentPage = 1;// 当前页码
    private int totalPage;// 总页数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_concern);
        initView();
        requestData(currentPage);
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_COMMUNTIY_NOTICE);
    }

    private void initView() {
        netWorkStateView = (NetWorkStateView) findViewById(R.id.member_netwrolStateView);
        netWorkStateView.setRetryCallBack(this);
        netWorkStateView.showLoadingBar();
        listView = (RefreshAndLoadMoreListView) findViewById(R.id.member_listView);
        listView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.margin_size_8dp));
        listView.setOnLoadMoreListener(this);
        listView.setmOnRefrshListener(this);
        titleBar = (TitleBarView) findViewById(R.id.group_member_bar);
        titleBar.setMoreButtonType(TitleMoreButton.MoreButtonType.BackHome);
        titleBar.setTitle("社区通知");
        titleBar.setBackHomeVisibility(0);
    }

    public void requestData(final int page) {
        Map<String, String> parames = new HashMap<String, String>();
        parames.put("page", page + "");
        Net.instance().executeGet(Host.HostType.FORUM_HOST, Uri.getCommunityNoticeUri(), parames, new WebCallBackToObj<Notices>() {
            @Override
            protected void handle(Notices info) {
                if (info != null) {
                    totalPage = info.total_page;
                    processData(info);
                    listView.onLoadMoreComplete();
                    int nextPage = page + 1;
                    if (nextPage > totalPage) {
                        listView.setCanLoadMore(false);
                    } else {
                        listView.setCanLoadMore(true);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                netWorkStateView.showNetworkFailLayout();
                listView.onLoadingFailed();
                switch (statusCode) {
                    case -2:
                        ToastUtils.showToast(ComNotificationActivity.this, "数据异常");
                        netWorkStateView.showNetworkFailLayout();
                        break;
                    case -3:
                        ToastUtils.showToast(ComNotificationActivity.this, "返回数据为空");
                        netWorkStateView.showNetworkNoDataLayout();
                        break;
                }
            }
        });
    }

    private List<Notice> tempList;

    private void processData(Notices info) {
        if (info.data.size() <= 0) {
            netWorkStateView.setNotDataState(NetWorkStateView.JoinGroup);
            netWorkStateView.setNoDataCatSyle(NetWorkStateView.CatStyle.SMILE);
            netWorkStateView.setNoDataLayoutText("暂时还没有通知", "");
            netWorkStateView.showNetworkNoDataLayout();
        } else {
            netWorkStateView.hideNetworkNoDataLayout();
            netWorkStateView.hideNetworkView();
            tempList = new ArrayList<Notice>();
            tempList.addAll(info.data);
            if (isFirstPage()) {
                setAdapter();
            } else {
                adapter.addAll(tempList);
            }
        }
    }

    private void setAdapter() {
        adapter = new QuickAdapter<Notice>(this, R.layout.com_notification_item, tempList) {
            @Override
            protected void convert(BaseAdapterHelper helper, Notice item) {
                TextView html = helper.getView(R.id.tv_content);
                String str = item.getContent();
                HtmlUtils.supportHtmlWithNet(html, str, false);
                helper.setText(R.id.tv_time, TimeUtils.curtimeDifference(item.getCreated_at()));
            }
        };
        listView.setAdapter(adapter, false);
    }

    protected boolean isFirstPage() {
        return currentPage == 1;
    }

    public static String Convert(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375) c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    @Override
    public void retry() {
//        listView.setRefreshing(true);
        requestData(currentPage);
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        currentPage = 1;
        requestData(currentPage);
    }

    /**
     * Called when the list reaches the last item (the last item is visible
     * to the user)
     */
    @Override
    public void onLoadMore() {
        requestData(++currentPage);
    }
}
