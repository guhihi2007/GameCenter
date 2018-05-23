package cn.lt.game.domain;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.bean.CatBean;
import cn.lt.game.bean.ConfigureBean;
import cn.lt.game.bean.DataShowBean;
import cn.lt.game.bean.DataShowVO;
import cn.lt.game.bean.FeedBackBean;
import cn.lt.game.bean.GameCommentBean;
import cn.lt.game.bean.GameDetailBean;
import cn.lt.game.bean.GameInfoBean;
import cn.lt.game.bean.GiftBean;
import cn.lt.game.bean.NewUrlBean;
import cn.lt.game.bean.PushBaseBean;
import cn.lt.game.bean.TagAndWordBean;
import cn.lt.game.bean.TopicBean;
import cn.lt.game.bean.VersionInfoBean;
import cn.lt.game.domain.detail.FeedBackDomainDetail;
import cn.lt.game.domain.detail.GameCommentDomainDetail;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.domain.detail.GameDomainDetail;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.domain.essence.DomainType;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.domain.essence.FunctionEssenceImpl;
import cn.lt.game.domain.essence.IdentifierType;
import cn.lt.game.domain.essence.ImageType;
import cn.lt.game.lib.netdata.BaseBean;
import cn.lt.game.lib.netdata.BaseBeanList;
import cn.lt.game.model.EntryPages;
import cn.lt.game.model.PageMap;
import cn.lt.game.ui.app.adapter.PresentType;

/***
 * Created by Administrator on 2015/11/18.
 */
public class DataTransformer {

    @SuppressWarnings("unchecked")
    public static BaseUIModule getUIDataFromNetData(BaseBean netData) {
        PresentType type = PresentType.valueOf(netData.getType());
        BaseUIModule uiData;
        switch (type) {
            case carousel:
                uiData = getUIDataFromDataShowVOList(type, (BaseBeanList<DataShowVO>) netData);
                break;
            case entry:
                uiData = getUIDataFromDataShowBeanList(type, (BaseBeanList<DataShowBean>) netData);
                break;
            case super_push:
            case game:
            case deeplink:
//            case query_ads:
                uiData = getUIDataFromGameInfoBean(type, (GameInfoBean) netData);
                break;
            case hot:
            case search_top10:
            case game_manage:
//            case query_ads:
                uiData = getUIDataFromGameInfoBeanList(type, (BaseBeanList<GameInfoBean>) netData);
                break;
            case banner:
                uiData = getUIDataFromDataShowVO(type, (DataShowVO) netData);
                break;
            case game_detail:
                uiData = getUIDataFromGameDetailBean(type, (GameDetailBean) netData);
                break;
            case hot_cats:
            case all_cats:
                uiData = getUIDataFromCatBeanList(type, (BaseBeanList<CatBean>) netData);
                break;
            case topic:
            case topic_detail:
                uiData = getUIDataFromTopicBean(type, (TopicBean) netData);
                break;
            case new_gifts:
            case hot_gifts:
            case gifts_search_ofgame:
                uiData = getUIDataFromGiftBeanList(type, (BaseBeanList<GiftBean>) netData);
                break;
            case gifts_search_lists:
            case my_gifts:
            case game_gifts_summary:
            case gifts_detail:
            case game_gifts_lists:
            case get_gift_code:
                uiData = getUIDataFromGiftBean(type, (GiftBean) netData);
                break;
            case activity:
                uiData = getUIDataFromDataShowBean(type, (DataShowBean) netData);
                break;
            case search_null:
                uiData = getUIDataFromGameInfoBeanList(type, (BaseBeanList<GameInfoBean>) netData);
                break;
            case query_ads:
                uiData = getUIDataFromGameInfoBeanList(type, (BaseBeanList<GameInfoBean>) netData);
                break;
            case query_data:
            case hot_words:
            case hot_tags:
                uiData = getUIDataFromTagAndWordBeanList(type, (BaseBeanList<TagAndWordBean>) netData);
                break;
            case image_feedback:
            case text_feedback:
                uiData = getUIDataFromFeedBackBean(type, (FeedBackBean) netData);
                break;
            case comments:
                uiData = getUIDataFromGameCommentBean(type, (GameCommentBean) netData);
                break;
            case update:
                uiData = getUIDataFromVersionInfoBean(type, (VersionInfoBean) netData);
                break;
            case push_game:
            case push_app:
            case push_topic:
            case push_h5:
            case push_routine_activity:
            case push_hot_tab:
            case push_hot_detail:
            case push_deeplink:
                uiData = getUIDataFromPushBean(type, (PushBaseBean) netData);
                break;
            case popupwindow:
                uiData = getUIDataFromConfigure(type, (ConfigureBean) netData);
                break;
            default:
                uiData = null;
                break;
        }
        return uiData;
    }

