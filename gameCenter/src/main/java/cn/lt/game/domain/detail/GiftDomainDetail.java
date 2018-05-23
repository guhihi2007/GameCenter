package cn.lt.game.domain.detail;

import java.io.Serializable;

import cn.lt.game.bean.GiftBean;
import cn.lt.game.domain.essence.DomainEssence;
import cn.lt.game.domain.essence.DomainType;

/**
 * Created by Administrator on 2015/11/18.
 */
public class GiftDomainDetail extends DomainEssence implements Serializable {
    private final static DomainType DOMAIN_TYPE = DomainType.GIFTDETAIL;

    private String title = null;//礼包名称
    private String content = null;//礼包内容
    private String usage = null;//礼包使用方法
    private int total = -1;//礼包总数
    private int remain = -1;//剩余礼包数
    private int receivedCount = -1;//已领取礼包数
    private boolean isReceived = false;//是否已领取
    private String startTime = null;//礼包上架时间
    private String endTime = null;//礼包失效时间
    private String code = null;//礼包领取码
    private String iconUrl = null;//礼包图片
    private int giftTotal = -1;//该游戏的礼包种类总数

    private GameDomainBaseDetail game = null;//相关联游戏信息

    public GiftDomainDetail() { /**/  }

    public GiftDomainDetail(GiftBean giftBean) {
        setAllFields(giftBean);
    }

    private void setAllFields(GiftBean giftBean) {
        setUniqueIdentifier(giftBean.getId());
        this.title = giftBean.getTitle();
        this.content = giftBean.getContent();
        this.usage = giftBean.getUsage();
        this.total = giftBean.getTotal();
        this.remain = giftBean.getRemain();
        this.receivedCount = giftBean.getReceived_count();
        this.isReceived = giftBean.is_received();
        this.startTime = giftBean.getStocked_at();
        this.endTime = giftBean.getUnstocked_at();
        this.code = giftBean.getCode();
        this.iconUrl = giftBean.getIcon_url();
        this.giftTotal = giftBean.getGift_total();
        if (giftBean.getGame_base_info() != null) {
            this.game = new GameDomainBaseDetail(giftBean.getGame_base_info());
        }
    }

    @Override
    public DomainType getDomainType() {
        return DOMAIN_TYPE;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getReceivedCount() {
        return receivedCount;
    }

    public void setReceivedCount(int receivedCount) {
        this.receivedCount = receivedCount;
    }

    public boolean isReceived() {
        return isReceived;
    }

    public void setIsReceived(boolean isReceived) {
        this.isReceived = isReceived;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public GameDomainBaseDetail getGame() {
        return game;
    }

    public void setGame(GameDomainBaseDetail game) {
        this.game = game;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    public int getGiftTotal() {
        return giftTotal;
    }
    public void setGiftTotal(int giftTotal) {
        this.giftTotal = giftTotal;
    }
}
