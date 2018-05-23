package cn.lt.game.bean;

import cn.lt.game.lib.netdata.BaseBean;

/**
 * 礼包
 * Created by Administrator on 2015/11/12.
 */
public class GiftBean extends BaseBean {
    private String id;
    private String title;
    private String icon_url;
    private String gift_id;
    private String gift_title;
    private int total;
    private int remain;
    private String content;
    private String usage;
    private boolean is_received;
    private String stocked_at;
    private String unstocked_at;
    private String code;
    private int received_count;
    private boolean isReceived = false;//是否已领取
    private int gift_total;
    private GameInfoBean game;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getGift_id() {
        return gift_id;
    }

    public void setGift_id(String gift_id) {
        this.gift_id = gift_id;
    }

    public String getGift_title() {
        return gift_title;
    }

    public void setGift_title(String gift_title) {
        this.gift_title = gift_title;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getRemain() {
        return remain;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getStocked_at() {
        return stocked_at;
    }

    public void setStocked_at(String stocked_at) {
        this.stocked_at = stocked_at;
    }

    public String getUnstocked_at() {
        return unstocked_at;
    }

    public void setUnstocked_at(String unstocked_at) {
        this.unstocked_at = unstocked_at;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean is_received() {
        return is_received;
    }

    public void setIs_received(boolean is_received) {
        this.is_received = is_received;
    }

    public int getReceived_count() {
        return received_count;
    }

    public void setReceived_count(int received_count) {
        this.received_count = received_count;
    }

    public GameInfoBean getGame_base_info() {
        return game;
    }

    public GameInfoBean getGame() {
        return game;
    }

    public void setGame(GameInfoBean game) {
        this.game = game;
    }

    public boolean isReceived() {
        return isReceived;
    }

    public void setIsReceived(boolean isReceived) {
        this.isReceived = isReceived;
    }

    public int getGift_total() {
        return gift_total;
    }

    public void setGift_total(int gift_total) {
        this.gift_total = gift_total;
    }
}
