package cn.lt.game.ui.app.community.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import cn.lt.game.R;
import cn.lt.game.ui.app.community.model.TopicDetail;

/**
 * 更多按键
 */
public class MoreButton extends ImageButton {

    private ShareDialog dialog;
    private  TopicType topicType;

    public  enum TopicType{
        Default_Topic(),MyCollect_Topic(),Hot_Topic(),My_Topic()
    }

    public MoreButton(Context context) {
        super(context);
        init(context);
    }

    public MoreButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MoreButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(0);
        setImageResource(R.mipmap.btn_community_more);
    }


    public void setData(TopicDetail topic, Handler handler) {
        initDialog();
        dialog.setTopicDetail(topic);
    }

    public void setData(TopicDetail topic,TopicType topicType) {
        this.topicType = topicType;
        initDialog();
        dialog.setTopicDetail(topic);
        if (null != topic.status && "verifying".equals(topic.status)) {
            this.setVisibility(View.GONE);
        }

    }
    private void initDialog() {
        dialog = null;
        switch (topicType){
            case My_Topic:
                dialog = new ShareDialog(getContext(), ShareDialog.ShareDialogType.Default);
                break;
            case Default_Topic:
                dialog = new ShareDialog(getContext(), ShareDialog.ShareDialogType.TopicMore);
                break;
        }
        setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.show();
            }
        });
    }
}
