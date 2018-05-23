package cn.lt.game.statistics.pageunits;

import android.text.TextUtils;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.bean.GameInfoBean;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleGroup;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.domain.essence.FunctionEssenceImpl;
import cn.lt.game.domain.essence.IdentifierType;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.model.PageMap;
import cn.lt.game.service.InstalledEventLooper;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.statistics.manger.StatManger;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.data.PresentData;


/**
 * @author chengyong
 * @time 2017/3/2 11:26
 * @des 页面多资源上报
 */

public class PageMultiUnitsReportManager<T> {
    private static PageMultiUnitsReportManager instance;

    public static PageMultiUnitsReportManager getInstance() {
        if (instance == null) {
            synchronized (InstalledEventLooper.class) {
                if (instance == null) {
                    instance = new PageMultiUnitsReportManager();
                }
            }
        }
        return instance;
    }

    public void buildPageUnits(List<ItemData<? extends BaseUIModule>> saveTemp, T mPullToRefreshListView, String page, String lable, String mCurrentTagId, String keyWord, String mCategoryTitle) {
        LogUtils.i("PageMultiUnitsReportManager", "PageName:" + page);
        if (saveTemp == null || saveTemp.size() == 0 || mPullToRefreshListView == null) return;
        ListView listView = null;
        int firstVisiblePosition = 0;
        int lastVisiblePosition = 0;
        List<ItemData<? extends BaseUIModule>> reportDatas = null;
        if (mPullToRefreshListView instanceof RefreshAndLoadMoreListView) {
            listView = ((RefreshAndLoadMoreListView) mPullToRefreshListView).getmListView();
        } else {
            listView = (ListView) mPullToRefreshListView;
        }
        firstVisiblePosition = listView.getFirstVisiblePosition();
        lastVisiblePosition = listView.getLastVisiblePosition();
        LogUtils.i("pppp", page + " 第一个可见==>" + firstVisiblePosition);
        LogUtils.i("pppp", page + "最后可见==>" + lastVisiblePosition);
        LogUtils.i("pppp", page + " 保存的数据集个数==>" + saveTemp.size());
        try {
            reportDatas = saveTemp.subList(firstVisiblePosition == 0 ? 0 : firstVisiblePosition - 1, (saveTemp.size() <= lastVisiblePosition + 1) ? saveTemp.size() : (Constant.PAGE_CATEGORY_LIST.equals(page) ? (lastVisiblePosition - 1) : lastVisiblePosition));
            LogUtils.i("pppp", page + " 正常截取后的个数==>" + reportDatas.size());
        } catch (Exception e) {
            e.printStackTrace();
            try {
                reportDatas = saveTemp.subList(firstVisiblePosition == 0 ? 0 : firstVisiblePosition - 1, (saveTemp.size() <= lastVisiblePosition + 1) ? saveTemp.size() : lastVisiblePosition - 2);
                LogUtils.i("pppp", page + "抛异常时的截取数据集个数==>");
            } catch (Exception e1) {
                e1.printStackTrace();
                LogUtils.i("pppp", page + "最后还是抛异常了，束手无策了");
            }
        }
        reportPageUnitsByPresentType(reportDatas, page, lable, mCurrentTagId, keyWord, mCategoryTitle);
    }


