package cn.lt.game.ui.app.awardgame.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.lt.game.R;
import cn.lt.game.ui.app.awardgame.listener.ItenFocusListener;


/**
 * @author chengyong
 * @time 2017/6/1 15:35
 * @des ${抽奖的奖品view}
 */

public class SingleAwardsView extends FrameLayout implements ItenFocusListener {

    private FrameLayout overlay;
    private ImageView mAwardIconView;
    private TextView mAwardDes;
    private RelativeLayout mBasicBg;

    public SingleAwardsView(Context context) {
        this(context, null);
    }

    public SingleAwardsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageView getmAwardIconView() {
        return mAwardIconView;
    }

    public TextView getmAwardDes() {
        return mAwardDes;
    }
    public void setBackGound(int srcId) {
        mBasicBg.setBackgroundResource(srcId);
    }

    public SingleAwardsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_award_single, this);
        overlay = (FrameLayout)findViewById(R.id.overlay);
        mAwardIconView = (ImageView)findViewById(R.id.award_iv_icon);
        mAwardDes = (TextView)findViewById(R.id.award_tv_des);
        mBasicBg = (RelativeLayout)findViewById(R.id.item_bg_basic);
    }

    @Override
    public void setFocus(boolean isFocused) {
        if (overlay != null) {
            overlay.setVisibility(isFocused?VISIBLE:GONE);
        }
    }

    @Override
    public void setAwardMessage(String imageUrl, String awardDes) {
        Glide.with(getContext()).load(imageUrl).crossFade().placeholder(R.mipmap.img_default_80x80_round).into(mAwardIconView);
        mAwardDes.setText(awardDes);
    }


}
