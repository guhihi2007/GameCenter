package cn.lt.game.ui.app.index.manger;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.statistics.exception.NullArgException;
import cn.lt.game.statistics.pageunits.IndexScroll;
import cn.lt.game.ui.app.index.animation.ViewRect;
import cn.lt.game.ui.app.index.animation.ViewWrapper;

/**
 * 该类用来管理某些空间的动画效果；构建该对象后必须执行init()方法初始化初始化的坐标值以及界限值等；
 *
 * @author dxx
 */
@SuppressLint("Recycle")
public class IndexTouchManger implements OnGestureListener, RefreshAndLoadMoreListView.IScrollTopListener, Animator.AnimatorListener, RefreshAndLoadMoreListView.IOnScrollStateChanged {

    private final int ANIMATION_TIME = 150;
    private boolean isBannerFlingDown = false;

    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 通过调整此view的上下位置来实现搜索框上下移动的动画
     * <p/>
     * 实际为 轮播图、listview 的父容器；
     */
    private View mLiftView;

    /**
     * 搜索框，可以通过动画实现拉伸、缩放
     */
    private View mZoomSearchBar;

    /**
     * 搜索框的包装对象；提供对象的目的是为属性动画提供setWidth()等{@link #mZoomSearchBar}本身没有的方法；
     */
    private ViewWrapper mZoomSearchBarwrapper;

    /**
     * 搜索框下面的listview控件，主要作用：
     * <p/>
     * 1、用来提供当前可见的ListView的item的位置坐标做为动画过程中的参考值；
     * <p/>
     * 2、用来监视Listview快速滚动（Fling）状态时是否需要执行动画；
     */
    private RefreshAndLoadMoreListView mListView;

    /**
     * 通过改变该view的透明度来实现动画时title的渐变色效果；
     */
    private View mTitleBackgroudView;

    /**
     * 动画的最大上偏移量；
     * <p/>
     * 达到此偏移量时，搜索条刚好达到可允许的最顶部；
     */
    private float mMaxTopOffset;

    /**
     * 初始化时，banner图的高度；
     */
    private int mBannerViewHeight;

    /**
     * 动画缩放的最大偏移量；
     * <p/>
     * 达到此偏移量时，搜索条刚好达到可允许的最顶部；
     */
    private float mMaxRightOffset;

    /**
     */
    private int mSearchOriginalWidth;

    /**
     * Y轴方向动画移动结束的位置坐标值，
     * <p/>
     * 相对Activity的左上点的坐标；
     */
    private float mGapYCOD;

    /**
     * {@link #mListView}
     */
    private AnimatorSet mAniamtaions;

    /**
     * <p>
     * true ---- 停止向子view传递touch事件；
     * </p>
     * <p>
     * false ---- 由系统判断是否传递事件；
     * </p>
     * <p/>
     * 主要用在当往下滑动时，如果listview已经滑动到最顶部的item，但是还需要继续动画往下移动{@link #mListView}；
     * <p/>
     * 为避免干扰listview的滑动必须将move事消化在父容器中；否则将会出现listview回退的现象；
     */
    private boolean isStopDispatchEventToChild;

    /**
     * 处理touch事件的相关手势操作；
     */
    private GestureDetector gesture;

    /**
     * Fling状态，为true时我们需要判断是否需要将{@link #mLiftView}移动到最顶部或者恢复到最初位置；
     */
    private boolean isFling;

    private boolean needHandleTouchEvent = true;

    private float mSearchBarScale = 1f;

    private float mPreYCOD;

    private float mPreXCOD;

    private float mListAdjustY;

    private MyApplication mApplication;

    private ViewRect mBannerRect;

    private boolean isPressBanner;

    private boolean isUpFromBanner;
    private VelocityTracker mVelocityTracker;
    private IndexScroll indexScroll;
    private float hasSend=2;

