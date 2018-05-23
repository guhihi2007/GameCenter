package cn.lt.game.lib;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * root装有效性检测(时间为1分钟)
 * Created by JohnsonLin on 2017/5/22.
 */

public class RootInstallVerify {

    public boolean rootInstallFail;
    private Timer timer;
    private TimerTask timerTask;
    Process process;
    boolean over;
    StringBuilder errorMsg;

    public RootInstallVerify(Process p) {
        this.process = p;
        over = false;
        errorMsg = new StringBuilder();
        rootInstallFail = false;

        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                errorMsg = new StringBuilder();
                errorMsg.append("root install fail, because be intercepted");
                Log.i("rootzhaung666","root装可能失败，强制终止process");
                rootInstallFail = true;
                process.destroy();
            }
        };

    }

    public void verify() {
        Log.i("rootzhaung666","RootInstallVerify开始运行~~~~~~~~~~~~~~~~~~~~~~~~~~ ");
        timer.schedule(timerTask, 60000);
    }

    public void cancelTimer() {
        timerTask.cancel();
        timerTask = null;
        timer = null;
        System.gc();
    }

    public void setOver(boolean over) {
        this.over = over;
    }

    public StringBuilder getErrorMsg() {
        return errorMsg;
    }
}
