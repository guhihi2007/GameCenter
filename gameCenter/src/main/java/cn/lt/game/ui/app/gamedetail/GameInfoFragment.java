package cn.lt.game.ui.app.gamedetail;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.huanju.data.HjDataClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.db.operation.FavoriteDbOperator;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.domain.detail.GameDomainDetail;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.ScreenUtils;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.image.BitmapUtil;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.DownLoadBar;
import cn.lt.game.lib.view.ElasticScrollView;
import cn.lt.game.lib.view.GameDetailInfoView;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsDataProductorImpl;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.statistics.pageunits.PageMultiUnitsReportManager;
import cn.lt.game.ui.app.gamegift.GiftListActivity;
import cn.lt.game.ui.common.TagGroup;
import cn.lt.game.ui.common.activity.GameOtherInfoActivity;
import cn.lt.game.ui.common.listener.InstallButtonClickListener;
import cn.lt.game.ui.installbutton.IndexUpdateButtonState;
import de.greenrobot.event.EventBus;


/***
 * 游戏详情-详情页面
 */
public class GameInfoFragment extends BaseFragment implements OnClickListener, NetWorkStateView.RetryCallBack, DownLoadBar.PullToBottomCallBack {
    public GameBaseDetail game = new GameBaseDetail();// 存放游戏信息
    public boolean isOpen = false;// 是否已经展开全文
    private GameDomainDetail gameDomainDetail;
    private View mView = null;
    private ElasticScrollView elasticScrollView = null;
    private int[] screenSize;// 屏幕尺寸
    /* 已经加载的截图 */
    private ArrayList<Integer> finishPosition = new ArrayList<>();
    private TextView description_content, review, tvVersion, tvUpdateTime;
    private ImageView tv_openText;
    private LinearLayout snapshotLl, sameTypeGameStubView, gameTitleView;
    private List<String> screenShotUrls;// 存放所有的截图链接
    private Bitmap[] screenShotBitmaps;// 存放所有截图
    private int gameId = 1, imageViewWidth, imageViewHeight;// 实际展示时截图的宽度/高度
    private GameImageDialog dialog;// 截图弹出框
    private GameDetailInfoView appMsgView;
    private boolean alreadySetSnapshotSize = false;
    private LinearLayout otherInfoLayout, otherInfoValue;
    private GameOtherInfoStatusListener otherDataStatusListener;
    private DownLoadBar downLoadBar;
    private NetWorkStateView netWorkStateView;
    private RelativeLayout gameGiftView;

    private boolean pushIsClick = false;

    private SparseArray<IndexUpdateButtonState> stateSparseArray;
    private TagGroup mDefaultTagGroup;
    /**
     * 是否来自推送通知那跳转过来的
     */
    private boolean isPush;

