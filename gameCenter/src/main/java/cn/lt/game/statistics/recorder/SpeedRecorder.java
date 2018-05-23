package cn.lt.game.statistics.recorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.lt.game.download.DownloadState;
import cn.lt.game.event.DownloadUpdateEvent;
import de.greenrobot.event.EventBus;

/**
 * Created by wenchao on 2016/3/24.
 */
public class SpeedRecorder {

    private List<Long> mSpeedList;

    private int gameId;

    public SpeedRecorder(int gameId) {
        mSpeedList = new ArrayList<>();
        Collections.synchronizedCollection(mSpeedList);
        this.gameId = gameId;
        EventBus.getDefault().register(this);
    }

    /**
     * 得到平均值
     * @return
     */
    public long getAVG() {
        if (mSpeedList.size() == 0) return 0;
        long total = 0;
        for (long speed : mSpeedList) {
            total += speed;
        }
        return total / mSpeedList.size();
    }


    public void onEventMainThread(DownloadUpdateEvent event) {
        if(event.game.getId()==gameId) {
            if (event.game.getState() == DownloadState.downInProgress) {
                long speed = event.game.getDownSpeed();
                mSpeedList.add(speed);
            }
        }
    }

    public void destory() {
        EventBus.getDefault().unregister(this);
        mSpeedList.clear();
    }
}
