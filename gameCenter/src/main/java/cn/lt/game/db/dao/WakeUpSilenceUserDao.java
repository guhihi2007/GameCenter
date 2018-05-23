package cn.lt.game.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.lt.game.global.LogTAG;
import cn.lt.game.db.DBHelper;
import cn.lt.game.db.factory.IDBFactory;
import cn.lt.game.db.factory.WakeUpSilenceUserFactory;
import cn.lt.game.db.operation.WakeUpSilenceUserDbOperator;
import cn.lt.game.lib.util.LogUtils;

/**
 * Created by LinJunSheng on 2016/11/24.
 */

public class WakeUpSilenceUserDao {
    private static WakeUpSilenceUserDao wakeUpSilenceUserDao;
    private SQLiteDatabase mDatabase;

    public WakeUpSilenceUserDao(Context context) {
        IDBFactory factory = new WakeUpSilenceUserFactory();
        DBHelper helper = factory.getDB(context);
        mDatabase = helper.getWritableDatabase();
    }

    private void insert(String fields, long time) {
        ContentValues cv = new ContentValues();
        cv.put(fields, time);
        mDatabase.insert(WakeUpSilenceUserDbOperator.TABLE_NAME, null, cv);
    }

    private long queryFirstStartTime() {

        Cursor cursor = null;
        long firstStartTime = 0;
        try {
            cursor = mDatabase.query(WakeUpSilenceUserDbOperator.TABLE_NAME,
                    new String[] { WakeUpSilenceUserDbOperator.FIRST_START_TIME }, null,
                    null, null, null, null);
            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                firstStartTime = cursor.getLong(cursor.getColumnIndex(WakeUpSilenceUserDbOperator.FIRST_START_TIME));
                LogUtils.i(LogTAG.wakeUpUser, "从数据库取出的首次启动时间是 = " + firstStartTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return firstStartTime;
    }

    private long queryFirstDownloadTime() {
        Cursor cursor = null;
        long firstDownloadTime = 0;
        try {
            cursor = mDatabase.query(WakeUpSilenceUserDbOperator.TABLE_NAME,
                    new String[] { WakeUpSilenceUserDbOperator.FIRST_DOWNLOAD_TIME }, null,
                    null, null, null, null);
            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                firstDownloadTime = cursor.getLong(cursor.getColumnIndex(WakeUpSilenceUserDbOperator.FIRST_DOWNLOAD_TIME));
                LogUtils.i(LogTAG.wakeUpUser, "从数据库取出的首次下载游戏是 = " + firstDownloadTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return firstDownloadTime;
    }

    public void close() {
        mDatabase.close();
    }
}
