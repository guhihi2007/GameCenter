package cn.lt.game.lib.util.threadpool;

import android.os.CountDownTimer;

/**
 * Created by wenchao on 2015/09/21.
 */
public class LTAsyncTaskCancelTimer extends CountDownTimer {

    private LTAsyncTask asyncTask;
    private boolean interrupt;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public LTAsyncTaskCancelTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if(asyncTask == null){
            this.cancel();
            return;
        }
        if(asyncTask.isCancelled()){
            this.cancel();
        }

        if(asyncTask.getStatus()==LTAsyncTaskStatus.FINISHED)
            this.cancel();
    }

    @Override
    public void onFinish() {
        if(asyncTask == null || asyncTask.isCancelled()){
            return;
        }
        if(asyncTask.getStatus()==LTAsyncTaskStatus.FINISHED){
            return;
        }

        asyncTask.cancel(interrupt);
    }

    public void setInterrupt(boolean interrupt) {
        this.interrupt = interrupt;
    }

    public LTAsyncTask getAsyncTask() {
        return asyncTask;
    }

    public boolean isInterrupt() {
        return interrupt;
    }

    public void setAsyncTask(LTAsyncTask asyncTask) {
        this.asyncTask = asyncTask;
    }
}
