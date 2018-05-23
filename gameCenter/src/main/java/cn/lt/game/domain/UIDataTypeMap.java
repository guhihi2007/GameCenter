package cn.lt.game.domain;

import cn.lt.game.domain.detail.FeedBackDomainDetail;
import cn.lt.game.domain.detail.GameCommentDomainDetail;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.domain.detail.GameDomainDetail;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.ui.app.adapter.PresentType;

/***
 * Created by Administrator on 2015/11/19.
 */
public class UIDataTypeMap {
    private static UIDataTypeMap ourInstance = new UIDataTypeMap();
    public static UIDataTypeMap getInstance() {
        return ourInstance;
    }
    private UIDataTypeMap() {
    }

    private static Object[][] map = {
            {PresentType.carousel,             UIModuleGroup.class,  FunctionEssence.class},
            {PresentType.entry,                UIModuleGroup.class,  FunctionEssence.class},
            {PresentType.super_push,           UIModule.class,            GameDomainBaseDetail.class},
            {PresentType.game,                 UIModule.class,            GameDomainBaseDetail.class},
            {PresentType.query_ads,            UIModule.class,            GameDomainBaseDetail.class},
            {PresentType.hot,                  UIModuleGroup.class,  GameDomainBaseDetail.class},
            {PresentType.search_top10,         UIModuleGroup.class,  GameDomainBaseDetail.class},
            {PresentType.banner,               UIModule.class,            FunctionEssence.class},
            {PresentType.game_detail,          UIModule.class,            GameDomainDetail.class},
            {PresentType.hot_cats,             UIModuleGroup.class,  FunctionEssence.class},
            {PresentType.all_cats,             UIModuleGroup.class,  FunctionEssence.class},
            {PresentType.topic,        UIModule.class,            FunctionEssence.class},
            {PresentType.topic_detail, UIModule.class,            FunctionEssence.class},
            {PresentType.hot_gifts,            UIModuleGroup.class,  GiftDomainDetail.class},
            {PresentType.new_gifts,            UIModuleGroup.class,  GiftDomainDetail.class},
            {PresentType.gifts_search_ofgame, UIModuleGroup.class,  GiftDomainDetail.class},
            {PresentType.gifts_search_lists,   UIModule.class,            GiftDomainDetail.class},
            {PresentType.my_gifts,             UIModule.class,            GiftDomainDetail.class},
            {PresentType.game_gifts_summary,   UIModule.class,            GiftDomainDetail.class},
            {PresentType.gifts_detail,         UIModule.class,            GiftDomainDetail.class},
            {PresentType.activity,             UIModule.class,            FunctionEssence.class},
            {PresentType.hot_words,            UIModuleGroup.class,  FunctionEssence.class},
            {PresentType.hot_tags,             UIModuleGroup.class,  FunctionEssence.class},
            {PresentType.search_null,          UIModuleGroup.class,  FunctionEssence.class},
            {PresentType.query_data,           UIModuleGroup.class,  FunctionEssence.class},
            {PresentType.comments,             UIModule.class,            GameCommentDomainDetail.class},
            {PresentType.text_feedback,        UIModule.class,            FeedBackDomainDetail.class},
            {PresentType.image_feedback,       UIModule.class,            FeedBackDomainDetail.class},
            {PresentType.game_manage,          UIModuleGroup.class,       GameDomainBaseDetail.class},
    };
}
