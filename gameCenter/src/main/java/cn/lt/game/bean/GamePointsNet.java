package cn.lt.game.bean;

import java.util.ArrayList;

import cn.lt.game.lib.netdata.BaseBean;

/**
 * 活动页面积分数据
 */
public class GamePointsNet extends BaseBean {
    private String download_ad;
    private int continuous;
    private int point;
    private String sign_ad;
    private boolean is_signed;
    private int activity_id;
    private String activity_name;

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public int getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
    }

    private ArrayList<GameInfoBean> games;

    public boolean is_signed() {
        return is_signed;
    }

    public void setIs_signed(boolean is_signed) {
        this.is_signed = is_signed;
    }

    public String getDownload_ad() {
        return download_ad;
    }

    public void setDownload_ad(String download_ad) {
        this.download_ad = download_ad;
    }

    public int getContinuous() {
        return continuous;
    }

    public void setContinuous(int continuous) {
        this.continuous = continuous;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getSign_ad() {
        return sign_ad;
    }

    public void setSign_ad(String sign_ad) {
        this.sign_ad = sign_ad;
    }

    public ArrayList<GameInfoBean> getGames() {
        return games;
    }

    public void setGames(ArrayList<GameInfoBean> games) {
        this.games = games;
    }

    @Override
    public String toString() {
        return "GamePointsNet{" +
                "download_ad='" + download_ad + '\'' +
                ", continuous=" + continuous +
                ", point=" + point +
                ", sign_ad='" + sign_ad + '\'' +
                ", is_signed=" + is_signed +
                ", games=" + games.toString() +
                '}';
    }
}
