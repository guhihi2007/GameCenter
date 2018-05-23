package cn.lt.game.install.autoinstaller.category;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import cn.lt.game.R;
import cn.lt.game.install.autoinstaller.AutoInstallerContext;
import cn.lt.game.install.autoinstaller.InstallerUtils;

/**
 * Created by wenchao on 2015/6/24.
 * 小米安装器
 */
public class MIUIInstaller extends DefaultInstaller {
    private final String app_auto_install_done_miui;
    private final String app_auto_install_done;
    private final String install_confirm;
    private final String app_auto_install_finish_miui;
    private final String install;

    public MIUIInstaller() {
        this.install = AutoInstallerContext.getInstance().getContext().getResources().getString(R.string.app_auto_install_install);
        this.app_auto_install_done_miui = AutoInstallerContext.getInstance().getContext().getResources()
                .getString(R.string.app_auto_install_done_miui);
        this.app_auto_install_done = AutoInstallerContext.getInstance().getContext().getResources()
                .getString(R.string.app_auto_install_done);
        this.install_confirm = AutoInstallerContext.getInstance().getContext().getResources()
                .getString(R.string.app_auto_install_confirm);
        this.app_auto_install_finish_miui = AutoInstallerContext.getInstance().getContext().getString(
                R.string.app_auto_install_finish_miui);
    }

    @Override
    public String getPackageInstallerName() {
        return "com.android.packageinstaller";
    }

    @Override
    public void onInstall(AccessibilityNodeInfo accessibilityNodeInfo,
                          AccessibilityNodeInfo accessibilityNodeInfo2, AccessibilityEvent accessibilityEvent) {
        //确认
        for (AccessibilityNodeInfo mAccessibilityNodeInfo : InstallerUtils.contains(accessibilityNodeInfo,
                this.install_confirm)) {
            InstallerUtils.performOnclick(mAccessibilityNodeInfo, this.install_confirm);
            showAnim();
        }

        //安装
        for (AccessibilityNodeInfo mAccessibilityNodeInfo : InstallerUtils
                .contains(accessibilityNodeInfo, this.install)) {
            InstallerUtils.performOnclick(mAccessibilityNodeInfo, this.install);
            showAnim();
        }

        //我知道了
        for (AccessibilityNodeInfo mAccessibilityNodeInfo : InstallerUtils.contains(accessibilityNodeInfo,
                this.app_auto_install_done_miui)) {
            InstallerUtils.performOnclick(mAccessibilityNodeInfo, this.app_auto_install_done_miui);
        }

        //done
        for (AccessibilityNodeInfo mAccessibilityNodeInfo : InstallerUtils.contains(accessibilityNodeInfo,
                this.app_auto_install_done)) {
            InstallerUtils.performOnclick(mAccessibilityNodeInfo, this.app_auto_install_done);
        }

        //finish
        for (AccessibilityNodeInfo mAccessibilityNodeInfo : InstallerUtils.contains(accessibilityNodeInfo,
                this.app_auto_install_finish_miui)) {
            InstallerUtils.performOnclick(mAccessibilityNodeInfo, this.app_auto_install_finish_miui);
            onInstallEnd();
        }
    }


}
