package cn.lt.game.ui.app.community.model;

import cn.lt.game.ui.app.community.EventTools;
//草稿箱需要更新的时候发送此Event
public class DraftsEvent {
	private int tag = EventTools.DRAFTS_TAG;
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

	public DraftsEvent(boolean result, int groupid, int topicId) {
		this.result = result;
		this.groupId = groupid;
		this.topicId = topicId;
	}
}