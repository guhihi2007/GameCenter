package cn.lt.game.ui.app.adapter.weight;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.lib.util.IntegratedDataUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.gamegift.GiftManger;
import de.greenrobot.event.EventBus;


public class ItemGiftNormalView extends ItemView {

    private TextView mName;

    private TextView mRest;

    private TextView mContentTV;

    private Button mGetGiftBT;

    private ProgressBar mProgress;

    private GiftDomainDetail mGift;

    private ImageView mIcon;

    private View v_buttomLine;

    @SuppressWarnings("unused")
    private int mResetGiftCount;
    private GiftState mGiftState;

    public ItemGiftNormalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public ItemGiftNormalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ItemGiftNormalView(Context context, BaseOnclickListener clickListener) {
        super(context);
        this.mContext = context;
        this.mClickListener = clickListener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        initView();
    }

    @Override
    public void fillLayout(ItemData<? extends BaseUIModule> data, int position, int listSize) {
        try {
            mItemData = data;
            if (mItemData != null) {
                mGift = (GiftDomainDetail) ((UIModule) mItemData.getmData()).getData();
                fillView();

                // 设置间隔线
                if (position == listSize - 1) {
                    v_buttomLine.setVisibility(View.GONE);
                } else {
                    v_buttomLine.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        EventBus.getDefault().register(this);
        LayoutInflater.from(mContext).inflate(R.layout.layout_gift_normal_item, this);
        mName = (TextView) findViewById(R.id.tv_gift_name);
        mRest = (TextView) findViewById(R.id.tv_reset_git_count);
        mContentTV = (TextView) findViewById(R.id.tv_gift_content);
        mProgress = (ProgressBar) findViewById(R.id.iv_cursor);
        mGetGiftBT = (Button) findViewById(R.id.bt_get_gift);
        v_buttomLine = findViewById(R.id.v_buttomLine);
    }

    public void onEventMainThread(GiftDomainDetail info) {
        if (info != null && info.getUniqueIdentifier().equals(mGift.getUniqueIdentifier())) {
            notifyGetGiftSuccess();
        }
    }

    private void fillView() {
        mResetGiftCount = mGift.getTotal() - mGift.getReceivedCount();
        mContentTV.setText("礼包内容：" + mGift.getContent());
        mName.setText(mGift.getTitle());
        String precent = String.format(mContext.getResources().getString(R.string.gift_precent), IntegratedDataUtil.calculatePrecent(mGift.getRemain(), mGift.getTotal()));
        mProgress.setProgress(mGift.getRemain() * 1000 / mGift.getTotal());
        LogUtils.d("ggg","赋值 ：");

        mRest.setText(Html.fromHtml(precent));
//        mRest.setTextColor(getResources().getColor(R.color.light_grey_a));
        mGetGiftBT.setClickable(true);
        if (mGift.isReceived()) {
            mGiftState = GiftState.Recived;
            mGetGiftBT.setBackgroundResource(R.drawable.btn_uninstall_selector);
            mGetGiftBT.setText("已领取");
            mGetGiftBT.setTextColor(getResources().getColor(R.color.light_grey_a));
        } else if (mGift.getRemain() <= 0) {
            mGiftState = GiftState.NoGift;
            mGetGiftBT.setBackgroundResource(R.drawable.btn_uninstall_selector);
            mGetGiftBT.setText("领完了");
            mGetGiftBT.setTextColor(getResources().getColor(R.color.light_grey_a));
        } else {
            mGiftState = GiftState.UnRecived;
            mGetGiftBT.setTag(R.id.gift_mine_click_tag, mGift);
            mGetGiftBT.setBackgroundResource(R.drawable.get_gift_button_selector);
            mGetGiftBT.setText("领取");
            mGetGiftBT.setTextColor(getResources().getColor(R.color.theme_green));
        }
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
                        manger.setGetGiftResponeseListener(new GiftManger.GetGiftResponseListener() {

                            @Override
                            public void onSuccess() {
                                notifyGetGiftSuccess();
                            }

                            @Override
                            public void onFailure(GiftDomainDetail gift) {
                                //fillView();
                            }
                        });
                        manger.getGift(mPageName);
                        break;
                }
            }
        });
        setViewTagForClick(this, mGift, mGift.getDomainType(), mItemData.getmPresentType(), null);
    }

    private void notifyGetGiftSuccess() {
        if (mGift != null) {
            mGift.setIsReceived(true);
            mGiftState = GiftState.Recived;
            mGetGiftBT.setBackgroundResource(R.drawable.get_gift_button_received);
            mGetGiftBT.setText("已领取");
            mGetGiftBT.setTextColor(getResources().getColor(R.color.light_grey_a));
            mGift.setRemain(mGift.getRemain() - 1);
            String precent = String.format(mContext.getResources().getString(R.string.gift_precent), IntegratedDataUtil.calculatePrecent(mGift.getRemain(), mGift.getTotal()));
            mProgress.setProgress(mGift.getRemain() * 1000 / mGift.getTotal());
            mRest.setText(Html.fromHtml(precent));
            LogUtils.d("ggg","赋值   成功：");
        }
    }

    private enum GiftState {
        Recived, NoGift, UnRecived
    }

}
