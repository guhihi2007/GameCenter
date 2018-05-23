package cn.lt.game.lib.util.threadpool;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by wenchao on 2015/09/21.
 * 主线程处理
 */
public class InternalHandler extends Handler implements Messages {
    public InternalHandler(){
        super(Looper.getMainLooper());
    }

    @Override
    public void handleMessage(Message msg) {
        LTAsyncTaskResult result = (LTAsyncTaskResult)msg.obj;
        switch (msg.what){
            case MESSAGE_POST_PROGRESS:
                result.mTask.onProgressUpdate(result.mData);
                break;
            case MESSAGE_POST_RESULT:
                result.mTask.finish(result.mData[0]);
                break;
        }
        

    }
}
