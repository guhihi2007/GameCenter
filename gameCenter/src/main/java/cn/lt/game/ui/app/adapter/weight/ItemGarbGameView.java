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
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;

/***
 * 此view为一个容器，主要用在首页最热游戏作为每个item的容器；
 */
public class ItemGarbGameView extends ItemView {

    private LinearLayout mGameRootView;
    private List<ItemData<UIModule>> mGames;
    private LinearLayout mTitleRootView;
    private TextView mTitleNameTv;
    public View mChangPadding,background;

    public ItemGarbGameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ItemGarbGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public ItemGarbGameView(Context context, BaseOnclickListener clickListener) {
        super(context);
        this.mContext = context;
        this.mClickListener = clickListener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        LayoutInflater.from(context).inflate(R.layout.index_item_subject_all, this);
        init();
    }

    private void init() {
        mGameRootView = (LinearLayout) findViewById(R.id.llt_root_game_square);
        mTitleNameTv = (TextView) findViewById(R.id.tv_title);
        mTitleRootView = (LinearLayout) findViewById(R.id.llt_title_root);
        mChangPadding = findViewById(R.id.change_padding);
        background = findViewById(R.id.background);
        // 调整大图尺寸
        int imageViewHeight = (((Utils.getScreenWidth(mContext) - DensityUtil.dip2px(mContext, 30)) * 140) / 420);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, imageViewHeight);
        lp.setMargins(0, mContext.getResources().getDimensionPixelOffset(R.dimen.index_width), 0, 0);
        for (int i = 0; i < 4; i++) {
            SquareGameView gameView = new SquareGameView(mContext, mClickListener);
            android.widget.LinearLayout.LayoutParams gameLP = new android.widget.LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
            gameLP.weight = 1;
            mGameRootView.addView(gameView, gameLP);
        }
    }

    private void adjustProfile(ItemLocal local, String title) {
        switch (local) {
            case topAndBottom:
                mTitleRootView.setVisibility(View.VISIBLE);
                mTitleNameTv.setText(title);
                background.setVisibility(View.VISIBLE);
                break;
            case top:
                mTitleRootView.setVisibility(View.VISIBLE);
                mTitleNameTv.setText(title);
                background.setVisibility(View.GONE);
                break;
            case bottom:
                mTitleRootView.setVisibility(View.GONE);
                background.setVisibility(View.VISIBLE);
                break;
            case middle:
                mTitleRootView.setVisibility(View.GONE);
                background.setVisibility(View.GONE);
                break;
        }
    }

    public void fillView() {
        try {
            if (mGames != null) {
                if (mItemData.isFirst() && mItemData.isLast()) {
                    adjustProfile(ItemLocal.topAndBottom, "时下热门");
                } else if (mItemData.isFirst()) {
                    adjustProfile(ItemLocal.top, "时下热门");
                } else if (mItemData.isLast()) {
                    adjustProfile(ItemLocal.bottom, "");
                } else {
                    adjustProfile(ItemLocal.middle, "");
                }
                int count = mGames.size();
                for (int i = 0; i < count; i++) {
                    SquareGameView view = (SquareGameView) mGameRootView.getChildAt(i);
                    ItemData<UIModule> game = mGames.get(i);
                    game.getmPresentData().getPos();
                    view.fillLayout(game, i, count);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fillLayout(ItemData<? extends BaseUIModule> data, int position, int listSize) {
        try {
            mItemData = data;
            if (mItemData != null) {
                UIModuleGroup mg = (UIModuleGroup) mItemData.getmData();
                //noinspection unchecked
                mGames = mg.getData();
                fillView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
