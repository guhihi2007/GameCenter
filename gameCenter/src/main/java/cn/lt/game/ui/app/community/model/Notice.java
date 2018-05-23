package cn.lt.game.ui.app.community.model;

/**
 * Created by ltbl on 2015/11/16.
 */
public class Notice {
    private int id;//社区通知ID
    private int user_id;//用户ID
    private String content;//通知内容
    private int is_read;//是否已读，0-未读，1-已读
    private String created_at;//创建时间
    private String updated_at;//最后更新时间

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
