package cn.lt.game.ui.app.community.model;

//点赞对象的类型
public enum LikeSubjectType {
	TOPIC("topic"),//话题
	COMMENT("comment");//评论
	
	private String type;
	
	LikeSubjectType(String type) {
		this.type = type;
	} 
	
	public String toString() {
		return type;
	}
}