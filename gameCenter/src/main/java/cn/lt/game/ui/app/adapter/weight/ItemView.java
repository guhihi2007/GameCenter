package cn.lt.game.ui.app.adapter.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.essence.DomainType;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.statistics.StatisticsDataProductor;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.data.PresentData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.common.listener.InstallButtonClickListener;
import cn.lt.game.ui.installbutton.IndexUpdateButtonState;

/***
 * Created by Administrator on 2015/11/20.
 */
public abstract class ItemView extends LinearLayout implements StatisticsDataProductor {
    /**
     * 点击事件监听器
     */
    protected BaseOnclickListener mClickListener;

    protected int mPosition;

    protected String mPageName = "";
    /**
     * 上下文对象
     */
    protected Context mContext;
    /**
     * 每个Item元数据
     */
    protected ItemData<? extends BaseUIModule> mItemData;

    protected GameBaseDetail gameDetailForDownload;
    protected IndexUpdateButtonState installButtonGroup;
    protected InstallButtonClickListener listener;

    public ItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public ItemView(Context context) {
        this(context,null);
    }

    @Override
    public StatisticsEventData produceStatisticsData(PresentData data, String id, String pageName, String action, String downloadType, String mark, String srcType, String packageName) {
        StatisticsEventData mStatisticsData = null;
        try {
            mStatisticsData = new StatisticsEventData();
            mStatisticsData.setPresentType(data.getmType().presentType);
            mStatisticsData.setSubPos(data.getSubPos() == -1 ? 1 : data.getSubPos());
            mStatisticsData.setPos(data.getPos());
            mStatisticsData.setSrc_id(id);
            mStatisticsData.setPage(pageName);
            mStatisticsData.setActionType(action);
            mStatisticsData.setPackage_name(packageName);
            mStatisticsData.setDownloadType(downloadType);
            mStatisticsData.setRemark(mark);
            mStatisticsData.setSrcType(srcType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mStatisticsData;
    }

    @Override
    public StatisticsEventData produceStatisticsData(String PresentType, int pos, int subPos, String id, String pageName, String action, String downloadType, String mark, String srcType) {
        return null;
    }

    /**
     * 给view绑定点击事件；
     *
     * @param data  通过tag携带的具体数据；
     * @param dType 通过tag携带的资源类型（如跳转ht、游戏、礼包...）；
     * @param pType 通过tag携带的数据展现类型；
     * @param sData 通过tag携带的具体统计数据；
     */
    protected void setViewTagForClick(View view, Object data, DomainType dType, PresentType pType, StatisticsEventData sData) {
        //添加点击时需要的各类数据； 1、实体对象；2、统计对象；3、PresentType;4、资源类型；
        view.setTag(R.id.view_data, data);
        view.setTag(R.id.src_type, dType);
        view.setTag(R.id.statistics_data, sData);
        view.setTag(R.id.present_type, pType);
        view.setOnClickListener(mClickListener);
    }

    /**
     * 填充布局，主要是将item数据传进来；
     *
     * @param data
     */
    public abstract void fillLayout(ItemData<? extends BaseUIModule> data, int position, int listSize);

    protected enum ItemLocal {
        top, bottom, middle, topAndBottom
    }
}
