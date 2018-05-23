package cn.lt.game.lib.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class ClipBoardManagerUtil {

	private Context mContext;

	private ClipboardManager mClip;

	@SuppressWarnings({ "unused", "deprecation" })
	private android.text.ClipboardManager mClipLowVersion;

	private ClipBoardManagerUtil() {

	}

	private static class ClipBoardManagerHoler {
		static final ClipBoardManagerUtil sInstance = new ClipBoardManagerUtil();
	}

	public static ClipBoardManagerUtil self() {
		return ClipBoardManagerHoler.sInstance;
	}

	public void init(Context context) {
		this.mContext = context;
		// if(VERSION.SDK_INT < VERSION_CODES.HONEYCOMB) {
		// mClipLowVersion = (android.text.ClipboardManager)
		// context.getSystemService(Context.CLIPBOARD_SERVICE);
		// } else {
		mClip = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		// }

	}

	public void save2ClipBoard(String text) {

		// if(VERSION.SDK_INT < VERSION_CODES.HONEYCOMB) {
		// ClipData data = ClipData.newPlainText("激活码", text);
		// mClipLowVersion.setText(text);
		// // mClipLowVersion.set
		//
		//
		// } else {
		ClipData data = ClipData.newPlainText("激活码", text);
		mClip.setPrimaryClip(data);
		ToastUtils.showToast(mContext, "复制成功");
		// }

	}

	public void save2ClipBoardCopy(String text) {
		ClipData data = ClipData.newPlainText("兑换码:", text);
		mClip.setPrimaryClip(data);
		ToastUtils.showToast(mContext, "复制成功");
	}

}
