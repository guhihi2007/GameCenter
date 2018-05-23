package cn.lt.game.ui.app.community;

import java.util.ArrayList;

import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.html.HtmlUtils;
import cn.lt.game.ui.app.community.model.PrivateMessageDetail;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;

/**
 * Created by wenchao on 2015/11/26.
 */
public class MyPrivateMessageItem implements Comparable {

    public int id;
    /**
     * 消息类型
     * {@link MyPrivateMessageDetailActivity }
     * #MyPrivateMessageDetailActivity.MSG_LEFT_TEXT
     */
    public int messageType;

    /**
     * 若type为文本，则是文本内容
     */
    public String content;

    /**
     * 本地图片路劲
     */
    public String locaImage;

    /**
     * 网络图片路劲
     */
    public String remoteImage;

    /***/
    public String remoteBigImage;

    /**
     * 头像
     */
    public String headIcon;
    /**
     * 发表时间
     */
    public String time;
    /**
     * 发送状态
     * {@link MyPrivateMessageDetailActivity }
     */
    public int sendStatus = MyPrivateMessageDetailActivity.SEND_SUCCESS;

    /**
     * 若发送图片，则包含此进度
     */
    public int progress;

    /**
     * 来源，web-网页版，app-手机版
     */
    public String source;

    public boolean needShowTime;

    public MyPrivateMessageItem() {

    }

    public MyPrivateMessageItem(PrivateMessageDetail detail, String friendHeadIcon) {
        int myUserId = UserInfoManager.instance().getUserInfo().getId();
        id = detail.letter_id;

        //消息是否自己发出的
        boolean isMyself;
        if (detail.user_id == myUserId) {
            isMyself = true;
            headIcon = UserInfoManager.instance().getUserInfo().getAvatar();
        } else {
            isMyself = false;
            headIcon = friendHeadIcon;
        }
        //是否图片消息或者文本消息
        boolean           isImageMsg;
        ArrayList<String> imageList = HtmlUtils.getImagePathList(detail.content);
        if (imageList.size() > 0) {
            remoteImage = imageList.get(0);
            isImageMsg = true;
        } else {
            content = detail.content;
            isImageMsg = false;
        }
        if (isMyself && isImageMsg)
            messageType = MyPrivateMessageDetailActivity.MSG_RIGHT_IMAGE;
        else if (isMyself && !isImageMsg)
            messageType = MyPrivateMessageDetailActivity.MSG_RIGHT_TEXT;
        else if (!isMyself && isImageMsg)
            messageType = MyPrivateMessageDetailActivity.MSG_LEFT_IMAGE;
        else if (!isMyself && !isImageMsg) {
            messageType = MyPrivateMessageDetailActivity.MSG_LEFT_TEXT;
        }
        time = detail.created_at;
        source = detail.source;
    }

    @Override
    public int compareTo(Object another) {
        MyPrivateMessageItem other     = (MyPrivateMessageItem) another;
        long                 otherTime = TimeUtils.getDateToStringHaveHour(other.time).getTime();
        long                 thisTime  = TimeUtils.getDateToStringHaveHour(this.time).getTime();
        return thisTime - otherTime > 0 ? 1 : -1;
    }
}
