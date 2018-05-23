package cn.lt.game.ui.app.community.model;

import cn.lt.game.ui.app.community.EventTools;
//草稿箱普通的需要删除的event（非编辑页面发送成功）
public class DraftsDeletEvent {
	private int tag = EventTools.DRAFTSDELET_TAG;
	private DraftBean db;

	public DraftBean getDb() {
		return db;
	}

	public int getTag() {
		return tag;
	}

	public DraftsDeletEvent(DraftBean db) {
		this.db = db;
	}
}
