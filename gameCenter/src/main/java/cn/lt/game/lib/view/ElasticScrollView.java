package cn.lt.game.lib.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

import cn.lt.game.lib.util.LogUtils;

/**
 * 有回弹效果的ScrollView
 */
public class ElasticScrollView extends ScrollView {
	private View inner;
	private float y;
	private Rect normal = new Rect();
	private boolean animationFinish = true;
	private float mDownPosX = 0;
	private float mDownPosY = 0;
	private ElasticCallBack callBack = null;
	private OnScrollListener onScrollListener; // 移动Y事件
	private Boolean scroll = true;// 让不让滚动

	public Boolean getScroll() {
		return scroll;
	}

	public void setScroll(Boolean scroll) {
		this.scroll = scroll;
	}

	public ElasticCallBack getCallBack() {
		return callBack;
	}

	public void setCallBack(ElasticCallBack callBack) {
		this.callBack = callBack;
	}

	public ElasticScrollView(Context context) {
		super(context);
	}

	public ElasticScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			inner = getChildAt(0);
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
		if (onScrollListener != null) {
			onScrollListener.onScroll(t);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final float x = ev.getX();
		final float y = ev.getY();

		final int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mDownPosX = x;
			mDownPosY = y;

			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaX = Math.abs(x - mDownPosX);
			final float deltaY = Math.abs(y - mDownPosY);
			if (deltaX > deltaY) {
				return false;
			}
		}

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (scroll) {
			if (inner == null) {
				return super.onTouchEvent(ev);
			} else {
				commOnTouchEvent(ev);
			}
			return super.onTouchEvent(ev);
		} else {
			return false;
		}

	}

	public void commOnTouchEvent(MotionEvent ev) {
		if (animationFinish) {
			int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				// System.out.println("ACTION_DOWN");
				y = ev.getY();
				super.onTouchEvent(ev);
				break;
			case MotionEvent.ACTION_UP:
				// System.out.println("ACTION_UP");
				y = 0;
				if (isNeedAnimation()) {
					animation();
				}
				LogUtils.i("detail_move", "********ACTION_UP");
				super.onTouchEvent(ev);
				break;
			case MotionEvent.ACTION_MOVE:
				LogUtils.i("detail_move", "********ACTION_MOVE");
				// System.out.println("ACTION_MOVE");
				final float preY = y == 0 ? ev.getY() : y;
				float nowY = ev.getY();
				int deltaY = (int) (preY - nowY);
				// 滚动
				// scrollBy(0, deltaY);

				y = nowY;
				// 当滚动到最上或者最下时就不会再滚动，这时移动布局
				if (isNeedMove()) {
					if (normal.isEmpty()) {
						// 保存正常的布局位置
						normal.set(inner.getLeft(), inner.getTop(),
								inner.getRight(), inner.getBottom());
					}
					// 移动布局
					inner.layout(inner.getLeft(), inner.getTop() - deltaY / 2,
							inner.getRight(), inner.getBottom() - deltaY / 2);
				} else {
					super.onTouchEvent(ev);
				}
				break;
			default:
				break;
			}
		}
	}

	// 开启动画移动

	public void animation() {
		// 开启移动动画
		TranslateAnimation ta = new TranslateAnimation(0, 0, 0, normal.top
				- inner.getTop());
		ta.setDuration(200);
		ta.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				animationFinish = false;

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				inner.clearAnimation();
				// 设置回到正常的布局位置
				inner.layout(normal.left, normal.top, normal.right,
						normal.bottom);
				normal.setEmpty();
				animationFinish = true;

				if (callBack != null) {
					callBack.onViewStop();
				}
				// scrollView.invalidate();
			}
		});
		inner.startAnimation(ta);
	}

	public OnScrollListener getOnScrollListener() {
		return onScrollListener;
	}

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	// 是否需要开启动画
	public boolean isNeedAnimation() {
		return !normal.isEmpty();
	}

	// 是否需要移动布局
	public boolean isNeedMove() {
		int offset = inner.getMeasuredHeight() - getHeight();
		int scrollY = getScrollY();
        return scrollY == 0 || scrollY == offset;
    }

	public interface ElasticCallBack {
		void onViewStop();
	}

	public interface OnScrollListener {
		void onScroll(int scrollY);
	}
	@Override
	public boolean fullScroll(int direction) {
		return super.fullScroll(direction);
	}
}
