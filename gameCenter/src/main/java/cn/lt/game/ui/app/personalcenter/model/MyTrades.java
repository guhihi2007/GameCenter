package cn.lt.game.ui.app.personalcenter.model;

public class MyTrades {
	private long id;
	private String order_id;
	private String pay;
	private String spend;
	private String platform_received_at;
	private String game_received_at;
	private String pay_method;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getPay() {
		return pay;
	}

	public void setPay(String pay) {
		this.pay = pay;
	}

	public String getSpend() {
		return spend;
	}

	public void setSpend(String spend) {
		this.spend = spend;
	}

	public String getPlatform_received_at() {
		return platform_received_at;
	}

	public void setPlatform_received_at(String platform_received_at) {
		this.platform_received_at = platform_received_at;
	}

	public String getGame_received_at() {
		return game_received_at;
	}

	public void setGame_received_at(String game_received_at) {
		this.game_received_at = game_received_at;
	}

	public String getPay_method() {
		return pay_method;
	}

	public void setPay_method(String pay_method) {
		this.pay_method = pay_method;
	}
}
