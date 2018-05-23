package cn.lt.game.lib.util.redpoints;

import android.content.Context;
import android.view.View;

import cn.lt.game.application.MyApplication;

/**
 * 处理小红点view，后期更改更方便
 * Created by honaf on 2017/8/4.
 */

public class RedPointsViewUtils {


    /**
     * 下载和升级控制titlebar小红点，点击不会消失
     * @param mRedPoint titlebar小红点view
     * @param context 上下文
     */
    public static void initTopManagerRedPoints(View mRedPoint, Context context) {
        if(mRedPoint == null) {
            return;
        }
        if (MyApplication.castFrom(context).getNewGameUpdate() || MyApplication.castFrom(context).getNewGameDownload()) {
            mRedPoint.setVisibility(View.VISIBLE);
        } else {
            mRedPoint.setVisibility(View.GONE);
        }
    }

}