    /**
     * 轮播图数据转换
     */
    private static UIModuleGroup<FunctionEssence> getUIDataFromDataShowVOList(PresentType type, BaseBeanList<DataShowVO> netData) {
        UIModuleGroup<FunctionEssence> uiData = new UIModuleGroup<>(type);
        /*把网络数据转换为UI数据*/
        for (DataShowVO netElem : netData) {

            /*通过click_type获取领域类型*/
            DomainType domainType = DomainType.getEnum(netElem.getRealClickType());

            if (domainType == DomainType.Invalid) {
                domainType = DomainType.getEnum(netElem.getClick_type());
            }

            /*生成对应领域类型的UI数据类型*/
            FunctionEssenceImpl uiElem = new FunctionEssenceImpl(domainType);

            // 没有High_click_type或者遇无法识别的跳转类型，默认使用旧版本的跳转数据
            if (TextUtils.isEmpty(netElem.getHigh_click_type()) || DomainType.getEnum(netElem.getRealClickType()) == DomainType.Invalid) {
                if (domainType == DomainType.H5 || domainType == DomainType.ACTIVITY) {
                    uiElem.setUniqueIdentifierByType(IdentifierType.URL, netElem.getUrl());
                } else {
                    uiElem.setUniqueIdentifierByType(IdentifierType.ID, netElem.getId());
                }
            } else {
                // 使用高版本跳转数据
                uiElem.verifyClickTypeDataByDomaintype(netElem.getHigh_resource(), netElem.getTitle());
            }


            /*(2)赋值图片链接值*/
            Map<ImageType, String> imgMap = new HashMap<>();
            imgMap.put(ImageType.COMMON, netElem.getImage_url());
            uiElem.setImageUrl(imgMap);

            /*存入总数据链表中*/
            uiData.add(uiElem);
        }
        /*返回转换后获得的UI数据*/
        return uiData.size() != 0 ? uiData : null;
    }

    /**
     * 入口数据装换
     */
    private static UIModuleGroup<FunctionEssence> getUIDataFromDataShowBeanList(PresentType type, BaseBeanList<DataShowBean> netData) {
        UIModuleGroup<FunctionEssence> uiData = new UIModuleGroup<>(type);
         /*把网络数据转换为UI数据*/
        for (DataShowBean netElem : netData) {
            /*通过click_type获取领域类型*/
            DomainType domainType = DomainType.PAGE;
            /*生成对应领域类型的UI数据类型*/
            FunctionEssenceImpl uiElem = new FunctionEssenceImpl(domainType);

            netElem.setRealClickType();

            if (!TextUtils.isEmpty(netElem.getHigh_click_type())
                    && PageMap.instance().isIdentifiable(netElem.getHigh_click_type())
                    && !netElem.getHigh_click_type().equals(EntryPages.h5)) {

                uiElem.verifyClickTypeDataByPageName(netElem.getHigh_click_type(), netElem.getData());

            } else if (!TextUtils.isEmpty(netElem.getPage_name_410())
                    && !netElem.getPage_name_410().equals(EntryPages.h5)) {

                uiElem.verifyClickTypeDataByPageName(netElem.getPage_name_410(), netElem.getData());

            } else {

                uiElem.setUniqueIdentifierByType(IdentifierType.ID, netElem.getId());
                uiElem.setUniqueIdentifierByType(IdentifierType.URL, netElem.getUrl());
            }

            /*(2)赋值图片链接值*/
            Map<ImageType, String> imgMap = new HashMap<>();
            imgMap.put(ImageType.COMMON, netElem.getImage_url());

            uiElem.setUniqueIdentifierByType(IdentifierType.NAME, netElem.getPage_name());

            uiElem.setImageUrl(imgMap);
            /*(3)赋值文字内容*/
            uiElem.setTitle(netElem.getTitle());
            /*(4)赋值颜色值*/
            uiElem.setColor(netElem.getColor());

            uiElem.setImage(netElem.getImage());

            uiElem.setPage_name_410(netElem.getPage_name_410());

            uiElem.setData(netElem.getData());

            uiElem.setHigh_click_type(netElem.getHigh_click_type());

            /*存入总数据链表中*/
            uiData.add(uiElem);
        }
        /*返回转换后获得的UI数据*/
        return uiData;
    }

