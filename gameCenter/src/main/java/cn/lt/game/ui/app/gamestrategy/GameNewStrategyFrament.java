package cn.lt.game.ui.app.gamestrategy;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.huanju.data.content.raw.info.HjInfoListItem;

import java.util.ArrayList;

import cn.lt.game.R;
import cn.lt.game.db.operation.FavoriteDbOperator;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.model.GameBaseDetail;

@SuppressLint("ValidFragment")
public class GameNewStrategyFrament extends BaseStrategyFragment implements OnItemClickListener, RetryCallBack, SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener {
    /**
     * 每页数据条数
     */
    private final int DATA_SIZE = 10;
    private View mView;
    private RefreshAndLoadMoreListView listView;
    private StrategyAdapter adapter;
    private Boolean hasMore = true;
    /**
     * 当前页
     */
    private int currentPage = 0;
    private Boolean isFirst = true;
    private String id;
    private NetWorkStateView netWorkStateView;
    private ArrayList<HjInfoListItem> list = new ArrayList<HjInfoListItem>();
    private GameBaseDetail game = new GameBaseDetail();

    public static GameNewStrategyFrament newInstance(String id) {
        GameNewStrategyFrament newFragment = new GameNewStrategyFrament();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_STRATEGY_NEWS);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        id = args.getString("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mView = inflater.inflate(R.layout.frament_newstrategy, container, false);
        currentPage = 0;
        init();
        super.onCreateView(inflater, container, savedInstanceState);
        return mView;
    }

    private void init() {
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.header_transparent, null);
        netWorkStateView = (NetWorkStateView) mView.findViewById(R.id.newstrategy_netWrokStateView);
        listView = (RefreshAndLoadMoreListView) mView.findViewById(R.id.newstrategy_listView);
        listView.setmOnRefrshListener(this);
        listView.setOnLoadMoreListener(this);
        listView.setRefreshEnabled(false);
        listView.setOnItemClickListener(this);
        adapter = new StrategyAdapter(mActivity, list);
        listView.setAdapter(adapter, false);
        listView.getmListView().addHeaderView(headView);
        netWorkStateView.setRetryCallBack(this);
        checkNetwork(netWorkStateView, currentPage, DATA_SIZE, id, isFirst);
        currentPage++;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtils.i("Erosion", "GameNewStrategyFrament|| position====" + position);
        if (position > 0) {
            HjInfoListItem item = list.get(position - 1);
            game.setPkgName(item.package_name);
            ActivityActionUtils.jumpToGameOtherDetail(mActivity, FavoriteDbOperator.GameStrategyTag, item.id, item.title, game);
        }
    }

    @Override
    public void onSuccess(ArrayList<HjInfoListItem> list) {
        // TODO Auto-generated method stub
        currentPage++;


        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).package_name.equals("")) {
                this.list.add(list.get(i));
            }
        }

        listView.onLoadMoreComplete();
        adapter.notifyDataSetChanged();

        if (list.size() < 10) {
            hasMore = false;
        }
        listView.setCanLoadMore(hasMore);
    }

    @Override
    public void retry() {
        if (!NetUtils.isConnected(getActivity())) {
            ToastUtils.showToast(getActivity(), "网络连接失败");
            return;
        }
        isFirst = true;
        checkNetwork(netWorkStateView, 0, DATA_SIZE, id, isFirst);
    }

    @Override
    public void onEmpty() {
        hasMore = false;
        listView.setCanLoadMore(false);
        listView.onLoadMoreComplete();
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        isFirst = false;
        if (hasMore) {
            checkNetwork(netWorkStateView, currentPage, DATA_SIZE, id, isFirst);
        }

        currentPage++;
    }
}
