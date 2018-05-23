package cn.lt.game.ui.app.rank.supers;

/**
 * Tab相关信息提供者超类
 * @author 林俊生
 *
 */
public abstract class AbstractTabInfoProvide {
	
	public int[] getTitles() {
		return makeTitles();
	}
	
	public String[] getPageTypes() {
		return makePageTypes();
	}
	
	/**
	 * 生成标题数组
	 * @return
	 * 		tab的标题数组
	 */
	protected abstract int[] makeTitles();
	
	/**
	 * 生成页面对应类型
	 * @return
	 * 		页面类型数组
	 */
	protected abstract String[] makePageTypes();
}
