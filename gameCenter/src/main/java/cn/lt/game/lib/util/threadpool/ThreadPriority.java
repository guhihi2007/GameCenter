package cn.lt.game.lib.util.threadpool;

/**
 * Created by wenchao on 2015/09/21.
 */
public enum ThreadPriority {
    LOW(19), MEDIUM(10), HIGH(0), HIGHEST(-1);

    private final int code;

    ThreadPriority(int code) {
        this.code = code;
    }

    public int toInt() {
        return code;
    }
}
