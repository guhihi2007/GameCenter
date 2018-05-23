package cn.lt.game.application.wakeup;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import cn.lt.game.db.operation.WakeUpSilenceUserDbOperator;

/**
 * Created by LinJunSheng on 2016/11/24.
 */

public class WakeUpUserTimer {
    public static synchronized void  saveFirstStartTime(Context context, final long time) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("time_id", 1);
            cv.put(WakeUpSilenceUserDbOperator.FIRST_START_TIME, time);

            ContentResolver resolver = context.getContentResolver();
            Uri insertUri = Uri.parse("content://cn.lt.game.WakeUpUserProvider/firstStartTime");
            resolver.insert(insertUri, cv);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static synchronized void  saveFirstDownloadTime(Context context, final long time) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("time_id", 2);
            cv.put(WakeUpSilenceUserDbOperator.FIRST_DOWNLOAD_TIME, time);

            ContentResolver resolver = context.getContentResolver();
            Uri insertUri = Uri.parse("content://cn.lt.game.WakeUpUserProvider/firstDownloadTime");
            resolver.insert(insertUri, cv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getFirstStartTime(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://cn.lt.game.WakeUpUserProvider/firstStartTime");
        Cursor cursor = null;

        long firstStartTime = 0;
        try {
            cursor = resolver.query(uri, null, "time_id = ?", new String[]{"1"}, null);
            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                firstStartTime = cursor.getLong(cursor.getColumnIndex(WakeUpSilenceUserDbOperator.FIRST_START_TIME));
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

    public static long getFirstDownloadTime(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://cn.lt.game.WakeUpUserProvider/firstDownloadTime");
        Cursor cursor = null;

        long firstDownloadTime = 0;
        try {
            cursor = resolver.query(uri, null, "time_id = ?", new String[]{"2"}, null);

            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                firstDownloadTime = cursor.getLong(cursor.getColumnIndex(WakeUpSilenceUserDbOperator.FIRST_DOWNLOAD_TIME));
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
}
