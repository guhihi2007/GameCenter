package cn.lt.game.lib.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.Window;

import java.io.File;
import java.io.FileOutputStream;

import cn.lt.game.R;

public class SavePohtoDialog extends Dialog implements
		android.view.View.OnClickListener {

	public static final int SAVE_SUCCESS = 2;

	public static final int SAVE_FAILED = 1;

	private Bitmap mBitmap;

	private String mFileName;

	@SuppressWarnings("unused")
	private Context mContext;

	private Handler mHandler;

	public SavePohtoDialog(Context context, Handler handler) {
		super(context, android.R.style.Theme);
		this.mContext = context;
		this.mHandler = handler;
	}

	private SavePohtoDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_save_dialog);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		setCanceledOnTouchOutside(false);
		setListener();
	}

	private void setListener() {
		this.findViewById(R.id.tv_save_to_local).setOnClickListener(this);
		this.findViewById(R.id.tv_cancel).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tv_save_to_local:
			new Thread(new Runnable() {

				@Override
				public void run() {
					saveBitmap(mBitmap, mFileName);
				}
			}).start();
		case R.id.tv_cancel:
			cancleDialog();
		}
	}

	public void showDialog(Bitmap mBitmap, String mSavePath) {
		this.mBitmap = mBitmap;
		this.mFileName = mSavePath;
		show();
	}

	private void cancleDialog() {

		this.cancel();
	}

	/** 保存方法 */
	public void saveBitmap(Bitmap map, String fileName) {
		if (map == null || fileName == null) {
			return;
		}
		String savePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator
				+ "GameCenter"
				+ File.separator + "cache" + File.separator + "piture";
		File dir = new File(savePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File saveFile = new File(savePath, fileName);
		if (saveFile.exists()) {
			saveFile.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(saveFile);
			map.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
			mHandler.sendMessage(mHandler.obtainMessage(SAVE_SUCCESS, saveFile));
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendMessage(mHandler.obtainMessage(SAVE_FAILED, null));
		}
	}
}