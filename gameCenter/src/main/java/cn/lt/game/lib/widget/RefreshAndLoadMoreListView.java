package cn.lt.game.lib.widget;


import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.LogUtils;


/***
 * 下拉刷新/上拉加载更多
 * @author ATian
 */
public class RefreshAndLoadMoreListView extends FrameLayout {

    private static final String TAG = "LoadMoreListView";
    private LayoutInflater mInflater;
    // footer view
    private RelativeLayout mFooterView;
    private TextView mLabLoadMore;
    private ProgressBar mProgressBarLoadMore;
    private SwipeRefreshLayout mSwipView;
    // Listener to process load more items when user reaches the end of the list
    private OnLoadMoreListener mOnLoadMoreListener;
    private IOnScrollStateChanged mScrollChangeListener;

    // To know if the list is loading more items
    private boolean mIsLoadingMore = false;
    private ListView mListView;

    private boolean mCanLoadMore = true;
    private int mCurrentScrollState;

    private String loadMoreText = "~我是有底线的~";

    public RefreshAndLoadMoreListView(Context context) {
        super(context);
        init(context);
    }

    public RefreshAndLoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefreshAndLoadMoreListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = mInflater.inflate(R.layout.refreshandload_list_view, this);
        mSwipView = (SwipeRefreshLayout) rootView.findViewById(R.id.my_swipe_view);
        mListView = (ListView) rootView.findViewById(R.id.my_listview);
        mFooterView = (RelativeLayout) mInflater.inflate(R.layout.load_more_footer, null);
        mLabLoadMore = (TextView) mFooterView.findViewById(R.id.no_more_textView);
        mProgressBarLoadMore = (ProgressBar) mFooterView.findViewById(R.id.load_more_progressBar);
        mSwipView.setColorSchemeResources(R.color.theme_green);
        mSwipView.setProgressBackgroundColorSchemeResource(R.color.white);
        mListView.setOnScrollListener(new MyScrollListener());

    }

    public void setAdapter(BaseAdapter adapter, boolean hasFooter) {
        if(!hasFooter){
            mListView.addFooterView(mFooterView);
        }
        mListView.setAdapter(adapter);
    }

    public ListView getmListView() {
        return mListView;
    }

    /**
     * Set the listener that will receive notifications every time the list
     * scrolls.
     *
     * @param l The scroll listener.
     */
    public void setOnScrollListener(OnScrollListener l) {
        mListView.setOnScrollListener(l);
    }

    /**
     * Register a callback to be invoked when this list reaches the end (last
     * item be visible)
     *
     * @param onLoadMoreListener The callback to run.
     */

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public void setmOnRefrshListener(SwipeRefreshLayout.OnRefreshListener mOnRefrshListener) {
        mSwipView.setOnRefreshListener(mOnRefrshListener);
    }

    public void setCanLoadMore(boolean canLoadMore) {
        mCanLoadMore = canLoadMore;
        mLabLoadMore.setVisibility(View.GONE);
        if(!canLoadMore){
            mProgressBarLoadMore.setVisibility(View.GONE);
        }
    }

    public void setDividerHeight(int height) {
        if (null != mListView) {
            mListView.setDividerHeight(height);
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        if (null != mListView) {
            mListView.setOnItemClickListener(listener);
        }
    }

    /***
     * 是否禁用下拉
     * @param enabled
     */
    public void setRefreshEnabled(boolean enabled) {
        mSwipView.setEnabled(enabled);
    }

    public void onLoadMore() {
        Log.d(TAG, "onLoadMore");
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore();
        }
    }

    /**
     * Notify the loading more operation has finished
     */
    public void onLoadMoreComplete() {
        mIsLoadingMore = false;
        mSwipView.setRefreshing(false);
    }

    /***
     * 加载失败
     */
    public void onLoadingFailed() {
        mIsLoadingMore = false;
        mProgressBarLoadMore.setVisibility(View.GONE);
        mSwipView.setRefreshing(false);
        mLabLoadMore.setVisibility(View.VISIBLE);
        mLabLoadMore.setText("~加载失败了~");
    }

    public void setMyOnScrollListener(IOnScrollStateChanged myOnScrollListener) {
        this.mScrollChangeListener = myOnScrollListener;
    }

    /**
     * Interface definition for a callback to be invoked when list reaches the
     * last item (the user load more items in the list)
     */
    public interface OnLoadMoreListener {
        /**
         * Called when the list reaches the last item (the last item is visible
         * to the user)
         */
        void onLoadMore();
    }

    public interface IOnScrollStateChanged {
        void onScrollChangeListener(int scrollState);
    }

    public interface IScrollTopListener {
        void onScrollTop(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    public IScrollTopListener mScrollTopListener;

    public void setmScrollTopListener(IScrollTopListener mScrollTopListener) {
        this.mScrollTopListener = mScrollTopListener;
    }

    class MyScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            mCurrentScrollState = scrollState;
            if (mScrollChangeListener != null) {
                mScrollChangeListener.onScrollChangeListener(scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            LogUtils.i(TAG, "ListView onScroll()");
            if (mScrollTopListener != null) {
                mScrollTopListener.onScrollTop(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
            if (visibleItemCount == totalItemCount) {
                mProgressBarLoadMore.setVisibility(View.GONE);
                mLabLoadMore.setVisibility(View.GONE);
                return;
            }
            boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
            if (!mIsLoadingMore && loadMore && mCurrentScrollState != OnScrollListener.SCROLL_STATE_IDLE) {
                if (!mCanLoadMore) {
                    mLabLoadMore.setVisibility(View.VISIBLE);
                    mLabLoadMore.setText(loadMoreText);
                    return;
                }
                mProgressBarLoadMore.setVisibility(View.VISIBLE);
                mLabLoadMore.setVisibility(View.VISIBLE);
                mLabLoadMore.setText("加载中");
                mIsLoadingMore = true;
                onLoadMore();
            }
        }
    }

    public void setLabLoadMoreText(String text) {
        loadMoreText = text;
    }
}