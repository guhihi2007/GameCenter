package cn.lt.game.statistics;

import android.text.TextUtils;

/**
 * Created by LinJunSheng on 2016/12/15.
 */

public class StatLogUtil {

    private final StringBuilder logStringBuilder;

    public StatLogUtil() {
        logStringBuilder = new StringBuilder();
    }

    public void append(String key, int content) {
        if (content == -1) {
            content=0;
        }

//        if (content != 0 && content != -1) {
            logStringBuilder.append(", " + key + "=" + content);
//        } else {
//            logStringBuilder.append(", " + key + "=");
//        }
    }

    public void append(String key, boolean flag) {
        logStringBuilder.append(", " + key + "=" + flag);
    }

    public void append(String key, String content) {
        if ("actionType".equals(key)) {
            logStringBuilder.append(key + "=" + (TextUtils.isEmpty(content) ? "" : content));
        } else {
            logStringBuilder.append(", " + key + "=" + (TextUtils.isEmpty(content) ? "" : content));
        }
    }

    public String getLog() {
        return " { " + logStringBuilder.toString() + " }";
    }
}
