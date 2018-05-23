package cn.lt.game.global;

import cn.lt.game.BuildConfig;
import cn.lt.game.lib.util.MetaDataUtil;

/***
 * Created by Administrator on 2015/12/18.
 */
public class Constant {

    /**
     * 广点通APPID
     */
    public static final String APPID = "1105924241";

    /**
     * 渠道名
     */
    public static String CHANNEL = BuildConfig.FLAVOR;

    static {
        String chanel = BuildConfig.FLAVOR;
        if (chanel.contains("_")) {
            CHANNEL = chanel.replace("_", "");
        }
    }

    /**
     * 广点通开屏ID
     */
    public static String SplashPosID;
    /**
     * 本地平台版本名称
     */
    public static final String versionName = MetaDataUtil.getMetaData("versionName");

    /**
     * 本地平台版本代号
     */
    public static final String versionCode = MetaDataUtil.getMetaData("versionCode");

    /**
     * 是否debug模式
     */
    public static boolean DEBUG = BuildConfig.DEBUG;

    /**
     * 启动页
     */
    public static final String PAGE_LOADING = "YM-QD";
    /**
     * 精选页
     */
    public static final String PAGE_INDEX = "YM-JX";

    /**
     * 精选必玩
     */
    public static final String PAGE_INDEX_NECESSARY = "JX-BW";

    /**
     * 自动触发--页面标识
     */
    public static final String AUTO_PAGE = "AUTO_PAGE";
    /**
     * 单机排行页面
     */
    public static final String PAGE_RANK_OFFLINE = "YM-PD";
    /**
     * 网游排行页面
     */
    public static final String PAGE_RANK_ONLINE = "YM-PW";
    /**
     * 最热排行页面
     */
    public static final String PAGE_RANK_HOT = "YM-PR";
    /**
     * 最新排行页面
     */
    public static final String PAGE_RANK_NEWS = "YM-PX";
    /**
     * 分类页面 大的fragmnet
     */
    public static final String PAGE_CATEGORY = "YM-FZ";
    /**
     * 分类列表页面 详情
     */
    public static final String PAGE_CATEGORY_LIST = "YM-FL";
    /**
     * 热门分类页面 详情
     */
    public static final String PAGE_CATEGORY_HOT = "YM-RF";
    /**
     * 下载管理页面
     */
    public static final String PAGE_MANGER_DOWNLOAD = "YM-XG";
    /**
     * 已安装页面
     */
    public static final String PAGE_MANGER_PALY = "YM-YAZ";
    /**
     * 升级页面
     */
    public static final String PAGE_MANGER_UPGRADE = "YM-SJ";
    /**
     * 专题列表页面
     */
    public static final String PAGE_SUBJECT_LIST = "YM-ZT";
    /**
     * 专题详情页面
     */
    public static final String PAGE_SUBJECT_DETAIL = "YM-ZX";
    /**
     * 热门话题列表页面
     */
    public static final String PAGE_TOPIC_HOT = "YM-RH";
    /**
     * 话题详情页面
     */
    public static final String PAGE_TOPIC_DETAIL = "YM-HTXQ";
    /**
     * 评论详情页面
     */
    public static final String PAGE_COMMENT_DETAIL = "YM-PLXQ";
    /**
     * 推荐小组页面
     */
    public static final String PAGE_GROUP_RECOMMEND = "YM-TJ";
    /**
     * 小组话题列表页面
     */
    public static final String PAGE_GROUP_TOPIC_LIST = "YM-XZHT";
    /**
     * 小组成员列表页面
     */
    public static final String PAGE_GROUP_MEMBER_LIST = "YM-XZCY";
    /**
     * 我的社区页面
     */
    public static final String PAGE_COMMUNTIY_MINE = "YM-WS";
    /**
     * 我的话题页面
     */
    public static final String PAGE_TOPIC_MINE = "YM-WDHT";
    /**
     * 我的评论页面
     */
    public static final String PAGE_COMMENT_MINE = "YM-WDPL";
    /**
     * 我的小组页面
     */
    public static final String PAGE_GROUP_MINE = "YM-WDXZ";
    /**
     * 我的私信页面
     */
    public static final String PAGE_PRIVATE_LATTER_MINE = "YM-WDSX";
    /**
     * 社区通知页面
     */
    public static final String PAGE_COMMUNTIY_NOTICE = "YM-SQTZ";
    /**
     * 我的关注页面
     */
    public static final String PAGE_ATTENTION_MINE = "YM-WDGZ";
    /**
     * 我的粉丝页面
     */
    public static final String PAGE_FANS_MINE = "YM-WDFS";
    /**
     * 我的收藏页面
     */
    public static final String PAGE_COLLECT_MINE = "YM-WDSC";
    /**
     * 我的草稿页面
     */
    public static final String PAGE_DRAFTS_MINE = "YM-WDCG";
    /**
     * 发表话题
     */
    public static final String PAGE_TOPIC_PUBLISH = "YM-FBHT";
    /**
     * 私信详情
     */
    public static final String PAGE_PRIVATE_LATTER_DETAIL = "YM-SXXQ";
    /**
     * 发表评论
     */
    public static final String PAGE_COMMENT_PUBLISH = "YM-FBPL";

