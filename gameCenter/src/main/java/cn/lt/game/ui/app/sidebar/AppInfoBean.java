package cn.lt.game.ui.app.sidebar;

public class AppInfoBean {
	private String name;
	private String value;
	public AppInfoBean(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	
	public AppInfoBean(String name) {
		super();
		this.name = name;
	}


	public AppInfoBean() {
		super();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "AppInfoBean [name=" + name + ", value=" + value + "]";
	}
	
	
}
