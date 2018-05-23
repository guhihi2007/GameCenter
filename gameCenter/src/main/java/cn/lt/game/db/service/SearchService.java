package cn.lt.game.db.service;

import android.content.Context;

import java.util.List;

import cn.lt.game.db.dao.SearchDao;

/**
 * 搜索功能做历史缓存数据库增加、删除、查询
 */
public class SearchService {

    private static SearchService searchService;
    private Context mContext;

    public SearchService(Context context) {
        mContext = context;
    }

    public static SearchService getInstance(Context context) {
        if (searchService == null) {
            synchronized (SearchService.class) {
                if (searchService == null) {
                    searchService = new SearchService(context);
                }
            }
        }
        return searchService;

    }

    // 保存用户搜索的关键字
    public synchronized void save(String keyword) {
        SearchDao dao = null;
        try {
            dao = new SearchDao(mContext);
            if (dao.findOne(keyword)) {
                dao.deleteOne(keyword);
                dao.add(keyword);
            } else {
                dao.add(keyword);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
    }

    /**
     * 增加
     */
    public synchronized void add(String appName) {
        SearchDao dao = null;
        try {
            dao = new SearchDao(mContext);
            dao.add(appName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
    }

    /**
     * 删除一个
     */
    public synchronized void deleteOne(String appName) {
        SearchDao dao = null;
        try {
            dao = new SearchDao(mContext);
            dao.deleteOne(appName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
    }

    /**
     * 删除全部
     */
    public synchronized void deleteAll() {
        SearchDao dao = null;
        try {
            dao = new SearchDao(mContext);
            dao.deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
    }


    /**
     * 查所有
     */
    public synchronized List<String> findAll(String keyword) {
        SearchDao dao = null;
        List<String> allSearch = null;
        try {
            dao = new SearchDao(mContext);
            allSearch = dao.findAll(keyword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return allSearch;
    }

    /**
     * 查存在
     */
    public synchronized boolean findOne(String appName) {
        SearchDao dao = null;
        boolean temp = false;
        try {
            dao = new SearchDao(mContext);
            temp = dao.findOne(appName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return temp;
    }
}
