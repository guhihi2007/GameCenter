package cn.lt.game.ui.app.gamegift.beans;

import java.io.Serializable;

/***
 * 单个游戏礼包基础数据类
 */
public class GiftBaseData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGift_title() {
		return gift_title;
	}

	public void setGift_title(String gift_title) {
		this.gift_title = gift_title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getRemian() {
		return remain;
	}

	public void setRemian(int remian) {
		this.remain = remian;
	}

	public boolean isIs_received() {
		return is_received;
	}

	public void setIs_received(boolean is_received) {
		this.is_received = is_received;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getClosed_at() {
		return closed_at;
	}

	public void setClosed_at(String closed_at) {
		this.closed_at = closed_at;
	}

	public GiftGameBaseData getGame_info() {
		return game_info;
	}

	public void setGame_info(GiftGameBaseData game_info) {
		this.game_info = game_info;
	}

	public int getGift_id() {
		return gift_id;
	}

	public void setGift_id(int gift_id) {
		this.gift_id = gift_id;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public int getReceived_count() {
		return received_count;
	}

	public void setReceived_count(int received_count) {
		this.received_count = received_count;
	}

	/**
	 * 礼包id
	 */
	private int id;

	/**
	 * 礼包id
	 */
	private int gift_id;

	/**
	 * 游戏icon
	 */
	private String icon;

	/**
	 * 游戏名称
	 */
	private String title;

	/**
	 * 礼包名称
	 */
	private String gift_title;

	/**
	 * 礼包总数
	 */
	private int total;

	/**
	 * 礼包剩余数量；
	 */
	private int remain;

	/**
	 * 礼包内容；
	 */
	private String content;

	/**
	 * 礼包是否已经被领取；
	 */
	private boolean is_received;

	/**
	 * 礼包有效期；
	 */
	private String closed_at;

	/**
	 * 礼包激活码；
	 */
	private String code;

	/**
	 * 礼包的使用方式；
	 */
	private String usage;

	/**
	 * 已经领取数量
	 */
	private int received_count;

	/**
	 * 礼包对应的游戏基础信息；
	 */
	private GiftGameBaseData game_info;

}
