package cn.lt.game.ui.app.gamestrategy;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.huanju.data.HjDataClient;
import com.huanju.data.content.raw.info.HjBatchInfoItem;
import com.huanju.data.content.raw.info.HjInfoItem;
import com.huanju.data.content.raw.listener.HjRequestBatchListListener;

import java.util.ArrayList;

public class BatchInformation implements
		HjRequestBatchListListener<HjBatchInfoItem> {
	private static final int MSG_RES_STATUS_SUCCESS = 0;
	private static final int MSG_RES_STATUS_FAILED = 1;
	private BachListCallBack bachListCallBack;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_RES_STATUS_SUCCESS:
				if(bachListCallBack!=null){
					bachListCallBack.BachListSuccess((ArrayList<HjInfoItem>) msg.obj);
				}

				break;
			case MSG_RES_STATUS_FAILED:
				if(bachListCallBack !=null){
					bachListCallBack.BachListFailed();
				}
				break;
			default:
				break;
			}
		}
	};

	public BatchInformation(Context context , ArrayList<String> list) {
		// TODO Auto-generated constructor stub
		HjDataClient.getInstance(context).requestBatchStrategyList(this, list,2);
	}

	@Override
	public void onFailed(String arg0) {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(MSG_RES_STATUS_FAILED);

	}
	@Override
	public void onEmpty() {
		ArrayList<HjInfoItem> list = new ArrayList<HjInfoItem>();
		Message msg = new Message();
		msg.what = MSG_RES_STATUS_SUCCESS;
		msg.obj = list;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onSuccess(ArrayList<HjBatchInfoItem> arg0) {
		// TODO Auto-generated method stub
		ArrayList<HjInfoItem> list = new ArrayList<HjInfoItem>();
		for (HjBatchInfoItem hjBatchInfoItem : arg0) {
	
			list.addAll(hjBatchInfoItem.getList());
			
		}

		
		Message msg = new Message();
		msg.what = MSG_RES_STATUS_SUCCESS;
		msg.obj = list;
		mHandler.sendMessage(msg);

	}

	public interface BachListCallBack {
		void BachListSuccess(ArrayList<HjInfoItem> list);

		void BachListFailed();
	}

	public BachListCallBack getBachListCallBack() {
		return bachListCallBack;
	}

	public void setBachListCallBack(BachListCallBack bachListCallBack) {
		this.bachListCallBack = bachListCallBack;
	}

}
