package cn.lt.game.ui.app.sidebar.feedback.model;

/**
 * Created by zhengweijian on 15/8/24.
 */
public class ChatInfo implements  Comparable<ChatInfo>{
    @Override
    public int compareTo(ChatInfo chatInfo) {
        return this.getId().compareTo(chatInfo.getId());
    }

    /* 发送状态 */
    public enum Status {
        sendIng(),failed(),success()
    }

    /*消息类型*/
    public enum MsgType{
        time(),msg(),hint(),img()
    }

    /*身份类型*/
    public enum Type {
       System(), User() ,Admin(),hint()
    }

    public int id;
    public String content;
    public String created_at;
    public String imageUrl;
    public String identifyUser;
    public Status status = Status.sendIng;
    public int progress = 0;
    public MsgType msgType = MsgType.msg;
    public String path;
    public Type identityType = Type.User;
    public long time;
    public String thumbUrl;


    public Integer getId() {
        return id;
    }
}
