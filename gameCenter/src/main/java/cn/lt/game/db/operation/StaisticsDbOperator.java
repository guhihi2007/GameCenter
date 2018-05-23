package cn.lt.game.db.operation;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import cn.lt.game.lib.util.LogUtils;

/***
 * Created by Administrator on 2015/10/28.
 */
public class StaisticsDbOperator extends AbstractDBOperator {

    /**
     * 3.1版本的数据库版本为1；
     * <p/>
     * 3.2版本数据库有修改升级到2；
     * <p/>
     * 3.2.1版本数据库无修改为2；
     * <p/>
     * 3.2.2版本数据库有修改升级到3；
     * <p/>
     * 4.0版本数据库有修改升级到4；
     * <p/>
     * 4.0.2版本数据库有修改升级到5；
     * 4.0.4版本数据库有修改升级到6；
     * 4.1.0版本数据库有修改升级到7；
     * 4.2.0版本数据库有修改升级到8；
     * 4.2.2，4.2.3版本数据库有修改升级到9；
     *
     * 4.4.0版本数据库有修改升级到 10；
     */
    public static final int DATABASE_VERSION = 11;

    public static final String DBNAME = "Staistics";

    public static final String TABLE_NAME_TOPIC = "topic";

    public static final String TABLE_NAME_PATH = "path";

    public static final String TABLE_NAME_ADS = "ads";

    public static final String TABLE_NAME_APPUPDATE = "upgrade";

    public static final String TABLE_NAME_GAMEDOWNLOAD = "download_install";

    public static final String TABLE_NAME_DOWNLOAD_INFO = "download_info";

    public static final String TABLE_NAME_COMMUNITY_FINAL = "community_final";

    public static final String TABLE_NAME_COMMUNITY_TEMPORARY = "community_temporary";

    public static final String COLUMN_COMMUNITY_IS_VISIT = "is_visit";

    public static final String COLUMN_COMMUNITY_USER_ID = "user_id";

    public static final String COLUMN_COMMUNITY_VISIT_TIME = "visit_time";

    public static final String COLUMN_COMMUNITY_VISIT_COUNT = "visit_count";

    public static final String COLUMN_COMMUNITY_VISIT_DURATION_BY_SECOND = "visit_duration_by_second";

    public static final String COLUMN_COMMUNITY_PAGE_NAME_LIST = "page_name_list";

    public static final String TABLE_NAME_SEARCH_DOWNLOAD_REQUEST = "search_download_request";

    public static final String TABLE_NAME_ADS_MODEL = "ads_model";

    public static final String TABLE_NAME_GAMEACTIVE = "game_active_click";

    public static final String TABLE_NAME_TEMPL = "temp_click";

    public static final String TABLE_NAME_RETRY_REPORT = "retry_report";
    public static final String COLUMN_FAIL_DATA = "fail_data";
    //440
    public static final String TABLE_NAME_REPORT_CONTAINER = "report_container";
    public static final String COLUMN_EACH_DATA = "each_data";


    /**************************************************************************************
     * 4.0版本开始下载统计需求的记录；
     */
    public static final String TABLE_NAME_DOWNLOAD_TEMP_INFO = "download_temp_info";

    public static final String COLUMN_DOWN_TEMP_GAME_ID = "game_id";

    public static final String COLUMN_DOWN_TEMP_PKG_NAME = "pkg_name";

    public static final String COLUMN_DOWN_TEMP_ACTION_TYPE = "action_type";

    public static final String COLUMN_DOWN_TEMP_DOWNLAOD_TYPE = "download_type";

    public static final String COLUMN_DOWN_TEMP_MPRESTATE = "pre_state";

    public static final String COLUMN_DOWN_TEMP_REMARK = "remark";

    public static final String COLUMN_DOWN_TEMP_PAGE = "page";

    public static final String COLUMN_DOWN_TEMP_POS = "pos";

    public static final String COLUMN_DOWN_TEMP_SUB_POS = "sub_pos";

    public static final String COLUMN_DOWN_TEMP_IS_DOWNLOAD = "is_download";


