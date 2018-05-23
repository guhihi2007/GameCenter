package cn.lt.game.ui.app.adapter.weight;

import android.content.Context;

import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.ui.app.adapter.data.ItemData;

/**
 * Created by Administrator on 2015/12/29.
 */
public class ItemViewNull extends ItemView{


    public ItemViewNull(Context context) {
        super(context);
    }

    @Override
    public void fillLayout(ItemData<? extends BaseUIModule> data,int position, int listSize) {

    }
}
