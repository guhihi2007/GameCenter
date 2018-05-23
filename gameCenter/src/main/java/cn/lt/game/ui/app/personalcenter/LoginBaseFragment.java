package cn.lt.game.ui.app.personalcenter;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.lt.game.application.GlobalParams;
import cn.lt.game.global.LogTAG;
import cn.lt.game.bean.SyncPointsBean;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.UtilsProcessData;
import cn.lt.game.lib.util.file.SyncPointsUtil;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.app.personalcenter.info.SetNickNameFragment;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;


public abstract class LoginBaseFragment extends BaseFragment {

    //登录成功后是否关闭登录的activity
    protected boolean loginEndIsFinsh = false;

    private int loginOrRegister = 0;//0：登录；1：注册

    public final int login = 0;
    public final int register = 1;

    protected WebCallBackToString loginCallBack = new WebCallBackToString() {

        @Override
        public void onSuccess(String result) {
            System.out.println(" 登陆 " + result);
            UserBaseInfo info = null;
            try {
                String data = new JSONObject(result).optString("data", "");
                info = UtilsProcessData.jsonTobean(data, UserBaseInfo.class);
                GlobalParams.token = info.getToken();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            info.setUserName((String) getParam().get("username"));
            UserInfoManager.instance().userLogin(info);
            UserInfoManager.instance().addHistoryUserInfo(info);
            getFragmentManager().popBackStack();
            hideLoadingDialog();
            if (loginEndIsFinsh) {
                syncPoints();
                getActivity().finish();
                ToastUtils.showToast(getActivity(), "登录成功");
            } else {
                if (loginOrRegister == 0) {
                    getActivity().finish();
                } else if (loginOrRegister == 1) {
                    syncPoints();
                    getFragmentManager().popBackStack();
                    LoginBaseFragment.this.replaceFragment(new SetNickNameFragment());
                    ToastUtils.showToast(getActivity(), "注册成功");
                }
            }
        }

        @Override
        public void onFailure(int statusCode, Throwable error) {

            System.out.println("  登陆失败  " + statusCode + "  error" + error);
            if (getActivity() != null) {

                ToastUtils.showToast(getActivity(), error.getMessage());
                hideLoadingDialog();
            }

        }
    };


    public BaseFragment setLoginEndIsFinsh(boolean loginEndIsFinsh) {
        this.loginEndIsFinsh = loginEndIsFinsh;
        return this;
    }


    public void setLoginOrRegister(int loginOrRegister) {
        this.loginOrRegister = loginOrRegister;
    }

    /**
     * 同步积分
     */
    public void syncPoints() {
        Map<String, String> params = new HashMap<>();
        String downloadJson = SyncPointsUtil.getLocalPointsListJson();
        if (TextUtils.isEmpty(downloadJson)) {
            return;
        }
        params.put("download", downloadJson);
        Net.instance().executePost(Host.HostType.GCENTER_HOST, Uri2.POINTS_SYNC, params, new WebCallBackToString() {

            @Override
            public void onSuccess(String result) {
                LogUtils.d(LogTAG.HTAG, "同步积分成功" + result);
                UserInfoManager.instance().notifyDataSetChanged();
                SyncPointsUtil.setSyncPointsList(new ArrayList<SyncPointsBean>());
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.d(LogTAG.HTAG, String.valueOf(statusCode));
            }
        });
    }


}
