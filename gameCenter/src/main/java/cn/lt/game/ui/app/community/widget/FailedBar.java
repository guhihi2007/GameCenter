package cn.lt.game.ui.app.community.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.ui.app.community.DraftsActivity;
import de.greenrobot.event.EventBus;

/***
 * 发送标题、评论、回复失败提示栏
 * 
 * @author ltbl
 * 
 */

public class FailedBar extends FrameLayout  {
	private Context context;
	private TextView tv_title;
	private RelativeLayout rl_onclick;
	
	public FailedBar(Context context) {
		super(context);
	}
	public FailedBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FailedBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.failed_bar, this);
		initView();
		this.setVisibility(View.GONE);
	}
	// 需要弹出的时候要调用这个方法
	public void setToFont() {
		this.setVisible(0);
		this.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_translate_out));
		this.bringToFront();
	}

	// 与上个相反
	public void setVisible(int i) {
		this.setVisibility(i);
	}

	private void initView() {
		tv_title = (TextView) findViewById(R.id.tv_failed);
		rl_onclick = (RelativeLayout) findViewById(R.id.rl_onclick);
		rl_onclick.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				context.startActivity(new Intent(context, DraftsActivity.class));
				//点击了以后，其他地方全部消失草稿箱提示栏
				EventBus.getDefault().post("hideFailedBar");
				setVisible(4);
			}
		});
	}

	public void setTitle(String title) {
		tv_title.setText(title);
	}

}
