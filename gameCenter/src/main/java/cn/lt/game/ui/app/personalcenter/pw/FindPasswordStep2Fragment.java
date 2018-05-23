package cn.lt.game.ui.app.personalcenter.pw;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.lt.game.R;
import cn.lt.game.application.GlobalParams;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.CheckUtil;
import cn.lt.game.lib.util.FromPageManager;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.net.Net;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsDataProductorImpl;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.ui.app.personalcenter.BaseFragment;
import cn.lt.game.ui.app.personalcenter.PCNet;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.model.AccountType;
import cn.lt.game.ui.app.personalcenter.model.ActionBarSetting;

public class FindPasswordStep2Fragment extends BaseFragment {
    private Fragment step2Content;
    private AccountType accountType = AccountType.phone;
    private String code;
    private String eMail;

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType type) {
        this.accountType = type;
    }

    @Override
    public void onDestroyView() {
        try {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.remove(step2Content);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            super.onDestroyView();
        }
    }

    @Override
    protected ActionBarSetting getActionBar() {
        ActionBarSetting bar = new ActionBarSetting();
        bar.tvTitleText = R.string.find_password;
        return bar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //单独上报这个页面,这样不会引发其他问题  修复bug(找回密码-手机、找回密码-邮箱两个页面没有上报)
        String page = Constant.PAGE_PERSONAL_FIND_PASSWORD_PHONE;
        if (accountType != AccountType.phone) {
            page = Constant.PAGE_PERSONAL_FIND_PASSWORD_EMAIL;
        }
        StatisticsEventData event = StatisticsDataProductorImpl.produceStatisticsData(null, 0, 0, mEventID, page, ReportEvent.ACTION_PAGEJUMP, null, null, null, "");
        FromPageManager.pageJumpReport(event);
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    protected int getFragmentLayoutRes() {
        return R.layout.fragment_find_pswd_s2;
    }

    @Override
    protected void findView() {

    }

    @Override
    protected void initView() {
        setStep2Content();
    }

    private void setStep2Content() {
        switch (getAccountType()) {
            case phone:
                setPhoneContent();
                break;

            case mail:
                setMailContent();
                break;

            default:
                break;
        }

    }

    private void setPhoneContent() {
        step2Content = new ResetPWByPhoneFragment();
        replaceChildeFragment(step2Content);
    }

    private void setMailContent() {
        step2Content = ResetPWByMailFragment.newInstance(eMail);
        replaceChildeFragment(step2Content);
    }

    private void replaceChildeFragment(Fragment fragment) {
        FragmentManager childFm = getChildFragmentManager();
        FragmentTransaction childTrs = childFm.beginTransaction();
        childTrs.replace(R.id.step2_content, fragment);
        childTrs.commit();
    }

    @Override
    public void setPageAlias() {
    }


    public void finishClick() {
        if (accountType == AccountType.phone) {
            String oldPwd = ((ResetPWByPhoneFragment) step2Content).getOldPwd();
            String newPwd = ((ResetPWByPhoneFragment) step2Content).getNewPwd();
            if (!CheckUtil.checkFindPassWord(getActivity(), oldPwd, newPwd)) {
                return;
            }
            showLoadingDialog();
            PCNet.modifyPwd(null, newPwd, code, new WebCallBackToString() {

                @Override
                public void onFailure(int statusCode, Throwable error) {
                    hideLoadingDialog();
                    Log.v("modifyPwd", error.getMessage());
                    ToastUtils.showToast(getActivity().getApplicationContext(), error.getMessage());
                }

                @Override
                public void onSuccess(String result) {
                    hideLoadingDialog();
                    //密码修改成功后还原这部分
                    ToastUtils.showToast(getActivity().getApplicationContext(), "密码重置成功");
                    GlobalParams.clearToken();
                    Net.instance().setUcenterSalt(null);
                    UserInfoManager.instance().userLogout(false);
                    returnToLogin();
                }
            });
        } else {
            returnToLogin();
        }
    }

    private void returnToLogin() {
        getActivity().finish();
        Intent intent = UserInfoManager.instance().getLoginIntent(getActivity().getApplicationContext(), false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

}
