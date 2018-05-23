package cn.lt.game.lib.util.threadpool;

/**
 * Created by wenchao on 2015/09/21.
 */
public class LTAsyncTaskResult<Data> {
    final LTAsyncTask mTask;
    final Data[] mData;

    LTAsyncTaskResult(LTAsyncTask task,Data...data){
        mTask = task;
        mData = data;
    }
}
