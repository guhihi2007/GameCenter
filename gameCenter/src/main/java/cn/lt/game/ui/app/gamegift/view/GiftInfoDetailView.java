package cn.lt.game.ui.app.gamegift.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import cn.lt.game.R;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.IntegratedDataUtil;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;

public class GiftInfoDetailView extends FrameLayout {

    private ImageView mImage;
    private TextView mName;
    private TextView mResetCounts;
    private TextView mResetTime;
    private GiftDomainDetail mGift;
    private Context mContext;


    public GiftDomainDetail getmGame() {
        return mGift;
    }

    public void setmGame(GiftDomainDetail mGame) {
        this.mGift = mGame;
    }


    public GiftInfoDetailView(Context context) {
        super(context);
    }

    public GiftInfoDetailView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.gift_title_layout, this);
        mName = (TextView) findViewById(R.id.mygift_title_txName);
        mImage = (ImageView) findViewById(R.id.mygift_title_img);
        mResetCounts = (TextView) findViewById(R.id.mygift_title_packCountTitle);
        mResetTime = (TextView) findViewById(R.id.mygift_title_txAlreadyGetCount);
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                goGameDetailHomeActivity(context);
            }
        });
    }

    private void goGameDetailHomeActivity(Context context) {
        if (mGift != null) {
            if (!TextUtils.isEmpty(mGift.getGame().getUniqueIdentifier()) && !"null".equals(mGift.getGame().getUniqueIdentifier())) {
                ActivityActionUtils.JumpToGameDetail(mContext, Integer.parseInt(mGift.getGame().getUniqueIdentifier()));
            }
        }
    }

    public void fillView(GiftDomainDetail mGiftInfo) {
        if (mGiftInfo == null) {
            return;
        }
        setmGame(mGiftInfo);
        mName.setText(mGiftInfo.getTitle());
        mResetCounts.setText(Html.fromHtml(String.format(mContext.getResources().getString(R.string.gift_reset), IntegratedDataUtil.calculatePrecent(mGiftInfo.getRemain(), mGiftInfo.getTotal()))));

        mResetTime.setText(Html.fromHtml(String.format(mContext.getResources().getString(R.string.gift_reset_time), getTime(mGiftInfo.getEndTime()))));
//		ImageLoader.getInstance().displayRoundImage(mGiftInfo.getGame().getIconUrl(), mImage);
        ImageloaderUtil.loadRoundImage(getContext(), TextUtils.isEmpty(mGiftInfo.getIconUrl()) ? mGiftInfo.getGame().getIconUrl() : mGiftInfo.getIconUrl(), mImage);
    }

    @SuppressLint("SimpleDateFormat")
    private String getTime(String date) {
        try {
            long vaildDate = TimeUtils.string2Long(date.contains(":") ? date : date + " 23:59:59", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
            long currentDate = System.currentTimeMillis();
            long gapDate = vaildDate - currentDate;
            return gapDate > 0 ? gapDate / 1000 / 60 / 60 / 24 + "天" : "0天";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "0天";
    }

}
