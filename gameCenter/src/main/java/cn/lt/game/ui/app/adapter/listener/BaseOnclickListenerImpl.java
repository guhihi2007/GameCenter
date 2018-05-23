package cn.lt.game.ui.app.adapter.listener;

import android.content.Context;
import android.view.View;

public class BaseOnclickListenerImpl extends BaseOnclickListener {

    public BaseOnclickListenerImpl(Context context, String pageName) {
        super(context, pageName);
        this.mContext = context;
        this.mPageName = pageName;
    }

    @Override
    public boolean realOnClick(View v, String mPageName) {
        return false;
    }


}
