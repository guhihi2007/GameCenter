package cn.lt.game.application;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by wenchao on 2015/8/26.
 */
public class Configuration {
    private static Context mContext;


    public static void init(Context context){
        mContext = context;
//        initCacheDir();
    }


    /**
     * 获取baseHost,
     * 1.优先properties中获取
     * 2.其次manifest中获取
     * @return
     * @throws NullPointerException
     */
    public static String getHostConfig(){
        String value = getHostConfigByLocalFile();
        if(value == null ){
            value = metadata("BaseHost",null);
        }
        if(value == null){
            throw new NullPointerException("BaseHost is null,please set it in manifest!");
        }
        return  value;
    }



    /**
     * 获取application标签下的配置
     * @param key
     * @param defaultValue
     * @return
     */
    private static String metadata(String key,String defaultValue){
        try {
            ApplicationInfo applicationInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            defaultValue = applicationInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * 根据sdcard中的文件获取配置，
     * @return
     */
    private static String getHostConfigByLocalFile(){
        Properties properties = new Properties();
        try {
            File file = new File("/mnt/sdcard/gamecenter.properties");
            if(!file.exists()){
                return null;
            }
            FileInputStream fis = new FileInputStream(file);
            properties.load(fis);
            String baseHost = properties.getProperty("BASE_HOST");
            if(TextUtils.isEmpty(baseHost)){
               return null;
            }
            return baseHost;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
