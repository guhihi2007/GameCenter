package cn.lt.game.lib.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.lib.util.SharedPreferencesUtil;

/**
 * Created by wenchao on 2015/7/21. 退出提醒弹框，提醒下载任务
 */
public class ExitWarnDialog extends Dialog

{

	private TextView messageTv;
	private CheckBox downloadCb;
	private int downloadTaskCount;
	private View.OnClickListener enterClickListener;

	public ExitWarnDialog(Context context, int downloadTaskCount, View.OnClickListener enterClickListener) {
		// super(context, R.style.updateInfoDialogStyle);
		super(context, android.R.style.Theme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		this.downloadTaskCount = downloadTaskCount;
		this.enterClickListener = enterClickListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_exit_warm);

		messageTv = (TextView) findViewById(R.id.messageDialog_message);

		downloadCb = (CheckBox) findViewById(R.id.exit_warm_cb);
		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext());
		boolean isCheck = sharedPreferencesUtil.getBoolean(SharedPreferencesUtil.EXIT_DIALOG_REMENBER_DOWNLOAD, true);
		downloadCb.setChecked(isCheck);

		messageTv.setText(String.format(getContext().getResources().getString(R.string.download_exit_tips), downloadTaskCount));

		findViewById(R.id.messageDialog_leftBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 取消
				dismiss();
			}
		});

		findViewById(R.id.messageDialog_rightBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 确定
				dismiss();
				boolean isCheck = downloadCb.isChecked();
				saveCheckedInSP(isCheck);

				if (!isCheck) {
					FileDownloaders.stopAllDownload();
				}
				if (enterClickListener != null) {
					enterClickListener.onClick(v);
				}
			}
		});

		// XX
		findViewById(R.id.messageDialog_cancelIv).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		downloadCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				saveCheckedInSP(isChecked);
			}
		});

	}

	private void saveCheckedInSP(boolean isCheck) {
		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext());
		sharedPreferencesUtil.add(SharedPreferencesUtil.EXIT_DIALOG_REMENBER_DOWNLOAD, isCheck);
	}
}
