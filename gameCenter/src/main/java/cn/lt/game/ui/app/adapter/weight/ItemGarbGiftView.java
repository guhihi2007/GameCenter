package cn.lt.game.ui.app.adapter.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleGroup;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;

/***
 * 此view为一个容器，主要作为每个礼包item的容器；
 */
public class ItemGarbGiftView extends ItemView {

    private LinearLayout mGiftRootView;
    private List<ItemData<UIModule>> mGifts;
    private LinearLayout mTitleRootView;
    private TextView mTitleNameTv;
    private View mMoreView;
    public View mChangPadding;

    public ItemGarbGiftView(Context context, AttributeSet attrs,
                            int defStyle) {
        super(context, attrs, defStyle);
    }

    public ItemGarbGiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public ItemGarbGiftView(Context context,
                            BaseOnclickListener clickListener) {
        super(context);
        this.mContext = context;
        this.mClickListener = clickListener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        LayoutInflater.from(context).inflate(R.layout.layout_item_gift_center,
                this);
        init();
    }

    private void init() {
        mGiftRootView = (LinearLayout) findViewById(R.id.llt_root_game_square);
        mMoreView = findViewById(R.id.iv_title);
        mTitleNameTv = (TextView) findViewById(R.id.tv_title);
        mTitleRootView = (LinearLayout) findViewById(R.id.llt_title_root);
        mChangPadding = findViewById(R.id.change_padding);
        for (int i = 0; i < 3; i++) {
            SquareGiftOrGameView gameView = new SquareGiftOrGameView(
                    mContext, mClickListener);
            LayoutParams gameLP = new LayoutParams(
                    0, LayoutParams.MATCH_PARENT);
            gameLP.weight = 1;
            gameView.setVisibility(INVISIBLE);
            mGiftRootView.addView(gameView, gameLP);
        }
    }

    private void adjustProfile(ItemLocal local, String title) {
        mMoreView.setVisibility(View.GONE);
        switch (local) {
            case topAndBottom:
                mTitleRootView.setVisibility(View.VISIBLE);
                mTitleNameTv.setText(title);
                break;
            case top:
                mTitleRootView.setVisibility(View.VISIBLE);
                mTitleNameTv.setText(title);
                break;
            case bottom:
                mTitleRootView.setVisibility(View.GONE);
                break;
            case middle:
                mTitleRootView.setVisibility(View.GONE);
                break;
        }
    }

    public void fillView() {
        try {
            if (mGifts != null) {
                PresentType type = mItemData.getmPresentType();
                String title = "";
                if (PresentType.hot_gifts == type) {
                    title = "最热礼包";
                } else if (PresentType.gifts_search_ofgame == type) {
                    title = "按游戏排序";
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
                int count = mGifts.size();
                for (int i = 0; i < 3; i++) {
                        SquareGiftOrGameView view = (SquareGiftOrGameView) mGiftRootView
                                .getChildAt(i);
                    if (i < count) {
                        view.setVisibility(VISIBLE);
                        ItemData<UIModule> game = mGifts.get(i);
                        view.fillLayout(game,i, 3);
                    } else {
                        view.setVisibility(INVISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fillLayout(ItemData<? extends BaseUIModule> data,int position, int listSize) {
        try {
            mItemData = data;
            if (mItemData != null) {
                UIModuleGroup mg = (UIModuleGroup) mItemData.getmData();
                //noinspection unchecked
                mGifts = mg.getData();
                fillView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
