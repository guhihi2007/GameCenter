package cn.lt.game.ui.app.jump;

import android.content.Context;

import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.ui.app.specialtopic.SpecialTopicDetailsActivity;

/***
 * Created by Administrator on 2015/12/14.
 */
public class SpecialTopicDetailJumper implements IJumper {

    @Override
    public void jump(Object o, Context context) {
        try {
            FunctionEssence data = (FunctionEssence) o;
            String topicId = data.getUniqueIdentifier();
            ActivityActionUtils.activity_Jump_Value(context,
                    SpecialTopicDetailsActivity.class, "topicId", topicId);
        } catch (Exception e) {
            LogUtils.i("GOOD", "跳转异常-->" + this.getClass().getSimpleName());
            e.printStackTrace();
        }
    }
}
