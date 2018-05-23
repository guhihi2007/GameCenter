package cn.lt.game.ui.app.jump;

import android.content.Context;
import android.content.Intent;

import cn.lt.game.ui.app.gameactive.GameActivitiesActivtiy;

/***
 * Created by Administrator on 2015/12/14.
 */
public class ActivitiesListJumper implements IJumper {

    @Override
    public void jump(Object o, Context context) {
        context.startActivity(new Intent(context, GameActivitiesActivtiy.class));
    }
}
