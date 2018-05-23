package cn.lt.game.lib.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.lt.game.BuildConfig;
import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.RedPointEvent;
import cn.lt.game.install.InstallState;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.model.State;
import cn.lt.game.statistics.StatisticsEventData;
import de.greenrobot.event.EventBus;

/**
 * @version 1.0
 * @Author SunnyCoffee
 * @Date 2014-1-28
 * @Desc 工具类
 */


public class Utils {

    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1) break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }

    }

    /* 传入activity的context，获取屏幕宽高 */
    public static int[] getScreenSize(Context context) {
        int[] size = new int[2];
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        size[0] = dm.widthPixels;
        size[1] = dm.heightPixels;

        return size;
    }

    /* 传入activity的context，获取屏幕宽 */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;

    }

    /* 传入activity的context，获取屏幕高 */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;

    }

    /**
     * 获取
     *
     * @param context
     * @return 得到IMEI 例如：*#06#
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();

    }

    /**
     * @param context
     * @return 得到公网IP http://www.ip.cn/
     */
    public static String getWANIP(Context context, String ipaddr) {
        URL infoUrl = null;
        InputStream inStream = null;
        try {
            infoUrl = new URL(ipaddr);
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setConnectTimeout(50000);
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) if (line.contains("当前 IP：")) {
                    sb.append(line + "\n");
                }

                inStream.close();
                int start = sb.indexOf("<code>");
                int end = sb.indexOf("</code>", start + 1);
                String netIP = sb.substring(start + 6, end);
                return netIP;
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return "IPERROR";


    }

    /**
     * @param context
     * @return 得到局域网IP
     */
    public static String getLANIP(Context context) {
        return null;

    }

    /**
     * @param
     * @return 得到设备型号 例如 Nexus 6、MOTO X
     */
    public static String getDeviceName() {
        String deviceName = Build.MODEL;
        if (isNumber(deviceName)) {
            deviceName = Build.MANUFACTURER + " " + Build.DEVICE.replace(deviceName, "");
        }
        if (isConSpeCharacters(deviceName)) {
            return "shanzhaiji";
        }
        if (isChineseChar(deviceName)) {
            return "guochanji";
        }
        return deviceName;

    }

    /**
     * @param
     * @return 得到设备品牌
     */
    public static String getBrand() {
        String deviceName = "unknown";
        try {
            deviceName = Build.BRAND;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isConSpeCharacters(deviceName)) {
            return "shanzhaiji";
        }
        if (isChineseChar(deviceName)) {
            return "guochanji";
        }
        return deviceName;

    }

    /***
     * 是否包含乱码
     * @param string
     * @return
     */
    public static boolean isConSpeCharacters(String string) {
        return string.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*", "").length() != 0;
    }

    /***
     * 是否包含中文字符
     * @param str
     * @return
     */
    public static boolean isChineseChar(String str) {
        boolean temp = false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            temp = true;
        }
        return temp;
    }

    /***
     * 判断是否熄屏
     *
     * @return
     */
    public static boolean isScreenOn() {
        PowerManager pm = (PowerManager) MyApplication.application.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    /**
     * 得到手机sdk版本号 例如4.4.4
     *
     * @return
     */
    public static String getAndroidSDKVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /* 将byte大小转换至G或M为单位的字符串 */
    public static String converByteToGOrM(long num) {
        double m = num / 1048576;
        double g = 0;
        if (m > 800) {
            g = m / 1024;
            BigDecimal b = new BigDecimal(g);
            float size = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            return size + "G";
        } else {
            BigDecimal b = new BigDecimal(m);
            float size = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            return size + "M";
        }

    }

    public static String[] getPermisson(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            // 得到自己的包名
            String pkgName = pi.packageName;

            PackageInfo pkgInfo = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);
            //通过包名，返回包信息
            String sharedPkgList[] = pkgInfo.requestedPermissions;//得到权限列表
            return sharedPkgList;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getSystemProperty(String propName) {
        String line = null;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }


    public static String getGameMBSize(float lengths) {
        String format = String.format(Locale.CHINESE, "%.2fMB", (lengths) / 1024 / 1024);


        return format.equals("0.00MB") ? "0.01MB" : format;

    }

    /**
     * 转换下载量为 万+
     *
     * @param download_display 需要转换的值
     * @return
     */
    public static String getGameDwncnt(String download_display) {
        try {
            int mDwnCnt = Integer.parseInt(download_display);
            if (mDwnCnt > 10000) {
                if (mDwnCnt % 10000 > 5000) {
                    return (mDwnCnt / 10000) + 1 + "万次下载";
                }
                return mDwnCnt / 10000 + "万次下载";
            } else {
                return mDwnCnt + "次下载";
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0 + "次下载";
    }


    /**
     * 游戏下载
     *
     * @param mContext
     * @param mGame
     * @param pageName
     * @param download_mode
     * @param download_type
     */
    public static void gameDown(Context mContext, GameBaseDetail mGame, String pageName, boolean showRedPoint, String download_mode, String download_type, StatisticsEventData eventData) {
        if (mGame.getState() == DownloadState.downInProgress) {
            return;
        }

        if (mGame.getState() == InstallState.upgrade) {
            State.updatePrevState(mGame, InstallState.upgrade);
        }
        LogUtils.d("hhh", "触发下载游戏，上报请求走了");
        FileDownloaders.download(mContext, mGame, download_mode, download_type, pageName, eventData, false, false, 0);
        if (showRedPoint) {
            EventBus.getDefault().post(new RedPointEvent(true));
        }
    }

    /**
     * 游戏下载(预约wifi下载)
     *
     * @param mContext
     * @param mGame
     * @param pageName
     * @param download_mode
     * @param download_type
     */
    public static void gameDownByOrderWifi(Context mContext, GameBaseDetail mGame, String pageName, boolean showRedPoint, String download_mode, String download_type, StatisticsEventData eventData) {
        if (mGame.getState() == InstallState.upgrade) {
            State.updatePrevState(mGame, InstallState.upgrade);
        }
//        DCStat.downloadRequestEvent(mGame, pageName, NetUtils.getNetworkType(mContext), null, false, download_mode, download_type);
        LogUtils.d("ccc", "Utils 执行预约下载了");
        FileDownloaders.orderWifiDownload(mContext, mGame, download_mode, download_type, pageName, eventData, false);
        if (showRedPoint) {
//            HomeActivity.showRedPoint(true);
            EventBus.getDefault().post(new RedPointEvent(true));
        }
    }

    /**
     * 是否完成改url的预下载
     *
     * @param context
     * @param url
     * @return
     */
    public static boolean isPreDownload(Context context, String url, String md5, String packageName) {
        if (url == null || url.length() < 1) {
            return false;
        }

        if (AppIsInstalledUtil.isInstalled(context, packageName)) {
            return true;
        }
        String urlMd5 = AdMd5.MD5(url);
        SharedPreferencesUtil share = new SharedPreferencesUtil(context);
        String path = share.get(SharedPreferencesUtil.downloadFilePath);
        // String path = Constant.APP_PATH + File.separator + urlMd5 + ".apk";
        if (path == null || path.equals("")) {
            return false;
        }
        File file = new File(path);
        return "1".equals(share.get(urlMd5)) && file.exists() && AdMd5.md5sum(path).equalsIgnoreCase(md5);
    }

    // 将Bitmap转换成InputStream
    public static InputStream Bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    public static PopupWindow albumSelectPopWindow(final Fragment fragment) {
        return albumSelectPopWindow(fragment.getActivity());
    }

    public static PopupWindow albumSelectPopWindow(final Activity activity) {
        final PopupWindow pop = new PopupWindow(activity.getApplicationContext());
        View view = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.album_select_popwindow_layout, null);
        pop.setContentView(view);
        pop.setWidth(LayoutParams.MATCH_PARENT);
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setFocusable(true);
        pop.setOutsideTouchable(false);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        backgroundAlpha(activity, 0.5f);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        pop.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                backgroundAlpha(activity, 1f);
            }
        });

        view.findViewById(R.id.album).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent intent = new Intent(Intent.ACTION_PICK);
                /* 开启Pictures画面Type设定为image */
                intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
