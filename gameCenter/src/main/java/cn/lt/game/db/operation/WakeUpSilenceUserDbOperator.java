package cn.lt.game.db.operation;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by LinJunSheng on 2016/11/24.
 */
public class WakeUpSilenceUserDbOperator extends AbstractDBOperator {

    public static final int DATABASE_VERSION = 1;

    public static final String DBNAME = "wakeup_silence_user.db";
    public static final String TABLE_NAME = "wusu_table";

    public static final String FIRST_START_TIME = "first_start_time";
    public static final String FIRST_DOWNLOAD_TIME = "first_download_time";
    public static final String TIME_ID = "time_id";
    public static final String _id = "_id";

    private final String createWakeUpWuseTable = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME + " (" + _id + " INTEGER primary key autoincrement, "
            + TIME_ID + " INTEGER, "
            + FIRST_START_TIME + " INTEGER, "
            + FIRST_DOWNLOAD_TIME + " INTEGER)";


    @Override
    public void create(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(createWakeUpWuseTable);
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion) {
        switch (oldVersion) {
            case 1:

                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;

            default:
                break;
        }
    }
}
