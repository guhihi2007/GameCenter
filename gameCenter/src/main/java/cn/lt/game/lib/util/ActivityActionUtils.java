package cn.lt.game.lib.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

import cn.lt.game.R;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.log.Logger;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.net.Host;
import cn.lt.game.ui.app.HomeActivity;
import cn.lt.game.ui.app.ImageViewPagerActivity;
import cn.lt.game.ui.app.ImageViewPagerActivity.ImageUrl;
import cn.lt.game.ui.app.WebViewActivity;
import cn.lt.game.ui.app.awardgame.AwardActivity;
import cn.lt.game.ui.app.awardpoints.AwardPointsRecordActivity;
import cn.lt.game.ui.app.awardpoints.awardrecord.PastAwardActivity;
import cn.lt.game.ui.app.category.CategoryHotCatsActivity;
import cn.lt.game.ui.app.category.CategoryItemResultActivity;
import cn.lt.game.ui.app.community.CommunityActivity;
import cn.lt.game.ui.app.community.personalpage.PersonalActivity;
import cn.lt.game.ui.app.community.topic.detail.TopicDetailActivity;
import cn.lt.game.ui.app.gamedetail.GameDetailHomeActivity;
import cn.lt.game.ui.app.gamegift.GiftHomeActivity;
import cn.lt.game.ui.app.hot.HotDetailActivity;
import cn.lt.game.ui.app.management.ManagementActivity;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.search.SearchTagActivity;
import cn.lt.game.ui.common.activity.GameOtherDetailActivity;

/**
 * Created by majian12344321 on 2015/1/23.
 */
public class ActivityActionUtils {

    /**
     * 基本的Activity跳转
     *
     * @param context
     * @param clazz
     */
    public static void activity_jump(Context context, Class clazz) {
        context.startActivity(new Intent(context, clazz));
    }

    /***
     * 跳转到游戏详情页面，添加了forumID字段
     *
     * @param context
     * @param clazz
     * @param key
     * @param value
     * @param value2
     */
    public static void activity_Jump_Value(Context context, Class clazz, String key, int value, int value2) {
        Intent intent = new Intent();
        intent.setClass(context, clazz); // 传出去的值
        intent.putExtra(key, value);
        intent.putExtra("forum_id", value2);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param clazz
     * @param key
     * @param value
     */
    public static void activity_Jump_Value(Context context, Class clazz, String key, String value) {
        if (!TextUtils.isEmpty(value)) {
            Intent intent = new Intent();
            intent.setClass(context, clazz); // 传出去的值
            intent.putExtra(key, value);
            System.out.println("value = " + intent.getStringExtra(key));
            context.startActivity(intent);
        }
    }

    public static void activity_Jump_Values(Context context, Class clazz, String key, Serializable values) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, values);
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void activity_Jump_Value(Context context, Class clazz, String key, int value) {
        boolean hasUserLogin = UserInfoManager.instance().isLogin();
        if (!hasUserLogin) {
            Intent intent = new Intent();
            intent.setClass(context, clazz); // 传出去的值
            intent.putExtra(key, value);
            context.startActivity(intent);
        } else {
            int userId = UserInfoManager.instance().getUserInfo().getId();
            if (value == userId) {
                jumpToCommunity(context, CommunityActivity.COM_MINE);
            } else {
                Intent intent = new Intent();
                intent.setClass(context, clazz); // 传出去的值
                intent.putExtra(key, value);
                context.startActivity(intent);
            }
        }
    }

    /**
     * 跳转到游戏详情页面，添加了forumID字段和来自推送跳转判断
     *
     * @param context
     * @param clazz
     * @param key
     * @param value
     * @param value2
     * @param isPush  来自推送跳转
     */
    public static void activity_Jump_Value(Context context, Class clazz, String key, int value, int value2, boolean isPush, String pushId, boolean isFromWake) {
        Intent intent = new Intent();
        intent.setClass(context, clazz); // 传出去的值
        intent.putExtra(key, String.valueOf(value));
        intent.putExtra("forum_id", value2);
        intent.putExtra("isPush", isPush);
        intent.putExtra("isFromWakeUp", isFromWake);
        intent.putExtra("pushId", pushId);
        context.startActivity(intent);
    }

    /***
     * 跳转游戏详情
     *
     * @param context
     * @param value
     */
    public static void JumpToGameDetail(Context context, int value) {
        Intent intent = new Intent();
//        ToastUtils.showToast(context,"value: " + value);
        //跳转前先判断游戏ID是否 为空
        if (value != 0) {
            intent.setClass(context, GameDetailHomeActivity.class);
            intent.putExtra("id", String.valueOf(value));
            context.startActivity(intent);
        } else {
            Log.i("zzz", "游戏ID为空");
        }
    }

