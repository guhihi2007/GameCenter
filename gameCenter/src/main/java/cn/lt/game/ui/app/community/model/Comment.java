package cn.lt.game.ui.app.community.model;

import java.util.List;

/**
 * 
 * 评论列表信息 用途： 话题评论列表项
 *
 */
public class Comment extends Author  {
	public int comment_id;// 评论id
	public String comment_content;// 评论内容
	public String published_at;// 发表时间
	public int reply_count;// 回复数
	public int upvote_count;// 点赞数
	public boolean is_upvoted;// 是否已点赞
	public boolean is_collected;// 是否已收藏
	public int floor;
	public AppendixData appendix;// 附件数据
	public List<Reply> replies;// 回复列表

	public int group_id; // 所属小组id
	public String group_title;// 小组名称
	public int topic_id; // 话题id
	public String topic_title;// 话题标题
	public String comment_summary;//评论简介
	// 话题的状态（normal正常/verifying审核中）
	public String status;

	public String type;

	/**广告标题*/
	public String title;
	/**广告图*/
	public String image;
	/**广告内容*/
	public String content;
	/**广告跳转链接*/
	public String link;

}
