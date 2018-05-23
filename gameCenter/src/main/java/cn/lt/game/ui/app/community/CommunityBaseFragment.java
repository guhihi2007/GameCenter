package cn.lt.game.ui.app.community;

import android.content.ComponentCallbacks2;
import android.os.Bundle;

import cn.lt.game.base.BaseFragment;

public abstract class CommunityBaseFragment extends BaseFragment implements
		ComponentCallbacks2 {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mActivity.registerComponentCallbacks(this);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onTrimMemory(int level) {
		// TODO Auto-generated method stub
		switch (level) {
		case TRIM_MEMORY_UI_HIDDEN:// 内存不足，并且该进程的UI已经不可见了。
			release();
			break;
		default:
			break;
		}

	}

	protected void requestData() {}

	protected void release() {}
}