    private static UIModule<GameDomainBaseDetail> getUIDataFromGameInfoBean(PresentType type, GameInfoBean netData) {
        UIModule<GameDomainBaseDetail> uiData = new UIModule<>(type);
         /*如果是特推，或者游戏卡片位*/
        GameDomainBaseDetail game = new GameDomainBaseDetail(netData);
        uiData.setData(game);
        /*返回转换后获得的UI数据*/
        return uiData;
    }

    private static UIModuleGroup<GameDomainBaseDetail> getUIDataFromGameInfoBeanList(PresentType type, BaseBeanList<GameInfoBean> netData) {
        UIModuleGroup<GameDomainBaseDetail> uiData = new UIModuleGroup<>(type);
         /*如果是热门游戏*/
         /*把网络数据转换为UI数据*/
        for (GameInfoBean netElem : netData) {
            GameDomainBaseDetail uiElem = new GameDomainBaseDetail(netElem);
            /*存入总数据链表中*/
            uiData.add(uiElem);
        }
        /*返回转换后获得的UI数据*/
        return uiData;
    }

    /**
     * 装换成banner数据
     */
    private static UIModule<FunctionEssence> getUIDataFromDataShowVO(PresentType type, DataShowVO netData) {
        UIModule<FunctionEssence> uiData = new UIModule<>(type);
        /*通过click_type获取领域类型*/
        DomainType domainType = DomainType.getEnum(netData.getRealClickType());

        if (domainType == DomainType.Invalid) {
            domainType = DomainType.getEnum(netData.getClick_type());
        }

        /*生成对应领域类型的UI数据类型*/
        FunctionEssenceImpl uiElem = new FunctionEssenceImpl(domainType);
        uiElem.setTitle(netData.getMark());

        // 没有High_click_type或者遇无法识别的跳转类型，默认使用旧版本的跳转数据
        if (TextUtils.isEmpty(netData.getHigh_click_type()) || DomainType.getEnum(netData.getRealClickType()) == DomainType.Invalid) {
            if (domainType == DomainType.H5 || domainType == DomainType.ACTIVITY) {
                uiElem.setUniqueIdentifierByType(IdentifierType.URL, netData.getUrl());
            } else {
                uiElem.setUniqueIdentifierByType(IdentifierType.ID, netData.getId());
            }
        } else {
            // 使用高版本跳转数据
            uiElem.verifyClickTypeDataByDomaintype(netData.getHigh_resource(), netData.getMark());
        }

        /*(2)赋值图片链接值*/
        Map<ImageType, String> imgMap = new HashMap<>();
        imgMap.put(ImageType.COMMON, netData.getImage_url());
        uiElem.setImageUrl(imgMap);
        /*(3)赋值文字内容*/

        /*(4)赋值颜色值*/
        uiElem.setColor(netData.getColor());
        uiData.setData(uiElem);
        /*返回转换后获得的UI数据*/
        return uiData;
    }

    private static UIModule<GameDomainDetail> getUIDataFromGameDetailBean(PresentType type, GameDetailBean netData) {
        UIModule<GameDomainDetail> uiData = new UIModule<>(type);
        /*如果是游戏详情*/
        /*把网络数据转换为UI数据*/
        GameDomainDetail uiElem = new GameDomainDetail(netData);
        /*存入总数据链表中*/
        uiData.setData(uiElem);
        /*返回转换后获得的UI数据*/
        return uiData;
    }

    /**
     * 热门分类或者所有分类 数据转换
     */
    private static UIModuleGroup<FunctionEssence> getUIDataFromCatBeanList(PresentType type, BaseBeanList<CatBean> netData) {
        UIModuleGroup<FunctionEssence> uiData = new UIModuleGroup<>(type);
         /*把网络数据转换为UI数据*/
        for (CatBean netElem : netData) {
            /*通过click_type获取领域类型*/
//            DomainType domainType = DomainType.CAT;
            DomainType domainType;
            FunctionEssenceImpl uiElem;

            netElem.setRealClickType();

            if (!TextUtils.isEmpty(netElem.getHigh_click_type()) && DomainType.getEnum(netElem.getHigh_click_type()) != DomainType.Invalid) {
                domainType = DomainType.getEnum(netElem.getHigh_click_type());
                uiElem = new FunctionEssenceImpl(domainType);
                uiElem.setHigh_click_type(netElem.getHigh_click_type());
                uiElem.verifyClickTypeDataByDomaintype(netElem.getHigh_resource(), netElem.getTitle());
            } else {
                domainType = DomainType.APPLIST;
                uiElem = new FunctionEssenceImpl(domainType);
                uiElem.setUniqueIdentifierByType(IdentifierType.ID, netElem.getId());
                uiElem.setTitle(netElem.getTitle());
            }

            /*(2)赋值图片链接值*/
            Map<ImageType, String> imgMap = new HashMap<>();
            imgMap.put(ImageType.COMMON, netElem.getImage_url());
            uiElem.setImageUrl(imgMap);
            uiElem.setImage(netElem.getImage());//添加一个分类入口的字段解析 added by ATian at 2016/11/24

            /*(4)赋值颜色值*/
            uiElem.setColor(netElem.getColor());

            /**/
            if (netElem.getTags() != null && netElem.getTags().size() != 0) {
                uiElem.setHasSubFuncEss(true);
                List<FunctionEssence> subFuncEss = new ArrayList<>();
                uiElem.setSubFunctionEssence(subFuncEss);
                for (TagAndWordBean tag : netElem.getTags()) {
                    /*生成对应领域类型的UI数据类型*/
                    FunctionEssenceImpl tagFuncEss = new FunctionEssenceImpl(DomainType.TAG);
                    /*(1)赋值领域唯一标识*/
                    tagFuncEss.setUniqueIdentifierByType(IdentifierType.ID, tag.getId());
                    /*(3)赋值文字内容*/
                    tagFuncEss.setTitle(tag.getTitle());
                    subFuncEss.add(tagFuncEss);
                }
            }
            /*存入总数据链表中*/
            uiData.add(uiElem);
        }

        /*返回转换后获得的UI数据*/
        return uiData;
    }

