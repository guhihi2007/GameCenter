package cn.lt.game.statistics.database.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.db.operation.UserAccountDbOperator;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

/**
 * @author chengyong
 * @time 2017/9/21 15:21
 * @des ${TODO}
 */

public class NotifyUserInfoToAppCenterMgr {

    public static final String NUMBER = "number";
    public static final String TOKEN = "token";
    public static final String USERID = "userid";
    public static final String TABLENAME = "USER_ENTITY";

    public static void insertIntoAppCenter(Context context, UserBaseInfo info){
        try {
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://cn.lt.appstore.UserAccountProvider/"+TABLENAME);
            ContentValues values = new ContentValues();
            values.put(NUMBER, info.getMobile());
            values.put(TOKEN, info.getToken());
            values.put(USERID, info.getId()+"");
            resolver.insert(uri, values);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(LogTAG.USER, "游戏中心 NotifyUserInfoToAppCenterMgr：insertIntoAppCenter-" + e.getMessage());
        }
    }

    public static void deleteIntoAppCenter(Context context){
        try {
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://cn.lt.appstore.UserAccountProvider/"+TABLENAME);
            resolver.delete(uri, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(LogTAG.USER, "游戏中心 NotifyUserInfoToAppCenterMgr：deleteIntoAppCenter-" + e.getMessage());
        }
    }

    public static List<UserBaseInfo> quaryFromAppCenter(Context context){
        Cursor cursor=null;
        try {
            List<UserBaseInfo> list=new ArrayList<>();
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://cn.lt.appstore.UserAccountProvider/"+TABLENAME);
            cursor = resolver.query(uri, null, null, null, null);
            if (cursor != null) {
                LogUtils.d(LogTAG.USER, "游戏中心 NotifyUserInfoToAppCenterMgr：cursor 不为空-");
                while (cursor.moveToNext()) {
                    LogUtils.d(LogTAG.USER, "游戏中心 NotifyUserInfoToAppCenterMgr：cursor遍历了");
                    UserBaseInfo info = new UserBaseInfo();
//                info.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.USERIDAppCenter))));
                    info.setToken(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.TOKENAppCenter)));
//                info.setMobile(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.NUMBERAppCenter)));
//                info.setAvatar(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.AVATARAppCenter)));
//                info.setEmail(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.EMAILAppCenter)));
//                info.setNickname(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.NICKNAMEAppCenter)));
//                info.setSex(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.SEXAppCenter)));
//                info.setBirthday(Long.parseLong(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.BIRTHDAYAppCenter))));
//                info.setAddress(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.ADDRESSAppCenter)));
//                info.setUserName(cursor.getString(cursor.getColumnIndex(UserAccountDbOperator.USERNAMEAppCenter)));
                    list.add(info);
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(LogTAG.USER, "游戏中心 NotifyUserInfoToAppCenterMgr：quaryFromAppCenter-" + e.getMessage());
        }finally {
            if(cursor!=null){
                cursor.close();
                cursor=null;
            }
        }
        return null;
    }
}
