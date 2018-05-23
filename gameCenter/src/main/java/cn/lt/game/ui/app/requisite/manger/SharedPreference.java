package cn.lt.game.ui.app.requisite.manger;

import android.content.Context;
import android.content.SharedPreferences.Editor;

public class SharedPreference {

    public static final String LAST_SHOW_TIME = "lastShowTime";
    public static final String HAS_DATA = "hasData";
    public static final String CACHED = "cached";
    public static final String CHECK_TIME = "check_time";
    public static final String MINSPACELIMIT = "minSpaceLimit";
    public static String PRE_FILE_NAME = "requisite";

    public static long getLastShowTime(Context context) {
        return context
                .getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE)
                .getLong(LAST_SHOW_TIME, 0);
    }

    public static void saveShowTime(Context context, long time) {
        Editor editor = context.getSharedPreferences(PRE_FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putLong(LAST_SHOW_TIME, time);
        editor.apply();
    }

    public static boolean hasData(Context context) {
        return context
                .getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE)
                .getBoolean(HAS_DATA, true);
    }

    public static void saveHasData(Context context, boolean hasData) {
        Editor editor = context.getSharedPreferences(PRE_FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putBoolean(HAS_DATA, hasData);
        editor.apply();
    }

    public static boolean isCached(Context context) {
        return context
                .getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE)
                .getBoolean(CACHED, false);
    }

    public static void saveCached(Context context, boolean cached) {
        Editor editor = context.getSharedPreferences(PRE_FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putBoolean(CACHED, cached);
        editor.apply();
    }


    public static String getMinSpaceLimit(Context context) {
        return context
                .getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE)
                .getString(MINSPACELIMIT, "");
    }

    public static void saveMinSpaceLimit(Context context, String minSpaceLimit) {
        Editor editor = context.getSharedPreferences(PRE_FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putString(MINSPACELIMIT, minSpaceLimit);
        editor.apply();
    }

    public static long getLastCheckTime(Context context) {
        return context
                .getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE)
                .getLong(CHECK_TIME, 0);
    }

    public static void saveThisCheckTime(Context context, long time) {
        Editor editor = context.getSharedPreferences(PRE_FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putLong(CHECK_TIME, time);
        editor.apply();
    }
}
