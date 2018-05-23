package cn.lt.game.ui.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.bean.PushAppInfo;
import cn.lt.game.bean.PushBaseBean;
import cn.lt.game.bean.PushH5Bean;
import cn.lt.game.domain.UIModule;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.install.InstallState;
import cn.lt.game.install.autoinstaller.AutoInstallerContext;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.PopWidowManageUtil;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.deeplink.DeepLinkUtil;
import cn.lt.game.lib.util.image.BitmapUtil;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.service.InstallIntentservice;
import cn.lt.game.statistics.DCStatIdJoint;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.update.PlatUpdateAction;
import cn.lt.game.update.PlatUpdateService;

public class LTNotification {
    private Context mContext;
    private static final int DOWNLOAD_ID = 10; // 下载通知ID
    private static final int UPGRADE_ID = 11;// 升级通知ID
    private static final int FAIL_ID = 12; // 重试通知ID
    private static final int AUTOPAUSE = 13; // 网络切换通知ID
    private static final int UPGRADESTATE_ID = 14;
    private static final int PUBLISHTOPIC_ID = 15; // 发表评论等通知
    private static final int PUSH_RANGE = 100; // 推送字段
    private static final int TAB_INSTALLED = 2;        // 4.1版本的安装
    public static final int TAB_INSTALL = 0;    // 4.1版本的下载管理
    public static final int TAB_UPGRADE = 1;    // 4.1版本的升级
    private int downloadCnt = 0; // 记录下载数
    private int failCnt = 0; // 记录失败数
    private int upGradeCnt = 0; // 成功记录数
    private StringBuilder downloadValueBuilder = new StringBuilder();
    private StringBuilder failValueBuilder = new StringBuilder();
    private StringBuilder upGradeValueBuilder = new StringBuilder();
    private ArrayList<GameBaseDetail> downloadFailList = null;
    private ArrayList<GameBaseDetail> upGradeList = null;
    private ArrayList<GameBaseDetail> newTask = null;
    private NotificationManager mNotifyManager = null;
    private String gameIds;

    /*AA啊*/
    protected LTNotification() {
        mContext = MyApplication.application.getApplicationContext();
        mNotifyManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        initData();
        initBuilder();
    }

    public synchronized void sendNotification(GameBaseDetail gameBaseDetail) {
        LogUtils.d("tuisongs", "发送通知了");
        if (gameBaseDetail != null) {

            if (newTask.contains(gameBaseDetail)) {

                newTask.remove(gameBaseDetail);
            }

            newTask.add(gameBaseDetail);
        }

        initCnt();
        initBuilder();

        for (GameBaseDetail game : newTask) {

            if (game.getState() == DownloadState.downInProgress
                    || game.getState() == DownloadState.waitDownload) {

                createContentValue(DOWNLOAD_ID, game);
                if (downloadFailList.contains(game)) {
                    downloadFailList.remove(game);
                }
            }

            if (game.getState() == DownloadState.downloadFail) {

                if (!downloadFailList.contains(game)) {
                    downloadFailList.add(game);
                }

            }

            if (game.getState() == DownloadState.downloadComplete) {
                if (downloadFailList.contains(game)) {
                    downloadFailList.remove(game);
                }

                sendDownloadCompleteNotification(game);

            }

            // 取消安装通知
            if (game.getState() == InstallState.installFail) {

                if (!downloadFailList.contains(game)) {
                    downloadFailList.add(game);
                }

                cancelNotification(game.getId());
            }

            sendAllUpdataNotification(game);
        }

        sendDownLoadNotification();
        sendFailNotification();

    }

    public synchronized void deleteGameNotification(GameBaseDetail game) {
        if (newTask != null && newTask.contains(game)) {
            newTask.remove(game);
            sendNotification(null);
        }

        if (upGradeList != null && upGradeList.contains(game)) {
            upGradeList.remove(game);
        }

        cancelNotification(game.getId());
    }

