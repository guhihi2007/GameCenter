package cn.lt.game.ui.app.community.topic.detail.reply;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import cn.lt.game.R;
import cn.lt.game.ui.app.community.model.Reply;
import cn.lt.game.ui.app.community.topic.detail.ReplyView;

/**
 * Created by zhengweijian on 15/9/1.
 */
public class ReplyItemView extends FrameLayout {
    private ReplyView replyView;

    public ReplyItemView(Context context) {
        this(context, null);
    }

    public ReplyItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReplyItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.view_replyitem, this);
        setBackgroundColor(getResources().getColor(R.color.white));
        int padding = (int) getResources().getDimension(R.dimen.inInterval);
        setPadding(padding, padding, padding,0);
        initView();
    }

    private void initView() {
        replyView = (ReplyView) findViewById(R.id.reply_listItem_replyView);
    }

    public void setTime(Reply reply) {
        replyView.setTime(reply);

    }

    public void setValue(Context context, String replyTo, String replyAt,
                         String value,int replyerId, int acceptorId) {
        replyView.setValue(context, replyTo, replyAt, value, replyerId, acceptorId);
    }

    public void setOnReplyerClickListener(ReplyView.OnReplyerClickListener onReplyerClickListener) {
        replyView.setOnReplyerClickListener(onReplyerClickListener);
    }

}