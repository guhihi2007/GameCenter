package cn.lt.game.ui.app.community;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.lt.game.R;
import cn.lt.game.db.service.DraftService;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.web.WebCallBackBase;
import cn.lt.game.net.Host.HostType;
import cn.lt.game.net.Net;
import cn.lt.game.ui.app.community.model.SendReplyBean;
import cn.lt.game.ui.notification.LTNotificationManager;

//发送回复的工具类
public class SendReplyTools {
    private volatile static SendReplyTools mInstance = null;

    public static SendReplyTools instance() {
        if (mInstance == null) {
            synchronized (SendCommentTools.class) {
                if (mInstance == null) {
                    mInstance = new SendReplyTools();
                }
            }
        }
        return mInstance;
    }

    //发送回复，无论成功是否调用EventTools发送通知告诉相应界面是否发送成功等等信息，其他页面好做相应的业务逻辑处理
    public void sendReply(final Context context, final SendReplyBean sb) {
        HashMap<String, String> ha = new HashMap<String, String>();
        ha.put("acceptor_id", sb.getAcceptorId() + "");
        ha.put("acceptor_nickname", sb.getAcceptorNickname());
        ha.put("reply_content", sb.getContent().replaceAll("\n", "\040"));

        Net.instance().executePost(HostType.FORUM_HOST, cn.lt.game.net.Uri.getrReplyCreateUri(sb.getTopicId(), sb.getCommentId()), ha, new WebCallBackBase() {
                    @Override
                    public void route(String result) {
                        DraftsJudgeTools.instance().remove(sb.getTag());
                        try {
                            JSONObject jb = new JSONObject(result);
                            int status = jb.getInt("status");
                            String msg = jb.getString("message");
                            if (status == 1) {
                                DraftService.getSingleton(context).deleteByTag(sb.getTag());
                                EventTools.instance().send(EventTools.REPLY_TAG, true, sb.getGroupId(), sb.getTopicId());
                                EventTools.instance().send(EventTools.DRAFTS_TAG, true, sb.getGroupId(), sb.getTopicId());
                                notifyFailed(context, "发送成功", R.mipmap.ic_publish_success);
                                LogUtils.i("zzzz","回复成功！");
                            }else if(800==status){
                                LogUtils.i("zzzz","回复有敏感词！");
                                DraftService.getSingleton(context).deleteByTag(sb.getTag());
                                EventTools.instance().send(EventTools.REPLY_TAG, true, sb.getGroupId(), sb.getTopicId());
                                EventTools.instance().send(EventTools.DRAFTS_TAG, true, sb.getGroupId(), sb.getTopicId());
                            } else {
                                LogUtils.i("zzzz","回复失败！");
                                DraftService.getSingleton(context).update(sb.getTag());
                                EventTools.instance().send(EventTools.REPLY_TAG, false, sb.getGroupId(), sb.getTopicId());
                                EventTools.instance().send(EventTools.DRAFTS_TAG, false, sb.getGroupId(), sb.getTopicId());
                                notifyFailed(context, "发送失败", R.mipmap.ic_publish_failed);
                            }
                            ToastUtils.showToast(context,msg);
                        } catch (JSONException e) {
                            DraftService.getSingleton(context).update(sb.getTag());
                            EventTools.instance().send(EventTools.REPLY_TAG, false, sb.getGroupId(), sb.getTopicId());
                            EventTools.instance().send(EventTools.DRAFTS_TAG, false, sb.getGroupId(), sb.getTopicId());
                            notifyFailed(context, "发送失败", R.mipmap.ic_publish_failed);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error) {
                        DraftsJudgeTools.instance().remove(sb.getTag());
                        DraftService.getSingleton(context).update(sb.getTag());
                        EventTools.instance().send(EventTools.REPLY_TAG, false, sb.getGroupId(), sb.getTopicId());
                        EventTools.instance().send(EventTools.DRAFTS_TAG, false, sb.getGroupId(), sb.getTopicId());
                        notifyFailed(context, "发送失败", R.mipmap.ic_publish_failed);
                    }
                });

    }

    private void notifyFailed(Context context, String content, int resId) {
        LTNotificationManager.getinstance().publishTopicMsg(context, content, resId);
    }
}
