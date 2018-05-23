package cn.lt.game.ui.app.gamegift.beans;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 *  baseGameInfo 对应服务器的game_baseinfo;
 *  单个礼包游戏的基础数据信息；
 *  额外添加游戏礼包的相关信息
 * @author Administrator
 *
 */
public class GiftGameBaseData implements Serializable{

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getVersion_code() {
		return version_code;
	}

	public void setVersion_code(int version_code) {
		this.version_code = version_code;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getDownload_link() {
		return download_link;
	}

	public void setDownload_link(String download_link) {
		this.download_link = download_link;
	}

	public int getDownload_display() {
		return download_display;
	}

	public void setDownload_display(int download_display) {
		this.download_display = download_display;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public String getCat() {
		return cat;
	}

	public void setCat(String cat) {
		this.cat = cat;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getReceived_count() {
		return received_count;
	}

	public void setReceived_count(int received_count) {
		this.received_count = received_count;
	}

	public int getGift_count() {
		return gift_count;
	}

	public void setGift_count(int gift_count) {
		this.gift_count = gift_count;
	}

	/**
	 * 游戏id；
	 */
	private int id;

	/**
	 * 游戏名称
	 */
	private String title;

	/**
	 * 游戏图表地址；
	 */
	private String icon;

	/**
	 * 游戏大小；
	 */
	private long size;

	/**
	 * 游戏版本
	 */
	private String version;

	/**
	 * 游戏版本code
	 */
	private int version_code;

	/**
	 * 游戏包名；
	 */
	@SerializedName("package")
	private String packageName;
	
	/**
	 * 游戏下载链接地址；
	 */
	private String download_link;
	
	/**
	 * 游戏下载次数；
	 */
	private int download_display;
	
	/**
	 * 游戏安装包md5值；
	 */
	private String md5;
	
	/**
	 * 游戏小编点评；
	 */
	private String review;
	
	/**
	 * 游戏最后更新时间；
	 */
	private String updated_at;
	
	/**
	 * 游戏简介；
	 */
	private String summary;
	
	/**
	 * 游戏版本特性；
	 */
	private String feature;
	
	/**
	 * 游戏分类；
	 */
	private String cat;

	/**
	 * 游戏评分；
	 */
	private String rate;
	
	/**
	 * 游戏评论总数；
	 */
	private String comments;
	
	/**
	 * 游戏礼包总数；
	 */
	private int total;
	
	/**
	 * 游戏礼包已经领取数量；
	 */
	private int received_count;
	
	/**
	 * 游戏剩余礼包数量；
	 */
	private int gift_count;
	
	
	/***
	 * 游戏社区ID
	 */
	private int forum_id;

	public int getForum_id() {
		return forum_id;
	}

	public void setForum_id(int forum_id) {
		this.forum_id = forum_id;
	}
	

}
