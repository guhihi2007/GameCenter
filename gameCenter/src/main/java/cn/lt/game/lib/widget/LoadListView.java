package cn.lt.game.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.LogUtils;

/**
 * @author chengyong
 * @time 2017/7/29 14:23
 * @des ${TODO}
 */

public class LoadListView extends ListView implements AbsListView.OnScrollListener,Cloneable{
    private static final String TAG = "LoadMoreListView";
    private String loadMoreText = "~我是有底线的~";
    private LayoutInflater mInflater;
    private RelativeLayout mFooterView;
    private TextView mLabLoadMore;
    private ProgressBar mProgressBarLoadMore;
    private boolean mIsLoadingMore;
    private double mCurrentScrollState;
    private boolean mCanLoadMore=true;
    private boolean isLoadMoreing;
    private RefreshAndLoadMoreListView.OnLoadMoreListener mOnLoadMoreListener;

    public LoadListView(Context context) {
        super(context);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        LogUtils.e(TAG, "loadlistView 初始化了");
        mFooterView = (RelativeLayout) View.inflate(context,R.layout.load_more_footer, null);
        mLabLoadMore = (TextView) mFooterView.findViewById(R.id.no_more_textView);
        mProgressBarLoadMore = (ProgressBar) mFooterView.findViewById(R.id.load_more_progressBar);
        setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        LogUtils.e(TAG, "onScrollStateChanged---------------");
        if (getLastVisiblePosition() == getAdapter().getCount() - 1) {
            if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
                LogUtils.e(TAG, "通知外界开始加载更多");
                if (!mCanLoadMore) {
                    mLabLoadMore.setVisibility(View.VISIBLE);
                    mProgressBarLoadMore.setVisibility(View.GONE);
                    mLabLoadMore.setText(loadMoreText);
                    return;
                }else{
                    mLabLoadMore.setVisibility(View.VISIBLE);
                    mProgressBarLoadMore.setVisibility(View.VISIBLE);
                    mLabLoadMore.setText("加载中");
                }
                if (mOnLoadMoreListener != null) {
                    if (!isLoadMoreing) {
                        mOnLoadMoreListener.onLoadMore();
                        isLoadMoreing = true;
                    }
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        LogUtils.e(TAG, "onScroll--------------");
    }


    public void setCanLoadMore(boolean canLoadMore) {
        mCanLoadMore = canLoadMore;
        if(!mCanLoadMore){
            mLabLoadMore.setText("加载中");
        }else{
            mLabLoadMore.setText(loadMoreText);
        }
    }

    public void setMyAdapter(BaseAdapter adapter){
        addFooterView(mFooterView);
        setAdapter(adapter);
    }

    public void setOnLoadMoreListener(RefreshAndLoadMoreListView.OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }
    public void onLoadMoreComplete() {
        mProgressBarLoadMore.setVisibility(View.GONE);
    }

}
