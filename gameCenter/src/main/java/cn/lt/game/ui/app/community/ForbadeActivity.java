package cn.lt.game.ui.app.community;

import android.os.Bundle;
import android.view.View;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.view.TitleMoreButton.MoreButtonType;

/***
 * 禁言/社区失窃/社区无内容公用页面
 * 
 * @author tiantian
 * @des
 */

public class ForbadeActivity extends BaseActivity {
	private NetWorkStateView networkView;
	private TitleBarView titleBar;
	private String type;
	public  enum IntentType{
		forbid("forbid"),nodata("nodata");
		public String type;
		IntentType(String type){
			this.type = type;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forbade);
		type = getIntent().getStringExtra("type");
		initView();
	}

	private void initView() {
		networkView = (NetWorkStateView) findViewById(R.id.forbade_netwrolStateView);
		titleBar = (TitleBarView) findViewById(R.id.forbade_title_bar);
		titleBar.setBackHomeVisibility(View.VISIBLE);
		titleBar.setTitle("社区");
		networkView.showNetworkNoDataLayout();
		titleBar.setMoreButtonType(MoreButtonType.BackHome);
		if ("forbid".equals(IntentType.forbid.type)) {
			networkView.setNotDataState(NetWorkStateView.gotoFeedback);
			networkView.setNoDataLayoutText("啊哦...您暂时无法进行该操作呢", "联系管理员");
		}else{
			networkView.setNoDataLayoutText("该页面暂时没有内容", "");
		}
	}

	@Override
	public void setPageAlias() {

	}
}
