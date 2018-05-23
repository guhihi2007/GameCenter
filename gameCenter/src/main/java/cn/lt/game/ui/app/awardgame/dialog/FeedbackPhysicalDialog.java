package cn.lt.game.ui.app.awardgame.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.ClipBoardManagerUtil;

/**
 * @author chengyong
 * @time 2017/6/8 11:08
 * @des ${抽奖：后台发货的反馈的dialog（无按钮）}
 */

public class FeedbackPhysicalDialog extends Dialog implements View.OnClickListener{

    private FeedbackPhysicalDialog.CancelCliclListener cancelListener;
    private ImageView cancelIv;
    private TextView musername;
    private TextView mNum;
    private TextView mAddress;
    private TextView mCompany;
    private TextView mExpressNum;
    private TextView mCopy;

    private String musernameText;
    private String mNumText;
    private String mAddressText;
    private String mCompanyText;
    private String mExpressNumText;

    public FeedbackPhysicalDialog setMusernameText(String musernameText) {
        this.musernameText = musernameText;
        return this;
    }

    public FeedbackPhysicalDialog setmNumText(String mNumText) {
        this.mNumText = mNumText;
        return this;
    }

    public FeedbackPhysicalDialog setmAddressText(String mAddressText) {
        this.mAddressText = mAddressText;
        return this;
    }

    public FeedbackPhysicalDialog setmCompanyText(String mCompanyText) {
        this.mCompanyText = mCompanyText;
        return this;
    }

    public FeedbackPhysicalDialog setmExpressNumText(String mExpressNumText) {
        this.mExpressNumText = mExpressNumText;
        return this;
    }

    public FeedbackPhysicalDialog(Context context){
        super(context, R.style.awardDialogStyle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public FeedbackPhysicalDialog(Context context, int theme) {
        super(context, theme);
    }

    protected FeedbackPhysicalDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_award_feedback_physical);
        initView();

    }

    public void initView(){
        cancelIv = (ImageView) findViewById(R.id.messageDialog_cancelIv);

        musername = (TextView) findViewById(R.id.messageDialog_username);
        mNum = (TextView) findViewById(R.id.messageDialog_num);
        mAddress = (TextView) findViewById(R.id.messageDialog_message_address_real);
        mCompany = (TextView) findViewById(R.id.messageDialog_express_company);
        mExpressNum = (TextView) findViewById(R.id.messageDialog_express_num);
        mCopy = (TextView) findViewById(R.id.messageDialog_copy);
        cancelIv.setOnClickListener(this);
        mCopy.setOnClickListener(this);
        if (mExpressNum != null) {
            mExpressNum.setText(mExpressNumText);
        }
        if (mCompany != null) {
            mCompany.setText(mCompanyText);
        }
        if (mAddress != null) {
            mAddress.setText(mAddressText);
        }
        if (mNum != null) {
            mNum.setText(mNumText);
        }
        if (musername != null) {
            musername.setText(musernameText);
        }
    }

    public FeedbackPhysicalDialog setCancelOnClickListener(FeedbackPhysicalDialog.CancelCliclListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
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
            ClipBoardManagerUtil.self().save2ClipBoardCopy(mExpressNumText);
        }

    }
}
