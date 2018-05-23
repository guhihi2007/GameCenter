package cn.lt.game.ui.app.community.topic.detail.reply;

import android.content.Context;
import android.view.View;

/**
 * Created by zhengweijian on 15/8/31.
 */
public interface IReplyView {

    boolean isClickable(int author_id);

    void onClick();

    int getType();

    void setReplyCount(int count);

    int getReplyCount();

    String getReplyName();

    View getView(Context context, View convertView,int position);
}
