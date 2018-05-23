package cn.lt.game.update;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.file.TTGC_DirMgr;


public class PlatUpdatePathManger {

    public static String getDownloadPath(Context context) {
        if (TextUtils.isEmpty(TTGC_DirMgr.GAME_CENTER_ROOT_DIRECTORY )) {
            TTGC_DirMgr.init();
        }
        return TTGC_DirMgr.GAME_CENTER_ROOT_DIRECTORY + File.separator + "update.apk.abyz";
    }

    public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if (file.exists()) {
            LogUtils.d(LogTAG.HTAG, "创建单个文件" + destFileName + "失败，目标文件已存在！");
            return false;
        }
        if (destFileName.endsWith(File.separator)) {
            LogUtils.d(LogTAG.HTAG, "创建单个文件" + destFileName + "失败，目标文件不能为目录！");
            return false;
        }
        //判断目标文件所在的目录是否存在
        if (!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            LogUtils.d(LogTAG.HTAG, "目标文件所在目录不存在，准备创建它！");
            if (!file.getParentFile().mkdirs()) {
                LogUtils.d(LogTAG.HTAG, "创建目标文件所在目录失败！");
                return false;
            }
        }
        //创建目标文件
        try {
            if (file.createNewFile()) {
                LogUtils.d(LogTAG.HTAG, "创建单个文件" + destFileName + "成功！");
                return true;
            } else {
                LogUtils.d(LogTAG.HTAG, "创建单个文件" + destFileName + "失败！");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.d(LogTAG.HTAG, "创建单个文件" + destFileName + "失败！" + e.getMessage());
            return false;
        }
    }

}
