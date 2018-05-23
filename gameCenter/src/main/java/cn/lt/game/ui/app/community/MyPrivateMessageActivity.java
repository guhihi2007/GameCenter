package cn.lt.game.ui.app.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.view.TitleMoreButton;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.NetIniCallBack;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.model.Category;
import cn.lt.game.ui.app.community.model.PrivateMessage;
import cn.lt.game.ui.app.community.model.PrivateMessageList;
import cn.lt.game.ui.app.community.topic.group.SpinerPopAdapter;
import cn.lt.game.ui.app.community.widget.SpinerPopWindow;
import cn.lt.game.ui.app.personalcenter.BindPhoneActivity;

/**
 * Created by wenchao on 2015/11/24.
 * 我的私信列表
 */
public class MyPrivateMessageActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, RefreshAndLoadMoreListView.OnLoadMoreListener {

    private RefreshAndLoadMoreListView mListView;
    private NetWorkStateView mNetWorkStateView;
    private TitleBarView mTitleBarView;
    private MyPrivateMessageAdapter mAdapter;
    private int currentPage = 1;
    private SpinerPopWindow popWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoryhottags);

        initialize();

    }

    @Override
    protected void onResume() {
        super.onResume();
        currentPage = 1;
        getData(currentPage);
    }

    private void initialize() {
        mTitleBarView = (TitleBarView) findViewById(R.id.search_bar);
        mTitleBarView.setTitle(R.string.my_private_message);
        mNetWorkStateView = (NetWorkStateView) findViewById(R.id.game_detail_netWrokStateView);
        mListView = (RefreshAndLoadMoreListView) findViewById(R.id.pullToRefreshListView);
        mAdapter = new MyPrivateMessageAdapter(this);
        mListView.setmOnRefrshListener(this);
        mListView.setOnItemClickListener(this);
        mListView.getmListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                showDeleteView(id);
                return true;
            }
        });
        mListView.setAdapter(mAdapter, false);

        mTitleBarView.setMoreButtonType(TitleMoreButton.MoreButtonType.BackHome);
        mTitleBarView.setBackHomeVisibility(View.VISIBLE);

        mNetWorkStateView.setNoDataCatSyle(NetWorkStateView.CatStyle.SMILE);
        mNetWorkStateView.setNoDataLayoutText("暂时没有私信", "");
        mNetWorkStateView.showLoadingBar();
        mNetWorkStateView.setRetryCallBack(new NetWorkStateView.RetryCallBack() {
            @Override
            public void retry() {
                currentPage = 1;
                getData(currentPage);
            }
        });

    }

    private void getData(int page) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        Net.instance().executeGet(Host.HostType.FORUM_HOST, Uri.COM_MY_PRIP_MSG, params, new WebCallBackToObj<PrivateMessageList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                mNetWorkStateView.showNetworkFailLayout();
                mListView.onLoadMoreComplete();
            }

            @Override
            protected void handle(PrivateMessageList info) {
                if (info == null || info.data == null || info.data.size() == 0) {
                    mNetWorkStateView.showNetworkNoDataLayout();
                } else {
                    if (info.total_page == currentPage) {
                        mListView.setCanLoadMore(false);
                    } else {
                        mListView.setCanLoadMore(true);
                    }

                    if (currentPage == 1) {
                        mAdapter.setList(info.data);
                    } else {
                        mAdapter.appendToList(info.data);
                    }
                    mNetWorkStateView.hideNetworkView();
                }

                mListView.onLoadMoreComplete();

            }
        });

    }

    private void delete(int friendUserId) {
        Net.instance().executeDelete(Host.HostType.FORUM_HOST, Uri.getPrivateMessageDetailUri(friendUserId), new WebCallBackToString() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
            }

        });

    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_PRIVATE_LATTER_MINE);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PrivateMessage message = mAdapter.getList().get((int) id);
        bindPhone(message);
//        Intent intent = new Intent(this, MyPrivateMessageDetailActivity.class);
//        intent.putExtra("friend_user_id",message.user_id);
//        intent.putExtra("friend_name",message.user_nickname);
//        intent.putExtra("friend_head",message.user_icon);
//        showDialog(intent);
    }

    private void showDeleteView(final long id) {
        List<Category> list = new ArrayList<Category>();
        list.add(new Category(0, "删除该私信"));
        popWindow = new SpinerPopWindow(MyPrivateMessageActivity.this, list);
        popWindow.showAtLocation(mTitleBarView, Gravity.CENTER, 0, 0);
        popWindow.setItemListener(new SpinerPopAdapter.IOnItemSelectListener() {
            @Override
            public void onItemClick(int pos) {
                PrivateMessage msg = mAdapter.getList().get((int) id);
                delete(msg.user_id);
                mAdapter.deleteMsg((int) id);
            }
        });
    }

    private void bindPhone(final PrivateMessage message) {
        CheckUserRightsTool.instance().hasBindPhone(this, new NetIniCallBack() {
            @Override
            public void callback(int code) {
                if (-1 == code) {
                    final MessageDialog messageDialog = new MessageDialog(MyPrivateMessageActivity.this, "提示", "您需要绑定手机号才能给对方发送私信哦", "取消", "现在绑定");
                    messageDialog.setLeftOnClickListener(new MessageDialog.LeftBtnClickListener() {
                        @Override
                        public void OnClick(View view) {
                            messageDialog.dismiss();
                        }
                    });
                    messageDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {
                        @Override
                        public void OnClick(View view) {
                            startActivity(new Intent(view.getContext(), BindPhoneActivity.class));
                            messageDialog.dismiss();
                        }
                    });
                    messageDialog.setCancelOnClickListener(new MessageDialog.CancelCliclListener() {
                        @Override
                        public void onClicl(View view) {
                            messageDialog.dismiss();
                        }
                    });
                    messageDialog.show();
                } else {
                    Intent intent = new Intent(MyPrivateMessageActivity.this, MyPrivateMessageDetailActivity.class);
                    intent.putExtra("friend_user_id", message.user_id);
                    intent.putExtra("friend_name", message.user_nickname);
                    intent.putExtra("friend_head", message.user_icon);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        getData(currentPage);
    }

    @Override
    public void onLoadMore() {
        getData(++currentPage);
    }
}
