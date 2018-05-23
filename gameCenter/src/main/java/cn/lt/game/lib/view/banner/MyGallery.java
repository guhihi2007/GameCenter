package cn.lt.game.lib.view.banner;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

import java.util.Timer;
import java.util.TimerTask;

import cn.lt.game.lib.util.LogUtils;

@SuppressWarnings("deprecation")
public class MyGallery extends Gallery {

    private static final int timerAnimation = 1;

    private boolean isPressed;

    private static final String TAG = "MyGallery";

    private final Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case timerAnimation:
                    if (!isPressed) {
                        int position = getSelectedItemPosition();
                        LogUtils.i("msg", "position:" + position);
                        if (position >= (getCount() - 1)) {
                            onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
                        } else {
                            onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
                        }
                    }
                    break;

            }
        }

    };

    @Override
    public void playSoundEffect(int soundConstant) {
        return;
    }

    private Timer timer;

    public MyGallery(Context paramContext) {
        super(paramContext);
    }

    public MyGallery(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public MyGallery(Context paramContext, AttributeSet paramAttributeSet,
                     int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    private boolean isScrollingLeft(MotionEvent paramMotionEvent1,
                                    MotionEvent paramMotionEvent2) {
        float f2 = paramMotionEvent2.getX();
        float f1 = paramMotionEvent1.getX();
        return f2 > f1;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            LogUtils.d(TAG, "MyGallery are pressed ----------");
            isPressed = true;
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            LogUtils.d(TAG, "MyGallery are up ----------");
            isPressed = false;
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean onFling(MotionEvent paramMotionEvent1,
                           MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
        int keyCode;
        if (isScrollingLeft(paramMotionEvent1, paramMotionEvent2)) {
            keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
        } else {
            keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        onKeyDown(keyCode, null);
        return true;
    }

    public void pause() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    public void start() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            boolean flag = true;

            public void run() {
                if (flag) {
                    isPressed = false;
                    flag = false;
                }
                mHandler.sendEmptyMessage(timerAnimation);
            }
        }, 3000, 3500);
    }
}