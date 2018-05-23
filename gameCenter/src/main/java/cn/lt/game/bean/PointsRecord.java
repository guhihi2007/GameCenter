package cn.lt.game.bean;

/**
 * Created by honaf on 2017/6/6.
 * 积分记录页面对象
 */

public class PointsRecord {
    private int id;
    private String content;
    private String created_at;
    private int point;

    public PointsRecord(int id, String content, String date, int point) {
        this.id = id;
        this.content = content;
        this.created_at = date;
        this.point = point;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return created_at;
    }

    public void setDate(String date) {
        this.created_at = date;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "PointsRecord{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", date='" + created_at + '\'' +
                ", point=" + point +
                '}';
    }
}
