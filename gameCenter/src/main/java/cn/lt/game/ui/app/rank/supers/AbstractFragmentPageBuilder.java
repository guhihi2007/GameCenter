package cn.lt.game.ui.app.rank.supers;

import android.support.v4.app.Fragment;

import java.util.ArrayList;

public abstract class AbstractFragmentPageBuilder<T extends Fragment> {
	
	public ArrayList<T> createFragments() {
		ArrayList<T> fragmentList = new ArrayList<T>();
		for(String type : getPageTypes()){
			T fragment = newFragment(type);
			fragmentList.add(fragment);
		}
		return fragmentList;
	}
	
	/**
	 * 创建fragment的实例
	 * @param type 
	 * 			fragment页面类型
	 * @return
	 * 		fragment的实例
	 */
	protected abstract T newFragment(String type);
	
	/**
	 * 提供页面类型数组
	 * @return
	 * 		页面类型数组
	 */
	protected abstract String[] getPageTypes();
}
