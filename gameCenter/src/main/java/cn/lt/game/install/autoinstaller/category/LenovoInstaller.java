package cn.lt.game.install.autoinstaller.category;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import cn.lt.game.R;
import cn.lt.game.install.autoinstaller.AutoInstallerContext;
import cn.lt.game.install.autoinstaller.InstallerUtils;

/**
 * Created by wenchao on 2015/6/24.
 * 联想安装器
 */
public class LenovoInstaller extends DefaultInstaller {
    private final String app_auto_install_lenovo_pass_le_security;

    private final String app_auto_install_confirm;
    private final String app_auto_install_lenovo_install_success_button;
    private final String app_auto_install_lenovo_no_perm;
    private final String app_auto_install_lenovo_perm;
    private final String app_auto_install_lenovo_install_success;
    private final String app_auto_install_install;

    public LenovoInstaller() {
        this.app_auto_install_install = AutoInstallerContext.getInstance().getContext().getResources()
                .getString(R.string.app_auto_install_install);
        this.app_auto_install_confirm = AutoInstallerContext.getInstance().getContext().getResources()
                .getString(R.string.app_auto_install_confirm);
        this.app_auto_install_lenovo_install_success_button = AutoInstallerContext.getInstance().getContext().getString(
                R.string.app_auto_install_lenovo_install_success_button);
        this.app_auto_install_lenovo_no_perm = AutoInstallerContext.getInstance().getContext().getString(
                R.string.app_auto_install_lenovo_no_perm);
        this.app_auto_install_lenovo_perm = AutoInstallerContext.getInstance().getContext().getResources()
                .getString(R.string.app_auto_install_lenovo_perm);
        this.app_auto_install_lenovo_install_success = AutoInstallerContext.getInstance().getContext().getResources()
                .getString(R.string.app_auto_install_lenovo_install_success);
        this.app_auto_install_lenovo_pass_le_security = AutoInstallerContext.getInstance().getContext().getResources()
                .getString(R.string.app_auto_install_lenovo_pass_le_security);
    }

    @Override
    public String getPackageInstallerName() {
        return "com.lenovo.safecenter";
    }

    @Override
    public void onInstall(AccessibilityNodeInfo parentNodeInfo,
                          AccessibilityNodeInfo sourceNodeInfo, AccessibilityEvent accessibilityEvent) {
        List<AccessibilityNodeInfo> app_auto_install_lenovo_perm = InstallerUtils.contains(parentNodeInfo,
                this.app_auto_install_lenovo_perm);
        List<AccessibilityNodeInfo> app_auto_install_lenovo_no_perm = InstallerUtils
                .contains(parentNodeInfo, this.app_auto_install_lenovo_no_perm);
        List<AccessibilityNodeInfo> app_auto_install_lenovo_pass_le_security = InstallerUtils.contains(parentNodeInfo,
                this.app_auto_install_lenovo_pass_le_security);
        if (app_auto_install_lenovo_perm.size() > 0 || app_auto_install_lenovo_no_perm.size() > 0
                || app_auto_install_lenovo_pass_le_security.size() > 0) {
            for (AccessibilityNodeInfo mAccessibilityNodeInfo : InstallerUtils
                    .contains(parentNodeInfo, this.app_auto_install_install)) {
                InstallerUtils.performOnclick(mAccessibilityNodeInfo, this.app_auto_install_install);
                showAnim();
            }
            for (AccessibilityNodeInfo mAccessibilityNodeInfo : InstallerUtils.contains(parentNodeInfo,
                    this.app_auto_install_confirm)) {
                InstallerUtils.performOnclick(mAccessibilityNodeInfo, this.app_auto_install_confirm);
                showAnim();
            }
            return;
        }
        List<AccessibilityNodeInfo> app_auto_install_lenovo_install_success = InstallerUtils.contains(parentNodeInfo,
                this.app_auto_install_lenovo_install_success);

        if (app_auto_install_lenovo_install_success.size() > 0) {
            for (AccessibilityNodeInfo mAccessibilityNodeInfo : InstallerUtils.contains(parentNodeInfo,
                    this.app_auto_install_lenovo_install_success_button)) {
                InstallerUtils.performOnclick(mAccessibilityNodeInfo,
                        this.app_auto_install_lenovo_install_success_button);
                onInstallEnd();
            }
        }
    }
}
