package cn.lt.game.ui.app.community.topic.detail;

public enum Orderby {
	ASC("asc", "从新到旧"), DESC("desc", "从旧到新");

	private String english;
	private String chinese;

	Orderby(String english, String chinese) {
		this.english = english;
		this.chinese = chinese;
	}

	public String getEnglish() {
		return english;
	}

	public void setEnglish(String english) {
		this.english = english;
	}

	public String getChinese() {
		return chinese;
	}

	public void setChinese(String chinese) {
		this.chinese = chinese;
	}

}
