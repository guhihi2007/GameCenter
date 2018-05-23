package cn.lt.game.lib.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;

public class UserLoginDialog extends Dialog implements OnClickListener {

	private TextView valueView;
	private ImageView closeView;
	private Button cancelBt, confirmBt;
	private Context context;
	private String text;
	private boolean loginEndIsFinsh;

	public UserLoginDialog(Context context, String text, boolean loginEndIsFinsh) {
		super(context, R.style.updateInfoDialogStyle);
		if (TextUtils.isEmpty(text)) {
			this.text = context.getResources().getString(R.string.unlogin_warn);
		} else {
			this.text = text;
		}
		this.context = context;
		this.loginEndIsFinsh = loginEndIsFinsh;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userlogindialoglayout);
		initView();
		setView();
	}

	private void initView() {
		valueView = (TextView) findViewById(R.id.toastDialog_valueText);
		closeView = (ImageView) findViewById(R.id.toastDialog_close);
		cancelBt = (Button) findViewById(R.id.toastDialog_cancel);
		confirmBt = (Button) findViewById(R.id.toastDialog_confirm);
		cancelBt.setOnClickListener(this);
		confirmBt.setOnClickListener(this);
		closeView.setOnClickListener(this);
	}

	private void setView() {
		valueView.setText(text);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.toastDialog_confirm:
			UserInfoManager.instance().starLogin(context, loginEndIsFinsh);
			this.cancel();
			break;
		case R.id.toastDialog_close:
		case R.id.toastDialog_cancel:
			this.cancel();
			break;
		default:
			break;
		}
	}
}
