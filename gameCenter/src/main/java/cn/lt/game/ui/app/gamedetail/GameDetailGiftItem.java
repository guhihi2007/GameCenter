package cn.lt.game.ui.app.gamedetail;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.ui.app.gamegift.GiftDetailActivity;
import cn.lt.game.ui.app.gamegift.GiftManger;
import cn.lt.game.ui.app.gamegift.GiftManger.GetGiftResponseListener;
import cn.lt.game.ui.app.gamegift.beans.GiftBaseData;
import de.greenrobot.event.EventBus;

/***
 * 游戏详情-游戏礼包item
 * @author ltbl
 * 
 */
public class GameDetailGiftItem extends LinearLayout {
	private Context context;
	private TextView tv_gift_name;
//	private Button btn_getGift;
	private View v_divider;
	private GiftDomainDetail mGift;
	private ClickListener mClickListener;

	private enum GiftState {
		Recived, NoGift, UnRecived
	}

	private GiftState mGiftState;
	private String pageName;

	public GameDetailGiftItem(Context context,String pageName) {
		super(context);
		this.pageName = pageName;
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.game_detail_gift_item, this);
		mClickListener = new ClickListener();
		initView();
		EventBus.getDefault().register(this);
	}

	private void initView() {
		tv_gift_name = (TextView) findViewById(R.id.tv_gift_name);
//		btn_getGift = (Button) findViewById(R.id.bt_get_gift);
		v_divider = findViewById(R.id.v_divider);
		this.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_game_detail_gift_item_selector));
	}

	/***
	 * 隐藏分割线
	 */
	public void setDividerGone(){
		v_divider.setVisibility(View.GONE);
	}
	public void setData(GiftDomainDetail gift) {
		this.mGift = gift;
		this.setOnClickListener(mClickListener);
		tv_gift_name.setText(gift.getTitle());
		if (mGift.isReceived()) {
			mGiftState = GiftState.Recived;
		} else if (mGift.getRemain() <= 0) {
			mGiftState = GiftState.NoGift;
		} else {
			mGiftState = GiftState.UnRecived;
		}

	}

	public void onEventMainThread(GiftBaseData info) {
		if (info != null && (info.getId()+"").equals(mGift.getUniqueIdentifier())) {
			notifyGetGiftSuccess();
		}
	}

	private void notifyGetGiftSuccess() {
		if (mGift != null) {
			mGift.setIsReceived(true);
			mGiftState = GiftState.Recived;
			mGift.setRemain(mGift.getRemain() - 1);
		}
	}

	class ClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (mGift == null) {
				return;
			}
			if (v.getId() == R.id.bt_get_gift) {
				switch (mGiftState) {
				case NoGift:
					ToastUtils.showToast(context, "没有礼包了哦！");
					break;

				case Recived:
					ToastUtils.showToast(context, "已经领取了哦！");
					break;

				case UnRecived:
					GiftManger manger = new GiftManger(context, mGift);
					manger.setGetGiftResponeseListener(new GetGiftResponseListener() {
						@Override
						public void onSuccess() {
//							notifyGetGiftSuccess();
						}
						@Override
						public void onFailure(GiftDomainDetail gift) {
							setData(mGift);
						}
					});
					manger.getGift(pageName);
					break;
				}
				return;
			}
			Intent intent = new Intent(context, GiftDetailActivity.class);
			intent.putExtra(GiftDetailActivity.GIFT_ID, mGift.getUniqueIdentifier());
			context.startActivity(intent);
		}
	}
}
