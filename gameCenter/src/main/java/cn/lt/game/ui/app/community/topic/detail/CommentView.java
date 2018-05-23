package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.html.HtmlUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.view.RoundImageView;
import cn.lt.game.net.NetIniCallBack;
import cn.lt.game.ui.app.community.CheckUserRightsTool;
import cn.lt.game.ui.app.community.model.Comment;
import cn.lt.game.ui.app.community.model.Reply;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.personalpage.PersonalActivity;
import cn.lt.game.ui.app.community.topic.detail.ReplyALLLoadingView.IGetDataComplete;
import cn.lt.game.ui.app.community.topic.detail.ReplyALLLoadingView.TagEnum;
import cn.lt.game.ui.app.community.topic.detail.reply.ReplyActivity;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;

public class CommentView extends RelativeLayout implements OnClickListener{
    public final static int DEFAULTNUM = 3;
    private final static int ALLREPLYTEXTVIEWID = 0xff;
    private View lineView;
    private RoundImageView icon;
    private ImageView levleView;
    private TextView floors;
    private TextView name;
    private TextView time;
    private TextView valueText;
    private TextView replyBt;
    private LinearLayout replyLayout;
    private Context context;
    private int reply_count;
    private int commentId;
    private int position;
    private ReplyALLLoadingView replyAllView;
    private IGetDataComplete iGetDataComplete;
    private replyOnclickPositionListener onclickPositionListener;
    private Comment comment;
    private int userId;
    private TopicDetail info;