//                 intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.putExtra("crop", "true");
//                // aspectX aspectY 是宽高的比例
//                intent.putExtra("aspectX", 1);
//                intent.putExtra("aspectY", 1);
//                // outputX outputY 是裁剪图片宽高
//                intent.putExtra("outputX", 100);
//                intent.putExtra("outputY", 100);
//                intent.putExtra("return-data", true);
//                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//                intent.putExtra("noFaceDetection", true);
                // intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                /* 取得相片后返回本画面 */
//                activity.startActivityForResult(intent, 1);
                activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                pop.dismiss();
            }
        });

        view.findViewById(R.id.camera).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                File fos = null;
                try {
                    fos = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ltgame.jpg");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Uri u;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    u = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileProvider", fos);
                } else {
                    u = Uri.fromFile(fos);
                }

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                i.putExtra(MediaStore.EXTRA_OUTPUT, u);

                activity.startActivityForResult(i, 2);

                pop.dismiss();
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });


        return pop;
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    private static void backgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        activity.getWindow().setAttributes(lp);
    }

    public static String getImagePath(Context context, Uri uri) {
        if (null == uri) {
            LogUtils.e("getImagePath", "uri return null");
            return null;
        }

        LogUtils.e("getImagePath", uri.toString());
        String path = null;
        final String scheme = uri.getScheme();
        if (null == scheme) {
            path = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            path = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            int nPhotoColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (null != cursor) {
                cursor.moveToFirst();
                path = cursor.getString(nPhotoColumn);
            }
            cursor.close();
        }

        return path;
    }

    public static void copy(Context context, String copyText) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
            ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(copyText);
        } else {
            android.content.ClipboardManager cmb = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(copyText);
        }

    }

    /**
     * 得到View宽高
     *
     * @param view
     * @return
     */
    public static int[] getWidthAndHeight(View view) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, heightSpec);
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
        return new int[]{width, height};
    }

    /**
     * 判断手机是否root
     *
     * @return
     */
    public boolean isRoot() {
        boolean bool = false;

        try {
            bool = !((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists()));
            LogUtils.d("TAG", "bool = " + bool);
        } catch (Exception e) {

        }
        return bool;
    }

    /**
     * 判断一个String是否是纯数字
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            if (ch[i] < '0' || ch[i] > '9') {
                return false;
            }
        }
        return true;
    }

    public static List<String> getVolumePaths(Context mContext) {
        List<String> paths = new ArrayList<String>();
        StorageManager storageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            LogUtils.e("SettingPathDialog", Environment.getExternalStorageDirectory().getPath());
            for (int i = 0; i < ((String[]) invoke).length; i++) {
                LogUtils.e("SettingPathDialog", ((String[]) invoke)[i]);
                LogUtils.e("SettingPathDialog", (new File(((String[]) invoke)[i]).canWrite() + ""));
                if (new File(((String[]) invoke)[i]).canWrite()) {
                    paths.add(((String[]) invoke)[i]);
                }
//                LogUtils.e("SettingPathDialog", Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)+"");
            }
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return paths;
    }

    /***
     * 判断服务对象是否在运行
     *
     * @param context
     * @param clazz
     * @return
     */
    public static boolean isServiceRunning(Context context, Class<? extends Service> clazz) {

        //1. 得到任务管理器对象
        android.app.ActivityManager manager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //2. 获取所有正在运行的服务
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);

        //3. 遍历集合
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {

            //4. 取出每个服务的名字
            String serviceName = runningServiceInfo.service.getClassName();
            if (serviceName.equals(clazz.getName())) {
                return true;
            }
        }

        return false;
    }

    private static long lastClickTime;

    /**
     * 判定是否快速点击
     *
     * @return
     */
    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
