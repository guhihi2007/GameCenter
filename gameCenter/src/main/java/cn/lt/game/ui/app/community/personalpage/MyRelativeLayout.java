package cn.lt.game.ui.app.community.personalpage;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * Created by tiantian on 2015/11/12.
 */
public class MyRelativeLayout extends RelativeLayout{
    private Scroller mScroller;
    public MyRelativeLayout(Context context) {
        super(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Scroller getmScroller() {
        return mScroller;
    }

    public void setmScroller(Scroller mScroller) {
        this.mScroller = mScroller;
    }
    @Override
    public void computeScroll() {
		if (mScroller!=null&&mScroller.computeScrollOffset()) {
			scrollTo(0, mScroller.getCurrY());
			postInvalidate();
		}
    }
}
