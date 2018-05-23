package cn.lt.game.install.autoinstaller;

import android.view.accessibility.AccessibilityEvent;

/**accessibility服务回调
 * Created by wenchao on 2015/6/24.
 */
public interface IAccessibilityService {

    void onInterrupt();

    void onAccessibilityEvent(AccessibilityEvent accessibilityEvent, android.accessibilityservice.AccessibilityService accessibilityService);


}
