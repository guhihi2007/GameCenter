package cn.lt.game.statistics.database.provider;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import java.util.List;

import cn.lt.game.application.GlobalParams;
import cn.lt.game.application.MyApplication;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.statistics.database.dao.UserDao;
import cn.lt.game.ui.app.personalcenter.PCNet;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

/**
 * @author chengyong
 * @time 2017/9/21 11:08
 * @des ${观察应用市场的登录变化}
 */

public class UserObservable extends ContentObserver {
    public UserObservable(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
//        LogUtils.d(LogTAG.USER,"游戏中心UserObservable：应用市场来改我的数据了，selfChange-"+selfChange);
//        decideToLoginOrOut();
    }

//    private void decideToLoginOrOut() {
//        try {
//            List<UserBaseInfo> userBaseInfos = UserDao.newInstance(MyApplication.application).queryUserData();
//            LogUtils.d(LogTAG.USER, "游戏中心UserObservable：所有账号信息 userBaseInfos:"+userBaseInfos);
//            if(userBaseInfos.size()==0){
//                GlobalParams.token="";
//                UserInfoManager.instance().userLogout(true);
//            }else{
//                LogUtils.d(LogTAG.USER, "游戏中心UserObservable：游戏中心查数据成功 Token:"+userBaseInfos.get(userBaseInfos.size()-1).getToken());
//                GlobalParams.token=userBaseInfos.get(userBaseInfos.size()-1).getToken();
//                getUserInfoByToken();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            LogUtils.d(LogTAG.USER, "游戏中心UserObservable：游戏中心查数据 decideToLoginOrOut 抛异常:"+e.getMessage());
//        }
//    }

//    public void getUserInfoByToken(){
//        PCNet.fetchUserInfo(new WebCallBackToObj<UserBaseInfo>() {
//
//            @Override
//            protected void handle(UserBaseInfo info) {
//                LogUtils.d(LogTAG.USER, "游戏中心UserObservable：token登陆成功");
//                info.setToken(GlobalParams.token);//token请求没有返回token
//                UserInfoManager.instance().setUserBaseInfo(info,true);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Throwable error) {
//                LogUtils.d(LogTAG.USER, "游戏中心UserObservable：token登陆失败");
//            }
//        });
//    }
}
