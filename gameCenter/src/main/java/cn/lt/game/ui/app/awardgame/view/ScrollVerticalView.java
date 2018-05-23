package cn.lt.game.ui.app.awardgame.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;

/**
 * @author chengyong
 * @time 2017/6/1 11:26
 * @des ${下载时title栏动画}
 */

public class ScrollVerticalView extends FrameLayout {
    private TextView download_num;
    private TextView download_num2;
    private TextView numTV;
    private LinearLayout download_container;
    private LinearLayout download_container2;

    public ScrollVerticalView(Context context) {
        super(context);
        initView(context);
    }

    public ScrollVerticalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ScrollVerticalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View rootView = inflate(context, R.layout.scroll_layout, this);
        download_container = (LinearLayout) rootView.findViewById(R.id.download_container);
        download_num = (TextView) rootView.findViewById(R.id.download_num);
        numTV = (TextView) rootView.findViewById(R.id.num);
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtils.i(LogTAG.CHOU, "ScrollVerticalView  =onDetachedFromWindow");
        if(animation!=null){
            animation.cancel();
        }
    }

    /**
     * 设置下载数量
     *
     * @param num
     */
    public void setDownloadNum(int num) {
        LogUtils.i(LogTAG.CHOU, "下载数目："+num);
        download_num.setText("" + num);
        numTV.setText("" + num);
    }

    /**
     * 开始动画
     */
    TranslateAnimation animation;

    public void startAnimation() {
        LogUtils.i(LogTAG.CHOU, "执行动画：");
        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0.35f);
        animation.setDuration(2000);
        animation.setRepeatCount(3);
        animation.setRepeatMode(Animation.RESTART);
        download_container.startAnimation(animation);
    }
}
