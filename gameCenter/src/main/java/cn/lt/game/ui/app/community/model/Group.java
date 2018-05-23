package cn.lt.game.ui.app.community.model;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * 
 * 小组信息
 * 用途：
 * 我的社区——加入的小组
 * 发现小组列表
 *
 */
public class Group implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8424190393192827861L;
	public int group_id;
	public String group_title;
	public String group_icon;
	public String group_summary;
	public int topic_count;
	public int member_count;
	public boolean is_join;
	public ArrayList<Category> categories;
	public int popularity;//人气值
}

