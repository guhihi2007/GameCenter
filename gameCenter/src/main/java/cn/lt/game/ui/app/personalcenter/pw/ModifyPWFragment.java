package cn.lt.game.ui.app.personalcenter.pw;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import cn.lt.game.R;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.CheckUtil;
import cn.lt.game.lib.util.PassWordKeyListener;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.ui.app.personalcenter.BaseFragment;
import cn.lt.game.ui.app.personalcenter.DisableCopyPaste;
import cn.lt.game.ui.app.personalcenter.PCNet;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.model.ActionBarSetting;

public class ModifyPWFragment extends BaseFragment implements OnClickListener {
	
	private EditText oldPW;
	private EditText newPW1;
	private EditText newPW2;

	@Override
	protected ActionBarSetting getActionBar() {
		ActionBarSetting bar = new ActionBarSetting();
		bar.tvTitleText = R.string.modify_password;
		return bar;
	}

	@Override
	protected int getFragmentLayoutRes() {
		return R.layout.fragment_modify_pw;
	}

	@Override
	protected void initView() {
		
		oldPW = (EditText)view.findViewById(R.id.old_pw_text);
		oldPW.setKeyListener(PassWordKeyListener.getInstance(CheckUtil.checkPassWorld));
		oldPW.setVisibility(View.VISIBLE);
		DisableCopyPaste.disable(oldPW);
	}

	@Override
	protected void findView() {
		newPW1 = (EditText)view.findViewById(R.id.new_pw_text);
		newPW1.setKeyListener(PassWordKeyListener.getInstance(CheckUtil.checkPassWorld));
		DisableCopyPaste.disable(newPW1);
		newPW2 = (EditText)view.findViewById(R.id.new_pw_text2);
		newPW2.setKeyListener(PassWordKeyListener.getInstance(CheckUtil.checkPassWorld));
		DisableCopyPaste.disable(newPW2);

		view.findViewById(R.id.btn_finish).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		String sOldPW = oldPW.getText().toString();
		String sNewPW1 = newPW1.getText().toString();
		String sNewPW2 = newPW2.getText().toString();
		if(!CheckUtil.checkModifyPassWordInfo(getActivity(), sOldPW, sNewPW1, sNewPW2)){
			return;
		}
		showLoadingDialog();
		PCNet.modifyPwd(sOldPW, sNewPW1, null, new WebCallBackToString() {
			
			@Override
			public void onFailure(int statusCode, Throwable error) {
				hideLoadingDialog();
				ToastUtils.showToast(getActivity().getApplicationContext(), error.getMessage());
			}
			
			@Override
			public void onSuccess(String result) {
				//密码修改成功
				hideLoadingDialog();
				getActivity().finish();
				ToastUtils.showToast(getActivity().getApplicationContext(), "密码修改成功");
				UserInfoManager.instance().userLogout(false);
			}
		});
	}

	@Override
	public void setPageAlias() {
		setmPageAlias(Constant.PAGE_PERSONAL_CHANGE_PASSWORD);
	}
}
