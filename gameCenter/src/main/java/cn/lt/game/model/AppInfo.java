package cn.lt.game.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import cn.lt.game.R;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.image.BitmapUtil;

public class AppInfo {

	private String name = null;
	private String version = null;
	private int versionCode = -1;
	private Drawable icon = null;
	private boolean isInstalled = false;
	private String packageName = null;
	private Boolean isShowToggle = false;

	public AppInfo(Context context, String packageName) {
		this.packageName = packageName;
		PackageManager pm = context.getPackageManager();
		try {

			PackageInfo packageInfo = pm.getPackageInfo(packageName,0);
			isInstalled = true;
			version = packageInfo.versionName;
			versionCode = packageInfo.versionCode;
			name = pm.getApplicationLabel(packageInfo.applicationInfo).toString();
			icon = pm.getApplicationIcon(packageInfo.applicationInfo);

		}  catch (Exception e){
			//有可能产生package manager has died。说明：http://dyang.sinaapp.com/?p=190
//			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public Drawable getIcon() {
		return icon;
	}

	public boolean isInstalled() {
		return isInstalled;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Boolean getIsShowToggle() {
		return isShowToggle;
	}

	public void setIsShowToggle(Boolean isShowToggle) {
		this.isShowToggle = isShowToggle;
	}

}