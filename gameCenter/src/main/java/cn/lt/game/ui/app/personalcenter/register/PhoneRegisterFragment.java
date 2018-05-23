package cn.lt.game.ui.app.personalcenter.register;

import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.CheckUtil;
import cn.lt.game.lib.util.PassWordKeyListener;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.ui.app.personalcenter.DisableCopyPaste;
import cn.lt.game.ui.app.personalcenter.LoginBaseFragment;
import cn.lt.game.ui.app.personalcenter.PCNet;
import cn.lt.game.ui.app.personalcenter.TermsActivity;
import cn.lt.game.ui.app.personalcenter.login.RegistCountMgr;
import cn.lt.game.ui.app.personalcenter.model.ActionBarSetting;

public class PhoneRegisterFragment extends LoginBaseFragment {
    private EditText phoneNum;
    private EditText password;
    private EditText verifyCode;

    private TextView btnSendVerifyCode;
    private Button btnRegister;
    private ImageView ivClear;

    private TextView registerText;

    private long codeTime = 0;
    private int count = 0;

    private Handler handler = new Handler(new Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            checkCode();
            return false;
        }
    });

    @Override
    protected ActionBarSetting getActionBar() {
        ActionBarSetting bar = new ActionBarSetting();
        bar.tvTitleText = R.string.phone_register;
        return bar;
    }

    @Override
    protected int getFragmentLayoutRes() {
        return R.layout.fragment_phone_register;
    }

    @Override
    protected void initView() {
        setLoginOrRegister(register);
        initRegisterButton();
        initVerifyCodeButton();

        String str = getResources().getString(R.string.register_text);
        SpannableStringBuilder style = new SpannableStringBuilder(str);

        int start = str.substring(0, str.indexOf("《")).length();
        int end = str.substring(0, str.indexOf("》") + 1).length();
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.theme_green));
        style.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        registerText.setText(style);
        registerText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                getActivity().startActivity(new Intent(getActivity(), TermsActivity.class));
            }
        });
        PCNet.showKeyboard(mActivity);
    }

    private void initRegisterButton() {
        btnRegister = (Button) view.findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final String userName = phoneNum.getText().toString();
                final String passWord = password.getText().toString();
                String code = verifyCode.getText().toString();
                if (!CheckUtil.checkPhoneRegisterInfo(getActivity(), userName, passWord, code)) {
                    return;
                }
                showLoadingDialog("正在提交");
                PCNet.register(userName, passWord, code, new WebCallBackToString() {

                    @Override
                    public void onSuccess(String result) {
                        PCNet.login(userName, passWord, loginCallBack);
                        RegistCountMgr.getInstance().saveRegistCount();
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error) {
                        ToastUtils.showToast(getActivity(), error.getMessage());
                        hideLoadingDialog();
                    }
                });
            }
        });
    }

    private void initVerifyCodeButton() {
        btnSendVerifyCode = (TextView) view.findViewById(R.id.get_verify_code);
        checkCode();
        btnSendVerifyCode.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String phone = phoneNum.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showToast(getActivity(), "手机号码不能为空");
                    return;
                } else if (!CheckUtil.isMobileNO(phone)) {
                    ToastUtils.showToast(getActivity(), "手机号码格式不正确");
                    return;
                }
                showLoadingDialog("正在提交");
                count++;
                PCNet.sendCode(phoneNum.getText().toString(), false, 1, new WebCallBackToString() {

                    @Override
                    public void onSuccess(String result) {
                        codeTime = System.currentTimeMillis();
                        checkCode();
                        hideLoadingDialog();
                        ToastUtils.showToast(getActivity(), "验证码已经发送");
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error) {
                        codeTime = 0;
                        checkCode();
                        hideLoadingDialog();
                        ToastUtils.showToast(getActivity(), error.getMessage());
                    }
                });
            }
        });
    }

    @Override
    protected void findView() {
        phoneNum = (EditText) view.findViewById(R.id.input_phone);
        ivClear = (ImageView) view.findViewById(R.id.iv_clear);
        password = (EditText) view.findViewById(R.id.input_password);
        password.setKeyListener(PassWordKeyListener.getInstance(CheckUtil.checkPassWorld));
        DisableCopyPaste.disable(password);
        verifyCode = (EditText) view.findViewById(R.id.verify_code);
        registerText = (TextView) view.findViewById(R.id.register_text);
        phoneNum.requestFocus();

        ivClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNum.setText("");
            }
        });
        phoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ivClear.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 检查验证码是否可以继续发送
     */
    private void checkCode() {
        if (!CheckUtil.checkCode(codeTime, btnSendVerifyCode, count)) {
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_PERSONAL_REGISTER_PHONE);
    }
}