    public static final String COLUMN_DOWN_TEMP_DOWNLOAD_MODE = "download_mode";

    //安装数据
    public static final String COLUMN_DOWN_TEMP_INSTALL_TYPE = "install_type";

    public static final String COLUMN_DOWN_TEMP_INSTALL_MODE = "install_mode";

    public static final String COLUMN_DOWN_TEMP_INSTALL_COUNT = "install_count";//安装请求次数，用于限制重复安装上报次数 Added by ATian

    public static final String COLUMN_DOWN_TEMP_PRESENT_TYPE = "present_type";//安装请求次数，用于限制重复安装上报次数 Added by ATian

    public static final String COLUMN_DOWN_TEMP_FROM_PAGE = "from_page";//420添加起
    public static final String COLUMN_DOWN_TEMP_FROM_ID = "from_id";//
    public static final String COLUMN_DOWN_TEMP_WORD = "word";//
    public static final String COLUMN_DOWN_TEMP_INSTALLTIME = "install_time";//4.2.2起添加

    public static final String COLUMN_DOWN_TEMP_ISUPDATE = "isUpdate";//4.4.0起添加

    public static final String COLUMN_DOWN_TEMP_PAGE_ID = "pageId";//4.5.0起添加


    /**
     * 用于4.0之后下载临时信息保存；
     */
    public static final String TABLE_DOWNLOAD_TEMP_INFO = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_DOWNLOAD_TEMP_INFO +

            " (" + "_ID INTEGER PRIMARY KEY," + COLUMN_DOWN_TEMP_GAME_ID + " TEXT UNIQUE," + COLUMN_DOWN_TEMP_PKG_NAME +
            " TEXT UNIQUE," + COLUMN_DOWN_TEMP_ACTION_TYPE + " TEXT," + COLUMN_DOWN_TEMP_DOWNLAOD_TYPE + " TEXT," +
            COLUMN_DOWN_TEMP_MPRESTATE + " INTEGER," + COLUMN_DOWN_TEMP_REMARK + " TEXT," + COLUMN_DOWN_TEMP_PAGE +
            " TEXT," + COLUMN_DOWN_TEMP_IS_DOWNLOAD + " TEXT," + COLUMN_DOWN_TEMP_POS + " INTEGER," + COLUMN_DOWN_TEMP_SUB_POS
            + " INTEGER," + COLUMN_DOWN_TEMP_DOWNLOAD_MODE + " TEXT," + COLUMN_DOWN_TEMP_INSTALL_TYPE +
            " TEXT," + COLUMN_DOWN_TEMP_INSTALL_MODE + " TEXT," + COLUMN_DOWN_TEMP_INSTALL_COUNT + " INTEGER,"
            + COLUMN_DOWN_TEMP_PRESENT_TYPE + " TEXT," + COLUMN_DOWN_TEMP_FROM_PAGE + " TEXT," + COLUMN_DOWN_TEMP_FROM_ID +
            " TEXT," + COLUMN_DOWN_TEMP_WORD + " TEXT," +COLUMN_DOWN_TEMP_ISUPDATE +" TEXT," +COLUMN_DOWN_TEMP_PAGE_ID +" TEXT,"+ COLUMN_DOWN_TEMP_INSTALLTIME+ " datetime," + " timestamp not null default (datetime('now','localtime')))";
    //********************************************************************************************


