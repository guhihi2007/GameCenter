package cn.lt.game.bean;

import java.io.Serializable;

/**
 * Created by honaf on 2017/6/14.
 */

public class SyncPointsBean implements Serializable{

    private int activity_id;
    private String activity_name;
    private String game_id;
    private String game_name;
    private int points;

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    @Override
    public String toString() {
        return "SyncPointsBean{" +
                "activity_id=" + activity_id +
                ", activity_name='" + activity_name + '\'' +
                ", game_id='" + game_id + '\'' +
                ", game_name='" + game_name + '\'' +
                ", points='" + points + '\'' +
                '}';
    }
}
