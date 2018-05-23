package cn.lt.game.ui.app.awardpoints.awardrecord;

/**
 * @author chengyong
 * @time 2017/6/9 15:07
 * @des ${中奖纪录的bean}
 */

public class AwardRecordBean {

        /**
         * prize_status : 奖品状态
         * prize_pic : 奖品图片
         * prize_word : 奖品说明
         * id : 中奖纪录 ID
         * prize_style : 奖品类型:实物 1，券 2，流量 3，积分 4，礼包 5
         * valid_date : 有效截止日期
         * prize_name : 奖品名称
         * prize_id : 奖品 ID
         */

        public String prize_status;
        public String prize_pic;
        public String prize_word;
        public String id;
        public String prize_style;
        public String valid_date;
        public String prize_name;
        public String prize_id;
        public String dataTime;
        public boolean isTime;

    @Override
    public String toString() {
        return "AwardRecordBean{" +
                "prize_status='" + prize_status + '\'' +
                ", prize_pic='" + prize_pic + '\'' +
                ", prize_word='" + prize_word + '\'' +
                ", id='" + id + '\'' +
                ", prize_style='" + prize_style + '\'' +
                ", valid_date='" + valid_date + '\'' +
                ", prize_name='" + prize_name + '\'' +
                ", prize_id='" + prize_id + '\'' +
                ", dataTime='" + dataTime + '\'' +
                ", isTime=" + isTime +
                '}';
    }
}
