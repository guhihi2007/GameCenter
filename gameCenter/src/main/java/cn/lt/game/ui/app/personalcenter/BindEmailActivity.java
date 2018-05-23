package cn.lt.game.ui.app.personalcenter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.CheckUtil;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.ui.app.sidebar.LoadingDialog;

public class BindEmailActivity extends BaseActivity implements View.OnClickListener {

    private ImageView btnBack;
    private EditText etEmailNumber;
    private TextView btnSubmit;

    private TextView tvTextBindEmail;
    private LinearLayout llShowBindEmail;
    private TextView tvShowBindEmail;

    protected LoadingDialog loadingDialog;

    public final static int BIND_EMAIL = 0;
    public final static int MODIFY_EMAIL = 1;

    private int count = 0;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_email);

        btnBack = (ImageView) findViewById(R.id.btn_page_back);
        type = getIntent().getIntExtra("type", 0);

        etEmailNumber = (EditText) findViewById(R.id.input_bind_eamil);
        btnSubmit = (TextView) findViewById(R.id.btn_submit);

        tvTextBindEmail = (TextView) findViewById(R.id.text_bind_email);
        llShowBindEmail = (LinearLayout) findViewById(R.id.show_bind_email_layout);
        tvShowBindEmail = (TextView) findViewById(R.id.show_bind_email);

        if (type == BIND_EMAIL) {
            tvTextBindEmail.setText(getResources().getString(R.string.user_center_bind_email_text));
            ((TextView) findViewById(R.id.tv_page_title)).setText(R.string.user_center_title_bind_email);
            llShowBindEmail.setVisibility(View.GONE);
        } else if (type == MODIFY_EMAIL) {
            tvTextBindEmail.setText(getResources().getString(R.string.user_center_modify_email_text));
            ((TextView) findViewById(R.id.tv_page_title)).setText(R.string.user_center_title_modify_email);
            tvShowBindEmail.setText(UserInfoManager.instance().getUserInfo().getEmail());
            llShowBindEmail.setVisibility(View.VISIBLE);
        }

        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        loadingDialog = new LoadingDialog(this);
        count = 0;
    }

    @Override
    public void setPageAlias() {
        if (type == BIND_EMAIL) {
            setmPageAlias(Constant.PAGE_PERSONAL_BINDING_EMAIL);
        } else if (type == MODIFY_EMAIL) {
            setmPageAlias(Constant.PAGE_PERSONAL_CHANGE_EMAIL);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_page_back:
                finish();
                break;
            case R.id.btn_submit:
                if (count > 0) {
                    finish();
                    break;
                }
                final String emailNumber = etEmailNumber.getText().toString();
                if (!CheckUtil.checkBindEmail(getApplicationContext(), emailNumber)) {
                    break;
                }
                showLoadingDialog();
                //请求绑定接口
                PCNet.bindEmail(emailNumber, new WebCallBackToString() {

                    @Override
                    public void onSuccess(String result) {
                        Log.e("user_center", result);
                        hideLoadingDialog();
                        count++;
                        ToastUtils.showToast(BindEmailActivity.this, "绑定成功");
                        etEmailNumber.setVisibility(View.GONE);
                        tvShowBindEmail.setText(emailNumber);
                        llShowBindEmail.setVisibility(View.VISIBLE);
                        tvTextBindEmail.setText(getResources().getString(R.string.user_center_bind_email_complete));
                        btnSubmit.setText(getResources().getString(R.string.finish));
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error) {
                        hideLoadingDialog();
                        ToastUtils.showToast(BindEmailActivity.this, error.getMessage());
                    }
                });
                break;
        }
    }

    private void showLoadingDialog() {
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        loadingDialog.hide();
    }
}
