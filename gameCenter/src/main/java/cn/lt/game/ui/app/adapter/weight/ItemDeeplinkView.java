package cn.lt.game.ui.app.adapter.weight;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.lt.game.R;
import cn.lt.game.bean.DeeplinkBean;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.IntegratedDataUtil;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.gamedetail.OperationSign;
import cn.lt.game.ui.common.WeakView;
import cn.lt.game.ui.common.listener.InstallButtonClickListener;
import cn.lt.game.ui.installbutton.IndexUpdateButtonState;

public class ItemDeeplinkView extends ItemView {

    public TextView mDescribeTv;// 小编点评
    public View mSignVs;
    public ViewStub mDescribeVs;
    private ImageView mLogoIv;
    private ImageView mMarkIv;
    private TextView mNameTv;
    private ImageView mGold;
    /**
     * 标签，礼包/社区/攻略
     */
    private TextView mLabel;
    /**
     * 游戏评分星级
     */
    private TextView mTagTv;
    private TextView mDownCount;
    private TextView mSizeTv;
    private ProgressBar mDownloadProgressBar;
    private Button mDonwloandBt;// 下载按钮
    private TextView mRankNumberView;
    private FrameLayout mfl_number_single_item;
    public View mChangPadding;
    public View mBottomLine;
    private GameBaseDetail downFile;
    private int gameState;
    private int gamePercent;
    private GameDomainBaseDetail mGame;
    private TextView tip;

