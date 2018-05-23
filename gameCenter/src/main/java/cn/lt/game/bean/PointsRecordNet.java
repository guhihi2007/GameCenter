package cn.lt.game.bean;

import java.util.ArrayList;

/**
 * Created by honaf on 2017/6/6.
 * 积分记录网络层对象
 */

public class PointsRecordNet {
    private int use_able;
    private ArrayList<PointsRecord> point_histories = new ArrayList<>();

    public int getUse_able() {
        return use_able;
    }

    public void setUse_able(int use_able) {
        this.use_able = use_able;
    }

    public ArrayList<PointsRecord> getPoint_histories() {
        return point_histories;
    }

    public void setPoint_histories(ArrayList<PointsRecord> point_histories) {
        this.point_histories = point_histories;
    }

    @Override
    public String toString() {
        return "PointsRecordNet{" +
                "use_able=" + use_able +
                ", point_histories=" + point_histories +
                '}';
    }
}
