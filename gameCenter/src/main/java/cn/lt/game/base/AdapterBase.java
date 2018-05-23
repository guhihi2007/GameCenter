package cn.lt.game.base;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于所有BaseAdapter 扩展的基类； 本程序可用此AdapterBase
 * 
 * @author wenchao
 * 
 * @param <T>
 */
public abstract class AdapterBase<T> extends BaseAdapter {

	private List<T> mList;

	protected Context mContext;

	public AdapterBase(Context context) {
		this(context,new ArrayList<T>());
	}

	public AdapterBase(Context context, List<T> installedGameList) {
		this.mContext = context;
		this.mList = installedGameList;
	}

	/**
	 * 获得list列表
	 * 
	 * @return
	 */
	public List<T> getList() {
		return mList;
	}

	/**
	 * 设置list列表
	 * 
	 * @param list
	 */
	public void setList(List<T> list) {
		mList.clear();
		appendToList(list);
	}

	private void appendToList(List<T> list) {
		if (list == null) {
			return;
		}
		mList.addAll(list);
		notifyDataSetChanged();
	}

	public void clear() {
		mList.clear();
		notifyDataSetChanged();
	}

	public void appendToTopList(List<T> list) {
		if (list == null)
			return;
		mList.addAll(0, list);
		notifyDataSetChanged();
	}

	/**
	 * 获取最后一个
	 * 有可能为null
	 * @return
	 */
	public T getLastItem() {
		if(mList.size()<1){
			return null;
		}
		return mList.get(mList.size() - 1);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}




}
