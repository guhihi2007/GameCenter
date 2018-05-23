package cn.lt.game.ui.app.awardgame.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;

/**
 * @author chengyong
 * @time 2017/6/8 11:08
 * @des ${抽奖：领取实物奖品后的确认dialog}
 */

public class PhysicalCheckDialog extends Dialog implements View.OnClickListener{

    private  boolean is4Gdialog;
    private String  mAddressText, mUserNameText, mPhoneNumText;
    private ImageView cancelIv;
    private Button rightBtn;
    private TextView mAddress;
    private TextView mUserName;
    private TextView mPhoneNum;
    private LinearLayout addressContainer;

    public PhysicalCheckDialog(Context context, String mAddressText, String mUserNameText,String mPhoneNumText,boolean is4Gdialog) {
        super(context, R.style.awardDialogStyle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.mAddressText = mAddressText;
        this.mUserNameText = mUserNameText;
        this.mPhoneNumText = mPhoneNumText;
        this.is4Gdialog = is4Gdialog;
    }

    public PhysicalCheckDialog(Context context) {
        super(context, R.style.updateInfoDialogStyle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public PhysicalCheckDialog(Context context, int theme) {
        super(context, theme);
    }

    protected PhysicalCheckDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_award_physical_check);
        initView();
    }

    public void initView(){
        cancelIv = (ImageView) findViewById(R.id.messageDialog_cancelIv);
        mAddress = (TextView) findViewById(R.id.award_edit_address);
        mUserName = (TextView) findViewById(R.id.award_edit_name);
        mPhoneNum = (TextView) findViewById(R.id.award_edit_num);
        addressContainer = (LinearLayout) findViewById(R.id.messageDialog_textBody_address);
        TextView mYellowMessage = (TextView) findViewById(R.id.messageDialog_yellow);
        cancelIv.setOnClickListener(this);
        if(is4Gdialog){
            addressContainer.setVisibility(View.GONE);
            mYellowMessage.setText(MyApplication.application.getResources().getString(R.string.award_message_contact_4G));
        }else{
            mYellowMessage.setText(MyApplication.application.getResources().getString(R.string.award_message_contact));
            addressContainer.setVisibility(View.VISIBLE);
        }
        if (mAddress != null) {
            mAddress.setText(mAddressText);
        }
        if (mUserName != null) {
            mUserName.setText(mUserNameText);
        }

        if (mPhoneNum != null) {
            mPhoneNum.setText(mPhoneNumText);
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.messageDialog_cancelIv) {
            this.dismiss();
            return;
        }

    }
}
