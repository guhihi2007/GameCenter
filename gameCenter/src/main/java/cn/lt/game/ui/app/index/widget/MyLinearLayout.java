package cn.lt.game.ui.app.index.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class MyLinearLayout extends LinearLayout {

	private Scroller mScroller;

	public Scroller getmScroller() {
		return mScroller;
	}

	public void setmScroller(Scroller mScroller) {
		this.mScroller = mScroller;
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

}
