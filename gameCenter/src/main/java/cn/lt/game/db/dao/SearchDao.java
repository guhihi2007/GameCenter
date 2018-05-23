package cn.lt.game.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.db.DBHelper;
import cn.lt.game.db.factory.DownloadDBFactory;
import cn.lt.game.db.factory.IDBFactory;
import cn.lt.game.db.operation.DownloadDbOperator;
import cn.lt.game.lib.util.LogUtils;

/**
 * 搜索功能做历史缓存数据库增加、删除、查询
 */
public class SearchDao {

    private SQLiteDatabase mDatabase;
    private String mTableName;

    public SearchDao(Context context) {
        IDBFactory factory = new DownloadDBFactory();
        DBHelper helper = factory.getDB(context);
        mDatabase = helper.getWritableDatabase();
        mTableName = DownloadDbOperator.TABLE_NAME_SERCH;
    }

    /**
     * 增加
     */
    public void add(String appName) {
        try {
            ContentValues values = new ContentValues();
            values.put("searchvalue", appName);
            mDatabase.insert(mTableName, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除一个
     */

    public void deleteOne(String appName) {
        try {
            mDatabase.delete(mTableName, "searchvalue=?", new String[]{appName});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除全部
     */

    public void deleteAll() {
        try {
            mDatabase.delete(mTableName, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 查所有
     */
    public List<String> findAll(String keyword) {
        List<String> allSearch = null;
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery("select * from searchvalues where searchvalue like ?",
                    new String[]{"%" + keyword + "%"});
            allSearch = new ArrayList<String>();
            while (cursor.moveToNext()) {
                String search = cursor.getString(0);
                LogUtils.i("ttt" , "search = " + search + "======> keyword = " + keyword);
                if (search.contains(keyword)){
                    allSearch.add(search);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }
        return allSearch;
    }

    /**
     * 查存在
     */
    public boolean findOne(String appName) {
        List<String> allSearch = null;
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery("select * from searchvalues", null);
            allSearch = new ArrayList<String>();
            while (cursor.moveToLast()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }
        return false;
    }

    public void close() {
        try {
            mDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
