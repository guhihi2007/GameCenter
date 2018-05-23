package cn.lt.game.ui.app.gamestrategy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.huanju.data.content.raw.info.HjInfoListItem;

import java.util.ArrayList;

import cn.lt.game.R;
import cn.lt.game.db.operation.FavoriteDbOperator;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.ui.app.gamestrategy.GameStrategyHomeActivity.removeCursorCallBack;

@SuppressLint("ValidFragment")
public class GameStrategyCenterFrament extends BaseStrategyFragment implements OnItemClickListener, removeCursorCallBack, RetryCallBack, RefreshAndLoadMoreListView.OnLoadMoreListener {
    /**
     * 每页数据条数
     */
    private final int DATA_SIZE = 10;
    private View mView;
    private RefreshAndLoadMoreListView listView;
    private StrategyAdapter adapter;
    private Boolean hasMore = true;
    private EditText etText;
    private ImageButton searchBT;
    /**
     * 当前页
     */
    private int currentPage = 0;
    private Boolean isFirst = true;
    private String id;
    private NetWorkStateView netWorkStateView;
    // private LinearLayout head;
    private int headHeight = 0;
    private ArrayList<HjInfoListItem> list = new ArrayList<HjInfoListItem>();
    private GameBaseDetail game = new GameBaseDetail();
    private int dividerHeight = 0;

    public static GameStrategyCenterFrament newInstance(String id) {
        GameStrategyCenterFrament newFragment = new GameStrategyCenterFrament();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_STRATEGY_CENTER);
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
        mView = inflater.inflate(R.layout.frament_strategycenter, container, false);
        currentPage = 0;
        init();
        super.onCreateView(inflater, container, savedInstanceState);
        return mView;
    }

    private void init() {

        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.header_transparent, null);

        netWorkStateView = (NetWorkStateView) mView.findViewById(R.id.strategycenter_networkstateView);
        listView = (RefreshAndLoadMoreListView) mView.findViewById(R.id.strategycenter_listView);
        listView.getmListView().addHeaderView(headView);
        etText = (EditText) mView.findViewById(R.id.strategycenter_searchbar_search_edt);
        searchBT = (ImageButton) mView.findViewById(R.id.strategycenter_searchbar_search_img);
        adapter = new StrategyAdapter(mActivity, list);
        listView.setRefreshEnabled(false);
        listView.setOnLoadMoreListener(this);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter, false);
        checkNetwork(netWorkStateView, currentPage, DATA_SIZE, id, isFirst);
        currentPage++;
        initListener();

    }

    private void initListener() {

        mActivity.setCallBack(this);

        netWorkStateView.setRetryCallBack(this);

        searchBT.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String data = etText.getText().toString().trim();
                if (!TextUtils.isEmpty(data)) {
                    Intent intent = new Intent(mActivity, StrategySearchActivity.class);
                    intent.putExtra("searchData", etText.getText().toString().trim());
                    startActivity(intent);
                } else {
                    ToastUtils.showToast(mActivity, "请输入关键字");
                }
            }
        });

        etText.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    etText.setCursorVisible(true);
                }
                return false;
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0) {
            HjInfoListItem item = list.get(position - 1);
            game.setPkgName(item.package_name);
            ActivityActionUtils.jumpToGameOtherDetail(mActivity, FavoriteDbOperator.GameStrategyTag, item.id, item.title, game);
        }

    }

    @Override
    public void removeCursor() {
        // TODO Auto-generated method stub
        if (etText != null) {
            etText.setCursorVisible(false);
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

        if (list.size() < 10) {
            hasMore = false;
        }
        listView.setCanLoadMore(hasMore);
        listView.onLoadMoreComplete();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void retry() {
        isFirst = true;
        checkNetwork(netWorkStateView, 0, DATA_SIZE, id, isFirst);
    }

    @Override
    public void onEmpty() {
        // TODO Auto-generated method stub
        hasMore = false;
        listView.onLoadMoreComplete();
        listView.setCanLoadMore(false);
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
