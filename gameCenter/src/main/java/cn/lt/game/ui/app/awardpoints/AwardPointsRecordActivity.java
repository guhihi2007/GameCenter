package cn.lt.game.ui.app.awardpoints;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.flyco.tablayout.SlidingTabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragmentActivity;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import de.greenrobot.event.EventBus;

/***
 * 奖品积分记录
 *
 * @author honaf
 */
public class AwardPointsRecordActivity extends BaseFragmentActivity {
    private SlidingTabLayout indicator;
    public AwardPointsRecordPagerAdapter adapter;
    public TitleBarView titleBarView;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        EventBus.getDefault().register(this);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_awardpoints);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        if (adapter == null) {
            adapter = new AwardPointsRecordPagerAdapter(getSupportFragmentManager());
        }
        pager = (ViewPager) findViewById(R.id.gamedetail_pager);
        pager.setAdapter(adapter);
        indicator = (SlidingTabLayout) findViewById(R.id.gamedetail_indicator);
        indicator.setViewPager(pager);
        titleBarView = (TitleBarView) findViewById(R.id.detail_action_bar);
        loadTitle();
    }

    public void loadTitle() {
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.AWARD_TITLE,
                null, new WebCallBackToString() {
                    @Override
                    public void onSuccess(String result) {
                        LogUtils.i(LogTAG.CHOU, "获取tltile成功=="+result);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            titleBarView.setTitle(jsonObject.getString("activity_name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error) {
                        LogUtils.i(LogTAG.CHOU, "获取tltile失败" + statusCode+error);
                    }
                });
    }

    @Override
    public void setNodeName() {
        setmNodeName("");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void onEventMainThread(String event) {
        if (event.equals("动态下载按钮")) {
            LogUtils.i("Erosion", "TitleBarView来了来了");
            titleBarView.startDownloadAnimation();
        }
    }

}
