package cn.lt.game.ui.app.personalcenter.login;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import java.util.List;

import cn.lt.game.R;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.CheckUtil;
import cn.lt.game.lib.util.PassWordKeyListener;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.ui.app.community.widget.AutoCompletePopWindow;
import cn.lt.game.ui.app.personalcenter.DisableCopyPaste;
import cn.lt.game.ui.app.personalcenter.LoginBaseFragment;
import cn.lt.game.ui.app.personalcenter.PCNet;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.model.ActionBarSetting;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;
import cn.lt.game.ui.app.personalcenter.pw.FindPasswordStep1Fragment;
import cn.lt.game.ui.app.personalcenter.register.PhoneRegisterFragment;

public class LoginFragment extends LoginBaseFragment {

    private EditText etUserName;
    private EditText etPassWord;
    private ToggleButton toggleLoginHistory;
    private ImageView mArrow;
    private ImageView mClear;

    private AutoCompletePopWindow autoCompletePopWindow;

    @Override
    protected ActionBarSetting getActionBar() {
        ActionBarSetting bar = new ActionBarSetting();
        bar.tvTitleText = R.string.login_title;
        bar.btnNextClickListener = null;
        return bar;
    }

    @Override
    protected int getFragmentLayoutRes() {
        return R.layout.fragment_login;
    }

    @Override
    protected void initView() {
        setLoginOrRegister(login);
        initForgetPW();
        initRegisterButton();
        initLoginButton();
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void initLoginButton() {
        Button loginButton = (Button) view.findViewById(R.id.log_in);
        loginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String userName = etUserName.getText().toString();
                String passWord = etPassWord.getText().toString();
                if (!CheckUtil.checkLoginInfo(getActivity(), userName, passWord)) {
                    return;
                }
                showLoadingDialog("正在登录");
                PCNet.login(userName, passWord, loginCallBack);
            }
        });
    }

    private void initRegisterButton() {
        Button registerButton = (Button) view.findViewById(R.id.register);
        registerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (RegistCountMgr.getInstance().canRegist()) {
                    LoginFragment.this.replaceFragment(new PhoneRegisterFragment().setLoginEndIsFinsh(loginEndIsFinsh));
                    clearText();
                    PCNet.hideKeyboard(getActivity());
                } else {
                    ToastUtils.showToast(getActivity(), "今天注册太多次了");
                }

            }
        });
    }

    private void initForgetPW() {
        Button forgetPw = (Button) view.findViewById(R.id.forget_password);
        forgetPw.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LoginFragment.this.replaceFragment(new FindPasswordStep1Fragment());
                clearText();
				PCNet.hideKeyboard(getActivity());
            }
        });
    }

    @Override
    protected void findView() {
        etUserName = (EditText) view.findViewById(R.id.input_account);
        etPassWord = (EditText) view.findViewById(R.id.input_password);
        etPassWord.setKeyListener(PassWordKeyListener.getInstance(CheckUtil.checkPassWorld));
        mArrow = (ImageView) view.findViewById(R.id.iv_arrow);
        toggleLoginHistory = (ToggleButton) view.findViewById(R.id.login_arrow);
        mClear = (ImageView) view.findViewById(R.id.iv_clear);
        etUserName.requestFocus();
        DisableCopyPaste.disable(etPassWord);

        List<UserBaseInfo> dataList = UserInfoManager.instance().getHistoryUserInfo();
        if (dataList == null) {
            mArrow.setVisibility(View.GONE);
            toggleLoginHistory.setVisibility(View.GONE);

            // mClear 的margin
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mClear.getLayoutParams();
            params.setMargins(0,0,0,0);
        } else {
            UserNameAdapter adapter = new UserNameAdapter(getActivity(), dataList);
            autoCompletePopWindow = new AutoCompletePopWindow(getActivity(), adapter);

            autoCompletePopWindow.setItemListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    String text = arg0.getAdapter().getItem(arg2).toString();
                    etUserName.setText(text);
                    etUserName.setSelection(text.length());
                    toggleLoginHistory.setChecked(false);
                }
            });

            toggleLoginHistory.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                    if (arg1) {
                        autoCompletePopWindow.setWidth(etUserName.getWidth());
                        autoCompletePopWindow.showAsDropDown(etUserName);
                        mArrow.setImageResource(R.mipmap.drawable_collapse);
                    } else {
                        mArrow.setImageResource(R.mipmap.drawable_expand);
                        autoCompletePopWindow.dismiss();
                    }
                }
            });
        }


        mClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etUserName.setText("");
                mClear.setVisibility(View.GONE);
            }
        });

        etUserName.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (!arg1) {
                    if (autoCompletePopWindow != null && autoCompletePopWindow.isShowing()) {
                        autoCompletePopWindow.dismiss();
                    }
                }
            }
        });

        etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)){
                    if (mClear.getVisibility() == View.GONE) {
                        mClear.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void clearText() {
        etUserName.setText("");
        etPassWord.setText("");
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_PERSONAL_LOGIN);
    }
}
