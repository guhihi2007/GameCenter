package cn.lt.game.ui.app.awardgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.global.LogTAG;
import cn.lt.game.base.BaseFragmentActivity;
import cn.lt.game.bean.SignPointsNet;
import cn.lt.game.event.RefreshPointsEvent;
import cn.lt.game.lib.netdata.ErrorFlag;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.file.SyncPointsUtil;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.web.WebCallBackToBean;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.app.awardgame.adapter.AwardAdapter;
import cn.lt.game.ui.app.awardpoints.AwardPointsRecordActivity;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;
import de.greenrobot.event.EventBus;

/**
 * @author chengyong
 * @time 2017/6/1 13:54
 * @des ${抽奖、积分主活动}
 */

public class AwardActivity extends BaseFragmentActivity implements NetWorkStateView.RetryCallBack, View.OnClickListener, UserInfoUpdateListening {
    public SlidingTabLayout indicator;
    public AwardAdapter adapter;
    public TitleBarView titleBarView;
    public ViewPager pager;
    private List<Fragment> mFragments = new ArrayList<>();
    private String[] mTabTile;
    private ImageView mUserIcon;
    private TextView mUserName;
    private TextView mUserScore;
    private LinearLayout mUserContainer;
    public int points;

    //    public static final int TO_AWARD = 0;
//    public static final int TO_SCORE = 1;
//    public static final String JUMP_TYPE = "jump_type";
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_award);
        EventBus.getDefault().register(this);
        initView();
        if (UserInfoManager.instance().isLogin()) {
            LogUtils.i(LogTAG.CHOU, "UserInfo=="+UserInfoManager.instance().getUserInfo());
            mUserName.setText(UserInfoManager.instance().getUserInfo().getNickname());
            ImageloaderUtil.loadUserHead(this, UserInfoManager.instance().getUserInfo().getAvatar(), mUserIcon);
            getUserDataNet();
        } else {
            setPoints(SyncPointsUtil.getLocalTotalPoints());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onEventMainThread(RefreshPointsEvent event) {
        setPoints(event.signPointsNet.getTotal_point());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserInfoManager.instance().removeListening(this);
    }

    private void initView() {
        titleBarView = (TitleBarView) findViewById(R.id.detail_action_bar);
        indicator = (SlidingTabLayout) findViewById(R.id.gamedetail_indicator);
        pager = (ViewPager) findViewById(R.id.gamedetail_pager);
        mUserIcon = (ImageView) findViewById(R.id.award_user_icon);
        mUserName = (TextView) findViewById(R.id.award_user_name);
        mUserScore = (TextView) findViewById(R.id.award_user_score);
        mUserContainer = (LinearLayout) findViewById(R.id.user_container);
        initFragmentAndTitle();
        if (adapter == null) {
            adapter = new AwardAdapter(getSupportFragmentManager(), this, mFragments, mTabTile);
        }
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
        titleBarView.setBackHomeVisibility(View.INVISIBLE);
        UserInfoManager.instance().addRealListening(this);
        mUserContainer.setOnClickListener(this);
    }

    private void initFragmentAndTitle() {
        mFragments.add(new AwardFragment());
        mFragments.add(new ScoreFragment());
        mTabTile = new String[]{"抽奖活动", "获取积分"};
    }

    @Override
    public void setNodeName() {
        setmNodeName("");
    }

    @Override
    public void retry() {
    }

    @Override
    public void onClick(View view) {
        if (UserInfoManager.instance().isLogin()) {
            startActivity(new Intent(getApplicationContext(), AwardPointsRecordActivity.class));
        } else {
            Intent intent = UserInfoManager.instance().getLoginIntent(this, true);
            startActivity(intent);
        }
    }

    @Override
    public void userLogin(UserBaseInfo userBaseInfo) {
        LogUtils.i(LogTAG.CHOU, "用户登录了:");
//        mUserName.setText(userBaseInfo.getNickname());
//        ImageloaderUtil.loadUserHead(this, userBaseInfo.getAvatar(), mUserIcon);

    }

    @Override
    public void updateUserInfo(UserBaseInfo userBaseInfo) {
        LogUtils.i(LogTAG.CHOU, "更新用户登录信息了:");
        mUserName.setText(userBaseInfo.getNickname());
        ImageloaderUtil.loadUserHead(this, userBaseInfo.getAvatar(), mUserIcon);
        getUserDataNet();
    }

    @Override
    public void userLogout() {
        LogUtils.i(LogTAG.CHOU, "用户登出了:");
        updateUIForLogout();
    }

    public void updateUIForLogout() {
        mUserIcon.setImageDrawable(getResources().getDrawable(R.mipmap.user_avatar));
        mUserName.setText(R.string.user_center_user_name_signin);
        setPoints(SyncPointsUtil.getLocalTotalPoints());
    }

    public void setPoints(int points) {
        if (UserInfoManager.instance().isLogin()) {
            mUserName.setText(UserInfoManager.instance().getUserInfo().getNickname());
        } else {
            if (points > 0) {
                mUserName.setText(R.string.user_center_user_name_no_signin_save);
            } else {
                mUserName.setText(R.string.user_center_user_name_signin);
            }
        }
        this.points = points;
        mUserScore.setText((points>9999)?"9999+":String.valueOf(points));
    }

    /**
     * 客户端主动处理积分
     * @param offsetPoints
     */
    public void setPointsByManual(int offsetPoints) {
        mUserScore.setText((points-offsetPoints>9999)?"9999+":String.valueOf(points-offsetPoints));
        LogUtils.e(LogTAG.CHOU, "客户端主动处理积分points=>"+points+"==offsetPoints=="+offsetPoints);
    }

    public void getUserDataNet() {
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.USER_CENTER_DATA, null, new WebCallBackToBean<SignPointsNet>() {

            @Override
            protected void handle(SignPointsNet info) {
                LogUtils.d(LogTAG.HTAG, info.toString());
                setPoints(info.getPoints());
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                if (statusCode == ErrorFlag.userLogout) {
                    updateUIForLogout();
                }
                LogUtils.d(LogTAG.HTAG, statusCode + "" + error.getMessage());
            }
        });
    }

    public void onEventMainThread(String event) {
        if (event.equals("动态下载按钮")) {
            LogUtils.i("Erosion", "TitleBarView来了来了");
            titleBarView.startDownloadAnimation();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        finish();
    }
}
