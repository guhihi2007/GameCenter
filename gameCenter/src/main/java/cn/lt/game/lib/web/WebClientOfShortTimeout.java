package cn.lt.game.lib.web;

/**
 * @author JohnsonLin
 * (启动页专用)
 */
public class WebClientOfShortTimeout extends WebClient{

	private volatile static WebClientOfShortTimeout webClient;

	public WebClientOfShortTimeout() {
		super();
		timeout = 3000;
	}

	public static WebClientOfShortTimeout singleton() {
		if (webClient == null) {
			synchronized (WebClientOfShortTimeout.class) {
				if (webClient == null) {
					webClient = new WebClientOfShortTimeout();
				}
			}
		}
		return webClient;
	}

}
