package cn.lt.game.ui.app.gamedetail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.LogUtils;

public class ScreenshotsSubView extends LinearLayout {

	private int imageCount;
	private Context context;
	private TextView tv_imagePositionAndCount;
	
	

	public ScreenshotsSubView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	public ScreenshotsSubView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public ScreenshotsSubView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	private void init() {
		LayoutInflater.from(context).inflate(R.layout.layout_game_screenshots_position_and_count, this);
		
		tv_imagePositionAndCount = (TextView) findViewById(R.id.tv_imagePositionAndCount);
		tv_imagePositionAndCount.getBackground().setAlpha(200);
	}
	
	public void showCurrentScreenshotsPosition(int position) {
		LogUtils.i("Erosion","ScreenshotsSubView===" + imageCount);
		if(imageCount == 0) {
			return;
		}
		tv_imagePositionAndCount.setText(position + 1  + "/" + imageCount);
	}
	
	public void setImageCount(int imageCount) {
		this.imageCount = imageCount;
	}

}
