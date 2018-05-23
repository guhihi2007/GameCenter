package cn.lt.game.install.autoinstaller;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.PowerManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by wenchao on 2015/6/24.
 */
public class DefaultInstallerService implements IAccessibilityService {

    private PowerManager.WakeLock mWakeLock;

    private InstallerGenerator mInstallerGenerator;

    @Override
    public void onInterrupt() {
        if (this.mInstallerGenerator != null) {
            this.mInstallerGenerator.getInstaller().onInterrupt();
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent, AccessibilityService accessibilityService) {
        AccessibilityNodeInfo sourceNodeInfo = accessibilityEvent.getSource();
        if (sourceNodeInfo != null && sourceNodeInfo.getPackageName() != null) {
            if (this.mInstallerGenerator == null) {
                this.mInstallerGenerator = InstallerGenerator.getGenerator(sourceNodeInfo);
            }
            AccessibilityNodeInfo parentNodeInfo = getParentNodeInfo(sourceNodeInfo);

            if (mWakeLock == null) {
                mWakeLock = newWakeLock();
            }
            mWakeLock.acquire(5 * 60 * 1000);
            this.mInstallerGenerator.getInstaller().onInstall(parentNodeInfo, sourceNodeInfo, accessibilityEvent);
        }
    }

    private PowerManager.WakeLock newWakeLock() {
        return ((PowerManager) AutoInstallerContext.getInstance().getContext().getSystemService(Context.POWER_SERVICE)).newWakeLock(268435482,
                "AUTO_INSTALL_WAKE_LOCK");
    }

    private AccessibilityNodeInfo getParentNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        AccessibilityNodeInfo parent = accessibilityNodeInfo.getParent();
        if (parent == null || parent == accessibilityNodeInfo) {
            return accessibilityNodeInfo;
        }
        return getParentNodeInfo(parent);
    }
}
