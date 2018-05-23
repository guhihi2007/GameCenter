package cn.lt.game.ui.app.management;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.ScreenUtils;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ItemPopupWindow extends PopupWindow{
	public interface OnPopupItemClickListener {
		void onClick(int position);
	}

	private OnPopupItemClickListener mListener;
	public void setOnPopupItemClickListener(OnPopupItemClickListener mListener) {
		this.mListener = mListener;
	}

	public ItemPopupWindow(Context context, int[] resLabels) {
        super(context);
        initView(context,resLabels);
    }

    private void initView(Context context,int[] resLabels) {
    	final LinearLayout tabsContainer = new LinearLayout(context);
    	tabsContainer.setOrientation(LinearLayout.VERTICAL);
		tabsContainer.setGravity(Gravity.CENTER);
    	tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
    	LinearLayout.LayoutParams defaultTabLayoutParams = new LinearLayout.LayoutParams(
				(int) ScreenUtils.dpToPx(tabsContainer.getContext(),70),
				(int)ScreenUtils.dpToPx(tabsContainer.getContext(),28));
    	for (int i = 0; i < resLabels.length; i++) {
    		final int j = i;
    		TextView tab = new TextView(context);
    		tab.setTextColor(Color.rgb(0x33,0x33,0x33));
    		tab.setBackgroundResource(R.drawable.management_popupwindow_bg);
    		tab.setText(resLabels[i]);
    		tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
    		tab.setGravity(Gravity.CENTER);
    		tab.setSingleLine();
    		tab.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismiss();
					if(mListener != null)
						mListener.onClick(j);
					for (int k = 0; k < tabsContainer.getChildCount(); k++) {
						tabsContainer.getChildAt(k).setSelected(false);
					}
					tabsContainer.getChildAt(j).setSelected(true);
				}
			});
    		tabsContainer.addView(tab, defaultTabLayoutParams);
		}
    	tabsContainer.getChildAt(0).setSelected(true);
        
        setContentView(tabsContainer);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setTouchable(true);
    }

}
