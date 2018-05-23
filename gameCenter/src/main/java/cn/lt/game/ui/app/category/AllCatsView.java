package cn.lt.game.ui.app.category;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.domain.essence.ImageType;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsDataProductorImpl;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.weight.ItemView;

/**
 * Created by wenchao on 2015/12/16.
 */
public class AllCatsView extends ItemView {

    private View clickView;
    private View divider, gap;
    private TextView tv, tv0, tv1, tv2, tv3, tv4, tv5;
    private ImageView imageView;
//    private View categoryLabel;

    public AllCatsView(Context context, BaseOnclickListener onclickListener) {
        super(context);
        super.mClickListener = onclickListener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        inflate(context, R.layout.item_all_cats_v4, this);
        initialize();

    }

    void initialize() {
        clickView = findViewById(R.id.ll);
        divider = findViewById(R.id.divider);
        gap = findViewById(R.id.gap);
        tv = (TextView) findViewById(R.id.tv);

        tv0 = (TextView) findViewById(R.id.tv0);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);

        imageView = (ImageView) findViewById(R.id.imageView);
    }

    @Override
    public void fillLayout(ItemData<? extends BaseUIModule> data, int position, int listSize) {
        UIModule uiModule = (UIModule) data.getmData();
        FunctionEssence fe = (FunctionEssence) uiModule.getData();

        if (data.isLast()) {
            divider.setVisibility(View.GONE);
            gap.setVisibility(View.VISIBLE);
        } else {
            divider.setVisibility(View.VISIBLE);
            gap.setVisibility(View.GONE);
        }

        tv.setText(fe.getTitle());
        try {
            tv.setTextColor(Color.parseColor(TextUtils.isEmpty(fe.getColor()) ? "#333333" : fe.getColor()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //加载标签图标
        ImageloaderUtil.loadImage(getContext(), fe.getImageUrl().get(ImageType.COMMON), imageView, false);
        ArrayList<String> idList = new ArrayList<>();
        ArrayList<String> titleList = new ArrayList<>();

        //子label
        if (fe.hasSubFuncEss()) {
            TextView[] textViews = new TextView[]{tv0, tv1, tv2, tv3, tv4, tv5};
            List<FunctionEssence> tags = fe.getSubFuncEss();

            for (FunctionEssence tag : tags) {
                idList.add(tag.getUniqueIdentifier());
                titleList.add(tag.getTitle());
            }

            int minSize = Math.min(textViews.length, tags.size());
//            getIdListAndTitleList(tags, idList, titleList);
            for (int i = 0; i < textViews.length; i++) {
                if (i < minSize) {
                    FunctionEssence tag = tags.get(i);
                    textViews[i].setText(tag.getTitle());
                    //获得统计数据
                    StatisticsEventData sData = StatisticsDataProductorImpl.produceStatisticsData("lable", data.getSubPos(), 1 + i, tag.getUniqueIdentifier(), mPageName, ReportEvent.ACTION_CLICK, null, titleList.get(i), null, "");
                    setViewTagForClick(textViews[i], tag, tag.getDomainEssence().getDomainType(), data.getmPresentType(), sData); //lable
                    //大类id
                    textViews[i].setTag(R.id.cat_data_01, fe.getUniqueIdentifier());
                    //id 列表
                    textViews[i].setTag(R.id.cat_data_02, idList);
                    //title列表
                    textViews[i].setTag(R.id.cat_data_03, titleList);
                    //当前选择id
                    textViews[i].setTag(R.id.cat_data_04, tag.getUniqueIdentifier());
                    //是否大类
                    textViews[i].setTag(R.id.cat_data_05, false);
                    textViews[i].setTag(R.id.cat_data_06, fe.getTitle());
                } else {
                    textViews[i].setText("");
                    textViews[i].setOnClickListener(null);
                }
            }
        }
        //all_cats
        //设置监听
        StatisticsEventData sBigData = StatisticsDataProductorImpl.produceStatisticsData(data   //position值来自于data
                .getmPresentData(), fe.getUniqueIdentifier(), mPageName, ReportEvent.ACTION_CLICK, null, fe.getTitle(), null);
        setViewTagForClick(clickView, fe, fe.getDomainEssence().getDomainType(), data.getmPresentType(), sBigData);
        //大类id
        clickView.setTag(R.id.cat_data_01, fe.getUniqueIdentifier());
        //id 列表
        clickView.setTag(R.id.cat_data_02, idList);
        //title列表
        clickView.setTag(R.id.cat_data_03, titleList);
        //当前选择id
        clickView.setTag(R.id.cat_data_04, fe.getUniqueIdentifier());
        //是否大类
        clickView.setTag(R.id.cat_data_05, true);
        clickView.setTag(R.id.cat_data_06, fe.getTitle());

    }
}
