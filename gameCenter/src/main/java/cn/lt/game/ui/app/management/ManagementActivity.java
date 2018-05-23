package cn.lt.game.ui.app.management;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseFragmentActivity;
import cn.lt.game.event.RedPointEvent;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.Utils;
import de.greenrobot.event.EventBus;

/**
 * @author chengyong
 * @time 2017/10/27 11:32
 * @des ${管理页}
 */

public class ManagementActivity extends BaseFragmentActivity {

    private TextView downloadManagementRedPoint, upgradeRedPoint;
    private SlidingTabLayout indicator;
    private ViewPager pager;
    private MyApplication myApplication;
    private ManagementPagerAdapter adapter;
    private Context mContext;
//    private View mRedPoint;
    private int mCurrentPage = POSITION_DOWNLOAD_MANAGEMENT;

    public static final int POSITION_DOWNLOAD_MANAGEMENT = 0;
    public static final int POSITION_UPGRADE = 1;
    public static final int POSITION_INSTALLED = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        initView();
    }

    @Override
    public void setNodeName() {

    }

    private void initView() {
        TextView titleBar = (TextView) findViewById(R.id.tv_page_title);
        titleBar.setText(getResources().getString(R.string.my_manager));
        findViewById(R.id.btn_page_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myApplication = (MyApplication) getApplication();
        mContext=this;
        mCurrentPage = this.getIntent().getIntExtra("id",POSITION_DOWNLOAD_MANAGEMENT);
        init_point();
        adapter = new ManagementPagerAdapter(getSupportFragmentManager(), mContext);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
//        mRedPoint = findViewById(R.id.tv_titleBar_redPoint);
//        RedPointsViewUtils.initTopManagerRedPoints(mRedPoint);
        indicator = (SlidingTabLayout) findViewById(R.id.indicator);

        indicator.setViewPager(pager);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                LogUtils.e(LogTAG.HTAG,"manage"+position);
                if (position == POSITION_DOWNLOAD_MANAGEMENT) {
                    downloadManagementRedPoint.setVisibility(View.GONE);
                    myApplication.setNewGameDownload(false);
                } else if (position == POSITION_UPGRADE) {
                    upgradeRedPoint.setVisibility(View.GONE);
                    myApplication.setNewGameUpdate(false);
                }
                notifyDownloadAndUpdateRed();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        indicator.setCurrentTab(mCurrentPage);
        setRedPoint();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i(LogTAG.HTAG,"Management onResume");
        EventBus.getDefault().post("updateView");
//        setRedPoint();
//        RedPointsViewUtils.initTopManagerRedPoints(mRedPoint);
    }

    private void setRedPoint() {
        if (adapter != null) {
            adapter.setUserVisibleHint(pager.getCurrentItem(), true);
        }
        downloadManagementRedPoint.setVisibility(myApplication.getNewGameDownload() ? View.VISIBLE : View.GONE);
        upgradeRedPoint.setVisibility(myApplication.getNewGameUpdate() ? View.VISIBLE : View.GONE);

        int position = pager.getCurrentItem();
        if (position == POSITION_DOWNLOAD_MANAGEMENT) {
            downloadManagementRedPoint.setVisibility(View.GONE);
            myApplication.setNewGameDownload(false);
        } else if (position == POSITION_UPGRADE) {
            upgradeRedPoint.setVisibility(View.GONE);
            myApplication.setNewGameUpdate(false);
        }

        notifyDownloadAndUpdateRed();

    }

    private void notifyDownloadAndUpdateRed() {
        if (myApplication.getNewGameDownload() || myApplication.getNewGameUpdate()) {
            EventBus.getDefault().post(new RedPointEvent(true));
        } else {
            EventBus.getDefault().post(new RedPointEvent(false));
        }
    }

    /* 设置小红点位置 */
    private void init_point() {
        downloadManagementRedPoint = (TextView) findViewById(R.id.management_redPoint1);
        upgradeRedPoint = (TextView) findViewById(R.id.management_redPoint2);

        int screenWidth = Utils.getScreenWidth(mContext);
        int tmp = (int) (screenWidth / 3.0);
        int size = DensityUtil.dip2px(mContext, 6);
        int margin = DensityUtil.dip2px(mContext, 8);
        int margin9 = DensityUtil.dip2px(mContext, 9);

        RelativeLayout.LayoutParams pPointLayoutParams = new RelativeLayout.LayoutParams(size, size);
        pPointLayoutParams.setMargins(tmp - size * 4, margin, 0, 0);
        downloadManagementRedPoint.setLayoutParams(pPointLayoutParams);

        RelativeLayout.LayoutParams iPointLayoutParams = new RelativeLayout.LayoutParams(size, size);
        iPointLayoutParams.setMargins(screenWidth / 2 + margin9 * 2, margin, 0, 0);
        upgradeRedPoint.setLayoutParams(iPointLayoutParams);


    }
}
