package cn.lt.game.statistics.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.application.MyApplication;
import cn.lt.game.db.DBHelper;
import cn.lt.game.db.factory.IDBFactory;
import cn.lt.game.db.factory.StaisticsDBFactory;
import cn.lt.game.db.factory.UserAccountFactory;
import cn.lt.game.db.operation.StaisticsDbOperator;
import cn.lt.game.db.operation.UserAccountDbOperator;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.statistics.database.dao.supers.AbstractDao;
import cn.lt.game.statistics.entity.StatisDownloadTempInfoData;
import cn.lt.game.statistics.exception.NullArgException;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;


public class StatisticTempBuilderDao {

    private SQLiteDatabase mDatabase;
    private static StatisticTempBuilderDao userDao;
    String mTableName = StaisticsDbOperator.TABLE_NAME_DOWNLOAD_TEMP_INFO;

    public static StatisticTempBuilderDao newInstance(Context context) {
        if (userDao == null) {
            synchronized (StatisticTempBuilderDao.class) {
                if (userDao == null) {
                    userDao = new StatisticTempBuilderDao(context);
                }
            }
        }
        return userDao;
    }

    public StatisticTempBuilderDao(Context context) {
        initDataBase(context);
    }

    private void initDataBase(Context context) {
        if (mDatabase == null) {
            IDBFactory factory = new StaisticsDBFactory();
            DBHelper helper = factory.getDB(context);
            mDatabase = helper.getWritableDatabase();
        }
    }

    public  void releaseDataBase() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    public synchronized void saveOrUpdateSingleData(StatisDownloadTempInfoData data) throws Exception {
        if (data == null) {
            throw new NullArgException("TempBuilderDao:传入参数 saveOrUpdateSingleData 对象引用为空");
        }
        if ((TextUtils.isEmpty(data.getmPkgName()))) {
            throw new NullArgException("TempBuilderDao:传入参数 saveOrUpdateSingleData 包名为空...");
        }
        if (quarySingleBuildData(data) == null) {
            LogUtils.d(LogTAG.DOWNLOAD_REPORT, "saveOrUpdateSingleData：执行插入数据 ");
            saveSingleBuildData(data);
        } else {
            LogUtils.d(LogTAG.DOWNLOAD_REPORT, "saveOrUpdateSingleData：执行更新数据 ");
            updateSingleBuildData(data);
        }
    }

