package cn.lt.game.ui.app.awardgame.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.lib.util.ToastUtils;


/**
 * @author chengyong
 * @time 2017/6/8 11:08
 * @des ${抽奖：领取实物奖品填用户信息的dialog}
 */

public class PhysicalEditDialog extends Dialog implements View.OnClickListener{

    private  boolean isPhysical;
    private String  title, rightBtnText, message;
    private ImageView cancelIv;
    private TextView  messageTv, tv_title;
    private Button rightBtn;
    private PhysicalEditDialog.RightBtnClickListener rightListener;
    private PhysicalEditDialog.CancelCliclListener cancelListener;
    private EditText mAddress;
    private EditText mUserName;
    private EditText mPhoneNum;
    private SharedPreferencesUtil mSp;
    private LinearLayout mAddressContainer;
    private Context context;

    public PhysicalEditDialog(Context context,boolean isPhysical) {
        super(context, R.style.awardDialogStyle);
        this.isPhysical=isPhysical;
        this.context=context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(mSp ==null){
            mSp = new SharedPreferencesUtil(context);
        }
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public PhysicalEditDialog(Context context, int theme) {
        super(context, theme);
    }

    protected PhysicalEditDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_award_physical_edit);
        initView();
    }

    /**
     *
     * @return
     */
    public String getEditName(){

        return mUserName.getText().toString().trim();
    }
    /**
     *
     * @return
     */
    public String getEditAddress(){
        return mAddress.getText().toString().trim();
    }
    /**
     *
     * @return
     */
    public String getEditPhone(){
        return mPhoneNum.getText().toString().trim();
    }


    public void initView(){
        messageTv = (TextView) findViewById(R.id.messageDialog_message);
        tv_title = (TextView) findViewById(R.id.tv_title);
        rightBtn = (Button) findViewById(R.id.messageDialog_rightBtn);
        cancelIv = (ImageView) findViewById(R.id.messageDialog_cancelIv);
        mAddress = (EditText) findViewById(R.id.award_edit_address);
        mUserName = (EditText) findViewById(R.id.award_edit_name);
        mPhoneNum = (EditText) findViewById(R.id.award_edit_num);
        View line =  findViewById(R.id.award_line_address);
        mAddressContainer = (LinearLayout) findViewById(R.id.messageDialog_textBody_address);
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
        if(!isPhysical){
            mAddressContainer.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }else{
            mAddressContainer.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        }
        try {
            mUserName.setText(mSp.get(SharedPreferencesUtil.AWARD_EDIT_NAME));
            mPhoneNum.setText(mSp.get(SharedPreferencesUtil.AWARD_EDIT_PHONE));
            mAddress.setText(mSp.get(SharedPreferencesUtil.AWARD_EDIT_ADDRESS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PhysicalEditDialog setRightOnClickListener(
            PhysicalEditDialog.RightBtnClickListener rightListener) {
        this.rightListener = rightListener;
        return this;
    }

    public void setCancelOnClickListener(PhysicalEditDialog.CancelCliclListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public PhysicalEditDialog setTitle(String title) {
        this.title = title;
        if (tv_title != null) {
            tv_title.setText(title);
        }
        return this;
    }

    public PhysicalEditDialog setMessage(String message) {
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
                if(TextUtils.isEmpty(getEditName())){
                    ToastUtils.showToast(context, "姓名不能为空");
                    return;
                }
                if(TextUtils.isEmpty(getEditPhone())){
                    ToastUtils.showToast(context, "手机号不能为空");
                    return;
                }
                if(isPhysical&& TextUtils.isEmpty(getEditAddress())){
                    ToastUtils.showToast(context, "地址不能为空");
                    return;
                }
                rightListener.OnClick(v);
                return;
            }
        }

    }
}
