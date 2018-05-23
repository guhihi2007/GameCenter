package cn.lt.game.ui.app.awardgame.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import cn.lt.game.R;


/**
 */
public class RatioLayout extends FrameLayout {
    //图片的宽高比
    public float mPicRatio = 1;
    public static final int RELATIVE_WIDTH = 0;//1.已知宽度,动态计算高度
    public static final int RELATIVE_HEIGHT = 1;//2.已知高度,动态计算宽度

    public int mRelative = RELATIVE_WIDTH;



    public RatioLayout(Context context) {
        this(context, null);
    }

    public RatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout);
        mPicRatio = typedArray.getFloat(R.styleable.RatioLayout_picRatio, 1);
        mRelative = typedArray.getInt(R.styleable.RatioLayout_relative, RELATIVE_WIDTH);
        typedArray.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*
        UNSPECIFIED 不确定 wrap_content
        EXACTLY 确定的 100dp  match_parent
        AT_MOST 至多
         */
        int selfWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int selfHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (selfWidthMode == MeasureSpec.EXACTLY && mRelative == RELATIVE_WIDTH) {//已知宽度
            int selfWidth = MeasureSpec.getSize(widthMeasureSpec);
            //根据公式,算出自身应有的高度 图片的宽高比  = RatioLayout宽度/RatioLayout高度
            int selfHeight = (int) (selfWidth / mPicRatio + .5f);


            //算出孩子应有的高度
            int childWidth = selfWidth - getPaddingLeft() - getPaddingRight();
            //算出孩子应有的宽度
            int childHeight = selfHeight - getPaddingTop() - getPaddingBottom();

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);

            //请求所有的孩子测绘自身
            measureChildren(childWidthMeasureSpec, childHeightMeasureSpec);


            //保存测量的结果
            setMeasuredDimension(selfWidth, selfHeight);
        } else if (selfHeightMode == MeasureSpec.EXACTLY && mRelative == RELATIVE_HEIGHT) {//已知高度
            //得到自身的高度
            int selfHeight = MeasureSpec.getSize(heightMeasureSpec);
            //根据公式计算自身应有的宽度-->图片的宽高比  = RatioLayout宽度/RatioLayout高度
            int selfWidth = (int) (mPicRatio * selfHeight + .5f);


            //算出孩子应有的高度
            int childWidth = selfWidth - getPaddingLeft() - getPaddingRight();
            //算出孩子应有的宽度
            int childHeight = selfHeight - getPaddingTop() - getPaddingBottom();

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);

            //请求所有的孩子测绘自身
            measureChildren(childWidthMeasureSpec, childHeightMeasureSpec);


            //保存测量的结果
            setMeasuredDimension(selfWidth, selfHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }


    }
}
