package cn.lt.game.db.factory;

import android.content.Context;

import cn.lt.game.db.DBHelper;
import cn.lt.game.db.operation.FavoriteDbOperator;

/**
 * Created by Administrator on 2015/10/29.
 */
public class FavoriteDBFactory implements IDBFactory {

    @Override
    public DBHelper getDB(Context context) {
        DBHelper helper = new DBHelper(context, FavoriteDbOperator.DBNAME, FavoriteDbOperator
                .DATABASE_VERSION);
        helper.setmOperator(new FavoriteDbOperator());
        return helper;
    }
}
