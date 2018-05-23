package cn.lt.game.ui.app.community.model;

import cn.lt.game.ui.app.community.EventTools;
//草稿箱编辑跳转然后发送成功需要发送的event
public class DraftsDeleteByEditeEvent {
	private int tag = EventTools.DRAFTSDELETBYEDIT_TAG;
	private String time;

	public int getTag() {
		return tag;
	}

	public String getTime() {
		return time;
	}

	public DraftsDeleteByEditeEvent(String time) {
		this.time = time;
	}
}
