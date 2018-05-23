package cn.lt.game.ui.app.hot;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragmentActivity;
import cn.lt.game.lib.util.H5Util;
import cn.lt.game.lib.view.TitleBarView;

/**
 * Created by lei on 2017/6/9.
 */

public class HotDetailActivity extends BaseFragmentActivity {
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_detail);
        TitleBarView actionbar = (TitleBarView) findViewById(R.id.actionbar);
        actionbar.setBackHomeVisibility(View.GONE);
        url = getIntent().getStringExtra(H5Util.HOT_DETAIL_URL);
        FragmentManager fragmentManager = getSupportFragmentManager();
        HotFragment hotFragment = new HotFragment();
        Bundle bundle = new Bundle();
        bundle.putString(H5Util.HOT_DETAIL_URL, url);
        hotFragment.setArguments(bundle);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.fl_hot_content, hotFragment);
        ft.commitAllowingStateLoss();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setNodeName() {

    }
}
