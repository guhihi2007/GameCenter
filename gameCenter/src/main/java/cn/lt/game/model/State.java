package cn.lt.game.model;

import java.util.List;

import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.install.InstallState;

public class State {

    public static void updateState(GameBaseDetail downFile, int state) {
        FileDownloaders.setState(downFile.getDownUrl(), state);
        downFile.setState(state);
    }

    public static void updatePrevState(GameBaseDetail downFile, int state) {
        FileDownloaders.setPrevState(downFile.getDownUrl(), state);
        downFile.setPrevState(state);
    }

    public static boolean isInstallState(int state) {
        return isProgressState(state)
                || state == DownloadState.downloadPause
                || state == DownloadState.downloadFail
                || state == InstallState.installFail;
    }

    public static boolean isProgressState(int state) {
        return state == DownloadState.downInProgress
                || state == DownloadState.waitDownload
                || state == InstallState.install
                || state == DownloadState.downloadComplete
                || state == InstallState.installing;
    }

    public static boolean isStateCanPause(int state) {
        return state == DownloadState.downInProgress
                || state == DownloadState.waitDownload;
    }

    public static boolean isAnyGameDownloading(List<GameBaseDetail> games) {
        if (games == null) {
            return false;
        }
        for (GameBaseDetail game : games) {
            int state = FileDownloaders.getState(game.getDownUrl());
            if (State.isProgressState(state)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasGameDownloading(List<String> urls) {
        if (urls == null) {
            return false;
        }
        for (String url : urls) {
            int state = FileDownloaders.getState(url);
            if (State.isProgressState(state)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isAnyGameDownloading(GameBaseDetail game) {
        if (game == null) {
            return false;
        }
        int state = FileDownloaders.getState(game.getDownUrl());
        return State.isProgressState(state);

    }

    public static String toString(int state) {
        switch (state) {
            case DownloadState.downInProgress:
                return "downInProgress";
            case DownloadState.downloadComplete:
                return "downloadComplete";
            case DownloadState.downloadFail:
                return "downloadFail";
            case DownloadState.downloadPause:
                return "downloadPause";
            case DownloadState.invalid:
                return "invalid";
            case DownloadState.undownload:
                return "undownload";
            case DownloadState.waitDownload:
                return "waitDownload";
            case InstallState.ignore_upgrade:
                return "ignore_upgrade";
            case InstallState.installComplete:
                return "installComplete";
            case InstallState.installFail:
                return "installFail";
            case InstallState.install:
                return "install";
            case InstallState.upgrade:
                return "upgrade";
            default:
                return null;
        }
    }
}
