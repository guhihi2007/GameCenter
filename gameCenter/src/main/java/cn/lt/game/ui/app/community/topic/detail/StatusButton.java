package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.StateSet;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

public class StatusButton extends Button {

	private Map<int[], ColorFilter> colorFilterMap;

	private ColorFilter colorFilter;

	public StatusButton(Context context) {
		this(context, null);
	}

	public StatusButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StatusButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		Drawable drawable = getBackground();
		if (drawable != null) {
			colorFilter = getColorFilterForState(getDrawableState());
			if (colorFilter == null) {
				drawable.clearColorFilter();
			} else {
				drawable.setColorFilter(colorFilter);
			}
		}
		
		Drawable[] dr = getCompoundDrawables();
		if (dr != null && dr.length > 0) {
			for (int i = 0; i < dr.length; i++) {
				drawable = dr[i];
				if (drawable != null) {
					colorFilter = getColorFilterForState(getDrawableState());
					if (colorFilter == null) {
						drawable.clearColorFilter();
					} else {
						drawable.setColorFilter(colorFilter);
					}
				}
			}
		}

	}

	public void setColorFilterMap(Map<int[], ColorFilter> colorFilterMap) {
		this.colorFilterMap = colorFilterMap;
	}

	public void addColorFilter(int[] stateSet, ColorFilter colorFilter) {
		if (colorFilterMap == null) {
			colorFilterMap = new HashMap<int[], ColorFilter>();
		}

		colorFilterMap.put(stateSet, colorFilter);
	}

	public void clearColorFilter() {
		colorFilterMap.clear();
		colorFilterMap = null;
	}

	public ColorFilter getColorFilterForState(int[] stateSet) {
		if (colorFilterMap != null) {
			for (Map.Entry<int[], ColorFilter> entry : colorFilterMap
					.entrySet()) {
				if (StateSet.stateSetMatches(entry.getKey(), stateSet)) {
					return entry.getValue();
				}
			}
		}

		return null;
	}
	
}
