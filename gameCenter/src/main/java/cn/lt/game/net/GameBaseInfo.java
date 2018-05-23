package cn.lt.game.net;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 对应后台文档中的“游戏统一基础数据（game_baseinfo）”
 */
public class GameBaseInfo implements Serializable{

	public  int id;   //游戏id
	protected String title;  //游戏名称
	protected String cat;    //游戏分类
	protected String icon;  //游戏icon图地址
	protected long size;     //安装包大小（单位：KB）
	protected String md5;    //安装包MD5
	protected String version;  //版本号
	protected int version_code;//版本代号
	protected String feature;  //版本特性
	protected String summary;  //游戏简介
	@SerializedName("package")
	protected String packageName;  //安装包名
	protected boolean is_patch;  //是否为增量包，1是，0不是
	protected String download_link; //下载链接
	protected int download_display;//显示的总下载次数
	protected float rate;//游戏评分（精确到小数点后2位）
	protected int comments;//总评论条数
	protected String review;//小编点评
	protected String updated_at;//最后更新时间
	public int forum_id;//社区ID, 0时是没有社区

}
