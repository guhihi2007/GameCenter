package cn.lt.game.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import cn.lt.game.R;

public class NoNetWorkViewInstance {
	private static NoNetWorkView instance = null;

	public static NoNetWorkView getInstance(Context context) {
		if (instance == null) {
			instance = new NoNetWorkView(context);
		}
		return instance;

	}

	public static class NoNetWorkView extends RelativeLayout {

		public NoNetWorkView(Context context) {
			this(context, null);
		}

		public NoNetWorkView(Context context, AttributeSet attrs) {
			this(context, attrs, 0);
			// TODO Auto-generated constructor stub
		}

		public NoNetWorkView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			LayoutInflater.from(context).inflate(
					R.layout.loading_fail_not_network, this);
			initView();
		}

		private void initView() {

		}

	}

}
