package cn.lt.game.ui.app.adapter.weight;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lzy.imagepicker.loader.ImageLoader;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.lib.util.IntegratedDataUtil;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.gamegift.GiftManger;
import cn.lt.game.ui.app.gamegift.GiftManger.GetGiftResponseListener;
import cn.lt.game.ui.app.gamegift.beans.GiftBaseData;
import de.greenrobot.event.EventBus;


public class ItemGiftLastestView extends ItemView {

    private GiftDomainDetail mGift;
    private TextView mName;
    private TextView mRest;
    private ImageView mIcon;
    private Button mGetGiftBT;
    private ProgressBar mProgress;
    private LinearLayout mTitleRootView;
    private TextView mTitleNameTv;
    private View mMoreView;
    @SuppressWarnings("unused")
    private int mResetGiftCount;
    private GiftState mGiftState;
    private TextView tv_gifContent;
    private View v_buttomLine;
    public View mChangPadding;

    public ItemGiftLastestView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemGiftLastestView(Context context, BaseOnclickListener clickListener) {
        super(context);
        this.mContext = context;
        this.mClickListener = clickListener;
        if (clickListener != null) {
            this.mPageName = clickListener.getmPageName();
        }
        EventBus.getDefault().register(this);
        LayoutInflater.from(mContext).inflate(R.layout.layout_lastest_gift_item, this);
        initView();
    }

    private void initView() {
        mName = (TextView) findViewById(R.id.tv_gift_name);
        mRest = (TextView) findViewById(R.id.tv_reset_git_count);
        mIcon = (ImageView) findViewById(R.id.iv_icon);
        mProgress = (ProgressBar) findViewById(R.id.iv_cursor);
        mGetGiftBT = (Button) findViewById(R.id.bt_get_gift);
        mMoreView = findViewById(R.id.iv_title);
        mTitleNameTv = (TextView) findViewById(R.id.tv_title);
        mTitleRootView = (LinearLayout) findViewById(R.id.llt_title_root);
        tv_gifContent = (TextView) findViewById(R.id.tv_gifContent);
        v_buttomLine = findViewById(R.id.v_buttomLine);
        mChangPadding = findViewById(R.id.change_padding);
    }

