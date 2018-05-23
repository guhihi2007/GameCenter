package cn.lt.game.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.lt.game.R;

public class DeleteToggleView  extends FrameLayout{
	private TextView tv;
	
	public DeleteToggleView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public DeleteToggleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.toggle_layout,this);
		initView();
	}
	private void initView() {
		tv = (TextView) findViewById(R.id.toggle_Button);
	}
	
	public void setText(String title){
		tv.setText(title);
	}

}
