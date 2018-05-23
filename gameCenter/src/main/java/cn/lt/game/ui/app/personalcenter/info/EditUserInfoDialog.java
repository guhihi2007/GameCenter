package cn.lt.game.ui.app.personalcenter.info;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lt.game.R;

/**
 * @author zhaoqile
 * @Description 两个按钮的消息框
 * @date 2014-09-01
 */
public class EditUserInfoDialog extends Dialog implements
        android.view.View.OnClickListener {

    private String  title, leftBtnText, rightBtnText;
    private ImageView cancelIv;
    private TextView  tv_title;
    private FrameLayout layoutBody;
    private Button leftBtn, rightBtn;
    private LeftBtnClickListener  leftListener;
    private RightBtnClickListener rightListener;
    private CancelCliclListener   cancelListener;

    /**
     * 圆角
     *
     * @param context
     * @param leftBtnText  返回按钮文字
     * @param rightBtnText 确定按钮文字
     * @return
     */
    public EditUserInfoDialog(Context context, String title,
                         String leftBtnText, String rightBtnText) {
        super(context, R.style.updateInfoDialogStyle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        this.title = title;
        this.leftBtnText = leftBtnText;
        this.rightBtnText = rightBtnText;
    }

    public void setLeftOnClickListener(LeftBtnClickListener leftListener) {
        this.leftListener = leftListener;
    }

    public EditUserInfoDialog setRightOnClickListener(
            RightBtnClickListener rightListener) {
        this.rightListener = rightListener;
        return this;
    }

    public void setCancelOnClickListener(CancelCliclListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public EditUserInfoDialog setTitle(String title) {
        this.title = title;
        if (tv_title != null) {
            tv_title.setText(title);
        }
        return this;
    }

    public EditUserInfoDialog setMessageLayout(int id,MessageLayoutCallback messageLayoutCallback) {
        layoutBody.removeAllViews();
        View view = LayoutInflater.from(getContext()).inflate(id,layoutBody,true);
        messageLayoutCallback.messageLayout(view);
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_user_center_message);
        init_findViewById();
        initView();
    }

    private void init_findViewById() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        leftBtn = (Button) findViewById(R.id.editUserInfoDialog_leftBtn);
        rightBtn = (Button) findViewById(R.id.editUserInfoDialog_rightBtn);
        cancelIv = (ImageView) findViewById(R.id.editUserInfoDialog_cancelIv);
        layoutBody = (FrameLayout) findViewById(R.id.editUserInfoDialog_Body);
    }

    public void goneLeft() {
        findViewById(R.id.editUserInfoDialog_leftBtn)
                .setVisibility(View.GONE);
    }

    private void initView() {
        // TODO Auto-generated method stub
        cancelIv.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        if (title != null) {
            tv_title.setText(title);
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

    public interface MessageLayoutCallback {
        void  messageLayout(View view);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.editUserInfoDialog_cancelIv) {
            this.dismiss();
            if (null != cancelListener) {
                cancelListener.onClicl(v);
            }
            return;
        }

        if (v.getId() == R.id.editUserInfoDialog_rightBtn) {
            if (rightListener != null)
                rightListener.OnClick(v);
            dismiss();
            return;
        }

        if (v.getId() == R.id.editUserInfoDialog_leftBtn) {
            if (leftListener != null)
                leftListener.OnClick(v);
            dismiss();
            return;
        }

    }

}
