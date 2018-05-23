package cn.lt.game.ui.app.community.model;

import java.io.Serializable;
import java.util.ArrayList;

//草稿箱数据Bean
public class DraftBean implements Serializable {
    private int id;
    private String tag = "0";// 以时间作为唯一标识
    private int type;// 发表类型 0：话题，1.评论，2.回复
    private int state;// 发表状态 0：发送失败，1：发送中，2，未发送
    private int group_id; // 小组ID
    private int topic_Id;// 话题ID
    private String topic_title; // 话题标题
    private String topic_content; // 话题内容
    private ArrayList<String> topic_paths = new ArrayList<String>();// 话题图片路径
    private String category_id; // 话题类型
    private int comment_id;// 评论ID
    private int acceptor_id;// 接受者ID
    private String comment_content;// 评论内容
    private ArrayList<String> comment_paths = new ArrayList<String>();// 评论图片
    private String acceptorNickname;
    private String groupTitle;
    private String reply_content;
    private ArrayList<Category> categoryList = new ArrayList<Category>();
    private String local_topicPaths = "-1";
    private String local_topicContent = "-1";
    private String local_commentContent = "-1";
    private String local_commentPaths = "-1";



    public String getLocal_commentPaths() {
        return local_commentPaths;
    }

    public void setLocal_commentPaths(String local_commentPaths) {
        this.local_commentPaths = local_commentPaths;
    }

    private String local_replyContent = "-1";

    public String getLocal_commentContent() {
        return local_commentContent;
    }

    public void setLocal_commentContent(String local_commentContent) {
        this.local_commentContent = local_commentContent;
    }

    public String getLocal_replyContent() {
        return local_replyContent;
    }

    public void setLocal_replyContent(String local_replyContent) {
        this.local_replyContent = local_replyContent;
    }

    public String getLocal_topicPaths() {
        return local_topicPaths;
    }

    public void setLocal_topicPaths(String local_topicPaths) {
        this.local_topicPaths = local_topicPaths;
    }

    public String getLocal_topicContent() {
        return local_topicContent;
    }

    public void setLocal_topicContent(String local_topicContent) {
        this.local_topicContent = local_topicContent;
    }

    public ArrayList<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(ArrayList<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReply_content() {
        return reply_content;
    }

    public void setReply_content(String reply_content) {
        this.reply_content = reply_content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public int getTopic_Id() {
        return topic_Id;
    }

    public void setTopic_Id(int topic_Id) {
        this.topic_Id = topic_Id;
    }

    public String getTopic_title() {
        return topic_title;
    }

    public void setTopic_title(String topic_title) {
        this.topic_title = topic_title;
    }

    public String getTopic_content() {
        return topic_content;
    }

    public void setTopic_content(String topic_content) {
        this.topic_content = topic_content;
    }

    public ArrayList<String> getTopic_paths() {
        return topic_paths;
    }

    public void setTopic_paths(ArrayList<String> topic_paths) {
        this.topic_paths = topic_paths;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public int getAcceptor_id() {
        return acceptor_id;
    }

    public void setAcceptor_id(int acceptor_id) {
        this.acceptor_id = acceptor_id;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public ArrayList<String> getComment_paths() {
        return comment_paths;
    }

    public void setComment_paths(ArrayList<String> comment_paths) {
        this.comment_paths = comment_paths;
    }

    public String getAcceptorNickname() {
        return acceptorNickname;
    }

    public void setAcceptorNickname(String acceptorNickname) {
        this.acceptorNickname = acceptorNickname;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public boolean isAutoJump() {
        return isAutoJump;
    }

    public void setAutoJump(boolean isAutoJump) {
        this.isAutoJump = isAutoJump;
    }

    private boolean isAutoJump;

}
