package cn.lt.game.ui.app.adapter.weight;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;


/***
 * text view;
 */
public class ItemNewGiftTitleView extends ItemView {
    private LinearLayout mContainer;
    private TextView mTitleView;
    private View mView;

    public ItemNewGiftTitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ItemNewGiftTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public ItemNewGiftTitleView(Context context, BaseOnclickListener clickListener) {
        super(context);
        this.mContext = context;
        this.mClickListener = clickListener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        LayoutInflater.from(context).inflate(R.layout.layout_item_new_gift_title, this);
        initView();
    }

    private void initView() {
        mContainer = (LinearLayout) findViewById(R.id.ll_container);
        mTitleView = (TextView) findViewById(R.id.tv_title);
        mView = findViewById(R.id.v_title);
    }


    public void fillView() {

    }

    @Override
    public void fillLayout(ItemData<? extends BaseUIModule> data, int position, int listSize) {
        if (((UIModule) data.getmData()).getData() instanceof String) {
            String title = (String) ((UIModule) data.getmData()).getData();
            if (!TextUtils.isEmpty(title)) {
                if ("最新礼包".equals(title)) {
                    mTitleView.setVisibility(View.GONE);
                    mView.setVisibility(View.GONE);
                    LinearLayout.LayoutParams paras = (LayoutParams) mContainer.getLayoutParams();
                    paras.height =(int) getResources().getDimension(R.dimen.margin_size_12dp);
                    mContainer.setLayoutParams(paras);
                } else {
                    mTitleView.setText(title);
                }
            }
        }
    }
}
