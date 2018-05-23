package cn.lt.game.ui.app.community.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import java.util.List;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.ui.app.community.model.Category;
import cn.lt.game.ui.app.community.topic.group.SpinerPopAdapter;
import cn.lt.game.ui.app.community.topic.group.SpinerPopAdapter.IOnItemSelectListener;
import cn.lt.game.ui.app.community.topic.group.SpinerPopAdapter.IOnMenuSelectListener;

/***
 * 单选框
 * 
 * @author tiantian
 * @des 用于小组话题列表里面的下拉列表
 */
public class SpinerPopWindow extends PopupWindow implements OnItemClickListener, OnDismissListener {

	private Activity mContext;
	private ListView mListView;
	private List<Category> dataList;
	public  SpinerPopAdapter adapter;
	private IOnItemSelectListener mItemSelectListener;
	private IOnMenuSelectListener mMenuSelectListener;

	public SpinerPopWindow(Activity context, List<Category> list, Popcallback callback) {
		super(context);
		this.mContext = context;
		this.dataList = list;
		this.mCallback = callback;
		initListView();
	}

	public SpinerPopWindow(Activity context, List<Category> list) {
		super(context);
		this.mContext = context;
		this.dataList = list;
		initListView();
	}

	/**
	 * 设置列表中的点击监听
	 * 
	 */
	public void setItemListener(IOnItemSelectListener listener) {
		mItemSelectListener = listener;
	}

	public void setmMenuSelectListener(IOnMenuSelectListener mMenuSelectListener) {
		this.mMenuSelectListener = mMenuSelectListener;
	}

	private void initListView() {
		View view = LayoutInflater.from(mContext).inflate(R.layout.spiner_window_layout, null);
		setContentView(view);
//		setWidth(LayoutParams.WRAP_CONTENT);
		setWidth(MyApplication.width*9/10);
		setPopHeight();
		setFocusable(true);// true 内容可点击
		setOutsideTouchable(true); // true:外部不可点击
		ColorDrawable dw = new ColorDrawable(0x00);
		setBackgroundDrawable(dw);
		setBackgroudAlpha(0.5f);
		mListView = (ListView) view.findViewById(R.id.listview);
		adapter = new SpinerPopAdapter(mContext, dataList);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);
		this.setOnDismissListener(this);
	}
	/***
	 * 设置弹窗框的高度（根据IITEM个数自适应高度）
	 */
	private void setPopHeight(){
		if (dataList.size() > 6) {
			setHeight(6 * (int)mContext.getResources().getDimension(R.dimen.user_center_48));
		} else {
			setHeight(LayoutParams.WRAP_CONTENT);
		}
	}
	/***
	 * 设置背景透明度
	 * 
	 * @param alpha
	 */
	public void setBackgroudAlpha(float alpha) {
		WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
		lp.alpha = alpha;
		mContext.getWindow().setAttributes(lp);
		mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		if (mItemSelectListener != null) {
			mItemSelectListener.onItemClick(pos);

		}
		if (mMenuSelectListener != null) {
			mMenuSelectListener.onMenuClick(pos);// 这个用来回调标题栏里面的弹出框事件
		}
		dismiss();
	}

	public interface Popcallback {
		void popCallBack();
	}

	private Popcallback mCallback;

	public Popcallback getmCallback() {
		return mCallback;
	}

	public void setmCallback(Popcallback mCallback) {
		this.mCallback = mCallback;
	}

	@Override
	public void onDismiss() {
		if (mCallback != null) {
			mCallback.popCallBack();// 回调只用来复位箭头和点击状态
		}
		this.setBackgroudAlpha(1.0f);
	}
}
