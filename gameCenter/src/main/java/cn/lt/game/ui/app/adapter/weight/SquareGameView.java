package cn.lt.game.ui.app.adapter.weight;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.common.WeakView;
import cn.lt.game.ui.common.listener.InstallButtonClickListener;
import cn.lt.game.ui.installbutton.IndexUpdateButtonState;

public class SquareGameView extends ItemView {

    /**
     * 游戏logo小图
     */
    private ImageView mLogoIv;

    private ImageView mMarkIv;

    private TextView mNameTv;// 游戏名

    private TextView mTagSizeTv;// 游戏标签和大小

    private ProgressBar mDownloadProgressBar;// 下载进度条

    private Button mDownloadBt;// 下载按钮
    private GameDomainBaseDetail mGame;
    private GameBaseDetail downFile;
    private int gameState;
    private int gamePercent;

    public SquareGameView(Context context, BaseOnclickListener clickListener) {
        super(context);
        this.mContext = context;
        this.mClickListener = clickListener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        LayoutInflater.from(context).inflate(R.layout.index_smallimage_down, this);
        init();
        initWeakView();
    }

    public SquareGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWeakView();
    }

    public SquareGameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWeakView();
    }

    private void init() {
        mLogoIv = (ImageView) findViewById(R.id.iv_square_game_view);
        mNameTv = (TextView) findViewById(R.id.nameTv);
        mTagSizeTv = (TextView) findViewById(R.id.tagSizeTv);
        mDownloadProgressBar = (ProgressBar) findViewById(R.id.download_progress_bar);
        mDownloadBt = (Button) findViewById(R.id.grid_item_button);
        mMarkIv = (ImageView) findViewById(R.id.iv_mark_index_game_item);
    }

    private void initWeakView() {
        new WeakView<SquareGameView>(this) {
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
        UIModule module = (UIModule) mItemData.getmData();
        mGame = (GameDomainBaseDetail) module.getData();
        if (mGame != null) {
            //获取统计数据
            StatisticsEventData sData = produceStatisticsData(mItemData.getmPresentData(), mGame.getUniqueIdentifier(), mPageName, ReportEvent.ACTION_CLICK, null, null, null, mGame.getPkgName());
            mDownloadBt.setTag(R.id.statistics_data, sData);

            gameDetailForDownload = new GameBaseDetail().setGameBaseInfo(mGame);
            installButtonGroup = new IndexUpdateButtonState(gameDetailForDownload, mDownloadBt, mDownloadProgressBar);
            listener = new InstallButtonClickListener(mContext, gameDetailForDownload, installButtonGroup, mPageName);
            mDownloadBt.setOnClickListener(listener); //按钮点击监听
            mNameTv.setText(mGame.getName());
            String categoryString = mGame.getCatName();
            if (!TextUtils.isEmpty(categoryString) && categoryString.length() >= 2) {
                mTagSizeTv.setText(IntegratedDataUtil.calculateSizeMB(mGame.getPkgSize()));
            } else {
                mTagSizeTv.setText(IntegratedDataUtil.calculateSizeMB(mGame.getPkgSize()));
            }
            //添加点击时需要的各类数据； 1、实体对象；2、统计对象；3、PresentType,
            setViewTagForClick(this, mGame, mGame.getDomainType(), mItemData.getmPresentType(), sData);
            ImageloaderUtil.loadRoundImage(getContext(), mGame.getIconUrl(), mLogoIv);
            if (!TextUtils.isEmpty(mGame.getSymbol_url())) {
                mMarkIv.setVisibility(VISIBLE);
                ImageloaderUtil.loadImage(getContext(), mGame.getSymbol_url(), mMarkIv, false);
            } else {
                mMarkIv.setVisibility(GONE);
            }

            setButtonView();
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
