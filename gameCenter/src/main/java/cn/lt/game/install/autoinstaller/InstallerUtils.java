package cn.lt.game.install.autoinstaller;

import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenchao on 2015/6/24.
 * 帮助类
 */
public class InstallerUtils {
    private static final String BUILD_PROP_FILE = "/system/build.prop";
    private static final String PROP_NAME_MIUI_VERSION_CODE = "ro.miui.ui.version.code";

    /**
     * 根据button文本查找Node列表
     *
     * @param parentNodeInfo
     * @param buttonName
     * @return
     */
    public static List<AccessibilityNodeInfo> contains(AccessibilityNodeInfo parentNodeInfo, String buttonName) {
        List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText = parentNodeInfo.findAccessibilityNodeInfosByText(buttonName);
        List<AccessibilityNodeInfo> arrayList = new ArrayList<AccessibilityNodeInfo>();
        if (findAccessibilityNodeInfosByText == null) return arrayList;
        for (AccessibilityNodeInfo accessibilityNodeInfo : findAccessibilityNodeInfosByText) {
            CharSequence text = accessibilityNodeInfo.getText();
            if (!TextUtils.isEmpty(text) && text.toString().equals(buttonName)) {
                arrayList.add(accessibilityNodeInfo);
            }
        }
        return arrayList;
    }


    /**
     * 执行buttonName的点击事件
     *
     * @param accessibilityNodeInfo
     * @param buttonName
     * @return if true执行成功， other false
     */
    public static boolean performOnclick(AccessibilityNodeInfo accessibilityNodeInfo, String buttonName) {
        if (accessibilityNodeInfo == null || !accessibilityNodeInfo.isClickable()) {
            return false;
        }
        return accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }

    /**
     * 判断是否  小米系统
     *
     * @return
     */
    public static boolean isMIUI() {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(new File(BUILD_PROP_FILE)));
            String readLine;
            do {
                readLine = bufferedReader.readLine();
                if (readLine == null) {
                    return false;
                }
            } while (!readLine.startsWith(PROP_NAME_MIUI_VERSION_CODE));
            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 判断 是否 魅族手机
     *
     * @return
     */
    public static boolean isFlymeOs() {
        try {
            return android.os.Build.FINGERPRINT.toLowerCase().contains("flyme");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
