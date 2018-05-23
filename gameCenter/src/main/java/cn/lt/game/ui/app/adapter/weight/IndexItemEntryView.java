package cn.lt.game.ui.app.adapter.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleGroup;
import cn.lt.game.ui.app.adapter.ItemSubView;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;

public class IndexItemEntryView extends ItemView {

    private List<ItemData<UIModule>> mEntrys;

    private LinearLayout mRoot;
    public View mChangePadding;

    public IndexItemEntryView(Context context, BaseOnclickListener onclickListener) {
        super(context);
        this.mContext = context;
        this.mClickListener = onclickListener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        LayoutInflater.from(context).inflate(R.layout.index_item_entry, this);
        init();
    }

    public IndexItemEntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IndexItemEntryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        mRoot = (LinearLayout) findViewById(R.id.llt_root_item_index_entry);
        mChangePadding = findViewById(R.id.change_padding);
        mRoot.setVisibility(View.GONE);
    }

    private void produceChild(int count) {
        int children = mRoot.getChildCount();
        if (children > count) {
            for (int i = children - 1; i >= count; i--) {
                mRoot.removeViewAt(i);
            }
        } else if (children < count) {
            for (int i = children; i < count; i++) {
                ItemSubView itemSubView = new ItemSubView(mContext, mClickListener);
                android.widget.LinearLayout.LayoutParams gameLP = new android.widget.LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
                gameLP.weight = 1;
                mRoot.addView(itemSubView, gameLP);
            }
        }
    }

    public void fillView() {
        try {
            int count = mEntrys.size();
            produceChild(count);
            mRoot.setVisibility(View.VISIBLE);
            for (int i = 0; i < count; i++) {

                ItemSubView itemSubView = (ItemSubView) mRoot.getChildAt(i);
                ItemData<UIModule> game = mEntrys.get(i);
                game.getmPresentData().getPos();
                itemSubView.fillLayout(game, i, count);
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
                mEntrys = mg.getData();
                fillView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
