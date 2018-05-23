package cn.lt.game.ui.app.jump;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.ui.app.gamegift.GiftDetailActivity;

/***
 * Created by Administrator on 2015/12/14.
 */
public class AdsToGiftDetailJumper implements IJumper {

    @Override
    public void jump(Object o, Context context) {
        try {
            FunctionEssence data = (FunctionEssence) o;
            Intent intent = new Intent(context, GiftDetailActivity.class);
            intent.putExtra(GiftDetailActivity.GIFT_ID, data.getUniqueIdentifier());
            context.startActivity(intent);
        } catch (Exception e) {
            Log.i("GOOD", "跳转异常-->" + this.getClass().getSimpleName());
            e.printStackTrace();
        }
    }
}
