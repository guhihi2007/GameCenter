package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;

public class GroupNameTitle extends RelativeLayout {

	private TextView name;
	
	private ImageView mIcon;

	public GroupNameTitle(Context context) {
		this(context, null);

	}

	public GroupNameTitle(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	public GroupNameTitle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.groupname_title, this);
		setMinimumHeight((int) context.getResources().getDimension(R.dimen.group_list_actionbar_height));
		setBackgroundResource(R.drawable.groupnametitle_selector);
		initView();
	}

	private void initView() {
		name = (TextView) findViewById(R.id.groupname_name);
		mIcon = (ImageView) findViewById(R.id.groupname_icon);
	}

	public void setTitle(String text) {
		name.setText(text);
	}
	
	public void setIcon(String url) {
		
	}

}
