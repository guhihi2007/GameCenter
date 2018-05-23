package cn.lt.game.install.autoinstaller;

import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.lib.util.LogUtils;


/**系统Accessibility服务，实现类似豌豆荚自动装功能
 * Created by wenchao on 2015/6/24.
 */
public class AccessibilityService extends android.accessibilityservice.AccessibilityService{


    /**
     * 安装服务监听回调
     */
    public interface IInstallMonitor{
        void onServiceAlive(boolean alive);
    }

    public AccessibilityService(){
        mIAccessibilityServices.add(new DefaultInstallerService());
    }


    private static List<IInstallMonitor> mInstallMonitors;
    private static List<IAccessibilityService> mIAccessibilityServices;

    static{
        mInstallMonitors = new ArrayList<IInstallMonitor>();
        mIAccessibilityServices = new ArrayList<IAccessibilityService>();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(AutoInstallerContext.getInstance().getContext() == null){
            LogUtils.e("Context not init.");
            return;
        }
        for(IAccessibilityService accessibilityService:mIAccessibilityServices){
            accessibilityService.onAccessibilityEvent(event,this);
        }
        LogUtils.i("AccessibilityService onAccessibilityEvent()");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        onServiceAlive(false);
        LogUtils.i("AccessibilityService onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        onServiceAlive(true);
        LogUtils.i("AccessibilityService onServiceConnected()");
    }
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	LogUtils.i("AccessibilityService onDestory()");
    }

    @Override
    public void onInterrupt() {
        for(IAccessibilityService accessibilityService:mIAccessibilityServices){
            accessibilityService.onInterrupt();
        }
        LogUtils.i("AccessibilityService onInterrupt()");
    }

    public static void addMonitor(IInstallMonitor monitor){
        if(!mInstallMonitors.contains(monitor)){
            mInstallMonitors.add(monitor);
        }

    }

    public static void removeMonitor(IInstallMonitor monitor){
        if(mInstallMonitors.contains(monitor)){
            mInstallMonitors.remove(monitor);
        }
    }

    private void onServiceAlive(boolean isAlive){
        for(IInstallMonitor monitor : mInstallMonitors){
            monitor.onServiceAlive(isAlive);
        }
    }


}
