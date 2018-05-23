package cn.lt.game.ui.app.awardgame.bean;

/**
 * @author chengyong
 * @time 2017/6/9 10:11
 * @des ${抽奖返回的实体}
 */

public class AwardDetailBean {

    /**
     * id : 中奖纪录 ID
     * position : 奖品位置
     * exchange_code : 兑换码
     * prize_style : 奖品类型:实物 1，券 2，流量 3，积分 4，礼包 5
     * prize_words : 奖品说明
     * message : 中奖话术
     */

    public String id;
    public String position;
    public String exchange_code;
    public String prize_style;
    public String prize_words;
    public String message;
    public int free_lottery_number;
    public String prize_name;
    public String prize_alias;
    public int today_lottery_number;
    public int waste_point_number;

    @Override
    public String toString() {
        return "AwardDetailBean{" +
                "id='" + id + '\'' +
                ", position='" + position + '\'' +
                ", exchange_code='" + exchange_code + '\'' +
                ", prize_style='" + prize_style + '\'' +
                ", prize_words='" + prize_words + '\'' +
                ", message='" + message + '\'' +
                ", free_lottery_number=" + free_lottery_number +
                ", prize_name='" + prize_name + '\'' +
                ", prize_alias='" + prize_alias + '\'' +
                ", max_lottery_number=" + today_lottery_number +
                '}';
    }
}
