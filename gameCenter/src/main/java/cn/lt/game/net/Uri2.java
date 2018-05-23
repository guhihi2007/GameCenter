package cn.lt.game.net;

/**
 * Created by linjunsheng on 2015/11/26.
 */
public class Uri2 {

    public static final String START_URI = "/start";// 精选必玩（第一次启动时有该数据）/启动app时预加载主页及其他信息
    public static final String INDEX_URI = "/index";// 首页/精选页
    public static final String GAMES_RANK_URI = "/ranking";// 游戏排行
    public static final String CATS_URI = "/cats";// 分类
    public static final String HOT_CATS_URI = "/hotcats";// 热门分类游戏列表
    public static final String SPECIAL_TOPICS_URI = "/topics";// 专题列表页
    public static final String GIFTS_URI = "/gifts";// 礼包
    public static final String GIFTS_MY = "/mygifts";//我的礼包；
    public static final String GIFTS_GAME_URI = "/games";// 礼包列表
    public static final String ACTIVITIES_URI = "/activities";// 活动
    public static final String FIRST_PUBLISH_URI = "/debuts";// 首发
    public static final String SEARCH_URI = "/search";// 搜索
    public static final String SEARCH_AUTOCOMPLETE_URI = "/search/autocomplete";// 自动匹配搜索
    public static final String CLIENT_UPDATE_URI = "/clients/releases/latest";// 检查更新
    public static final String CLIENT_UPDATE_URI_MANUAL = "/clients/releases/manual";// 检查更新(手动)
    public static final String GAME_MANAGER_URI = "/games/manage";// 游戏管理
    public static final String FEEDBACKS_URI = "/feedbacks";// 反馈信息展示、提交反馈
    public static final String RETRY_DOWNLOAD = "/games/download/detction";// 请求下载新地址
    public static final String SILENCE_USER_NOTICE_URI = "/silent/search";// 沉默用户通知数据
    public static final String POPUP_INTERVAL_URI = "/popup";// 弹窗事件间隔
    public static final String COVER_SYNC = "/gamecoveragewhitelist";// 游戏覆盖
    public static final String CID_REPORT = "/client/report/info";// CID上报
    public static final String LIMIT_GET_TICKET = "/lua/ticket/get";// 限流买票
    public static final String LIMIT_RETURN_TICKET = "/lua/ticket/drop";// 限流退票
    public static final String LIMIT_CHECK_TICKET = "/lua/ticket/check";// 限流验票
    public static final String SPREADSCREENAD = "/openscreenadvert";// 开屏广告
    public static final String USER_CENTER_DATA = "/profile";// 个人中心几个int数据,比如积分,连续签到
    public static final String POINTS_RECORD = "/point/histories";//积分记录
    public static final String POINTS_SYNC = "/point/sync";//积分同步
    public static final String ACTIVITY_POINTS = "/routineactivities/points";//活动积分tab页面
    public static final String SIGN_POINTS = "/point";//签到
    public static final String FEEDBACK_RED = "/feedback/unread";//反馈小红点
    public static final String FLOAT_ADS = "/floating/layer/adverting"; // 浮层广告

    public static final String AWARD_INFO_FRAGMENT = "/routineactivities/v2/lottery";// 抽奖活动页面  /api/routineactivities/v2/lottery
    public static final String AWARD_START = "/prize/v2/lottery";// 点击抽奖  /api/prize/v2/lottery
    public static final String AWARD_GET = "/prize/accept/";// 领奖（id）
    public static final String AWARD_HISTORY = "/prize/history";// 中奖纪录
    public static final String AWARD_EXPIRE = "/prize/expire";// 过期产品
    public static final String AWARD_SEND = "/prize/";// 发奖(id）
    public static final String AWARD_TITLE = "/routineactivities/title";
    public static final String BUTTOM_MENU = "/bottom-menus";
    public static final String ALL_GIFTS = "/totalgifts";// 所有礼包页面

    public static final String TAOBAO_CITY = "http://ip.taobao.com/service/getIpInfo2.php?ip=myip";// 淘宝api

    public static final String MY_VOUCHER_LIST = "/voucher/mine"; // 我的代金券
    public static final String EXCHANGE_VOUCHER_LIST = "/voucher/exchange"; // 兑换代金券列表
    public static final String EXCHANGE_VOUCHER = "/voucher/exchange"; // 兑换代金券

    public static final String COUNPON_COUNTS = "/voucher/count";//代金券数量

    /* 游戏详情(游戏ID或游戏包名)*/
    public static String getGameDetailUriByIdOrPkgName(String idOrPkgName) {
        return "/games/" + idOrPkgName;
    }

    /* 分类游戏列表*/
    public static String getCatsListUri(String catId) {
        return CATS_URI + "/" + catId;
    }

    /* 专题详情*/
    public static String getSpecialTopicDetailUri(String topicId) {
        return SPECIAL_TOPICS_URI + "/" + topicId;
    }

    /* 我的礼包*/
    public static String getMyGiftsUri(String userId) {
        return GIFTS_URI;
    }

    /* 游戏的礼包列表*/
    public static String getGiftListUri(String gameId) {
        return GIFTS_GAME_URI + "/" + gameId + "/gifts";
    }


    /* 礼包详情*/
    public static String getGiftDetailUri(String giftId) {
        return GIFTS_URI + "/" + giftId;
    }

    /* 礼包领取*/
    public static String getObtainGiftUri(String giftId) {
        return GIFTS_URI + "/" + giftId + "/code";
    }

    /* 游戏评论、提交游戏评论*/
    public static String getGameCommentsUri(String gameId) {
        return "/games/" + gameId + "/discuss";
    }

    /* 热门标签详情 */
    public static String getHotTagDetailUri(String tagId) {
        return "/tags/" + tagId;
    }

    /* 个推资源请求 */
    public static String getPushUri(String pushId) {
        return "/push/" + pushId;
    }

    /* 个推资源请求 */
    public static String getSilenceUserNoticeUri(String userTypeAndTimeType) {
        return "silent/search" + userTypeAndTimeType;
    }


}
