package cn.lt.game.ui.app.specialtopic.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.domain.essence.FunctionEssenceImpl;
import cn.lt.game.domain.essence.ImageType;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsDataProductorImpl;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.weight.ItemView;

/**
 * Created by Administrator on 2015/12/14.
 */
public class ItemSpecialTopicView extends ItemView {

    private ImageView iv_left;
    private TextView tv_title;

    private int screenWidth;// 实际屏幕宽

    public ItemSpecialTopicView(Context context, BaseOnclickListener clickListener) {
        super(context);
        this.mContext = context;
        this.mClickListener = clickListener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        LayoutInflater.from(context).inflate(
                R.layout.item_special_topic_v2, this);
        initView();
    }

    public ItemSpecialTopicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ItemSpecialTopicView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private void initView() {
        iv_left = (ImageView) findViewById(R.id.iv_specialTopicImage);
        tv_title = (TextView) findViewById(R.id.tv_specialTopicTitle);
        screenWidth = Utils.getScreenWidth(mContext);
        int imageViewHeight = (((screenWidth - DensityUtil.dip2px(mContext, 10)) * 140) / 420);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, imageViewHeight);
        iv_left.setLayoutParams(lp);
    }

    @Override
    public void fillLayout(ItemData<? extends BaseUIModule> data,int position, int listSize) {
        try {
            mItemData = data;
            if (mItemData != null) {
                fillView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillView() {
        UIModule module = (UIModule) mItemData.getmData();
        FunctionEssence data = (FunctionEssenceImpl) module.getData();
        ImageloaderUtil.loadBigImage(mContext,data.getImageUrl().get(ImageType.COMMON),iv_left);
        tv_title.setText(data.getTitle());
        StatisticsEventData sData = StatisticsDataProductorImpl.produceStatisticsData(mItemData
                        .getmPresentData(), data.getUniqueIdentifier(), mClickListener
                        .getmPageName(),
                ReportEvent.ACTION_CLICK, null, null, null);
        setViewTagForClick(this, data, data.getDomainEssence().getDomainType(), mItemData
                .getmPresentType(), sData);
    }


}
