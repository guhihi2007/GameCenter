package cn.lt.game.ui.app.community.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.ui.app.community.model.Comment;
import cn.lt.game.ui.app.community.topic.detail.reply.ReplyActivity;
import cn.lt.game.ui.app.community.topic.group.GroupTopicActivity;

/**
 * Created by Administrator on 2015/11/6.
 */
public class MyCommentItemView extends LinearLayout {


    private Context context;
    private LinearLayout ll_title;
    private TextView tv_groupName;
    private TextView tv_myCommentContent;
    private TextView tv_commentTopicTitle;
    private TextView tv_commentTime;

    public MyCommentItemView(Context context) {
        super(context);
        this.context = context;
        initView();
    }


    public MyCommentItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public MyCommentItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(context).inflate(R.layout.comment_my_item, this);
        ll_title = (LinearLayout) findViewById(R.id.ll_title);
        tv_groupName = (TextView) findViewById(R.id.tv_group_name_comment_topic);
        tv_myCommentContent = (TextView) findViewById(R.id.tv_myCommentContent);
        tv_commentTopicTitle = (TextView) findViewById(R.id.tv_commentTopicTitle);
        tv_commentTime = (TextView) findViewById(R.id.tv_topicTime);
    }

    public void setData(Comment comment) {
        tv_groupName.setText(comment.group_title);
        setCommentSummary(comment);
//        tv_myCommentContent.setText(comment.comment_summary);
        tv_commentTopicTitle.setText(comment.topic_title);
        tv_commentTime.setText(comment.published_at);
        setListener(comment);
    }

    private void setCommentSummary(Comment comment) {
        if (null!=comment.status && "verifying".equals(comment.status)){
            SpannableStringBuilder style = new SpannableStringBuilder();
            Drawable check_logo = getResources().getDrawable(R.mipmap.topic_check_logo);
            check_logo.setBounds(0, 0, check_logo.getIntrinsicWidth(), check_logo.getIntrinsicHeight());
            // 把标签图片插入到文本控件的最前面
            style.insert(0, comment.comment_summary);
            style.setSpan(new ImageSpan(check_logo, ImageSpan.ALIGN_BASELINE), 0, comment.comment_summary.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.append(comment.comment_summary);
            tv_myCommentContent.setText(style);
        } else {
            tv_myCommentContent.setText(comment.comment_summary);
        }
    }

    private void setListener(final Comment comment) {
        tv_commentTopicTitle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (comment != null) {
                    // 点击item需要跳转；
                    ActivityActionUtils.jumpToTopicDetail(context, comment.topic_id);
                }
            }
        });

        ll_title.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (comment != null) {
                    // 点击item需要跳转；
                    ActivityActionUtils.activity_Jump_Value(context, GroupTopicActivity.class, "group_id", comment.group_id);
                }
            }
        });

        tv_myCommentContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comment != null && "verifying".equals(comment.status)) {
                    ToastUtils.showToast(v.getContext(), "您查看的评论正在审核中！");
                }else{
                    context.startActivity(ReplyActivity.getIntent(context, comment, comment.topic_id, comment.author_id, comment.author_nickname, ReplyActivity.JumpType.Default));
                }
            }
        });
    }

}
