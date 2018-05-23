package cn.lt.game.ui.app.adapter;

import java.util.concurrent.atomic.AtomicInteger;

import cn.lt.game.ui.app.adapter.parser.NetDataAddShellDir;

/***
 * Created by Administrator on 2015/11/12.
 */
public enum PresentType {
    /**
     * 轮播图；
     */
    carousel(),
    /**
     * 入口；
     */
    entry(),
    /**
     * 特推；
     */
    super_push(),
    /**
     * 最热游戏；
     */
    hot(),
    /**
     * banner；
     */
    banner(),
    /**
     * 游戏；
     */
    game(),
    /**
     * 游戏详情；
     */
    game_detail(),
    /**
     * 热门分类；
     */
    hot_cats(),
    /**
     * 所有分类；
     */
    all_cats(),
    /**
     * 专题；
     */
    topic(),
    /**
     * 专题详情；
     */
    topic_detail(),
    /**
     * 最热礼包；
     */
    hot_gifts(),
    /**
     * 最新礼包；
     */
    new_gifts(),
    /**
     * 最新礼包标题；
     */
    new_gifts_title(),
    /**
     * 礼包搜索by游戏；
     */
    gifts_search_ofgame(),
    /**
     * 礼包所有by 礼包；
     */
    gifts_search_lists(),
    /**
     * 我的礼包；
     */
    my_gifts(),
    /**
     * 游戏礼包列表的游戏描述；
     */
    game_gifts_summary(),
    /**
     * 游戏礼包列表；
     */
    game_gifts_lists(),
    /**
     * 礼包详情；
     */
    gifts_detail(),
    /**
     * 礼包返回码；
     */
    get_gift_code(),
    /**
     * 活动；
     */
    activity(),
    /**
     * 热门标签；
     */
    hot_tags(),
    /**
     * 热词；
     */
    hot_words(),
    /**
     * 搜索top10；
     */
    search_top10(),
    /**
     * 搜索无结果返回；
     */
    search_null(),
    /**
     * 搜索无结果返回；
     */
    search_null_head(),
    /**
     * 搜索自动匹配返回结果；
     */
    query_ads(),
    /**
     * 搜索成功返回数据；
     */
    query_data(),
    /**
     * 评论；
     */
    comments(),
    /**
     * 更新；
     */
    update(),
    /**
     * 文字反馈；
     */
    text_feedback(),
    /**
     * 图片反馈；
     */
    image_feedback(),
    /**
     * 游戏管理
     */
    game_manage(),
    /**
     * 文本信息
     */
    text(),
    /**
     * 推送游戏
     */
    push_game(),
    /**
     * 推送专题
     */
    push_topic(),
    /**
     * 推送平台版本升级；
     */
    push_app(),

    /**
     * 获取重试下载新地址
     */
    retrydownload(),

    /**
     * 弹窗管理
     */
    popupwindow(),

    /**
     * 游戏活动
     */
    game_activity(),

    /**
     * 分类标签(实际只有数据上报才会用到)
     */
    label(),
    /***
     * H5 推送
     */
    push_h5(),

    /***
     * 常规活动 推送
     */
    push_routine_activity(),

    /***
     * 启动页
     */
    loading_page(),

    /**
     * 热点h5
     */
    hot_h5(),

    /**
     * 推送内容tab
     */
    push_hot_tab(),

    /**
     * 推送内容详情
     */
    push_hot_detail(),

    /***
     * deepLink 推送
     */
    push_deeplink(),

    /**
     * deeplink搜索
     */
    deeplink();

    public int viewType;
    public String presentType;

    PresentType() {
        this.presentType = this.toString();
        this.viewType = Atomicln.ai.incrementAndGet() - 1;
    }

    public NetDataAddShellDir needSplit() {
        NetDataAddShellDir dir = new NetDataAddShellDir();
        switch (this) {
            case carousel:
            case entry:
            case hot_tags:
            case hot_words:
            case hot_cats:
                dir.setNeedSplit(false);
                dir.setIsWhole(true);
                dir.setNumberOfLine(0);
                break;
            case hot:
                dir.setNeedSplit(true);
                dir.setIsWhole(false);
                dir.setNumberOfLine(4);
                break;
            case hot_gifts:
            case gifts_search_ofgame:
                dir.setNeedSplit(true);
                dir.setIsWhole(false);
                dir.setNumberOfLine(3);
                break;
            case super_push:
            case banner:
            case game:
            case new_gifts:
            case all_cats:
                dir.setNeedSplit(false);
                dir.setIsWhole(false);
                dir.setNumberOfLine(0);
            case game_detail:

            case topic:
            case topic_detail:
            case gifts_search_lists:
            case my_gifts:
            case game_gifts_summary:
            case game_gifts_lists:
                break;
            case gifts_detail:
            case get_gift_code:
            case activity:
            case search_top10:
            case search_null:
            case query_ads:
            case query_data:
            case comments:
            case update:
            case text_feedback:
            case image_feedback:
                break;
            case game_manage:
                break;
            case text:
                break;
        }
        return dir;
    }

    static class Atomicln {
        public static AtomicInteger ai = new AtomicInteger();
    }

}
