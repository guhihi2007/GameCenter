package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import cn.lt.game.R;

public class GradientRightView extends RelativeLayout {
	private View rightLine;
	private Drawable drawable;
	private Drawable line;

	public GradientRightView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.gradientrightview_layout,
				this);
		drawable = context.getResources().getDrawable(
				R.drawable.top_bottom_line);
		this.setBackgroundDrawable(drawable);
		initView();
	}

	public GradientRightView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.gradientrightview_layout,
				this);
		drawable = context.getResources().getDrawable(
				R.drawable.top_bottom_line);
		this.setBackgroundDrawable(drawable);
		initView();
	}

	public GradientRightView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.gradientrightview_layout,
				this);
		drawable = context.getResources().getDrawable(
				R.drawable.top_bottom_line);
		this.setBackgroundDrawable(drawable);
		initView();
	}

	private void initView() {
		rightLine = findViewById(R.id.gradientright_Line);
	}

	public void setAlpha(float alpha) {
		int min = (int) (255 - alpha);
		drawable.mutate().setAlpha((int) alpha);
		rightLine.getBackground().mutate().setAlpha(min);

	}
}
