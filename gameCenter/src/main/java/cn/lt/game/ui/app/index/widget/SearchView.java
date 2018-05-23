package cn.lt.game.ui.app.index.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.ui.app.search.SearchActivity;

public class SearchView extends FrameLayout implements
        android.view.View.OnClickListener {

    private Context mContext;

    private boolean isIndex;

    private LinearLayout mRoot;

    private View btn_downloadMgr;

    private TextView mRedPoint;

    private RelativeLayout mRealSearchView;

    private int mBackgroudColor;

    public SearchView(Context context) {
        super(context);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.SearchView);
        isIndex = mTypedArray
                .getBoolean(R.styleable.SearchView_is_index, false);
        mBackgroudColor = mTypedArray.getInt(
                R.styleable.SearchView_backgroudColor, Color.TRANSPARENT);
        mTypedArray.recycle();
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.lt_search, this);
        init();
    }

    private void init() {
        mRoot = (LinearLayout) findViewById(R.id.title_bar);
        btn_downloadMgr = findViewById(R.id.rl_downloadMgr);
        mRedPoint = (TextView) findViewById(R.id.tv_titleBar_redPoint);
        mRealSearchView = (RelativeLayout) findViewById(R.id.rl_content);
        if (isIndex) {
            btn_downloadMgr.setVisibility(View.GONE);
            mRedPoint.setVisibility(View.GONE);
        }
        mRoot.setBackgroundColor(mBackgroudColor);
        mRealSearchView.setOnClickListener(this);
        btn_downloadMgr.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_content:
                mContext.startActivity(new Intent(mContext,
                        SearchActivity.class));
                break;
            case R.id.rl_downloadMgr:
                ActivityActionUtils.JumpToManager(mContext, 0);
            default:
                break;
        }
    }


}
