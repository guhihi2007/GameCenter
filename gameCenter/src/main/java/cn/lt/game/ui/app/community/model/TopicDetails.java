package cn.lt.game.ui.app.community.model;

import java.util.List;
/**
 * 
 * 话题列表
 * 
 * 用途：
 * 最新话题列表
 * 小组话题列表
 * 我的社区——发表的话题列表
 * 我的社区——评论的话题
 * 我的社区——收藏的话题
 *
 */
public class TopicDetails {
	private int total_page;//总页数
	private List<TopicDetail> detail;//话题列表数据
	private List<TopicDetail> data;//TA的话题列表数据

	public int getTotal_page() {
		return total_page;
	}

	public void setTotal_page(int total_page) {
		this.total_page = total_page;
	}

	public List<TopicDetail> getDetails() {
		return detail;
	}

	public void setDetails(List<TopicDetail> details) {
		this.detail = details;
	}

	public List<TopicDetail> getData() {
		return data;
	}

	public void setData(List<TopicDetail> data) {
		this.data = data;
	}
}
