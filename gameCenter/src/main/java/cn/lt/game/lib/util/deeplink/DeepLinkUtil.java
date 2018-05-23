package cn.lt.game.lib.util.deeplink;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;


/**
 * Created by honaf on 2017/10/27.
 * 跳转到应用市场做相应工作
 */

public class DeepLinkUtil {
    public static final String FROM_PREFIX = "gamecenter_chaoqian_";
    public static final String FROM_TYPE = "from";
    public static final String APPSTORE_PKG = "cn.lt.appstore";

    /**
     * ~~~~~~~~~测试使用~~~~~~~~~~~~~
     * 打开指定app的scheme
     *
     * @param context 上下文
     */
    public static void openApp(Context context) {

        String url = "appcenter_chaoqian://index?dplink={\"click_type\":\"app_info\",\"data\":{\"apps_type\":\"software\",\"id\":\"10000046\",\"package_name\":\"com.qiyi.video\"}}";
//        String url = "appcenter_chaoqian://index";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(context.getPackageManager()) == null) {
            ToastUtils.showToast(context, "应用未安装");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }

    }

    /**
     * 打开指定app的scheme
     *
     * @param context 上下文
     * @param url     link地址
     */
    public static void openApp(Context context, String url) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(context.getPackageManager()) == null) {
            ToastUtils.showToast(context, "应用未安装");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }

    }

    /**
     * 打开指定app的scheme
     *
     * @param context 上下文
     * @param url     link地址
     */
    public static void openApp(Context context, String url, String from) {
        if (context == null || url == null) {
            return;
        }
        if (url.contains("?")) {
            url += "&" + FROM_TYPE + "=" + FROM_PREFIX + from;
        } else {
            url += "?" + FROM_TYPE + "=" + FROM_PREFIX + from;
        }
        openApp(context,url);

    }

    /**
     * 通过scheme判断app是否存在
     *
     * @param context 上下文
     * @param url     link地址
     * @return 将要打开的app是否存在
     */
    public static boolean isExistApp(Context context, String url) {
        LogUtils.e(LogTAG.HTAG, "isExistApp_url" + url);
        if (context == null || TextUtils.isEmpty(url)) {
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        return intent.resolveActivity(context.getPackageManager()) != null;
    }

    /**
     * 通过intent拿到相应的uri
     *
     * @param intent 传递
     * @return
     */
    public static Uri getUriData(Intent intent) {
        if (intent == null) {
            Log.e(LogTAG.HTAG, "intent-null");
            return null;
        }
        String action = intent.getAction();
        String data = intent.getDataString();

        if (Intent.ACTION_VIEW.equals(action) && !TextUtils.isEmpty(data)) {
            return Uri.parse(data);
        }
        return null;
    }

    /**
     * 通过Intent和key拿到指定参数的value
     *
     * @param intent 传递
     * @param key    键
     * @return 值
     */
    public static String getUriParam(Intent intent, String key) {
        if (intent == null) {
            Log.e(LogTAG.HTAG, "intent-null");
            return null;
        }
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && !TextUtils.isEmpty(data)) {
            Uri uriData = Uri.parse(data);
            if (uriData != null) {
                return uriData.getQueryParameter(key);
            }
        }
        return null;
    }

    /**
     * 通过Uri和key拿到指定参数的value
     *
     * @param uriData 格式化的地址
     * @param key     键
     * @return 值
     */
    public static String getUriParam(Uri uriData, String key) {
        if (uriData == null) {
            Log.e(LogTAG.HTAG, "uriData-null");
            return null;
        }
        return uriData.getQueryParameter(key);
    }

}
