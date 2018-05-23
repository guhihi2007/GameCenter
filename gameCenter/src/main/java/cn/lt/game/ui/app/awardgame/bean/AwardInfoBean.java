package cn.lt.game.ui.app.awardgame.bean;

import java.util.List;

/**
 * @author chengyong
 * @time 2017/6/7 11:45
 * @des ${抽奖页面实体}
 */

public class AwardInfoBean {

    /**
     * prizes : [{"prize_pic":"http://gcenter.ol.ttigame.cn/uploads/prize/2/e/2edf5883ce37b30fc4e79708f8dd4ab0.png","position":1,"prize_name":"1"},{"prize_pic":"http://gcenter.ol.ttigame.cn/uploads/prize/4/9/492a7a8f57baba6a3c0513a054642fae.png","position":2,"prize_name":"2"},{"prize_pic":"http://gcenter.ol.ttigame.cn/uploads/prize/a/0/a0069ff78e3cce39d9e757748eff568a.png","position":3,"prize_name":"1"},{"prize_pic":"http://gcenter.ol.ttigame.cn/uploads/prize/6/7/670530661ee6785e01aa9f6bee05c1c2.png","position":4,"prize_name":"1"},{"prize_pic":"http://gcenter.ol.ttigame.cn/uploads/prize/2/9/292cd28e2e9d6d85c196b19c699cdc7a.png","position":5,"prize_name":"1"},{"prize_pic":"http://gcenter.ol.ttigame.cn/uploads/prize/2/5/25e391ad33d317c29d6f15fc52b4ae4e.png","position":6,"prize_name":"1"},{"prize_pic":"http://gcenter.ol.ttigame.cn/uploads/prize/5/a/5a367d3fd338fef9e2fd468e976bc265.png","position":7,"prize_name":"1"},{"prize_pic":"http://gcenter.ol.ttigame.cn/uploads/prize/7/c/7c5adcb4a4ba5e43b7ad4030cb5f3873.png","position":8,"prize_name":"1"}]
     * activity_flow : 活动流程：
     * free_lottery_number : 3
     * point : 0
     * participants_number : 178
     * waste_point_number : 10
     * activity_name : 八月桂花
     * activity_rule : 活动规则：
     */

    public String activity_flow;
    public int free_lottery_number;
    public int point;
    public int participants_number;
    public int unaccepted_number; //未领奖的数量
    public int waste_point_number;
    public String activity_name;
    public String activity_rule;
    public String activity_id;
    public int max_lottery_number;
    public int today_lottery_number;
    public List<PrizesBean> prizes;

    public static class PrizesBean {
        /**
         * prize_pic : http://gcenter.ol.ttigame.cn/uploads/prize/2/e/2edf5883ce37b30fc4e79708f8dd4ab0.png
         * position : 1
         * prize_name : 1
         */

        public String prize_pic;
        public int position;
        public String prize_name;
        public String prize_alias;
        public String prize_style;
    }

    @Override
    public String toString() {
        return "AwardInfoBean{" +
                "activity_flow='" + activity_flow + '\'' +
                ", free_lottery_number=" + free_lottery_number +
                ", point=" + point +
                ", participants_number=" + participants_number +
                ", unaccepted_number=" + unaccepted_number +
                ", waste_point_number=" + waste_point_number +
                ", activity_name='" + activity_name + '\'' +
                ", activity_rule='" + activity_rule + '\'' +
                ", activity_id='" + activity_id + '\'' +
                ", max_lottery_number=" + max_lottery_number +
                ", prizes=" + prizes +
                ", today_lottery_number=" + today_lottery_number +
                '}';
    }
}
