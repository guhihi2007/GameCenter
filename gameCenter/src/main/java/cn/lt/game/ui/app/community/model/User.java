package cn.lt.game.ui.app.community.model;

/**
 * 用户信息
 * 用途：
 * 话题点赞列表项
 * 小组成员列表项
 * 我的关注/我的粉丝
 */
public class User {
    public String user_nickname;//作者昵称
    public int user_id;//作者id
    public String user_icon;//作者icon图地址

    public String joined_at;//加入时间
    public String upvoted_at;//点赞时间
    public int user_type;//作者⽤用户类型，0-普通，1-管理员或组⻓长
    public boolean is_join;//是否有加入小组
    public boolean is_myself;//是否为当前用户
    public int user_level;//用户等级
    public String followed_at;//关注时间

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_icon() {
        return user_icon;
    }

    public void setUser_icon(String user_icon) {
        this.user_icon = user_icon;
    }

    public String getJoined_at() {
        return joined_at;
    }

    public void setJoined_at(String joined_at) {
        this.joined_at = joined_at;
    }

    public String getUpvoted_atString() {
        return upvoted_at;
    }

    public void setUpvoted_atString(String upvoted_at) {
        this.upvoted_at = upvoted_at;
    }

    public String getFollowed_at() {
        return followed_at;
    }

    public void setFollowed_at(String followed_at) {
        this.followed_at = followed_at;
    }

    public int getUser_level() {
        return user_level;
    }

    public void setUser_level(int user_level) {
        this.user_level = user_level;
    }
}
