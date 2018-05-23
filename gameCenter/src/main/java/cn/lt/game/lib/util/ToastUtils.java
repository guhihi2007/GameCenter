package cn.lt.game.lib.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.lt.game.R;

public class ToastUtils {
    private static Toast mToast;
    private static Handler mhandler = new Handler(Looper.getMainLooper());
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };
    private static View view;

    public static void showToast(Context context, String text) {
        if (null == context) return;
        mhandler.removeCallbacks(r);
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.widget_toast, null);
        }
        TextView tv_tip = (TextView) view.findViewById(R.id.tv_tip);
        if (null != mToast) {
//			mToast.setView(view);
            tv_tip.setText(text);
        } else {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            mToast.setView(view);
            tv_tip.setText(text);
        }
        mhandler.postDelayed(r, 3000);
        mToast.show();
    }

    public static void showToast(Context context, int strId) {
        showToast(context, context.getString(strId));
    }
}
