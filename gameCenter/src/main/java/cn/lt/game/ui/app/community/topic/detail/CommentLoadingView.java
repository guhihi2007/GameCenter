package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;

public class CommentLoadingView extends LinearLayout {
	private TextView hint;
	private ProgressBar bar;
	private RelativeLayout layout;

	public CommentLoadingView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public CommentLoadingView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public CommentLoadingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.comment_loading_layout,
				this);
		setBackground(getResources()
				.getDrawable(R.drawable.left_right_botton));
		setOrientation(VERTICAL);
		initView();
	}

	private void initView() {
		hint = (TextView) findViewById(R.id.comment_loading_hint);
		bar = (ProgressBar) findViewById(R.id.comment_loading_progressBar);
		layout = (RelativeLayout) findViewById(R.id.comment_loading_layout);

	}

	public void show() {
		layout.setVisibility(View.VISIBLE);
	}

	public void hide() {
		layout.setVisibility(View.GONE);
	}

}
