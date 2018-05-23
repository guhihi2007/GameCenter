package cn.lt.game.ui.app.jump;

import android.content.Context;
import android.text.TextUtils;

import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.LogUtils;

/***
 * Created by Administrator on 2015/12/14.
 */
public class GameToGameDetailJumper implements IJumper {

    @Override
    public void jump(Object o, Context context) {
        try {
            GameDomainBaseDetail data = (GameDomainBaseDetail) o;
//            Intent intent = new Intent(context, GameDetailHomeActivity.class);
//            intent.putExtra("id", data.getUniqueIdentifier());
//            intent.putExtra("package", data.getPkgName());
//            intent.putExtra("forum_id", data.getGroupId());
//            context.showDialog(intent);
            if (!TextUtils.isEmpty(data.getUniqueIdentifier())&&!"null".equals(data.getUniqueIdentifier().equals("null")))
                ActivityActionUtils.JumpToGameDetail(context,Integer.parseInt(data.getUniqueIdentifier()));
        } catch (Exception e) {
            LogUtils.i("GOOD", "跳转异常");
            e.printStackTrace();
        }
    }
}
