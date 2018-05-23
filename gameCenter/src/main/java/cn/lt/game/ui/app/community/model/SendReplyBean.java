package cn.lt.game.ui.app.community.model;

import java.io.Serializable;
//回复Bean
public class SendReplyBean implements Serializable {
	private int topicId;
	private int GroupId;


	public int getGroupId() {
		return GroupId;
	}

	public void setGroupId(int groupId) {
		GroupId = groupId;
	}

	private int commentId;
	private int acceptorId;
	private String content;
	private String tag = "0";

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	private String acceptorNickname;
	private boolean isSingle;

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public int getAcceptorId() {
		return acceptorId;
	}

	public void setAcceptorId(int acceptorId) {
		this.acceptorId = acceptorId;
	}

	public String getAcceptorNickname() {
		return acceptorNickname;
	}

	public void setAcceptorNickname(String acceptorNickname) {
		this.acceptorNickname = acceptorNickname;
	}

	public boolean isSingle() {
		return isSingle;
	}

	public void setSingle(boolean isSingle) {
		this.isSingle = isSingle;
	}

}