    /**
     * 他的主页
     */
    public static final String PAGE_HOME_PAGE_HER = "YM-TDZY";
    /**
     * 他的评论
     */
    public static final String PAGE_COMMENT_HER = "YM-TDPL";
    /**
     * 他的话题
     */
    public static final String PAGE_TOPIC_HER = "YM-TDHT";
    /**
     * 他的小组
     */
    public static final String PAGE_GROUP_HER = "YM-TDXZ";


    /**
     * 礼包中心页面
     */
    public static final String PAGE_GIFT_CENTER = "YM-LB";

    /**
     * 所有礼包页面
     */
    public static final String PAGE_ALL_GIFT = "YM-SYLB";

    /**
     * 礼包搜索页面
     */
    public static final String PAGE_GIFT_SEARCH = "YM-LS";
    /**
     * 礼包列表页面
     */
    public static final String PAGE_GIFT_LIST = "YM-LL";
    /**
     * 礼包列表页面
     */
    public static final String PAGE_GIFT_MINE = "YM-LW";
    /**
     * 礼包详情页面
     */
    public static final String PAGE_GIFT_DETAIL = "YM-LX";
    /**
     * 最新攻略页面
     */
    public static final String PAGE_STRATEGY_NEWS = "YM-ZG";
    /**
     * 攻略中心页面
     */
    public static final String PAGE_STRATEGY_CENTER = "YM-GZ";
    /**
     * 攻略搜索页面
     */
    public static final String PAGE_STRATEGY_SEARCH = "YM-GS";
    /**
     * 攻略列表页面
     */
    public static final String PAGE_STRATEGY_LIST = "YM-GB";
    /**
     * 攻略详情页面
     */
    public static final String PAGE_STRATEGY_DETAIL = "YM-GX";
    /**
     * 游戏详情页面
     */
    public static final String PAGE_GAME_DETAIL = "YM-YX";
    /**
     * 游戏详情推荐
     */
    public static final String PAGE_GAME_DETAIL_RECOMMEND = "YM-YX-TJ";
    /***
     * 发表游戏评论页面
     */
    public static final String PAGE_GAME_SEND_COMMENT = "YM-YXPL";
    /**
     * 游戏评论列表页面
     */
    public static final String PAGE_GAME_DETAIL_COMMENT_LIST = "YM-PLLB";

