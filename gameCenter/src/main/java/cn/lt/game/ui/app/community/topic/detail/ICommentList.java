package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.view.View;

import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.topic.detail.CommentView.replyOnclickPositionListener;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

public interface ICommentList {

	// 是否可以点击
    boolean isClickable();

	// 点击事件
    void onClick();

	// 返回类型
    int getType();

	// 数据
    Object getData();

	View createView(Context context, View convertView,
                    int position, TopicDetail info,
                    replyOnclickPositionListener positionListener,
                    UserBaseInfo UserBaseInfo);

	View getView();

	void setLineVisibility(int visibility);

}
