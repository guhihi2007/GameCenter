package cn.lt.game.ui.app.awardgame.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lt.game.R;

/**
 * @author chengyong
 * @time 2017/6/8 11:08
 * @des ${抽奖：一个按钮的dialog}
 */

public class NormalDialog extends Dialog implements android.view.View.OnClickListener{

    private String  title, rightBtnText, message;
    private ImageView cancelIv;
    private TextView  messageTv, tv_title;
    private Button rightBtn;
    private NormalDialog.RightBtnClickListener rightListener;
    private NormalDialog.CancelCliclListener cancelListener;

    public NormalDialog(Context context,String title, String message,
                         String rightBtnText) {
        super(context, R.style.awardDialogStyle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.title = title;
        this.rightBtnText = rightBtnText;
        this.message = message;
    }

    public NormalDialog(Context context, int theme) {
        super(context, theme);
    }

    protected NormalDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_award_normal);
        initView();
    }

    public void initView(){
        messageTv = (TextView) findViewById(R.id.messageDialog_message);
        tv_title = (TextView) findViewById(R.id.tv_title);
        rightBtn = (Button) findViewById(R.id.messageDialog_rightBtn);
        cancelIv = (ImageView) findViewById(R.id.messageDialog_cancelIv);
        cancelIv.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        if (title != null) {
            tv_title.setText(title);
        }
        if (message != null) {
            messageTv.setText(message);
        }

        if (rightBtnText != null) {
            rightBtn.setText(rightBtnText);
        }
    }

    public NormalDialog setRightOnClickListener(
            NormalDialog.RightBtnClickListener rightListener) {
        this.rightListener = rightListener;
        return this;
    }

    public void setCancelOnClickListener(NormalDialog.CancelCliclListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public NormalDialog setTitle(String title) {
        this.title = title;
        if (tv_title != null) {
            tv_title.setText(title);
        }
        return this;
    }

    public NormalDialog setMessage(String message) {
        this.message = message;
        if (messageTv != null) {
            messageTv.setText(message);
        }
        return this;
    }

    public interface RightBtnClickListener {
         void OnClick(View view);
    }

    public interface CancelCliclListener {
         void onClicl(View view);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.messageDialog_cancelIv) {
            this.dismiss();
            if (null != cancelListener) {
                cancelListener.onClicl(v);
            }
            return;
        }

        if (v.getId() == R.id.messageDialog_rightBtn) {
            if (rightListener != null) {
                rightListener.OnClick(v);
                dismiss();
                return;
            }
        }

    }
}
