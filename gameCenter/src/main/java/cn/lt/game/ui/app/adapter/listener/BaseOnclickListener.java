package cn.lt.game.ui.app.adapter.listener;

import android.content.Context;
import android.view.View;

import cn.lt.game.R;
import cn.lt.game.domain.essence.DomainType;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.jump.IJumper;
import cn.lt.game.ui.app.jump.JumpFactory;

/***
 * Created by daikin on 2015/11/20.
 */
public abstract class BaseOnclickListener implements View.OnClickListener {
    public GameBaseDetail mGame;
    protected String mPageName;
    protected Context mContext;
    public boolean hasOnclick = true;
    public BaseOnclickListener(Context context, String pageName) {
        this.mPageName = pageName;
        this.mContext = context;
    }


    public String getmPageName() {
        return mPageName;
    }

    public void setmPageName(String mPageName) {
        this.mPageName = mPageName;
    }

    @Override
    public final void onClick(View v) {
        if(mGame != null) {
            mGame.mRemark = "";
        }
        LogUtils.d("hhh", "BaseOnclick的点击走了");
        /*********数据上报**********/
        reportClickEvent(v);

        /*********功能**********/
        if (!realOnClick(v, mPageName)) {
            handleJump(v);
        }
    }

    /**
     * 处理跳转问题；
     *
     * @param v
     */
    private void handleJump(View v) {
        try {
            PresentType pType = (PresentType) v.getTag(R.id.present_type);
            DomainType dType = (DomainType) v.getTag(R.id.src_type);
            IJumper jumper = JumpFactory.produceJumper(pType, dType);
            jumper.jump(v.getTag(R.id.view_data), mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 上报点击事件数据*/
    private void reportClickEvent(View v) {
        try {
            StatisticsEventData eventData = (StatisticsEventData) v.getTag(R.id.statistics_data);
            if (eventData != null) {
                LogUtils.d("hhh", "BaseOnclick的点击位置一p1:" + eventData.getPos() + "==p2:" + eventData.getSubPos()+"==="+eventData.getPresentType());
            }

            //统计数据上报；
            if (mGame == null) {
                DCStat.clickEvent(eventData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 子类需要实现该方法，如果子类在此方法完了点击事件的处理，则返回true。
     * 否则返回false交给父类处理；
     * * @param v
     *
     * @return
     */
    public abstract boolean realOnClick(View v, String mPageName);
}
