package cn.lt.game.lib.util.threadpool;

import java.util.concurrent.Callable;

/**
 * Created by wenchao on 2015/09/21.
 */
abstract class WorkerRunnable<Params,Result> implements Callable<Result> {
    Params[] mParams;
}
