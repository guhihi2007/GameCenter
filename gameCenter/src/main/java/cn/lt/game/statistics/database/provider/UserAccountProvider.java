package cn.lt.game.statistics.database.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import cn.lt.game.db.DBHelper;
import cn.lt.game.db.factory.IDBFactory;
import cn.lt.game.db.factory.UserAccountFactory;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;

/**
 * @author chengyong
 * @time 2017/9/21 11:08
 * @des ${TODO}
 */
public class UserAccountProvider extends ContentProvider {

	static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static{
		matcher.addURI("cn.lt.game.UserAccountProvider", "account", 7);
	}

	private SQLiteDatabase mDatabase;
	@Override
	public boolean onCreate() {
		initDataBase();
		return true;
	}

	private synchronized void initDataBase() {
		IDBFactory factory = new UserAccountFactory();
		DBHelper helper = factory.getDB(getContext());
		mDatabase = helper.getWritableDatabase();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if(matcher.match(uri) == 7){
			LogUtils.d(LogTAG.USER, "gamecenter:provider,DELETE---应用市场通知游戏中心退出");

			mDatabase.delete("account", selection, selectionArgs);

//			mDatabase.close();
			
			getContext().getContentResolver().notifyChange(uri, null);
		}else{
			throw new  IllegalArgumentException("URI不匹配");
		}
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		if(matcher.match(uri) == 7){
			LogUtils.d(LogTAG.USER, "gamecenter:provider,insert---应用市场通知游戏中心 登陆");

			mDatabase.insert("account", null, values);

//			mDatabase.close();

			getContext().getContentResolver().notifyChange(uri, null);
			
			
		}else{
			throw new  IllegalArgumentException("URI不匹配");
		}
		return null;
	}



	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cursor = null;
		if(matcher.match(uri) == 7){
			LogUtils.d(LogTAG.USER, "gamecenter:provider,query---");
			cursor = mDatabase.query("account", projection, selection, selectionArgs, null, null, sortOrder);
			LogUtils.d(LogTAG.USER, "gamecenter:provider,query成功："+cursor);
			/*cursor.close();
			db.close();*/
		}else{
			throw new  IllegalArgumentException("URI不匹配");
		}
		return cursor ;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		if(matcher.match(uri) == 7){

			LogUtils.d(LogTAG.USER, "gamecenter:provider,update---");

			mDatabase.update("account", values, selection, selectionArgs);

			mDatabase.close();
			
			getContext().getContentResolver().notifyChange(uri, null);
		}else{
			throw new  IllegalArgumentException("URI不匹配");
		}
		return 0;
	}

}