    private static UIModule<FunctionEssence> getUIDataFromTopicBean(PresentType type, TopicBean netData) {
        UIModule<FunctionEssence> uiData = new UIModule<>(type);
        /*如果是专题，或是专题详情*/
        /*生成对应领域类型的UI数据类型*/
        FunctionEssenceImpl uiElem = new FunctionEssenceImpl(DomainType.SPECIAL_TOPIC);
        /*(1)赋值领域唯一标识*/
        uiElem.setUniqueIdentifierByType(IdentifierType.ID, netData.getId());
        /*(2)赋值图片链接值*/
        Map<ImageType, String> imgMap = new HashMap<>();
        imgMap.put(ImageType.COMMON, netData.getImage_url());
        uiElem.setImageUrl(imgMap);
        /*(3)赋值文字内容*/
        uiElem.setTitle(netData.getTitle());
        /*(4)赋值summary*/
        uiElem.setSummary(netData.getSummary());
        /*(4)赋值发布时间*/
        uiElem.setUpdateTime(netData.getUpdated_at());
        /*存入总数据链表中*/
        uiData.setData(uiElem);

        /*返回转换后获得的UI数据*/
        return uiData;
    }

    private static UIModuleGroup<GiftDomainDetail> getUIDataFromGiftBeanList(PresentType type, BaseBeanList<GiftBean> netData) {
        UIModuleGroup<GiftDomainDetail> uiData = new UIModuleGroup<>(type);
         /*把网络数据转换为UI数据*/
        for (GiftBean netElem : netData) {
            GiftDomainDetail uiElem = new GiftDomainDetail(netElem);
            /*存入总数据链表中*/
            uiData.add(uiElem);
        }

        /*返回转换后获得的UI数据*/
        return uiData;
    }

    private static UIModule<GiftDomainDetail> getUIDataFromGiftBean(PresentType type, GiftBean netData) {
        UIModule<GiftDomainDetail> uiData = new UIModule<>(type);
        /*把网络数据转换为UI数据*/
        GiftDomainDetail uiElem = new GiftDomainDetail(netData);
        /*存入总数据链表中*/
        uiData.setData(uiElem);
        /*返回转换后获得的UI数据*/
        return uiData;
    }

    private static UIModule<FunctionEssence> getUIDataFromDataShowBean(PresentType type, DataShowBean netData) {
        UIModule<FunctionEssence> uiData = new UIModule<>(type);
        /*把网络数据转换为UI数据*/
        /*通过click_type获取领域类型*/
        DomainType domainType = DomainType.ACTIVITY;
        /*生成对应领域类型的UI数据类型*/
        FunctionEssenceImpl uiElem = new FunctionEssenceImpl(domainType);
        /*(1)赋值领域唯一标识*/
        uiElem.setUniqueIdentifierByType(IdentifierType.ID, netData.getId());
        /*(2)赋值图片链接值*/
        Map<ImageType, String> imgMap = new HashMap<>();
        imgMap.put(ImageType.COMMON, netData.getImage_url());
        imgMap.put(ImageType.BIG, netData.getBig_image_url());
        uiElem.setImageUrl(imgMap);
        /*(3)赋值文字内容*/
        uiElem.setTitle(netData.getTitle());
        /*(4)赋值活动简介*/
        uiElem.setSummary(netData.getSummary());
        /*(5)赋值更新时间*/
        uiElem.setUpdateTime(netData.getStocked_at());
        /*(6)赋值结束时间*/
        uiElem.setEndTime(netData.getUnstocked_at());
        /*(4)赋值活动连接url*/
        uiElem.setUniqueIdentifierByType(IdentifierType.URL, netData.getUrl());

        /*存入总数据链表中*/
        uiData.setData(uiElem);

        /*返回转换后获得的UI数据*/
        return uiData;
    }

