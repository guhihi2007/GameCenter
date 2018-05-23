package cn.lt.game.ui.app.community.model;

import java.io.Serializable;
/***
 * 小组话题分类信息 用途： 我的社区——小组话题分类 发现小组话题列表
 * 
 */

public class Category implements Serializable {
	public int id;
	public String title;
	public boolean bolean = false;
	public Category(int id, String title) {
		super();
		this.id = id;
		this.title = title;
	}
}
