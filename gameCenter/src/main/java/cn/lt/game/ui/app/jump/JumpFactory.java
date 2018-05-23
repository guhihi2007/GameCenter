package cn.lt.game.ui.app.jump;

import cn.lt.game.domain.essence.DomainType;
import cn.lt.game.ui.app.adapter.PresentType;

/***
 * Created by Administrator on 2015/12/14.
 */
public class JumpFactory {

    public static IJumper produceJumper(PresentType pType, DomainType srcType) {
        IJumper jumper = null;
        switch (pType) {
            case hot_h5:
            case loading_page:
            case entry:
                jumper = Jumps.self().get(PageJumper.class.getSimpleName());
                if (jumper == null) {
                    jumper = new PageJumper();
                    Jumps.self().put(jumper);
                }
                break;
            case super_push:
            case hot:
            case game:
            case query_ads:
            case search_top10:
            case search_null:
                jumper = Jumps.self().get(GameToGameDetailJumper.class.getSimpleName());
                if (jumper == null) {
                    jumper = new GameToGameDetailJumper();
                    Jumps.self().put(jumper);
                }
                break;
            case banner:
            case carousel:
            case hot_cats:
                switch (srcType) {
                    case GAME:
                        jumper = Jumps.self().get(AdsToGameDetailJumper.class.getSimpleName());
                        if (jumper == null) {
                            jumper = new AdsToGameDetailJumper();
                            Jumps.self().put(jumper);
                        }
                        break;
                    case COMMUNITY:
                        jumper = Jumps.self().get(AdsToTopicJumper.class.getSimpleName());
                        if (jumper == null) {
                            jumper = new AdsToTopicJumper();
                            Jumps.self().put(jumper);
                        }
                        break;
                    case GIFTDETAIL:
                        jumper = Jumps.self().get(AdsToGiftDetailJumper.class.getSimpleName());
                        if (jumper == null) {
                            jumper = new AdsToGiftDetailJumper();
                            Jumps.self().put(jumper);
                        }
                        break;
                    case GAMEGIFTLIST:
                        jumper = Jumps.self().get(AdsToGiftListJumper.class.getSimpleName());
                        if (jumper == null) {
                            jumper = new AdsToGiftListJumper();
                            Jumps.self().put(jumper);
                        }
                        break;
                    case CAT:
                        break;
                    case TAG:
                        jumper = Jumps.self().get(AdsToGameDetailJumper.class.getSimpleName());
                        if (jumper == null) {
                            jumper = new AdsToGameDetailJumper();
                            Jumps.self().put(jumper);
                        }
                        break;
                    case ACTIVITY:
                        break;
                    case SPECIAL_TOPIC:
                        jumper = Jumps.self().get(SpecialTopicDetailJumper.class.getSimpleName());
                        if (jumper == null) {
                            jumper = new SpecialTopicDetailJumper();
                            Jumps.self().put(jumper);
                        }
                        break;
                    case SPECIAL_TOPIC_LIST:
                        // 专题列表
                        jumper = Jumps.self().get(SpecialTopicListJumper.class.getSimpleName());
                        if (jumper == null) {
                            jumper = new SpecialTopicListJumper();
                            Jumps.self().put(jumper);
                        }
                        break;
                    case H5:
                        jumper = Jumps.self().get(AdsToH5Jumper.class.getSimpleName());
                        if (jumper == null) {
                            jumper = new AdsToH5Jumper();
                            Jumps.self().put(jumper);
                        }
                        break;
                    case ROUTINE_ACTIVITIES:
                        jumper = Jumps.self().get(AwardJumper.class.getSimpleName());
                        if (jumper == null) {
                            jumper = new AwardJumper();
                            Jumps.self().put(jumper);
                        }
                        break;
                    case APPLIST:
                        jumper = Jumps.self().get(CatsToHotCatsJumper.class.getSimpleName());
                        if (jumper == null) {
                            jumper = new CatsToHotCatsJumper();
                            Jumps.self().put(jumper);
                        }
                        break;
                    case ACTIVITY_LIST:
                        // 跳活动列表页面
                        jumper = Jumps.self().get(ActivitiesListJumper.class.getSimpleName());
                        if (jumper == null) {
                            jumper = new ActivitiesListJumper();
                            Jumps.self().put(jumper);
                        }
                        break;
                    case HOT_TAB:
                        jumper = Jumps.self().get(HotTabJumper.class.getSimpleName());
                        if (jumper == null) {
                            jumper = new HotTabJumper();
                            Jumps.self().put(jumper);
                        }
                        break;
                    case HOT_DETAIL:
                        jumper = Jumps.self().get(HotDetailJumper.class.getSimpleName());
                        if (jumper == null) {
                            jumper = new HotDetailJumper();
                            Jumps.self().put(jumper);
                        }
                        break;
                    case PAGE:
                        break;
                    case KEY_WORD:
                        break;
                }
                break;
            case gifts_search_ofgame:
                jumper = Jumps.self().get(GameToGiftListJumper.class.getSimpleName());
                if (jumper == null) {
                    jumper = new GameToGiftListJumper();
                    Jumps.self().put(jumper);
                }
                break;
            case gifts_search_lists:
            case game_gifts_lists:
            case my_gifts:
            case hot_gifts:
            case new_gifts:
                jumper = Jumps.self().get(GiftToGiftDetailJumper.class.getSimpleName());
                if (jumper == null) {
                    jumper = new GiftToGiftDetailJumper();
                    Jumps.self().put(jumper);
                }
                break;
            case topic:
                jumper = Jumps.self().get(SpecialTopicDetailJumper.class.getSimpleName());
                if (jumper == null) {
                    jumper = new SpecialTopicDetailJumper();
                    Jumps.self().put(jumper);
                }
                break;
            case topic_detail:
            case game_gifts_summary:
            case game_detail:
            case all_cats:
            case gifts_detail:
            case get_gift_code:
            case activity:
            case hot_tags:
            case hot_words:
            case query_data:
            case comments:
            case update:
            case text_feedback:
            case image_feedback:
            case game_manage:
            case text:
                break;
        }
        return jumper;
    }

}
