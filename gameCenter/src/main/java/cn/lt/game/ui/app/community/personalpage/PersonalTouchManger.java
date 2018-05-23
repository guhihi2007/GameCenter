package cn.lt.game.ui.app.community.personalpage;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import cn.lt.game.lib.view.ElasticScrollView;
import cn.lt.game.ui.app.index.animation.ViewRect;

/**
 * Created by tiantain on 2015/11/12.
 * 管理个人主页滑动事件
 */
public class PersonalTouchManger implements ElasticScrollView.OnScrollListener {
    /**
     * Y轴上一次停留的坐标值
     */
    private float mPreYCOD;
    /**
     * X轴上一次停留的坐标值
     */
    private float mPreXCOD;
    /**
     * Y轴方向动画移动结束的位置坐标值，
     * <p/>
     * 相对Activity的左上点的坐标；
     */
    private float mGapYCOD;

    /**
     * 动画的最大上偏移量；
     * <p/>
     * 达到此偏移量时，搜索条刚好达到可允许的最顶部；
     */
    private float mMaxTopOffset;

    private Context mContext;

    private View mRootView, mHeadView, mFloatView;

    private Scroller mScroller;

    private boolean isHeadViewPressed;

    public PersonalTouchManger(Context context, View rootView, View headView, View floatView, Scroller scroller) {
        this.mContext = context;
        this.mRootView = rootView;
        this.mHeadView = headView;
        this.mFloatView = floatView;
        this.mScroller = scroller;

    }

    public PersonalTouchManger init() {
        mMaxTopOffset = mFloatView.getHeight();
        return this;
    }

    public boolean onEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setPreCOD(ev.getX(), ev.getRawY());
                break;
            case MotionEvent.ACTION_MOVE:
                if (headViewPressConfirm(ev)) {
                    return false;
                }
                float moveTo = calculateMoveSize(ev);
                if (moveTo != Integer.MAX_VALUE && moveTo != Integer.MIN_VALUE) {
                    mHeadView.scrollTo(0, (int) -moveTo);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }

    private float calculateMoveSize(MotionEvent ev) {
        float tempRawY = ev.getRawY();
        float tempDet = tempRawY - mPreYCOD;
        // 增加判断是往下滚还是往上滚，然后处理mGapYCOD的值；
        if (tempDet < 0) {// 往上滑动；
            if (mGapYCOD == -mMaxTopOffset) {
                mPreYCOD = tempRawY;
                return Integer.MAX_VALUE;
            }
            // 计算缩放后需要移动的距离；
            mGapYCOD = (mGapYCOD += tempDet) > 0 ? 0 : mGapYCOD;
            // 增加判断，如果mGapYCOD的值大于mMaxTopOffset；
            if (Math.abs(mGapYCOD) > mMaxTopOffset) {
                mGapYCOD = -mMaxTopOffset;
            }
        } else {// 往下滑动；
            if (mGapYCOD == 0) {
                mPreYCOD = tempRawY;
                return Integer.MIN_VALUE;
            }
            // 计算缩放后需要移动的距离；
            mGapYCOD = (mGapYCOD += tempDet) > 0 ? 0 : mGapYCOD;
            if (mRootView.getY() == -mMaxTopOffset) {
                mGapYCOD = -mMaxTopOffset;
            }
        }
        setPreCOD(ev.getX(0), ev.getRawY());
        return mGapYCOD;
    }

    /**
     * 判断顶部是否被触碰
     *
     * @param ev
     * @return
     */
    private boolean headViewPressConfirm(MotionEvent ev) {
        return ViewRect.isHorizontalMove(mPreXCOD, mPreYCOD, ev.getX(0), ev.getY(0));
    }

    private void setPreCOD(float x, float y) {
        mPreYCOD = y;
        mPreXCOD = x;
    }

    @Override
    public void onScroll(int scrollY) {

    }

}
