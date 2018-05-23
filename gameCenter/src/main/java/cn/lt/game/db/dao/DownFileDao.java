package cn.lt.game.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.db.DBHelper;
import cn.lt.game.db.factory.DownloadDBFactory;
import cn.lt.game.db.factory.IDBFactory;
import cn.lt.game.db.operation.DownloadDbOperator;
import cn.lt.game.download.DownloadState;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.util.threadpool.RequestTagManager;
import cn.lt.game.model.GameBaseDetail;

/**
 * The Class DownFileDao.
 */
public class DownFileDao {

    private SQLiteDatabase mDatabase;
    private String mTableName;
    private Context context;

    /**
     * Instantiates a new down file dao.
     *
     * @param context the context
     */
    public DownFileDao(Context context) {
        this.context = context;
        IDBFactory factory = new DownloadDBFactory();
        DBHelper helper = factory.getDB(context);
        mDatabase = helper.getWritableDatabase();
        mTableName = DownloadDbOperator.TABLE_NAME_DOWNLOAD;
    }

    /**
     * 获取已经下载的文件的信息.
     *
     * @param path the path
     * @return the down file
     */
    public GameBaseDetail getDownFile(String path) {
        Cursor cursor = null;
        GameBaseDetail mDownFile = null;
        try {
            String where = "DOWNURL = ?";
            String[] whereValue = {path};
            cursor = mDatabase.query(mTableName, null, where, whereValue, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                mDownFile = new GameBaseDetail();
                mDownFile.setId(getIntColumnValue("_ID", cursor));
                mDownFile.setLogoUrl(getStringColumnValue("LOGOURL", cursor));
                mDownFile.setName(getStringColumnValue("NAME", cursor));
                mDownFile.setPkgName(getStringColumnValue("PAKAGENAME", cursor));
                mDownFile.setDownUrl(getStringColumnValue("DOWNURL", cursor));
                mDownFile.setDownPath(getStringColumnValue("DOWNPATH", cursor));
                mDownFile.setState(getIntColumnValue("STATE", cursor));
                mDownFile.setDownLength(getIntColumnValue("DOWNLENGTH", cursor));
                mDownFile.setFileTotalLength(getIntColumnValue("TOTALLENGTH", cursor));
                mDownFile.setSuffix(getStringColumnValue("DOWNSUFFIX", cursor));
                mDownFile.setVersionCode(getIntColumnValue("VERSIONCODE", cursor));
                mDownFile.setVersion(getStringColumnValue("VERSION", cursor));
                mDownFile.setMd5(getStringColumnValue("MDFIVE", cursor));
                mDownFile.setUpdateContent(getStringColumnValue("UPDATECONTENT", cursor));
                mDownFile.setPrevState(getIntColumnValue("PREVSTATE", cursor));
                mDownFile.setForumId(getIntColumnValue("FORUM_ID", cursor));
                mDownFile.setHasGift(getIntColumnValue("HAS_GIFT", cursor) != 0);
                mDownFile.setHasStrategy(getIntColumnValue("HAS_STRATEGY", cursor) != 0);

                mDownFile.setMark(getStringColumnValue("MARK", cursor));
                mDownFile.setCategory(getStringColumnValue("CATEGORY", cursor));
                mDownFile.setReview(getStringColumnValue("REVIEWS", cursor));
                String scoreStr = getStringColumnValue("SCORE", cursor);
                try {
                    mDownFile.setScore(Float.parseFloat(scoreStr));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                mDownFile.setDownloadCnt(getStringColumnValue("DOWNLOAD_CNT", cursor));
                String pkgSizeStr = getStringColumnValue("PKG_SIZE", cursor);
                try {
                    mDownFile.setPkgSize(Long.parseLong(pkgSizeStr));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                mDownFile.setDownloadFailedReason(getStringColumnValue("DOWNLOAD_FAILD_REASON", cursor));

                // 4.1新加字段（预约wifi下载）
                mDownFile.setOrderWifiDownload(getIntColumnValue("IS_ORDER_WIFI_DOWNLOAD", cursor) > 0);
                // 4.2新添加字段（是否可覆盖游戏）
                mDownFile.isCoveredApp = getIntColumnValue("IS_COVERED_APP", cursor) > 0;
                //4.3新添加字段( // "is_accepted": "是否已领取积分","download_point": "下载积分",)
                mDownFile.setDownloadPoint(getIntColumnValue("DOWNLOAD_POINT", cursor));
                mDownFile.setAccepted(getIntColumnValue("IS_ACCEPTED", cursor));
                mDownFile.setDownloadFrom(getStringColumnValue("DOWNLOAD_FROM", cursor));
                // 是否可升级
                mDownFile.setCanUpgrade(getIntColumnValue("CAN_UPGRADE", cursor) > 0);
                mDownFile.setBusinessPackage(getIntColumnValue("BUSINESS_PACKAGE", cursor) > 0);
                //4.5 deeplink链接
                mDownFile.setDeeplink(getStringColumnValue("DEEPLINK", cursor));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeCursor(cursor);
        }
        return mDownFile;
    }


    /**
     * 获取已经下载的文件的信息.
     *
     * @param pkg the package
     * @return the down file
     */
    public GameBaseDetail getDownFileByPkg(String pkg) {
        Cursor cursor = null;
        GameBaseDetail mDownFile = null;
        try {
            String where = "PAKAGENAME = ?";
            String[] whereValue = {pkg};
            cursor = mDatabase.query(mTableName, null, where, whereValue, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                mDownFile = new GameBaseDetail();
                mDownFile.setId(getIntColumnValue("_ID", cursor));
                mDownFile.setLogoUrl(getStringColumnValue("LOGOURL", cursor));
                mDownFile.setName(getStringColumnValue("NAME", cursor));
                mDownFile.setPkgName(getStringColumnValue("PAKAGENAME", cursor));
                mDownFile.setDownUrl(getStringColumnValue("DOWNURL", cursor));
                mDownFile.setDownPath(getStringColumnValue("DOWNPATH", cursor));
                mDownFile.setState(getIntColumnValue("STATE", cursor));
                mDownFile.setDownLength(getIntColumnValue("DOWNLENGTH", cursor));
                mDownFile.setFileTotalLength(getIntColumnValue("TOTALLENGTH", cursor));
                mDownFile.setSuffix(getStringColumnValue("DOWNSUFFIX", cursor));
                mDownFile.setVersionCode(getIntColumnValue("VERSIONCODE", cursor));
                mDownFile.setVersion(getStringColumnValue("VERSION", cursor));
                mDownFile.setMd5(getStringColumnValue("MDFIVE", cursor));
                mDownFile.setUpdateContent(getStringColumnValue("UPDATECONTENT", cursor));
                mDownFile.setPrevState(getIntColumnValue("PREVSTATE", cursor));
                mDownFile.setForumId(getIntColumnValue("FORUM_ID", cursor));
                mDownFile.setHasGift(getIntColumnValue("HAS_GIFT", cursor) != 0);
                mDownFile.setHasStrategy(getIntColumnValue("HAS_STRATEGY", cursor) != 0);

                mDownFile.setMark(getStringColumnValue("MARK", cursor));
                mDownFile.setCategory(getStringColumnValue("CATEGORY", cursor));
                mDownFile.setReview(getStringColumnValue("REVIEWS", cursor));
                String scoreStr = getStringColumnValue("SCORE", cursor);
                try {
                    mDownFile.setScore(Float.parseFloat(scoreStr));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                mDownFile.setDownloadCnt(getStringColumnValue("DOWNLOAD_CNT", cursor));
                String pkgSizeStr = getStringColumnValue("PKG_SIZE", cursor);
                try {
                    mDownFile.setPkgSize(Long.parseLong(pkgSizeStr));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                mDownFile.setDownloadFailedReason(getStringColumnValue("DOWNLOAD_FAILD_REASON", cursor));

                // 4.1新加字段（预约wifi下载）
                mDownFile.setOrderWifiDownload(getIntColumnValue("IS_ORDER_WIFI_DOWNLOAD", cursor) > 0);
                // 4.2新添加字段（是否可覆盖游戏）
                mDownFile.isCoveredApp = getIntColumnValue("IS_COVERED_APP", cursor) > 0;
                //4.3新添加字段( // "is_accepted": "是否已领取积分","download_point": "下载积分",)
                mDownFile.setDownloadPoint(getIntColumnValue("DOWNLOAD_POINT", cursor));
                mDownFile.setAccepted(getIntColumnValue("IS_ACCEPTED", cursor));
                mDownFile.setDownloadFrom(getStringColumnValue("DOWNLOAD_FROM", cursor));
                // 是否可升级
                mDownFile.setCanUpgrade(getIntColumnValue("CAN_UPGRADE", cursor) > 0);
                mDownFile.setBusinessPackage(getIntColumnValue("BUSINESS_PACKAGE", cursor) > 0);

                //4.5 deeplink链接
                mDownFile.setDeeplink(getStringColumnValue("DEEPLINK", cursor));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeCursor(cursor);
        }
        return mDownFile;
    }

    /**
     * s 获取已经下载的文件的信息.
     * <p/>
     * the path
     *
     * @return the down file
     */
    public GameBaseDetail getDownFileById(int id) {
        Cursor cursor = null;
        GameBaseDetail mDownFile = null;
        try {
            String where = "_ID = ?";
            String[] whereValue = {id + ""};
            cursor = mDatabase.query(mTableName, null, where, whereValue, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                mDownFile = new GameBaseDetail();
                mDownFile.setId(getIntColumnValue("_ID", cursor));
                mDownFile.setLogoUrl(getStringColumnValue("LOGOURL", cursor));
                mDownFile.setName(getStringColumnValue("NAME", cursor));
                mDownFile.setPkgName(getStringColumnValue("PAKAGENAME", cursor));
                mDownFile.setDownUrl(getStringColumnValue("DOWNURL", cursor));
                mDownFile.setDownPath(getStringColumnValue("DOWNPATH", cursor));
                mDownFile.setState(getIntColumnValue("STATE", cursor));
                mDownFile.setDownLength(getIntColumnValue("DOWNLENGTH", cursor));
                mDownFile.setFileTotalLength(getIntColumnValue("TOTALLENGTH", cursor));
                mDownFile.setSuffix(getStringColumnValue("DOWNSUFFIX", cursor));
                mDownFile.setVersionCode(getIntColumnValue("VERSIONCODE", cursor));
                mDownFile.setVersion(getStringColumnValue("VERSION", cursor));
                mDownFile.setMd5(getStringColumnValue("MDFIVE", cursor));
                mDownFile.setUpdateContent(getStringColumnValue("UPDATECONTENT", cursor));
                mDownFile.setPrevState(getIntColumnValue("PREVSTATE", cursor));
                // 2.0新添加 游戏打开时间
                mDownFile.setOpenTime(getLongColumnValue("OPENTIME", cursor));
                mDownFile.setForumId(getIntColumnValue("FORUM_ID", cursor));
                mDownFile.setHasGift(getIntColumnValue("HAS_GIFT", cursor) != 0);
                mDownFile.setHasStrategy(getIntColumnValue("HAS_STRATEGY", cursor) != 0);

                mDownFile.setMark(getStringColumnValue("MARK", cursor));
                mDownFile.setCategory(getStringColumnValue("CATEGORY", cursor));
                mDownFile.setReview(getStringColumnValue("REVIEWS", cursor));
                String scoreStr = getStringColumnValue("SCORE", cursor);
                try {
                    mDownFile.setScore(Float.parseFloat(scoreStr));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                mDownFile.setDownloadCnt(getStringColumnValue("DOWNLOAD_CNT", cursor));
                String pkgSizeStr = getStringColumnValue("PKG_SIZE", cursor);
                try {
                    mDownFile.setPkgSize(Long.parseLong(pkgSizeStr));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                mDownFile.setDownloadFailedReason(getStringColumnValue("DOWNLOAD_FAILD_REASON", cursor));

                // 4.1新加字段（预约wifi下载）
                mDownFile.setOrderWifiDownload(getIntColumnValue("IS_ORDER_WIFI_DOWNLOAD", cursor) > 0);
                // 4.2新添加字段（是否可覆盖游戏）
                mDownFile.isCoveredApp = getIntColumnValue("IS_COVERED_APP", cursor) > 0;
                //4.3新添加字段( // "is_accepted": "是否已领取积分","download_point": "下载积分",)
                mDownFile.setDownloadPoint(getIntColumnValue("DOWNLOAD_POINT", cursor));
                mDownFile.setAccepted(getIntColumnValue("IS_ACCEPTED", cursor));
                mDownFile.setDownloadFrom(getStringColumnValue("DOWNLOAD_FROM", cursor));
                // 是否可升级
                mDownFile.setCanUpgrade(getIntColumnValue("CAN_UPGRADE", cursor) > 0);
                mDownFile.setBusinessPackage(getIntColumnValue("BUSINESS_PACKAGE", cursor) > 0);

                //4.5 deeplink链接
                mDownFile.setDeeplink(getStringColumnValue("DEEPLINK", cursor));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeCursor(cursor);
        }
        return mDownFile;
    }

    /**
     * 获取安装、下载中、待升级、待安装的游戏；
     * <p/>
     * the path
     *
     * @return the down file
     */
    public List<GameBaseDetail> getInProgressDownFile() {
        Cursor cursor = null;
        List<GameBaseDetail> datas = null;
        try {
            String where = " STATE!= ? and STATE != ?";
            String[] whereValue = {DownloadState.downloadFail + "", InstallState.installFail + ""};
            cursor = mDatabase.query(mTableName, null, where, whereValue, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                datas = new ArrayList<GameBaseDetail>();
                while (cursor.moveToNext()) {
                    GameBaseDetail mDownFile = new GameBaseDetail();
                    mDownFile.setId(getIntColumnValue("_ID", cursor));
                    mDownFile.setLogoUrl(getStringColumnValue("LOGOURL", cursor));
                    mDownFile.setName(getStringColumnValue("NAME", cursor));
                    mDownFile.setPkgName(getStringColumnValue("PAKAGENAME", cursor));
                    mDownFile.setDownUrl(getStringColumnValue("DOWNURL", cursor));
                    mDownFile.setDownPath(getStringColumnValue("DOWNPATH", cursor));
                    mDownFile.setState(getIntColumnValue("STATE", cursor));
                    mDownFile.setDownLength(getIntColumnValue("DOWNLENGTH", cursor));
                    mDownFile.setFileTotalLength(getIntColumnValue("TOTALLENGTH", cursor));
                    mDownFile.setSuffix(getStringColumnValue("DOWNSUFFIX", cursor));
                    mDownFile.setVersionCode(getIntColumnValue("VERSIONCODE", cursor));
                    mDownFile.setVersion(getStringColumnValue("VERSION", cursor));
                    mDownFile.setMd5(getStringColumnValue("MDFIVE", cursor));
                    mDownFile.setUpdateContent(getStringColumnValue("UPDATECONTENT", cursor));
                    mDownFile.setPrevState(getIntColumnValue("PREVSTATE", cursor));
                    // 2.0新添加 游戏打开时间
                    mDownFile.setOpenTime(getLongColumnValue("OPENTIME", cursor));
                    mDownFile.setForumId(getIntColumnValue("FORUM_ID", cursor));
                    mDownFile.setHasGift(getIntColumnValue("HAS_GIFT", cursor) != 0);
                    mDownFile.setHasStrategy(getIntColumnValue("HAS_STRATEGY", cursor) != 0);

                    mDownFile.setMark(getStringColumnValue("MARK", cursor));
                    mDownFile.setCategory(getStringColumnValue("CATEGORY", cursor));
                    mDownFile.setReview(getStringColumnValue("REVIEWS", cursor));
                    String scoreStr = getStringColumnValue("SCORE", cursor);
                    try {
                        mDownFile.setScore(Float.parseFloat(scoreStr));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    mDownFile.setDownloadCnt(getStringColumnValue("DOWNLOAD_CNT", cursor));
                    String pkgSizeStr = getStringColumnValue("PKG_SIZE", cursor);
                    try {
                        mDownFile.setPkgSize(Long.parseLong(pkgSizeStr));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    mDownFile.setDownloadFailedReason(getStringColumnValue("DOWNLOAD_FAILD_REASON", cursor));

                    // 4.1新加字段（预约wifi下载）
                    mDownFile.setOrderWifiDownload(getIntColumnValue("IS_ORDER_WIFI_DOWNLOAD", cursor) > 0);
                    // 4.2新添加字段（是否可覆盖游戏）
                    mDownFile.isCoveredApp = getIntColumnValue("IS_COVERED_APP", cursor) > 0;
                    //4.3新添加字段( // "is_accepted": "是否已领取积分","download_point": "下载积分",)
                    mDownFile.setDownloadPoint(getIntColumnValue("DOWNLOAD_POINT", cursor));
                    mDownFile.setAccepted(getIntColumnValue("IS_ACCEPTED", cursor));
                    mDownFile.setDownloadFrom(getStringColumnValue("DOWNLOAD_FROM", cursor));
                    // 是否在白名单
                    mDownFile.setCanUpgrade(getIntColumnValue("CAN_UPGRADE", cursor) > 0);
                    mDownFile.setBusinessPackage(getIntColumnValue("BUSINESS_PACKAGE", cursor) > 0);

                    //4.5 deeplink链接
                    mDownFile.setDeeplink(getStringColumnValue("DEEPLINK", cursor));

                    datas.add(mDownFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeCursor(cursor);
        }
        return datas;
    }

    /**
     * 获取所有已经下载的文件的信息.
     * <p/>
     * the path
     *
     * @return the down file
     */
    public List<GameBaseDetail> getDownFiles() {
        Cursor cursor = null;
        List<GameBaseDetail> downFiles = null;
        try {
            cursor = mDatabase.query(mTableName, null, null, null, null, null, null);
            if (cursor != null) {
                downFiles = new ArrayList<>();
                while (cursor.moveToNext()) {
                    GameBaseDetail downItem = new GameBaseDetail();
                    downItem.setId(getIntColumnValue("_ID", cursor));
                    downItem.setLogoUrl(getStringColumnValue("LOGOURL", cursor));
                    downItem.setName(getStringColumnValue("NAME", cursor));
                    downItem.setPkgName(getStringColumnValue("PAKAGENAME", cursor));
                    downItem.setDownUrl(getStringColumnValue("DOWNURL", cursor));
                    downItem.setDownPath(getStringColumnValue("DOWNPATH", cursor));
                    downItem.setState(getIntColumnValue("STATE", cursor));
                    downItem.setDownLength(getIntColumnValue("DOWNLENGTH", cursor));
                    downItem.setFileTotalLength(getIntColumnValue("TOTALLENGTH", cursor));
                    downItem.setSuffix(getStringColumnValue("DOWNSUFFIX", cursor));
                    downItem.setVersionCode(getIntColumnValue("VERSIONCODE", cursor));
                    downItem.setVersion(getStringColumnValue("VERSION", cursor));
                    downItem.setMd5(getStringColumnValue("MDFIVE", cursor));
                    downItem.setUpdateContent(getStringColumnValue("UPDATECONTENT", cursor));
                    downItem.setPrevState(getIntColumnValue("PREVSTATE", cursor));
                    // 2.0新添加 游戏打开时间
                    downItem.setOpenTime(getLongColumnValue("OPENTIME", cursor));
                    downItem.setForumId(getIntColumnValue("FORUM_ID", cursor));
                    downItem.setHasGift(getIntColumnValue("HAS_GIFT", cursor) != 0);
                    downItem.setHasStrategy(getIntColumnValue("HAS_STRATEGY", cursor) != 0);

                    downItem.setMark(getStringColumnValue("MARK", cursor));
                    downItem.setCategory(getStringColumnValue("CATEGORY", cursor));
                    downItem.setReview(getStringColumnValue("REVIEWS", cursor));
                    String scoreStr = getStringColumnValue("SCORE", cursor);
                    try {
                        downItem.setScore(Float.parseFloat(scoreStr));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    downItem.setDownloadCnt(getStringColumnValue("DOWNLOAD_CNT", cursor));
                    String pkgSizeStr = getStringColumnValue("PKG_SIZE", cursor);
                    try {
                        downItem.setPkgSize(Long.parseLong(pkgSizeStr));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    downItem.setDownloadFailedReason(getStringColumnValue("DOWNLOAD_FAILD_REASON", cursor));

                    // 4.1新加字段（预约wifi下载）
                    downItem.setOrderWifiDownload(getIntColumnValue("IS_ORDER_WIFI_DOWNLOAD", cursor) > 0);
                    // 4.2新添加字段（是否可覆盖游戏）
                    downItem.isCoveredApp = getIntColumnValue("IS_COVERED_APP", cursor) > 0;
                    //4.3新添加字段( // "is_accepted": "是否已领取积分","download_point": "下载积分",)
                    downItem.setDownloadPoint(getIntColumnValue("DOWNLOAD_POINT", cursor));
                    downItem.setAccepted(getIntColumnValue("IS_ACCEPTED", cursor));
                    downItem.setDownloadFrom(getStringColumnValue("DOWNLOAD_FROM", cursor));
                    // 是否在白名单
                    downItem.setCanUpgrade(getIntColumnValue("CAN_UPGRADE", cursor) > 0);
                    downItem.setBusinessPackage(getIntColumnValue("BUSINESS_PACKAGE", cursor) > 0);
                    //4.5 deeplink链接
                    downItem.setDeeplink(getStringColumnValue("DEEPLINK", cursor));

                    downFiles.add(downItem);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeCursor(cursor);
        }
        return downFiles;
    }

    /**
     * 保存线程已经下载的文件信息.
     *
     * @param mDownFile the m down file
     * @return the long
     */
    public long save(GameBaseDetail mDownFile) {
        long row = 0;
        try {
            ContentValues cv = new ContentValues();
            cv.put("_ID", mDownFile.getId());
            cv.put("LOGOURL", mDownFile.getLogoUrl());
            cv.put("NAME", mDownFile.getName());
            cv.put("PAKAGENAME", mDownFile.getPkgName());
            cv.put("DOWNURL", mDownFile.getDownUrl());
            cv.put("DOWNPATH", mDownFile.getDownPath());
            cv.put("STATE", mDownFile.getState());
            cv.put("DOWNLENGTH", mDownFile.getDownLength());
            cv.put("TOTALLENGTH", mDownFile.getFileTotalLength());
            cv.put("DOWNSUFFIX", mDownFile.getSuffix());
            cv.put("VERSIONCODE", mDownFile.getVersionCode());
            cv.put("VERSION", mDownFile.getVersion());
            cv.put("MDFIVE", mDownFile.getMd5());
            cv.put("UPDATECONTENT", mDownFile.getUpdateContent());
            cv.put("PREVSTATE", mDownFile.getPrevState());
            cv.put("FORUM_ID", mDownFile.getForumId());

            //4.0新添加字段
            cv.put("HAS_STRATEGY", mDownFile.hasStrategy());
            cv.put("HAS_GIFT", mDownFile.hasGift() ? 1 : 0);

            cv.put("MARK", mDownFile.getMark());
            cv.put("CATEGORY", mDownFile.getCategory());
            cv.put("REVIEWS", mDownFile.getReview());
            cv.put("SCORE", mDownFile.getScore() + "");
            cv.put("DOWNLOAD_CNT", mDownFile.getDownloadCnt());
            cv.put("PKG_SIZE", mDownFile.getPkgSize() + "");
            cv.put("DOWNLOAD_FAILD_REASON", mDownFile.getDownloadFailedReason() == null ? "" : mDownFile.getDownloadFailedReason());

            // 4.1新添加字段（预约WiFi下载）
            cv.put("IS_ORDER_WIFI_DOWNLOAD", mDownFile.isOrderWifiDownload());
            // 4.2新添加字段（是否可覆盖游戏）
            cv.put("IS_COVERED_APP", mDownFile.isCoveredApp);
            //4.3新添加字段( // "is_accepted": "是否已领取积分","download_point": "下载积分",)
            cv.put("DOWNLOAD_POINT", mDownFile.getDownloadPoint());
            cv.put("IS_ACCEPTED", mDownFile.isAccepted());
            cv.put("DOWNLOAD_FROM",mDownFile.getDownloadFrom());

            cv.put("CAN_UPGRADE", mDownFile.canUpgrade());
            cv.put("BUSINESS_PACKAGE", mDownFile.isBusinessPackage());

            //4.5
            cv.put("DEEPLINK",mDownFile.getDeeplink());

            row = mDatabase.insert(mTableName, null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return row;
    }

    /**
     * 实时更新线程已经下载的文件长度.
     *
     * @param mDownFile the m down file
     * @return the long
     */
    public long updateById(GameBaseDetail mDownFile) {
        long row = -1;
        try {
            String where = "_ID = ? ";
            String[] whereValue = {String.valueOf(mDownFile.getId())};
            ContentValues cv = new ContentValues();
            cv.put("LOGOURL", mDownFile.getLogoUrl());
            cv.put("STATE", mDownFile.getState());
            cv.put("NAME", mDownFile.getName());
            cv.put("PREVSTATE", mDownFile.getPrevState());
            cv.put("DOWNLENGTH", mDownFile.getDownLength());
            cv.put("TOTALLENGTH", mDownFile.getFileTotalLength());
            cv.put("DOWNPATH", mDownFile.getDownPath());
            cv.put("VERSIONCODE", mDownFile.getVersionCode());
            cv.put("VERSION", mDownFile.getVersion());
            cv.put("UPDATECONTENT", mDownFile.getUpdateContent());
            cv.put("DOWNURL", mDownFile.getDownUrl());
            if (mDownFile.isMd5Valid()) {
                cv.put("MDFIVE", mDownFile.getMd5());
            }

            // ***dada 此处用来处理当游戏下载完成或者下载失败后删除所有统计相关记录信息；
            if (mDownFile.getState() == DownloadState.downloadComplete || mDownFile.getState() ==
                    DownloadState.downloadFail) {
                cv.put("topic_id", 0);
            }
            // ***dada <----
            //4.0新添加字段
            cv.put("HAS_STRATEGY", mDownFile.hasStrategy() ? 1 : 0);
            cv.put("HAS_GIFT", mDownFile.hasGift() ? 1 : 0);

            cv.put("MARK", mDownFile.getMark());
            cv.put("CATEGORY", mDownFile.getCategory());
            cv.put("REVIEWS", mDownFile.getReview());
            cv.put("SCORE", mDownFile.getScore() + "");
            cv.put("DOWNLOAD_CNT", mDownFile.getDownloadCnt());
            cv.put("PKG_SIZE", mDownFile.getPkgSize() + "");

            cv.put("DOWNLOAD_FAILD_REASON", mDownFile.getDownloadFailedReason() == null ? "" : mDownFile.getDownloadFailedReason());

            // 4.1新添加字段（预约WiFi下载）
            cv.put("IS_ORDER_WIFI_DOWNLOAD", mDownFile.isOrderWifiDownload());
            // 4.2新添加字段（是否可覆盖游戏）
            cv.put("IS_COVERED_APP", mDownFile.isCoveredApp);
            //4.3新添加字段( // "is_accepted": "是否已领取积分","download_point": "下载积分",)
            cv.put("DOWNLOAD_POINT", mDownFile.getDownloadPoint());
            cv.put("IS_ACCEPTED", mDownFile.isAccepted());
            cv.put("DOWNLOAD_FROM",mDownFile.getDownloadFrom());
            cv.put("CAN_UPGRADE", mDownFile.canUpgrade());
            cv.put("BUSINESS_PACKAGE", mDownFile.isBusinessPackage());

            //4.5
            cv.put("DEEPLINK",mDownFile.getDeeplink());

            row = mDatabase.update(mTableName, cv, where, whereValue);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return row;
    }

    public long updateOpenTimeByPackName(String packName, long openTime) {
        long row = -1;
        try {
            String where = "PAKAGENAME = ? ";
            String[] whereValue = {packName};
            ContentValues cv = new ContentValues();
            cv.put("OPENTIME", openTime);
            row = mDatabase.update(mTableName, cv, where, whereValue);
            // System.out.println(row +"数据库");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return row;
    }


    /**
     * 删除对应的下载记录.
     * <p/>
     * the path
     *
     * @return the long
     */
    public long delete(int id, boolean isFromSave) {
        long row = -1;
        try {
            String where = "_ID = ? ";
            String[] whereValue = {String.valueOf(id)};
            row = mDatabase.delete(mTableName, where, whereValue);
            //TODO SP 复位
            if(!isFromSave)RequestTagManager.deleteRequestTag(context,String.valueOf(id));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return row;
    }

    /**
     * 得到列值.
     *
     * @param columnName the column name
     * @param cursor     the cursor
     * @return the string column value
     */
    public String getStringColumnValue(String columnName, Cursor cursor) {
        String columnValue = cursor.getString(cursor.getColumnIndex(columnName));
        if (columnValue == null) {
            return "";
        }
        return columnValue;
    }

    /**
     * 得到列值.
     *
     * @param columnName the column name
     * @param cursor     the cursor
     * @return the int column value
     */
    public int getIntColumnValue(String columnName, Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    public long getLongColumnValue(String columnName, Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndex(columnName));
    }

    public void close() {
        try {
            mDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 描述：关闭游标.
     *
     * @param cursor the cursor
     */
    public void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    public long updateDeeplinkByPkg(String packName, String deeplink) {
        long row = -1;
        try {
            String where = "PAKAGENAME = ? ";
            String[] whereValue = {packName};
            ContentValues cv = new ContentValues();
            cv.put("DEEPLINK", deeplink);
            row = mDatabase.update(mTableName, cv, where, whereValue);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return row;
    }
}
