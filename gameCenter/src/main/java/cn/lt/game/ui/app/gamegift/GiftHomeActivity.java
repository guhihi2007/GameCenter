package cn.lt.game.ui.app.gamegift;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.flyco.tablayout.SlidingTabLayout;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragmentActivity;
import cn.lt.game.event.JumpToContentTabEvent;
import cn.lt.game.ui.app.gamegift.adapter.GiftHomeAdapter;
import de.greenrobot.event.EventBus;

public class GiftHomeActivity extends BaseFragmentActivity {

	private SlidingTabLayout indicator;

	private GiftHomeAdapter adapter;

	public final static int GIFT_NUMBER_PER_LINE = 3;

	private ViewPager mViewPager;

	private int mPage;

	public static final String GIFT_PAGE = "page";

	/**
	 * 代表礼包中心页；
	 */
	public static final int GIFT_CENTER = 0;

	/**
	 * 表示我的礼包页；
	 */
	public static final int GIFT_MINE = 1;

	public ViewPager getmViewPager() {
		return mViewPager;
	}

	public void setmViewPager(ViewPager mViewPager) {
		this.mViewPager = mViewPager;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gamepackagehome);
		getIntentData();
		mViewPager = (ViewPager) findViewById(R.id.gamepackage_pager);
		adapter = new GiftHomeAdapter(getSupportFragmentManager(), this);
		mViewPager.setAdapter(adapter);
		indicator = (SlidingTabLayout) findViewById(R.id.gamepackage_indicator);
		indicator.setViewPager(mViewPager);

		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == 1) {
					// 隐藏软键盘
					if (GiftHomeActivity.this.getCurrentFocus() != null) {
						((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
								.hideSoftInputFromWindow(GiftHomeActivity.this
										.getCurrentFocus().getWindowToken(),
										InputMethodManager.HIDE_NOT_ALWAYS);
					}

				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		mViewPager.setCurrentItem(mPage);

		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
	}

	private void getIntentData() {
		mPage = getIntent().getIntExtra(GIFT_PAGE, 0);
		if (mPage < 0 || mPage > 1) {
			mPage = 0;
		}
	}

	@Override
	public void setNodeName() {
		setmNodeName("");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	// 如果点击了礼包轮播图的跳转到热点tab，礼包页面需要关闭
	public void onEventMainThread(JumpToContentTabEvent event) {
		if (!isDestroyed()) {
			finish();
		}
	}
}
