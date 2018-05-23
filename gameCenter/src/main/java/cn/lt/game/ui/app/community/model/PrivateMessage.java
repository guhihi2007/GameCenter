package cn.lt.game.ui.app.community.model;

import java.io.Serializable;

/**
 * Created by wenchao on 2015/11/26.
 * 私信
 */
public class PrivateMessage implements Serializable{
    public int user_id;
    public String user_nickname;
    public String user_icon;
    public int user_level;
    public String last_statement;
    public String published_at;
    public boolean is_read;


}
