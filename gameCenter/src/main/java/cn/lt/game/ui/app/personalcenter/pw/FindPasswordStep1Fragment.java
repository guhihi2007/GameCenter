package cn.lt.game.ui.app.personalcenter.pw;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import cn.lt.game.R;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.CheckUtil;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.ui.app.personalcenter.BaseFragment;
import cn.lt.game.ui.app.personalcenter.PCNet;
import cn.lt.game.ui.app.personalcenter.model.AccountType;
import cn.lt.game.ui.app.personalcenter.model.ActionBarSetting;
import cn.lt.game.ui.app.sidebar.AboutActivity;

public class FindPasswordStep1Fragment extends BaseFragment {

    private View lineInMail;
    private EditText inputMail;
    private View lineInPhone;
    private EditText inputPhoneNumber;
    private Button getVerifyCode;
    private EditText inputVerifyCode;
    private ImageView choosePhoneIV, chooseMailIV;
    private Button btnContact;
    private LinearLayout choosePhone, chooseMail;
    private AccountType accountType = AccountType.phone;
    private ClickListener clickListener = new ClickListener();

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
        bar.tvTitleText = R.string.find_password;
        return bar;
    }

    @Override
    protected int getFragmentLayoutRes() {
        return R.layout.fragment_find_pswd_s1;
    }

    @Override
    protected void findView() {
        // mail
        lineInMail = view.findViewById(R.id.line_in_mail);
        inputMail = (EditText) view.findViewById(R.id.input_mail);
        chooseMailIV = (ImageView) view.findViewById(R.id.choose_mail_check);
        chooseMail = (LinearLayout) view.findViewById(R.id.choose_mail);
        chooseMail.setOnClickListener(clickListener);
        chooseMailIV.setOnClickListener(clickListener);

        // phone
        lineInPhone = view.findViewById(R.id.line_in_phone);
        inputPhoneNumber = (EditText) view.findViewById(R.id.input_phone_number);
        getVerifyCode = (Button) view.findViewById(R.id.get_verify_code);
        checkCode();
        getVerifyCode.setOnClickListener(clickListener);
        inputVerifyCode = (EditText) view.findViewById(R.id.input_verify_code);
        choosePhoneIV = (ImageView) view.findViewById(R.id.choose_phone_check);
        choosePhone = (LinearLayout) view.findViewById(R.id.choose_phone);
        choosePhone.setOnClickListener(clickListener);
        choosePhoneIV.setOnClickListener(clickListener);

        btnContact = (Button) view.findViewById(R.id.contact);
        btnContact.setOnClickListener(clickListener);
        view.findViewById(R.id.btn_next_step).setOnClickListener(clickListener);
        PCNet.showKeyboard(mActivity);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        inputPhoneNumber.requestFocus();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void initView() {

    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_PERSONAL_FIND_PASSWORD);
    }

    public class ClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.choose_phone:
                case R.id.choose_phone_check:
                    onPhoneCheckClick();
                    break;

                case R.id.choose_mail:
                case R.id.choose_mail_check:
                    onMailCheckClick();
                    break;

                case R.id.btn_next_step:
                    startStep2Fragment();
                    break;

                case R.id.get_verify_code:
                    fetchVerifyCode();
                    break;

                case R.id.contact:
                    getActivity().startActivity(new Intent(getActivity(),AboutActivity.class));
                    break;
                default:
                    break;
            }

        }

        private void onMailCheckClick() {
            chooseMailIV.setImageResource(R.mipmap.check);
            choosePhoneIV.setImageResource(R.mipmap.uncheck);
            expandMailArea();
            accountType = AccountType.mail;
        }

        private void onPhoneCheckClick() {
            choosePhoneIV.setImageResource(R.mipmap.check);
            chooseMailIV.setImageResource(R.mipmap.uncheck);
            expandPhoneArea();
            accountType = AccountType.phone;
        }

        private void fetchVerifyCode() {
            if (TextUtils.isEmpty(inputPhoneNumber.getText().toString())) {
                ToastUtils.showToast(getActivity(), "请输入手机号码");
                return;
            }
            showLoadingDialog();
            count++;
            PCNet.sendCode(inputPhoneNumber.getText().toString(), true, 0, new WebCallBackToString() {

                @Override
                public void onSuccess(String result) {
                    hideLoadingDialog();
                    codeTime = System.currentTimeMillis();
                    checkCode();
                    ToastUtils.showToast(getActivity(), "验证码已经发送");
                }

                @Override
                public void onFailure(int statusCode, Throwable error) {
                    //此处验证码发送失败就不再倒计时了
                    codeTime = 0;
                    checkCode();
                    hideLoadingDialog();
                    ToastUtils.showToast(getActivity(), error.getMessage().toString());
                    Log.e("error", error.getMessage());
                }
            });
        }

        private void startStep2Fragment() {
            if (accountType == AccountType.phone) {
                if (TextUtils.isEmpty(inputPhoneNumber.getText().toString()) || TextUtils.isEmpty(inputVerifyCode.getText().toString())) {
                    ToastUtils.showToast(getActivity(), "手机号码或验证码为空");
                    return;
                }
                showLoadingDialog();
                PCNet.findPwdCheck(inputPhoneNumber.getText().toString(), inputVerifyCode.getText().toString(), new WebCallBackToString() {

                    @Override
                    public void onFailure(int statusCode, Throwable error) {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onSuccess(String result) {
                        hideLoadingDialog();
                        String code = "";
                        try {
                            JSONObject jsonObj = new JSONObject(result);
                            jsonObj = jsonObj.optJSONObject("data");
                            if (jsonObj != null) {
                                code = jsonObj.optString("resetCode", "");
                            }
                        } catch (JSONException e) {
                        }
                        FindPasswordStep2Fragment findPwS2 = new FindPasswordStep2Fragment();
                        findPwS2.setAccountType(accountType);
                        findPwS2.setCode(code);
                        replaceFragment(findPwS2);
                        Log.v("findpw", result);
                    }
                });
            } else {
                if (TextUtils.isEmpty(inputMail.getText())) {
                    ToastUtils.showToast(getActivity(), "请输入邮箱");
                    return;
                }
                if (!CheckUtil.isEmail(inputMail.getText().toString())) {
                    ToastUtils.showToast(getActivity(), "请输入正确的邮箱地址！");
                    return;
                }
                showLoadingDialog();
                PCNet.findPwdbyMail(inputMail.getText().toString(), new WebCallBackToString() {

                    @Override
                    public void onFailure(int statusCode, Throwable error) {
                        hideLoadingDialog();
                        ToastUtils.showToast(getActivity(), error.getMessage());
                    }

                    @Override
                    public void onSuccess(String result) {
                        hideLoadingDialog();
                        FindPasswordStep2Fragment findPwS2 = new FindPasswordStep2Fragment();
                        findPwS2.setAccountType(accountType);
                        findPwS2.seteMail(inputMail.getText().toString());
                        replaceFragment(findPwS2);
                        Log.v("findpw", result);
                    }
                });
            }
        }

        private void expandMailArea() {
            lineInMail.setVisibility(View.VISIBLE);
            inputMail.setVisibility(View.VISIBLE);
            chooseMailIV.setVisibility(View.VISIBLE);
            btnContact.setVisibility(View.VISIBLE);

            foldPhoneArea();
        }

        private void foldPhoneArea() {
            lineInPhone.setVisibility(View.GONE);
            inputPhoneNumber.setVisibility(View.GONE);
            getVerifyCode.setVisibility(View.GONE);
            inputVerifyCode.setVisibility(View.GONE);
        }

        private void expandPhoneArea() {
            lineInPhone.setVisibility(View.VISIBLE);
            inputPhoneNumber.setVisibility(View.VISIBLE);
            getVerifyCode.setVisibility(View.VISIBLE);
            inputVerifyCode.setVisibility(View.VISIBLE);

            foldMailArea();
        }

        private void foldMailArea() {
            lineInMail.setVisibility(View.GONE);
            inputMail.setVisibility(View.GONE);
            btnContact.setVisibility(View.GONE);
        }

    }

    /**
     * 检查验证码是否可以继续发送
     */
    private void checkCode() {
        if (!CheckUtil.checkCode(codeTime, getVerifyCode, count)) {
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    }
}
