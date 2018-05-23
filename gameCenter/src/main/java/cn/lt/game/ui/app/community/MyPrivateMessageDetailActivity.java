package cn.lt.game.ui.app.community;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.TimeUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.html.HtmlUtils;
import cn.lt.game.lib.util.log.Logger;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.web.WebCallBackBase;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.model.PrivateMessageDetaiList;
import cn.lt.game.ui.app.community.model.PrivateMessageDetail;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.sidebar.feedback.PhotographDialog;


/**
 * Created by wenchao on 2015/11/26.
 */
public class MyPrivateMessageDetailActivity extends BaseActivity implements View.OnClickListener, RefreshAndLoadMoreListView.OnLoadMoreListener, AdapterView.OnItemClickListener {

    /**
     * 左侧文本
     */
    public static final int MSG_LEFT_TEXT = 0;
    /**
     * 右侧文本
     */
    public static final int MSG_RIGHT_TEXT = 1;
    /**
     * 左侧图片
     */
    public static final int MSG_LEFT_IMAGE = 2;
    /**
     * 右侧图片
     */
    public static final int MSG_RIGHT_IMAGE = 3;

    /**
     * 发送状态，发送成功
     */
    public static final int SEND_SUCCESS = 0;
    /**
     * 发送失败
     */
    public static final int SEND_FAILED = 1;
    /**
     * 正在发送
     */
    public static final int SEND_ING = 2;

    private TitleBarView titlebar;
    private RefreshAndLoadMoreListView listView;
    private ImageButton photographBtn;
    private TextView sendBtn;
    private EditText editText;
    private NetWorkStateView netWorkStateView;

    private MyPrivateMessageDetailAdapter mAdapter;

    private int currentPage = 1;

