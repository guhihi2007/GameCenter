package cn.lt.game.ui.app.community.topic.detail.reply;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.face.FaceView;
import cn.lt.game.ui.app.community.model.Comment;
import cn.lt.game.ui.app.community.model.Relies;
import cn.lt.game.ui.app.community.model.Reply;
import cn.lt.game.ui.app.community.topic.detail.ReplyView;

/**
 * 评论详情页面
 * Created by zhengweijian on 15/8/30.
 */
public class ReplyActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener,
        FaceView.Work,SwipeRefreshLayout.OnRefreshListener, RefreshAndLoadMoreListView.OnLoadMoreListener,
        NetWorkStateView.RetryCallBack, ReplyView.OnReplyerClickListener {



    public enum KeyboardState {
        face(), Keyboard(), Default()
    }

    public enum JumpType {
        Default(), Inoperatio(), SetName(), ClickReply()
    }

    private static final String CACHE_KEY = "reply_context";

    private ArrayList<IReplyView> list = new ArrayList<IReplyView>();
    private ReplyAdapter adapter;
    private TitleBarView titlebar;
    private RefreshAndLoadMoreListView listView;
    private EditText replyEdt;
    private ImageButton expressionBtn;
    private TextView send;
    private LinearLayout faceLayout;
    private FaceView faceView;
    private Comment comment;
    private NetWorkStateView netWorkStateView;
    private int page = 1;
    private String masterName; //楼主名字
    private int masterId;// 楼主ID
    private String author_nickname; //被回复人
    private int topic_id;
    private HeaderItem headerItem;
    private int reply_count = 0;
    private int page_count = 0;
    private int author_id = 0;
    private StringBuilder builder = new StringBuilder();
    private String beforeS;
    private JumpType jumpType;
    private boolean isRecord = false;
    private int clickReplyId;// 被点击“回复人”的id
    private String clickReplyName;// 被点击“回复人”的昵称
    private String replyContent;// 回复的内容


    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_COMMENT_DETAIL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        getIntentData();
        initView();
        initface();
        initReplyEdt();
        getNetWorkData(page, false);
    }


    public void getIntentData() {
        Intent intent = getIntent();
        comment = (Comment) intent.getSerializableExtra("comment");
        masterName = intent.getStringExtra("author_nickname");
        Log.i("zzz","评论昵称==="+masterName);
        author_nickname = masterName;
        masterId = intent.getIntExtra("author_id", -1);
        author_id = masterId;
        topic_id = intent.getIntExtra("topic_id", -1);
        jumpType = (JumpType) intent.getSerializableExtra("type");
        clickReplyId = intent.getIntExtra("clickReplyId", 0);
        clickReplyName = intent.getStringExtra("clickReplyName");
        Log.i("zzz", "clickReplyId = " + clickReplyId + " , clickReplyName = " + clickReplyName);
        headerItem = new HeaderItem(comment);
        list.add(headerItem);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("zzz","销毁评论详情参数");
        getIntent().removeExtra("comment");
        getIntent().removeExtra("author_nickname");
    }

    private void initView() {
        titlebar = (TitleBarView) findViewById(R.id.activityReply_titleBar);
        titlebar.setBackHomeVisibility(View.GONE);
        titlebar.setTitle(comment.floor + "楼");
        listView = (RefreshAndLoadMoreListView) findViewById(R.id.activityReplay_listView);
        send = (TextView) findViewById(R.id.activityReply_sendBtn);
        expressionBtn = (ImageButton) findViewById(R.id.activityReply_expressionBtn);
        expressionBtn.setTag(KeyboardState.Keyboard);
        replyEdt = (EditText) findViewById(R.id.activityReply_edt);


        netWorkStateView = (NetWorkStateView) findViewById(R.id.activityReplay_netwrokStateView);
        netWorkStateView.setRetryCallBack(this);
        netWorkStateView.showLoadingBar();

        faceLayout = (LinearLayout) findViewById(R.id.activityReply_faceLayout);

        adapter = new ReplyAdapter(this, list);
//        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setAdapter(adapter, false);
//        listView.setOnRefreshListener(this);
        listView.getmListView().setOnItemClickListener(this);
//        listView.setOnLoadMoreListener(this);
        listView.setmOnRefrshListener(this);
        expressionBtn.setOnClickListener(this);
        send.setOnClickListener(this);


        replyEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    faceLayout.setVisibility(View.GONE);
                    expressionBtn.setTag(KeyboardState.Keyboard);
                    expressionBtn.setImageDrawable(getResources().getDrawable(R.mipmap.community_topic_face));
                    imm.showSoftInput(replyEdt, 0);

                }
                return false;
            }
        });


        replyEdt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                beforeS = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                System.out.println("S length" + s.length() + " " + s.toString() + "befores length" + beforeS.length() + " " + beforeS.toString() + builder.length());

                if (isRecord) {

                    if (s.length() > beforeS.length()) {

                        builder.append(s.subSequence(beforeS.length(), s.length()));

                    }

                    if (builder.length() <= 0) {

                        s.delete(0, s.length());
                    }

                    if (s.length() < beforeS.length() && s.length() - beforeS.length() < builder.length() && builder.length() > 0) {

                        builder.delete(builder.length() - (beforeS.length() - s.length()), builder.length());

                    }

                }

                isRecord = true;
            }

        });





    }

    /** 根据打开本页面的类型初始化输入框的内容*/
    private void initReplyEdt() {
        if (jumpType == JumpType.Default) {
            hideKeyboard();
            this.isRecord = true;
        } else if (jumpType == JumpType.SetName) {
            replyEdt.setFocusable(true);
            replyEdt.setFocusableInTouchMode(true);
            replyEdt.requestFocus();
            replyEdt.setSelection(replyEdt.getText().length());
            setEditName(author_nickname, true);
        } else if (jumpType == JumpType.Inoperatio) {
            setEditName(author_nickname, true);
            hideKeyboard();
        } else if(jumpType == JumpType.ClickReply) {
            setReplyIdAndName();
        }
    }

    private void setReplyIdAndName() {
        if(clickReplyId != 0 && !clickReplyName.equals("")) {
            author_id = clickReplyId;
            author_nickname = clickReplyName;
        }
        replyEdt.setFocusable(true);
        replyEdt.setFocusableInTouchMode(true);
        replyEdt.requestFocus();
        replyEdt.setSelection(replyEdt.getText().length());
        setEditName(author_nickname, true);

    }


    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(replyEdt.getWindowToken(), 0);
    }

    private void initface() { // 初始化表情 以及键盘隐藏或者显示等等状态
        faceView = new FaceView(this, null, this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        faceLayout.addView(faceView, params);
    }


    private void getNetWorkData(final int page, final Boolean isPullDown) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", page + "");

        Net.instance().executeGet(Host.HostType.FORUM_HOST, Uri.getCommentRepliesUri(topic_id, comment.comment_id), params, new WebCallBackToObj<Relies>() {

            @Override
            protected void handle(Relies info) {

                netWorkStateView.setVisibility(View.GONE);

                if (info.detail.size() == 0) {
//                    listView.onRefreshComplete();
                    listView.onLoadMoreComplete();
                    adapter.notifyDataSetChanged();
                    return;
                }

//                page_count = info.total_page;
//
//                if (page_count > page) {
//                    listView.setMode(PullToRefreshBase.Mode.BOTH);
//                }
                listView.onLoadMoreComplete();
                listView.setCanLoadMore(page <= page_count);

                reply_count = info.total;
                headerItem.setReplyCount(info.total);
                if (isPullDown) {
                    list.clear();
                    list.add(headerItem);
                }

                initItemList(page, info.detail);

//                listView.onRefreshComplete();
                adapter.notifyDataSetChanged();

                if (list.size() - 2 == reply_count) {
//                    listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    listView.setCanLoadMore(false);
                }

            }


            @Override
            public void onFailure(int statusCode, Throwable error) {
                if (800==statusCode){
                    ToastUtils.showToast(ReplyActivity.this,"您查看的评论已删除！");
                    ReplyActivity.this.finish();
                }else if (901==statusCode){
                    hideKeyboard();
                    listView.setVisibility(View.GONE);
                    netWorkStateView.showNetworkNoDataLayout();
                    netWorkStateView.setNotDataState(NetWorkStateView.gotoFeedback);
                    netWorkStateView.setNoDataLayoutText("啊哦...您暂时无法进行该操作呢", "联系管理员");
                }else{
//                    listView.onRefreshComplete();
//                    listView.onLoadMoreComplete();
                    listView.onLoadingFailed();
                    netWorkStateView.showNetworkFailLayout();
                }

            }

        });
    }

    // 点击回复按钮
    private void sendMsg(String content, final String author_nickname) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("acceptor_id", author_id + "");
        params.put("acceptor_nickname", author_nickname);
        params.put("reply_content", content);
        send.setClickable(false);

        Net.instance().executePost(Host.HostType.FORUM_HOST, Uri.getrReplyCreateUri(topic_id, comment.comment_id), params, new WebCallBackToObj<Reply>() {

            @Override
            protected void handle(Reply info) {
                Log.i("RATime", info.created_at);
                if (page_count <= 1 || page == page_count) {
                    addItemForList(info);
                }

                headerItem.setReplyCount(headerItem.getReplyCount() + 1);
                info.reply_content = replyContent;
//                listView.onRefreshComplete();
                listView.onLoadMoreComplete();
                adapter.notifyDataSetChanged();
                listView.getmListView().setSelection(list.size() - 1);
                ToastUtils.showToast(ReplyActivity.this,"回复成功！");
                ReplyActivity.this.author_nickname = masterName;
                author_id = masterId;
                send.setClickable(true);
            }


            @Override
            public void onFailure(int statusCode, Throwable error) {

//                listView.onRefreshComplete();
                listView.onLoadMoreComplete();
                Log.i("zzz", "返回码==" + statusCode);
                if (800 == statusCode) {
                    ToastUtils.showToast(ReplyActivity.this,"发布的内容包含敏感词");
                    replyEdt.setText(replyContent);
                    replyEdt.setSelection(replyEdt.getText().length());
                } else {
                    ToastUtils.showToast(getApplicationContext(), "回复失败，请检查您的网络");
                    replyEdt.setText(replyContent);
                    replyEdt.setSelection(replyEdt.getText().length());
                }
                send.setClickable(true);
            }
        });
    }


    private void addItemForList(Reply info) {
        if (list.get(list.size() - 1).getType() == ReplyAdapter.ReplyItemType.FOOT_ITEM_TYPE.type) {
            list.remove(list.size() - 1);
        }

        list.add(new ReplyViewItem(info, this));
        list.add(new FootItem());
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        if(position == 1 && list.get(0) != null) {
            HeaderItem item = (HeaderItem)list.get(0);
            author_nickname = item.getReplyName();
            setTargetIdAndName(item.getReplyName(), item.getAuthorId());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activityReply_sendBtn:
                check();
                break;
            case R.id.activityReply_expressionBtn:
                initExpressionBtn(view);
                break;
            default:
        }
    }


    /*更改表情按钮状态*/
    private void initExpressionBtn(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        switch ((KeyboardState) view.getTag()) {
            case Keyboard:
                view.setTag(KeyboardState.face);
                imm.hideSoftInputFromWindow(replyEdt.getWindowToken(), 0);
                faceLayout.setVisibility(View.VISIBLE);
                ((ImageButton) view).setImageDrawable(getResources().getDrawable(R.mipmap.topic_keyboard));
                break;
            case face:
                view.setTag(KeyboardState.Keyboard);
                faceLayout.setVisibility(View.GONE);
                replyEdt.setFocusable(true);
                replyEdt.setFocusableInTouchMode(true);
                replyEdt.requestFocus();
                imm.showSoftInput(replyEdt, 0);
                ((ImageButton) view).setImageDrawable(getResources().getDrawable(R.mipmap.community_topic_face));
                break;
            case Default:
                view.setTag(KeyboardState.Keyboard);
                imm.hideSoftInputFromWindow(replyEdt.getWindowToken(), 0);
                faceLayout.setVisibility(View.GONE);
                ((ImageButton) view).setImageDrawable(getResources().getDrawable(R.mipmap.community_topic_face));
                break;
        }
    }

    private void check() {
        replyContent = replyEdt.getText().toString();
        String authorName = "";

        if (builder.length() <= 0) {
            ToastUtils.showToast(this, "请输入回复内容");

            return;
        }


        if (replyContent.length() > author_nickname.length()) {

            authorName = replyContent.substring(1, author_nickname.length() + 1);

        }

        if (authorName.equalsIgnoreCase(author_nickname)) {
            replyContent = replyContent.substring(author_nickname.length() + 2, replyContent.length());
        }


        try {
            if (replyContent.getBytes("GBK").length > 40000) {
                ToastUtils.showToast(this, "内容最大长度不可超过40000字符哦");
                return;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(replyContent.trim().isEmpty()){
            ToastUtils.showToast(this, "输入的字符不能都是空的哦");
            return;
        }

        builder.delete(0, builder.length());
        replyEdt.setText("");
        sendMsg(replyContent, author_nickname);
        expressionBtn.setTag(KeyboardState.Default);
        initExpressionBtn(expressionBtn);

    }

    private void initItemList(int page, List<Reply> replyList) {
        if(list.get(list.size() - 1) != null) {
            if (list.get(list.size() - 1).getType() == ReplyAdapter.ReplyItemType.FOOT_ITEM_TYPE.type) {
                list.remove(list.size() - 1);
            }
        }


        for (int i = 0; i < replyList.size(); i++) {
            list.add(new ReplyViewItem(replyList.get(i), this));
        }

        if (list.size() - 1 == reply_count) {
            list.add(new FootItem());
        }


    }


    /*表情点击回调*/
    @Override
    public void onClick(String item_str) {
        int index = replyEdt.getSelectionStart();//获取光标所在位置
        Editable edit = replyEdt.getEditableText();//获取EditText的文字

        if (index < 0 || index >= edit.length()) {
            edit.append(item_str);
        } else {
            edit.insert(index, item_str);//光标所在位置插入文字
        }

    }


    @Override
    public void retry() {
        page = 1;
        getNetWorkData(page, true);
    }

    public static Intent getIntent(Context context, Comment comment, int topic_id, int author_id, String author_nickname, JumpType type) {
        return new Intent(context, ReplyActivity.class).putExtra("comment", comment).putExtra("topic_id", topic_id).putExtra("author_id", author_id).putExtra("author_nickname", author_nickname).putExtra("type", type);
    }

    public static Intent getIntent(Context context, Comment comment, int topic_id, int author_id, String author_nickname, JumpType type, int clickReplyId, String clickReplyName) {
        return new Intent(context, ReplyActivity.class).putExtra("comment", comment).
                putExtra("topic_id", topic_id).putExtra("author_id", author_id).
                putExtra("author_nickname", author_nickname).putExtra("type", type).
                putExtra("clickReplyId", clickReplyId).putExtra("clickReplyName", clickReplyName);
    }


    private void setEditName(String author_nickname, boolean isRecord) {
        this.isRecord = isRecord;
        replyEdt.setText("@" + author_nickname + ":");
        replyEdt.setSelection(replyEdt.getText().length());
        builder.delete(0, builder.length());

    }

    @Override
    public void OnReplyerNameClick(String replyerName, int replyerId) {
        setTargetIdAndName(replyerName, replyerId);
    }

    @Override
    public void OnAcceptorNameClick(String acceptorName, int acceptorId) {
        setTargetIdAndName(acceptorName, acceptorId);
    }

    /** 设置将要被回复目标用户的昵称与ID，并在输入框显示目标昵称*/
    private void setTargetIdAndName(String targetName, int targetId) {
        author_nickname = targetName;
        author_id = targetId;
        setEditName(author_nickname, false);
        expressionBtn.setTag(KeyboardState.face);
        initExpressionBtn(expressionBtn);
    }


    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        page = 1;
        getNetWorkData(page, true);
    }

    /**
     * Called when the list reaches the last item (the last item is visible
     * to the user)
     */
    @Override
    public void onLoadMore() {
        page++;
        getNetWorkData(page, false);
    }

}
