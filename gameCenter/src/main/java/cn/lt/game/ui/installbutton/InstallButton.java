package cn.lt.game.ui.installbutton;

import cn.lt.game.download.DownloadState;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.model.GameBaseDetail;

public abstract class InstallButton {
    public GameBaseDetail game;

    InstallButton(GameBaseDetail game) {
        this.game = game;
    }

    public void setViewBy(int state, int downPercent) {
        // 当状态设置为未下载时，需再次验证是否为已安装应用，进而修正状态
        if (state == DownloadState.undownload) {
            if ((null != game) && AppUtils.isInstalled(game.getPkgName())) {
                state = InstallState.installComplete;
            }
        }

        if (state == DownloadState.undownload) {
            onUndownload();

        } else if (state == DownloadState.downInProgress) {

            onDownInProgress(downPercent);

        } else if (state == DownloadState.downloadPause) {

            onDownloadPause(downPercent);

        } else if (state == DownloadState.downloadComplete) {

            onDownloadComplete(downPercent);

        } else if (state == DownloadState.downloadFail) {

            onDownloadFail(downPercent);

        } else if (state == DownloadState.waitDownload) {

            onWaitDownload(downPercent);

        } else if (state == InstallState.install) {

            onInstall();

        } else if (state == InstallState.installing) {

            onInstalling();

        } else if (state == InstallState.installComplete) {

            onInstallComplete();

        } else if (state == InstallState.upgrade) {

            onUpgrade();

        } else if (state == InstallState.ignore_upgrade) {

            onIgnoreUpgrade();

        } else if (state == InstallState.installFail) {

            onInstallFail();

        } else if (state == InstallState.check) {

            onCheck();

        }
    }

    protected abstract void onInstalling();

    protected abstract void onIgnoreUpgrade();

    protected abstract void onInstallFail();

    protected abstract void onUpgrade();

    protected abstract void onInstallComplete();

    protected abstract void onInstall();

    protected abstract void onWaitDownload(int downPercent);

    protected abstract void onDownloadFail(int downPercent);

    protected abstract void onDownloadComplete(int downPercent);

    protected abstract void onDownloadPause(int downPercent);

    protected abstract void onDownInProgress(int downPercent);

    protected abstract void onUndownload();

    protected abstract void onCheck();
}
