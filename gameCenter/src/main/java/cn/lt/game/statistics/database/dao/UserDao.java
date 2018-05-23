package cn.lt.game.statistics.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.db.DBHelper;
import cn.lt.game.db.factory.IDBFactory;
import cn.lt.game.db.factory.UserAccountFactory;
import cn.lt.game.db.operation.UserAccountDbOperator;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.statistics.exception.NullArgException;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;


public class UserDao {

    private SQLiteDatabase mDatabase;
    private int i;
    private Context context;
    private static UserDao userDao;

    public static UserDao newInstance(Context context) {
        if (userDao == null) {
            synchronized (UserDao.class) {
                if (userDao == null) {
                    userDao = new UserDao(context);
                }
            }
        }
        return userDao;
    }

    public UserDao(Context context) {
        this.context = context;
        initDataBase(context);
    }

    private synchronized void initDataBase(Context context) {
        if (mDatabase == null) {
            IDBFactory factory = new UserAccountFactory();
            DBHelper helper = factory.getDB(context);
            mDatabase = helper.getWritableDatabase();
        }
    }

    public synchronized void insertSingleData(UserBaseInfo info) throws Exception {
        if (info == null ){
            throw new NullArgException("insertSingleData 传入UserBaseInfo为空...");
        }
        if ((TextUtils.isEmpty(info.getToken()) )) {
            throw new NullArgException("insertSingleData token为空...");
        }
        ContentValues values = new ContentValues();
        values.put(UserAccountDbOperator.USERID, info.getId());
        values.put(UserAccountDbOperator.TOKEN, TextUtils.isEmpty(info.getToken())?"":info.getToken());
        values.put(UserAccountDbOperator.NUMBER, TextUtils.isEmpty(info.getMobile())?"":info.getMobile());
        values.put(UserAccountDbOperator.AVATAR , TextUtils.isEmpty(info.getAvatar())?"":info.getAvatar());
        values.put(UserAccountDbOperator.EMAIL , TextUtils.isEmpty(info.getEmail())?"":info.getEmail());
        values.put(UserAccountDbOperator.NICKNAME , TextUtils.isEmpty(info.getNickname())?"":info.getNickname());
        values.put(UserAccountDbOperator.SEX , TextUtils.isEmpty(info.getSex())?"":info.getSex());
        values.put(UserAccountDbOperator.BIRTHDAY , info.getBirthday());
        values.put(UserAccountDbOperator.ADDRESS , TextUtils.isEmpty(info.getAddress())?"":info.getAddress());
        values.put(UserAccountDbOperator.USERNAME , TextUtils.isEmpty(info.getUserName())?"":info.getUserName());
        long v = mDatabase.insert(UserAccountDbOperator.TABLE_NAME, null, values);
        if (v > 0) {
            LogUtils.d(LogTAG.USER, "gamecenter:self, 插入user数据成功=userid:"+info.getId());
//            Uri uri = Uri.parse("content://cn.lt.game.UserAccountProvider/account");
//            context.getContentResolver().notifyChange(uri, null);
        }
    }


    public List<UserBaseInfo> queryUserData() throws Exception {
        Cursor cursor = null;
        List<UserBaseInfo> list=new ArrayList<>();
        cursor = mDatabase.query(UserAccountDbOperator.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            UserBaseInfo info=new UserBaseInfo();
            info.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.USERID))));
            info.setToken(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.TOKEN)));
            info.setMobile(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.NUMBER)));
            info.setAvatar(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.AVATAR)));
            info.setEmail(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.EMAIL)));
            info.setNickname(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.NICKNAME)));
            info.setSex(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.SEX)));
            info.setBirthday(Long.parseLong(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.BIRTHDAY))));
            info.setAddress(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.ADDRESS)));
            info.setUserName(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.USERNAME)));
            list.add(info);
        }
        cursor.close();
//        mDatabase.close();
        return list;
    }

    public void deleteAll() throws Exception {
        mDatabase.delete(UserAccountDbOperator.TABLE_NAME, null, null);
        LogUtils.d(LogTAG.USER, "gamecenter:self,游戏中心自己 删除user数据成功=userid:");
    }

//    public void updateSingleData(StatisDownloadTempInfoData data, UpdateTableType type) throws Exception {
//        if (data == null) {
//            throw new NullArgException("传入参数DownloadInfoData 对象引用为空...");
//        }
//        ContentValues values = new ContentValues();
//        values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_GAME_ID, data.getmGameID());
//        values.put(StaisticsDbOperator.COLUMN_DOWN_TEMP_ISUPDATE, data.getIsupdate());//4.4.0起添加
//        int v = mDb.update(mTableName, values, StaisticsDbOperator.COLUMN_DOWN_TEMP_GAME_ID + "=?", new String[]{data.getmGameID() + ""});
//        if (v > 0) {
//            LogUtils.i(LogTAG.DOWNLOAD_REPORT, "DownloadTempInfoDao层更新数据成功");
//        }
//    }

}

