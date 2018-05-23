package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import cn.lt.game.R;

public class GradientLeftView extends RelativeLayout {
	private View leftLine;
	private Drawable drawable;
	private Drawable line;

	public GradientLeftView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.gradientleftview_layout,
				this);
		drawable = context.getResources().getDrawable(
				R.drawable.top_bottom_line);
		line = context.getResources().getDrawable(
				R.color.holo_light_grey);
		this.setBackgroundDrawable(drawable);
		initView();
	}

	public GradientLeftView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.gradientleftview_layout,
				this);
		drawable = context.getResources().getDrawable(
				R.drawable.top_bottom_line);
		line = context.getResources().getDrawable(
				R.color.holo_light_grey);
		this.setBackgroundDrawable(drawable);
		initView();
	}

	public GradientLeftView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.gradientleftview_layout,
				this);
		drawable = context.getResources().getDrawable(
				R.drawable.top_bottom_line);
		line = context.getResources().getDrawable(
				R.color.holo_light_grey);
		this.setBackgroundDrawable(drawable);
		initView();
	}

	private void initView() {
		leftLine = findViewById(R.id.gradientleft_Line);
		leftLine.setBackgroundDrawable(line);
	}

	public void setAlpha(float alpha) {
		int min = (int) (255 - alpha);
		drawable.mutate().setAlpha((int) alpha);
		line.mutate().setAlpha(min);

	}
}
