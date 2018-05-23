package cn.lt.game.ui.app.jump;

import android.content.Context;

import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.LogUtils;

/***
 * Created by Administrator on 2015/12/14.
 */
public class CatsToHotCatsJumper implements IJumper {

    @Override
    public void jump(Object o, Context context) {
        try {
            FunctionEssence hotcat = (FunctionEssence) o;
            ActivityActionUtils.jumpToCategoryHotCats(context, hotcat
                    .getUniqueIdentifier(), hotcat.getTitle());
        } catch (Exception e) {
            LogUtils.i("GOOD", "跳转异常-->" + this.getClass().getSimpleName());
            e.printStackTrace();
        }
    }
}
