package cn.lt.game.ui.notification;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.lt.game.domain.UIModule;
import cn.lt.game.model.GameBaseDetail;

public class LTNotificationManager {
	private static LTNotificationManager instance;
	private LTNotification notification;
	private ExecutorService mThreadPool;

	public static LTNotificationManager getinstance() {
		if (instance == null) {
			synchronized (LTNotification.class) {
				instance = new LTNotificationManager();
			}

		}
		return instance;

	}

	public LTNotificationManager() {
		mThreadPool = Executors.newSingleThreadExecutor();
		notification = new LTNotification();

	}

	/**
	 * 发送应用内通知
	 * @param game
     */
	public void sendNotification(GameBaseDetail game) {
		final GameBaseDetail cloneGame = game.clone();
		mThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				notification.sendNotification(cloneGame);
			}
		});
	}

	public void deleteGameNotification(final GameBaseDetail game) {
		final GameBaseDetail cloneGame = game.clone();
		mThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				notification.deleteGameNotification(cloneGame);
			}
		});
	}

	public void sendAllUpdataNotification() {
		mThreadPool.execute(new Runnable() {

			@Override
			public void run() {

				notification.sendAllUpdataNotification();
			}

		});
	}

	/**
	 * 平台升级通知 ，关掉弹窗发，coreservice周期性发
	 * @param versionCode
	 * @param isPush
     */
	public void sendGameCenterUpGradeN(final String versionCode,
			final Boolean isPush) {
		mThreadPool.execute(new Runnable() {

			@Override
			public void run() {

				notification.sendGameCenterUpGradeN(versionCode, isPush);
			}
		});
	}

	public void UpGradeNotification(final String title,final String content) {
		mThreadPool.execute(new Runnable() {

			@Override
			public void run() {

				notification.UpGradeNotification(title,content);
			}
		});
	}

	public void UpGradeNotification(final long intTotalKb,
			final long intCurrentKb, final int percent, final String versionCode) {
		mThreadPool.execute(new Runnable() {

			@Override
			public void run() {

				notification.UpGradeNotification(intTotalKb, intCurrentKb,
						percent, versionCode);
			}

		});
	}

	public void cancelNotification(final int id) {
		mThreadPool.execute(new Runnable() {

			@Override
			public void run() {

				notification.cancelNotification(id);
			}
		});
	}

	public void autoPauseDownloadInMobile(final int count) {
		mThreadPool.execute(new Runnable() {

			@Override
			public void run() {

				notification.autoPauseDownloadInMobile(count);
			}
		});
	}

	public void publishTopicMsg(final Context context, final String title,
			final int resid) {
		mThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				notification.publishTopicMsg(context, title, resid);
			}
		});
	}

	public void cancelAutoPauseNoti() {
		mThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				notification.cancelAutoPauseNoti();
			}
		});
	}

	public void cancelUpGradeNotification() {
		mThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				notification.cancelUpGradeNotification();
			}
		});
	}

	public void sendPushMessage(final UIModule module) {
		notification.handlePushMessage(module);
	}

	public void release() {
		instance = null;
		mThreadPool.execute(new Runnable() {

			@Override
			public void run() {

				notification.release();
			}
		});
	}
}
