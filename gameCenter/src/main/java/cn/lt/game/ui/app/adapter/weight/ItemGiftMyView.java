package cn.lt.game.ui.app.adapter.weight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.lib.util.ClipBoardManagerUtil;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;

/**
 * Created by Administrator on 2015/12/17.
 */
public class ItemGiftMyView extends ItemView {

    private ImageView mImg;
    private TextView mTxName;
    private TextView mTxTime;
    private TextView mBtCopy;
    private TextView mActivationCode;
    private GiftDomainDetail mGift;
    private View v_bottomInterval;

    public ItemGiftMyView(Context context,
                          BaseOnclickListener clickListener) {
        super(context);
        this.mContext = context;
        this.mClickListener = clickListener;
        if (clickListener != null) {
            this.mPageName = clickListener.getmPageName();
        }
        LayoutInflater.from(mContext).inflate(R.layout.mygift_listview_item,
                this);
        initView();
    }

    private void initView() {
        mImg = (ImageView) findViewById(R.id.mygift_listView_item_img);
        mTxName = (TextView) findViewById(R.id.mygift_listView_item_txName);
        mTxTime = (TextView) findViewById(R.id.mygift_listView_item_txTimeTitle);
        mBtCopy = (TextView) findViewById(R.id.mygift_listView_item_btCopy);
        mActivationCode = (TextView) findViewById(R.id.mygift_listView_item_activationCode);
        v_bottomInterval = findViewById(R.id.v_bottomInterval);

    }

    private void fillView() {
        ImageloaderUtil.loadRoundImage(getContext(), TextUtils.isEmpty(mGift.getIconUrl()) ? mGift.getGame().getIconUrl() : mGift.getIconUrl(), mImg);
        mTxName.setText(mGift.getTitle());
        mTxTime.setText("剩余时间: " + getTime(mGift.getEndTime()));
        mActivationCode.setText(mGift.getCode());
        mBtCopy.setTag(mGift.getCode());
        mBtCopy.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ClipBoardManagerUtil.self().save2ClipBoard(mGift.getCode());
            }
        });
        setViewTagForClick(this, mGift, mGift.getDomainType(), mItemData.getmPresentType(), null);
    }

    @SuppressLint("SimpleDateFormat")
    private String getTime(String date) {
        try {
            long vaildDate = TimeUtils.string2Long(date.contains(":") ? date : date + " 23:59:59",
                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
            long currentDate = System.currentTimeMillis();
            long gapDate = vaildDate - currentDate;
            return gapDate > 0 ? gapDate / 1000 / 60 / 60 / 24 + "天" : "0天";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "0天";
    }

    @Override
    public void fillLayout(ItemData<? extends BaseUIModule> data, int position, int listSize) {
        try {
            mItemData = data;
            if (mItemData != null) {
                mGift = (GiftDomainDetail) ((UIModule) mItemData.getmData()).getData();
                fillView();

                if (position == listSize - 1) {
                    v_bottomInterval.setVisibility(View.VISIBLE);
                } else {
                    v_bottomInterval.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
