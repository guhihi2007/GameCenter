package cn.lt.game.ui.app.community.topic.detail.reply;

import android.content.Context;
import android.view.View;

import cn.lt.game.ui.app.community.model.Comment;

/**
 * Created by zhengweijian on 15/8/31.
 */
public class HeaderItem implements IReplyView {
    private Comment comment;

    public HeaderItem(Comment comment){
        this.comment = comment;
    }

    @Override
    public boolean isClickable( int  author_id) {
//        return comment.author_id == author_id ? false :true;

        return true;
    }

    @Override
    public void onClick() {

    }

    @Override
    public int getType() {
        return ReplyAdapter.ReplyItemType.HEADER_ITEM_TYPE.type;
    }


    @Override
    public void setReplyCount(int count) {
        comment.reply_count = count;
    }

    @Override
    public int getReplyCount() {
        return comment.reply_count;
    }

    @Override
    public String getReplyName() {

        return comment.author_nickname;
    }

    public int getAuthorId(){
        return comment.author_id;
    }


    @Override
    public View getView(Context context, View convertView, int position) {
        if(convertView == null ){
            convertView = new ReplyHeaderView(context);
        }

        UpdateTo(((ReplyHeaderView)convertView));

        return convertView;
    }

    private void UpdateTo(ReplyHeaderView view){
        view.setComment(comment);
    }

}
