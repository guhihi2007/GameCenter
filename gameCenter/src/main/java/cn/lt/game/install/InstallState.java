package cn.lt.game.install;

public class InstallState {
    //等待安装
    public final static int install = 11;
    //安装完成
    public final static int installComplete = 12;
    //安装失败
    public final static int installFail = 13;
    //升级
    public final static int upgrade = 14;
    //忽略升级
    public final static int ignore_upgrade = 15;
    //安装中
    public final static int installing = 16;
    //升级暂停（在升级的过程中，由于网络中断导致的暂停）
    //此状态目前只在prev_state中使用，不会在state中使用
    public final static int upgrade_inProgress = 17;

    public final static int signError = 18; //签名不一致

    public final static int check = 19; //签名校验

    /**
     * 安装状态监听
     */
    public final static int install_listener = 0;  //未安装

    public final static int installing_listener = 1;  //代表在安装中

    public static boolean isInstalledState(int state) {
        return state == installComplete || state == upgrade || state == ignore_upgrade;
    }
}
