package cn.lt.game.lib.netdata;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Map;

import cn.lt.game.lib.cachedisk.DiskLruCacheHelper;
import cn.lt.game.lib.util.AdMd5;

/**
 * Created by Administrator on 2015/12/18.
 */
public class NetDataCache {

    private DiskLruCacheHelper diskLruCacheHelper;

    public NetDataCache(Context context) throws IOException {
        diskLruCacheHelper = new DiskLruCacheHelper(context);
    }

    public void putNetData_head(String key, Map<String, String> head) {
        Gson gson = new Gson();
        String str = gson.toJson(head);
        diskLruCacheHelper.put(key + "HEAD", NetDataEncoding.encode(str));
    }

    public Map<String,String> getNetData_head(String key) {
        String val = diskLruCacheHelper.getAsString(key + "HEAD");
        String re =  NetDataEncoding.decode(val);
        Gson gson = new Gson();
        return gson.fromJson(re,new TypeToken<Map<String,String>>(){}.getType());
    }

    public void putNetData(String key, String data) {
        diskLruCacheHelper.put(key, NetDataEncoding.encode(data));
    }

    public String getNetData(String key) {
        String val = diskLruCacheHelper.getAsString(key);
        return NetDataEncoding.decode(val);
    }

    public String getNetDataKey(String uri, Map<String, ?> param) {
        Gson gson = new Gson();
        String str = gson.toJson(param);
        return AdMd5.MD5(uri + str);
    }
}
