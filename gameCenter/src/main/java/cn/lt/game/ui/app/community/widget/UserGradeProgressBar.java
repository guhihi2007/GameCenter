package cn.lt.game.ui.app.community.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import cn.lt.game.R;

/**
 * Created by tiantian on 2015/11/9.
 * 用途：我的社区-用户等级进度条
 */
public class UserGradeProgressBar extends ProgressBar{
    private Paint mPaint;
    private String mText ="加速中";
    private Rect mRect;

    public UserGradeProgressBar(Context context) {
        super(context);
        initPaint();
    }

    public UserGradeProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.mini_font_size));
        if(mRect==null){
            mRect = new Rect(0,0,getWidth(),getHeight());
        }
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = (mRect.bottom + mRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(Color.WHITE);
        canvas.drawText(mText, mRect.centerX(), baseline, mPaint);
    }

    @Override
    public synchronized void setProgress(int progress) {
        setText(mText);
        super.setProgress(progress);
    }
    /**
     * 初始化画笔
     */
    private void initPaint(){
        this.mPaint  =  new Paint();
        this.mPaint.setColor(Color.WHITE);
    }

    /**
     * 设置要画的内容
     * @param string
     */
    public void setText(String string){
        this.mText = string;
    }
}
