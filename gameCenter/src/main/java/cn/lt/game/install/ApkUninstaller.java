package cn.lt.game.install;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ApkUninstaller {
	public static void uninstall(Context context, String packageName) {
		Uri packageUri = Uri.parse("package:"+packageName);
		Intent intent = new Intent(Intent.ACTION_DELETE, packageUri);
		context.startActivity(intent);
	}
}
