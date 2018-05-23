package cn.lt.game.bean;

import android.text.TextUtils;

import java.io.Serializable;

import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.TimeUtils;

/**
 * Created by JohnsonLin on 2017/8/3.
 */

public class RegistCountBean implements Serializable {
    private long date;
    private int count;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /** 是否能升级*/
    public boolean canRegist() {
        if (isSameDate()) {
            LogUtils.i(LogTAG.registCountTAG, "同一天，count = " + count);
            return count < 3;
        }
        return  true;
    }

    /** 判断是否同一天*/
    public boolean isSameDate() {
        String dataStr = TimeUtils.getLongtoString(date);
        return !TextUtils.isEmpty(dataStr) && dataStr.equals(TimeUtils.getLongtoString(System.currentTimeMillis()));
    }

    /** 次数加1*/
    public void addCount() {
        count ++;
    }

    /** 保存新日期并且次数为1*/
    public void saveNewData() {
        date = System.currentTimeMillis();
        count = 1;
    }

    @Override
    public String toString() {
        return "RegistCountBean{" +
                "date=" + TimeUtils.getLongtoString(date) +
                ", count=" + count +
                '}';
    }
}
