package cn.lt.game.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import cn.lt.game.global.LogTAG;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.threadpool.LTAsyncTask;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.notification.LTNotificationManager;
import de.greenrobot.event.EventBus;

public class NoticeService extends Service {
	private static final String ACTION = "cn.lt.game.service.NoticeService";
	private int id = -1;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			id = intent.getIntExtra("id", -1);
			final int waitTime = intent.getIntExtra("waitTime", 0);
			intent.removeExtra("id");
			intent.removeExtra("waitTime");

			if (id != -1) {
				new LTAsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(getWaitTime(waitTime));
						} catch (InterruptedException e) {
							e.printStackTrace();
							LogUtils.e(LogTAG.PushTAG, "散裂分布式请求 --> 线程睡眠出异常，本次随机请求前的等待时间无效。。");
						}

						return null;
					}

					@Override
					protected void onPostExecute(Void aVoid) {
						super.onPostExecute(aVoid);
						// 发送请求
						pushMessage(id);
					}
				}.execute();
			}

		}
		return Service.START_NOT_STICKY;
	}

	public void pushMessage(int id) {
		Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.getPushUri(String.valueOf(id)), new WebCallBackToObject<UIModuleList>() {

			@Override
			protected void handle(UIModuleList info) {
				if(info.size() > 0 ){
					UIModule module = (UIModule)info.get(0);
					LogUtils.i(LogTAG.PushTAG, "请求推送数据成功");
					LTNotificationManager.getinstance().sendPushMessage(module);
				}
			}

			@Override
			public void onFailure(int statusCode, Throwable error) {
				LogUtils.i(LogTAG.PushTAG, "推送请求失败， statusCode = " + statusCode);
			}

		});
	}

	public static Intent getIntent(Context mContext,
			GameBaseDetail gameBaseDetail) {
		Intent intent = new Intent(mContext, NoticeService.class);
		intent.putExtra("gameBaseDetail", gameBaseDetail);

		return intent;

	}

	public static Intent getIntent(Context mContext, int id) {
		Intent intent = new Intent(mContext, NoticeService.class);
		intent.putExtra("id", id);

		return intent;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		System.out.println("推送服务死了");
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 计算随机请求等待时间（实现散裂分布式请求）
	 */
	private long getWaitTime(int t) {
		int waitTime = (int) (1 + Math.random() * 60);// 一分钟之内随机秒数
		LogUtils.i(LogTAG.PushTAG, "散裂分布式请求 --> 随机计算请求前的等待时间 = " + (t == 0 ? 0 : waitTime)  + "秒");
		return waitTime * t;
	}

}
