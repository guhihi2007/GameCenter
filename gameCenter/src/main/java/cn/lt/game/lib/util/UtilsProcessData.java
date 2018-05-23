package cn.lt.game.lib.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by majian12344321 on 2015/1/19.
 * 联网时数据解析 及sp保存数据
 */
public class UtilsProcessData {


    public static SharedPreferences sp;

    /**
     * Gson 解析json
     *
     * @param result
     * @param clazz
     * @return
     */
    public static <T> T jsonTobean(String result, Class<T> clazz) {
        Gson gson = new Gson();
        T t = gson.fromJson(result, clazz);
        return t;
    }
    
    /**
     * Gson 解析json
     *
     * @param result
     * @param clazz
     * @return
     */
    public static String beanToJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /**
     * sp保存数据
     *
     * @param ct
     * @param key
     * @param value
     */
    public static void spSavaData(Context ct, String key, String value) {

        if (sp == null) {
            sp = ct.getSharedPreferences("config", 0);

        }
        sp.edit().putString(key, value).apply();

    }

    /**
     * sp获取数据
     *
     * @param ct
     * @param key
     * @param value
     * @return
     */
    public static String spGetStringData(Context ct, String key, String value) {
        if (sp == null) {
            sp = ct.getSharedPreferences("config", 0);
        }
        return sp.getString(key, value);

    }


}

