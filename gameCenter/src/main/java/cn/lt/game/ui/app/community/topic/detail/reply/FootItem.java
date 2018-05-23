package cn.lt.game.ui.app.community.topic.detail.reply;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import cn.lt.game.R;

/**
 * Created by zhengweijian on 15/8/31.
 */
public class FootItem implements IReplyView {

    public FootItem(){

    }
    @Override
    public boolean isClickable( int  author_id) {
        return false;
    }

    @Override
    public void onClick() {

    }

    @Override
    public int getType() {
        return ReplyAdapter.ReplyItemType.FOOT_ITEM_TYPE.type;
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
        return null;
    }

    @Override
    public View getView(Context context, View convertView, int position) {

        if(convertView == null ){
            convertView = LayoutInflater.from(context).inflate(R.layout.view_reply_footview,null);
        }
        return convertView;
    }
}
