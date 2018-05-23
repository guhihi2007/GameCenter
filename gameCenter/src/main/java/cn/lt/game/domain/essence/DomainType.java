package cn.lt.game.domain.essence;

/**
 * Created by Administrator on 2015/11/17.
 */
public enum DomainType {
    /** 无效的，无法识别的类型*/
    Invalid("invalid"),
    GAME("game"),
    COMMUNITY("discussion"),
    GIFTDETAIL("gift") ,
    GAMEGIFTLIST("game_gift") ,
    CAT("cat"),
    TAG("tag"),
    ACTIVITY("activity"),

    /** 专题详情*/
    SPECIAL_TOPIC("topic"),
    H5("h5"),
    PAGE("page"),
    KEY_WORD("key_word"),

    /** 常规活动*/
    ROUTINE_ACTIVITIES("routine_activity"),

    /** 普通列表*/
    APPLIST("applist"),

    /** 活动列表*/
    ACTIVITY_LIST("YM-HD"),

    /** 热点Ttab*/
    HOT_TAB("hot_tab"),

    /** 热点详情*/
    HOT_DETAIL("hot_detail"),

    /** 专题列表*/
    SPECIAL_TOPIC_LIST("YM-ZT");

    private final String value;

    DomainType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static DomainType getEnum(String value) {
        for (DomainType v : DomainType.values()) {
            if(v.toString().equalsIgnoreCase(value)) return v;
        }
        return Invalid;
    }
}
