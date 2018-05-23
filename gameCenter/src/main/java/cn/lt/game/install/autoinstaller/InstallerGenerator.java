package cn.lt.game.install.autoinstaller;

import android.view.accessibility.AccessibilityNodeInfo;

import cn.lt.game.install.autoinstaller.category.DefaultInstaller;
import cn.lt.game.install.autoinstaller.category.FlymeInstaller;
import cn.lt.game.install.autoinstaller.category.IInstaller;
import cn.lt.game.install.autoinstaller.category.LenovoInstaller;
import cn.lt.game.install.autoinstaller.category.MIUIInstaller;

/**
 * Created by wenchao on 2015/6/24.
 * 安装生成器
 */
public enum InstallerGenerator {
    /**默认安装*/
    DefaultGenerator(new DefaultInstaller()),
    /**小米安装*/
    MIUIGenerator(new MIUIInstaller()),
    /**联想安装*/
    LenovoGenerator(new LenovoInstaller()),
    /**魅族安装*/
    FlymeGenerator(new FlymeInstaller());

    private final IInstaller installer;

    InstallerGenerator(IInstaller installer){
        this.installer = installer;
    }

    public IInstaller getInstaller(){
        return installer;
    }

    /**
     * 根据NodeInfo 自动选择安装器
     * @param accessibilityNodeInfo
     * @return
     */
    public static InstallerGenerator getGenerator(AccessibilityNodeInfo accessibilityNodeInfo){
        if(accessibilityNodeInfo.getPackageName() == null){
            throw new NullPointerException();
        }else if(InstallerUtils.isMIUI()){
            return MIUIGenerator;
        }else{
            if(InstallerUtils.isFlymeOs()){
                return FlymeGenerator;
            }
            if(accessibilityNodeInfo.getPackageName().equals(LenovoGenerator.getInstaller().getPackageInstallerName())){
                return LenovoGenerator;
            }
            return DefaultGenerator;
        }
    }
}
