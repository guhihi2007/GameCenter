package cn.lt.game.ui.app.community.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.view.ShareView;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.CheckUserRightsTool;
import cn.lt.game.ui.app.community.model.ShareBean;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.topic.TopicListAdapter;
import cn.lt.game.ui.app.community.topic.detail.GroupListActionbar;
import cn.lt.game.ui.app.community.topic.detail.Orderby;
import de.greenrobot.event.EventBus;

/***
 * 游戏详情、游戏攻略、社区模块分享插件
 */
public class ShareDialog extends Dialog implements View.OnClickListener, ShareView.shareViewOnclick {

    private int height;
    private int width;
    private ShareBean shareBean;
    private ShareView shareView;
    private ShareDialogType type;
    private Orderby orderbyType;
    private ItopdetailSortCallback sortCallback;
    private SharedPreferencesUtil SharedPreferences;
    private TopicDetail topic;
    private TextView collect;
    private TextView comment;
    private Handler handler;

    public enum ShareDialogType {
        Default(), TopicDetail(), TopicMore(), gameDetail()
    }

    public ShareDialog(Context context, ShareDialogType type) {
        super(context, R.style.updateInfoDialogStyle);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        height = dm.heightPixels;
        width = dm.widthPixels;
        this.type = type;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (type) {
            case Default:
            case gameDetail:
                setContentView(R.layout.sharedilog_default);
                initView();
                break;
            case TopicDetail:
                setContentView(R.layout.sharedialog_topdetail);
                initView();
                initTopDetailView();
                break;
            case TopicMore:
                setContentView(R.layout.sharedialog_topdetail);
                initView();
                initTopicMore();
                break;
            default:
                break;
        }

        initState(width, height);
    }

    private void initTopicDetailViewState(TextView collect) {
        SharedPreferences = new SharedPreferencesUtil(getContext());
        int type = SharedPreferences.getInteger(GroupListActionbar.NAME);
        initOrderBy(type);
        collect.setText(orderbyType.getChinese());
        collect.setTag(orderbyType);
    }

    private void initOrderBy(int type) {
        switch (type) {
            case 0:// 默认
                orderbyType = Orderby.ASC;
                break;
            case 1:
                orderbyType = Orderby.ASC;
                break;
            case 2:
                orderbyType = Orderby.DESC;
                break;

            default:
                break;
        }

    }

    private void initTopDetailView() {
        comment = (TextView) findViewById(R.id.shareDialog_comment_view);
        comment.setVisibility(View.VISIBLE);
        initCollect();
        comment.setOnClickListener(this);
        // *设置排序文字和Tag*/
        initTopicDetailViewState(comment);

    }


    private void initCollect() {
        collect = (TextView) findViewById(R.id.shareDialog_collect_view);
        // 收藏按键点击事件
        if (collect != null) {
            if (topic.is_collected) {
                setButtonToCancelCollect();
            } else {
                setButtonToCollect();
            }
        }
    }

    private void initTopicMore() {
        initCollect();
        handler = new Handler(Looper.getMainLooper());
    }

    private void setButtonToCollect() {
        collect.setText("收藏话题");
        collect.setOnClickListener(new CollectClickListener());
    }

    private void setButtonToCancelCollect() {
        collect.setText("取消收藏");
        collect.setOnClickListener(new CancelCollectClickListener());
    }

    private void initView() {
        FrameLayout root = (FrameLayout) findViewById(R.id.shareDialog_root);
        root.setOnClickListener(this);
        shareView = (ShareView) findViewById(R.id.shareDialog_share_view);
        shareView.setShareBean(shareBean);
        shareView.setOnclick(this);

    }

    private void initState(int width, int height) {

        WindowManager.LayoutParams p = this.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = width;
        p.height = height;
        this.getWindow().setAttributes(p);
    }

