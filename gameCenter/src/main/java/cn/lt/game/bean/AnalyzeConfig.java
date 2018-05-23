package cn.lt.game.bean;

import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import cn.lt.game.lib.netdata.AnalyzeJsonBean;
import cn.lt.game.lib.netdata.BaseBeanList;
import cn.lt.game.lib.util.LogUtils;

/**
 * Created by Administrator on 2015/11/14.
 */
public class AnalyzeConfig {
    public static Map<String, AnalyzeJsonBean> typeMap;

    public static Map<String, AnalyzeJsonBean> getMap() {
        if (typeMap == null) {
            typeMap = new HashMap<>();
            typeMap.put("carousel", new AnalyzeJsonBean(true, new TypeToken<BaseBeanList<DataShowVO>>() {}));

            typeMap.put("entry", new AnalyzeJsonBean(true, new TypeToken<BaseBeanList<DataShowBean>>() {}));

            typeMap.put("super_push", new AnalyzeJsonBean(true, new TypeToken<GameInfoBean>() {}));

            typeMap.put("hot", new AnalyzeJsonBean(true, new TypeToken<BaseBeanList<GameInfoBean>>() {}));

            typeMap.put("banner", new AnalyzeJsonBean(true, new TypeToken<DataShowVO>() {}));

            typeMap.put("game", new AnalyzeJsonBean(true, new TypeToken<GameInfoBean>() {}));

            typeMap.put("game_detail", new AnalyzeJsonBean(true, new TypeToken<GameDetailBean>() {}));

            typeMap.put("hot_cats", new AnalyzeJsonBean(true, new TypeToken<BaseBeanList<CatBean>>() {}));

            typeMap.put("all_cats", new AnalyzeJsonBean(true, new TypeToken<BaseBeanList<CatBean>>() {}));

            typeMap.put("topic", new AnalyzeJsonBean(true, new TypeToken<TopicBean>() {}));

            typeMap.put("topic_detail", new AnalyzeJsonBean(true, new TypeToken<TopicBean>() {}));

            typeMap.put("hot_gifts", new AnalyzeJsonBean(true, new TypeToken<BaseBeanList<GiftBean>>() {}));

            typeMap.put("new_gifts", new AnalyzeJsonBean(true, new TypeToken<BaseBeanList<GiftBean>>() {}));

            typeMap.put("gifts_search_ofgame", new AnalyzeJsonBean(true, new TypeToken<BaseBeanList<GiftBean>>() {}));

            typeMap.put("gifts_search_lists", new AnalyzeJsonBean(true, new TypeToken<GiftBean>() {}));

            typeMap.put("my_gifts", new AnalyzeJsonBean(true, new TypeToken<GiftBean>() {}));

            typeMap.put("game_gifts_summary", new AnalyzeJsonBean(true, new TypeToken<GiftBean>() {}));

            typeMap.put("gifts_detail", new AnalyzeJsonBean(true, new TypeToken<GiftBean>() {}));

            typeMap.put("activity", new AnalyzeJsonBean(true, new TypeToken<DataShowBean>() {}));

            typeMap.put("hot_words", new AnalyzeJsonBean(true, new TypeToken<BaseBeanList<TagAndWordBean>>() {}));

            typeMap.put("hot_tags", new AnalyzeJsonBean(true, new TypeToken<BaseBeanList<TagAndWordBean>>() {}));

            typeMap.put("search_top10", new AnalyzeJsonBean(true, new TypeToken<BaseBeanList<GameInfoBean>>() {}));

            typeMap.put("search_null", new AnalyzeJsonBean(true, new TypeToken<BaseBeanList<GameInfoBean>>() {}));

            typeMap.put("query_ads", new AnalyzeJsonBean(true, new TypeToken<BaseBeanList<GameInfoBean>>() {}));

            typeMap.put("query_data", new AnalyzeJsonBean(true, new TypeToken<BaseBeanList<TagAndWordBean>>() {}));

            typeMap.put("comments", new AnalyzeJsonBean(true, new TypeToken<GameCommentBean>() {}));

            typeMap.put("text_feedback", new AnalyzeJsonBean(true, new TypeToken<FeedBackBean>() {}));

            typeMap.put("image_feedback", new AnalyzeJsonBean(true, new TypeToken<FeedBackBean>() {}));

            typeMap.put("update", new AnalyzeJsonBean(true, new TypeToken<VersionInfoBean>() {}));

            typeMap.put("game_gifts_lists", new AnalyzeJsonBean(true, new TypeToken<GiftBean>() {}));

            typeMap.put("get_gift_code", new AnalyzeJsonBean(true, new TypeToken<GiftBean>() {}));

            typeMap.put("game_manage", new AnalyzeJsonBean(true, new TypeToken<BaseBeanList<GameInfoBean>>() {}));

            typeMap.put("push_game", new AnalyzeJsonBean(true, new TypeToken<PushAppInfo>() {}));

            typeMap.put("push_app", new AnalyzeJsonBean(true, new TypeToken<PushAppInfo>() {}));

            typeMap.put("push_topic", new AnalyzeJsonBean(true, new TypeToken<PushBaseBean>() {}));

            typeMap.put("push_h5", new AnalyzeJsonBean(true, new TypeToken<PushH5Bean>() {}));

            typeMap.put("retrydownload", new AnalyzeJsonBean(true, new TypeToken<NewUrlBean>() {}));

            typeMap.put("popupwindow", new AnalyzeJsonBean(true, new TypeToken<ConfigureBean>() {}));

            typeMap.put("push_routine_activity", new AnalyzeJsonBean(true, new TypeToken<PushBaseBean>() {}));

            typeMap.put("push_hot_tab", new AnalyzeJsonBean(true, new TypeToken<PushBaseBean>() {}));

            typeMap.put("push_hot_detail", new AnalyzeJsonBean(true, new TypeToken<PushBaseBean>() {}));

            typeMap.put("push_deeplink", new AnalyzeJsonBean(true, new TypeToken<PushBaseBean>() {}));

            typeMap.put("deeplink", new AnalyzeJsonBean(true, new TypeToken<GameInfoBean>() {}));

            LogUtils.e("AnalyzeConfig", "typeMap构建完成");
        }
        return typeMap;
    }
}
