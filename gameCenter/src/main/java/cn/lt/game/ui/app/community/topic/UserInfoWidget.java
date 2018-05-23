package cn.lt.game.ui.app.community.topic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.ui.app.community.personalpage.PersonalActivity;

/**
 * 用户基本信息控件（话题使用）
 */
public class UserInfoWidget extends RelativeLayout {

    private ImageView user_icon;
    private ImageView admin;
    private ImageView user_level;
    private TextView user_name;
    private TextView time;
    private int userId;


    public UserInfoWidget(Context context) {
        super(context);
        init(context);
        init_findView();
    }

    public UserInfoWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        init_findView();
    }

    public UserInfoWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        init_findView();
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.widget_user_info, this);
    }

    public void setUserInfo(int userId) {
        this.userId = userId;
    }

    private void init_findView() {
        user_icon = (ImageView) findViewById(R.id.user_icon);
        admin = (ImageView) findViewById(R.id.admin);
        user_name = (TextView) findViewById(R.id.user_name);
        time = (TextView) findViewById(R.id.time);
        user_level = (ImageView) findViewById(R.id.user_level);

        // 跳转到“ta的主页”
        user_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityActionUtils.activity_Jump_Value(v.getContext(), PersonalActivity.class, "userId",userId);
            }
        });
        user_name.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityActionUtils.activity_Jump_Value(v.getContext(), PersonalActivity.class, "userId",userId);
            }
        });
    }

    public void loadUserIcon(String url) {
//        ImageLoader.getInstance().display(url, user_icon);
        ImageloaderUtil.loadImage(getContext(),url, user_icon, false);
    }

    public void setUserType(int type) {
        if (type == UserType.ADMIN) {
            admin.setVisibility(View.VISIBLE);
        } else {
            admin.setVisibility(View.GONE);
        }
    }

    public void setUserLevel(int level) {
        user_level.setImageLevel(level);
        user_level.setVisibility(View.VISIBLE);
    }


    public void setUser_name(String user_name) {
        this.user_name.setText(user_name);
    }

    public void setTime(String time) {

        this.time.setText(TimeUtils.curtimeDifference(time));
    }

}
