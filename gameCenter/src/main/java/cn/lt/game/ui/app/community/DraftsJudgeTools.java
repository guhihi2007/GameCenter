package cn.lt.game.ui.app.community;

import java.util.HashMap;

//草稿箱判断是否有已经有数据在发送工具类
public class DraftsJudgeTools {
    private volatile static DraftsJudgeTools mInstance = null;
    private static HashMap<String, String> loadingRequest = new HashMap<>();

    public static DraftsJudgeTools instance() {
        if (mInstance == null) {
            synchronized (DraftsJudgeTools.class) {
                if (mInstance == null) {
                    mInstance = new DraftsJudgeTools();
                }
            }
        }
        return mInstance;
    }

    public boolean showDialog(String tag) {  //根据本地发送hashMap里面存储的引用来判断
        return loadingRequest.get(tag) == null;

    }

    public void save(String tag) {   //存入到集合里面
        loadingRequest.put(tag, tag);
    }

    public void remove(String tag) {
        loadingRequest.remove(tag);
    }

}