    @Override
    public void setPageAlias() {
        if (isPush) {
            setmPageAlias(Constant.PAGE_GE_TUI);
        } else {
            setmPageAlias(Constant.PAGE_GAME_DETAIL);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenSize = Utils.getScreenSize(mActivity);
        isPush = mActivity.getIntent().getBooleanExtra("isPush", false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mView == null) {
            mView = inflater.inflate(R.layout.activity_game_msg, container, false);
            findViewById();
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        }
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initListener() {
        /* 触发事件 */
        tv_openText.setOnClickListener(this);
        description_content.setOnClickListener(this);
        otherDataStatusListener = new GameOtherInfoStatusListener() {
            @Override
            public void onSuccess() {
                initGameOtherInfo();
            }

            @Override
            public void onFailed() {
                otherInfoLayout.setVisibility(View.GONE);
            }
        };

    }

    /* 初始化界面 */
    private void findViewById() {
        netWorkStateView = (NetWorkStateView) mView.findViewById(R.id.game_detail_netWrokStateView);
        netWorkStateView.setRetryCallBack(this);
        downLoadBar = (DownLoadBar) mView.findViewById(R.id.game_detail_downlaodBar);
        review = (TextView) mView.findViewById(R.id.gamedetail_review);
        appMsgView = (GameDetailInfoView) mView.findViewById(R.id.infoView);
        elasticScrollView = (ElasticScrollView) mView.findViewById(R.id.elasticSV);
        tvUpdateTime = (TextView) mView.findViewById(R.id.game_detail_updateTime);
        tvVersion = (TextView) mView.findViewById(R.id.game_detail_version);
        description_content = (TextView) mView.findViewById(R.id.description_content);
        snapshotLl = (LinearLayout) mView.findViewById(R.id.detail_snapshotLl);
        tv_openText = (ImageView) mView.findViewById(R.id.tv_openText);
        sameTypeGameStubView = (LinearLayout) mView.findViewById(R.id.same_type_gamesLl_stubview);
        gameTitleView = (LinearLayout) mView.findViewById(R.id.gamedetail_gametitleView);
        otherInfoLayout = (LinearLayout) mView.findViewById(R.id.game_otherInfo_layout);
        otherInfoValue = (LinearLayout) mView.findViewById(R.id.gamedetail_relevant_value);
        gameGiftView = (RelativeLayout) mView.findViewById(R.id.game_gifts_stubview);
        mDefaultTagGroup = (TagGroup) mView.findViewById(R.id.tag_group);
        initListener();

    }


    /* 载入图片 */
    private void loadImage() {
        // 加载游戏截图
        int size = screenShotUrls.size();
        screenShotBitmaps = new Bitmap[size];
        snapshotLl.removeAllViews();// 先移除再添加，防止重复添
        for (int i = 0; i < size; i++) {
            final int postion = i;
            final ImageView imageView = new ImageView(mActivity);
            imageView.setBackgroundResource(R.mipmap.screen_def);
            imageView.setTag(i);
            final int finalI = i;
            ImageloaderUtil.loadImageCallBack(getActivity(), screenShotUrls.get(i), new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
                    Bitmap loadedImage = BitmapUtil.drawable2Bitmap(resource.getCurrent());
                    if (loadedImage != null) {
                        boolean firstDisplay = !displayedImages.contains(screenShotUrls.get(finalI));
                        if (firstDisplay) {
                            displayedImages.add(screenShotUrls.get(finalI)); // 将图片uri添加到集合中
                        }

                        // i=图片加载完成的编号
                        int i = Integer.parseInt(imageView.getTag().toString());
                        screenShotBitmaps[i] = loadedImage;
                        finishPosition.add(i);// 设置第i张图片为已经加载完成

				        /* 初始化截图imageview的大小 */
                        if (!alreadySetSnapshotSize) {
                            if (resource.getIntrinsicWidth() > resource.getIntrinsicHeight()) {
                                // 横图
                                imageViewHeight = (int) (screenSize[0] * 0.5);
                                imageViewWidth = (int) ((((double) imageViewHeight) / loadedImage.getHeight()) * loadedImage.getWidth());
                            } else {
                                // 竖图
                                imageViewHeight = (int) (screenSize[0] * 0.75);
                                imageViewWidth = (int) ((((double) imageViewHeight) / loadedImage.getHeight()) * loadedImage.getWidth());
                            }
                        }

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(imageViewWidth, imageViewHeight);
                        if (Integer.parseInt(imageView.getTag().toString()) != 0) {
                            int paddingLeft = (int) mActivity.getResources().getDimension(R.dimen.gameScreenshotFramePaddingLeft);
                            // lp.setMargins((int) (screenSize[0] * 0.039), 0, 0, 0);
                            lp.setMargins(paddingLeft, 0, 0, 0);
                        }
                        imageView.setLayoutParams(lp);
                        imageView.setScaleType(ScaleType.FIT_XY);
                        imageView.setImageDrawable(resource);

                        alreadySetSnapshotSize = true;

				/* 如果图片加载进来，就刷新查看大截图的dialog */
                        if (dialog != null) {
                            dialog.notifyDataSetChanged();
                        }
                    }
                }
            });

            // 点击事件
            imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog = new GameImageDialog(mActivity, screenShotBitmaps, screenSize[0], screenSize[1]);
                    ArrayList<String> tempUrls = new ArrayList<>();
                    int size = screenShotUrls.size();
                    for (int j = 0; j < size; j++) {
                        tempUrls.add(screenShotUrls.get(j));
                    }
                    dialog.setUrls(tempUrls);// 传入所有的截图链接
                    dialog.setFinishposition(finishPosition);// 传入已经加载的截图位置
                    dialog.show();
                    dialog.setCurrentPosition(postion);
                }
            });
