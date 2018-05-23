package cn.lt.game.ui.app.personalcenter.info;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.lib.widget.MessageDialog.LeftBtnClickListener;
import cn.lt.game.lib.widget.MessageDialog.RightBtnClickListener;
import cn.lt.game.ui.app.community.CommunityActivity;
import cn.lt.game.ui.app.gamegift.GiftHomeActivity;
import cn.lt.game.ui.app.personalcenter.BaseFragment;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.login.LoginFragment;
import cn.lt.game.ui.app.personalcenter.model.ActionBarSetting;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;
import cn.lt.game.ui.app.personalcenter.pw.ModifyPWFragment;
import cn.lt.game.ui.common.ClickListenerSetter;

public class PersonalHomePageFragment extends BaseFragment implements
		UserInfoUpdateListening {
	private ImageView ivUserPhoto;
	private TextView tvUserName;
	private TextView tvUserId;

	@Override
	protected ActionBarSetting getActionBar() {
		ActionBarSetting bar = new ActionBarSetting();
		bar.tvTitleText = R.string.personal_homepage;
		bar.btnSettingVisibility = View.VISIBLE;
		bar.btnSettingOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			}
		};
		return bar;
	}

	@Override
	protected int getFragmentLayoutRes() {
		return R.layout.fragment_personal_homepage;
	}

	@Override
	protected void initView() {
		initModifyPWBtn();
		initChangeAccountBtn();
		initEditPersonalInfoBtn();
		initMyGame();
		initMyGiftBtn();
		initMyCommunity();
		iniQuitBtn();
		UserInfoManager.instance().addListening(this);
		
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	protected void findView() {
		ivUserPhoto = (ImageView) view.findViewById(R.id.iv_user_photo);
		tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
		tvUserId = (TextView) view.findViewById(R.id.tv_user_id);
	}

	private void initMyGame() {
		ClickListenerSetter.set(view, R.id.my_game, new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
	}


	private void iniQuitBtn() {
		ClickListenerSetter.set(view, R.id.quit, new OnClickListener() {

			@Override
			public void onClick(View v) {
				final MessageDialog exitDialog = new MessageDialog(
						getActivity(), "退出登录", "确定退出当前账号吗？", "取消", "确定");
				exitDialog.setRightOnClickListener(new RightBtnClickListener() {

					@Override
					public void OnClick(View view) {
						UserInfoManager.instance().userLogout(false);
						exitDialog.cancel();
					}
				});
				exitDialog.setLeftOnClickListener(new LeftBtnClickListener() {

					@Override
					public void OnClick(View view) {
						exitDialog.cancel();
					}
				});
				exitDialog.show();

			}
		});
	}

	private void initModifyPWBtn() {
		ClickListenerSetter.set(view, R.id.btn_modify_password,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						replaceFragment(new ModifyPWFragment());

					}
				});
	}

	private void initChangeAccountBtn() {
		ClickListenerSetter.set(view, R.id.btn_change_account,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						replaceFragment(new LoginFragment());

					}
				});
	}

	private void initMyGiftBtn() {
		ClickListenerSetter.set(view, R.id.my_gift, new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityActionUtils.jumpToGift(getActivity(),
						GiftHomeActivity.GIFT_MINE);

			}
		});
	}

	private void initMyCommunity() {
		ClickListenerSetter.set(view, R.id.my_community, new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityActionUtils.jumpToCommunity(getActivity(),
						CommunityActivity.COM_MINE);
			}
		});
	}

	private void initEditPersonalInfoBtn() {
		ClickListenerSetter.set(view, R.id.iv_edit, new OnClickListener() {

			@Override
			public void onClick(View v) {
				replaceFragment(new PersonalInfoFragment());
			}
		});
	}

	@Override
	public void updateUserInfo(UserBaseInfo userBaseInfo) {
		tvUserName.setText(userBaseInfo.getNickname());
		tvUserId.setText("ID:" + userBaseInfo.getId());
		ImageloaderUtil.loadUserHead(getActivity(),userBaseInfo.getAvatar(), ivUserPhoto);
	}

	@Override
	public void setPageAlias() {

	}


	@Override
	public void userLogout() {
		getActivity().finish();
	}

	@Override
	public void userLogin(UserBaseInfo userBaseInfo) {
	}
	

}
