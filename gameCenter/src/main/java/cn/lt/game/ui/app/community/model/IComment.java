package cn.lt.game.ui.app.community.model;

public interface IComment extends ITopic, IGroup {
	int getCommentNum();
	void setCommentNum(int num);
}