    public ItemDeeplinkView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWeakView();
    }

    public ItemDeeplinkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWeakView();
    }

    public ItemDeeplinkView(Context context, BaseOnclickListener clickListener) {
        super(context);
        initWeakView();
        this.mClickListener = clickListener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.item_deeplink, this);
        init();
    }

    private void init() {
        mGold = (ImageView) findViewById(R.id.rank_index_iv);
        mfl_number_single_item = (FrameLayout) findViewById(R.id.fl_number);
        mRankNumberView = (TextView) findViewById(R.id.tv_number_single_item);
        mLogoIv = (ImageView) findViewById(R.id.logoIv);
        mNameTv = (TextView) findViewById(R.id.nameTv);
        mLabel = (TextView) findViewById(R.id.label);
        mTagTv = (TextView) findViewById(R.id.tagTv);
        mDownCount = (TextView) findViewById(R.id.down_count);
        mSizeTv = (TextView) findViewById(R.id.game_size);
        mSignVs = findViewById(R.id.signVs);
        mDescribeVs = (ViewStub) findViewById(R.id.describeVs);
        mDownloadProgressBar = (ProgressBar) findViewById(R.id.download_progress_bar);
        mDonwloandBt = (Button) findViewById(R.id.grid_item_button);
        mMarkIv = (ImageView) findViewById(R.id.iv_mark_index_game_item);
        mChangPadding = findViewById(R.id.change_padding);
        mBottomLine = findViewById(R.id.decorate);
        //deeplink搜索这里颜色不一样，另外不要分割线
        mChangPadding.setBackgroundColor(getResources().getColor(R.color.deeplink_bg));
        this.findViewById(R.id.body).setBackgroundColor(getResources().getColor(R.color.deeplink_bg));
        mBottomLine.setVisibility(View.GONE);
        tip = (TextView) this.findViewById(R.id.tip);
    }

    private void initWeakView() {
        new WeakView<ItemDeeplinkView>(this) {
            @Override
            public void onEventMainThread(DownloadUpdateEvent updateEvent) {
                if (updateEvent == null || updateEvent.game == null || mGame == null) {
                    return;
                }

                if (updateEvent.game.getPkgName().equals(mGame.getPkgName())) {
                    setButtonView();
                }
            }
        };
    }

    public void fillView() {
        try {
            UIModule module = (UIModule) mItemData.getmData();
            mGame = (GameDomainBaseDetail) module.getData();
            DeeplinkBean  deeplinkBean = mGame.getDeeplink_app();
            if (mGame != null) {
                gameDetailForDownload = new GameBaseDetail().setGameBaseInfo(mGame);
                installButtonGroup = new IndexUpdateButtonState(gameDetailForDownload, mDonwloandBt, mDownloadProgressBar);
                listener = new InstallButtonClickListener(mContext, gameDetailForDownload, installButtonGroup, mPageName);
                mDonwloandBt.setOnClickListener(listener);
                if (PresentType.super_push == module.getUIType()) {
                    mSignVs.setVisibility(VISIBLE);
                } else {
                    mSignVs.setVisibility(GONE);
                }

                List<String> flags = mGame.getFlags();
                if (flags != null && flags.size() > 0) {
                    OperationSign sign;
                    try {
                        sign = OperationSign.valueOf(flags.get(0));
                        mLabel.setVisibility(VISIBLE);
                        mLabel.setText(sign.getSign());
                        mLabel.setBackgroundResource(sign.getBackgroundRes());
                        mLabel.setTextColor(getResources().getColor(sign.getColorRes()));
                    } catch (IllegalArgumentException e) {
                        mLabel.setVisibility(GONE);
                    }

                } else {
                    mLabel.setVisibility(GONE);
                }


                if (TextUtils.isEmpty(deeplinkBean.getReviews())) {
                    mTagTv.setVisibility(View.GONE);
                } else {
                    mTagTv.setVisibility(View.VISIBLE);
                    mTagTv.setText(deeplinkBean.getReviews().trim());
                }

                mNameTv.setText(deeplinkBean.getName());
                tip.setText(deeplinkBean.getAd());
                tip.setTextColor(Color.parseColor(deeplinkBean.getColor()));
                String downloadCount = IntegratedDataUtil.calculateCountsV4(Integer.valueOf(deeplinkBean.getDownload_count()));
                String gameSize = IntegratedDataUtil.calculateSizeMB(deeplinkBean.getPackage_size());
                ImageloaderUtil.loadRoundImage(mContext, deeplinkBean.getIcon_url(), mLogoIv);


                mDownCount.setText(downloadCount);
                mSizeTv.setText(gameSize);

                @SuppressWarnings("unchecked") StatisticsEventData sData = produceStatisticsData(mItemData.getmPresentData(), mGame.getUniqueIdentifier(), mPageName, ReportEvent.ACTION_CLICK, null, null, null, mGame.getPkgName());
                setViewTagForClick(this, mGame, mGame.getDomainType(), mItemData.getmPresentType(), sData);

                dealWithRankGold();
                //给按钮设置统计数据
                mDonwloandBt.setTag(R.id.statistics_data, sData);
                setButtonView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setButtonView() {
        downFile = FileDownloaders.getDownFileInfoById(gameDetailForDownload.getId());
        if (downFile != null) {
            gameDetailForDownload.setDownInfo(downFile);
        } else {
            gameDetailForDownload.setState(DownloadState.undownload);
            gameDetailForDownload.setDownLength(0);
        }
        gameState = gameDetailForDownload.getState();
        gamePercent = gameDetailForDownload.getDownPercent();
        installButtonGroup.setViewBy(gameState, gamePercent);
    }

    private void dealWithRankGold() {
        if (Constant.PAGE_RANK_HOT.equals(mPageName) || Constant.PAGE_RANK_NEWS.equals(mPageName) || Constant.PAGE_RANK_OFFLINE.equals(mPageName) || Constant.PAGE_RANK_ONLINE.equals(mPageName)) {
            IvLogoLayout();
            switch (mPosition) {
                case 0:
                    mfl_number_single_item.setVisibility(GONE);
                    mGold.setVisibility(View.VISIBLE);
                    mGold.setBackgroundResource(R.mipmap.rank_no_1);
                    break;
                case 1:
                    mfl_number_single_item.setVisibility(GONE);
                    mGold.setVisibility(View.VISIBLE);
                    mGold.setBackgroundResource(R.mipmap.rank_no_2);
                    break;
                case 2:
                    mfl_number_single_item.setVisibility(GONE);
                    mGold.setVisibility(View.VISIBLE);
                    mGold.setBackgroundResource(R.mipmap.rank_no_3);
                    break;
                default:

                    mGold.setVisibility(View.GONE);
                    mfl_number_single_item.setVisibility(VISIBLE);
                    mRankNumberView.setText(String.valueOf(mPosition + 1));
            }
        }
    }

    private void IvLogoLayout() {
        //设置icorginleft
        RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(mLogoIv.getLayoutParams());
        layoutParam.setMargins((DensityUtil.dip2px(getContext(), 44)), DensityUtil.dip2px(getContext(), 14), 0, 0);
        mLogoIv.setLayoutParams(layoutParam);
    }

    private void initSubView() {
        if (mDescribeTv == null) {
            View view = mDescribeVs.inflate();
            mDescribeTv = (TextView) view.findViewById(R.id.describeTv);
        }
    }

    @Override
    public void fillLayout(ItemData<? extends BaseUIModule> data, int position, int listSize) {
        try {
            mPosition = position;
            mItemData = data;
            if (mItemData != null) {
                fillView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
