package cn.lt.game.ui.app.community.model;

import java.io.Serializable;

/**
 * 
 * 作者信息
 *
 */
public class Author implements Serializable{
	private static final long serialVersionUID = 1L;
	public String author_nickname;//作者昵称
	public int author_id;//作者id
	public String author_icon;//作者icon图地址
	public int user_type;//作者⽤用户类型，0-普通，1-管理员或组长
	public int user_level; //用户等级

}
