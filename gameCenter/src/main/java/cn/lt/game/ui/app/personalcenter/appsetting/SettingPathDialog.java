package cn.lt.game.ui.app.personalcenter.appsetting;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import cn.lt.game.R;
import cn.lt.game.global.LogTAG;
import cn.lt.game.application.MyApplication;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.file.TTGC_DirMgr;
import cn.lt.game.model.GameBaseDetail;


/**
 * Created by zhengweijian on 15/8/25.
 */
public class SettingPathDialog extends Dialog implements View.OnClickListener {
    public static Uri uri;
    private int height;
    private int width;
    private TextView tvPhoneSize;
    private TextView tvSdCardSize;
    private FrameLayout flRootLayout;
    private Context mContext;
    private MyApplication application;

    private long LSDCardSize;
    private long LphoneSize;

    public SettingPathDialog(Context context) {
        super(context, R.style.updateInfoDialogStyle);
        mContext = context;
        application = (MyApplication) context.getApplicationContext();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        height = dm.heightPixels; // 高度设置为屏幕的0.6，根据实际情况调整
        width = dm.widthPixels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_sdcard_size);
        TTGC_DirMgr.init();
        initView();
        initState(width, height);


        LphoneSize = getAvailableSize(TTGC_DirMgr.PHONE_STORAGE_ROOT_PATH);
        String phoneSize = Utils.converByteToGOrM(LphoneSize);
        LSDCardSize = getAvailableSize(TTGC_DirMgr.SDCARD_ROOT_PATH);
        String SDCardSize = Utils.converByteToGOrM(LSDCardSize);
        Resources res = mContext.getResources();
        String textPhoneSize = String.format(res.getString(R.string.user_center_phone_size_setting), phoneSize);
        String textSDCardSize = String.format(res.getString(R.string.user_center_sdcard_size_setting), SDCardSize);

        tvPhoneSize.setText(textPhoneSize);
        tvSdCardSize.setText(textSDCardSize);
        if (TTGC_DirMgr.SAVE_SIGN == TTGC_DirMgr.saveToPhoneStorage) {
            tvPhoneSize.setTextColor(mContext.getResources().getColor(R.color.theme_green));
            tvSdCardSize.setTextColor(mContext.getResources().getColor(R.color.light_black));
        } else if (TTGC_DirMgr.SAVE_SIGN == TTGC_DirMgr.saveToSD) {
            tvPhoneSize.setTextColor(mContext.getResources().getColor(R.color.light_black));
            tvSdCardSize.setTextColor(mContext.getResources().getColor(R.color.theme_green));
        }

    }

    private void initView() {
        flRootLayout = (FrameLayout) findViewById(R.id.sdcard_size_root);
        tvPhoneSize = (TextView) findViewById(R.id.setting_phone_size);
        tvSdCardSize = (TextView) findViewById(R.id.setting_sdcard_size);
        View setting_sdLine = findViewById(R.id.setting_sdLine);

        if(!TTGC_DirMgr.hasSdCard()) {
            setting_sdLine.setVisibility(View.GONE);
            tvSdCardSize.setVisibility(View.GONE);
        }
        flRootLayout.setOnClickListener(this);
        tvPhoneSize.setOnClickListener(this);
        tvSdCardSize.setOnClickListener(this);
    }

    private void initState(int width, int height) {

        WindowManager.LayoutParams p = this.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = width;
        p.height = height;
        this.getWindow().setAttributes(p);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_phone_size:
                if (LphoneSize != 0) {
                    if(TTGC_DirMgr.SAVE_SIGN == TTGC_DirMgr.saveToSD && thereAreTasks()) {
                        showCannotChange();
                        break;
                    }
                    TTGC_DirMgr.setSaveSign(TTGC_DirMgr.saveToPhoneStorage);
                    TTGC_DirMgr.saveRootDirectory(TTGC_DirMgr.PHONE_STORAGE_ROOT_PATH + TTGC_DirMgr.APK_RELATIVE_PATH);
                    LogUtils.i(LogTAG.DirErrorTAG, "选中phoneCardPath = " + TTGC_DirMgr.GAME_CENTER_ROOT_DIRECTORY);


                    tvPhoneSize.setTextColor(mContext.getResources().getColor(R.color.theme_green));
                    tvSdCardSize.setTextColor(mContext.getResources().getColor(R.color.light_black));
                } else {
                    ToastUtils.showToast(mContext, "内存不足，不能选择");
                }
                dismiss();
                break;
            case R.id.setting_sdcard_size:
                if (LSDCardSize != 0) {
                    if(TTGC_DirMgr.SAVE_SIGN == TTGC_DirMgr.saveToPhoneStorage && thereAreTasks()) {
                        showCannotChange();
                        break;
                    }
                    TTGC_DirMgr.setSaveSign(TTGC_DirMgr.saveToSD);
                    TTGC_DirMgr.saveRootDirectory(TTGC_DirMgr.SDCARD_ROOT_PATH + TTGC_DirMgr.APK_RELATIVE_PATH);
                    LogUtils.i(LogTAG.DirErrorTAG, "选中SDCardPath = " + TTGC_DirMgr.GAME_CENTER_ROOT_DIRECTORY);

                    tvPhoneSize.setTextColor(mContext.getResources().getColor(R.color.light_black));
                    tvSdCardSize.setTextColor(mContext.getResources().getColor(R.color.theme_green));
                } else {
                    ToastUtils.showToast(mContext, "内存不足，不能选择");
                }
                dismiss();
                break;
            case R.id.sdcard_size_root:
                dismiss();
                break;
            default:
        }
    }

    private void showCannotChange() {
        ToastUtils.showToast(mContext, "切换失败，请先删除下载/升级任务后切换路径！");
    }

    /** 判断是否存在下载任务或者升级中的任务*/
    private boolean thereAreTasks() {
        // 是否有下载任务
        List<GameBaseDetail> allDownloadFileList = FileDownloaders.getAllDownloadFileInfo();
        for (GameBaseDetail game : allDownloadFileList) {
            if(isInDownloadTask(game.getState())) {
                LogUtils.i(LogTAG.DirErrorTAG, "目前拥有下载/升级任务哦  " + game.getName() + "，，state = " + game.getState());
                return true;
            }
        }

        LogUtils.i(LogTAG.DirErrorTAG, "没有存在下载任务或者升级中的任务");

        return false;
    }

    private boolean isInDownloadTask(int state) {
        return state == DownloadState.downloadPause
                || state == DownloadState.downloadFail
                || state == DownloadState.waitDownload
                || state == DownloadState.downInProgress;
    }


    //获取当前路径，可用空间
    private static long getAvailableSize(String path) {
        long nAvailableCount = 0;
        try {
            StatFs stat = new StatFs(path);
            nAvailableCount = stat.getBlockSize() * ((long) stat.getAvailableBlocks());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return nAvailableCount;
        }
    }

}
