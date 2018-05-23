package cn.lt.game.ui.app.gamestrategy;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.baidu.mobstat.StatService;
import com.huanju.data.HjDataClient;
import com.huanju.data.content.raw.HjRequestFrom;
import com.huanju.data.content.raw.info.HjAlbumInfo;
import com.huanju.data.content.raw.info.HjInfoListItem;
import com.huanju.data.content.raw.listener.IHjRequestItemListListener;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.base.BaseFragment;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.NetWorkStateView;

public abstract class BaseStrategyFragment extends BaseFragment {
	public GameStrategyHomeActivity mActivity;
	public Boolean isConnected = false;
	private HjDataClient client;
	private NetWorkStateView netWorkStateView;
	/** 消息:数据为空 */
	public static final int MESSAGE_DATA_EMPTY = 1;
	/** 消息:数据请求失败 */
	public static final int MESSAGE_REQUEST_FAILED = 2;
	/** 消息:数据请求成功 */
	public static final int MESSAGE_DATA_READY = 3;

	private Handler mHander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_DATA_READY:
				if(netWorkStateView !=null){
					netWorkStateView.hideNetworkView();
				}
		
				 onSuccess((ArrayList<HjInfoListItem>) msg.obj);
				break;
			case MESSAGE_REQUEST_FAILED:
				if (netWorkStateView != null) {
					netWorkStateView.showNetworkFailLayout();
				}

				break;
			case MESSAGE_DATA_EMPTY:
				System.out.println("kongde");
				onEmpty();
				break;
			default:
				break;
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mActivity = (GameStrategyHomeActivity) getActivity();
		client = HjDataClient.getInstance(mActivity);
	}

	protected void sendMessage(ArrayList<HjInfoListItem> list) {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.what = MESSAGE_DATA_READY;
		message.obj = list;
		mHander.sendMessage(message);
	}

	/* 检查网络状态 并获取玩咖数据 */
	public synchronized void checkNetwork(NetWorkStateView netWorkStateView, int pageIndex, int dataSize, String id,
										  Boolean isFrist) {
		// TODO Auto-generated method stub
		this.netWorkStateView = netWorkStateView;
		if (NetUtils.isConnected(mActivity)) {
			if (isFrist && netWorkStateView != null) {
				netWorkStateView.showLoadingBar();
			}
			getNetworkData(pageIndex, dataSize, id);
		} else {
			netWorkStateView.showNetworkFailLayout(); //没网络
		}
	}

	/* 联网获取数据 最新攻略 */
	private void getNetworkData(int pageIndex, int dataSize, final String id) {

		IHjRequestItemListListener<HjInfoListItem> listener = new IHjRequestItemListListener<HjInfoListItem>() {

			@Override
			public void onEmpty() {
				// TODO Auto-generated method stub
				mHander.sendEmptyMessage(MESSAGE_DATA_EMPTY);
				
			}

			@Override
			public void onFailed(int arg0, int arg1, String arg2) {
				
				mHander.sendEmptyMessage(MESSAGE_REQUEST_FAILED);
			}

			@Override
			public void onSuccess(long arg0, boolean arg1, List<String> arg2,
					HjAlbumInfo arg3, List<HjInfoListItem> arg4) {
				// TODO Auto-generated method stub
				
				ArrayList<HjInfoListItem> list = new ArrayList<HjInfoListItem>();
				list.addAll(arg4);
				sendMessage(list);
			}
		};

		client.requestStrategyAlbumList(listener, id, dataSize, pageIndex,
				HjRequestFrom.hj_gamecenter);

	}

	public abstract void onSuccess(ArrayList<HjInfoListItem> list);
	public abstract void onEmpty();

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
		StatService.onResume(this);
		super.onResume();
	}
	

	private void initInputMethod() {
		// TODO Auto-generated method stub
		 View view = mActivity.getWindow().peekDecorView();
		InputMethodManager imm = (InputMethodManager) mActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			InputMethodManager inputmanger = (InputMethodManager)mActivity. getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
	
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		if(mActivity !=null && isVisibleToUser){
			initInputMethod();
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		StatService.onPause(this);
		super.onPause();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		client.release();
		super.onDetach();
	}

	public PendingIntent getDefalutIntent(int flags) {
		PendingIntent pendingIntent = PendingIntent.getActivity(mActivity, 1,
				new Intent(), flags);
		return pendingIntent;
	}

}
