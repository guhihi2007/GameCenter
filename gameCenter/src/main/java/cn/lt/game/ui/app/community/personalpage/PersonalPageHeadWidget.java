package cn.lt.game.ui.app.community.personalpage;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import cn.lt.game.R;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.ui.app.community.model.OthersPage;
import cn.lt.game.ui.app.community.widget.UserGradeProgressBar;

/**
 * Created by tiantian on 2015/11/13.
 * 我的社区/TA的主页头部自定义控件
 */
public class PersonalPageHeadWidget extends RelativeLayout {
    private ImageView mUserHeadView;
    private TextView mUserName;
    private ImageView mUserLevel;
    private TextView mUserSign;
    private TextView mGold;
    private UserGradeProgressBar mProgressBar;

    public PersonalPageHeadWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PersonalPageHeadWidget(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.setMinimumHeight((int)context.getResources().getDimension(R.dimen.topic_item_hintHeight));
        LayoutInflater.from(context).inflate(R.layout.personal_head_view, this);
        mUserHeadView = (ImageView) findViewById(R.id.iv_user_head);
        mUserName = (TextView) findViewById(R.id.tv_user_nickname);
        mUserLevel = (ImageView) findViewById(R.id.tv_user_level);
        mUserSign = (TextView) findViewById(R.id.tv_user_sign);
        mGold = (TextView) findViewById(R.id.tv_user_gold);
        mProgressBar = (UserGradeProgressBar) findViewById(R.id.pb_user_grade);
    }

    public void setData(OthersPage user, boolean isGradeVisible) {
        ImageloaderUtil.loadUserHead(getContext(),user.getUser_icon(), mUserHeadView);
        mUserName.setText(user.getUser_nickname());
        if (!isGradeVisible) {
            mProgressBar.setVisibility(View.GONE);
        }
        mProgressBar.setProgress(user.getUser_upgrade_percent());
        mUserLevel.setImageLevel(user.getUser_level());
        mUserSign.setText(TextUtils.isEmpty(user.getUser_summary()) ? "这个人很懒，什么也没留下" : user.getUser_summary());
        mGold.setText(user.getUser_gold() + "");

        ImageloaderUtil.loadImageCallBack(getContext(), user.getBackground_img(), new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                if (resource != null){
                    PersonalPageHeadWidget.this.setBackgroundDrawable(resource);
                } else {
                    setBackgroundResource(R.mipmap.ic_my_com_backgroud);
                }
            }
        });
    }
}
