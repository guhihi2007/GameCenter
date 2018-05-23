package cn.lt.game.model;

import java.io.Serializable;

public class RecommendGame implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;// ID
	private String icon;// 图片链接
	private String title;// 名称
	private int forum_id;//游戏社区ID

	public int getForum_id() {
		return forum_id;
	}

	public void setForum_id(int forum_id) {
		this.forum_id = forum_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
