package cn.lt.game.ui.app.personalcenter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.lt.game.R.id;
import cn.lt.game.ui.app.personalcenter.model.ActionBarSetting;
import cn.lt.game.ui.app.sidebar.LoadingDialog;

public abstract class BaseFragment extends cn.lt.game.base.BaseFragment {
	
	protected View view;
	protected LoadingDialog loadingDialog;
	
	abstract protected ActionBarSetting getActionBar();
	abstract protected int getFragmentLayoutRes();
	abstract protected void initView();
	abstract protected void findView();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater,container,savedInstanceState);
		view = inflater.inflate(getFragmentLayoutRes(), container,
				false);
		loadingDialog = new LoadingDialog(getActivity());
		findView();
		initView();
		return view;
	}

	protected void replaceFragment(BaseFragment fragment) {
		FragmentManager fManager = getLoginActivity().getSupportFragmentManager();
		FragmentTransaction transaction = fManager.beginTransaction();
		transaction.replace(id.content, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		getLoginActivity().setActionBar(getActionBar());
	}

	protected PersonalCenterActivity getLoginActivity() {
		return (PersonalCenterActivity)getActivity();
	}
	
	protected void showLoadingDialog(){
		loadingDialog.show();
	}
	
	protected void showLoadingDialog(String str){
		loadingDialog.show();
		loadingDialog.getTv().setText(str);
	}
	
	protected void hideLoadingDialog(){
		loadingDialog.hide();
	}
	
}
