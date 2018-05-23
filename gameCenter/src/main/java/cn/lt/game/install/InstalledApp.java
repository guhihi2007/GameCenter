package cn.lt.game.install;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.lt.game.lib.util.AdMd5;
import cn.lt.game.model.ToServiceApp;

public class InstalledApp {

    public synchronized static List<PackageInfo> getAll(Context context) {
        PackageManager pm = context.getPackageManager();

        // use fallback:
        Process process;
        List<PackageInfo> result = new ArrayList<>();
        BufferedReader bufferedReader = null;
        try {
            process = Runtime.getRuntime().exec("pm list packages");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                try {
                    final String packageName = line.substring(line.indexOf(':') + 1);
                    final PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
                    result.add(packageInfo);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            process.waitFor();
        } catch (Exception e) {
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
        if (result.size() > 0) {
            return result;
        }

        try {
            // some rom may need permission（huawei）
            result =  pm.getInstalledPackages(0);
        } catch (Exception ignored) {
            // we don't care why it didn't succeed. We'll do it using an alternative way instead
        }

        return result;
    }

    public static List<PackageInfo> getUserApp(Context context) {
        List<PackageInfo> pkgs = getAll(context);
        List<PackageInfo> userPkgs = new ArrayList<>();
        for (PackageInfo packageInfo : pkgs) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                userPkgs.add(packageInfo);
            }
        }
        return userPkgs;
    }

    public synchronized static List<ToServiceApp> getUserPkgName(Context context, boolean calcMD5) {
        List<PackageInfo> pkgs = getAll(context);
        List<ToServiceApp> userPkgs = new ArrayList<>();
        for (PackageInfo packageInfo : pkgs) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String md5 = null;
                if (calcMD5) {
                    md5 = AdMd5.md5sum(appInfo.sourceDir);
                }

                String versionName = TextUtils.isEmpty(packageInfo.versionName) ? "" : packageInfo.versionName;
                ToServiceApp to = new ToServiceApp(packageInfo.packageName, versionName, String.valueOf(packageInfo.versionCode), md5);
                userPkgs.add(to);

            }
        }
        return userPkgs;
    }


    public synchronized static List<ToServiceApp> getUserPkgName(Context context) {
        return getUserPkgName(context, false);
    }
}
