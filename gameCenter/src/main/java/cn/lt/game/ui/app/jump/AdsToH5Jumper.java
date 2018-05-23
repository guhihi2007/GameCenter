package cn.lt.game.ui.app.jump;

import android.content.Context;
import android.util.Log;

import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.domain.essence.IdentifierType;
import cn.lt.game.lib.util.ActivityActionUtils;

/***
 * Created by Administrator on 2015/12/14.
 */
public class AdsToH5Jumper implements IJumper {

    @Override
    public void jump(Object o, Context context) {
        try {
            FunctionEssence data = (FunctionEssence) o;
            ActivityActionUtils.jumpToByUrl(context, data.getTitle(), data.getUniqueIdentifierBy
                    (IdentifierType.URL));
        } catch (Exception e) {
            Log.i("GOOD", "跳转异常-->" + this.getClass().getSimpleName());
            e.printStackTrace();
        }
    }
}
