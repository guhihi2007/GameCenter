package cn.lt.game.ui.app.jump;

import android.content.Context;
import android.content.Intent;

import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.ui.app.gamegift.GiftListActivity;

/***
 * Created by Administrator on 2015/12/14.
 */
public class GameToGiftListJumper implements IJumper {

    @Override
    public void jump(Object o, Context context) {
        try {
            GameDomainBaseDetail data = (GameDomainBaseDetail) o;
            Intent intent = new Intent(context, GiftListActivity.class);
            intent.putExtra(GiftListActivity.GAME_ID, data.getUniqueIdentifier());
            context.startActivity(intent);
        } catch (Exception e) {
            LogUtils.i("GOOD", "跳转异常");
            e.printStackTrace();
        }
    }
}
