package cn.lt.game.ui.app.community.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.ui.app.personalcenter.login.UserNameAdapter;

public class AutoCompletePopWindow extends PopupWindow implements Callback {

    private Context mContext;
    private ListView mListView;
    private TextView mNoDateView;
    private BaseAdapter mAdapter;

    public AutoCompletePopWindow(Context context, BaseAdapter mAdapter) {
        super(context);
        mContext = context;
        this.mAdapter = mAdapter;
        init();
    }

    public void setItemListener(OnItemClickListener listener) {
        ((UserNameAdapter) mAdapter).setListener(listener, this);
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.auto_complete__layout, null);
        setContentView(view);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);
        mListView = (ListView) view.findViewById(R.id.listview);
        mNoDateView = (TextView) view.findViewById(R.id.nodate);
        mListView.setAdapter(mAdapter);
        layoutChangeByDate();
    }

    private void layoutChangeByDate() {
        if (mAdapter.getCount() != 0) {
            mListView.setVisibility(View.VISIBLE);
            mNoDateView.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.GONE);
            mNoDateView.setVisibility(View.VISIBLE);
            mNoDateView.setText("没有历史记录");
        }
    }


    @Override
    public boolean handleMessage(Message msg) {
        layoutChangeByDate();
        return false;
    }

}
