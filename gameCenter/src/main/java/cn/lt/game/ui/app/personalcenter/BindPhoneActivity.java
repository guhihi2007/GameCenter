package cn.lt.game.ui.app.personalcenter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.CheckUtil;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;
import cn.lt.game.ui.app.sidebar.LoadingDialog;

public class BindPhoneActivity extends BaseActivity implements View.OnClickListener {

    private ImageView btnBack;
    private EditText etPhoneNumber;
    private EditText etVerifyCode;
    private TextView tvBindPhoneText;
    private TextView btnGetVerifyCode;
    private TextView btnSubmit;

    protected LoadingDialog loadingDialog;

    private long codeTime = 0;
    private int count = 0;

    public final static int BIND_PHONE = 0;
    public final static int MODIFY_PHONE = 1;
    private int type;
    private Handler handler = new Handler(new Handler.Callback() {


        @Override
        public boolean handleMessage(Message msg) {
            checkCode();
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);

        btnBack = (ImageView) findViewById(R.id.btn_page_back);
        type = getIntent().getIntExtra("type", 0);

        etPhoneNumber = (EditText) findViewById(R.id.input_bind_phone);
        etVerifyCode = (EditText) findViewById(R.id.input_verify_code);
        tvBindPhoneText = (TextView) findViewById(R.id.bind_phone_text);
        btnGetVerifyCode = (TextView) findViewById(R.id.btn_verify_code);
        btnSubmit = (TextView) findViewById(R.id.btn_submit);

        if (type == BIND_PHONE) {
            ((TextView) findViewById(R.id.tv_page_title)).setText(R.string.user_center_title_bind_phone);
            tvBindPhoneText.setText(R.string.user_center_bind_phone_text);
            etPhoneNumber.setHint(R.string.user_center_bind_phone_hint);
            btnSubmit.setText(R.string.commit);
        } else if (type == MODIFY_PHONE) {
            ((TextView) findViewById(R.id.tv_page_title)).setText(R.string.user_center_title_modify_phone);
            tvBindPhoneText.setText(R.string.user_center_modify_phone_text);
            etPhoneNumber.setHint(R.string.user_center_modify_phone_hint);
            etPhoneNumber.setText(UserInfoManager.instance().getUserInfo().getMobile());
            etPhoneNumber.setSelection(UserInfoManager.instance().getUserInfo().getMobile().length());
            btnSubmit.setText(R.string.next);
        }

        btnBack.setOnClickListener(this);
        btnGetVerifyCode.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        loadingDialog = new LoadingDialog(this);

        //进入的时候先检查一次，确定验证码时候处于倒计时状态
        checkCode();
    }

    @Override
    public void setPageAlias() {
        if (type == BIND_PHONE) {
            setmPageAlias(Constant.PAGE_PERSONAL_BINDING_PHONE);
        } else if (type == MODIFY_PHONE) {
            setmPageAlias(Constant.PAGE_PERSONAL_CHANGE_PHONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_page_back:
                finish();
                break;
            case R.id.btn_verify_code:
                String phoneNumber = etPhoneNumber.getText().toString();
                String verifyCode = etVerifyCode.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)) {
                    ToastUtils.showToast(getApplicationContext(), "手机号码不能为空");
                    break;
                }
                if (!CheckUtil.isMobileNO(phoneNumber)) {
                    ToastUtils.showToast(getApplicationContext(), "手机号码格式错误");
                    break;
                }
                showLoadingDialog();
                //获取验证码 此处直接使用注册的接口
                int check = 1;
                if (type == BIND_PHONE) {
                    check = 1;
                } else if (type == MODIFY_PHONE) {
                    check = 0;
                }

                count++;
                PCNet.sendCode(phoneNumber, false, check, new WebCallBackToString() {

                    @Override
                    public void onSuccess(String result) {
                        codeTime = System.currentTimeMillis();
                        checkCode();
                        hideLoadingDialog();
                        ToastUtils.showToast(BindPhoneActivity.this, "验证码已经发送");
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error) {
                        //此处验证码发送失败就不再倒计时了
                        codeTime = 0;
                        checkCode();
                        hideLoadingDialog();
                        ToastUtils.showToast(BindPhoneActivity.this, error.getMessage());
                    }
                });
                break;
            case R.id.btn_submit:
                phoneNumber = etPhoneNumber.getText().toString();
                verifyCode = etVerifyCode.getText().toString();
                if (!CheckUtil.checkBindPhone(getApplicationContext(), phoneNumber, verifyCode)) {
                    break;
                }
                showLoadingDialog();
                if (type == BIND_PHONE) {
                    //请求绑定接口
                    PCNet.bindPhone(phoneNumber, verifyCode, new WebCallBackToString() {

                        @Override
                        public void onSuccess(String result) {
                            Log.e("user_center", result);
                            hideLoadingDialog();
                            UserBaseInfo user = UserInfoManager.instance().getUserInfo();
                            user.setMobile(getParam().get("mobile").toString());
                            UserInfoManager.instance().setUserBaseInfo(user, false);
                            ToastUtils.showToast(BindPhoneActivity.this, "绑定成功");
                            BindPhoneActivity.this.finish();
                        }

                        @Override
                        public void onFailure(int statusCode, Throwable error) {
                            hideLoadingDialog();
                            ToastUtils.showToast(BindPhoneActivity.this, error.getMessage());
                        }
                    });
                } else if (type == MODIFY_PHONE) {
                    //请求检查验证码
                    phoneNumber = etPhoneNumber.getText().toString();
                    verifyCode = etVerifyCode.getText().toString();
                    PCNet.checkOldPhone(phoneNumber, verifyCode, new WebCallBackToString() {
                        @Override
                        public void onSuccess(String result) {
                            // 修改手机号码 校验完成验证码时
                            hideLoadingDialog();
                            tvBindPhoneText.setText(R.string.user_center_change_phone);
                            etPhoneNumber.setHint(R.string.user_center_bind_phone_hint);
                            btnSubmit.setText(R.string.finish);
                            etPhoneNumber.setText("");
                            etVerifyCode.setText("");
                            ToastUtils.showToast(BindPhoneActivity.this, R.string.user_center_check_phone_code);
                            type = BIND_PHONE;

                            codeTime = 0;
                            count = 0;
                            checkCode();
                        }

                        @Override
                        public void onFailure(int statusCode, Throwable error) {
                            hideLoadingDialog();
                            ToastUtils.showToast(BindPhoneActivity.this, error.getMessage());
                        }
                    });
                }
                break;
        }
    }

    /**
     * 检查验证码是否可以继续发送
     */
    private void checkCode() {
        if (!CheckUtil.checkCode(codeTime, btnGetVerifyCode, count)) {
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    private void showLoadingDialog() {
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        loadingDialog.hide();
    }
}
