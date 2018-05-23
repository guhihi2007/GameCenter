package cn.lt.game.application;

import cn.lt.game.net.Salt;

/**
 * 应用程序保存的临时数据
 * Created by wenchao on 2015/9/21.
 */
public class GlobalParams {

    public static Salt salt = new Salt();

    /**用户登录token*/
    public static String token = null;

    /**
     * 清除token
     */
    public static void clearToken(){
        token = null;
    }

}
