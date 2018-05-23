package cn.lt.game.lib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import cn.lt.game.R;

/***
 * @author Administrator daxingxiang
 */
public class RoundTextView extends TextView {

    private Context mContext;
    private int gap = 2;
    private Bitmap mBitmap;
    private Canvas mCanvas1;
    private Paint mPaint;

    public int getmBackgroudAlpha() {
        return mBackgroudAlpha;
    }

    public void setmBackgroudAlpha(int mBackgroudAlpha) {
        this.mBackgroudAlpha = mBackgroudAlpha;
    }

    private int mBackgroudAlpha = 255;

    public void setColor(int color) {
        this.color = color;
    }

    private int color;

    public RoundTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;

    }

    public RoundTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.color = mContext.getResources().getColor(
                R.color.white);

    }

    public RoundTextView(Context context) {
        super(context);
        this.mContext = context;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub

        int width = getWidth();
        int height = getHeight();
        if(mBitmap==null){
            mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_4444);
        }
        if(mCanvas1==null) {
            mCanvas1 = new Canvas(mBitmap);
        }
        if(mPaint==null) {
            mPaint = new Paint();
        }
//        paint.setColor(color);
//        canvas1.drawRect(0, 0, width, height, paint);
//        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
//        paint.setAlpha(255);
        mPaint.setColor(color);
        // 去锯齿；
        mPaint.setAntiAlias(true);
        mCanvas1.drawCircle(
                width / 2,
                height / 2,
                width / 2
                        - gap, mPaint);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        super.onDraw(canvas);
    }

}
