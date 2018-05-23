package cn.lt.game.ui.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

import cn.lt.game.lib.util.AdMd5;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.file.TTGC_DirMgr;

public class LoadingImgWorker {
    private static LoadingImgWorker instance;
    private SharedPreferences loadingImgSp;// 保存loading图片链接的sharedPreferences
    private SharedPreferences.Editor loadingImgEditor;// 保存loading图片链接的editor

    public static final String spKey_loadingImgDataJsonStr = "spKey_loadingImgDataJsonStr";

    public String LOADING_IMG_URL = "";
    public String LOADING_IMG_DATA_JSONstr = "";

    public void resetIMG_INFO() {
        LOADING_IMG_URL = "";
    }

    public void setLOADING_IMG_URL(String img_url) {
        LOADING_IMG_URL = img_url;
    }

    private LoadingImgWorker(Context context) {
        loadingImgSp = context.getSharedPreferences("loadingImg", Context.MODE_PRIVATE);
        loadingImgEditor = loadingImgSp.edit();
    }

    public static LoadingImgWorker getInstance(Context context) {
        if (instance == null) {
            synchronized (LoadingImgWorker.class) {
                if (instance == null) {
                    instance =  new LoadingImgWorker(context);
                }
            }
        }

        return instance;
    }

    public void downloadImg() {
        if (!TextUtils.isEmpty(LOADING_IMG_URL)) {

            final String oldImgMd5 = loadingImgSp.getString("img_md5", "");
            String oldImgpath = TTGC_DirMgr.getCachePicDirectory() + File.separator + oldImgMd5;
            final File oldImgFile = new File(oldImgpath);
            final String newImgMd5 = AdMd5.MD5(LOADING_IMG_URL);
            LogUtils.i("nidaye", "LOADING_IMG_URL =" + LOADING_IMG_URL + "\noldMd5=" + oldImgMd5 + "\nnewMd5=" + newImgMd5);

            if (!newImgMd5.equals(oldImgMd5) || !oldImgFile.exists()) {
                final String newPath = TTGC_DirMgr.getCachePicDirectory() + File.separator + newImgMd5;
                judgeOldMd5IsExists(oldImgMd5, newPath);
                LogUtils.i("nidaye", newPath + "\n" + LOADING_IMG_URL);

                new HttpUtils().download(LOADING_IMG_URL, newPath, true, false, new RequestCallBack<File>() {
                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        LogUtils.i("nidaye", total + "");
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        if (arg1.toString().equals("maybe the file has downloaded completely")) {
                            LogUtils.i("nidaye", "这张图片已经下载过了哦~" + " , 错误 ： " + arg1.toString());
                            loadingImgEditor.putString("img_md5", newImgMd5);
                            loadingImgEditor.commit();
                        } else {
                            LogUtils.i("nidaye", "下载失败了" + " , 错误 ： " + arg1.toString());
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> arg0) {
                        LogUtils.i("nidaye", "图片下载成功");

                        // 下载之后保存md5
                        loadingImgEditor.putString("img_md5", newImgMd5);
                        loadingImgEditor.commit();

                        if (!newImgMd5.equals(oldImgMd5) && oldImgFile.exists()) {
                            oldImgFile.delete();
                            LogUtils.i("nidaye", "新的图片下载成功，与newImage的md5不相同的oldImgFile删除掉了");
                        }


                    }
                });
            }
        }

        // 不管图片是否下载成功与否，也要保存跳转数据
        saveDataJsonStr();
    }

    /**
     * 解决app卸载后，重装APP新的启动图片下载不了
     */
    private void judgeOldMd5IsExists(String oldImgMd5, String newPath) {
        if (TextUtils.isEmpty(oldImgMd5)) {
            LogUtils.i("nidaye", "oldImgMd5不存在哦！");
            File newImgFile = new File(newPath);
            if (newImgFile.exists()) {
                LogUtils.i("nidaye", "newPath的file居然存在哦！干掉它！");
                newImgFile.delete();
            } else {
                LogUtils.i("nidaye", "newPath的file不存在!");
            }
        }

    }

    /** 保存启动页数据的JSON串*/
    private void saveDataJsonStr() {
        loadingImgEditor.putString(spKey_loadingImgDataJsonStr, LOADING_IMG_DATA_JSONstr);
        loadingImgEditor.commit();
    }

    public String getLoadingImgDataJsonStr() {
        return loadingImgSp.getString(spKey_loadingImgDataJsonStr, "");
    }

}