//            snapshotImageViews.add(imageView);// 放入arraylist
            snapshotLl.addView(imageView);// 放入界面的linearlayout中
        }
    }

    /* 初始化游戏礼包界面 */
    private void initGameGift(List<GiftDomainDetail> giftList) {
        if (null != giftList) {
            int size = giftList.size();
            if (size > 0) {
                gameGiftView.setVisibility(View.VISIBLE);
                LinearLayout rootView = (LinearLayout) mView.findViewById(R.id.ll_game_gift_item);
                rootView.removeAllViews();//先移除上一次数据，防止缓存
                RelativeLayout rl_gift_title_bar = (RelativeLayout) mView.findViewById(R.id.rl_gift_title_bar);
                addGiftItem(rootView, giftList);
                rl_gift_title_bar.setOnClickListener(this);
            }
        } else {
            gameGiftView.setVisibility(View.GONE);
        }
    }

    /* 添加礼包item */
    private void addGiftItem(LinearLayout rootView, List<GiftDomainDetail> giftList) {
        for (int i = 0; i < giftList.size(); i++) {
            GameDetailGiftItem item = new GameDetailGiftItem(mActivity, getPageAlias());
            item.setData(giftList.get(i));
            if (i == giftList.size() - 1) {
                item.setDividerGone();
            }
            rootView.addView(item);
        }
    }


    /* 初始化游戏推荐页面 */
    private void initRecommendGames(List<GameDomainBaseDetail> recommendGameList) {
        if (null != recommendGameList) {
            int size = recommendGameList.size();
            if (size > 0) {
                LinearLayout sameTypeLl = (LinearLayout) mView.findViewById(R.id.detail_sameTypeLl);
                sameTypeLl.removeAllViews();//先移除搜索再添加
                addImageTextView(sameTypeLl, recommendGameList, size);
            }
        } else {
            sameTypeGameStubView.setVisibility(View.GONE);
        }
    }

    /* 往HorizontalScrollView里添加视图 */
    private void addImageTextView(LinearLayout root, List<GameDomainBaseDetail> recommendGameList, int size) {

        if (stateSparseArray == null) {
            stateSparseArray = new SparseArray<>();
        } else {
            stateSparseArray.clear();
        }

        for (int i = 0; i < size; i++) {
            final GameDomainBaseDetail recommendGame = recommendGameList.get(i);
            View view = View.inflate(mActivity, R.layout.item_detail_recommend, null);

            ImageView mLogoIv = (ImageView) view.findViewById(R.id.iv_square_game_view);
            ImageloaderUtil.loadLTLogo(getActivity(), recommendGame.getIconUrl(), mLogoIv);

            TextView mNameTv = (TextView) view.findViewById(R.id.nameTv);
            mNameTv.setText(recommendGame.getName());

            ProgressBar mDownloadProgressBar = (ProgressBar) view.findViewById(R.id.download_progress_bar);
            Button mDownloadBt = (Button) view.findViewById(R.id.grid_item_button);

            // ----------------------------------------
            GameBaseDetail detail = new GameBaseDetail();
            detail.setGameBaseInfo(recommendGame);
            detail.pageId = gameId + "";

            IndexUpdateButtonState installButtonGroup = new IndexUpdateButtonState(detail, mDownloadBt, mDownloadProgressBar);
            InstallButtonClickListener listener = new InstallButtonClickListener(mActivity, detail, installButtonGroup, Constant.PAGE_GAME_DETAIL_RECOMMEND);
            mDownloadBt.setOnClickListener(listener); //按钮点击监听

            StatisticsEventData eventData = StatisticsDataProductorImpl.produceStatisticsData(
                    "", i + 1, 0, String.valueOf(detail.getId()), Constant.PAGE_GAME_DETAIL_RECOMMEND, ReportEvent.ACTION_CLICK, "", "", "", "", detail.getPkgName()
            );
            mDownloadBt.setTag(R.id.statistics_data, eventData);

            GameBaseDetail downFile = FileDownloaders.getDownFileInfoById(detail.getId());
            if (downFile != null) {
                detail.setDownInfo(downFile);
            } else {
                detail.setState(DownloadState.undownload);
                detail.setDownLength(0);
            }
            int gameState = detail.getState();
            int gamePercent = detail.getDownPercent();
            installButtonGroup.setViewBy(gameState, gamePercent);

            stateSparseArray.put(detail.getId(), installButtonGroup);
            // ----------------------------------------

            final int finalI = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(recommendGame.getUniqueIdentifier()) && !"null".equals(recommendGame.getUniqueIdentifier())) {
                        reportClickRecomData(finalI, recommendGame);
                        ActivityActionUtils.JumpToGameDetail(mActivity, Integer.parseInt(recommendGame.getUniqueIdentifier()));
                        getActivity().finish();
                    }
                }
            });

            root.addView(view);

        }
        root.requestLayout();
    }

    public void onEventMainThread(DownloadUpdateEvent updateEvent) {
        if (updateEvent == null || updateEvent.game == null || stateSparseArray == null) {
            return;
        }

        IndexUpdateButtonState installButtonGroup = stateSparseArray.get(updateEvent.game.getId());
        if (installButtonGroup != null) {
            installButtonGroup.setViewBy(updateEvent.game.getState(), updateEvent.game.getDownPercent());
        }

    }


    private void reportClickRecomData(int finalI, GameDomainBaseDetail recommendGame) {
        try {
            StatisticsEventData statisticsEventData = StatisticsDataProductorImpl.produceStatisticsData(null, finalI + 1, 0, recommendGame.getUniqueIdentifier(), getPageAlias(), ReportEvent.ACTION_CLICK, Constant.RETRY_TYPE_MANUAL, null, Constant.SRCTYPE_DETAILRECOM, null, recommendGame.getPkgName());
            DCStat.clickEvent(statisticsEventData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reportClickLableData(int finalI, FunctionEssence lableBean) {
        try {
            StatisticsEventData statisticsEventData = StatisticsDataProductorImpl.
                    produceStatisticsData(Constant.SRCTYPE_DETAILLABLE, finalI + 1, 1, null, getPageAlias(), ReportEvent.ACTION_CLICK, Constant.RETRY_TYPE_MANUAL, lableBean.getTitle(), null, null);
            DCStat.clickEvent(statisticsEventData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 本周热门标签
    private void initHotGameTagView(final List<FunctionEssence> data) {
        if (null != data) {
            int size = data.size();
            if (size > 0) {
                /*LinearLayout root = (LinearLayout) mView.findViewById(R.id.gamedetail_rootlayout);
                root.removeAllViews();// 防止个别手机重复添加
                int spaceWidth = (int) mActivity.getResources().getDimension(R.dimen.indicator_right_padding);
                int spaceHeight = (int) mActivity.getResources().getDimension(R.dimen.mygift_listView_margin);

                FlowLayout flowLayout = new FlowLayout(mActivity);
                flowLayout.setData(data, spaceWidth, spaceHeight);
                root.addView(flowLayout);
                flowLayout.setOnItemClickListener(new FlowLayout.OnClickListener() {
                    @Override
                    public void onClick(FunctionEssence hotSearchBean, int j) {
                        // 跳转到标签列表页
                        reportClickLableData(j, hotSearchBean);
                        ActivityActionUtils.jumpToSearhTagActiviy(mActivity, hotSearchBean.getUniqueIdentifier(), hotSearchBean.getTitle());
                    }
                });*/
                String[] tags = new String[size];
                for (int i = 0; i < data.size(); i++) {
                    tags[i] = data.get(i).getTitle();
                }
                mDefaultTagGroup.setTags(tags);
                mDefaultTagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {

                    @Override
                    public void onTagClick(String tag) {
                        if (TextUtils.isEmpty(tag)) {
                            return;
                        }
                        int tempPosition = -1;
                        FunctionEssence hotSearchBean = null;
                        for (int i = 0; i < data.size(); i++) {
                            if (tag.equals(data.get(i).getTitle())) {
                                tempPosition = i;
                                hotSearchBean = data.get(i);
                                break;
                            }
                        }
                        if (hotSearchBean == null) {
                            return;
                        }
                        reportClickLableData(tempPosition, hotSearchBean);
                        ActivityActionUtils.jumpToSearhTagActiviy(mActivity, hotSearchBean.getUniqueIdentifier(), hotSearchBean.getTitle());
                    }
                });
            }
        } else {
            gameTitleView.setVisibility(View.GONE);
        }
    }

    // 初始化攻略 新闻 评测 界面；
    protected void initGameOtherInfo() {
        otherInfoValue.removeAllViews();// 先清除掉再添加，防止在个别手机执行Onresume方法多次重复添加
        if (otherDataStatusListener == null) {
            return;
        }
        if (otherDataStatusListener.getHasNewsData() || otherDataStatusListener.getHasInformationData() || otherDataStatusListener.getHasStrategyData()) {
            otherInfoLayout.setVisibility(View.VISIBLE);
        } else {
            return;
        }
        Drawable icon;
        RelativeLayout text;
        int margin = (int) mActivity.getResources().getDimension(R.dimen.inIntervalLeft);
        if (otherDataStatusListener.getHasStrategyData()) {
            icon = mActivity.getResources().getDrawable(R.mipmap.img_strategy_icon);
            text = getDrawableCenterTextView("攻略", margin, icon, R.drawable.btn_strategy_selector);
            text.setId(1);
            text.setOnClickListener(this);
            otherInfoValue.addView(text);
        }
        if (otherDataStatusListener.getHasInformationData()) {
            icon = mActivity.getResources().getDrawable(R.mipmap.img_information_icon);
            text = getDrawableCenterTextView("评测", margin, icon, R.drawable.btn_information_selector);
            text.setId(2);
            text.setOnClickListener(this);
            otherInfoValue.addView(text);
        }
        if (otherDataStatusListener.getHasNewsData()) {
            icon = mActivity.getResources().getDrawable(R.mipmap.new_icon);
            text = getDrawableCenterTextView("新闻", margin, icon, R.drawable.btn_new_selector);
            text.setId(3);
            text.setOnClickListener(this);
            otherInfoValue.addView(text);
        }

    }

    private RelativeLayout getDrawableCenterTextView(String title, int margins, Drawable icon, int backgroupResid) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) ScreenUtils.dpToPx(getActivity(), 48));
        params.weight = 1.0f;
        float textSize = mActivity.getResources().getDimension(R.dimen.middle_font_size);
        int drawPadding = (int) mActivity.getResources().getDimension(R.dimen.margin_size_6dp);
        params.setMargins(margins, 0, 0, 0);
        RelativeLayout relativeLayout = new RelativeLayout(mActivity);
        relativeLayout.setLayoutParams(params);
        relativeLayout.setBackgroundResource(backgroupResid);
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (int) ScreenUtils.dpToPx(getActivity(), 48));
        textParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        TextView textView = new TextView(mActivity);
        textView.setTextColor(mActivity.getResources().getColor(R.color.white));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setCompoundDrawablePadding(drawPadding);
        textView.setText(title);
        textView.setGravity(Gravity.CENTER);
        textView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        textView.setLayoutParams(textParams);
        relativeLayout.addView(textView);
        return relativeLayout;
    }

    @Override
    public void pullToBottom() {
        elasticScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_openText:
            case R.id.description_content:
                if (isOpen) {
                    isOpen = false;
                    description_content.setMaxLines(3);
                    tv_openText.setImageResource(R.mipmap.drop_down);
                    description_content.setPadding(0, DensityUtil.dip2px(getActivity(), 12), 0, DensityUtil.dip2px(getActivity(), 0));
                } else {
                    // 弹出
                    isOpen = true;
                    description_content.setMaxLines(120);
                    description_content.setPadding(0, DensityUtil.dip2px(getActivity(), 12), 0, DensityUtil.dip2px(getActivity(), 6));
                    tv_openText.setImageResource(R.mipmap.pack_up);
                }

                break;
            // 1 攻略
            case 1:
                jumptoGameOtherInfoActivity(FavoriteDbOperator.GameStrategyTag);
                break;
            case 2:
                jumptoGameOtherInfoActivity(FavoriteDbOperator.GameinfomationTag);
                break;
            case 3:
                jumptoGameOtherInfoActivity(FavoriteDbOperator.GameNewsTag);
                break;
            // 跳转到礼包列表页面
            case R.id.rl_gift_title_bar:
                Intent intent = new Intent(mActivity, GiftListActivity.class);
                intent.putExtra(GiftListActivity.GAME_ID, String.valueOf(gameId));
                mActivity.startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void jumptoGameOtherInfoActivity(int whereFrom) {
        Intent intent = new Intent(mActivity, GameOtherInfoActivity.class);
        intent.putExtra("whereFrom", whereFrom);
        intent.putExtra("gameDetail", game);
        startActivity(intent);
    }

    private void initGameBaseView(GameBaseDetail game) {
        if (downLoadBar != null) {
            downLoadBar.initDownLoadBar(game, getPageAlias());
            downLoadBar.setPullToBottomCallBack(this);
        }
        tvUpdateTime.setText("更新时间 ：" + TimeUtils.getDateToString(game.getUpdated_at()));
        tvVersion.setText("版本 ：" + game.getVersion());
        if (!TextUtils.isEmpty(game.getReview())) {
            review.setText("小编点评 ：" + game.getReview());
        } else {
            review.setVisibility(View.GONE);
        }
        appMsgView.setGame(game);
        String s = game.getDescription();
        try {
            s = s.replace("<p>&nbsp;</p>", "").replace("<p>", "").replace("</p>", "<br>");
        } catch (Exception e) {
            e.printStackTrace();
        }


        final String finalS = s;
        description_content.setText(Html.fromHtml(finalS));
        description_content.postDelayed(new Runnable() {
            @Override
            public void run() {
                 /* 假如内容本身就不超过3行，隐藏显示全文功能 */
                if (mActivity != null && !mActivity.isDestroyed()) {
                    if (description_content.getLineCount() <= 3) {
                        tv_openText.setVisibility(View.GONE);
                        description_content.setPadding(0, DensityUtil.dip2px(mActivity, 12), 0, DensityUtil.dip2px(mActivity, 8));
                    } else {
                        tv_openText.setVisibility(View.VISIBLE);
                        description_content.setMaxLines(3);
                        description_content.setPadding(0, DensityUtil.dip2px(mActivity, 12), 0, DensityUtil.dip2px(mActivity, 0));
                    }
                }
            }
        }, 500);
        screenShotUrls = game.getScreenshotUrls();// 取出游戏截图链接数组
        loadImage();// 载入所需要的图片
    }

    /**
     * 请求网络
     */
    public void getData() {
        LogUtils.d("GameInfoFragment", "getData()");
        game = ((GameDetailHomeActivity) (getActivity())).getAdapterGameDetail();
        gameDomainDetail = ((GameDetailHomeActivity) getActivity()).getGameDomainDetail();
        initGameBaseView(game);// 初始化游戏基本信息界面
        initGameGift(game.getGiftList());// 游戏礼包
        initRecommendGames(game.getRecommendList());//推荐游戏
        initHotGameTagView(gameDomainDetail.getHotTags());//热门标签
        HjDataClient.getInstance(mActivity).requestResourceStatus(otherDataStatusListener, game.getPkgName());//游戏攻略、新闻、评测
        goDownloadGameByPush();
        netWorkStateView.hideNetworkView();
        LogUtils.i("xiangqing", "游戏详情页可见吗=请求网络=" + getUserVisibleHint());
        PageMultiUnitsReportManager.gameInfoDataBuildAndReport(game.getRecommendList(), gameDomainDetail.getHotTags(), Constant.PAGE_GAME_DETAIL, game);
    }

    /**
     * 如果是点击推送进来的，WIFI状态自动启动下载
     */
    private void goDownloadGameByPush() {
        boolean isFromWakeUp = mActivity.getIntent().getBooleanExtra("isFromWakeUp", false);
//        String pushId = mActivity.getIntent().getStringExtra("pushId");
        String gameId = "" + ((GameDetailHomeActivity) mActivity).mID;
        String pushId = ((GameDetailHomeActivity) mActivity).pushId;
        LogUtils.i("GameInfoFragment", "推送游戏打开时 推送id==>" + pushId);
        LogUtils.i("GameInfoFragment", "推送游戏打开时 gameId>" + gameId);
        LogUtils.i("GameInfoFragment", "url==> " + game.getDownUrl());
        if (isPush) {
            if (canUpgrade()) {
                if (NetUtils.isWifi(mActivity)) {
                    game.setPrevState(InstallState.upgrade);
                    FileDownloaders.update(game);
                    FileDownloaders.download(getActivity(), game, Constant.MODE_SINGLE, Constant.DOWNLOAD_TYPE_NORMAL, Constant.PAGE_GE_TUI, null, false, false, 0);
                }
                DCStat.pushEvent(pushId, gameId, "Game", "clicked", isFromWakeUp ? "WAKE_UP" : Constant.PAGE_GE_TUI, "", "");   //推送过来直接下载
                LogUtils.i("GameInfoFragment", "可以升级");
            } else {
                LogUtils.i("GameInfoFragment", "不可以升级");
                DCStat.pushEvent(pushId, gameId, "Game", "clicked", isFromWakeUp ? "WAKE_UP" : Constant.PAGE_GE_TUI, "", (AppUtils.isInstalled(game.getPkgName())) ? "open" : "");   //推送过来直接打开
                if (!pushIsClick) {// 判断是否已经点击过(强制只能自动点击一次)
                    LogUtils.i("GameInfoFragment", "执行DownloadBar的点击了");
                    downLoadBar.goPerformClick();
                    pushIsClick = true;
                }
            }
        }
    }

    private boolean canUpgrade() {
        PackageInfo packageInfo = AppUtils.getPackageInfoByPkgName(game.getPkgName());
        return packageInfo != null && packageInfo.versionCode < game.getVersionCode();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (otherDataStatusListener != null) {
            otherDataStatusListener.release();
            otherDataStatusListener = null;
        }
        if (downLoadBar != null) {
            downLoadBar.release();
            downLoadBar = null;
        }

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        // 更新安装按键状态
        try {
            gameId = Integer.parseInt(mActivity.getIntent().getStringExtra("id"));
            mEventID = gameId == -1 ? "" : gameId + "";
//            FromPageManager.setLastPageId(""+gameId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        mEventID = gameId == -1 ? "" : gameId + "";
        try {
            LogUtils.i("xiangqing", "游戏详情页可见吗=是来自通知吗？=" + MyApplication.application.mIsFromNotificationForGameInfo);
            if (isVisibleToUser && !MyApplication.application.mIsFromNotificationForGameInfo) {
                LogUtils.i("xiangqing", "游戏详情页可见吗=setUserVisibleHint=" + getUserVisibleHint());
                PageMultiUnitsReportManager.gameInfoDataBuildAndReport(game.getRecommendList(), gameDomainDetail.getHotTags(), Constant.PAGE_GAME_DETAIL, game);
            }
            MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MyApplication.application.mIsFromNotificationForGameInfo = false;
                }
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void checkNetwork() {
        try {
            getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void retry() {
        checkNetwork();
    }

    public void recycleBitmaps() {
        try {
            if (dialog != null) {
                dialog.recycleBitmaps();
            } else {
                for (Bitmap bitmap : screenShotBitmaps) {
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
