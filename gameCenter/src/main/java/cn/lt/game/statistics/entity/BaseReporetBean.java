package cn.lt.game.statistics.entity;

/**
 * Created by ltbl on 2017/6/9.
 */

public class BaseReporetBean {
    private String networkType;//网络类型
    private String remark;//备注信息


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
//    private String actionType;//事件类型
//    public String getActionType() {
//        return actionType;
//    }
//
//    public void setActionType(String actionType) {
//        this.actionType = actionType;
//    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }
}
