package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;

import cn.lt.game.R;
import cn.lt.game.lib.util.ColorUtil;

public class SelectButton extends StatusButton {

	public SelectButton(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public SelectButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public SelectButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		addColorFilter(
				new int[] { -android.R.attr.state_enabled },
				new ColorMatrixColorFilter(ColorUtil.changlePure(getResources()
						.getColor(R.color.theme_green))));
	}

}