    public CommentView(Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.group_comment_item, this);
        this.setBackgroundResource(R.drawable.left_right_selector);
        int padding = (int) context.getResources().getDimension(
                R.dimen.inInterval);
        this.setPadding(padding, padding, padding, 0);
//
        initView();
    }

    private void initView() {
        icon = (RoundImageView) findViewById(R.id.group_comment_item_img);
        floors = (TextView) findViewById(R.id.group_comment_item_floors);
        name = (TextView) findViewById(R.id.group_comment_item_name);
        time = (TextView) findViewById(R.id.group_comment_item_time);
        valueText = (TextView) findViewById(R.id.group_comment_item_value);
        replyBt = (TextView) findViewById(R.id.group_comment_item_replyBt);
        levleView = (ImageView) findViewById(R.id.group_comment_item_levelView);
        replyLayout = (LinearLayout) findViewById(R.id.group_comment_item_replyGroup);

        lineView = findViewById(R.id.group_comment_item_line);

        replyBt.setOnClickListener(this);

    }

    public void upDate(final Comment comment, int position, int UserId,
                       TopicDetail info) {
        this.position = position;
        this.comment = comment;
        this.userId = UserId;
        this.info = info;
        reply_count = comment.reply_count;
        commentId = comment.comment_id;
        replyBt.setText("回复 " + reply_count);
        name.setText(comment.author_nickname);
        time.setText(TimeUtils.curtimeDifference(comment.published_at));
        levleView.setImageLevel(comment.user_level);
        HtmlUtils.supportCommentHtmlWithNet(valueText, comment.comment_content, true);
        floors.setText(comment.floor + " 楼");

        initReplyLayout(comment.replies);
//        ImageLoader.getInstance().displayLogo(comment.author_icon, icon);
        ImageloaderUtil.loadLTLogo(getContext(),comment.author_icon, icon);

        // 跳转到“ta的主页”
        icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityActionUtils.activity_Jump_Value(v.getContext(), PersonalActivity.class, "userId", comment.author_id);
            }
        });
        name.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityActionUtils.activity_Jump_Value(v.getContext(), PersonalActivity.class, "userId", comment.author_id);
            }
        });

    }

    private void initReplyLayout(List<Reply> replies) {
        if (replies == null || replies.size() == 0) {
            replyLayout.setVisibility(View.GONE);
            return;
        } else {
            replyLayout.setVisibility(View.VISIBLE);
        }

        replyLayout.removeAllViews();

        addReplyTextView(replyLayout, replies);

    }

    // 话题详情内，评论view里的回复view
    private void addReplyTextView(LinearLayout ViewGroup,
                                  final List<Reply> replies) {
        int padding = (int) getResources().getDimension(R.dimen.inInterval);
        for (int i = 0; i < replies.size(); i++) {
            Reply reply = replies.get(i);
            ReplyView replyView = new ReplyView(context);
            replyView.setReplyMaxLines(5);// 设置回复内容最多显示的行数
            replyView.setisDisplayOnTopicDetail();// 标记此回复view是显示在话题详情页面上的

            replyView.setBackgroundResource(R.drawable.reply_backgroud_color);

            if (i == replies.size() - 1) {
                replyView.setPadding(padding, padding, padding, padding);
            } else {
                replyView.setPadding(padding, padding, padding, 0);
            }

            replyView.setValue(context, reply.author_nickname,
                    reply.acceptor_nickname, reply.reply_content,
                    reply.author_id, reply.acceptor_id);
            replyView.setTime(reply);
            replyView.setTag(i);

            replyView.setOnReplyerClickListener(new ReplyView.OnReplyerClickListener() {

                @Override
                public void OnReplyerNameClick(String replyerName, int replyerId) {
                    replyOnClick(replyerName, replyerId);
                    Log.i("replyLog", "CommentView~~~OnReplyerNameClick~~~点我啦！");

                }

                @Override
                public void OnAcceptorNameClick(String acceptorName, int acceptorId) {
                    replyOnClick(acceptorName, acceptorId);
                    Log.i("replyLog", "CommentView~~~OnAcceptorNameClick~~~被点啦！");
                }
            });


            ViewGroup.addView(replyView);
        }

        addReplyTextViewFoot(ViewGroup, reply_count);
    }

    // 查看全部回复按钮
    private void addReplyTextViewFoot(LinearLayout ViewGroup, int size) {

        if (size > DEFAULTNUM) {

            replyAllView = new ReplyALLLoadingView(context);
            replyAllView.setTag(TagEnum.Default);
            replyAllView.setiGetDataComplete(iGetDataComplete);
            replyAllView.setComment(comment);
            replyAllView.setTopicId(info.topic_id);
            replyAllView.setUserId(userId);
            replyAllView.setTopicId(info.topic_id);
            replyAllView.setGroup_id(info.group_id);
            ViewGroup.addView(replyAllView);
        }
    }


    private void replyOnClick(final String clickReplyName, final int clickReplyId) {
        CheckUserRightsTool.instance().checkUserRights(context, false,
                info.group_id, new NetIniCallBack() {

                    @Override
                    public void callback(int code) {
                        if(code != -1) {

                            if (userId != -1) {

                                ((BaseActivity) context).startActivityForResult(
                                        ReplyActivity.getIntent(context, comment, info.topic_id, comment.author_id, comment.author_nickname, ReplyActivity.JumpType.ClickReply, clickReplyId, clickReplyName), 1);

                            } else {

                                UserInfoManager.instance().isLoginHaveCall(
                                        context, true);
                            }
                        }
                    }
                });

    }


    public void hideline(int Visibility) {
        lineView.setVisibility(Visibility);
        if (Visibility == View.VISIBLE) {
            setBackgroundResource(R.drawable.left_right_selector);
        } else {
            setBackground(context.getResources().getDrawable(
                    R.drawable.left_right_bottom_selector));
        }

        int padding = (int) context.getResources().getDimension(
                R.dimen.inInterval);
        setPadding(padding, padding, padding, 0);
    }


    public replyOnclickPositionListener getOnclickPositionListener() {
        return onclickPositionListener;
    }

    public void setOnclickPositionListener(
            replyOnclickPositionListener onclickPositionListener) {
        this.onclickPositionListener = onclickPositionListener;
    }

    public IGetDataComplete getiGetDataComplete() {
        return iGetDataComplete;
    }

    public void setiGetDataComplete(IGetDataComplete iGetDataComplete) {
        this.iGetDataComplete = iGetDataComplete;
    }

    public interface replyOnclickPositionListener {
        void positionListener(int position);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.group_comment_item_replyBt:// 点击“回复”绿色图标时
                if (onclickPositionListener != null) {
                    onclickPositionListener.positionListener(position);
                }
                CheckUserRightsTool.instance().checkUserRights(context, false,
                        info.group_id, new NetIniCallBack() {

                            @Override
                            public void callback(int code) {

                                if (code != -1) {

                                    if (userId != -1) {
                                        ((BaseActivity) v.getContext()).startActivityForResult(
                                                ReplyActivity.getIntent(context, comment, info.topic_id, comment.author_id, comment.author_nickname, ReplyActivity.JumpType.SetName), 1);

                                    } else {
                                        UserInfoManager.instance().isLoginHaveCall(
                                                v.getContext(), true);
                                    }

                                }

                            }
                        });

                break;
            case ALLREPLYTEXTVIEWID:
                break;

            default:
                break;
        }
    }

}
