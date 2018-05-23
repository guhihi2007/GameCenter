package cn.lt.game.ui.app.search;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.baidu.mobstat.StatService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.MyBaseFragment;
import cn.lt.game.db.service.SearchService;
import cn.lt.game.domain.UIModuleGroup;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsDataProductorImpl;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.statistics.pageunits.PageMultiUnitsReportManager;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.jump.IJumper;
import cn.lt.game.ui.app.jump.JumpFactory;
import cn.lt.game.ui.app.search.AdvertisementFragment.TitlesListFragmentCallBack;
import de.greenrobot.event.EventBus;

/***
 * 搜索自定匹配页面
 *
 * @author tiantian
 * @des
 */
public class SearchAutoMatchFragment extends MyBaseFragment implements OnItemClickListener {
    public static boolean isLoaded = false;
    private String keyword = "";
    private ListView lv;
    private SearchMatchResultAdapter adapter;
    private TitlesListFragmentCallBack mTitlesListFragmentCallBack;

    public int getContentViewLayoutId() {
        return R.layout.searchresult_match_fragment;
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_SEARCH_AUTO_MATCH);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        StatService.onResume(this);
        super.onResume();
        // 更新数据，触发注册按键的下载进度监听
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            PageMultiUnitsReportManager.buildPageUnitsForAutoMatchSearch(wrapAutoMatchPos(adapter.getmList()), lv, Constant.PAGE_SEARCH_AUTO_MATCH, keyword);
        }

    }

    public void onPause() {
        super.onPause();
    }

    @Override
    public void initAction() {
        netWorkStateView.showLoadingBar();
        lv = (ListView) view.findViewById(R.id.searchresult_match_listview);
        adapter = new SearchMatchResultAdapter(context, getPageAlias());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                LogUtils.d("eee", "搜索匹配页=onScrollStateChanged" );
                reportWhenScrollForAutoMatch(scrollState);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    public List<Object>  wrapAutoMatchPos(List<Object> tempList) {
        for (int i = 0; i < tempList.size(); i++) {
            LogUtils.i("pppp", "autoMatchEvent 截取后分发数据==>" + tempList.size());
            if (tempList.get(i) instanceof FunctionEssence) {
                ((FunctionEssence) tempList.get(i)).autoMatchPos=i;
            } else if (tempList.get(i) instanceof GameDomainBaseDetail) {
                ((GameDomainBaseDetail) tempList.get(i)).autoMatchPos=i;
            } else {
            }
        }
        return tempList;

    }
    /**
     * 滚动停止时逻辑--自动匹配的独特性
     * @param scrollState
     */
    public void reportWhenScrollForAutoMatch(int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                PageMultiUnitsReportManager.buildPageUnitsForAutoMatchSearch(wrapAutoMatchPos(adapter.getmList()), lv, Constant.PAGE_SEARCH_AUTO_MATCH, keyword);
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                break;
            default:
                break;
        }
    }

    // 获取本地缓存的搜索记录
    public List<String> getLocalData(String keyword) {
        netWorkStateView.showLoadingBar();
        List<String> list = SearchService.getInstance(context).findAll(keyword);
        Collections.reverse(list);// 逆序排序
        isLoaded = false;
        return list;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object o = view.getTag(R.id.search_item_click_type);
        Object data = null;
        StatisticsEventData sData=null;
        if ("accurate".equals(o)) {
            data = view.getTag(R.id.search_item_click_data);
            GameDomainBaseDetail game = (GameDomainBaseDetail) data;
            IJumper jumper = JumpFactory.produceJumper(PresentType.query_ads, game.getDomainType());
            jumper.jump(game, getActivity());
            SearchService.getInstance(context).add(game.getName());
            sData = StatisticsDataProductorImpl.produceStatisticsData(
                    PresentType.query_ads.presentType, position + 1, 1, game.getUniqueIdentifier(),
                    getPageAlias(), ReportEvent.ACTION_CLICK, Constant.RETRY_TYPE_MANUAL, null, Constant.SRCTYPE_SEARCHACCU,keyword);
        } else if ("fuzzy".equals(o)) {
            data = view.getTag(R.id.search_item_click_data);
            FunctionEssence game = (FunctionEssence) data;
            LogUtils.d("zzz", "模糊点击了要上报了" );
            sData = StatisticsDataProductorImpl.produceStatisticsData(
                    null, position + 1, 1, "",
                    getPageAlias(), ReportEvent.ACTION_CLICK, Constant.RETRY_TYPE_MANUAL, null, Constant.SRCTYPE_SEARCHFUZ,keyword);
            mTitlesListFragmentCallBack.hotWordOnclick(game.getTitle());
            SearchService.getInstance(context).add(game.getTitle());
        } else if ("history".equals(o)) {
            data = view.getTag(R.id.search_item_click_data);
            String keyword_his = (String) data;
            LogUtils.d("zzz", "搜索历史点击了要上报了" +keyword_his);
            sData = StatisticsDataProductorImpl.produceStatisticsData(
                    null, position + 1, 1, null,
                    getPageAlias(), ReportEvent.ACTION_CLICK, Constant.RETRY_TYPE_MANUAL, null, Constant.SRCTYPE_SEARCHHIS,keyword_his);
            mTitlesListFragmentCallBack.hotWordOnclick(keyword_his);
        }
        DCStat.clickEvent(sData);
    }

    /**
     * 获取网络请求数据；
     */
    public void requestData(final String keyword) {
        this.keyword = keyword;
        Map<String, String> parames = new HashMap<String, String>();
        parames.put("q", keyword);
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.SEARCH_AUTOCOMPLETE_URI, parames, new WebCallBackToObject<UIModuleList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                netWorkStateView.hideLoadingBar();
                isLoaded = false;
            }

            @Override
            protected void handle(UIModuleList info) {
                UIModuleList list = info;
                adapter.clearList();
                LogUtils.i("zzz", "请求搜索成功");
                final List<Object> tempList = new ArrayList<Object>();
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        if (PresentType.query_ads == list.get(i).getUIType())
                            for (int j = 0; j < ((UIModuleGroup) list.get(i)).getData().size(); j++) {
                                GameDomainBaseDetail gameDomainBaseDetail = (GameDomainBaseDetail) ((UIModuleGroup) list.get(i)).getData().get(j);
                                tempList.add(gameDomainBaseDetail);
                            }
                    }
                }
                List<String> temp = getLocalData(keyword);
                if (temp != null && temp.size() > 0) {
                    tempList.addAll(temp);
                }
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        if (PresentType.query_data == list.get(i).getUIType())
                            tempList.addAll(((UIModuleGroup) list.get(i)).getData());
                    }
                }
                lv.setVisibility(View.VISIBLE);
                adapter.addData(tempList);
                MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.i("pppp", "搜索匹配页构建数据 ==>" + adapter.getmList().size());
                        PageMultiUnitsReportManager.buildPageUnitsForAutoMatchSearch(wrapAutoMatchPos(adapter.getmList()), lv, Constant.PAGE_SEARCH_AUTO_MATCH,
                                keyword);
                    }
                }, 500);  //才能取到可见的position
                netWorkStateView.hideNetworkView();
                isLoaded = false;
            }
        });
    }



    private void updateView(int position) {
        int visiblePosition = lv.getFirstVisiblePosition();
        if (position - visiblePosition >= 0) {
            View view = lv.getChildAt(position - visiblePosition);
            adapter.updateView(view, position);
        }
    }
    public void onEventMainThread(DownloadUpdateEvent updateEvent) {
        if (updateEvent == null || updateEvent.game == null || adapter.getmList() == null) return;
        for (int i = 0; i < adapter.getmList().size(); i++) {
            Object o = adapter.getmList().get(i);
            if (o instanceof GameDomainBaseDetail) {
                updateView(i);
            }
        }
    }
}
