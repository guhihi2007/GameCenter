package cn.lt.game.ui.app.community;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.lt.game.ui.app.community.model.DraftBean;
//发送队列
public class SqlQueueTools {
	public static final int TOPIC = 1;
	public static final int COMMENT = 2;
	public static final int REPLY = 3;
	private volatile static SqlQueueTools mInstance = null;
	private static ExecutorService topic_executorService; // topic单一线程池
	private static ExecutorService comment_executorService; // comment单一线程池
	private static ExecutorService reply_executorService; // reply单一线程池

	public static SqlQueueTools instance() {
		if (mInstance == null) {
			synchronized (SqlQueueTools.class) {
				if (mInstance == null) {
					mInstance = new SqlQueueTools();
					topic_executorService = Executors.newSingleThreadExecutor();
					comment_executorService = Executors
							.newSingleThreadExecutor();
					reply_executorService = Executors.newSingleThreadExecutor();
				}
			}
		}
		return mInstance;
	}
    //不同的线程池。有话题，评论，回复  分别对应各自的线程池
	public void sendToQueue(int type, final DraftBean db, final Context cn) {
		switch (type) {
		case TOPIC:
			topic_executorService.execute(new Runnable() {
				@Override
				public void run() {
					SendTools.instance().sendtopic(DraftsBeanTools.toTopic(db),
							cn);
				}
			});
			break;
		case COMMENT:
			comment_executorService.execute(new Runnable() {
				@Override
				public void run() {
					SendCommentTools.instance().sendComment(
							DraftsBeanTools.toComment(db), cn);
				}
			});
			break;
		case REPLY:
			reply_executorService.execute(new Runnable() {
				@Override
				public void run() {
					SendReplyTools.instance().sendReply(cn,
							DraftsBeanTools.toReply(db));
				}
			});
			break;
		default:
			break;
		}

	}

	public void close() {  //关闭线程池，目前还未用到此功能
		topic_executorService.shutdown();
		comment_executorService.shutdown();
		reply_executorService.shutdown();
	}
}
