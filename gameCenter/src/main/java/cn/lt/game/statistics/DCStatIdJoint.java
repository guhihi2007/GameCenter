package cn.lt.game.statistics;

import java.util.List;

import cn.lt.game.model.GameBaseDetail;

/**
 * Created by LinJunSheng on 2016/12/14.
 */

public class DCStatIdJoint {

    /** 拼接游戏集合的id*/
    public static String jointIdByGameDetailBean(List<GameBaseDetail> gameList) {
        String appIds = "";
        if(gameList != null) {
            for (int i = 0; i < gameList.size(); i++) {
                GameBaseDetail game = gameList.get(i);
                if(i != gameList.size() - 1) {
                    appIds += game.getId() + " | ";
                } else {
                    appIds += game.getId();
                }
            }
        }

        return appIds;
    }
}
