package cn.lt.game.bean;

import java.util.List;

/**
 * Created by Erosion on 2018/2/27.
 */

public class MyVoucherBean {
    private List<MyVoucherItemBean> vouchers;

    public List<MyVoucherItemBean> getVouchers() {
        return vouchers;
    }

    public void setVouchers(List<MyVoucherItemBean> vouchers) {
        this.vouchers = vouchers;
    }
}
