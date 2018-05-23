package cn.lt.game.ui.app.voucher;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.flyco.tablayout.SlidingTabLayout;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragmentActivity;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.view.TitleBarView;
import de.greenrobot.event.EventBus;

/**
 * 我的代金券页面
 * Created by Gpp on 2018/1/13.
 */

public class CouponRecordActivity extends BaseFragmentActivity {

    @Override
    public void setNodeName() {

    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        EventBus.getDefault().register(this);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mycoupon);
        assignViews();
        initView();
    }

    private TitleBarView mMyCouponActionBar;
    private SlidingTabLayout mCouponIndicator;
    private ViewPager mCouponPager;
    private CouponRecordAdapter mAdapter;

    private void assignViews() {
        mMyCouponActionBar = (TitleBarView) findViewById(R.id.mycoupon_action_bar);
        mCouponIndicator = (SlidingTabLayout) findViewById(R.id.coupon_record_indicator);
        mCouponPager = (ViewPager) findViewById(R.id.coupon_record_pager);
    }

    private void initView() {
        mMyCouponActionBar.setTitle(R.string.userc_center_my_coupon);
        if (mAdapter == null) {
            mAdapter = new CouponRecordAdapter(getSupportFragmentManager());
        }
        mCouponPager.setAdapter(mAdapter);
        mCouponIndicator.setViewPager(mCouponPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(String event) {
        if (event.equals("动态下载按钮")) {
            LogUtils.i("Erosion", "TitleBarView来了来了");
            mMyCouponActionBar.startDownloadAnimation();
        }
    }
}
