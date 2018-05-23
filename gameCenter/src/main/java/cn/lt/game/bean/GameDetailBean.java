package cn.lt.game.bean;

import java.util.List;

import cn.lt.game.lib.netdata.BaseBean;

/**
 * 游戏详情（这个页面结构比较特殊，故单独写了一个）
 * Created by Administrator on 2015/11/13.
 */
public class GameDetailBean extends BaseBean {
    private GameInfoBean game;
    private String updated_at;
    private String description;
    private String comments_count;
    private List<GiftBean> gifts;
    private List<String> screenshots;
    private List<GameInfoBean> game_recommend;
    private List<DataShowBean> hot_tags;
    private GameRating game_rating;

    public List<GiftBean> getGifts() {
        return gifts;
    }

    public void setGifts(List<GiftBean> gifts) {
        this.gifts = gifts;
    }

    public List<String> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<String> screenshots) {
        this.screenshots = screenshots;
    }

    public List<DataShowBean> getHot_tags() {
        return hot_tags;
    }

    public void setHot_tags(List<DataShowBean> hot_tags) {
        this.hot_tags = hot_tags;
    }

    public GameRating getGame_rating() {
        return game_rating;
    }

    public void setGame_rating(GameRating game_rating) {
        this.game_rating = game_rating;
    }

    public List<GameInfoBean> getGame_recommend() {
        return game_recommend;
    }

    public void setGame_recommend(List<GameInfoBean> game_recommend) {
        this.game_recommend = game_recommend;
    }

    public GameInfoBean getGame_base_info() {
        return game;
    }

    public GameInfoBean getGame() {
        return game;
    }

    public void setGame(GameInfoBean game) {
        this.game = game;
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

    public String getComments_count() {
        return comments_count;
    }

    public void setComments_count(String comments_count) {
        this.comments_count = comments_count;
    }
}
