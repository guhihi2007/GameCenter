package cn.lt.game.db.operation;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/28.
 */
public abstract class AbstractDBOperator {

    public int mDbVersion;
    List<String> mCreate = new ArrayList<String>();

    public abstract void create(SQLiteDatabase db);

    public abstract void upgrade(SQLiteDatabase db, int oldVersion);

}
