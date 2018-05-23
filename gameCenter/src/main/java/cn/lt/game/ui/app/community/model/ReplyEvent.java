package cn.lt.game.ui.app.community.model;

import cn.lt.game.ui.app.community.EventTools;
//回复Event
public class ReplyEvent {
	private int tag = EventTools.REPLY_TAG;
	private boolean result;
	private int groupId;
	private int topicId;

	public int getTag() {
		return tag;
	}

	public int getGroupId() {
		return groupId;
	}

	public int getTopicId() {
		return topicId;
	}

	public boolean isResult() {
		return result;
	}

	public ReplyEvent(boolean result, int groupid, int topicId) {
		this.result = result;
		this.groupId = groupid;
		this.topicId = topicId;
	}
}
