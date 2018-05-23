package cn.lt.game.lib.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Map;
import java.util.Set;

/**
 * SharedPreferences工具类
 *
 * @author zouyb
 */
public class SharedPreferencesUtil {

    private SharedPreferences sp;
    private Editor            editor;
    private String name = "LTBL_Game";
    private int    mode = Context.MODE_MULTI_PROCESS;

    public final static String userBaseInfoKey = "userBaseInfoKey";
    public final static String userToKenKey    = "userToKenKey";

    public final static String userHistoryUserId   = "userHistoryUserId";
    public final static String userHistoryUserName = "userHistoryUserName";
    public final static String userHistoryAvatar   = "userHistoryAvatar";
    // 如果以后要能直接登录的话，需要记录他
    public final static String userHistoryToKenKey = "userHistoryToKenKey";

    // 此值之前用来标记用户中心的salt，之后准备用来记录token更新的时间
    // public final static String userTokenTime = "userTokenTime";

    public final static String downloadKey      = "downloadKey";
    public final static String downloadFilePath = "downloadFilePath";
    public final static String updateFilePath   = "updateFilePath";
    public final static String updateFileUrl    = "updateFileUrl";
    
    public final static String aboutCommunity = "aboutCommunity";
    public final static String firstVisitCommunity = "firstVisitCommunity";

    //保存游戏打开时间或者 游戏中心升级通知时间
    public final static String openTime = "openTime";
    public final static String AWARD_SHOWTIME = "awardshowTime";
    public final static String FREE_AWARDTIME = "FreeAwardTime";

    public final static String AWARD_EDIT_ADDRESS = "AwardeditAddress";
    public final static String AWARD_EDIT_NAME = "AwardeditName";
    public final static String AWARD_EDIT_PHONE = "AwardeditPhone";

    public final static String netUUID = "netUUID";

    /**
     * 退出程序弹框提醒是否继续未完成的下载，这里记住checkbox
     */
    public final static String EXIT_DIALOG_REMENBER_DOWNLOAD   = "exit_dialog_remember_download";

    public final static String AUTO_INSTALL_TIME = "auto_install_time";

    public SharedPreferencesUtil(Context context) {
        this.sp = context.getSharedPreferences(name, mode);
        this.editor = sp.edit();
    }

    /**
     * 创建一个工具类，默认打开名字为name的SharedPreferences实例
     *
     * @param context
     * @param name    唯一标识
     * @param mode    权限标识
     */
    public SharedPreferencesUtil(Context context, String name, int mode) {
        try {
            this.sp = context.getSharedPreferences(name, mode);
            this.editor = sp.edit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加信息到SharedPreferences
     *
     * @param map
     * @throws Exception
     */
    public void add(Map<String, String> map) {
        Set<String> set = map.keySet();
        for (String key : set) {
            editor.putString(key, map.get(key));
        }
        editor.apply();
    }

    /**
     * 添加信息到SharedPreferences
     *
     * @param key
     * @param value
     */
    public void add(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * @param key
     * @param value
     */
    public void add(String key, long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    public void add(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }


    /**
     * 删除信息
     *
     * @throws Exception
     */
    public void deleteAll() throws Exception {
        editor.clear();
        editor.apply();
    }

    /**
     * 删除一条信息
     */
    public void delete(String key) {
        editor.remove(key);
        editor.apply();
    }

    /**
     * 获取信息
     *
     * @param key
     * @return
     * @throws Exception
     */
    public String get(String key) {
        if (sp != null) {
            return sp.getString(key, "");
        }
        return "";
    }

    public int getInteger(String key) {
        if (sp != null) {
            return sp.getInt(key, 0);
        }
        return 0;
    }

    public long getLong(String key) {
        if (sp != null) {
            return sp.getLong(key, 0);
        }
        return 0;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (sp != null) {
            return sp.getBoolean(key, defaultValue);
        }
        return defaultValue;
    }


    /**
     * 获取此SharedPreferences的Editor实例
     *
     * @return
     */
    public Editor getEditor() {
        return editor;
    }
}
