package cn.lt.game.ui.installbutton;

import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.lib.StringUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.model.GameBaseDetail;

public class ManageInstallButton extends IndexUpdateButtonState {

    private TextView trafficView;
    private String traffic;
    private ImageView networkIndication;
    private TextView managementDownSize;

    public ManageInstallButton(GameBaseDetail game, Button btn, ProgressBar pb, TextView traffic, ImageView networkIndication, TextView managementDownSize) {
        super(game, btn, pb);
        this.trafficView = traffic;
        this.networkIndication = networkIndication;
        this.managementDownSize = managementDownSize;
    }


    public void setViewBy(int state, int downPercent, String traffic) {
        this.traffic = traffic;
        super.setViewBy(state, downPercent);
    }

    @Override
    protected void onInstallFail() {
        super.onInstallFail();
        trafficView.setText(R.string.install_fail_retry);
        showErrorIcon();
    }

    @Override
    protected void onInstall() {
        super.onInstall();
        trafficView.setText(R.string.down_complete_install);
        showNetworkIcon();
    }

    @Override
    protected void onInstalling() {
        super.onInstalling();
        trafficView.setText("下载完成，安装中");
        showNetworkIcon();
    }

    @Override
    protected void onWaitDownload(int downPercent) {
        super.onWaitDownload(downPercent);
        trafficView.setText(R.string.waiting);
        showNetworkIcon();
    }

    @Override
    protected void onDownloadFail(int downPercent) {
        super.onDownloadFail(downPercent);
        String failReason = game.getDownloadFailedReason();
        failReason = failReason.trim();
        if (StringUtils.isEmpty(failReason)) {
            trafficView.setText(R.string.download_fail);
        } else {
            trafficView.setText(failReason);
        }

        showErrorIcon();

    }

    @Override
    protected void onDownloadComplete(int downPercent) {
        super.onDownloadComplete(downPercent);
        trafficView.setText(R.string.down_complete_install);
        showNetworkIcon();
    }

    @Override
    protected void onDownloadPause(int downPercent) {
        super.onDownloadPause(downPercent);
        // 判断是否预约wifi下阿紫
        if (FileDownloaders.judgeIsOrderWifiDownload(game.getId())) {
            trafficView.setText("等待WIFI继续下载");
        } else {
            trafficView.setText(R.string.already_pause);
        }
        showNetworkIcon();
    }

    @Override
    protected void onDownInProgress(int downPercent) {
        super.onDownInProgress(downPercent);
        trafficView.setText(this.traffic);
        showNetworkIcon();
    }

    private void showNetworkIcon() {
        if (networkIndication != null) {
            // 网络状态 图标
            switch (NetUtils.getNetType(MyApplication.application)) {
                case -1:// 无网络状态
                    networkIndication.setVisibility(View.GONE);
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    networkIndication.setVisibility(View.VISIBLE);
                    networkIndication.setImageResource(R.mipmap.wifi_indication);
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    networkIndication.setVisibility(View.VISIBLE);
                    networkIndication.setImageResource(R.mipmap.ng_indication);
                    break;
                default:
                    break;
            }
        }
        managementDownSize.setVisibility(View.VISIBLE);
    }

    private void showErrorIcon() {
        if (networkIndication != null) {
            networkIndication.setVisibility(View.VISIBLE);
            networkIndication.setImageResource(R.mipmap.ic_error);
        }
        //不显示size
        managementDownSize.setVisibility(View.INVISIBLE);

        trafficView.setVisibility(View.VISIBLE);
        if (trafficView != null && TextUtils.isEmpty(trafficView.getText())) {
            trafficView.setText(R.string.download_fail);
        }
    }
}