    private static UIModuleGroup<FunctionEssence> getUIDataFromTagAndWordBeanList(PresentType type, BaseBeanList<TagAndWordBean> netData) {
        UIModuleGroup<FunctionEssence> uiData = new UIModuleGroup<>(type);
        /*如果是热门分类，或者所有分类*/
        /*把网络数据转换为UI数据*/
        for (TagAndWordBean netElem : netData) {
            /*通过click_type获取领域类型*/
            DomainType domainType = DomainType.TAG;
            /*生成对应领域类型的UI数据类型*/
            FunctionEssenceImpl uiElem = new FunctionEssenceImpl(domainType);
            /*(1)赋值领域唯一标识*/
            uiElem.setUniqueIdentifierByType(IdentifierType.ID, netElem.getId());
                    /*(2)赋值图片链接值*/
            Map<ImageType, String> imgMap = new HashMap<>();
            imgMap.put(ImageType.COMMON, netElem.getIcon_url());
//            imgMap.put(ImageType.BIG, netData.getBig_image_url());
            uiElem.setImageUrl(imgMap);

            /*(3)赋值文字内容*/
            uiElem.setTitle(netElem.getTitle());

            /*存入总数据链表中*/
            uiData.add(uiElem);
        }

        /*返回转换后获得的UI数据*/
        return uiData;
    }

    private static UIModuleGroup<FunctionEssence> getUIDataFromStringList(PresentType type, BaseBeanList<String> netData) {
        UIModuleGroup<FunctionEssence> uiData = new UIModuleGroup<>(type);
        /*如果是热门分类，或者所有分类*/
        /*把网络数据转换为UI数据*/
        for (String netElem : netData) {
            /*通过click_type获取领域类型*/
            DomainType domainType = DomainType.KEY_WORD;
            /*生成对应领域类型的UI数据类型*/
            FunctionEssenceImpl uiElem = new FunctionEssenceImpl(domainType);
            /*(3)赋值文字内容*/
            uiElem.setTitle(netElem);

            /*存入总数据链表中*/
            uiData.add(uiElem);
        }

        /*返回转换后获得的UI数据*/
        return uiData;
    }

    private static UIModule<GameCommentDomainDetail> getUIDataFromGameCommentBean(PresentType type, GameCommentBean netData) {
        UIModule<GameCommentDomainDetail> uiData = new UIModule<>(type);
        GameCommentDomainDetail uiTmp = new GameCommentDomainDetail(netData);
        uiData.setData(uiTmp);
        /*返回转换后获得的UI数据*/
        return uiData;
    }

    private static UIModule<FeedBackDomainDetail> getUIDataFromFeedBackBean(PresentType type, FeedBackBean netData) {
        UIModule<FeedBackDomainDetail> uiData = new UIModule<>(type);
        FeedBackDomainDetail uiTmp = new FeedBackDomainDetail(netData);
        uiData.setData(uiTmp);
        /*返回转换后获得的UI数据*/
        return uiData;
    }

    private static UIModule<VersionInfoBean> getUIDataFromVersionInfoBean(PresentType type, VersionInfoBean netData) {
        UIModule<VersionInfoBean> uiData = new UIModule<>(type);
        uiData.setData(netData);
        /*返回转换后获得的UI数据*/
        return uiData;
    }

    private static UIModule<PushBaseBean> getUIDataFromPushBean(PresentType type, PushBaseBean netData) {
        UIModule<PushBaseBean> uiData = new UIModule<>(type);
        uiData.setData(netData);
        /*返回转换后获得的UI数据*/
        return uiData;
    }

    private static UIModule<NewUrlBean> getUIDataFromRetryDownload(PresentType type, NewUrlBean netData) {
        UIModule<NewUrlBean> uiData = new UIModule<>(type);
        uiData.setData(netData);
        /*返回转换后获得的UI数据*/
        return uiData;
    }

    private static UIModule<ConfigureBean> getUIDataFromConfigure(PresentType type, ConfigureBean netData) {
        UIModule<ConfigureBean> uiData = new UIModule<>(type);
        uiData.setData(netData);
        return uiData;
    }
}
