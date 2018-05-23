package cn.lt.game.ui.app.requisite.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/***
 * 此类主要用作实现在listview的item中添加gridview时可以完全显示grideview的每个item；
 * 以及在gridview的每行之间的添加分割线效果；
 * 
 * @author daxingxiang
 * 
 */
public class AutoGridView extends GridView {

	public AutoGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, height);
	}

}
