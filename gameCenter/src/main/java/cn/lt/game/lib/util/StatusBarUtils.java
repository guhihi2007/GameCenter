package cn.lt.game.lib.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by honaf on 2017/8/10.
 * 首页4个fragment处理statusBarView工具类
 */

public class StatusBarUtils {
    /**
     * 根据手机状态栏高度，把顶部往下偏移（只针对Android4.4以上）
     */
    public static void showSelfStatusBar(Context context, View statusBar) {
        // 手机系统版本小于4.4的，不执行
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        // 获取手机状态栏高度
        int top = ActivityManager.getStatusHeight((Activity) context);
        statusBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, top));
        statusBar.setVisibility(View.VISIBLE);

    }
}
