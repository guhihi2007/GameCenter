package cn.lt.game.db.service;

import android.content.Context;

import java.util.List;

import cn.lt.game.db.dao.DraftDao;
import cn.lt.game.ui.app.community.model.DraftBean;

/***
 * 草稿箱数据库操作类
 *
 * @author ltbl
 */
public class DraftService {
    private static DraftService draftDao;
    private Context mContext;

    public DraftService(Context context) {
        mContext = context;
    }

    public static DraftService getSingleton(Context context) {
        if (draftDao == null) {
            synchronized (DraftService.class) {
                if (draftDao == null) {
                    draftDao = new DraftService(context);
                }
            }
        }
        return draftDao;
    }

    /***
     * 插入一条对象
     *
     * @param bean
     * @return
     */
    public synchronized boolean save(DraftBean bean) {
        DraftDao dao = null;
        boolean isSucc = false;
        try {
            dao = new DraftDao(mContext);
            isSucc = dao.save(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return isSucc;

    }

    /***
     * 根据page查询，只查询失败的，一次查询10条
     *
     * @return 返回一个ArrayList，默认一次性返回10条数据
     */
    public synchronized List<DraftBean> findByPage(int page) {

        DraftDao dao = null;
        List<DraftBean> list = null;
        try {
            dao = new DraftDao(mContext);
            list = dao.findByPage(page);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return list;
    }

    /***
     * 获取草稿箱列表的总长度(降序)
     *
     * @return
     */
    public synchronized int getDraftListCount() {
        DraftDao dao = null;
        int temp = 0;
        try {
            dao = new DraftDao(mContext);
            temp = dao.getDraftListCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return temp;
    }

    /***
     * 查询数据库发送失败的话题记录
     *
     * @return
     */
    public synchronized boolean hasFailedTopicRecord() {

        DraftDao dao = null;
        boolean temp = false;
        try {
            dao = new DraftDao(mContext);
            temp = dao.hasFailedTopicRecord();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return temp;
    }

    /***
     * 查询数据库是否有发送失败的评论或者回复
     *
     * @return
     */
    public synchronized boolean hasFailedCommOrReplyRecord() {

        DraftDao dao = null;
        boolean temp = false;
        try {
            dao = new DraftDao(mContext);
            temp = dao.hasFailedTopicRecord();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return temp;
    }

    /***
     * 根据GroupID查询是否存在有失败的记录
     *
     * @param groupId
     * @return
     */
    public synchronized boolean hasRecordByGroupId(int groupId) {

        DraftDao dao = null;
        boolean temp = false;
        try {
            dao = new DraftDao(mContext);
            temp = dao.hasRecordByGroupId(groupId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return temp;
    }

    /***
     * 删除表，用于用户切换账户时执行
     *
     * @return
     */
    public synchronized void deleteAll() {

        DraftDao dao = null;
        try {
            dao = new DraftDao(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
    }

    /***
     * 根据Tag删除
     *
     * @return
     */
    public synchronized boolean deleteByTag(String tag) {

        DraftDao dao = null;
        boolean temp = false;
        try {
            dao = new DraftDao(mContext);
            temp = dao.deleteByTag(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return temp;
    }

    /***
     * 根据Tag更新一条，只更新其state
     *
     * @return
     */
    public synchronized int update(String tag) {
        DraftDao dao = null;
        int temp = -1;
        try {
            dao = new DraftDao(mContext);
            temp = dao.update(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
        return temp;
    }

    /**
     * 跟新数据
     *
     * @param tag
     * @param bean
     */
    public synchronized void updateByTag(String tag, DraftBean bean) {
        DraftDao dao = null;
        try {
            dao = new DraftDao(mContext);
            dao.updateByTag(tag, bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.close();
        }
    }

}
