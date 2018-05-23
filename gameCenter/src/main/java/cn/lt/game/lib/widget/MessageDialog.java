package cn.lt.game.lib.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lt.game.R;

/**
 * @author zhaoqile
 * @Description 两个按钮的消息框
 * @date 2014-09-01
 */
public class MessageDialog extends Dialog implements
        android.view.View.OnClickListener {

    private String  title, leftBtnText, rightBtnText, message;
    private ImageView cancelIv;
    private TextView  messageTv, tv_title;
    private Button leftBtn, rightBtn;
    private LeftBtnClickListener  leftListener;
    private RightBtnClickListener rightListener;
    private CancelCliclListener   cancelListener;
    private Context context;
    private Spannable spannable;

    /**
     * 圆角
     *
     * @param context
     * @param message      内容
     * @param leftBtnText  返回按钮文字
     * @param rightBtnText 确定按钮文字
     * @return
     */
    public MessageDialog(Context context, String title, String message,
                         String leftBtnText, String rightBtnText) {
        super(context, R.style.updateInfoDialogStyle);
//    	super(context, android.R.style.Theme);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.title = title;
        this.leftBtnText = leftBtnText;
        this.rightBtnText = rightBtnText;
        this.message = message;
        this.context = context;
    }

    /**
     * 一个按钮对话框样式
     *
     * @param context
     * @param title
     * @param message
     * @param rightBtnText
     */
    public MessageDialog(Context context, String title, String message,
                         String rightBtnText,Spannable spannable) {
        this(context, title, message, null, rightBtnText);
        this.spannable = spannable;
    }

//    private MessageDialog(Context context, int theme) {
//    	super(context, android.R.style.Theme);
//    	requestWindowFeature(Window.FEATURE_NO_TITLE);
//		this.getWindow().setBackgroundDrawableResource(
//				android.R.color.transparent);
//    }

    public void setLeftOnClickListener(LeftBtnClickListener leftListener) {
        this.leftListener = leftListener;
    }

    public MessageDialog setRightOnClickListener(
            RightBtnClickListener rightListener) {
        this.rightListener = rightListener;
        return this;
    }

    public void setCancelOnClickListener(CancelCliclListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public MessageDialog setTitle(String title) {
        this.title = title;
        if (tv_title != null) {
            tv_title.setText(title);
        }
        return this;
    }

    public MessageDialog setMessage(String message) {
        this.message = message;
        if (messageTv != null) {
            messageTv.setText(message);
        }
        return this;
    }

    public MessageDialog setLeftBtnText(String leftBtnText) {
        this.leftBtnText = leftBtnText;
        if (leftBtn != null && leftBtnText != null) {
            leftBtn.setText(leftBtnText);
        } else if (leftBtn != null && leftBtnText == null) {
            goneLeft();
        }
        return this;
    }

    public MessageDialog setRightBtnText(String rightBtnText) {
        this.rightBtnText = rightBtnText;
        if (rightBtn != null) {
            rightBtn.setText(rightBtnText);
        }
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_message);
        init_findViewById();
        initView();
    }

    private void init_findViewById() {
        messageTv = (TextView) findViewById(R.id.messageDialog_message);
        tv_title = (TextView) findViewById(R.id.tv_title);
        leftBtn = (Button) findViewById(R.id.messageDialog_leftBtn);
        rightBtn = (Button) findViewById(R.id.messageDialog_rightBtn);
        cancelIv = (ImageView) findViewById(R.id.messageDialog_cancelIv);

    }

    public void goneClose() {
        cancelIv.setVisibility(View.GONE);
    }

    public void goneRight() {
        rightBtn.setVisibility(View.GONE);
    }

    public void goneLeft() {
        findViewById(R.id.messageDialog_leftBtn).setVisibility(View.GONE);
    }

    private void initView() {
        // TODO Auto-generated method stub
        cancelIv.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        if (title != null) {
            tv_title.setText(title);
        }
        if (message != null) {
            messageTv.setText(message);
        }

        if(spannable != null) {
            messageTv.setText(spannable);
        }

        if (leftBtnText != null) {
            leftBtn.setText(leftBtnText);
        } else {
            goneLeft();
        }

        if (rightBtnText != null) {
            rightBtn.setText(rightBtnText);
        }
    }

    public interface LeftBtnClickListener {
        void OnClick(View view);
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
            if (rightListener != null)
                rightListener.OnClick(v);
            dismiss();
            return;
        }

        if (v.getId() == R.id.messageDialog_leftBtn) {
            if (leftListener != null)
                leftListener.OnClick(v);
            dismiss();
            return;
        }

    }

}
