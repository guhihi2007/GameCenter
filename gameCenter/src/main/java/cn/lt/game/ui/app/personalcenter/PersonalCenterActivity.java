package cn.lt.game.ui.app.personalcenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragmentActivity;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.ui.app.personalcenter.info.PersonalHomePageFragment;
import cn.lt.game.ui.app.personalcenter.info.PersonalInfoFragment;
import cn.lt.game.ui.app.personalcenter.login.LoginFragment;
import cn.lt.game.ui.app.personalcenter.model.ActionBarSetting;
import cn.lt.game.ui.app.personalcenter.pw.ModifyPWFragment;
import cn.lt.game.ui.app.personalcenter.register.PhoneRegisterFragment;

public class PersonalCenterActivity extends BaseFragmentActivity {

    private ClickListener clickListener = new ClickListener();
    private FragmentManager fm;
    private Button btnNext;
    private Fragment fragment;
    private FragmentTransaction transaction;
    private TextView tvTitle;
    private ImageView btnBack;
    private String fragmentTag;
    private ImageButton btnSetting;
    //关闭侧边栏，去掉侧边栏后可以去除该属性及相关代码
    private boolean sidebarOff;
    private String callBackKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        findView();
        setDefaultFragment();
    }


    public void setActionBar(ActionBarSetting actionBar) {
        if (actionBar.tvTitleText != 0) {
            tvTitle.setText(actionBar.tvTitleText);
        }
        if (actionBar.btnNextText != 0) {
            btnNext.setVisibility(View.VISIBLE);
            btnNext.setText(actionBar.btnNextText);
        }
        if (actionBar.btnNextBackground != 0) {
            btnNext.setBackgroundResource(actionBar.btnNextBackground);
        }
        if (actionBar.btnNextText == 0 && actionBar.btnNextBackground == 0) {
            setButtonNextInvisible();
        }
        if (actionBar.btnNextClickListener != null) {
            btnNext.setOnClickListener(actionBar.btnNextClickListener);
        }

        btnSetting.setVisibility(actionBar.btnSettingVisibility);
        btnSetting.setOnClickListener(actionBar.btnSettingOnClickListener);
    }

    private void setButtonNextInvisible() {
        btnNext.setText("");
        btnNext.setVisibility(View.GONE);
        btnNext.setOnClickListener(null);
    }

    private void findView() {
        btnNext = (Button) findViewById(R.id.btn_next);
        tvTitle = (TextView) findViewById(R.id.tv_page_title);
        btnSetting = (ImageButton) findViewById(R.id.btn_setting);

        btnBack = (ImageView) findViewById(R.id.btn_page_back);
        btnBack.setOnClickListener(clickListener);
    }

    private void setDefaultFragment() {
        String type = getIntent().getStringExtra("type");
        // 登录之后是否finsh掉登录页面,回到之前的界面
        boolean loginEndIsFinsh = getIntent().getBooleanExtra(
                "loginEndIsFinsh", false);
        sidebarOff = getIntent().getBooleanExtra(
                "sidebarOff", false);
        callBackKey = getIntent().getStringExtra(
                "callBackKey");
        if (type.equals("login")) {
            fragment = new LoginFragment().setLoginEndIsFinsh(loginEndIsFinsh);
            fragmentTag = "LoginFragment";
            addFragment(fragment, fragmentTag);

        } else if (type.equals("register")) {

            fragment = new PhoneRegisterFragment()
                    .setLoginEndIsFinsh(loginEndIsFinsh);
            fragmentTag = "PhoneRegisterFragment";
            addFragment(fragment, fragmentTag);

        } else if (type.equals("personal_homepage")) {

            fragment = new PersonalHomePageFragment();
            fragmentTag = "PersonalHomePageFragment";
            addFragment(fragment, fragmentTag);

        } else if (type.equals("personalInfo_Fragment")) {

            fragment = new PersonalInfoFragment();
            fragmentTag = "PersonalInfoFragment";
            addFragment(fragment, fragmentTag);

        } else if (type.equals("modifyPW_Fragment")) {

            fragment = new ModifyPWFragment();
            fragmentTag = "ModifyPWFragment";
            addFragment(fragment, fragmentTag);

        }
    }

    private void addFragment(Fragment fragment, String tag) {
        fm = getSupportFragmentManager();
        transaction = fm.beginTransaction();

        transaction.add(R.id.content, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    private class ClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_page_back:
                    onBack();
                    break;

                default:
                    break;
            }
        }

    }

    public void onBack() {
        int size = fm.getBackStackEntryCount();
        if (size == 1) {
            if (!TextUtils.isEmpty(callBackKey)) {
                UserInfoManager.instance().removeListening(callBackKey);
            }
            finish();
        } else {
            fm.popBackStack();
        }
        PCNet.hideKeyboard(PersonalCenterActivity.this);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBack();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        List<Fragment> list = getSupportFragmentManager().getFragments();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).onActivityResult(requestCode, resultCode, intent);
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void setNodeName() {
        setmNodeName("");
    }

}
