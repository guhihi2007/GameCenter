package cn.lt.game.lib.web;

import java.net.URI;

/**
 * 过滤器接口
 */
public interface WebHttpFilter{
	String filterHandler(String str, URI uri);
}