    @Override
    public void fillLayout(ItemData<? extends BaseUIModule> data, int position, int listSize) {
        try {
            mItemData = data;
            if (mItemData != null) {
                mGift = (GiftDomainDetail) ((UIModule) mItemData.getmData()).getData();
                fillView();
            }

            // 设置间隔线
            if (position == listSize - 1) {
                v_buttomLine.setBackground(getResources().getDrawable(R.color.white));
                ((LayoutParams) v_buttomLine.getLayoutParams()).bottomMargin = getResources().getDimensionPixelOffset(R.dimen.margin_size_0dp);

            } else {
                v_buttomLine.setBackground(getResources().getDrawable(R.color.tab_line_unpressed_color_copy));
                ((LayoutParams) v_buttomLine.getLayoutParams()).bottomMargin = getResources().getDimensionPixelOffset(R.dimen.margin_size_14dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onEventMainThread(GiftBaseData info) {
        if (info != null && (info.getId() + "").equals(mGift.getUniqueIdentifier())) {
            notifyGetGiftSuccess();
        }
    }

    private void adjustProfile(ItemLocal local, String title) {
        mMoreView.setVisibility(View.GONE);
        switch (local) {
            case topAndBottom:
                mTitleRootView.setVisibility(View.VISIBLE);
                mTitleNameTv.setText(title);
                break;
            case bottom:
            case middle:
                mTitleRootView.setVisibility(View.GONE);
                break;
        }
    }

    private void fillView() {
        try {
            if (mGift != null) {
                PresentType type = mItemData.getmPresentType();
                String title = "";
                if (PresentType.new_gifts == type) {
                    title = "最新礼包";
                } else {
                }
                if (mItemData.isFirst() && mItemData.isLast()) {
                    adjustProfile(ItemLocal.topAndBottom, title);
                } else if (mItemData.isFirst()) {
                    adjustProfile(ItemLocal.top, title);
                } else if (mItemData.isLast()) {
                    adjustProfile(ItemLocal.bottom, title);
                } else {
                    adjustProfile(ItemLocal.middle, title);
                }
                mResetGiftCount = mGift.getTotal() - mGift.getReceivedCount();
                mName.setText(mGift.getTitle());
                String precent = String.format(mContext.getResources().getString(R.string.gift_precent), IntegratedDataUtil.calculatePrecent(mGift.getRemain(), mGift.getTotal()));
                mProgress.setProgress(mGift.getRemain() * 1000 / mGift.getTotal());
                String substring = Html.fromHtml(precent).toString().substring(2);
                mRest.setText(substring);
                mGetGiftBT.setClickable(true);
                if (mGift.isReceived()) {
                    mGiftState = GiftState.Recived;
                    // mGetGiftBT.setClickable(false);
                    mGetGiftBT.setBackgroundResource(R.drawable.btn_uninstall_selector);
                    mGetGiftBT.setTextColor(getResources().getColor(R.color.point_grey));
                    mGetGiftBT.setTextSize(13);
                    mGetGiftBT.setText("已领取");
                } else if (mGift.getRemain() <= 0) {
                    mGiftState = GiftState.NoGift;
                    // mGetGiftBT.setClickable(false);
                    mGetGiftBT.setBackgroundResource(R.drawable.btn_uninstall_selector);
                    mGetGiftBT.setTextColor(getResources().getColor(R.color.point_grey));
                    mGetGiftBT.setTextSize(13);
                    mGetGiftBT.setText("领完了");
                } else {
                    mGiftState = GiftState.UnRecived;
                    mGetGiftBT.setTag(R.id.gift_mine_click_tag, mGift);
                    mGetGiftBT.setBackgroundResource(R.drawable.get_gift_button_selector);
                    mGetGiftBT.setTextColor(getResources().getColor(R.color.theme_green));
                    mGetGiftBT.setTextSize(13);
                    mGetGiftBT.setText("领取");
                }
                ImageloaderUtil.loadRoundImage(getContext(), TextUtils.isEmpty(mGift.getIconUrl()) ? mGift.getGame().getIconUrl() : mGift.getIconUrl(), mIcon);
                mGetGiftBT.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if (mGift == null) {
                            return;
                        }
                        switch (mGiftState) {
                            case NoGift:
                                ToastUtils.showToast(mContext, "没有礼包了哦！");
                                break;

                            case Recived:
                                ToastUtils.showToast(mContext, "已经领取了哦！");
                                break;

                            case UnRecived:
                                GiftManger manger = new GiftManger(mContext, mGift);
                                manger.setGetGiftResponeseListener(new GetGiftResponseListener() {

                                    @Override
                                    public void onSuccess() {
                                        notifyGetGiftSuccess();
                                    }

                                    @Override
                                    public void onFailure(GiftDomainDetail gift) {
                                        //                                        fillView();
                                    }
                                });
                                manger.getGift(mPageName);
                                break;
                        }
                    }
                });
                setViewTagForClick(this, mGift, mGift.getDomainType(), type, null);

                if (!TextUtils.isEmpty(mGift.getContent())) {
                    tv_gifContent.setText(mGift.getContent());
                }
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

    }

    private void notifyGetGiftSuccess() {
        if (mGift != null) {
            mGift.setIsReceived(true);
            mGiftState = GiftState.Recived;
            mGetGiftBT.setBackgroundResource(R.drawable.btn_uninstall_selector);
            mGetGiftBT.setTextColor(getResources().getColor(R.color.point_grey));
            mGetGiftBT.setTextSize(13);
            mGetGiftBT.setText("已领取");
            mGift.setRemain(mGift.getRemain() - 1);
            String precent = String.format(mContext.getResources().getString(R.string.gift_precent), IntegratedDataUtil.calculatePrecent(mGift.getRemain(), mGift.getTotal()));
            mProgress.setProgress(mGift.getRemain() * 1000 / mGift.getTotal());
            String substring = Html.fromHtml(precent).toString().substring(2);
            mRest.setText(substring);
            ImageloaderUtil.loadRoundImage(getContext(), TextUtils.isEmpty(mGift.getIconUrl()) ? mGift.getGame().getIconUrl() : mGift.getIconUrl(), mIcon);
        }
    }

    private enum GiftState {
        Recived, NoGift, UnRecived
    }
}
