package cn.lt.game.ui.installbutton;

import android.graphics.Color;
import android.preference.ListPreference;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.text.DecimalFormat;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.db.service.DownFileService;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.global.Constant;
import cn.lt.game.model.GameBaseDetail;

public class ActivityUpdateButtonState extends InstallButton {
    protected Button btn;
    protected ProgressBar pb;
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public ActivityUpdateButtonState(GameBaseDetail game, Button btn, ProgressBar pb) {
        super(game);
        this.btn = btn;
        this.pb = pb;
    }

    private void coverHandle() {
        if (game != null && game.isCoveredApp) {
            onInstallComplete();
        }
    }

    @Override
    protected void onIgnoreUpgrade() {
        pb.setVisibility(View.GONE);
        btn.setText(R.string.update);
        btn.setTextColor(Color.parseColor("#86bf13"));
        btn.setBackgroundResource(R.drawable.btn_orange_selector);

        coverHandle();
    }

    @Override
    protected void onInstallFail() {
        pb.setVisibility(View.GONE);
        btn.setText(R.string.retry);
        btn.setTextColor(Color.parseColor("#86bf13"));
        btn.setBackgroundResource(R.drawable.btn_green_selector);

        coverHandle();
    }

    @Override
    protected void onUpgrade() {

        pb.setVisibility(View.GONE);
        btn.setText(R.string.update);
        btn.setTextColor(Color.parseColor("#86bf13"));
        btn.setBackgroundResource(R.drawable.btn_orange_selector);

        coverHandle();
    }

    @Override
    protected void onInstallComplete() {
        pb.setVisibility(View.GONE);
        if (game.isAccepted() == 1) {
            btn.setText(R.string.accepted);
        } else {
            if (Constant.FROM_ACTIVITY.equals(game.getDownloadFrom())) {
                btn.setText(R.string.open_accept);
            } else {
                btn.setText(R.string.open);
                FileDownloaders.update(game);
                DownFileService.getInstance(MyApplication.application).save(game);
            }

        }

        btn.setBackgroundResource(R.drawable.btn_open_selector);
        btn.setTextColor(Color.parseColor("#ff8800"));

    }

    @Override
    protected void onInstall() {
        pb.setVisibility(View.GONE);
        btn.setText(R.string.install);
        btn.setTextColor(Color.parseColor("#ffffff"));
        btn.setBackgroundResource(R.drawable.btn_install_selector);

        coverHandle();
    }

    @Override
    protected void onWaitDownload(int downPercent) {
        pb.setVisibility(View.VISIBLE);
        pb.setMax(10000);
        pb.setProgress(game.getDownPercent());
        btn.setText(R.string.wait);
        btn.setTextColor(Color.parseColor("#ffffff"));
        btn.setBackgroundColor(Color.TRANSPARENT);

        coverHandle();
    }

    @Override
    protected void onDownloadFail(int downPercent) {
        pb.setVisibility(View.VISIBLE);
        pb.setMax(10000);
        pb.setProgress(game.getDownPercent());
        btn.setText(R.string.retry);
        btn.setTextColor(Color.parseColor("#86bf13"));
        btn.setBackgroundResource(R.drawable.btn_green_selector);

        coverHandle();
    }

    @Override
    protected void onDownloadComplete(int downPercent) {
        pb.setVisibility(View.GONE);
        btn.setText(R.string.install);
        btn.setTextColor(Color.parseColor("#ffffff"));
        btn.setBackgroundResource(R.drawable.btn_install_selector);

        coverHandle();
    }

    @Override
    protected void onDownloadPause(int downPercent) {
        pb.setVisibility(View.VISIBLE);
        pb.setMax(10000);
        pb.setProgress(game.getDownPercent());
        btn.setText(R.string.go_on);
        btn.setTextColor(Color.parseColor("#ffffff"));
        btn.setBackgroundColor(Color.TRANSPARENT);

        coverHandle();
    }

    @Override
    protected void onDownInProgress(int downPercent) {
        pb.setMax(10000);
        pb.setProgress(downPercent);
        pb.setVisibility(View.VISIBLE);
        btn.setBackgroundColor(Color.TRANSPARENT);
        btn.setTextColor(Color.parseColor("#ffffff"));
        String perStr = decimalFormat.format(downPercent / 100f);
        btn.setText(perStr + "%");

        coverHandle();
    }

    @Override
    protected void onUndownload() {
        pb.setVisibility(View.GONE);
        btn.setText(R.string.download);
        btn.setTextColor(Color.parseColor("#86bf13"));
        btn.setBackgroundResource(R.drawable.btn_green_selector);

        coverHandle();
    }

    @Override
    protected void onCheck() {
        pb.setVisibility(View.GONE);
        btn.setText("校验中");
        btn.setTextColor(Color.parseColor("#ffffff"));
        btn.setBackgroundResource(R.drawable.btn_install_selector);

        coverHandle();
    }

    @Override
    protected void onInstalling() {
        pb.setVisibility(View.GONE);
        btn.setText(R.string.installing);
        btn.setTextColor(Color.parseColor("#ffffff"));
        btn.setBackgroundResource(R.drawable.btn_install_selector);

        coverHandle();
    }

}
