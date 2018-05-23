package cn.lt.game.db.operation;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author chengyong
 * @time 2017/9/21 11:08
 * @des ${TODO}
 */
public class UserAccountDbOperator extends AbstractDBOperator {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "user.db";

    public static final String TABLE_NAME = "account";

    public static final String NUMBER = "number";
    public static final String TOKEN = "token";
    public static final String USERID = "userid";

    public static final String AVATAR = "avatar";
    public static final String EMAIL = "email";
    public static final String NICKNAME = "nickname";
    public static final String SEX = "sex";
    public static final String BIRTHDAY = "birthday";
    public static final String ADDRESS = "address";
    public static final String USERNAME = "userName";

    public static final String NUMBERAppCenter = "NUMBER";
    public static final String TOKENAppCenter = "TOKEN";
    public static final String USERIDAppCenter = "USERID";

    public static final String AVATARAppCenter = "AVATAR";
    public static final String EMAILAppCenter = "EMAIL";
    public static final String NICKNAMEAppCenter = "NICKNAME";
    public static final String SEXAppCenter = "SEX";
    public static final String BIRTHDAYAppCenter = "BIRTHDAY";
    public static final String ADDRESSAppCenter = "ADDRESS";
    public static final String USERNAMEAppCenter = "USERNAME";

    @Override
    public void create(SQLiteDatabase db) {
        String sql = "create table "+TABLE_NAME+ " (_id integer primary key autoincrement , " +
                 NUMBER+ " varchar(1),  "+TOKEN+ "  varchar(1),  "+AVATAR+ "  varchar(1),  "+
                EMAIL+ "  varchar(1),  "+NICKNAME+ "  varchar(1),  "+SEX+ "  varchar(1),  "+
                BIRTHDAY+ "  varchar(1),  "+ADDRESS+ "  varchar(1),  "+USERNAME+ "  varchar(1),  "+USERID+ "  varchar(1) unique)";
        db.execSQL(sql);
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion) {
        switch (oldVersion) {
            case 1:

                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;

            default:
                break;
        }
    }
}
