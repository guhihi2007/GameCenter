package cn.lt.game.lib.web;

import java.net.URI;

/**
 *	默认的过滤器类
 */
public class SimpleWebHttpFilter implements WebHttpFilter{

	@Override
	public String filterHandler(String str, URI uri) {
		return str;
	}

}
