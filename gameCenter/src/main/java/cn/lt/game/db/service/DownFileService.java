package cn.lt.game.db.service;

import android.content.Context;

import java.util.List;

import cn.lt.game.db.dao.DownFileDao;
import cn.lt.game.db.dao.DraftDao;
import cn.lt.game.model.GameBaseDetail;


/**
 * The Class DownFileDao.
 */
public class DownFileService {

    /**
     * The m context.
     */
    public static Context mContext = null;
    public static DownFileService service;

    /**
     * Instantiates a new down file dao.
     *
     * @param context the context
     */
    public DownFileService(Context context) {
        mContext = context;
    }

    /**
     * Gets the single instance of DownFileDao.
     *
     * @param context the context
     * @return single instance of DownFileDao
     */
    public static DownFileService getInstance(Context context) {
        if (service == null) {
            synchronized (DraftDao.class) {
                if (service == null) {
                    service = new DownFileService(context);
                }
            }
        }
        return service;
    }

    /**
     * 获取已经下载的文件的信息.
     *
     * @param path the path
     * @return the down file
     */
    public GameBaseDetail getDownFile(String path) {
        DownFileDao dao = null;
        GameBaseDetail mDownFile = null;
        try {
            dao = new DownFileDao(mContext);
            mDownFile = dao.getDownFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return mDownFile;
    }

    /**
     * s 获取已经下载的文件的信息.
     *
     * @return the down file
     */
    public GameBaseDetail getDownFileById(int id) {
        DownFileDao dao = null;
        GameBaseDetail mDownFile = null;
        try {
            dao = new DownFileDao(mContext);
            mDownFile = dao.getDownFileById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return mDownFile;
    }

    /**
     * s 获取已经下载的文件的信息.
     *
     * @return the down file
     */
    public GameBaseDetail getDownFileByPkg(String pkg) {
        DownFileDao dao = null;
        GameBaseDetail mDownFile = null;
        try {
            dao = new DownFileDao(mContext);
            mDownFile = dao.getDownFileByPkg(pkg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return mDownFile;
    }




    /**
     * 获取安装、下载中、待升级、待安装的游戏；
     *
     * @return the down file
     */
    public synchronized List<GameBaseDetail> getInProgressDownFile() {

        DownFileDao dao = null;
        List<GameBaseDetail> datas = null;
        try {
            dao = new DownFileDao(mContext);
            datas = dao.getInProgressDownFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return datas;
    }

    /**
     * 获取所有已经下载的文件的信息.
     *
     * @return the down file
     */
    public synchronized List<GameBaseDetail> getDownFiles() {
        DownFileDao dao = null;
        List<GameBaseDetail> downFiles = null;
        try {
            dao = new DownFileDao(mContext);
            downFiles = dao.getDownFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return downFiles;
    }

    /**
     * 保存线程已经下载的文件信息.
     *
     * @param mDownFile the m down file
     * @return the long
     */
    public synchronized long save(GameBaseDetail mDownFile) {
        DownFileDao dao = null;
        long row = -1;
        try {
            dao = new DownFileDao(mContext);
            dao.delete(mDownFile.getId(),true);
            row = dao.save(mDownFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return row;
    }

    /**
     * 实时更新线程已经下载的文件长度.
     *
     * @param mDownFile the m down file
     * @return the long
     */
    public synchronized long updateById(GameBaseDetail mDownFile) {
        DownFileDao dao = null;
        long row = -1;
        try {
            dao = new DownFileDao(mContext);
            row = dao.updateById(mDownFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return row;
    }

    public synchronized long updateOpenTimeByPackName(String packName, long openTime) {
        DownFileDao dao = null;
        long row = -1;
        try {
            dao = new DownFileDao(mContext);
            row = dao.updateOpenTimeByPackName(packName, openTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
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
    public synchronized long delete(int id) {
        DownFileDao dao = null;
        long row = -1;
        try {
            dao = new DownFileDao(mContext);
            row = dao.delete(id, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return row;
    }

    public synchronized long updateDeeplinkByPkg(String packName, String deeplink) {
        DownFileDao dao = null;
        long row = -1;
        try {
            dao = new DownFileDao(mContext);
            row = dao.updateDeeplinkByPkg(packName, deeplink);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return row;
    }

}
