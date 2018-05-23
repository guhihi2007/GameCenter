package cn.lt.game.ui.app.community.model;

/**
 * Created by tiantian on 2015/11/17.
 * TA主页实体
 * 通途：TA的主页、我的社区
 */
public class OthersPage extends User{
    private String background_img;//TA的主页封面图片地址
    private String user_summary;//用户签名
    private int user_gold;//用户金币
    private int topic_count;//用户话题数
    private int comment_count;//用户评论数
    private int group_count;//用户加入的小组数
    private int relation;//与此⽤户的关系，0-未关注，1-已 关注，2-互相关注

    private int user_upgrade_percent;//⽤户距离升级的经验百分⽐
    private int has_new_letter;//是否有未读的私信
    private int has_new_notice;//是否有新的社区通知


    public String getBackground_img() {
        return background_img;
    }

    public void setBackground_img(String background_img) {
        this.background_img = background_img;
    }

    public String getUser_summary() {
        return user_summary;
    }

    public void setUser_summary(String user_summary) {
        this.user_summary = user_summary;
    }

    public int getUser_gold() {
        return user_gold;
    }

    public void setUser_gold(int user_gold) {
        this.user_gold = user_gold;
    }

    public int getTopic_count() {
        return topic_count;
    }

    public void setTopic_count(int topic_count) {
        this.topic_count = topic_count;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public int getGroup_count() {
        return group_count;
    }

    public void setGroup_count(int group_count) {
        this.group_count = group_count;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public int getUser_upgrade_percent() {
        return user_upgrade_percent;
    }

    public void setUser_upgrade_percent(int user_upgrade_percent) {
        this.user_upgrade_percent = user_upgrade_percent;
    }

    public int getHas_new_letter() {
        return has_new_letter;
    }

    public void setHas_new_letter(int has_new_letter) {
        this.has_new_letter = has_new_letter;
    }

    public int getHas_new_notice() {
        return has_new_notice;
    }

    public void setHas_new_notice(int has_new_notice) {
        this.has_new_notice = has_new_notice;
    }
}
