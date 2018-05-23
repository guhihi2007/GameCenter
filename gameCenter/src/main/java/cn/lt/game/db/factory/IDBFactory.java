package cn.lt.game.db.factory;


import android.content.Context;

import cn.lt.game.db.DBHelper;

/**
 * Created by Administrator on 2015/10/26.
 */
public interface IDBFactory {

    DBHelper getDB(Context context);
}
