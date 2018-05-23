package cn.lt.game.ui.app.gamegift.view;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.IntegratedDataUtil;
import cn.lt.game.lib.util.image.ImageloaderUtil;

public class GiftInfoView extends FrameLayout {

    private ImageView mImage;
    private TextView mName;
    private TextView mPackageSize;
    private TextView mDownloadCounts;
    private TextView mGiftCounts;
    private TextView mReceivedCounts;
    private GiftDomainDetail mGift;
    private Context mContext;


    public GiftInfoView(Context context) {
        super(context);
    }

    public GiftInfoView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.gamegift_title_layout,
                this);
        mName = (TextView) findViewById(R.id.mygift_title_txName);
        mPackageSize = (TextView) findViewById(R.id.mygift_title_txSize);
        mImage = (ImageView) findViewById(R.id.mygift_title_img);
        mDownloadCounts = (TextView) findViewById(R.id.mygift_title_txCountTitle);
        mGiftCounts = (TextView) findViewById(R.id.mygift_title_packCountTitle);
        mReceivedCounts = (TextView) findViewById(R.id.mygift_title_txAlreadyGetCount);
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                goGameDetailHomeActivity(context);
            }
        });
    }

    public GiftDomainDetail getmGift() {
        return mGift;
    }

    public void setmGift(GiftDomainDetail mGift) {
        this.mGift = mGift;
    }

    private void goGameDetailHomeActivity(Context context) {
        if (mGift != null) {
//            Intent intent = new Intent(context, GameDetailHomeActivity.class);
//            intent.putExtra("id", mGift.getGame().getUniqueIdentifier());
//            intent.putExtra("forum_id", mGift.getGame().getGroupId());
//            context.showDialog(intent);
            if (!TextUtils.isEmpty(mGift.getGame().getUniqueIdentifier())&&!"null".equals(mGift.getGame().getUniqueIdentifier())){
                ActivityActionUtils.JumpToGameDetail(mContext,Integer.parseInt(mGift.getGame().getUniqueIdentifier()));
            }
        }
    }

    public void fillView(GiftDomainDetail gift) {
        if (gift == null) {
            return;
        }
        setmGift(gift);
        mName.setText(gift.getGame().getName());
        mPackageSize.setText(IntegratedDataUtil.calculateSizeMB(gift.getGame().getPkgSize()));
        mDownloadCounts.setText(IntegratedDataUtil.calculateCounts(Integer.valueOf(gift
                .getGame().getDownCnt())));
        mGiftCounts.setText(Html.fromHtml(String.format(mContext.getResources()
                .getString(R.string.gift_total), String.valueOf(gift.getTotal()))));
        mReceivedCounts.setText(Html.fromHtml(String.format(mContext
                .getResources().getString(R.string.gift_received), String.valueOf(gift
                .getReceivedCount()))));
//        ImageLoader.getInstance().displayRoundImage(gift.getGame().getIconUrl(), mImage);
        ImageloaderUtil.loadRoundImage(getContext(),gift.getGame().getIconUrl(), mImage);
    }

}
