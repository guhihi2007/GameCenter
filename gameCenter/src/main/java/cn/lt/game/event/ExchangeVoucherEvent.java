package cn.lt.game.event;

/**
 * 代金券兑换成功发出的事件
 * Created by Erosion on 2018/3/3.
 */

public class ExchangeVoucherEvent {
    public boolean isSuccess;
    public boolean updateMine;

    public ExchangeVoucherEvent (boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public ExchangeVoucherEvent (boolean isSuccess,boolean updateMine) {
        this.isSuccess = isSuccess;
        this.updateMine = updateMine;
    }
}
