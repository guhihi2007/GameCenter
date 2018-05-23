package cn.lt.game.ui.app.awardgame.bean;

/**
 * @author chengyong
 * @time 2017/6/15 16:10
 * @des ${发奖}
 */

public class SendAwardBean {

    /**
     * prize_pic : 奖品图片
     * phone : 收货人手机号
     * express_number : 快递单号
     * prize_id : 奖品 ID
     * address : 收货人地址
     * prize_name : 奖品名称
     * exchange_code : 兑换码
     * prize_word : 奖品说明
     * prize_style : 奖品类型:实物 1，券 2，流量 3，积分 4，礼包 5
     * prize_status : 奖品状态:待发奖 1, 已发奖 2, 未领取 3, 已领取 4, 已过期 5
     * name : 收货人姓名
     * express_company : 快递公司
     */

    public String prize_pic;
    public String phone;
    public String express_number;
    public String prize_id;
    public String address;
    public String prize_name;
    public String exchange_code;
    public String prize_word;
    public String prize_style;
    public String prize_status;
    public String name;
    public String express_company;

    @Override
    public String toString() {
        return "SendAwardBean{" +
                "prize_pic='" + prize_pic + '\'' +
                ", phone='" + phone + '\'' +
                ", express_number='" + express_number + '\'' +
                ", prize_id='" + prize_id + '\'' +
                ", address='" + address + '\'' +
                ", prize_name='" + prize_name + '\'' +
                ", exchange_code='" + exchange_code + '\'' +
                ", prize_word='" + prize_word + '\'' +
                ", prize_style='" + prize_style + '\'' +
                ", prize_status='" + prize_status + '\'' +
                ", name='" + name + '\'' +
                ", express_company='" + express_company + '\'' +
                '}';
    }
}
