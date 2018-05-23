package cn.lt.game.lib.util.threadpool;

/**
 * Created by wenchao on 2015/09/21.
 */
public enum QueuePriority {
    LOWEST(0), LOW(1), MEDIUM(2), HIGH(3), HIGHEST(4);

    private final int code;

    QueuePriority(int code) {
        this.code = code;
    }

    public int toInt() {
        return code;
    }
}
