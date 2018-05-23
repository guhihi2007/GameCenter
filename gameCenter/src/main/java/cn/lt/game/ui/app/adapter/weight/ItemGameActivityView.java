package cn.lt.game.ui.app.adapter.weight;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.lib.util.IntegratedDataUtil;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.gamedetail.OperationSign;
import cn.lt.game.ui.common.listener.ActivityButtonClickListener;
import cn.lt.game.ui.installbutton.ActivityUpdateButtonState;
import de.greenrobot.event.EventBus;

public class ItemGameActivityView extends ItemView {

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
    private TextView tvPoint;

    public ItemGameActivityView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWeakView();
    }

    public ItemGameActivityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWeakView();
    }

    public ItemGameActivityView(Context context, BaseOnclickListener clickListener) {
        super(context);
        initWeakView();
        this.mClickListener = clickListener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.index_item_activity_game, this);
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
        tvPoint = (TextView) findViewById(R.id.tv_point);
    }

    /**
     * 先不用弱引用对象，防止低内存手机下载按钮被回收出现的不可见现象 modify by ATian at 2016/11/28
     */
    private void initWeakView() {
        EventBus.getDefault().register(this);
    }

    public void onEventMainThread(DownloadUpdateEvent updateEvent) {
        if (updateEvent == null || updateEvent.game == null) return;
        UIModule module = (UIModule) mItemData.getmData();
        GameDomainBaseDetail game = (GameDomainBaseDetail) module.getData();
        if (updateEvent.game.getGameDomainBase().getUniqueIdentifier().equals(game.getUniqueIdentifier())) {
            setButtonView(game);
        }
    }

    public void fillView() {
        try {
            UIModule module = (UIModule) mItemData.getmData();
            GameDomainBaseDetail game = (GameDomainBaseDetail) module.getData();
            if (game != null) {
                if (PresentType.super_push == module.getUIType()) {
                    mSignVs.setVisibility(VISIBLE);
                } else {
                    mSignVs.setVisibility(GONE);
                }
                mNameTv.setText(game.getName());
                List<String> flags = game.getFlags();
                if (flags != null && flags.size() > 0) {
                    OperationSign sign;
                    try {
                        sign = OperationSign.valueOf(flags.get(0));
                        mNameTv.setMaxEms(5);
                        mLabel.setVisibility(VISIBLE);
                        mLabel.setText(sign.getSign());
                        mLabel.setBackgroundResource(sign.getBackgroundRes());
                        mLabel.setTextColor(getResources().getColor(sign.getColorRes()));
                    } catch (IllegalArgumentException e) {
                        mNameTv.setMaxEms(7);
                        mLabel.setVisibility(GONE);
                    }
                } else {
                    mNameTv.setMaxEms(7);
                    mLabel.setVisibility(GONE);
                }

                if (TextUtils.isEmpty(game.getReviews())) {
                    mTagTv.setVisibility(View.GONE);
                } else {
//                    mTagTv.setText(game.getCatName() + game.getReviews());
                    mTagTv.setVisibility(View.VISIBLE);
                    mTagTv.setText(game.getReviews().trim());
                }
                String downloadCount = IntegratedDataUtil.calculateCountsV4(Integer.valueOf(game.getDownCnt()));
                String gameSize = IntegratedDataUtil.calculateSizeMB(game.getPkgSize());
                mDownCount.setText(downloadCount);
                tvPoint.setText("+" + String.valueOf(game.getDownload_point()));
                mSizeTv.setText(gameSize);
                @SuppressWarnings("unchecked")
                StatisticsEventData sData = produceStatisticsData(mItemData.getmPresentData(), game.getUniqueIdentifier(), mPageName, ReportEvent.ACTION_CLICK, null, null, null, game.getPkgName());
                setViewTagForClick(this, game, game.getDomainType(), mItemData.getmPresentType(), sData);
                ImageloaderUtil.loadRoundImage(mContext, game.getIconUrl(), mLogoIv);
//                dealWithRankGold();
                //给按钮设置统计数据
                mDonwloandBt.setTag(R.id.statistics_data, sData);

                setButtonView(game);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setButtonView(GameDomainBaseDetail game) {
        GameBaseDetail gameDetailForDownload = new GameBaseDetail().setGameBaseInfo(game);
        GameBaseDetail downFile = FileDownloaders.getDownFileInfoById(gameDetailForDownload.getId());
        if (downFile != null) {
            gameDetailForDownload.setDownInfo(downFile);
        } else {
            gameDetailForDownload.setState(DownloadState.undownload);
            gameDetailForDownload.setDownLength(0);
        }
        ActivityUpdateButtonState installButtonGroup = new ActivityUpdateButtonState(gameDetailForDownload, mDonwloandBt, mDownloadProgressBar);
        ActivityButtonClickListener listener = new ActivityButtonClickListener(mContext, gameDetailForDownload, installButtonGroup, mPageName);
        mDonwloandBt.setOnClickListener(listener);

        int state = gameDetailForDownload.getState();
        int percent = gameDetailForDownload.getDownPercent();
        installButtonGroup.setViewBy(state, percent);
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
