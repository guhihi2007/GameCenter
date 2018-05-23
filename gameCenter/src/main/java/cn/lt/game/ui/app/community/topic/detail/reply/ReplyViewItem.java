package cn.lt.game.ui.app.community.topic.detail.reply;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import cn.lt.game.ui.app.community.model.Reply;
import cn.lt.game.ui.app.community.topic.detail.ReplyView;

/**
 * Created by zhengweijian on 15/8/31.
 */
public class ReplyViewItem implements IReplyView  {
    private ReplyView.OnReplyerClickListener onReplyerClickListener;
    private Reply reply;

    public ReplyViewItem(Reply reply){

        this.reply = reply;
    }

    public ReplyViewItem(Reply reply, ReplyView.OnReplyerClickListener onReplyerClickListener){

        this.reply = reply;
        this.onReplyerClickListener = onReplyerClickListener;
    }
    @Override
    public boolean isClickable(int  author_id) {

//        return reply.author_id == author_id ? false :true;   //判断是不是自己

        return true;
    }

    @Override
    public void onClick() {

    }

    @Override
    public int getType() {
        return ReplyAdapter.ReplyItemType.REPLY_ITEM_TYPE.type;
    }



    @Override
    public void setReplyCount(int count) {

    }

    @Override
    public int getReplyCount() {
        return 0;
    }

    @Override
    public String getReplyName() {

        return reply.author_nickname;
    }

    @Override
    public View getView(Context context, View convertView, int position) {
        if(convertView == null ){
            convertView = new ReplyItemView(context);
        }

        updateTo(context,(ReplyItemView)convertView );

        return convertView;
    }

    private void updateTo(Context context,ReplyItemView view){

        String value = reply.reply_content;
        if(TextUtils.isEmpty(value)){
            value = reply.content;
        }
        view.setValue(context, reply.author_nickname,
                reply.acceptor_nickname, value, reply.author_id, reply.acceptor_id);
        view.setOnReplyerClickListener(onReplyerClickListener);
        view.setTime(reply);
    }
}
