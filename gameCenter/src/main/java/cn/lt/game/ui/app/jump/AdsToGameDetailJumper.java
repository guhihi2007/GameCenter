package cn.lt.game.ui.app.jump;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.lib.util.ActivityActionUtils;

/***
 * Created by Administrator on 2015/12/14.
 */
public class AdsToGameDetailJumper implements IJumper {

    @Override
    public void jump(Object o, Context context) {
        try {
            FunctionEssence data = (FunctionEssence) o;
            String id = data.getUniqueIdentifier();
            if (!TextUtils.isEmpty(data.getUniqueIdentifier()) && !"null".equals(data
                    .getUniqueIdentifier())) {
                ActivityActionUtils.JumpToGameDetail(context, Integer.parseInt(id));
            }
        } catch (Exception e) {
            Log.i("GOOD", "跳转异常-->" + this.getClass().getSimpleName());
            e.printStackTrace();
        }
    }
}
