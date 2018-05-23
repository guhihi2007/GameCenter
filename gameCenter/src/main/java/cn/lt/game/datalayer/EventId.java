package cn.lt.game.datalayer;

/**
 * Created by Administrator on 2015/11/25.
 */
public enum EventId {
    NECESSARY(1),//精选必玩             邓冲
    INDEX(2),//首页                    王呈勇
    RANK(3),//排行                     周天令
    CAT(4),//分类                      邓冲
    HOT_CATS(5),//热门分类游戏列表       邓冲
    CAT_DETAIL(6),//分类游戏列表         邓冲
    SPECIAL_TOPICS(7),//专题列表         林俊生
    SPECIAL_TOPICS_DETAIL(8),//专题详情  林俊生
    GAME_DETAIL(9),//游戏详情            袁磊
    GIFTS_INDEX(10),//礼包首页           吴超凡
    GIFTS_SEARCH(11),//礼包搜索          吴超凡
    GIFTS_MY(12),//我的礼包              吴超凡
    GIFTS_GAME(13),//游戏礼包            吴超凡
    GIFTS_DETAIL(14),//礼包详情          吴超凡
    GIFTS_OBTAIN(15),//领取礼包          吴超凡
    ACTIVITIES(16),//活动                周天令
    FIRST_ISSUE(17),//首发               周天令
    SEARCH_INDEX(18),//搜索首页           袁磊
    SEARCH(19),//搜索                     袁磊
    AUTO_COMPLETE(20),//搜索关键字自动匹配   袁磊
    COMMENTS_GAME(21),//获取游戏评论        袁磊
    COMMENTS_GAME_PUBLISH(22),//发表游戏评论  袁磊
    DATA_STATISTIC(23),//统计                王呈勇
    PLATFORM_UPDATE_CHECK(24),//检查平台升级   吴超凡
    LOCAL_GAME_MANAGE(25),//游戏管理相关，获取本地游戏升级信息等      邓冲
    FEEDBACK(26),//获取反馈信息                                   周天令
    FEEDBACK_PUBLISH(27),//提交反馈                             周天令
    HOT_TAG_DETAIL(28),//热门标签详情                             周天令
    PUSH_DETAIL(29),//请求推送信息                                林俊生
    STRATEGY_SEARCH(30),//攻略搜索                                王呈勇
    GAME_DETAIL_FOR_INSTALL(31),//游戏详情                         袁磊
    GAME_NEW_URL_MD5(32),//获取游戏新下载地址                        林俊生
    SILENCE_USER_NOTICE(33),// 获取沉默用户通知数据                   林俊生
    POPWINDOW(34),//弹窗管理                                        周天令
    GAME_SYNC(35),//游戏管理相关，获取本地游戏升级信息等
    COVER_SYNC(36);//获取可覆盖游戏

    private int eventId;

    public boolean needParser() {
        return this != NECESSARY;
    }

    EventId(int eventId) {
        this.eventId = eventId;
    }
}
