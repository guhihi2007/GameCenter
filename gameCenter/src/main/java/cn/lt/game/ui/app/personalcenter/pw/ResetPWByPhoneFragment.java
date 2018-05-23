package cn.lt.game.ui.app.personalcenter.pw;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.lib.util.CheckUtil;
import cn.lt.game.lib.util.PassWordKeyListener;
import cn.lt.game.ui.app.personalcenter.DisableCopyPaste;

public class ResetPWByPhoneFragment extends BaseFragment {
	private View view;
	private EditText edOldPwd;
	private EditText edNewPwd;


	@Override
	public void setPageAlias() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.reset_password_by_phone, container, false);
		edOldPwd = (EditText) view.findViewById(R.id.new_pw_text);
		edNewPwd = (EditText) view.findViewById(R.id.new_pw_text2);
		edOldPwd.setKeyListener(PassWordKeyListener.getInstance(CheckUtil.checkPassWorld));
		edNewPwd.setKeyListener(PassWordKeyListener.getInstance(CheckUtil.checkPassWorld));
		view.findViewById(R.id.btn_finish).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragment parentFragment = getParentFragment();
				if (parentFragment != null && parentFragment instanceof FindPasswordStep2Fragment) {
					((FindPasswordStep2Fragment) parentFragment).finishClick();
				}
			}
		});
		DisableCopyPaste.disable(edOldPwd);
		DisableCopyPaste.disable(edNewPwd);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		edOldPwd.requestFocus();
		super.onViewCreated(view, savedInstanceState);
	}
	
	public String getOldPwd() {
		return edOldPwd.getText().toString();
	}
	
	public String getNewPwd() {
		return edNewPwd.getText().toString();
	}

}