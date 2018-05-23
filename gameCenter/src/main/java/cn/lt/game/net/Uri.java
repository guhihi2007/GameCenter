package cn.lt.game.net;

public class Uri {

	public static final String CATS_URI = "/cats"; // 游戏分类
	public static final String TAGS_HOtCATS = "/hotcats";//热门分类
	public static final String GAMES_MORE_URI = "/games/more"; // 文件夹跳转
	// 用户中心
	public static final String USER_BASE_URI = "/api/base"; // base数据
	public static final String USER_SIGIN_URI = "/user/signin"; // 用户登录
	public static final String USER_CREATE_URI = "/user/create"; // 用户注册
	public static final String USER_INFO_URI = "/user/info"; // 获取个人资料（进入编辑页）
	public static final String USER_AVATAR_URI = "/user/avatar"; // 设置头像
	public static final String USER_UPDATE_URI = "/user/update"; // 个人资料提交
	public static final String USER_TRADES_URI = "/trades"; // 我的交易
	public static final String USER_PWD_URI = "/user/pwd"; // 修改密码
	public static final String USER_SMS_SEND_URI = "/sms/send"; // 发送短信验证（注册时的短信验证和忘记密码时的短信验证）
	public static final String USER_SMS_CHECK_URI = "/sms/check"; // 忘记密码手机找回验证手机
	public static final String USER_BIND_URI = "/user/bind"; // 绑定手机
	public static final String USER_BIND_CHECK_PHONE = "/user/change";// 修改手机绑定的时候，验证旧手机
	// 社区
	public static final String COM_TOPIC_NEW = "/topics/new";//最新话题列表
	public static final String COM_GROUP = "/groups";//推荐小组列表
	public static final String MY_COMMUNITY = "/v2/my/hub";//社区-我的社区
	public static final String COM_MULTIMEDIAS_PHOTOS_UPLOAD = "/multimedia/photos/upload";//图⽚片上传
	public static final String COM_TOPICS_CREATE = "/v2/topic/create";//发表话题
	public static final String COM_USERS_TOPIC = "/v2/my/topic";//我的社区-发表的话题
	public static final String COM_USERS_COMMENTS = "/v2/my/comment";  //我的社区-评论的话题

	public static final String COM_USERS_COLLECT_TOPICS = "/users/collects/topics";//我的社区-收藏的话题
	public static final String COM_MY_GROUP = "/v2/my/group";//我的社区-我的小组
	/**我的私信列表*/
	public static final String COM_MY_PRIP_MSG = "/v2/my/letter";
	/**发送私信*/
	public static final String COM_SEND_PRIP_MSG = "/v2/letter";
	// 游戏详情
	public static String getOtherGameDetailUri(String pack) {
		return "/games/" + pack;
	}

	//发表评论
	public static String getCommentCreateUri(int topicId) {
		return "/v2/topic/" + topicId + "/comment/create";
	}
	
	//发表回复
	public static String getrReplyCreateUri(int topicId, int commentId) {
		return "/topics/" + topicId + "/comments/" + commentId + "/reply/create";
	}
	
	//小组成员列表
	public static String getGroupMembersUri(int groupId) {
		return "/v2/group/" + groupId + "/member";
	}
	
	//小组话题列表
	public static String getGroupTopicsUri(int groupId) {
		return "/v2/group/" + groupId + "/topic";
	}
	
	//小组信息
	public static String getGroupInfoUri(int groupId) {
		return "/groups/" + groupId + "/info";
	}
	
	//话题详情
	public static String getTopicDetailUri(int topicId) {
		return "/v2/topic/" + topicId;
	}
	
	//话题评论列表
	public static String getTopicCommentsUri(int topicId) {
		return "/v2/topic/" + topicId + "/comment";
	}

	//评论回复列表
	public static String getCommentRepliesUri(int topicId, int commentId) {
		return "/topics/" + topicId + "/comments/" + commentId + "/replies";
	}

	//话题点赞
	public static String getTopicLikeUri(int topicId) {
		return "/topics/" + topicId + "/upvote";
	}
	
	//取消点赞
	public static String getTopicCancelLikeUri(int topicId) {
		return "/topics/" + topicId + "/upvote";
	}
	
	//话题点赞列表
	public static String getTopicLikesUri(int topicId) {
		return "/v2/topic/" + topicId + "/upvoteList";
	}
	
	//加入小组
	public static String getJoinGroupUri(int groupId) {
		return "/groups/" + groupId + "/join";
	}
	//退出小组
	public static String quitGroupUri(int groupId) {
		return "/v2/group/" + groupId + "/join";
	}
	
	//检测用户是否加入该小组
	public static String getIsJoinGroupUri(int groupId){
		return "/groups/" + groupId + "/checkjoin";
	}
	
	//分享话题
	public static String getTopicShareUri(int topicId) {
		return "/topics/" + topicId + "/share";
	}
	
	//收藏话题
	public static String getCollectTopicUri(int topicId) {
		return "/topics/" + topicId + "/collect";
	}

	//取消收藏话题
	public static String getCancelCollectUri(int topicId) {
		return "/topics/" + topicId + "/collect";
	}
	
	//推送
	public static String getNotic(int noticId){
		return "/notice/"+noticId;
		
	}
	//检查用户是否有被禁言
	public static String getIsUserForbade(int groupId){return "/users/check/cansay/"+groupId;}
	//我的关注
	public static String getMyAttentionUri(){ return "/v2/my/follow";}
	//我的粉丝
	public static String getMyFansUri(){return "/v2/my/fan";}
	//社区通知
	public static String getCommunityNoticeUri(){return "/v2/my/notice";}
	//TA的主页
	public static String getOthersPageUri(int userId){return "/v2/ta/"+userId;}
	//TA的话题
	public static String getOthersPageTopicUri(int userId){return "/v2/ta/"+userId+"/topic";}
	//TA的评论
	public static String getOthersPageCommentUri(int userId){return "/v2/ta/"+userId+"/comment";}
	//TA的小组
	public static String getOthersPageGroupUrl(int userId){return "/v2/ta/"+userId+"/group";}
	//加关注&&取消关注
	public static String addAttentionUri(int userId){
		return "/socialization/follow/"+userId;
	}

	/**私信详情 用get请求
	 * 删除私信 用delete请求
	 * */
	public static String getPrivateMessageDetailUri(int userId){
		return "/v2/my/letter/with/"+userId;
	}
	/**根据游戏ID获取社区信息  返回小组ID==0？该游戏没有关联社区:有关联社区*/
	public static String getForumInfoUri(String gameId){
		return "/v2/group/search/by_game_id/"+gameId;
	}

}
