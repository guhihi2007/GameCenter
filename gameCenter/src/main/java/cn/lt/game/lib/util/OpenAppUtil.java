package cn.lt.game.lib.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import cn.lt.game.db.service.DownFileService;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.lib.util.deeplink.DeepLinkUtil;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.model.State;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.gamegift.GiftManger;

public class OpenAppUtil {

    /**
     * 打开应用；
     *
     * @param mGame
     * @param mContext
     * @return true---打开成功； false----打开失败；
     */
    public static boolean openApp(GameBaseDetail mGame, Context mContext, String from) {
        if (mGame == null) return false;
        String deeplink = mGame.getDeeplink();

        if(DeepLinkUtil.APPSTORE_PKG.equals(mGame.getPkgName()) ) {
            if(TextUtils.isEmpty(deeplink)) {
                GameBaseDetail gameBaseDetail = DownFileService.getInstance(mContext).getDownFileByPkg(mGame.getPkgName());
                if(gameBaseDetail != null) {
                    deeplink = gameBaseDetail.getDeeplink();
                }
            }else {
                DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, from, 0,
                        "game", 0, "" + mGame.getId(), "",
                        null, "open", mGame.getPkgName(), ""));
            }
        }

        try {
            if (DeepLinkUtil.isExistApp(mContext, deeplink)) {
                DeepLinkUtil.openApp(mContext, deeplink, from);
                //打开后将deeplink置为空
                DownFileService.getInstance(mContext).updateDeeplinkByPkg(mGame.getPkgName(),"");
            } else {
                FileDownloaders.setOpenTime(mGame.getPkgName(), System.currentTimeMillis());
                PackageManager pm = mContext.getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(mGame.getPkgName());
                mContext.startActivity(intent);
            }
            return true;
        } catch (Exception e) {
            mGame.setDownLength(0);
            FileDownloaders.setDownLength(mGame.getDownUrl(), 0);
            State.updateState(mGame, DownloadState.undownload);
            return false;

        }
    }


    /**
     * 打开应用；
     *
     * @param
     * @param mContext
     * @return true---打开成功； false----打开失败；
     */
    public static boolean openApp(GiftDomainDetail mGiftInfo, Context mContext) {
        GameBaseDetail mGame = null;
        try {
            GameBaseDetail game = GiftManger.giftGame2GameDetail(mGiftInfo.getGame(), mContext);
            mGame = FileDownloaders.getDownFileInfoById(game.getId());
            FileDownloaders.setOpenTime(mGame.getPkgName(), System.currentTimeMillis());
            PackageManager pm = mContext.getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(mGame.getPkgName());
            mContext.startActivity(intent);
            return true;

        } catch (Exception e) {

            e.printStackTrace();
            if (mGame != null) {
                mGame.setDownLength(0);
                FileDownloaders.setDownLength(mGame.getDownUrl(), 0);
                State.updateState(mGame, DownloadState.undownload);
            }

            return false;

        }
    }

}
