package cn.lt.game.lib.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yanzhenjie.permission.Rationale;

import cn.lt.game.R;
import cn.lt.game.lib.util.LogUtils;




public class PermissionDialog extends Dialog {
    private Rationale mRationale;
    private Activity activity;
    private String msg;
    private Context context;

    public PermissionDialog(@NonNull Context context,Rationale mRationale,String message) {
        super(context, android.R.style.Theme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.activity = (Activity) context;
        this.context = context;
        this.mRationale = mRationale;
        this.msg = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_dialog);
        assignViews();
        initView();
    }

    RelativeLayout messageDialogTitleBar;
    TextView tvTitle;
    TextView message;
    Button messageDialogRightBtn;

    private void assignViews() {
        messageDialogTitleBar = (RelativeLayout) findViewById(R.id.messageDialog_titleBar);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        message = (TextView) findViewById(R.id.message);
        messageDialogRightBtn = (Button) findViewById(R.id.messageDialog_rightBtn);
    }

    private void initView() {
        messageDialogRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRationale.resume();
                dismiss();
            }
        });

        String str = "游戏中心需要获取" + msg + "，用于为您推荐最适合的游戏，保障下载更新顺畅与设备安全。";
        if (msg.contains("和")) {
            LogUtils.i("Erosion","SpannableSpannableSpannable");
            Spannable spannable = new SpannableString(str);
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FF8800")),8,12,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FF8800")),13,17,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            message.setText(spannable);
        } else {
            Spannable spannable = new SpannableString(str);
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FF8800")),8,8 + msg.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            message.setText(spannable);
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
