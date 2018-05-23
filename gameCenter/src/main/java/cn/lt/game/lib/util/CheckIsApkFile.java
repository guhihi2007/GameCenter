package cn.lt.game.lib.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileInputStream;

/**
 * @author chengyong
 * @time 2017/5/4 16:18
 * @des 安装时检查是否是apk文件，是否是解析包出错
 */

public class CheckIsApkFile {

    public static boolean isApkFile(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            byte[] bys = new byte[64];   //TODO 是否会越界
            int len = fis.read(bys);
            String result = new String(bys, 0, len);
            String realdecodeResult=new String(result.getBytes("iso8859-1"), "UTF-8");
            Log.e("juice", "读出的结果 iso88格式==>" + realdecodeResult);
            fis.close();
//            备注：通常格式有GBK、UTf-8、iso8859-1、GB2312，如果上面的强制转换不成功，依次进行这些格式的尝试，肯定是可以解决问题的。
            if(realdecodeResult.startsWith("PK") && (realdecodeResult.contains("META-INF/MANIFEST")
                    ||realdecodeResult.contains("AndroidManifest.xml"))){
                Log.e("juice", "读流确定是apk文件");
                return true;
            }else{
                Log.e("juice", "读流确定 不是apk文件");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("juice", "读取抛异常==>" + e.getMessage());
            return false;
        }
    }

    /**
     * 条件：单线程下载，没有临时文件
     * @param filePath
     */
    public static boolean checkIsApkisComplete(String filePath, Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            Log.e("juice", filePath);
            PackageInfo info = pm.getPackageArchiveInfo(filePath,
                    PackageManager.GET_ACTIVITIES);
            if(info==null){
                Log.e("juice", "PackageInfo为空，有可能是apk文件，也有可能不是,需要读流进一步检查;" +
                        "解析包出错"); //TODO上报解析包出错，以及以后执行重新下载的逻辑
//                ifApkFile(filePath);
                return false;
            }else{
                Log.e("juice", "PackageInfo不为空，就是完整的apk文件");
                Log.e("juice", "PackageInfo 完整正常的apk:包名是==>"+info.packageName);

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 通过路径拿包名
     * @param filePath
     */
    public static String getPackageNameByDownPath(String filePath, Context context) {
        try {
            if(TextUtils.isEmpty(filePath))return null;
            PackageManager pm = context.getPackageManager();
            Log.e("juice", filePath);
            PackageInfo info = pm.getPackageArchiveInfo(filePath,
                    PackageManager.GET_ACTIVITIES);
            if(info==null){
                Log.e("juice", "PackageInfo为空，有可能是apk文件，也有可能不是,需要读流进一步检查;" +
                        "解析包出错"); //TODO上报解析包出错，以及以后执行重新下载的逻辑
//                ifApkFile(filePath);
                return null;
            }else{
                Log.e("juice", "PackageInfo 下载完成、安装请求时的real包名是==>"+info.packageName);
                return info.packageName;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
