package cn.lt.game.ui.app.sidebar.feedback;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.detail.FeedBackDomainDetail;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.TimeUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.file.FeedbackRedUtil;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.community.MyPrivateMessageDetailActivity;
import cn.lt.game.ui.app.community.MyPrivateMessageItem;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;

/**
 * Created by zhengweijian on 15/8/24.
 */
public class FeedBackActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
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

    private TextView titlebar;
    private RefreshAndLoadMoreListView listView;
    private TextView version, model, system, netType;
    private ImageButton photographBtn;
    private TextView sendBtn;
    private EditText editText;
    private FeedbackAdapter mAdapter;
    private NetWorkStateView netWorkStateView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbackv2);
        initView();
        initState();
        getNetWorkData();
    }

    private void initView() {
        listView = (RefreshAndLoadMoreListView) findViewById(R.id.feedback_listView);
        listView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.margin_size_8dp));
        mAdapter = new FeedbackAdapter(this);
        listView.getmListView().addHeaderView(onCreateTopView());
        listView.setmOnRefrshListener(this);
        listView.setCanLoadMore(false);
        listView.setAdapter(mAdapter, false);

        netWorkStateView = (NetWorkStateView) findViewById(R.id.feedback_netWrokStateView);
        netWorkStateView.showLoadingBar();

        titlebar = (TextView) findViewById(R.id.tv_page_title);
        titlebar.setText("反馈");
        findViewById(R.id.btn_page_back).setOnClickListener(this);

        version = (TextView) findViewById(R.id.feedback_version_value);
        model = (TextView) findViewById(R.id.feedback_model_value);
        system = (TextView) findViewById(R.id.feedback_system_value);
        netType = (TextView) findViewById(R.id.feedback_netType_value);

        photographBtn = (ImageButton) findViewById(R.id.feedback_photographBtn);
        sendBtn = (TextView) findViewById(R.id.feedback_sendBtn);
        editText = (EditText) findViewById(R.id.feedback_edt);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendBtn.setEnabled(!TextUtils.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        photographBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);


    }

    private View onCreateTopView() {
        View view = LayoutInflater.from(this).inflate(R.layout.view_computer_top, null);
        TextView time = (TextView) view.findViewById(R.id.time);
        time.setText(TimeUtils.getCurrentTimeInString());
        return view;
    }

    private void initState() {
        String versionCode = null;
        try {
            versionCode = getVersionName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        version.setText(versionCode);
        model.setText(Utils.getDeviceName());
        system.setText("Android" + android.os.Build.VERSION.RELEASE);
        netType.setText(NetUtils.getNetworkType(this));
    }


    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_PERSONAL_FEEDBACK);
    }

    public void getNetWorkData() {
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.FEEDBACKS_URI, null, new WebCallBackToObject<UIModuleList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                listView.onLoadMoreComplete();
            }

            @Override
            protected void handle(UIModuleList info) {
                listView.onLoadMoreComplete();
                netWorkStateView.hideNetworkView();
                UIModuleList uiModuleList = info;
                ArrayList<MyPrivateMessageItem> chatInfoArrayList = new ArrayList<>();
                for (int i = 0; i < uiModuleList.size(); i++) {
                    UIModule uiModule = (UIModule) uiModuleList.get(i);
                    if (uiModule != null) {
                        FeedBackDomainDetail feedBackDomainDetail = (FeedBackDomainDetail) uiModule.getData();
                        MyPrivateMessageItem chatInfo = getMyPrivateMessageItem(uiModule, feedBackDomainDetail);
                        chatInfoArrayList.add(chatInfo);
                        if (i == 0) {
                            FeedbackRedUtil.setLocalFeedback(String.valueOf(feedBackDomainDetail.getId()));
                        }
                    }
                }
                mAdapter.setList(chatInfoArrayList);
            }
        });

    }

    private MyPrivateMessageItem getMyPrivateMessageItem(UIModule uiModule, FeedBackDomainDetail feedBackDomainDetail) {
        MyPrivateMessageItem chatInfo = new MyPrivateMessageItem();
        if (uiModule.getUIType().equals(PresentType.text_feedback)) {
            //文本内容
            chatInfo.content = feedBackDomainDetail.getContent();
            if ("user".equals(feedBackDomainDetail.getIdentifyUser())) {
                chatInfo.messageType = MyPrivateMessageDetailActivity.MSG_RIGHT_TEXT;
                chatInfo.headIcon = getMyHeadIcon();
            } else {//system , admin
                chatInfo.messageType = MyPrivateMessageDetailActivity.MSG_LEFT_TEXT;
            }
            chatInfo.time = feedBackDomainDetail.getCreated_at();

        } else {//PresentType.image_feedback
            //图片内容
            if ("user".equals(feedBackDomainDetail.getIdentifyUser())) {
                chatInfo.messageType = MyPrivateMessageDetailActivity.MSG_RIGHT_IMAGE;
                chatInfo.headIcon = getMyHeadIcon();
            } else {//system , admin
                chatInfo.messageType = MyPrivateMessageDetailActivity.MSG_LEFT_IMAGE;
            }
            chatInfo.remoteImage = feedBackDomainDetail.getThumb_url();
            chatInfo.remoteBigImage = feedBackDomainDetail.getImage_url();
            chatInfo.time = feedBackDomainDetail.getCreated_at();
        }
        return chatInfo;
    }

    private void sendImageMsg(String imagePath) {
        MyPrivateMessageItem msg = new MyPrivateMessageItem();
        msg.sendStatus = SEND_ING;
        msg.time = TimeUtils.getCurrentTimeInString();
        msg.messageType = MSG_RIGHT_IMAGE;
        msg.locaImage = imagePath;
        msg.headIcon = getMyHeadIcon();
        msg.progress = 0;
        mAdapter.add(msg);
        toListBottom();
        uploadImage(msg);
    }

    private void uploadImage(final MyPrivateMessageItem msg) {
        Map<String, Object> params = new HashMap<>();
        params.put("image", new File(msg.locaImage));
        uploadData(msg, params);
    }

    private void uploadText(final MyPrivateMessageItem msg) {
        Map<String, Object> params = new HashMap<>();
        params.put("content", msg.content);
        uploadData(msg, params);
    }

    /***
     * 上传文本或图片
     * @param msg
     * @param params
     */
    private void uploadData(final MyPrivateMessageItem msg, Map<String, Object> params) {
        Net.instance().executePost(Host.HostType.GCENTER_HOST, Uri2.FEEDBACKS_URI, params, new WebCallBackToObject<UIModuleList>() {
            @Override
            public void onFailure(int statusCode, Throwable error) {
                listView.onLoadingFailed();
                msg.sendStatus = SEND_FAILED;
                mAdapter.notifyDataSetChanged();
            }

            @Override
            protected void handle(UIModuleList info) {
                listView.onLoadMoreComplete();
                netWorkStateView.hideNetworkView();
                for (int i = 0; i < info.size(); i++) {
                    UIModule uiModule = (UIModule) info.get(i);
                    FeedBackDomainDetail feedBackDomainDetail = (FeedBackDomainDetail) uiModule.getData();
                    MyPrivateMessageItem chatInfo = getMyPrivateMessageItem(uiModule, feedBackDomainDetail);
                    if (i == 0) {
                        msg.sendStatus = SEND_SUCCESS;
                        msg.messageType = chatInfo.messageType;
                        msg.time = chatInfo.time;
                        msg.needShowTime = chatInfo.needShowTime;
                        msg.content = chatInfo.content;
                        msg.headIcon = chatInfo.headIcon;
                        msg.id = chatInfo.id;
                        msg.progress = chatInfo.progress;
                        msg.remoteImage = chatInfo.remoteImage;
                        msg.remoteBigImage = chatInfo.remoteBigImage;
                        msg.source = chatInfo.source;
                        mAdapter.handleTimeTag();
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mAdapter.add(chatInfo);
                    }
                    if (i == 0) {
                        FeedbackRedUtil.setLocalFeedback(String.valueOf(feedBackDomainDetail.getId()));
                    }
                }


            }
        });
    }

    private String getMyHeadIcon() {
        if (UserInfoManager.instance().isLogin()) {
            return UserInfoManager.instance().getUserInfo().getAvatar();
        }
        return "";
    }


    private void toListBottom() {
        listView.getmListView().setSelection(listView.getBottom());
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
        msg.headIcon = getMyHeadIcon();
        mAdapter.add(msg);
        toListBottom();
        uploadText(msg);

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
        uploadText(item);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.feedback_sendBtn:
                cheak();
                break;
            case R.id.feedback_photographBtn:
                PhotographDialog dialog = new PhotographDialog(view.getContext());
                dialog.show();
                break;
            case R.id.btn_page_back:
                finish();
                break;
            default:
        }

    }

    private void cheak() {
        sendTextMsg();
    }


    private String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        String version = packInfo.versionName;
        return version;
    }

    @Override
    public void onRefresh() {
        getNetWorkData();
    }
}
