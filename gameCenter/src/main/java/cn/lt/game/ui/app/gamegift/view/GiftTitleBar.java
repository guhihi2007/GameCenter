package cn.lt.game.ui.app.gamegift.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.global.Constant;
import cn.lt.game.ui.app.HomeActivity;

public class GiftTitleBar extends FrameLayout {
    private ImageButton imgBT;
    private ImageButton mGoHome;
    private TextView tx;
    private Context mContext;

    public GiftTitleBar(Context context) {
        super(context);
        mContext = context;
    }

    public GiftTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.gift_titel_action_bar, this);
        tx = (TextView) findViewById(R.id.not_serach_tv_title);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NoSearchTitleBar);
        String name = a.getString(R.styleable.NoSearchTitleBar_titleName);
        tx.setText(name);
        a.recycle();
        imgBT = (ImageButton) findViewById(R.id.not_serach_btn_back);
        mGoHome = (ImageButton) findViewById(R.id.ib_go_home);
        imgBT.setOnClickListener(new ClickListener());
        mGoHome.setOnClickListener(new ClickListener());
    }

    public void setTitle(String title) {
        tx.setText(title);
    }

    class ClickListener implements OnClickListener {

        @SuppressLint("InlinedApi")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.not_serach_btn_back:
                    ((Activity) getContext()).finish();
                    break;

                case R.id.ib_go_home:
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(Constant.PAGE_EXTRA, "index");
                    mContext.startActivity(intent);
                    break;
            }
        }

    }
}
