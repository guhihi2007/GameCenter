package cn.lt.game.lib.util.threadpool;

/**
 * Created by wenchao on 2015/09/21.
 */
public enum LTAsyncTaskStatus {
    /**
     * 未开始执行
     */
    PENDING,

    /**
     * 正在执行
     */
    RUNNING,

    /**
     * 执行完毕
     */
    FINISHED,
}