    public static final String TABLE_COMMUNITY_TEMPORARY = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_COMMUNITY_TEMPORARY + " (" + "_ID INTEGER PRIMARY KEY," + COLUMN_COMMUNITY_IS_VISIT + " INTEGER," + COLUMN_COMMUNITY_USER_ID + " INTEGER," + COLUMN_COMMUNITY_VISIT_TIME + " TEXT," + COLUMN_COMMUNITY_PAGE_NAME_LIST + " TEXT," + COLUMN_COMMUNITY_VISIT_COUNT + " INTEGER," + COLUMN_COMMUNITY_VISIT_DURATION_BY_SECOND + " INTEGER)";
    public static final String TABLE_COMMUNITY_FINAL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_COMMUNITY_FINAL + " (" + "_ID INTEGER PRIMARY KEY," + COLUMN_COMMUNITY_IS_VISIT + " INTEGER," + COLUMN_COMMUNITY_USER_ID + " INTEGER," + COLUMN_COMMUNITY_VISIT_TIME + " TEXT," + COLUMN_COMMUNITY_PAGE_NAME_LIST + " TEXT," + COLUMN_COMMUNITY_VISIT_COUNT + " INTEGER," + COLUMN_COMMUNITY_VISIT_DURATION_BY_SECOND + " INTEGER)";
    public static final String TABLE_RETRY_REPORT_DATA = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_RETRY_REPORT + " (" + "_ID INTEGER PRIMARY KEY," + COLUMN_FAIL_DATA + " TEXT)";
    //440新建统计容器表
    public static final String TABLE_REPORT_CONTAINER = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_REPORT_CONTAINER + " (" + "_ID INTEGER PRIMARY KEY," + COLUMN_EACH_DATA + " TEXT)";


    public StaisticsDbOperator() {
        initCreate();
        initUpgrade();
    }

    private void initUpgrade() {
        for (int i = 1; i <= mDbVersion; i++) {
        }
    }

    private void initCreate() {

        mCreate.add(TABLE_COMMUNITY_TEMPORARY);
        mCreate.add(TABLE_COMMUNITY_FINAL);
        mCreate.add(TABLE_DOWNLOAD_TEMP_INFO);
        mCreate.add(TABLE_RETRY_REPORT_DATA);
        mCreate.add(TABLE_REPORT_CONTAINER); //440添加
    }


    @Override
    public void create(SQLiteDatabase db) {
//        RetryRealReportDao.getInstance().init();
        for (String s : mCreate) {
            db.execSQL(s);
        }
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL(TABLE_COMMUNITY_TEMPORARY);
                db.execSQL(TABLE_COMMUNITY_FINAL);
                db.execSQL("drop table " + TABLE_NAME_TOPIC);
                db.execSQL("drop table " + TABLE_NAME_PATH);
                db.execSQL("drop table " + TABLE_NAME_ADS);
                db.execSQL("drop table " + TABLE_NAME_APPUPDATE);
                db.execSQL("drop table " + TABLE_NAME_GAMEDOWNLOAD);
                db.execSQL("drop table " + TABLE_NAME_DOWNLOAD_INFO);
                db.execSQL(TABLE_DOWNLOAD_TEMP_INFO);
                db.execSQL(TABLE_REPORT_CONTAINER);
                db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_ISUPDATE + " text");
                break;
            case 2:

                db.execSQL("drop table " + TABLE_NAME_TOPIC);
                db.execSQL("drop table " + TABLE_NAME_PATH);
                db.execSQL("drop table " + TABLE_NAME_ADS);
                db.execSQL("drop table " + TABLE_NAME_APPUPDATE);
                db.execSQL("drop table " + TABLE_NAME_GAMEDOWNLOAD);
                db.execSQL("drop table " + TABLE_NAME_DOWNLOAD_INFO);
                db.execSQL("drop table " + TABLE_NAME_ADS_MODEL);
                db.execSQL("drop table " + TABLE_NAME_SEARCH_DOWNLOAD_REQUEST);

