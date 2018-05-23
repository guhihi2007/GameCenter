package cn.lt.game.lib.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;

/**
 * Created by Administrator on 2017/8/14.
 */

public class DefaultSettingDialog extends Dialog {
    private Activity activity;
    private int requestCode;
    public DefaultSettingDialog(Activity activity,int requestCode) {
        super(activity, android.R.style.Theme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.activity = activity;
        this.requestCode = requestCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_setting_dialog);
        assignViews();
        initView();
    }

    RelativeLayout messageDialogTitleBar;
    ImageView messageDialogCancelIv;
    TextView tvTitle;
    TextView message;
    Button messageDialogRightBtn;

    private void assignViews() {
        messageDialogTitleBar = (RelativeLayout) findViewById(R.id.messageDialog_titleBar);
        messageDialogCancelIv = (ImageView) findViewById(R.id.messageDialog_cancelIv);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        message = (TextView) findViewById(R.id.message);
        messageDialogRightBtn = (Button) findViewById(R.id.messageDialog_rightBtn);
        messageDialogCancelIv.setVisibility(View.GONE);
    }

    private void initView() {
        messageDialogCancelIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                activity.finish();
            }
        });

        messageDialogRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivityForResult(intent, requestCode);
                dismiss();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
