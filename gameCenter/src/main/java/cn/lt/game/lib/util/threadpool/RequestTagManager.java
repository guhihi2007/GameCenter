package cn.lt.game.lib.util.threadpool;

import android.content.Context;

import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.model.SharePreferencesKey;

/**
 * @author chengyong
 * @time 2017/11/2 17:02
 * @des ${下载请求是否报过的标记}
 */

public class RequestTagManager {

    private static SharedPreferencesUtil spUtil;

    public static void deleteRequestTag(Context context, String id) {
        if(spUtil ==null){
            spUtil = new SharedPreferencesUtil(context, SharePreferencesKey.REQUEST_TAG, Context.MODE_PRIVATE);
        }
        spUtil.delete(SharePreferencesKey.REQUEST_TAG+id);
        LogUtils.i("requestTag", "deleteRequestTag:"+id );
    }

    public static void saveRequestTag(Context context, String id) {
        if(spUtil==null){
            spUtil = new SharedPreferencesUtil(context, SharePreferencesKey.REQUEST_TAG, Context.MODE_PRIVATE);
        }
        spUtil.add(SharePreferencesKey.REQUEST_TAG+id,true);
        LogUtils.i("requestTag", "saveRequestTag:"+id );
    }

    public static boolean hasTag(Context context, String id) {
        Boolean hasTag=false;
        if(spUtil==null){
            spUtil = new SharedPreferencesUtil(context, SharePreferencesKey.REQUEST_TAG, Context.MODE_PRIVATE);
        }
        hasTag=spUtil.getBoolean(SharePreferencesKey.REQUEST_TAG+id,false);
        LogUtils.i("requestTag", "hasTag:?"+id +"=="+hasTag);
        return hasTag;
    }
}
