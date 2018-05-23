package cn.lt.game.statistics;

/**
 * 所有事件映射关系类；
 * 
 * 出现click相关事件时需要检查该完整路径是否需要将该事件数据记录保存；
 * 
 * @author Administrator
 * 
 */
public class NodeConstant {


	/** 下载按钮点击事件(不包括游戏详情页面的下载按钮以及一键下载按钮) */
	public static final String DownloadButtonClick = "BTCLK";

	/** 排行开始节点 */
	public static final String RankRoot = "RANK";



	/** 首页开始节点 */
	public static final String IndexActivity = "INDEX";



	/** 游戏详情页面显示节点 */
	public static final String GameDetailHomeActivity = "GMDT";


	/** 搜索点击事件 */
	/** 搜索开始节点 */
	public static final String SearchRoot = "SEARCH";

	// 分类事件节点；
	public static final String CatRoot = "CAT";


}
