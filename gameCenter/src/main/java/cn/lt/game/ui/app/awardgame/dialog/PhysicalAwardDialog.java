package cn.lt.game.ui.app.awardgame.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.lib.util.ClipBoardManagerUtil;

import static cn.lt.game.ui.app.awardgame.view.AwardsPlateView.AWARDCOUPON;

/**
 * @author chengyong
 * @time 2017/6/8 11:08
 * @des ${抽奖：获取实物奖品的dialog}
 */

public class PhysicalAwardDialog extends Dialog implements View.OnClickListener {

    private String realTitle;
    private boolean isTicket;
    private String title, rightBtnText, message;
    private ImageView cancelIv;
    private TextView messageTv, tv_title;
    private Button rightBtn;
    private PhysicalAwardDialog.RightBtnClickListener rightListener;
    private PhysicalAwardDialog.CancelCliclListener cancelListener;
    private RelativeLayout mTicketContainer;
    private TextView mCopy;
    private String mChangeCode;
    private TextView mrealTitle;
    private TextView messageDialog_exchange_number;

    public PhysicalAwardDialog(Context context, int theme) {
        super(context, theme);
    }

    protected PhysicalAwardDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public PhysicalAwardDialog(Context context, String title, String message, String button,
                               boolean isTicket, String mChangeCode, String realTitle) {
        super(context, R.style.awardDialogStyle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.title = title;
        this.rightBtnText = button;
        this.message = message;
        this.isTicket = isTicket;
        this.mChangeCode = mChangeCode;
        this.realTitle = realTitle;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_award_physical);
        initView();
    }

    public void initView() {
        messageTv = (TextView) findViewById(R.id.messageDialog_message);
        tv_title = (TextView) findViewById(R.id.tv_title);
        rightBtn = (Button) findViewById(R.id.messageDialog_rightBtn);
        cancelIv = (ImageView) findViewById(R.id.messageDialog_cancelIv);
        mTicketContainer = (RelativeLayout) findViewById(R.id.messageDialog_exchange_number_container);
        mCopy = (TextView) findViewById(R.id.messageDialog_copy);
        mrealTitle = (TextView) findViewById(R.id.messageDialog_yellow);
        messageDialog_exchange_number = (TextView) findViewById(R.id.messageDialog_exchange_number);
        mTicketContainer.setVisibility(isTicket ? View.VISIBLE : View.GONE);
        cancelIv.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        mCopy.setOnClickListener(this);
        if (title != null) {
            tv_title.setText(title);
        }
        if (message != null) {
            messageTv.setText(message);
        }

        if (rightBtnText != null) {
            rightBtn.setText(rightBtnText);
        }

        messageDialog_exchange_number.setText(MyApplication.application.getResources().getString(R.string.award_message_change_code_half, mChangeCode));

        if (mChangeCode.equals(AWARDCOUPON)) {
            mTicketContainer.setVisibility(View.GONE);
        }

        mrealTitle.setText(realTitle);
    }

    public PhysicalAwardDialog setRightOnClickListener(
            PhysicalAwardDialog.RightBtnClickListener rightListener) {
        this.rightListener = rightListener;
        return this;
    }

    public void setCancelOnClickListener(PhysicalAwardDialog.CancelCliclListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public PhysicalAwardDialog setTitle(String title) {
        this.title = title;
        if (tv_title != null) {
            tv_title.setText(title);
        }
        return this;
    }

    public PhysicalAwardDialog setMessage(String message) {
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
        if (v.getId() == R.id.messageDialog_copy) {
            ClipBoardManagerUtil.self().save2ClipBoardCopy(mChangeCode);
        }
    }
}
