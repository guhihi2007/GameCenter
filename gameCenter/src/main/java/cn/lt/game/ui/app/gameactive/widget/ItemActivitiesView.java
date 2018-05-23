package cn.lt.game.ui.app.gameactive.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.domain.essence.FunctionEssenceImpl;
import cn.lt.game.domain.essence.ImageType;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.weight.ItemView;

/**
 * Created by Administrator on 2015/12/17.
 */
public class ItemActivitiesView extends ItemView{

    private ImageView iv_icon;
    private TextView tv_title;
    private TextView tv_beginTime;
    private TextView tv_endTime;
    private TextView tv_activitiesSummery;

    public ItemActivitiesView(Context context, BaseOnclickListener clickListener) {
        super(context);
        this.mContext = context;
        this.mClickListener = clickListener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        LayoutInflater.from(context).inflate(
                R.layout.game_activity_item, this);
        initView();
    }

    private void initView() {
        iv_icon = (ImageView) findViewById(R.id.iv_activity_icon);
        // 调整大图尺寸
        int imageViewHeight = (((Utils.getScreenWidth(mContext) - DensityUtil.dip2px(mContext, 10)) * 152) / 456);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, imageViewHeight);
        iv_icon.setLayoutParams(lp);

        tv_title = (TextView) findViewById(R.id.tv_activity_title);
        tv_beginTime = (TextView) findViewById(R.id.tv_begin_time);
        tv_endTime = (TextView) findViewById(R.id.tv_end_time);
        tv_activitiesSummery = (TextView) findViewById(R.id.tv_activity_summery);

    }


    @Override
    public void fillLayout(ItemData<? extends BaseUIModule> data,int position, int listSize) {
        try {
            mItemData = data;
            if (mItemData != null) {
                setData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setData() {
        UIModule module = (UIModule) mItemData.getmData();
        FunctionEssence data = (FunctionEssenceImpl) module.getData();

        ImageloaderUtil.loadImage(getContext(),data.getImageUrl().get(ImageType.COMMON), iv_icon, false);
        tv_title.setText(data.getTitle());
        tv_beginTime.setText("开始时间：" + TimeUtils.StringToString(data.getUpdateTime()));
        tv_endTime.setText("结束时间：" + TimeUtils.StringToString(data.getEndTime()));
        tv_activitiesSummery.setText(data.getSummary());

        StatisticsEventData sData = produceStatisticsData(
                mItemData.getmPresentData(), data.getUniqueIdentifier(), mPageName, ReportEvent.ACTION_CLICK, null, null, data.getDomainEssence().getDomainType().toString(),"");

        setViewTagForClick(this, data, data.getDomainEssence().getDomainType(), mItemData.getmPresentType(), sData);

    }
}
