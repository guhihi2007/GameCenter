package cn.lt.game.application;

import android.content.Context;
import android.util.Log;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CrashExceptionHandler implements UncaughtExceptionHandler {
    private Map<String, String> infos = new HashMap<String, String>();
    // 格式化日期时间
    private DateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.US);

    private Context mContext;

    private CrashExceptionHandler() {

    }

    private static final String TAG = "CrashExceptionHandler";

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        logErr(ex);
    }

    private void logErr(Throwable e) {
        Log.d(TAG, "crash error begin-*----*----*----*----*----*----*----*----*----*-");
        Log.e(TAG, e.getMessage());
        Log.d(TAG, "crash error end-*----*----*----*----*----*----*----*----*----*-");
    }

    private static class CrashExceptionHandlerHolder {
        static final CrashExceptionHandler sInstance = new CrashExceptionHandler();
    }

    public static CrashExceptionHandler self() {
        return CrashExceptionHandlerHolder.sInstance;
    }

    public void init(Context context) {
        this.mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


}
