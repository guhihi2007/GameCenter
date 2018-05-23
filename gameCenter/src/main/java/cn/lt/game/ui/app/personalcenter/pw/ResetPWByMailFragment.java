package cn.lt.game.ui.app.personalcenter.pw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragment;

public class ResetPWByMailFragment extends BaseFragment {
	
	private String eMail;

	public static ResetPWByMailFragment newInstance(String eMail) {
		ResetPWByMailFragment newFragment = new ResetPWByMailFragment();
		Bundle bundle = new Bundle();
		bundle.putString("eMail", eMail);
		newFragment.setArguments(bundle);
		return newFragment;
	}

	@Override
	public void setPageAlias() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		eMail = args.getString("eMail");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.reset_password_by_mail, container, false);
		((TextView)rootView.findViewById(R.id.mail_account)).setText(eMail);
		rootView.findViewById(R.id.btn_finish).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		return rootView;
	}
	
}
