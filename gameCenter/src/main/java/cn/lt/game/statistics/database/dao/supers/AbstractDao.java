package cn.lt.game.statistics.database.dao.supers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import cn.lt.game.db.DBHelper;
import cn.lt.game.db.factory.IDBFactory;
import cn.lt.game.db.factory.StaisticsDBFactory;

public abstract class AbstractDao<T> {

    protected final String TAG = this.getClass().getName();
    protected Context mContext;
    protected SQLiteDatabase mDb;
    /**
     * 数据库表名称；
     */
    protected String mTableName;

    public AbstractDao() {

    }

    public AbstractDao(Context context) {
        this.mContext = context;
        IDBFactory factory = new StaisticsDBFactory();
        DBHelper helper = factory.getDB(context);
        this.mDb = helper.getWritableDatabase();
    }

    /**
     * 插入一条数据；
     *
     * @param data
     */
    public abstract void insertSingleData(T data) throws Exception;

    /**
     * 删除指定的数据；
     */
    public abstract void deleteSingleData(T data) throws Exception;

    /**
     * 更新一条数据；
     */
    public abstract void updateSingleData(T data, UpdateTableType type) throws Exception;

    /**
     * 查询指定的对吸纳
     *
     * @return 返回的数据库实体对象；
     */
    public abstract T requireSingleData(T data) throws Exception;

    /**
     * 删除数据库所有数据；
     */
    public void deleteAll() throws Exception {
        mDb.delete(mTableName, null, null);
    }

    /**
     * 查询所有数据；
     *
     * @return
     */
    public List<T> requireAll() throws Exception {
        Cursor cursor = mDb.query(mTableName, null, null, null, null, null, null);
        return rowMaps(cursor);
    }

    /**
     * 行映射；
     *
     * @return
     */
    protected abstract List<T> rowMaps(Cursor cursor) throws Exception;

    /**
     * 关闭数据库；
     */
    public void close() {
        try {
            if (mDb != null) {
                mDb.close();
            }
        } catch (Exception e) {

        }

    }

    /**
     * 对数据库已有数据做修改的类型“增加数据、减少数据”，主要针对次数的修改；
     */
    public enum UpdateTableType {
        add, cut
    }

}