    private void initData() {
        List<GameBaseDetail> mGameList = FileDownloaders
                .getAllDownloadFileInfo();

        downloadFailList = new ArrayList<GameBaseDetail>();
        upGradeList = new ArrayList<GameBaseDetail>();
        newTask = new ArrayList<GameBaseDetail>();
        if (mGameList.size() == 0) {
            return;
        }

        initCnt();
        initBuilder();

        for (GameBaseDetail gameBaseDetail : mGameList) {
            int state = gameBaseDetail.getState();

            if (state == DownloadState.downloadFail
                    || state == InstallState.installFail) {
                if (!downloadFailList.contains(gameBaseDetail)) {
                    downloadFailList.add(gameBaseDetail);
                }

            }

            if (state == DownloadState.downInProgress
                    || state == DownloadState.waitDownload) {
                if (!newTask.contains(gameBaseDetail)) {
                    newTask.add(gameBaseDetail);
                }
            }
        }

    }

    private void sendAllUpdataNotification(GameBaseDetail game) {
        if (!upGradeList.contains(game)) {
            return;
        } else {
            upGradeList.remove(game);
        }

        sendAllUpdataNotification();
    }

    public void sendAllUpdataNotification() {
        List<GameBaseDetail> mGameList = FileDownloaders
                .getAllDownloadFileInfo();

        upGradeCnt = 0;
        upGradeValueBuilder.delete(0, upGradeValueBuilder.length());

        for (GameBaseDetail gameBaseDetail : mGameList) {
            int state = gameBaseDetail.getState();
            int prevState = gameBaseDetail.getPrevState();

            if (state == InstallState.upgrade
                    || prevState == InstallState.upgrade
                    && state != InstallState.ignore_upgrade
                    && state != DownloadState.downInProgress
                    && state != DownloadState.waitDownload
                    && state != InstallState.install) {

                if (!upGradeList.contains(gameBaseDetail)) {
                    upGradeList.add(gameBaseDetail);
                }

            }
        }

        for (GameBaseDetail gameBaseDetail : upGradeList) {
            createContentValue(UPGRADE_ID, gameBaseDetail);
        }

        LogUtils.i("Erosion", "upGradeCnt===" + upGradeCnt);
        if (upGradeCnt > 0) {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    mContext);
            String title = upGradeCnt + "款游戏发现新版本，点击升级";
            String value = upGradeValueBuilder.substring(1,
                    upGradeValueBuilder.length());

            gameIds = DCStatIdJoint.jointIdByGameDetailBean(upGradeList);
            Intent intent = getManagementIntent(TAB_UPGRADE, true, false, false, true);
            PendingIntent pendIntent = getPendingIntentForActivity(intent);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setWhen(0L);
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(value);
            mBuilder.setContentIntent(pendIntent);
            mBuilder.setAutoCancel(true);
            mNotifyManager.notify(UPGRADE_ID, mBuilder.build());

        } else {
            cancelNotification(UPGRADE_ID);
        }

    }

    private void sendGameCenterUpGradeN(String title, String subTitle, Bitmap bitmap, boolean isFromWakeUp, String pushId) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        Intent intent = getGameCenterIntent(false);
        intent.putExtra("isPush", true);
        intent.putExtra("isFromWakeUp", isFromWakeUp);
        intent.putExtra("pushId", pushId);
        PendingIntent pendIntent = getPendingIntentForService(intent);


        mBuilder.setContentTitle(title);
        mBuilder.setContentText(subTitle);
        mBuilder.setLargeIcon(bitmap);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentIntent(pendIntent);
        mBuilder.setAutoCancel(true);
        mNotifyManager.notify(UPGRADESTATE_ID, mBuilder.build());
    }

    public void sendGameCenterUpGradeN(String versionCode, Boolean isPush) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);

        String title = "发现新版本";
        String value = "点击升级游戏中心 V" + versionCode;

        Intent intent = getGameCenterIntent(false);
        intent.putExtra("isPush", isPush);

        PendingIntent pendIntent = getPendingIntentForService(intent);

        if (!isPush) {
            mBuilder.setWhen(0L);
        }

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(value);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentIntent(pendIntent);
        mBuilder.setAutoCancel(true);
        mNotifyManager.notify(UPGRADESTATE_ID, mBuilder.build());
    }

    public void UpGradeNotification(long intTotalKb, long intCurrentKb,
                                    int percent, String versionCode) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext).setSmallIcon(R.mipmap.ic_launcher);

        if (percent != 100) {

            mBuilder.setContentTitle("游戏中心正在下载");
            mBuilder.setContentText(percent + "%");
            mBuilder.setProgress(100, percent, false);
            Intent intent = getNormalIntent();
            PendingIntent pendIntent = getPendingIntentForService(intent);
            mBuilder.setWhen(0L);
            mBuilder.setContentIntent(pendIntent);
            mBuilder.setAutoCancel(true);
            mNotifyManager.notify(UPGRADESTATE_ID, mBuilder.build());

        } else {
            mBuilder.setContentTitle("游戏中心  V" + versionCode + "下载完成");
            mBuilder.setContentText("点击进行安装");
            mBuilder.setProgress(0, 0, false);
            mBuilder.setWhen(0L);
            mBuilder.setAutoCancel(true);
            Intent intent = getGameCenterIntent(false);
            PendingIntent pendIntent = getPendingIntentForService(intent);
            mBuilder.setContentIntent(pendIntent);
            mNotifyManager.notify(UPGRADESTATE_ID, mBuilder.build());
        }
    }

    public void UpGradeNotification(String title, String value) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);

