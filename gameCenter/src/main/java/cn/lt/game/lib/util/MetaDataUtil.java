package cn.lt.game.lib.util;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import cn.lt.game.application.MyApplication;

/**
 * Created by LinJunSheng on 2016/4/21.
 */
public class MetaDataUtil {
    public static String getMetaData(String key) {
        String value = "";
        try {
            PackageManager pm = MyApplication.application.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(MyApplication.application.getPackageName(), PackageManager
                    .GET_META_DATA);
            Object o = ai.metaData.get(key);
            if(o instanceof String) {
                value = (String) o;
            }
            if(o instanceof Integer) {
                value = String.valueOf(ai.metaData.getInt(key,-1));
            }
//            value = String.valueOf(ai.metaData.get(key));
//            if (TextUtils.isEmpty(value)) {
//                int i = ai.metaData.getInt(key,-1);
//                value = String.valueOf(i);
//            }
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
