package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.view.View;

import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.ui.app.community.model.Comment;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

/**
 * Created by zhengweijian on 15/8/30.
 */
public class AdvItem implements ICommentList {

    public static  final int ADV_TYPE = 7;

    private Context context;
    private Comment comment;
    private advertisementView advView;

    public  AdvItem(Comment comment){
        this.comment = comment;

    }
    @Override
    public boolean isClickable() {

        return true;
    }

    @Override
    public void onClick() {
        ActivityActionUtils.jumpToByUrl(context, comment.title, comment.link);
    }

    @Override
    public int getType() {
        return ADV_TYPE;
    }

    @Override
    public Object getData() {
        return comment;
    }

    @Override
    public View createView(Context context, View convertView, int position, TopicDetail info, CommentView.replyOnclickPositionListener positionListener, UserBaseInfo UserBaseInfo) {

        this.context = context;
        if(convertView == null ){
            convertView = new advertisementView(context);
        }

        ((advertisementView)convertView).setComment(comment);

        advView = (advertisementView) convertView;

        return convertView;
    }

    @Override
    public View getView() {
        return advView;
    }

    @Override
    public void setLineVisibility(int visibility) {

    }
}
