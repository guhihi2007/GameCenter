package cn.lt.game.ui.app.awardgame.listener;

/**
 * @author chengyong
 * @time 2017/6/13 14:36
 * @des ${更新积分，抽奖次数}
 */
public interface AwardUpdateListener {
    void updateScore();
    void updateScoreByManual(int offScore);
    void updateTimes(boolean isRefreshByTimeout);//重新请求活动页数据
    void jumpToScoreFragment();
}
