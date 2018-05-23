package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

public class GroupListIndicator extends View {
	private Paint mPaint;
	private int width = 0;
	private int height = 0;
	private int offsetX = 0;
	private int angle = 10;
	private Path mPath;

	public GroupListIndicator(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initPaint();

	}

	public GroupListIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initPaint();
	}

	private void initPaint() {
		if (mPaint == null) {
			mPaint = new Paint();
		}
		mPaint.reset();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setTextSize(1.0f);
		mPaint.setColor(Color.parseColor("#dddddd"));
		mPaint.setDither(true);
		mPaint.setFilterBitmap(true);
		mPaint.setSubpixelText(true);
		mPaint.setStrokeWidth(2);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawColor(Color.parseColor("#ffffff"));
		width = getWidth();
		height = getHeight();
		if(mPath==null){
			mPath = new Path();
		}
		mPath.moveTo(0, height);
		mPath.lineTo(offsetX - angle, height);
		mPath.lineTo(offsetX, height / 2 - 5);
		mPath.lineTo(offsetX + angle, height);
		mPath.lineTo(width, height);
		canvas.drawPath(mPath, mPaint);

	}

	public void setButtonOffset(int left, int right) {

		offsetX = (right - left) / 2 + left;
		mPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		initPaint();
		invalidate();
	}

}
