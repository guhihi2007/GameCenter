package cn.lt.game.ui.app.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.MyBaseFragment;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.statistics.pageunits.PageMultiUnitsReportManager;
import cn.lt.game.ui.app.adapter.LTBaseAdapter;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListenerImpl;

/***
 * 搜索无结果页面
 *
 * @author ltbl
 */
public class SearchNoDataFragment extends MyBaseFragment {
    private List<ItemData<? extends BaseUIModule>> mNoDataLists;
    private BaseOnclickListener mClickListener;
    private ListView mListView;
    private LTBaseAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mClickListener = new BaseOnclickListenerImpl(getActivity(), getPageAlias());
        return view;
    }

    public void initAction() {
        SearchActivity activity = (SearchActivity) getActivity();
        List<ItemData<? extends BaseUIModule>> temp = activity.getmNoDataLists();
        if (temp != null) {
            mNoDataLists = new ArrayList<>();
            mNoDataLists.addAll(temp);
            activity.setmNoDataLists(null);
        }
//        // 去掉小键盘；
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_HIDDEN);

        if (mNoDataLists != null && mNoDataLists.size() > 0) {

//            View head_detailsView = View.inflate(getActivity(), R.layout.search_no_data_headview,null);


            mListView = (ListView) view.findViewById(R.id.lv_searchadv);
            adapter = new LTBaseAdapter(this.getActivity(), mClickListener);
//            mListView.addHeaderView(head_detailsView);
            mListView.setAdapter(adapter);
            adapter.setList(mNoDataLists);
            MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    PageMultiUnitsReportManager.getInstance().buildPageUnits(
                            mNoDataLists,mListView,Constant.PAGE_SEARCH_FAILED, "", "", "", "");
                }
            },500);  //才能取到可见的position
            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    reportWhenScroll(scrollState,Constant.PAGE_SEARCH_FAILED,mNoDataLists,mListView);
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        try {
            LogUtils.i("juice", "无搜索页面 onResume");
            PageMultiUnitsReportManager.getInstance().buildPageUnits(mNoDataLists,mListView,Constant.PAGE_SEARCH_FAILED, "", "", "", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_SEARCH_FAILED);
    }

    public int getContentViewLayoutId() {
        return R.layout.activity_serachnoresult;
    }

    @Override
    public void retry() {
        LogUtils.i("zzz", "无搜索页面重试");
    }

}
