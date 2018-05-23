package cn.lt.game.ui.app.personalcenter;

import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

public interface UserInfoUpdateListening {
	/**登陆成功返回*/
    void userLogin(UserBaseInfo userBaseInfo);

	void updateUserInfo(UserBaseInfo userBaseInfo);

	/**注销*/
    void userLogout();
}
