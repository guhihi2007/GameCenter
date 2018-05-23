package cn.lt.game.ui.app.community.model;

import java.io.Serializable;
import java.util.ArrayList;
//评论Bean，
public class SendCommentBean implements Serializable {
	private String comment_content;
	private ArrayList<String> paths;
	private int topicId;
	private int groupId;
	private String group_title;

	public String getGroup_title() {
		return group_title;
	}

	public void setGroup_title(String group_title) {
		this.group_title = group_title;
	}

	private boolean isAutoJump;

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public boolean isAutoJump() {
		return isAutoJump;
	}

	public void setAutoJump(boolean isAutoJump) {
		this.isAutoJump = isAutoJump;
	}

	private String tag = "0";

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getComment_content() {
		return comment_content;
	}

	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}

	public ArrayList<String> getPaths() {
		return paths;
	}

	public void setPaths(ArrayList<String> paths) {
		this.paths = paths;
	}

}