    /**
     * 对话的对象
     */
    private int friendUserId;
    private String friendNickname;
    private String friendHeadIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_message_detail);
        getIntentData();
        initView();

        getData(currentPage);
    }

    private void getIntentData() {
        friendUserId = getIntent().getIntExtra("friend_user_id", 0);
        friendNickname = getIntent().getStringExtra("friend_name");
        friendHeadIcon = getIntent().getStringExtra("friend_head");
    }


    private void initView() {
        listView = (RefreshAndLoadMoreListView) findViewById(R.id.listView);
//        listView.setHeaderViewLoadMore();
        listView.setOnLoadMoreListener(this);
        listView.getmListView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.margin_size_8dp));
        mAdapter = new MyPrivateMessageDetailAdapter(this, friendUserId);
        listView.setAdapter(mAdapter, false);

        netWorkStateView = (NetWorkStateView) findViewById(R.id.netWrokStateView);

        titlebar = (TitleBarView) findViewById(R.id.titleBarView);
        titlebar.setBackHomeVisibility(View.INVISIBLE);
        titlebar.setTitle(friendNickname);

        photographBtn = (ImageButton) findViewById(R.id.photographBtn);
        sendBtn = (TextView) findViewById(R.id.sendBtn);
        editText = (EditText) findViewById(R.id.edit);

        photographBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

        listView.setOnItemClickListener(this);
        listView.setRefreshEnabled(false);

    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_PRIVATE_LATTER_DETAIL);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendBtn:
                sendTextMsg();
                break;
            case R.id.photographBtn:
                PhotographDialog dialog = new PhotographDialog(this);
                dialog.show();
                break;
            default:
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /*获取图片地址，并发送图片*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PhotographDialog.PHOTO_URL_RESULT:

                    if (data != null) {
                        android.net.Uri uri = data.getData();
                        String path = Utils.getImagePath(this, uri);
                        sendImageMsg(path);
                    }
                    break;
                case PhotographDialog.TAKE_PICTURE:
                    if (PhotographDialog.uri != null) {
                        String path = PhotographDialog.uri.getPath();
                        sendImageMsg(path);
                    }
                    break;
                default:
            }
        }

    }

    private void getData(int page) {
        if (page == 1) netWorkStateView.showLoadingBar();
        HashMap<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        Net.instance().executeGet(Host.HostType.FORUM_HOST, Uri.getPrivateMessageDetailUri(friendUserId), params, new WebCallBackToObj<PrivateMessageDetaiList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                netWorkStateView.hideNetworkView();
                listView.onLoadingFailed();
            }

            @Override
            protected void handle(PrivateMessageDetaiList info) {
                if (info == null || info.data == null || info.data.size() == 0) {
                } else {
                    final List<MyPrivateMessageItem> list = new ArrayList<MyPrivateMessageItem>();
                    if (info.total_page == currentPage) {
                        listView.setCanLoadMore(false);
                    } else {
                        listView.setCanLoadMore(true);
                    }
                    for (PrivateMessageDetail detail : info.data) {
                        list.add(new MyPrivateMessageItem(detail, friendHeadIcon));
                    }
                    if (currentPage == 1) {
                        mAdapter.setList(list);
                        toListBottom();
                        onTouchByNeedUpdateToListBottom();
                    } else {
                        mAdapter.appendToList(list);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                listView.getmListView().setSelection(list.size() - 1);
                            }
                        }, 10);
                    }
                }
                netWorkStateView.hideNetworkView();
                listView.onLoadMoreComplete();
            }
        });
    }

    private void sendMsg(final MyPrivateMessageItem msg, String sendContent) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(friendUserId));
        params.put("content", sendContent);
        Net.instance().executePost(Host.HostType.FORUM_HOST, Uri.COM_SEND_PRIP_MSG, params, new WebCallBackToString() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                Logger.i("send_failed=" + statusCode);
                msg.sendStatus = SEND_FAILED;
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSuccess(String result) {
                Logger.i(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        msg.sendStatus = SEND_SUCCESS;
                        mAdapter.notifyDataSetChanged();
                    } else {
                        msg.sendStatus = SEND_FAILED;
                        mAdapter.notifyDataSetChanged();
                        ToastUtils.showToast(MyPrivateMessageDetailActivity.this, jsonObject.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.sendStatus = SEND_FAILED;
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void sendTextMsg() {
        String text = editText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            return;
        }
        MyPrivateMessageItem msg = new MyPrivateMessageItem();
        msg.sendStatus = SEND_ING;

        msg.time = TimeUtils.getCurrentTimeInString();
        msg.messageType = MSG_RIGHT_TEXT;
        msg.content = text;
        msg.headIcon = UserInfoManager.instance().getUserInfo().getAvatar();
        mAdapter.add(msg);
        toListBottom();
        sendMsg(msg, text);

        editText.setText("");
    }

    /**
     * 重新发送文本消息
     *
     * @param item
     */
    public void sendTextMsgRetry(MyPrivateMessageItem item) {
        item.sendStatus = SEND_ING;
        item.time = TimeUtils.getCurrentTimeInString();
        toListBottom();
        sendMsg(item, item.content);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 重新发送图片消息
     *
     * @param item
     */
    public void sendImageMsgRetry(MyPrivateMessageItem item) {
        item.sendStatus = SEND_ING;
        item.time = TimeUtils.getCurrentTimeInString();
        item.progress = 0;
        toListBottom();
        uploadImage(item);
        mAdapter.notifyDataSetChanged();
    }


    private void sendImageMsg(String imagePath) {
        MyPrivateMessageItem msg = new MyPrivateMessageItem();
        msg.sendStatus = SEND_ING;
        msg.time = TimeUtils.getCurrentTimeInString();
        msg.messageType = MSG_RIGHT_IMAGE;
        msg.locaImage = imagePath;
        msg.headIcon = UserInfoManager.instance().getUserInfo().getAvatar();
        msg.progress = 0;
        mAdapter.add(msg);
        toListBottom();

        uploadImage(msg);
    }


    private void toListBottom() {
        listView.getmListView().setSelection(listView.getBottom());
    }

    private void uploadImage(final MyPrivateMessageItem msg) {
        final String imagePath = msg.locaImage;
        Map<String, Object> ha = new HashMap<String, Object>();
        ha.put("file", Utils.Bitmap2InputStream(BitmapFactory.decodeFile(imagePath)));
        ha.put("use", "comment");
        Net.instance().executePost(Host.HostType.FORUM_HOST, cn.lt.game.net.Uri.COM_MULTIMEDIAS_PHOTOS_UPLOAD, ha, new WebCallBackBase() {
            @Override
            public void route(String result) {
                try {
                    JSONObject jb = new JSONObject(result);
                    if (jb.getInt("status") == 1) {
                        String resourceId = jb.getJSONObject("data").getString("resource_id");
                        String url = jb.getJSONObject("data").getString("url");

//                                uploadImage.resourceId = resourceId;
//                                uploadImage.downUrl = url;
                        msg.remoteImage = url;
                        sendMsg(msg, HtmlUtils.convertToHtmlWidthImg(url));
                    } else {
                        sendImageFailed(msg);
                    }
                } catch (JSONException e) {
                    sendImageFailed(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                sendImageFailed(msg);
            }

            @Override
            public void transferred(long uploadSize, long totalSize) {
                msg.progress = (int) (uploadSize * 100.0f / totalSize);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void sendImageFailed(MyPrivateMessageItem item) {
        item.sendStatus = SEND_FAILED;
        item.progress = 0;
        mAdapter.notifyDataSetChanged();
    }

    private void onTouchByNeedUpdateToListBottom() {
        listView.getmListView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mAdapter.setNeedBottom(false);
                return false;
            }
        });
    }


    @Override
    public void onLoadMore() {
        getData(++currentPage);
    }
}
