package cn.lt.game.lib.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;

/**
 * Created by wenchao on 2016/3/31.
 */
public class GlobalDialog implements View.OnClickListener{

    private Context       mContext;
    private WindowManager mWindowManager;
    private View          mRootView;

    private ImageView      mMessageDialogCancelIv;
    private TextView       mTvTitle;
    private TextView       mMessageDialogMessage;
    private Button         mMessageDialogLeftBtn;
    private Button         mMessageDialogRightBtn;

    private String title;
    private String message;
    private String leftButtonText;
    private String rightButtonText;
    private View.OnClickListener leftButtonListener;
    private View.OnClickListener rightButtonListener;



    public GlobalDialog(String title, String message, String leftButtonText, String rightButtonText, View.OnClickListener leftButtonListener, View.OnClickListener rightButtonListener) {
        this.title = title;
        this.message = message;
        this.leftButtonText = leftButtonText;
        this.rightButtonText = rightButtonText;
        this.leftButtonListener = leftButtonListener;
        this.rightButtonListener = rightButtonListener;
        this.mContext = MyApplication.application;
        init();
    }

    private void init() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        layoutParams.flags = flags;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.CENTER;

        initRootView();
        mWindowManager.addView(mRootView, layoutParams);
    }


    private void initRootView() {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_message, null);
        mMessageDialogCancelIv = (ImageView) mRootView.findViewById(R.id.messageDialog_cancelIv);
        mTvTitle = (TextView) mRootView.findViewById(R.id.tv_title);
        mMessageDialogMessage = (TextView) mRootView.findViewById(R.id.messageDialog_message);
        mMessageDialogLeftBtn = (Button) mRootView.findViewById(R.id.messageDialog_leftBtn);
        mMessageDialogRightBtn = (Button) mRootView.findViewById(R.id.messageDialog_rightBtn);

        mTvTitle.setText(title);
        mMessageDialogMessage.setText(message);
        mMessageDialogLeftBtn.setText(leftButtonText);
        mMessageDialogRightBtn.setText(rightButtonText);

        mMessageDialogCancelIv.setOnClickListener(this);
        mMessageDialogLeftBtn.setOnClickListener(this);
        mMessageDialogRightBtn.setOnClickListener(this);

        mRootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == keyCode) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    public void dismiss() {
        mWindowManager.removeView(mRootView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.messageDialog_cancelIv:
                dismiss();
                break;
            case R.id.messageDialog_leftBtn:
                leftButtonListener.onClick(v);
                dismiss();
                break;
            case R.id.messageDialog_rightBtn:
                rightButtonListener.onClick(v);
                dismiss();
                break;
        }
    }
}
