package cn.lt.game.ui.app.community.model;

import cn.lt.game.ui.app.community.EventTools;
//跳转详情页面Event
public class JumpEvent {
	private int tag = EventTools.JUMP_TAG;
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

	public JumpEvent(boolean result, int groupid, int topicId) {
		this.result = result;
		this.groupId = groupid;
		this.topicId = topicId;
	}
}