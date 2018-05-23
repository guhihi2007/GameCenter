package cn.lt.game.ui.app.community.widget;

import android.content.Context;
import android.util.AttributeSet;

import java.util.HashMap;
import java.util.Map;

import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/12/17.
 */
public class LikeTextViewOfTopicDerail extends LikeTextView {
    public LikeTextViewOfTopicDerail(Context context) {
        super(context);
    }

    public LikeTextViewOfTopicDerail(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LikeTextViewOfTopicDerail(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // 执行点赞操作
    protected void execLikeAction(final Context context) {
        if (isSendingRequest) {
            return;
        }
        isSendingRequest = true;
        // 构造点赞请求参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", data.getLikeType().toString());
        params.put("id", Integer.toString(data.getTopicId()));

        // 发送点赞请求给后台
        Net.instance().executePost(Host.HostType.FORUM_HOST,
                Uri.getTopicLikeUri(data.getTopicId()), params,
                new WebCallBackToString() {

                    @Override
                    public void onFailure(int statusCode, Throwable error) {
                        ToastUtils.showToast(context, "点赞失败——" + error.getMessage());
                        isSendingRequest = false;
                    }

                    @Override
                    public void onSuccess(String result) {
                        // 设置为已点击状态
                        data.setLiked(true);

                        // 点赞数目+1
                        increaseLikeNum();

                        // 设置图标为已点击状态
                        setPressedView();

                        ToastUtils.showToast(context, "赞的漂亮");

                        EventBus.getDefault().post(data);

                    }
                });
    }

    // 执行取消点赞操作
    protected void execCancelLikeAction(final Context context) {
        if (isSendingRequest) {
            return;
        }
        isSendingRequest = true;
        // 发送取消点赞消息
        Net.instance().executeDelete(Host.HostType.FORUM_HOST,
                Uri.getTopicCancelLikeUri(data.getTopicId()),
                new WebCallBackToString() {

                    @Override
                    public void onFailure(int statusCode, Throwable error) {
                        ToastUtils.showToast(context,
                                "取消点赞失败——" + error.getMessage());
                        isSendingRequest = false;
                    }

                    @Override
                    public void onSuccess(String result) {
                        // 设置为未点击状态
                        data.setLiked(false);
                        // 点赞数目-1
                        decreaseLikeNum();

                        // 设置图标为未点击状态
                        setNotPressedView();

                        ToastUtils.showToast(context, "取消点赞");
                        EventBus.getDefault().post(data);

                    }
                });
    }

    public void setIsSendingRequest(boolean isSendingRequest) {
        this.isSendingRequest = isSendingRequest;
    }
}
