package cn.lt.game.lib.util.threadpool;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;

/**
 * Created by wenchao on 2015/09/21.
 */
public class SerialExecutor implements Executor {

    final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
    Runnable mActive;

    @Override
    public void execute(final Runnable command) {
        mTasks.offer(new Runnable() {
            @Override
            public void run() {
                try {
                    command.run();
                } finally {
                    scheduleNext();
                }
            }
        });
        if(mActive == null){
            scheduleNext();
        }
    }
    protected synchronized void scheduleNext(){
        if((mActive = mTasks.poll())==null){
            return;
        }

    }
}
