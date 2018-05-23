package cn.lt.game.ui.app.search;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.MyBaseFragment;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleGroup;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.view.ElasticScrollView.ElasticCallBack;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.pageunits.PageMultiUnitsReportManager;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.data.PresentData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListenerImpl;
import cn.lt.game.ui.app.adapter.parser.NetDataAddShell;

/***
 * 搜索推荐页面
 *
 * @author tiantian
 */
public class AdvertisementFragment extends MyBaseFragment implements ElasticCallBack {
    private ViewStub gameTitleView2;
    private LinearLayout mHotWordTitleViewRoot;
    private BaseOnclickListener mClickListener;
    private TitlesListFragmentCallBack mTitlesListFragmentCallBack;
    // 当该Fragment被添加,显示到Activity时调用该方法
    // 在此判断显示到的Activity是否已经实现了接口
    private int mlastPagePositionOfNetData = 0;
    private List<FunctionEssence> allDatas = new ArrayList<>();

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_SEARCH_RECOMMEND);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mClickListener = new BaseOnclickListenerImpl(getActivity(), getPageAlias());
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof TitlesListFragmentCallBack)) {
            throw new IllegalStateException("TitlesListFragment所在的Activity必须实现TitlesListFragmentCallBack接口");
        }
        mTitlesListFragmentCallBack = (TitlesListFragmentCallBack) activity;
    }

    // 当该Fragment从它所属的Activity中被删除时调用该方法
    @Override
    public void onDetach() {
        super.onDetach();
        mTitlesListFragmentCallBack = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    PageMultiUnitsReportManager.searchAdvertisementEvent(allDatas, Constant.PAGE_SEARCH_RECOMMEND);
                }
            }, 500);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        gameTitleView2 = (ViewStub) view.findViewById(R.id.SSA_gametitleView);
        netWorkStateView.setRetryCallBack(this);
        if (gameTitleView2 != null) {
            gameTitleView2.inflate();
        }
        mHotWordTitleViewRoot = (LinearLayout) view.findViewById(R.id.SSA_rootlayout1);
    }

    public void initAction() {
        initView();
        requestData();
    }

    /***
     * 处理本周标签
     *
     * @param group
     */
    private void processHotWordOrTagData(final UIModuleGroup<ItemData<UIModule<FunctionEssence>>> group, ViewGroup root) {
        try {
            if (group != null) {
                List<FunctionEssence> list = new ArrayList<FunctionEssence>();
                List<PresentData> presentDataList = new ArrayList<>();
                /**小屏幕手机会超出三行*/
                if (MyApplication.width <= 480) {
                    for (int i = 0; i < group.size() - 3; i++) {
                        list.add(group.getData().get(i).getmData().getData());
                        presentDataList.add(group.getData().get(i).getmPresentData());
                    }
                } else {
                    for (int i = 0; i < group.size(); i++) {
                        list.add(group.getData().get(i).getmData().getData());
                        presentDataList.add(group.getData().get(i).getmPresentData());
                    }
                }

                int lineCount = list.size() / 4;   //总行数
                mHotWordTitleViewRoot.removeAllViews();
                allDatas.clear();
                for (int j = 0; j < lineCount; j++) {
                    SearchAdvItemView advItemView = new SearchAdvItemView(context, getPageAlias(), mTitlesListFragmentCallBack);
                    List<FunctionEssence> functionEssences = list.subList(j * 4, (j + 1) * 4);
                    List<PresentData> presentDatas = presentDataList.subList(j * 4, (j + 1) * 4);
                    LogUtils.i("pppp", "搜索推荐页 小于总行数==>functionEssences的个数" + functionEssences.size() + "==title" + functionEssences.get(j).getTitle());
                    allDatas.addAll(functionEssences);
                    advItemView.setData(functionEssences);
                    advItemView.setPresentData(presentDatas);
                    mHotWordTitleViewRoot.addView(advItemView);
                }

                if (lineCount * 4 < list.size()) {
                    SearchAdvItemView advItemView = new SearchAdvItemView(context, getPageAlias(), mTitlesListFragmentCallBack);
                    List<FunctionEssence> functionEssences_ = list.subList(lineCount * 4, list.size());
                    List<PresentData> presentDatas_ = presentDataList.subList(lineCount * 4, list.size());
                    LogUtils.i("pppp", "搜索推荐页 *4还小于集合长度==>functionEssences_的个数" + functionEssences_.size() + "==title" + functionEssences_.get(0).getTitle());
                    allDatas.addAll(functionEssences_);
                    advItemView.setData(functionEssences_);
                    advItemView.setPresentData(presentDatas_);
                    mHotWordTitleViewRoot.addView(advItemView);
                }
            }

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

    }

    public void onPause() {
        super.onPause();
    }

    @Override
    public void onViewStop() {
    }

    public int getContentViewLayoutId() {
        return R.layout.activity_searchadvertisementactivity_v2;
    }

    @Override
    public void retry() {
        LogUtils.i("zzz", "搜索广告页重试");
        initAction();
    }

    /**
     * 获取网络请求数据；
     */
    private void requestData() {
        netWorkStateView.showLoadingBar();
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.SEARCH_URI, null, new WebCallBackToObject<UIModuleList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                netWorkStateView.showNetworkFailLayout();
            }

            @Override
            protected void handle(UIModuleList info) {
                List<ItemData<? extends BaseUIModule>> temp = NetDataAddShell.wrapModuleList(info, mlastPagePositionOfNetData);
                for (int i = 0; i < temp.size(); i++) {
                    BaseUIModule module = temp.get(i).getmData();
                    LogUtils.i("ttt", "module Type = " + module.getUIType());
                    if (PresentType.hot_words == module.getUIType()) {
                        processHotWordOrTagData((UIModuleGroup<ItemData<UIModule<FunctionEssence>>>) module, mHotWordTitleViewRoot);
                    }
                }
                setMlastPagePositionOfNetData(temp);
                netWorkStateView.setVisibility(View.GONE);
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

    // 在Activity需要实现该接口
    // 该Fragment将通过此接口与它所在的Activity交互
    public interface TitlesListFragmentCallBack {
        void onHotTagSelected(FunctionEssence gameTag);// 本周热门标签点击回调

        void hotWordOnclick(String hotword);// 本周热词点击回调

        void gotoNoDataFragment();// 跳转到无搜索结果页面回调

        void gotoAdverFragment();//跳转到搜索推荐页面回调

        void saveNoDataList(List<ItemData<? extends BaseUIModule>> list);
    }

}
