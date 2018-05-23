package cn.lt.game.ui.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.domain.essence.IdentifierType;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.model.PageDetail;
import cn.lt.game.model.PageMap;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.weight.ItemView;

/**
 * Created by Administrator on 2016/11/10.
 */

public class ItemSubView extends ItemView {
    private ImageView imageView;
    private TextView textView;
    private LinearLayout layout;

    public ItemSubView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ItemSubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public ItemSubView(Context context, BaseOnclickListener clickListener) {
        super(context);
        this.mContext = context;
        this.mClickListener = clickListener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        LayoutInflater.from(context).inflate(R.layout.layout_gridview_item, this);
        initView();
    }


    private void initView() {
        imageView = (ImageView) findViewById(R.id.iv_icon_entry_elem);
        textView = (TextView) findViewById(R.id.tv_name_entry_elem);//yinggai zhege huozhe shangmian zhege
        layout = (LinearLayout) findViewById(R.id.layout_entry);//bu yinggai yong zhege jianting dianjishijian why?  布局不都是一样的setTag吗，
        //但是这个linearlayout是一个parent， 那你settag三次 当然是最后一次有效果呀 也就把前面的覆盖掉了，
        // 可是我new 了三个应该不会混淆

    }


    private void fillLayout() {
        final UIModule module = (UIModule) mItemData.getmData();
        final FunctionEssence functionEssence = (FunctionEssence) module.getData();
        if (functionEssence != null) {
            String clickType = "";
            String pageName = functionEssence.getUniqueIdentifierBy(IdentifierType.NAME);
            String highClickType = functionEssence.getHighClickType();
            String pageName410 = functionEssence.getPage_name_410();
            String id = functionEssence.getUniqueIdentifierBy(IdentifierType.ID);
            final String url = functionEssence.getUniqueIdentifierBy(IdentifierType.URL);

            ImageloaderUtil.loadImage(mContext, functionEssence.getImage(), imageView, true);
            textView.setText(functionEssence.getTitle());
            PageDetail page;

            /*如果高版本或pageName410为空则取pageName的值，不为空则直接取高版本或pagaName410或的值*/
            if ( (!TextUtils.isEmpty(highClickType) && PageMap.instance().isIdentifiable(highClickType))
                    || !TextUtils.isEmpty(pageName410)) {

                // 高版本跳转类型并且能识别时，优先级最高
                if (!TextUtils.isEmpty(highClickType) && PageMap.instance().isIdentifiable(highClickType)) {
                    clickType = highClickType;
                } else {
                    clickType = pageName410;
                }


                page = PageMap.instance().getPageDetail(clickType);
                page = (PageDetail) page.clone();
                if (page != null) {
                    String data = functionEssence.getData();
                    page.id = id;
                    if (!TextUtils.isEmpty(data)) {
                        page.value = data;
                        page.value2 =  functionEssence.getTitle();
                    } else if (!TextUtils.isEmpty(url)) {
                        page.value = url;
                    }
                }



            } else {
                page = PageMap.instance().getPageDetail(pageName);
                page = (PageDetail) page.clone();
                clickType = pageName;
                if (page != null) {
                    page.id = id;
                }


            }

            final StatisticsEventData sData = produceStatisticsData(mItemData.getmPresentData(), functionEssence.getUniqueIdentifier(), mPageName, ReportEvent.ACTION_CLICK, Constant.RETRY_TYPE_MANUAL, url, clickType, "");
            setViewTagForClick(layout, page, functionEssence.getDomainEssence().getDomainType(), mItemData.getmPresentType(), sData);

        }
    }

    @Override
    public void fillLayout(ItemData<? extends BaseUIModule> data, int position, int listSize) {
        try {
            mItemData = data;
            if (mItemData != null) {
                fillLayout();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
