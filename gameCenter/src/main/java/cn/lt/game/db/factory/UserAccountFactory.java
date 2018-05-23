package cn.lt.game.db.factory;

import android.content.Context;

import cn.lt.game.db.DBHelper;
import cn.lt.game.db.operation.UserAccountDbOperator;
import cn.lt.game.db.operation.WakeUpSilenceUserDbOperator;

/**
 * @author chengyong
 * @time 2017/9/21 11:08
 * @des ${TODO}
 */
public class UserAccountFactory implements IDBFactory {

    @Override
    public DBHelper getDB(Context context) {

        DBHelper helper = new DBHelper(context, UserAccountDbOperator.DATABASE_NAME, UserAccountDbOperator
                .DATABASE_VERSION);
        helper.setmOperator(new UserAccountDbOperator());
        return helper;
    }
}
