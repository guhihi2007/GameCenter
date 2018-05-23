package cn.lt.game.ui.app.community.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/***
 * 悬浮刷新球
 * 
 * @author tiantian
 * @des
 */
public class CircleRefreshView extends ImageView {

	public OnClickToRefresh onclick;

	public CircleRefreshView(Context context) {
		super(context);
		init();
	}

	public CircleRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setOnclick(OnClickToRefresh onclick) {
		this.onclick = onclick;
	}

	private void init() {

		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
//		rotatebolowImage(this);
		if (MotionEvent.ACTION_UP == event.getAction()) {
			if (onclick != null) {
				onclick.onClick();
			}
		}
		return super.dispatchTouchEvent(event);
	}

	public interface OnClickToRefresh {
		void onClick();
	}

	/** 开始旋转 */
	public void startRotate() {
		rotatebolowImage(this);
	}

	/** 停止旋转 */
	public void stopRotate() {
		stopRotatebolowImage(this);
	}

	/** 顺时针旋转小球 */
	private void rotatebolowImage(ImageView imageView) {
		stopRotatebolowImage(imageView);
		RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(300);// 小球的旋转速率
		ra.setRepeatCount(Animation.INFINITE);
		ra.setRepeatMode(Animation.RESTART);
		LinearInterpolator lir = new LinearInterpolator();
		ra.setInterpolator(lir);
		imageView.setAnimation(ra);
	}

	/** 停止旋转小球 */
	public  void stopRotatebolowImage(ImageView imageView) {
		imageView.clearAnimation();
	}
}