    /***
     * 跳转管理
     *
     * @param context
     * @param value
     */
    public static void JumpToManager(Context context, int value) {
        Intent intent = new Intent();
        intent.setClass(context, ManagementActivity.class);
        intent.putExtra("id", value);
        context.startActivity(intent);
    }

    /***
     * 跳转管理
     *
     * @param context
     * @param value
     */
    public static void JumpToManager(Intent intent, Context context, int value) {
        intent.setClass(context, ManagementActivity.class);
        intent.putExtra("id", value);
        context.startActivity(intent);
    }

    public static void hotPageJumpToGameDetail(Context context, int value) {
        Intent intent = new Intent();
//        ToastUtils.showToast(context,"value: " + value);
        //跳转前先判断游戏ID是否 为空
        if (value != 0) {
            intent.setClass(context, GameDetailHomeActivity.class);
            intent.putExtra("id", String.valueOf(value));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Log.i("zzz", "游戏ID为空");
        }
    }

    /***
     * 跳转抽奖页面
     * @param context
     */
    public static void JumpToAwardActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, AwardActivity.class);
        context.startActivity(intent);
    }

    /***
     * 跳转中奖记录、积分记录页面
     * @param context
     */
    public static void JumpToAwardRecordActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, AwardPointsRecordActivity.class);
        context.startActivity(intent);
    }

    /***
     * 跳转过期奖品页面
     * @param context
     */
    public static void JumpToPastAwardActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, PastAwardActivity.class);
        context.startActivity(intent);
    }

    public static void activity_Jump_Value(Context context, Class clazz, String key, Boolean value) {

        Intent intent = new Intent();
        intent.setClass(context, clazz); // 传出去的值
        intent.putExtra(key, value);
        context.startActivity(intent);
    }

    public static void activity_Jump_Value(Context context, Class clazz, String key, Parcelable value) {
        Intent intent = new Intent();
        intent.setClass(context, clazz); // 传出去的值
        intent.putExtra(key, value);
        context.startActivity(intent);
    }

    public static void activity_Jump_Value(Context context, Class clazz, String key, Bundle value) {

        Intent intent = new Intent();
        intent.setClass(context, clazz); // 传出去的值
        intent.putExtra(key, value);

        context.startActivity(intent);
    }


    /**
     * 次方法用来跳转到图片浏览器，
     *
     * @param activity 指跳转时是从这个activity 对象发起的，不仅仅是上下文对象，必须为Activity对象（需要overwrite
     *                 overridePendingTransition方法）；
     * @param urls     ImageUrl 对象(包含一个list对象，可以通过构造函数传入)；无则传null
     * @param position 点击图片跳转时的图片所处的位置；若为-1默认是第一张图片；位置从0开始。。
     */
    public static void jumpToImagEye(Activity activity, ImageUrl urls, int position) {
        if (urls != null && activity != null) {
            if (position < 0) {
                position = 0;
            }
            Intent intent = new Intent(activity, ImageViewPagerActivity.class);
            intent.putExtra(ImageViewPagerActivity.POSITION, position);
            intent.putExtra(ImageViewPagerActivity.PHOTOS, urls);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.image_slide_in, R.anim.image_slide_exit);
        }
    }

    public static void jumpToImagEyeForResult(Activity activity, ImageUrl urls, int position, int requestCode) {
        if (urls != null && activity != null) {
            if (position < 0) {
                position = 0;
            }
            Intent intent = new Intent(activity, ImageViewPagerActivity.class);
            intent.putExtra(ImageViewPagerActivity.POSITION, position);
            intent.putExtra(ImageViewPagerActivity.PHOTOS, urls);
            activity.startActivityForResult(intent, requestCode);
            activity.overridePendingTransition(R.anim.image_slide_in, R.anim.image_slide_exit);
        }
    }

    /**
     * 次方法用来跳转到礼包部分我的礼包、礼包中心页面；
     *
     * @param context 上下文对象；
     * @param index   可以使用{@link GiftHomeActivity#GIFT_CENTER}和{@link GiftHomeActivity#GIFT_MINE};
     */
    public static void jumpToGift(Context context, int index) {
        if (context != null) {
            Intent intent = new Intent(context, GiftHomeActivity.class);
            intent.putExtra(GiftHomeActivity.GIFT_PAGE, index);
            context.startActivity(intent);
        }
    }

    /**
     * 次方法用来跳转到社区部分最新话题、发现小组、我的社区页面；
     *
     * @param context 上下文对象；
     * @param index   可以使用GiftHomeActivity.COM_GROUP_FOUND、GiftHomeActivity.
     *                COM_TOPIC_LATEST和GiftHomeActivity.COM_MINE;
     */
    public static void jumpToCommunity(Context context, int index) {
        if (context != null) {
            Intent intent = new Intent(context, CommunityActivity.class);
            intent.putExtra(CommunityActivity.COM_PAGE, index);
            context.startActivity(intent);
        }
    }

    /**
     * 根据url跳转
     *
     * @param context
     * @param title
     * @param url
     */
    public static void jumpToByUrl(Context context, String title, String url) {
        //如果是话题详情
        String baseTopicUrl = Host.getHost(Host.HostType.FORUM_HOST).replace("api", "topic");
        if (url.contains(baseTopicUrl)) {
            try {
                int topicIdStart = url.lastIndexOf("/");
                String topicIdStr = url.substring(topicIdStart + 1);
                int topicId = Integer.parseInt(topicIdStr);
                ActivityActionUtils.jumpToTopicDetail(context, topicId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Logger.e("parse topicid faild,please the topic url!");
            }
            //如果是TA的主页
        } else if (url.contains("others")) {
            try {
                String[] s = url.split("/");
                String userIdStr = s[s.length - 2];
                int userId = Integer.parseInt(userIdStr);
                ActivityActionUtils.activity_Jump_Value(context, PersonalActivity.class, "userId", userId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        //如果是评论详情
        else if (url.contains("comment")) {
            String[] s = url.split("/");
            String topicIdStr = s[s.length - 3];
            //            String commentIdStr = s[s.length-1];
            ActivityActionUtils.jumpToTopicDetail(context, Integer.parseInt(topicIdStr));
        } else {
            ActivityActionUtils.jumpToWebView(context, title, url);
        }
    }

    /**
     * 跳转到webview视图
     *
     * @param context
     * @param title
     * @param gotoUrl
     */
    public static void jumpToWebView(Context context, String title, String gotoUrl) {
        if (title == null) {
            title = "";
        }
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("gotoUrl", gotoUrl);
        context.startActivity(intent);
    }

    public static void jumpToWebView(Context context, String title, String gotoUrl, String tabId) {
        if (title == null) {
            title = "";
        }
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("gotoUrl", gotoUrl);
        intent.putExtra("tabId", tabId);
        context.startActivity(intent);
    }

    /**
     * 跳转话题详情
     *
     * @param context
     * @param topicId
     */
    public static void jumpToTopicDetail(Context context, int topicId) { // 跳转到详情页面
        Intent in = new Intent(context, TopicDetailActivity.class);
        in.putExtra("topicId", topicId);
        context.startActivity(in);
    }

    /**
     * 跳转到首页，
     *
     * @param context
     */
    public static void jumpToHomeActivityIndex(Context context) {
        Intent in = new Intent(context, HomeActivity.class);
        in.putExtra(Constant.PAGE_EXTRA, "index");
        context.startActivity(in);
    }

    public static void jumpToSearhTagActiviy(Context context, String id, String title) {
        Intent in = new Intent(context, SearchTagActivity.class);
        in.putExtra(SearchTagActivity.INTENT_TAG_ID, id);
        in.putExtra(SearchTagActivity.INTENT_TAG_TITLE, title);
        context.startActivity(in);
    }


    /**
     * 跳转热门分类详情
     *
     * @param context
     * @param id
     * @param title
     */
    public static void jumpToCategoryHotCats(Context context, String id, String title) {
        Intent in = new Intent(context, CategoryHotCatsActivity.class);
        in.putExtra("id", id);
        in.putExtra("title", title);
        context.startActivity(in);
    }

    /**
     * 跳转到分类详情
     *
     * @param context
     * @param categoryId
     * @param idList
     * @param titleList
     * @param clickId
     * @param isBigCategory
     */
    public static void jumpToCategoryDetail(Context context, String categoryId, String categoryTitle, ArrayList<String> idList, ArrayList<String> titleList, String clickId, boolean isBigCategory) {
        Intent intent = new Intent(context, CategoryItemResultActivity.class);
        intent.putExtra("category_id", categoryId);
        intent.putStringArrayListExtra("id_list", idList);
        intent.putStringArrayListExtra("title_list", titleList);
        intent.putExtra("click_id", clickId);
        intent.putExtra("is_big_category", isBigCategory);
        intent.putExtra("category_title", categoryTitle);
        context.startActivity(intent);
    }

    public static void jumpToGameOtherDetail(Context context, int whereFrom, String id, String title, GameBaseDetail gameBaseDetail) {
        Intent intent = new Intent(context, GameOtherDetailActivity.class);
        intent.putExtra("where_from", whereFrom);
        intent.putExtra("title", title);
        intent.putExtra("id", id);
        intent.putExtra("game_base_detail", gameBaseDetail);
        context.startActivity(intent);
    }

    /**
     * 跳转到内容详情
     *
     * @param context
     * @param url
     */
    public static void jumpToHotDetail(Context context, String url) {
        Intent intent = new Intent(context, HotDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(H5Util.HOT_DETAIL_URL, url);
        context.startActivity(intent);
    }

}
