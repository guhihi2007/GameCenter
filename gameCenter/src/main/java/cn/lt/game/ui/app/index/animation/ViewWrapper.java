package cn.lt.game.ui.app.index.animation;

import android.view.View;

public class ViewWrapper {
	private View mTarget;

	public ViewWrapper(View target) {
		mTarget = target;
	}

	public int getWidth() {
		if (mTarget == null) {
			return 0;
		}
		return mTarget.getLayoutParams().width;
	}

	public void setWidth(int width) {
		if (mTarget == null) {
			return;
		}
		mTarget.getLayoutParams().width = width;
		mTarget.requestLayout();
	}
}
