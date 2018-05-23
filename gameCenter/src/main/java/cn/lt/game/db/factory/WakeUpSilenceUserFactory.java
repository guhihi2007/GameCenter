package cn.lt.game.db.factory;

import android.content.Context;

import cn.lt.game.db.DBHelper;
import cn.lt.game.db.operation.WakeUpSilenceUserDbOperator;

/**
 * Created by LinJunSheng on 2016/11/24.
 */
public class WakeUpSilenceUserFactory implements IDBFactory {

    @Override
    public DBHelper getDB(Context context) {
        DBHelper helper = new DBHelper(context, WakeUpSilenceUserDbOperator.DBNAME, WakeUpSilenceUserDbOperator
                .DATABASE_VERSION);
        helper.setmOperator(new WakeUpSilenceUserDbOperator());
        return helper;
    }
}
