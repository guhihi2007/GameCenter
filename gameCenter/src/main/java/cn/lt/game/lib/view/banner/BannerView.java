package cn.lt.game.lib.view.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleGroup;
import cn.lt.game.domain.essence.DomainType;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.domain.essence.IdentifierType;
import cn.lt.game.domain.essence.ImageType;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsDataProductorImpl;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.weight.ItemView;
import cn.lt.game.ui.app.jump.IJumper;
import cn.lt.game.ui.app.jump.JumpFactory;

public class BannerView extends ItemView implements OnItemClickListener, OnItemSelectedListener {

    /**
     * 多久切换轮播图
     */
    public static final int sleepTime = 4000;
    public static final int DEFAULT_HEIGHT = -1;
    /**
     * 页签
     */
    private ArrayList<View> pointList;
    private List<ItemData<UIModule>> mBannerItemDatas;
    private MyGallery bannerViewPager;
    private LinearLayout pointLayout;
    private headViewOnGlobalLayoutListener globalLayoutListener;
    private ImgAdapter mBannerAdapter;
    private int preSelImgIndex = 0;
    private float mBannerHeight;
    private boolean showPoint = true;
    private String mPageName = Constant.PAGE_INDEX;
    private boolean needShowWithoutData;

    /**
     * @param context  上下文
     * @param height   指定轮播图的高度，使用默认值{@link #DEFAULT_HEIGHT}
     * @param listener 点击事件监听器
     */
    public BannerView(Context context, int height, BaseOnclickListener listener, boolean
            showWithoutData) {
        super(context);
        this.mContext = context;
        this.mClickListener = listener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        this.needShowWithoutData = showWithoutData;
        //轮播图默认高度；
        if (height < 0) {
            mBannerHeight = context.getResources().getDimensionPixelOffset(R.dimen.banner_height);
        } else {
            mBannerHeight = height;
        }
        LayoutInflater.from(context).inflate(R.layout.gift_header_layout, this);
        initInfiniteLoopView(true);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
        mBannerHeight = mTypedArray.getDimension(R.styleable.BannerView_banner_height, context
                .getResources().getDimensionPixelOffset(R.dimen.banner_height));
        showPoint = mTypedArray.getBoolean(R.styleable.BannerView_show_point, true);
        needShowWithoutData = mTypedArray.getBoolean(R.styleable.BannerView_show_without_data,
                false);
        mTypedArray.recycle();
        LayoutInflater.from(context).inflate(R.layout.gift_header_layout, this);
        initInfiniteLoopView(false);
    }

    public void setmPageName(String mPageName) {
        this.mPageName = mPageName;
    }

    public boolean isNeedShowWithoutData() {
        return needShowWithoutData;
    }

    public void setNeedShowWithoutData(boolean needShowWithoutData) {
        this.needShowWithoutData = needShowWithoutData;
    }

