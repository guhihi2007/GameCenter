package cn.lt.game.ui.app.jump;

import android.content.Context;
import android.content.Intent;

import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.ui.app.gamegift.GiftDetailActivity;

/***
 * Created by Administrator on 2015/12/14.
 */
public class GiftToGiftDetailJumper implements IJumper {

    @Override
    public void jump(Object o, Context context) {
        try {
            GiftDomainDetail data = (GiftDomainDetail) o;
            Intent intent = new Intent(context, GiftDetailActivity.class);
            intent.putExtra(GiftDetailActivity.GIFT_ID, data.getUniqueIdentifier());
            context.startActivity(intent);
        } catch (Exception e) {
            LogUtils.i("GOOD", "跳转异常"+getClass().getSimpleName());
            e.printStackTrace();
        }
    }
}
