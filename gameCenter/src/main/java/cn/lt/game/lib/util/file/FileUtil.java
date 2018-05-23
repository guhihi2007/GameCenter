package cn.lt.game.lib.util.file;

import android.content.Context;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.lt.game.application.MyApplication;
import cn.lt.game.lib.util.Utils;

import static cn.lt.game.lib.util.file.TTGC_DirMgr.APK_RELATIVE_PATH;

public class FileUtil {

    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }


    /**
     * 删除文件或文件夹
     *
     * @param f 要删除的文件；
     */
    public static boolean delFile(File f) {
        boolean flag = false;
        try {
            if (!f.exists()) {
                flag = false;
            } else {
                if (f.isFile()) {
                    flag = f.delete();
                } else {
                    // 先删除文件夹下的文件或子文件夹
                    String[] files = f.list();
                    for (int i = 0; i < files.length; i++) {
                        String p = f.getPath() + "/" + files[i];
                        delFile(new File(p));
                    }
                    // 再删除文件夹
                    flag = f.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除文件夹以及目录下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        // 如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        // 遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                // 删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                // 删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        // 删除当前空目录
        return dirFile.delete();
    }

    /**
     * 删除目录下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory2(String filePath) {
        boolean flag = false;
        // 如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        // 遍历删除文件夹下的所有文件(不包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                // 删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                // 删除子目录
                flag = deleteDirectory2(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        return flag;

    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param filePath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public boolean DeleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long 单位为M
     * @throws Exception
     */
    public static long getFolderSize(java.io.File file) throws Exception {
        long size = 0;
        java.io.File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                size = size + getFolderSize(fileList[i]);
            } else {
                size = size + fileList[i].length();
            }
        }
        return size;
    }

    public static float getFolderSizeM(java.io.File file) throws Exception {
        long size = getFolderSize(file);
        double sizeDouble = size / 1048576.0;
        BigDecimal bd = new BigDecimal(sizeDouble);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    /*
     * 给出路径，返回textInt[0]="已用大小/总大小" textInt[1] = "已用大小占总大小的百分比*1000" textInt[2]
     * = "剩余空间大小"
     */
    public static String[] getUsedAllSize(String pathString) {
        String[] textInt = new String[3];
        long allSize = getAllSize(pathString);
        if (allSize == 0) {
            return null;
        }
        long freeSize = getFreeSize(pathString);
        long usedSize = allSize - freeSize;
        textInt[0] = Utils.converByteToGOrM(usedSize) + "/" + Utils.converByteToGOrM(allSize);
        double percentSD = usedSize / (double) allSize;
        textInt[1] = String.valueOf(((int) (percentSD * 1000)));
        textInt[2] = Utils.converByteToGOrM(freeSize);
        return textInt;
    }

    /* 剩余空间 */
    public static long getFreeSize(String pathString) {
        // 取得SD卡文件路径
        File path = new File(pathString);// Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        // 返回SD卡空闲大小
        return freeBlocks * blockSize; // 单位Byte
        // return (freeBlocks * blockSize)/1024; //单位KB
        // return (freeBlocks * blockSize)/1024 /1024; //单位MB
    }

    /* 总空间 */
    public static long getAllSize(String pathString) {
        // 取得SD卡文件路径
        File path = new File(pathString);// Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 获取所有数据块数
        long allBlocks = sf.getBlockCount();
        // 返回SD卡大小
        return allBlocks * blockSize; // 单位Byte
        // return (allBlocks * blockSize)/1024; //单位KB
        // return (allBlocks * blockSize)/1024/1024; //单位MB
    }

    /**
     * 描述：获取网络文件的大小.
     *
     * @param Url 图片的网络路径
     * @return int 网络文件的大小
     */
    public static int getContentLengthFormUrl(String Url) {
        int mContentLength = 0;
        try {
            URL url = new URL(Url);
            HttpURLConnection mHttpURLConnection = (HttpURLConnection) url.openConnection();
            mHttpURLConnection.setConnectTimeout(5 * 1000);
            mHttpURLConnection.setRequestMethod("GET");
            mHttpURLConnection.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
            mHttpURLConnection.setRequestProperty("Accept-Language", "zh-CN");
            mHttpURLConnection.setRequestProperty("Referer", Url);
            mHttpURLConnection.setRequestProperty("Charset", "UTF-8");
            mHttpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
            mHttpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            mHttpURLConnection.connect();
            if (mHttpURLConnection.getResponseCode() == 200) {
                // 根据响应获取文件大小
                mContentLength = mHttpURLConnection.getContentLength();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mContentLength;
    }

//    public static boolean createTestFile(String path) {
//        if (Build.VERSION.SDK_INT >= 18) {
//            File file = new File(path + Constant.APK_RELATIVE_PATH_4_4 + "/test.txt");
//            if (!file.getParentFile().exists()) {
//                file.getParentFile().mkdirs();
//            }
//            if (!file.exists()) {
//                try {
//                    if (!file.createNewFile()) {
//                        return false;
//                    }
//                } catch (IOException e) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

    public static void saveJsonFile(String name, String data) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        String path = toCachePath(name);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(path);
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getJsonFile(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        String path = toCachePath(name);
        if (!new File(path).exists()) {
            return null;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String readLine;
            StringBuffer sb = new StringBuffer();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String toCachePath(String name) {
        return APK_RELATIVE_PATH + File.separator + name + ".json";
    }

    /**
     * 保存数据到缓存中
     *
     * @param filename
     * @param object
     */
    public static void saveDataToCache(String filename, Object object) {
        if (object != null) {
//            UserInfo userInfo = UserInfoManager.instance().getUserInfo();
//
//            filename += userInfo.mobile;
            File file = new File(filename);
            try {
                if (file.exists()) {
                    file.delete();
                }
                FileOutputStream ops = MyApplication.application.openFileOutput(filename,
                        Context.MODE_PRIVATE);
                ObjectOutputStream outputStream = new ObjectOutputStream(ops);
                outputStream.writeObject(object);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                Log.w(filename, e.toString());
            }
        }
    }

    /**
     * 读取缓存
     *
     * @param filename
     * @return
     */
    public static Object getDataFromCache(String filename) {
        Object obj = null;
//        UserInfo userInfo = UserInfoManager.instance().getUserInfo();
//
//        filename += userInfo.mobile;
        try {
            InputStream ies = MyApplication.application.openFileInput(filename);
            ObjectInputStream obi = new ObjectInputStream(ies);
            obj = obi.readObject();
            obi.close();
            ies.close();
        } catch (Exception e) {
            Log.w(filename, e.toString());
        }
        return obj;
    }

}
