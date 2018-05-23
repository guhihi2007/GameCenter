package cn.lt.game.db.factory;

import android.content.Context;

import cn.lt.game.db.DBHelper;
import cn.lt.game.db.operation.StaisticsDbOperator;

/**
 * Created by Administrator on 2015/10/29.
 */
public class StaisticsDBFactory implements IDBFactory {

    @Override
    public DBHelper getDB(Context context) {
        DBHelper helper = new DBHelper(context, StaisticsDbOperator.DBNAME, StaisticsDbOperator
                .DATABASE_VERSION);
        helper.setmOperator(new StaisticsDbOperator());
        return helper;
    }
}
