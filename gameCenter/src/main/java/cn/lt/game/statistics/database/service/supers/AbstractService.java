package cn.lt.game.statistics.database.service.supers;

import android.content.Context;

import java.util.List;

import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.statistics.database.dao.supers.AbstractDao;
import cn.lt.game.statistics.database.dao.supers.AbstractDao.UpdateTableType;

public abstract class AbstractService<T> {

    protected final String TAG = LogTAG.DOWNLOAD_REPORT;

    protected Context mContext;

    public AbstractService(Context context) {
        this.mContext = context;
    }

    /**
     * 往数据库中插入相关数据；
     *
     * @param data
     */
    public void insertSingleDataToDB(T data, AbstractDao<T> dao) {
        try {
            if (dao.requireSingleData(data) != null) {
                dao.updateSingleData(data, UpdateTableType.add);
            } else {
                dao.insertSingleData(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(TAG, e.getMessage());
        }

        dao.close();
    }

    /**
     * 删除数据；
     *
     * @param data
     */
    public abstract void deleteSingleDataFromDB(T data, AbstractDao<T> dao);

    /**
     * 查询所有数据；
     *
     * @return
     */
    public List<T> getAllDataFromDB(AbstractDao<T> dao) {
        List<T> list = null;
        try {
            list = dao.requireAll();

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(TAG, e.getMessage());
        }
        dao.close();
        return list;
    }

    /**
     * 删除所有数据；
     */
    public void deleteAllDataFromDB(AbstractDao<T> dao) {
        try {
            dao.deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(TAG, e.getMessage());
        }
        dao.close();
    }

    public T getSingleDataFromDB(T data, AbstractDao<T> dao) {
        return null;
    }

}
