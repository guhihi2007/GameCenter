package cn.lt.game.ui.app.community.model;

import java.io.Serializable;
import java.util.ArrayList;
//话题Bean
public class SendTopicBean implements Serializable {
	private String topic_title;
	private String topic_content;
	private int group_id;
	private String category_id;
	private ArrayList<String> paths;
	private String tag = "0";
	private ArrayList<Category> categoryList;

	public ArrayList<Category> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<Category> categoryList) {
		this.categoryList = categoryList;
	}

	public String getTopic_title() {
		return topic_title;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setTopic_title(String topic_title) {
		this.topic_title = topic_title;
	}

	public String getTopic_content() {
		return topic_content;
	}

	public void setTopic_content(String topic_content) {
		this.topic_content = topic_content;
	}


	public int getGroup_id() {
		return group_id;
	}

	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public ArrayList<String> getPaths() {
		return paths;
	}

	public void setPaths(ArrayList<String> paths) {
		this.paths = paths;
	}

}
