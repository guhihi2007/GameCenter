package cn.lt.game.ui.app.personalcenter;

import android.R.string;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.lt.game.R;
import cn.lt.game.application.GlobalParams;
import cn.lt.game.application.MyApplication;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.lib.util.UtilsProcessData;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.lib.widget.MessageDialog.CancelCliclListener;
import cn.lt.game.lib.widget.MessageDialog.LeftBtnClickListener;
import cn.lt.game.lib.widget.MessageDialog.RightBtnClickListener;
import cn.lt.game.net.Net;
import cn.lt.game.statistics.database.dao.UserDao;
import cn.lt.game.statistics.database.provider.NotifyUserInfoToAppCenterMgr;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

public class UserInfoManager {
    private UserBaseInfo userBaseInfo;
    private Map<String, UserInfoUpdateListening> map;

    private final static int historyMaxSize = 5;

    private volatile static UserInfoManager userInfoManager;

    //自增序列，添加匿名回调的时候作为key
    private int seq = 1;

    private UserInfoManager() {
        map = new HashMap<String, UserInfoUpdateListening>();
    }

    public static UserInfoManager instance() {
        if (userInfoManager == null) {
            synchronized (Net.class) {
                if (userInfoManager == null) {
                    userInfoManager = new UserInfoManager();
                }
            }
        }
        return userInfoManager;
    }

    /**
     * 添加用户信息变动的监听
     * 添加的监听是非匿名调用的时候会以类名作为key
     *
     * @param listening
     */
    public String addListening(UserInfoUpdateListening listening) {
        String key = listening.getClass().getSimpleName();
        if ("".equals(key)) {
            key = seq + "";
            seq++;
        }
        map.put(key, listening);
        if (userBaseInfo == null) {
            userBaseInfo = getUserInfo();
        }
        if (userBaseInfo != null) {
            try {
                listening.userLogin(userBaseInfo);
                listening.updateUserInfo(userBaseInfo);
            } catch (Exception e) {
                Log.e("userInfoUpdate", "用户数据刷新的时候捕获到一个错误:" + e.getMessage());
            }
        }
        return key;
    }

    public String addRealListening(UserInfoUpdateListening listening) {
        String key = listening.getClass().getSimpleName();
        if ("".equals(key)) {
            key = seq + "";
            seq++;
        }
        map.put(key, listening);
        return key;
    }

    /**
     * 根据类名移除监听(非匿名调用)
     *
     * @param listening
     */
    public void removeListening(UserInfoUpdateListening listening) {
        removeListening(listening.getClass().getSimpleName());
    }

    /**
     * 根据对应的key移除监听
     *
     * @param key
     */
    public void removeListening(String key) {
        map.remove(key);
    }

    /**
     * 移除全部监听
     */
    public void removeAllListening() {
        map.clear();
    }

    public void userLogin(UserBaseInfo userBaseInfo) {
        setUserBaseInfo(userBaseInfo, false);
        notifyLoginChanged();
    }

    /**
     * 设置用户信息
     *
     * @param userBaseInfo
     * @param isFromToken
     */
    public void setUserBaseInfo(UserBaseInfo userBaseInfo, boolean isFromToken) {
        this.userBaseInfo = userBaseInfo;
        savaUserInfo(userBaseInfo, isFromToken);
        notifyDataSetChanged();
    }

    /**
     * 用户登出
     *
     * @param isFromToken
     */
    public void userLogout(boolean isFromToken) {
        userBaseInfo = null;
        delUserInfo(isFromToken);
        logoutProcess();
        notifyLogoutChanged();
    }

