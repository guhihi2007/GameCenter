package cn.lt.game.lib.view;

import android.view.ViewGroup;

public class NetWorkStateViewFactory {
	private static final String TAG = "NetWorkState";
	private ViewGroup viewGroup;
	private ViewGroup rootGroup;

	public NetWorkStateViewFactory(ViewGroup rootGroup) {
		this.rootGroup = rootGroup;
		init(rootGroup);
	}

	private void init(ViewGroup rootGroup) {
		for (int i = 0; i < rootGroup.getChildCount(); i++) {
			System.out.println(rootGroup.getChildAt(i));
			if (TAG.equalsIgnoreCase((String) rootGroup.getChildAt(i).getTag())) {
				viewGroup = (ViewGroup) rootGroup.getChildAt(i);
				break;
			}
		}
	}

	public void showLoadingBar() {
		removeView();
		rootGroup.addView(LoadingViewInstance.getInstance(rootGroup
				.getContext()));
	}

	public void showNetworkNoDataLayout() {
		// ((ViewGroup)rootGroup.getParent()).removeAllViews();
		removeView();
		rootGroup
				.addView(LoadingViewInstance.getInstance(rootGroup.getContext()));
	}

	public void removeView() {
		rootGroup.removeAllViews();
	}
}
