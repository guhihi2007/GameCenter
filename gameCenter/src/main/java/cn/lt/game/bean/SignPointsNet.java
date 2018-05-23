package cn.lt.game.bean;

/**
 * Created by honaf on 2017/6/7.
 * 个人中心网络请求字段
 */

public class SignPointsNet {
    private int points;
    private int unaccepted;
    private int continuous;
    private boolean is_signed;
    private int total_point;
    private int feedback;
    public SignPointsNet() {
    }

    public SignPointsNet(int total_point) {
        this.total_point = total_point;
    }

    public int getFeedback() {
        return feedback;
    }

    public void setFeedback(int feedback) {
        this.feedback = feedback;
    }

    public int getTotal_point() {
        return total_point;
    }

    public void setTotal_point(int total_point) {
        this.total_point = total_point;
    }

    public boolean is_signed() {
        return is_signed;
    }

    public void setIs_signed(boolean is_signed) {
        this.is_signed = is_signed;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getUnaccepted() {
        return unaccepted;
    }

    public void setUnaccepted(int unaccepted) {
        this.unaccepted = unaccepted;
    }

    public int getContinuous() {
        return continuous;
    }

    public void setContinuous(int continuous) {
        this.continuous = continuous;
    }

    @Override
    public String toString() {
        return "SignPointsNet{" +
                "points=" + points +
                ", unaccepted=" + unaccepted +
                ", continuous=" + continuous +
                ", is_signed=" + is_signed +
                ", total_point=" + total_point +
                ", feedback=" + feedback +
                '}';
    }
}
