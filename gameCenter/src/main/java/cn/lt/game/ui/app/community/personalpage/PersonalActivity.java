package cn.lt.game.ui.app.community.personalpage;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.flyco.tablayout.SlidingTabLayout;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragmentActivity;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.model.OthersPage;

/***
 * TA的主页
 *
 * @author tiantian at 2015/11/10
 */
public class PersonalActivity extends BaseFragmentActivity {
    private PersonalAdapter mAdapter;
//    private IconTabPageIndicator indicator;
    private SlidingTabLayout indicator;


    private TitleBarView mTitleBar;
    private Scroller mScroller;
    private RelativeLayout mRootView;
    private MyRelativeLayout headView;
    private PersonalTouchManger mTouchmanger;
    private PersonalPageHeadWidget mFloatView;
    private PersonalBottomBar mBottomBar;
    private static final String TAG = "TA的主页";
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_personal);
        initView();
        userId = getIntent().getIntExtra("userId", -1);
        requestData();

    }

    private void requestData() {
        Net.instance().executeGet(Host.HostType.FORUM_HOST, Uri.getOthersPageUri(userId), null, new WebCallBackToObj<OthersPage>() {
            @Override
            protected void handle(OthersPage info) {
                if (info != null) {
                    processData(info);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.i("zzz", "请求网络失败" + error.getMessage() + statusCode);
                switch (statusCode) {
                    case -2:
                        ToastUtils.showToast(PersonalActivity.this, "数据异常");
                        break;
                    case -3:
                        ToastUtils.showToast(PersonalActivity.this, "返回数据为空");
                        break;
                }
            }
        });
    }

    private void processData(OthersPage info) {
        mTitleBar.setTitle(info.getUser_nickname() + "的主页");
        String mTabTile[] = {"话题(" + info.getTopic_count() + ")", "评论(" + info.getComment_count() + ")", "小组(" + info.getGroup_count() + ")"};
        mAdapter.setmTabTile(mTabTile);
        indicator.notifyDataSetChanged();
        mFloatView.setData(info, false);
        mBottomBar.initAttentionState(info.getRelation());
        mBottomBar.setUserId(userId,info.getUser_nickname(),info.getUser_icon());
    }


    private void initView() {
        mTitleBar = (TitleBarView) findViewById(R.id.personal_title_bar);
        mTitleBar.setBackHomeVisibility(View.INVISIBLE);
        headView = (MyRelativeLayout) findViewById(R.id.context_view);
        mBottomBar = (PersonalBottomBar) findViewById(R.id.ll_bottom_view);
        mFloatView = (PersonalPageHeadWidget) findViewById(R.id.head_view);
        mFloatView.setBackgroundResource(R.mipmap.ic_my_com_backgroud);//还没开始加载预先加载默认图片
        mRootView = (RelativeLayout) findViewById(R.id.root_view);
        mAdapter = new PersonalAdapter(getSupportFragmentManager(), this);
        ViewPager pager = (ViewPager) findViewById(R.id.personal_pager);
        pager.setAdapter(mAdapter);
        pager.setOffscreenPageLimit(mAdapter.getCount());
        indicator = (SlidingTabLayout) findViewById(R.id.personal_indicator);
//        UnderlinePageIndicator underlinePageIndicator = (UnderlinePageIndicator) findViewById(R.id.underline_indicator);
//        underlinePageIndicator.setViewPager(pager, false);
//        underlinePageIndicator.setFades(false);
//        indicator.setOnPageChangeListener(underlinePageIndicator);
        indicator.setViewPager(pager);
        mScroller = new Scroller(this);
        headView.setmScroller(mScroller);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            if (mTouchmanger == null) {
                mTouchmanger = new PersonalTouchManger(this, mRootView, headView, mFloatView, mScroller).init();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mTouchmanger != null) {
            if (mTouchmanger.onEvent(ev)) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setNodeName() {

    }
}
