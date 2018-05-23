package cn.lt.game.ui.app.adapter.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;

public class ItemSearchNoDataHeadView extends ItemView {


    public ItemSearchNoDataHeadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ItemSearchNoDataHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemSearchNoDataHeadView(Context context, BaseOnclickListener clickListener) {
        super(context);
        this.mClickListener = clickListener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.search_no_data_headview, this);
    }






    @Override
    public void fillLayout(ItemData<? extends BaseUIModule> data, int position, int listSize) {
    }

}
