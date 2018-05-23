package cn.lt.game.bean;

import android.text.TextUtils;

import cn.lt.game.lib.util.TimeUtils;

/**
 * Created by LinJunSheng on 2016/11/1.
 * 记录每天请求通知数据次数（拉活沉默用户）
 */

public class RequestCountBean {
    /** 日期*/
    private String date = "";

    /** 次数*/
    private int requestCount = 0;

    /** 最后一次请求的时间戳*/
    private long lastRequestTime = 0;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public long getLastRequestTime() {
        return lastRequestTime;
    }

    public void setLastRequestTime(long lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }

    /** 判断是否同一天*/
    public boolean isSameDate(long time) {
        return !TextUtils.isEmpty(date) && date.equals(TimeUtils.getLongtoString(time));
    }

    /** 请求次数+1*/
    public void add1() {
        requestCount ++;
    }

    /** 判断是否超过3次*/
    public boolean isExceed3() {
        return requestCount >= 3;

    }

}
