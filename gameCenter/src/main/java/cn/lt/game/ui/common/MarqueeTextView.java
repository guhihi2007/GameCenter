package cn.lt.game.ui.common;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView {

	//这个方法是为在布局中使用这个控件的时候调用的。
	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		/* android:maxLines="1"
	                android:ellipsize="marquee"
	                android:focusable="true"
	                android:focusableInTouchMode="true"
	                android:marqueeRepeatLimit="marquee_forever"*/
		
		setSingleLine(true);
		setEllipsize(TruncateAt.MARQUEE);
		setFocusable(true); //可以有焦点
		setFocusableInTouchMode(true); //触摸的时候也有焦点
		setMarqueeRepeatLimit(-1); //无限循环
		
	}

	//一般适用于在代码里面创建这个控件的时候使用
	public MarqueeTextView(Context context) {
		super(context);
		
	}
	
	
	//当window的焦点发生了变化的时候，就会调用这个方法
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(true);
	}
	
	//当同一个window内的某个控件拿到焦点之后，会执行这个方法
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(true, direction, previouslyFocusedRect);
	}
	
	
	//让它以为自己有焦点
	@Override
	public boolean isFocused() {
		return true;
	}
	
	
}
