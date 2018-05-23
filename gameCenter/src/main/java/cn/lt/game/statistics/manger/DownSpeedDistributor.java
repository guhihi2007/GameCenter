package cn.lt.game.statistics.manger;

import java.util.HashMap;
import java.util.Map;

import cn.lt.game.statistics.recorder.SpeedRecorder;

/**
 * Created by wenchao on 2016/3/24.
 */
public class DownSpeedDistributor {

    private Map<Integer, SpeedRecorder> mSpeedRecorderMap;

    public void start(int gameId) {
        synchronized (mSpeedRecorderMap) {
            if (!mSpeedRecorderMap.containsKey(gameId)) {
                mSpeedRecorderMap.put(gameId, new SpeedRecorder(gameId));
            }
        }
    }

    public long getAVG(int gameId) {
        synchronized (mSpeedRecorderMap) {
            if (mSpeedRecorderMap.containsKey(gameId)) {
                return mSpeedRecorderMap.get(gameId).getAVG();
            }
            return 0;
        }
    }

    public void stop(int gameId) {
        synchronized (mSpeedRecorderMap) {
            if (mSpeedRecorderMap.containsKey(gameId)) {
                SpeedRecorder speedRecorder = mSpeedRecorderMap.get(gameId);
                speedRecorder.destory();
                mSpeedRecorderMap.remove(gameId);
            }
        }
    }


    private DownSpeedDistributor() {
        mSpeedRecorderMap = new HashMap<>();
    }

    private final static class HolderClass {
        private final static DownSpeedDistributor INSTANCE
                = new DownSpeedDistributor();
    }

    public static DownSpeedDistributor getInstance() {
        return HolderClass.INSTANCE;
    }
}
