package cn.lt.game.ui.app.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.db.service.SearchService;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.global.Constant;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsDataProductorImpl;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.adapter.data.PresentData;

/**
 * 搜索推荐页的view
 */
public class SearchAdvItemView extends LinearLayout implements View.OnClickListener {
    private List<FunctionEssence> hotSearchBeanList = new ArrayList<>();
    private List<PresentData> presentDatas = new ArrayList<>();
    private TextView[] mTV = new TextView[4];
    private View[] mViews = new View[4];
    private Context mContext;
    private String mPageName;
    private AdvertisementFragment.TitlesListFragmentCallBack mTitlesListFragmentCallBack;


    public SearchAdvItemView(Context context, String pageName,  AdvertisementFragment.TitlesListFragmentCallBack mTitlesListFragmentCallBack) {
        super(context);
        this.mContext = context;
        this.mPageName = pageName;
        this.mTitlesListFragmentCallBack = mTitlesListFragmentCallBack;
        initView(context);
    }

    public SearchAdvItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView(context);
    }

    public SearchAdvItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView(context);
    }

    private void initView(Context mContext) {
        LayoutInflater.from(mContext).inflate(R.layout.search_item_view, this);
        mTV[0] = (TextView) findViewById(R.id.tv01);
        mTV[1] = (TextView) findViewById(R.id.tv02);
        mTV[2] = (TextView) findViewById(R.id.tv03);
        mTV[3] = (TextView) findViewById(R.id.tv04);

        mViews[0] = findViewById(R.id.line01);
        mViews[1] = findViewById(R.id.line02);
        mViews[2] = findViewById(R.id.line03);
        mViews[3] = findViewById(R.id.line04);

        mTV[0].setOnClickListener(this);
        mTV[1].setOnClickListener(this);
        mTV[2].setOnClickListener(this);
        mTV[3].setOnClickListener(this);
    }

    public void setData(List<FunctionEssence> list) {
        this.hotSearchBeanList.clear();
        this.hotSearchBeanList.addAll(list);
        drawTitle();
    }

    public void setPresentData(List<PresentData> presentDatas){
        this.presentDatas.clear();
        this.presentDatas.addAll(presentDatas);
    }

    private void drawTitle() {
        for (int i = hotSearchBeanList.size();i<mTV.length; i++){
            mTV[i].setVisibility(View.INVISIBLE);
            mViews[i-1].setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i<hotSearchBeanList.size();i++){
            mTV[i].setText(hotSearchBeanList.get(i).getTitle());
            mViews[3].setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv01:
                jump(hotSearchBeanList.get(0),0);
                break;
            case R.id.tv02:
                jump(hotSearchBeanList.get(1),1);
                break;
            case R.id.tv03:
                jump(hotSearchBeanList.get(2),2);
                break;
            case R.id.tv04:
                jump(hotSearchBeanList.get(3),3);
                break;
        }
    }

    private void jump(FunctionEssence appDetailBean,int pos) {
        if (appDetailBean == null) return;
        SearchService.getInstance(mContext).save(appDetailBean.getTitle());
        StatisticsEventData sData = StatisticsDataProductorImpl.produceStatisticsData(presentDatas.get(pos), appDetailBean.getUniqueIdentifier(), mPageName, ReportEvent.ACTION_CLICK, Constant.RETRY_TYPE_MANUAL, appDetailBean.getTitle(), null);
        DCStat.clickEvent(sData);
        mTitlesListFragmentCallBack.hotWordOnclick(appDetailBean.getTitle());
    }
}
