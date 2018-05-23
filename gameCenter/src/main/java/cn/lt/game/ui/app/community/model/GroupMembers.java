package cn.lt.game.ui.app.community.model;

import java.util.List;

/**
 * 
 * 用途：
 * 小组成员列表
 *
 */
public class GroupMembers {
	public int member_count;//小组成员总数
	public int total_page;//总页数
	public int total;
	public List<User> detail;//成员用户列表数据
	public List<User> data;//我的关注列表数据
}
