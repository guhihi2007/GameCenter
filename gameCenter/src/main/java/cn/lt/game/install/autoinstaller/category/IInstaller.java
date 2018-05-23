package cn.lt.game.install.autoinstaller.category;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by wenchao on 2015/6/24.
 */
public interface IInstaller {
    String getPackageInstallerName();

    void onInterrupt();

    void onInstall(AccessibilityNodeInfo parentNodeInfo, AccessibilityNodeInfo sourceNodeInfo, AccessibilityEvent accessibilityEventc);

    void onInstallEnd();
}
