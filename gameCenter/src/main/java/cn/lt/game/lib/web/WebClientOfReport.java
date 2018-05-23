package cn.lt.game.lib.web;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.lt.game.lib.util.LogUtils;

/**
 * @author JohnsonLin
 * 专门给数据上报用
 * // TODO: 2017/6/27 测试没问题的话就可以删了
 */
public class WebClientOfReport extends WebClient{

	private volatile static WebClientOfReport webClient;

	/**
	 * 线程池维护线程的最少数量
	 */
	private static final int DEFAULT_CORE_POOL_SIZE = 5;
	private static final int DEFAULT_MAXIMUM_POOL_SIZE = 28;
	/**
	 * 线程池维护线程所允许的空闲时间
	 */
	private static final int DEFAULT_KEEP_ALIVETIME = 100;

	public WebClientOfReport() {
		super();
		threadPool = new ThreadPoolExecutor(
				DEFAULT_CORE_POOL_SIZE,
				DEFAULT_MAXIMUM_POOL_SIZE,
				DEFAULT_KEEP_ALIVETIME,
				TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(3),
				new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public static WebClientOfReport singleton() {
		if (webClient == null) {
			synchronized (WebClientOfReport.class) {
				if (webClient == null) {
					webClient = new WebClientOfReport();
				}
			}
		}
		return webClient;
	}

	@Override
	public void doPost_messagePack(String url, Map<String, ?> params, Map<String, String> cusHeaders, WebCallBackBase callBack) {
		LogUtils.i("kasitag", "WebClientOfReport~~~size = " + threadPool.getMaximumPoolSize());
		super.doPost_messagePack(url, params, cusHeaders, callBack);
	}
}
