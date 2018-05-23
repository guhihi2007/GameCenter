package cn.lt.game.ui.app.jump;

import android.content.Context;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/***
 * Created by Administrator on 2015/12/30.
 */
public class Jumps {

    private Map<String, SoftReference<IJumper>> mJumps;

    private Jumps() {
        if (mJumps == null) {
            mJumps = new HashMap<>();
        }
    }

    public static Jumps self() {
        return JumpsHolder.sInstance;
    }

    public void put(IJumper jumper) {
        try {
            mJumps.put(jumper.getClass().getSimpleName(), new SoftReference<IJumper>(jumper));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IJumper get(String key) {
        try {
            return mJumps.get(key).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void init(Context context) {
    }

    private static class JumpsHolder {
        static final Jumps sInstance = new Jumps();
    }

}
