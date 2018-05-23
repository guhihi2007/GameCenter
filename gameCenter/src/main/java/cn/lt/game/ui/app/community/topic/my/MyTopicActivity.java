package cn.lt.game.ui.app.community.topic.my;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragmentActivity;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.ui.app.community.topic.TopicListFragment;

/**
 * 
 * 名称：我的社区话题列表页面
 * 用途：发表的话题、收藏的话题，共用此Activity
 *
 */
public class MyTopicActivity extends BaseFragmentActivity {

	private FragmentManager fm;
	private Fragment fragment;
	private FragmentTransaction transaction;
	private TitleBarView titleBar;
	private int title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_my_community_topic);
		titleBar = (TitleBarView) findViewById(R.id.community_title_bar);
		title = getIntent().getIntExtra("title", 0);
		titleBar.setTitle(title);
		titleBar.setBackHomeVisibility(View.VISIBLE);
		setDefaultFragment();
	}

	private void setDefaultFragment() {
		fragment = TopicListFragment.newInstance(title);
		addFragment(fragment, null);
	}

	private void addFragment(Fragment fragment, String tag) {
		fm = getSupportFragmentManager();
		transaction = fm.beginTransaction();

		transaction.add(R.id.content, fragment, tag);
		transaction.addToBackStack(tag);
		transaction.commit();
	}

	@Override
	public void setNodeName() {
		// TODO Auto-generated method stub
		setmNodeName("");
	}

}