    /**
     * 游戏标签页
     */
    public static final String PAGE_GAME_TAG = "YM-BQ";
    /**
     * 搜索推荐页
     */
    public static final String PAGE_SEARCH_RECOMMEND = "YM-STJ";
    /**
     * 搜索结果页
     */
    public static final String PAGE_SEARCH_RESULT = "YM-SJG";
    /**
     * 搜索自动匹配页
     */
    public static final String PAGE_SEARCH_AUTO_MATCH = "YM-SZD";
    /**
     * 搜索失败页-无结果页
     */
    public static final String PAGE_SEARCH_FAILED = "YM-SSB";
    /**
     * 个人中心-编辑资料页面
     */
    public static final String PAGE_PERSONAL_EDIT = "YM-BJ";
    /**
     * 个人中心-编辑资料-绑定手机页面
     */
    public static final String PAGE_PERSONAL_BINDING_PHONE = "YM-BDSJ";
    /**
     * 个人中心-编辑资料-绑定邮箱页面
     */
    public static final String PAGE_PERSONAL_BINDING_EMAIL = "YM-BDYX";
    /**
     * 个人中心-编辑资料-修改手机页面
     */
    public static final String PAGE_PERSONAL_CHANGE_PHONE = "YM-XGSJ";
    /**
     * 个人中心-编辑资料-修改邮箱页面
     */
    public static final String PAGE_PERSONAL_CHANGE_EMAIL = "YM-XGYX";
    /**
     * 个人中心-登录页面
     */
    public static final String PAGE_PERSONAL_LOGIN = "YM-GD";
    /**
     * 个人中心-反馈页面
     */
    public static final String PAGE_PERSONAL_FEEDBACK = "YM-FK";
    /**
     * 个人中心-关于我们页面
     */
    public static final String PAGE_PERSONAL_ABOUT = "YM-GYWM";
    /**
     * 个人中心-设置页面
     */
    public static final String PAGE_PERSONAL_SETTING = "YM-SZ";
    /**
     * 个人中心-未登录页面
     */
    public static final String PAGE_PERSONAL_UNLOGIN = "YM-WDL";
    /**
     * 个人中心-修改密码
     */
    public static final String PAGE_PERSONAL_CHANGE_PASSWORD = "YM-XGMM";
    /**
     * 个人中心-已登录页面
     */
    public static final String PAGE_PERSONAL_HAS_LOGIN = "YM-YDL";
    /**
     * 个人中心-找回密码
     */
    public static final String PAGE_PERSONAL_FIND_PASSWORD = "YM-ZHMM";
    /**
     * 个人中心-找回密码-手机
     */
    public static final String PAGE_PERSONAL_FIND_PASSWORD_PHONE = "YM-ZHMM-SJ";
    /**
     * 个人中心-找回密码-邮箱
     */
    public static final String PAGE_PERSONAL_FIND_PASSWORD_EMAIL = "YM-ZHMM-YX";
    /**
     * 个人中心-注册-设置昵称
     */
    public static final String PAGE_PERSONAL_REGISTER_SET_ALIAS = "YM-ZC-NC";
    /**
     * 个人中心-注册-手机
     */
    public static final String PAGE_PERSONAL_REGISTER_PHONE = "YM-ZC-SJ";
    /**
     * 个人中心-注册-邮箱
     */
    public static final String PAGE_PERSONAL_REGISTER_EMAIL = "YM-ZC-YX";
    /**
     * 活动列表
     */
    public static final String PAGE_ACTIVITY_LIST = "YM-HDLB";
    /**
     * 首发游戏列表
     */
    public static final String PAGE_FIRST_PUBLISH = "YM-SFLB";

    /**
     * H5页面
     */
    public static final String PAGE_H5 = "YM-H5";
    /***
     * 热点
     */
    public static final String PAGE_HOT = "YM-HOT";

    /**
     * 热点详情
     */
    public static final String PAGE_HOT_DETAIL = "YM-HOT-DETAIL";

    /**
     * 浮层广告
     */
    public static final String PAGE_FLOAT = "Float";

    /**
     * 活动抽奖类
     */
    public static final String PAGE_CHOUJIANG = "YM-CJ";//抽奖
    public static final String PAGE_JIFENG = "YM-HQ";//积分
    public static final String PAGE_ZHONGJIANG = "YM-ZJ";//中奖记录
    public static final String PAGE_JIFEN_RECORD = "YM-JF";//积分记录
    public static final String PAGE_HISTORY = "YM-GQ";//过期奖品

    public static final String PAGE_MY_VOUCHER = "YM-WDDJQ";//我的代金券
    public static final String PAGE_EXCHANGE_VOUCHER = "YM-DHDJQ";//我的代金券

    /**
     * ---------------------------------------------------------------------------------------------------
     * download_mode/install_mode
     */
    public static final String MODE_AUTO_UPGRADE = "single";
    /**
     * download_mode/install_mode
     */
    public static final String MODE_SINGLE = "single";
    /**
     * download_mode/install_mode
     */
    public static final String MODE_ONEKEY = "onekey";
    public static final String MODE_ONEKEY_EXIT = "onekey_exit";
    /**
     * download_mode/install_mode
     */
    public static final String MODE_RETRY_REQUEST = "retry_request";

    public static final String TYPE_REDOWNLOAD = "request [file is not exist, reDownload(byInstall)]";

    //    downloadType:    auto/manual/auto_update /normal.   //自动（重试）、手动（重试）、自动升级、手动点击下载
    //下载重试区分手动（manual）或自动（auto）
    public static final String RETRY_TYPE_MANUAL = "manual";
    public static final String AUTO = "auto";
    public static final String DOWNLOAD_TYPE_AUTO_UPDATE = "auto_update";
    public static final String DOWNLOAD_TYPE_AUTO_COVER = "MD5_auto";
    public static final String DOWNLOAD_TYPE_NORMAL = "manual";
    public static final String PAGE_GE_TUI = "GE_TUI";

    /**
     * -------------------------------下载异常字段------------------------------------
     * 断网或切换引起的异常
     */

    public static final String TITLE_ERROR = "游戏中心下载失败";
    public static final String CONTENT_ERROR = "请点击重试";
    public static final String RETRY_FLAG = "retry_flag";

