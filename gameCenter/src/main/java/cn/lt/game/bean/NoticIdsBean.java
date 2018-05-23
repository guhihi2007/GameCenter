package cn.lt.game.bean;

import java.util.List;

import cn.lt.game.lib.util.TimeUtils;

/**
 * Created by LinJunSheng on 2016/11/1.
 */

public class NoticIdsBean {
    private String date;
    private List<Integer> idList;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Integer> getIdList() {
        return idList;
    }

    public void setIdList(List<Integer> idList) {
        this.idList = idList;
    }

    /**
     * 是否属于七天内相同的通知id
     */
    public boolean isSameIdOf_7Day(String curDate, int noticeId) throws Exception {
            if (TimeUtils.daysBetween(date, curDate) <= 7) {
                if (idList.contains(noticeId)) {
                    return true;
                }
            }
        return false;
    }
}
