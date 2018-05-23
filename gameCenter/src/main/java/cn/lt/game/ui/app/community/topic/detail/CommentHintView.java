package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;

public class CommentHintView extends RelativeLayout {
	private RelativeLayout hintLayout;
	private TextView hintView;

	public CommentHintView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public CommentHintView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public CommentHintView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context)
				.inflate(R.layout.comment_hint_layout, this);
		setBackground(getResources().getDrawable(
				R.drawable.left_right_botton));

		initView();
	}

	private void initView() {
		hintLayout = (RelativeLayout) findViewById(R.id.group_list_hintLayout);
		hintView = (TextView) findViewById(R.id.group_list_empty_Hint);
	}

	public void hintSetVisibility(int visibility) {
		hintLayout.setVisibility(visibility);
	}

	public void setHint(String hint) {
		hintView.setText(hint);
	}

}