    /**
     * srcType:历史搜索类型
     */
    public static final String SRCTYPE_SEARCHACCU = "searchAccu";
    public static final String SRCTYPE_SEARCHHIS = "searchHis";
    public static final String SRCTYPE_SEARCHFUZ = "searchFuz";
    /**
     * srcType:详情页类型
     */
    public static final String SRCTYPE_DETAILRECOM = "detailRecom";
    public static final String SRCTYPE_DETAILLABLE = "detailLable";


    /**
     * 在sharedPreferences中记录SD里天天游戏安装包的版本号key
     */
    public static final String VERSIONCODE = "VersionCode";

    //应用“设置（SettingActivity）”的一些SharedPreferences的KEY
    public static class Setting {
        public static final String NO_PIC = "nopic";//是否无图模式
        public static final String DELETE_APK = "deleteapk";//自动删除安装包
        //		public static final String AUTOUPDATE = "autoupdate";//安装成功后是否删除安装包
        public static final String AUTOINSTALL = "autoinstall";//自动安装游戏 root按钮开关
        public static final String SYSTEMINSTALL = "systeminstall";//系统安装游戏权限
        public static final String ROOTINSTALL = "rootinstall";//root安装游戏权限
        public static final String AUTOUPGRADE = "autoupgrade";//是否开启零流量升级(需满足系统权限等一系列条件)
        public static final String FIRST_OPEN = "firstopen";
    }

    public static class Other {
        public static final String DOWNLOAD_AD_TIME = "download_ad_time";
    }

    public static final long DEFAULT_PERIOD = 1000 * 60 * 60 * 24 * 7L; //默认弹框时间(七天)
    public static long CHECKVERSIONPERIOD = 1000 * 60 * 60 * 8;//5分钟检查一次版本升级（测试）

    public static final String CLIENT_UPDATE_PERIOD = "client_update"; //客户端升级弹出框时间间隔

    public static final String AUTO_INSTALL_PERIOD = "auto_install";//自动装弹框时间间隔

    public static final String AUTO_UPGRADE_PERIOD = "auto_upgrade";//应用自动升级间隔时间

    public static final String SELECTION_PLAY_PERIOD = "selection_play";//精选必备间隔时间

    public static final String FLOAT_ADS_PERIOD = "float_ads_period";//浮层广告间隔时间

    public static final String SPREAD_PERIOD = "spread_period";//开屏广告间隔时间

    public static final String SPREAD_STATUS = "spread_status";//开屏广告开关状态


    public static final String CLIENT_UPDATE_POP_STATE = "client_update_state"; //客户端升级弹出框状态

    public static final String AUTO_INSTALL_POP_STATE = "auto_install_state";//自动装弹框状态

    public static final String AUTO_UPGRADE_POP_STATE = "auto_upgrade_state";//应用自动升级状态

    public static final String SELECTION_PLAY_POP_STATE = "selection_play_state";//精选必备状态

    public static final String HOT_CENTENT_STATE = "hot_content_state";//热点开光状态

    public static final String FLOAT_ADS_STATE = "float_ads_state";//浮层广告状态

    public static final String INSTALL_PKG = "install_pkg";//安装器名称

    public static final String DEF_INSTALL_PKG = "cn.lt.game";//默认安装器包名

    public static final String CLIENT_UPDATE_SHOWED = "client_update_tag"; //客户端升级弹出框是否已经展示过

    public static final String AUTO_INSTALL_SHOWED = "auto_install_tag";

    public static final String AUTO_UPGRADE_SHOWED = "auto_upgrade_tag";

    public static final String SELECTION_PLAY_SHOWED = "selection_tag";

    public static final String FLOAT_ADS_SHOWED = "float_ads_tag";

    public static final String POST_CID_PERIOD = "cid_period";//上报CID间隔时间
    /**
     * apk存储路径的sp的key的前缀
     */
    public static final String PREFIX_PATH_KEY = "save_";

    public static final String INSTALL_LIMIT = "install_limit";//安装次数限制

    // 精选必玩、浮层广告交替显示
    public static final String POPUP_PRIORITY = "popup_priority";
    //api/point 签到抽奖等(去问一阵风)
    public static final int POINTS_AWARD = 1;
    public static final int POINTS_DOWNLOAD = 2;
    public static final int POINTS_SIGN = 3;
    public static final int POINTS_WIN_PRIZE = 4;
    //下载来源(活动)
    public static final String FROM_ACTIVITY = "activity";
    //sync文件名
    public static final String SYNC_POINTS = "sync_";
    public static final String TASK_POINTS = "task_";
    public static final String FEEDBACK_RED = "feedback_";
    //下载是 普通、还是升级
    public static final String STATE_NORMAL = "normal";
    public static final String STATE_UPDATE = "update";


    public static boolean EXPOSURE_TOGGLE = true;
    public static String PAGE_EXTRA = "page_extra";


}