    /**
     * 触发用户登录的监听事件
     */
    private void notifyLoginChanged() {
        synchronized (map) {
            Iterator<Entry<String, UserInfoUpdateListening>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                try {
                    it.next().getValue().userLogin(userBaseInfo);
                } catch (Exception e) {
                    it.remove();
                }
            }
        }
    }

    /**
     * 触发用户信息变动的监听事件
     */
    public void notifyDataSetChanged() {
        synchronized (map) {
            Iterator<Entry<String, UserInfoUpdateListening>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                try {
                    it.next().getValue().updateUserInfo(userBaseInfo);
                } catch (Exception e) {
                    it.remove();
                }
            }
        }
    }

    /**
     * 触发用户登出的监听事件
     */
    private void notifyLogoutChanged() {
        for (Map.Entry<String, UserInfoUpdateListening> entry : map.entrySet()) {
            UserInfoUpdateListening listening = entry.getValue();
            if (listening != null) {
                listening.userLogout();
            }
        }
    }

    /**
     * 把用户信息存在本地
     *
     * @param userBaseInfo
     * @param isFromToken
     */
    private void savaUserInfo(UserBaseInfo userBaseInfo, boolean isFromToken) {
        String re = UtilsProcessData.beanToJson(userBaseInfo);
        SharedPreferencesUtil share = new SharedPreferencesUtil(MyApplication.application.getApplicationContext());
        share.add(SharedPreferencesUtil.userBaseInfoKey, re);
//        share.add(SharedPreferencesUtil.userToKenKey, Net.instance().getToken());

        updateHistoryUserAvatar(userBaseInfo.getId(), userBaseInfo.getAvatar());

        try {
            UserDao.newInstance(MyApplication.application).deleteAll();
            UserDao.newInstance(MyApplication.application).insertSingleData(userBaseInfo);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("UserProvider", "savaUserInfo数据库抛异常=" + e.getMessage());
        }
//        try {
//            if(!isFromToken){
//                NotifyUserInfoToAppCenterMgr.insertIntoAppCenter(MyApplication.application,userBaseInfo);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    /**
     * 删除本地的用户信息
     *
     * @param isFromToken
     */
    private void delUserInfo(boolean isFromToken) {
//        SharedPreferencesUtil share = new SharedPreferencesUtil(MyApplication.application.getApplicationContext());
//        share.delete(SharedPreferencesUtil.userBaseInfoKey);
//        share.delete(SharedPreferencesUtil.userToKenKey);

        try {
            LogUtils.d(LogTAG.USER, "游戏中心：点击退出：" + UserInfoManager.instance().getUserInfo());
            UserDao.newInstance(MyApplication.application).deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            if(!isFromToken){
//                NotifyUserInfoToAppCenterMgr.deleteIntoAppCenter(MyApplication.application);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public UserBaseInfo getUserInfo() {
//        SharedPreferencesUtil share = new SharedPreferencesUtil(MyApplication.application.getApplicationContext());
//        if (Net.instance().getToken() == null) {
//            GlobalParams.token = (share.get(SharedPreferencesUtil.userToKenKey));
//        }
//
//        if (userBaseInfo != null) {
//            return userBaseInfo.clone();
//        }
//        String data = share.get(SharedPreferencesUtil.userBaseInfoKey);
        try {
            List<UserBaseInfo> userBaseInfos = UserDao.newInstance(MyApplication.application).queryUserData();
            if (userBaseInfos != null && userBaseInfos.size() == 1) {
                return userBaseInfos.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//        return userBaseInfo = UtilsProcessData.jsonTobean(data, UserBaseInfo.class);
    }

    /**
     * 是否登录
     *
     * @return false 未登录
     */
    public boolean isLogin() {
        try {
            List<UserBaseInfo> userBaseInfos = UserDao.newInstance(MyApplication.application).queryUserData();
            if (userBaseInfos != null && userBaseInfos.size() == 1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 是否登录，没登录就跳到登录页面
     *
     * @param context         上下文
     * @param text            提示的文本
     * @param loginEndIsFinsh 登录后回到当前页面
     * @return false 未登录
     */
    public boolean isLoginHaveCall(Context context, String text, boolean loginEndIsFinsh) {
        return isLoginHaveCall(context, null, loginEndIsFinsh, null);
    }

    /**
     * 是否登录，没登录就跳到登录页面
     *
     * @param context         上下文
     * @param loginEndIsFinsh 登录后回到当前页面
     * @return false 未登录
     */
    public boolean isLoginHaveCall(Context context, boolean loginEndIsFinsh) {
        return isLoginHaveCall(context, loginEndIsFinsh, null);
    }

    /**
     * 是否登录，没登录就跳到登录页面
     *
     * @param context         上下文
     * @param text            提示的文本
     * @param loginEndIsFinsh 登录后回到当前页面
     * @return false 未登录
     */
    public boolean isLoginHaveCall(final Context context, String text, final boolean loginEndIsFinsh, final UserInfoLoginCallback listening) {
        if (isLogin()) {
            return true;
        } else {
            final StringBuilder key = new StringBuilder();
            if (listening != null) {
                key.append(addListening(new UserInfoUpdateListening() {

                    @Override
                    public void userLogout() {

                    }

                    @Override
                    public void userLogin(UserBaseInfo userBaseInfo) {
                        listening.userLogin(userBaseInfo);
                        LogUtils.i("loginTest", "登录成功:");
                        //此处抛出异常，以便回调后删除该回调
                        string a = null;
                        a.toString();
                    }

                    @Override
                    public void updateUserInfo(UserBaseInfo userBaseInfo) {
                    }
                }));
            }

            String message = TextUtils.isEmpty(text) ? context.getResources().getString(R.string.unlogin_by_get_gift) : text;
            MessageDialog dialog = new MessageDialog(context, "提示", message, "取消", "登录");
            dialog.setRightOnClickListener(new RightBtnClickListener() {

                @Override
                public void OnClick(View view) {
                    UserInfoManager.instance().starLogin(context, loginEndIsFinsh, key.toString());
                }
            });

            dialog.setLeftOnClickListener(new LeftBtnClickListener() {

                @Override
                public void OnClick(View view) {
                    removeListening(key.toString());
                }
            });

            dialog.setCancelOnClickListener(new CancelCliclListener() {

                @Override
                public void onClicl(View view) {
                    removeListening(key.toString());
                }
            });
//					new UserLoginDialog(context, text, loginEndIsFinsh);
            dialog.show();
            return false;
        }
    }

    /**
     * 是否登录，没登录就跳到登录页面
     *
     * @param context         上下文
     * @param loginEndIsFinsh 登录后回到当前页面
     * @return false 未登录
     */
    public boolean isLoginHaveCall(Context context, boolean loginEndIsFinsh, UserInfoLoginCallback listening) {
        return isLoginHaveCall(context, null, loginEndIsFinsh, listening);
    }

    /**
     * 调用登录界面
     *
     * @param context         上下文
     * @param loginEndIsFinsh 登录后回到当前页面
     */
    public void starLogin(Context context, boolean loginEndIsFinsh) {
        starLogin(context, loginEndIsFinsh, "");
    }

    public void starLogin(Context context, boolean loginEndIsFinsh, String callBackKey) {
        // 登录
        Intent intent = getLoginIntent(context, loginEndIsFinsh, callBackKey);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public Intent getLoginIntent(Context context, boolean loginEndIsFinsh, String callBackKey) {
        Intent intent = new Intent(context, PersonalCenterActivity.class);
        intent.putExtra("type", "login");
        intent.putExtra("loginEndIsFinsh", loginEndIsFinsh);
        intent.putExtra("callBackKey", callBackKey);
        return intent;
    }

    //为了兼容之前的调用
    public Intent getLoginIntent(Context context, boolean loginEndIsFinsh) {
        return getLoginIntent(context, loginEndIsFinsh, "");
    }

    /**
     * 调用注册界面
     *
     * @param context 上下文
     */
    public void starRegister(Context context) {
        // 登录
        Intent intent = new Intent(context, PersonalCenterActivity.class);
        intent.putExtra("type", "register");
        intent.putExtra("loginEndIsFinsh", false);
        context.startActivity(intent);
    }

    /**
     * 一些和业务相关的数据的清理
     */
    private void logoutProcess() {
        GlobalParams.clearToken();
        Net.instance().setUcenterSalt(null);
    }

    /**
     * 保存用户登录的历史记录,记录
     */
    private void saveHistoryUserInfo(List<UserBaseInfo> list) {
        StringBuffer userName = new StringBuffer();
        StringBuffer userAvatar = new StringBuffer();
        StringBuffer userId = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            userName.append(list.get(i).getUserName());
            userAvatar.append(list.get(i).getAvatar());
            userId.append(list.get(i).getId());
            if (i != list.size() - 1) {
                userName.append(",");
                userAvatar.append(",");
                userId.append(",");
            }
        }
        SharedPreferencesUtil share = new SharedPreferencesUtil(MyApplication.application.getApplicationContext());
        share.add(SharedPreferencesUtil.userHistoryUserName, userName.toString());
        share.add(SharedPreferencesUtil.userHistoryAvatar, userAvatar.toString());
        share.add(SharedPreferencesUtil.userHistoryUserId, userId.toString());
    }

    /**
     * 获取用户登录的历史记录,记录
     */
    public List<UserBaseInfo> getHistoryUserInfo() {
        SharedPreferencesUtil share = new SharedPreferencesUtil(MyApplication.application.getApplicationContext());
        String userId = share.get(SharedPreferencesUtil.userHistoryUserId);
        String userName = share.get(SharedPreferencesUtil.userHistoryUserName);
        String userAvatar = share.get(SharedPreferencesUtil.userHistoryAvatar);
        if (!TextUtils.isEmpty(userId)) {
            String[] userIdArr = userId.split(",");
            String[] userNameArr = userName.split(",");
            String[] userAvatarArr = userAvatar.split(",");
            List<UserBaseInfo> list = new ArrayList<UserBaseInfo>();
            UserBaseInfo user;
            for (int i = 0; i < userIdArr.length; i++) {
                user = new UserBaseInfo();
                user.setId(Integer.parseInt(userIdArr[i]));
                if (i < userNameArr.length) {
                    user.setUserName(userNameArr[i]);
                }
                if (i < userAvatarArr.length) {
                    user.setAvatar(userAvatarArr[i]);
                }
                list.add(user);
            }
            return list;
        }
        return null;
    }

    /**
     * 删除用户登录的历史记录
     */
    public void delHistoryUserInfo(UserBaseInfo user) {
        List<UserBaseInfo> list = getHistoryUserInfo();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId() == user.getId()) {
                    list.remove(i);
                    break;
                }
            }
            saveHistoryUserInfo(list);
        }
    }

    /**
     * 添加用户登录的历史记录
     */
    public void addHistoryUserInfo(UserBaseInfo user) {
        List<UserBaseInfo> list = getHistoryUserInfo();
        if (list == null) {
            list = new ArrayList<UserBaseInfo>();
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == user.getId()) {
                return;
            }
        }
        list.add(user);
        if (list.size() > historyMaxSize) {
            list.remove(0);
        }
        saveHistoryUserInfo(list);
    }

    /**
     * 更新用户历史记录的头像
     */
    public void updateHistoryUserAvatar(int id, String avatar) {
        boolean isChange = false;
        List<UserBaseInfo> list = getHistoryUserInfo();
        if (list == null) {
            list = new ArrayList<UserBaseInfo>();
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) {
                if (avatar != null && !avatar.equals(list.get(i).getAvatar())) {
                    list.get(i).setAvatar(avatar);
                    isChange = true;
                    break;
                }
            }
        }
        if (isChange) {
            saveHistoryUserInfo(list);
        }
    }

}
