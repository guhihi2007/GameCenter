package cn.lt.game.ui.installbutton;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;

import cn.lt.game.R;
import cn.lt.game.model.GameBaseDetail;

/**
 * 详情里面的按钮控件
 */
public class DetailInstallButton extends InstallButton {

    private ProgressBar downProgress;
    private TextView btnDownloadCtrl;
    private String title;
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public DetailInstallButton(GameBaseDetail game, ProgressBar downProgress, TextView btnDownloadCtrl, String title) {
        super(game);
        this.downProgress = downProgress;
        this.btnDownloadCtrl = btnDownloadCtrl;
        this.title = title;
    }

    @Override
    protected void onIgnoreUpgrade() {
        onUpgrade();
    }

    @Override
    protected void onInstallFail() {
        btnDownloadCtrl.setGravity(Gravity.CENTER);
        btnDownloadCtrl.setText(R.string.retry);
        btnDownloadCtrl.setTextColor(Color.parseColor("#ffffff"));
        btnDownloadCtrl.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onUpgrade() {
        btnDownloadCtrl.setGravity(Gravity.CENTER);
        btnDownloadCtrl.setText(R.string.update);
        btnDownloadCtrl.setTextColor(Color.parseColor("#ffffff"));
        btnDownloadCtrl.setBackgroundResource(R.drawable.deep_btn_green_selector);
    }

    @Override
    protected void onInstallComplete() {
        btnDownloadCtrl.setGravity(Gravity.CENTER);
        btnDownloadCtrl.setText(R.string.open);
        btnDownloadCtrl.setTextColor(Color.parseColor("#ffffff"));
        btnDownloadCtrl.setBackgroundResource(R.drawable.deep_btn_open_selector);
    }

    @Override
    protected void onInstall() {
        btnDownloadCtrl.setText(R.string.install);
        btnDownloadCtrl.setGravity(Gravity.CENTER);
        btnDownloadCtrl.setTextColor(Color.parseColor("#ffffff"));
        btnDownloadCtrl.setBackgroundResource(R.drawable.btn_install_selector);
    }

    @Override
    protected void onWaitDownload(int downPercent) {
        downProgress.setMax(10000);
        downProgress.setProgress(downPercent);
        btnDownloadCtrl.setGravity(Gravity.CENTER);
        btnDownloadCtrl.setText(R.string.wait);
        btnDownloadCtrl.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDownloadFail(int downPercent) {
        downProgress.setMax(10000);
        downProgress.setProgress(downPercent);
        btnDownloadCtrl.setGravity(Gravity.CENTER);
        btnDownloadCtrl.setText(R.string.retry);
        btnDownloadCtrl.setTextColor(Color.parseColor("#ffffff"));
        btnDownloadCtrl.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDownloadComplete(int downPercent) {
        downProgress.setMax(10000);
        downProgress.setProgress(downPercent);
        btnDownloadCtrl.setGravity(Gravity.CENTER);
        btnDownloadCtrl.setText(R.string.install);
        btnDownloadCtrl.setTextColor(Color.parseColor("#ffffff"));
        btnDownloadCtrl.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDownloadPause(int downPercent) {
        downProgress.setMax(10000);
        downProgress.setProgress(downPercent);
        btnDownloadCtrl.setGravity(Gravity.CENTER);
        btnDownloadCtrl.setText(R.string.go_on);
        btnDownloadCtrl.setTextColor(Color.parseColor("#ffffff"));
        btnDownloadCtrl.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDownInProgress(int downPercent) {
        downProgress.setMax(10000);
        downProgress.setProgress(downPercent);
        btnDownloadCtrl.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        String perStr = decimalFormat.format(downPercent / 100f);
        btnDownloadCtrl.setText(perStr + "%");
        btnDownloadCtrl.setGravity(Gravity.CENTER);
        btnDownloadCtrl.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onUndownload() {
        btnDownloadCtrl.setText(title);
        btnDownloadCtrl.setGravity(Gravity.CENTER);
        btnDownloadCtrl.setTextColor(Color.parseColor("#ffffff"));
        btnDownloadCtrl.setBackgroundResource(R.drawable.deep_btn_green_selector);
    }

    @Override
    protected void onCheck() {
        btnDownloadCtrl.setText("校验中");
        btnDownloadCtrl.setGravity(Gravity.CENTER);
        btnDownloadCtrl.setTextColor(Color.parseColor("#ffffff"));
        btnDownloadCtrl.setBackgroundResource(R.drawable.deep_btn_green_selector);
    }

    @Override
    protected void onInstalling() {
        btnDownloadCtrl.setText(R.string.installing);
        btnDownloadCtrl.setGravity(Gravity.CENTER);
        btnDownloadCtrl.setTextColor(Color.parseColor("#ffffff"));
        btnDownloadCtrl.setBackgroundResource(R.drawable.deep_btn_green_selector);
    }

}
