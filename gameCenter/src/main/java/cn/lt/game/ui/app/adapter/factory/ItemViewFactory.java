package cn.lt.game.ui.app.adapter.factory;

import android.content.Context;

import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.weight.IndexItemEntryView;
import cn.lt.game.ui.app.adapter.weight.ItemBannerView;
import cn.lt.game.ui.app.adapter.weight.ItemDeeplinkView;
import cn.lt.game.ui.app.adapter.weight.ItemGameActivityView;
import cn.lt.game.ui.app.adapter.weight.ItemGarbGameView;
import cn.lt.game.ui.app.adapter.weight.ItemGarbGiftView;
import cn.lt.game.ui.app.adapter.weight.ItemGiftLastestView;
import cn.lt.game.ui.app.adapter.weight.ItemGiftMyView;
import cn.lt.game.ui.app.adapter.weight.ItemGiftNormalView;
import cn.lt.game.ui.app.adapter.weight.ItemNewGiftTitleView;
import cn.lt.game.ui.app.adapter.weight.ItemSearchNoDataHeadView;
import cn.lt.game.ui.app.adapter.weight.ItemSingleGameView;
import cn.lt.game.ui.app.adapter.weight.ItemTextView;
import cn.lt.game.ui.app.adapter.weight.ItemView;
import cn.lt.game.ui.app.adapter.weight.ItemViewNull;
import cn.lt.game.ui.app.category.AllCatsView;
import cn.lt.game.ui.app.category.HotCatsView;
import cn.lt.game.ui.app.gameactive.widget.ItemActivitiesView;
import cn.lt.game.ui.app.specialtopic.widget.ItemSpecialTopicView;

/***
 * Created by Administrator on 2015/11/23.
 */
public class ItemViewFactory {

    public static ItemView produceItemView(PresentType presentType, BaseOnclickListener
            clickListener, Context context) {
        switch (presentType) {
            case game_gifts_lists:
                return new ItemGiftNormalView(context, clickListener);
            case entry:
                return new IndexItemEntryView(context, clickListener);
            case hot:
                return new ItemGarbGameView(context, clickListener);
            case banner:
                return new ItemBannerView(context, clickListener);
            case super_push:
            case game:
            case search_null:
            case search_top10:
                return new ItemSingleGameView(context, clickListener);
            case hot_cats:
                return new HotCatsView(context, clickListener);  //分类fragment的热门分类
            case all_cats:
                return new AllCatsView(context, clickListener); //分类fragment的所有分类
            case topic:
                return new ItemSpecialTopicView(context, clickListener);
            case activity:
                return new ItemActivitiesView(context, clickListener);
            case hot_gifts:
            case gifts_search_ofgame:
                return new ItemGarbGiftView(context, clickListener);
            case new_gifts:
            case gifts_search_lists:
                return new ItemGiftLastestView(context, clickListener);
            case my_gifts:
                return new ItemGiftMyView(context, clickListener);
            case text:
                return new ItemTextView(context, clickListener);
            case new_gifts_title:
                return new ItemNewGiftTitleView(context, clickListener);
            case game_activity:
                return new ItemGameActivityView(context, clickListener);
            case deeplink:
                return new ItemDeeplinkView(context, clickListener);
            case search_null_head:
                return new ItemSearchNoDataHeadView(context,clickListener);
            case game_gifts_summary:
            case game_detail:
            case gifts_detail:
            case get_gift_code:
            case topic_detail:
            case hot_tags:
            case hot_words:
            case query_ads:
            case query_data:
            case comments:
            case update:
            case text_feedback:
            case image_feedback:
            case game_manage:
                return new ItemViewNull(context);
        }

        return null;
    }
}
