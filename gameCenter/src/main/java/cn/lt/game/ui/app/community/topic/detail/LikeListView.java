package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.view.RoundImageView;
import cn.lt.game.ui.app.community.model.User;
import cn.lt.game.ui.app.community.personalpage.PersonalActivity;

public class LikeListView extends RelativeLayout {
	private RoundImageView icon;
	private ImageView level;
	private TextView name;
	private TextView time;
	private View line;

	public LikeListView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.likelist_item, this);
		this.setBackgroundResource(R.drawable.left_right_selector);
		initView();
	}

	private void initView() {
		icon = (RoundImageView) findViewById(R.id.likelist_item_icon);
		name = (TextView) findViewById(R.id.likelist_item_name);
		time = (TextView) findViewById(R.id.likelist_item_time);
		line = findViewById(R.id.likelist_item_line);
		level  = (ImageView) findViewById(R.id.likelist_item_level);

	}

	public void upDate(final User user) {
//			ImageLoader.getInstance().displayLogo(user.user_icon, icon);
		ImageloaderUtil.loadLTLogo(getContext(),user.user_icon, icon);

		name.setText(user.user_nickname);
		time.setText(user.upvoted_at);
		level.setImageLevel(user.user_level);

		// 跳转到“ta的主页”
		icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityActionUtils.activity_Jump_Value(v.getContext(), PersonalActivity.class, "userId", user.user_id);
			}
		});
		name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityActionUtils.activity_Jump_Value(v.getContext(), PersonalActivity.class, "userId", user.user_id);
			}
		});
	}

	public void hidetLine(int visibility) {
		line.setVisibility(visibility);
		if (visibility == View.VISIBLE) {
			setBackgroundResource(R.drawable.left_right_selector);
		} else {
			setBackground(getResources().getDrawable(
					R.drawable.left_right_bottom_selector));
		}
	}


}
