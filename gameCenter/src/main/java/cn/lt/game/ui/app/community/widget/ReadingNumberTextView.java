package cn.lt.game.ui.app.community.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.ui.app.community.model.IReadingNum;
import cn.lt.game.ui.app.community.model.JumpEvent;
import de.greenrobot.event.EventBus;
/**
 * 
 * 阅读数按键
 *
 */
public class ReadingNumberTextView extends TextView {
	
	private IReadingNum data;

	public ReadingNumberTextView(Context context) {
		super(context);
		init(context);
		EventBus.getDefault().register(ReadingNumberTextView.this);
	}

	public ReadingNumberTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		EventBus.getDefault().register(ReadingNumberTextView.this);
	}

	public ReadingNumberTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		EventBus.getDefault().register(ReadingNumberTextView.this);
	}
	
	public void onEventMainThread(JumpEvent info) {
		if (info.getGroupId() == data.getGroupId()
				&& info.getTopicId() == data.getTopicId()) {
			increaseNum();
		}
	}

	public void setNumber(int num) {
		setText(getNumString(data.getReadingNum()));
	}

	public void setData(IReadingNum data) {
		this.data = data;
		setNumber(data.getReadingNum());
	}

	public void increaseNum() {
		data.setReadingNum(data.getReadingNum() + 1);
		setNumber(data.getReadingNum());
	}
	
	private void init(Context context) {
		setClickable(false);
	}
	private String getNumString(int num) {
		return num == 0 ? getResources().getString(R.string.reading) : String
				.valueOf(num);
	}
}
