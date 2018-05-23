package cn.lt.game.db.operation;

import android.database.sqlite.SQLiteDatabase;

import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;

/***
 * Created by Administrator on 2015/10/28.
 */
public class DownloadDbOperator extends AbstractDBOperator {

    public static final String TABLE_NAME_DOWNLOAD = "FILEDOWN";
    public static final String TABLE_NAME_SERCH = "searchvalues";
    //(自动重试)
    //--请求次数最多4次（一天一个UUID请求同一个游戏）。
    //--每天最多上报4条数据，超过4条则不上报，上报数据的格式与手动重试数据格式一致，downloadType=auto；
    public static final String TABLE_NAME_RETRY = "retrydownload";
    /**
     * The Constant DBNAME.
     */
    public static final String DBNAME = "download.db";

    /**
     * The Constant VERSION.
     * <p/>
     * V3.1版本为17
     * <p/>
     * V3.2版本修改为18
     * <p/>
     * V4.0版本修改为19
     * <p>
     * V4.1版本修改为20
     * V4.2版本修改为21
     * V4.3版本修改为22
     * V4.5版本修改为23
     */
    public static final int DATABASE_VERSION = 23;

    @Override
    public void create(SQLiteDatabase db) {
        LogUtils.d(LogTAG.HTAG, "SQLiteDatabase=create=>begin");
        db.execSQL("CREATE TABLE IF NOT EXISTS FILEDOWN (" + "_ID INTEGER PRIMARY KEY," +
                "LOGOURL TEXT," + "NAME TEXT," + "PAKAGENAME TEXT," + "DOWNURL TEXT," + "DOWNPATH" +
                " TEXT," + "STATE INTEGER," + "DOWNLENGTH INTEGER," + "TOTALLENGTH INTEGER," +
                "DOWNSUFFIX TEXT," + "VERSIONCODE TEXT," + "VERSION TEXT," + "MDFIVE TEXT," +
                "UPDATECONTENT TEXT," + "OPENTIME TEXT," + "PREVSTATE TEXT," + "event_type text,"
                + "topic_id INTEGER," + "trigger_path text," + "FORUM_ID INTEGER," + "HAS_STRATEGY INTEGER," +
                "HAS_GIFT INTEGER," + " MARK TEXT," + "CATEGORY TEXT," + "REVIEWS TEXT," + "SCORE TEXT,"
                + "DOWNLOAD_CNT TEXT," + "PKG_SIZE TEXT," + "DOWNLOAD_FAILD_REASON TEXT,"
                + "IS_ORDER_WIFI_DOWNLOAD INTEGER,"  // 4.1版本，预约WiFi下载新增字段
                + "IS_COVERED_APP INTEGER,"    // 4.2新添加字段（是否可覆盖游戏）
                + "CAN_UPGRADE INTEGER,"    // 4.3新添加字段（是否可升级）
                + "BUSINESS_PACKAGE INTEGER,"    // 4.3新添加字段（是否是商务包）
                + "DOWNLOAD_FROM TEXT,"+"DOWNLOAD_POINT INTEGER," + "IS_ACCEPTED INTEGER," // 4.3新添加字段（下载来源,是否已领取积分,下载积分）
                + "DEEPLINK TEXT"    // 4.3新添加字段（是否是商务包）
                + ")");//【表添加社区ID字段
        // added by tiantian 2015.9.2】

        // 创建搜索缓存的表
        db.execSQL("create table if not exists searchvalues(searchvalue varchar(10) PRIMARY KEY )");
        // 创建图片缓存的表
        db.execSQL("create table if not exists categorycache(_ID INTEGER  PRIMARY KEY , " +
                "ImageCache Text, GameTitle Text , GameCount Text )");
//		// 创建下载通知存储表
//		db.execSQL("create table if not exists notification(install_notification varchar(10)
// PRIMARY KEY)");
        // 创建重试下载计数表（honaf）
        //自动重试相关代码暂时关闭,包括数据库创建,包括数据库创建,包括数据库创建,重要的事情说三遍
        //产品说暂时不要,千万不要删,不知道哪天又会要
//        db.execSQL("create table if not exists " + TABLE_NAME_RETRY + "(_id INTEGER  PRIMARY KEY , " +
//                "time DATE,count INTEGER )");
        LogUtils.d(LogTAG.HTAG, "SQLiteDatabase=create=>end");
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion) {
        LogUtils.d(LogTAG.HTAG, "SQLiteDatabase==>upgrade==>" + oldVersion);
        switch (oldVersion) {
            case 15:
                db.execSQL("alter table FILEDOWN add column OPENTIME text");
                db.execSQL(" ALTER TABLE FILEDOWN ADD COLUMN event_type text");
                db.execSQL(" ALTER TABLE FILEDOWN ADD COLUMN topic_id INTEGER");
                db.execSQL(" ALTER TABLE FILEDOWN ADD COLUMN trigger_path text");
                db.execSQL("DROP TABLE notification");
                db.execSQL(" ALTER TABLE FILEDOWN ADD COLUMN FORUM_ID INTEGER");//【添加一个新的字段:社区ID
                // tiantian】
//                break;
            case 16:
                // 修改表；
                db.execSQL(" ALTER TABLE FILEDOWN ADD COLUMN event_type text");
                db.execSQL(" ALTER TABLE FILEDOWN ADD COLUMN topic_id INTEGER");
                db.execSQL(" ALTER TABLE FILEDOWN ADD COLUMN trigger_path text");
                db.execSQL("DROP TABLE notification");
                db.execSQL(" ALTER TABLE FILEDOWN ADD COLUMN FORUM_ID INTEGER");//【添加一个新的字段:社区ID
                // tiantian】
            case 17:
                // 修改表；
                db.execSQL(" ALTER TABLE FILEDOWN ADD COLUMN FORUM_ID INTEGER");//【添加一个新的字段:社区ID
                // tiantian】
//                break;
            case 18:
                db.execSQL(" ALTER TABLE FILEDOWN ADD COLUMN HAS_STRATEGY INTEGER");
                db.execSQL(" ALTER TABLE FILEDOWN ADD COLUMN HAS_GIFT INTEGER");
                //mark
                db.execSQL(" ALTER TABLE FILEDOWN ADD COLUMN MARK TEXT");
                //category
                db.execSQL(" ALTER TABLE FILEDOWN ADD COLUMN CATEGORY TEXT");
//                reviews
                db.execSQL(" ALTER TABLE FILEDOWN ADD COLUMN REVIEWS TEXT");
//              Score
                db.execSQL(" ALTER TABLE FILEDOWN ADD COLUMN SCORE TEXT");
                //DownloadCnt
                db.execSQL(" ALTER TABLE FILEDOWN ADD COLUMN DOWNLOAD_CNT TEXT");
                //PkgSize
                db.execSQL(" ALTER TABLE FILEDOWN ADD COLUMN PKG_SIZE TEXT");
                //下载失败原因，给存储空间不足使用

//                break;
            case 19:
                // 新增预约wifi下载字段
                db.execSQL(" ALTER TABLE FILEDOWN ADD IS_ORDER_WIFI_DOWNLOAD INTEGER");
                //自动重试相关代码暂时关闭,包括数据库创建,包括数据库创建,包括数据库创建,重要的事情说三遍
                //产品说暂时不要,千万不要删,不知道哪天又会要
//                db.execSQL("create table if not exists " + TABLE_NAME_RETRY + "(_id INTEGER  PRIMARY KEY , " +
//                        "time DATE,count INTEGER )");

//                break;
            case 20:
                // 新增预约wifi下载字段
                db.execSQL(" ALTER TABLE FILEDOWN ADD IS_COVERED_APP INTEGER");
            case 21:
                db.execSQL(" ALTER TABLE FILEDOWN ADD CAN_UPGRADE INTEGER");
                db.execSQL(" ALTER TABLE FILEDOWN ADD BUSINESS_PACKAGE INTEGER");
                // "is_accepted": "是否已领取积分","download_point": "下载积分",
                db.execSQL(" ALTER TABLE FILEDOWN ADD DOWNLOAD_POINT INTEGER");
                db.execSQL(" ALTER TABLE FILEDOWN ADD IS_ACCEPTED INTEGER");
                db.execSQL(" ALTER TABLE FILEDOWN ADD DOWNLOAD_FROM TEXT");
            case 22:
                db.execSQL(" ALTER TABLE FILEDOWN ADD DEEPLINK TEXT");
                break;
            default:
                break;
        }
    }
}
