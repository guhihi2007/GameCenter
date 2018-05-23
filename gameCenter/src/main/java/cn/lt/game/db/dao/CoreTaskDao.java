package cn.lt.game.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.db.DBHelper;
import cn.lt.game.db.factory.FavoriteDBFactory;
import cn.lt.game.db.factory.IDBFactory;
import cn.lt.game.db.operation.FavoriteDbOperator;
import cn.lt.game.model.CoreTask;

public class CoreTaskDao {

    private SQLiteDatabase mDatabase;
    private String mTableName;

    public CoreTaskDao(Context context) {
        IDBFactory factory = new FavoriteDBFactory();
        DBHelper helper = factory.getDB(context);
        mDatabase = helper.getWritableDatabase();
        mTableName = FavoriteDbOperator.GAMESERVICETASK_TABLE_NAME;
    }

    /**
     * 增
     *
     * @param coreTask
     */
    public void insert(CoreTask coreTask) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(FavoriteDbOperator.PRIORITY, coreTask.getPriority());
            cv.put(FavoriteDbOperator.NETWORKCATEGORY, coreTask.getNetworkCategory());
            cv.put(FavoriteDbOperator.ISURGENT, coreTask.isUrgent());
            cv.put(FavoriteDbOperator.TIMING, coreTask.getTime() + "");
            cv.put(FavoriteDbOperator.LOOPTIME, coreTask.getLoopTime() + "");
            cv.put(FavoriteDbOperator.TASKTYPE, coreTask.getTaskType());
            cv.put(FavoriteDbOperator.LASTTIMING, coreTask.getLastTime());
            mDatabase.insert(mTableName, null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 改
     *
     * @param coreTask
     */
    public void update(CoreTask coreTask) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(FavoriteDbOperator.PRIORITY, coreTask.getPriority());
            cv.put(FavoriteDbOperator.NETWORKCATEGORY, coreTask.getNetworkCategory());
            cv.put(FavoriteDbOperator.ISURGENT, coreTask.isUrgent());
            cv.put(FavoriteDbOperator.TIMING, coreTask.getTime() + "");
            cv.put(FavoriteDbOperator.LOOPTIME, coreTask.getLoopTime() + "");
            cv.put(FavoriteDbOperator.TASKTYPE, coreTask.getTaskType());
            cv.put(FavoriteDbOperator.LASTTIMING, coreTask.getLastTime());
            mDatabase.update(mTableName, cv, FavoriteDbOperator._id + "=?", new String[]{coreTask
                    .getId() + ""});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删
     *
     * @param coreTask
     */
    public void delete(CoreTask coreTask) {
        try {
            mDatabase.delete(mTableName, FavoriteDbOperator._id +
                    " " +
                    "=? or " + FavoriteDbOperator.TASKTYPE + " = ? ", new String[]{coreTask.getId
                    () + "", coreTask.getTaskType() + ""});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查
     *
     * @param deviationTime
     * @return
     */
    public List<CoreTask> selectAllCanExe(long deviationTime) {
        List<CoreTask> list = new ArrayList<CoreTask>();
        CoreTask task = null;
        long curTime = System.currentTimeMillis();
        Cursor cursor = null;
        try {
            cursor = mDatabase.query(mTableName, null, null, null, null, null, FavoriteDbOperator
                    .ISURGENT + " desc");
            System.out.println("数据库中共有:" + cursor.getCount());
            if (cursor.moveToFirst()) {
                long timeTemp;
                do {
                    timeTemp = cursor.getLong(cursor.getColumnIndex(FavoriteDbOperator.TIMING));
                    if (timeTemp <= curTime + deviationTime) {
                        task = new CoreTask();
                        task.setTime(timeTemp);
                        task.setId(cursor.getInt(cursor.getColumnIndex(FavoriteDbOperator._id)));
                        task.setLoopTime(cursor.getLong(cursor.getColumnIndex(FavoriteDbOperator
                                .LOOPTIME)));
                        task.setNetworkCategory(cursor.getInt(cursor.getColumnIndex
                                (FavoriteDbOperator.NETWORKCATEGORY)));
                        task.setPriority(cursor.getInt(cursor.getColumnIndex(FavoriteDbOperator
                                .PRIORITY)));
                        task.setTaskType(cursor.getInt(cursor.getColumnIndex(FavoriteDbOperator
                                .TASKTYPE)));
                        task.setLastTime(cursor.getLong(cursor.getColumnIndex(FavoriteDbOperator
                                .LASTTIMING)));
                        task.setUrgent(cursor.getInt(cursor.getColumnIndex(FavoriteDbOperator
                                .ISURGENT)) != 0);
                        list.add(task);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public List<CoreTask> selectByType(int type) {
        List<CoreTask> list = new ArrayList<CoreTask>();
        CoreTask task = null;
        Cursor cursor = null;
        try {
            cursor = mDatabase.query(mTableName, null, FavoriteDbOperator.TASKTYPE + "=? ", new
                    String[]{type + ""}, null, null, null);
            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    task = new CoreTask();
                    task.setId(cursor.getInt(cursor.getColumnIndex(FavoriteDbOperator._id)));
                    task.setLoopTime(cursor.getLong(cursor.getColumnIndex(FavoriteDbOperator
                            .LOOPTIME)));
                    task.setNetworkCategory(cursor.getInt(cursor.getColumnIndex
                            (FavoriteDbOperator.NETWORKCATEGORY)));
                    task.setPriority(cursor.getInt(cursor.getColumnIndex(FavoriteDbOperator
                            .PRIORITY)));
                    task.setTaskType(cursor.getInt(cursor.getColumnIndex(FavoriteDbOperator
                            .TASKTYPE)));
                    task.setTime(cursor.getInt(cursor.getColumnIndex(FavoriteDbOperator.TIMING)));
                    task.setLastTime(cursor.getLong(cursor.getColumnIndex(FavoriteDbOperator
                            .LASTTIMING)));
                    task.setUrgent(cursor.getInt(cursor.getColumnIndex(FavoriteDbOperator
                            .ISURGENT)) != 0);
                    list.add(task);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public void close() {
        mDatabase.close();
    }
}
