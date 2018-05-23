package cn.lt.game.ui.app.community.model;

/**
 * 
 * 共用此结构： 社区话题基础数据{topic_baseinfo} 话题详情 收藏的话题 评论的话题 发表的话题 小组话题列表 最新话题
 * 
 * 后台消息里没有返回的字段，采用默认值
 *
 */
public class TopicDetail extends Author implements ILike, IReadingNum, IComment {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4206086202404276219L;
	public int group_id; // 所属小组id
	public String group_title;// 小组名称

	public int topic_id; // 话题id
	public String topic_title;// 话题标题
	public String topic_summary;// 话题简介
	public String published_at;// 话题发表时间
	public boolean is_fulltext;// 是否已展⽰示完全⽂文内容

	public int hit_count;// 点击数
	public int comment_count;// 评论数
	public int upvote_count;// 点赞数
	public boolean is_upvoted;// 是否已对此话题点赞
	public boolean is_collected;// 是否已对此话题收藏
	public int comment_id;// 评论id

	public String share_link;// 话题分享链接

	public AppendixData appendix;// 附件数据

	// 仅话题详情使用
	public String topic_content;// 话题全文内容

	// 仅评论的话题使用
	public String comment_content;// 评论内容

	// 话题的状态（normal正常/verifying审核中）
	public String status;


	/**
	 * 评论时间
	 */
	public String commented_at;
	
	private boolean is_push;
	
	/**是否置顶话题*/
	private boolean is_top;
	
	/**是否精华话题*/
	private boolean is_essence;
	
	/**是否采纳话题*/
	private boolean is_accept;
	

	//类型advertisement 或者 topic
	public String type;


	//以下数据是广告项才包含的数据
	/**广告标题*/
	public String title;
	/**广告图*/
	public String image;
	/**广告内容*/
	public String content;
	/**广告跳转链接*/
	public String link;


	public boolean isIs_push() {
		return is_push;
	}

	public void setIs_push(boolean is_push) {
		this.is_push = is_push;
	}
	
	

	public boolean isIs_top() {
		return is_top;
	}

	public void setIs_top(boolean is_top) {
		this.is_top = is_top;
	}

	public boolean isIs_essence() {
		return is_essence;
	}

	public void setIs_essence(boolean is_essence) {
		this.is_essence = is_essence;
	}

	public boolean isIs_accept() {
		return is_accept;
	}

	public void setIs_accept(boolean is_accept) {
		this.is_accept = is_accept;
	}

	@Override
	public boolean isLiked() {
		return is_upvoted;
	}

	@Override
	public int getLikeNum() {
		return upvote_count;
	}

	@Override
	public void setLikeNum(int num) {
		upvote_count = num;
	}

	@Override
	public LikeSubjectType getLikeType() {
		return LikeSubjectType.TOPIC;
	}

	@Override
	public int getTopicId() {
		return topic_id;
	}

	@Override
	public void setLiked(boolean isLiked) {
		is_upvoted = isLiked;
	}

	@Override
	public int getGroupId() {
		return group_id;
	}

	@Override
	public int getReadingNum() {
		return hit_count;
	}

	@Override
	public void setReadingNum(int num) {
		hit_count = num;
	}

	@Override
	public String getGroupTitle() {
		return group_title;
	}

	@Override
	public int getCommentNum() {
		return comment_count;
	}

	@Override
	public void setCommentNum(int num) {
		comment_count = num;
	}

	@Override
	public String getTopicTitle() {
		return topic_title;
	}

	@Override
	public String getTopicContent() {
		return topic_content != null ? topic_content : topic_summary;
	}

}