package cn.lt.game.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mobstat.StatService;

import cn.lt.game.R;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.RetryCallBack;

public abstract class MyBaseFragment extends BaseFragment implements RetryCallBack {
	public View view;
	public Activity context;
	public NetWorkStateView netWorkStateView;

	@Override
	public void onCreate(Bundle savedInstanceState) {   
		context = this.getActivity();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(getContentViewLayoutId(), null);
		netWorkStateView = (NetWorkStateView) view.findViewById(R.id.game_detail_netWrokStateView);
		netWorkStateView.setRetryCallBack(this);
		super.onCreateView(inflater,container,savedInstanceState);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadData();
	}

	protected void loadData() {
		if (NetUtils.isConnected(this.getActivity())) {
			initAction();
		} else {
			if (netWorkStateView != null) {
				initAction();
				netWorkStateView.showNetworkFailLayout();
			}
		}
	}
	
	@Override
	public void retry() {
		LogUtils.i("zzz", "BaseFragments 重试");
	}

	@Override
	public void onResume() {
		super.onResume();
		StatService.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this);
	}

	/**
	 * 初始化的动作方法
	 */
	public abstract void initAction();

	/**
	 * 相当于setContentView（）
	 * 
	 * @return
	 */
	public int getContentViewLayoutId() {
		return R.layout.activity_netstate;
	}
}
