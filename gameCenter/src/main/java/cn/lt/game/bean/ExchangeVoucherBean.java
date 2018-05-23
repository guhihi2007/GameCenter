package cn.lt.game.bean;

import java.util.List;

/**
 * Created by Erosion on 2018/2/28.
 */

public class ExchangeVoucherBean {
    private List<ExchangeVoucherItemBean> data;
    private int point;

    public List<ExchangeVoucherItemBean> getData() {
        return data;
    }

    public void setData(List<ExchangeVoucherItemBean> data) {
        this.data = data;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