    public IndexTouchManger(Context context, View liftView, View zoomSerachBar, RefreshAndLoadMoreListView listView, View titleBackgroudView) {
        try {
            if (context == null || liftView == null || zoomSerachBar == null || listView == null || titleBackgroudView == null) {
                throw new NullArgException("传入参数不能有空：context：" + context + "\n" + "liftView：" + liftView + "\n" + "zoomSerachBar：" + zoomSerachBar + "\n" + "listView：" + listView + "\n" + "titleBackgroudView：" + titleBackgroudView + "\n");
            }
            this.mContext = context;
            this.mLiftView = liftView;
            this.mZoomSearchBar = zoomSerachBar;
            this.mTitleBackgroudView = titleBackgroudView;
            this.mListView = listView;
        } catch (Exception e) {
            needHandleTouchEvent = false;
            LogUtils.i("GOOD", "传入的参数中有为空的...");
            e.printStackTrace();
        }
    }

    public IndexTouchManger init(IndexScroll indexScroll) {
        try {
            this.indexScroll = indexScroll;
            mApplication = (MyApplication) mContext.getApplicationContext();
            mZoomSearchBarwrapper = new ViewWrapper(mZoomSearchBar);
            gesture = new GestureDetector(mContext, this);
            mListView.setmScrollTopListener(this);
            View tempView = ((ViewGroup) mZoomSearchBar).getChildAt(0);
            mMaxTopOffset = mZoomSearchBar.getHeight() - tempView.getHeight();
            mSearchOriginalWidth = mZoomSearchBar.getWidth();
            mBannerViewHeight = ((ViewGroup) mLiftView).getChildAt(0).getHeight();
            mMaxRightOffset = mContext.getResources().getDimensionPixelOffset(R.dimen.titel_bar_muen_icon_width) - mContext.getResources().getDimensionPixelOffset(R.dimen.list_item_lift_right_padding);
            mSearchBarScale = (mMaxTopOffset - mContext.getResources().getDimensionPixelOffset(R.dimen.index_search_margin_bottom_other)) / mMaxTopOffset;
            mListView.getmListView().setScrollingCacheEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @SuppressWarnings("unused")
    private void getV(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        final VelocityTracker velocityTracker = mVelocityTracker;
        velocityTracker.computeCurrentVelocity(1000);
    }

    /**
     * 处理touch事件；
     *
     * @param ev touch事件对象；
     * @return
     */
    public boolean onEvent(MotionEvent ev) {
        // getV(ev);
        initUp(ev);
        if (needHandleTouchEvent) {
            if (gesture.onTouchEvent(ev)) {
                return false;
            }
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (isBannerFlingDown) {
                        if (mAniamtaions != null && mAniamtaions.isRunning()) {
                            mAniamtaions.cancel();
                        }
                        return true;
                    }
                    initDown(ev);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (bannerPressConfirm(ev)) {
                        return false;
                    }
                    float moveTo = calculateMoveSize(ev);
                    if (moveTo != Integer.MAX_VALUE && moveTo != Integer.MIN_VALUE) {
                        startDance(moveTo, 0, true);
                    }
                    if (isStopDispatchEventToChild) {
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    isStopDispatchEventToChild = false;
                    mListAdjustY = 0;
                    startPageRefresh();
                    break;
            }
        } else {
            LogUtils.i("GOOD", "不满足执行动画的基本条件!");
        }
        return false;
    }

    private void initDown(MotionEvent ev) {
        mBannerRect = ViewRect.getRect(mZoomSearchBar);
        if (mBannerRect != null && mBannerRect.contains(ev.getRawX(), ev.getRawY())) {
            isPressBanner = true;
        }
        setPreCOD(ev.getX(0), ev.getY(0));
        mListAdjustY = 0;
        stopPageRefresh();
    }

    private void initUp(MotionEvent ev) {
        if (ev.getAction() == 1) {
            mBannerRect = ViewRect.getRect(mZoomSearchBar);
            if (mBannerRect != null && mBannerRect.contains(ev.getRawX(), ev.getRawY())) {
                isUpFromBanner = true;
                LogUtils.i("test", "is...");
            } else {
                isUpFromBanner = false;
                LogUtils.i("test", "no...");
            }
        }
    }

    private boolean bannerPressConfirm(MotionEvent ev) {
        if (isPressBanner) {
            if (ViewRect.isHorizontalMove(mPreXCOD, mPreYCOD, ev.getX(0), ev.getY(0))) {
                return true;
            }
        }
        return false;
    }

    private void stopPageRefresh() {
        if (mApplication.isRun) {
            mApplication.isRun = false;
        }
    }

    private void startPageRefresh() {
        mApplication.isRun = true;
    }

    /**
     * 计算上下移动的距离；所有的动画效果依赖该返回值来计算；
     *
     * @param ev touch事件对象
     * @return 得到Y轴移动到的坐标位置；
     */
    private float calculateMoveSize(MotionEvent ev) {
        float tempRawY = ev.getY(0);
        float tempDet = tempRawY - mPreYCOD;
        float scale = getMoveScale(tempDet);
        tempDet = tempDet / scale;
        // 增加判断是往下滚还是往上滚，然后处理mGapYCOD的值；
        if (tempDet < 0) {// 往上滑动；
            isStopDispatchEventToChild = false;
            if (mGapYCOD == -mMaxTopOffset) {
                mPreYCOD = tempRawY;
                return Integer.MAX_VALUE;
            }
            // setlistAdjustY(tempDet, tempRawY);
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
            // setlistAdjustY(tempDet, tempRawY);
            // 计算缩放后需要移动的距离；
            mGapYCOD = (mGapYCOD += tempDet) > 0 ? 0 : mGapYCOD;
            if (mListView.getmListView().getFirstVisiblePosition() == 0) {
                // 注意，这里因为我们使用带下拉刷新的listview，包含的item==0的view的高度为0，只有当listview滑到最顶端的时候才是可见的；
                isStopDispatchEventToChild = true;
            } else if (mLiftView.getY() == -mMaxTopOffset) {
                mGapYCOD = -mMaxTopOffset;
            }
        }
        setPreCOD(ev.getX(0), ev.getY(0));
        return mGapYCOD;
    }

    private void setPreCOD(float x, float y) {
        mPreYCOD = y;
        mPreXCOD = x;
    }

    /**
     * 该方法用于：
     * <p/>
     * 当此次move事件与上次move时间的距离比较大时用来修改MotionEvent的值，再将次对象传递到子view中，此功能暂未实现；
     *
     * @param distace
     * @param tempRawY
     * @return
     */
    @SuppressWarnings("unused")
    private float setlistAdjustY(float distace, float tempRawY, MotionEvent ev) {
        float tempY = ev.getY();
        if (Math.abs(distace) >= mMaxTopOffset / 4) {
            distace = distace / 2.1f;
            mListAdjustY += tempRawY - mPreYCOD - distace;
            if (tempY - mListAdjustY > tempRawY) {
                mListAdjustY += tempY - mListAdjustY - tempRawY;
            }
            mGapYCOD = -mMaxTopOffset;
        }
        if (Math.abs(distace) >= mMaxTopOffset / 4) {
            distace = mMaxTopOffset / 6;
            mListAdjustY += tempRawY - mPreYCOD - distace;
        }
        if (mListAdjustY != 0) { // 修改值；
            MotionEvent eventClone = MotionEvent.obtain(ev);
            eventClone.setAction(MotionEvent.ACTION_CANCEL);
            eventClone.setLocation(ev.getX(), tempY - mListAdjustY);
        }

        return distace;
    }

    /**
     * 计算搜索框拉升的尺寸；
     *
     * @param distance 上下移动的距离；
     * @return 横向拉升尺寸；
     */
    private float calculateZoomSize(float distance) {
        float rate = distance / mMaxTopOffset;
        return rate * mMaxRightOffset;
    }

    /**
     * 计算titleview的透明度；
     *
     * @param distance 上下移动的距离；
     * @return title view的透明度alpha值；
     */
    private float calculateAlphaValue(float distance) {
        float temp = mMaxTopOffset / 4; // 当动画移动到总的偏移量的1/4时开始背景色变化效果比较好；
        if (distance > -temp) {
            return 0f;
        }
        float value = Math.abs((distance + temp) / (mMaxTopOffset - temp));
        return value > 1f ? 1f : value;
    }

    /**
     * 根据触摸移动的距离得到动画移动时需要依照缩放的比例；如果动画效果的移动距离==触摸移动距离，
     * 则会出现listview动画移动的距离刚好抵消掉触摸滚动的效果。所以应该控制好动画滚动的速度小于触摸滑动的速度；
     *
     * @param tempRemoveDistance
     * @return 返回缩放率；
     */
    private float getMoveScale(float tempRemoveDistance) {
        float scale = 1;
        // 根据上拉、下拉的判断对移动的处理做相应缩放；
        if (tempRemoveDistance > 0) {
            scale = 1.2f;
        } else {
            scale = 1.1f;
        }
        return scale;
    }

    private void startDance(float nextY, long duration, boolean all) {
        try {
            float zoomRote = calculateZoomSize(nextY);
            float alpha = calculateAlphaValue(nextY);
            if (mAniamtaions != null && mAniamtaions.isRunning()) {
                mAniamtaions.end();
            }
            mAniamtaions = new AnimatorSet();
            mAniamtaions.addListener(this);
            mAniamtaions.setDuration(duration);
            mAniamtaions.setInterpolator(new LinearInterpolator());
            ObjectAnimator mSearchBarZoomAnim = ObjectAnimator.ofInt(mZoomSearchBarwrapper, "width", mSearchOriginalWidth - (int) Math.abs(zoomRote));// 缩小
            ObjectAnimator mTitleBackgroudViewAnim = ObjectAnimator.ofFloat(mTitleBackgroudView, "alpha", alpha);
            if (hasSend!=alpha && (alpha==0 || alpha==1) && indexScroll != null) {
                hasSend =alpha;
                indexScroll.scrollFromBottomToTop(alpha);
            }
            if (all) {
                ObjectAnimator mLiftViewAnim = ObjectAnimator.ofFloat(mLiftView, "y", nextY);
                ObjectAnimator mSearchBarLiftAnim = ObjectAnimator.ofFloat(mZoomSearchBar, "y", nextY * mSearchBarScale);
                mAniamtaions.playTogether(mLiftViewAnim, mSearchBarZoomAnim, mSearchBarLiftAnim, mTitleBackgroudViewAnim);
            } else {
                mAniamtaions.playTogether(mSearchBarZoomAnim, mTitleBackgroudViewAnim);
            }
            mAniamtaions.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (velocityY < 0 && (!isUpFromBanner || Math.abs(velocityY / 1.15) > Math.abs(velocityX))) {
                mGapYCOD = mGapYCOD + velocityY / 1.5f;
                if (Math.abs(mGapYCOD) > mMaxTopOffset) {
                    mGapYCOD = -mMaxTopOffset;
                }
                LogUtils.i("test", "********Fling up-->" + mGapYCOD);
                startDance(mGapYCOD, ANIMATION_TIME, true);
            } else if (mListView.getmListView().getFirstVisiblePosition() >= 1) {
                isFling = true;
            }
            isUpFromBanner = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (isBannerFlingDown) {
            isBannerFlingDown = false;
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        if (isBannerFlingDown) {
            isBannerFlingDown = false;
        }
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    @Override
    public void onScrollTop(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        try {
            LogUtils.i("LoadMoreListView", "TouchManger onScroll()");
            if (isFling && mListView != null && mListView.getChildAt(0) != null && firstVisibleItem <= 1) {
                int[] location = new int[2];
                int[] parent = new int[2];
                mListView.getChildAt(0).getLocationInWindow(location);
                mLiftView.getLocationInWindow(parent);
                if (mBannerViewHeight - (location[1] - parent[1]) <= 2.01f) {
                    isFling = false;
                    mGapYCOD = 0;
                    isBannerFlingDown = true;
                    startDance(mGapYCOD, ANIMATION_TIME, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onScrollChangeListener(int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (indexScroll != null) {
                    indexScroll.scrollStopSubscriberToIndex();
                }
                startPageRefresh();
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                stopPageRefresh();
                break;
        }
    }
}
