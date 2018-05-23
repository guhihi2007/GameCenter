package cn.lt.game.db.operation;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2015/10/28.
 */
public class FavoriteDbOperator extends AbstractDBOperator {


    public static final int GameDetailTag = 0;
    public static final int GameinfomationTag = 2;
    public static final int GameNewsTag = 3;
    public static final int GameStrategyTag = 1;
    public static final String DBNAME = "favorite";
    public static final int DATABASE_VERSION = 5;
    public static final String GAMENOTSAVE = "";
    public static final String DRAFT_TABLE_NAME = "draftTable";
    public static final String GAMEDETAIL_TABLE_NAME = "gameDetailTable";
    public static final String GAMESTRATEGU_TABLE_NAME = "gameStrategyTable";
    public static final String GAMEINFORMATION_TABLE_NAME = "gameInformationTable";
    public static final String GAMENEW_TABLE_NAME = "gameNewTable";
    // 任务统计表
    public static final String GAMESERVICETASK_TABLE_NAME = "gameServiceTaskTable";
    // 下载统计表
    public static final String GAMEDOWLOADMSG_TABLE_NAME = "gameDownMsgTable";
    public static final String[] TABLEARRAYS = {GAMEDETAIL_TABLE_NAME,
            GAMESTRATEGU_TABLE_NAME, GAMEINFORMATION_TABLE_NAME,
            GAMENEW_TABLE_NAME};
    public static final String _id = "_id";
    public static final String ID = "Id";
    public static final String PARENTID = "parentId";
    public static final String NAME = "Name";
    public static final String CATEGORY = "Category";
    public static final String SIZE = "Size";
    public static final String LOGO_URL = "Url";
    public static final String LOCAL_PATH = "Path";
    public static final String PACKAGE = "package";
    public static final String TITLE = "title";
    public static final String TIME = "time";
    public static final String CONTONT = "arrayList";
    public static final String CATEGORYTAG = "CATEGORYID";
    public final static int pageSize = 10;//每页显示记录数
    // 优先级,此字段预留
    public static final String PRIORITY = "priority";
    // 1、全网络状态；2、流量；3、wifi状态
    public static final String NETWORKCATEGORY = "networkCategory";
    // 是否是紧急状况
    public static final String ISURGENT = "isUrgent";
    // 定时执行，不定时则为0
    public static final String TIMING = "timing";
    // 最后执行时间，超过了就不执行，不定时则为0
    public static final String LASTTIMING = "lastTiming";
    // 循环时间,不循环则为0
    public static final String LOOPTIME = "loopTime";
    // 任务类型:1、预下载；2、发送统计数据；3、统计应用使用频率
    public static final String TASKTYPE = "taskType";
    public static final String CURRENTTIME = "currenTime";
    public static final String DATAOBJ = "dataobj";
    public static final String DOWNLOADTAG = "downloadtag";
    // 话题相关
    public static final String TOPIC = "topic";
    public static final String GROUP_ID = "group_id";
    private final String createGameDetailTable = "CREATE TABLE "
            + GAMEDETAIL_TABLE_NAME + " (" + _id
            + " INTEGER primary key autoincrement, " + ID + " INTEGER," + NAME
            + " text," + CATEGORY + " text," + SIZE + " text," + LOGO_URL
            + " text," + LOCAL_PATH + " text," + PACKAGE + " text," + TITLE
            + " text," + TIME + " text," + CATEGORYTAG + " INTEGER)";
    private final String createGameStrategyTable = "CREATE TABLE "
            + GAMESTRATEGU_TABLE_NAME + " (" + _id
            + " INTEGER primary key autoincrement, " + ID + " INTEGER," + NAME
            + " text," + CATEGORY + " text," + SIZE + " text," + LOGO_URL
            + " text," + LOCAL_PATH + " text," + PACKAGE + " text," + TITLE
            + " text," + TIME + " text," + CATEGORYTAG + " INTEGER)";
    private final String createGameInformationTable = "CREATE TABLE "
            + GAMEINFORMATION_TABLE_NAME + " (" + _id
            + " INTEGER primary key autoincrement, " + ID + " INTEGER," + NAME
            + " text," + CATEGORY + " text," + SIZE + " text," + LOGO_URL
            + " text," + LOCAL_PATH + " text," + PACKAGE + " text," + TITLE
            + " text," + TIME + " text," + CATEGORYTAG + " INTEGER)";
    private final String createGameNewTable = "CREATE TABLE "
            + GAMENEW_TABLE_NAME + " (" + _id
            + " INTEGER primary key autoincrement, " + ID + " INTEGER," + NAME
            + " text," + CATEGORY + " text," + SIZE + " text," + LOGO_URL
            + " text," + LOCAL_PATH + " text," + PACKAGE + " text," + TITLE
            + " text," + TIME + " text," + CATEGORYTAG + " INTEGER)";
    // 任务储存表
    private final String createGameServiceTaskTable = "CREATE TABLE "
            + GAMESERVICETASK_TABLE_NAME + "(" + _id
            + " INTEGER primary key autoincrement, " + PRIORITY + " INTEGER,"
            + NETWORKCATEGORY + " INTEGER," + ISURGENT + " INTEGER," + TIMING
            + " text," + LOOPTIME + " text," + LASTTIMING + " text," + TASKTYPE
            + " INTEGER)";
    /***
     * 创建草稿箱表
     */
    private final String createGraftTable = "CREATE TABLE "
            + DRAFT_TABLE_NAME + "("
            + "_id" + " INTEGER primary key autoincrement, "
            + " tag " + " text,"
            + " type " + " INTEGER,"
            + " state " + " INTEGER,"
            + " group_id " + " INTEGER,"
            + " topic_Id " + " INTEGER,"
            + " comment_id " + " INTEGER,"
            + " acceptor_id " + " INTEGER,"
            + " topic_title " + " text,"
            + " groupTitle " + " text,"
            + " userID " + " text,"
            + " topic_content " + " text,"
            + " acceptorNickname" + " text,"
            + " category_id" + " text,"
            + " comment_content" + " text,"
            + " reply_content" + " text,"
            + " topic_paths" + " text,"
            + " comment_paths" + " text,"
            + " categoryList" + " text,"
            + " local_topicPaths" + " text,"
            + " local_topicContent" + " text,"
            + " local_commentContent" + " text,"
            + " local_replyContent" + " text)";
    private final String createGameDownStatisticsTable = "CREATE TABLE "
            + GAMEDOWLOADMSG_TABLE_NAME + "(" + _id
            + " INTEGER primary key autoincrement, " + DATAOBJ + " text,"
            + CURRENTTIME + " text," + DOWNLOADTAG + " INTEGER)";