    public void setShareBean(ShareBean shareBean) {
        switch (type) {
            case TopicDetail:
            case TopicMore:
                shareBean.setTitle("天天游戏中心社区");
                break;
            case Default:
                shareBean.setTitle("天天游戏中心");
                break;
            default:
                break;
        }

        this.shareBean = shareBean;
    }

    @Override
    public void shareOnClick(View view) {
        dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shareDialog_root:
                dismiss();
                break;

            case R.id.shareDialog_comment_view:
                SharedPreferences.Editor editor = SharedPreferences.getEditor();

                if (view.getTag() == Orderby.ASC) {
                    orderbyType = Orderby.DESC;
                    view.setTag(orderbyType);
                    ((TextView) view).setText(orderbyType.getChinese());
                    editor.putInt(GroupListActionbar.NAME, 2);

                } else {
                    orderbyType = Orderby.ASC;
                    view.setTag(orderbyType);
                    ((TextView) view).setText(Orderby.ASC.getChinese());
                    editor.putInt(GroupListActionbar.NAME, 1);
                }

                editor.commit();

                if (sortCallback != null) {
                    sortCallback.sortCallBack(orderbyType);
                }

                dismiss();
                break;
            default:
                break;
        }

    }

    public interface ItopdetailSortCallback {
        void sortCallBack(Orderby orderby);
    }

    public void setSortCallback(ItopdetailSortCallback sortCallback) {
        this.sortCallback = sortCallback;
    }

    public void setTopicDetail(TopicDetail topicDetail) {
        this.topic = topicDetail;
        ShareBean sb = new ShareBean();
        sb.setText(topic.topic_title);
        sb.setTitleurl(topic.share_link);
        setShareBean(sb);
        if(shareView != null ){
            shareView.setShareBean(sb);
        }

        System.out.println("setTopic Detail");

        initCollect();
    }

    private class CollectClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            /***
             * 先判断用户是否登陆
             */
            if (!CheckUserRightsTool.instance().isLogin()) {
                CheckUserRightsTool.instance().gotoLogin(v.getContext());
            } else {
                doClick();
            }

        }

        private void doClick() {
            // 发送收藏请求
            Net.instance().executePost(Host.HostType.FORUM_HOST, Uri.getCollectTopicUri(topic.topic_id), new WebCallBackToString() {

                @Override
                public void onSuccess(String result) {
                    ToastUtils.showToast(getContext(), "收藏成功");
                    topic.is_collected = true;
                    setButtonToCancelCollect();

                }

                @Override
                public void onFailure(int statusCode, Throwable error) {
                    ToastUtils.showToast(getContext(), "收藏失败—— " + error.getMessage());
                }

            });

            // 收回遮罩层
            dismiss();
        }
    }

    private class CancelCollectClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            /***
             * 先判断用户是否登陆
             */
            if (!CheckUserRightsTool.instance().isLogin()) {
                CheckUserRightsTool.instance().gotoLogin(v.getContext());
            } else {
                doClick();
            }

        }

        private void doClick() {
            // 发送收藏请求
            Net.instance().executeDelete(Host.HostType.FORUM_HOST, Uri.getCancelCollectUri(topic.topic_id), new WebCallBackToString() {

                @Override
                public void onSuccess(String result) {
                    ToastUtils.showToast(getContext(), "取消收藏");
                    topic.is_collected = false;
                    setButtonToCollect();
                    EventBus.getDefault().post(new CollectEventBean("refreshCollectData"));
                    if (handler != null) {
                        Message msg = handler.obtainMessage();
                        msg.what = TopicListAdapter.CANCEL_COLLECT_TOPIC;
                        msg.obj = topic;
                        handler.sendMessage(msg);
                    }
                }

                @Override
                public void onFailure(int statusCode, Throwable error) {
                    ToastUtils.showToast(getContext(), "取消收藏失败—— " + error.getMessage());
                }

            });

            // 收回遮罩层
            dismiss();
        }

    }
    public static class CollectEventBean {

        public CollectEventBean(String s) {
            this.tag = s;
        }

        public String tag = "";
    }
}
