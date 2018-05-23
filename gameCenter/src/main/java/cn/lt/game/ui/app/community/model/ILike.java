package cn.lt.game.ui.app.community.model;

/**
 * 
 * @author wcn
 *
 *
 */
public interface ILike extends IGroup, ITopic {
	
	boolean isLiked();//是否已点赞
	void setLiked(boolean isLiked);
	int getLikeNum();//获取点赞数目
	void setLikeNum(int num);//设置点赞数据
	LikeSubjectType getLikeType();//获取赞主体的类型:评论 或 话题
	
}
