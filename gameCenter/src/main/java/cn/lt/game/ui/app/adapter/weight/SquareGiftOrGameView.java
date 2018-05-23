package cn.lt.game.ui.app.adapter.weight;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.lib.util.IntegratedDataUtil;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;

public class SquareGiftOrGameView extends ItemView {

    private ImageView mIcon;
    private TextView mName;
    private TextView mRest;

    public SquareGiftOrGameView(Context context,
                                BaseOnclickListener clickListener) {
        super(context);
        this.mContext = context;
        this.mClickListener = clickListener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        LayoutInflater.from(context).inflate(R.layout.gamegift_listview_item_view, this);
        init();
    }

    private void init() {
        mIcon = (ImageView) findViewById(R.id.gift_listView_item_img);
        mName = (TextView) findViewById(R.id.gift_listView_item_txName);
        mRest = (TextView) findViewById(R.id.gift_listView_item_txMsg);
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
        try {
            if (mItemData != null) {
                PresentType type = mItemData.getmPresentType();
                GiftDomainDetail gift = (GiftDomainDetail) ((UIModule) mItemData
                        .getmData()).getData();
                if (PresentType.hot_gifts == type) {
//                    ImageLoader.getInstance().displayLogo(gift.getIconUrl(), mIcon);
                    ImageloaderUtil.loadLTLogo(getContext(),gift.getIconUrl(), mIcon);
                    mName.setText(gift.getTitle());
                    String precent = String.format(
                            mContext.getResources().getString(R.string.gift_precent_hot),
                            IntegratedDataUtil.calculatePrecent(gift.getRemain(),
                                    gift.getTotal()));
                    mRest.setText(Html.fromHtml(precent));
                    setViewTagForClick(this, gift, gift.getDomainType(), type, null);
                } else if (PresentType.gifts_search_ofgame == type) {
//                    ImageLoader.getInstance().displayRoundImage(gift.getGame().getIconUrl(), mIcon);
                    ImageloaderUtil.loadRoundImage(getContext(),gift.getGame().getIconUrl(), mIcon);
                    mName.setText(gift.getGame().getName());
                    mRest.setText(Html.fromHtml(String.format(mContext.getResources()
                            .getString(R.string.gift_count), String.valueOf(gift.getGiftTotal()))));
                    setViewTagForClick(this, gift.getGame(), gift.getDomainType(), type, null);
                }
            }
        } catch (Exception e) {
            this.setVisibility(View.INVISIBLE);
            e.printStackTrace();
        }

    }

}