    @Override
    public void create(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(createGameDetailTable);
        db.execSQL(createGameStrategyTable);
        db.execSQL(createGameInformationTable);
        db.execSQL(createGameNewTable);
        db.execSQL(createGameServiceTaskTable);
        db.execSQL(createGameDownStatisticsTable);
//		db.execSQL(createSendToPICTable);
        db.execSQL(createGraftTable);
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("alter table Favorite rename to "
                        + GAMEDETAIL_TABLE_NAME);
                db.execSQL("alter table " + GAMEDETAIL_TABLE_NAME + " add column "
                        + PACKAGE + " text");
                db.execSQL("alter table " + GAMEDETAIL_TABLE_NAME + " add column "
                        + TITLE + " text");
                db.execSQL("alter table " + GAMEDETAIL_TABLE_NAME + " add column "
                        + TIME + " text");
                db.execSQL("alter table " + GAMEDETAIL_TABLE_NAME + " add column "
                        + CATEGORYTAG + " integer");
                db.execSQL(createGameStrategyTable);
                db.execSQL(createGameInformationTable);
                db.execSQL(createGameNewTable);
                db.execSQL(createGameServiceTaskTable);
                db.execSQL(createGameDownStatisticsTable);
                db.execSQL(createGraftTable);
                break;
            case 2:
                db.execSQL(createGraftTable);
                break;
            case 3:
                db.execSQL(createGraftTable);
                break;
            case 4:
                db.execSQL(createGraftTable);
                break;

            default:
                break;
        }
    }
}
