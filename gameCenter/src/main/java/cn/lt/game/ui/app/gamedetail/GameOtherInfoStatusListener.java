package cn.lt.game.ui.app.gamedetail;

import android.os.Handler;
import android.os.Message;

import com.huanju.data.content.raw.listener.IHjRequestItemDetailListener;
import com.huanju.data.content.raw.utility.HjGameResInfo;

public abstract class GameOtherInfoStatusListener implements
		IHjRequestItemDetailListener<HjGameResInfo> {
	private static final int SUCCESS = 1;
	private static final int FAILED = 2;
	private Boolean hasStrategyData = false, hasInformationData = false,
			hasNewsData = false;
	private int mStrategyTotalCnt, mInformationTotalCnt, mNewsTotalCnt;
	private int cout = 0;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				onSuccess();
				break;
			case FAILED:
				onFailed();
				break;

			default:
				break;
			}
		}
	};

	public GameOtherInfoStatusListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onFailed(int arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
//		System.out.println(arg0  +" == " + arg1 +"arg==" +arg2);
		hasStrategyData = false;
		hasInformationData = false;
		hasNewsData = false;
		sendOtherMsg(FAILED);
	}

	@Override
	public void onSuccess(HjGameResInfo status) {
		// TODO Auto-generated method stub
		if (status.mStrategyTotalCnt != 0) {
			hasStrategyData = true;
			mStrategyTotalCnt = (int) status.mStrategyTotalCnt;
			cout++;
		}
		if (status.mReviewTotalCnt != 0) {
			hasInformationData = true;
			mInformationTotalCnt = (int) status.mReviewTotalCnt;
			cout++;
		}
		if (status.mNewsTotalCnt != 0) {
			hasNewsData = true;
			mNewsTotalCnt = (int) status.mNewsTotalCnt;
			cout++;
		}
		sendOtherMsg(SUCCESS);
	}

	private void sendOtherMsg(int state) {
		Message msg = handler.obtainMessage();
		msg.what = state;
		handler.sendMessage(msg);
	}

	public Boolean getHasStrategyData() {
		return hasStrategyData;
	}

	public void setHasStrategyData(Boolean hasStrategyData) {
		this.hasStrategyData = hasStrategyData;
	}

	public Boolean getHasInformationData() {
		return hasInformationData;
	}

	public void setHasInformationData(Boolean hasInformationData) {
		this.hasInformationData = hasInformationData;
	}

	public Boolean getHasNewsData() {
		return hasNewsData;
	}

	public void setHasNewsData(Boolean hasNewsData) {
		this.hasNewsData = hasNewsData;
	}

	public int getmStrategyTotalCnt() {
		return mStrategyTotalCnt;
	}

	public void setmStrategyTotalCnt(int mStrategyTotalCnt) {
		this.mStrategyTotalCnt = mStrategyTotalCnt;
	}

	public int getmInformationTotalCnt() {
		return mInformationTotalCnt;
	}

	public void setmInformationTotalCnt(int mInformationTotalCnt) {
		this.mInformationTotalCnt = mInformationTotalCnt;
	}

	public int getmNewsTotalCnt() {
		return mNewsTotalCnt;
	}

	public void setmNewsTotalCnt(int mNewsTotalCnt) {
		this.mNewsTotalCnt = mNewsTotalCnt;
	}

	public int getCout() {
		return cout;
	}

	public void setCout(int cout) {
		this.cout = cout;
	}

	public void release() {
		cout = 0;
	}
	public abstract void onSuccess();
	public abstract void onFailed();


}
