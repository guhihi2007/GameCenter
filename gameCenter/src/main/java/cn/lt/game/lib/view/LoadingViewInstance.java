package cn.lt.game.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import cn.lt.game.R;

public class LoadingViewInstance {
	private static final String TAG = "LoadingView";
	private static LoadingView instance = null;

	public static LoadingView getInstance(Context context) {
		if (instance == null) {
			instance = new LoadingView(context);
		}else{
		}
		return instance;

	}

	public static class LoadingView extends FrameLayout {
		
		

		public LoadingView(Context context) {
			this(context, null);
		}

		public LoadingView(Context context, AttributeSet attrs) {
			this(context, attrs, 0);
			// TODO Auto-generated constructor stub
		}

		public LoadingView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			LayoutInflater.from(context).inflate(
					R.layout.loading_progress_bar2, this);
			setTag(TAG);
			initView();
		}
		
		public void init(){
			
		}

		private void initView() {

		}

	}
}
