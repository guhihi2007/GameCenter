package cn.lt.game.install.autoinstaller.category;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import cn.lt.game.R;
import cn.lt.game.install.autoinstaller.AutoInstallerContext;
import cn.lt.game.install.autoinstaller.InstallerUtils;

/**
 * Created by wenchao on 2015/6/24.
 * 魅族安装器
 */
public class FlymeInstaller extends DefaultInstaller {
    private final String app_auto_install_confirm;
    private final String app_auto_install_install;

    public FlymeInstaller() {
        this.app_auto_install_install = AutoInstallerContext.getInstance().getContext().getResources()
                .getString(R.string.app_auto_install_install);
        this.app_auto_install_confirm = AutoInstallerContext.getInstance().getContext().getResources()
                .getString(R.string.app_auto_install_confirm);
    }

    @Override
    public String getPackageInstallerName() {
        return "com.android.packageinstaller";
    }

    @Override
    public void onInstall(AccessibilityNodeInfo parentNodeInfo,
                          AccessibilityNodeInfo sourceNodeInfo, AccessibilityEvent accessibilityEvent) {
        for (AccessibilityNodeInfo mAccessibilityNodeInfo : InstallerUtils.contains(parentNodeInfo,
                this.app_auto_install_confirm)) {
            InstallerUtils.performOnclick(mAccessibilityNodeInfo, this.app_auto_install_confirm);
            showAnim();
        }
        for (AccessibilityNodeInfo mAccessibilityNodeInfo : InstallerUtils.contains(parentNodeInfo,
                this.app_auto_install_install)) {
            InstallerUtils.performOnclick(mAccessibilityNodeInfo, this.app_auto_install_install);
        }
    }

}
