package cn.lt.game.ui.app.awardgame.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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
 * @des ${抽奖：获取劵的dialog（无按钮）}
 */

public class ChangeCodeDialog extends Dialog implements View.OnClickListener{

    private  String exChangeCode;
    private String  title, message;
    private ImageView cancelIv;
    private TextView  messageTv, tv_title;
    private ChangeCodeDialog.RightBtnClickListener rightListener;
    private ChangeCodeDialog.CancelCliclListener cancelListener;
    private TextView mExchangeCode;
    private TextView mCopy;
    private RelativeLayout rl_changeCode;
    public ChangeCodeDialog(Context context, String exChangeCode, String message) {

        super(context, R.style.awardDialogStyle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.message = message;
        this.exChangeCode = exChangeCode;
    }

    public ChangeCodeDialog(Context context, int theme) {
        super(context, theme);
    }

    protected ChangeCodeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_award_chang_code);
        initView();
    }

    public void initView(){
        messageTv = (TextView) findViewById(R.id.messageDialog_message);//说明
        tv_title = (TextView) findViewById(R.id.tv_title);
        cancelIv = (ImageView) findViewById(R.id.messageDialog_cancelIv);
        //兑换码
        rl_changeCode=(RelativeLayout)findViewById(R.id.messageDialog_exchange_number_container) ;
        mExchangeCode = (TextView) findViewById(R.id.messageDialog_exchange_number);
        mCopy = (TextView) findViewById(R.id.messageDialog_copy);
        cancelIv.setOnClickListener(this);
        mCopy.setOnClickListener(this);
        if (title != null) {
            tv_title.setText(title);
        }
        if (message != null) {
            messageTv.setText(message);
        }

        mExchangeCode.setText(MyApplication.application.getResources().getString(R.string.award_message_change_code_half, exChangeCode));

        if (exChangeCode.equals(AWARDCOUPON)) {
           rl_changeCode.setVisibility(View.GONE);
        }
    }

    public ChangeCodeDialog setRightOnClickListener(
            ChangeCodeDialog.RightBtnClickListener rightListener) {
        this.rightListener = rightListener;
        return this;
    }

    public void setCancelOnClickListener(ChangeCodeDialog.CancelCliclListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public ChangeCodeDialog setTitle(String title) {
        this.title = title;
        if (tv_title != null) {
            tv_title.setText(title);
        }
        return this;
    }

    public ChangeCodeDialog setMessage(String message) {
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
        if (v.getId() == R.id.messageDialog_copy) {
            ClipBoardManagerUtil.self().save2ClipBoardCopy(exChangeCode);
        }
    }
}