//		String title = "游戏中心下载失败";
//		String value = "请点击重试";

        Intent intent = getGameCenterIntent(true);
        PendingIntent pendIntent = getPendingIntentForService(intent);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(value);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentIntent(pendIntent);
        mBuilder.setAutoCancel(true);

        mNotifyManager.notify(UPGRADESTATE_ID, mBuilder.build());
    }

    private void sendFailNotification() {

        for (GameBaseDetail gameBaseDetail : downloadFailList) {
            createContentValue(FAIL_ID, gameBaseDetail);

        }

        if (failCnt > 0) {
            String title = failCnt + "个任务下载失败，点击重试";
            String value = failValueBuilder.substring(1,
                    failValueBuilder.length());

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    mContext);
            int tab = getManagementTab();
            Intent intent = getManagementIntent(tab, true, false, true, false);
            PendingIntent pendIntent = getPendingIntentForActivity(intent);

            mBuilder.setWhen(0L);
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(value);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));
            mBuilder.setContentIntent(pendIntent);
            mBuilder.setAutoCancel(true);
            mNotifyManager.notify(FAIL_ID, mBuilder.build());
        } else {
            cancelNotification(FAIL_ID);
        }

    }

    private void sendDownLoadNotification() {
        if (downloadCnt > 0) {
            String title = downloadCnt + "个任务正在下载，点击查看";
            String value = downloadValueBuilder.substring(1,
                    downloadValueBuilder.length());

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    mContext);

            int tab = getManagementTab();
            Intent intent = getManagementIntent(tab, true, true, false, false);
            PendingIntent pendIntent = getPendingIntentForActivity(intent);
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(value);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));
            mBuilder.setWhen(0L);
            mBuilder.setContentIntent(pendIntent);
            mBuilder.setAutoCancel(false);
            mBuilder.setOngoing(true);
            mNotifyManager.notify(DOWNLOAD_ID, mBuilder.build());
        } else {
            cancelNotification(DOWNLOAD_ID);
        }

    }

    private void sendDownloadCompleteNotification(GameBaseDetail game) {
        if (AutoInstallerContext.getInstance().getAccessibilityStatus() != AutoInstallerContext.STATUS_OPEN) {
            return;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);
        String title = game.getName() + "下载完成";
        String value = "点击安装";
        Intent intent = getInstallIntent(game);
        PendingIntent pendIntent = getPendingIntentForService(intent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setWhen(0L);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(value);
        mBuilder.setContentIntent(pendIntent);
        mBuilder.setAutoCancel(true);
        mNotifyManager.notify(game.getId(), mBuilder.build());

    }

    private void createContentValue(int id, GameBaseDetail gameBaseDetail) {
        switch (id) {
            case DOWNLOAD_ID:
                downloadCnt++;
                downloadValueBuilder.append("、").append(gameBaseDetail.getName());
                break;
            case FAIL_ID:
                failCnt++;
                failValueBuilder.append("、").append(gameBaseDetail.getName());
                break;
            case UPGRADE_ID:
                upGradeCnt++;
                upGradeValueBuilder.append("、").append(gameBaseDetail.getName());
                break;

            default:
                break;
        }
    }

    public void cancelNotification(int id) {

        mNotifyManager.cancel(id);
    }

    // 初始化计数器
    private void initCnt() {
        downloadCnt = 0;
        failCnt = 0;
        upGradeCnt = 0;
    }

    // 初始化内容构造器
    private void initBuilder() {
        downloadValueBuilder.delete(0, downloadValueBuilder.length());
        failValueBuilder.delete(0, failValueBuilder.length());
        upGradeValueBuilder.delete(0, upGradeValueBuilder.length());
    }

    public void autoPauseDownloadInMobile(int count) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);
        String title = String.format(
                mContext.getResources().getString(
                        R.string.download_pause_noti_title), count);
        String value = mContext.getResources().getString(
                R.string.download_pause_noti_content);
        int tab = getManagementTab();
        Intent intent = getManagementIntent(tab, true, false, false, false);
        PendingIntent pendIntent = getPendingIntentForActivity(intent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(value);
        mBuilder.setWhen(0L);
        mBuilder.setContentIntent(pendIntent);
        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(true);
        mNotifyManager.notify(AUTOPAUSE, mBuilder.build());
    }

    public void publishTopicMsg(Context context, String title, int resid) {
        final NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                R.layout.topic_notification);
        Notification notification = new Notification();
        notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击后自动消失
        Notification.Builder builder = new Notification.Builder(context);
        builder.setTicker(title);
        builder.setSmallIcon(resid);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setContentIntent(PendingIntent.getActivity(context, 0,
                new Intent(Intent.ACTION_DELETE), 0));
        notification = builder.build();
        remoteView.setViewVisibility(R.id.progressbar, 4);
        remoteView.setViewVisibility(R.id.tv_percent, 4);
        remoteView.setImageViewResource(R.id.iv_publishing,
                R.mipmap.ic_launcher);
        remoteView.setTextViewText(R.id.tv_tip, title);
        remoteView.setTextViewText(R.id.tv_time,
                TimeUtils.formatTime(Calendar.getInstance().getTime()));
        notification.contentView = remoteView;
        manager.notify(PUBLISHTOPIC_ID, notification);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    manager.cancel(PUBLISHTOPIC_ID);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();

    }

    /**
     * 清理自动暂停通知
     */
    public void cancelAutoPauseNoti() {
        cancelNotification(AUTOPAUSE);
    }

    public void cancelUpGradeNotification() {
        cancelNotification(UPGRADESTATE_ID);
    }

    /**
     * 发送通知
     *
     * @param module
     */
    public synchronized void handlePushMessage(final UIModule module) {

        // 热点tab或热点详情，如果条件木满足就不执行
        if ((module.getUIType() == PresentType.push_hot_tab || module.getUIType() == PresentType.push_hot_detail)
                && !PopWidowManageUtil.hotContentIsReady()) {

            return;
        }

        if (module.getUIType() == PresentType.push_deeplink) {

            // 如果deeplink目标应用未安装，则不显示通知
            if (!DeepLinkUtil.isExistApp(mContext, ((PushBaseBean) module.getData()).getUrl())) {
                LogUtils.i(LogTAG.PushTAG, "deeplink url = " + ((PushBaseBean) module.getData()).getUrl() + "， deeplink目标应用未安装，则不显示通知");
                return;
            }
        }

        final int mNoticeStyle = Integer.parseInt(((PushBaseBean) module.getData()).getNotice_style());
        final String iconUrl = ((PushBaseBean) module.getData()).getIcon();
        final String mImageUrl = ((PushBaseBean) module.getData()).getImage();

        ImageloaderUtil.loadImageCallBack(MyApplication.application, iconUrl, new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                final Bitmap mIcon = BitmapUtil.drawable2Bitmap(resource.getCurrent());
                if (mNoticeStyle == 1) {
                    sendPushMessage(module, mIcon, null);
                } else if (mNoticeStyle == 2 || mNoticeStyle == 3) {

                    ImageloaderUtil.loadImageCallBack(MyApplication.application, mImageUrl, new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            Bitmap mImage = BitmapUtil.drawable2Bitmap(resource.getCurrent());
                            sendPushMessage(module, mIcon, mImage);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            LogUtils.i(LogTAG.PushTAG, "推送大图或小图加载失败~， img url = " + mImageUrl);

                        }
                    });
                }

            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                LogUtils.i(LogTAG.PushTAG, "推送icon加载失败~， icon url = " + iconUrl);

            }
        });
    }

    private void sendPushMessage(UIModule module, Bitmap icon, Bitmap image) {

        int noticeId = 0;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext).setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setLargeIcon(icon);//设置大图标，即通知条上左侧的图片（如果只设置了小图标，则此处会显示小图标）
        String title = "";
        String subTitle = "";
        int notice_style = 0;
        Intent intent = null;
        PendingIntent pendingIntent = null;
        RemoteViews remoteViews = null;

        LogUtils.i("mmm", "==============>>>" + module.getUIType().toString());
        // 游戏
        if (module.getUIType() == PresentType.push_game) {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);// 显示在通知栏上的小图标
            PushAppInfo gameInfo = (PushAppInfo) module.getData();
            String pushId = gameInfo.getId();

            // 用于发通知用的通知id
            noticeId = PUSH_RANGE + Integer.parseInt(gameInfo.getGame().getId());
            subTitle = gameInfo.getSub_title();
            title = gameInfo.getMain_title();
            notice_style = Integer.parseInt(gameInfo.getNotice_style());

            boolean isPicture = false;
            intent = getGameDetailIntent(Integer.parseInt(gameInfo.getGame().getId()),
                    !isPicture, isPicture, gameInfo.getId(), gameInfo.isFromWakeUp);

            pendingIntent = getPendingIntentForActivity(intent);
            LogUtils.i(LogTAG.PushTAG, "这是推送游戏, gameId = " + gameInfo.getGame().getId());
            PushAppInfo data = ((PushAppInfo) module.getData());
            reportPushReceivedData(module, gameInfo.getId(), data.isFromWakeUp, gameInfo.getGame().getId(), "Game");  //Game/platUpgrade/topic/gameUpgrade
        }

        //h5
        if (module.getUIType() == PresentType.push_h5) {
            PushH5Bean h5 = (PushH5Bean) module.getData();
            title = h5.getMain_title();
            subTitle = h5.getSub_title();
            noticeId = PUSH_RANGE + Integer.parseInt(h5.getId());
            String pushId = h5.getId() + "";
            intent = getH5Intent(true, pushId, h5.isFromWakeUp, h5.getH5());
            pendingIntent = getPendingIntentForActivity(intent);
            reportPushReceivedData(module, pushId, h5.isFromWakeUp, "", h5.getH5().contains("voucher") ? "H5-DJQ" : "H5");
        }

        // 专题
        if (module.getUIType() == PresentType.push_topic) {
            PushBaseBean topic = (PushBaseBean) module.getData();
            title = topic.getMain_title();
            subTitle = topic.getSub_title();
            notice_style = Integer.parseInt(topic.getNotice_style());
            boolean isPicture = getIsPicture(notice_style);
            String pushId = topic.getNotice_id();

            // 用于发通知用的通知id
            noticeId = PUSH_RANGE + Integer.parseInt(topic.getId());
            intent = getSpecialtIntent(topic, !isPicture,
                    isPicture, pushId, topic.isFromWakeUp);

            pendingIntent = getPendingIntentForActivity(intent);
            LogUtils.i(LogTAG.PushTAG, "这是推送专题, topicId = " + topic.getId());
            reportPushReceivedData(module, pushId, topic.isFromWakeUp, "", "Topic");
        }

        // 常规活动
        if (module.getUIType() == PresentType.push_routine_activity) {
            PushBaseBean prab = (PushBaseBean) module.getData();
            title = prab.getMain_title();
            subTitle = prab.getSub_title();
            noticeId = PUSH_RANGE + Integer.parseInt(prab.getId());
            String pushId = prab.getId() + "";
            intent = getRoutineActivityIntent(true, pushId, prab.isFromWakeUp);
            pendingIntent = getPendingIntentForActivity(intent);
            reportPushReceivedData(module, pushId, prab.isFromWakeUp, "", "routineActivity");
        }

        // 内容TAB或内容详情
        if (module.getUIType() == PresentType.push_hot_tab
                || module.getUIType() == PresentType.push_hot_detail) {

            PushBaseBean noticBean = (PushBaseBean) module.getData();
            title = noticBean.getMain_title();
            subTitle = noticBean.getSub_title();
            notice_style = Integer.parseInt(noticBean.getNotice_style());
            boolean isPicture = getIsPicture(notice_style);
            String pushId = noticBean.getNotice_id();

            // 用于发通知用的通知id
            noticeId = PUSH_RANGE + Integer.parseInt(noticBean.getNotice_id());
            intent = getContentIntent(noticBean, !isPicture,
                    isPicture, pushId, noticBean.isFromWakeUp);

            pendingIntent = getPendingIntentForActivity(intent);
            LogUtils.i(LogTAG.PushTAG, "这是" + module.getUIType().presentType + "推送类型, Id = " + noticBean.getId());
            reportPushReceivedData(module, pushId, noticBean.isFromWakeUp, "", noticBean.getType());
        }

        // deepLink
        if (module.getUIType() == PresentType.push_deeplink) {

            PushBaseBean noticBean = (PushBaseBean) module.getData();
            title = noticBean.getMain_title();
            subTitle = noticBean.getSub_title();
            notice_style = Integer.parseInt(noticBean.getNotice_style());
            String pushId = noticBean.getId();
            String url = noticBean.getUrl();

            // 用于发通知用的通知id
            noticeId = PUSH_RANGE + Integer.parseInt(pushId);
            intent = getDeepLinkIntent(pushId, url, noticBean.isFromWakeUp);

            pendingIntent = getPendingIntentForService(intent);
            LogUtils.i(LogTAG.PushTAG, "这是" + module.getUIType().presentType + "推送类型, Id = " + pushId);
            reportPushReceivedData(module, pushId, noticBean.isFromWakeUp, "", noticBean.getType());
        }

        // 平台升级
        if (module.getUIType() == PresentType.push_app) {
            PushAppInfo platInfo = (PushAppInfo) module.getData();
            title = platInfo.getMain_title();
            subTitle = platInfo.getSub_title();

            // 版本号为空，认为没版本升级
            if (TextUtils.isEmpty(Constant.versionName)) {
                LogUtils.i(LogTAG.PushTAG, "版本号是空的");
                return;
            }

            if (platInfo.getApp_channel().equalsIgnoreCase("all")) {
                sendGameCenterUpGradeN(title, subTitle, icon, platInfo.isFromWakeUp, platInfo.getId());
            } else {
                String channelName = Constant.CHANNEL;
                LogUtils.i(LogTAG.PushTAG, "本地 channel:" + channelName);
                LogUtils.i(LogTAG.PushTAG, " 服务器 channel: " + platInfo.getApp_channel());
                if (platInfo.getApp_channel().equalsIgnoreCase(channelName)) {
                    sendGameCenterUpGradeN(title, subTitle, icon, platInfo.isFromWakeUp, platInfo.getId());
                } else {
                    LogUtils.i(LogTAG.PushTAG, "渠道号不相同，不显示通知");

                }
            }

            LogUtils.i(LogTAG.PushTAG, "这是推送版本升级, pushId = " + platInfo.getId());
            PushAppInfo data = ((PushAppInfo) module.getData());
            reportPushReceivedData(module, platInfo.getId(), data.isFromWakeUp, "", "platUpgrade");
            return;
        }

        if (notice_style == 1) {// 只有icon
//			remoteViews = getNormalStyle(icon, title, subTitle);// 暂时取消使用自定义（容易导致某些系统，字体和推送背景颜色一样）
        }

        if (notice_style == 2) {// 小图
            remoteViews = getOnlyPictureStyle(image);
        }

        if (notice_style == 3) {// 大图
            if (android.os.Build.VERSION.SDK_INT > 16) {
                remoteViews = getOnlyPictureStyle(image);

                mBuilder.setContentTitle(title);
                mBuilder.setContentText(subTitle);
                mBuilder.setContentIntent(pendingIntent);
                mBuilder.setAutoCancel(true);
                mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

                Notification notification = mBuilder.build();
                notification.bigContentView = remoteViews;
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                NotificationManager mNotifyManager = (NotificationManager) mContext
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                mNotifyManager.notify(noticeId, notification);
                return;
            }
        }

        if (remoteViews != null) {
            mBuilder.setContent(remoteViews);
        }

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(subTitle);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);// 通知优先级（最大）
        mBuilder.setAutoCancel(true);
        mNotifyManager.notify(noticeId, mBuilder.build());

        if (icon != null && !icon.isRecycled()) {
            icon.recycle();
            icon = null;
        }

        if (image != null && !image.isRecycled()) {
            image.recycle();
            image = null;
        }

    }

    private Intent getDeepLinkIntent(String pushId, String url, boolean isFromWakeUp) {
        Intent intent = new Intent(mContext, PushIntentService.class);
        intent.setAction(PushIntentService.ACTION);
        intent.putExtra(NoticeConstants.jumpBy, NoticeConstants.jumpByPushDeepLink);
        intent.putExtra("pushId", pushId);
        intent.putExtra("url", url);
        intent.putExtra("isFromWakeUp", isFromWakeUp);
        return intent;
    }

    private void reportPushReceivedData(UIModule module, String id, boolean isFromWakeUp, String gameId, String presentType) {
        if (isFromWakeUp) {
            DCStat.pushEventByWakeupUser(id, gameId, "", "received", "WAKE_UP", "", "");   //唤醒推送到达
        } else {
            DCStat.pushEvent(id, gameId, presentType, "received", Constant.PAGE_GE_TUI, "", "");   //个推推送到达
        }
    }

    private String getDcPushType(PresentType type) {
        if (type == PresentType.push_app) {
            return "platUpgrade";
        } else if (type == PresentType.push_game) {
            return "Game";
        } else if (type == PresentType.topic) {
            return "Topic";
        } else {
            return "H5";
        }
    }

    private Intent getH5Intent(Boolean isPush, String pushId, boolean isFromWakeUp, String url) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(mContext,
                mContext.getResources().getString(R.string.packageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("jump_h5", true);
        intent.putExtra("isPush", isPush);
        intent.putExtra("pushId", pushId);
        intent.putExtra("isFromWakeUp", isFromWakeUp);
        intent.putExtra("h5Url", url);
        return intent;
    }

    private Intent getRoutineActivityIntent(Boolean isPush, String pushId, boolean isFromWakeUp) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(mContext,
                mContext.getResources().getString(R.string.packageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("jump_RoutineActivity", true);
        intent.putExtra("isPush", isPush);
        intent.putExtra("pushId", pushId);
        intent.putExtra("isFromWakeUp", isFromWakeUp);
        return intent;
    }

    private Intent getSpecialtIntent(PushBaseBean topic, Boolean isPush, Boolean isPicture, String pushId, boolean isFromWakeUp) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(mContext,
                mContext.getResources().getString(R.string.packageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("jump_SpecialDetail", true);
        intent.putExtra("isPush", isPush);
        intent.putExtra("isPicture", isPicture);
        intent.putExtra("pushId", pushId);
        intent.putExtra("isFromWakeUp", isFromWakeUp);
        Bundle bundle = new Bundle();
        bundle.putString("topicId", topic.getId());
        bundle.putString("image", topic.getImage());
        bundle.putString("review", topic.getSummary());
        bundle.putString("title", topic.getTitle());
        bundle.putString("updated_at", topic.getUpdated_at());
        intent.putExtras(bundle);
        return intent;

    }

    private Intent getGameDetailIntent(int id, Boolean isPush, Boolean isPicture, String pushId, boolean isFromWakeUp) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(mContext,
                mContext.getResources().getString(R.string.packageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("jump_gameDetail", true);
        intent.putExtra("id", id);
        intent.putExtra("isPush", isPush);
        intent.putExtra("isPicture", isPicture);
        intent.putExtra("pushId", pushId);
        intent.putExtra("isFromWakeUp", isFromWakeUp);
        return intent;
    }

    private Intent getManagementIntent(int tab_id, boolean isNotif, boolean isLook, boolean clickRetry, boolean isUpgradeAll) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("jump_Management", true);
        intent.putExtra("upgradeGameIds", gameIds);
        intent.putExtra("isNotif", isNotif);
        intent.putExtra("isLook", isLook);
        intent.putExtra("clickRetry", clickRetry);
        intent.putExtra("tab_id", tab_id);
        intent.putExtra("upgrade_all", isUpgradeAll);
        intent.setClassName(mContext,
                mContext.getResources().getString(R.string.packageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        LogUtils.i("tuisongs", "游戏推送的id?" + gameIds);
        return intent;

    }

    /**
     * 内容类型
     */
    private Intent getContentIntent(PushBaseBean bean, Boolean isPush, Boolean isPicture, String pushId, boolean isFromWakeUp) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(mContext,
                mContext.getResources().getString(R.string.packageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("jump_content", true);
        intent.putExtra("isPush", isPush);
        intent.putExtra("isPicture", isPicture);
        intent.putExtra("pushId", pushId);
        intent.putExtra("isFromWakeUp", isFromWakeUp);
        Bundle bundle = new Bundle();
        bundle.putString("contentId", String.valueOf(bean.getId()));
        bundle.putString("presentType", bean.getType());
        bundle.putString("url", bean.getUrl());
        intent.putExtra("contentBundle", bundle);
        return intent;

    }

    private Intent getInstallIntent(GameBaseDetail game) {
        Intent intent = new Intent(mContext, InstallIntentservice.class);
        intent.setAction(InstallIntentservice.ACTION);
        return intent.putExtra("gameBaseDetail", game);
    }

    private Intent getGameCenterIntent(boolean isRetry) {
        Intent intent = new Intent(mContext, PlatUpdateService.class);
        intent.setAction(PlatUpdateAction.SERVICE_START_ACTION);
        intent.putExtra(Constant.RETRY_FLAG, isRetry);
        intent.putExtra(PlatUpdateAction.ACTION,
                PlatUpdateAction.ACTION_NOTIFICATION);
        return intent;
    }

    private Intent getNormalIntent() {
        Intent intent = new Intent();
        return intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    /**
     * 只有一张图片样式
     *
     * @return
     */
    private RemoteViews getOnlyPictureStyle(Bitmap bitmap) {
        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(),
                R.layout.notification_imglayout);
        remoteView.setImageViewBitmap(R.id.notification_smallImg, bitmap);
        return remoteView;
    }

    private PendingIntent getPendingIntentForActivity(Intent intent) {
        int requestCode = (int) SystemClock.uptimeMillis();
        PendingIntent pendIntent = PendingIntent.getActivity(mContext,
                requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendIntent;
    }

    private PendingIntent getPendingIntentForService(Intent intent) {
        int requestCode = (int) SystemClock.uptimeMillis();
        PendingIntent pendIntent = PendingIntent.getService(mContext,
                requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendIntent;
    }

    // 判断跳转管理也那个页面
    private int getManagementTab() {
        boolean isUpgrade = false;
        for (int i = 0; i < newTask.size(); i++) {

            GameBaseDetail gameBaseDetail = newTask.get(i);
            if (gameBaseDetail.getState() == DownloadState.downInProgress
                    || gameBaseDetail.getState() == DownloadState.waitDownload
                    || gameBaseDetail.getState() == DownloadState.downloadFail) {

                if (gameBaseDetail.getState() == InstallState.upgrade
                        || gameBaseDetail.getPrevState() == InstallState.upgrade
                        && gameBaseDetail.getState() != InstallState.ignore_upgrade) {
                    isUpgrade = true;
                }
            }

        }

        if (isUpgrade) {
            return TAB_UPGRADE;
        } else {
            return TAB_INSTALL;
        }
    }

    private boolean getIsPicture(int notice_style) {
        switch (notice_style) {
            case 1:
                return false;
            case 2:
                return android.os.Build.VERSION.SDK_INT > 16;

            case 3:
                return true;

            default:
                break;
        }
        return false;

    }

    public synchronized void release() {
        downloadFailList.clear();
        upGradeList.clear();
        newTask.clear();
        initBuilder();
        initCnt();
    }
}
