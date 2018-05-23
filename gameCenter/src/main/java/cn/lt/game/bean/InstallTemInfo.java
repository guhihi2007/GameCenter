package cn.lt.game.bean;

/**
 * Created by ltbl on 2017/6/19.
 */

public class InstallTemInfo {
    private String gameId;
    private boolean isUpgrade;
    private long timeStamp;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public boolean isUpgrade() {
        return isUpgrade;
    }

    public void setUpgrade(boolean upgrade) {
        isUpgrade = upgrade;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public InstallTemInfo(String gameId, boolean isUpgrade, long timeStamp) {
        this.gameId = gameId;
        this.isUpgrade = isUpgrade;
        this.timeStamp = timeStamp;
    }
}
