package cn.lt.game.ui.app.gamestrategy;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;

import com.flyco.tablayout.SlidingTabLayout;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragmentActivity;
import cn.lt.game.lib.view.TitleBarView;

/**
 * 攻略中心 Activity
 *
 * @author Administrator
 */
public class GameStrategyHomeActivity extends BaseFragmentActivity {


    private SlidingTabLayout indicator;
    private StrategyPagerAdapter adapter;
    private removeCursorCallBack callBack;
    private TitleBarView titleBarView;
    private View mNoneSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_strategyhome);
        titleBarView = (TitleBarView) findViewById(R.id.strategy_titleBar);
        mNoneSearchView = findViewById(R.id.not_serach_btn_moreButton);
        mNoneSearchView.setVisibility(View.GONE);
        titleBarView.setTitle("攻略");
        titleBarView.setBackHomeVisibility(View.INVISIBLE);
        titleBarView.setScrollVerticalViewVisible(View.GONE);
        adapter = new StrategyPagerAdapter(getSupportFragmentManager(), this);
        ViewPager pager = (ViewPager) findViewById(R.id.strategy_pager);
        pager.setAdapter(adapter);

        indicator = (SlidingTabLayout) findViewById(R.id.strategy_indicator);
        indicator.setViewPager(pager);
        pager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                if (arg0 == 0 && callBack != null) {
                    callBack.removeCursor();
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    public interface removeCursorCallBack {
        void removeCursor();
    }

    public removeCursorCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(removeCursorCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        if (callBack != null) {
            callBack.removeCursor();
        }
        super.onResume();
    }

    @Override
    public void setNodeName() {
        setmNodeName("STRATEGYHOME");
    }

}
