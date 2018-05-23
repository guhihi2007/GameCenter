package cn.lt.game.ui.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;

import java.util.Locale;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.statistics.manger.DCStat;

/**
 * Created by yuan on 2017/3/14.
 * * 用于跳转广告的临时页面
 */

public class SplashADActivity extends Activity implements SplashADListener {

    private ViewGroup container;
    private TextView skipView;
    private SplashAD splashAD;
    private static final String SKIP_TEXT = "点击跳过 %d";
    public boolean canJump = false;
    private boolean isFromGameCenterActivity;
    private ImageView rootIv;
    private long time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_demo);
        int srceenHeight = MyApplication.height;
        container = (ViewGroup) this.findViewById(R.id.splash_container);
        skipView = (TextView) findViewById(R.id.skip_view);
        rootIv = (ImageView) findViewById(R.id.app_logo);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rootIv.getLayoutParams();
        params.height = (int) (srceenHeight * 0.184375);
        MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                time = System.currentTimeMillis();
                splashAD = new SplashAD(SplashADActivity.this, container, skipView, Constant.APPID, Constant.SplashPosID, SplashADActivity.this, 3000);
            }
        }, 1000);
        MyApplication.isBackGroud = false;

        isFromGameCenterActivity = getIntent().getBooleanExtra("fromGameCenterActivity", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            next();
        }
        canJump = true;
    }


    @Override
    public void onADDismissed() {
        LogUtils.i("AD_DEMO", "SplashADDismissed");
        next();
    }

    @Override
    public void onNoAD(int i) {
        LogUtils.i("AD_DEMO", "LoadSplashADFail, eCode=" + i);
        finish();
        DCStat.adsSpreadEvent("noAD", "GDT", "splash");
    }

    @Override
    public void onADPresent() {
        LogUtils.i("AD_DEMO", "SplashADPresent");
        long times = System.currentTimeMillis();
        long spaTime = (times - time)/1000;
        LogUtils.i("AD_DEMO","saptime==========" + spaTime);
        skipView.setVisibility(View.VISIBLE);
        DCStat.adsSpreadEvent("adPresent", "GDT", "splash");
        rootIv.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            LogUtils.i("oooo", "按下了");
            finish();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onADClicked() {
        DCStat.adsSpreadEvent("adClicked", "GDT", "splash");
    }

    @Override
    public void onADTick(long l) {
        LogUtils.i("AD_DEMO", "SplashADTick " + l + "ms");
        skipView.setText(String.format(Locale.CHINESE,SKIP_TEXT, Math.round(l / 1000f)));
    }

    private void next() {
        if (canJump) {
            if (isFromGameCenterActivity) {
                finish();
            }
        } else {
            canJump = true;
        }
    }

    /**
     * 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