    /**
     * presentType：轮播图/入口/时下热门/banner/单游戏
     * srcType：游戏详情/专题详情/游戏礼包/礼包详情/专题列表/礼包列表/社区主题/H5
     * id：游戏id/专题id/礼包id
     * package_name：游戏包名
     * pos：
     * subpos：
     * url：h5的url
     * page：
     *
     * @param itemDatas
     * @param pageName
     * @param keyWord
     * @param mCategoryTitle
     */
    public static void reportPageUnitsByPresentType(List<ItemData<? extends BaseUIModule>> itemDatas, String pageName, String lable, String mCurrentTagId, String keyWord, String mCategoryTitle) {
        List<String> reportDatas = null;
        String specialTitle = "";
        try {
            if (itemDatas == null || itemDatas.size() == 0) return;
            reportDatas = new ArrayList<>();
            reportDatas.clear();
            String url = "";
            String srcType = "";
            for (ItemData itemData : itemDatas) {
                switch (itemData.getmPresentType()) {
                    case game_gifts_lists:
                        break;
                    case entry:
                        UIModuleGroup umg = (UIModuleGroup) itemData.getmData();
                        List<ItemData<UIModule>> mEntrys = umg.getData();
                        int counts = mEntrys.size();
                        for (int i = 0; i < counts; i++) {

                            ItemData<UIModule> game = mEntrys.get(i);
                            UIModule module = game.getmData();

                            final FunctionEssence functionEssence = (FunctionEssence) module.getData();
                            String id = functionEssence.getUniqueIdentifierBy(IdentifierType.ID);
                            url = functionEssence.getUniqueIdentifierBy(IdentifierType.URL);
                            String highClickType = functionEssence.getHighClickType();
                            String pageName410 = functionEssence.getPage_name_410();
                            String title = functionEssence.getTitle();

                            if (!TextUtils.isEmpty(highClickType) || !TextUtils.isEmpty(pageName410)) {
                                if (!TextUtils.isEmpty(highClickType) && PageMap.instance().isIdentifiable(highClickType)) {
                                    srcType = highClickType;
                                } else {
                                    srcType = pageName410;
                                }

                            } else {
                                srcType = functionEssence.getUniqueIdentifierBy(IdentifierType.NAME); //绑定的跳转页面
                            }

                            LogUtils.i("auiosiod88", "entry=>id=" + id + "=>pageName410=" + pageName410 + "=>srcType=" + srcType + "=>title=" + title);
                            reportDatas.add(DCStat.buildPageMultiUnitsData(srcType, itemData.getPos(), mEntrys.get(i).getSubPos(), "entry", "", id, pageName, url, title));
                        }
                        break;
                    case hot:     //三个一排  多排
                        UIModuleGroup mg = (UIModuleGroup) itemData.getmData();
                        List<ItemData<UIModule>> mGames = mg.getData();
                        if (mGames != null) {
                            int count = mGames.size();
                            for (int i = 0; i < count; i++) {
                                ItemData<UIModule> game = mGames.get(i);
                                UIModule module = game.getmData();
                                GameDomainBaseDetail gameDomainBaseDetail = (GameDomainBaseDetail) module.getData();
                                LogUtils.d("ddd", "hot=>getPkgName=" + gameDomainBaseDetail.getPkgName() + "hot=>getPkgName=" + gameDomainBaseDetail.getUniqueIdentifier());
                                reportDatas.add(DCStat.buildPageMultiUnitsData("game", itemData.getPos(), game.getSubPos(), "hot", gameDomainBaseDetail.getPkgName(), gameDomainBaseDetail.getUniqueIdentifier(), pageName, "", ""));
                            }
                        }
                        break;
                    case banner:
                        UIModule module_banner = (UIModule) itemData.getmData();
                        FunctionEssence fe_ = (FunctionEssence) module_banner.getData();
                        String src_type_banner = fe_.getDomainEssence().getDomainType().toString();
                        String id_banner = fe_.getUniqueIdentifierBy(IdentifierType.ID);
                        if (src_type_banner.equals("h5")) {
                            url = fe_.getUniqueIdentifierBy(IdentifierType.URL);
                        }
                        reportDatas.add(DCStat.buildPageMultiUnitsData(src_type_banner, itemData.getPos(), itemData.getSubPos(), "banner", "", id_banner, pageName, url, ""));
                        break;
                    case carousel:
                        UIModuleGroup mgCarousel = (UIModuleGroup) itemData.getmData();
                        List<ItemData<UIModule>> mBannerItemDatas = mgCarousel.getData();
                        if (mBannerItemDatas != null) {
                            int count = mBannerItemDatas.size();
                            for (int i = 0; i < count; i++) {
                                ItemData<UIModule> game = mBannerItemDatas.get(i);
                                PresentData presentData = game.getmPresentData();  //获取position
                                FunctionEssence fe = (FunctionEssence) game.getmData().getData();  //获取src_type  要跳转的类型
                                String src_type = fe.getDomainEssence().getDomainType().toString();
                                LogUtils.e("juice", "carousel的srcType=" + src_type);
                                String id = fe.getUniqueIdentifier();
//                                if (src_type.equals("h5")) {
//                                }
                                url = fe.getUniqueIdentifierBy(IdentifierType.URL);
                                reportDatas.add(DCStat.buildPageMultiUnitsData(src_type, presentData.getPos(), game.getSubPos(), "carousel", "", id, pageName, url, ""));
                            }
                        }
                        break;
                    case super_push:
                    case game:
                    case search_null:
                    case search_top10:
                        UIModule module_game = (UIModule) itemData.getmData();
                        GameDomainBaseDetail game = (GameDomainBaseDetail) module_game.getData();
                        String id = game.getUniqueIdentifier();
                        LogUtils.e("juice", "公共中的lable=" + lable + "mCurrentTagId==" + mCurrentTagId);
                        reportDatas.add(DCStat.buildPageMultiUnitsData("game", itemData.getPos(), itemData.getSubPos(), "game", game.getPkgName(), id, pageName, "", mCategoryTitle, lable, mCurrentTagId, keyWord));

                        break;
                    case hot_cats:
                        UIModuleGroup hotcatsData = (UIModuleGroup) itemData.getmData();
                        List<ItemData> hotcatsList = hotcatsData.getData();
                        for (ItemData itemData1 : hotcatsList) {
                            FunctionEssence functionEssence = (FunctionEssence) ((UIModule) itemData1.getmData()).getData();

                            url = functionEssence.getUniqueIdentifierBy(IdentifierType.URL);
                            String clickType;
                            if (!TextUtils.isEmpty(functionEssence.getHighClickType())) {
                                clickType = functionEssence.getHighClickType();
                            } else {
                                clickType = "hot_cats";
                            }

                            LogUtils.d("juice", pageName.toString() + "的hot_cats数据==id" + functionEssence.getUniqueIdentifier());
                            reportDatas.add(DCStat.buildPageMultiUnitsData(clickType, itemData.getPos(), itemData1.getSubPos(), "hot_cats", "", functionEssence.getUniqueIdentifier(), pageName, url, functionEssence.getTitle()));
                        }
                        break;
                    case all_cats:
                        UIModule uiModule = (UIModule) itemData.getmData();
                        FunctionEssence fe = (FunctionEssence) uiModule.getData();
                        //子label
                        if (fe.hasSubFuncEss()) {
                            List<FunctionEssence> tags = fe.getSubFuncEss();
                            for (int i = 0; i < (tags.size() >= 6 ? 6 : tags.size()); i++) {
                                LogUtils.d("eee", pageName.toString() + "的lable数据==id" + tags.get(i).getUniqueIdentifier() + "的lable数据==title" + tags.get(i).getTitle());
                                reportDatas.add(DCStat.buildPageMultiUnitsData("", itemData.getSubPos() + 1, 1 + i, "lable", "", tags.get(i).getUniqueIdentifier(), pageName, "", tags.get(i).getTitle()));
                            }
                        }
                        reportDatas.add(DCStat.buildPageMultiUnitsData("", itemData.getPos() + 1, itemData.getSubPos(), "all_cats", "", fe.getUniqueIdentifier(), pageName, keyWord, "", fe.getTitle()));
                        break;
                    case topic:
                        UIModule module = (UIModule) itemData.getmData();
                        FunctionEssenceImpl data = (FunctionEssenceImpl) module.getData();
                        reportDatas.add(DCStat.buildPageMultiUnitsData("", itemData.getPos(), itemData.getSubPos(), "topic", "", data.getUniqueIdentifier(), pageName, "", data.getTitle()));
                        break;
                    case topic_detail:
                        UIModule module_topic_detail = (UIModule) itemData.getmData();
                        FunctionEssence data_detail = (FunctionEssenceImpl) module_topic_detail.getData();
                        if (!TextUtils.isEmpty(data_detail.getTitle())) {
                            specialTitle = data_detail.getTitle();
                        }
                        LogUtils.d("eee", "title是:" + specialTitle);
                        reportDatas.add(DCStat.buildPageMultiUnitsData("", itemData.getPos(), itemData.getSubPos(), "topic_detail", "", data_detail.getUniqueIdentifier(), pageName, "", specialTitle, "", mCurrentTagId));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        reportMultiPart(pageName, reportDatas);
    }

    /**
     * 组装成jsonArray
     *
     * @param pageName
     * @param reportDatas
     */
    private static void reportMultiPart(String pageName, List<String> reportDatas) {
        try {
            String result = "{ \"exposureData\":[";
            for (int i = 0; i < reportDatas.size(); i++) {
                if (i == reportDatas.size() - 1) {
                    result = result.concat(reportDatas.get(i)).concat("]}");
                } else {
                    result = result.concat(reportDatas.get(i)).concat(",");
                }
            }
            LogUtils.d("ggg", pageName.toString() + "要上报的资源数据是：" + result);
            if (result.equals("{ \"exposureData\":[")) return;
            StatManger.self().saveStatisticDataToDb(result,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建并上报页面详情资源
     *
     * @param recommendGameList
     * @param hotTags
     * @param page
     * @param game
     */
    public static void gameInfoDataBuildAndReport(List<GameDomainBaseDetail> recommendGameList, List<FunctionEssence> hotTags, String page, GameBaseDetail game) {
        List<String> reportDatas = null;
        try {
            LogUtils.d("juice", page.toString() + "的pageId是==>" + game.getId() + "的title是==>" + game.getName());
            if (recommendGameList == null) return;
            reportDatas = new ArrayList<>();
            reportDatas.clear();
            for (int i = 0; i < recommendGameList.size(); i++) {
                reportDatas.add(DCStat.pageGameDetailRecommen(recommendGameList.get(i), page, i, game.getId(), game.getName()));
            }

            for (int j = 0; j < hotTags.size(); j++) {
                reportDatas.add(DCStat.pageGameDetailLable(hotTags.get(j), page, j, game.getId(), game.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        reportMultiPart(page, reportDatas);
    }

    /**
     * id：游戏id
     * pkg_name：游戏包名
     * pos：游戏位置
     * subpos：
     * page：
     *
     * @param tempGame
     * @param pageName
     */
    public static void buildBiWanEvent(List<GameInfoBean> tempGame, String pageName) {
        if (tempGame == null) return;
        List<String> reportDatas = new ArrayList<>();
        reportDatas.clear();
        for (int i = 0; i < tempGame.size(); i++) {
            reportDatas.add(DCStat.pageBiWanEvent(tempGame.get(i), i, pageName));
        }
        reportMultiPart(pageName, reportDatas);
    }


    /**
     * @param tempList
     * @param mPullToRefreshListView
     * @param page
     * @param keyword
     */
    public static void buildPageUnitsForAutoMatchSearch(List<Object> tempList, ListView mPullToRefreshListView, String page, String keyword) {
        if (tempList != null && tempList.size() > 0) {
            try {
                if (mPullToRefreshListView != null) {
                    int firstVisiblePosition = mPullToRefreshListView.getFirstVisiblePosition();
                    int lastVisiblePosition = mPullToRefreshListView.getLastVisiblePosition();
                    LogUtils.i("pppp", page + " 第一个可见==>" + firstVisiblePosition);
                    LogUtils.i("pppp", page + "最后可见==>" + lastVisiblePosition);
                    LogUtils.i("pppp", page + " 保存的数据集个数==>" + tempList.size());
                    List<Object> reportDatas = tempList.subList(firstVisiblePosition == 0 ? 0 : firstVisiblePosition, lastVisiblePosition + 1);
                    LogUtils.i("pppp", page + " 截取后的个数==>" + reportDatas.size());
                    autoMatchEvent(reportDatas, page, keyword);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.i("nnn", "搜索匹配list crash了" + e.getMessage());
            }
        }
    }

    /**
     */
    public static void autoMatchEvent(List<Object> tempList, String pageName, String keyword) {
        if (tempList == null || tempList.size() == 0) return;
        List<String> reportDatas = new ArrayList<>();
        reportDatas.clear();
        try {
            for (int i = 0; i < tempList.size(); i++) {
                LogUtils.i("pppp", "autoMatchEvent 截取后分发数据==>" + tempList.size());
                if (tempList.get(i) instanceof String) {
                    reportDatas.add(DCStat.autoSearchhistoryBuild((String) tempList.get(i), pageName, keyword, i));  //TODO 这个会不准
                } else if (tempList.get(i) instanceof GameDomainBaseDetail) {
                    reportDatas.add(DCStat.autoSearchAccuratebuild((GameDomainBaseDetail) tempList.get(i), pageName, keyword, ((GameDomainBaseDetail) tempList.get(i)).autoMatchPos));
                } else {
                    reportDatas.add(DCStat.autoSearchFuzzyBuild((FunctionEssence) tempList.get(i), pageName, keyword, ((FunctionEssence) tempList.get(i)).autoMatchPos));
                }

            }
        } catch (Exception e) {
            LogUtils.i("pppp", "autoMatchEvent Exception==>" + e.getMessage());
            e.printStackTrace();
        }
        reportMultiPart(pageName, reportDatas);
    }

    /**
     * 搜索推荐
     */
    public static void searchAdvertisementEvent(List<FunctionEssence> functionEssences_, String pageName) {
        List<String> reportDatas = null;
        try {
            if (functionEssences_ == null) return;
            reportDatas = new ArrayList<>();
            reportDatas.clear();
            for (int j = 0; j < functionEssences_.size(); j++) {
                reportDatas.add(DCStat.searchAdvertisementBuild(functionEssences_.get(j), pageName, j));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("sousuo", "搜索推荐 Exception==>" + e.getMessage());
        }
        LogUtils.i("sousuo", "搜索推荐 的个数==>" + reportDatas.size());
        reportMultiPart(pageName, reportDatas);
    }

    /**
     * 启动页数据
     * pic_title
     * srcType：游戏详情/h5
     * id：游戏id
     * url：h5的url
     * page：
     */
    public static void buildlLoadingDataEvent(String pic_title, String presentType, String srcType, int id, String url, String page, String mPackagName) {
        try {
            StatisticsEventData data = new StatisticsEventData();
            data.setActionType(ReportEvent.ACTION_PAGE_MULTI_UNITS);
            data.setSrc_id(id == 0 ? "" : String.valueOf(id));
            data.setSrcType(srcType);  //单游戏、还是h5
            data.setPresentType(presentType);  //单游戏、还是h5
            data.setUrl(url);
            data.setPage(page);
            data.setTitle(pic_title);
            data.setPackage_name(mPackagName);
            String result = "{ \"exposureData\":";
            result = result.concat(data.getString()).concat("}");
            LogUtils.d("nnn", "启动也要上报的资源数据是：" + result);
            StatManger.self().saveStatisticDataToDb(result, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
