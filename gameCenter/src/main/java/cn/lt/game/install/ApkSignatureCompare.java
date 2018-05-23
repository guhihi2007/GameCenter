package cn.lt.game.install;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import cn.lt.game.lib.util.LogUtils;

/**
 * Created by LinJunSheng on 2016/6/22.
 * apk签名与已安装应用签名对比器
 */
public class ApkSignatureCompare {
    private static final String TAG = "ApkSignatureCompareTAG";

    /**
     * @param apkPath             apk目录路径
     * @param installedAppPkgName 已安装应用的包名
     * @return 是否签名一直
     */
    public static boolean isSignatureSame(Context context, String apkPath, String installedAppPkgName) {
        long t1 = System.currentTimeMillis();
        String installedAppSignature = getInstalledAppSignature(context, installedAppPkgName);
        long t2 = System.currentTimeMillis();
        if (TextUtils.isEmpty(installedAppSignature)) {
            // 手机之前没有安装过此应用，所以返回true，不需弹窗
            LogUtils.i(TAG, "installedAppSignature = " + installedAppSignature + "， 手机之前没有安装过此应用，所以返回true，不需弹窗");
            return true;
        }

        // PS:讲真这个过程真的好耗时啦，apk体积越大，耗时越久
        String apkSignature = getUninstallAPKSignatures(context, apkPath);
        if (TextUtils.isEmpty(apkSignature) || TextUtils.isEmpty(installedAppSignature)) {
            // 由于两个签名不能全部获得，无法对比，只能返回true，不需弹窗
            LogUtils.i(TAG, "由于两个签名不能全部获得，无法对比，只能返回true，不需弹窗");
            return true;
        }

        LogUtils.i(TAG, "isSignatureSame = " + apkSignature.equals(installedAppSignature));
        return apkSignature.equals(installedAppSignature);
    }


    /**
     * 根据apk路径获取它的签名
     *
     * @param apkPath apk目录路径
     * @return 签名
     */
    private static String getUninstallAPKSignatures(Context context, String apkPath) {
        String sign = "";
        File file = new File(apkPath);
        try {
            sign = getSignaturesFromApk(file).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sign;
    }

    /**
     * 获取已安装应用的签名
     *
     * @param pkgName 传入包名
     * @return 签名
     */
    private static String getInstalledAppSignature(Context context, String pkgName) {
        boolean isEmpty = TextUtils.isEmpty(pkgName);
        if (isEmpty) {
            return "";
        }
        try {
            PackageManager manager = context.getPackageManager();

            // 通过包管理器获得指定包名包含签名的包信息
            PackageInfo packageInfo = manager.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);

            // 通过返回的包信息获得签名数组
            Signature[] signatures = packageInfo.signatures;

            // 循环遍历签名数组拼接应用签名
            StringBuilder builder = new StringBuilder();
            for (Signature signature : signatures) {
                builder.append(signature.toCharsString());
            }
            // 得到应用签名
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }


    /**
     * 从APK中读取签名
     *
     * @param file
     * @return
     * @throws IOException
     */
    private static List<String> getSignaturesFromApk(File file) throws IOException {
        List<String> signatures = new ArrayList<>();
        JarFile jarFile = new JarFile(file);
        try {
            JarEntry je = jarFile.getJarEntry("AndroidManifest.xml");
            byte[] readBuffer = new byte[8192];
            Certificate[] certs = loadCertificates(jarFile, je, readBuffer);
            if (certs != null) {
                for (Certificate c : certs) {
                    String sig = toCharsString(c.getEncoded());
                    signatures.add(sig);
                }
            }
        } catch (Exception ex) {
        }
        return signatures;
    }

    /**
     * 加载签名
     *
     * @param jarFile
     * @param je
     * @param readBuffer
     * @return
     */
    private static Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
        try {
            InputStream is = jarFile.getInputStream(je);
            while (is.read(readBuffer, 0, readBuffer.length) != -1) {
            }
            is.close();
            return je != null ? je.getCertificates() : null;
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * 将签名转成转成可见字符串
     *
     * @param sigBytes
     * @return
     */
    private static String toCharsString(byte[] sigBytes) {
        byte[] sig = sigBytes;
        final int N = sig.length;
        final int N2 = N * 2;
        char[] text = new char[N2];
        for (int j = 0; j < N; j++) {
            byte v = sig[j];
            int d = (v >> 4) & 0xf;
            text[j * 2] = (char) (d >= 10 ? ('a' + d - 10) : ('0' + d));
            d = v & 0xf;
            text[j * 2 + 1] = (char) (d >= 10 ? ('a' + d - 10) : ('0' + d));
        }
        return new String(text);
    }
}
