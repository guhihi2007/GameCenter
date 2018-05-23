package cn.lt.game.ui.app.community.model;

import cn.lt.game.ui.app.community.EventTools;

/***
 * 话题、评论、回复清除通知事件
 * 
 * @author ltbl
 * 
 */
public class ClearEvent {
	private int tag = EventTools.CLEAR_TAG;
	private boolean result;

	public int getTag() {
		return tag;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public ClearEvent(boolean result) {
		this.result = result;
	}
}
