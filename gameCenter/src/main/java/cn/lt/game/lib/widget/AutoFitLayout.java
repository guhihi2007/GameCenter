package cn.lt.game.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import cn.lt.game.lib.ScreenUtils;

/**
 * Created by wenchao on 2015/11/10.
 * 游戏名称和标签的组合组件
 * 目的为了自适用 游戏名称长度，超出长度不会挤压掉后面的lable视图
 */
public class AutoFitLayout extends ViewGroup {

    private int divider;

    public AutoFitLayout(Context context) {
        super(context);
        init(context);
    }

    public AutoFitLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AutoFitLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public AutoFitLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        divider = (int) ScreenUtils.dpToPx(context,4);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutHorizonal(l, t, r, b);
    }

    void layoutHorizonal(int left, int top, int right, int bottom) {

        int childTop  = 0;
        int childLeft = 0;

        final int height = bottom - top;
//
        final int count = getChildCount();

        if (count != 2) {
//            Log.e("AutoFitLayout", "AutoFitLayout must be 2 child,please check it!");
            return;
        }


        final View child1       = getChildAt(0);
        final int  childWidth1  = child1.getMeasuredWidth();
        final int  childHeight1 = child1.getMeasuredHeight();

        final View child2       = getChildAt(1);
        int  childWidth2  = child2.getMeasuredWidth();
        int  childHeight2 = child2.getMeasuredHeight();

        if(child2.getVisibility()!=VISIBLE){
            childWidth2 = 0;
            childHeight2 = 0;
        }



        childTop = (height - childHeight1) / 2;

        int width = getMeasuredWidth();
        if(childWidth1 + childWidth2 +divider >= width){

            setChildFrame(child1, childLeft, childTop,
                    width - childWidth2 - divider, childHeight1);

            childLeft += width- childWidth2;

            childTop = (height - childHeight2) / 2;

            setChildFrame(child2, childLeft, childTop,
                    childWidth2, childHeight2);

        }else{
            setChildFrame(child1, childLeft, childTop,
                    childWidth1, childHeight1);

            childLeft += childWidth1+divider;


            childTop = (height - childHeight2) / 2;

            setChildFrame(child2, childLeft, childTop,
                    childWidth2, childHeight2);

        }

    }

    private void setChildFrame(View child, int left, int top, int width, int height) {
        child.layout(left, top, left + width, top + height);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeigth = MeasureSpec.getSize(heightMeasureSpec);

        // TODO Auto-generated method stub
        for(int i= 0;i<getChildCount();i++){
            View v = getChildAt(i);
//            Log.v("AutoFitLayout", "measureWidth is " +v.getMeasuredWidth() + "measureHeight is " +v.getMeasuredHeight());
            int widthSpec = 0;
            int heightSpec = 0;
            LayoutParams params = v.getLayoutParams();
            if(params.width > 0){
                widthSpec = MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.EXACTLY);
            }else if (params.width == -1) {
                widthSpec = MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.EXACTLY);
            } else if (params.width == -2) {
                widthSpec = MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.AT_MOST);
            }

            if(params.height > 0){
                heightSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
            }else if (params.height == -1) {
                heightSpec = MeasureSpec.makeMeasureSpec(measureHeigth, MeasureSpec.EXACTLY);
            } else if (params.height == -2) {
                heightSpec = MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.AT_MOST);
            }
            v.measure(widthSpec, heightSpec);
            measureHeigth =Math.max(measureHeigth, v.getMeasuredHeight());
        }
        setMeasuredDimension(measureWidth, measureHeigth);
    }



}
