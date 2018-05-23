package cn.lt.game.db.factory;

import android.content.Context;

import cn.lt.game.db.DBHelper;
import cn.lt.game.db.operation.DownloadDbOperator;

/**
 * Created by Administrator on 2015/10/29.
 */
public class DownloadDBFactory implements IDBFactory {

    @Override
    public DBHelper getDB(Context context) {
        DBHelper helper = new DBHelper(context, DownloadDbOperator.DBNAME, DownloadDbOperator
                .DATABASE_VERSION);
        helper.setmOperator(new DownloadDbOperator());
        return helper;
    }
}
