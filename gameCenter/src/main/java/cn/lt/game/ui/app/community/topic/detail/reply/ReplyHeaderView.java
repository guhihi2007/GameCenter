package cn.lt.game.ui.app.community.topic.detail.reply;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.html.HtmlUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.view.RoundImageView;
import cn.lt.game.ui.app.community.model.Comment;
import cn.lt.game.ui.app.community.personalpage.PersonalActivity;

/**
 * Created by zhengweijian on 15/8/31.
 * 评论详情头部
 */
public class ReplyHeaderView extends RelativeLayout implements View.OnClickListener {
    private TextView name;
    private RoundImageView iconView;
    private TextView time ;
    private ImageView levlelView;
    private TextView replyText;
    private TextView hintLayout;

    private int userId;
    private TextView commentContent;

    public ReplyHeaderView(Context context) {
        this(context, null);
    }

    public ReplyHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReplyHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.view_replyheader, this);
        initView();
    }

    private void initView() {
        name = (TextView) findViewById(R.id.reply_listItem_headerName);
        iconView = (RoundImageView) findViewById(R.id.reply_listItem_headerIcon);
        time = (TextView) findViewById(R.id.reply_listItem_time);
        levlelView = (ImageView) findViewById(R.id.reply_listItem_levelView);
        replyText = (TextView) findViewById(R.id.reply_listItem_headerReply);
        hintLayout = (TextView) findViewById(R.id.reply_listItem_headerHint);
        iconView.setOnClickListener(this);
        name.setOnClickListener(this);
        commentContent = (TextView) findViewById(R.id.reply_listItem_content);

    }

    public void setComment(Comment comment){
        name.setText(comment.author_nickname);
//        ImageLoader.getInstance().display(comment.author_icon, iconView, new SimpleImageLoadingListener(iconView, R.mipmap.user_center_avatar));
        ImageloaderUtil.loadUserHead(getContext(),comment.author_icon, iconView);
        time.setText(comment.published_at);
        levlelView.setImageLevel(comment.user_level);
        replyText.setText("回复" + comment.reply_count);
        HtmlUtils.supportHtmlWithNet(commentContent, comment.comment_content, true);//设置评论内容（HTML文本）

        // 屏蔽此控件抢夺focus的控件权限，解决ListView的onitemClick不响应的问题
        commentContent.setFocusable(false);
        if(comment.reply_count> 0 ){
            hintLayout.setVisibility(VISIBLE);
        }else{
            hintLayout.setVisibility(GONE);
        }
        userId = comment.author_id;
    }

    @Override
    public void onClick(View v) {
        ActivityActionUtils.activity_Jump_Value(v.getContext(), PersonalActivity.class, "userId", userId);
    }
}
