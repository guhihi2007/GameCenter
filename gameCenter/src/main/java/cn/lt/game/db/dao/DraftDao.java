package cn.lt.game.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.db.DBHelper;
import cn.lt.game.db.factory.FavoriteDBFactory;
import cn.lt.game.db.factory.IDBFactory;
import cn.lt.game.db.operation.FavoriteDbOperator;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.ui.app.community.model.Category;
import cn.lt.game.ui.app.community.model.DraftBean;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;

/***
 * 草稿箱数据库操作类
 *
 * @author ltbl
 */
public class DraftDao {
    private SQLiteDatabase mDatabase;
    private String mTableName;

    public DraftDao(Context context) {
        IDBFactory factory = new FavoriteDBFactory();
        DBHelper helper = factory.getDB(context);
        mDatabase = helper.getWritableDatabase();
        mTableName = FavoriteDbOperator.DRAFT_TABLE_NAME;
    }

    /***
     * 插入一条对象
     *
     * @param bean
     * @return
     */
    public boolean save(DraftBean bean) {
        if (null == bean) {
            return false;
        }
        int userID = UserInfoManager.instance().getUserInfo().getId();
        LogUtils.i("zzz", "要插入的用户userID=" + userID);
        try {
            LogUtils.i("zzz", "路径字符窜=" + bean.getTopic_paths().size());
            ContentValues cv = new ContentValues();
            cv.put("tag", bean.getTag());
            cv.put("type", bean.getType());
            cv.put("state", bean.getState());
            cv.put("group_id", bean.getGroup_id());
            cv.put("topic_Id", bean.getTopic_Id());
            cv.put("userID", userID);
            //话题路径数组、评论列表数组和话题分类数据列表单独转换成字符窜存储
            cv.put("topic_paths", arrayToString(bean.getTopic_paths()));
            cv.put("comment_paths", arrayToString(bean.getComment_paths()));
            cv.put("categoryList", categoryToString(bean.getCategoryList()));
            cv.put("comment_id", bean.getComment_id());
            cv.put("acceptor_id", bean.getAcceptor_id());
            cv.put("topic_title", bean.getTopic_title());
            cv.put("groupTitle", bean.getGroupTitle());
            cv.put("topic_content", bean.getTopic_content());
            cv.put("acceptorNickname", bean.getAcceptorNickname());
            cv.put("category_id", bean.getCategory_id());
            cv.put("comment_content", bean.getComment_content());
            cv.put("reply_content", bean.getReply_content());
            cv.put("local_topicPaths", bean.getLocal_topicPaths());
            cv.put("local_topicContent", bean.getLocal_topicContent());
            cv.put("local_commentContent", bean.getLocal_commentContent());
            cv.put("local_replyContent", bean.getLocal_replyContent());
            mDatabase.insert(mTableName, null, cv);
            LogUtils.i("zzz", "插入对象成功");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    /***
     * 根据page查询，只查询失败的，一次查询20条
     *
     * @return 返回一个ArrayList，默认一次性返回20条数据
     */
    public List<DraftBean> findByPage(int page) {
        List<DraftBean> list = new ArrayList<DraftBean>();
        Cursor cursor = null;
        try {
            int userID = UserInfoManager.instance().getUserInfo().getId();
            cursor = mDatabase.query(mTableName, null, "state = ? and " + "userID= ?",
                    new String[]{"0", userID + ""}, null, null, "_id" + " " +
                            "desc", FavoriteDbOperator.pageSize * page + " ," +
                            FavoriteDbOperator.pageSize);

            DraftBean draftBean = null;
            while (cursor.moveToNext()) {
                draftBean = new DraftBean();
                draftBean.setId(cursor.getColumnIndex("_id"));
                draftBean.setTag(cursor.getString(cursor.getColumnIndex("tag")));
                draftBean.setType(cursor.getInt(cursor.getColumnIndex("type")));
                draftBean.setState(cursor.getInt(cursor.getColumnIndex("state")));
                draftBean.setGroup_id(cursor.getInt(cursor.getColumnIndex("group_id")));
                draftBean.setTopic_Id(cursor.getInt(cursor.getColumnIndex("topic_Id")));
                draftBean.setComment_id(cursor.getInt(cursor.getColumnIndex("comment_id")));
                draftBean.setAcceptor_id(cursor.getInt(cursor.getColumnIndex("acceptor_id")));
                draftBean.setCategory_id(cursor.getString(cursor.getColumnIndex("category_id")));
                draftBean.setTopic_title(cursor.getString(cursor.getColumnIndex("topic_title")));
                draftBean.setTopic_content(cursor.getString(cursor.getColumnIndex
                        ("topic_content")));
                draftBean.setGroupTitle(cursor.getString(cursor.getColumnIndex("groupTitle")));
                draftBean.setReply_content(cursor.getString(cursor.getColumnIndex
                        ("reply_content")));
                draftBean.setComment_content(cursor.getString(cursor.getColumnIndex
                        ("comment_content")));
                draftBean.setLocal_topicContent(cursor.getString(cursor.getColumnIndex
                        ("local_topicContent")));
                draftBean.setLocal_topicPaths(cursor.getString(cursor.getColumnIndex
                        ("local_topicPaths")));
                draftBean.setLocal_commentContent(cursor.getString(cursor.getColumnIndex
                        ("local_commentContent")));
                draftBean.setLocal_replyContent(cursor.getString(cursor.getColumnIndex
                        ("local_replyContent")));
                draftBean.setAcceptorNickname(cursor.getString(cursor.getColumnIndex
                        ("acceptorNickname")));
                String categoryListStr = cursor.getString(cursor.getColumnIndex("categoryList"));
                ArrayList<Category> categoryListss = fromJSONToCategory(categoryListStr);

                String topic_pathStr = cursor.getString(cursor.getColumnIndex("topic_paths"));
                ArrayList<String> topic_pathss = fromJSONToArray(topic_pathStr);

                String comment_pathStr = cursor.getString(cursor.getColumnIndex("comment_paths"));
                ArrayList<String> comment_pathss = fromJSONToArray(comment_pathStr);

                draftBean.setCategoryList(categoryListss);
                draftBean.setTopic_paths(topic_pathss);
                draftBean.setComment_paths(comment_pathss);
                list.add(draftBean);
            }
            cursor.close();
            if (null != list && list.size() > 0) {
                LogUtils.i("zzz", "草稿箱列表长度==" + list.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /***
     * 获取草稿箱列表的总长度(降序)
     *
     * @return
     */
    public int getDraftListCount() {
        //		Cursor cursor = db.selectTable(mTableName);
        Cursor cursor = null;
        try {
            int userID = UserInfoManager.instance().getUserInfo().getId();
            cursor = mDatabase.query(mTableName, null, "state = ? and " + "userID= ?", new
                    String[]{"0", userID + ""}, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }

        return cursor==null?0:cursor.getCount();
    }

    /***
     * 查询数据库发送失败的话题记录
     *
     * @return
     */
    public boolean hasFailedTopicRecord() {
        Cursor cursor = null;
        try {
            int userID = UserInfoManager.instance().getUserInfo().getId();
            cursor = mDatabase.query(mTableName, null, "state = ? and " + "type= ? and userID= " +
                    "?", new String[]{"0", "0", userID + ""}, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }

        if (null!=cursor && cursor.getCount() > 0) {
            return true;
        }
        return false;
    }


    /***
     * 根据GroupID查询是否存在有失败的记录
     *
     * @param groupId
     * @return
     */
    public boolean hasRecordByGroupId(int groupId) {

        Cursor cursor = null;
        try {
            int userID = UserInfoManager.instance().getUserInfo().getId();
            cursor = mDatabase.query(mTableName, null, "state = ? and " + "type= ? and group_id =" +
                    " ? and userID= ? ", new String[]{"0", "0", groupId + "", userID +
                    ""}, null, null, null);
            if (null!=cursor && cursor.getCount() > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }

        return false;
    }


    /***
     * json字符窜转ArrayList<String>
     *
     * @param str
     * @return
     */
    private ArrayList<String> fromJSONToArray(String str) {
        if (!"".equals(str) && null != str) {
            Gson gs = new Gson();
            return gs.fromJson(str, new TypeToken<ArrayList<String>>() {
            }.getType());
        }
        return null;
    }

    /***
     * String 转ArrayList<Category>
     *
     * @param str
     * @return
     */
    private ArrayList<Category> fromJSONToCategory(String str) {
        if (!"".equals(str) && null != str) {
            Gson gs = new Gson();
            return gs.fromJson(str, new TypeToken<ArrayList<Category>>() {
            }.getType());
        }
        return null;
    }

    /***
     * ArrayList<String> 转 String
     *
     * @param list
     * @return
     */
    private String arrayToString(ArrayList<String> list) {
        if (null != list) {
            Gson gs = new Gson();
            return gs.toJson(list, new TypeToken<ArrayList<String>>() {
            }.getType());
        }
        return null;
    }

    /***
     * ArrayList<Category>转String
     *
     * @param categoryList
     * @return
     */
    private String categoryToString(ArrayList<Category> categoryList) {
        if (null != categoryList) {
            Gson gs = new Gson();
            return gs.toJson(categoryList, new TypeToken<ArrayList<Category>>() {
            }.getType());
        }
        return "";
    }

    /***
     * 根据Tag删除
     *
     * @return
     */
    public boolean deleteByTag(String tag) {
        try {
            mDatabase.delete(mTableName, "tag = ? ", new String[]{tag});

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /***
     * 根据Tag更新一条，只更新其state
     *
     * @return
     */
    public int update(String tag) {
        if (null == tag && "".equals(tag)) {
            return -1;
        }
        try {
            ContentValues cv = new ContentValues();
            cv.put("state", "0");
            mDatabase.update(mTableName, cv, "tag = ? ", new String[]{tag});

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * 跟新数据
     *
     * @param tag
     * @param bean
     */
    public void updateByTag(String tag, DraftBean bean) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("tag", bean.getTag());
            cv.put("type", bean.getType());
            cv.put("state", bean.getState());
            cv.put("group_id", bean.getGroup_id());
            cv.put("topic_Id", bean.getTopic_Id());
            //话题路径数组、评论列表数组和话题分类数据列表单独转换成字符窜存储
            cv.put("topic_paths", arrayToString(bean.getTopic_paths()));
            cv.put("comment_paths", arrayToString(bean.getComment_paths()));
            cv.put("categoryList", categoryToString(bean.getCategoryList()));

            cv.put("comment_id", bean.getComment_id());
            cv.put("acceptor_id", bean.getAcceptor_id());
            cv.put("topic_title", bean.getTopic_title());
            cv.put("groupTitle", bean.getGroupTitle());
            cv.put("topic_content", bean.getTopic_content());
            cv.put("acceptorNickname", bean.getAcceptorNickname());
            cv.put("category_id", bean.getCategory_id());
            cv.put("comment_content", bean.getComment_content());
            cv.put("reply_content", bean.getReply_content());
            cv.put("local_topicPaths", bean.getLocal_topicPaths());
            cv.put("local_topicContent", bean.getLocal_topicContent());
            cv.put("local_commentContent", bean.getLocal_commentContent());
            cv.put("local_replyContent", bean.getLocal_replyContent());
            mDatabase.update(mTableName, cv, " tag =?", new String[]{tag});
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void close() {
        mDatabase.close();
    }

}
