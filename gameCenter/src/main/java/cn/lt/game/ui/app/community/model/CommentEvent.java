package cn.lt.game.ui.app.community.model;

import cn.lt.game.ui.app.community.EventTools;

public class CommentEvent {
	private int tag = EventTools.COMMENT_TAG;
	private boolean result;
	private int groupId;
	private int topicId;

	public int getTag() {
		return tag;
	}

	public boolean getResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public int getGroupId() {
		return groupId;
	}

	public int getTopicId() {
		return topicId;
	}

	public CommentEvent(boolean result, int groupid, int topicId) {
		this.result = result;
		this.groupId = groupid;
		this.topicId = topicId;
	}
}