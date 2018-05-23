package cn.lt.game.domain.detail;

import java.io.Serializable;
import java.util.List;

import cn.lt.game.bean.GameDetailBean;
import cn.lt.game.domain.essence.FunctionEssence;

/**
 * Created by Administrator on 2015/11/18.
 */
public class GameDomainDetail extends GameDomainBaseDetail implements Serializable{
    private GameDomainExtraDetail extraDetail;

    public GameDomainDetail(GameDetailBean game) {
        super(game.getGame_base_info());
        extraDetail = new GameDomainExtraDetail();
        extraDetail.setDescription(game.getDescription());
        extraDetail.setUpdated_at(game.getUpdated_at());
        extraDetail.setCommentCnt(Integer.parseInt(game.getComments_count()));
        extraDetail.setScreenShots(game.getScreenshots());
        extraDetail.transformGifts(game.getGifts());
        extraDetail.transformGameRating(game.getGame_rating());
        extraDetail.transformRecommendedGames(game.getGame_recommend());
        extraDetail.transformHotTags(game.getHot_tags());
    }

    public GameDomainExtraDetail getExtraDetail() {
        return extraDetail;
    }

    public List<GiftDomainDetail> getGifts() {
        return extraDetail.getGifts();
    }

    public GameDomainDetail setGifts(List<GiftDomainDetail> gifts) {
        extraDetail.setGifts(gifts);
        return this;
    }

    public List<String> getScreenShots() {
        return extraDetail.getScreenShots();
    }

    public GameDomainDetail setScreenShots(List<String> screenShots) {
        extraDetail.setScreenShots(screenShots);
        return this;
    }

    public List<GameDomainBaseDetail> getRecommenedGames() {
        return extraDetail.getRecommendedGames();
    }

    public GameDomainDetail setRecommenedGames(List<GameDomainBaseDetail> recommenedGames) {
        extraDetail.setRecommendedGames(recommenedGames);
        return this;
    }

    public List<FunctionEssence> getHotTags() {
        return extraDetail.getHotTags();
    }

    public GameDomainDetail setHotTags(List<FunctionEssence> hotTags) {
        extraDetail.setHotTags(hotTags);
        return this;
    }

}



