package cn.lt.game.ui.app.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragmentActivity;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.view.TitleMoreButton;
import cn.lt.game.ui.app.HomeActivity;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.sidebar.feedback.FeedBackActivity;

/***
 * 
 * @author ltbl 社区
 * 
 */
public class CommunityActivity extends BaseFragmentActivity{
	private ViewPager pager;
	private CommunityAdapter adapter;
	private TitleBarView titleBar;
	private LinearLayout mRoot;
	
    private int mPage = 1;
    
    public static final String COM_PAGE = "page";
    
	public static final int COM_TOPIC_LATEST = 0;

	public static final int COM_GROUP_FOUND= 1;

	public static final int COM_MINE = 2;
	
	public enum CloseType {
		disita, shut
	}

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_community);
		titleBar = (TitleBarView) findViewById(R.id.community_title_bar);
		mRoot = (LinearLayout) findViewById(R.id.root);
		titleBar.setTitle("社区");
		titleBar.setMoreButtonType(TitleMoreButton.MoreButtonType.BackHome);
		getIntentData();
		setAdapter();
		setIndicator();
		pager.setCurrentItem(mPage);
		
	}

	

	private void getIntentData() {
		mPage = getIntent().getIntExtra(COM_PAGE, 0);
		if (mPage < 0 || mPage > 2) {
			mPage = 0;
		}
	}
	
	private void setAdapter() {
		adapter = new CommunityAdapter(getSupportFragmentManager(), this);
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);
		pager.setOffscreenPageLimit(adapter.getCount());
	}

	private void setIndicator() {
		SlidingTabLayout indicator = (SlidingTabLayout) findViewById(R.id.tab_indicator);
		indicator.setViewPager(pager);

	}

	public void abnormalDisplay(CloseType type) {
		mRoot.removeView(pager);
		LayoutInflater.from(this).inflate(R.layout.disita_and_shut, mRoot);
		TextView tv = (TextView) mRoot.findViewById(R.id.tv_text);
		TextView bt = (TextView) mRoot.findViewById(R.id.tv_back_to_home);
		bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CommunityActivity.this, FeedBackActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		switch (type) {
		case disita:
			tv.setText("您已被封禁，该操作无法进行");
			bt.setText("联系管理员");
			tv.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.empty_data_img), null, null);
			bt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(CommunityActivity.this, FeedBackActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			});
			break;
		case shut:
			tv.setText("社区在维护中，暂停访问哦");
			tv.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.network_error), null, null);
			bt.setText("返回游戏中心");
			bt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(CommunityActivity.this, HomeActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			});
			break;
		}
	}

	@Override
	public void setNodeName() {
		// TODO Auto-generated method stub
		setmNodeName("");
	}

	private int getUserId() {
		if(null != UserInfoManager.instance().getUserInfo()) {
			return UserInfoManager.instance().getUserInfo().getId();
		} else {
			return 0;
		}
	}
}
