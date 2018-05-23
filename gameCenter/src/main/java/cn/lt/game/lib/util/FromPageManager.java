package cn.lt.game.lib.util;

import android.text.TextUtils;

import java.util.List;

import cn.lt.game.application.MyApplication;
import cn.lt.game.global.Constant;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;

/**
 * @author chengyong
 * @time 2016/7/23 11:03
 * @des 页面及页面id的管理类
 */
public class FromPageManager {
    /**
     *  直接获取上一级页面
     * @return
     */
    public static String getLastPage() {
        List<String> mLastPageList = MyApplication.application.getmLastPageList();
        String lastPage = "";
        if (mLastPageList.size() == 1) {
            lastPage = mLastPageList.get(0);
            LogUtils.d("juice","当前的页面（或上级）是==>" + mLastPageList.get(0));
        }else if(mLastPageList.size() > 1){
            lastPage = mLastPageList.get(1);
            LogUtils.d("juice","当前的页面是==>" + mLastPageList.get(0)+"上级的页面是==>" + mLastPageList.get(1));
        }
        return lastPage;
    }

    /**
     * 传入当前页面并设置确定上一级页面
     * @param getCurrentPage
     * @return
     */
    public static void setLastPage(String getCurrentPage) {
        try {
            List<String> mLastPageList = MyApplication.application.getmLastPageList();
            LogUtils.d("juice", "设置上级页面时传入的当前的页面是==>" + getCurrentPage);
            if (!TextUtils.isEmpty(getCurrentPage)) { //过滤同一页面（详情页除外）
                if (!Constant.PAGE_GAME_DETAIL.equals(getCurrentPage)) {
                    if (mLastPageList.size() >= 1 && mLastPageList.get(0).equals(getCurrentPage)) {
                        LogUtils.d("juice", "同一页面，不设置fromPage==>" + getCurrentPage);
                        return;
                    }
                }
                LogUtils.d("juice", "真正赋值：页面时传入的当前的页面是==>" + getCurrentPage);
                mLastPageList.add(0, getCurrentPage);
            }
            if (mLastPageList.size() > 2) {
                String currentPage = mLastPageList.get(0);
                String secondPage = mLastPageList.get(1);
                mLastPageList.removeAll(mLastPageList);
                mLastPageList.add(0, currentPage);
                mLastPageList.add(1, secondPage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 传入当前页面id 并设置确定上一级页面id
     *
     * @param page
     * @param currentPageId
     * @return
     */
    public static void setLastPageId(String page, String currentPageId) {
        try {
            List<String> mLastPageIdList = MyApplication.application.getmLastPageIdList();
            LogUtils.d("juice", "设置上级页面id时传入的当前的id是==>" + currentPageId);
//            if (!TextUtils.isEmpty(currentPageId)) {
//                if (mLastPageIdList.size() >= 1 && mLastPageIdList.get(0).equals(currentPageId) && !isAwardPage(page)) {
//                    LogUtils.e("juice", "同一id，不设置fromId==>" + currentPageId);
//                    return;
//                }
                LogUtils.d("juice", "真正传入赋值：id时传入的当前的id是==>" + currentPageId);
                mLastPageIdList.add(0, currentPageId);
//            }
            if (mLastPageIdList.size() > 2) {
                String currentPage = mLastPageIdList.get(0);
                String secondPage = mLastPageIdList.get(1);
                mLastPageIdList.removeAll(mLastPageIdList);
                mLastPageIdList.add(0, currentPage);
                mLastPageIdList.add(1, secondPage);
                LogUtils.d("juice", "当前的id是==>" + currentPageId+"from_id是==>" + secondPage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAwardPage(String page) {
        return Constant.PAGE_CHOUJIANG.equals(page)
                || Constant.PAGE_JIFENG.equals(page)
                || Constant.PAGE_ZHONGJIANG.equals(page)
                || Constant.PAGE_JIFEN_RECORD.equals(page)
                || Constant.PAGE_HISTORY.equals(page);
    }

    /**
     * 获取上一级页面id
     * @return
     */
    public static String getLastPageId() {
        List<String> mLastPageIdList = MyApplication.application.getmLastPageIdList();
        String lastPageId = "";
//        if (mLastPageIdList.size() == 1 || !MyApplication.application.hasPageId) {//没有ID
        if (mLastPageIdList.size() == 1 ) {
            lastPageId = mLastPageIdList.get(0);
            LogUtils.d("juice","A:currentPageId:"+mLastPageIdList.get(0));
        }else if(mLastPageIdList.size() > 1){
            lastPageId = mLastPageIdList.get(1);
        }
        if(mLastPageIdList.size()>1){
            LogUtils.d("juice","B:currentPageId:"+mLastPageIdList.get(0)+"-----lastPageId:"+mLastPageIdList.get(1));
        }
        return lastPageId;
    }


    /**
     * 上报页面浏览
     */
    public static void pageJumpReport(StatisticsEventData event) {
        try {
            DCStat.pageJumpEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 当前页、上级页面是搜索时确定word
     * @param currPage
     * @return
     */
    public static boolean setWordByPage(String currPage) {
        if (Constant.PAGE_SEARCH_AUTO_MATCH.equals(getLastPage()) ||
                Constant.PAGE_SEARCH_RESULT.equals(getLastPage()) ||
                Constant.PAGE_SEARCH_FAILED.equals(getLastPage()) ||
                Constant.PAGE_SEARCH_AUTO_MATCH.equals(currPage) ||
                Constant.PAGE_SEARCH_RESULT.equals(currPage)) {
            LogUtils.d("juice", "满足搜索页条件：搜索词是" + MyApplication.application.mCurrentWord);
            return true;
        } else {
            return false;
        }
    }
}
