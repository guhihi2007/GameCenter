package cn.lt.game.ui.app.awardgame;

import java.util.Comparator;

import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.install.InstallState;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.ui.app.adapter.data.ItemData;

/**
 * Created by honaf on 2017/8/3.
 * 优先级：打开领取》安装》其他
 */

public class ScoreComparator implements Comparator<ItemData<? extends BaseUIModule>> {

    @Override
    public int compare(ItemData<? extends BaseUIModule> o1, ItemData<? extends BaseUIModule> o2) {

        UIModule<GameDomainBaseDetail> module1 = (UIModule<GameDomainBaseDetail>) o1.getmData();
        GameBaseDetail gameDetailForDownload1 = new GameBaseDetail().setGameBaseInfo(module1.getData());
        GameBaseDetail downFile1 = FileDownloaders.getDownFileInfoById(gameDetailForDownload1.getId());
        if (downFile1 != null) {
            gameDetailForDownload1.setDownInfo(downFile1);
        } else {
            gameDetailForDownload1.setState(DownloadState.undownload);
            gameDetailForDownload1.setDownLength(0);
        }

        UIModule<GameDomainBaseDetail> module2 = (UIModule<GameDomainBaseDetail>) o2.getmData();
        GameBaseDetail gameDetailForDownload2 = new GameBaseDetail().setGameBaseInfo(module2.getData());
        GameBaseDetail downFile2 = FileDownloaders.getDownFileInfoById(gameDetailForDownload2.getId());
        if (downFile2 != null) {
            gameDetailForDownload2.setDownInfo(downFile2);
        } else {
            gameDetailForDownload2.setState(DownloadState.undownload);
            gameDetailForDownload2.setDownLength(0);
        }
        int state1 = gameDetailForDownload1.getState();
        int state2 = gameDetailForDownload2.getState();
        switch (state1) {
            case InstallState.installComplete:
                if (state2 == InstallState.installComplete) {
                    return 0;
                }
                return -1;
            case InstallState.install:
                if (state2 == InstallState.installComplete) {
                    return 1;
                }
                if(state2 == InstallState.install) {
                    return 0;
                }
                return -1;
            default:
                if (state2 == InstallState.installComplete || state2 == InstallState.install) {
                    return 1;
                }
                return 0;
        }
    }
}