                db.execSQL(TABLE_DOWNLOAD_TEMP_INFO);
                db.execSQL(TABLE_REPORT_CONTAINER);
                db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_ISUPDATE + " text");
                break;
            case 3:

                db.execSQL("drop table " + TABLE_NAME_TOPIC);
                db.execSQL("drop table " + TABLE_NAME_PATH);
                db.execSQL("drop table " + TABLE_NAME_ADS);
                db.execSQL("drop table " + TABLE_NAME_APPUPDATE);
                db.execSQL("drop table " + TABLE_NAME_GAMEDOWNLOAD);
                db.execSQL("drop table " + TABLE_NAME_DOWNLOAD_INFO);
                db.execSQL("drop table " + TABLE_NAME_ADS_MODEL);
                db.execSQL("drop table " + TABLE_NAME_SEARCH_DOWNLOAD_REQUEST);
                db.execSQL("drop table " + TABLE_NAME_GAMEACTIVE);
                db.execSQL("drop table " + TABLE_NAME_TEMPL);

                db.execSQL(TABLE_DOWNLOAD_TEMP_INFO);
                db.execSQL(TABLE_REPORT_CONTAINER);
                db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_ISUPDATE + " text");
                break;

            case 4:
                try {
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_IS_DOWNLOAD + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_ISUPDATE + " text");
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (e.getMessage().contains("no such table")) {
                        db.execSQL(TABLE_DOWNLOAD_TEMP_INFO);
                    }
                }
                db.execSQL(TABLE_REPORT_CONTAINER);
                break;
            case 5:
                try {//为了修复从低版本升级到4.0之后继续升级的数据库的异常；
                    db.query(TABLE_NAME_DOWNLOAD_TEMP_INFO, null, null, null, null, null, null);
                    LogUtils.i("StaisticsDbOperator", "修改统计表 5");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_POS + " integer");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_SUB_POS + " integer");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_DOWNLOAD_MODE + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_INSTALL_TYPE + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_INSTALL_MODE + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_INSTALL_COUNT + " integer");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_PRESENT_TYPE + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_FROM_PAGE + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_FROM_ID + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_WORD + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_INSTALLTIME + " datetime");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_ISUPDATE + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_PAGE_ID + " text");
                    db.execSQL(TABLE_RETRY_REPORT_DATA);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e.getMessage().contains("no such table")) {
                        db.execSQL(TABLE_DOWNLOAD_TEMP_INFO);
                    }
                }
                db.execSQL(TABLE_REPORT_CONTAINER);
                break;
            case 6:
                try {
                    LogUtils.i("StaisticsDbOperator", "修改统计表 6");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_POS + " integer");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_SUB_POS + " integer");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_DOWNLOAD_MODE + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_INSTALL_TYPE + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_INSTALL_MODE + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_INSTALL_COUNT + " integer");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_PRESENT_TYPE + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_FROM_PAGE + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_FROM_ID + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_WORD + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_INSTALLTIME + " datetime");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_ISUPDATE + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_PAGE_ID + " text");
                    db.execSQL(TABLE_RETRY_REPORT_DATA);
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (e.getMessage().contains("no such table")) {
                        db.execSQL(TABLE_DOWNLOAD_TEMP_INFO);
                    }
                }
                db.execSQL(TABLE_REPORT_CONTAINER);
                break;
            case 7:
                try {
                    LogUtils.i("StaisticsDbOperator", "修改统计表 7");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_FROM_PAGE + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_FROM_ID + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_WORD + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_INSTALLTIME + " datetime");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_ISUPDATE + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_PAGE_ID + " text");
                    db.execSQL(TABLE_RETRY_REPORT_DATA);
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (e.getMessage().contains("no such table")) {
                        db.execSQL(TABLE_DOWNLOAD_TEMP_INFO);
                    }
                }
                db.execSQL(TABLE_REPORT_CONTAINER);
                break;
            case 8:
                LogUtils.i("StaisticsDbOperator", "修改统计表 8");
                try {
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_INSTALLTIME + " datetime");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_ISUPDATE + " text");
                    db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_PAGE_ID + " text");
                    db.execSQL(TABLE_RETRY_REPORT_DATA);
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (e.getMessage().contains("no such table")) {
                        db.execSQL(TABLE_DOWNLOAD_TEMP_INFO);
                    }
                }
                db.execSQL(TABLE_REPORT_CONTAINER);
                break;
            case 9:
                LogUtils.i("StaisticsDbOperator", "修改统计表 9");
                db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_ISUPDATE + " text");
                db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_PAGE_ID + " text");
                db.execSQL(TABLE_REPORT_CONTAINER);
                break;

            case 10:
                LogUtils.i("StaisticsDbOperator", "修改统计表 10");
                db.execSQL("alter table " + TABLE_NAME_DOWNLOAD_TEMP_INFO + " add column " + COLUMN_DOWN_TEMP_PAGE_ID + " text");
                db.execSQL(TABLE_REPORT_CONTAINER);
                break;
        }
    }
}