    public synchronized void saveSingleBuildData(StatisDownloadTempInfoData data) throws Exception {
        if (data == null) {
            throw new NullArgException("TempBuilderDao:传入参数saveSingleBuildData 对象引用为空");
        }
        if ((TextUtils.isEmpty(data.getmPkgName()))) {
            throw new NullArgException("TempBuilderDao:传入参数saveSingleBuildData 包名为空...");
        }
        try {
            ContentValues values = new ContentValues();
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_GAME_ID, data.getmGameID());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_PKG_NAME, data.getmPkgName());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_ACTION_TYPE, data.getmActionType());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_DOWNLAOD_TYPE, data.getmDownloadType());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_MPRESTATE, data.getmPreState());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_REMARK, data.getmRemark());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_PAGE, data.getmPage());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_POS, data.getPos());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_SUB_POS, data.getSubPos());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_DOWNLOAD_MODE, data.getDownload_mode());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_INSTALL_TYPE, data.getInstall_type());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_INSTALL_MODE, data.getInstall_mode());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_INSTALL_COUNT, data.getInstall_count());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_PRESENT_TYPE, data.getPresenType());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_FROM_PAGE, data.getFrom_page()); //420添加起始
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_FROM_ID, data.getFrom_id());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_WORD, data.getWord());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_INSTALLTIME, data.getInstall_time());//4.2.2起添加
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_ISUPDATE, data.getIsupdate());//4.4.0起添加
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_PAGE_ID, data.getPage_id());//4.5.0起添加
            long v = mDatabase.insert(mTableName, null, values);
            if (v > 0) {
                LogUtils.i(LogTAG.DOWNLOAD_REPORT, "TempBuilderDao:StatisticTempBuilderDao层插入数据成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(LogTAG.DOWNLOAD_REPORT, "TempBuilderDao:saveSingleBuildData 插入数据抛异常");
        }
    }

    public synchronized void updateSingleBuildData(StatisDownloadTempInfoData data) throws Exception {
        if (data == null) {
            throw new NullArgException("TempBuilderDao:传入参数DownloadInfoData 对象引用为空...");
        }
        try {
            ContentValues values = new ContentValues();
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_GAME_ID, data.getmGameID());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_PKG_NAME, data.getmPkgName());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_ACTION_TYPE, data.getmActionType());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_DOWNLAOD_TYPE, data.getmDownloadType());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_MPRESTATE, data.getmPreState());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_REMARK, data.getmRemark());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_PAGE, data.getmPage());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_POS, data.getPos());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_SUB_POS, data.getSubPos());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_DOWNLOAD_MODE, data.getDownload_mode());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_INSTALL_TYPE, data.getInstall_type());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_INSTALL_MODE, data.getInstall_mode());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_INSTALL_COUNT, data.getInstall_count());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_PRESENT_TYPE, data.getPresenType());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_FROM_PAGE, data.getFrom_page()); //420添加起始
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_FROM_ID, data.getFrom_id());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_WORD, data.getWord());
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_INSTALLTIME, data.getInstall_time());//4.2.2起添加
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_ISUPDATE, data.getIsupdate());//4.4.0起添加
            values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_PAGE_ID, data.getPage_id());//4.5.0起添加
            int v = mDatabase.update(mTableName, values, StaisticsDbOperator.COLUMN_DOWN_TEMP_GAME_ID + "=?", new String[]{data.getmGameID() + ""});
            if (v > 0) {
                LogUtils.i(LogTAG.DOWNLOAD_REPORT, "TempBuilderDao:StatisticTempBuilderDao层更新数据成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(LogTAG.DOWNLOAD_REPORT, "TempBuilderDao:updateSingleBuildData 更新数据抛异常");
        } finally {

        }
    }

    public synchronized StatisDownloadTempInfoData quarySingleBuildData(StatisDownloadTempInfoData data) throws Exception {
        if (data == null) {
            throw new NullArgException("TempBuilderDao:传入参数 quarySingleBuildData 对象引用为空...");
        }
        StatisDownloadTempInfoData tempData = null;
        Cursor cursor = null;
        try {
            tempData = null;
            if (!TextUtils.isEmpty(data.getmPkgName())) {
                cursor = mDatabase.query(mTableName, null, StaisticsDbOperator.COLUMN_DOWN_TEMP_PKG_NAME + "=?", new String[]{data.getmPkgName()}, null, null, null);
            }
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                tempData = new StatisDownloadTempInfoData();
                tempData.setmGameID(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_GAME_ID)));
                tempData.setmPkgName(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_PKG_NAME)));
                tempData.setmActionType(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_ACTION_TYPE)));
                tempData.setmDownloadType(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_DOWNLAOD_TYPE)));
                tempData.setmPreState(cursor.getInt(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_MPRESTATE)));
                tempData.setmRemark(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_REMARK)));
                tempData.setmPage(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_PAGE)));
                tempData.setPos(Integer.parseInt(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_POS))));
                tempData.setSubPos(Integer.parseInt(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_SUB_POS))));
                tempData.setDownload_mode(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_DOWNLOAD_MODE)));
                tempData.setInstall_type(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_INSTALL_TYPE)));
                tempData.setInstall_mode(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_INSTALL_MODE)));
                tempData.setInstall_count(cursor.getInt(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_INSTALL_COUNT)));
                tempData.setPresenType(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_PRESENT_TYPE)));
                tempData.setFrom_page(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_FROM_PAGE))); //420添加起始
                tempData.setFrom_id(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_FROM_ID)));
                tempData.setWord(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_WORD)));
                tempData.setInstall_time(cursor.getLong(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_INSTALLTIME)));
                tempData.setIsupdate(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_ISUPDATE)));//4.4.0起添加
                tempData.setPage_id(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_PAGE_ID)));//4.5.0起添加
            }
            LogUtils.i(LogTAG.DOWNLOAD_REPORT, "TempBuilderDao:quarySingleBuildData 查询数据成功");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            LogUtils.i(LogTAG.DOWNLOAD_REPORT, "TempBuilderDao:quarySingleBuildData 查询数据抛异常");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tempData;
    }


    public synchronized void deleteSingleBuildData(StatisDownloadTempInfoData data) throws Exception {
        if (data == null) {
            throw new NullArgException("传入参数DownloadInfoData 对象引用为空...");
        } else if (TextUtils.isEmpty(data.getmGameID())) {
            throw new NullArgException("传入的参数DownloadInfoData不正确，gameId不能等于0 ...");
        }
        try {
            int deleteCode = mDatabase.delete(mTableName, StaisticsDbOperator.COLUMN_DOWN_TEMP_GAME_ID + "=?", new String[]{data.getmGameID() + ""});
            LogUtils.d(LogTAG.DOWNLOAD_REPORT, "deleteSingleBuildData：删除数据成功吗？= " + deleteCode);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(LogTAG.DOWNLOAD_REPORT, "deleteSingleBuildData：删除数据抛异常 ");
        }
    }


    /*********************************************************/
    public synchronized List<StatisDownloadTempInfoData> quaryAll(Cursor cursor) throws Exception {
        List<StatisDownloadTempInfoData> datas = null;
        if (cursor != null && cursor.getCount() > 0) {
            datas = new ArrayList<>();
            while (cursor.moveToNext()) {
                StatisDownloadTempInfoData data = new StatisDownloadTempInfoData();
                data.setmGameID(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_GAME_ID)));
                data.setmPkgName(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_PKG_NAME)));
                data.setmActionType(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_ACTION_TYPE)));
                data.setmDownloadType(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_DOWNLAOD_TYPE)));
                data.setmPreState(cursor.getInt(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_MPRESTATE)));
                data.setmRemark(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_REMARK)));
                data.setmPage(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_PAGE)));
                data.setPos(Integer.parseInt(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_POS))));
                data.setSubPos(Integer.parseInt(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_SUB_POS))));
                data.setDownload_mode(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_DOWNLOAD_MODE)));
                data.setInstall_type(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_INSTALL_TYPE)));
                data.setInstall_mode(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_INSTALL_MODE)));
                data.setInstall_mode(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_INSTALL_COUNT)));
                data.setPresenType(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_PRESENT_TYPE)));
                data.setFrom_page(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_FROM_PAGE)));
                data.setFrom_id(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_FROM_ID)));
                data.setWord(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_WORD)));
                data.setInstall_time(cursor.getLong(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_INSTALLTIME)));
                data.setIsupdate(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_ISUPDATE)));//4.4.0起添加
                data.setPage_id(cursor.getString(cursor.getColumnIndex(StaisticsDbOperator.COLUMN_DOWN_TEMP_PAGE_ID)));//4.5.0起添加
                datas.add(data);
            }
        }
        return datas;
    }
}

