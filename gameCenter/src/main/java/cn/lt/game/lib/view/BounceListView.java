package cn.lt.game.lib.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;

/**
 * 头部和尾部具有反弹效果的Listview
 * 
 * @author wcn
 * 
 */
public class BounceListView extends ListView {

	private Context mContext;
	private boolean outBound = false;
	private int distance;
	private int firstOut;
	private float lastRawY = 0;
	private float distanceY = 0;
	private final int DELTA = 3;

	public BounceListView(Context context) {
		super(context);
		this.mContext = context;
		if (Build.VERSION.SDK_INT >= 9) {
			this.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
	}

	public BounceListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		if (Build.VERSION.SDK_INT >= 9) {
			this.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
	}

	public BounceListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		if (Build.VERSION.SDK_INT >= 9) {
			this.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
	}

	GestureDetector gestureDetector = new GestureDetector(mContext,
			new OnGestureListener() {

				private float lastRawY = 0;

				@Override
				public boolean onDown(MotionEvent e) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void onLongPress(MotionEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public boolean onScroll(MotionEvent e1, MotionEvent e2,
						float distanceX, float distanceY) {
					int firstPos = getFirstVisiblePosition();
					int lastPos = getLastVisiblePosition();
					int itemCount = getCount();
					if (outBound && firstPos != 0 && lastPos != (itemCount - 1)) {
						scrollTo(0, 0);
						lastRawY = e2.getRawY();
						return false;
					}
					View firstView = getChildAt(firstPos);
					View lastView = getChildAt(lastPos - 1);
					if (!outBound) {
						firstOut = (int) e2.getRawX();
					}
					if (firstView != null
							&& (outBound || (firstPos == 0
									&& firstView.getTop() == 0 && distanceY < 0))) {
						distance = (int) (firstOut - e2.getRawY());

						if (shouldScroll()) {
							scrollBy(0, distance / 2);
						}
						lastRawY = e2.getRawY();
						return true;
					}
					if (lastView == null
							&& (outBound || (lastPos == itemCount - 1 && distanceY > 0))) {
//						Log.d("bottom", "bottom");
						distance = (int) ((itemCount - 1) / 2 - e2.getRawY());
						if (shouldScroll()) {
							scrollBy(0, distance / 2);
						}
						lastRawY = e2.getRawY();
						return true;
					}
					return false;
				}

				@Override
				public void onShowPress(MotionEvent e) {

				}

				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					return false;
				}
			});

	/**
	 * 最早响应触屏事件，按下和释放响应两次
	 */
	public boolean dispatchTouchEvent(MotionEvent ev) {
		distanceY = lastRawY - ev.getRawY();
		if (getFirstVisiblePosition() == 0) {
			int act = ev.getAction();
			if ((act == MotionEvent.ACTION_UP || act == MotionEvent.ACTION_CANCEL)
					&& outBound) {
				outBound = false;
			}
            outBound = gestureDetector.onTouchEvent(ev);
			Rect rect = new Rect();
			getLocalVisibleRect(rect);
			TranslateAnimation am = new TranslateAnimation(0, 0, -rect.top, 0);
			am.setDuration(300);
			if (shouldScroll()) {
				if (-rect.top != 0) {
					startAnimation(am);
					scrollTo(0, 0);
				}
			}
		}
//		Log.d("getLastVisiblePosition()", getLastVisiblePosition() + "");
//		Log.d("getCount()", getCount() + "");
		if (getLastVisiblePosition() == getCount() - 1) {
			int act = ev.getAction();
			if ((act == MotionEvent.ACTION_DOWN || act == MotionEvent.ACTION_CANCEL)
					&& outBound) {
				outBound = false;
			}
            outBound = gestureDetector.onTouchEvent(ev);
			if (outBound) {
				Rect rect1 = new Rect();
				getLocalVisibleRect(rect1);
				 TranslateAnimation am1 = new TranslateAnimation(0, 0,
				 (-rect1.top - rect1.bottom) / 3, getCount() - 1);
				am1.setDuration(500);
				if (shouldScroll()) {
					startAnimation(am1);
					scrollTo(0, 0);
				}
			}
		}
		lastRawY = ev.getRawY();
		return super.dispatchTouchEvent(ev);
	}

	private boolean shouldScroll() {
		return Math.abs(distanceY) >= DELTA;
	}

}
