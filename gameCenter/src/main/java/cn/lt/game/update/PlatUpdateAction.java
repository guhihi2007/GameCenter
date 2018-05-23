package cn.lt.game.update;

/**
 * 启动UpdateService的一些操作类型；
 * 
 * @author dxx
 *
 */
public class PlatUpdateAction {

	/**
	 * 启动平台更新service Action
	 */
	public static final String SERVICE_START_ACTION = "cn.lt.game.update.PlatUpdateService";

	public static final String ACTION = "action";

	/**
	 * 从通知过来；
	 */
	public static final String ACTION_NOTIFICATION = "action.notification";

	/**
	 * 点击升级提升框的取消按钮或者返回键；
	 */
	public static final String ACTION_DIALOG_CANCEL = "action.dialog.cancel";

	/**
	 * 点击升级提示框的确定按钮；
	 */
	public static final String ACTION_DIALOG_CONFIRM = "action.dialog.confirm";

	/**
	 * 应用启动时首先调用UpdateService的action;
	 */
	public static final String ACTION_NORMAL = "action.normal";

}
