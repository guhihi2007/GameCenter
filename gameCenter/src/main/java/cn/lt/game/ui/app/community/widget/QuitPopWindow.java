package cn.lt.game.ui.app.community.widget;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.lib.widget.MessageDialog.LeftBtnClickListener;
import cn.lt.game.lib.widget.MessageDialog.RightBtnClickListener;
import cn.lt.game.net.NetIniCallBack;
import cn.lt.game.ui.app.community.CheckUserRightsTool;
import cn.lt.game.ui.app.community.group.GroupMemberActivity.EventBean;
import de.greenrobot.event.EventBus;

/***
 * 
 * @author tiantian
 * @des 小组成员列表推出小组弹出
 */
public class QuitPopWindow extends PopupWindow {
	/**
	 * 
	 * @param context
	 */
	private int groupId;
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public QuitPopWindow(final Activity context) {
		LinearLayout root1 = new LinearLayout(context);
		root1.setBackgroundColor(Color.TRANSPARENT);
		root1.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams lp1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		root1.setLayoutParams(lp1);

		LinearLayout root = new LinearLayout(context);
		root.setBackgroundResource(R.drawable.quit_background);
		root.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.rightMargin = DensityUtil.dip2px(context,4);
		lp.topMargin =  DensityUtil.dip2px(context,6);
		root.setLayoutParams(lp);
		root1.addView(root);
		TextView tv = new TextView(context);
		tv.setText("   退出小组   ");
		tv.setTextSize(16);
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(Color.parseColor("#333333"));
		android.widget.LinearLayout.LayoutParams lp2 = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
		tv.setLayoutParams(lp2);
		lp2.setMargins(18, 15, 18, 15);

		root.addView(tv);
		// 设置SelectPicPopupWindow的View
		this.setContentView(root1);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 刷新状态
		this.update();
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		this.setBackgroundDrawable(dw);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(android.R.style.Animation_Dialog);
		root.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				final MessageDialog promptDialog = new MessageDialog(context, "提示", "确定退出小组吗？", "取消", "确定");
				promptDialog.setLeftOnClickListener(new LeftBtnClickListener() {
					@Override
					public void OnClick(View view) {
						promptDialog.dismiss();
					}
				});
				promptDialog.setRightOnClickListener(new RightBtnClickListener() {
					@Override
					public void OnClick(View view) {
						promptDialog.dismiss();
						CheckUserRightsTool.instance().quitGroup(context, groupId, new NetIniCallBack() {
							@Override
							public void callback(int code) {
								if (0 == code) {
									ToastUtils.showToast(context, "您已退出该小组！");
									EventBus.getDefault().post(new EventBean("refreshData"));
								} else {
									ToastUtils.showToast(context, "退出小组失败，请稍后重试！");
								}
							}
						});
					}
				});
				promptDialog.show();
			}
		});

	}
	/***
	 * @param parent
	 */
	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			this.showAsDropDown(parent, 0, 0);
		} else {
			this.dismiss();
		}
	}
}
