package cn.lt.game.datalayer;

import java.util.HashMap;
import java.util.Map;

import cn.lt.game.net.Host;
import cn.lt.game.net.Uri2;

/**
 * Created by Administrator on 2015/11/25.
 */
class NetReqConfigInfo {

    private static Map<EventId, NetReqInfo> netReqInfoMap = new HashMap<>();

    static {
        /*首页*/
        netReqInfoMap.put(EventId.INDEX, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.INDEX_URI;
            }
        }));
        /*精选必玩*/
        netReqInfoMap.put(EventId.NECESSARY, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.START_URI;
            }
        }));
        /*排行*/
        netReqInfoMap.put(EventId.RANK, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.GAMES_RANK_URI;
            }
        }));
        /*分类*/
        netReqInfoMap.put(EventId.CAT, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.CATS_URI;
            }
        }));
        /*热门分类游戏列表*/
        netReqInfoMap.put(EventId.HOT_CATS, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.HOT_CATS_URI;
            }
        }));
        /*分类游戏列表*/
        netReqInfoMap.put(EventId.CAT_DETAIL, new NetReqInfo(new OnGetUriByParam() {

            @Override
            protected String getUriBy(String catId) {
                return Uri2.getCatsListUri(catId);
            }
        }));
        /*专题列表*/
        netReqInfoMap.put(EventId.SPECIAL_TOPICS, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.SPECIAL_TOPICS_URI;
            }
        }));
        /*专题详情（专题游戏列表）*/
        netReqInfoMap.put(EventId.SPECIAL_TOPICS_DETAIL, new NetReqInfo(new OnGetUriByParam() {

            @Override
            protected String getUriBy(String specialTopicId) {
                return Uri2.getSpecialTopicDetailUri(specialTopicId);
            }
        }));
        /*游戏详情*/
        netReqInfoMap.put(EventId.GAME_DETAIL, new NetReqInfo(new OnGetUriByIdOrPkgName() {

            @Override
            protected String getUriByIdOrPkgName(String idOrPkgName) {
                return Uri2.getGameDetailUriByIdOrPkgName(idOrPkgName);
            }
        }));
        /*礼包首页*/
        netReqInfoMap.put(EventId.GIFTS_INDEX, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.GIFTS_URI;
            }
        }));
        /*礼包搜索*/
        netReqInfoMap.put(EventId.GIFTS_SEARCH, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.GIFTS_URI;
            }
        }));
        /*我的礼包*/
        netReqInfoMap.put(EventId.GIFTS_MY, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.GIFTS_MY;
            }
        }));
        /*游戏礼包*/
        netReqInfoMap.put(EventId.GIFTS_GAME, new NetReqInfo(new OnGetUriByParam() {

            @Override
            protected String getUriBy(String gameId) {
                return Uri2.getGiftListUri(gameId);
            }
        }));
        /*礼包详情*/
        netReqInfoMap.put(EventId.GIFTS_DETAIL, new NetReqInfo(new OnGetUriByParam() {


            @Override
            protected String getUriBy(String giftId) {
                return Uri2.getGiftDetailUri(giftId);
            }
        }));
        /*领取礼包*/
        netReqInfoMap.put(EventId.GIFTS_OBTAIN, new NetReqInfo(new OnGetUriByParam() {

            @Override
            protected String getUriBy(String giftId) {
                return Uri2.getObtainGiftUri(giftId);
            }
        }));
        /*活动*/
        netReqInfoMap.put(EventId.ACTIVITIES, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.ACTIVITIES_URI;
            }
        }));
        /*首发*/
        netReqInfoMap.put(EventId.FIRST_ISSUE, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.FIRST_PUBLISH_URI;
            }
        }));
        /*搜索首页*/
        netReqInfoMap.put(EventId.SEARCH_INDEX, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.SEARCH_URI;
            }
        }));
        /*搜索*/
        netReqInfoMap.put(EventId.SEARCH, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.SEARCH_URI;
            }
        }));
        /*搜索关键字自动匹配*/
        netReqInfoMap.put(EventId.AUTO_COMPLETE, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.SEARCH_AUTOCOMPLETE_URI;
            }
        }));
        /*获取游戏评论*/
        netReqInfoMap.put(EventId.COMMENTS_GAME, new NetReqInfo(new OnGetUriByParam() {

            @Override
            protected String getUriBy(String gameId) {
                return Uri2.getGameCommentsUri(gameId);
            }
        }));
        /*发表游戏评论*/
        netReqInfoMap.put(EventId.COMMENTS_GAME_PUBLISH, new NetReqInfo(NetMethodType.POST, new OnGetUriByParam() {

            @Override
            protected String getUriBy(String gameId) {
                return Uri2.getGameCommentsUri(gameId);
            }
        }));
        /*检查平台升级*/
        netReqInfoMap.put(EventId.PLATFORM_UPDATE_CHECK, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.CLIENT_UPDATE_URI;
            }
        }));
        /*游戏管理相关，获取本地游戏升级信息等*/
        netReqInfoMap.put(EventId.LOCAL_GAME_MANAGE, new NetReqInfo(NetMethodType.POST, new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.GAME_MANAGER_URI;
            }
        }));
        /*游戏管理相关，获取本地游戏升级信息等*/
        netReqInfoMap.put(EventId.GAME_SYNC, new NetReqInfo(NetMethodType.POST, new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.GAME_MANAGER_URI;
            }
        }));
        /*游戏管理相关，获取本地可覆盖游戏*/
        netReqInfoMap.put(EventId.COVER_SYNC, new NetReqInfo(NetMethodType.POST, new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.COVER_SYNC;
            }
        }));
        /*获取反馈信息*/
        netReqInfoMap.put(EventId.FEEDBACK, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.FEEDBACKS_URI;
            }
        }));
        /*提交反馈*/
        netReqInfoMap.put(EventId.FEEDBACK_PUBLISH, new NetReqInfo(NetMethodType.POST, new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.FEEDBACKS_URI;
            }
        }));
        /*热门标签详情页*/
        netReqInfoMap.put(EventId.HOT_TAG_DETAIL, new NetReqInfo(new OnGetUriByParam() {

            @Override
            protected String getUriBy(String tagId) {
                return Uri2.getHotTagDetailUri(tagId);
            }
        }));
        /*个推资源请求*/
        netReqInfoMap.put(EventId.PUSH_DETAIL, new NetReqInfo(new OnGetUriByParam() {
            @Override
            protected String getUriBy(String pushId) {
                return Uri2.getPushUri(pushId);
            }
        }));
        /*统计请求*/
        netReqInfoMap.put(EventId.DATA_STATISTIC, new NetReqInfo(NetMethodType.POST, Host.HostType.DCENTER_HOST, new OnGetUri() {
            @Override
            String getUri(Map<String, ?> params) {
                return "";
            }
        }));

                /*搜索*/
        netReqInfoMap.put(EventId.STRATEGY_SEARCH, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.SEARCH_URI;
            }
        }));
         /*安装游戏时请求游戏详情*/
        netReqInfoMap.put(EventId.GAME_DETAIL_FOR_INSTALL, new NetReqInfo(new OnGetUriByIdOrPkgName() {

            @Override
            protected String getUriByIdOrPkgName(String idOrPkgName) {
                return Uri2.getGameDetailUriByIdOrPkgName(idOrPkgName);
            }
        }));

         /*获取游戏新的下载地址和md5*/
        netReqInfoMap.put(EventId.GAME_NEW_URL_MD5, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.RETRY_DOWNLOAD;
            }
        }));

        /*沉默用户通知数据请求*/
        netReqInfoMap.put(EventId.SILENCE_USER_NOTICE, new NetReqInfo(NetMethodType.GET, Host.HostType.GCENTER_HOST, new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.SILENCE_USER_NOTICE_URI;
            }
        }));
        /*弹窗管理*/
        netReqInfoMap.put(EventId.POPWINDOW, new NetReqInfo(new OnGetUri() {

            @Override
            public String getUri(Map<String, ?> params) {
                return Uri2.POPUP_INTERVAL_URI;
            }
        }));
    }

    static NetReqInfo get(EventId eventId) {
        return netReqInfoMap.get(eventId);
    }

    private static abstract class OnGetUriByIdOrPkgName extends OnGetUri {

        protected abstract String getUriByIdOrPkgName(String idOrPkgName);

        @Override
        public String getUri(Map<String, ?> params) {
            if (params == null) return "";
            String idOrPkgName = "";
            String id = getId(params);
            if (id != null) {
                idOrPkgName = id;
                removeId(params);
            } else {
                String pkgName = getPkgName(params);
                if (pkgName != null) {
                    idOrPkgName = pkgName;
                    removePkgName(params);
                }
            }
            return idOrPkgName == null ? "" : getUriByIdOrPkgName(idOrPkgName);
        }

        public String getId(Map<String, ?> params) {
            return (String) params.get(NetParamName.ID.toString());
        }

        public String getPkgName(Map<String, ?> params) {
            return (String) params.get(NetParamName.PKG_NAME.toString());
        }

        public void removeId(Map<String, ?> params) {
            params.remove(NetParamName.ID.toString());
        }

        public void removePkgName(Map<String, ?> params) {
            params.remove(NetParamName.PKG_NAME.toString());
        }
    }

    private static abstract class OnGetUriByParam extends OnGetUri {
        private String paramName;

        OnGetUriByParam(String paramName) {
            this.paramName = paramName;
        }

        OnGetUriByParam() {
            this.paramName = NetParamName.ID.toString();
        }

        protected abstract String getUriBy(String param);

        @Override
        public String getUri(Map<String, ?> params) {
            if (params == null) return null;
            String param = getParam(params);
            if (param != null) {
                removeParam(params);
            }
            return param == null ? null : getUriBy(param);
        }

        private String getParam(Map<String, ?> params) {
            try {
                return params.get(paramName).toString();
            } catch (Exception e) {
                return null;
            }
        }

        private void removeParam(Map<String, ?> params) {
            params.remove(paramName);
        }
    }
}
