package cn.lt.game.ui.app.adapter.weight;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.domain.essence.IdentifierType;
import cn.lt.game.domain.essence.ImageType;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.view.RoundTextView;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;

public class ItemBannerView extends ItemView {

    private ImageView mIv;
    private TextView mTitle;
    public View mChangPadding;

    private RoundTextView mRoundView;

    public ItemBannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ItemBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemBannerView(Context context, BaseOnclickListener clickListener) {
        super(context);
        this.mContext = context;
        this.mClickListener = clickListener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        LayoutInflater.from(context).inflate(R.layout.index_item_subject_single, this);
        init();
    }

    private void init() {
        mIv = (ImageView) findViewById(R.id.bigImageIv);
        mRoundView = (RoundTextView) findViewById(R.id.rtv_item_banner_view);
//        mRoundView.setmBackgroudAlpha(255*8/10);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mChangPadding = findViewById(R.id.change_padding);
        // 调整大图尺寸
        int imageViewHeight = (((Utils.getScreenWidth(mContext) - DensityUtil.dip2px(mContext, 10)) * 152) / 456);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, imageViewHeight);
        mIv.setLayoutParams(lp);
    }

    public void fillView() {
        try {
            UIModule module = (UIModule) mItemData.getmData();
            FunctionEssence data = (FunctionEssence) module.getData();
            @SuppressWarnings("unchecked") StatisticsEventData sData = produceStatisticsData(mItemData.getmPresentData(),
                    data.getUniqueIdentifier(), mPageName, ReportEvent.ACTION_CLICK, null, data.getUniqueIdentifierBy(IdentifierType.URL), data.getDomainEssence().getDomainType().toString(), "");

            //添加点击时需要的各类数据； 1、实体对象；2、统计对象；3、PresentType,
            setViewTagForClick(this, data, data.getDomainEssence().getDomainType(), mItemData.getmPresentType(), sData);
//                ImageLoader.getInstance().display(data.getImageUrl().get(ImageType.COMMON), mIv,
//                        R.mipmap.img_default, 0);
            ImageloaderUtil.loadBigImage(getContext(), data.getImageUrl().get(ImageType.COMMON), mIv);
            mTitle.setText(data.getTitle());
            String textColor = data.getColor();
            try {
                mTitle.setBackgroundColor(Color.parseColor(textColor.startsWith("#") ? textColor : "#" + textColor));
            } catch (Exception e) {
                e.printStackTrace();
                mTitle.setBackgroundColor(Color.TRANSPARENT);
            }
            try {
                if (textColor.startsWith("#")) {
                    textColor = "#aa" + textColor.replace("#", "");
                }
                mRoundView.setColor(Color.parseColor(textColor.startsWith("#") ? textColor : "#aa" + textColor));
            } catch (Exception e) {
                e.printStackTrace();
                mRoundView.setColor(Color.TRANSPARENT);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fillLayout(ItemData<? extends BaseUIModule> data, int position, int listSize) {
        try {
            mItemData = data;
            if (mItemData != null) {
                fillView();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
