package cn.lt.game.statistics.database.dao.supers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public abstract class AbstractPreferenceDao<T> {

	protected SharedPreferences mSharedPreference;

	@SuppressWarnings("unused")
	private Context mContext;

	public AbstractPreferenceDao(Context context) {
		this.mContext = context;
		this.mSharedPreference = assginSharedPreference(context);
	}

	public abstract SharedPreferences assginSharedPreference(Context context);

	public abstract void resetAll();

	public abstract T getData();

	public abstract void saveData(T data);

	protected void saveBoolean(boolean flag, String key) {
		Editor editor = mSharedPreference.edit();
		editor.putBoolean(key, flag);
		editor.apply();//异步、效率高
	}

	protected void saveFloat(float flag, String key) {
		Editor editor = mSharedPreference.edit();
		editor.putFloat(key, flag);
		editor.apply();
	}

	protected void saveInt(int flag, String key) {
		Editor editor = mSharedPreference.edit();
		editor.putInt(key, flag);
		editor.apply();
	}

	protected void saveString(String flag, String key) {
		Editor editor = mSharedPreference.edit();
		editor.putString(key, flag);
		editor.apply();
	}

	protected void saveLong(long flag, String key) {
		Editor editor = mSharedPreference.edit();
		editor.putLong(key, flag);
		editor.apply();
	}

	protected void getBoolean(boolean flag, String key) {
		Editor editor = mSharedPreference.edit();
		editor.putBoolean(key, flag);
		editor.apply();
	}

	protected float getFloat(String key) {
		return mSharedPreference.getFloat(key, -1);
	}

	protected int getInt(String key) {
		return mSharedPreference.getInt(key, -1);
	}

	protected String getString(String key) {
		return mSharedPreference.getString(key, null);
	}

	protected Long getLong(String key) {
		return mSharedPreference.getLong(key, -1);
	}

}
