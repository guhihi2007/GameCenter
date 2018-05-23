package cn.lt.game.bean;

/**
 * Created by honaf on 2017/10/30.
 */

public class RedPointsBean {
    private boolean myTask;
    private boolean platUpdate;
    private int feedbackNum;

    public boolean isMyTask() {
        return myTask;
    }

    public void setMyTask(boolean myTask) {
        this.myTask = myTask;
    }


    public boolean isPlatUpdate() {
        return platUpdate;
    }

    public void setPlatUpdate(boolean platUpdate) {
        this.platUpdate = platUpdate;
    }

    public int getFeedbackNum() {
        return feedbackNum;
    }

    public void setFeedbackNum(int feedbackNum) {
        this.feedbackNum = feedbackNum;
    }
}
