package cn.lt.game.ui.app.community.model;

import cn.lt.game.ui.app.community.EventTools;

public class TopicEvent {
	private int tag = EventTools.TOPIC_TAG;
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

	public TopicEvent(boolean result, int groupid, int topicId) {
		this.result = result;
		this.groupId = groupid;
		this.topicId = topicId;
	}
}