package cn.lt.game.lib.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.LogUtils;


/**
 * @项目名: FlowLayoutDemo
 * @类名: FlowLayout
 * @创建时间: 2015-4-29 下午2:25:03
 * @描述: 流式布局
 * @svn版本: $Rev: 63 $
 * @更新人: $Author: admin $
 * @更新时间: $Date: 2016-03-30 11:24:59 +0800 (星期三, 30 三月 2016) $
 * @更新描述: TODO
 */
public class FlowLayout extends ViewGroup {
    public Context mContext;
    private List<Line> mLines = new ArrayList<Line>(); // 用来记录描述有多少行View
    private Line mCurrrenLine;                                            // 用来记录当前已经添加到了哪一行
    private int mHorizontalSpace = 10;
    private int mVerticalSpace = 10;
    private boolean mIsLastLine;

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public FlowLayout(Context context) {
        super(context);
        mContext = context;
        // TODO Auto-generated constructor stub
    }


    public void setSpace(int horizontalSpace, int verticalSpace) {
        this.mHorizontalSpace = horizontalSpace;
        this.mVerticalSpace = verticalSpace;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // 清空
        mLines.clear();
        mCurrrenLine = null;

        int layoutWidth = MeasureSpec.getSize(widthMeasureSpec);

        // 获取行最大的宽度
//        int maxLineWidth = layoutWidth - 2*getPaddingLeft() - 2*getPaddingRight();
        int maxLineWidth = layoutWidth - DensityUtil.dip2px(getContext(),8) ;
        LogUtils.e("FlowLayout", "maxLineWidth" + maxLineWidth );
        // 测量孩子
        int count = getChildCount();
        for (int i = 0; i < count; i++) {

            View view = getChildAt(i);

            // 如果孩子不可见
            if (view.getVisibility() == View.GONE || mLines.size() > 4) {
                continue;
            }

            if (mLines.size() > 4) {
                break;
            }

            // 测量孩子
            measureChild(view, widthMeasureSpec, heightMeasureSpec);

            // 往lines添加孩子
            if (mCurrrenLine == null) {
                // 说明还没有开始添加孩子
                mCurrrenLine = new Line(maxLineWidth, mHorizontalSpace, mContext);

                // 添加到 Lines中
                mLines.add(mCurrrenLine);

                // 行中一个孩子都没有
                mCurrrenLine.addView(view);
            } else {
                // 行不为空,行中有孩子了
                boolean canAdd = mCurrrenLine.canAdd(view);
                if (canAdd) {
                    // 可以添加
                    mCurrrenLine.addView(view);
                } else {

                    if (mLines.size() == 4) {
                        break;
                    }

                    // 不可以添加,装不下去
                    // 换行

                    // 新建行
                    mCurrrenLine = new Line(maxLineWidth, mHorizontalSpace, mContext);
                    //控制最多4行
                    LogUtils.i("FlowLayout", "mLines:" + mLines.size());
                    //                    if(mLines.size()>4)return;
                    // 添加到lines中
                    mLines.add(mCurrrenLine);
                    // 将view添加到line
                    mCurrrenLine.addView(view);
                }
            }
        }

        // 设置自己的宽度和高度
        int measuredWidth = layoutWidth;
        // paddingTop + paddingBottom + 所有的行间距 + 所有的行的高度

        float allHeight = 0;
        for (int i = 0; i < mLines.size(); i++) {
            float mHeigth = mLines.get(i).mHeigth;

            // 加行高
            allHeight += mHeigth;
            // 加间距
            if (i != 0) {
                allHeight += mVerticalSpace;
            }
        }

        int measuredHeight = (int) (allHeight + getPaddingTop() + getPaddingBottom() + 0.5f);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 给Child 布局---> 给Line布局

        int paddingLeft = getPaddingLeft();
        int offsetTop = getPaddingTop();
        for (int i = 0; i < mLines.size(); i++) {
            if (i != mLines.size() - 1) {
                Line line = mLines.get(i);

                // 给行布局
                line.layout(paddingLeft, offsetTop);

                offsetTop += line.mHeigth + mVerticalSpace;
            } else {
                Line line = mLines.get(i);

                // 给行布局
                line.layout(paddingLeft, offsetTop, true);

                offsetTop += line.mHeigth + mVerticalSpace;
            }
        }

    }

    //暴漏给外界的API
    public void setData(List<FunctionEssence> mAllSearchList, int spaceWidth, int spaceHeight) {

        for (int i = 0; i < mAllSearchList.size(); i++) {
            final int j=i;
            TextView tv = new TextView(mContext);
            final FunctionEssence hotSearchBean = mAllSearchList.get(i);
            tv.setText(hotSearchBean.getTitle());
            tv.setTextSize(13);
            tv.setSingleLine(true);
            tv.setMaxEms(7);
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setTextColor(mContext.getResources().getColor(R.color.theme_green));
            int padding = DensityUtil.dip2px(mContext, 7);
            int paddingTop = DensityUtil.dip2px(mContext, 4);
            tv.setPadding(padding, paddingTop, padding, paddingTop);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.shape_hot_tv);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(hotSearchBean,j);
                }
            });
            this.addView(tv);
        }

        setSpace(spaceWidth, spaceHeight);
    }


    private OnClickListener listener;


    public interface OnClickListener {
        void onClick(FunctionEssence hotSearchBean, int j);
    }

    public void setOnItemClickListener(OnClickListener listener) {
        this.listener = listener;
    }
}