    @Override
    public void fillLayout(ItemData<? extends BaseUIModule> data,int position, int listSize) {
        try {
            mItemData = data;
            if (mItemData != null) {
                UIModuleGroup mg = (UIModuleGroup) mItemData.getmData();
                //noinspection unchecked
                mBannerItemDatas = mg.getData();
                fillView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void fillView() {
        try {
            if (mBannerItemDatas == null || mBannerItemDatas.size() == 0) {
                if (!needShowWithoutData) {
                    bannerViewPager.setVisibility(View.GONE);
                }
                return;
            }
            bannerViewPager.setVisibility(View.VISIBLE);
            int size = mBannerItemDatas.size();
            List<String> bannerIvs = new ArrayList<>();
            bannerIvs.clear();
            for (int i = 0; i < size; i++) {
                FunctionEssence banner = (FunctionEssence) mBannerItemDatas.get(i).getmData()
                        .getData();
                bannerIvs.add(banner.getImageUrl().get(ImageType.COMMON));
            }
            // 确保每次刷新和第一次初始化时banner图的指示器在第一个位置；
            bannerViewPager.setSelection((Integer.MAX_VALUE >> 2) - (Integer.MAX_VALUE >> 2) %
                    bannerIvs.size());
            setViewPagePoint(pointLayout, size, 0);// 设置轮播图页签
            bannerViewPager.setOnItemSelectedListener(this);
            bannerViewPager.setOnItemClickListener(this);
            mBannerAdapter.setList(bannerIvs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jumpPage(DomainType dt, FunctionEssence fe) {
        IJumper jumper = JumpFactory.produceJumper(PresentType.carousel, dt);
        jumper.jump(fe, mContext);
        LogUtils.i("SS",dt.toString());
    }

    public void initInfiniteLoopView(boolean isFromGiftCenter) {
        bannerViewPager = (MyGallery) findViewById(R.id.gamegiftCenter_viewpager);
        pointLayout = (LinearLayout) findViewById(R.id.giftCenter_logo_pointLayout);
//        if (showPoint) {
//            pointLayout.setVisibility(View.VISIBLE);
//        } else {
            pointLayout.setVisibility(View.GONE);
//        }
        pointLayout.removeAllViews();
        bannerViewPager.getLayoutParams().height = (int) mBannerHeight;
        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(bannerViewPager.getLayoutParams());
        if(isFromGiftCenter) {   //针对礼包中心录播图的改变高度
            layoutParam.setMargins(0, (DensityUtil.dip2px(mContext, 8)), 0, 0);
            bannerViewPager.setLayoutParams(layoutParam);
        }else{
            layoutParam.setMargins(0, (DensityUtil.dip2px(mContext, 0)), 0, 0);
            bannerViewPager.setLayoutParams(layoutParam);
        }
        mBannerAdapter = new ImgAdapter(mContext, null);
        bannerViewPager.setAdapter(mBannerAdapter);
        bannerViewPager.setFocusable(true);
        bannerViewPager.setSelection(Integer.MAX_VALUE >> 2);
    }

    public void stopBannerTimer() {
        if (bannerViewPager != null) {
            bannerViewPager.pause();
        }
    }

    public void startBannerTimer() {
        if (bannerViewPager != null) {
            bannerViewPager.start();
        }
    }

    private void setViewPagePoint(LinearLayout pointLayout, int imgCnt, int curr) {

        // pointLayout.setVisibility(View.VISIBLE);
        pointLayout.removeAllViews();
        pointList = new ArrayList<>();
        if (pointList.size() != 0) {
            pointList.clear();
        }
        int pointHeight = (int) mContext.getResources().getDimension(R.dimen
                .giftCenter_logo_point_height);
        int pointWidth = (int) mContext.getResources().getDimension(R.dimen
                .giftCenter_logo_point_width);
        int marginRight = (int) mContext.getResources().getDimension(R.dimen
                .giftCenter_logo_point_marginRigth);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pointWidth, pointHeight);
        params.setMargins(0, 0, marginRight, 0);
        for (int i = 0; i < imgCnt; i++) {
            pointLayout.addView(getPointView(params, i, imgCnt, curr));
        }
    }

    private TextView getPointView(LinearLayout.LayoutParams params, int i, int imgCnt, int curr) {

        TextView pointView = new TextView(mContext);
        if (i == curr) {
            pointView.setBackgroundResource(R.color.point_orange);
        } else {
            pointView.setBackgroundResource(R.color.white);
        }
        pointView.setLayoutParams(params);
        pointList.add(pointView);
        return pointView;
    }

    public headViewOnGlobalLayoutListener getGlobalLayoutListener() {
        return globalLayoutListener;
    }

    public void setGlobalLayoutListener(headViewOnGlobalLayoutListener globalLayoutListener) {
        this.globalLayoutListener = globalLayoutListener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mBannerItemDatas != null && mBannerItemDatas.size() > 0) {
            int index = position % mBannerItemDatas.size();
            ItemData<UIModule> item = mBannerItemDatas.get(index);
            FunctionEssence fe = (FunctionEssence) item.getmData().getData();
            StatisticsEventData sData = StatisticsDataProductorImpl.produceStatisticsData(item.getmPresentData(), fe
                    .getUniqueIdentifier(), mPageName, ReportEvent
                    .ACTION_CLICK, Constant.RETRY_TYPE_MANUAL, fe.getUniqueIdentifierBy(IdentifierType.URL), fe.getDomainEssence().getDomainType()
                    .toString());
            DCStat.clickEvent(sData);
            DomainType dt = fe.getDomainEssence().getDomainType();
            jumpPage(dt, fe);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            position = position % mBannerItemDatas.size();
            pointLayout.getChildAt(preSelImgIndex).setBackgroundResource(R.color.white);
            pointLayout.getChildAt(position).setBackgroundResource(R.color.point_orange);
            preSelImgIndex = position;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("Banner", "轮播图设置错误");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface headViewOnGlobalLayoutListener {
        void OnGlobalLayoutListener(View headRoot, int minHeaderTranslation);
    }

}
