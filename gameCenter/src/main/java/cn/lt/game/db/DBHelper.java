package cn.lt.game.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.lt.game.db.operation.AbstractDBOperator;

/**
 * Created by Administrator on 2015/10/29.
 */
public class DBHelper extends SQLiteOpenHelper {

    private AbstractDBOperator mOperator;

    public DBHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    public void setmOperator(AbstractDBOperator mOperator) {
        this.mOperator = mOperator;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            mOperator.create(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            mOperator.upgrade(db, oldVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