class Line {
    // 属性
    private List<View> mViews = new ArrayList<View>();    // 用来记录每一行有几个View
    private float mMaxWidth;                            // 行最大的宽度
    private float mUsedWidth;                        // 已经使用了多少宽度
    public float mHeigth;                            // 行的高度
    private float mMarginLeft;
    private float mMarginRight;
    private float mMarginTop;
    private float mMarginBottom;
    private float mHorizontalSpace;                    // View和view之间的水平间距
    private int mTempLeft;
    private int mTempRight;
    private Context mContext;

    // 构造
    public Line(int maxWidth, int horizontalSpace, Context mContext) {
        this.mMaxWidth = maxWidth;
        this.mHorizontalSpace = horizontalSpace;
        this.mContext = mContext;
    }

    // 方法

    /**
     * 添加view，记录属性的变化
     *
     * @param view
     */
    public void addView(View view) {
        // 加载View的方法

        int size = mViews.size();
        int viewWidth = view.getMeasuredWidth();
        int viewHeight = view.getMeasuredHeight();
        // 计算宽和高
        if (size == 0) {
            // 说还没有添加View
            if (viewWidth > mMaxWidth) {
                mUsedWidth = mMaxWidth;
            } else {
                mUsedWidth = viewWidth;
            }
            mHeigth = viewHeight;
        } else {
            // 多个view的情况
            mUsedWidth += viewWidth + mHorizontalSpace;
            mHeigth = mHeigth < viewHeight ? viewHeight : mHeigth;
        }

        // 将View记录到集合中
        mViews.add(view);
    }

    /**
     * 用来判断是否可以将View添加到line中
     *
     * @param view
     * @return
     */
    public boolean canAdd(View view) {
        // 判断是否能添加View

        int size = mViews.size();

        if (size == 0) {
            return true;
        }

        int viewWidth = view.getMeasuredWidth();

        // 预计使用的宽度
        float planWidth = mUsedWidth + mHorizontalSpace + viewWidth;
        LogUtils.e("FlowLayout", "planWidth____" + planWidth + "___mMaxWidth" + mMaxWidth);
        return planWidth <= mMaxWidth;

    }

    /**
     * 给孩子布局--除了最后一行
     *
     * @param offsetLeft
     * @param offsetTop
     */
    public void layout(int offsetLeft, int offsetTop) {
        // 给孩子布局

        int currentLeft = offsetLeft;

        int size = mViews.size();
        // 判断已经使用的宽度是否小于最大的宽度
        float extra = 0;
        float widthAvg = 0;
        if (mMaxWidth > mUsedWidth) {
            extra = mMaxWidth - mUsedWidth;
            widthAvg = extra / size;
        }

        for (int i = 0; i < size; i++) {
            View view = mViews.get(i);
            int viewWidth = view.getMeasuredWidth();
            int viewHeight = view.getMeasuredHeight();

            // 判断是否有富余
            if (widthAvg != 0) {
                // 改变宽度
                int newWidth = (int) (viewWidth + widthAvg + 0.5f);
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(newWidth, View.MeasureSpec.EXACTLY);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY);
                view.measure(widthMeasureSpec, heightMeasureSpec);

                viewWidth = view.getMeasuredWidth();
                viewHeight = view.getMeasuredHeight();
            }

            // 布局
            int left = currentLeft;
            int top = (int) (offsetTop + (mHeigth - viewHeight) / 2 +
                    0.5f);
            // int top = offsetTop;
            int right = left + viewWidth;
            int bottom = top + viewHeight;
            view.layout(left, top, right, bottom);

            currentLeft += viewWidth + mHorizontalSpace;
            //保存其它行的左右宽度
            mTempLeft = left;
            mTempRight = right;
        }
    }


    /**
     * 给孩子布局--最后一行的布局
     *
     * @param offsetLeft
     * @param offsetTop
     * @param isLastLine
     */
    public void layout(int offsetLeft, int offsetTop, boolean isLastLine) {
        // 给孩子布局

        int currentLeft = offsetLeft;

        int size = mViews.size();
        // 判断已经使用的宽度是否小于最大的宽度
        float extra = 0;
        float widthAvg = 0;
        if (mMaxWidth > mUsedWidth) {
            extra = mMaxWidth - mUsedWidth;
            widthAvg = extra / size;
        }

        for (int i = 0; i < size; i++) {
            // 布局
            View view = mViews.get(i);
            int viewWidth = view.getMeasuredWidth();
            int viewHeight = view.getMeasuredHeight();

            int left = currentLeft;
            int right = left + viewWidth;
            int top = (int) (offsetTop + (mHeigth - viewHeight) / 2 +
                    0.5f);
            int bottom = top + viewHeight;
            LogUtils.i("FlowLayout", "长宽:" + mTempLeft + "___" + mTempRight);
            LogUtils.i("FlowLayout", "自己测出的长宽:" + left + "___" + right);
//                view.layout(12, top, 12, bottom);
            view.layout(left, top, right + DensityUtil.px2dip(mContext, 25), bottom);    //暂时这样写
            currentLeft += viewWidth + DensityUtil.px2dip(mContext, 25) + mHorizontalSpace;
        }
    }
}
