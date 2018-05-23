package cn.lt.game.model;

import java.util.HashMap;
import java.util.Map;

import cn.lt.game.lib.util.H5Util;
import cn.lt.game.net.Net;
import cn.lt.game.ui.app.WebViewActivity;
import cn.lt.game.ui.app.awardgame.AwardActivity;
import cn.lt.game.ui.app.category.CategoryFragment;
import cn.lt.game.ui.app.category.CategoryHotCatsActivity;
import cn.lt.game.ui.app.community.CommunityActivity;
import cn.lt.game.ui.app.gameactive.GameActivitiesActivtiy;
import cn.lt.game.ui.app.gamedetail.GameDetailHomeActivity;
import cn.lt.game.ui.app.gamegift.GiftDetailActivity;
import cn.lt.game.ui.app.gamegift.GiftHomeActivity;
import cn.lt.game.ui.app.gamestrategy.GameStrategyHomeActivity;
import cn.lt.game.ui.app.hot.HotDetailActivity;
import cn.lt.game.ui.app.hot.HotFragment;
import cn.lt.game.ui.app.index.IndexFragment;
import cn.lt.game.ui.app.rank.RankMainFragment;
import cn.lt.game.ui.app.search.SearchTagActivity;
import cn.lt.game.ui.app.selectedgame.DownloadAdDialog;
import cn.lt.game.ui.app.specialtopic.SpecialTopicActivity;
import cn.lt.game.ui.app.specialtopic.SpecialTopicDetailsActivity;

public class PageMap {
    private volatile static PageMap mInstance = null;
    private Map<String, PageDetail> mMap;
    private boolean startFromFriend;

    private PageMap() {
        if (mMap == null) {
            mMap = new HashMap<>();
            mMap.put(EntryPages.YM_JX, new PageDetail.Builder().setDesc("精选页面").setClass(IndexFragment.class).build());

//            mMap.put(EntryPages.YM_JW, new PageDetail.Builder().setDesc("精选必玩页面").setClass(DownloadAdDialog.class).build());

            mMap.put(EntryPages.YM_PR, new PageDetail.Builder().setDesc("最热板块页面").setClass(RankMainFragment.class).build());

//            mMap.put(EntryPages.YM_PB, new PageDetail.Builder().setDesc("飙升板块页面").setClass(RankMainFragment.class).setNeedParam(true).setKey("rankItemid").build());
//
//            mMap.put(EntryPages.YM_PX, new PageDetail.Builder().setDesc("最新板块页面").setClass(RankMainFragment.class).setNeedParam(true).setKey("rankItemid").build());

            mMap.put(EntryPages.YM_FL, new PageDetail.Builder().setDesc("分类列表页面").setClass(CategoryFragment.class).build());

            mMap.put(EntryPages.YM_ZX, new PageDetail.Builder().setDesc("专题详情(页面)").setClass(SpecialTopicActivity.class).build());

            mMap.put(EntryPages.topic, new PageDetail.Builder().setDesc("专题详情(点击跳转用)").setClass(SpecialTopicDetailsActivity.class).setNeedParam(true).setKey("topicId").build());

            mMap.put(EntryPages.h5,new PageDetail.Builder().setDesc("H5").setClass(WebViewActivity.class).setNeedParam(true).setKey("gotoUrl").build());

            mMap.put(EntryPages.game, new PageDetail.Builder().setDesc("游戏详情").setClass(GameDetailHomeActivity.class).setNeedParam(true).setKey("id").build());

            mMap.put(EntryPages.applist, new PageDetail.Builder().setDesc("普通列表(热门分类)").setClass(CategoryHotCatsActivity.class).setNeedParam(true).setKey("id").setKey2("title").build());

//            mMap.put(EntryPages.YM_SQ, new PageDetail.Builder().setDesc("社区页面").build());

            mMap.put(EntryPages.YM_SF, new PageDetail.Builder().setDesc("社区-发现小组页面").build());

            mMap.put(EntryPages.YM_SH, new PageDetail.Builder().setDesc("小组话题页面").build());

            mMap.put(EntryPages.YM_LS, new PageDetail.Builder().setDesc("礼包搜索页面").build());

            mMap.put(EntryPages.YM_LW, new PageDetail.Builder().setDesc("我的礼包页面").build());

            mMap.put(EntryPages.YM_GS, new PageDetail.Builder().setDesc("攻略搜索匹配页面").setClass(GameStrategyHomeActivity.class).build());

            mMap.put(EntryPages.YM_GB, new PageDetail.Builder().setDesc("攻略列表页面").setClass(GameStrategyHomeActivity.class).build());

            mMap.put(EntryPages.YM_GX, new PageDetail.Builder().setDesc("攻略详情页面").setClass(GameStrategyHomeActivity.class).build());

//            mMap.put(EntryPages.YM_YX, new PageDetail.Builder().setDesc("游戏详情页面").setClass(GameDetailHomeActivity.class).setNeedParam(true).build());

            mMap.put(EntryPages.YM_SS, new PageDetail.Builder().setDesc("搜索页面").build());

            mMap.put(EntryPages.YM_SW, new PageDetail.Builder().setDesc("搜索无匹配页面").build());

            mMap.put(EntryPages.YM_ZT, new PageDetail.Builder().setDesc("专题列表页面").setClass(SpecialTopicActivity.class).build());

            mMap.put(EntryPages.YM_SZ, new PageDetail.Builder().setDesc("社区页面").setClass(CommunityActivity.class).build());

            mMap.put(EntryPages.YM_LB, new PageDetail.Builder().setDesc("礼包页面").setClass(GiftHomeActivity.class).build());

            mMap.put(EntryPages.YM_GZ, new PageDetail.Builder().setDesc("攻略中心页面").setClass(GameStrategyHomeActivity.class).build());

            mMap.put(EntryPages.YM_SL, new PageDetail.Builder().setDesc("首发页面").setClass(SearchTagActivity.class).build());

            mMap.put(EntryPages.YM_HD, new PageDetail.Builder().setDesc("活动列表页面").setClass(GameActivitiesActivtiy.class).build());

            mMap.put(EntryPages.gift, new PageDetail.Builder().setDesc("礼包详情").setClass(GiftDetailActivity.class).setNeedParam(true).setKey(GiftDetailActivity.GIFT_ID).build());

            mMap.put(EntryPages.routine_activity, new PageDetail.Builder().setDesc("常规活动").setClass(AwardActivity.class).build());

            mMap.put(EntryPages.hot_tab, new PageDetail.Builder().setDesc(EntryPages.hot_tab).setClass(HotFragment.class).build());

            mMap.put(EntryPages.hot_detail, new PageDetail.Builder().setDesc("内容详情").setClass(HotDetailActivity.class).setNeedParam(true).setKey(H5Util.HOT_DETAIL_URL).build());
        }
    }

    public static PageMap instance() {
        if (mInstance == null) {
            synchronized (Net.class) {
                if (mInstance == null) {
                    mInstance = new PageMap();
                }
            }
        }
        return mInstance;
    }

    public boolean hasInstance() {
        return mInstance != null && startFromFriend;
    }

    public void setStartFromFriend(boolean flag) {
        startFromFriend = flag;
    }

    public PageDetail getPageDetail(String page_name) {
        return mMap.get(page_name);
    }

    /**
     * 是否可识别的类型
     */
    public boolean isIdentifiable(String clickType) {
        return mMap.containsKey(clickType);
    }

}
