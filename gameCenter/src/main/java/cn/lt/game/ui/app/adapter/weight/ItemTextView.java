package cn.lt.game.ui.app.adapter.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;

/***
 * text view;
 */
public class ItemTextView extends ItemView {

    private TextView mTitleView;

    public ItemTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ItemTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public ItemTextView(Context context, BaseOnclickListener clickListener) {
        super(context);
        this.mContext = context;
        this.mClickListener = clickListener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        LayoutInflater.from(context).inflate(R.layout.layout_item_text, this);
        init();
    }

    private void init() {
        mTitleView = (TextView) findViewById(R.id.tv_title);
    }


    public void fillView() {
        try {
            if (mItemData != null) {
                mTitleView.setText((String) ((UIModule) mItemData.getmData()).getData());
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
                fillView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
