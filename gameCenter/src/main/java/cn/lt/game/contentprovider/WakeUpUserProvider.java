package cn.lt.game.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import cn.lt.game.db.DBHelper;
import cn.lt.game.db.factory.IDBFactory;
import cn.lt.game.db.factory.WakeUpSilenceUserFactory;
import cn.lt.game.db.operation.WakeUpSilenceUserDbOperator;

/**
 * Created by LinJunSheng on 2016/11/24.
 */

public class WakeUpUserProvider extends ContentProvider {

    public static final int FIRST_START_TIME = 1;
    public static final int FIRST_DOWNLOAD_TIME = 2;

    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI("cn.lt.game.WakeUpUserProvider", "firstStartTime", FIRST_START_TIME);
        MATCHER.addURI("cn.lt.game.WakeUpUserProvider", "firstDownloadTime", FIRST_DOWNLOAD_TIME);
    }

    private SQLiteDatabase mDatabase;

    @Override
    public boolean onCreate() {
        initDataBase();
        return true;
    }

    private synchronized void initDataBase() {
        IDBFactory factory = new WakeUpSilenceUserFactory();
        DBHelper helper = factory.getDB(getContext());
        mDatabase = helper.getWritableDatabase();
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c;
        switch (MATCHER.match(uri)) {
            case FIRST_START_TIME:
                c = mDatabase.query(WakeUpSilenceUserDbOperator.TABLE_NAME, null, selection, selectionArgs, null, null, null);
                break;
            case FIRST_DOWNLOAD_TIME:
                c = mDatabase.query(WakeUpSilenceUserDbOperator.TABLE_NAME, null, selection, selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (MATCHER.match(uri)) {
            case FIRST_START_TIME:
                mDatabase.insert(WakeUpSilenceUserDbOperator.TABLE_NAME, null, values);
                break;
            case FIRST_DOWNLOAD_TIME:
                mDatabase.insert(WakeUpSilenceUserDbOperator.TABLE_NAME, null, values);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
