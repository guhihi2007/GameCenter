package cn.lt.game.ui.app.personalcenter.model;

import android.view.View;
import android.view.View.OnClickListener;

public class ActionBarSetting {
	
	//标题
	public int tvTitleText = 0; 
	
	//下一步按键
	public int btnNextText = 0;
	public int btnNextBackground = 0;
	public OnClickListener btnNextClickListener;
	
	//设置按键
	public int btnSettingVisibility = View.GONE;
	public OnClickListener btnSettingOnClickListener;
}
