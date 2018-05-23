package cn.lt.game.statistics.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.db.operation.StaisticsDbOperator;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.statistics.database.dao.supers.AbstractDao;
import cn.lt.game.statistics.exception.NullArgException;

/**
 * @author chengyong
 * @time 2017/7/24 11:39
 * @des ${TODO}
 */

public class RealReportDataContainerDao extends AbstractDao<String> {
    private static RealReportDataContainerDao dao;

    public RealReportDataContainerDao(Context context) {
        super(context);
        mTableName = StaisticsDbOperator.TABLE_NAME_REPORT_CONTAINER;
    }

    public static RealReportDataContainerDao getInstance(Context context) {
        if (dao == null) {
            synchronized (RealReportDataContainerDao.class) {
                if (dao == null) {
                    return new RealReportDataContainerDao(context);
                }
            }
        }
        return dao;
    }
    @Override
    public  synchronized void insertSingleData(String data) throws Exception {
        try {
                if (data.equals("")) {
                    throw new NullArgException("传入参数failreport数据为空...");
                }
//                LogUtils.i(LogTAG.RC, "将要操作的 mTableName:" + mTableName + "存入的数据" + data.toString());
                ContentValues values = new ContentValues();
                values.put(StaisticsDbOperator.COLUMN_EACH_DATA, data);
                if (mDb != null) {
                    mDb.insert(mTableName, null, values);
                }
        }catch (NullArgException e) {
            e.printStackTrace();
        } finally {
            mDb.close();
        }
    }

    @Override
    public  synchronized void deleteSingleData(String data) throws Exception {
        try {
            mDb.delete(mTableName, StaisticsDbOperator.COLUMN_EACH_DATA + "=?", new
                    String[]{data});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    @Override
    public synchronized void updateSingleData(String data, UpdateTableType type) throws Exception {

    }

    @Override
    public synchronized String requireSingleData(String data) throws Exception {
        return null;
    }

    @Override
    protected synchronized List rowMaps(Cursor cursor) throws Exception {
        String[] columns = {StaisticsDbOperator.COLUMN_EACH_DATA};
        List<String> list = new ArrayList<>();
        try {
            cursor = mDb.query(mTableName, columns, null, null, null, null, null);
            while (cursor.moveToNext()) {
                list.add(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_EACH_DATA)));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(LogTAG.RC, "requeryAllStatisticData抛异常：" + e.getMessage());
            return list;
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }
}
