package cn.lt.game.application.wakeup;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.global.LogTAG;
import cn.lt.game.bean.NoticIdsBean;
import cn.lt.game.lib.util.GsonUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.model.SharePreferencesKey;

/**
 * Created by LinJunSheng on 2016/11/3.
 */

public class NoticeIdComparator {

    public static boolean isSameId(Context context, int noticeId) {
        boolean isSameId = false;

        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(context, SharePreferencesKey.WAKEUP_SILENCE_USER, Context.MODE_PRIVATE);

        String noticeIdsData = spUtil.get(SharePreferencesKey.NOTICE_IDS_DATA);
        if(!TextUtils.isEmpty(noticeIdsData)) {
            List<NoticIdsBean> list = new Gson().fromJson(noticeIdsData, new TypeToken<List<NoticIdsBean>>() {
            }.getType());

            for (NoticIdsBean noticIdsBean : list) {
                boolean isSame = false;

                try {
                    isSame = noticIdsBean.isSameIdOf_7Day(TimeUtils.getLongtoString(System.currentTimeMillis()), noticeId);
                } catch (Exception e) {
                    e.printStackTrace();
                    // 若是抛异常，直接返回true，不显示通知,bing并清除原有数据以防再次出错
                    isSame = true;
                    spUtil.add(SharePreferencesKey.NOTICE_IDS_DATA, "");
                    LogUtils.i(LogTAG.wakeUpUser, "noticIdsBean解析出问题，清除原来记录的所有id数据并且不发通知");

                }

                if(isSame) {
                    LogUtils.i(LogTAG.wakeUpUser, "(检测重复通知id)当前通知id = " + noticeId + " ，在七天内已经显示过，so此次不发通知了");
                    isSameId = true;
                    break;
                }
            }
        }

        if (!isSameId) {
            LogUtils.i(LogTAG.wakeUpUser, "(检测重复通知id)当前通知id = " + noticeId + " ，在七天内没有显示过，马上发出通知~");
            saveId(noticeIdsData, noticeId, spUtil);
        }

        return isSameId;

    }

    private static void saveId(String noticeIdsData, int noticeId, SharedPreferencesUtil spUtil) {
        if(!TextUtils.isEmpty(noticeIdsData)) {
            List<NoticIdsBean> list = new Gson().fromJson(noticeIdsData, new TypeToken<List<NoticIdsBean>>() {
            }.getType());

            for (NoticIdsBean noticIdsBean : list) {
                if(noticIdsBean.getDate().equals(TimeUtils.getLongtoString(System.currentTimeMillis()))) {
                    noticIdsBean.getIdList().add(noticeId);

                    String data = GsonUtil.GsonString(list);
                    spUtil.add(SharePreferencesKey.NOTICE_IDS_DATA, data);
                    return;
                }
            }

            // 方法没返回的话，新建数据存入list
            creatDataAndSave(noticeId, spUtil, list);

        } else {
            creatDataAndSave(noticeId, spUtil, null);
        }
    }

    private static void creatDataAndSave(int noticeId, SharedPreferencesUtil spUtil, List<NoticIdsBean> list) {
        NoticIdsBean bean = new NoticIdsBean();
        bean.setDate(TimeUtils.getLongtoString(System.currentTimeMillis()));
        List<Integer> idList = new ArrayList<>();
        idList.add(noticeId);
        bean.setIdList(idList);

        if(list == null) {
            list = new ArrayList<>();
        }

        list.add(bean);

        String data = GsonUtil.GsonString(list);
        spUtil.add(SharePreferencesKey.NOTICE_IDS_DATA, data);
    }
}
