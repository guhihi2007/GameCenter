package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.net.Host.HostType;
import cn.lt.game.net.Net;
import cn.lt.game.net.NetIniCallBack;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.CheckUserRightsTool;
import cn.lt.game.ui.app.community.model.Comment;
import cn.lt.game.ui.app.community.model.Relies;
import cn.lt.game.ui.app.community.model.Reply;
import cn.lt.game.ui.app.community.topic.detail.reply.ReplyActivity;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;

/* 3.2 弃用*/
public class ReplyALLLoadingView extends RelativeLayout implements OnClickListener {
    private static final int all = 1; // 这个版本暂时返回全部

    public enum TagEnum {
        getdata(), removedata(), Default()
    }

    private TextView allBt;
    private ProgressBar bar;
    private int topicId;
    private int group_id;
    private int userId;
    private Context context;
    private IGetDataComplete iGetDataComplete;
    private TagEnum tag;
    private Comment comment;

    public ReplyALLLoadingView(Context context) {
        this(context, null);

    }

    public ReplyALLLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReplyALLLoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        int padding = (int) context.getResources().getDimension(
                R.dimen.inInterval);
        setBackground(getResources().getDrawable(R.drawable.top_line_click));
        setPadding(0, padding, 0, padding);
        LayoutInflater.from(context).inflate(R.layout.replyall_foot_layout,
                this);
        init();
    }

    private void init() {
        allBt = (TextView) findViewById(R.id.replyAll_foot_bt);
        bar = (ProgressBar) findViewById(R.id.replyAll_progressBar);
        allBt.setOnClickListener(this);

    }

    @Override
    public void onClick(final View v) {

        switch (tag) {
            case getdata:
                allBt.setText("正在获取回复");
                bar.setVisibility(View.VISIBLE);
                checkNetWork();
                break;
            case removedata:
                if (iGetDataComplete != null) {
                    iGetDataComplete.remove();
                }
                break;
            case Default:

                CheckUserRightsTool.instance().checkUserRights(context, false,
                        group_id, new NetIniCallBack() {

                            @Override
                            public void callback(int code) {

                                if (code == 0) {

                                    if (userId != -1 ) {

                                        ((BaseActivity) v.getContext()).startActivityForResult(ReplyActivity.getIntent(context, comment, topicId, comment.author_id, comment.author_nickname, ReplyActivity.JumpType.Inoperatio), 1);

                                    } else {

                                        UserInfoManager.instance().isLoginHaveCall(
                                                v.getContext(), true);
                                    }

                                }

                            }
                        });

                break;

            default:
                break;
        }

    }

    private void checkNetWork() {
        if (NetUtils.isConnected(context)) {
            getReplyAllData();
        } else {
            bar.setVisibility(View.GONE);
            allBt.setText("获取回复失败，请点击重新获取");
        }
    }

    private void getReplyAllData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", all + "");
        Net.instance().executeGet(HostType.FORUM_HOST,
                new Uri().getCommentRepliesUri(topicId, comment.comment_id), params,
                new WebCallBackToObj<Relies>() {

                    @Override
                    protected void handle(Relies info) {

                        bar.setVisibility(View.GONE);
                        allBt.setText("收起回复");

                        if (iGetDataComplete != null) {
                            iGetDataComplete.onSuccess(info.detail);
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error) {

                        bar.setVisibility(View.GONE);
                        allBt.setText("获取回复失败，请点击重新获取");
                    }
                });

    }

    public TagEnum getTag() {
        return tag;
    }

    public void setTag(TagEnum tag) {
        this.tag = tag;
        switch (tag) {
            case getdata:
                allBt.setText("展开所有回复");
                break;
            case removedata:
                allBt.setText("收起回复");
                break;
            case Default:
                allBt.setText("查看全部回复");
                break;
            default:
                break;
        }
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public IGetDataComplete getiGetDataComplete() {
        return iGetDataComplete;
    }

    public void setiGetDataComplete(IGetDataComplete iGetDataComplete) {
        this.iGetDataComplete = iGetDataComplete;
    }

    public interface IGetDataComplete {
        void onSuccess(List<Reply> list);

        void remove();
    }

}
