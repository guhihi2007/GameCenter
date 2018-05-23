package cn.lt.game.ui.app.community.model;

/**
 * 
 * 回复信息
 * 用途：
 * 评论回复列表项
 *
 */
public class Reply extends Author {

	public int reply_id;//回复id
	public int acceptor_id;//被回复作者用户id
	public String acceptor_nickname;//被回复作者用户昵称
	public String reply_content;//回复内容
	public String published_at;//发表时间
	public String created_at;//回复时间


	//发送消息饭回来的内容
	public String content;


}
