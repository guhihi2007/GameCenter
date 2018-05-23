package cn.lt.game.ui.common;

import android.view.View;
import android.view.View.OnClickListener;

public class ClickListenerSetter {
	
	public static void set(View view, int resId, OnClickListener l) {
		view.findViewById(resId).setOnClickListener(l);
	}
	
}

