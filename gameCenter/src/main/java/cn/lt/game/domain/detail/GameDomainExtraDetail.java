package cn.lt.game.domain.detail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.bean.DataShowBean;
import cn.lt.game.bean.GameInfoBean;
import cn.lt.game.bean.GameRating;
import cn.lt.game.bean.GiftBean;
import cn.lt.game.domain.essence.DomainType;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.domain.essence.FunctionEssenceImpl;

/**
 * Created by Administrator on 2015/12/2.
 */
public class GameDomainExtraDetail implements Serializable{
    private int commentCnt = -1; // 评论次数
    private String updated_at = null; // 最后更新时间
    private String description = null; // 游戏简介
    private List<GiftDomainDetail> gifts = null;//游戏礼包
    private List<String> screenShots = null;//游戏截图
    private List<GameDomainBaseDetail> recommendedGames = null;//推荐游戏
    private List<FunctionEssence> hotTags = null;//热门标签
    private Map<Integer, Integer> gameRating = null;    //1~5星的百分比

    public int getCommentCnt() {
        return commentCnt;
    }

    public void setCommentCnt(int commentCnt) {
        this.commentCnt = commentCnt;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<GiftDomainDetail> getGifts() {
        return gifts;
    }

    public void setGifts(List<GiftDomainDetail> gifts) {
        this.gifts = gifts;
    }

    public List<String> getScreenShots() {
        return screenShots;
    }

    public void setScreenShots(List<String> screenShots) {
        this.screenShots = screenShots;
    }

    public List<GameDomainBaseDetail> getRecommendedGames() {
        return recommendedGames;
    }

    public void setRecommendedGames(List<GameDomainBaseDetail> recommendedGames) {
        this.recommendedGames = recommendedGames;
    }

    public List<FunctionEssence> getHotTags() {
        return hotTags;
    }

    public void setHotTags(List<FunctionEssence> hotTags) {
        this.hotTags = hotTags;
    }

    public Map<Integer, Integer> getGameRating() {
        return gameRating;
    }

    public void setGameRating(Map<Integer, Integer> gameRating) {
        this.gameRating = gameRating;
    }

    void transformHotTags(List<DataShowBean> hot_tags) {
        if (hot_tags == null || hot_tags.size() == 0) { return; }
        this.hotTags = new ArrayList<>();
        for (DataShowBean elem : hot_tags) {
            FunctionEssenceImpl functionEssence = new FunctionEssenceImpl(DomainType.TAG);
            functionEssence.setUniqueIdentifier(elem.getId());
            functionEssence.setTitle(elem.getTitle());
            this.hotTags.add(functionEssence);
        }
    }

    void transformGifts(List<GiftBean> gifts) {
        if (gifts == null || gifts.size() == 0) { return; }
        this.gifts = new ArrayList<>();
        for (GiftBean elem : gifts) {
            GiftDomainDetail gift = new GiftDomainDetail(elem);
            this.gifts.add(gift);
        }
    }

    void transformGameRating(GameRating gameRating) {
        if (gameRating == null) { return; }
        if (this.gameRating == null) {
            this.gameRating = new HashMap<>(5);
        }
        this.gameRating.put(1, gameRating.getStar1());
        this.gameRating.put(2, gameRating.getStar2());
        this.gameRating.put(3, gameRating.getStar3());
        this.gameRating.put(4, gameRating.getStar4());
        this.gameRating.put(5, gameRating.getStar5());
    }

    void transformRecommendedGames(List<GameInfoBean> games) {
        if (games == null || games.size() == 0) { return; }
        this.recommendedGames = new ArrayList<>();
        for (GameInfoBean elem : games) {
            GameDomainBaseDetail game = new GameDomainBaseDetail(elem);
            this.recommendedGames.add(game);
        }
    }

}
