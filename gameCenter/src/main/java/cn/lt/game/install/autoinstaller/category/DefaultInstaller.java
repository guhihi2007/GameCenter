package cn.lt.game.install.autoinstaller.category;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import cn.lt.game.R;
import cn.lt.game.install.autoinstaller.AutoInstallerContext;
import cn.lt.game.install.autoinstaller.InstallerUtils;

/**
 * Created by wenchao on 2015/6/24.
 */
public class DefaultInstaller implements IInstaller {
    private final String app_auto_install_next;
    private final String app_auto_install_done;
    private final String app_auto_install_confirm;
    private final String app_auto_install_install;

    public DefaultInstaller() {
        this.app_auto_install_install = AutoInstallerContext.getInstance().getContext().getResources()
                .getString(R.string.app_auto_install_install);
        this.app_auto_install_next = AutoInstallerContext.getInstance().getContext().getResources()
                .getString(R.string.app_auto_install_next);
        this.app_auto_install_done = AutoInstallerContext.getInstance().getContext().getResources()
                .getString(R.string.app_auto_install_done);
        this.app_auto_install_confirm = AutoInstallerContext.getInstance().getContext().getResources()
                .getString(R.string.app_auto_install_confirm);
    }

    @Override
    public String getPackageInstallerName() {
        return "com.android.packageinstaller";
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onInstall(AccessibilityNodeInfo parentNodeInfo, AccessibilityNodeInfo sourceNodeInfo, AccessibilityEvent accessibilityEventc) {
        //确认
        for (AccessibilityNodeInfo accessibilityNodeInfo : InstallerUtils.contains(parentNodeInfo, this.app_auto_install_confirm)) {
            InstallerUtils.performOnclick(accessibilityNodeInfo, this.app_auto_install_confirm);
            showAnim();
        }

        //下一步
        for (AccessibilityNodeInfo accessibilityNodeInfo : InstallerUtils.contains(parentNodeInfo, this.app_auto_install_next)) {
            InstallerUtils.performOnclick(accessibilityNodeInfo, this.app_auto_install_next);
            showAnim();
        }

        //安装
        for (AccessibilityNodeInfo accessibilityNodeInfo : InstallerUtils.contains(parentNodeInfo, this.app_auto_install_install)) {
            InstallerUtils.performOnclick(accessibilityNodeInfo, this.app_auto_install_install);
            showAnim();
        }

        //完成
        for (AccessibilityNodeInfo accessibilityNodeInfo : InstallerUtils.contains(parentNodeInfo, this.app_auto_install_done)) {
            InstallerUtils.performOnclick(accessibilityNodeInfo, this.app_auto_install_done);
            onInstallEnd();
        }
    }

    @Override
    public void onInstallEnd() {
    }

    protected void showAnim() {
        // TODO show anim
    }


}
